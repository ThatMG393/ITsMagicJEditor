package com.itsmagic.code.jeditor.fragments.editor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.itsmagic.code.jeditor.R;

public class EditorTerminalView extends AppCompatActivity {
    public static void start(Context context) {
        Intent launchIntent = new Intent(context, EditorTerminalView.class);
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
                .replace(R.id.fragment_base_frame, new InnerEditorTerminalView())
                .commit();
    }

    public static class InnerEditorTerminalView extends Fragment {
        // private TerminalView terminalView;
        // private TerminalViewClient termViewClient;

        // private TerminalSessionClient termSessionClient;

        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            TextView tv = new TextView(inflater.getContext());
			tv.setLayoutParams(
				new ViewGroup.LayoutParams(
					ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.MATCH_PARENT
				)
			);
			tv.setText("Function not implemented!");
			
			return tv;
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            init();
        }

        private final void init() {
            // terminalView = requireView().findViewById(R.id.terminal_main);
        }
    }
}
