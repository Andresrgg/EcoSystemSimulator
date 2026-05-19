
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import simulator.misc.Vector2D;
import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.SelectClosest;
import simulator.model.AnimalPack.SelectFirst;
import simulator.model.AnimalPack.SelectionStrategy;
import simulator.model.AnimalPack.Sheep;
import simulator.model.AnimalPack.Wolf;
import simulator.model.RegionPack.RegionManager;

public class AnimalsInRangeTest {

    private RegionManager regMgr;
    private SelectionStrategy stratA;
    private SelectionStrategy stratB;

    @BeforeEach
    void setUp() {
        regMgr = new RegionManager(10, 10, 100, 100);
        stratA = new SelectFirst();
        stratB = new SelectClosest();
    }

    @Test
    void  getAnimalsInRangeTestA() {
        Animal sheep = new Sheep(stratA, stratB, new Vector2D(0.1, 0.1));
        Animal wolf = new Wolf(stratA, stratB, new Vector2D(5, 5));
        Animal wolfN = new Wolf(stratA, stratB, new Vector2D(99, 99));

        regMgr.registerAnimal(sheep);
        regMgr.registerAnimal(wolf);
        regMgr.registerAnimal(wolfN);

        List<Animal> Animals = regMgr.getAnimalsInRange(sheep, n->n.getDiet() == Animal.Diet.CARNIVORE);

        assertEquals(1, Animals.size(), "More than 1 wolf");
        assertTrue(Animals.contains(wolf), "The close wolf shold be in the list");
        assertFalse(Animals.contains(wolfN), "Other farthest should not be in the list");
    }

    @Test
    void getAnimalsInRangeTestB() {
        Animal sheep = new Sheep(stratA, stratB, new Vector2D(50, 50));
        regMgr.registerAnimal(sheep);
        List<Animal> Animals = regMgr.getAnimalsInRange(sheep, n->true);
        assertFalse(Animals.contains(sheep), "An animal should reflect itself");
    }

}
