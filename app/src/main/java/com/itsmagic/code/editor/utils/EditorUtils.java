package com.itsmagic.code.editor.utils;
import android.content.Context;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.DefaultGrammarDefinition;
import java.nio.charset.Charset;
import org.eclipse.tm4e.core.registry.IGrammarSource;
import org.eclipse.tm4e.core.registry.IThemeSource;

public class EditorUtils {
	public static TextMateLanguage createTMLanguage(Context context, String name) {
		String fullPath = "tm-language/" + name;
	
		TextMateLanguage tmp = null;
		
		try {
			tmp = TextMateLanguage.create(
				DefaultGrammarDefinition.withGrammarSource(
					IGrammarSource.fromInputStream(
						context.getAssets().open(fullPath + "/syntaxes/" + name + ".tmLanguage.json"),
						fullPath + "/syntaxes/" + name + ".tmLanguage.json", Charset.defaultCharset()
					)
				), true
			);
		} catch (Exception e) { }
		
		return tmp;
	}
	
	public static void loadTMTheme(Context context, String name) {
		try {
			ThemeRegistry.getInstance().loadTheme(
				IThemeSource.fromInputStream(
					context.getAssets().open("tm-themes/" + name + ".json"),
					"tm-themes/" + name + ".json", Charset.defaultCharset()
				)
			);
		} catch (Exception e) { }
	}
}
