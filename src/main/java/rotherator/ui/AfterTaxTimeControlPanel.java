package rotherator.ui;

import rotherator.EconomyAllYrs;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class AfterTaxTimeControlPanel extends JPanel {

    private final JComboBox<String> economySelector;


    public AfterTaxTimeControlPanel(List<EconomyAllYrs> allEconomies, AfterTaxTimeTab parent) {
        List<String> economyDesriptions = new ArrayList<>();
        for (int i=0; i<allEconomies.size(); i++) economyDesriptions.add(allEconomies.get(i).getDescription());
        economySelector = new JComboBox<>(new DefaultComboBoxModel<>(new Vector<>(economyDesriptions)));
        economySelector.setMaximumSize(new Dimension(300, 50));

        setPreferredSize(new Dimension(300, 100));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        add(new JLabel("Economy"));
        add(economySelector);

        economySelector.addItemListener(itemEvent -> {
            int economyIndex = economyDesriptions.indexOf(itemEvent.getItem());
            parent.setEconomyIndex(economyIndex);
        });
    }


    public String getEconomy() {
        return (String) economySelector.getSelectedItem();
    }

}