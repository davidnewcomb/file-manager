package com.github.filemanager.gui.details.al;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.filemanager.FmModel;

public class LaunchFileActionListener implements ActionListener {

	private static Logger L = LoggerFactory.getLogger(LaunchFileActionListener.class);
	private static Desktop desktop = Desktop.getDesktop();

	private FmModel model;

	public LaunchFileActionListener(FmModel _model) {
		this.model = _model;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			L.info("Desktop::open '" + model.getCurrentFile().toString() + "'");
			desktop.open(model.getCurrentFile());
		} catch (Throwable t) {
			L.warn("Could not find viewer for " + model.getCurrentFile().toString());
		}
		// gui.repaint();
	}

}