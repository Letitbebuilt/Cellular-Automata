package main.display.environmentControl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	public <T> void createDropdownControlOption(String controlName, Consumer<T> actionToPerform, List<String> displayNames, List<T> values) {
		
		JPanel dropdownControl = new JPanel();
		dropdownControl.setLayout(new BoxLayout(dropdownControl, BoxLayout.X_AXIS));
		JLabel label = new JLabel(controlName);
		dropdownControl.add(label);
		
		String[] displayNamesArray = new String[displayNames.size()];
		JComboBox<String> sizeOptions = new JComboBox<String>(displayNames.toArray(displayNamesArray));
		sizeOptions.setSelectedIndex(0);
		dropdownControl.add(sizeOptions);
		
		sizeOptions.addActionListener(e ->{
			actionToPerform.accept(values.get(((JComboBox<String>)e.getSource()).getSelectedIndex()));
		});
		
		this.add(dropdownControl);
		applyGeneralStyling(dropdownControl);
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
	
	private void applyGeneralStyling(JComponent component) {
		component.setBackground(Color.black);
		component.setForeground(Color.LIGHT_GRAY);
	}
}
