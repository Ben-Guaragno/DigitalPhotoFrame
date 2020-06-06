package com.voidStudios.photoDisplay;

import java.io.File;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainController {

	@FXML
	private ImageView imageViewer;

	@FXML
	private Label dateLabel;

	public void initialize() {
		imageViewer.setImage(new Image(new File("photos/space crop.png").toURI().toString()));
		alignImage(imageViewer, false);
	}

	public void setDateLabel(String s) {
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				dateLabel.setText(s);
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

}
