package main.automata;

import java.awt.Point;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import main.board.Board;

public class Automata {
	public enum NeighborType{
		MOORE,
		VON_NEUMANN
	}
	public Point pos;
	public State currentState;
	private State nextState;
	private List<State> possibleStates = new ArrayList<>();
	private List<Function<Automata, Boolean>> stateTransitions = new ArrayList<>();
	private NeighborType neighborType;
	public List<Automata> neighbors = new ArrayList<>();
	
	protected Automata(Point pos) {
		this.pos = pos;
	}

	public LinkedList<Automata> loadNeighbors(Board board) {
		neighbors.clear();
		switch(neighborType) {
			case MOORE:
				for(int x = pos.x-1; x<=pos.x+1; x++) {
					for(int y = pos.y-1; y<=pos.y+1; y++) {
						if(x == pos.x && y == pos.y) {
							continue;
						}
						Automata cell = board.getCell(x, y);
						if(cell != null) {
							neighbors.add(cell);
						}
					}
				}
				break;
			case VON_NEUMANN:
				Automata leftCell = board.getCell(pos.x-1, pos.y);
				if(leftCell != null) {
					neighbors.add(leftCell);
				}
				Automata rightCell = board.getCell(pos.x+1, pos.y);
				if(rightCell != null) {
					neighbors.add(rightCell);
				}
				Automata topCell = board.getCell(pos.x, pos.y-1);
				if(topCell != null) {
					neighbors.add(topCell);
				}
				Automata bottomCell = board.getCell(pos.x, pos.y+1);
				if(bottomCell != null) {
					neighbors.add(bottomCell);
				}
				break;
			
		}
		
		return new LinkedList<>(neighbors);
	}
	

	public List<State> getStates(){
		return possibleStates;
	}
	
	
	public void addState(State s) {
		possibleStates.add(s);
		if(possibleStates.size() == 1) {
			currentState = s;
			nextState = s;
		}
	}
	
	public void addStateTransitionRule(Function<Automata, Boolean> f) {
		stateTransitions.add(f);
	}
	
	public void calculateNextState() {
		for(Function<Automata, Boolean> transition: stateTransitions) {
			if(transition.apply(this)) {
				break;
			}
		}
	}
	
	public void updateStateToNextState() {
		currentState = nextState;
	}
	
	public void setCurrentState(String stateName) {
		Optional<State> possibleNewState = possibleStates.stream().filter(e -> e.stateName.equalsIgnoreCase(stateName.trim())).findFirst();
		if(!possibleNewState.isEmpty()) {
			currentState = possibleNewState.get();
			nextState = possibleNewState.get();
		}
	}
	
	public void setNextState(String stateName) {
		Optional<State> possibleNewState = possibleStates.stream().filter(e -> e.stateName.equalsIgnoreCase(stateName.trim())).findFirst();
		if(!possibleNewState.isEmpty()) {
			this.nextState = possibleNewState.get();
		}
	}
	
	public boolean isState(String stateName) {
		Optional<State> possibleNewState = possibleStates.stream().filter(e -> e.stateName.equalsIgnoreCase(stateName.trim())).findFirst();
		if(!possibleNewState.isEmpty() && currentState == possibleNewState.get()) {
			return true;
		}
		return false;
	}

	public void setNeighborType(NeighborType neighborType) {
		this.neighborType = neighborType;
	}
}
