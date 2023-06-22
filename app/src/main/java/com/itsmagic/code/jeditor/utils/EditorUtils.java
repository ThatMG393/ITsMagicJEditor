package com.itsmagic.code.jeditor.utils;
import io.github.rosemoe.sora.langs.textmate.TextMateColorScheme;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.model.ThemeModel;
import io.github.rosemoe.sora.widget.CodeEditor;
import io.github.rosemoe.sora.widget.schemes.EditorColorScheme;

import org.eclipse.tm4e.core.registry.IThemeSource;

public class EditorUtils {
	public static String[] tmThemes = {
		"darcula",
		"quietlight"
	};

	public static void ensureTMTheme(CodeEditor editor) {
		EditorColorScheme colorScheme = editor.getColorScheme();
		if (!(colorScheme instanceof TextMateColorScheme)) {
			try {
				colorScheme = TextMateColorScheme.create(ThemeRegistry.getInstance());
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			editor.setColorScheme(colorScheme);
		}
	}
	
	public static void loadTMThemes() {
		ThemeRegistry themeRegistry = ThemeRegistry.getInstance();
	
		for(String theme : tmThemes) {
			try {
				String themePath = "tm-themes/" + theme + ".json";
				themeRegistry.loadTheme(
					new ThemeModel(
						IThemeSource.fromInputStream(
							FileProviderRegistry.getInstance().tryGetInputStream(themePath),
							themePath, null
						), theme
					)
				);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
