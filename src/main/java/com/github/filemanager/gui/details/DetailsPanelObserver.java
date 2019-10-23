package com.github.filemanager.gui.details;

import java.io.File;
import java.util.Observable;
import java.util.Observer;

public class DetailsPanelObserver implements Observer {

	private DetailsPanel detailsPanel;

	public DetailsPanelObserver(DetailsPanel _detailsPanel) {
		this.detailsPanel = _detailsPanel;
	}

	@Override
	public void update(Observable o, Object arg) {
		File f = (File) arg;
		detailsPanel.updatetFileDetails(f);
	}

}
