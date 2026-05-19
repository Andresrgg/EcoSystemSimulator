package simulator.factories;
import org.json.JSONObject;

import simulator.model.RegionPack.DynamicSupplyRegion;
import simulator.model.RegionPack.Region;

public class DynamicSupplyRegionBuilder extends Builder<Region> {
    public DynamicSupplyRegionBuilder() {
        super("dynamic", "Dynamic food supply");
    }

    @Override
    protected void fillInData(JSONObject o) {
        o.put("factor", "food increase factor (optional, default 2.0)");
        o.put("food", "initial amount of food (optional, default 1000.0)");
    }

    @Override
    protected DynamicSupplyRegion createInstance(JSONObject data) {
        double food;
        double factor;
        if (data == null || data.isEmpty())
            return new DynamicSupplyRegion(1000.0, 2.0);
        
        if (!data.has("food"))
            food = 1000.0;
        else
            food = data.getDouble("food");
        if (!data.has("factor"))
            factor = 2.0;
        else
            factor = data.getDouble("factor");
        
        return new DynamicSupplyRegion(food, factor);
    }
}
