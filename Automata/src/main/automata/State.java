package main.automata;

import java.awt.Color;

public class State{
	public final String stateName;
	private Color stateColor = Color.BLACK;
	
	public State(String stateName, Color stateColor) {
		this.stateName = stateName == null? "DEFAULT":stateName.trim();
		this.stateColor = stateColor;
	}
	
	public void setColor(Color c) {
		stateColor = c;
	}
	
	public Color getColor() {
		return stateColor;
	}
}