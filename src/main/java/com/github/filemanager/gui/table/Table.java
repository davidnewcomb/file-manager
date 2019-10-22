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

public class Table extends JTable {

	private static final int ROW_ICON_PADDING = 6;
	private static FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	private TableModel fileTableModel;
	private ListSelectionListener listSelectionListener;

	public Table(Gui gui) {
		setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setAutoCreateRowSorter(true);
		setShowVerticalLines(false);
		fileTableModel = new TableModel();
		setModel(fileTableModel);

		// size adjustment to better account for icons
		Icon icon = fileSystemView.getSystemIcon(new File("/"));
		setRowHeight(icon.getIconHeight() + ROW_ICON_PADDING);

		setColumnWidth(TableModel.ICON, -1);
		setColumnWidth(TableModel.SIZE, 100);
		// setColumnWidth(FileTableModel.FILE_NAME, Integer.MAX_VALUE);
		getColumnModel().getColumn(TableModel.FILE_NAME).setMaxWidth(Integer.MAX_VALUE);
		setColumnWidth(TableModel.LAST_MODIFIED, -1);
		setColumnWidth(TableModel.READ, 20);
		setColumnWidth(TableModel.WRITE, 20);
		setColumnWidth(TableModel.EXECUTE, 20);
		setColumnWidth(TableModel.DIRECTORY, 20);
		setColumnWidth(TableModel.FILE, 20);

		listSelectionListener = new TableListSelectionListener(this, gui);
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
