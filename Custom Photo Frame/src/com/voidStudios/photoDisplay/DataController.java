package com.voidStudios.photoDisplay;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;

public class DataController {
	private String date,day;
	private View view;
	private ImageDirectory imageDirectory;
	private File file;
	private ArrayList<Hashtable<String,String>> weatherDay,weatherHour;
	private String weatherSummary;
	private DecimalFormat df=new DecimalFormat("#");
	private final String[] dayOfWeek= {"Mon","Tue","Wed","Thu","Fri","Sat","Sun"};
	private HashMap<String,File> icons;
	
	public DataController(ImageDirectory imageDirectory, IconLoader iconLoader) {
		date="";
		this.view=null;
		this.imageDirectory=imageDirectory;
		file=imageDirectory.nextFile();
		icons=iconLoader.getIcons();
	}
	
	public void requestUpdate() {
		if(!imageDirectory.isPaused)
			view.repaint();
	}
	//Sets View pointer after DataController has been created
	public void updateView(View view) {
		this.view=view;
	}
	
	public String getDate() {
		return date;
	}
	public String getDay() {
		return day;
	}
	public void setDate(String date, String day) {
		this.date=date;
		this.day=day;
		requestUpdate();
	}
	public void nextFile() {
		if(imageDirectory.isPaused)
			return;
		file=imageDirectory.nextFile();
		requestUpdate();
	}
	public File getFile() {
		return file;
	}
	public void setWeather(ArrayList<Hashtable<String,String>> weatherDay,ArrayList<Hashtable<String,String>> weatherHour,String weatherSummary) {
		if(this.weatherHour!=null && this.weatherHour.equals(weatherHour)) {
			System.out.println(new Date()+ ": New hourly weather identical to old");
		}
		if(weatherDay==null)
			System.out.println(new Date()+ ": New daily weather is null");
		if(weatherHour==null)
			System.out.println(new Date()+ ": New hourly weather is null");
		if(weatherSummary==null || weatherSummary.length()==0)
			System.out.println(new Date()+ ": New weather summary is null");
		this.weatherDay=weatherDay;
		this.weatherHour=weatherHour;
		this.weatherSummary=weatherSummary;
		modWeather(true);
		requestUpdate();
	}
	private void modWeather(boolean degree) {
		if(weatherDay!=null) {
			for(Hashtable<String,String> h:weatherDay) {
				String max=h.get("apparentTemperatureHigh");
				String low=h.get("apparentTemperatureLow");
				String icon=h.get("icon");
				String symbol="";

				icon=icon.substring(1, icon.length()-1);

				if(degree)
					symbol="\u00b0";
				h.replace("apparentTemperatureHigh", df.format(Float.parseFloat(max))+symbol);
				h.replace("apparentTemperatureLow", df.format(Float.parseFloat(low))+symbol);
				h.replace("icon", icon);
			}
		}
		if(weatherHour!=null) {
			for(Hashtable<String,String> h:weatherHour) {
				String sum=h.get("summary");
				String temp=h.get("apparentTemperature");
				String icon=h.get("icon");

				String symbol="";
				if(degree)
					symbol="\u00b0";

				sum=sum.substring(1).substring(0, sum.length()-2);
				icon=icon.substring(1, icon.length()-1);

				h.replace("summary", sum);
				h.replace("apparentTemperature", df.format(Float.parseFloat(temp))+symbol);
				h.replace("icon", icon);
			}
		}
	}
	public ArrayList<Hashtable<String,String>> getWeatherDay() {
		return weatherDay;
	}
	public ArrayList<Hashtable<String,String>> getWeatherHour() {
		return weatherHour;
	}
	public String getWeatherSummary() {
		return weatherSummary;
	}
	public HashMap<String,File> getIcons(){
		return icons;
	}
	public String getDayOfWeek(int day) {
		return dayOfWeek[day%7];
	}
	public boolean getIsPaused() {
		return imageDirectory.isPaused;
	}
}
