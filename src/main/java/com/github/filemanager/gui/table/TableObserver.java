package com.github.filemanager.gui.table;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class TableObserver implements Observer {

	private Table table;

	public TableObserver(Table _table) {
		table = _table;
	}

	@Override
	public void update(Observable o, Object arg) {
		File f = (File) arg;
		if (f.isDirectory()) {
			table.updateFiles(f);
		}
	}

}
