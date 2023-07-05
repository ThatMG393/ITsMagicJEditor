package com.itsmagic.code.jeditor.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.itsmagic.code.jeditor.activities.base.BaseActivity;

public class CrashActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		System.out.println("AM I ALAIVE??");
		
		String stacktrace = getIntent().getStringExtra("crashStacktrace");
		
		new MaterialAlertDialogBuilder(getApplicationContext())
			.setTitle("CRASH")
			.setMessage(stacktrace)
			.create().show();
    }
}
