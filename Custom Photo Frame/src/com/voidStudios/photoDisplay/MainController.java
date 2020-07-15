package com.voidStudios.photoDisplay;

import java.io.File;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class MainController {

	public static final int NUM_DAILY_WEATHER=3;
	public static final int NUM_HOURLY_WEATHER=6;
	private static final String DEGREE_SYMBOL="\u00b0";
	@FXML
	private ImageView imageViewer;
	@FXML
	private Label dateLabel;
	@FXML
	private HBox dailyWeatherHBox;
	@FXML
	private Rectangle dailyBackgroundRectangle;
	@FXML
	private Rectangle hourlyBackgroundRectangle;
	@FXML
	private Rectangle summaryBackgroundRectangle;
	@FXML
	private Rectangle dateBackgroundRectangle;
	@FXML
	private GridPane hourlyGrid;
	@FXML
	private Label summaryLabel;
	@FXML
	private Rectangle energyBackgroundRectangle;
	@FXML
	private HBox energyHBox;

	public void initialize() {
	}

	public void setDateLabel(String s) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				dateLabel.setText(s);
				setDateBackgroundRect();
			}
		});
	}

	public void setWeather(WeatherContainer wc) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				for(int i=0; i<wc.getNumDays(); i++) {
					setDayWeather(i, wc);
				}
				for(int i=0; i<wc.getNumHours(); i++) {
					setHourWeather(i, wc);
				}
				setWeatherSummary(wc);

				setDailyBackgroundRect();
				setHourlyBackgroundRect();
				setSummaryBackgroundRect();
			}
		});
	}

	public void setImage(File f, boolean center) {
		imageViewer.setImage(new Image(f.toURI().toString()));

		alignImage(imageViewer, center);
	}

	public void setEnergy(String s) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				ImageView energyIcon=(ImageView) energyHBox.getChildrenUnmodifiable().get(0);
				Label energyLabel=(Label) energyHBox.getChildrenUnmodifiable().get(1);
				if(s!=null) {
					energyLabel.setText(s);
					energyIcon.setVisible(true);
					energyBackgroundRectangle.setVisible(true);
					setEnergyBackgroundRect();
				}else {
					energyIcon.setVisible(false);
					energyLabel.setVisible(false);
					energyBackgroundRectangle.setVisible(false);
				}
			}
		});
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

	private void setDayWeather(int i, WeatherContainer wc) {
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

	private void setHourWeather(int i, WeatherContainer wc) {
		ObservableList<Node> hourlyList=hourlyGrid.getChildrenUnmodifiable();
		Label lTime=(Label) hourlyList.get(i*3);
		ImageView iv=(ImageView) hourlyList.get(i*3+1);
		Label lTempSumm=(Label) hourlyList.get(i*3+2);

		lTime.setText(wc.getHourTime(i));
		iv.setImage(new Image(wc.getHourIcon(i).toURI().toString()));
		lTempSumm.setText(wc.getHourTemp(i)+DEGREE_SYMBOL+" "+wc.getHourSumm(i));
	}

	private void setWeatherSummary(WeatherContainer wc) {
		String summ=wc.getSummary();
		summaryLabel.setText(summ);
	}

	private void setDailyBackgroundRect() {
		double height=dailyWeatherHBox.getHeight();
		double width=dailyWeatherHBox.prefWidth(height);
		dailyBackgroundRectangle.setWidth(width);
	}

	private void setHourlyBackgroundRect() {
		double height=hourlyGrid.getHeight();
		double width=hourlyGrid.prefWidth(height);
		hourlyBackgroundRectangle.setWidth(width);
	}

	private void setSummaryBackgroundRect() {
		double height=summaryLabel.getHeight();
		double width=summaryLabel.prefWidth(height);
		summaryBackgroundRectangle.setWidth(width+10);
	}

	private void setDateBackgroundRect() {
		double height=dateLabel.getHeight();
		double width=dateLabel.prefWidth(height);
		height=dateLabel.prefHeight(width);
		dateBackgroundRectangle.setWidth(width+4);
		dateBackgroundRectangle.setHeight(height+4);
	}

	private void setEnergyBackgroundRect() {
		Label energyLabel=(Label) energyHBox.getChildrenUnmodifiable().get(1);
		double height=energyLabel.getHeight();
		double imageWidth=((ImageView) energyHBox.getChildrenUnmodifiable().get(0)).getFitWidth();
		double width=energyLabel.prefWidth(height);
		energyBackgroundRectangle.setWidth(width+imageWidth+5);
	}

}
