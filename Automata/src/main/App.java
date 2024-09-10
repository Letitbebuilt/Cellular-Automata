package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Point;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import main.automata.AutomataFactory;
import main.automata.AutomataFactory.AutomataTypes;
import main.display.CellDisplay;
import main.display.ControlDisplayLinkup;
import main.display.ZoomedCellDisplay;
import main.display.environmentControl.EnvironmentControlPanel;

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
		controlPanelWrapper.add(getVerticalSpacer(15));
		controlPanelWrapper.setMaximumSize(new Dimension(zoomDisplay.getPreferredSize().width, display.getPreferredSize().height));
		
		
		JComboBox<String> stateOptions = new JComboBox<String>();
		linkup.setSimulationType(AutomataFactory.AutomataTypes.CONWAY);
		stateOptions.removeAllItems();
		for(String stateName: AutomataFactory.AutomataTypes.CONWAY.getStateNames()) {
			stateOptions.addItem(stateName);
		}
		linkup.setDrawingStateType((String) stateOptions.getSelectedItem());
		
		
		
		EnvironmentControlPanel controlPanel = new EnvironmentControlPanel();
		controlPanel.setMaximumSize(new Dimension((int)zoomDisplay.getPreferredSize().getWidth(), 10000));
		controlPanelWrapper.add(controlPanel);
		LinkedHashMap<String, AutomataTypes> automataTypesMap = new LinkedHashMap<>();
		Stream.of(AutomataFactory.AutomataTypes.values()).forEach(e -> automataTypesMap.put(e.name, e));
		controlPanel.createDropdownControlOption("Type of automata: ", 
				(e -> {
					linkup.setSimulationType(e);
					stateOptions.removeAllItems();
					for(String stateName: e.getStateNames()) {
						stateOptions.addItem(stateName);
					}
				}),
				automataTypesMap);
		controlPanel.addVerticalSpacer(15);
		
		
		JPanel stateDrawingWrapper = new JPanel();
		controlPanel.add(stateDrawingWrapper);
		stateDrawingWrapper.setLayout(new BoxLayout(stateDrawingWrapper, BoxLayout.X_AXIS));
		Label stateDrawingLabel = new Label("Draw Cells in State: ");
		stateDrawingWrapper.add(stateDrawingLabel);
		applyLabelStyling(stateDrawingLabel, 12);
		stateDrawingWrapper.add(stateOptions);
		stateOptions.addActionListener(e ->{
			linkup.setDrawingStateType((String)((JComboBox<String>)e.getSource()).getSelectedItem());
		});
		controlPanel.addVerticalSpacer(15);
		
		LinkedHashMap<String, Integer> dimensionOptionHashMap = new LinkedHashMap<>();
		dimensionOptionHashMap.put("50x50", 50);
		dimensionOptionHashMap.put("100x100", 100);
		dimensionOptionHashMap.put("200x200", 200);
		dimensionOptionHashMap.put("400x400", 400);
		controlPanel.createRadioControlOption("Dimensions of simulation: ", 
				(e -> {
					linkup.adjustBoardDimensions(e);
				}),
				dimensionOptionHashMap);
		
		controlPanel.addVerticalSpacer(15);

		LinkedHashMap<String, Integer> cycleOptionHashMap = new LinkedHashMap<>();
		cycleOptionHashMap.put("1/s", 1);
		cycleOptionHashMap.put("5/s", 5);
		cycleOptionHashMap.put("10/s", 10);
		cycleOptionHashMap.put("20/s", 20);
		cycleOptionHashMap.put("30/s", 30);
		cycleOptionHashMap.put("60/s", 60);
		controlPanel.createRadioControlOption("Desired Cycles Per Second: ", 
				(e -> {
					linkup.setPreferredFPS(e);
				}),
				cycleOptionHashMap);

		controlPanel.addVerticalSpacer(15);
		
		
		
		JPanel playPauseButtonWrapper = new JPanel(new GridLayout(1, 5));
		playPauseButtonWrapper.setLayout(new BoxLayout(playPauseButtonWrapper, BoxLayout.X_AXIS));
		playPauseButtonWrapper.setMaximumSize(new Dimension((int)zoomDisplay.getPreferredSize().getWidth(), 100));
		Dimension buttonSize = new Dimension(50, 50);
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"play.png", ()->linkup.startSimulation(), buttonSize));
		playPauseButtonWrapper.add(getHorizontalSpacer(2));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"step.png", ()->linkup.stepForwardOneCycle(), buttonSize));
		playPauseButtonWrapper.add(getHorizontalSpacer(2));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"pause.png", ()->linkup.stopSimulation(), buttonSize));
		playPauseButtonWrapper.add(getHorizontalSpacer(2));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"rng-cells.png", ()->linkup.generateRandomBoard(), buttonSize));
		playPauseButtonWrapper.add(getHorizontalSpacer(2));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"empty-cells.png", ()->linkup.clearBoard(), buttonSize));
		controlPanelWrapper.add(playPauseButtonWrapper);
		
		controlPanelWrapper.add(getVerticalSpacer(45));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, 20, () -> "Status: "+(linkup.isRunning()?"Running":"Paused")));
		controlPanelWrapper.add(getVerticalSpacer(5));
		DecimalFormat sdf = new DecimalFormat("0.000");
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, 20, () -> "Cycles/Sec: "+(sdf.format(linkup.getCyclesPerSecond()))));
		controlPanelWrapper.add(getVerticalSpacer(10));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, 10, () -> "Application created by Samuel Vega"));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, 10, () -> "Distributed under CC BY-NC-SA 4.0."));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, 10, () -> "https://creativecommons.org/licenses/by-nc-sa/4.0/"));
		
		wrapper.add(display);
		wrapper.add(controlPanelWrapper);
		controlPanelWrapper.setBackground(Color.black);
        linkup.linkupZoomedCellDisplay(display, zoomDisplay);
        frame.getContentPane().add(wrapper, BorderLayout.CENTER);
	}
	
	private static JPanel getVerticalSpacer(int vertSpace) {
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(0, vertSpace));
		spacer.setBackground(Color.black);
		return spacer;
	}
	private static JPanel getHorizontalSpacer(int horiSpace) {
		JPanel spacer = new JPanel();
		spacer.setPreferredSize(new Dimension(horiSpace, 0));
		spacer.setBackground(Color.black);
		return spacer;
	}
	
	private static Label getLabelSpacerWithUpdatingText(ControlDisplayLinkup linkup, int fontSize, Supplier<String> text) {
		Label label = new Label();
		applyLabelStyling(label, fontSize);
		linkup.addUpdatingLabel(label, text);
		return label;
	}
	
	private static void applyLabelStyling(Label label, int fontHeight) {
		label.setBackground(Color.black);
		label.setForeground(Color.LIGHT_GRAY);
		label.setFont(new Font("Arial", Font.BOLD, fontHeight));
	}
}
