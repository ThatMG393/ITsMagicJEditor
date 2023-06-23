package com.itsmagic.code.jeditor.models;

import android.net.Uri;
import java.io.Serializable;

public class ProjectModel implements Serializable {
	public String projectPath;
	public String projectName;
		
	public ProjectModel(String projectPath, String projectName) {
		this.projectPath = projectPath;
		this.projectName = projectName;
	}
}