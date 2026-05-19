package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.misc.Utils;
import simulator.model.EcoSysObserver;
import simulator.model.Simulator;
import simulator.control.Controller;
import simulator.model.AnimalPack.*;
import simulator.model.RegionPack.*;
import simulator.view.MainWindow;
import simulator.factories.*;

public class Main {

    private enum ExecMode {
        BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

        private String tag;
        private String desc;

        private ExecMode(String modeTag, String modeDesc) {
            tag = modeTag;
            desc = modeDesc;
        }

        public String getTag() {
            return tag;
        }

        public String getDesc() {
            return desc;
        }
    }

    // default values for some parameters
    //
    private final static Double DEFAULT_TIME = 10.0; // in seconds
    private final static Double DEFAULT_PS = 100.0;
    private final static Double DEFAULT_DT = 0.03; // in seconds
    private final static String DEFAULT_M = "gui";
    // some attributes to stores values corresponding to command-line parameters
    //
    private static Double time = null;
    public static Double dt = null;
    private static boolean simpleViewer = false;
    private static String inFile = null;
    private static String outFile = null;
    private static ExecMode mode = null;
    public static Factory<Animal> animalsFactory;
    public static Factory<Region> regionsFactory;
    public static Factory<SelectionStrategy> selectionStrategyFactory;
    public static Double stabNumber = null;
    private static boolean stabilityFlag = false;

