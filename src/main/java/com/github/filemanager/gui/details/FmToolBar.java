package com.github.filemanager.gui.details;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.tree.DefaultTreeModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.filemanager.FileManager;
import com.github.filemanager.gui.Gui;

public class FmToolBar extends JToolBar {
	private static final Logger L = LoggerFactory.getLogger(FmToolBar.class);

	private Gui gui;
	private DefaultTreeModel treeModel;

	/** Used to open/edit/print files. */
	private Desktop desktop;
	private File currentFile;

	/* File controls. */
	private JButton openFile;
	private JButton launchFile;
	private JButton printFile;
	// private JButton editFile;
	private JButton deleteFile;
	private JButton newFile;
	private JButton copyFile;

	private FileManager fileManager;

	public FmToolBar(FileManager _fileManager, Gui _gui, DefaultTreeModel _treeModel) {

		this.fileManager = _fileManager;
		this.gui = _gui;
		this.treeModel = _treeModel;

		desktop = Desktop.getDesktop();

		// mnemonics stop working in a floated toolbar
		setFloatable(false);

		openFile = new JButton("Open");
		openFile.setMnemonic('o');

		openFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				try {
					L.info("Desktop::open::asText '" + currentFile.toString() + "'");
					Runtime rt = Runtime.getRuntime();
					String[] args = { "open", "-a", "TextEdit", currentFile.toString() };
					if (currentFile.isDirectory()) {
						args = new String[] { "open", currentFile.toString() };
					}
					rt.exec(args);
				} catch (Throwable t) {
					L.warn("Could not find viewer for " + currentFile.toString());
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
					L.info("Desktop::open '" + currentFile.toString() + "'");
					desktop.open(currentFile);
				} catch (Throwable t) {
					L.warn("Could not find viewer for " + currentFile.toString());
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
					desktop.print(currentFile);
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
				fileManager.newFile(treeModel, currentFile);
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
				fileManager.renameFile(treeModel, currentFile);
			}
		});
		add(renameFile);

		deleteFile = new JButton("Delete");
		deleteFile.setMnemonic('d');
		deleteFile.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				fileManager.deleteFile(treeModel, currentFile);
			}
		});
		add(deleteFile);

	}

	public void updateFile(File file) {
		currentFile = file;
	}
}
