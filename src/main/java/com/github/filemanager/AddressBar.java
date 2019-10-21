package com.github.filemanager;

import java.awt.BorderLayout;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JTextField;

public class AddressBar extends JPanel {

	private JTextField address;

	public AddressBar() {
		setLayout(new BorderLayout());
		address = new JTextField();
		add(address, BorderLayout.CENTER);
	}

	public void updateFile(File f) {
		if (f.isDirectory()) {
			address.setText(f.toString());
		} else {
			address.setText(f.getParent().toString());
		}
	}
}
