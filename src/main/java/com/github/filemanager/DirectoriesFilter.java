package com.github.filemanager;

import java.io.File;
import java.io.FileFilter;

public class DirectoriesFilter implements FileFilter {

	@Override
	public boolean accept(File file) {
		String s = file.getName();
		return !(".".equals(s) || "..".equals(s)) && file.isDirectory();
	}

}