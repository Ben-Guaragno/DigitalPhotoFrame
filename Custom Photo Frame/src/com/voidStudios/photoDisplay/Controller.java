package com.voidStudios.photoDisplay;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

public class Controller {

	private static final int ONE_HOUR_MILLIS=1000*60*60;
	private static final int OFFSET_FROM_HOUR=5000;	//5 seconds in milliseconds
	private Timer timer;
	private SimpleDateFormat dateFormat;
	private MainController mainController;
	private SettingsLoader sloader;
	private WeatherManager weatherManager;
	private ImageDirectory iDir;
	private EnergyManager energyManager;

	public Controller(MainController mainController, SettingsLoader sloader) {
		this.mainController=mainController;
		this.sloader=sloader;
		dateFormat=new SimpleDateFormat("MMMM d");

		iDir=new ImageDirectory("photos", this);
		weatherManager=new WeatherManager(sloader.getAPIKey(), sloader.getLat(), sloader.getLon());
		energyManager=new EnergyManager(sloader.getIP(), sloader.getPassword());
	}

	public void pause() {
		timer.cancel();
		timer.purge();
		timer=null;
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
		while(finalTime>currentTime)
			finalTime-=desiredTime;

		return finalTime;
	}

	public void start() {
		//Ensures that multiple timers cannot be started
		if(timer!=null)
			pause();

		long startTime;
		int desiredTime;
		timer=new Timer();
		TimerTask task;

		//Photo
		task=new TimerTask() {
			@Override
			public void run() {
				File f=iDir.nextFile();
				if(f!=null)
					mainController.setImage(f, sloader.getPhotoCenterAlign());
			}
		};
		desiredTime=sloader.getPhotoUpdate();
		startTime=calcStartTime(desiredTime);
		timer.scheduleAtFixedRate(task, new Date(startTime), desiredTime);

		//Weather
		task=new TimerTask() {
			@Override
			public void run() {
				try {
					WeatherContainer wc=weatherManager.getWeather();
					if(wc!=null)
						mainController.setWeather(wc);
				}catch(IllegalArgumentException|ParseException|IOException|InterruptedException|JSONException e) {
					System.err.println(new Date()+": WARNING: Exception "+e.getClass().getName()+"recieved. Hiding weather. Exception details to follow:");
					System.err.println(e.toString());
					mainController.hideWeather();
				}
			}
		};
		desiredTime=sloader.getWeatherUpdate();
		startTime=calcStartTime(desiredTime);
		timer.scheduleAtFixedRate(task, new Date(startTime), desiredTime);

		//Date
		if(sloader.getIsDateEnabled()) {
			task=new TimerTask() {
				@Override
				public void run() {
					String dateS=dateFormat.format(new Date());
					mainController.setDateLabel(dateS);
				}
			};
			desiredTime=sloader.getDateUpdate();
			startTime=calcStartTime(desiredTime);
			timer.scheduleAtFixedRate(task, new Date(startTime), desiredTime);
		}

		//Energy
		if(sloader.getIsEnergyEnabled()) {
			task=new TimerTask() {
				@Override
				public void run() {
					String s=energyManager.getEnergy();
					mainController.setEnergy(s);
				}
			};
			desiredTime=sloader.getEnergyUpdate();
			startTime=calcStartTime(desiredTime);
			timer.scheduleAtFixedRate(task, new Date(startTime), desiredTime);
		}
	}
}
