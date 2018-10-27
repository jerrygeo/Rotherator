package rotherator.ui;

import rotherator.EconomyAllYrs;
import rotherator.RothXfrsAllYrs1Plan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.function.Consumer;

public class TimeHistoryControlPanel extends JPanel {

   public static class ScenarioEvent {
       public final String rothTransferPlan;
       public final String economy;

       public ScenarioEvent(String rothTransferPlan, String economy) {
           this.rothTransferPlan = rothTransferPlan;
           this.economy = economy;
       }
   }

   private final JComboBox<String> rothTransferPlanSelector;
   private final JComboBox<String> economySelector;

   // assuming we only need 1 listener
   private Consumer<ScenarioEvent> onChanged;

   public TimeHistoryControlPanel(List<RothXfrsAllYrs1Plan> transferPlans, List<EconomyAllYrs> allEconomies) {
       List<String> economyDesriptions = new ArrayList<>();
       for (int i=0; i<allEconomies.size(); i++) economyDesriptions.add(allEconomies.get(i).getDescription());
       List<String> rothDescriptions = new ArrayList<>();
       for (int i=0; i<transferPlans.size(); i++) rothDescriptions.add(transferPlans.get(i).getDescription());
       rothTransferPlanSelector = new JComboBox<>(new DefaultComboBoxModel<>(new Vector<>(rothDescriptions)));
       rothTransferPlanSelector.setMaximumSize(new Dimension(300, 50));
       economySelector = new JComboBox<>(new DefaultComboBoxModel<>(new Vector<>(economyDesriptions)));
       economySelector.setMaximumSize(new Dimension(300, 50));

       setPreferredSize(new Dimension(300, 100));
//       setLayout(new FlowLayout(FlowLayout.LEFT, 15, 5 ));
       setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
       add(new JLabel("Roth transfer plan"));
       add(rothTransferPlanSelector);
       add(new JLabel(" "));
       add(new JLabel(" "));
       add(new JLabel("Economy"));
       add(economySelector);

       ItemListener listener = event -> {
           if(onChanged != null) onChanged.accept(
                   new ScenarioEvent(
                           (String) rothTransferPlanSelector.getSelectedItem(),
                           (String) economySelector.getSelectedItem()
                   )
           );
       };

       rothTransferPlanSelector.addItemListener(listener);
       economySelector.addItemListener(listener);
   }

   public String getRothTransferPlan() {
       return (String) rothTransferPlanSelector.getSelectedItem();
   }

   public String getEconomy() {
       return (String) economySelector.getSelectedItem();
   }

   public void onScenarioChanged(Consumer<ScenarioEvent> listener) {
       onChanged = listener;
   }
}
