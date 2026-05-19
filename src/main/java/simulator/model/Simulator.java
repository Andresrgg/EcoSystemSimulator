package simulator.model;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Stream;

import simulator.factories.Factory;
import simulator.model.AnimalPack.*;
import org.json.JSONObject;
import simulator.model.RegionPack.*;

public class Simulator implements JSONable, Observable<EcoSysObserver>{

    private Factory<Animal> animalsFactory;
    private Factory<Region> regionsFactory;
    private RegionManager regionMngr;
    protected List<Animal> animalList;
    private double time;
    private List<EcoSysObserver> observers;

    public Simulator(int cols, int rows, int width, int height, Factory<Animal> animalsFactory, Factory<Region> regionsFactory) {
        this.animalsFactory = animalsFactory;
        this.regionsFactory = regionsFactory;
        regionMngr = new RegionManager(cols, rows, width, height);
        animalList = new ArrayList<Animal>();
        time = 0.0;
        //Da NPE
        this.observers = new ArrayList<>();
    }

    private void setRegion(int row, int col, Region r) {
        regionMngr.setRegion(row, col, r);
        observers.forEach(o -> o.onRegionSet(row, col, getMapInfo(), r));
    }

//METODO FACTORIA, cambiado
    public void setRegion(int row, int col, JSONObject rJson) {
        Region r = regionsFactory.createInstance(rJson);
        setRegion(row, col, r);
    }

    private void addAnimal(Animal a) {
        a.init(regionMngr);
        animalList.add(a);
        regionMngr.registerAnimal(a);
        observers.forEach(o -> o.onAnimalAdded(time, getMapInfo(), getAnimalInfos(), a));
    }

//METODO FACTORIA,cambiado
    public void addAnimal(JSONObject aJson) {
        Animal a = animalsFactory.createInstance(aJson);
        addAnimal(a);
    }

    public MapInfo getMapInfo() {
        return regionMngr;
    }

    public List<? extends AnimalInfo> getAnimals() {
        return Collections.unmodifiableList(animalList);
    }
    public List<AnimalInfo> getAnimalInfos() {
        return Collections.unmodifiableList(animalList);
    }

    public double getTime() {
        return time;
    }

    public void advance(double dt) {
        time += dt;
        Stream<Animal> stream_dead = animalList.stream();
        List<Animal> dead = stream_dead.filter(a -> a.getState() == Animal.State.DEAD).toList();
        dead.forEach(a -> {
            regionMngr.unregisterAnimal(a);
            animalList.remove(a);
        });

        animalList.forEach(a -> {
            a.update(dt);
            regionMngr.updateAnimalRegion(a);
        });
        regionMngr.updateAllRegions(dt);

        Stream<Animal> stream_baby = animalList.stream();
        List<Animal> baby = stream_baby.filter(Animal::isPregnant).toList();
        baby.forEach(a -> {
            Animal bornBaby = a.deliverBaby();
            addAnimal(bornBaby);
        });
        observers.forEach(o -> o.onAdvance(time, getMapInfo(), getAnimalInfos(), dt));
    }

    public JSONObject asJSON() {
        JSONObject sim = new JSONObject();
        sim.put("time", time);
        sim.put("state", regionMngr.asJSON());
        return sim;
    }

    public void reset(int cols, int rows, int width, int height){
        time = 0.0;
        animalList = new ArrayList<Animal>();
        this.regionMngr = new RegionManager(cols, rows, width, height);
        observers.forEach(o -> o.onReset(time, getMapInfo(), getAnimalInfos()));
    }

    @Override
    public void addObserver(EcoSysObserver o) {
        observers.add(o);
        o.onRegister(time, getMapInfo(), getAnimalInfos());
    }
    @Override
    public void removeObserver(EcoSysObserver o) {
        observers.remove(o);
    }

}
