package com.itsmagic.code.jeditor.fragments.editor;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import androidx.fragment.app.Fragment;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import com.itsmagic.code.jeditor.utils.EditorUtils;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import com.itsmagic.code.jeditor.utils.SharedPreferenceUtils;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.text.ContentIO;
import io.github.rosemoe.sora.widget.CodeEditor;
import java.io.FileInputStream;

public class EditorTabFragment extends Fragment {
	public CodeEditor editor;
	
	private String filePath;
	
	public EditorTabFragment(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
		editor = new CodeEditor(getContext());
		return editor;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceBundle) {
		super.onViewCreated(view, savedInstanceBundle);
		initEditor();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		editor.requestFocus();
	}
	
	private final void initEditor() {
		FileProviderRegistry.getInstance().addFileProvider(
			new AssetsFileResolver(
				requireActivity().getApplicationContext().getAssets()
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
		
		try {
			editor.setText(
				ContentIO.createFrom(
					new FileInputStream(filePath)
				)
			);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
