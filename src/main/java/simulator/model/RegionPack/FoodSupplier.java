package simulator.model.RegionPack;

import simulator.model.AnimalPack.AnimalInfo;

public interface FoodSupplier {
    public double getFood(AnimalInfo a, double dt);
}
