package com.itsmagic.code.jeditor.lsp.java;

import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.widget.Toast;

import com.itsmagic.code.jeditor.MainApplication;
import com.itsmagic.code.jeditor.lsp.LSPManager;
import com.itsmagic.code.jeditor.lsp.base_services.BaseLSPBinder;
import com.itsmagic.code.jeditor.lsp.base_services.BaseLSPService;
import com.itsmagic.code.jeditor.utils.ThreadPlus;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.ArrayList;

public class JavaLSPService extends BaseLSPService {
	private JavaLSPBinder binder = new JavaLSPBinder();
	
	private ThreadPlus serverThread;
	private volatile boolean isServerRunning;
	
// LSP variables {
	private AsynchronousServerSocketChannel serverSocket;
	private AsynchronousSocketChannel serverClientSocket;
	
	private InputStream clientOut;
	private OutputStream clientIn;
// }
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		serverThread = new ThreadPlus(() -> {
			try {
				initializeServer();
				startServer();	
			} catch (Exception e) {
				e.printStackTrace();
				new Handler(Looper.getMainLooper()).postAtFrontOfQueue(() -> {
					Toast.makeText(getApplicationContext(), "An error occurred!\n" + e.getMessage(), Toast.LENGTH_LONG).show();
				});
			}
		});
	}
	
	@Override
	public void startLSPServer() {
		serverThread.start();
	}
	
	@Override
	public void stopLSPServer() {
		// TODO: Implement this method
	}
	
	@Override
	protected void startServer() throws Exception {
		serverClientSocket = serverSocket.accept().get();
		
		while (serverThread.isRunning()) {
			if (!isServerRunning) {
				/*
				LanguageServerWrapper wrapper = LSPManager.getInstance().getLanguageServers().get("java").getLanguageServerWrapper();
				wrapper.start();
				*/
			}
		}
	}
	
	public void initializeServer() throws Exception {
		int clientPort = LSPManager.getInstance().getLanguageServerModel("java").getLSPPort();
		// Os.setenv("CLIENT_PORT", String.valueOf(clientPort + 1), true);
		
		if (serverSocket == null) {
			serverSocket = AsynchronousServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress("localhost", clientPort));
		}
	}
	
	private ArrayList<BaseLSPService.LanguageServerListener> listeners = new ArrayList<>();
	@Override
	public void addServerListener(BaseLSPService.LanguageServerListener listener) {
		listeners.add(listener);
		
		if (isServerRunning()) { listener.onServerConnected(); }
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		stopLSPServer();
		return true;
	}
	
	@Override
	public boolean isServerRunning() {
		return isServerRunning && serverThread.isRunning();
	}
	
	@Override
	public IBinder onBind(Intent intent) { return binder; }
	
	public class JavaLSPBinder extends BaseLSPBinder<JavaLSPService> {
		public JavaLSPService getInstance() {
			return JavaLSPService.this;
		}
	}
}
