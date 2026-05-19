package simulator.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.json.JSONArray;
import org.json.JSONObject;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalPack.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.RegionPack.MapInfo;
import simulator.model.RegionPack.RegionInfo;

public class ChangeRegionsDialog extends JDialog implements EcoSysObserver {

    private DefaultComboBoxModel<String> regionsModel;
    private DefaultComboBoxModel<Integer> fromRowModel;
    private DefaultComboBoxModel<Integer> toRowModel;
    private DefaultComboBoxModel<Integer> fromColModel;
    private DefaultComboBoxModel<Integer> toColModel;

    private DefaultTableModel dataTableModel;
    private Controller ctrl;
    private List<JSONObject> regionsInfo;

    private String[] headers = {"Key", "Value", "Description"};

    private int status;

    ChangeRegionsDialog(Controller ctrl) {
        super((Frame) null, true);
        this.ctrl = ctrl;
        this.status = 0;
        initGUI();
        ctrl.addObserver(this);
    }

    private void initGUI() {
        setTitle("Change Regions");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        //  crea varios paneles para organizar los componentes visuales en el
        //      dialogo, y añadelos al mainpanel. P.ej., uno para el texto de ayuda,
        //      uno para la tabla, uno para los combobox, y uno para los botones.
        //  crear el texto de ayuda que aparece en la parte superior del diálogo y
        //      añadirlo al panel correspondiente diálogo
        JLabel helpLabel = new JLabel("<html><p>Select a region type, the rows/cols interval, and provide values for the parameters in the Value column (default values <br> are used for parameters with no value).</p></html>");
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        helpPanel.add(helpLabel);
        mainPanel.add(helpPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        // this.regionsInfo se usará para establecer la información en la tabla
        //MUY IMPORTANTE,EN LOS EXAMENES A VECES PIDEN AÑADIR OTRA REGION SI SE HACE SE DEBE HACER CON GETINFO NO CON CLAVES EXPLICITAS
        this.regionsInfo = Main.regionsFactory.getInfo();

        // this.dataTableModel es un modelo de tabla que incluye todos los parámetros de
        // la region
        this.dataTableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 ? true : false;
            }
        };
        this.dataTableModel.setColumnIdentifiers(this.headers);

        //  crear un JTable que use dataTableModel, y añadirlo al diálogo
        JTable paramsTable = new JTable(dataTableModel);
        JScrollPane scrollTable = new JScrollPane(paramsTable);
        mainPanel.add(scrollTable);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
    
        JPanel selectorsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        selectorsPanel.add(new JLabel("Region type: "));

        // this.regionsModel es un modelo de combobox que incluye los tipos de regiones
        this.regionsModel = new DefaultComboBoxModel<>();

        //  añadir la descripción de todas las regiones a regionsModel. Para eso
        //      usa la clave “desc” o “type” de los JSONObject en regionsInfo,
        //      ya que estos nos dan información sobre lo que puede crear la factoría.
        for (JSONObject info : regionsInfo) {
            regionsModel.addElement(info.getString("type"));
        }
        
        //  crear un combobox que use regionsModel y añadirlo al diálogo.
        JComboBox<String> regionsCombo = new JComboBox<>(regionsModel);
        regionsCombo.addActionListener(e -> updateParamsTable(regionsCombo.getSelectedIndex()));
        selectorsPanel.add(regionsCombo);
        //  crear 4 modelos de combobox para this.fromRowModel, this.toRowModel,
        //      this.fromColModel y this.toColModel.
        this.fromRowModel = new DefaultComboBoxModel<>();
        this.toRowModel = new DefaultComboBoxModel<>();
        this.fromColModel = new DefaultComboBoxModel<>();
        this.toColModel = new DefaultComboBoxModel<>();
        //  crear 4 combobox que usen estos modelos y añadirlos al diálogo.
        selectorsPanel.add(new JLabel(" Row from/to: "));
        selectorsPanel.add(new JComboBox<>(fromRowModel));
        selectorsPanel.add(new JComboBox<>(toRowModel));

        selectorsPanel.add(new JLabel(" Column from/to: "));
        selectorsPanel.add(new JComboBox<>(fromColModel));
        selectorsPanel.add(new JComboBox<>(toColModel));
        mainPanel.add(selectorsPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));

        //  crear los botones OK y Cancel y añadirlos al diálogo.
        JPanel okCancelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> {
            this.status = 0;
            setVisible(false);
        });

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> handleOK());

        okCancelPanel.add(cancelButton);
        okCancelPanel.add(okButton);
        mainPanel.add(okCancelPanel);
        if (!regionsInfo.isEmpty()) {
            updateParamsTable(0);
        }
        setPreferredSize(new Dimension(700, 400)); // puedes usar otro tamaño
        pack();
        setResizable(false);
        setVisible(false);
    }

    private void handleOK() {
        try {

            JSONObject region_data = new JSONObject();
            for (int i = 0; i < dataTableModel.getRowCount(); i++) {
                String key = (String) dataTableModel.getValueAt(i, 0);
                String value = (String) dataTableModel.getValueAt(i, 1);

                if (value != null && !value.trim().isEmpty()) {
                    //Se guarda como int si es posible si no como String
                    try {
                        region_data.put(key, Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        region_data.put(key, value);
                    }
                }
            }

            int selectedIndex = regionsModel.getIndexOf(regionsModel.getSelectedItem());
            String region_type = regionsInfo.get(selectedIndex).getString("type");

            int row_from = (int) fromRowModel.getSelectedItem();
            int row_to = (int) toRowModel.getSelectedItem();
            int col_from = (int) fromColModel.getSelectedItem();
            int col_to = (int) toColModel.getSelectedItem();

            JSONObject spec = new JSONObject();
            spec.put("type", region_type);
            spec.put("data", region_data);

            JSONObject regionUpdate = new JSONObject();
            regionUpdate.put("row", new JSONArray(new int[]{row_from, row_to}));
            regionUpdate.put("col", new JSONArray(new int[]{col_from, col_to}));
            regionUpdate.put("spec", spec);

            JSONArray regionsArray = new JSONArray();
            regionsArray.put(regionUpdate);

            JSONObject finalJSON = new JSONObject();
            finalJSON.put("regions", regionsArray);

            ctrl.setRegions(finalJSON);

            this.status = 1;
            setVisible(false);

        } catch (Exception e) {
            ViewUtils.showErrorMsg(this, "Error setting regions: " + e.getMessage());
        }
    }

    public void open(Frame parent) {
        setLocation(
                parent.getLocation().x + parent.getWidth() / 2 - getWidth() / 2,
                parent.getLocation().y + parent.getHeight() / 2 - getHeight() / 2);
        pack();
        setVisible(true);
    }
    private void updateParamsTable(int index) {
        dataTableModel.setRowCount(0);
        JSONObject info = regionsInfo.get(index);
        JSONObject data = info.getJSONObject("data");
        
        for (String k : data.keySet()) {
            dataTableModel.addRow(new Object[] { k, "", data.get(k) });
        }
    }
    private void updateCoordModels(MapInfo m) {
        fromRowModel.removeAllElements();
        toRowModel.removeAllElements();
        fromColModel.removeAllElements();
        toColModel.removeAllElements();

        for (int i = 0; i < m.getRows(); i++) {
            fromRowModel.addElement(i);
            toRowModel.addElement(i);
        }
        for (int i = 0; i < m.getCols(); i++) {
            fromColModel.addElement(i);
            toColModel.addElement(i);
        }
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        updateCoordModels(map);
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        updateCoordModels(map);
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
    }

    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
    }

    @Override
    public void onAdvance(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
    }
}
