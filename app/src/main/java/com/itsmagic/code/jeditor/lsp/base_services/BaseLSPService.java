package com.itsmagic.code.jeditor.lsp.base_services;

import android.app.Service;

public abstract class BaseLSPService extends Service {
    public abstract boolean isServerRunning();

    public abstract void startLSPServer();
    public abstract void stopLSPServer();

    protected abstract void startServer() throws Exception;
	
    public abstract void addServerListener(LanguageServerListener callback);
	
	public interface LanguageServerListener {
		public default void onServerConnected() { }
		public default void onServerDisconnected() { }
	}
}