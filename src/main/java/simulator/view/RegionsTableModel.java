package simulator.view;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

import simulator.model.RegionPack.MapInfo;

import javax.swing.table.AbstractTableModel;
import simulator.control.Controller;
import simulator.model.EcoSysObserver;
import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.AnimalInfo;
import simulator.model.RegionPack.RegionInfo;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {

  private Map<MapInfo.RegionData, Map<Animal.Diet, Integer>> tableData;
  private List<MapInfo.RegionData> regionsInfo;
  private String[] columnNames = {"Row", "Col", "Desc."};


  RegionsTableModel(Controller ctrl) {
    tableData = new HashMap<>();
    regionsInfo = new ArrayList<>();
    Animal.Diet[] diets = Animal.Diet.values();
    this.columnNames = new String[diets.length + 3];
    this.columnNames[0] = "Row";
    this.columnNames[1] = "Col";
    this.columnNames[2] = "Desc.";
        for (int i = 0; i < diets.length; i++) {
            this.columnNames[i + 3] = diets[i].toString();
        }
    ctrl.addObserver(this);
  }

  @Override
  public int getRowCount() {
    return regionsInfo.size();
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
    MapInfo.RegionData region = regionsInfo.get(row);

    switch (col) {
      case 0: return region.row();
      case 1: return region.col();
      case 2: return region.r().toString();
      default: 
              Animal.Diet diet = Animal.Diet.values()[col - 3];
              return tableData.get(region).getOrDefault(diet, 0);
    }    
  }

  private void updateData(MapInfo regions) {
    tableData.clear();
    regionsInfo.clear(); 

    for (MapInfo.RegionData region : regions) {
      regionsInfo.add(region);
      tableData.put(region, new HashMap<>());
      for (AnimalInfo a : region.r().getAnimalsInfo()) {
        Animal.Diet diet = a.getDiet();
        tableData.get(region).putIfAbsent(diet, 0);
        tableData.get(region).put(diet, tableData.get(region).get(diet) + 1);
      }
    }
    fireTableDataChanged();
  } 
  @Override 
  public void onRegister(double t, MapInfo m, List<AnimalInfo> a) { updateData(m); }

  @Override 
  public void onReset(double t, MapInfo m, List<AnimalInfo> a) { updateData(m); }

  @Override 
  public void onAdvance(double t, MapInfo m, List<AnimalInfo> a, double dt) { updateData(m); }

  @Override 
  public void onAnimalAdded(double t, MapInfo m, List<AnimalInfo> a, AnimalInfo animal) { updateData(m); }

  @Override 
  public void onRegionSet(int r, int c, MapInfo m, RegionInfo ri) { 
      updateData(m);
  }


}
  
