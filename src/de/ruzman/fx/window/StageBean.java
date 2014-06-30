package de.ruzman.fx.window;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.StageStyle;

public class StageBean {
	private ObjectProperty<StageStyle> stageStyle = new SimpleObjectProperty<>(StageStyle.DECORATED);	
	
	private DoubleProperty x = new SimpleDoubleProperty(0);
	private DoubleProperty y = new SimpleDoubleProperty(0);
	
	private DoubleProperty totalWidth = new SimpleDoubleProperty(0);
	private DoubleProperty totalHeight = new SimpleDoubleProperty(0);
	private DoubleProperty width = new SimpleDoubleProperty(0);
	private DoubleProperty height = new SimpleDoubleProperty(0);
	private DoubleProperty minHeight = new SimpleDoubleProperty(0);
	private DoubleProperty minWidth = new SimpleDoubleProperty(0);
	private DoubleProperty maxHeight = new SimpleDoubleProperty(0);
	private DoubleProperty maxWidth = new SimpleDoubleProperty(0);
	
	private BooleanProperty docked = new SimpleBooleanProperty(false);
	private BooleanProperty resizable = new SimpleBooleanProperty(true);
	private BooleanProperty maximized = new SimpleBooleanProperty(false);
	private BooleanProperty iconified = new SimpleBooleanProperty(false);

	private StringProperty title = new SimpleStringProperty("");
	
	public double getX() {
		return x.get();
	}

	public void setX(double x) {
		this.x.set(x);
	}

	public DoubleProperty xProperty() {
		return x;
	}

	public double getY() {
		return y.get();
	}

	public void setY(double y) {
		this.y.set(y);
	}

	public DoubleProperty yProperty() {
		return y;
	}
	
	public double getTotalWidth() {
		return totalWidth.get();
	}

	public void setTotalWidth(double totalWidth) {
		this.totalWidth.set(totalWidth);
	}

	public DoubleProperty totalWidthProperty() {
		return totalWidth;
	}

	public double getTotalHeight() {
		return totalHeight.get();
	}

	public void setTotalHeight(double totalHeight) {
		this.totalHeight.set(totalHeight);
	}

	public DoubleProperty totalHeightProperty() {
		return totalHeight;
	}
	
	public double getWidth() {
		return width.get();
	}

	public void setWidth(double width) {
		this.width.set(width);
	}

	public DoubleProperty widthProperty() {
		return width;
	}

	public double getHeight() {
		return height.get();
	}

	public void setHeight(double height) {
		this.height.set(height);
	}

	public DoubleProperty heightProperty() {
		return height;
	}
	
	public double getMinWidth() {
		return minWidth.get();
	}

	public void setMinWidth(double minWidth) {
		this.minWidth.set(minWidth);
	}

	public DoubleProperty minWidthProperty() {
		return minWidth;
	}

	public double getMinHeight() {
		return minHeight.get();
	}

	public void setMinHeight(double minHeight) {
		this.minHeight.set(minHeight);
	}

	public DoubleProperty minHeightProperty() {
		return minHeight;
	}
	
	public double getMaxWidth() {
		return maxWidth.get();
	}

	public void setMaxWidth(double maxWidth) {
		this.maxWidth.set(maxWidth);
	}

	public DoubleProperty maxWidthProperty() {
		return maxWidth;
	}

	public double getMaxHeight() {
		return maxHeight.get();
	}

	public void setMaxHeight(double maxHeight) {
		this.maxHeight.set(maxHeight);
	}

	public DoubleProperty maxHeightProperty() {
		return maxHeight;
	}

	public boolean getResizable() {
		return resizable.get();
	}

	public boolean isResizable() {
		return resizable.get();
	}

	public void setResizable(boolean resizable) {
		this.resizable.set(resizable);
	}

	public BooleanProperty resizableProperty() {
		return resizable;
	}

	public boolean getMaximized() {
		return maximized.get();
	}

	public boolean isMaximized() {
		return maximized.get();
	}

	public void setMaximized(boolean maximized) {
		this.maximized.set(maximized);
	}

	public BooleanProperty maximizedProperty() {
		return maximized;
	}

	public boolean getIconified() {
		return iconified.get();
	}

	public boolean isIconified() {
		return iconified.get();
	}

	public void setIconified(boolean iconified) {
		this.iconified.set(iconified);
	}

	public BooleanProperty iconifiedProperty() {
		return iconified;
	}

	public boolean getDocked() {
		return docked.get();
	}

	public boolean isDocked() {
		return docked.get();
	}

	public void setDocked(boolean docked) {
		this.docked.set(docked);
	}

	public BooleanProperty dockedProperty() {
		return docked;
	}

	public String getTitle() {
		return title.get();
	}

	public void setTitle(String title) {
		this.title.set(title);
	}

	public StringProperty titleProperty() {
		return title;
	}
	
	public StageStyle getStageStyle() {
		return stageStyle.get();
	}

	public void setStageStyle(StageStyle stageStyle) {
		this.stageStyle.set(stageStyle);
	}

	public ObjectProperty<StageStyle> stageStyleProperty() {
		return stageStyle;
	}
}
