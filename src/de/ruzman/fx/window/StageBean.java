package de.ruzman.fx.window;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StageBean {
	private DoubleProperty x = new SimpleDoubleProperty(0);
	private DoubleProperty y = new SimpleDoubleProperty(0);
	private DoubleProperty width = new SimpleDoubleProperty(0);
	private DoubleProperty height = new SimpleDoubleProperty(0);
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
		return iconified.get();
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
}
