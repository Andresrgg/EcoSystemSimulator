package simulator.model.RegionPack;
import java.util.List;
import java.util.function.Predicate;

import simulator.model.AnimalPack.Animal;

public interface AnimalMapView extends MapInfo, FoodSupplier {
    public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter);
}
