package com.voidStudios.photoDisplay;

import java.io.File;
import java.util.HashMap;

public class IconLoader {
	private HashMap<String,File> icons;
	
	public IconLoader() {
		icons=new HashMap<String,File>();
		load();
	}
	
	private void load() {
		File fog=new File("./Icons/Cloud-Fog.png");
		File sleet=new File("./Icons/Cloud-Hail.png");
		File partlyCloudNight=new File("./Icons/Cloud-Moon.png");
		File rain=new File("./Icons/Cloud-Rain.png");
		File snow=new File("./Icons/Cloud-Snow-Alt.png");
		File partlyCloudDay=new File("./Icons/Cloud-Sun.png");
		File cloudy=new File("./Icons/Cloud.png");
		File clearNight=new File("./Icons/Moon.png");
		File clearDay=new File("./Icons/Sun.png");
		File wind=new File("./Icons/Wind.png");
		
		icons.put("fog", fog);
		icons.put("sleet", sleet);
		icons.put("partly-cloudy-night", partlyCloudNight);
		icons.put("rain", rain);
		icons.put("snow", snow);
		icons.put("partly-cloudy-day", partlyCloudDay);
		icons.put("cloudy", cloudy);
		icons.put("clear-night", clearNight);
		icons.put("clear-day", clearDay);
		icons.put("wind", wind);
	}
	
	public HashMap<String,File> getIcons(){
		return icons;
	}
}

//.\Icons\Cloud-Fog.svg
//.\Icons\Cloud-Hail.svg
//.\Icons\Cloud-Moon.svg
//.\Icons\Cloud-Rain.svg
//.\Icons\Cloud-Snow-Alt.svg
//.\Icons\Cloud-Sun.svg
//.\Icons\Cloud.svg
//.\Icons\desktop.ini
//.\Icons\Moon.svg
//.\Icons\Sun.svg
//.\Icons\Wind.svg
