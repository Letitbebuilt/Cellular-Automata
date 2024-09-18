package main.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import main.automata.Automata;
import main.automata.Automata.NeighborType;
import main.automata.AutomataRules;
import main.automata.AutomataType;
import main.automata.State;
import main.board.Board;

public class ControlDisplayLinkup {
	private Board board;
	Timer timer;
	long oldTime;
	long deltaMillis;
	int desiredFPS = 20;
	int counter = 0;
	ArrayList<JPanel> refreshPanels = new ArrayList<>();
	HashMap<Label, Supplier<String>> textRefreshPanels = new HashMap<>();
	String selectedStateToDraw;
	ArrayList<AutomataType> types = new ArrayList<>();

	AutomataType currentType;
	
	public ControlDisplayLinkup(CellDisplay display) {
		board = new Board(100);
		display.setBoard(board);
		refreshPanels.add(display);
        timer = new Timer(1, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deltaMillis += Math.max(0, System.currentTimeMillis() - oldTime);
				oldTime = System.currentTimeMillis();
				if(deltaMillis > (1000/desiredFPS)) {
					board.loadNextStates();
					deltaMillis -= (1000/desiredFPS);
				}
				repaintObjects();
			}
        	
        });
        timer.setRepeats(true);
        loadAutomataTypes();
	}

	
	
	public void linkupZoomedCellDisplay(final CellDisplay cellDisplay, final ZoomedCellDisplay zoomedDisplay) {
		refreshPanels.removeIf(p -> p instanceof ZoomedCellDisplay);
		refreshPanels.add(zoomedDisplay);
		zoomedDisplay.setBoard(board);
		
		cellDisplay.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				Point cellTarget = new Point(e.getPoint().x/cellDisplay.scale, e.getPoint().y/cellDisplay.scale);
				board.setCell(cellTarget.x, cellTarget.y, SwingUtilities.isLeftMouseButton(e)?selectedStateToDraw: currentType.defaultStateName);
				setZoomBox(cellDisplay, zoomedDisplay, cellTarget);
				repaintObjects();
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				Point cellTarget = new Point(e.getPoint().x/cellDisplay.scale, e.getPoint().y/cellDisplay.scale);
				setZoomBox(cellDisplay, zoomedDisplay, cellTarget);				
			}
		});
		
		cellDisplay.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				
			}
			public void mousePressed(MouseEvent e) {
				Point cellTarget = new Point(e.getPoint().x/cellDisplay.scale, e.getPoint().y/cellDisplay.scale);
				board.setCell(cellTarget.x, cellTarget.y, SwingUtilities.isLeftMouseButton(e)?selectedStateToDraw: currentType.defaultStateName);
				repaintObjects();
			}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			
			public void mouseExited(MouseEvent e) {
				zoomedDisplay.setCenterCellTarget(new Point(-1, -1));
				cellDisplay.clearMouseZoomBox();
			}
			
		});
		
		cellDisplay.addMouseWheelListener(new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent e) {
				zoomedDisplay.adjustScale(-e.getWheelRotation());
				Point cellTarget = new Point(e.getPoint().x/cellDisplay.scale, e.getPoint().y/cellDisplay.scale);
				cellDisplay.setMouseZoomBox(new Point(cellTarget.x - zoomedDisplay.getRadius(), cellTarget.y - zoomedDisplay.getRadius()),
						new Point(cellTarget.x + zoomedDisplay.getRadius(), cellTarget.y + zoomedDisplay.getRadius()));
				repaintObjects();
			}
		});
	}
	
	public void addUpdatingLabel(Label label, Supplier<String> text) {
		textRefreshPanels.put(label, text);

		repaintObjects();
	}
	
	public boolean startSimulation() {
		repaintObjects();
		deltaMillis = 0;
		oldTime = System.currentTimeMillis();
		timer.start();
		return true;
	}
	
	public boolean setPreferredFPS(int cps) {
		repaintObjects();
		this.desiredFPS = Math.min(100, Math.max(1, cps));
		deltaMillis = 0;
		return true;
	}
	
	public boolean stopSimulation() {
		repaintObjects();
		timer.stop();
		return true;
	}
	
	public boolean stepForwardOneCycle() {
		board.loadNextStates();
		repaintObjects();
		return true;
	}
	
	public boolean adjustBoardDimensions(int newDimensions) {
		board.setDimensions(newDimensions);
		repaintObjects();
		return true;
	}
	
	public boolean generateRandomBoard() {
		board.generateRandomSpread();
		repaintObjects();
		return true;
	}
	
	public boolean clearBoard() {
		board.clear();
		repaintObjects();
		return true;
	}
	
	public int getNumberOfCellsAlive() {
		return 0;
	}
	
	public boolean isRunning() {
		return timer.isRunning();
	}
	
	
	public JButton getIconButtonForTask(String imgSrc, Supplier<Boolean> supplier, Dimension dimensions) {
		JButton button = new JButton();
		button.setBackground(Color.BLACK);		
		button.addMouseListener(new MouseListener() {
			@Override
			public void mousePressed(MouseEvent e) {
				supplier.get();
				repaintObjects();
			}
			public void mouseClicked(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			
		});
		button.setPreferredSize(dimensions);
		ImageIcon icon = new ImageIcon(imgSrc);
		icon.setImage(icon.getImage().getScaledInstance(dimensions.width, dimensions.height, Image.SCALE_DEFAULT));
		button.setIcon(icon);
		return button;
	}
	
	
	private void setZoomBox(final CellDisplay cellDisplay, final ZoomedCellDisplay zoomedDisplay,
			Point cellTarget) {
		zoomedDisplay.setCenterCellTarget(cellTarget);
		cellDisplay.setMouseZoomBox(new Point(cellTarget.x - zoomedDisplay.getRadius(), cellTarget.y - zoomedDisplay.getRadius()),
				new Point(cellTarget.x + zoomedDisplay.getRadius(), cellTarget.y + zoomedDisplay.getRadius()));
		repaintObjects();
	}
	
	private void repaintObjects() {
		for(JPanel panel: refreshPanels) {
			panel.repaint();
		}
		for(Label key: textRefreshPanels.keySet()) {
			key.setText(textRefreshPanels.get(key).get());
		}
	}

	public void setSimulationType(AutomataType type) {
		this.currentType = type;
		board.setSimulationType(type);
		repaintObjects();
	}
	
	public AutomataType getCurrentSimulationType() {
		return currentType;
	}
	
	public ArrayList<AutomataType> getSimulationTypes() {
		return types;
	}

	public void setDrawingStateType(String selectedStateToDraw) {
		this.selectedStateToDraw = selectedStateToDraw;
	}
	
	
	
	private void loadAutomataTypes() {
		//manually load as a default example even if XML gets deleted
		types.add(new AutomataType("Conway's Game of Life",
				List.of(new State("Alive", Color.LIGHT_GRAY), new State("Dead", Color.BLACK)), 
				List.of(new Function<>() {
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
			}), "Dead", NeighborType.MOORE));
		
        //manually loaded for speed: xml instruction processing currently a bit too slow for an automata this complex representing as a state machine. 
        types.add(new AutomataType("Langton's Ant",
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
		}), "Black", NeighborType.VON_NEUMANN));
        
        File automataDirectory = new File("resources/automata/");
        if(automataDirectory.isDirectory()) {
        	for(File xml: automataDirectory.listFiles()) {
        		if(xml.getName().trim().endsWith(".xml")) {
        			types.add(new AutomataType(new AutomataRules(xml.getAbsolutePath())));
        		}
        	}
        }
        setSimulationType(types.get(0));
	}
}
