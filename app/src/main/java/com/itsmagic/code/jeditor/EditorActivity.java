package com.itsmagic.code.jeditor;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.itsmagic.code.jeditor.adapters.TabEditorAdapter;
import com.itsmagic.code.jeditor.fragments.editor.EditorTabFragment;
import com.itsmagic.code.jeditor.models.ProjectModel;
import com.itsmagic.code.jeditor.utils.EditorUtils;
import com.itsmagic.code.jeditor.utils.SharedPreferenceUtils;

import io.github.rosemoe.sora.langs.textmate.TextMateLanguage;
import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.ThemeRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;
import io.github.rosemoe.sora.widget.CodeEditor;

public class EditorActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {
	private ProjectModel projectInfo;
	
	private Toolbar layoutToolbar;
	private DrawerLayout drawerLayout;
	private TabLayout tabLayout;
	private ViewPager2 viewPager;
	private NavigationView navigationView;
	
	private TabEditorAdapter tabAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		init();
		
		/*
		tabAdapter.newTab(new EditorTabFragment(
			getExternalFilesDir("").getAbsolutePath() + "/hello.java"
		), "hello.java");
		
		tabAdapter.newTab(new EditorTabFragment(
			getExternalFilesDir("").getAbsolutePath() + "/jejhello.java"
		), "n.java");
		
		tabAdapter.newTab(new EditorTabFragment(
			getExternalFilesDir("").getAbsolutePath() + "/hejjfllo.java"
		), "jdj.java");
		*/
	}
	
	private final void init() {
		projectInfo = getIntent().getSerializableExtra("projectInfo", ProjectModel.class);
		
		layoutToolbar = findViewById(R.id.editor_toolbar);
		drawerLayout = findViewById(R.id.editor_drawerLayout);
		tabLayout = findViewById(R.id.editor_tabLayout);
		viewPager = findViewById(R.id.editor_viewPager);
		navigationView = findViewById(R.id.editor_navigationView);
		
		// Drawer init
		setSupportActionBar(layoutToolbar);
		ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
			this, drawerLayout, layoutToolbar, R.string.open_navigation_view, R.string.close_navigation_view
		);
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();
		
		// Tab init
		tabAdapter = new TabEditorAdapter(getLifecycle(), getSupportFragmentManager(), tabLayout, viewPager);
		viewPager.setAdapter(tabAdapter);
		viewPager.setUserInputEnabled(false);
		
		tabLayout.setOnTabSelectedListener(this);
	}
	
	private void showPopupMenu(View anchor, int position) {
		PopupMenu menu = new PopupMenu(getApplicationContext(), anchor);
		MenuInflater inflater = menu.getMenuInflater();
		inflater.inflate(R.menu.editor_tab_popup, menu.getMenu());
		
		menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				if (item.getItemId() ==  R.id.editor_removeTab) {
					System.out.println("Removing tab in " + position);
					tabAdapter.removeTab(position);
				} else {
					return false;
				}
				
				return true;
			}
		});
		
		menu.show();
	}
	
	@Override
	public void onTabSelected(TabLayout.Tab tab) {
		viewPager.setCurrentItem(tab.getPosition());
	}
	
	@Override
	public void onTabReselected(TabLayout.Tab tab) {
		showPopupMenu(((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition()), tab.getPosition());
	}
	
	@Override
	public void onTabUnselected(TabLayout.Tab tab) { }
	
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		return true;
	}
}
