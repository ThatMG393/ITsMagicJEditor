package com.itsmagic.code.jeditor.adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.tabs.TabLayout;
import com.itsmagic.code.jeditor.fragments.editor.EditorTabFragment;
import java.util.ArrayList;

public class TabEditorAdapter extends FragmentStateAdapter {
	public ArrayList<EditorTabFragment> fragments = new ArrayList<EditorTabFragment>();
	
	private TabLayout tabLayout;
	private ViewPager2 viewPager;
	
	public TabEditorAdapter(Lifecycle lifecycle, FragmentManager fragmentManager, TabLayout tabLayout, ViewPager2 viewPager) {
		super(fragmentManager, lifecycle);
		this.tabLayout = tabLayout;
		this.viewPager = viewPager;
		
		tabLayout.setVisibility(View.GONE);
		animateTabLayoutIfNeeded();
	}
	
	public void newTab(EditorTabFragment fragment, String name) {
		fragments.add(fragment);
		notifyDataSetChanged();
		
		TabLayout.Tab tab = tabLayout.newTab();
		tab.setText(name);
		
		tabLayout.addTab(tab);
		
		animateTabLayoutIfNeeded();
	}
	
	public void removeTab(int position) {
		fragments.remove(position);
		tabLayout.removeTabAt(position);
		
		notifyDataSetChanged();
		animateTabLayoutIfNeeded();
	}
	
	public void animateTabLayoutIfNeeded() {
		if (tabLayout.getTabCount() == 0 && tabLayout.getVisibility() == View.VISIBLE) {
			tabLayout.animate()
				.translationY(-tabLayout.getHeight())
				.setDuration(200)
				.setInterpolator(new AccelerateDecelerateInterpolator())
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						super.onAnimationEnd(animation);
						tabLayout.setVisibility(View.GONE);
					}
				});
		} else if (tabLayout.getTabCount() > 0 && tabLayout.getVisibility() == View.GONE) {
			System.out.println("WGAT");
			System.out.println("WGAT");
			System.out.println("WGAT");
			
			tabLayout.setVisibility(View.VISIBLE);
			tabLayout.animate()
				.setDuration(200)
				.setInterpolator(new AccelerateDecelerateInterpolator())
				.translationY(0)
				.setListener(null);
		}
	}
	
    @Override
    public int getItemCount() {
		return fragments.size();
	}

    @Override
    public Fragment createFragment(int position) {
		return fragments.get(position);
	}
}
