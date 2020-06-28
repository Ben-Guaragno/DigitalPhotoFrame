package com.voidStudios.photoDisplay;

import java.io.File;
import java.nio.BufferOverflowException;
import java.util.ArrayList;

public class WeatherContainer {
	private ArrayList<String> dayName;
	private ArrayList<File> dayIcon;
	private ArrayList<Integer> dayHigh;
	private ArrayList<Integer> dayLow;
	private ArrayList<String> hourTime;
	private ArrayList<File> hourIcon;
	private ArrayList<Integer> hourTemp;
	private ArrayList<String> hourSumm;
	int numDays;
	int numHours;

	public WeatherContainer() {
		int numDays=MainController.NUM_DAILY_WEATHER;
		dayName=new ArrayList<String>(numDays);
		dayIcon=new ArrayList<File>(numDays);
		dayHigh=new ArrayList<Integer>(numDays);
		dayLow=new ArrayList<Integer>(numDays);
		hourTime=new ArrayList<String>(numDays);
		hourIcon=new ArrayList<File>(numDays);
		hourTemp=new ArrayList<Integer>(numDays);
		hourSumm=new ArrayList<String>(numDays);
	}
	
	public void addDay(String dayName, File dayIcon, int dayHigh, int dayLow) {
		if(numDays>=MainController.NUM_DAILY_WEATHER)
			throw new BufferOverflowException();
		this.dayName.add(dayName);
		this.dayIcon.add(dayIcon);
		this.dayHigh.add(dayHigh);
		this.dayLow.add(dayLow);
		numDays++;
	}
	
	public void addHour(String hourTime, File hourIcon, int hourTemp, String hourSumm) {
		if(numHours>=MainController.NUM_HOURLY_WEATHER)
			throw new BufferOverflowException();
		this.hourTime.add(hourTime);
		this.hourIcon.add(hourIcon);
		this.hourTemp.add(hourTemp);
		this.hourSumm.add(hourSumm);
		numHours++;
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
	
	public int getNumHours() {
		return numHours;
	}
	
	public String getHourTime(int i) {
		return hourTime.get(i);
	}
	
	public File getHourIcon(int i) {
		return hourIcon.get(i);
	}
	
	public int getHourTemp(int i) {
		return hourTemp.get(i);
	}
	
	public String getHourSumm(int i) {
		return hourSumm.get(i);
	}

}
