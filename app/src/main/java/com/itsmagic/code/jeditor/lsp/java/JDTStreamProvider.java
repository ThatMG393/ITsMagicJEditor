package com.itsmagic.code.jeditor.lsp.java;

import android.content.Context;
import android.system.Os;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.itsmagic.code.jeditor.MainApplication;
import com.itsmagic.code.jeditor.utils.ProcessUtils;
import io.github.rosemoe.sora.lsp.client.connection.StreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.languageserver.requestmanager.RequestManager;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class JDTStreamProvider implements StreamConnectionProvider {
	private final AppCompatActivity activity;
	private final String[] jdtExecCmd;
	private final String workingDir;
	// private final Supplier<RequestManager> requestManagerSupplier;
	
	private Process jdtProcess;
	
	public JDTStreamProvider(AppCompatActivity activity, String jdtRoot, String workingDir) {//, Supplier<RequestManager> requestManagerSupplier) {
		this.activity = activity;
		this.workingDir = workingDir;
		// this.requestManagerSupplier = requestManagerSupplier;
		this.jdtExecCmd = new String[] {
			Os.getenv("JAVA_HOME") + "/java",
			// "--version"
            "-Declipse.application=org.eclipse.jdt.ls.core.id1",
            "-Dosgi.bundles.defaultStartLevel=4",
            "-Declipse.product=org.eclipse.jdt.ls.core.product",
            "-Dlog.level=ALL",
            // "-noverify",
            "-Xmx969M",
            "-jar",
        	jdtRoot + "/plugins/org.eclipse.equinox.launcher_1.6.400.v20210924-0641.jar",
            "-configuration",
            jdtRoot + "/config_linux",
            "-data",
			activity.getExternalFilesDir("").getAbsolutePath()
		};
	}
	
    @Override
    public void start() throws IOException {
		Log.d("JDT", "Starting JDTLS!");
		
		ProcessBuilder jdtProcBuilder = new ProcessBuilder(jdtExecCmd);
		/*
		jdtProcBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
		jdtProcBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		*/
		// jdtProcBuilder.inheritIO();
		jdtProcess = jdtProcBuilder.start();
		
		// byte[] fileBytes = new FileInputStream("/data/user/0/com.itsmagic.code.jeditor/files/jdt-language-server-1.23.0-202304271346/config_linux/1688533977225.log").readAllBytes();
		Log.d("JDT", new String(jdtProcess.getInputStream().readAllBytes(), StandardCharsets.UTF_8));
		
		if (!jdtProcess.isAlive()) throw new IOException("Process is not alive!");
		
		/*
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            RequestManager requestManager = requestManagerSupplier.get();
            if (requestManager != null) {
				requestManager.shutdown().orTimeout(10, TimeUnit.SECONDS).thenRun(() -> {
					if (jdtProcess.isAlive()) {
						jdtProcess.destroyForcibly();
					}
				});
			}
        }));
		*/
	}
	
	@Override
    public void close() {
		if (jdtProcess == null) return;
		
		jdtProcess.destroy();
	}

    @Override
    public InputStream getInputStream() {
		if (jdtProcess == null) return null;
		
		return jdtProcess.getInputStream();
	}

    @Override
    public OutputStream getOutputStream() {
		if (jdtProcess == null) return null;
		
		return jdtProcess.getOutputStream();
	}
}
