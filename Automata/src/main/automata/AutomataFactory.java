package main.automata;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import main.automata.Automata.NeighborType;

public class AutomataFactory {
	public static enum AutomataTypes{
		CONWAY("Conway's Game of Life",
			List.of(new State("Alive", Color.LIGHT_GRAY), new State("Dead", Color.BLACK)), new Function<>() {
			public Boolean apply(Automata t) {
				int counter = 0;
				for(Automata neighbor: t.neighbors) {
					if(neighbor.isState("Alive")) {
						counter++;
					}
				}
				
				if(counter == 3) {
					t.setNextState("Alive");
				}
				else if(counter != 2 && t.isState("Alive")) {
					t.setNextState("Dead");
				}
				return true;
			}
		}, "Dead", NeighborType.MOORE),
		BRIANS_BRAIN("Brian's Brain",
				List.of(new State("On", Color.LIGHT_GRAY), new State("Dying", Color.CYAN), new State("Off", Color.BLACK)), new Function<>() {
			public Boolean apply(Automata t) {
				int counter = 0;
				if(t.isState("On")) {
					t.setNextState("Dying");
				}
				else if(t.isState("Dying")) {
					t.setNextState("Off");
				}
				else {
					for(Automata neighbor: t.neighbors) {
						if(neighbor.isState("On")) {
							counter++;
						}
					}
					if(counter == 2) {
						t.setNextState("On");
					}
				}
				return true;
			}
		}, "Off", NeighborType.MOORE),
		WIRE_WORLD("Wire World",
				List.of(new State("Wire", Color.LIGHT_GRAY), new State("Electron Head", Color.YELLOW), new State("Electron Tail", Color.RED), new State("Grounding", Color.BLACK)), new Function<>() {
			public Boolean apply(Automata t) {
				int counter = 0;
				if(t.isState("Grounding")) {/*do nothing*/}
				else if(t.isState("Electron Head")) {
					t.setNextState("Electron Tail");
				}
				else if(t.isState("Electron Tail")) {
					t.setNextState("Wire");
				}
				else {
					for(Automata neighbor: t.neighbors) {
						if(neighbor.isState("Electron Head")) {
							counter++;
						}
					}
					if(counter == 2 || counter == 1) {
						t.setNextState("Electron Head");
					}
				}
				return true;
			}
		}, "Grounding", NeighborType.MOORE),
		LANGTON_ANT("Langton's Ant",
				List.of(
				new State("White", Color.LIGHT_GRAY), 
				new State("Black", Color.BLACK), 
				new State("Ant L W", Color.MAGENTA), 
				new State("Ant R W", Color.MAGENTA), 
				new State("Ant U W", Color.MAGENTA), 
				new State("Ant D W", Color.MAGENTA), 
				new State("Ant L B", Color.MAGENTA), 
				new State("Ant R B", Color.MAGENTA), 
				new State("Ant U B", Color.MAGENTA), 
				new State("Ant D B", Color.MAGENTA))
			, new Function<>() {
			public Boolean apply(Automata t) {
				List<Character> directions = List.of('U', 'R', 'D', 'L');
				int currentDirection = 0;
				String stateName = t.currentState.stateName;
				if(stateName.contains("Ant")) {
					char direction = stateName.charAt(4);
					currentDirection = directions.indexOf(direction);
					char color = stateName.charAt(6);
					if(color == 'W') {
						t.setNextState("Black");
						currentDirection = (currentDirection + 1) % directions.size();
					}
					else {
						t.setNextState("White");
						currentDirection = (currentDirection - 1);
						if(currentDirection < 0) currentDirection = directions.size()-1;
					}
					Optional<Automata> possibleNeighbor = null;
					switch(directions.get(currentDirection)) {
						case 'L':
							possibleNeighbor = t.neighbors.stream().filter(n -> n.pos.x == t.pos.x-1).findFirst();
							if(!possibleNeighbor.isEmpty()) {
								Automata neighbor = possibleNeighbor.get();
								neighbor.setNextState(neighbor.isState("White")? "Ant L W":"Ant L B");
							}
							break;
						case 'R':
							possibleNeighbor = t.neighbors.stream().filter(n -> n.pos.x == t.pos.x+1).findFirst();
							if(!possibleNeighbor.isEmpty()) {
								Automata neighbor = possibleNeighbor.get();
								neighbor.setNextState(neighbor.isState("White")? "Ant R W":"Ant R B");
							}
							break;
						case 'U':
							possibleNeighbor = t.neighbors.stream().filter(n -> n.pos.y == t.pos.y-1).findFirst();
							if(!possibleNeighbor.isEmpty()) {
								Automata neighbor = possibleNeighbor.get();
								neighbor.setNextState(neighbor.isState("White")? "Ant U W":"Ant U B");
							}
							break;
						case 'D':
							possibleNeighbor = t.neighbors.stream().filter(n -> n.pos.y == t.pos.y+1).findFirst();
							if(!possibleNeighbor.isEmpty()) {
								Automata neighbor = possibleNeighbor.get();
								neighbor.setNextState(neighbor.isState("White")? "Ant D W":"Ant D B");
							}
							break;
					}
				}
				return true;
			}
		}, "Black", NeighborType.VON_NEUMANN),
		DAY_AND_NIGHT("Day and Night",
				List.of(new State("Alive", Color.ORANGE), new State("Dead", Color.BLACK)), new Function<>() {
			public Boolean apply(Automata t) {
				int counter = 0;
				for(Automata neighbor: t.neighbors) {
					if(neighbor.isState("Alive")) {
						counter++;
					}
				}
				
				if(counter == 3 || counter == 6 || counter == 7 || counter == 8) {
					t.setNextState("Alive");
				}
				else if(counter != 4 && t.isState("Alive")) {
					t.setNextState("Dead");
				}
				return true;
			}
		}, "Dead", NeighborType.MOORE);
		public String name;
		public List<State> states;
		public Function<Automata, Boolean> transitionRules;
		public String defaultStateName;
		public NeighborType neighborType;
		AutomataTypes(String name, List<State> states, Function<Automata, Boolean> transitionRules, String defaultStateName, NeighborType neighborType) {
			this.name = name;
			this.states = states;
			this.transitionRules = transitionRules;
			this.defaultStateName = defaultStateName;
			this.neighborType = neighborType;
		}
		
		public String[] getStateNames() {
			String[] names = new String[states.size()];
			for(int i = 0; i<names.length; i++) {
				names[i] = states.get(i).stateName;
			}
			return names;
		}
	};
	
	

	final static Random rand = new Random();
	public static final Automata getCellForAutomata(Point pos, AutomataTypes type) {
		Automata a = new Automata(pos);
		for(State state: type.states) {
			a.addState(state);
		}
		a.addStateTransitionRule(type.transitionRules);
		a.setCurrentState(type.defaultStateName);
		a.setNeighborType(type.neighborType);
		return a;
	}
	
}
