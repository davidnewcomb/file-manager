package com.github.filemanager.gui.details.al;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.filemanager.FmModel;

public class PrintFileActionListener implements ActionListener {

	private static Logger L = LoggerFactory.getLogger(PrintFileActionListener.class);
	private static Desktop desktop = Desktop.getDesktop();

	private FmModel model;

	public PrintFileActionListener(FmModel _model) {
		this.model = _model;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			desktop.print(model.getCurrentFile());
		} catch (Throwable t) {
			// gui.showThrowable(t);
			L.error("Desktop::print '" + model.getCurrentFile().toString() + "'", t);
		}
	}

}