package de.ruzman.fx.window;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageDecorator {
	public static final String AERO_STYLE_PATH = "bin/de/ruzman/fx/window/skin/aero/";
	public static final String GITHUB_STYLE_PATH = "bin/de/ruzman/fx/window/skin/github/";
	
	public FrameController decorate(Stage stage) {
		return decorate(stage, AERO_STYLE_PATH);
	}
	
	public FrameController decorate(Stage stage, String fxmlPath) {
		try {
			stage.initStyle(StageStyle.TRANSPARENT);
			
			FXMLLoader loader = new FXMLLoader();
			loader.load(new File(fxmlPath + "Frame.fxml").toURL().openStream());
			Scene scene = new Scene(loader.getRoot());
			scene.getStylesheets().add(new File(fxmlPath + "application.css").toURL().toExternalForm());
	        scene.setFill(null); 
	        
	        FrameController controller = loader.getController();

			controller.getTopBar().maxWidthProperty().bind(stage.widthProperty());
	        controller.xProperty().setValue(stage.getX());
	        controller.yProperty().setValue(stage.getY());
	        controller.widthProperty().setValue(stage.getWidth());
	        controller.heightProperty().setValue(stage.getHeight());
	        controller.resizableProperty().setValue(stage.isResizable());
	        controller.iconifiedProperty().setValue(stage.isIconified());
			controller.titleProperty().setValue(stage.getTitle());

			// FIXME: Replace ....
			stage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					controller.maximized.setValue(newValue);
				}
			});
			controller.maximized.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					stage.setMaximized(newValue);
				}
			});
			
			stage.iconifiedProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					// FIXME: Klick in der Windowsliste, hat keine Auswirkung?!
					controller.iconified.setValue(newValue);
				}
			});
			controller.iconified.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					stage.setIconified(newValue);
				}
			});
			
			stage.resizableProperty().addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					controller.resizable.setValue(newValue);
				}
			});
			controller.resizable.addListener(new ChangeListener<Boolean>() {
				@Override
				public void changed(
						ObservableValue<? extends Boolean> observable,
						Boolean oldValue, Boolean newValue) {
					stage.setResizable(newValue);
				}
			});
			
			stage.xProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					controller.x.setValue(newValue);
				}
				
			});
			controller.x.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					stage.setX(newValue.doubleValue());
				}
				
			});
			stage.yProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					controller.y.setValue(newValue);
				}
				
			});
			controller.y.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					stage.setY(newValue.doubleValue());
				}
				
			});
			stage.widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					controller.width.setValue(newValue);
				}
				
			});
			controller.width.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					stage.setWidth(newValue.doubleValue());
					controller.mapFrameToStage();
				}
			});
			stage.heightProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					controller.height.setValue(newValue);
				}
				
			});
			controller.height.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(
						ObservableValue<? extends Number> observable,
						Number oldValue, Number newValue) {
					stage.setHeight(newValue.doubleValue());
					controller.mapFrameToStage();
				}
			});
			stage.titleProperty().addListener(new ChangeListener<String>() {
				@Override
				public void changed(
						ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					controller.title.setValue(newValue);
				}
				
			});
			controller.title.addListener(new ChangeListener<String>() {
				@Override
				public void changed(
						ObservableValue<? extends String> observable,
						String oldValue, String newValue) {
					stage.setTitle(newValue);
				}
			});
						
	        // FIXME: Scene == ContentPane
	        stage.setScene(scene);


			return controller;
		} catch(Exception e) { 
			e.printStackTrace();
		}

		return null;
	}
}
