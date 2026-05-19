package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.RegionPack.MapInfo;
import simulator.model.RegionPack.RegionInfo;

public class AgeLimitDialog extends JDialog {

    private Controller ctrl;
    private ExtinctionTableModel tableModel;

    private int currentLimit = 3;

    private JLabel infoLabel;
    private JTextField limit;

    public AgeLimitDialog(Controller ctrl) {

        super((Frame) null, "Age Limit Statistics", true);
        this.ctrl = ctrl;
        initGUI();
    }

    public void initGUI() {
        setTitle("Age Limit Statistics");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        this.infoLabel = new JLabel("Age Statistic for age limit:  " + currentLimit);
        updateInfoText();
        infoLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(infoLabel, BorderLayout.NORTH);

        this.tableModel = new ExtinctionTableModel(ctrl);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(500, 250));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        add(bottomPanel, BorderLayout.PAGE_START);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel t = new JLabel("Age limit");
        this.limit = new JTextField("3", 6);

        this.limit.setPreferredSize(new Dimension(100, 20));

        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> {
            this.currentLimit = 3;
            setVisible(false);
        });

        JButton setButton = new JButton("Set");
        setButton.addActionListener(e -> {

            currentLimit = Integer.parseInt(limit.getText());
            updateInfoText();
            tableModel.setLimitAndClear();

        });
        bottomPanel.add(t);
        bottomPanel.add(limit);
        bottomPanel.add(setButton);
        bottomPanel.add(closeButton);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        pack();
    }

    private void updateInfoText() {
        infoLabel.setText("Age statistic for limit:" + currentLimit);
    }

    public void open(Frame parent) {
        setLocation(
                parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
                parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
        pack();
        setVisible(true);
    }

    private class ExtinctionTableModel extends AbstractTableModel implements EcoSysObserver {

        private Map<Double, int[]> tableData;
        private String[] columnNames;
        private List<Double> times;

        ExtinctionTableModel(Controller ctrl) {
            this.tableData = new TreeMap<>();
            this.times = new ArrayList<>();
            Animal.State[] states = Animal.State.values();
            this.columnNames = new String[3];
            this.columnNames[0] = "Time";
            this.columnNames[1] = "Young";
            this.columnNames[2] = "Old";
            ctrl.addObserver(this);
        }

        public void setLimitAndClear() {
            this.times.clear();
            this.tableData.clear();
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return tableData.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            Double t = times.get(row);
            if (col == 0) {
                return String.format("%.3f", t);
            }

            int[] stats = tableData.get(t);
            if (col == 1) {
                return stats[0];
            }
            if (col == 2) {
                return stats[1];
            }

            return null;
        }

        private void updateData(double time, List<AnimalInfo> animals) {
            if (animals == null) {
                return;
            }
            int old = 0;
            int young = 0;
            for (AnimalInfo a : animals) {
                if (a.getAge() > currentLimit) {
                    old++;
                } else {
                    young++;
                }
            }
            if (tableData.get(time) == null) {
                times.add(0, time);
                tableData.put(time, new int[]{young, old});
                fireTableDataChanged();
            }
        }

        @Override
        public void onRegister(double t, MapInfo m, List<AnimalInfo> a) {
            updateData(t, a);
        }

        @Override
        public void onReset(double t, MapInfo m, List<AnimalInfo> a) {
            updateData(t, a);
        }

        @Override
        public void onAdvance(double t, MapInfo m, List<AnimalInfo> a, double dt) {
            updateData(t, a);
        }

        @Override
        public void onAnimalAdded(double t, MapInfo m, List<AnimalInfo> a, AnimalInfo animal) {
            updateData(t, a);
        }

        @Override
        public void onRegionSet(int r, int c, MapInfo m, RegionInfo ri) {
        }
    }
}
