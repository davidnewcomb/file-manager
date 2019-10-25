package com.github.filemanager.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.github.filemanager.FmModel;

class AddressBar extends JPanel {

	private JTextField address;
	private FmModel model;

	AddressBar(FmModel _model) {
		setLayout(new BorderLayout());
		model = _model;

		address = new JTextField();
		address.addKeyListener(new AddressBarKeyAdapter(model));
		add(address, BorderLayout.CENTER);

		model.addObserver(new AddressBarObserver(address));

	}
}
