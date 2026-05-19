package simulator.factories;
import org.json.JSONObject;

import simulator.model.RegionPack.DefaultRegion;
import simulator.model.RegionPack.Region;

public class DefaultRegionBuilder extends Builder<Region> {

    public DefaultRegionBuilder() {
        super("default", "Infinite food supply");
    }

    @Override
    protected DefaultRegion createInstance(JSONObject data) {
        return new DefaultRegion();
    }
    
}
