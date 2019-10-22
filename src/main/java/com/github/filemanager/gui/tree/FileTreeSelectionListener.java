package com.github.filemanager.gui.tree;

import java.io.File;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.github.filemanager.FileManager;
import com.github.filemanager.gui.Gui;

public class FileTreeSelectionListener implements TreeSelectionListener {

	private FileManager fileManager;
	private Gui gui;

	public FileTreeSelectionListener(Gui gui, FileManager fileManager) {
		this.fileManager = fileManager;
		this.gui = gui;
	}

	@Override
	public void valueChanged(TreeSelectionEvent tse) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
		fileManager.showChildren(node);
		File payload = (File) node.getUserObject();
		gui.updateFile(payload);
	}
}