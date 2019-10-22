package com.github.filemanager.gui.tree;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;

import com.github.filemanager.FileManager;
import com.github.filemanager.gui.Gui;

public class FileTree extends JTree {

	public FileTree(Gui gui, DefaultTreeModel treeModel, FileManager fileManager) {
		super(treeModel);

		setRootVisible(false);
		TreeSelectionListener treeSelectionListener = new FileTreeSelectionListener(gui, fileManager);
		addTreeSelectionListener(treeSelectionListener);
		setCellRenderer(new FileTreeCellRenderer());
		expandRow(0);
		setVisibleRowCount(15);
	}

}
