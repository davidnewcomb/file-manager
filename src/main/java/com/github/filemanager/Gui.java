package com.github.filemanager;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
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

	private Gui thiz;
	/** Used to open/edit/print files. */
	private Desktop desktop;

	/* File controls. */
	private JButton openFile;
	private JButton printFile;
	private JButton editFile;
	private JButton deleteFile;
	private JButton newFile;
	private JButton copyFile;

	// /* File details. */
	// private JLabel fileName;
	// private JTextField path;
	// private JLabel date;
	// private JLabel size;
	// private JCheckBox readable;
	// private JCheckBox writable;
	// private JCheckBox executable;
	// private JRadioButton isDirectory;
	// private JRadioButton isFile;

	private JProgressBar progressBar;

	/** File-system tree. Built Lazily */
	private JTree tree;
	private DefaultTreeModel treeModel;

	/** Directory listing */
	private JTable table;
	private FileTableModel fileTableModel;
	private FileSystemView fileSystemView;

	/** currently selected File. */
	// TODO Duplicated in FileManager
	private File currentFile;

	private FileDetailsView fileDetailsView;

	private ListSelectionListener listSelectionListener;

	public Gui(final FileManager fileManager) {
		super(new BorderLayout(3, 3));
		this.thiz = this;

		setBorder(new EmptyBorder(5, 5, 5, 5));

		fileSystemView = FileSystemView.getFileSystemView();
		desktop = Desktop.getDesktop();

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
				fileDetailsView.updatetFileDetails(ftm.getFile(row));
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
				fileManager.showChildren(node);
				fileDetailsView.updatetFileDetails((File) node.getUserObject());
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

		fileDetailsView = new FileDetailsView();

		// details for a File
		// JPanel fileMainDetails = new JPanel(new BorderLayout(4, 2));
		// fileMainDetails.setBorder(new EmptyBorder(0, 6, 0, 6));
		//
		// JPanel fileDetailsLabels = new JPanel(new GridLayout(0, 1, 2, 2));
		// fileMainDetails.add(fileDetailsLabels, BorderLayout.WEST);
		//
		// JPanel fileDetailsValues = new JPanel(new GridLayout(0, 1, 2, 2));
		// fileMainDetails.add(fileDetailsValues, BorderLayout.CENTER);
		//
		// fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
		// fileName = new JLabel();
		// fileDetailsValues.add(fileName);
		// fileDetailsLabels.add(new JLabel("Path/name", JLabel.TRAILING));
		// path = new JTextField(5);
		// path.setEditable(false);
		// fileDetailsValues.add(path);
		// fileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
		// date = new JLabel();
		// fileDetailsValues.add(date);
		// fileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
		// size = new JLabel();
		// fileDetailsValues.add(size);
		// fileDetailsLabels.add(new JLabel("Type", JLabel.TRAILING));
		//
		// JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 0));
		// isDirectory = new JRadioButton("Directory");
		// isDirectory.setEnabled(false);
		// flags.add(isDirectory);
		//
		// isFile = new JRadioButton("File");
		// isFile.setEnabled(false);
		// flags.add(isFile);
		// fileDetailsValues.add(flags);
		//
		// int count = fileDetailsLabels.getComponentCount();
		// for (int ii = 0; ii < count; ii++) {
		// fileDetailsLabels.getComponent(ii).setEnabled(false);
		// }

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
				thiz.repaint();
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
				fileManager.newFile(treeModel, currentFile);
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
				fileManager.renameFile(treeModel, currentFile);
			}
		});
		toolBar.add(renameFile);

		deleteFile = new JButton("Delete");
		deleteFile.setMnemonic('d');
		deleteFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				fileManager.deleteFile(treeModel, currentFile);
			}
		});
		toolBar.add(deleteFile);

		// toolBar.addSeparator();
		//
		// readable = new JCheckBox("Read ");
		// readable.setMnemonic('a');
		// readable.setEnabled(false);
		// toolBar.add(readable);
		//
		// writable = new JCheckBox("Write ");
		// writable.setMnemonic('w');
		// writable.setEnabled(false);
		// toolBar.add(writable);
		//
		// executable = new JCheckBox("Execute");
		// executable.setMnemonic('x');
		// executable.setEnabled(false);
		// toolBar.add(executable);

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

	public void showThrowable(Throwable t) {
		t.printStackTrace();
		JOptionPane.showMessageDialog(thiz, t.toString(), t.getMessage(), JOptionPane.ERROR_MESSAGE);
		thiz.repaint();
	}

	public void showErrorMessage(String errorMessage, String errorTitle) {
		JOptionPane.showMessageDialog(thiz, errorMessage, errorTitle, JOptionPane.ERROR_MESSAGE);
	}

}
