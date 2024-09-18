package main.board;

import java.awt.Point;
import java.util.Random;

import main.automata.Automata;
import main.automata.AutomataFactory;
import main.automata.AutomataType;

public class Board {	
	Random rand = new Random();
	private Automata[][] cells;
	public int width; 
	public int height;
	public AutomataType currentType;
	public Board(int sizeInCells) {
		setDimensions(sizeInCells);
	}
	
	public void setDimensions(int sizeInCells) {
		this.width = sizeInCells;
		this.height = sizeInCells;
		cells = new Automata[height][width];
		loadCellsForBoard(currentType);
	}
	
	public void setSimulationType(AutomataType currentType) {
		this.currentType = currentType;
		loadCellsForBoard(currentType);
		
	}
	
	public void loadCellsForBoard(AutomataType type) {
		if(type != null) {
			for(int y = 0; y<height; y++) {
				for(int x = 0; x<width; x++) {
					cells[y][x] = AutomataFactory.getCellForAutomata(new Point(x, y), type);
				}
			}
			for(int x = 0; x<cells.length; x++) {
				for(int y = 0; y<cells[x].length; y++) {
					cells[x][y].loadNeighbors(this);
				}
			}
		}
		clear();
	}
	
	public Automata getCell(int x, int y) {
		if(x < 0 || y < 0) {
			return null;
		}
		if(x >= width || y >= height) {
			return null;
		}
		return cells[y][x];
	}
	
	public boolean setCell(int x, int y, String state) {
		Automata cell = getCell(x, y);
		if(cell != null) {
			cell.setCurrentState(state);
		}
		return true;
	}

	public boolean loadNextStates(){
		for(int y = 0; y<height; y++) {
			for(int x = 0; x<width; x++) {
				cells[y][x].calculateNextState();
			}
		}
		for(int y = 0; y<height; y++) {
			for(int x = 0; x<width; x++) {
				cells[y][x].updateStateToNextState();
			}
		}
		return true;
	}

	public void generateRandomSpread() {
		for(int y = 0; y<height; y++) {
			for(int x = 0; x<width; x++) {
				cells[y][x].setCurrentState(currentType.states.get(rand.nextInt(currentType.states.size())).stateName);
			}
		}
	}
	
	public void clear() {
		for(int y = 0; y<height; y++) {
			for(int x = 0; x<width; x++) {
				if(currentType != null) {
					cells[y][x].setCurrentState(currentType.defaultStateName);
				}
			}
		}
	}
}
