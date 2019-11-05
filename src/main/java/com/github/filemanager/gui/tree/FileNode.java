package com.github.filemanager.gui.tree;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.filemanager.DirectoriesFilter;

public class FileNode extends DefaultMutableTreeNode {
	private static final Logger L = LoggerFactory.getLogger(FileNode.class);
	private static final DirectoriesFilter FILTER = new DirectoriesFilter();
	private File node;

	public FileNode(File file) {
		super(file);
		node = file;
		allowsChildren = internalAllowsChildren(file);
	}

	@Override
	public boolean isLeaf() {
		L.info(node.getAbsoluteFile() + " " + node.listFiles().length);
		if (node.isDirectory() && node.listFiles(FILTER).length > 0) {
			return false;
		}
		return true;
	}

	public File getFile() {
		return node;
	}

	// scope for expanding tar etc
	private boolean internalAllowsChildren(File f) {
		return f.isDirectory();
	}

}
