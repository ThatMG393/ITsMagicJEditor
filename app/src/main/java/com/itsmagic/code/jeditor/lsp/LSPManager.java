package com.itsmagic.code.jeditor.lsp;

import android.content.ComponentName;
import android.os.IBinder;
import android.content.ServiceConnection;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;
import com.itsmagic.code.jeditor.lsp.base_services.BaseLSPService;
import com.itsmagic.code.jeditor.lsp.java.JavaLSPService;
import com.itsmagic.code.jeditor.models.LanguageServer;
import java.util.ArrayList;

public class LSPManager {
    private static volatile LSPManager INSTANCE;

    public static synchronized LSPManager getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("Initialize first, use 'LSPManager#initializeInstance(AppCompatActivity)'");
        }

        return INSTANCE;
    }

    public static synchronized LSPManager initializeInstance(@NonNull AppCompatActivity activity) {
        if (INSTANCE == null) {
            INSTANCE = new LSPManager(activity);
        }

        return INSTANCE;
    }

    private final AppCompatActivity activity;
	private final ArrayMap<String, LanguageServer> languageServers = new ArrayMap<String, LanguageServer>();

    private LSPManager(AppCompatActivity activity) {
        this.activity = activity;
		
		// Register LSP
		languageServers.put("java", new LanguageServer(
			activity.getApplicationContext(),
			"java",
			JavaLSPService.class,
			17364
		));
    }

    public void startLSP(String language) {
		languageServers.get(language).startLSPService();
	}
	
	public void startLSPAll() {
		languageServers.forEach((language, model) -> {
			model.startLSPService();
		});
	}

    public void stopLSP(String language) {
		languageServers.get(language).stopLSPService();
	}
	
	public ArrayMap<String, LanguageServer> getLanguageServers() {
		return languageServers;
	}
}
