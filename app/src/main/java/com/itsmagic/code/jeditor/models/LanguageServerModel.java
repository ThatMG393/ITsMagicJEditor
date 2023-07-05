package com.itsmagic.code.jeditor.models;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.itsmagic.code.jeditor.lsp.LSPManager;
import com.itsmagic.code.jeditor.lsp.base_services.BaseLSPBinder;
import com.itsmagic.code.jeditor.lsp.base_services.BaseLSPService;
import com.itsmagic.code.jeditor.utils.LSPUtils;

import io.github.rosemoe.sora.lsp.client.connection.SocketStreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.connection.StreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition;
import io.github.rosemoe.sora.lsp.client.languageserver.wrapper.LanguageServerWrapper;

import java.util.ArrayList;

public class LanguageServerModel implements ServiceConnection {
	private Context context;
	private String languagerServerLanguage;
	private Intent languageServerServiceIntent;
	private int languageServerPort;
	private LanguageServerWrapper languageServerWrapper;
	
	private boolean notInterProcessComms;
	
	public LanguageServerModel(
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
		this.languageServerWrapper = LSPUtils.createNewServerWrapper(
			languagerServerLanguage,
			new SocketStreamConnectionProvider(() -> languageServerPort),
			LSPManager.getInstance().getCurrentProject().projectPath
		);
	}
	
	public LanguageServerModel(
		@NonNull String languagerServerLanguage,
		@NonNull StreamConnectionProvider lspConnectionProvider
	) {
		this.languagerServerLanguage = languagerServerLanguage;
		this.languageServerWrapper = LSPUtils.createNewServerWrapper(
			languagerServerLanguage,
			lspConnectionProvider,
			LSPManager.getInstance().getCurrentProject().projectPath
		);
		this.notInterProcessComms = true;
	}
	
	public int getLSPPort() {
		if (notInterProcessComms) {
			return -1;
		} else {
			return this.languageServerPort;
		}
	}
	
	public CustomLanguageServerDefinition getServerDefinition() {
		return (CustomLanguageServerDefinition)this.languageServerWrapper.getServerDefinition();
	}
	
	public void startLSP() {
		if (context == null && notInterProcessComms) {
			callbackOnReady(); return;
		} else {
			context.bindService(languageServerServiceIntent, this, Context.BIND_AUTO_CREATE);
		}
	}
	
	public void stopLSP() {
		if (context == null && notInterProcessComms) {
			callbackOnShutdown();
		} else {
			context.unbindService(this);
		}
	}
	
	// Service-based lsps
	private BaseLSPService languageServerServiceInstance;
	@Override
    public void onServiceConnected(ComponentName component, IBinder binder) {
		languageServerServiceInstance = ((BaseLSPBinder) binder ).getInstance();
		languageServerServiceInstance.startLSPServer();
		
		callbackOnReady();
	}

    @Override
    public void onServiceDisconnected(ComponentName component) {
		callbackOnShutdown();
	}
	
	private ArrayList<LanguageServerServiceListener> listeners = new ArrayList<>();
	public void addListener(LanguageServerServiceListener listener) {
		if (notInterProcessComms) {
			listener.onServerServiceConnected();
			return;
		}
		
		listeners.add(listener);
		if (languageServerServiceInstance != null) listener.onServerServiceConnected();
	}
	public void addServerListener(BaseLSPService.LanguageServerListener listener) {
		if (context == null && notInterProcessComms) return;
		if (languageServerServiceInstance == null) return;
		
		languageServerServiceInstance.addServerListener(listener);
	}
	public void callbackOnReady() {
		listeners.forEach(LanguageServerServiceListener::onServerServiceConnected);
	}
	public void callbackOnShutdown() {
        listeners.forEach(LanguageServerServiceListener::onServerServiceDisconnected);
	}
	
	public interface LanguageServerServiceListener {
		public default void onServerServiceConnected() { }
		public default void onServerServiceDisconnected() { }
	}
}
