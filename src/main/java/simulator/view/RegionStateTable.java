package simulator.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.RegionPack.MapInfo;
import simulator.model.RegionPack.RegionInfo;

class RegionStateTable extends AbstractTableModel implements EcoSysObserver {

    private Map<Double, Map<Animal.State, Integer>> tableData;
    private List<Double> times;
    private String[] columnNames;

    RegionStateTable(Controller ctrl) {
        this.tableData = new TreeMap<>(Collections.reverseOrder());
        times = new ArrayList<>();
        Animal.State[] states = Animal.State.values();
        this.columnNames = new String[states.length + 1 ];
        this.columnNames[0] = "Time";
        for (int i = 0; i < states.length; i++) {
            this.columnNames[i + 1] = states[i].toString();
        }
        ctrl.addObserver(this);
    }

    @Override
    public int getRowCount() {

        return times.size();
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        if (times.isEmpty() || row >= times.size()) return null;
        Double t = times.get(row);
        if (col == 0) {return String.format("%.3f", t);
        }
        Animal.State state = Animal.State.values()[col - 1];
        Map<Animal.State, Integer> statsForTime = tableData.get(t);
        if (statsForTime != null) {
            return statsForTime.getOrDefault(state, 0);
        }
        return 0;
    }

    private void updateData(MapInfo regions, double t) {
        Map<Animal.State, Integer> map=new HashMap<>();
        for(Animal.State a :Animal.State.values()){
            map.put(a,0);
        }
        for (MapInfo.RegionData region : regions) {
            for (AnimalInfo a : region.r().getAnimalsInfo()) {
                Animal.State state = a.getState();
                map.put(state,map.get(state)+1);
            }
        }
        tableData.put(t, map);
        if (!times.contains(t)) {
        times.add(0, t);
            }
        fireTableDataChanged();
    }

    @Override
    public void onRegister(double t, MapInfo m, List<AnimalInfo> a) {
        this.times.clear();
        this.tableData.clear();
        updateData(m, t);
    }

    @Override
    public void onReset(double t, MapInfo m, List<AnimalInfo> a) {
        this.times.clear();
        this.tableData.clear();
        updateData(m, t);
    }

    @Override
    public void onAdvance(double t, MapInfo m, List<AnimalInfo> a, double dt) {
        updateData(m, t);
    }

    @Override
    public void onAnimalAdded(double t, MapInfo m, List<AnimalInfo> a, AnimalInfo animal) {
        updateData(m, t);
    }

    @Override
    public void onRegionSet(int r, int c, MapInfo m, RegionInfo ri) {
    }

}
