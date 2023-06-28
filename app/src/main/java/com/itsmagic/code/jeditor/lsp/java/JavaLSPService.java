package com.itsmagic.code.jeditor.lsp.java;

import android.content.Intent;
import android.os.IBinder;

import android.widget.Toast;
import com.itsmagic.code.jeditor.lsp.LSPManager;
import com.itsmagic.code.jeditor.lsp.base_services.BaseLSPBinder;
import com.itsmagic.code.jeditor.lsp.base_services.BaseLSPService;
import com.itsmagic.code.jeditor.utils.ThreadPlus;

import org.eclipse.jdt.ls.core.internal.JavaClientConnection.JavaLanguageClient;
import org.eclipse.jdt.ls.core.internal.handlers.JDTLanguageServer;
import org.eclipse.jdt.ls.core.internal.preferences.PreferenceManager;
import org.eclipse.lsp4j.jsonrpc.Launcher;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.Channels;

public class JavaLSPService extends BaseLSPService {
	private JavaLSPBinder binder = new JavaLSPBinder();
	
	private ThreadPlus serverThread;
	private volatile boolean isServerRunning;
	
	private AsynchronousServerSocketChannel serverSocket;
	private AsynchronousSocketChannel serverClientSocket;
	
	private InputStream serverIS;
	private OutputStream serverOS;
	
	private JDTLanguageServer jdtServer;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		serverThread = new ThreadPlus(() -> {
			try {
				initializeServer();
				startServer();	
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	@Override
	public void startLSPServer() {
		Toast.makeText(getApplicationContext(), "STARTING LSP", Toast.LENGTH_LONG).show();
		serverThread.start();
	}
	
	@Override
	public void stopLSPServer() {
		// TODO: Implement this method
	}
	
	@Override
	protected void startServer() throws Exception {
		System.out.println("listening");
		serverClientSocket = serverSocket.accept().get();
		System.out.println("yahooooooo");
		
		while (serverThread.isRunning()) {
			if (!isServerRunning) {
				serverIS = Channels.newInputStream(serverClientSocket);
				serverOS = Channels.newOutputStream(serverClientSocket);
				
				Launcher serverLauncher = Launcher.createLauncher(jdtServer, JavaLanguageClient.class, serverIS, serverOS);
				
				jdtServer.connectClient((JavaLanguageClient) serverLauncher.getRemoteProxy());
				serverLauncher.startListening();
				
				isServerRunning = true;
			}
		}
	}
	
	public void initializeServer() throws Exception {
		int serverPort = LSPManager.getInstance().getLanguageServers().get("java").getLSPPort();
		
		if (serverSocket == null) {
			serverSocket = AsynchronousServerSocketChannel.open();
			serverSocket.bind(new InetSocketAddress("localhost", serverPort));
		}
		
		if (jdtServer == null) {
			jdtServer = new JDTLanguageServer(
				null,
				new PreferenceManager()
			);
		}
	}
	
	@Override
	public void addServerListener(BaseLSPService.ILanguageServerCallback callback) { }
	
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
