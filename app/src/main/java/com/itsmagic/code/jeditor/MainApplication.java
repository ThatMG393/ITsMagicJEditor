
package com.itsmagic.code.jeditor;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.system.Os;
import android.widget.Toast;

import com.itsmagic.code.jeditor.utils.ProcessUtils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.xz.XZCompressorInputStream;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class MainApplication extends Application {
	private static final int UNTAR_BUFFER_SIZE = 2048;
	
	public boolean isJDK17Installed;
	public boolean isJDTInstalled;
	
	public String jdtlsPath;

	@Override
	public void onCreate() {
		super.onCreate();
		Toast.makeText(getApplicationContext(), "Extracting JDK and JDTLS", Toast.LENGTH_SHORT).show();
		
		Handler mainThread = new Handler(Looper.getMainLooper());
		CompletableFuture.runAsync(() -> {
			setupJDK17(getApplicationContext());
			extractJDTLS(getApplicationContext());
			
			boolean success = (isJDK17Installed && isJDTInstalled);
			if (success) {
				mainThread.postAtFrontOfQueue(() -> {
					Toast.makeText(getApplicationContext(), "Successfully installed JDK and JDTLS", Toast.LENGTH_LONG).show();
				});
				setupEnvVariable(getApplicationContext());
			} else {
				mainThread.postAtFrontOfQueue(() -> {
					Toast.makeText(getApplicationContext(), "Failed to installed JDK and JDTLS", Toast.LENGTH_LONG).show();
				});
			}
		});
	}

	public void setupJDK17(Context context) {
		File f = new File(context.getFilesDir().getAbsolutePath(), "release");
		if (f.exists()) { isJDK17Installed = true; return; }
		
		System.out.println("Extracting JDK 17");
		try {
			InputStream tarFileIS = context.getAssets().open("lsp/jre17-arm.tar.xz");
			try {
				uncompressTarXZ(tarFileIS, context.getFilesDir());
				ProcessUtils.runExecutable("sh", "-c", "chmod a+x " + getFilesDir().getAbsolutePath() + "/bin/*");
				
				isJDK17Installed = true;
			} catch (Exception e) {
				e.printStackTrace();
			}
			tarFileIS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void extractJDTLS(Context context) {
		File f = new File(context.getFilesDir().getAbsolutePath(), "jdt-language-server-1.23.0-202304271346");
		if (f.exists()) { isJDTInstalled = true; return; }
		
		System.out.println("Extracting JDT 1.23.0");
		try {
			InputStream tarFileIS = context.getAssets().open("lsp/jdtls-1.23.0.tar.xz");
			try {
				uncompressTarXZ(tarFileIS, context.getFilesDir());
				isJDTInstalled = true;
			} catch (Exception e) {
				e.printStackTrace();
			}

			tarFileIS.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setupEnvVariable(Context context) {
		try {
			Os.setenv("LD_LIBRARY_PATH", context.getFilesDir().getAbsolutePath() + "/lib", true);
			Os.setenv("JAVA_HOME", context.getFilesDir().getAbsolutePath() + "/bin", true);
			Os.setenv("PATH", Os.getenv("JAVA_HOME") + ":" + Os.getenv("PATH"), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* Thanks PojavTeam!
	   https://github.com/PojavLauncherTeam/PojavLauncher/blob/a7444aa99e0ee0d9f9f1956da997639b09cb5cf9/app_pojavlauncher/src/main/java/net/kdt/pojavlaunch/multirt/MultiRTUtils.java#L226
	*/
	private static void uncompressTarXZ(final InputStream tarFileInputStream, final File dest)
			throws IOException {
		if (dest.isFile()) throw new IOException("Attempting to unpack into a file");
		if (!dest.exists() && !dest.mkdirs())
			throw new IOException("Failed to create destination directory");

		byte[] buffer = new byte[UNTAR_BUFFER_SIZE];
		TarArchiveInputStream tarIn =
				new TarArchiveInputStream(new XZCompressorInputStream(tarFileInputStream));
		TarArchiveEntry tarEntry = tarIn.getNextTarEntry();

		while (tarEntry != null) {
			final String tarEntryName = tarEntry.getName();
			System.out.println("Unpacking " + tarEntryName);

			File destPath = new File(dest, tarEntry.getName());
			File destParent = destPath.getParentFile();
			if (tarEntry.isSymbolicLink()) {
				if (destParent != null && !destParent.exists() && !destParent.mkdirs())
					throw new IOException("Failed to create parent directory for symlink");
				try {
					// android.system.Os
					// Libcore one support all Android versions
					Os.symlink(tarEntry.getName(), tarEntry.getLinkName());
				} catch (Throwable e) {
				}
			} else if (tarEntry.isDirectory()) {
				if (!destPath.exists() && !destPath.mkdirs())
					throw new IOException("Failed to create directory");
			} else if (!destPath.exists() || destPath.length() != tarEntry.getSize()) {
				if (destParent != null && !destParent.exists() && !destParent.mkdirs())
					throw new IOException("Failed to create parent directory for file");

				FileOutputStream os = new FileOutputStream(destPath);
				IOUtils.copyLarge(tarIn, os, buffer);

				if (destPath.getName().equals("java")) destPath.setExecutable(true);

				os.close();
			}
			tarEntry = tarIn.getNextTarEntry();
		}
		tarIn.close();
	}
}
