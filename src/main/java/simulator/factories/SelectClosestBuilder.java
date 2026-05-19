package simulator.factories;

import org.json.JSONObject;

import simulator.model.AnimalPack.SelectClosest;
import simulator.model.AnimalPack.SelectionStrategy;

public class SelectClosestBuilder extends Builder<SelectionStrategy> {
    public SelectClosestBuilder() {
        super("closest", "Selects the closest animal in the list of animals in sightRange");
    }

    @Override
    protected SelectClosest createInstance(JSONObject data) {
        return new SelectClosest();
    }
}
