package com.itsmagic.code.jeditor.fragments.editor;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import com.itsmagic.code.jeditor.R;

public class EditorSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceBundle, String rootKey) {
        setPreferencesFromResource(R.xml.xml_editor_preference, rootKey);
    }
}
