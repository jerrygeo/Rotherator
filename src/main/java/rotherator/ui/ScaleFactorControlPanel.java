package rotherator.ui;

import javax.swing.*;
import java.awt.*;

public class ScaleFactorControlPanel extends JPanel {

    private JTextField scaleFactorField;
    private JButton recomputeButton;

    private float previousScaleFactor = 1.0f;
    private float scaleFactor = 1.0f;

    public ScaleFactorControlPanel() {
        super();

        scaleFactorField = new JTextField("1.0");
        recomputeButton = new JButton("Apply New Scale Factor");

        setLayout(new FlowLayout());
        scaleFactorField.setColumns(5);
        add(new JLabel("Spending Scale Factor:"));
        add(scaleFactorField);
        add(recomputeButton);

        setSize(0, 80);

        recomputeButton.addActionListener(action -> {
            float oldValue = previousScaleFactor;

            try {
                scaleFactor = Float.parseFloat(scaleFactorField.getText());
            } catch (NumberFormatException e) {
                scaleFactorField.setText(Float.toString(oldValue));
            }

            firePropertyChange("scaleFactor", previousScaleFactor, scaleFactor);
            //dispatchEvent(new ActionEvent(this, ActionEvent.ACTION_FIRST, "RECOMPUTE" ));
        });
    }

}
