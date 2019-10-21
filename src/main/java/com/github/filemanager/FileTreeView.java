package com.github.filemanager;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;

public class FileTreeView extends JTree {

	public FileTreeView(Gui gui, DefaultTreeModel treeModel, FileManager fileManager) {
		super(treeModel);

		setRootVisible(false);
		TreeSelectionListener treeSelectionListener = new FileTreeSelectionListener(gui, fileManager);
		addTreeSelectionListener(treeSelectionListener);
		setCellRenderer(new FileTreeCellRenderer());
		expandRow(0);
		setVisibleRowCount(15);
	}

}
