package de.ruzman.fx;

import java.net.URL;
import java.util.EnumSet;
import java.util.ResourceBundle;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.stage.WindowEvent;

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
	protected boolean isDocked;

	protected DoubleProperty x = new SimpleDoubleProperty(0.0);
	protected DoubleProperty y = new SimpleDoubleProperty(0.0);
	protected DoubleProperty width = new SimpleDoubleProperty(0.0);
	protected DoubleProperty height = new SimpleDoubleProperty(0.0);
	protected BooleanProperty resizable = new SimpleBooleanProperty(true);
	protected BooleanProperty maximized = new SimpleBooleanProperty(false);
	protected BooleanProperty iconified = new SimpleBooleanProperty(false);

	@FXML private WindowShadowBorder windowShadowBorder;
	@FXML private Group root;
	@FXML private StackPane topBar;
	@FXML private BorderPane frame;
	@FXML private HBox frameControl;
	@FXML private Button maximize;
	@FXML private Button close;
	@FXML private Rectangle scalePane;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (windowShadowBorder == null) {
			// FIXME: Skalierung funktioniert bei Programmstart nicht.
			windowShadowBorder = new WindowShadowBorder();
		}

		topBar.prefWidthProperty().bind(frame.widthProperty());
		scalePane.widthProperty().bind(frame.widthProperty());
		scalePane.heightProperty().bind(frame.heightProperty());

		addResizableChangeListener();
		addMaximizedChangeListener();

		restoreTranslation();
	}

	private void addResizableChangeListener() {
		resizable.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				// isResizable: false -> true
				if (!oldValue.booleanValue() && newValue.booleanValue()) {
					frameControl.getChildren()
							.add(frameControl.getChildren().indexOf(close),
									maximize);
					// isResizable: true -> false
				} else if (oldValue.booleanValue() && !newValue.booleanValue()) {
					frameControl.getChildren().remove(maximize);
				}
			}
		});
	}

	private void addMaximizedChangeListener() {
		// FIXME: Minimize funktioniert nicht
		maximized.addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				// isMaximized: false -> true
				if (!oldValue.booleanValue() && newValue.booleanValue()) {
					if (state != State.DRAG) {
						saveOldBounds();
					}

					scale(getVisualBounds());

					root.setTranslateX(0);
					root.setTranslateY(0);
					root.getChildren().remove(scalePane);

					maximize.getStyleClass().remove("maximize");
					maximize.getStyleClass().add("minimize");

					isDocked = false;
					// isMaximized: true -> false
				} else if (oldValue.booleanValue() && !newValue.booleanValue()) {

					restoreTranslation();
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
			saveOldBounds();
		}
	}

	public void onWindowDragged(double x, double y) {
		if (state == State.DRAG && !maximized.getValue()) {
			// FIXME: Logik ist falsch ... funktioniert bei verschiedenen
			// Dockarten wahrscheinlich nicht.
			if (isDocked) {
				if (!isDockingIntoTop(y)) {
					isDocked = false;
					restoreTranslation();
					scale(oldBounds);
				}
			} else {
				root.getScene().getWindow().setY(y - draggedPosition.getY());
			}
			root.getScene().getWindow().setX(x - draggedPosition.getX());
		}
	}

	public void deactivateWindowDragged(double x, double y) {
		if (state == State.DRAG) {
			if (y < getVisualBounds().getHeight() / 15) {
				maximized.setValue(true);
			} else if (x < getVisualBounds().getWidth() / 15) {
				isDocked = true;
				root.setTranslateX(0);
				root.setTranslateY(0);
				scale(new Rectangle2D(0, 0, getVisualBounds().getWidth() / 2,
						getVisualBounds().getHeight()));
			} else if (x > getVisualBounds().getWidth()
					- getVisualBounds().getWidth() / 15) {
				isDocked = true;
				root.setTranslateY(0);
				scale(new Rectangle2D(getVisualBounds().getWidth() / 2 + windowShadowBorder.getEastWidth(), 0,
						getVisualBounds().getWidth() / 2, getVisualBounds()
								.getHeight()));
			}
			state = State.NONE;
		}
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
		if (resizable.getValue()
				&& (direction.contains(ResizeDirection.SOUTH) || direction
						.contains(ResizeDirection.NORTH))) {
			if (isDocked) {
				isDocked = false;
				restoreTranslation();
				scale(oldBounds);
			} else {
				isDocked = true;
				saveOldBounds();
				scale(new Rectangle2D(x.getValue(), 0, width.getValue(),
						getVisualBounds().getHeight()));
				root.setTranslateY(0);
			}
		}
	}

	public void onWindowResize(EnumSet<ResizeDirection> direction,
			double screenX, double screenY) {
		// FIXME: Minimum beim Resize beachten
		if (resizable.getValue() && state == State.RESIZE) {
			double px = x.getValue();
			double py = y.getValue();
			double w = width.getValue();
			double h = height.getValue();

			if (isDocked
					&& (direction.contains(ResizeDirection.NORTH) || direction
							.contains(ResizeDirection.SOUTH))) {
				restoreTranslation();

				px = oldBounds.getMinX();
				py = oldBounds.getMinY();
				w = oldBounds.getWidth();
				h = oldBounds.getHeight();
				isDocked = false;
			}

			if (direction.contains(ResizeDirection.WEST)) {
				px = screenX - windowShadowBorder.getEastWidth();
				w = oldBounds.getWidth() + windowShadowBorder.getEastWidth()
						+ oldBounds.getMinX() - screenX;
			} else if (direction.contains(ResizeDirection.EAST)) {
				w = screenX - x.getValue()
						+ windowShadowBorder.getHorizonalWidth();
			}

			if (direction.contains(ResizeDirection.NORTH)) {
				py = screenY - windowShadowBorder.getNorthWidth();
				h = oldBounds.getHeight() + windowShadowBorder.getNorthWidth()
						+ oldBounds.getMinY() - screenY;
			} else if (direction.contains(ResizeDirection.SOUTH)) {
				h = screenY - y.getValue()
						+ windowShadowBorder.getVerticalWidth();
			}

			scale(new Rectangle2D(px, py, w, h));
		}
	}

	public void deactivateWindowResize() {
		if (state == State.RESIZE) {
			state = State.NONE;
		}
	}

	public void closeWindow() {
		root.getScene()
				.getWindow()
				.fireEvent(
						new WindowEvent(root.getScene().getWindow(),
								WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	public Rectangle2D getVisualBounds() {
		ObservableList<Screen> screensForRectangle = Screen
				.getScreensForRectangle(x.getValue(), y.getValue(),
						width.getValue(), height.getValue());

		return screensForRectangle.get(0).getVisualBounds();
	}

	protected void saveOldBounds() {
		if (!isDocked) {
			oldBounds = new Rectangle2D(x.getValue(), y.getValue(),
					width.getValue(), height.getValue());
		} else {
			// FIXME: Resize im Docking-Mode muss oldBounds ?berschreiben.
		}
	}

	protected void scale(Rectangle2D bounds) {
		height.setValue(bounds.getHeight());
		width.setValue(bounds.getWidth());
		x.setValue(bounds.getMinX());
		y.setValue(bounds.getMinY());

		mapFrameToStage();
	}

	protected void mapFrameToStage() {
		if (maximized.getValue()) {
			frame.setPrefSize(width.getValue(), height.getValue());
		} else if (isDocked) {
			frame.setPrefSize(
					width.getValue() - windowShadowBorder.getHorizonalWidth(),
					getVisualBounds().getHeight());
		} else {
			// FIXME: ???
			frame.setPrefSize(
					width.getValue() - windowShadowBorder.getNorthWidth() * 3,
					height.getValue() - windowShadowBorder.getNorthWidth() * 3);
		}
	}

	protected void restoreTranslation() {
		root.setTranslateX(windowShadowBorder.getWestWidth());
		root.setTranslateY(windowShadowBorder.getNorthWidth());
		mapFrameToStage();
	}

	private boolean isDockingIntoTop(double y) {
		// FIXME: Falsch: Seitenwechsel von Top > Out und Out > Top wird nicht berücksichtigt.
		// FIXME: Funktioniert nicht, wenn in der Mitte der Frames gedraggt wird.
		return Math.abs(draggedPosition.getY()) + Math.abs(y) < getVisualBounds()
				.getHeight() / 15;
	}

	@FXML
	private void iconifyWindow(ActionEvent event) {
		iconified.setValue(true);
	}

	@FXML
	private void maximizeWindow(ActionEvent event) {
		maximized.setValue(!maximized.getValue());
	}

	@FXML
	private void maximizeWindowOnDoubleCLick(MouseEvent me) {
		if (isPrimaryMouseButton(me) && me.getClickCount() == 2) {
			// FIXME: Logik in public-Methode verlagern
			if (isDocked) {
				isDocked = false;
				restoreTranslation();
				scale(oldBounds);
			} else {
				maximized.setValue(!maximized.getValue());
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
			deactivateWindowDragged(me.getScreenX(), me.getScreenY());
		}
	}

	@FXML
	private void setResizeCursor(MouseEvent me) {
		StringBuilder cursorName = new StringBuilder(9);
		// FIXME: Worng Value: double resizeArea = SHADOW_SIZE +
		// scalePane.getStrokeWidth();
		double resizeArea = windowShadowBorder.getHorizonalWidth()
				+ scalePane.getStrokeWidth();
		direction.clear();

		if (me.getSceneY() < resizeArea) {
			cursorName.append('N');
			direction.add(ResizeDirection.NORTH);
		} else if (me.getSceneY() > height.getValue() - resizeArea) {
			cursorName.append('S');
			direction.add(ResizeDirection.SOUTH);
		}

		if (me.getSceneX() < resizeArea) {
			cursorName.append('W');
			direction.add(ResizeDirection.WEST);
		} else if (me.getSceneX() > width.getValue() - resizeArea) {
			cursorName.append('E');
			direction.add(ResizeDirection.EAST);
		}

		if (resizable.getValue() && cursorName.length() != 0) {
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