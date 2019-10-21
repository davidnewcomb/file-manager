package com.github.filemanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.util.Arrays;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gui extends JPanel {

	private static final Logger L = LoggerFactory.getLogger(Gui.class);
	private static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	private JProgressBar progressBar;
	private AddressBar addressBar;

	private FileTreeView fileTreeView;

	private DefaultTreeModel treeModel;

	private FileListView fileListView;

	private FileDetailsView fileDetailsView;
	private FmToolBar toolBar;

	public Gui(final FileManager fileManager) {
		super(new BorderLayout(3, 3));

		setBorder(new EmptyBorder(5, 5, 5, 5));

		JPanel detailView = new JPanel(new BorderLayout(3, 3));
		fileListView = new FileListView(this);

		JScrollPane tableScroll = new JScrollPane(fileListView);
		Dimension d = tableScroll.getPreferredSize();
		tableScroll.setPreferredSize(new Dimension((int) d.getWidth(), (int) d.getHeight() / 2));
		detailView.add(tableScroll, BorderLayout.CENTER);

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

		fileTreeView = new FileTreeView(this, treeModel, fileManager);
		JScrollPane treeScroll = new JScrollPane(fileTreeView);

		Dimension preferredSize = treeScroll.getPreferredSize();
		Dimension widePreferred = new Dimension(200, (int) preferredSize.getHeight());
		treeScroll.setPreferredSize(widePreferred);

		fileDetailsView = new FileDetailsView();

		toolBar = new FmToolBar(fileManager, this, treeModel);

		JPanel fileView = new JPanel(new BorderLayout(3, 3));

		fileView.add(toolBar, BorderLayout.NORTH);
		fileView.add(fileDetailsView, BorderLayout.CENTER);

		detailView.add(fileView, BorderLayout.SOUTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, detailView);
		add(splitPane, BorderLayout.CENTER);

		JPanel simpleOutput = new JPanel(new BorderLayout(3, 3));
		progressBar = new JProgressBar();
		simpleOutput.add(progressBar, BorderLayout.EAST);
		progressBar.setVisible(false);

		add(simpleOutput, BorderLayout.SOUTH);

		addressBar = new AddressBar();
		add(addressBar, BorderLayout.NORTH);
	}

	public void updateFile(File file) {
		fileDetailsView.updatetFileDetails(file);
		toolBar.updateFile(file);
		addressBar.updateFile(file);
	}

	public void showRootFile() {
		// ensure the main files are displayed
		fileTreeView.setSelectionInterval(0, 0);
	}

	public void uiShowChildrenOn() {
		fileTreeView.setEnabled(false);
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
	}

	public void uiShowChildrenOff() {
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
		fileTreeView.setEnabled(true);
	}

	public TreePath findTreePath(File find) {
		for (int ii = 0; ii < fileTreeView.getRowCount(); ii++) {
			TreePath treePath = fileTreeView.getPathForRow(ii);
			Object object = treePath.getLastPathComponent();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) object;
			File nodeFile = (File) node.getUserObject();

			if (nodeFile == find) {
				return treePath;
			}
		}
		// TODO is this possible?
		return null;
	}

	public void updateTableFiles(File[] files) {
		L.info("Gui:updateTableFiles");
		fileListView.updateFiles(files);
	}

	public void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(this, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
	}

	public void showThrowable(Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(this, t.toString(), t.getMessage(), JOptionPane.ERROR_MESSAGE);
		this.repaint();
	}

}
