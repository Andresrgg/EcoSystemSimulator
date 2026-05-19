package simulator.model.RegionPack;

import simulator.misc.Utils;
import simulator.model.AnimalPack.AnimalInfo;

public class LimitedRegion extends DefaultRegion {

    private int min;
    private int max;
    private int x;
    private int cont;
    private double T;
    private double time;

    public LimitedRegion(double t, int min, int max) {
        if (t <= 0 || min <= 0 || max <= min) {
            throw new IllegalArgumentException("Invalid parameters for LimitedRegion.");
        }
        this.T = t;
        this.min = min;
        this.max = max;
        time = 0;
        x = this.min + Utils.RAND.nextInt((this.max - this.min) + 1);
        cont = 0;
    }

    public double getFood(AnimalInfo a, double dt) {
        if (cont<x){
            cont++;
            return super.getFood(a, dt);
        }
        return 0;
    }

    public void update(double dt) {
        time += dt;
        cont = 0;
        if (time >= T) {
            time = 0;
            x = this.min + Utils.RAND.nextInt((this.max - this.min) + 1);

        }

    }

    public String toString() {
        return ("limited");
    }
}
