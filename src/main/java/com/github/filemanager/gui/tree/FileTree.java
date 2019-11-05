package com.github.filemanager.gui.tree;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;

import com.github.filemanager.FmModel;

public class FileTree extends JTree {

	private FmModel model;

	public FileTree(FmModel _model) {
		super(_model.getTreeModel());
		model = _model;
		setRootVisible(false);
		setVisibleRowCount(7);
		// putClientProperty("JTree.lineStyle", "Horizontal");
		setCellRenderer(new FileTreeCellRenderer());

		TreeSelectionListener treeSelectionListener = new FileTreeSelectionListener(model);
		addTreeSelectionListener(treeSelectionListener);

		expandRow(0);

		addTreeWillExpandListener(new FileTreeWillExpandListener());
	}

}
