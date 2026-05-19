package simulator.factories;

import org.json.JSONObject;

import simulator.model.AnimalPack.SelectYoungest;
import simulator.model.AnimalPack.SelectionStrategy;

public class SelectYoungestBuilder extends Builder<SelectionStrategy>{
    public SelectYoungestBuilder() {
        super("youngest", "Selects the youngest animal in the list of animals in sightRange");
    }

    @Override
    protected SelectYoungest createInstance(JSONObject data) {
        return new SelectYoungest();
    }
}
