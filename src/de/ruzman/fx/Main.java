package de.ruzman.fx;
	
import javafx.application.Application;
import javafx.stage.Stage;


public class Main extends Application {	
	
	@Override
	public void start(Stage primaryStage) {
		new StageDecorator().decorate(primaryStage);
        primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
