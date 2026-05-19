package simulator.model.RegionPack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.misc.Utils;
import simulator.misc.Vector2D;
import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.AnimalInfo;

public class RegionManager implements AnimalMapView{

    private int cols;
    private int rows;
    private int width;
    private int height;
    private int RegionWidth;
    private int RegionHeigth;
    private Region[][] regions;
    private Map<AnimalInfo, Region> animalRegion;

    //tomo como widht/heigth es altura y anchura total 
    public RegionManager(int cols, int rows, int width, int height) {
        this.cols = cols;
        this.rows = rows;
        this.width = width;
        this.height = height;
        //regionWidth/Heigth=altura/anchura de una celda
        this.RegionHeigth = height / rows;
        this.RegionWidth = width / cols;
        this.regions = new Region[rows][cols];
        this.animalRegion = new HashMap<>();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                this.regions[i][j] = new DefaultRegion();
            }
        }
    }

    public void setRegion(int row, int col, Region r) {
        Region aux = regions[row][col];
        regions[row][col] = r;
        for (Animal a : aux.getAnimals()) {
            regions[row][col].addAnimal(a);
            //actualizo el mapa
            animalRegion.put(a, regions[row][col]);
        }
    }

    public void registerAnimal(Animal a) {
        int col = Double.valueOf(a.getPosition().getX() / getRegionWidth()).intValue();
        int row = Double.valueOf(a.getPosition().getY() / getRegionHeight()).intValue();
        regions[row][col].addAnimal(a);
        animalRegion.put(a, regions[row][col]);
    }

    public void unregisterAnimal(Animal a) {
        Region region = animalRegion.get(a);
        region.removeAnimal(a);
        animalRegion.remove(a);
    }

    public void updateAnimalRegion(Animal a) {
        Region oldRegion = animalRegion.get(a);
        int col = Double.valueOf(a.getPosition().getX() / getRegionWidth()).intValue();
        int row = Double.valueOf(a.getPosition().getY() / getRegionHeight()).intValue();
        if (oldRegion != regions[row][col]) {
            oldRegion.removeAnimal(a);
            regions[row][col].addAnimal(a);
            animalRegion.put(a, regions[row][col]);
        }
    }

    public double getFood(AnimalInfo a, double dt) {
        Region region = animalRegion.get(a);
        if (region != null) {
            return region.getFood(a, dt);
        }
        return 0.0;
    }

    public void updateAllRegions(double dt) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                regions[i][j].update(dt);
            }
        }
    }

    public List<Animal> getAnimalsInRange(Animal e, Predicate<Animal> filter) {
        List<Animal> animalsInRange = new ArrayList<>();
        
        int firstCol = Double.valueOf((e.getPosition().getX() - e.getSightRange()) / RegionWidth).intValue();
        int lastCol = Double.valueOf((e.getPosition().getX() + e.getSightRange()) / RegionWidth).intValue();

        int firstRow = Double.valueOf((e.getPosition().getY() - e.getSightRange()) / RegionHeigth).intValue();
        int lastRow = Double.valueOf((e.getPosition().getY() + e.getSightRange()) / RegionHeigth).intValue();

        for (int i = firstRow; i <= lastRow; i++) {
            int fixedRow = i;
            while (fixedRow < 0) fixedRow = fixedRow + rows;
            while (fixedRow >= rows) fixedRow = fixedRow - rows;

            for (int j = firstCol; j <= lastCol; j++) {
                int fixedCol = j;
                while (fixedCol < 0) fixedCol = fixedCol + cols;
                while (fixedCol >= cols) fixedCol = fixedCol - cols;

                List<Animal> animals = regions[fixedRow][fixedCol].getAnimals();
                for (Animal animal : animals) {
                    if (animal != e && filter.test(animal) && e.getPosition().distanceTo(animal.getPosition()) <= e.getSightRange()){
                        animalsInRange.add(animal);
                    }
                }
            }
        }
        return animalsInRange;
    }

    public boolean isOutOfMap(Vector2D pos) {
        return pos.getX() < 0 || pos.getX() >= width || pos.getY() < 0 || pos.getY() >= height;
    }

    public Vector2D adjustPosition(Vector2D pos) {
        double x = pos.getX();
        double y = pos.getY();
        Vector2D nuevaPos;

        while (x >= width) x = (x - width);
        while (x < 0) x = (x + width);
        while (y >= height) y = (y - height);
        while (y < 0) y = (y + height);
        
        nuevaPos = new Vector2D(x, y);
        return nuevaPos;
    }

    public Vector2D getRandomPosition() {
        double x = Utils.RAND.nextDouble() * width;
        double y = Utils.RAND.nextDouble() * height;
        return new Vector2D(x, y);
    }

    @Override
    public JSONObject asJSON() {
        JSONObject ret = new JSONObject();
        JSONArray array = new JSONArray();
        this.forEach((region) -> {
            JSONObject obj = new JSONObject();
            obj.put("row", region.row());
            obj.put("col", region.col());
            obj.put("data", regions[region.row()][region.col()].asJSON());
            array.put(obj);
        });
        ret.put("regions", array);
        return ret;
    }

    public int getCols() {
        return this.cols;
    }

    public int getRows() {
        return this.rows;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getRegionWidth() {
        return this.RegionWidth;
    }

    public int getRegionHeight() {
        return this.RegionHeigth;
    }

    public Iterator<MapInfo.RegionData> iterator() {
        return new Iterator<MapInfo.RegionData>() {
            protected  int currentRow = 0;
            protected int currentCol = 0;

            @Override
            public boolean hasNext() {
                return currentRow < rows;
            }

            @Override
            public MapInfo.RegionData next() {
                MapInfo.RegionData data = new MapInfo.RegionData(currentRow, currentCol, regions[currentRow][currentCol]);
                currentCol++;
                if(currentCol == cols){
                    currentCol = 0;
                    currentRow++;
                }
                return data;
            }
        };
    }
}
