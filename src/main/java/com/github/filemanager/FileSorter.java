package com.github.filemanager;

import java.io.File;
import java.util.Comparator;

public class FileSorter implements Comparator<File> {

	@Override
	public int compare(File a, File b) {
		return a.getName().compareToIgnoreCase(b.getName());
		// return a.getName().compareTo(b.getName());
	}

}
