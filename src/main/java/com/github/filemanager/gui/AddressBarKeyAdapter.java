package com.github.filemanager.gui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JTextField;

import com.github.filemanager.FmModel;

class AddressBarKeyAdapter extends KeyAdapter {

	private FmModel model;

	AddressBarKeyAdapter(FmModel _model) {
		model = _model;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		JTextField tf = (JTextField) e.getSource();
		if (e.getKeyCode() == KeyEvent.VK_ENTER) {
			String s = tf.getText().trim();
			File f = new File(s);
			if (f.isDirectory()) {
				model.setCurrentFile(f);
			}
		}
	}

}