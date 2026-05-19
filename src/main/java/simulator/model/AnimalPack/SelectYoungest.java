package simulator.model.AnimalPack;
import java.util.List;
import java.util.Optional;

public class SelectYoungest implements SelectionStrategy {

    @Override
    public Animal select(Animal a, List<Animal> as) {
        Optional<Animal> youngest = as.stream().min((a1,a2) -> 
            Double.valueOf(a1.getAge() - a2.getAge()).intValue());
        return youngest.isEmpty() ? null : youngest.get();
    }
    
}
