package main.automata;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import main.automata.Automata.NeighborType;

public class AutomataRules {
	public String automataName = "unknown";
	public ArrayList<State> possibleStates = new ArrayList<>();
	public String defaultState = "";
	public List<Function<Automata, Boolean>> stateTransitions = new ArrayList<>();
	public NeighborType neighborType = NeighborType.MOORE;
	HashMap<String, Color> knownColors = new HashMap<>();;
	
	public AutomataRules(String filePath) {
		
		knownColors.put("WHITE", Color.white);
		knownColors.put("BLACK", Color.black);
		knownColors.put("BLUE", Color.blue);
		knownColors.put("CYAN", Color.cyan);
		knownColors.put("DARK_GRAY", Color.darkGray);
		knownColors.put("GRAY", Color.GRAY);
		knownColors.put("GREEN", Color.GREEN);
		knownColors.put("LIGHT_GRAY", Color.LIGHT_GRAY);
		knownColors.put("MAGENTA", Color.MAGENTA);
		knownColors.put("ORANGE", Color.ORANGE);
		knownColors.put("RED", Color.RED);
		knownColors.put("PINK", Color.PINK);
		knownColors.put("YELLOW", Color.YELLOW);
		
		File file = new File(filePath);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document document = db.parse(file);
			NodeTraverser rootNode = new NodeTraverser(document.getChildNodes().item(0));
			automataName = rootNode.getChild("name").get(0).getText();
			setupNeighborType(rootNode.getChild("neighbor-type").get(0));
			setupStates(rootNode.getChild("states").get(0));
			setupTransitions(rootNode.getChild("transitions").get(0));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setupStates(NodeTraverser node) {
		ArrayList<NodeTraverser> states = node.getChild("state");
		for(int c = 0; c < states.size(); c++) {
			NodeTraverser stateNode = states.get(c);
			String stateName = stateNode.getChild("name").get(0).getText();
			String colorString = stateNode.getChild("color").get(0).getText();
			Color stateColor = knownColors.get(colorString.toUpperCase().trim());
			if(stateColor == null) {
				try {
					int r = Integer.parseInt(colorString.substring(0, colorString.indexOf(",")).trim());
					colorString = colorString.substring(colorString.indexOf(",")+1).trim();
					int g = Integer.parseInt(colorString.substring(0, colorString.indexOf(",")).trim());
					colorString = colorString.substring(colorString.indexOf(",")+1).trim();
					int b = Integer.parseInt(colorString.trim());
					stateColor = new Color(r, g, b);
				}
				catch(Exception e) {
					stateColor = Color.black;
					e.printStackTrace();
				}
			}
			State state = new State(stateName, stateColor);
			possibleStates.add(state);
		}
		if(!node.getChild("default-state").isEmpty()) {
			defaultState = node.getChild("default-state").get(0).getText();
		}
		if(defaultState.isBlank()) {
			defaultState = possibleStates.get(0).stateName;
		}
	}
	
	private void setupTransitions(NodeTraverser node) {
		ArrayList<NodeTraverser> transitions = node.getChild("transition");
		for(int c = 0; c < transitions.size(); c++) {
			NodeTraverser transitionNode = transitions.get(c);
			List<String> fromStates = Stream.of(transitionNode.getChild("state-from").get(0).getText().split(","))
					.map(s -> s.toLowerCase().trim())
					.collect(Collectors.toList());
			String toStateName = transitionNode.getChild("state-to").get(0).getText();
			ArrayList<NodeTraverser> conditions;
			if(!transitionNode.getChild("conditions").isEmpty()) {
				conditions = transitionNode.getChild("conditions").get(0).getChild("condition");
			} 
			else {
				conditions = new ArrayList<>();
			}

			Function<Automata, Boolean> transitionFunction = new Function<>() {
				@Override
				public Boolean apply(Automata a) {
					boolean conditionalsAllMet = true;
					if(fromStates.contains(a.currentState.stateName.toLowerCase())) {
						NeighborInformation neighborInfo = new NeighborInformation(a);
						for(int i = 0; i<conditions.size() && conditionalsAllMet; i++) {
							NodeTraverser condition = conditions.get(i);
							String[] statesToCheckForConditional = condition.getChild("state").get(0).textValue.split(",");
							String directionCheck = "";
							if(!condition.getChild("direction").isEmpty()) {
								directionCheck = condition.getChild("direction").get(0).textValue;
							}
							
							String quantityToCheckForConditionalString = "";
							if(!condition.getChild("quantity").isEmpty()) {
								quantityToCheckForConditionalString = condition.getChild("quantity").get(0).textValue;
							}
							
							if(!directionCheck.isBlank()) {
								conditionalsAllMet = conditionalsAllMet && neighborInfo.isNeighborInState(directionCheck, statesToCheckForConditional);
							}
							else if(!quantityToCheckForConditionalString.isBlank()) {
								boolean quantitySatisfied = false;
								for(String quantityAsString: quantityToCheckForConditionalString.split(",")) {
									try{
										Integer quantity = Integer.parseInt(quantityAsString.trim());
										quantitySatisfied = quantitySatisfied || neighborInfo.hasQuantityNeighborsInState(quantity, statesToCheckForConditional[0]);
									}
									catch(Exception e) {
										e.printStackTrace();
									}
								}
								conditionalsAllMet = conditionalsAllMet && quantitySatisfied;
							}
						}
					}
					else {
						conditionalsAllMet = false;
					}
					
					if(conditionalsAllMet) {
						a.setNextState(toStateName);
					}
					return conditionalsAllMet;
				}
				
			};
			
			stateTransitions.add(transitionFunction);
		}
	}
	
	
	private void setupNeighborType(NodeTraverser node) {
		String neighborTypeName = node.getText();
		if(neighborTypeName.equalsIgnoreCase("MOORE")) {
			neighborType = NeighborType.MOORE;
		}
		else {
			neighborType = NeighborType.VON_NEUMANN;
		}
	}
	
	private class NodeTraverser {
		private String name = "null";
		private String textValue = "null";
		private NodeList children = null;
		public NodeTraverser(Node node) {
			if(node != null) {
				name = node.getNodeName();
				textValue = node.getTextContent();
				children = node.getChildNodes();
			}
		}
		
		public String getText() {
			return new String(textValue);
		}
		
		public ArrayList<NodeTraverser> getChild(String name) {
			ArrayList<NodeTraverser> childrenWithName = new ArrayList<>();
			for(int c = 0; c<children.getLength(); c++) {
				Node possibleNode = children.item(c);
				if(name.equalsIgnoreCase(possibleNode.getNodeName())) {
					childrenWithName.add(new NodeTraverser(possibleNode));
				}
			}
			return childrenWithName;
		}
	}
	
	private class NeighborInformation{
		private String upState = "";
		private String downState = "";
		private String leftState = "";
		private String rightState = "";
		private String upLeftState = "";
		private String downLeftState = "";
		private String upRightState = "";
		private String downRightState = "";
		private final HashMap<String, Integer> neighborsInState = new HashMap<>();
		
		public NeighborInformation(Automata a) {
			
			for(State state: a.getStates()) {
				neighborsInState.put(state.stateName.toLowerCase(), 0);
			}
			for(Automata neighbor: a.neighbors) {
				String neighborState = neighbor.currentState.stateName.toLowerCase();
				neighborsInState.put(neighborState, neighborsInState.get(neighborState)+1);
				if(neighbor.pos.x == a.pos.x+1) { //to the right
					if(neighbor.pos.y == a.pos.y-1) { //above visually
						upRightState = neighborState;
					}
					else if(neighbor.pos.y == a.pos.y+1) { //below visually
						downRightState = neighborState;
					}
					else {
						rightState = neighborState;
					}
				}
				else if(neighbor.pos.x == a.pos.x-1) { //to the left
					if(neighbor.pos.y == a.pos.y-1) { //above visually
						upLeftState = neighborState;
					}
					else if(neighbor.pos.y == a.pos.y+1) { //below visually
						downLeftState = neighborState;
					}
					else {
						leftState = neighborState;
					}
				}
				else if(neighbor.pos.y == a.pos.y-1) { //above visually
					upState = neighborState;
				}
				else if(neighbor.pos.y == a.pos.y+1) { //below visually
					downState = neighborState;
				}
			}
		}
		
		public boolean isNeighborInState(String direction, String... state) {
			List<String> statesAllowed = Stream.of(state).map(s -> s.toLowerCase().trim()).collect(Collectors.toList());
			int counter = 0;
			for(String key: statesAllowed) {
				counter += neighborsInState.get(key);
			}
			if(counter == 0) {
				return false;
			}
			
			switch(direction.toLowerCase().trim()) {
				case "left": return statesAllowed.contains(leftState);
				case "right": return statesAllowed.contains(rightState);
				case "up": return statesAllowed.contains(upState);
				case "down": return statesAllowed.contains(downState);
				case "upleft": return statesAllowed.contains(upLeftState);
				case "upright": return statesAllowed.contains(upRightState);
				case "downleft": return statesAllowed.contains(downLeftState);
				case "downright": return statesAllowed.contains(downRightState);

				default: return false;
			}
		}
		
		public boolean hasQuantityNeighborsInState(Integer quantity, String state) {
			return neighborsInState.get(state.toLowerCase()) == quantity;
		}
	}
}
