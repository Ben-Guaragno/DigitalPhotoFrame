package com.voidStudios.photoDisplay;

import java.io.File;
import java.nio.BufferOverflowException;
import java.util.ArrayList;

public class WeatherContainer {

	private ArrayList<String> dayName;
	private ArrayList<File> dayIcon;
	private ArrayList<String> dayHigh;
	private ArrayList<String> dayLow;
	private ArrayList<String> hourTime;
	private ArrayList<File> hourIcon;
	private ArrayList<String> hourTemp;
	private ArrayList<String> hourSumm;
	int numDays;
	int numHours;
	String summary;

	public WeatherContainer() {
		//FIXME daily weather static used for hourly
		//also probably initialize the numDays numHours to be more clear
		int numDays=MainController.NUM_DAILY_WEATHER;
		dayName=new ArrayList<String>(numDays);
		dayIcon=new ArrayList<File>(numDays);
		dayHigh=new ArrayList<String>(numDays);
		dayLow=new ArrayList<String>(numDays);
		hourTime=new ArrayList<String>(numDays);
		hourIcon=new ArrayList<File>(numDays);
		hourTemp=new ArrayList<String>(numDays);
		hourSumm=new ArrayList<String>(numDays);
	}

	public void addDay(String dayName, File dayIcon, String dayHigh, String dayLow) {
		if(numDays>=MainController.NUM_DAILY_WEATHER)
			throw new BufferOverflowException();
		this.dayName.add(dayName);
		this.dayIcon.add(dayIcon);
		this.dayHigh.add(dayHigh);
		this.dayLow.add(dayLow);
		numDays++;
	}

	public void addHour(String hourTime, File hourIcon, String hourTemp, String hourSumm) {
		if(numHours>=MainController.NUM_HOURLY_WEATHER)
			throw new BufferOverflowException();
		this.hourTime.add(hourTime);
		this.hourIcon.add(hourIcon);
		this.hourTemp.add(hourTemp);
		this.hourSumm.add(hourSumm);
		numHours++;
	}

	public void addSummary(String summary) {
		this.summary=summary;
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

	public String getDayHigh(int i) {
		return dayHigh.get(i);
	}

	public String getDayLow(int i) {
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

	public String getHourTemp(int i) {
		return hourTemp.get(i);
	}

	public String getHourSumm(int i) {
		return hourSumm.get(i);
	}

	public String getSummary() {
		return summary;
	}

}
