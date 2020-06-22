package com.voidStudios.photoDisplay;

import java.io.File;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class MainController {

	public static final int NUM_DAILY_WEATHER=3;
	private static final String DEGREE_SYMBOL="\u00b0";
	@FXML
	private ImageView imageViewer;
	@FXML
	private Label dateLabel;
	@FXML
	private HBox dailyWeatherHBox;
	@FXML
	private Rectangle dailyBackgroundRectangle;

	public void initialize() {
//		imageViewer.setImage(new Image(new File("photos/space crop.png").toURI().toString()));
//		alignImage(imageViewer, false);
	}

	public void setDateLabel(String s) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				dateLabel.setText(s);
			}
		});
	}
	
	public void setWeather(WeatherContainer wc) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				for(int i=0; i<wc.getNumDays(); i++) {
					setDayWeatherHelper(i, wc);
				}
			}
		});
		
		//This isn't required to be wrapped in a runLater. Odd.
		setDailyBackgroundRect();
	}
	
	public void setImage(File f) {
		imageViewer.setImage(new Image(f.toURI().toString()));
		
		alignImage(imageViewer, false);
	}

	private void alignImage(ImageView imageView, boolean center) {
		Image img=imageView.getImage();
		if(img!=null) {
			double w=0;
			double h=0;

			double ratioX=imageView.getFitWidth()/img.getWidth();
			double ratioY=imageView.getFitHeight()/img.getHeight();

			double reducCoeff=0;
			if(ratioX>=ratioY) {
				reducCoeff=ratioY;
			}else {
				reducCoeff=ratioX;
			}

			w=img.getWidth()*reducCoeff;
			h=img.getHeight()*reducCoeff;

//			System.out.println(w+" "+h);

			if(center)
				imageView.setX((imageView.getFitWidth()-w)/2);
			else
				imageView.setX((imageView.getFitWidth()-w));

			imageView.setY((imageView.getFitHeight()-h)/2);

		}
	}
	
	private void setDayWeatherHelper(int i, WeatherContainer wc) {
		HBox day=(HBox) dailyWeatherHBox.getChildrenUnmodifiable().get(i);
		VBox vbox1=(VBox) day.getChildrenUnmodifiable().get(0);
		VBox vbox2=(VBox) day.getChildrenUnmodifiable().get(1);
		ImageView iv=(ImageView) vbox1.getChildrenUnmodifiable().get(0);
		Label lDay=(Label) vbox1.getChildrenUnmodifiable().get(1);
		Label lHigh=(Label) vbox2.getChildrenUnmodifiable().get(0);
		Label lLow=(Label) vbox2.getChildrenUnmodifiable().get(1);

		iv.setImage(new Image(wc.getDayIcon(i).toURI().toString()));
		lDay.setText(wc.getDayName(i));
		lHigh.setText(wc.getDayHigh(i)+DEGREE_SYMBOL);
		lLow.setText(wc.getDayLow(i)+DEGREE_SYMBOL);
	}
	
	private void setDailyBackgroundRect() {
		double width=dailyWeatherHBox.getWidth();
		dailyBackgroundRectangle.setWidth(width);
		double height=dailyWeatherHBox.getHeight();
		dailyBackgroundRectangle.setHeight(height);
	}

}
