package com.github.filemanager.gui.table.render_size;

public class PrintPretty implements RenderSizeText {

	private int precision = 1;

	@Override
	public String render(long bytes) {
		String[] dictionary = { "", " KB", " MB", " GB", " TB", " PB", " EB", " ZB", " YB" };
		int index = 0;
		for (index = 0; index < dictionary.length; index++) {
			if (bytes < 1024) {
				break;
			}
			bytes = bytes / 1024;
		}
		double bytesD = bytes;
		String s = String.format("%." + precision + "f", bytesD) + dictionary[index];
		s = s.replaceAll(".0", "");
		return s;
	}

	public void setPrecision(int _precision) {
		precision = _precision;
	}
}
