package simulator.model.RegionPack;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.Entity;
import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.AnimalInfo;

public abstract class Region implements Entity, FoodSupplier, RegionInfo{

    protected List<Animal> animals;

    public Region() {
        this.animals = new ArrayList<>();
    }

    final void addAnimal(Animal a) {
        animals.add(a);
    }

    final void removeAnimal(Animal a) {
        animals.remove(a);
    }

    final List<Animal> getAnimals() {
        return Collections.unmodifiableList(animals);
    }
    public JSONObject asJSON() {
        JSONObject json = new JSONObject();
        JSONArray animalsArray = new JSONArray();
        for (Animal a : this.animals) {
            animalsArray.put(a.asJSON());
        }
        json.put("animals", animalsArray);
        return json;
    }

    public List<AnimalInfo> getAnimalsInfo() {
        return Collections.unmodifiableList(animals);
    }
}
