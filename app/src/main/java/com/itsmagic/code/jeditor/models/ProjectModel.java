package com.itsmagic.code.jeditor.models;

import java.io.Serializable;

public class ProjectModel implements Serializable {
	public String absolutePath;
	public String projectName;
		
	public ProjectModel(String absolutePath, String projectName) {
		this.absolutePath = absolutePath;
		this.projectName = projectName;
	}
}