package com.voidStudios.photoDisplay;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.imageio.ImageIO;

public class ImageDirectory {

	private static final List<String> PAUSE_FILES=Arrays.asList(new String[] {"pause.txt", "pause2.txt"});
	private WatchService dirWatcher;
	private Vector<File> imageFiles;
	private Path imageDirPath;
	private File imageDirFile;
	private int imageIndex;
	private boolean isPaused;
	private Controller controller;

	public ImageDirectory(String dir, Controller controller) {
		this.controller=controller;
		
		imageFiles=new Vector<File>();
		isPaused=false;

		//Set up a watcher for the photo directory
		imageDirPath=FileSystems.getDefault().getPath(dir);
		imageDirFile=imageDirPath.toFile();

		try {
			dirWatcher=FileSystems.getDefault().newWatchService();
		}catch(IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		(new WatcherThread(dirWatcher)).start();
		try {
			imageDirPath.register(dirWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		}catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Get the initial list of images in the directory
		sync();
	}

	private void sync() {
		imageFiles.clear();
		imageIndex=0;

		File[] files=imageDirFile.listFiles();
		if(files==null) {
			System.err.println("ERROR: Photos directory does not exist.");
			return;
		}
		boolean pause=false;

		for(File f : files) {
			if(f.isFile() && f.canRead() && !f.isHidden()) {
				if(PAUSE_FILES.contains(f.getName())) {
					//If a pause file exists set the pause flag and cease file checking
					//When the pause file is removed the list will be re-synced anyway
					pause=true;
					break;
				}

				try {
					//TODO test on Linux environment
					boolean swap=false;
					if(swap) {
						//Resource intensive, based on file contents
						BufferedImage pic=ImageIO.read(f);
						if(pic!=null)
							imageFiles.add(f);
						else
							System.err.println(new Date()+": Unreadable file: "+f);
					}else {
						//Fast, based off of extension exclusively
						String mimetype=Files.probeContentType(f.toPath());
						String type=mimetype.split("/")[0];
						if(type.equals("image"))
							imageFiles.add(f);
						else
							System.err.println(new Date()+": Unreadable file: "+f);

					}
				}catch(IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if(isPaused && !pause) {
			//Restart
			controller.start();
		}else if(!isPaused && pause) {
			//Stop
			controller.pause();
		}else {
			//No Action
			//Either still paused or still running
		}
		isPaused=pause;
		
		if(imageFiles.size()==0) {
			System.err.println(new Date()+": No valid photos found in photos directory");
		}

		Collections.shuffle(imageFiles);
	}

	public File nextFile() {
		if(imageFiles.size()>0) {
			File f=imageFiles.get(imageIndex);
			imageIndex++;

			//If necessary shuffle the list and reset the index
			if(imageIndex>=imageFiles.size()) {
				imageIndex=0;
				Collections.shuffle(imageFiles);
			}
			return f;
		}else {
			return null;
		}
	}

	public boolean isPaused() {
		return isPaused;
	}

	private class WatcherThread extends Thread {

		WatchService dirWatcher;

		public WatcherThread(WatchService dirWatcher) {
			this.dirWatcher=dirWatcher;
		}

		@Override
		public void run() {
			try {
				while(true) {
					WatchKey key;
					key=dirWatcher.take();
					//Ensures events are removed from the queue
					key.pollEvents();
					if(key.isValid())
						sync();

					if(!key.reset()) {
						//TODO key no longer valid
					}
				}
			}catch(InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}