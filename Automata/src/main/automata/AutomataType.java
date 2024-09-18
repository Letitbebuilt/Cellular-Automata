package main.automata;

import java.util.List;
import java.util.function.Function;

import main.automata.Automata.NeighborType;

public class AutomataType{
	public String name;
	public List<State> states;
	public List<Function<Automata, Boolean>> transitionRules;
	public String defaultStateName;
	public NeighborType neighborType;
	
	public AutomataType(AutomataRules rules) {
		this.name = rules.automataName;
		this.states = rules.possibleStates;
		this.transitionRules = rules.stateTransitions;
		this.defaultStateName = rules.defaultState;
		this.neighborType = rules.neighborType;
	}
	
	public AutomataType(String name, List<State> states, List<Function<Automata, Boolean>> transitionRules, String defaultStateName, NeighborType neighborType) {
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
}