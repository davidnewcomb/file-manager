package com.github.filemanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
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

public class Gui extends JPanel {

	private static final Logger L = LoggerFactory.getLogger(Gui.class);

	private static final int ROW_ICON_PADDING = 6;

	private JProgressBar progressBar;

	/** File-system tree. Built Lazily */
	private JTree tree;
	private DefaultTreeModel treeModel;

	/** Directory listing */
	private JTable table;
	private FileTableModel fileTableModel;
	private FileSystemView fileSystemView;

	// /** currently selected File. */
	// // TODO Duplicated in FileManager
	// private File currentFile;

	private FileDetailsView fileDetailsView;
	private FmToolBar toolBar;

	private ListSelectionListener listSelectionListener;

	public Gui(final FileManager fileManager) {
		super(new BorderLayout(3, 3));
		// this.thiz = this;

		setBorder(new EmptyBorder(5, 5, 5, 5));

		fileSystemView = FileSystemView.getFileSystemView();
		// desktop = Desktop.getDesktop();

		JPanel detailView = new JPanel(new BorderLayout(3, 3));
		// fileTableModel = new FileTableModel();

		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setAutoCreateRowSorter(true);
		table.setShowVerticalLines(false);
		fileTableModel = new FileTableModel();
		table.setModel(fileTableModel);

		Icon icon = fileSystemView.getSystemIcon(new File("/"));

		// size adjustment to better account for icons
		table.setRowHeight(icon.getIconHeight() + ROW_ICON_PADDING);

		setColumnWidth(FileTableModel.ICON, -1);
		setColumnWidth(FileTableModel.SIZE, 60);
		table.getColumnModel().getColumn(FileTableModel.FILE_NAME).setMaxWidth(120);
		setColumnWidth(FileTableModel.LAST_MODIFIED, -1);
		setColumnWidth(FileTableModel.READ, -1);
		setColumnWidth(FileTableModel.WRITE, -1);
		setColumnWidth(FileTableModel.EXECUTE, -1);
		setColumnWidth(FileTableModel.DIRECTORY, -1);
		setColumnWidth(FileTableModel.FILE, -1);

		listSelectionListener = new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent lse) {
				int row = table.getSelectionModel().getLeadSelectionIndex();
				FileTableModel ftm = (FileTableModel) table.getModel();
				File payload = ftm.getFile(row);
				fileDetailsView.updatetFileDetails(payload);
				toolBar.updateFile(payload);
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
				// TODO should be called via listeners
				fileManager.showChildren(node);
				File payload = (File) node.getUserObject();
				fileDetailsView.updatetFileDetails(payload);
				toolBar.updateFile(payload);
			}
		};

		// show the file system roots.
		File[] roots = fileSystemView.getRoots();
		for (File fileSystemRoot : roots) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileSystemRoot);
			root.add(node);
			// showChildren(node);

			File[] files = fileSystemView.getFiles(fileSystemRoot, true);
			for (File file : files) {
				if (file.isDirectory()) {
					node.add(new DefaultMutableTreeNode(file));
				}
			}
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

	}

	public void showRootFile() {
		// ensure the main files are displayed
		tree.setSelectionInterval(0, 0);
	}

	public void setColumnWidth(int column, int width) {
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

	public void uiShowChildrenOn() {
		tree.setEnabled(false);
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
	}

	public void uiShowChildrenOff() {
		progressBar.setIndeterminate(false);
		progressBar.setVisible(false);
		tree.setEnabled(true);
	}

	public TreePath findTreePath(File find) {
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

	public void updateTableFiles(File[] files) {
		L.info("Gui:updateTableFiles");
		table.getSelectionModel().removeListSelectionListener(listSelectionListener);
		fileTableModel.setFiles(files);
		table.getSelectionModel().addListSelectionListener(listSelectionListener);
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
