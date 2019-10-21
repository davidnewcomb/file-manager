package com.github.filemanager;

import java.awt.Dimension;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;

public class FileListView extends JTable {

	private static final int ROW_ICON_PADDING = 6;
	private static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	private FileTableModel fileTableModel;
	private ListSelectionListener listSelectionListener;

	public FileListView(Gui gui) {
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoCreateRowSorter(true);
		setShowVerticalLines(false);
		fileTableModel = new FileTableModel();
		setModel(fileTableModel);

		// size adjustment to better account for icons
		Icon icon = fileSystemView.getSystemIcon(new File("/"));
		setRowHeight(icon.getIconHeight() + ROW_ICON_PADDING);

		setColumnWidth(FileTableModel.ICON, -1);
		setColumnWidth(FileTableModel.SIZE, 60);
		getColumnModel().getColumn(FileTableModel.FILE_NAME).setMaxWidth(120);
		setColumnWidth(FileTableModel.LAST_MODIFIED, -1);
		setColumnWidth(FileTableModel.READ, -1);
		setColumnWidth(FileTableModel.WRITE, -1);
		setColumnWidth(FileTableModel.EXECUTE, -1);
		setColumnWidth(FileTableModel.DIRECTORY, -1);
		setColumnWidth(FileTableModel.FILE, -1);

		listSelectionListener = new TableListSelectionListener(this, gui);
		getSelectionModel().addListSelectionListener(listSelectionListener);
	}

	class TableListSelectionListener implements ListSelectionListener {
		private JTable table;
		private Gui gui;

		public TableListSelectionListener(JTable table, Gui gui) {
			this.table = table;
			this.gui = gui;
		}

		@Override
		public void valueChanged(ListSelectionEvent lse) {
			int row = table.getSelectionModel().getLeadSelectionIndex();
			FileTableModel ftm = (FileTableModel) table.getModel();
			File file = ftm.getFile(row);
			gui.updateFile(file);
		}

	}

	public void setColumnWidth(int column, int width) {
		TableColumn tableColumn = getColumnModel().getColumn(column);
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

	public void updateFiles(File[] files) {
		getSelectionModel().removeListSelectionListener(listSelectionListener);
		fileTableModel.setFiles(files);
		getSelectionModel().addListSelectionListener(listSelectionListener);
	}

}
