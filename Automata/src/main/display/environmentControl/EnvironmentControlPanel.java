package main.display.environmentControl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import main.display.ControlDisplayLinkup;

public class EnvironmentControlPanel extends JPanel{

	private static final long serialVersionUID = -8660163481777461110L;

	protected ControlDisplayLinkup linkup;
	
	public EnvironmentControlPanel() {
		super();
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		applyGeneralStyling(this);
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> void createDropdownControlOption(String controlName, Consumer<T> actionToPerform, HashMap<String, T> displayValueMap) {
		
		JPanel dropdownControl = new JPanel();
		dropdownControl.setLayout(new BoxLayout(dropdownControl, BoxLayout.X_AXIS));
		JLabel label = new JLabel(controlName);
		dropdownControl.add(label);
		
		ArrayList<String>displayNames = new ArrayList<>(displayValueMap.keySet());
		String[] displayNamesArray = new String[displayNames.size()];
		JComboBox<String> sizeOptions = new JComboBox<String>(displayNames.toArray(displayNamesArray));
		sizeOptions.setSelectedIndex(0);
		dropdownControl.add(sizeOptions);
		
		
		sizeOptions.addActionListener(e -> {
			int index = ((JComboBox<String>)e.getSource()).getSelectedIndex();
			actionToPerform.accept(displayValueMap.get(displayNamesArray[index]));
		});
		this.add(dropdownControl);
		applyGeneralStyling(dropdownControl);
		applyLabelStyling(label, 12);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void createRadioControlOption(String controlName, Consumer<T> actionToPerform, HashMap<String, T> displayValueMap, int initialSelected) {
		
		JPanel radioControl = new JPanel();
		radioControl.setLayout(new BoxLayout(radioControl, BoxLayout.Y_AXIS));
		JPanel labelFloatPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		JLabel label = new JLabel(controlName);
		labelFloatPanel.add(label);
		radioControl.add(labelFloatPanel);
		ButtonGroup radioButtonGroup = new ButtonGroup();
		JPanel radioButtonsPanel = new JPanel();
		radioButtonsPanel.setLayout(new GridLayout(2, 3));
		radioControl.add(radioButtonsPanel);
		ArrayList<String> names = new ArrayList<>(displayValueMap.keySet());
		for(int i = 0; i<names.size(); i++) {
			String name = names.get(i);
			JPanel labelButtonPair = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			JLabel radioLabel = new JLabel(name);
			this.applyLabelStyling(radioLabel, 12);
			labelButtonPair.add(radioLabel);
			JRadioButton radioButton = new JRadioButton();
			
			radioButton.addActionListener(e -> actionToPerform.accept(displayValueMap.get(name)));
			radioButton.setSelected(i == initialSelected);
			labelButtonPair.add(radioButton);
			radioButtonGroup.add(radioButton);
			radioButtonsPanel.add(labelButtonPair);
			applyGeneralStyling(labelButtonPair, radioButton);
		}
		
		this.add(radioControl);
		applyGeneralStyling(radioControl, radioButtonsPanel, labelFloatPanel);
		applyLabelStyling(label, 12);
	}
	
	public void addVerticalSpacer(int space) {
		JPanel spacer = new JPanel();
		applyGeneralStyling(spacer);
		spacer.setPreferredSize(new Dimension(this.getPreferredSize().width, space));
		this.add(spacer);
	}
	
	private void applyLabelStyling(JLabel label, int fontHeight) {
		label.setBackground(Color.black);
		label.setForeground(Color.LIGHT_GRAY);
		label.setFont(new Font("Arial", Font.BOLD, fontHeight));
	}
	
	private void applyGeneralStyling(JComponent... components) {
		for(JComponent component: components) {
			component.setBackground(Color.black);
			component.setForeground(Color.LIGHT_GRAY);
		}
	}
}
