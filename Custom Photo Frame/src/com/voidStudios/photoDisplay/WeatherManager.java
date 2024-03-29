package com.voidStudios.photoDisplay;

import java.io.File;
import java.io.IOException;
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
import org.json.JSONException;
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
	
	/**
	 * Makes required call to Visual Crossing for weather data and processes the reply.
	 * Returns a WeatherContainer.
	 * 
	 * @return WeatherContainer containing weather information
	 * @throws IllegalArgumentException No API key provided
	 * @throws ParseException Failed to parse a datetime in response
	 * @throws IOException Failed to register response from Visual Crossing
	 * @throws InterruptedException Failed to register response from Visual Crossing
	 * @throws JSONException Response from Visual Crossing not valid JSON
	 */
	public WeatherContainer getWeather() throws IllegalArgumentException, ParseException, IOException, InterruptedException, JSONException {
		if(apiKey==null) {
			System.err.println(new Date()+": No API Key provided, skipping weather fetch.");
			throw new IllegalArgumentException("No API key provided");
		}
		
		//Java request syntax from Visual Crossing docs with added exception handling
		HttpRequest request = HttpRequest.newBuilder()
				.uri(buildURI())
				.method("GET", HttpRequest.BodyPublishers.noBody()).build();
		HttpResponse<String> response= null;
		try {
			response=HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		}catch(IOException|InterruptedException e) {
			System.err.println(new Date()+": ERROR: HTTP request to Visual Crossing failed.");
			throw e;
		}
		
		//Process request into a JSONObject
		JSONObject json=null;
		try {
			json=new JSONObject(response.body());
		}catch(JSONException e) {
			System.err.println(new Date()+": ERROR: JSON response from Visual Crossing not valid");
			e.printStackTrace();
			System.err.println(new Date()+": Full response body to follow:");
			System.err.println(response.body());
			System.err.println(new Date()+": Response body concluded. Re-throwing exception.");
			throw e;
		}
		
		WeatherContainer wc=new WeatherContainer();
		
		//Load weather summary into WC
		String summary=json.getString("description");
		if(summary.length()>1)
			summary=summary.substring(0, summary.length()-1);
		wc.addSummary(summary);
		
		//Load daily weather into WC
		JSONArray values=json.getJSONArray("days");
		for(int i=0; i<MainController.NUM_DAILY_WEATHER; i++) {
			JSONObject dayValues=values.getJSONObject(i);
			
			String date=dayValues.getString("datetime");
			String dayName;
			try {
				dayName=getDayName(date);
			}catch(ParseException e) {
				System.err.println(new Date()+": Failed to parse datetime for day "+i+". datetime: "+date);
				e.printStackTrace();
				throw e;
			}
			
			String icon=dayValues.getString("icon");
			File dayIcon=iconLoader.getIcon(icon);
			
			Double dayHighDouble=dayValues.getDouble("feelslikemax");
			String dayHigh=Math.round(dayHighDouble)+"";
			
			Double dayLowDouble=dayValues.getDouble("feelslikemin");
			String dayLow=Math.round(dayLowDouble)+"";
			
			wc.addDay(dayName, dayIcon, dayHigh, dayLow);
		}
		
		//Load hourly weather into WC
		JSONArray dayValues=values;
		values=dayValues.getJSONObject(0).getJSONArray("hours");
		SimpleDateFormat sdf=new SimpleDateFormat("H");
		Date d=new Date();
		int startHour=Integer.parseInt(sdf.format(d));
		int i=startHour;
		int iMax=MainController.NUM_HOURLY_WEATHER+startHour;
		while(i<iMax) {
			//Handle hourly weather rolling over into the next day
			if(i>23) {
				i=0;
				values=dayValues.getJSONObject(1).getJSONArray("hours");
				iMax=iMax-24;
			}
			JSONObject hourValues=values.getJSONObject(i);
			
			String time=hourValues.getString("datetime");
			sdf=new SimpleDateFormat("HH:mm:ss");
			Calendar c=Calendar.getInstance();
			try {
				c.setTime(sdf.parse(time));
			}catch(ParseException e) {
				System.err.println(new Date()+": Failed to parse datetime for hour "+i+". datetime: "+time);
				e.printStackTrace();
				throw e;
			}
			sdf=new SimpleDateFormat("h a");
			String hourTime=sdf.format(c.getTime());
			
			String icon=hourValues.getString("icon");
			File hourIcon=iconLoader.getIcon(icon);
			
			Double hourTempDouble=hourValues.getDouble("feelslike");
			String hourTemp=Math.round(hourTempDouble)+"";
			
			String hourSumm=hourValues.getString("conditions");
			
			wc.addHour(hourTime, hourIcon, hourTemp, hourSumm);
			i++;
		}

		return wc;
	}
	
	/**
	 * Gives the named day of the week for a given date.
	 * @param date Date string in the yyyy-MM-dd format
	 * @return String containing the abbreviated day of the week
	 * @throws ParseException Couldn't parse date properly
	 */
	private static String getDayName(String date) throws ParseException {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Calendar c=Calendar.getInstance();
		c.setTime(sdf.parse(date));
		int dayNum=c.get(Calendar.DAY_OF_WEEK);
		String day=namesOfDays[dayNum];
		return day;
	}
	
	/**
	 * Builds the request URL for fetching weather.
	 * Adds location and API as needed. Converts URL to a URI object.
	 * 
	 * @return URI for weather request
	 */
	private URI buildURI() {
		//Build request, adding dynamic data as needed
		StringBuilder s=new StringBuilder();
		s.append("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/");
		s.append(lat+"%2C"+lon);
		s.append("/");
		s.append(dateBuilder());
		s.append("?unitGroup=us&elements=datetime%2CdatetimeEpoch%2Cfeelslikemax%2Cfeelslikemin%2Cfeelslike%2Cconditions%2Cdescription"
				+ "%2Cicon&include=events%2Ccurrent%2Cdays%2Calerts%2Chours%2Cfcst&key=");
		s.append(apiKey);
		s.append("&contentType=json");
		
		//Create URI
		URI uri=URI.create(s.toString());

		return uri;
	}
	
	/**
	 * Builds a properly formatted date range string for use in the Visual Crossing API.
	 * Gives a 3 day range starting today.
	 * 
	 * @return <Current Date> / <Date 2 Days From Now>
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
