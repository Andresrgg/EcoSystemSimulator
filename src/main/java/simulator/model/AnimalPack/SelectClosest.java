package simulator.model.AnimalPack;
import java.util.List;

public class SelectClosest implements SelectionStrategy {

    @Override
    public Animal select(Animal a, List<Animal> as) {
            return  as.stream().min((a1,a2) -> 
            Double.valueOf(a1.getPosition().distanceTo(a.getPosition()) - a2.getPosition().distanceTo(a.getPosition())).intValue())
            .orElse(null);
    }
    
}
