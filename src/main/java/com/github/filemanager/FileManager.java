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
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;
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

	/** Title of the application */
	public static final String APP_TITLE = "FileMan";
	/** Used to open/edit/print files. */
	private Desktop desktop;
	/** Provides nice icons and names for files. */
	private FileSystemView fileSystemView;

	/** currently selected File. */
	private File currentFile;

	/** Main GUI container */
	private JPanel gui;

	/** File-system tree. Built Lazily */
	private JTree tree;
	private DefaultTreeModel treeModel;

	/** Directory listing */
	private JTable table;
	private JProgressBar progressBar;
	/** Table model for File[]. */
	private FileTableModel fileTableModel;
	private ListSelectionListener listSelectionListener;
	private boolean cellSizesSet = false;
	private int rowIconPadding = 6;

	/* File controls. */
	private JButton openFile;
	private JButton printFile;
	private JButton editFile;
	private JButton deleteFile;
	private JButton newFile;
	private JButton copyFile;
	/* File details. */
	private JLabel fileName;
	private JTextField path;
	private JLabel date;
	private JLabel size;
	private JCheckBox readable;
	private JCheckBox writable;
	private JCheckBox executable;
	private JRadioButton isDirectory;
	private JRadioButton isFile;

	/*
	 * GUI options/containers for new File/Directory creation. Created lazily.
	 */
	private JPanel newFilePanel;
	private JRadioButton newTypeFile;
	private JTextField name;

	public Container getGui() {
		if (gui == null) {
			gui = new JPanel(new BorderLayout(3, 3));
			gui.setBorder(new EmptyBorder(5, 5, 5, 5));

			fileSystemView = FileSystemView.getFileSystemView();
			desktop = Desktop.getDesktop();

			JPanel detailView = new JPanel(new BorderLayout(3, 3));
			// fileTableModel = new FileTableModel();

			table = new JTable();
			table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			table.setAutoCreateRowSorter(true);
			table.setShowVerticalLines(false);

			listSelectionListener = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent lse) {
					int row = table.getSelectionModel().getLeadSelectionIndex();
					setFileDetails(((FileTableModel) table.getModel()).getFile(row));
				}
			};
			table.getSelectionModel().addListSelectionListener(listSelectionListener);
			JScrollPane tableScroll = new JScrollPane(table);
			Dimension d = tableScroll.getPreferredSize();
			tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
			detailView.add(tableScroll, BorderLayout.CENTER);

			// the File tree
			DefaultMutableTreeNode root = new DefaultMutableTreeNode();
			treeModel = new DefaultTreeModel(root);

			TreeSelectionListener treeSelectionListener = new TreeSelectionListener() {
				@Override
				public void valueChanged(TreeSelectionEvent tse) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) tse.getPath().getLastPathComponent();
					showChildren(node);
					setFileDetails((File) node.getUserObject());
				}
			};

			// show the file system roots.
			File[] roots = fileSystemView.getRoots();
			for (File fileSystemRoot : roots) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
				root.add(node);
				// showChildren(node);
				//
				File[] files = fileSystemView.getFiles(fileSystemRoot, true);
				for (File file : files) {
					if (file.isDirectory()) {
						node.add(new DefaultMutableTreeNode(file));
					}
				}
				//
			}

			tree = new JTree(treeModel);
			tree.setRootVisible(false);
			tree.addTreeSelectionListener(treeSelectionListener);
			tree.setCellRenderer(new FileTreeCellRenderer());
			tree.expandRow(0);
			JScrollPane treeScroll = new JScrollPane(tree);

			// as per trashgod tip
			tree.setVisibleRowCount(15);

			Dimension preferredSize = treeScroll.getPreferredSize();
			Dimension widePreferred = new Dimension(200, (int) preferredSize.getHeight());
			treeScroll.setPreferredSize(widePreferred);

			// details for a File
			JPanel fileMainDetails = new JPanel(new BorderLayout(4, 2));
			fileMainDetails.setBorder(new EmptyBorder(0, 6, 0, 6));

			JPanel fileDetailsLabels = new JPanel(new GridLayout(0, 1, 2, 2));
			fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);

			JPanel fileDetailsValues = new JPanel(new GridLayout(0, 1, 2, 2));
			fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);

			fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
			fileName = new JLabel();
			fileDetailsValues.add(fileName);
			fileDetailsLabels.add(new JLabel("Path/name", JLabel.TRAILING));
			path = new JTextField(5);
			path.setEditable(false);
			fileDetailsValues.add(path);
			fileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
			date = new JLabel();
			fileDetailsValues.add(date);
			fileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
			size = new JLabel();
			fileDetailsValues.add(size);
			fileDetailsLabels.add(new JLabel("Type", JLabel.TRAILING));

			JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 0));
			isDirectory = new JRadioButton("Directory");
			isDirectory.setEnabled(false);
			flags.add(isDirectory);

			isFile = new JRadioButton("File");
			isFile.setEnabled(false);
			flags.add(isFile);
			fileDetailsValues.add(flags);

			int count = fileDetailsLabels.getComponentCount();
			for (int ii = 0; ii < count; ii++) {
				fileDetailsLabels.getComponent(ii).setEnabled(false);
			}

			JToolBar toolBar = new JToolBar();
			// mnemonics stop working in a floated toolbar
			toolBar.setFloatable(false);

			openFile = new JButton("Open");
			openFile.setMnemonic('o');

			openFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					try {
						desktop.open(currentFile);
					} catch (Throwable t) {
						showThrowable(t);
					}
					gui.repaint();
				}
			});
			toolBar.add(openFile);

			editFile = new JButton("Edit");
			editFile.setMnemonic('e');
			editFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					try {
						desktop.edit(currentFile);
					} catch (Throwable t) {
						showThrowable(t);
					}
				}
			});
			toolBar.add(editFile);

			printFile = new JButton("Print");
			printFile.setMnemonic('p');
			printFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					try {
						desktop.print(currentFile);
					} catch (Throwable t) {
						showThrowable(t);
					}
				}
			});
			toolBar.add(printFile);

			// Check the actions are supported on this platform!
			openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
			editFile.setEnabled(desktop.isSupported(Desktop.Action.EDIT));
			printFile.setEnabled(desktop.isSupported(Desktop.Action.PRINT));

			toolBar.addSeparator();

			newFile = new JButton("New");
			newFile.setMnemonic('n');
			newFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					newFile();
				}
			});
			toolBar.add(newFile);

			copyFile = new JButton("Copy");
			copyFile.setMnemonic('c');
			copyFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					showErrorMessage("'Copy' not implemented.", "Not implemented.");
				}
			});
			toolBar.add(copyFile);

			JButton renameFile = new JButton("Rename");
			renameFile.setMnemonic('r');
			renameFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					renameFile();
				}
			});
			toolBar.add(renameFile);

			deleteFile = new JButton("Delete");
			deleteFile.setMnemonic('d');
			deleteFile.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent ae) {
					deleteFile();
				}
			});
			toolBar.add(deleteFile);

			toolBar.addSeparator();

			readable = new JCheckBox("Read  ");
			readable.setMnemonic('a');
			// readable.setEnabled(false);
			toolBar.add(readable);

			writable = new JCheckBox("Write  ");
			writable.setMnemonic('w');
			// writable.setEnabled(false);
			toolBar.add(writable);

			executable = new JCheckBox("Execute");
			executable.setMnemonic('x');
			// executable.setEnabled(false);
			toolBar.add(executable);

			JPanel fileView = new JPanel(new BorderLayout(3, 3));

			fileView.add(toolBar, BorderLayout.NORTH);
			fileView.add(fileMainDetails, BorderLayout.CENTER);

			detailView.add(fileView, BorderLayout.SOUTH);

			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, detailView);
			gui.add(splitPane, BorderLayout.CENTER);

			JPanel simpleOutput = new JPanel(new BorderLayout(3, 3));
			progressBar = new JProgressBar();
			simpleOutput.add(progressBar, BorderLayout.EAST);
			progressBar.setVisible(false);

			gui.add(simpleOutput, BorderLayout.SOUTH);

		}
		return gui;
	}

	public void showRootFile() {
		// ensure the main files are displayed
		tree.setSelectionInterval(0, 0);
	}

	private TreePath findTreePath(File find) {
		for (int ii = 0; ii < tree.getRowCount(); ii++) {
			TreePath treePath = tree.getPathForRow(ii);
			Object object = treePath.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			File nodeFile = (File) node.getUserObject();

			if (nodeFile == find) {
				return treePath;
			}
		}
		// not found!
		return null;
	}

	private void renameFile() {
		if (currentFile == null) {
			showErrorMessage("No file selected to rename.", "Select File");
			return;
		}

		String renameTo = JOptionPane.showInputDialog(gui, "New Name");
		if (renameTo != null) {
			try {
				boolean directory = currentFile.isDirectory();
				TreePath parentPath = findTreePath(currentFile.getParentFile());
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

				boolean renamed = currentFile.renameTo(new File(currentFile.getParentFile(), renameTo));
				if (renamed) {
					if (directory) {
						// rename the node..

						// delete the current node..
						TreePath currentPath = findTreePath(currentFile);
						L.info(currentPath == null ? "(null)" : currentPath.toString());
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath
								.getLastPathComponent();

						treeModel.removeNodeFromParent(currentNode);

						// add a new node..
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" + currentFile + "' could not be renamed.";
					showErrorMessage(msg, "Rename Failed");
				}
			} catch (Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
	}

	private void deleteFile() {
		if (currentFile == null) {
			showErrorMessage("No file selected for deletion.", "Select File");
			return;
		}

		int result = JOptionPane.showConfirmDialog(gui, "Are you sure you want to delete this file?", "Delete File",
				JOptionPane.ERROR_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			try {
				L.info("currentFile: " + currentFile);
				TreePath parentPath = findTreePath(currentFile.getParentFile());
				L.info("parentPath: " + parentPath);
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();
				L.info("parentNode: " + parentNode);

				boolean directory = currentFile.isDirectory();
				boolean deleted = currentFile.delete();
				if (deleted) {
					if (directory) {
						// delete the node..
						TreePath currentPath = findTreePath(currentFile);
						L.info(currentPath == null ? "(null)" : currentPath.toString());
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath
								.getLastPathComponent();

						treeModel.removeNodeFromParent(currentNode);
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" + currentFile + "' could not be deleted.";
					showErrorMessage(msg, "Delete Failed");
				}
			} catch (Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
	}

	private void newFile() {
		if (currentFile == null) {
			showErrorMessage("No location selected for new file.", "Select Location");
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

					TreePath parentPath = findTreePath(parentFile);
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) parentPath.getLastPathComponent();

					if (file.isDirectory()) {
						// add the new node..
						DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file);

						TreePath currentPath = findTreePath(currentFile);
						DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) currentPath
								.getLastPathComponent();
						L.info("TODO is currentNode needed: " + currentNode);

						treeModel.insertNodeInto(newNode, parentNode, parentNode.getChildCount());
					}

					showChildren(parentNode);
				} else {
					String msg = "The file '" + file + "' could not be created.";
					showErrorMessage(msg, "Create Failed");
				}
			} catch (Throwable t) {
				showThrowable(t);
			}
		}
		gui.repaint();
	}

	private void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(gui, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
	}

	private void showThrowable(Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(gui, t.toString(), t.getMessage(), JOptionPane.ERROR_MESSAGE);
		gui.repaint();
	}

	/** Update the table on the EDT */
	private void setTableData(final File[] files) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (fileTableModel == null) {
					fileTableModel = new FileTableModel();
					table.setModel(fileTableModel);
				}
				table.getSelectionModel().removeListSelectionListener(listSelectionListener);
				fileTableModel.setFiles(files);
				table.getSelectionModel().addListSelectionListener(listSelectionListener);
				if (!cellSizesSet) {
					Icon icon = fileSystemView.getSystemIcon(files[0]);

					// size adjustment to better account for icons
					table.setRowHeight(icon.getIconHeight() + rowIconPadding);

					setColumnWidth(FileTableModel.ICON, -1);
					setColumnWidth(FileTableModel.SIZE, 60);
					table.getColumnModel().getColumn(FileTableModel.FILE_NAME).setMaxWidth(120);
					setColumnWidth(FileTableModel.LAST_MODIFIED, -1);
					setColumnWidth(FileTableModel.READ, -1);
					setColumnWidth(FileTableModel.WRITE, -1);
					setColumnWidth(FileTableModel.EXECUTE, -1);
					setColumnWidth(FileTableModel.DIRECTORY, -1);
					setColumnWidth(FileTableModel.FILE, -1);

					cellSizesSet = true;
				}
			}
		});
	}

	private void setColumnWidth(int column, int width) {
		TableColumn tableColumn = table.getColumnModel().getColumn(column);
		if (width < 0) {
			// use the preferred width of the header..
			JLabel label = new JLabel((String) tableColumn.getHeaderValue());
			Dimension preferred = label.getPreferredSize();
			// altered 10->14 as per camickr comment.
			width = (int) preferred.getWidth() + 14;
		}
		tableColumn.setPreferredWidth(width);
		tableColumn.setMaxWidth(width);
		tableColumn.setMinWidth(width);
	}

	/**
	 * Add the files that are contained within the directory of this node.
	 * Thanks to Hovercraft Full Of Eels.
	 */
	private void showChildren(final DefaultMutableTreeNode node) {
		tree.setEnabled(false);
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);

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
				progressBar.setIndeterminate(false);
				progressBar.setVisible(false);
				tree.setEnabled(true);
			}
		};
		worker.execute();
	}

	/** Update the File details view with the details of this File. */
	private void setFileDetails(File file) {
		currentFile = file;
		Icon icon = fileSystemView.getSystemIcon(file);
		fileName.setIcon(icon);
		fileName.setText(fileSystemView.getSystemDisplayName(file));
		path.setText(file.getPath());
		date.setText(new Date(file.lastModified()).toString());
		size.setText(file.length() + " bytes");
		readable.setSelected(file.canRead());
		writable.setSelected(file.canWrite());
		executable.setSelected(file.canExecute());
		isDirectory.setSelected(file.isDirectory());

		isFile.setSelected(file.isFile());

		JFrame f = (JFrame) gui.getTopLevelAncestor();
		if (f != null) {
			f.setTitle(APP_TITLE + " :: " + fileSystemView.getSystemDisplayName(file));
		}

		gui.repaint();
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// Significantly improves the look of the output in
					// terms of the file names returned by FileSystemView!
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception weTried) {
				}
				JFrame f = new JFrame(APP_TITLE);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				FileManager fileManager = new FileManager();
				f.setContentPane(fileManager.getGui());

				try {
					URL urlBig = fileManager.getClass().getResource("fm-icon-32x32.png");
					URL urlSmall = fileManager.getClass().getResource("fm-icon-16x16.png");
					ArrayList<Image> images = new ArrayList<Image>();
					images.add(ImageIO.read(urlBig));
					images.add(ImageIO.read(urlSmall));
					f.setIconImages(images);
				} catch (Exception weTried) {
				}

				f.pack();
				f.setLocationByPlatform(true);
				f.setMinimumSize(f.getSize());
				f.setVisible(true);

				fileManager.showRootFile();
			}
		});
	}
}
