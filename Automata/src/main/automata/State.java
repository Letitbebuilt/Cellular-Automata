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
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof State)) return false;
		State otherState = (State)obj;
		if(!otherState.stateName.trim().equalsIgnoreCase(stateName.trim())) {
			return false;
		}
		if(otherState.stateColor != stateColor) {
			return false;
		}
		return true;
	}
}