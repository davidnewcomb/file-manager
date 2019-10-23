package com.github.filemanager.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.filemanager.FmModel;

class AddressBar extends JPanel {

	private JTextField address;

	AddressBar(FmModel model) {
		setLayout(new BorderLayout());
		address = new JTextField();
		add(address, BorderLayout.CENTER);
		model.addObserver(new AddressBarObserver(address));
	}
}
