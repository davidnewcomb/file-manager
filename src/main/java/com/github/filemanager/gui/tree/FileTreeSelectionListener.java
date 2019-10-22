package com.github.filemanager.gui.tree;

import java.io.File;
import java.util.Arrays;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import com.github.filemanager.FileSorter;
import com.github.filemanager.FmModel;

public class FileTreeSelectionListener implements TreeSelectionListener {

	private FmModel model;

	public FileTreeSelectionListener(FmModel _model) {
		this.model = _model;
	}

	@Override
	public void valueChanged(TreeSelectionEvent tse) {

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();

		File selectedFile = (File) node.getUserObject();
		model.setCurrentFile(selectedFile);

		if (node.getChildCount() == 0) {
			File[] files = selectedFile.listFiles();
			Arrays.sort(files, new FileSorter());
			for (File file : files) {
				if (file.isDirectory()) {
					node.add(new DefaultMutableTreeNode(file));
				}
			}
		}
		// // xx - send node or file to model as change?
		// fileManager.showChildren(node);
		// File payload = (File) node.getUserObject();
		// gui.updateFile(payload);
	}
}
