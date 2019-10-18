/** A TableModel to hold File[]. */
package com.github.filemanager;

import java.io.File;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTableModel extends AbstractTableModel {

	private static final Logger L = LoggerFactory.getLogger(FileTableModel.class);
	public static final int ICON = 0;
	public static final int FILE_NAME = 1;
	public static final int PATH = 2;
	public static final int SIZE = 3;
	public static final int LAST_MODIFIED = 4;
	public static final int READ = 5;
	public static final int WRITE = 6;
	public static final int EXECUTE = 7;
	public static final int DIRECTORY = 8;
	public static final int FILE = 9;

	private File[] files;
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private String[] columns = { "Icon", "File", "Path/name", "Size", "Last Modified", "R", "W", "E", "D", "F", };

	FileTableModel() {
		this(new File[0]);
	}

	FileTableModel(File[] files) {
		this.files = files;
	}

	@Override
	public Object getValueAt(int row, int column) {
		File file = files[row];
		switch (column) {
		case ICON:
			return fileSystemView.getSystemIcon(file);
		case FILE_NAME:
			return fileSystemView.getSystemDisplayName(file);
		case PATH:
			return file.getPath();
		case SIZE:
			return file.length();
		case LAST_MODIFIED:
			return file.lastModified();
		case READ:
			return file.canRead();
		case WRITE:
			return file.canWrite();
		case EXECUTE:
			return file.canExecute();
		case DIRECTORY:
			return file.isDirectory();
		case FILE:
			return file.isFile();
		default:
			L.error("Logic Error");
		}
		return "";
	}

	@Override
	public int getColumnCount() {
		return columns.length;
	}

	@Override
	public Class<?> getColumnClass(int column) {
		switch (column) {
		case ICON:
			return ImageIcon.class;
		case SIZE:
			return Long.class;
		case LAST_MODIFIED:
			return Date.class;
		case READ:
		case WRITE:
		case EXECUTE:
		case DIRECTORY:
		case FILE:
			return Boolean.class;
		}
		return String.class;
	}

	@Override
	public String getColumnName(int column) {
		return columns[column];
	}

	@Override
	public int getRowCount() {
		return files.length;
	}

	public File getFile(int row) {
		return files[row];
	}

	public void setFiles(File[] files) {
		this.files = files;
		fireTableDataChanged();
	}
}
