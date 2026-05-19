package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import simulator.control.Controller;

public class MainWindow extends JFrame {

  private Controller ctrl;

  public MainWindow(Controller ctrl) {
    super("[ECOSYSTEM SIMULATOR]");
    this.ctrl = ctrl;
    initGUI();
  }

  private void initGUI() {
    JPanel mainPanel = new JPanel(new BorderLayout());
    setContentPane(mainPanel);

    getContentPane().add(new ControlPanel(ctrl), BorderLayout.PAGE_START);

    getContentPane().add(new StatusBar(ctrl), BorderLayout.PAGE_END);

    // Definición del panel de tablas (usa un BoxLayout vertical)
    JPanel contentPanel = new JPanel();
    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
    mainPanel.add(contentPanel, BorderLayout.CENTER);

    
    contentPanel.add(new InfoTable("Species", new SpeciesTableModel(ctrl)));
    setPreferredSize(new Dimension(500, 50));

    contentPanel.add(new InfoTable("Regions", new RegionsTableModel(ctrl)));
    setPreferredSize(new Dimension(500, 250));

    contentPanel.add(new InfoTable("Regions/State", new RegionStateTable(ctrl)));
    setPreferredSize(new Dimension(500, 100));

    addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(WindowEvent windowEvent) {
        ViewUtils.quit(MainWindow.this);
      }
    });

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    setPreferredSize(new Dimension(750, 500));
    pack();
    setVisible(true);
   }
}