    private static void parseArgs(String[] args) {

        // define the valid command line options
        //
        Options cmdLineOptions = buildOptions();

        // parse the command line as provided in args
        //
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse(cmdLineOptions, args);
            parseHelpOption(line, cmdLineOptions);
            parseModeOption(line);
            parseInFileOption(line);
            parseOutFileOption(line);
            parseTimeOption(line);
            parseDtOption(line);
            parseSimpleViewerOption(line);
            parsePsOption(line);
            // if there are some remaining arguments, then something wrong is
            // provided in the command line!
            //
            String[] remaining = line.getArgs();
            if (remaining.length > 0) {
                String error = "Illegal arguments:";
                for (String o : remaining) {
                    error += (" " + o);
                }
                throw new ParseException(error);
            }

        } catch (ParseException e) {
            System.err.println(e.getLocalizedMessage());
            System.exit(1);
        }

    }

    private static void parseOutFileOption(CommandLine line) throws ParseException {
        outFile = line.getOptionValue("o");
    }

    private static Options buildOptions() {
        Options cmdLineOptions = new Options();

        // dt
        cmdLineOptions.addOption(Option.builder("dt").longOpt("delta-time").hasArg()
                .desc("A double representing actual time, in seconds, per simulation step. Default value: "
                        + DEFAULT_DT + ".")
                .build());

        // help
        cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());
        // m
        cmdLineOptions.addOption(Option.builder("m").longOpt("mode").hasArg().desc("Execution Mode. Possible values: 'batch' (Batch mode), 'gui' (Graphical User Interface mode). Default value: " + DEFAULT_M + ".").build());
        // input file
        cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

        // output file
        cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg()
                .desc("Output file, where output is written.")
                .build());

        // simple viewer
        cmdLineOptions.addOption(Option.builder("sv").longOpt("simple-viewer")
                .desc("Show the viewer window in console mode.")
                .build());

        // steps
        cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
                .desc("An real number representing the total simulation time in seconds. Default value: "
                        + DEFAULT_TIME + ".")
                .build());
        // stab
        cmdLineOptions.addOption(Option.builder("ps").longOpt("populationChange").hasArg()
                .desc("Veces que ha habido más de r animales en un paso de la simulación ")
                .build());
        return cmdLineOptions;
    }

    private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
        if (line.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
            System.exit(0);
        }
    }

    private static void parseInFileOption(CommandLine line) throws ParseException {
        inFile = line.getOptionValue("i");
        if (mode == ExecMode.BATCH && inFile == null) {
            throw new ParseException("In batch mode an input configuration file is required");
        }
    }

    private static void parseModeOption(CommandLine line) {
        String m = line.getOptionValue("m", DEFAULT_M).toLowerCase();
        if (m.equals("batch")) {
            mode = ExecMode.BATCH;
        } else if (m.equals("gui")) {
            mode = ExecMode.GUI;
        }
    }

    private static void parseTimeOption(CommandLine line) throws ParseException {
        String t = line.getOptionValue("t", DEFAULT_TIME.toString());
        try {
            time = Double.parseDouble(t);
            assert (time >= 0);
        } catch (Exception e) {
            throw new ParseException("Invalid value for time: " + t);
        }
    }

    private static void parsePsOption(CommandLine line) throws ParseException {
        stabilityFlag = line.hasOption("ps");
        if (stabilityFlag) {
            String ps = line.getOptionValue("ps", DEFAULT_PS.toString());
            try {
                stabNumber = Double.parseDouble(ps);
                assert (stabNumber > 0);
            } catch (Exception e) {
                throw new ParseException("Invalid value for ps: " + ps);
            }
        }
    }

    private static void parseDtOption(CommandLine line) throws ParseException {
        String t = line.getOptionValue("dt", DEFAULT_DT.toString());
        try {
            dt = Double.parseDouble(t);
            assert (dt > 0);
        } catch (Exception e) {
            throw new ParseException("Invalid value for dt: " + t);
        }
    }

    private static void parseSimpleViewerOption(CommandLine line) {
        simpleViewer = line.hasOption("sv");
    }

    private static void initFactories() {
        List<Builder<SelectionStrategy>> selectionStrategyBuilders = new ArrayList<>();
        //los builder deben extender un builder de tipo <SelectionStrategy/Animal/Region> generico sino da aqui problemas,
        // igualmente pregunto por el foro para ver
        selectionStrategyBuilders.add(new SelectFirstBuilder());
        selectionStrategyBuilders.add(new SelectClosestBuilder());
        selectionStrategyBuilders.add(new SelectYoungestBuilder());
        Main.selectionStrategyFactory = new BuilderBasedFactory<SelectionStrategy>(selectionStrategyBuilders);

        List<Builder<Animal>> animalBuilders = new ArrayList<>();
        animalBuilders.add(new SheepBuilder(selectionStrategyFactory));
        animalBuilders.add(new WolfBuilder(selectionStrategyFactory));
        Main.animalsFactory = new BuilderBasedFactory<Animal>(animalBuilders);

        List<Builder<Region>> regionBuilders = new ArrayList<>();
        regionBuilders.add(new DefaultRegionBuilder());
        regionBuilders.add(new DynamicSupplyRegionBuilder());
        regionBuilders.add(new DryRegionBuilder());
        regionBuilders.add(new LimitedRegionBuilder());
        Main.regionsFactory = new BuilderBasedFactory<Region>(regionBuilders);
    }

    private static JSONObject loadJSONFile(InputStream in) {
        return new JSONObject(new JSONTokener(in));
    }

    private static void start_batch_mode() throws Exception {
        InputStream is = new FileInputStream(new File(inFile));
        JSONObject dataIn = loadJSONFile(is);
        OutputStream os;
        if (outFile == null) {
            os = System.out;
        } else {
            File parent = new File(outFile).getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            os = new FileOutputStream(new File(outFile));
        }
        Simulator sim = new Simulator(dataIn.getInt("cols"), dataIn.getInt("rows"), dataIn.getInt("width"),
                dataIn.getInt("height"), animalsFactory, regionsFactory);
        Controller c = new Controller(sim);
        c.loadData(dataIn);
        Map<String, Integer> psCounters = new HashMap<>();

        if (stabilityFlag) {
            c.addObserver(new EcoSysObserver() {

                private Map<String, Integer> globalStartCounts = new HashMap<>();

                @Override
                public void onAdvance(double t, MapInfo m, List<AnimalInfo> animals, double dt) {

                    Map<String, Integer> globalEndCounts = new HashMap<>();
                    for (AnimalInfo a : animals) {
                        String species = a.getGeneticCode().toString();
                        globalEndCounts.put(species, globalEndCounts.getOrDefault(species, 0) + 1);
                        psCounters.putIfAbsent(species, 0);
                    }

                    for (String species : psCounters.keySet()) {
                        int countAtStart = globalStartCounts.getOrDefault(species, 0);
                        int countAtEnd = globalEndCounts.getOrDefault(species, 0);

                        // CONDICIÓN: "menor o igual de r al principio y mayor de r al final"
                        if (countAtStart <= stabNumber && countAtEnd > stabNumber) {
                            psCounters.put(species, psCounters.get(species) + 1);
                        }
                    }
                    globalStartCounts = globalEndCounts;
                }

                @Override
                public void onRegister(double t, MapInfo m, List<AnimalInfo> animals) {
                    globalStartCounts.clear();
                    for (AnimalInfo a : animals) {
                        String species = a.getGeneticCode().toString();
                        globalStartCounts.put(species, globalStartCounts.getOrDefault(species, 0) + 1);
                        psCounters.putIfAbsent(species, 0);
                    }
                }

                @Override
                public void onReset(double t, MapInfo m, List<AnimalInfo> animals) {
                }

                @Override
                public void onAnimalAdded(double t, MapInfo m, List<AnimalInfo> animals, AnimalInfo animal) {
                    String species = animal.getGeneticCode().toString();
                    globalStartCounts.put(species, globalStartCounts.getOrDefault(species, 0) + 1);
                    psCounters.putIfAbsent(species, 0);
                }

                @Override
                public void onRegionSet(int r, int c, MapInfo m, RegionInfo ri) {
                }
            });
        }

        // Ejecuta toda la simulación en base a los parámetros provistos
        c.run(time, dt, simpleViewer, os);

        if (os != System.out) {
            os.close();
        }
        is.close();

        if (stabilityFlag) {
            System.out.println("Population change for r=" +  stabNumber + ":\n");
            for (Map.Entry<String, Integer> entry : psCounters.entrySet()) {
                System.out.println(entry.getKey() + " => " + entry.getValue());
            }
        }
    }

    private static void start_GUI_mode() throws Exception {
        OutputStream os;
        if (outFile == null) {
            os = System.out;
        } else {
            File parent = new File(outFile).getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            os = new FileOutputStream(new File(outFile));
        }

        Simulator sim;
        Controller ctrl;
        if (inFile != null) {
            InputStream is = new FileInputStream(new File(inFile));
            JSONObject dataIn = loadJSONFile(is);
            sim = new Simulator(dataIn.getInt("cols"), dataIn.getInt("rows"), dataIn.getInt("width"),
                    dataIn.getInt("height"), animalsFactory, regionsFactory);
            ctrl = new Controller(sim);
            ctrl.loadData(dataIn);
            is.close();
        } else {
            sim = new Simulator(800, 600, 15, 20, animalsFactory, regionsFactory);
            ctrl = new Controller(sim);
        }
        SwingUtilities.invokeAndWait(() -> new MainWindow(ctrl));
        if (os != System.out) {
            os.close();
        }

    }

    private static void start(String[] args) throws Exception {
        initFactories();
        parseArgs(args);
        switch (mode) {
            case BATCH:
                start_batch_mode();
                break;
            case GUI:
                start_GUI_mode();
                break;
        }
    }

    public static void main(String[] args) {
        Utils.RAND.setSeed(2147483647l);
        try {
            start(args);
        } catch (Exception e) {
            System.err.println("Something went wrong ...");
            System.err.println();
            e.printStackTrace();
        }
    }
}
