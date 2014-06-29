package de.ruzman.fx.window;

import java.io.File;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.WritableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StageDecorator {
	public static final String AERO_STYLE_PATH = "bin/de/ruzman/fx/window/skin/aero/";
	public static final String GITHUB_STYLE_PATH = "bin/de/ruzman/fx/window/skin/github/";

	public StageDecorator(Stage stage) throws Exception {
		this(stage, AERO_STYLE_PATH);
	}

	public StageDecorator(Stage stage, String fxmlPath) throws Exception {		
		FXMLLoader loader = new FXMLLoader();
		loader.load(new File(fxmlPath + "Frame.fxml").toURL().openStream());
		FrameController controller = loader.getController();

		try {
			controller.getStageBean().setStageStyle(stage.getStyle());
			if(!stage.showingProperty().get()) {
				stage.initStyle(StageStyle.TRANSPARENT);
				stage.show();
				stage.hide();
			}
		} catch(Exception ex) {
			// Coudn't lock TRANSPARENT StageStyle.
		}
		
		// FIXME: Layout an StageStyle anpassen.
		// FIXME: Layout bei StageStyle-Änderungen anpssen.
		
		init(stage, controller.getStageBean());
		bindBiderectional(stage, controller.getStageBean());
		controller.getTopBar().maxWidthProperty().bind(stage.widthProperty());

		Scene scene = new Scene(loader.getRoot());
		scene.getStylesheets()
				.add(new File(fxmlPath + "application.css").toURL()
						.toExternalForm());
		scene.setFill(null);
		// FIXME: Scene == ContentPane
		stage.setScene(scene);
	}

	private void init(Stage stage, StageBean stageBean) throws Exception {
		stageBean.xProperty().setValue(stage.getX());
		stageBean.yProperty().setValue(stage.getY());

		stageBean.widthProperty().setValue(stage.getWidth());
		stageBean.heightProperty().setValue(stage.getHeight());
		stageBean.minWidthProperty().setValue(stage.getMinWidth());
		stageBean.minHeightProperty().setValue(stage.getMinHeight());
		stageBean.maxWidthProperty().setValue(stage.getMaxWidth());
		stageBean.maxHeightProperty().setValue(stage.getMaxHeight());

		stageBean.maximizedProperty().setValue(stage.isMaximized());
		stageBean.resizableProperty().setValue(stage.isResizable());
		stageBean.iconifiedProperty().setValue(stage.isIconified());
		stageBean.titleProperty().setValue(stage.getTitle());
	}

	private void bindBiderectional(Stage stage, StageBean stageBean) {
		// stage -> stageBean
		bind(stage.xProperty(), stageBean.xProperty());
		bind(stage.yProperty(), stageBean.yProperty());
		
		// Unfixable: Schatten reduziert die Größe der Stage.
		bind(stage.widthProperty(), stageBean.widthProperty());
		bind(stage.heightProperty(), stageBean.heightProperty());
		// FIXME: minGröße könnte problemeatisch werden, wenn im Layout-Code was größeres festgelegt ist ..
		bind(stage.minWidthProperty(), stageBean.minWidthProperty());
		bind(stage.minHeightProperty(), stageBean.minHeightProperty());
		bind(stage.maxWidthProperty(), stageBean.maxWidthProperty());
		bind(stage.maxHeightProperty(), stageBean.maxHeightProperty());
		
		bind(stage.maximizedProperty(), stageBean.maximizedProperty());
		bind(stage.iconifiedProperty(), stageBean.iconifiedProperty());
		bind(stage.resizableProperty(), stageBean.resizableProperty());
		
		bind(stage.titleProperty(), stageBean.titleProperty());

		// stageBean -> stage
		stageBean.xProperty().addListener(
				(a, b, newValue) -> stage.setX(newValue.doubleValue()));
		stageBean.yProperty().addListener(
				(a, b, newValue) -> stage.setY(newValue.doubleValue()));

		stageBean.widthProperty().addListener(
				(a, b, newValue) -> stage.setWidth(newValue.doubleValue()));
		stageBean.heightProperty().addListener(
				(a, b, newValue) -> stage.setHeight(newValue.doubleValue()));

		stageBean.minWidthProperty().addListener(
				(a, b, newValue) -> stage.setMinWidth(newValue.doubleValue()));
		stageBean.minHeightProperty().addListener(
				(a, b, newValue) -> stage.setMinHeight(newValue.doubleValue()));
		stageBean.maxWidthProperty().addListener(
				(a, b, newValue) -> stage.setMaxWidth(newValue.doubleValue()));
		stageBean.maxHeightProperty().addListener(
				(a, b, newValue) -> stage.setMaxHeight(newValue.doubleValue()));
		
		stageBean.maximizedProperty().addListener(
				(a, b, newValue) -> stage.setMaximized(newValue));
		stageBean.iconifiedProperty().addListener(
				(a, b, newValue) -> stage.setIconified(newValue));
		stageBean.resizableProperty().addListener(
				(a, b, newValue) -> stage.setResizable(newValue));
		
		stageBean.titleProperty().addListener(
				(a, b, newValue) -> stage.setTitle(newValue));
	}

	private void bind(ReadOnlyProperty stageProperty,
			WritableValue stageBeanProperty) {
		stageProperty.addListener((a, b, newValue) -> stageBeanProperty
				.setValue(newValue));
	}
}
