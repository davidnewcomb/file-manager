package com.github.filemanager.gui.table;

import java.awt.Dimension;
import java.io.File;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.TableColumn;

import com.github.filemanager.gui.Gui;

public class FileListView extends JTable {

	private static final int ROW_ICON_PADDING = 6;
	private static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	private FileTableModel fileTableModel;
	private ListSelectionListener listSelectionListener;

	public FileListView(Gui gui) {
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoCreateRowSorter(true);
		setShowVerticalLines(false);
		fileTableModel = new FileTableModel();
		setModel(fileTableModel);

		// size adjustment to better account for icons
		Icon icon = fileSystemView.getSystemIcon(new File("/"));
		setRowHeight(icon.getIconHeight() + ROW_ICON_PADDING);

		setColumnWidth(FileTableModel.ICON, -1);
		setColumnWidth(FileTableModel.SIZE, 100);
		// setColumnWidth(FileTableModel.FILE_NAME, Integer.MAX_VALUE);
		getColumnModel().getColumn(FileTableModel.FILE_NAME).setMaxWidth(Integer.MAX_VALUE);
		setColumnWidth(FileTableModel.LAST_MODIFIED, -1);
		setColumnWidth(FileTableModel.READ, 20);
		setColumnWidth(FileTableModel.WRITE, 20);
		setColumnWidth(FileTableModel.EXECUTE, 20);
		setColumnWidth(FileTableModel.DIRECTORY, 20);
		setColumnWidth(FileTableModel.FILE, 20);

		listSelectionListener = new FileListViewListSelectionListener(this, gui);
		getSelectionModel().addListSelectionListener(listSelectionListener);
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
		if (width == Integer.MAX_VALUE) {
			tableColumn.setMaxWidth(Integer.MAX_VALUE);
		} else {
			tableColumn.setMaxWidth(width);
		}
		// tableColumn.setMinWidth(100);
	}

	public void updateFiles(File[] files) {
		getSelectionModel().removeListSelectionListener(listSelectionListener);
		fileTableModel.setFiles(files);
		getSelectionModel().addListSelectionListener(listSelectionListener);
	}

}
