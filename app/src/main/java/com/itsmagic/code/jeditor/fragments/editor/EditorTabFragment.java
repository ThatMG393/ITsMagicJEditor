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
import com.itsmagic.code.jeditor.utils.EditorUtils;

import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.text.ContentIO;
import io.github.rosemoe.sora.widget.CodeEditor;

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
		
		/*
		ThemeRegistry.getInstance().setTheme(
			SharedPreferenceUtils.getInstance().getStringFallback("editorTheme", "quitelight")
		);
		*/
		
		ThemeRegistry.getInstance().setTheme("darcula");
		
		editor.setEditorLanguage(TextMateLanguage.create("source.java", true));
		
		DocumentFile file = DocumentFileCompat.fromFullPath(context, filePath, DocumentFileType.FILE);
		
		new Thread(() -> {
			try {
				editor.setText(
					ContentIO.createFrom(
						context.getContentResolver().openInputStream(file.getUri())
					)
				);
					
			} catch (Exception e) {
				e.printStackTrace();
			}
				
			Thread.currentThread().interrupt();
		}).run();
	}
}
