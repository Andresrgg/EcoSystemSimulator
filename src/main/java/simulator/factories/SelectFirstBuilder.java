package simulator.factories;
import org.json.JSONObject;

import simulator.model.AnimalPack.SelectFirst;
import simulator.model.AnimalPack.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {
  public SelectFirstBuilder() {
    super("first", "Selects the first animal in the list of animals in sightRange");
  }

  @Override
  protected SelectFirst createInstance(JSONObject data) {
    return new SelectFirst();
  }
    
}
