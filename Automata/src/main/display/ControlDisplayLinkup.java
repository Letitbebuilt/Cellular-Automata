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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Supplier;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import main.automata.AutomataFactory.AutomataTypes;
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
	AutomataTypes type;
	
	public ControlDisplayLinkup(CellDisplay display) {
		board = new Board(100);
		board.clear();
		display.setBoard(board);
		refreshPanels.add(display);
        timer = new Timer(1, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				deltaMillis += Math.max(0, System.currentTimeMillis() - oldTime);
				oldTime = System.currentTimeMillis();
				while(deltaMillis > (1000/desiredFPS)) {
					board.loadNextStates();
					deltaMillis -= (1000/desiredFPS);
				}
				repaintObjects();
			}
        	
        });
        timer.setRepeats(true);
	}
	
	public void linkupZoomedCellDisplay(final CellDisplay cellDisplay, final ZoomedCellDisplay zoomedDisplay) {
		refreshPanels.removeIf(p -> p instanceof ZoomedCellDisplay);
		refreshPanels.add(zoomedDisplay);
		zoomedDisplay.setBoard(board);
		
		cellDisplay.addMouseMotionListener(new MouseMotionListener() {
			public void mouseDragged(MouseEvent e) {
				Point cellTarget = new Point(e.getPoint().x/cellDisplay.scale, e.getPoint().y/cellDisplay.scale);
				board.setCell(cellTarget.x, cellTarget.y, SwingUtilities.isLeftMouseButton(e)?selectedStateToDraw: type.defaultStateName);
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
				board.setCell(cellTarget.x, cellTarget.y, SwingUtilities.isLeftMouseButton(e)?selectedStateToDraw: type.defaultStateName);
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

	public void setSimulationType(AutomataTypes type) {
		this.type = type;
		board.setSimulationType(type);
		repaintObjects();
	}

	public void setDrawingStateType(String selectedStateToDraw) {
		this.selectedStateToDraw = selectedStateToDraw;
	}
}
