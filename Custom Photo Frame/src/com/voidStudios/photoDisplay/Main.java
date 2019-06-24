package com.voidStudios.photoDisplay;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import com.voidStudios.photoDisplay.threads.DateThread;
import com.voidStudios.photoDisplay.threads.PhotoThread;
import com.voidStudios.photoDisplay.threads.WeatherThread;

public class Main{

	public static void main(String[] args) {
		//Read outputs from config file (specified in command line)
		//All inputs assumed to be minutes
			//well... not right now...
		HashMap<String,String> config=readConfig(args);
		
		IconLoader iconLoader=new IconLoader();
		ImageDirectory imageDirectory=new ImageDirectory("./photos");
		DataController data=new DataController(imageDirectory,iconLoader);
		View view=new View(data);
		data.updateView(view);

		PhotoThread photoThread=new PhotoThread(Integer.parseInt(config.get("photoUpdate")), data);
		DateThread dateThread=new DateThread(minToMilli(Integer.parseInt(config.get("dateUpdate"))),data);
		WeatherThread weatherThread=new WeatherThread(minToMilli(Integer.parseInt(config.get("weatherUpdate"))), data, config.get("apiKey"), config.get("lat"), config.get("lon"));
		photoThread.start();
		dateThread.start();
		weatherThread.start();
		
		@SuppressWarnings("unused")
		Controller c=new Controller(view);
	}

	private static int minToMilli(int min) {
		return min*60*1000;
	}
	
	private static HashMap<String,String> readConfig(String[] args) {
		HashMap<String,String> config=new HashMap<String,String>();
		config.put("photoUpdate", "5000");
		config.put("dateUpdate", "60");
		config.put("weatherUpdate", "10");
		config.put("apiKey", null);
		config.put("lat", null);
		config.put("lon", null);
		
		if(args.length<1)
			return config;
		Scanner sc;
		try {
			sc=new Scanner(new File(args[0]));
		} catch (FileNotFoundException e) {
			System.out.println("Error: Config not found");
			return config;
		}
		
		
		while(sc.hasNextLine()) {
			String s=sc.nextLine();
			int x=s.indexOf('=');
			if(x==-1) {
				System.out.println("Error: Invalid config line: "+s);
				break;
			}
			String tag=s.substring(0, x);
			String valS=s.substring(x+1);

			if(config.containsKey(tag)){
				config.replace(tag, valS);
			}else{
				System.out.println("Error: Unrecognized tag: "+tag);
			}
		}
		
		sc.close();
		return config;
	}
}
