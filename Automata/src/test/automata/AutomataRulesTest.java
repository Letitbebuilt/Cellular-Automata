package test.automata;

import java.awt.Color;

import org.junit.jupiter.api.Test;

import main.automata.Automata.NeighborType;
import main.automata.AutomataRules;
import main.automata.State;

public class AutomataRulesTest {
	
	@Test
	public void ruleRead_noFile_defaults() {
		AutomataRules defaultRules = new AutomataRules("notARealFilePath");
		assert(defaultRules.automataName.equals("unknown"));
		assert(defaultRules.stateTransitions.isEmpty());
		assert(defaultRules.possibleStates.isEmpty());
		assert(defaultRules.neighborType == NeighborType.MOORE);
	}
	
	@Test
	public void ruleRead_conwayFile_conwayRulesLoadedIn() {
		State aliveState = new State("Alive", Color.white);
		State deadState = new State("Dead", Color.black);
		AutomataRules defaultRules = new AutomataRules("resources/automata/conway.xml");
		assert(defaultRules.automataName.equals("Conway's Game Of Life"));
		assert(defaultRules.possibleStates.size() == 2);
		assert(defaultRules.possibleStates.contains(aliveState));
		assert(defaultRules.possibleStates.contains(deadState));
	}
}
