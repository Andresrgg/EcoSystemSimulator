package simulator.model.RegionPack;

import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.AnimalInfo;


public class DefaultRegion extends Region {

    public double getFood(AnimalInfo a, double dt){
        if (a.getDiet() == Animal.Diet.CARNIVORE) {
            return 0.0;
        }
        long n = this.animals.stream().filter(animal -> {return animal.getDiet() == Animal.Diet.HERBIVORE;}).count();
        
        return 60.0 * Math.exp(-Math.max(0, n - 5.0) * 2.0) * dt;
    }
    
    public void update(double dt){}
    public String toString(){
        return ("Default");
    }
}
