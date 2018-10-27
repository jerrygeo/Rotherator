package rotherator.ui;

import rotherator.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Vector;
import java.util.function.Consumer;

public class AssetsSnapshotControlPanel extends JPanel {

    public static class ScenarioEvent {
        public final String yearSelector;
        public final float expenseScaleFactor;

        public ScenarioEvent(String yearSelector, float expenseScaleFactor) {
            this.yearSelector = yearSelector;
            this.expenseScaleFactor = expenseScaleFactor;
        }
    }


    private JComboBox<String> yearSelector;
    private JTextField scaleFactorField;
    private JButton recomputeButton;
    private JLabel firstYearLabel;
    private JLabel yearSuffix = new JLabel(" Years");
    private String blankLine = "                 ";

    private JCheckBox taxOption;

    private Consumer<ScenarioEvent> onChanged;

    public AssetsSnapshotControlPanel(Integer firstYear, String[] years) {


        ArrayList<String> yearStrings = new ArrayList<>();
        for (int i=0; i<years.length; i++) yearStrings.add(years[i]);
//       for (int i=0; i<years.length; i++) yearStrings.add(years[i]);

        yearSelector = new JComboBox<>(new DefaultComboBoxModel<>(new Vector<> (yearStrings)));
        scaleFactorField = new JTextField(Float.toString(Main.spendingScaleFactor.get()));
        firstYearLabel = new JLabel(firstYear.toString() +" + ");
        add(new JLabel(blankLine));
        add(new JLabel(blankLine));
        add(firstYearLabel);
        add(yearSelector);
        add(yearSuffix);


        yearSelector.setSelectedIndex(yearStrings.size()-1);
        setPreferredSize(new Dimension(150, 100));
        ItemListener listener = event -> {
            if(onChanged != null) onChanged.accept(
                    new ScenarioEvent(
                            (String) Integer.toString((Integer.parseInt((String) yearSelector.getSelectedItem()))),
                            Float.parseFloat(scaleFactorField.getText())
                    )
            );
        };

        yearSelector.addItemListener(listener);

    }

    public String getYearSelector() {
        return (String) yearSelector.getSelectedItem();
    }

    public void onScenarioChanged(Consumer<ScenarioEvent> listener) {
        onChanged = listener;
    }

}
