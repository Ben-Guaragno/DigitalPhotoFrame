package com.voidStudios.photoDisplay;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.Scanner;
import com.voidStudios.photoDisplay.threads.DateThread;
import com.voidStudios.photoDisplay.threads.PhotoThread;
import com.voidStudios.photoDisplay.threads.WeatherThread;

public class Main{

	public static void main(String[] args) {
		//Read outputs from config file (specified in command line)
		//All inputs assumed to be minutes
			//well... not right now...
		int[] times=readConfig(args);
		
//		for(int x:times) {
//			System.out.print(x+" ");
//		}
//		System.out.println();
		
		
		IconLoader iconLoader=new IconLoader();
		ImageDirectory imageDirectory=new ImageDirectory("./photos");
		DataController data=new DataController(imageDirectory,iconLoader);
		View view=new View(data);
		data.updateView(view);

		PhotoThread photoThread=new PhotoThread(times[0], data);
		DateThread dateThread=new DateThread(minToMilli(times[1]),data);
		WeatherThread weatherThread=new WeatherThread(minToMilli(times[2]), data);
		photoThread.start();
		dateThread.start();
		weatherThread.start();
		
		@SuppressWarnings("unused")
		Controller c=new Controller(view);
	}

	private static int minToMilli(int min) {
		return min*60*1000;
	}
	
	private static int[] readConfig(String[] args) {
		int[] times= {5000,60,10};
		
		if(args.length<1)
			return times;
		Scanner sc;
		try {
			sc=new Scanner(new File(args[0]));
		} catch (FileNotFoundException e) {
			System.out.println("Config not found");
			return times;
		}
		
		
		while(sc.hasNextLine()) {
			String s=sc.nextLine();
			int x=s.indexOf('=');
			if(x==-1) {
				System.out.println("Invalid config line: "+s);
				break;
			}
			String tag=s.substring(0, x);
			String valS=s.substring(x+1);
			int val=Integer.parseInt(valS);
			if(tag.equals("photoUpdate")) {
				times[0]=val;
			}else if(tag.equals("dateUpdate")) {
				times[1]=val;
			}else if(tag.equals("weatherUpdate")) {
				times[2]=val;
			}else {
				System.out.println("Unrecognized tag: "+tag);
			}
		}
		
		sc.close();
		return times;
	}
}
