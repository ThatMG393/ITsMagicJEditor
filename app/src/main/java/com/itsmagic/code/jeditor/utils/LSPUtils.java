package com.itsmagic.code.jeditor.utils;

import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
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
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import org.eclipse.lsp4j.DidChangeWorkspaceFoldersParams;
import org.eclipse.lsp4j.WorkspaceFolder;
import org.eclipse.lsp4j.WorkspaceFoldersChangeEvent;

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
		Handler mainThread = new Handler(Looper.getMainLooper());
		return CompletableFuture.supplyAsync(() -> {
			try {
				lspEditor.getEditor().setEditable(false);
				lspEditor.connectWithTimeout();
				lspEditor.getEditor().setEditable(true);
				mainThread.postAtFrontOfQueue(() -> {
					Toast.makeText(
						lspEditor.getEditor().getContext(),
						"connected to lsp!",
						Toast.LENGTH_SHORT
					).show();
				});
				
				WorkspaceFoldersChangeEvent wfce = new WorkspaceFoldersChangeEvent();
				wfce.setAdded(
					Arrays.asList(
						new WorkspaceFolder(LSPManager.getInstance().getCurrentProject().projectPath + "/java")
					)
				);
					
				DidChangeWorkspaceFoldersParams dwfp = new DidChangeWorkspaceFoldersParams();
				dwfp.setEvent(wfce);

				lspEditor.getRequestManager().didChangeWorkspaceFolders(
					dwfp
				);
				return true;
			} catch(TimeoutException | InterruptedException e) {
				e.printStackTrace();
				return false;
			}
		});
	}
}
