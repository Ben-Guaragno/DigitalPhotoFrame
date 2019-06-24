package com.voidStudios.photoDisplay.threads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import com.github.dvdme.ForecastIOLib.FIODaily;
import com.github.dvdme.ForecastIOLib.FIOHourly;
import com.github.dvdme.ForecastIOLib.ForecastIO;
import com.voidStudios.photoDisplay.DataController;

public class WeatherThread extends Thread{
	private long time;
	private long desireRuntime;
	private DataController data;
	private ArrayList<Hashtable<String,String>> weatherDay,weatherHour;
	private String weatherSummary;
	
	private String apiKey;
	private String lat;
	private String lon;
	
	public WeatherThread(int desireRuntime, DataController data, String apiKey, String lat, String lon) {
		this.desireRuntime=desireRuntime;
		this.data=data;
		if(desireRuntime<=0) {
			System.out.println(new Date()+": The WeatherThread has been created with a runtime less than 0. Runtime: "+desireRuntime);
		}
		this.apiKey=apiKey;
		this.lat=lat;
		this.lon=lon;
	}
	
	public void run(){
		boolean firstRun=true;
		while(desireRuntime>0) {
			long runtime=System.currentTimeMillis()-time;
			if(firstRun || runtime>=desireRuntime) {
				firstRun=false;
				time=System.currentTimeMillis();
				if(!data.getIsPaused()){
					weatherDay=new ArrayList<Hashtable<String,String>>();
					weatherHour=new ArrayList<Hashtable<String,String>>();

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
							System.out.println(new Date()+": Error in weather timezone");
							e1.printStackTrace();
							isError=true;
							errCount++;
						}
						if(errCount>10){
							System.out.println(new Date()+": NullPointerException encountered "+errCount+" times. Breaking loop");
							break;
						}
					}

					//Daily Forecast
					if(fio.hasDaily()) {
						FIODaily daily=new FIODaily(fio);
						if(daily.days()<3) {
							//Forecast Error
							weatherDay=null;
							System.out.println(new Date()+ ": Error in daily forecast");
						}
						else {
							for(int j=0;j<3;j++) {
								Hashtable<String,String> temp=new Hashtable<String,String>();
								String [] h = daily.getDay(j).getFieldsArray();
								for(int i=0;i<h.length;i++) {
									temp.put(h[i], daily.getDay(j).getByKey(h[i]));
								}
								weatherDay.add(j, temp);
							}
						}
					}else {
						System.out.println(new Date()+ ": Daily forecast not available");
					}
					if(fio.hasHourly()) {
						//Hourly Forecast
						FIOHourly hourly=new FIOHourly(fio);
						if(hourly.hours()<6) {
							//Forecast Error
							weatherHour=null;
							System.out.println(new Date()+ ": Error in hourly forecast, only "+hourly.hours()+" hours");
						}
						else {
							for(int j=0;j<6;j++) {
								Hashtable<String,String> temp=new Hashtable<String,String>();
								String [] h = hourly.getHour(j).getFieldsArray();
								for(int i=0;i<h.length;i++) {
									temp.put(h[i], hourly.getHour(j).getByKey(h[i]));
								}
								weatherHour.add(j, temp);
							}
						}
					}else {
						System.out.println(new Date()+ ": Hourly forecast not available");
					}
					
					//Whole system to extract the overall hourly summary
					String s="";
					try {
						ForecastIO fio2=new ForecastIO(apiKey);
						fio2.setExcludeURL("minutely,daily,currently");

						URL url=new URL(fio2.getUrl(lat, lon));
						HttpURLConnection con = (HttpURLConnection) url.openConnection();
						con.setRequestMethod("GET");
						BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
						String inputLine;
						StringBuffer content = new StringBuffer();
						while ((inputLine = in.readLine()) != null) {
							content.append(inputLine);
						}
						in.close();
						int start=content.indexOf("summary")+"summary".length()+3;
						if(start!=-1) {
							s=content.substring(start);
							int end=s.indexOf('"')-1;
							s=s.substring(0, end);
						}else{
							System.out.println(new Date()+ ": Summary not contained in custom forecast fetch");
						}
					} catch (IOException e) {
						System.out.println(new Date()+ ": Cobbled together stuff broke... Suprise!");
						e.printStackTrace();
					}
					weatherSummary=s;
					data.setWeather(weatherDay,weatherHour,weatherSummary);
				}else {
					System.out.println(new Date()+ ": Pause file in effect");
				}
			}
			else {
				try {
					sleep(desireRuntime-runtime);
				} catch (InterruptedException e) {
					System.out.println(new Date()+ ": WeatherThread encountered error when sleeping");
					e.printStackTrace();
				}
			}
		}
		
		System.out.println(new Date()+ ": WeatherThread escaped the infinite loop somehow");
	}
}