package com.voidStudios.photoDisplay;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

	private static final int ONE_HOUR_MILLIS=3600000;
	private static final int OFFSET_FROM_HOUR=5000;
	private Timer timer;
	private SimpleDateFormat dateFormat;
	private MainController mainController;
	SettingsLoader sloader;

	public Controller(MainController mainController, SettingsLoader sloader) {
		this.mainController=mainController;
		this.sloader=sloader;
		dateFormat=new SimpleDateFormat("MMMM d");
		
		start();
	}
	
	private void photoTask() {
		System.out.println(new Date()+": Photo Task");
	}
	
	private void weatherTask() {
		System.out.println(new Date()+": Weather Task");
	}
	
	private void dateTask() {
		String dateS = dateFormat.format(new Date());
		mainController.setDateLabel(dateS);
		
		System.out.println(new Date()+": Date Task");
	}
	
	public void pause() {
		timer.cancel();
	}
	
	private long calcStartTime(int desiredTime) {
		//Evenly start at 5 seconds past the current hour
		long currentTime=System.currentTimeMillis();
		long roundedCurrent=(currentTime/ONE_HOUR_MILLIS)*ONE_HOUR_MILLIS+OFFSET_FROM_HOUR;
		
		//Strip excess run operations
		long excess=currentTime%ONE_HOUR_MILLIS;
		long finalTime=roundedCurrent+(excess/desiredTime)*desiredTime;

		//Ensure the scheduled time is not in the future
		//Ensures that the task will be immediately run at startup
		if(finalTime>currentTime)
			finalTime-=desiredTime;
		
		return finalTime;
	}
	
	public void start() {
		long startTime;
		int desiredTime;
		timer=new Timer();
		TimerTask tmp;
		
		//Photo
		tmp=new TimerTask() {
			@Override
			public void run() {
				photoTask();
			}
		};
		desiredTime=sloader.getPhotoUpdate();
		startTime=calcStartTime(desiredTime);
		timer.scheduleAtFixedRate(tmp, new Date(startTime), desiredTime);
		
		//Weather
		tmp=new TimerTask() {
			@Override
			public void run() {
				weatherTask();
			}
		};
		desiredTime=sloader.getWeatherUpdate();
		startTime=calcStartTime(desiredTime);
		timer.scheduleAtFixedRate(tmp, new Date(startTime), desiredTime);
		
		//Date
		if(sloader.getIsDateEnabled()) {
			tmp=new TimerTask() {
				@Override
				public void run() {
					dateTask();
				}
			};
			desiredTime=sloader.getDateUpdate();
			startTime=calcStartTime(desiredTime);
			timer.scheduleAtFixedRate(tmp, new Date(startTime), desiredTime);
		}
	}
}
