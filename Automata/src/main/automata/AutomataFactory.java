package main.automata;

import java.awt.Point;
import java.util.Random;
import java.util.function.Function;

public class AutomataFactory {
	final static Random rand = new Random();
	public static final Automata getCellForAutomata(Point pos, AutomataType type) {
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
