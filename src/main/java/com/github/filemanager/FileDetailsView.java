package com.github.filemanager;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.util.Date;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileSystemView;

public class FileDetailsView extends JPanel {

	private static final FileSystemView fileSystemView = FileSystemView.getFileSystemView();

	/* File details. */
	private JLabel fileName;
	private JTextField path;
	private JLabel date;
	private JLabel size;
	private JCheckBox readable;
	private JCheckBox writable;
	private JCheckBox executable;
	private JRadioButton isDirectory;
	private JRadioButton isFile;

	public FileDetailsView() {
		super(new BorderLayout(4, 2));
		setBorder(new EmptyBorder(0, 6, 0, 6));

		JPanel fileDetailsLabels = new JPanel(new GridLayout(0, 1, 2, 2));
		add(fileDetailsLabels, BorderLayout.WEST);

		JPanel fileDetailsValues = new JPanel(new GridLayout(0, 1, 2, 2));
		add(fileDetailsValues, BorderLayout.CENTER);

		fileDetailsLabels.add(new JLabel("File", JLabel.TRAILING));
		fileName = new JLabel();
		fileDetailsValues.add(fileName);
		fileDetailsLabels.add(new JLabel("Full name", JLabel.TRAILING));
		path = new JTextField(5);
		path.setEditable(false);
		fileDetailsValues.add(path);
		fileDetailsLabels.add(new JLabel("Last Modified", JLabel.TRAILING));
		date = new JLabel();
		fileDetailsValues.add(date);
		fileDetailsLabels.add(new JLabel("File size", JLabel.TRAILING));
		size = new JLabel();
		fileDetailsValues.add(size);

		fileDetailsLabels.add(new JLabel("Type", JLabel.TRAILING));
		JPanel flags = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 0));
		isDirectory = new JRadioButton("Directory");
		isDirectory.setEnabled(false);
		flags.add(isDirectory);

		isFile = new JRadioButton("File");
		isFile.setEnabled(false);
		flags.add(isFile);
		fileDetailsValues.add(flags);

		fileDetailsLabels.add(new JLabel("Perms", JLabel.TRAILING));
		JPanel perms = new JPanel(new FlowLayout(FlowLayout.LEADING, 4, 0));

		readable = new JCheckBox("Read ");
		readable.setMnemonic('a');
		readable.setEnabled(false);
		perms.add(readable);

		writable = new JCheckBox("Write ");
		writable.setMnemonic('w');
		writable.setEnabled(false);
		perms.add(writable);

		executable = new JCheckBox("Execute ");
		executable.setMnemonic('x');
		executable.setEnabled(false);
		perms.add(executable);

		fileDetailsValues.add(perms);

		// TODO not sure if this is used
		int count = fileDetailsLabels.getComponentCount();
		for (int ii = 0; ii < count; ii++) {
			fileDetailsLabels.getComponent(ii).setEnabled(false);
		}

	}

	/** Update the File details view with the details of this File. */
	public void updatetFileDetails(File file) {
		Icon icon = fileSystemView.getSystemIcon(file);
		fileName.setIcon(icon);
		fileName.setText(fileSystemView.getSystemDisplayName(file));
		path.setText(file.getPath());
		date.setText(new Date(file.lastModified()).toString());
		size.setText(file.length() + " bytes");
		readable.setSelected(file.canRead());
		writable.setSelected(file.canWrite());
		executable.setSelected(file.canExecute());
		isDirectory.setSelected(file.isDirectory());

		isFile.setSelected(file.isFile());

		JFrame f = (JFrame) getTopLevelAncestor();
		if (f != null) {
			f.setTitle(Main.APP_TITLE + " :: " + fileSystemView.getSystemDisplayName(file));
		}

		repaint();
	}

}
