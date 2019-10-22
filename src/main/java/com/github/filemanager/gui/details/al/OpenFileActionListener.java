package com.github.filemanager.gui.details.al;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.filemanager.FmModel;

public class OpenFileActionListener implements ActionListener {

	private static Logger L = LoggerFactory.getLogger(OpenFileActionListener.class);

	private FmModel model;

	public OpenFileActionListener(FmModel _model) {
		this.model = _model;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		try {
			L.info("Desktop::open::asText '" + model.getCurrentFile().toString() + "'");
			Runtime rt = Runtime.getRuntime();
			String[] args = { "open", "-a", "TextEdit", model.getCurrentFile().toString() };
			if (model.getCurrentFile().isDirectory()) {
				args = new String[] { "open", model.getCurrentFile().toString() };
			}
			rt.exec(args);
		} catch (Throwable t) {
			L.warn("Could not find viewer for " + model.getCurrentFile().toString());
		}
		// gui.repaint();
	}

}