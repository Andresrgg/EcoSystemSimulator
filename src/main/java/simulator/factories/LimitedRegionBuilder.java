package simulator.factories;

import org.json.JSONObject;

import simulator.model.RegionPack.LimitedRegion;
import simulator.model.RegionPack.Region;

public class LimitedRegionBuilder extends Builder<Region> {

    public LimitedRegionBuilder() {
        super("limited", "limited food for x animals");
    }

    @Override
    protected void fillInData(JSONObject o) {
        o.put("min", "min animals getting food, default 1.0");
        o.put("max", "max animals getting food, default 100.0)");
        o.put("t", "T factor reviewing the amount of animals getting food, default 5.o");
    }

    @Override
    protected LimitedRegion createInstance(JSONObject data) {
        double t = 5.0;
        int min = 1;
        int max = 100;
        if (data.has("t")) {
            t = data.getDouble("min");
        }
        if (data.has("min")) {
            min = data.getInt("min");
        }
        if (data.has("max")) {
            max = data.getInt("max");
        }
        return new LimitedRegion(t, min, max);
    }

}
