package com.voidStudios.photoDisplay;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

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
		}catch(IOException e) {
			//Failed to create a watch service
			System.err.println("ERROR: Failed to create a filesystem WatchService");
			return;
		}

		try {
			imageDirPath.register(dirWatcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
		}catch(IOException e) {
			//Failed to register WatchService on the photo directory
			System.err.println("ERROR: Failed to register WatchService for photo directory");
			return;
		}
		(new WatcherThread(dirWatcher)).start();
	}

	private void sync() {
		imageFiles.clear();
		//Ensure imageFiles is not permanently grown by a large file batch
		imageFiles=new Vector<File>();
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
					//Fast, based off of extension exclusively
					String mimetype=Files.probeContentType(f.toPath());
					String type=null;
					if(mimetype!=null) {
						type=mimetype.split("/")[0];
					}
					if(type!=null && type.equals("image"))
						imageFiles.add(f);
					else
						System.err.println(new Date()+": Unreadable file: "+f);
				}catch(IOException e) {
					//Could not read file to determine mimetype
					System.err.println(new Date()+": Cannot open file: "+f);
				}
			}
		}
		if(isPaused && !pause) {
			//Pause file removed, resume
			controller.start();
		}else if(!isPaused && pause) {
			//Pause file creates, pause
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
			//Get an initial list of images before watcher is loaded
			sync();
			try {
				while(true) {
					WatchKey key;
					key=dirWatcher.take();
					//Ensures events are removed from the queue
					key.pollEvents();
					if(key.isValid())
						sync();

					key.reset();
				}
			}catch(InterruptedException e) {
				//Interrupted while waiting for new key
				System.err.println(new Date()+": ERROR: Interrupted while waiting for a watch key");
			}
		}
	}
}