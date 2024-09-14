package main.automata;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
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
	public List<Function<Automata, Boolean>> stateTransitions = new ArrayList<>();
	public NeighborType neighborType = NeighborType.MOORE;
	HashMap<String, Consumer<Node>> nodeParserMap = new HashMap<>();;
	HashMap<String, Color> knownColors = new HashMap<>();;
	
	public AutomataRules(String filePath) {
		nodeParserMap.put("name", e -> automataName = e.getTextContent());
		nodeParserMap.put("neighbor-type", e -> setupNeighborType(e));
		nodeParserMap.put("states", e -> setupStates(e));
		nodeParserMap.put("transitions", e -> setupTransitions(e));
		
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
			NodeList nodes = document.getChildNodes().item(0).getChildNodes();
			for(int i = 0; i<nodes.getLength(); i++) {
				Node node = nodes.item(i);
				Consumer<Node> cons = nodeParserMap.get(node.getNodeName());
				if(cons != null) {
					cons.accept(node);
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void setupStates(Node node) {
		NodeList stateNodes = node.getChildNodes();
		for(int c = 0; c < stateNodes.getLength(); c++) {
			Node stateNode = stateNodes.item(c);
			String stateName = "";
			Color stateColor = Color.black;
			for(int i = 0; i<stateNode.getChildNodes().getLength(); i++) {
				Node childNode = stateNode.getChildNodes().item(i);
				if(childNode.getNodeName().equalsIgnoreCase("name")) {
					stateName = childNode.getTextContent();
				}
				if(childNode.getNodeName().equalsIgnoreCase("color")) {
					String colorString = childNode.getTextContent();
					stateColor = knownColors.get(colorString.toUpperCase().trim());
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
						}
					}
				}
			}
			State state = new State(stateName, stateColor);
			possibleStates.add(state);
		}
		
	}
	
	private void setupTransitions(Node node) {
		
	}
	
	
	private void setupNeighborType(Node node) {
		String neighborTypeName = node.getTextContent();
		if(neighborTypeName.equalsIgnoreCase("MOORE")) {
			neighborType = NeighborType.MOORE;
		}
		else {
			neighborType = NeighborType.VON_NEUMANN;
		}
	}
}
