package com.itsmagic.code.jeditor.fragments.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.preference.PreferenceFragmentCompat;
import com.itsmagic.code.jeditor.R;

public class MainSettingsFragment extends AppCompatActivity {
	public static void start(Context context) {
		Intent launchIntent = new Intent(context, MainSettingsFragment.class);
		launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		context.startActivity(launchIntent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceBundle) {
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.activity_fragment_base);
		setSupportActionBar(findViewById(R.id.fragment_base_toolbar));
		
   	 getSupportFragmentManager()
			.beginTransaction()
			.replace(R.id.fragment_base_frame, new InnerMainSettingsFragment())
			.commit();
	}

	public static class InnerMainSettingsFragment extends PreferenceFragmentCompat {
		@Override
		public void onCreatePreferences(Bundle savedInstanceBundle, String rootKey) {
			setPreferencesFromResource(R.xml.xml_main_preference, rootKey);
		}
	}
}
