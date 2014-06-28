package de.ruzman.fx.window;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class WindowShadowBorder {
	private DoubleProperty northWidth = new SimpleDoubleProperty(0);
	private DoubleProperty eastWidth = new SimpleDoubleProperty(0);
	private DoubleProperty southWidth = new SimpleDoubleProperty(0);
	private DoubleProperty westWidth = new SimpleDoubleProperty(0);
	
	public double getNorthWidth() {
		return northWidth.getValue();
	}
	public DoubleProperty northWidth() {
		return northWidth;
	}
	public void setNorthWidth(double northWidth) {
		this.northWidth.setValue(northWidth);;
	}
	
	public double getEastWidth() {
		return eastWidth.getValue();
	}
	public DoubleProperty eastWidth() {
		return eastWidth;
	}
	public void setEastWidth(double eastWidth) {
		this.eastWidth.setValue(eastWidth);;
	}
	
	public double getSouthWidth() {
		return southWidth.getValue();
	}
	public DoubleProperty southWidth() {
		return southWidth;
	}
	public void setSouthWidth(double southWidth) {
		this.southWidth.setValue(southWidth);;
	}
	
	public double getWestWidth() {
		return westWidth.getValue();
	}
	public DoubleProperty westWidth() {
		return westWidth;
	}
	public void setWestWidth(double westWidth) {
		this.westWidth.setValue(westWidth);;
	}
	
	public double getHorizonalWidth() {
		return westWidth.get() + eastWidth.get();
	}
	
	public double getVerticalWidth() {
		return northWidth.get() + southWidth.get();
	}
}
