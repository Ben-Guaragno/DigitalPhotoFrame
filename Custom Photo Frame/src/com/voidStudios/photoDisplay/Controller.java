package com.voidStudios.photoDisplay;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Controller extends Thread {

	private long desiredTime=5000;
	private Timer timer;

	public Controller() {
		timer=new Timer();
		TimerTask tmp=new TimerTask() {
			@Override
			public void run() {
				task();
			}
		};
		//Evenly start at 0 seconds past the minute.
		long roundedCurrent=(System.currentTimeMillis()/10000)*10000;
		timer.scheduleAtFixedRate(tmp, new Date(roundedCurrent), desiredTime);
	}
	
	private void task() {
		System.out.println(new Date()+": The thing is here!");
	}
}
