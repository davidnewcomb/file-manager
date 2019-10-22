package com.github.filemanager.gui.details;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.filemanager.FileManager;
import com.github.filemanager.FmModel;
import com.github.filemanager.gui.Gui;

public class ToolBar extends JToolBar {
	private static final Logger L = LoggerFactory.getLogger(ToolBar.class);

	private Gui gui;
	private FmModel model;

	/** Used to open/edit/print files. */
	private Desktop desktop;
	// private File model.getCurrentFile();

	/* File controls. */
	private JButton openFile;
	private JButton launchFile;
	private JButton printFile;
	// private JButton editFile;
	private JButton deleteFile;
	private JButton newFile;
	private JButton copyFile;

	private FileManager fileManager;

	public ToolBar(FileManager _fileManager, Gui _gui, FmModel _model) {

		this.fileManager = _fileManager;
		this.gui = _gui;
		this.model = _model;

		desktop = Desktop.getDesktop();

		// mnemonics stop working in a floated toolbar
		setFloatable(false);

		openFile = new JButton("Open");
		openFile.setMnemonic('o');

		openFile.addActionListener(new ActionListener() {

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
				gui.repaint();
			}

		});
		add(openFile);

		launchFile = new JButton("Launch");
		launchFile.setMnemonic('l');

		launchFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					L.info("Desktop::open '" + model.getCurrentFile().toString() + "'");
					desktop.open(model.getCurrentFile());
				} catch (Throwable t) {
					L.warn("Could not find viewer for " + model.getCurrentFile().toString());
				}
				gui.repaint();
			}
		});
		add(launchFile);

		printFile = new JButton("Print");
		printFile.setMnemonic('p');
		printFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					desktop.print(model.getCurrentFile());
				} catch (Throwable t) {
					gui.showThrowable(t);
				}
			}
		});
		add(printFile);

		// Check the actions are supported on this platform!
		// openFile.setEnabled(desktop.isSupported(Desktop.Action.OPEN));
		// launchFile.setEnabled(desktop.isSupported(Desktop.Action.EDIT));
		printFile.setEnabled(desktop.isSupported(Desktop.Action.PRINT));

		addSeparator();

		newFile = new JButton("New");
		newFile.setMnemonic('n');
		newFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				fileManager.newFile(model.getTreeModel(), model.getCurrentFile());
			}
		});
		add(newFile);

		copyFile = new JButton("Copy");
		copyFile.setMnemonic('c');
		copyFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				gui.showErrorMessage("'Copy' not implemented.", "Not implemented.");
			}
		});
		add(copyFile);

		JButton renameFile = new JButton("Rename");
		renameFile.setMnemonic('r');
		renameFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				fileManager.renameFile(model.getTreeModel(), model.getCurrentFile());
			}
		});
		add(renameFile);

		deleteFile = new JButton("Delete");
		deleteFile.setMnemonic('d');
		deleteFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				fileManager.deleteFile(model.getTreeModel(), model.getCurrentFile());
			}
		});
		add(deleteFile);

	}

	// public void updateFile(File file) {
	// currentFile = file;
	// }
}
