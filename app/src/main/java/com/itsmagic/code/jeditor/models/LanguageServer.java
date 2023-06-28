package com.itsmagic.code.jeditor.models;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.content.ServiceConnection;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.itsmagic.code.jeditor.lsp.base_services.BaseLSPBinder;
import com.itsmagic.code.jeditor.lsp.base_services.BaseLSPService;
import io.github.rosemoe.sora.lsp.client.connection.SocketStreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.connection.StreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition;
import io.github.rosemoe.sora.lsp.client.languageserver.wrapper.EventHandler;
import java.util.ArrayList;

public class LanguageServer implements ServiceConnection {
	private final Context context;
	private final String languagerServerLanguage;
	private final Intent languageServerServiceIntent;
	private final int languageServerPort;
	private final CustomLanguageServerDefinition languageServerDefinition;
	
	public LanguageServer(
		@NonNull Context context,
		@NonNull String languagerServerLanguage,
		@NonNull Class<? extends BaseLSPService> languageServerServiceClass,
		@NonNull int languageServerPort
	) {
		this.context = context;
		this.languagerServerLanguage = languagerServerLanguage;
		this.languageServerServiceIntent = new Intent(
			context, languageServerServiceClass
		);
		
		this.languageServerPort = languageServerPort;
		this.languageServerDefinition = new CustomLanguageServerDefinition(
			"." + languagerServerLanguage, new CustomLanguageServerDefinition.ConnectProvider() {
				@Override
				public StreamConnectionProvider createConnectionProvider(String workingDir) {
					SocketStreamConnectionProvider tmp = new SocketStreamConnectionProvider(() -> languageServerPort) {
						public EventHandler.EventListener getEventListener() {
							return EventHandler.EventListener.DEFAULT;
						}
					};
					
					return tmp;
				}
			}
		);
	}
	
	public int getLSPPort() {
		return this.languageServerPort;
	}
	
	public CustomLanguageServerDefinition getServerDefinition() {
		return this.languageServerDefinition;
	}
	
	public void startLSPService() {
		Toast.makeText(context, "STARTING SE4VUC3", Toast.LENGTH_LONG).show();
		context.bindService(languageServerServiceIntent, this, Context.BIND_AUTO_CREATE);
	}
	
	public void stopLSPService() {
		context.unbindService(this);
	}
	
	private BaseLSPService languageServerServiceInstance;
	@Override
    public void onServiceConnected(ComponentName component, IBinder binder) {
		Toast.makeText(context, "WEBINDED", Toast.LENGTH_LONG).show();
		languageServerServiceInstance = ((BaseLSPBinder) binder ).getInstance();
		languageServerServiceInstance.startLSPServer();
		
		callbackOnReady();
	}

    @Override
    public void onServiceDisconnected(ComponentName component) {
		callbackOnShutdown();
	}
	
	private ArrayList<LanguagerServerCallback> lspCallbackArr = new ArrayList<>();
	public void addListener(LanguagerServerCallback lspCallback) {
		lspCallbackArr.add(lspCallback);
		
		if (languageServerServiceInstance != null) { lspCallback.onServerServiceConnected(); }
	}
	
	public void callbackOnReady() {
		lspCallbackArr.forEach((callback) -> {
			callback.onServerServiceConnected();
		});
	}
	public void callbackOnShutdown() {
        lspCallbackArr.forEach(LanguagerServerCallback::onServerServiceDisconnected);
	}
	
	public interface LanguagerServerCallback {
		public default void onServerServiceConnected() { }
		public default void onServerServiceDisconnected() { }
	}
}
