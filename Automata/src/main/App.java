package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Label;
import java.awt.Point;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.function.Supplier;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main.automata.AutomataFactory;
import main.display.CellDisplay;
import main.display.ControlDisplayLinkup;
import main.display.ZoomedCellDisplay;

public class App {
	final static String IMG_PATH = "resources/";
	private static void createAndShowGUI() {
		//Create and set up the window.
        JFrame frame = new JFrame("Cellular Automata Simulator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setupDisplay(frame);
    	

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //Display the window.
        frame.pack();
        frame.setLocation(new Point(screenSize.width/2-frame.getWidth()/2, screenSize.height/2-frame.getHeight()/2));
        frame.setResizable(false);
        frame.setIconImage(new ImageIcon(IMG_PATH+"logo.png").getImage());
        frame.setVisible(true);
	}
	
	public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
	
	@SuppressWarnings({"unchecked"})
	private static void setupDisplay(JFrame frame) {
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        CellDisplay display = new CellDisplay();
        ZoomedCellDisplay zoomDisplay = new ZoomedCellDisplay();
        ControlDisplayLinkup linkup = new ControlDisplayLinkup(display);

		JPanel controlPanelWrapper = new JPanel();
		controlPanelWrapper.setLayout(new BoxLayout(controlPanelWrapper, BoxLayout.Y_AXIS));
		controlPanelWrapper.add(zoomDisplay);
		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 15)));
		
		
		
		JComboBox<String> stateOptions = new JComboBox<String>();
		linkup.setSimulationType(AutomataFactory.AutomataTypes.CONWAY);
		stateOptions.removeAllItems();
		for(String stateName: AutomataFactory.AutomataTypes.CONWAY.getStateNames()) {
			stateOptions.addItem(stateName);
		}
		linkup.setDrawingStateType((String) stateOptions.getSelectedItem());
		
		
		JPanel simTypeControlWrapper = new JPanel();
		simTypeControlWrapper.setLayout(new BoxLayout(simTypeControlWrapper, BoxLayout.X_AXIS));
		Label typeLabel = new Label("Type of automata: ");
		simTypeControlWrapper.add(typeLabel);
		applyLabelStyling(typeLabel, 12);
		String[] typeArray = {"Conway's Game of Life", "Brian's Brain", "WireWorld", "Langton's Ant", "Day and Night"};
		JComboBox<String> typeOptions = new JComboBox<String>(typeArray);
		simTypeControlWrapper.add(typeOptions);
		applyDropdownStyling(typeOptions);
		typeOptions.addActionListener(e ->{
			@SuppressWarnings("unchecked")
			int optionSelected = ((JComboBox<String>)e.getSource()).getSelectedIndex();
			AutomataFactory.AutomataTypes type = AutomataFactory.AutomataTypes.CONWAY;
			switch(optionSelected) {
				case 0: type = AutomataFactory.AutomataTypes.CONWAY;
					break;
				case 1: type = AutomataFactory.AutomataTypes.BRIANS_BRAIN;
					break;
				case 2: type = AutomataFactory.AutomataTypes.WIRE_WORLD;
					break;
				case 3: type = AutomataFactory.AutomataTypes.LANGTON_ANT;
					break;
				case 4: type = AutomataFactory.AutomataTypes.DAY_AND_NIGHT;
					break;
			}
			
			linkup.setSimulationType(type);
			stateOptions.removeAllItems();
			for(String stateName: type.getStateNames()) {
				stateOptions.addItem(stateName);
			}
		});
		controlPanelWrapper.add(simTypeControlWrapper);
		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 15)));
		
		
		
		JPanel stateDrawingWrapper = new JPanel();
		stateDrawingWrapper.setLayout(new BoxLayout(stateDrawingWrapper, BoxLayout.X_AXIS));
		Label stateDrawingLabel = new Label("Draw Cells in State: ");
		stateDrawingWrapper.add(stateDrawingLabel);
		applyLabelStyling(stateDrawingLabel, 12);
		stateDrawingWrapper.add(stateOptions);
		stateOptions.addActionListener(e ->{
			linkup.setDrawingStateType((String)((JComboBox<String>)e.getSource()).getSelectedItem());
		});
		controlPanelWrapper.add(stateDrawingWrapper);
		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 15)));
		
		
		
		JPanel simSizeControlWrapper = new JPanel();
		simSizeControlWrapper.setLayout(new BoxLayout(simSizeControlWrapper, BoxLayout.X_AXIS));
		Label sizeControlLabel = new Label("Dimensions of simulation: ");
		simSizeControlWrapper.add(sizeControlLabel);
		applyLabelStyling(sizeControlLabel, 12);

		String[] sizeOptArray = {"50 x 50", "100 x 100", "200 x 200", "400 x 400"};
		JComboBox<String> sizeOptions = new JComboBox<String>(sizeOptArray);
		sizeOptions.setSelectedIndex(1);
		simSizeControlWrapper.add(sizeOptions);
		sizeOptions.addActionListener(e ->{
			@SuppressWarnings("unchecked")
			int optionSelected = ((JComboBox<String>)e.getSource()).getSelectedIndex();
			switch(optionSelected) {
				case 0: linkup.adjustBoardDimensions(50);
					break;
				case 1: linkup.adjustBoardDimensions(100);
					break;
				case 2: linkup.adjustBoardDimensions(200);
					break;
				case 3: linkup.adjustBoardDimensions(400);
					break;
			}
		});
		controlPanelWrapper.add(simSizeControlWrapper);
		controlPanelWrapper.setBackground(Color.BLACK);
		
		
		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 15)));
		
		JPanel playPauseButtonWrapper = new JPanel();
		playPauseButtonWrapper.setLayout(new BoxLayout(playPauseButtonWrapper, BoxLayout.X_AXIS));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"play.png", ()->linkup.startSimulation(), new Dimension(90, 90)));
		playPauseButtonWrapper.add(getJPanelSpacer(new Dimension(15, 90)));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"step.png", ()->linkup.stepForwardOneCycle(), new Dimension(90, 90)));
		playPauseButtonWrapper.add(getJPanelSpacer(new Dimension(15, 90)));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"pause.png", ()->linkup.stopSimulation(), new Dimension(90, 90)));
		controlPanelWrapper.add(playPauseButtonWrapper);

		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 30)));
		
		JPanel boardResetButtonWrapper= new JPanel();
		boardResetButtonWrapper.setLayout(new BoxLayout(boardResetButtonWrapper, BoxLayout.X_AXIS));
		boardResetButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"rng-cells.png", ()->linkup.generateRandomBoard(), new Dimension(90, 90)));
		boardResetButtonWrapper.add(getJPanelSpacer(new Dimension(15, 90)));
		boardResetButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"empty-cells.png", ()->linkup.clearBoard(), new Dimension(90, 90)));
		boardResetButtonWrapper.add(getJPanelSpacer(new Dimension(15, 90)));
		boardResetButtonWrapper.add(getJPanelSpacer(new Dimension(90, 90)));
		controlPanelWrapper.add(boardResetButtonWrapper);

		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 30)));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, new Dimension(300, 20), () -> "Status: "+(linkup.isRunning()?"Running":"Paused")));
		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 5)));
		DecimalFormat sdf = new DecimalFormat("0.000");
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, new Dimension(300, 20), () -> "Cycles/Sec: "+(sdf.format(linkup.getCyclesPerSecond()))));
		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 45)));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, new Dimension(300, 10), () -> "Application created by Samuel Vega"));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, new Dimension(300, 10), () -> "Distributed under CC BY-NC-SA 4.0."));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, new Dimension(300, 10), () -> "https://creativecommons.org/licenses/by-nc-sa/4.0/"));
		
		wrapper.add(display);
		wrapper.add(controlPanelWrapper);
        linkup.linkupZoomedCellDisplay(display, zoomDisplay);
        frame.getContentPane().add(wrapper, BorderLayout.CENTER);
	}
	
	private static JPanel getJPanelSpacer(Dimension d) {
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(d);
		spacer.setBackground(Color.black);
		return spacer;
	}
	
	private static Label getLabelSpacerWithUpdatingText(ControlDisplayLinkup linkup, Dimension d, Supplier<String> text) {
		Label label = new Label();
		label.setPreferredSize(d);
		applyLabelStyling(label, d.height);
		linkup.addUpdatingLabel(label, text);
		return label;
	}
	
	private static void applyLabelStyling(Label label, int fontHeight) {
		label.setBackground(Color.black);
		label.setForeground(Color.LIGHT_GRAY);
		label.setFont(new Font("Arial", Font.BOLD, fontHeight));
	}
	
	private static void applyDropdownStyling(JComboBox<?> box) {
		
	}
}
