package simulator.model.RegionPack;

import simulator.misc.Utils;
import simulator.model.AnimalPack.AnimalInfo;
public class DryRegion extends DefaultRegion {

    private double r1;
    private double r2;
    private double n;
    private double m;
    private double time;
    private boolean dry;
    private double currentDryDuration;
    public DryRegion(double r1, double r2, double n, double m) {
        super();
        this.r1 = r1;
        this.r2 = r2;
        this.n = n;
        this.m = m;
        time=0;
        dry=false;
    }

    public double getFood(AnimalInfo a, double dt){
        double foodAntesCorrecion = super.getFood(a, dt);
        if(dry){
            return foodAntesCorrecion*r1;
        }else{
            return foodAntesCorrecion;
        }
    }
    public void update(double dt) {
        time+=dt;
        if (!dry) {
            if (this.time >= n) {
                this.dry = true;
                this.time = 0.0;
                double rand =Utils.RAND.nextDouble();
                this.currentDryDuration   = Utils.constrainValueInRange(rand, 1, this.m);
            }
        } else {
            if (this.time >= currentDryDuration) {
                this.dry = false;
                this.time = 0.0;
                this.r1 = this.r1 * this.r2;
            }
        }
    }

    public String toString() {
        return ("Dry");
    }
}
