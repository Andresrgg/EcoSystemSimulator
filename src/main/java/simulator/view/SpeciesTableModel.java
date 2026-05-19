package simulator.view;

import java.util.ArrayList;
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

class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {

    private Map<String, Map<Animal.State, Integer>> tableData;
    private List<String> speciesNames;
    private String[] columnNames;

    SpeciesTableModel(Controller ctrl) {
        this.tableData = new TreeMap<>(); // Mantiene orden de especies
        this.speciesNames = new ArrayList<>();

        // Configuración de columnas dinámica (Species + Estados)
        Animal.State[] states = Animal.State.values();
        this.columnNames = new String[states.length + 1];
        this.columnNames[0] = "Species";
        for (int i = 0; i < states.length; i++) {
            this.columnNames[i + 1] = states[i].toString();
        }
        
        ctrl.addObserver(this);
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
        String species = speciesNames.get(row);

        if (col == 0) return species;

        Animal.State state = Animal.State.values()[col - 1];
        return tableData.get(species).getOrDefault(state, 0);
    }

    private void updateData(List<AnimalInfo> animals) {
        tableData.clear();
        speciesNames.clear();

        for (AnimalInfo a : animals) {
            String species = a.getGeneticCode();
            Animal.State state = a.getState();

            tableData.putIfAbsent(species, new HashMap<>());
            if (!speciesNames.contains(species)) {
                speciesNames.add(species);
            }

            tableData.get(species).putIfAbsent(state, 0);
            tableData.get(species).put(state, tableData.get(species).get(state) + 1);
        }
        
        // Ordenar speciesNames alfabéticamente
        speciesNames.sort(String::compareTo);
        fireTableDataChanged();
    }

    @Override 
    public void onRegister(double t, MapInfo m, List<AnimalInfo> a) { updateData(a); }

    @Override 
    public void onReset(double t, MapInfo m, List<AnimalInfo> a) { updateData(a); }

    @Override 
    public void onAdvance(double t, MapInfo m, List<AnimalInfo> a, double dt) { updateData(a); }

    @Override 
    public void onAnimalAdded(double t, MapInfo m, List<AnimalInfo> a, AnimalInfo animal) { 
        updateData(a);
    }
    
    @Override public void onRegionSet(int r, int c, MapInfo m, RegionInfo ri) {}
}