package com.itsmagic.code.jeditor.fragments.editor;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;

import androidx.documentfile.provider.DocumentFile;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewAdapter;
import com.amrdeveloper.treeview.TreeViewHolderFactory;
import com.anggrayudi.storage.file.DocumentFileUtils;
import com.itsmagic.code.jeditor.R;
import com.itsmagic.code.jeditor.lsp.LSPManager;
import com.itsmagic.code.jeditor.utils.UriUtils;

import com.itsmagic.code.jeditor.viewholders.treeview.FileViewHolder;
import com.itsmagic.code.jeditor.viewholders.treeview.FolderViewHolder;
import java.util.ArrayList;
import java.util.List;

public class EditorTreeViewFragment extends Fragment {
	private Uri projectPath;
	
	private HorizontalScrollView recyclerScrollView;
	private RecyclerView recyclerView;
	private TreeViewAdapter treeAdapter;
	
	public EditorTreeViewFragment() {
		this.projectPath = Uri.parse(LSPManager.getInstance().getCurrentProject().projectPath + "%2FFiles");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
		init();
		return recyclerScrollView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceBundle) {
		super.onViewCreated(view, savedInstanceBundle);
		
		List<TreeNode> nodes = new ArrayList<TreeNode>();
		nodes.add(traverseFileSystem(projectPath));
		
		treeAdapter.updateTreeNodes(nodes);
	}
	
	private void init() {
		TreeViewHolderFactory treeFactory = (view, layout) -> {
			if (layout == R.layout.tree_directory_view) return new FolderViewHolder(view);
			return new FileViewHolder(view);
		};
		
		treeAdapter = new TreeViewAdapter(treeFactory);
		treeAdapter.setTreeNodeClickListener(new TreeViewAdapter.OnTreeNodeClickListener() {
			@Override
			public void onTreeNodeClick(TreeNode node, View treeView) {
				callOnNodeClickListeners(node, treeView);
			}
		});
		
		recyclerView = new RecyclerView(getContext());
		recyclerView.setAdapter(treeAdapter);
		recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
		recyclerView.setVerticalScrollBarEnabled(true);
		recyclerView.setLayoutParams(
			new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT
			)
		);
		
		recyclerScrollView = new HorizontalScrollView(getContext());
		recyclerScrollView.setFillViewport(true);
		recyclerScrollView.setLayoutParams(
			new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT
			)
		);
		
		recyclerScrollView.addView(recyclerView);
	}
	
	private TreeNode traverseFileSystem(Uri path) {
		DocumentFile directory = DocumentFile.fromSingleUri(requireActivity().getApplicationContext(), path);
		
		if (directory.isDirectory()) {
			TreeNode directoryNode = new TreeNode(DocumentFileUtils.getAbsolutePath(directory, requireActivity().getApplicationContext()), R.layout.tree_directory_view);
			
			DocumentFile[] files = UriUtils.listFileTree(requireActivity().getApplicationContext(), path);
			if (files != null) {
				for (int i = 0; i < files.length; i++) {
					if (i > 15) break;
					directoryNode.addChild(traverseFileSystem(files[i].getUri()));
				}
			}
			
			return directoryNode;
		} else {
			TreeNode fileNode = new TreeNode(DocumentFileUtils.getAbsolutePath(directory, requireActivity().getApplicationContext()), R.layout.tree_file_view);
			return fileNode;
		}
	}
	
	private ArrayList<TreeViewAdapter.OnTreeNodeClickListener> nodeClickListeners = new ArrayList<TreeViewAdapter.OnTreeNodeClickListener>();
	public void addTreeNodeListener(TreeViewAdapter.OnTreeNodeClickListener listener) {
		nodeClickListeners.add(listener);
	}
	public void removeTreeNodeListener(TreeViewAdapter.OnTreeNodeClickListener listener) {
		nodeClickListeners.remove(listener);
	}
	public void callOnNodeClickListeners(TreeNode node, View treeView) {
		nodeClickListeners.forEach((listener) -> { listener.onTreeNodeClick(node, treeView); });
	}
}
