package com.itsmagic.code.jeditor.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.itsmagic.code.jeditor.R;
import com.itsmagic.code.jeditor.activities.base.BaseActivity;
import com.itsmagic.code.jeditor.adapters.TabEditorAdapter;
import com.itsmagic.code.jeditor.fragments.editor.EditorSettingsFragment;
import com.itsmagic.code.jeditor.fragments.editor.EditorTabFragment;
import com.itsmagic.code.jeditor.fragments.editor.EditorTerminalView;
import com.itsmagic.code.jeditor.fragments.editor.EditorTreeViewFragment;
import com.itsmagic.code.jeditor.lsp.LSPManager;
import com.itsmagic.code.jeditor.models.ProjectModel;
import com.itsmagic.code.jeditor.utils.EditorUtils;

import io.github.rosemoe.sora.langs.textmate.registry.FileProviderRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.GrammarRegistry;
import io.github.rosemoe.sora.langs.textmate.registry.provider.AssetsFileResolver;

import java.io.File;

public class EditorActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {
	private Toolbar layoutToolbar;
	private DrawerLayout drawerLayout;
	private TabLayout tabLayout;
	private ViewPager2 viewPager;
	private NavigationView navigationViewLeft;
	private NavigationView navigationViewRight;
	private TabEditorAdapter tabAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.setUEH();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_editor);
		init();
	}

	private final void init() {
		LSPManager.getInstance().setCurrentProject(getIntent().getSerializableExtra("projectInfo", ProjectModel.class));
		LSPManager.getInstance().registerLSPs();

		layoutToolbar = findViewById(R.id.editor_toolbar);
		drawerLayout = findViewById(R.id.editor_drawerLayout);
		tabLayout = findViewById(R.id.editor_tabLayout);
		viewPager = findViewById(R.id.editor_viewPager);
		navigationViewLeft = findViewById(R.id.editor_navigationViewLeft);
		navigationViewRight = findViewById(R.id.editor_navigationViewRight);

		// Drawer init
		setSupportActionBar(layoutToolbar);
		ActionBarDrawerToggle toggle =
				new ActionBarDrawerToggle(
						this,
						drawerLayout,
						layoutToolbar,
						R.string.open_navigation_view,
						R.string.close_navigation_view);
						
		drawerLayout.addDrawerListener(toggle);
		toggle.syncState();
		
		EditorTreeViewFragment treeViewFragment = new EditorTreeViewFragment();
		treeViewFragment.addTreeNodeListener(new TreeViewAdapter.OnTreeNodeClickListener() {
			@Override
			public void onTreeNodeClick(TreeNode node, View treeView) {
				File nodeFile = new File((String) node.getValue());
				
				if (!nodeFile.isDirectory()) {
					tabAdapter.newTab(
						new EditorTabFragment(getApplicationContext(), nodeFile.getAbsolutePath()), nodeFile.getName()
					);
					
					drawerLayout.closeDrawer(GravityCompat.END);
				}
			}
		});
		
		getSupportFragmentManager()
				.beginTransaction()
				.replace(R.id.editor_treeViewContainer, treeViewFragment)
				.commit();

		// Tab init
		tabAdapter = new TabEditorAdapter(getLifecycle(), getSupportFragmentManager(), tabLayout, viewPager);
		viewPager.setAdapter(tabAdapter);
		viewPager.setUserInputEnabled(false);

		tabLayout.setOnTabSelectedListener(this);
		
		FileProviderRegistry.getInstance().addFileProvider(
			new AssetsFileResolver(
				getAssets()
			)
		);
		
		GrammarRegistry.getInstance().loadGrammars("tm-language/languages.json");
		EditorUtils.loadTMThemes();
		
		LSPManager.getInstance().startLSP("java");
	}

	private void showPopupMenu(View anchor, int position) {
		PopupMenu menu = new PopupMenu(getApplicationContext(), anchor);
		MenuInflater inflater = menu.getMenuInflater();
		inflater.inflate(R.menu.editor_tab_popup, menu.getMenu());

		menu.setOnMenuItemClickListener(
				new PopupMenu.OnMenuItemClickListener() {
					@Override
					public boolean onMenuItemClick(MenuItem item) {
						if (item.getItemId() == R.id.editor_removeTab) {
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
		showPopupMenu(
				((ViewGroup) tabLayout.getChildAt(0)).getChildAt(tab.getPosition()),
				tab.getPosition());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.editor_toolbar_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.editor_toolbar_settings) {
			EditorSettingsFragment.start(getApplicationContext());
		} else if (item.getItemId() == R.id.editor_toolbar_terminal) {
			EditorTerminalView.start(getApplicationContext());
		} else {
			return false;
		}
		
		return true;
	}
	
	@Override
	public void onTabUnselected(TabLayout.Tab tab) {}

	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		return true;
	}
}
