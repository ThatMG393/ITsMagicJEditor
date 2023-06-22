package com.itsmagic.code.jeditor;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import com.anggrayudi.storage.file.DocumentFileUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsmagic.code.jeditor.models.ProjectModel;
import com.itsmagic.code.jeditor.utils.SharedPreferenceUtils;
import com.itsmagic.code.jeditor.utils.StorageUtils;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
	public static final String itsMagicDataPath = "Android/data/com.itsmagic.engine";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		init();
        setContentView(R.layout.activity_main);
		
		MaterialButton openProjectButton = findViewById(R.id.main_open_project_button);
		openProjectButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				StorageUtils.askForDirectoryAccess(
					MainActivity.this, itsMagicDataPath, 0, new StorageUtils.OnAllowFolderAccess() {
						@Override
						public void onAllowFolderAccess(int rCode, Uri path) {
							final ArrayAdapter<String> projectsAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_item);
							final ArrayList<ProjectModel> projectsList = new ArrayList<ProjectModel>();
							
							DocumentFile fromTreeUri = DocumentFile.fromTreeUri(getApplicationContext(), Uri.parse(path.toString() + "%2Ffiles%2FITsMagic%2FProjects"));
							DocumentFile[] documentFiles = fromTreeUri.listFiles();
							
							for (int i = 1; i < documentFiles.length; i++) {
								File projectDir = new File(DocumentFileUtils.getAbsolutePath(documentFiles[i], MainActivity.this));
								
								projectsAdapter.add(projectDir.getName());
								projectsList.add(new ProjectModel(
									projectDir.getAbsolutePath(),
									projectDir.getName()
								));
							}
							
							AlertDialog dialog = new MaterialAlertDialogBuilder(MainActivity.this)
								.setTitle("Pick a project")
								.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								}).setAdapter(projectsAdapter, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										Intent launchIntent = new Intent(MainActivity.this, EditorActivity.class);
										launchIntent.putExtra("projectInfo", projectsList.get(which));
										
										startActivity(launchIntent);
									}
							}).create();
							
							dialog.show();
						}
					}
				);
			}
		});
    }
	
	private final void init() {
		SharedPreferenceUtils.initializeInstance(this);
		StorageUtils.setupStorageHelper(this);
	}
}
