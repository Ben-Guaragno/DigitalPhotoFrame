package com.voidStudios.photoDisplay;

import java.io.File;
import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONObject;

public class WeatherManager {

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
	
	//TODOL delete once testing is done
	public static void main(String[] args) throws Exception {
		System.out.println(new Date()+": Initializing JFX Photo Frame.");
		
		SettingsLoader sl=new SettingsLoader("config");
		
		WeatherManager wm=new WeatherManager(sl.getAPIKey(), sl.getLat(), sl.getLon());
		wm.getWeather();
	}
	
	public WeatherContainer getWeather() throws NoRouteToHostException, IOException, InterruptedException, ParseException {
		if(apiKey==null) {
			System.err.println(new Date()+": No API Key provided, skipping weather fetch.");
			throw new NoRouteToHostException("No API key provided");
		}
		HttpRequest request = HttpRequest.newBuilder()
				.uri(buildURI())
				.method("GET", HttpRequest.BodyPublishers.noBody()).build();
		HttpResponse<String> response = HttpClient.newHttpClient()
				.send(request, HttpResponse.BodyHandlers.ofString());
		
		JSONObject json=new JSONObject(response.body());
		
		WeatherContainer wc=new WeatherContainer();
		
		wc.addSummary(json.getJSONArray("days").getJSONObject(0).getString("description"));
		
		//Load daily weather into WC
		JSONArray values=json.getJSONArray("days");
		for(int i=0; i<MainController.NUM_DAILY_WEATHER; i++) {
			JSONObject dayValues=values.getJSONObject(i);
			
			String date=dayValues.getString("datetime");
			String dayName=getDayName(date);
			
			String icon=dayValues.getString("icon");
			File dayIcon=iconLoader.getIcon(icon);
			
			Double dayHighDouble=dayValues.getDouble("feelslikemax");
			String dayHigh=Math.round(dayHighDouble)+"";
			
			Double dayLowDouble=dayValues.getDouble("feelslikemin");
			String dayLow=Math.round(dayLowDouble)+"";
			
			wc.addDay(dayName, dayIcon, dayHigh, dayLow);
		}
		
		values=values.getJSONObject(0).getJSONArray("hours");
		SimpleDateFormat sdf=new SimpleDateFormat("H");
		Date d=new Date();
		int startHour=Integer.parseInt(sdf.format(d));
		for(int i=startHour; i<MainController.NUM_HOURLY_WEATHER+startHour; i++) {
			//FIXME day rolled over
			if(i>23)break;
			JSONObject hourValues=values.getJSONObject(i);
			
			String time=hourValues.getString("datetime");
			sdf=new SimpleDateFormat("HH:mm:ss");
			Calendar c=Calendar.getInstance();
			c.setTime(sdf.parse(time));
			sdf=new SimpleDateFormat("h a");
			String hourTime=sdf.format(c.getTime());
			
			String icon=hourValues.getString("icon");
			File hourIcon=iconLoader.getIcon(icon);
			
			Double hourTempDouble=hourValues.getDouble("feelslike");
			String hourTemp=Math.round(hourTempDouble)+"";
			
			//TODO determine if this tag is the best analog to Dark Sky
			String hourSumm=hourValues.getString("conditions");
			
			
			wc.addHour(hourTime, hourIcon, hourTemp, hourSumm);
		}

		return wc;
	}
	
	//TODO javadoc javadoc javadoc javadoc
	private static String getDayName(String date) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar c=Calendar.getInstance();
		c.setTime(sdf.parse(date));
		int dayNum=c.get(Calendar.DAY_OF_WEEK);
		String day=namesOfDays[dayNum];
		return day;
	}
	
	private URI buildURI() {
		//Build request, adding dynamic data as needed
		StringBuilder s=new StringBuilder();
		s.append("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/");
		s.append(lat+"%2C"+lon);
		s.append("/");
		s.append(dateBuilder());
		s.append("?unitGroup=us&elements=datetime%2Cfeelslikemax%2Cfeelslikemin%2Cfeelslike%2Cconditions%2Cdescription%2Cicon&include=days%2Chours&key=");
		s.append(apiKey);
		s.append("&contentType=json");
		
		//Create URI
		URI uri=URI.create(s.toString());

		return uri;
	}
	
	/**
	 * Builds a properly formatted string for use in the Visual Crossing API.
	 * Gives a 3 day range starting today.
	 * @return Current Date / 2 Days From Now
	 */
	private static String dateBuilder() {
		//Format the start date (today)
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date d=new Date();
		String dateStart=sdf.format(d);
		
		//Add 2 days and format
		Calendar c=Calendar.getInstance();
		c.setTime(d);
		c.add(Calendar.DATE, 2);
		String dateEnd=sdf.format(c.getTime());

		return dateStart+"/"+dateEnd;
	}

}
