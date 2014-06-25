package de.ruzman.fx;

import java.net.URL;
import java.util.EnumSet;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class FrameController implements Initializable {
	public enum ResizeDirection {
		NORTH, EAST, SOUTH, WEST
	}

	public enum State {
		NONE, DRAG, RESIZE
	}

	private EnumSet<ResizeDirection> direction = EnumSet
			.noneOf(ResizeDirection.class);
	private State state = State.NONE;
	protected Rectangle2D oldBounds;
	protected Point2D draggedPosition;
	protected Stage primaryStage;
	protected boolean isDocked;

	// FIXME: Konstanten Global nutzen (CSS + FXML)
	public static final int SHADOW_SIZE = 15;
	public static final int HALF_SHADOW_SIZE = SHADOW_SIZE / 2;

	@FXML private Group root;
	@FXML private StackPane topBar;
	@FXML private BorderPane frame;
	@FXML private HBox frameControl;
	@FXML private Button maximize;
	@FXML private Button close;
	@FXML private Rectangle scalePane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		root.setTranslateX(HALF_SHADOW_SIZE);
		root.setTranslateY(HALF_SHADOW_SIZE);

		topBar.prefWidthProperty().bind(frame.widthProperty());
		scalePane.widthProperty().bind(frame.widthProperty());
		scalePane.heightProperty().bind(frame.heightProperty());
	}

	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		if (!primaryStage.isResizable()) {
			frameControl.getChildren().remove(maximize);
		}
		addResizableChangeListener();
		addMaximizedChangeListener();
	}

	private void addResizableChangeListener() {
		primaryStage.resizableProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean oldValue, Boolean newValue) {
						// isResizable: false -> true
						if (!oldValue.booleanValue() && newValue.booleanValue()) {
							frameControl.getChildren().add(
									frameControl.getChildren().indexOf(close),
									maximize);
							// isResizable: true -> false
						} else if (oldValue.booleanValue()
								&& !newValue.booleanValue()) {
							frameControl.getChildren().remove(maximize);
						}
					}
				});
	}

	private void addMaximizedChangeListener() {
		primaryStage.maximizedProperty().addListener(
				new ChangeListener<Boolean>() {
					@Override
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean oldValue, Boolean newValue) {
						// isMaximized: false -> true
						if (!oldValue.booleanValue() && newValue.booleanValue()) {
							saveOldBounds();
							scale(getVisualBounds());

							root.setTranslateX(0);
							root.setTranslateY(0);
							root.getChildren().remove(scalePane);

							maximize.getStyleClass().remove("maximize");
							maximize.getStyleClass().add("minimize");

							isDocked = false;
							// isMaximized: true -> false
						} else if (oldValue.booleanValue()
								&& !newValue.booleanValue()) {

							root.setTranslateX(HALF_SHADOW_SIZE);
							root.setTranslateY(HALF_SHADOW_SIZE);
							root.getChildren().add(scalePane);

							scale(oldBounds);

							maximize.getStyleClass().add("maximize");
							maximize.getStyleClass().remove("minimize");
						}
					}
				});
	}

	public synchronized void activateWindowDragged(double x, double y) {
		if (state == State.NONE) {
			state = State.DRAG;
			draggedPosition = new Point2D(x, y);
		}
	}

	public void onWindowDragged(double x, double y) {
		if (state == State.DRAG && !primaryStage.isMaximized()) {
			// FIXME: Logik ist falsch ... funktioniert bei verschiedenen
			// Dockarten wahrscheinlich nicht.
			if (isDocked) {
				if (Math.abs(draggedPosition.getY()) + Math.abs(y) > getVisualBounds()
						.getHeight() / 15) {
					root.setTranslateY(HALF_SHADOW_SIZE);
					scale(oldBounds);
					isDocked = false;
				}
			} else {
				root.getScene().getWindow().setY(y - draggedPosition.getY());
			}
			root.getScene().getWindow().setX(x - draggedPosition.getX());
		}
	}

	public void deactivateWindowDragged() {
		if (state == State.DRAG) {
			state = State.NONE;
		}
	}

	public void closeWindow() {
		primaryStage.close();
	}

	public void iconifyWindow() {
		primaryStage.setIconified(true);
	}

	public synchronized void activateWindowResize() {
		if (state == State.NONE) {
			state = State.RESIZE;
			if (isDocked
					&& (direction.contains(ResizeDirection.NORTH) || direction
							.contains(ResizeDirection.SOUTH))) {
				// Do nothing
			} else {
				saveOldBounds();
			}
		}
	}

	private void onVerticalWindowResize(EnumSet<ResizeDirection> direction) {
		if (primaryStage.isResizable()
				&& (direction.contains(ResizeDirection.SOUTH) || direction
						.contains(ResizeDirection.NORTH))) {
			if (isDocked) {
				isDocked = false;
				root.setTranslateY(HALF_SHADOW_SIZE);
				scale(oldBounds);
			} else {
				isDocked = true;
				saveOldBounds();
				scale(new Rectangle2D(primaryStage.getX(), 0,
						primaryStage.getWidth(), getVisualBounds().getHeight()));
				root.setTranslateY(0);
			}
		}
	}

	public void onWindowResize(EnumSet<ResizeDirection> direction,
			double screenX, double screenY) {
		// FIXME: Minimum beim Resize beachten
		if (primaryStage.isResizable() && state == State.RESIZE) {
			double x = primaryStage.getX();
			double y = primaryStage.getY();
			double w = primaryStage.getWidth();
			double h = primaryStage.getHeight();

			if (isDocked
					&& (direction.contains(ResizeDirection.NORTH) || direction
							.contains(ResizeDirection.SOUTH))) {
				root.setTranslateY(HALF_SHADOW_SIZE);

				x = oldBounds.getMinX();
				y = oldBounds.getMinY();
				w = oldBounds.getWidth();
				h = oldBounds.getHeight();
				isDocked = false;
			}

			if (direction.contains(ResizeDirection.WEST)) {
				x = screenX - HALF_SHADOW_SIZE;
				w = oldBounds.getWidth() + HALF_SHADOW_SIZE
						+ oldBounds.getMinX() - screenX;
			} else if (direction.contains(ResizeDirection.EAST)) {
				w = screenX - primaryStage.getX() + SHADOW_SIZE;
			}

			if (direction.contains(ResizeDirection.NORTH)) {
				y = screenY - HALF_SHADOW_SIZE;
				h = oldBounds.getHeight() + HALF_SHADOW_SIZE
						+ oldBounds.getMinY() - screenY;
			} else if (direction.contains(ResizeDirection.SOUTH)) {
				h = screenY - primaryStage.getY() + SHADOW_SIZE;
			}

			scale(new Rectangle2D(x, y, w, h));
		}
	}

	public void deactivateWindowResize() {
		if (state == State.RESIZE) {
			state = State.NONE;
		}
	}

	public Rectangle2D getVisualBounds() {
		ObservableList<Screen> screensForRectangle = Screen
				.getScreensForRectangle(primaryStage.getX(),
						primaryStage.getY(), primaryStage.getWidth(),
						primaryStage.getHeight());

		return screensForRectangle.get(0).getVisualBounds();
	}

	protected void saveOldBounds() {
		if (!isDocked) {
			oldBounds = new Rectangle2D(primaryStage.getX(),
					primaryStage.getY(), primaryStage.getWidth(),
					primaryStage.getHeight());
		} else {
			// FIXME: Resize im Docking-Mode muss oldBounds überschreiben.
		}
	}

	protected void scale(Rectangle2D bounds) {
		primaryStage.setHeight(bounds.getHeight());
		primaryStage.setWidth(bounds.getWidth());
		primaryStage.setX(bounds.getMinX());
		primaryStage.setY(bounds.getMinY());

		mapFrameToStage();
	}

	protected void mapFrameToStage() {
		if (primaryStage.isMaximized()) {
			frame.setPrefSize(primaryStage.getWidth(), primaryStage.getHeight());
		} else if (isDocked) {
			frame.setPrefSize(primaryStage.getWidth() - SHADOW_SIZE,
					getVisualBounds().getHeight());
		} else {
			frame.setPrefSize(primaryStage.getWidth() - HALF_SHADOW_SIZE * 3,
					primaryStage.getHeight() - HALF_SHADOW_SIZE * 3);
		}
	}

	@FXML
	private void iconifyWindow(ActionEvent event) {
		iconifyWindow();
	}

	@FXML
	private void maximizeWindow(ActionEvent event) {
		primaryStage.setMaximized(!primaryStage.isMaximized());
	}

	@FXML
	private void maximizeWindowOnDoubleCLick(MouseEvent me) {
		if (isPrimaryMouseButton(me) && me.getClickCount() == 2) {
			// FIXME: Logik in public-Methode verlagern
			if (isDocked) {
				root.setTranslateY(HALF_SHADOW_SIZE);
				scale(oldBounds);
				isDocked = false;
			} else {
				primaryStage.setMaximized(!primaryStage.isMaximized());
			}
		}
	}

	@FXML
	private void closeWindow(ActionEvent event) {
		closeWindow();
	}

	@FXML
	private void activateWindowDragged(MouseEvent me) {
		if (isPrimaryMouseButton(me)) {
			activateWindowDragged(me.getSceneX(), me.getSceneY());
		}
	}

	@FXML
	private void onWindowDragged(MouseEvent me) {
		if (isPrimaryMouseButton(me)) {
			onWindowDragged(me.getScreenX(), me.getScreenY());
		}
	}

	@FXML
	private void deactivateWindowDragged(MouseEvent me) {
		if (isPrimaryMouseButton(me)) {
			deactivateWindowDragged();
		}
	}

	@FXML
	private void setResizeCursor(MouseEvent me) {
		StringBuilder cursorName = new StringBuilder(9);
		double resizeArea = SHADOW_SIZE + scalePane.getStrokeWidth();
		direction.clear();

		if (me.getSceneY() < resizeArea) {
			cursorName.append('N');
			direction.add(ResizeDirection.NORTH);
		} else if (me.getSceneY() > primaryStage.getHeight() - resizeArea) {
			cursorName.append('S');
			direction.add(ResizeDirection.SOUTH);
		}

		if (me.getSceneX() < resizeArea) {
			cursorName.append('W');
			direction.add(ResizeDirection.WEST);
		} else if (me.getSceneX() > primaryStage.getWidth() - resizeArea) {
			cursorName.append('E');
			direction.add(ResizeDirection.EAST);
		}

		if (primaryStage.isResizable() && cursorName.length() != 0) {
			scalePane.setCursor(Cursor.cursor(cursorName.append("_RESIZE")
					.toString()));
		}
	}

	@FXML
	private void activateWindowResize(MouseEvent me) {
		if (isPrimaryMouseButton(me)) {
			if (me.getClickCount() == 2) {
				onVerticalWindowResize(direction);
			} else {
				activateWindowResize();
			}
			me.consume();
		}
	}

	@FXML
	private void onWindowResize(MouseEvent me) {
		if (isPrimaryMouseButton(me) && !direction.isEmpty()) {
			onWindowResize(direction, me.getScreenX(), me.getScreenY());
		}
	}

	@FXML
	private void deactivateWindowResize(MouseEvent me) {
		if (isPrimaryMouseButton(me)) {
			deactivateWindowResize();
		}
	}

	private boolean isPrimaryMouseButton(MouseEvent me) {
		return me.getButton() == MouseButton.PRIMARY;
	}
}