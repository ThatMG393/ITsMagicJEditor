package com.itsmagic.code.editor;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.itsmagic.code.editor.model.ProjectModel;
import com.itsmagic.code.editor.utils.EditorUtils;
import io.github.rosemoe.sora.lang.Language;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.DefaultGrammarDefinition;
import io.github.rosemoe.sora.langs.textmate.registry.model.GrammarDefinition;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;
import io.github.rosemoe.sora.widget.schemes.SchemeDarcula;
import org.eclipse.tm4e.core.internal.theme.IRawTheme;
import org.eclipse.tm4e.core.internal.theme.ThemeReader;
import org.eclipse.tm4e.core.registry.IGrammarSource;

public class EditorActivity extends AppCompatActivity {
	private ProjectModel projectInfo;
	private CodeEditor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		init();
		super.onCreate(savedInstanceState);
		/*
		TextView t = new TextView(this);
		t.setText("Project Name: " + projectInfo.projectName + "\nProject Path: " + projectInfo.absolutePath);
		*/
		editor = new CodeEditor(this);
		setContentView(editor);
		
		initEditor();
	}
	
	private final void initEditor() {
		EditorUtils.loadTMTheme(getApplicationContext(), "darcula");
	
		if (!(editor.getColorScheme() instanceof TextMateColorScheme)) {
			editor.setColorScheme(TextMateColorScheme.create(ThemeRegistry.getInstance()));
		}
		
		editor.setEditorLanguage(EditorUtils.createLanguage(getApplicationContext(), "java");
	}
	
	private final void init() {
		projectInfo = getIntent().getSerializableExtra("projectInfo", ProjectModel.class);
	}
}
