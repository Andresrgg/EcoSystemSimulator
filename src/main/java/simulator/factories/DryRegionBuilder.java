package simulator.factories;

import org.json.JSONObject;

import simulator.model.RegionPack.DryRegion;
import simulator.model.RegionPack.Region;
public class DryRegionBuilder extends Builder<Region> {

    public DryRegionBuilder() {
        super("dry", "dry periods");
    }

    @Override
    protected void fillInData(JSONObject o) {
        o.put("r1", "food factor)");
        o.put("r2", "dry factor reduction");
        o.put("m", "time without dryness");
        o.put("n", "dryness time");

    }

    @Override
    protected DryRegion createInstance(JSONObject data) {
        double r1;
        double r2;
        double n;
        double m;
        if (data == null || data.isEmpty()) {
            return new DryRegion(0.95,0.95,10.0,5.0);
        }

        if (!data.has("r1")) {
            r1 = 0.95; 
        }else {
            r1 = data.getDouble("r1");
        }
        if (!data.has("r2")) {
            r2 = 0.95; 
        }else {
            r2 = data.getDouble("r2");
        }
         if (!data.has("n")) {
            n = 10; 
        }else {
            n = data.getDouble("n");
        }
        if (!data.has("m")) {
            m = 5;
        }else {
            m = data.getDouble("m");
        }

        return new DryRegion(r1,r2,n,m);
    }
}
