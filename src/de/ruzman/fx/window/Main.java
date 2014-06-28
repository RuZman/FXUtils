package de.ruzman.fx.window;
	
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {	
	
	@Override
	public void start(Stage primaryStage) {
		try {
			new StageDecorator(primaryStage);
			primaryStage.setX(5);
			primaryStage.setY(100);
			primaryStage.setMinHeight(80);
			primaryStage.setMinWidth(80);
			primaryStage.setWidth(700);
			primaryStage.setHeight(200);
			primaryStage.setMaxHeight(800);
			primaryStage.setMaxWidth(800);
			primaryStage.setResizable(true);
			primaryStage.setMaximized(false);
			primaryStage.setIconified(false);
			primaryStage.setTitle("Neues_Textdokument_(2)");
	        primaryStage.show();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
