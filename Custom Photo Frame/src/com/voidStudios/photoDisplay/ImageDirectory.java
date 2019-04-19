package com.voidStudios.photoDisplay;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;
import javax.imageio.ImageIO;

public class ImageDirectory {

	WatchService dirWatcher;
	Vector<File> imageFiles = new Vector<>();
	Path imageDirPath;
	File imageDirFile;
	int imageIndex;
	boolean isPaused=false;

	public ImageDirectory(String dir) {
		//Set up a watcher for the images directory
		imageDirPath = FileSystems.getDefault().getPath(dir);
		imageDirFile = imageDirPath.toFile();

		try {
			dirWatcher = FileSystems.getDefault().newWatchService();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		(new WatcherThread(dirWatcher)).start();
		try {
			imageDirPath.register(dirWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		} catch (IOException e) {
			e.printStackTrace();
		}

		//Get the initial list of files in the directory
		sync();
	}

	private void sync() {
		imageFiles.clear();
		imageIndex = 0;

		File[] files = imageDirFile.listFiles();
		boolean pause=false;

		for (File f : files) {
			if (f.isFile() && f.canRead() && !f.isHidden()) {
				if(f.getName().equals("pause.txt") || f.getName().equals("pause2.txt")) {
					pause=true;
					continue;
				}
				try {
					BufferedImage pic=ImageIO.read(f);
					if(pic!=null)
						imageFiles.add(f);
					else
						System.out.println(new Date()+ ": Unreadable file in photos: "+f);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		isPaused=pause;
		
//		System.out.println(imageFiles);

		Collections.shuffle(imageFiles);
	}

	public File nextFile() {
		if (imageFiles.size() > 0) {
			File f = imageFiles.get(imageIndex);

			//advance and cycle the index if necessary
			imageIndex++;
			if (imageIndex == imageFiles.size()) {
				imageIndex = 0;
				Collections.shuffle(imageFiles);
			}
			return f;
		}
		else {
			return null;
		}
	}

	private class WatcherThread extends Thread {
		WatchService dirWatcher;

		public WatcherThread(WatchService dirWatcher) {
			this.dirWatcher = dirWatcher;
		}

		@Override
		public void run() {
			try {
				for (;;) {
					WatchKey key = dirWatcher.take();
					key.pollEvents();

					if (key.isValid())
						sync();

					key.reset();
				}
			}
			catch (Exception e) {
			}
		}
	}
}
