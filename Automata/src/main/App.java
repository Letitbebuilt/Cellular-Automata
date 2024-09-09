package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Point;
import java.awt.Toolkit;
import java.text.DecimalFormat;
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
		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 15)));
		controlPanelWrapper.setMaximumSize(new Dimension(zoomDisplay.getPreferredSize().width, display.getPreferredSize().height));
		
		
		JComboBox<String> stateOptions = new JComboBox<String>();
		linkup.setSimulationType(AutomataFactory.AutomataTypes.CONWAY);
		stateOptions.removeAllItems();
		for(String stateName: AutomataFactory.AutomataTypes.CONWAY.getStateNames()) {
			stateOptions.addItem(stateName);
		}
		linkup.setDrawingStateType((String) stateOptions.getSelectedItem());
		
		
		
		EnvironmentControlPanel controlPanel = new EnvironmentControlPanel();
		controlPanelWrapper.add(controlPanel);
		
		controlPanel.createDropdownControlOption("Type of automata: ", 
				(e -> {
					linkup.setSimulationType(e);
					stateOptions.removeAllItems();
					for(String stateName: e.getStateNames()) {
						stateOptions.addItem(stateName);
					}
				}),
				Stream.of(AutomataFactory.AutomataTypes.values()).map(e -> e.name).collect(Collectors.toList()),
				Stream.of(AutomataFactory.AutomataTypes.values()).collect(Collectors.toList()));
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
		
		controlPanel.createDropdownControlOption("Dimensions of simulation: ", 
				(e -> {
					linkup.adjustBoardDimensions(e);
				}),
				List.of("50 x 50", "100 x 100", "200 x 200", "400 x 400"),
				List.of(50, 100, 200, 400));
		
		controlPanel.addVerticalSpacer(15);

		controlPanel.createDropdownControlOption("Desired Cycles Per Second: ", 
				(e -> {
					linkup.setPreferredFPS(e);
				}),
				List.of("1/s", "5/s", "10/s", "20/s", "30/s", "60/s"),
				List.of(1, 5, 10, 20, 30, 60));

		controlPanel.addVerticalSpacer(15);
		
		
		
		JPanel playPauseButtonWrapper = new JPanel(new GridLayout(1, 5));
		playPauseButtonWrapper.setLayout(new BoxLayout(playPauseButtonWrapper, BoxLayout.X_AXIS));
		Dimension buttonSize = new Dimension(50, 50);
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"play.png", ()->linkup.startSimulation(), buttonSize));
		playPauseButtonWrapper.add(getJPanelSpacer(new Dimension(10, buttonSize.height)));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"step.png", ()->linkup.stepForwardOneCycle(), buttonSize));
		playPauseButtonWrapper.add(getJPanelSpacer(new Dimension(10, buttonSize.height)));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"pause.png", ()->linkup.stopSimulation(), buttonSize));
		playPauseButtonWrapper.add(getJPanelSpacer(new Dimension(10, buttonSize.height)));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"rng-cells.png", ()->linkup.generateRandomBoard(), buttonSize));
		playPauseButtonWrapper.add(getJPanelSpacer(new Dimension(10, buttonSize.height)));
		playPauseButtonWrapper.add(linkup.getIconButtonForTask(IMG_PATH+"empty-cells.png", ()->linkup.clearBoard(), buttonSize));
		controlPanelWrapper.add(playPauseButtonWrapper);
		
		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 45)));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, new Dimension(300, 20), () -> "Status: "+(linkup.isRunning()?"Running":"Paused")));
		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 5)));
		DecimalFormat sdf = new DecimalFormat("0.000");
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, new Dimension(300, 20), () -> "Cycles/Sec: "+(sdf.format(linkup.getCyclesPerSecond()))));
		controlPanelWrapper.add(getJPanelSpacer(new Dimension(300, 10)));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, new Dimension(300, 10), () -> "Application created by Samuel Vega"));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, new Dimension(300, 10), () -> "Distributed under CC BY-NC-SA 4.0."));
		controlPanelWrapper.add(getLabelSpacerWithUpdatingText(linkup, new Dimension(300, 10), () -> "https://creativecommons.org/licenses/by-nc-sa/4.0/"));
		
		wrapper.add(display);
		wrapper.add(controlPanelWrapper);
		controlPanelWrapper.setBackground(Color.black);
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
}
