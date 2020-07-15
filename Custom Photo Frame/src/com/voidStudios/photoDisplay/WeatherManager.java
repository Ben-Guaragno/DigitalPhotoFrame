package com.voidStudios.photoDisplay;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.TimeZone;

import com.github.dvdme.ForecastIOLib.FIODaily;
import com.github.dvdme.ForecastIOLib.FIOHourly;
import com.github.dvdme.ForecastIOLib.ForecastIO;

public class WeatherManager {
	
	private static final int MAX_ERROR_COUNT=10;
	private static String[] namesOfDays=DateFormatSymbols.getInstance().getShortWeekdays();
	private String apiKey;
	private String lat;
	private String lon;
	private IconLoader iconLoader;

	public WeatherManager(String apiKey, String lat, String lon) {
		this.apiKey=apiKey;
		this.lat=lat;
		this.lon=lon;
		
		iconLoader=new IconLoader();
	}
	
	public WeatherContainer getWeather() {
		if(apiKey==null) {
			System.err.println(new Date()+": No API Key provided, skipping weather fetch.");
			return null;
		}
		
		ArrayList<Hashtable<String,String>> weatherDay,weatherHour;
		weatherDay=new ArrayList<Hashtable<String,String>>();
		weatherHour=new ArrayList<Hashtable<String,String>>();
		
		String weatherSummary;
		
		//ForecastIO.getTimezone() occasionally throws an unhandled null pointer exception
		//This code block checks for the exception and retries creation of the ForecastIO object
		//Only retries MAX_ERROR_COUNT times to prevent excessive looping
		boolean isError=true;
		int errCount=0;
		ForecastIO fio=null;
		while(isError) {
			isError=false;
			fio=new ForecastIO(apiKey);
			fio.getForecast(lat, lon);
			try{
				fio.getTimezone();
			}catch(NullPointerException e1){
//				System.err.println(new Date()+": Error in weather timezone");
//				e1.printStackTrace();
				isError=true;
				errCount++;
			}
			if(errCount>MAX_ERROR_COUNT){
				System.err.println(new Date()+": NullPointerException encountered "+errCount+" times. Breaking loop");
				break;
			}
		}
		
		if(isError) {
			System.err.println(new Date()+": Skipped weather due to error in weather timezone");
			return null;
		}
		
		//Daily Forecast
		if(fio.hasDaily()){
			FIODaily daily=new FIODaily(fio);
			if(daily.days()<MainController.NUM_DAILY_WEATHER){
				//Forecast Error
				weatherDay=null;
				System.err.println(new Date()+": Error in daily forecast, only "+daily.days()+" days");
			}else{
				for(int j=0; j<MainController.NUM_DAILY_WEATHER; j++){
					Hashtable<String,String> temp=new Hashtable<String,String>();
					String[] h=daily.getDay(j).getFieldsArray();
					for(int i=0; i<h.length; i++){
						temp.put(h[i], daily.getDay(j).getByKey(h[i]));
					}
					weatherDay.add(j, temp);
				}
			}
		}else{
			System.err.println(new Date()+": Daily forecast not available");
		}
		
		//Hourly Forecast
		if(fio.hasHourly()){
			FIOHourly hourly=new FIOHourly(fio);
			if(hourly.hours()<MainController.NUM_HOURLY_WEATHER){
				//Forecast Error
				weatherHour=null;
				System.out.println(
						new Date()+": Error in hourly forecast, only "+hourly.hours()+" hours");
			}else{
				for(int j=0; j<MainController.NUM_HOURLY_WEATHER; j++){
					Hashtable<String,String> temp=new Hashtable<String,String>();
					String[] h=hourly.getHour(j).getFieldsArray();
					for(int i=0; i<h.length; i++){
						temp.put(h[i], hourly.getHour(j).getByKey(h[i]));
					}
					weatherHour.add(j, temp);
				}
			}
		}else{
			System.err.println(new Date()+": Hourly forecast not available");
		}
		
		//Whole system to extract the overall hourly summary
		String s="";
		try{
			ForecastIO fio2=new ForecastIO(apiKey);
			fio2.setExcludeURL("minutely,daily,currently");

			URL url=new URL(fio2.getUrl(lat, lon));
			HttpURLConnection con=(HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			BufferedReader in=new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer content=new StringBuffer();
			while((inputLine=in.readLine())!=null){
				content.append(inputLine);
			}
			in.close();
			int start=content.indexOf("summary")+"summary".length()+3;
			if(start!=-1){
				s=content.substring(start);
				int end=s.indexOf('"')-1;
				s=s.substring(0, end);
			}else{
				System.err.println(new Date()+": Summary not contained in custom forecast fetch");
			}
		}catch(IOException e){
			System.err.println(new Date()+": Failed to fetch overall hourly summary");
			e.printStackTrace();
		}
		weatherSummary=s;

		WeatherContainer wc=new WeatherContainer();
		//Load daily weather into WC
		for(int i=0; i<weatherDay.size(); i++) {
			Hashtable<String, String> ht=weatherDay.get(i);
			String dayName=getDayName(i);
			String icon=ht.get("icon");
			icon=icon.substring(1, icon.length()-1);
			File dayIcon=iconLoader.getIcon(icon);
			String dayHigh=ht.get("apparentTemperatureHigh");
			String dayLow=ht.get("apparentTemperatureLow");
			wc.addDay(dayName, dayIcon, roundTemp(dayHigh), roundTemp(dayLow));
		}
		
		//Load hourly into WC
		for(int i=0; i<weatherHour.size(); i++) {
			Hashtable<String, String> ht=weatherHour.get(i);
			String hourTime=null;
			try {
				String timeGMT=ht.get("time")+" "+"GMT";
				SimpleDateFormat sdf =  new SimpleDateFormat("dd-MM-yyyy HH:mm:ss z");
				TimeZone tz = TimeZone.getDefault();
				sdf.setTimeZone(tz);
				Date date=null;
				date=sdf.parse(timeGMT);
				sdf = new SimpleDateFormat("h a");
				hourTime = sdf.format(date);
			} catch (ParseException e) {
				//Failed to parse time
				hourTime="";
			}
			String icon=ht.get("icon");
			icon=icon.substring(1, icon.length()-1);
			File hourIcon=iconLoader.getIcon(icon);
			String hourTemp=ht.get("apparentTemperature");
			String hourSumm=ht.get("summary");
			hourSumm=hourSumm.substring(1, hourSumm.length()-1);
			wc.addHour(hourTime, hourIcon, roundTemp(hourTemp), hourSumm);
		}
		
		wc.addSummary(weatherSummary);
		
		return wc;
	}
	
	private static String roundTemp(String temp) {
		Float f=Float.parseFloat(temp);
		return Math.round(f)+"";
	}
	
	private static String getDayName(int i) {
		int today=Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
		int dayNum=(today-1+i)%7;
		String day=namesOfDays[dayNum+1];
		return day;
	}

}
