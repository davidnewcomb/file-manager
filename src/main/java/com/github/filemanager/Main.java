package com.github.filemanager;

import java.awt.Image;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
	/** Title of the application */
	public static final String APP_TITLE = "FileMan";

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					// Significantly improves the look of the output in
					// terms of the file names returned by FileSystemView!
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (Exception weTried) {
				}
				JFrame f = new JFrame(APP_TITLE);
				f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				FileManager fileManager = new FileManager();
				f.setContentPane(fileManager.getGui());

				try {
					URL urlBig = fileManager.getClass().getResource("fm-icon-32x32.png");
					URL urlSmall = fileManager.getClass().getResource("fm-icon-16x16.png");
					ArrayList<Image> images = new ArrayList<Image>();
					images.add(ImageIO.read(urlBig));
					images.add(ImageIO.read(urlSmall));
					f.setIconImages(images);
				} catch (Exception weTried) {
				}

				f.pack();
				f.setLocationByPlatform(true);
				f.setMinimumSize(f.getSize());
				f.setVisible(true);

				fileManager.showRootFile();
			}
		});
	}

}
