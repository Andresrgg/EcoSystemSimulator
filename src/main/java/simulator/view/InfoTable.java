package simulator.view;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel {

  private String title;
  private TableModel tableModel;

  InfoTable(String title, TableModel tableModel) {
    this.title = title;
    this.tableModel = tableModel;
    initGUI();
  }

  private void initGUI() {

    setLayout(new BorderLayout());
    setBorder(javax.swing.BorderFactory.createTitledBorder(this.title));

    JTable table = new JTable(this.tableModel);
    JScrollPane scrollPane = new JScrollPane(table);

    add(scrollPane);
  }
}
