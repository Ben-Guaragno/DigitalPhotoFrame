package com.voidStudios.photoDisplay;

import java.io.File;
import java.util.Random;

public class WeatherManager {
	
	private boolean tmp=false;

	public WeatherManager() {
		//TODO Initialize paramaters necessary for weather API
	}
	
	public WeatherContainer getWeather() {
		WeatherContainer wc=new WeatherContainer();
		Random ran=new Random();
		for(int i=0; i<3; i++) {
			wc.addDay("Fri", new File("Icons/Sun.png"), ran.nextInt(100), ran.nextInt(100));
		}
		String s="Extremely Cloudy";
		if(tmp) {
			s="Clear";
			tmp=false;
		}else
			tmp=true;
		for(int i=0; i<6; i++) {
			wc.addHour("12 PM", new File("Icons/Sun.png"), ran.nextInt(100), s);
		}
		return wc;
	}

}
