package com.voidStudios.photoDisplay;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsLoader {

	private static final String LAT_DEFAULT="0";
	private static final String LON_DEFAULT="0";
	private static final String API_KEY_DEFAULT=null;
	private static final String PHOTO_DEFAULT="5";
	private static final String WEATHER_DEFAULT="1";
	private static final String DATE_DEFAULT="60";
	private static final String ENERGY_DEFAULT="5";
	private static final String IP_DEFAULT="127.0.0.1";
	private static final String PASSWORD_DEFAULT="password";
	private static final boolean ENABLE_DATE_DEFAULT=true;
	private static final boolean PHOTO_CENTER_ALIGN=true;
	private static final boolean ENABLE_ENERGY_DEFAULT=false;
	private Properties props;

	public SettingsLoader(String configFileString) {
		props=new Properties();
		File configFile=configFileString!=null ? new File(configFileString) : null;
		FileInputStream fis=null;
		try {
			fis=new FileInputStream(configFile);
			props.load(fis);
		}catch(Exception e) {
			//Configuration file could not be found or read
			//Defaults will automatically be substituted
			System.err.println("WARNING: No configuration file supplied, using default values.");
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
		String s=props.getProperty("photoUpdateSec", PHOTO_DEFAULT);
		try {
			int x=Integer.parseInt(s)*1000;
			return x;
		}catch(NumberFormatException e) {
			System.err.println("WARNING: photoUpdate config value not a number. Defaulting to "+PHOTO_DEFAULT+".");
			return Integer.parseInt(PHOTO_DEFAULT)*1000;
		}
	}

	public int getWeatherUpdate() {
		String s=props.getProperty("weatherUpdateMin", WEATHER_DEFAULT);
		try {
			int x=Integer.parseInt(s)*60000;
			return x;
		}catch(NumberFormatException e) {
			System.err.println("WARNING: weatherUpdate config value not a number. Defaulting to "+WEATHER_DEFAULT+".");
			return Integer.parseInt(WEATHER_DEFAULT)*60000;
		}
	}

	public int getDateUpdate() {
		String s=props.getProperty("dateUpdateMin", DATE_DEFAULT);
		try {
			int x=Integer.parseInt(s)*60000;
			return x;
		}catch(NumberFormatException e) {
			System.err.println("WARNING: dateUpdate config value not a number. Defaulting to "+DATE_DEFAULT+".");
			return Integer.parseInt(DATE_DEFAULT)*60000;
		}
	}

	public boolean getIsDateEnabled() {
		String s=props.getProperty("enableDate", ENABLE_DATE_DEFAULT+"");
		if(s.equalsIgnoreCase("true")) {
			return true;
		}else if(s.equalsIgnoreCase("false")) {
			return false;
		}else {
			System.err.println("WARNING: enableDate config value not true/false. Defaulting to "+ENABLE_DATE_DEFAULT+".");
			return ENABLE_DATE_DEFAULT;
		}
	}

	public boolean getPhotoCenterAlign() {
		String s=props.getProperty("centerPhoto", PHOTO_CENTER_ALIGN+"");
		if(s.equalsIgnoreCase("true")) {
			return true;
		}else if(s.equalsIgnoreCase("false")) {
			return false;
		}else {
			System.err.println("WARNING: photoAlign config value not true/false. Defaulting to "+PHOTO_CENTER_ALIGN+".");
			return PHOTO_CENTER_ALIGN;
		}
	}

	public int getEnergyUpdate() {
		String s=props.getProperty("energyUpdate", ENERGY_DEFAULT);
		try {
			int x=Integer.parseInt(s)*60000;
			return x;
		}catch(Exception e) {
			System.err.println("WARNING: energyUpdate config value not a number. Defaulting to "+ENERGY_DEFAULT+".");
			return Integer.parseInt(ENERGY_DEFAULT)*1000;
		}
	}

	public boolean getIsEnergyEnabled() {
		String s=props.getProperty("enableEnergy", ENABLE_ENERGY_DEFAULT+"");
		if(s.equalsIgnoreCase("true")) {
			return true;
		}else if(s.equalsIgnoreCase("false")) {
			return false;
		}else {
			System.err.println("WARNING: enableEnergy config value not true/false. Defaulting to "+ENABLE_ENERGY_DEFAULT+".");
			return ENABLE_ENERGY_DEFAULT;
		}
	}

	public String getIP() {
		return props.getProperty("ip", IP_DEFAULT);
	}

	public String getPassword() {
		return props.getProperty("password", PASSWORD_DEFAULT);
	}

}
