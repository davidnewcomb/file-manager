package com.github.filemanager;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;

import com.github.filemanager.gui.Gui;

public class ShowChildrenSwingWorker extends SwingWorker<Void, File> {
	private static FileSystemView fileSystemView = FileSystemView.getFileSystemView();;

	private Gui gui;
	private DefaultMutableTreeNode node;

	public ShowChildrenSwingWorker(Gui gui, DefaultMutableTreeNode node) {
		this.gui = gui;
		this.node = node;
	}

	@Override
	public Void doInBackground() {
		gui.uiShowChildrenOn();
		File file = (File) node.getUserObject();
		if (file.isDirectory()) {
			File[] files = fileSystemView.getFiles(file, true);
			if (node.isLeaf()) {
				for (File child : files) {
					if (child.isDirectory()) {
						publish(child);
					}
				}
			}
			gui.updateTableFiles(files);
		}
		return null;
	}

	@Override
	protected void process(List<File> chunks) {
		for (File child : chunks) {
			node.add(new DefaultMutableTreeNode(child));
		}
	}

	@Override
	protected void done() {
		gui.uiShowChildrenOff();
	}
}