package simulator.model.AnimalPack;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
public class Sheep extends Animal {

    Animal dangerSource;
    SelectionStrategy dangerStrategy;

    public Sheep(SelectionStrategy mateStrategy, SelectionStrategy dangerStrategy, Vector2D pos) {
        super("Sheep", Diet.HERBIVORE, 40.0, 35.0, mateStrategy, pos);
        this.dangerStrategy = dangerStrategy;
        this.dangerSource = null;
    }

    public Sheep(Sheep p1, Animal p2) {
        super(p1, p2);
        this.dangerStrategy = p1.dangerStrategy;
        this.dangerSource = null;
    }

    @Override
    public void update(double dt) {
        if (getState() == State.DEAD){return;}
        switch (getState()) {
            case NORMAL:
                updateNormal(dt);
                break;
            case DANGER:
                updateDanger(dt);
                break;
            case MATE:
                updateMate(dt);
                break;
            case HUNGER:
                break;
            default:
                break;
        }
        if (regionMngr.isOutOfMap(pos)) {
            pos = this.regionMngr.adjustPosition(pos);
            setState(State.NORMAL);
        }
        if (getEnergy() <= 0.0 || getAge() > 8.0) {
            setState(State.DEAD);
        }
        if (getState() != State.DEAD) {
            double food = this.regionMngr.getFood(this, dt);
            energy = Utils.constrainValueInRange(getEnergy() + food, 0.0, 100.0);
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
            move(2.0 * getSpeed() * dt * Math.exp((energy - 100.0) * 0.007));
            age += dt;
            energy = Utils.constrainValueInRange(energy - 20.0 * 1.2 * dt, 0.0, 100.0);
            desire = Utils.constrainValueInRange(desire + 40.0 * dt, 0.0, 100.0);

            if (pos.distanceTo(mateTarget.pos) < 8.0) {
                desire = 0.0;
                mateTarget.desire = 0.0;
                if (!isPregnant() && Utils.RAND.nextDouble() < 0.9) {
                    baby = new Sheep(this, mateTarget);
                }
                mateTarget = null;
            }
        }
        if (dangerSource == null)
            dangerSource = dangerStrategy.select(this, this.regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.CARNIVORE));

        if (dangerSource != null) 
            setState(State.DANGER);
        else if (dangerSource == null && desire < 65.0)
            setState(State.NORMAL);
    }

    private void updateDanger(double dt){
        if (dangerSource != null && dangerSource.getState() == State.DEAD) {
            dangerSource = null;
        }
        if (dangerSource == null) {
            moveNormal(dt);
        } else {
            this.dest = pos.plus(pos.minus(dangerSource.pos).direction());
            move(2.0 * getSpeed() * dt * Math.exp((energy - 100.0) * 0.007));
            this.age += dt;
            this.energy = Utils.constrainValueInRange(energy - 20.0 * 1.2 * dt, 0.0, 100.0);
            this.desire = Utils.constrainValueInRange(desire + 40.0 * dt, 0.0, 100.0);
        }
        if (dangerSource == null || getSightRange() < pos.distanceTo(dangerSource.pos)) {
            dangerSource = dangerStrategy.select(this, this.regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.CARNIVORE));
        }
        if (dangerSource == null) {
            if (desire < 65.0) {
                setState(State.NORMAL);
            } else {
                setState(State.MATE);
            }
        }
    }

    private void updateNormal(double dt) {
        moveNormal(dt);
        if (dangerSource == null) {
            dangerSource = dangerStrategy.select(this, regionMngr.getAnimalsInRange(this, a -> a.getDiet() == Diet.CARNIVORE));
        }
        if (dangerSource != null) {
            setState(State.DANGER);
        } else if (dangerSource == null && desire > 65.0) {
            setState(State.MATE);
        }
    }

    private void moveNormal(double dt) {
        if (getDestination() == null || pos.distanceTo(dest) < 8.0) {
            this.dest = this.regionMngr.getRandomPosition();
        }
        move(getSpeed() * dt * Math.exp((energy - 100.0) * 0.007));
        this.age += dt;
        this.energy = Utils.constrainValueInRange(energy - 20.0 * dt, 0.0, 100.0);
        this.desire = Utils.constrainValueInRange(desire + 40.0 * dt, 0.0, 100.0);
    }

    @Override
    protected void setNormalStateAction() {
        dangerSource = null;
        mateTarget = null;
    }

    @Override
    protected void setDeadStateAction() {
        dangerSource = null;
        mateTarget = null;
    }

    @Override
    protected void setMateStateAction() { dangerSource = null; }

    @Override
    protected void setDangerStateAction() { mateTarget = null; }

    @Override
    protected void setHungerStateAction() {}

}