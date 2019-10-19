/*
 * $Id$
 *
 * Copyright 2015 Valentyn Kolesnikov
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.filemanager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic File Manager. Requires 1.6+ for the Desktop &amp; SwingWorker
 * classes, amongst other minor things.
 * 
 * Includes support classes FileTableModel &amp; FileTreeCellRenderer.
 * 
 * TODO Bugs
 * <ul>
 * <li>Still throws occasional AIOOBEs and NPEs, so some update on the EDT must
 * have been missed.
 * <li>Fix keyboard focus issues - especially when functions like rename/delete
 * etc. are called that update nodes &amp; file lists.
 * <li>Needs more testing in general.
 * 
 * TODO Functionality
 * <li>Implement Read/Write/Execute checkboxes
 * <li>Implement Copy
 * <li>Extra prompt for directory delete (camickr suggestion)
 * <li>Add File/Directory fields to FileTableModel
 * <li>Double clicking a directory in the table, should update the tree
 * <li>Move progress bar?
 * <li>Add other file display modes (besides table) in CardLayout?
 * <li>Menus + other cruft?
 * <li>Implement history/back
 * <li>Allow multiple selection
 * <li>Add file search
 * </ul>
 * 
 * @author Andrew Thompson
 * @version 2011-06-01
 */
public class FileManager {

	private static Logger L = LoggerFactory.getLogger(FileManager.class);

	/** Provides nice icons and names for files. */
	private FileSystemView fileSystemView;

	/** currently selected File. */
	// private File currentFile;

	/** Main GUI container */
	private Gui gui;

	/*
	 * GUI options/containers for new File/Directory creation. Created lazily.
	 */
	private JPanel newFilePanel;
	private JRadioButton newTypeFile;
	private JTextField name;

	public FileManager() {
		fileSystemView = FileSystemView.getFileSystemView();
	}

	public Gui getGui() {
		if (gui == null) {
			gui = new Gui(this);
		}
		return gui;
	}

	public void renameFile(DefaultTreeModel treeModel, File currentFile) {
		if (currentFile == null) {
			gui.showErrorMessage("No file selected to rename.", "Select File");
			return;
		}

		String renameTo = JOptionPane.showInputDialog(gui, "New Name");
		if (renameTo != null) {
			try {
				boolean directory = currentFile.isDirectory();
				TreePath parentPath = gui.findTreePath(currentFile.getParentFile());
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

				boolean renamed = currentFile.renameTo(new File(currentFile.getParentFile(), renameTo));
				if (renamed) {
					if (directory) {
						// rename the node..

						// delete the current node..
						TreePath currentPath = gui.findTreePath(currentFile);
						L.info(currentPath == null ? "(null)" : currentPath.toString());
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath
								.getLastPathComponent();

						treeModel.removeNodeFromParent(currentNode);

						// add a new node..
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" + currentFile + "' could not be renamed.";
					gui.showErrorMessage(msg, "Rename Failed");
				}
			} catch (Throwable t) {
				gui.showThrowable(t);
			}
		}
		gui.repaint();
	}

	public void deleteFile(DefaultTreeModel treeModel, File currentFile) {
		if (currentFile == null) {
			gui.showErrorMessage("No file selected for deletion.", "Select File");
			return;
		}

		int result = JOptionPane.showConfirmDialog(gui, "Are you sure you want to delete this file?", "Delete File",
				JOptionPane.ERROR_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				TreePath parentPath = gui.findTreePath(currentFile.getParentFile());
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

				boolean directory = currentFile.isDirectory();
				boolean deleted = currentFile.delete();
				if (deleted) {
					if (directory) {
						// delete the node..
						TreePath currentPath = gui.findTreePath(currentFile);
						L.info(currentPath == null ? "(null)" : currentPath.toString());
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath
								.getLastPathComponent();

						treeModel.removeNodeFromParent(currentNode);
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" + currentFile + "' could not be deleted.";
					gui.showErrorMessage(msg, "Delete Failed");
				}
			} catch (Throwable t) {
				gui.showThrowable(t);
			}
		}
		gui.repaint();
	}

	public void newFile(DefaultTreeModel treeModel, File currentFile) {
		if (currentFile == null) {
			gui.showErrorMessage("No location selected for new file.", "Select Location");
			return;
		}

		if (newFilePanel == null) {
			newFilePanel = new JPanel(new BorderLayout(3, 3));

			JPanel southRadio = new JPanel(new GridLayout(1, 0, 2, 2));
			newTypeFile = new JRadioButton("File", true);
			JRadioButton newTypeDirectory = new JRadioButton("Directory");
			ButtonGroup bg = new ButtonGroup();
			bg.add(newTypeFile);
			bg.add(newTypeDirectory);
			southRadio.add(newTypeFile);
			southRadio.add(newTypeDirectory);

			name = new JTextField(15);

			newFilePanel.add(new JLabel("Name"), BorderLayout.WEST);
			newFilePanel.add(name);
			newFilePanel.add(southRadio, BorderLayout.SOUTH);
		}

		int result = JOptionPane.showConfirmDialog(gui, newFilePanel, "Create File", JOptionPane.OK_CANCEL_OPTION);
		if (result == JOptionPane.OK_OPTION) {
			try {
				boolean created;
				File parentFile = currentFile;
				if (!parentFile.isDirectory()) {
					parentFile = parentFile.getParentFile();
				}
				File file = new File(parentFile, name.getText());
				if (newTypeFile.isSelected()) {
					created = file.createNewFile();
				} else {
					created = file.mkdir();
				}
				if (created) {

					TreePath parentPath = gui.findTreePath(parentFile);
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

					if (file.isDirectory()) {
						// add the new node..
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file);

						TreePath currentPath = gui.findTreePath(currentFile);
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath
								.getLastPathComponent();
						L.info("TODO is currentNode needed: " + currentNode);

						treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" + file + "' could not be created.";
					gui.showErrorMessage(msg, "Create Failed");
				}
			} catch (Throwable t) {
				gui.showThrowable(t);
			}
		}
		gui.repaint();
	}

	/** Update the table on the EDT */
	private void setTableData(final File[] files) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				L.info("FileManager:setTableData:run");
				gui.updateTableFiles(files);
			}
		});

	}

	/**
	 * Add the files that are contained within the directory of this node.
	 * Thanks to Hovercraft Full Of Eels.
	 */
	void showChildren(final DefaultMutableTreeNode node) {
		gui.uiShowChildrenOn();
		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			@Override
			public Void doInBackground() {
				File file = (File) node.getUserObject();
				if (file.isDirectory()) {
					File[] files = fileSystemView.getFiles(file, true); // !!
					if (node.isLeaf()) {
						for (File child : files) {
							if (child.isDirectory()) {
								publish(child);
							}
						}
					}
					setTableData(files);
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
		};
		worker.execute();
	}

	public static boolean copyFile(File from, File to) throws IOException {

		boolean created = to.createNewFile();
		FileChannel fromChannel = null;
		FileChannel toChannel = null;

		if (created) {
			fromChannel = new FileInputStream(from).getChannel();
			toChannel = new FileOutputStream(to).getChannel();

			toChannel.transferFrom(fromChannel, 0, fromChannel.size());

			// set the flags of the to the same as the from
			to.setReadable(from.canRead());
			to.setWritable(from.canWrite());
			to.setExecutable(from.canExecute());

			fromChannel.close();
			toChannel.close();
		}
		return created;
	}

}
