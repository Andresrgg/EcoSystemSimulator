package simulator.factories;

import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.SelectFirst;
import simulator.model.AnimalPack.SelectionStrategy;
import simulator.model.AnimalPack.Sheep;

public class SheepBuilder extends AnimalBuilder {

    public SheepBuilder(Factory<SelectionStrategy> selection_strategy_factory) {
        super("sheep", "Animal Sheep", selection_strategy_factory);
    }

    @Override
    protected void fillInData(JSONObject o) {
        o.put("mate_strategy", "Select mate strat, select first auto");
        o.put("danger_strategy", "Select danger strat, select first auto");
        o.put("pos", " x,y sheep position");
    }

    @Override
    protected Animal createInstance(JSONObject data) {       
        return super.createInstance(data);
    }

    protected Sheep instantiate(SelectionStrategy mate_strategy, Vector2D pos, JSONObject data){
        SelectionStrategy danger_strategy;
        if (data.has("danger_strategy")) danger_strategy = selection_strategy_factory.createInstance(data.getJSONObject("danger_strategy"));
        else danger_strategy = new SelectFirst();
        return new Sheep(mate_strategy, danger_strategy, pos);
    }
}
