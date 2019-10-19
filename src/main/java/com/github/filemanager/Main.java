package com.github.filemanager;

import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	private static Logger L = LoggerFactory.getLogger(Main.class);

	/** Title of the application */
	public static final String APP_TITLE = "FileMan";

	public static void main(String[] args) {
		setLookAndFeel();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {

				JFrame f = new JFrame(APP_TITLE);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				FileManager fileManager = new FileManager();
				f.setContentPane(fileManager.getGui());

				try {
					ArrayList<Image> images = new ArrayList<Image>();
					load(images, fileManager, "fm-icon-32x32.png");
					load(images, fileManager, "fm-icon-16x16.png");
					f.setIconImages(images);
				} catch (Exception e) {
					L.warn("Problem while loading icons", e);
				}

				f.pack();
				f.setLocationByPlatform(true);
				f.setMinimumSize(f.getSize());
				f.setVisible(true);

				fileManager.getGui().showRootFile();
			}
		});
	}

	private static void load(ArrayList<Image> images, FileManager fileManager, String name) throws IOException {
		URL url = fileManager.getClass().getResource(name);
		if (url == null) {
			L.warn("Can not load : " + url);
		} else {
			images.add(ImageIO.read(url));
		}
	}

	private static void setLookAndFeel() {
		try {
			// Significantly improves the look of the output in
			// terms of the file names returned by FileSystemView!
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception weTried) {
			L.error("Could not set look and feel");
		}

	}
}
