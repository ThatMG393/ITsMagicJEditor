package com.itsmagic.code.jeditor.viewholders.treeview;

import android.view.View;

import com.amrdeveloper.treeview.TreeNode;
import com.amrdeveloper.treeview.TreeViewHolder;
import com.google.android.material.textview.MaterialTextView;
import com.itsmagic.code.jeditor.R;

import java.io.File;

public class FileViewHolder extends TreeViewHolder {
    private final MaterialTextView layoutTextView;

    public FileViewHolder(View layout) {
        super(layout);
        this.layoutTextView = layout.findViewById(R.id.tree_file_text);
    }

    @Override
    public void bindTreeNode(TreeNode node) {
        super.bindTreeNode(node);
        File node2File = new File((String) node.getValue());
        layoutTextView.setText(node2File.getName());

        // TODO: Implement icon login using switch statements and file ext
    }
}
