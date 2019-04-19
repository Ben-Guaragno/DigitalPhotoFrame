package com.voidStudios.photoDisplay.threads;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.voidStudios.photoDisplay.DataController;

public class DateThread extends Thread {
	private long time;
	private long desireRun;
	private DataController data;
	SimpleDateFormat dateFormat;
	SimpleDateFormat dayFormat;
	
	public DateThread(long desireRun, DataController data) {
		super("DateThread");
		this.desireRun=desireRun;
		time=System.currentTimeMillis();
		this.data=data;
		dateFormat=new SimpleDateFormat("MMMM d yyyy");
		dayFormat=new SimpleDateFormat("u");
	}
	
	public void run() {
		boolean firstRun=true;
		while(true) {
			long runtime=System.currentTimeMillis()-time;
			if(firstRun || runtime>desireRun) {
				firstRun=false;
				
				Date now = new Date();
		        String dateS = dateFormat.format(now);
		        String dayS = dayFormat.format(now);
				data.setDate(dateS,dayS);
				
				time=System.currentTimeMillis();
			}
			else
				try {
					sleep(desireRun-runtime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
		}
	}
}
