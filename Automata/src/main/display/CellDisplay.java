package main.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import javax.swing.JPanel;

import main.board.Board;

public class CellDisplay extends JPanel{
	private static final long serialVersionUID = 6648765169132422473L;
	public Dimension dimensions = new Dimension(800, 800);
	public int scale;
	private Board board;
	
	Point mouseZoomUpperLeft;
	Point mouseZoomBottomRight;
	public CellDisplay() {
		super();
		this.setPreferredSize(dimensions);
	}
	
	public void setMouseZoomBox(Point upperLeft, Point bottomRight) {
		mouseZoomUpperLeft = upperLeft;
		mouseZoomBottomRight = bottomRight;
	}
	
	public void clearMouseZoomBox() {
		mouseZoomUpperLeft = null;
		mouseZoomBottomRight = null;
	}
	
	public void setBoard(Board board) {
		this.board = board;
	}
	
	@Override
	public void paintComponent(Graphics graphics) {
		this.scale = dimensions.width / board.width;
		
		graphics.setColor(Color.black);
		graphics.fillRect(0, 0, (int) dimensions.getWidth(), (int) dimensions.getHeight());
		for(int x = 0; x < board.width; x++) {
			for(int y = 0; y < board.height; y++) {
				graphics.setColor(Color.LIGHT_GRAY);
				graphics.setColor(board.getCell(x, y).currentState.getColor());
				
				graphics.drawRect(x*scale, y*scale, scale-1, scale-1);
				graphics.drawLine(x*scale, y*scale, x*scale+scale-1, y*scale+scale-1);
				graphics.drawLine(x*scale, y*scale+scale-1, x*scale+scale-1, y*scale);
				
			}
		}
		if(mouseZoomUpperLeft != null && mouseZoomBottomRight != null) {
			graphics.setColor(Color.BLUE);
			graphics.drawRect(mouseZoomUpperLeft.x*scale, 
					mouseZoomUpperLeft.y*scale, 
					(mouseZoomBottomRight.x - mouseZoomUpperLeft.x)*scale, 
					(mouseZoomBottomRight.y - mouseZoomUpperLeft.y)*scale);
		}
	}
	
	
}
