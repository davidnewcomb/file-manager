package com.github.filemanager.gui.tree;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;

import com.github.filemanager.FileManager;
import com.github.filemanager.FmModel;
import com.github.filemanager.gui.Gui;

public class FileTree extends JTree {

	public FileTree(Gui gui, FmModel treeModel, FileManager fileManager) {
		super(treeModel.getTreeModel());

		setRootVisible(false);
		TreeSelectionListener treeSelectionListener = new FileTreeSelectionListener(gui, fileManager);
		addTreeSelectionListener(treeSelectionListener);
		setCellRenderer(new FileTreeCellRenderer());
		expandRow(0);
		setVisibleRowCount(15);
	}

}
