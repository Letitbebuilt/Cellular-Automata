package main.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import main.automata.Automata;
import main.board.Board;

public class ZoomedCellDisplay extends JPanel{
	private static final long serialVersionUID = 8992998847635770948L;
	Board board;
	Point centerCellTarget = new Point(-1, -1);
	private int[] allowedScales = {10, 15, 25, 30, 50};
	private int currentScale = 2;
	private int dispWidth = 300;
	private int dispHeight = 300;

	public ZoomedCellDisplay() {
		super();
		this.setPreferredSize(new Dimension(dispWidth, dispHeight));
	}
	
	public int getRadius() {
		return (dispWidth / allowedScales[currentScale]) / 2 ;
	}
	
	public void adjustScale(int mod) {
		currentScale = Math.min(allowedScales.length-1, Math.max(0, currentScale + mod));
	}
	public int getDisplayWidth() {
		return dispWidth;
	}
	
	public int getDisplayHeight() {
		return dispHeight;
	} 
	
	public void setCenterCellTarget(Point p) {
		if(p != null) {
			centerCellTarget = p;
		}
	}

	public void setBoard(Board board) {
		this.board = board;
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		graphics.setColor(Color.BLACK);
		graphics.fillRect(0, 0, this.getWidth(), this.getHeight());
		if(board.getCell(centerCellTarget.x, centerCellTarget.y) == null) {
			graphics.setColor(Color.DARK_GRAY);
			graphics.fillRect(0, 0, dispWidth, dispHeight);
		}
		else {
			int scale = allowedScales[currentScale];
			for(int x = centerCellTarget.x-getRadius(), i = 0; x <= centerCellTarget.x+getRadius()-1; x++, i++) {
				for(int y = centerCellTarget.y-getRadius(), j = 0; y <= centerCellTarget.y+getRadius()-1; y++, j++) {
					Automata cell = board.getCell(x, y);
					if(cell == null) {
						graphics.setColor(Color.DARK_GRAY);
						graphics.fillRect(i*scale, j*scale, scale, scale);
					}
					else {
						graphics.setColor(Color.LIGHT_GRAY);
						graphics.setColor(board.getCell(x, y).currentState.getColor());
						graphics.drawRect(i*scale, j*scale, scale-1, scale-1);
						graphics.drawLine(i*scale, j*scale, i*scale+scale-1, j*scale+scale-1);
						graphics.drawLine(i*scale, j*scale+scale-1, i*scale+scale-1, j*scale);
					}
				}
			}
			graphics.setColor(Color.RED);
			graphics.drawRect(getRadius()*scale, getRadius()*scale, scale, scale);
			
			graphics.setColor(Color.gray);
			graphics.drawRect(0, 0, dispWidth-1, dispHeight-1);
		}
	}
}
