package com.voidStudios.photoDisplay;

import java.io.File;
import java.util.Random;

public class WeatherManager {

	public WeatherManager() {
		//TODO Initialize paramaters necessary for weather API
	}
	
	public WeatherContainer getWeather() {
		WeatherContainer wc=new WeatherContainer();
		Random ran=new Random();
		for(int i=0; i<3; i++) {
			wc.addDay("Fri", new File("Icons/Sun.png"), ran.nextInt(100), ran.nextInt(100));
		}
		return wc;
	}

}
