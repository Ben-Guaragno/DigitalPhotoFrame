package com.voidStudios.photoDisplay;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root=(BorderPane) FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene=new Scene(root, 1920, 1080);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			boolean debug=true;
			if(!debug) {
				ObservableList<Screen> screens=Screen.getScreens();
				Rectangle2D bounds=screens.get(1).getVisualBounds();
				primaryStage.setX(bounds.getMinX());
				primaryStage.setY(bounds.getMinY());
				primaryStage.setFullScreen(true);
			}
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(@SuppressWarnings("unused") WindowEvent t) {
			        Platform.exit();
			        System.exit(0);
			    }
			});
			
			primaryStage.show();
			
			Controller c=new Controller();
			c.start();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
