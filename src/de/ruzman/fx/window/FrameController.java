package de.ruzman.fx.window;

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
import javafx.stage.StageStyle;
import javafx.stage.Window;
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

	@FXML protected StageBean stageBean = new StageBean();

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
		if(windowShadowBorder == null) {
			windowShadowBorder = new WindowShadowBorder();
		}
		
		addMaximizedChangeListener();
		restoreTranslation();
		
		if(topBar != null) {
			topBar.maxWidthProperty().bind(stageBean.widthProperty());
		}
		
		frame.prefHeightProperty().bind(stageBean.heightProperty());
		frame.prefWidthProperty().bind(stageBean.widthProperty());

		stageBean.widthProperty().addListener((a,b, c) -> calcTotalWidth());
		stageBean.heightProperty().addListener((a,b, c) -> calcTotalHeight());
	}

	private void addMaximizedChangeListener() {
		stageBean.maximizedProperty().addListener(
				new ChangeListener<Boolean>() {
					
					@Override
					public void changed(
							ObservableValue<? extends Boolean> observable,
							Boolean oldValue, Boolean newValue) {
						// isMaximized: false -> true
						if (!oldValue.booleanValue() && newValue.booleanValue()) {
							if (state != State.DRAG) {
								saveOldBounds();
							}

							root.setTranslateX(0);
							root.setTranslateY(0);
							stageBean.setX(0);
							stageBean.setY(0);
							scale(getVisualBounds());
							
							root.getChildren().remove(scalePane);

							maximize.getStyleClass().remove("maximize");
							maximize.getStyleClass().add("minimize");

							stageBean.setDocked(false);
							// isMaximized: true -> false
						} else if (oldValue.booleanValue()
								&& !newValue.booleanValue()) {

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
		if (state == State.DRAG && !stageBean.isMaximized()) {
			// FIXME: Logik ist falsch ... funktioniert bei verschiedenen
			// Dockarten wahrscheinlich nicht.
			if (stageBean.isDocked()) {
				if (!isDockingIntoTop(y)) {
					stageBean.setDocked(false);
					restoreTranslation();
					scale(oldBounds);
				}
			} else {
				getWindow().setY(y - draggedPosition.getY());
			}
			getWindow().setX(x - draggedPosition.getX());
		}
	}

	public void deactivateWindowDragged(double x, double y) {
		if (state == State.DRAG) {
			if (y < getVisualBounds().getHeight() / 15) {
				stageBean.setMaximized(true);
			} else if (x < getVisualBounds().getWidth() / 15) {
				stageBean.setDocked(true);
				root.setTranslateX(0);
				root.setTranslateY(0);
				scale(0, 0, getVisualBounds().getWidth() / 2, getVisualBounds()
						.getHeight());
				stageBean.setX(0);
				stageBean.setY(0);
			} else if (x > getVisualBounds().getWidth()
					- getVisualBounds().getWidth() / 15) {
				stageBean.setDocked(true);
				root.setTranslateY(0);
				scale(getVisualBounds().getWidth() / 2
						+ windowShadowBorder.getEastWidth(), 0,
						getVisualBounds().getWidth() / 2, getVisualBounds()
								.getHeight());
				stageBean.setX(getVisualBounds().getWidth()
						- stageBean.getWidth()
						+ windowShadowBorder.getEastWidth());
				stageBean.setY(0);
			}
			state = State.NONE;
		}
	}

	public synchronized void activateWindowResize(double screenX, double screenY) {
		if (state == State.NONE) {
			state = State.RESIZE;
			if (stageBean.isDocked() && isVerticalResizeDirection()) {
				// Do nothing
			} else {
				saveOldBounds();
			}
			draggedPosition = new Point2D(screenX, screenY);
		}
	}

	private void onVerticalWindowResize(EnumSet<ResizeDirection> direction) {
		if (stageBean.isResizable()
				&& (direction.contains(ResizeDirection.SOUTH) || direction
						.contains(ResizeDirection.NORTH))) {
			if (stageBean.isDocked()) {
				stageBean.setDocked(false);
				restoreTranslation();
				scale(oldBounds);
			} else {
				stageBean.setDocked(true);
				saveOldBounds();
				// FIX: Wenn maxHight überschritten wird, dann ignoriert scale
				// die Y-Verschiebung
				stageBean.setY(0);
				root.setTranslateY(0);
				scale(stageBean.getX(), 0, stageBean.getWidth(),
						getVisualBounds().getHeight());
			}
		}
	}

	public void onWindowResize(EnumSet<ResizeDirection> direction,
			double screenX, double screenY) {
		// FIXME: Resize, wenn seitlich angedockt ist.

		if (!hasMouseMoved(screenX, screenY) || !stageBean.isResizable()
				|| state != State.RESIZE) {
			return;
		}

		double px = stageBean.getX();
		double py = stageBean.getY();
		double w = stageBean.getWidth();
		double h = stageBean.getHeight();

		if (stageBean.isDocked() && isVerticalResizeDirection()) {
			restoreTranslation();

			px = oldBounds.getMinX();
			py = oldBounds.getMinY();
			w = oldBounds.getWidth();
			h = oldBounds.getHeight();
			stageBean.setDocked(false);
		}

		if (direction.contains(ResizeDirection.WEST)) {
			px = screenX - windowShadowBorder.getEastWidth();
			w = oldBounds.getWidth() + windowShadowBorder.getEastWidth()
					+ oldBounds.getMinX() - screenX;
		} else if (direction.contains(ResizeDirection.EAST)) {
			w = screenX - stageBean.getX()
					+ windowShadowBorder.getHorizonalWidth();
		}

		if (direction.contains(ResizeDirection.NORTH)) {
			py = screenY - windowShadowBorder.getNorthWidth();
			h = oldBounds.getHeight() + windowShadowBorder.getNorthWidth()
					+ oldBounds.getMinY() - screenY;
		} else if (direction.contains(ResizeDirection.SOUTH)) {
			h = screenY - stageBean.getY()
					+ windowShadowBorder.getVerticalWidth();
		}

		scale(px, py, w, h);
	}

	private boolean hasMouseMoved(double screenX, double screenY) {
		return ((int) draggedPosition.getX()) != ((int) screenX)
				|| ((int) draggedPosition.getY()) != ((int) screenY);
	}

	public void deactivateWindowResize() {
		if (state == State.RESIZE) {
			state = State.NONE;
		}
	}

	public void closeWindow() {
		getWindow().fireEvent(
				new WindowEvent(getWindow(), WindowEvent.WINDOW_CLOSE_REQUEST));
	}

	public Rectangle2D getVisualBounds() {
		ObservableList<Screen> screensForRectangle = Screen
				.getScreensForRectangle(stageBean.getX(), stageBean.getY(),
						stageBean.getWidth(), stageBean.getHeight());		
		return screensForRectangle.get(0).getVisualBounds();
	}

	protected void saveOldBounds() {
		if (!stageBean.isDocked()) {
			oldBounds = new Rectangle2D(stageBean.getX(), stageBean.getY(),
					stageBean.getWidth(), stageBean.getHeight());
		} else {
			// FIXME: Resize im Docking-Mode muss oldBounds ?berschreiben.
		}
	}

	public void scale(double x, double y, double width, double height) {
		if (width >= 0 && height >= 0) {
			scale(new Rectangle2D(x, y, width, height));
		}
	}

	protected void scale(Rectangle2D bounds) {
		// FIXME: Schatten stimmt nicht immer, wenn min/max Größe
		// unter/überschritten wird.
		double newHeight = bounds.getHeight();
		double newWidth = bounds.getWidth();
		double newX = bounds.getMinX();
		double newY = bounds.getMinY();		

		// FIXME: Abhängig von Style, wird maxGröße anders gehändelt.
		// FIXME: In den Properties regeln und nicht hier.
		//if(!stageBean.isMaximized() || (stageBean.isMaximized() && stageBean.getStageStyle() == StageStyle.DECORATED)) {
			if (newHeight > stageBean.getMaxHeight()) {
				newHeight = stageBean.getMaxHeight();
				newY = stageBean.getY();
			} else if (newHeight < stageBean.getMinHeight()) {
				newHeight = stageBean.getMinHeight();
				newY = stageBean.getY();
			}
	
			if (newWidth > stageBean.getMaxWidth()) {
				newWidth = stageBean.getMaxWidth();
				newX = stageBean.getX();
			} else if (newWidth < stageBean.getMinWidth()) {
				newWidth = stageBean.getMinWidth();
				newX = stageBean.getX();
			}
		//}

		stageBean.setHeight(newHeight);
		stageBean.setWidth(newWidth);
		stageBean.setX(newX);
		stageBean.setY(newY);
	}
	
	protected void calcTotalWidth() {
		System.out.println(stageBean.getWidth() + " -> " + (stageBean.getWidth()+windowShadowBorder.getHorizonalWidth()));
		
		stageBean.setTotalWidth(stageBean.getWidth()+windowShadowBorder.getHorizonalWidth());
	}
	
	protected void calcTotalHeight() {
		stageBean.setTotalHeight(stageBean.getHeight()+windowShadowBorder.getVerticalWidth());
	}
	
	
	protected void restoreTranslation() {
		root.setTranslateX(windowShadowBorder.getWestWidth());
		root.setTranslateY(windowShadowBorder.getNorthWidth());
	}

	private boolean isDockingIntoTop(double y) {
		// FIXME: Falsch: Seitenwechsel von Top > Out und Out > Top wird nicht
		// berücksichtigt.
		// FIXME: Funktioniert nicht, wenn in der Mitte der Frames gedraggt
		// wird.
		return Math.abs(draggedPosition.getY()) + Math.abs(y) < getVisualBounds()
				.getHeight() / 15;
	}

	private boolean isVerticalResizeDirection() {
		return direction.contains(ResizeDirection.NORTH)
				|| direction.contains(ResizeDirection.SOUTH);
	}

	private Window getWindow() {
		return root.getScene().getWindow();
	}

	@FXML
	private void iconifyWindow(ActionEvent event) {
		stageBean.setIconified(true);
	}

	@FXML
	private void maximizeWindow(ActionEvent event) {
		stageBean.setMaximized(!stageBean.isMaximized());
	}

	@FXML
	private void maximizeWindowOnDoubleCLick(MouseEvent me) {
		if (isPrimaryMouseButton(me) && me.getClickCount() == 2) {
			// FIXME: Logik in public-Methode verlagern
			if (stageBean.isDocked()) {
				stageBean.setDocked(false);
				restoreTranslation();
				scale(oldBounds);
			} else {
				stageBean.setMaximized(!stageBean.isMaximized());
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
		} else if (me.getSceneY() > stageBean.getHeight() - resizeArea) {
			cursorName.append('S');
			direction.add(ResizeDirection.SOUTH);
		}

		if (me.getSceneX() < resizeArea) {
			cursorName.append('W');
			direction.add(ResizeDirection.WEST);
		} else if (me.getSceneX() > stageBean.getWidth() - resizeArea) {
			cursorName.append('E');
			direction.add(ResizeDirection.EAST);
		}

		if (stageBean.isResizable() && cursorName.length() != 0) {
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
				activateWindowResize(me.getScreenX(), me.getScreenY());
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

	public StackPane getTopBar() {
		return topBar;
	}

	public StageBean getStageBean() {
		return stageBean;
	}

}