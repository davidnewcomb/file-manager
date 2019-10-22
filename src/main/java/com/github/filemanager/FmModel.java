package com.github.filemanager;

import java.io.File;
import java.util.Arrays;
import java.util.Observable;

import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class FmModel extends Observable {

	private static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	private File currentFile;
	private DefaultTreeModel treeModel;

	public FmModel() {
		// the File tree
		DefaultMutableTreeNode root = new DefaultMutableTreeNode();
		treeModel = new DefaultTreeModel(root);

		// show the file system roots.
		File[] roots = fileSystemView.getRoots();
		for (File fileSystemRoot : roots) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
			root.add(node);
			// fileManager.showChildren(node);

			File[] files = fileSystemView.getFiles(fileSystemRoot, true);
			Arrays.sort(files, new FileSorter());
			for (File file : files) {
				if (file.isDirectory()) {
					node.add(new DefaultMutableTreeNode(file));
				}
			}
		}

	}

	public DefaultTreeModel getTreeModel() {
		return treeModel;
	}

	public File getCurrentFile() {
		return currentFile;
	}

	public void setCurrentFile(File currentFile) {
		this.currentFile = currentFile;
		setChanged();
		notifyObservers(currentFile);
	}

	private DefaultMutableTreeNode getNode(TreePath path) {
		File[] files = (File[]) path.getPath();
		DefaultMutableTreeNode n = (DefaultMutableTreeNode) treeModel.getRoot();
		while (true) {

		}
	}

	public void trimBranch(TreePath path) {
		DefaultMutableTreeNode node = getNode(path);
		node.removeAllChildren();
	}
}
