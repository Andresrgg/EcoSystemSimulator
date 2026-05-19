package simulator.model.RegionPack;

import simulator.misc.Vector2D;
import simulator.model.JSONable;

public interface MapInfo extends JSONable, Iterable<MapInfo.RegionData> {
  public record RegionData(int row, int col, RegionInfo r) {
  }
  
  public int getCols();

  public int getRows();

  public int getWidth();

  public int getHeight();

  public int getRegionWidth();

  public int getRegionHeight();

  public boolean isOutOfMap(Vector2D pos);

  public Vector2D adjustPosition(Vector2D pos);

  public Vector2D getRandomPosition();
}
