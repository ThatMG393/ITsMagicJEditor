package com.itsmagic.code.jeditor.lsp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.ArrayMap;

import com.itsmagic.code.jeditor.lsp.java.JDTStreamProvider;
import com.itsmagic.code.jeditor.lsp.java.JavaLSPService;
import com.itsmagic.code.jeditor.models.LanguageServerModel;
import com.itsmagic.code.jeditor.models.ProjectModel;

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
	private final ArrayMap<String, LanguageServerModel> languageServers = new ArrayMap<String, LanguageServerModel>();
	private ProjectModel currentProject;
	
    private LSPManager(AppCompatActivity activity) {
        this.activity = activity;
    }
	
	/* Set currentProject first!
	 * Use LSPManager.getInstance().setCurrentProject(ProjectModel)
	 */
	public void registerLSPs() {
		if (currentProject == null) return;
		
		languageServers.put("java", new LanguageServerModel(
			"java",
			new JDTStreamProvider(
				activity,
				activity.getFilesDir().getAbsolutePath() + "/jdt-language-server-1.26.0-202307041703",
				getCurrentProject().projectPath
			)
		));
	}

    public void startLSP(String language) {
		languageServers.get(language).startLSP();
	}
	
	public void startAllLSP() {
		languageServers.forEach((language, model) -> {
			model.startLSP();
		});
	}

    public void stopLSP(String language) {
		languageServers.get(language).stopLSP();
	}
	
	public void setCurrentProject(ProjectModel newProject) {
		this.currentProject = newProject;
	}
	
	public ProjectModel getCurrentProject() {
		return this.currentProject;
	}
	
	public LanguageServerModel getLanguageServerModel(String language) {
		return languageServers.get(language);
	}
}
