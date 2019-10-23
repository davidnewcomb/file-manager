package com.github.filemanager;

import java.io.File;
import java.util.Arrays;

public class Utils {

	private static final File[] NO_FILES = new File[0];
	private static final FileSorter FILE_SORTER = new FileSorter();

	public File[] sortedDirectoryListing(File file) {
		File[] files = file.listFiles();
		if (files == null) {
			files = NO_FILES;
		} else {
			Arrays.sort(files, FILE_SORTER);
		}
		return files;
	}
}
