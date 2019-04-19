package com.voidStudios.photoDisplay.threads;

import com.voidStudios.photoDisplay.DataController;

public class PhotoThread extends Thread {
	private long time;
	private long desireRuntime;
	private DataController data;
	
	public PhotoThread(long desireRuntime, DataController data) {
		super("PhotoThread");
		this.desireRuntime=desireRuntime;
		this.data=data;
		time=System.currentTimeMillis();
	}
	
	public void run() {
		while(true) {
			long runtime=System.currentTimeMillis()-time;
			if(runtime>desireRuntime) {
				data.nextFile();			
				time=System.currentTimeMillis();
			}
			else
				try {
					sleep(desireRuntime-runtime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
}
