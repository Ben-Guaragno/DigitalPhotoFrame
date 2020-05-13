package com.voidStudios.photoDisplay;

import java.io.File;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainController {

	@FXML
	private ImageView imageViewer;
	
	public void initialize() {
		imageViewer.setImage(new Image(new File("photos/space.jpg").toURI().toString()));
	}
}
