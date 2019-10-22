package com.github.filemanager.gui.table;

import java.io.File;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.github.filemanager.FmModel;

public class TableListSelectionListener implements ListSelectionListener {
	private JTable table;
	private FmModel model;

	public TableListSelectionListener(FmModel _model, JTable _table) {
		this.table = _table;
		this.model = _model;
	}

	@Override
	public void valueChanged(ListSelectionEvent lse) {
		int row = table.getSelectionModel().getLeadSelectionIndex();
		TableModel ftm = (TableModel) table.getModel();
		File file = ftm.getFile(row);
		model.setCurrentFile(file);
	}

}
