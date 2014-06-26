package de.ruzman.fx;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageDecorator {
	public void decorate(Stage stage) {
		try {
			stage.initStyle(StageStyle.TRANSPARENT);
			
			FXMLLoader loader = new FXMLLoader();
			loader.load(getClass().getResource("Frame.fxml").openStream());
			Scene scene = new Scene(loader.getRoot());
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
	        scene.setFill(null); 

	       FrameController controller = loader.getController();
	        
			
			if (!stage.isResizable()) {
				// FIXME: frameControl.getChildren().remove(maximize);
			}

			controller.x.setValue(stage.getX());
			controller.y.setValue(stage.getY());
			controller.width.setValue(stage.getWidth());
			controller.height.setValue(stage.getHeight());
			controller.resizable.setValue(stage.isResizable());
			controller.iconified.setValue(stage.isIconified());
			
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
				}
			});

	        // FIXME: Scene == ContentPane
	        stage.setScene(scene);

	        
		} catch(Exception e) { 
			e.printStackTrace();
		}
	}
}
