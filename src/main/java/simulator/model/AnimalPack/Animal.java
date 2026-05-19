package simulator.model.AnimalPack;

import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.Entity;
import simulator.model.RegionPack.AnimalMapView;

public abstract class Animal implements Entity, AnimalInfo{
    public enum State { NORMAL, MATE, HUNGER, DANGER, DEAD }
    public enum Diet { HERBIVORE, CARNIVORE }

    private String geneticCode; //Cadena de caracteres no vacía que representa el código genético.
    private Diet diet; //Indica si el animal es herbívoro o carnívoro.
    private State state; //Estado actual del animal (NORMAL, MATE, HUNGER, DANGER, DEAD).

    protected Vector2D pos;
    protected Vector2D dest; //El animal siempre tiene un destino, y cuando lo alcanza elige otro, o lo cambia según si está siguiendo a otro animal o siendo perseguido por otro animal

    protected double energy; //Cuando llega a 0.0 el animal muere.
    private double speed; 
    protected double age; //Cuando llega a un máximo (dependiendo del tipo de animal) el animal muere.
    protected double desire; //Lo vamos a usar para decidir si un animal entra en (o sale de) un estado de emparejamiento.
    private double sightRange; //El radio del campo visual del animal (para decidir qué animales puede ver).

    protected Animal mateTarget; //Una referencia a un animal con el que quiere emparejarse.
    protected Animal baby;  //Una referencia que indica si el animal lleva un bebé que no ha nacido aún.

    protected AnimalMapView regionMngr; //Referencia al gestor de regiones para notificar movimientos.
    protected SelectionStrategy mateStrategy; //Estrategia de selección de pareja para el emparejamiento.

    public Animal.State getState() { return state; }
    public Vector2D getPosition() { return pos; }
    public String getGeneticCode() { return geneticCode; }
    public Animal.Diet getDiet() { return diet; } 
    public double getSpeed() { return speed; }
    public double getSightRange() { return sightRange; } 
    public double getEnergy() { return energy; }
    public double getAge() { return age; } 
    public Vector2D getDestination() { return dest; } 
    public boolean isPregnant() { return baby != null; }

    public void init(AnimalMapView regionMngr) {
        this.regionMngr = regionMngr;
        if (pos == null) {
            double x = Utils.RAND.nextDouble()*(regionMngr.getWidth() - 1);
            double y = Utils.RAND.nextDouble()* (regionMngr.getHeight() - 1);
            this.pos = new Vector2D(x, y);
        } else {
        this.pos = regionMngr.adjustPosition(this.pos);
        }
        double destX = Utils.RAND.nextDouble() * (regionMngr.getWidth() - 1);
        double destY = Utils.RAND.nextDouble() * (regionMngr.getHeight() - 1);
        this.dest = new Vector2D(destX, destY);
    }

    protected Animal(String geneticCode, Diet diet, double sightRange, double initSpeed, SelectionStrategy mateStrategy, Vector2D pos){
        if(geneticCode == null || geneticCode.isBlank())
        	throw new IllegalArgumentException("geneticCode cannot be empty");
        else this.geneticCode = geneticCode;

        if (sightRange < 0)
        	throw new IllegalArgumentException("sightRange cannot be negative");
        else this.sightRange = sightRange;

        if(initSpeed < 0)
        	throw new IllegalArgumentException("initSpeed cannot be negative");
        else this.speed = Utils.getRandomizedParameter(initSpeed, 0.1);
        
        if(mateStrategy == null)
        	throw new IllegalArgumentException("mateStrategy cannot be null");
        else this.mateStrategy = mateStrategy;
        this.diet = diet;
        this.pos = pos;

        this.state = State.NORMAL;
        this.energy = 100.0;
        this.desire = 0.0;
        this.dest = null;
        this.mateTarget = null;
        this.baby = null;
        this.regionMngr = null;
    }

    protected Animal(Animal p1, Animal p2){
        this.dest = null;
        this.mateTarget = null;
        this.baby = null;
        this.regionMngr = null;

        this.state = State.NORMAL;
        this.desire = 0.0;

        this.geneticCode = p1.geneticCode;
        this.diet = p1.diet;

        this.mateStrategy = p2.mateStrategy;
        this.energy = (p1.energy + p2.energy) / 2;
        
        this.pos = p1.getPosition().plus(Vector2D.get_random_vector(-1,1).scale(60.0*(Utils.RAND.nextGaussian()+1)));
        this.sightRange = Utils.getRandomizedParameter((p1.getSightRange()+p2.getSightRange())/2,0.2);
        this.speed = Utils.getRandomizedParameter((p1.getSpeed()+p2.getSpeed())/2,0.2);
    }
    
    public Animal deliverBaby(){
        Animal bornBaby = this.baby;
        this.baby = null;
        return bornBaby;
    }

    protected void move(double speed){
        pos = pos.plus(dest.minus(pos).direction().scale(speed));
    }

    protected void setState(State state) {
        this.state = state;
        switch (state) {
        case NORMAL:
            setNormalStateAction();
            break;
        case MATE:
            setMateStateAction();
            break;
        case HUNGER:
            setHungerStateAction();
            break;
        case DANGER:
            setDangerStateAction();
            break;
        case DEAD:
            setDeadStateAction();
            break;
        }
    }
    
    protected abstract void setNormalStateAction();
    protected abstract void setMateStateAction();
    protected abstract void setHungerStateAction();
    protected abstract void setDangerStateAction();
    protected abstract void setDeadStateAction();

    @Override
    public JSONObject asJSON() {
        JSONObject json = new JSONObject();
        json.put("pos", this.pos.asJSONArray()); // Usa el método de Vector2D 
        json.put("gcode", this.geneticCode);
        json.put("diet", this.diet.toString());
        json.put("state", this.state.toString());
        return json;
    }

}
