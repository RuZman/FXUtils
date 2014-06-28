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
	
	public StageDecorator(Stage stage) throws Exception {
		this(stage, GITHUB_STYLE_PATH);
	}
	
	public StageDecorator(Stage stage, String fxmlPath) throws Exception {
		// FIXME: Use selected Style
		stage.initStyle(StageStyle.TRANSPARENT);
				
		FXMLLoader loader = new FXMLLoader();
		loader.load(new File(fxmlPath + "Frame.fxml").toURL().openStream());
		FrameController controller = loader.getController();
		
		init(stage, controller.getStageBean());
        bindBiderectional(stage, controller.getStageBean());
        controller.getTopBar().maxWidthProperty().bind(stage.widthProperty());
        controller.getStageBean().heightProperty().addListener((a,b,c) -> controller.mapFrameToStage());

		Scene scene = new Scene(loader.getRoot());
		scene.getStylesheets().add(new File(fxmlPath + "application.css").toURL().toExternalForm());
        scene.setFill(null);
        // FIXME: Scene == ContentPane
        stage.setScene(scene);
	}

	private void init(Stage stage, StageBean stageBean) throws Exception {
		stageBean.xProperty().setValue(stage.getX());
        stageBean.yProperty().setValue(stage.getY());
        
        stageBean.widthProperty().setValue(stage.getWidth());
        stageBean.heightProperty().setValue(stage.getHeight());
        
        stageBean.resizableProperty().setValue(stage.isResizable());
        stageBean.iconifiedProperty().setValue(stage.isIconified());
        stageBean.titleProperty().setValue(stage.getTitle());
	}
	
	// FIXME: Replace ....
	private void bindBiderectional(Stage stage, StageBean stageBean) {
		stage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				stageBean.maximizedProperty().setValue(newValue);
			}
		});
		stageBean.maximizedProperty().addListener(new ChangeListener<Boolean>() {
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
				stageBean.iconifiedProperty().setValue(newValue);
			}
		});
		stageBean.iconifiedProperty().addListener(new ChangeListener<Boolean>() {
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
				stageBean.resizableProperty().setValue(newValue);
			}
		});
		stageBean.resizableProperty().addListener(new ChangeListener<Boolean>() {
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
				stageBean.xProperty().setValue(newValue);
			}
			
		});
		stageBean.xProperty().addListener(new ChangeListener<Number>() {
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
				stageBean.yProperty().setValue(newValue);
			}
			
		});
		stageBean.yProperty().addListener(new ChangeListener<Number>() {
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
				stageBean.widthProperty().setValue(newValue);
			}
			
		});
		stageBean.widthProperty().addListener(new ChangeListener<Number>() {
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
				stageBean.heightProperty().setValue(newValue);
			}
			
		});
		stageBean.heightProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(
					ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				stage.setHeight(newValue.doubleValue());
			}
		});
		stage.titleProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				stageBean.titleProperty().setValue(newValue);
			}
			
		});
		stageBean.titleProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(
					ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				stage.setTitle(newValue);
			}
		});
	}
}
