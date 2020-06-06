package com.voidStudios.photoDisplay;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsLoader {

	private static final String LAT_DEFAULT="0";
	private static final String LON_DEFAULT="0";
	private static final String API_KEY_DEFAULT="abcdefg1234567";
	private static final String PHOTO_DEFAULT="5";
	private static final String WEATHER_DEFAULT="1";
	private static final String DATE_DEFAULT="60";
	private static final String ENABLE_DATE_DEFAULT="true";
	private Properties props;

	public SettingsLoader(String configFileString) {
		props=new Properties();
		File configFile=new File(configFileString);
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(configFile);
			props.load(fis);
		}catch(Exception e) {
			//Configuration file could not be found or read
			//Defaults will automatically be substituted
		}
		try {
			if(fis!=null)
				fis.close();
		}catch(IOException e) {
			System.err.println("WARNING: Configuration Input Stream failed to close cleanly.");
		}
		
	}

	public String getLat() {
		return props.getProperty("lat", LAT_DEFAULT);
	}

	public String getLon() {
		return props.getProperty("lon", LON_DEFAULT);
	}

	public String getAPIKey() {
		return props.getProperty("apiKey", API_KEY_DEFAULT);
	}

	public int getPhotoUpdate() {
		String s=props.getProperty("photoUpdate", PHOTO_DEFAULT);
		try {
			int x=Integer.parseInt(s)*1000;
			return x;
		}catch(NumberFormatException e) {
			System.err.println("WARNING: photoUpdate config value not a number. Defaulting to "+PHOTO_DEFAULT+".");
			return Integer.parseInt(PHOTO_DEFAULT)*1000;
		}
	}

	public int getWeatherUpdate() {
		String s=props.getProperty("weatherUpdate", WEATHER_DEFAULT);
		try {
			int x=Integer.parseInt(s)*60000;
			return x;
		}catch(NumberFormatException e) {
			System.err.println("WARNING: weatherUpdate config value not a number. Defaulting to "+WEATHER_DEFAULT+".");
			return Integer.parseInt(WEATHER_DEFAULT)*60000;
		}
	}
	
	public int getDateUpdate() {
		String s=props.getProperty("dateUpdate", DATE_DEFAULT);
		try {
			int x=Integer.parseInt(s)*60000;
			return x;
		}catch(NumberFormatException e) {
			System.err.println("WARNING: dateUpdate config value not a number. Defaulting to "+DATE_DEFAULT+".");
			return Integer.parseInt(DATE_DEFAULT)*60000;
		}
	}
	
	public boolean getIsDateEnabled() {
		String s=props.getProperty("enableDate", ENABLE_DATE_DEFAULT);
		if(s.equalsIgnoreCase("true")) {
			return true;
		}else if(s.equalsIgnoreCase("false")) {
			return false;
		}else {
			System.err.println("WARNING: enableDate config value not true/false. Defaulting to "+ENABLE_DATE_DEFAULT+".");
			return true;
		}
	}

}