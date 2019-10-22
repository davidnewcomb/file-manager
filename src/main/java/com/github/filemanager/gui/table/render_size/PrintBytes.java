package com.github.filemanager.gui.table.render_size;

public class PrintBytes implements RenderSizeText {

	@Override
	public String render(long bytes) {
		return String.valueOf(bytes);
	}

}
