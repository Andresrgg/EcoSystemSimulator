package simulator.model.RegionPack;

import simulator.misc.Utils;
import simulator.model.AnimalPack.AnimalInfo;
import simulator.model.AnimalPack.Animal;

public class DynamicSupplyRegion extends Region {

    private double food; //cantidad inicial de la comida
    private double factor; //factor de crecimiento de la comida

    public DynamicSupplyRegion(double food, double dt) {
        super();
        this.food = food;
        this.factor = dt;
    }

    public double getFood(AnimalInfo a, double dt) {
        double ret;
        if (a.getDiet() == Animal.Diet.CARNIVORE) {
            ret = 0.0;
        }
        long n = this.animals.stream().filter(animal -> {return animal.getDiet() == Animal.Diet.HERBIVORE;}).count();
        ret = Math.min(food,60.0*Math.exp(-Math.max(0,n-5.0)*2.0)*dt);
        this.food=this.food-ret;
        return ret;
    }
    public void update(double dt){
        if(Utils.RAND.nextDouble()>0.5){
            this.food=this.food+this.food*dt*factor;
        }

    }
public String toString(){
        return ("Dynamic");
    }
}
