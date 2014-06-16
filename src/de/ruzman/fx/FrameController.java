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
	} private EnumSet<ResizeDirection> direction = EnumSet.of(ResizeDirection.NORTH);
	public enum State {
		NONE, DRAG, RESIZE, MAXIMIZE
	} private State state = State.NONE;
	protected Rectangle2D oldBounds;
	protected Stage primaryStage;
	protected boolean isDocked;
	
	// FIXME: Konstanten Global nutzen (CSS + FXML)
	public static final int SHADOW_SIZE = 15;
	public static final int HALF_SHADOW_SIZE = SHADOW_SIZE/2;
	
	@FXML private Group root;
	@FXML private StackPane topBar;
	@FXML private BorderPane frame;
	@FXML private HBox frameControl;
	@FXML private Button maximize;
	@FXML private Button close;
	@FXML private Rectangle scalePane;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {		
        root.setTranslateX(SHADOW_SIZE/2);
        root.setTranslateY(SHADOW_SIZE/2);
		
        topBar.prefWidthProperty().bind(frame.widthProperty());
        scalePane.widthProperty().bind(frame.widthProperty());
        scalePane.heightProperty().bind(frame.heightProperty());
   	}
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        if(!primaryStage.isResizable()) { 
        	frameControl.getChildren().remove(maximize);
        }
        addResizableChangeListener();
        addMaximizedChangeListener();
	}
    
	private void addResizableChangeListener() {
        primaryStage.resizableProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				// isResizable: false -> true
				if(!oldValue.booleanValue() && newValue.booleanValue()) {
					frameControl.getChildren().add(frameControl.getChildren().indexOf(close), maximize);
				// isResizable: true -> false
				} else if(oldValue.booleanValue() && !newValue.booleanValue()) {
					frameControl.getChildren().remove(maximize);
				}
			}
		});
	}
	
	private void addMaximizedChangeListener() {
		primaryStage.maximizedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				// isMaximized: false -> true
				if(!oldValue.booleanValue() && newValue.booleanValue()) {
		        	ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(primaryStage.getX(),
		        			primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight());
		            Screen screen = screensForRectangle.get(0);

		            if(!isDocked) {
		            	oldBounds = new Rectangle2D(primaryStage.getX(), primaryStage.getY(),
		            			primaryStage.getWidth(), primaryStage.getHeight());
		            }
		        	scale(screen.getVisualBounds());
		            
		            root.setTranslateX(0);
		            root.setTranslateY(0);
		            root.getChildren().remove(scalePane);
					
					maximize.getStyleClass().remove("maximize");
					maximize.getStyleClass().add("minimize");
				// isMaximized: true -> false
				} else if(oldValue.booleanValue() && !newValue.booleanValue()) {
					// FIXME: Doppelklick zum Kleiner machen und gleichzeitig verschieben:
		        	mapFrameToStage();
		        	
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

	public synchronized void activateFrameDragged(double x, double y) {
		if(state == State.NONE) {
			state = State.DRAG;
			oldBounds = new Rectangle2D(x, y, 0, 0);
		}
	}
	
	public void onFrameDragged(double x, double y) {
		if(state == State.DRAG && !primaryStage.isMaximized() && !isDocked) {
            root.getScene().getWindow().setX(x - oldBounds.getMinX());
            root.getScene().getWindow().setY(y - oldBounds.getMinY());
		}
	}
	
	public void deactivateFrameDragged() {
		if(state == State.DRAG) {
			state = State.NONE;
		}
	}
	
	public void closeWindow() {
		primaryStage.close();
	}
	
	public void iconifyWindow() {
		primaryStage.setIconified(true);
	}
    
	public synchronized void activateFrameResize() {
		if(state == State.NONE) {
			state = State.RESIZE;
			oldBounds = new Rectangle2D(primaryStage.getX(), primaryStage.getY(),
					primaryStage.getWidth(), primaryStage.getHeight());
		}
	}
	
    private void onVerticalFrameResize(EnumSet<ResizeDirection> direction) {
    	if(primaryStage.isResizable() && (direction.contains(ResizeDirection.SOUTH) || 
    			direction.contains(ResizeDirection.NORTH))) {
            oldBounds = new Rectangle2D(primaryStage.getX(), primaryStage.getY(),
					primaryStage.getWidth(), primaryStage.getHeight());
    		
    		isDocked = true;
        	ObservableList<Screen> screensForRectangle = Screen.getScreensForRectangle(primaryStage.getX(),
        			primaryStage.getY(), primaryStage.getWidth(), primaryStage.getHeight());
            Screen screen = screensForRectangle.get(0);
            
        	scale(new Rectangle2D(primaryStage.getX(), 0, primaryStage.getWidth(), screen.getVisualBounds().getHeight()+SHADOW_SIZE*3/2));
            root.setTranslateY(0);

    	}
	}
	
    public void onFrameResize(EnumSet<ResizeDirection> direction, double screenX, double screenY) {
    	// FIXME: Minimum beim Resize beachten
        if(primaryStage.isResizable() && state == State.RESIZE) {
        	double x = primaryStage.getX();
        	double y = primaryStage.getY();
        	double w = primaryStage.getWidth();
        	double h = primaryStage.getHeight();
        	
        	if(direction.contains(ResizeDirection.WEST)) {
        		x = screenX - HALF_SHADOW_SIZE;
        		w = oldBounds.getWidth() + HALF_SHADOW_SIZE + oldBounds.getMinX() - screenX;
        	} else if(direction.contains(ResizeDirection.EAST)) {
        		w = screenX - primaryStage.getX() + SHADOW_SIZE;
        	}
        	
        	if(direction.contains(ResizeDirection.NORTH)) {
        		y = screenY - HALF_SHADOW_SIZE;
        		h = oldBounds.getHeight() + HALF_SHADOW_SIZE + oldBounds.getMinY() - screenY;
        	} else if(direction.contains(ResizeDirection.SOUTH)) {
        		h = screenY - primaryStage.getY() + SHADOW_SIZE;
        	}

            scale(new Rectangle2D(x, y, w, h));
       	}
    }
	
	public void deactivateFrameResize() {
		if(state == State.RESIZE) {
			state = State.NONE;
		}
	}
    
    private void scale(Rectangle2D bounds) {
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        
        mapFrameToStage();
    }
    
    private void mapFrameToStage() {
    	if(primaryStage.isMaximized()) {
    		frame.setPrefSize(primaryStage.getWidth(), primaryStage.getHeight());
    	} else {
    		frame.setPrefSize(primaryStage.getWidth()-HALF_SHADOW_SIZE*3,
    				primaryStage.getHeight()-HALF_SHADOW_SIZE*3);
    	}
    }
	
    @FXML private void iconifyWindow(ActionEvent event) {
    	iconifyWindow();
    }
    
    @FXML private void maximizeWindow(ActionEvent event) {
    	primaryStage.setMaximized(!primaryStage.isMaximized());
    }
    
    @FXML private void maximizeWindowOnDoubleCLick(MouseEvent me) {
        if (me.getButton().equals(MouseButton.PRIMARY) && me.getClickCount() == 2) {
    		// FIXME: Logik in public-Methode verlagern
        	if(isDocked) {
        		root.setTranslateY(HALF_SHADOW_SIZE);
        		scale(oldBounds);
        		mapFrameToStage();
        		isDocked = false;
        	} else {
        		primaryStage.setMaximized(!primaryStage.isMaximized());
        	}
        	
       }
    }
    
    @FXML private void closeWindow(ActionEvent event) {
    	closeWindow();
    }
    
    @FXML private void activateFrameDragged(MouseEvent me) {
    	if(me.getButton() == MouseButton.PRIMARY) {
    		activateFrameDragged(me.getSceneX(), me.getSceneY());
    	}
    }
    
    @FXML private void onFrameDragged(MouseEvent me) {
    	if(me.getButton() == MouseButton.PRIMARY) {
    		onFrameDragged(me.getScreenX(), me.getScreenY());
    	}
    }
    
    @FXML private void deactivateFrameDragged(MouseEvent me) {
    	if(me.getButton() == MouseButton.PRIMARY) {
    		deactivateFrameDragged();
    	}
    }
    
    @FXML private void setResizeCursor(MouseEvent me) {
		String cursorName = "_RESIZE";
		double resizeArea = SHADOW_SIZE + scalePane.getStrokeWidth();
		direction.clear();
		
		if (me.getSceneX() < resizeArea) {
			cursorName = "W" + cursorName;
			direction.add(ResizeDirection.WEST);
		} else if (me.getSceneX() > primaryStage.getWidth() - resizeArea) {
			cursorName = "E" + cursorName;
			direction.add(ResizeDirection.EAST);
		}

		if (me.getSceneY() < resizeArea) {
			cursorName = "N" + cursorName;
			direction.add(ResizeDirection.NORTH);
		} else if (me.getSceneY() > primaryStage.getHeight() - resizeArea) {
			cursorName = "S" + cursorName;
			direction.add(ResizeDirection.SOUTH);
		}

		if (primaryStage.isResizable() && !direction.isEmpty()) {
			scalePane.setCursor(Cursor.cursor(cursorName));
		}
	}
    
    @FXML private void activateFrameResize(MouseEvent me) {
    	if(me.getButton() == MouseButton.PRIMARY) {
    		if(me.getClickCount() == 2) {
    			onVerticalFrameResize(direction);
    		} else {
        		activateFrameResize();
    		}
    	}
    }

	@FXML private void onFrameResize(MouseEvent me) {
    	if(me.getButton() == MouseButton.PRIMARY && !direction.isEmpty()) {
        	onFrameResize(direction, me.getScreenX(), me.getScreenY());
    	}
    }
    
    @FXML private void deactivateFrameResize(MouseEvent me) {
    	if(me.getButton() == MouseButton.PRIMARY) {
    		deactivateFrameResize();
    	}
    }
}
