/** A TableModel to hold File[]. */
package com.github.filemanager.gui.table;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;
import javax.swing.table.AbstractTableModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.filemanager.FileSorter;
import com.github.filemanager.gui.table.render_size.RenderSizeBuilder;
import com.github.filemanager.gui.table.render_size.RenderSizeText;

public class TableModel extends AbstractTableModel {

	private static final Logger L = LoggerFactory.getLogger(TableModel.class);

	public static final int ICON = 0;
	public static final int FILE_NAME = 1;
	public static final int SIZE = 2;
	public static final int LAST_MODIFIED = 3;
	public static final int READ = 4;
	public static final int WRITE = 5;
	public static final int EXECUTE = 6;
	public static final int DIRECTORY = 7;
	public static final int FILE = 8;

	private File[] files;
	private FileSystemView fileSystemView = FileSystemView.getFileSystemView();
	private String[] columns = { "Icon", "File", "Size", "Last Modified", "R", "W", "E", "D", "F", };
	private RenderSizeText renderSize;

	public TableModel() {
		this.files = new File[0];
		configUpdate();
	}

	private void configUpdate() {
		// TODO use observers to update
		renderSize = new RenderSizeBuilder().build();
	}

	@Override
	public Object getValueAt(int row, int column) {
		File file = files[row];
		switch (column) {
		case ICON:
			return fileSystemView.getSystemIcon(file);
		case FILE_NAME:
			return fileSystemView.getSystemDisplayName(file);
		case SIZE:
			return renderSize.render(file.length());
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
			L.error("Unknown FileTable index row=" + row + " col=" + column);
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
		Arrays.sort(files, new FileSorter());
		this.files = files;
		fireTableDataChanged();
	}

}
