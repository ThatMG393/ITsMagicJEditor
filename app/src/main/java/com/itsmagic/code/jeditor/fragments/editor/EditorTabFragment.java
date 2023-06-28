package com.itsmagic.code.jeditor.fragments.editor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;

import com.anggrayudi.storage.file.DocumentFileCompat;
import com.anggrayudi.storage.file.DocumentFileType;
import com.itsmagic.code.jeditor.lsp.LSPManager;
import com.itsmagic.code.jeditor.models.LanguageServer;
import com.itsmagic.code.jeditor.utils.EditorUtils;
import com.itsmagic.code.jeditor.utils.SharedPreferenceUtils;

import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.lsp.editor.LspEditor;
import io.github.rosemoe.sora.lsp.editor.LspEditorManager;
import io.github.rosemoe.sora.lsp.utils.URIUtils;
import io.github.rosemoe.sora.text.Content;
import io.github.rosemoe.sora.text.ContentIO;
import io.github.rosemoe.sora.widget.CodeEditor;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class EditorTabFragment extends Fragment {
	public CodeEditor editor;
	
	private String filePath;
	
	public EditorTabFragment(Context context, String filePath) {
		this.filePath = filePath;
		editor = new CodeEditor(context);
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
	}
	
	private final void initEditor(Context context) {
		FileProviderRegistry.getInstance().addFileProvider(
			new AssetsFileResolver(
				context.getAssets()
			)
		);
		
		GrammarRegistry.getInstance().loadGrammars("tm-language/languages.json");
		EditorUtils.loadTMThemes();
		EditorUtils.ensureTMTheme(editor);
		
		ThemeRegistry.getInstance().setTheme(
			SharedPreferenceUtils.getInstance().getStringFallback("editor_code_theme", "quitelight")
		);
		
		editor.setEditorLanguage(TextMateLanguage.create("source.java", true));
		
		LSPManager.getInstance().getLanguageServers().get("java").addListener(new LanguageServer.LanguagerServerCallback() {
			@Override
			public void onServerServiceConnected() {
				LspEditor lspEditor = LspEditorManager.getOrCreateEditorManager(filePath)
					.createEditor(
						URIUtils.fileToURI(new File(filePath)).toString(),
						LSPManager.getInstance().getLanguageServers().get("java").getServerDefinition()
					);
					
				lspEditor.setWrapperLanguage(editor.getEditorLanguage());
				lspEditor.setEditor(editor);
				
				System.out.println("LETSA GO????");
				
				CompletableFuture.runAsync(() -> {
					try {
						System.out.println("JDT????");
						lspEditor.connectWithTimeout();
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}
		});
		
		DocumentFile file = DocumentFileCompat.fromFullPath(context, filePath, DocumentFileType.FILE);
		CompletableFuture.runAsync(() -> {
			try {
				Content text = ContentIO.createFrom(context.getContentResolver().openInputStream(file.getUri()));
				editor.post(() -> {
					editor.setText(text);
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}

