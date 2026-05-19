package simulator.view;

import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import java.awt.Dimension;
import java.util.List;

import simulator.control.Controller;
import simulator.model.EcoSysObserver;
import simulator.model.RegionPack.MapInfo;
import simulator.model.AnimalPack.AnimalInfo;
import simulator.model.RegionPack.RegionInfo;

class StatusBar extends JPanel implements EcoSysObserver {

  private double time;
  private int numAnimals;
  private int mapWidth;
  private int mapHeight;
  private int mapRows;
  private int mapCols;

  StatusBar(Controller ctrl) {
    initGUI();
    ctrl.addObserver(this);
  }

  private void initGUI() {
    this.setLayout(new FlowLayout(FlowLayout.LEFT));
    this.setBorder(BorderFactory.createBevelBorder(1));

    JLabel timeLabel = new JLabel("Time: " + String.format("%.3f", this.time));
    this.add(timeLabel);
    JSeparator s = new JSeparator(JSeparator.VERTICAL);
    s.setPreferredSize(new Dimension(10, 20));
    this.add(s);

    JLabel numAnimalsLabel = new JLabel("Animals: " + this.numAnimals);
    this.add(numAnimalsLabel);
    JSeparator s2 = new JSeparator(JSeparator.VERTICAL);
    s2.setPreferredSize(new Dimension(10, 20));
    this.add(s2);

    JLabel mapInfoLabel = new JLabel("Map: " + this.mapWidth + "x" + this.mapHeight + " " + this.mapRows + "x" + this.mapCols);
    this.add(mapInfoLabel);
    JSeparator s3 = new JSeparator(JSeparator.VERTICAL);
    s3.setPreferredSize(new Dimension(10, 20));
    this.add(s3);
  }

  private void updateStatusBar() {
    removeAll();
    initGUI();
    revalidate();
    repaint();
  }

  @Override
  public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
    this.time = time;
    this.numAnimals = animals.size();
    this.mapWidth = map.getWidth();
    this.mapHeight = map.getHeight();
    this.mapRows = map.getRows();
    this.mapCols = map.getCols();
    updateStatusBar();
  }

  @Override
  public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
    onRegister(time, map, animals);
  }

  @Override
  public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
    onRegister(time, map, animals);
  }

  @Override
  public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {}

  @Override
  public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
    onRegister(time, map, animals);
  }

}
