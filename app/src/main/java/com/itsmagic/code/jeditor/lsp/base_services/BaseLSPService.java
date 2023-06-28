package com.itsmagic.code.jeditor.lsp.base_services;

import android.app.Service;

public abstract class BaseLSPService extends Service {
    public abstract boolean isServerRunning();

    public abstract void startLSPServer();
    public abstract void stopLSPServer();

    protected abstract void startServer() throws Exception;
	
    public abstract void addServerListener(ILanguageServerCallback callback);
	
	public interface ILanguageServerCallback {
        public default void onStart() { }
        public default void onShutdown() { }
	}
}