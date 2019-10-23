package com.github.filemanager.gui;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JTextField;

class AddressBarObserver implements Observer {

	private JTextField address;

	AddressBarObserver(JTextField _address) {
		address = _address;
	}

	@Override
	public void update(Observable o, Object arg) {
		File f = (File) arg;
		if (f.isDirectory()) {
			address.setText(f.toString());
		} else {
			address.setText(f.getParent().toString());
		}
	}
}