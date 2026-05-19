package simulator.model.AnimalPack;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal {
    Animal huntTarget;
    SelectionStrategy huntingStrategy;

    public Wolf(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy, Vector2D pos) {
        super("Wolf", Diet.CARNIVORE, 50.0, 60.0, mateStrategy, pos);
        this.huntingStrategy = dangerStrategy;
        this.huntTarget = null;
    }
    
    public Wolf(Wolf p1, Animal p2) {
        super(p1, p2);
        this.huntingStrategy = p1.huntingStrategy;
        this.huntTarget = null;
    }

    @Override
    public void update(double dt) {
        if (getState() == State.DEAD){return;}
        switch (getState()) {
            case NORMAL:
                updateNormal(dt);
                break;
            case DANGER:
                break;
            case MATE:
                updateMate(dt);
                break;
            case HUNGER:
                updateHunger(dt);
                break;
            default:
                break;
        }
        if (regionMngr.isOutOfMap(pos)) {
            pos = this.regionMngr.adjustPosition(pos);
            setState(State.NORMAL);
        }
        if (getEnergy() <= 0.0 || getAge() > 14.0) {
            setState(State.DEAD);
        }
        if (getState() != State.DEAD) {
            double food = this.regionMngr.getFood(this, dt);
            energy = Utils.constrainValueInRange(energy + food, 0.0, 100.0);
        }
    }

    private void updateMate(double dt) {
        if (mateTarget != null && (mateTarget.getState() == State.DEAD || getSightRange() < pos.distanceTo(mateTarget.pos))) {
            mateTarget = null;
        }
        if (mateTarget == null) {
            mateTarget = mateStrategy.select(this, this.regionMngr.getAnimalsInRange(this, a -> a.getGeneticCode().equals(this.getGeneticCode())));
        }

        if (mateTarget == null) {
            moveNormal(dt);
        }
        else {
            dest = mateTarget.pos;
            move(3.0 * getSpeed() * dt * Math.exp((energy - 100.0) * 0.007));
            age += dt;
            this.energy = Utils.constrainValueInRange(energy - 18.0 * dt, 0.0, 100.0);
            this.desire = Utils.constrainValueInRange(desire + 30.0 * dt, 0.0, 100.0);

            if (pos.distanceTo(mateTarget.pos) < 8.0) {
                desire = 0.0;
                mateTarget.desire = 0.0;
                if (!isPregnant() && Utils.RAND.nextDouble() < 0.9) {
                    baby = new Wolf(this, mateTarget);
                    this.energy = Utils.constrainValueInRange(energy - 10.0, 0.0, 100.0);
                }
                mateTarget = null;
            }
        }

        if (energy < 50.0) {
            setState(State.HUNGER);
        } else if (energy >= 50.0 && desire > 65.0) {
            setState(State.MATE);
        }
        
    }

    private void updateHunger(double dt){
        if ((huntTarget == null) || (huntTarget != null && (huntTarget.getState() == State.DEAD || getSightRange() < pos.distanceTo(huntTarget.pos)))) {
            huntTarget = huntingStrategy.select(this, this.regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.HERBIVORE));;
        }
        if (huntTarget == null) {
            moveNormal(dt);
        } else {
            this.dest = huntTarget.pos;
            move(3.0 * getSpeed() * dt * Math.exp((energy - 100.0) * 0.007));
            this.age += dt;
            this.energy = Utils.constrainValueInRange(energy - 18.0 * 1.2 * dt, 0.0, 100.0);
            this.desire = Utils.constrainValueInRange(desire + 30.0 * dt, 0.0, 100.0);

            if (pos.distanceTo(huntTarget.pos) < 8.0) {
                huntTarget.setState(State.DEAD);
                huntTarget = null;
                this.energy = Utils.constrainValueInRange(energy + 50.0, 0.0, 100.0);
            }
            
        }
        
        if (energy > 50.0) {
            if (desire >= 65.0) 
                setState(State.MATE);
            else 
                setState(State.NORMAL);
        }
    }

    private void updateNormal(double dt) {
        moveNormal(dt);
        if (energy < 50.0) {
            setState(State.HUNGER);
        } else if (energy >= 50.0 && desire > 65.0) {
            setState(State.MATE);
        }
    }

    private void moveNormal(double dt) {
        if (getDestination() == null || pos.distanceTo(dest) < 8.0) {
            this.dest = this.regionMngr.getRandomPosition();
        }
        move(getSpeed() * dt * Math.exp((energy - 100.0) * 0.007));
        this.age += dt;
        this.energy = Utils.constrainValueInRange(energy - 18.0 * dt, 0.0, 100.0);
        this.desire = Utils.constrainValueInRange(desire + 30.0 * dt, 0.0, 100.0);
    }

    @Override
    protected void setNormalStateAction() {
        huntTarget = null;
        mateTarget = null;
    }

    @Override
    protected void setDeadStateAction() {
        huntTarget = null;
        mateTarget = null;
    }

    @Override
    protected void setMateStateAction() { huntTarget = null; }

    @Override
    protected void setDangerStateAction() {}

    @Override
    protected void setHungerStateAction() { mateTarget = null; }
}