package com.github.filemanager.gui;

import java.awt.BorderLayout;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.filemanager.FmModel;

public class AddressBar extends JPanel {

	private JTextField address;

	public AddressBar(FmModel model) {
		setLayout(new BorderLayout());
		address = new JTextField();
		add(address, BorderLayout.CENTER);
		model.addObserver(new Observer() {

			@Override
			public void update(Observable o, Object arg) {
				File f = (File) arg;
				if (f.isDirectory()) {
					address.setText(f.toString());
				} else {
					address.setText(f.getParent().toString());
				}
			}
		});
	}
}
