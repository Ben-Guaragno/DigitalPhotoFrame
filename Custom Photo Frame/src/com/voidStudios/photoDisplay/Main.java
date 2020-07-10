package com.voidStudios.photoDisplay;

import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;

public class Main extends Application {

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader=new FXMLLoader(getClass().getResource("Main.fxml"));
			BorderPane root=(BorderPane) loader.load();
			MainController mainController=loader.getController();
			Scene scene=new Scene(root, 1920, 1080);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			scene.setCursor(Cursor.NONE);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			ObservableList<Screen> screens=Screen.getScreens();
			Rectangle2D bounds;
			if(screens.size()==1) {
				bounds=screens.get(0).getVisualBounds();
			}else {
				bounds=screens.get(1).getVisualBounds();
			}
			primaryStage.setX(bounds.getMinX());
			primaryStage.setY(bounds.getMinY());
			primaryStage.setFullScreen(true);
			
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			    @Override
			    public void handle(@SuppressWarnings("unused") WindowEvent t) {
			        Platform.exit();
			        System.exit(0);
			    }
			});
			
			primaryStage.show();
			
			List<String> args=getParameters().getRaw();
			String configFile;
			if(args.size()==0)
				configFile=null;
			else
				configFile=args.get(0);
			
			SettingsLoader sl=new SettingsLoader(configFile);
			
			@SuppressWarnings("unused")
			Controller c=new Controller(mainController, sl);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		System.out.println(new Date()+": Initializing JFX Photo Frame.");
		launch(args);
	}
}
