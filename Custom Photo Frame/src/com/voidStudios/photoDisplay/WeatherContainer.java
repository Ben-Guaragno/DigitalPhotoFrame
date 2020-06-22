package com.voidStudios.photoDisplay;

import java.io.File;
import java.nio.BufferOverflowException;
import java.util.ArrayList;

public class WeatherContainer {
	private ArrayList<String> dayName;
	private ArrayList<File> dayIcon;
	private ArrayList<Integer> dayHigh;
	private ArrayList<Integer> dayLow;
	int numDays;

	public WeatherContainer() {
		int numDays=MainController.NUM_DAILY_WEATHER;
		dayName=new ArrayList<String>(numDays);
		dayIcon=new ArrayList<File>(numDays);
		dayHigh=new ArrayList<Integer>(numDays);
		dayLow=new ArrayList<Integer>(numDays);
	}
	
	public void addDay(String dayName, File iconFile, int tempHigh, int tempLow) {
		if(numDays>=MainController.NUM_DAILY_WEATHER)
			throw new BufferOverflowException();
		this.dayName.add(dayName);
		this.dayIcon.add(iconFile);
		this.dayHigh.add(tempHigh);
		this.dayLow.add(tempLow);
		numDays++;
	}
	
	public int getNumDays() {
		return numDays;
	}
	
	public String getDayName(int i) {
		return dayName.get(i);
	}
	
	public File getDayIcon(int i) {
		return dayIcon.get(i);
	}
	
	public int getDayHigh(int i) {
		return dayHigh.get(i);
	}
	
	public int getDayLow(int i) {
		return dayLow.get(i);
	}

}
