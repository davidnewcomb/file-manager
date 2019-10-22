package com.github.filemanager.gui.table.render_size;

import com.github.filemanager.Cfg;

public class RenderSizeBuilder {

	public RenderSizeText build() {
		if (Cfg.prettyFileLength) {
			return new PrintPretty();
		}
		return new PrintBytes();
	}
}
