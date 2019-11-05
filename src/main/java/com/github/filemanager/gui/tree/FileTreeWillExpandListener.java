package com.github.filemanager.gui.tree;

import java.io.File;

import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.filemanager.Utils;

public class FileTreeWillExpandListener implements TreeWillExpandListener {

	private static Logger L = LoggerFactory.getLogger(FileTreeWillExpandListener.class);
	private static Utils U = new Utils();

	@Override
	public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
		L.info("treeWillExpand");
		TreePath path = event.getPath();

		// setExpandedState(TreePath path, boolean state)

		FileNode node = (FileNode) path.getLastPathComponent();
		File selectedFile = node.getFile();
		node.removeAllChildren();

		File[] files = U.sortedDirectoryListing(selectedFile);
		for (File file : files) {
			if (file.isDirectory()) {
				node.add(new FileNode(file));
			}
		}
		// setExpandedState(path, true);
	}

	@Override
	public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
		L.info("treeWillCollapse");
	}
}