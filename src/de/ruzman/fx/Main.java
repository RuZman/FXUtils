package de.ruzman.fx;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class Main extends Application {	
	
	@Override
	public void start(Stage primaryStage) {
		try {			
			FXMLLoader loader = new FXMLLoader();
			loader.load(getClass().getResource("Frame.fxml").openStream());
		    ((FrameController) loader.getController()).setPrimaryStage(primaryStage);
			Scene scene = new Scene(loader.getRoot());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        scene.setFill(null);
	        
	        primaryStage.setScene(scene);
	        primaryStage.show();
	        
		} catch(Exception e) { 
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
