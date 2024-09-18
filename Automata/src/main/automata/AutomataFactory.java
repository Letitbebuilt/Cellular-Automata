package main.automata;

import java.awt.Color;
import java.awt.Point;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import main.automata.Automata.NeighborType;

public class AutomataFactory {
	
	static AutomataRules conway = new AutomataRules("resources/automata/conway.xml");
	static AutomataRules briansBrain = new AutomataRules("resources/automata/brians-brain.xml");
	static AutomataRules wireWorld = new AutomataRules("resources/automata/wire-world.xml");
	static AutomataRules dayAndNight = new AutomataRules("resources/automata/day-and-night.xml");
	public static enum AutomataTypes{
		CONWAY(conway.automataName, conway.possibleStates, conway.stateTransitions, conway.defaultState, conway.neighborType),
		BRIANS_BRAIN(briansBrain.automataName, briansBrain.possibleStates, briansBrain.stateTransitions, briansBrain.defaultState, briansBrain.neighborType),
		WIRE_WORLD(wireWorld.automataName, wireWorld.possibleStates, wireWorld.stateTransitions, wireWorld.defaultState, wireWorld.neighborType),
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
			, List.of(new Function<>() {
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
		}), "Black", NeighborType.VON_NEUMANN),
		DAY_AND_NIGHT(dayAndNight.automataName, dayAndNight.possibleStates, dayAndNight.stateTransitions, dayAndNight.defaultState, dayAndNight.neighborType);
		public String name;
		public List<State> states;
		public List<Function<Automata, Boolean>> transitionRules;
		public String defaultStateName;
		public NeighborType neighborType;
		AutomataTypes(String name, List<State> states, List<Function<Automata, Boolean>> transitionRules, String defaultStateName, NeighborType neighborType) {
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
		for(Function<Automata, Boolean> transition: type.transitionRules) {
			a.addStateTransitionRule(transition);
		}
		a.setCurrentState(type.defaultStateName);
		a.setNeighborType(type.neighborType);
		return a;
	}
	
}
