package com.itsmagic.code.jeditor.utils;

import androidx.annotation.NonNull;
import com.itsmagic.code.jeditor.lsp.LSPManager;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.lsp.client.connection.StreamConnectionProvider;
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.CustomLanguageServerDefinition;
import io.github.rosemoe.sora.lsp.client.languageserver.serverdefinition.LanguageServerDefinition;
import io.github.rosemoe.sora.lsp.client.languageserver.wrapper.LanguageServerWrapper;
import io.github.rosemoe.sora.lsp.editor.LspEditor;
import io.github.rosemoe.sora.lsp.editor.LspEditorManager;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;

public class LSPUtils {
	public static LanguageServerWrapper createNewServerWrapper(
		@NonNull String language,
		@NonNull StreamConnectionProvider connectionProvider,
		@NonNull String projectPath
	) {
		return new LanguageServerWrapper(
			new CustomLanguageServerDefinition(
				"." + language, new CustomLanguageServerDefinition.ConnectProvider() {
					@Override
					public StreamConnectionProvider createConnectionProvider(String workingDir) {
						return connectionProvider;
					}
				}
			), projectPath
		);
	}
	
	public static LspEditor createNewLspEditor(
		@NonNull String fileUri,
		@NonNull LanguageServerDefinition serverDefinition,
		@NonNull CodeEditor editor
	) {
		LspEditor lspEditor = LspEditorManager.getOrCreateEditorManager(LSPManager.getInstance().getCurrentProject().projectPath)
			.createEditor(
				fileUri,
				serverDefinition
			);
					
		lspEditor.setWrapperLanguage((TextMateLanguage)editor.getEditorLanguage());
		lspEditor.setEditor(editor);
		
		return lspEditor;
	}
	
	public static CompletableFuture<Boolean> connectToLsp(LspEditor lspEditor) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				lspEditor.connectWithTimeout();
				return true;
			} catch(TimeoutException | InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		});
	}
}
