package com.github.filemanager.gui.table;

import java.io.File;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.filemanager.gui.Gui;

public class FileListViewListSelectionListener implements ListSelectionListener {
	private JTable table;
	private Gui gui;

	public FileListViewListSelectionListener(JTable table, Gui gui) {
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
