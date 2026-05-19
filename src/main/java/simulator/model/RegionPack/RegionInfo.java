package simulator.model.RegionPack;

import simulator.model.JSONable;
import simulator.model.AnimalPack.AnimalInfo;
import java.util.List;

public interface RegionInfo extends JSONable {
  public List<AnimalInfo> getAnimalsInfo();
}
