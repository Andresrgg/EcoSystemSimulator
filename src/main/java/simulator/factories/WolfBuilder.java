package simulator.factories;
import org.json.JSONObject;

import simulator.misc.Vector2D;
import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.SelectFirst;
import simulator.model.AnimalPack.SelectionStrategy;
import simulator.model.AnimalPack.Wolf;

public class WolfBuilder extends AnimalBuilder{
    
    public WolfBuilder(Factory<SelectionStrategy> selection_strategy_factory) {
        super("wolf", "Animal Wolf", selection_strategy_factory);
    }

    @Override
    protected void fillInData(JSONObject o) {
        o.put("mate_strategy", "Select mate strat, select first auto");
        o.put("hunt_strategy", "Select danger strat,select first auto");
        o.put("pos", " x,y wolf position");
    }

    @Override
    protected Animal createInstance(JSONObject data) {       
        return super.createInstance(data);
    }

    protected Wolf instantiate(SelectionStrategy mate_strategy, Vector2D pos, JSONObject data){
        SelectionStrategy hunt_strategy;
        if (data.has("hunt_strategy")) hunt_strategy = selection_strategy_factory.createInstance(data.getJSONObject("hunt_strategy"));
        else hunt_strategy = new SelectFirst();
        return new Wolf(mate_strategy, hunt_strategy, pos);
    }
}
