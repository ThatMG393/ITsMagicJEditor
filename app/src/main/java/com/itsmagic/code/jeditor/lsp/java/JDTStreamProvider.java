package com.itsmagic.code.jeditor.lsp.java;

import android.system.Os;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import io.github.rosemoe.sora.lsp.client.connection.StreamConnectionProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
			activity.getExternalFilesDir("").getAbsolutePath() + "jdt-data"
		};
	}
	
    @Override
    public void start() throws IOException {
		Log.d("JDT", "Starting JDTLS!");
		
		ProcessBuilder jdtProcBuilder = new ProcessBuilder(jdtExecCmd);
		jdtProcess = jdtProcBuilder.start();
		
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
