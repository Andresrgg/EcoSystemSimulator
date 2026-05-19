package simulator.control;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.model.AnimalPack.AnimalInfo;
import simulator.model.RegionPack.MapInfo;
import simulator.model.EcoSysObserver;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller {

    Simulator sim;

    public Controller(Simulator sim) {
        this.sim = sim;
    }

    public void loadData(JSONObject data) {
        if (data.has("regions")) {
            setRegions(data);
        }
        if(data.has("animals")){
            JSONArray animals = data.getJSONArray("animals");
        for (int i = 0; i < animals.length(); i++) {

            JSONObject animal = animals.getJSONObject(i);
            int N = animal.getInt("amount");
            JSONObject spec = animal.getJSONObject("spec");
            for (int j = 0; j < N; j++) {
                sim.addAnimal(spec);
            }
        }
        }else{
            throw new IllegalArgumentException("No animals key in JSONinput"); 
        }
    }

    public void run(double t, double dt, boolean sv, OutputStream out) {
        SimpleObjectViewer view = null;
        if (sv) {
            MapInfo m = sim.getMapInfo();
            view = new SimpleObjectViewer("[ECOSYSTEM]", m.getWidth(), m.getHeight(), m.getCols(), m.getRows());
            view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
        }
        JSONObject initState = sim.asJSON();
        while (sim.getTime() < t) {
            sim.advance(dt);
            if (sv) {
                view.update(toAnimalsInfo(sim.getAnimals()), sim.getTime(), dt);
            }
        }
        JSONObject finalState = sim.asJSON();
        JSONObject outJSON = new JSONObject();
        outJSON.put("in", initState);
        outJSON.put("out", finalState);
        PrintStream p = new PrintStream(out);
        p.println(outJSON.toString());
        if (sv) view.close();

    }

    //mira en el enunciando el apartado visor de objetos, y como funciona el outputstream al final del enunciado
    private List<ObjInfo> toAnimalsInfo(List<? extends AnimalInfo> animals) {
        List<ObjInfo> ol = new ArrayList<>(animals.size());
        for (AnimalInfo a : animals) {
            ol.add(new ObjInfo(a.getGeneticCode(), (int) a.getPosition().getX(), (int) a.getPosition().getY(), (int)Math.round(a.getAge())+2));
        }
        return ol;
    }

    public void reset(int cols, int rows, int width, int height) {
        sim.reset(cols, rows, width, height);
    }
    
    public void setRegions(JSONObject rs) {
        JSONArray regionArray = rs.getJSONArray("regions");
        for (int i = 0; i < regionArray.length(); i++) {
            JSONObject region = regionArray.getJSONObject(i);

            JSONArray row = region.getJSONArray("row");
            JSONArray col = region.getJSONArray("col");
            JSONObject spec = region.getJSONObject("spec");
            int r1 = row.getInt(0);
            int r2 = row.getInt(1);

            int c1 = col.getInt(0);
            int c2 = col.getInt(1);
            for (int r = r1; r <= r2; r++) {
                for (int c = c1; c <= c2; c++) {
                    sim.setRegion(r, c, spec);
                }
            }
        }
    }

    public void advance(double dt) {
        sim.advance(dt);
    }

    public void addObserver(EcoSysObserver o) {
        sim.addObserver(o);
    }
    public void removeObserver(EcoSysObserver o) {
        sim.removeObserver(o);
    }
}
