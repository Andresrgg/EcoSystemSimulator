package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.SelectFirst;
import simulator.model.AnimalPack.SelectionStrategy;

public abstract class AnimalBuilder extends Builder<Animal> {

    protected Factory<SelectionStrategy> selection_strategy_factory;
    public AnimalBuilder(String typeTag, String desc, Factory<SelectionStrategy> selection_strategy_factory){
        super(typeTag, desc);
        if (selection_strategy_factory == null) {
            throw new IllegalArgumentException("selection_strategy_factory cannot be null");
        }
        this.selection_strategy_factory = selection_strategy_factory;
    }

    @Override
    protected Animal createInstance(JSONObject data){
        SelectionStrategy mate_strategy;

        if (data == null) throw new IllegalArgumentException("data cannot be null");
        if (data.has("mate_strategy")) mate_strategy = selection_strategy_factory.createInstance(data.getJSONObject("mate_strategy"));
        else mate_strategy = new SelectFirst();

        if (data.has("pos")) {
            JSONObject posData = data.getJSONObject("pos");

            JSONArray x = posData.getJSONArray("x_range");
            JSONArray y = posData.getJSONArray("y_range");
            
            double xPos = Utils.RAND.nextDouble(x.getDouble(0), x.getDouble(1));
            double yPos = Utils.RAND.nextDouble(y.getDouble(0), y.getDouble(1));
            Vector2D pos = new Vector2D(xPos, yPos);
            return instantiate(mate_strategy, pos, data);
        } else {
            return instantiate(mate_strategy, null, data);
        }
    }

    protected abstract Animal instantiate(SelectionStrategy mate_strategy, Vector2D pos, JSONObject data);
}
