package com.itsmagic.code.jeditor.fragments.editor;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.anggrayudi.storage.file.DocumentFileCompat;
import com.anggrayudi.storage.file.DocumentFileType;
import com.itsmagic.code.jeditor.activities.EditorActivity;
import com.itsmagic.code.jeditor.lsp.LSPManager;
import com.itsmagic.code.jeditor.models.LanguageServerModel;
import com.itsmagic.code.jeditor.utils.EditorUtils;
import com.itsmagic.code.jeditor.utils.SharedPreferenceUtils;

import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.lsp.editor.LspEditor;
import io.github.rosemoe.sora.lsp.editor.LspEditorManager;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentIO;
import io.github.rosemoe.sora.widget.CodeEditor;

import java.util.concurrent.CompletableFuture;

public class EditorTabFragment extends Fragment {
	public CodeEditor editor;
	
	private DocumentFile documentFile;
	
	public EditorTabFragment(Context context, String filePath) {
		this.documentFile = DocumentFileCompat.fromFullPath(context, filePath, DocumentFileType.FILE);
		initEditor(context);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
		return editor;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceBundle) {
		super.onViewCreated(view, savedInstanceBundle);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		editor.requestFocus();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		editor.release();
		LspEditorManager.getOrCreateEditorManager(LSPManager.getInstance().getCurrentProject().projectPath)
			.getEditor(documentFile.getUri().toString())
			.close();
	}
	
	private final void initEditor(Context context) {
		editor = new CodeEditor(context);
		
		EditorUtils.ensureTMTheme(editor);
		
		ThemeRegistry.getInstance().setTheme(
			SharedPreferenceUtils.getInstance().getStringFallback("editor_code_theme", "quitelight")
		);
		
		editor.setEditorLanguage(TextMateLanguage.create("source.java", true));
		
		LSPManager.getInstance().getLanguageServers().get("java").addListener(new LanguageServerModel.LanguageServerServiceListener() {
			@Override
			public void onServerServiceConnected() {
				Log.d("LSP", "onServerServiceConnected()");
				LspEditor lspEditor = LspEditorManager.getOrCreateEditorManager(LSPManager.getInstance().getCurrentProject().projectPath)
					.createEditor(
						documentFile.getUri().toString(),
						LSPManager.getInstance().getLanguageServers().get("java").getServerDefinition()
					);
					
				lspEditor.setWrapperLanguage(TextMateLanguage.create("source.java", true));
				lspEditor.setEditor(editor);
				
				CompletableFuture.runAsync(() -> {
					try {
						Log.d("LSP", "Connecting to JDT with timeout!");
						lspEditor.connectWithTimeout();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		});
		
		CompletableFuture.runAsync(() -> {
			try {
				Content text = ContentIO.createFrom(
					context.getContentResolver()
							.openInputStream(documentFile.getUri())
				);
					
				editor.post(() -> { editor.setText(text); });
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}

