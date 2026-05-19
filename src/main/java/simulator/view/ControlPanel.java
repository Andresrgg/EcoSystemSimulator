package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;

class ControlPanel extends JPanel {

    private Controller ctrl;
    private ChangeRegionsDialog changeRegionsDialog;
    private AgeLimitDialog ageLimitDialog;
    private JToolBar toolBar;
    private JFileChooser fc;
    private boolean stopped = true; // utilizado en los botones de run/stop
    private JButton quitButton;
    private JButton loadButton;
    private JButton mapButton;
    private JButton regionsButton;
    private JButton runButton;
    private JButton stopButton;
    private JButton ageButton;

    private JSpinner stepsSpinner;
    private JTextField deltaTimeTextField;

    ControlPanel(Controller ctrl) {
        this.ctrl = ctrl;
        initGUI();
    }

    private void initGUI() {
        setLayout(new BorderLayout());
        toolBar = new JToolBar();
        add(toolBar, BorderLayout.PAGE_START);

        // Load Button
        this.loadButton = new JButton();
        this.loadButton.setToolTipText("Load a file");
        this.loadButton.setIcon(new ImageIcon(ViewUtils.class.getResource("/icons/open.png")));
        this.loadButton.addActionListener((e) -> {
            if (this.fc.showOpenDialog(ViewUtils.getWindow(this)) == JFileChooser.APPROVE_OPTION) {
                File file = this.fc.getSelectedFile();
                try {
                    // Leer el fichero y convertirlo a JSONObject
                    InputStream is = new FileInputStream(file);
                    JSONObject openJsonObject = new JSONObject(new JSONTokener(is));
                    this.ctrl.reset(openJsonObject.getInt("cols"), openJsonObject.getInt("rows"), openJsonObject.getInt("width"),
                            openJsonObject.getInt("height"));
                    this.ctrl.loadData(openJsonObject);
                    is.close();

                } catch (Exception ex) {
                    ViewUtils.showErrorMsg(this, "Error loading file: " + ex.getMessage());
                }
            }
        });
        this.toolBar.add(loadButton);
        this.toolBar.addSeparator();

        // Map Button
        this.mapButton = new JButton();
        this.mapButton.setToolTipText("View map");
        this.mapButton.setIcon(new ImageIcon(ViewUtils.class.getResource("/icons/viewer.png")));
        this.mapButton.addActionListener((e) -> {
            new MapWindow(ViewUtils.getWindow(this), ctrl);
        });
        this.toolBar.add(mapButton);

        // Regions Button
        this.regionsButton = new JButton();
        this.regionsButton.setToolTipText("Change Regions Dialog");
        this.regionsButton.setIcon(new ImageIcon(ViewUtils.class.getResource("/icons/regions.png")));
        this.regionsButton.addActionListener((e) -> {
            this.changeRegionsDialog.open(ViewUtils.getWindow(this));
        });
        this.toolBar.add(regionsButton);
        this.toolBar.addSeparator();

        // Run Button
        this.runButton = new JButton();
        this.runButton.setToolTipText("Run");
        this.runButton.setIcon(new ImageIcon(ViewUtils.class.getResource("/icons/run.png")));
        this.runButton.addActionListener((e) -> {
            loadButton.setEnabled(false);
            regionsButton.setEnabled(false);
            runButton.setEnabled(false);
            quitButton.setEnabled(false);

            this.stopped = false;
            double dt = Double.parseDouble(this.deltaTimeTextField.getText());
            int steps = Integer.parseInt(this.stepsSpinner.getValue().toString());
            runSim(steps, dt);
        });
        this.toolBar.add(runButton);

        // Stop Button
        this.stopButton = new JButton();
        this.stopButton.setToolTipText("Stop");
        this.stopButton.setIcon(new ImageIcon(ViewUtils.class.getResource("/icons/stop.png")));
        this.stopButton.addActionListener((e) -> {
            this.stopped = true;
        });
        this.toolBar.add(stopButton);

        // Steps Spinner
        JLabel stepsLabel = new JLabel("Steps:");
        this.toolBar.add(stepsLabel);
        this.stepsSpinner = new JSpinner(new SpinnerNumberModel(10000, 0, Integer.MAX_VALUE, 1));
        this.stepsSpinner.setToolTipText("Number of steps to run");
        this.stepsSpinner.setPreferredSize(new Dimension(100, 20));// para evitar que el spinner se expanda
        this.toolBar.add(this.stepsSpinner);

        // Delta Time Text Field
        JLabel deltaTimeLabel = new JLabel("Delta Time:");
        this.toolBar.add(deltaTimeLabel);
        this.deltaTimeTextField = new JTextField("0.03", 5);
        this.deltaTimeTextField.setToolTipText("Delta-Time");
        this.deltaTimeTextField.setPreferredSize(new Dimension(100, 20));// para evitar que el textField se expanda
        this.toolBar.add(this.deltaTimeTextField);

        // Quit Button
        this.toolBar.add(Box.createGlue()); // this aligns the button to the right
        this.toolBar.addSeparator();
        this.quitButton = new JButton();
        this.quitButton.setToolTipText("Quit");

        this.quitButton.setIcon(new ImageIcon(ViewUtils.class.getResource("/icons/exit.png")));
        this.quitButton.addActionListener((e) -> ViewUtils.quit(this));
        this.toolBar.add(quitButton);
        //AgeLimisStats
        this.ageButton = new JButton();
        this.ageButton.setToolTipText("AgeStats");
        ageButton.setPreferredSize(new Dimension(100,100));
        this.ageButton.setIcon(new ImageIcon(ViewUtils.class.getResource("/icons/stats.png")));
        this.ageButton.addActionListener((e) -> this.ageLimitDialog.open((Frame)ViewUtils.getWindow(this)));
        this.toolBar.add(ageButton);
        this.toolBar.addSeparator();

        // JFileChooser
        this.fc = new JFileChooser();
        this.fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/src/main/resources/examples"));

        // Change Regions Dialog
        this.changeRegionsDialog = new ChangeRegionsDialog(this.ctrl);
        this.ageLimitDialog = new AgeLimitDialog(this.ctrl);
    }

    private void runSim(int n, double dt) {
        if (n > 0 && !this.stopped) {
            try {
                this.ctrl.advance(dt);
                SwingUtilities.invokeLater(() -> runSim(n - 1, dt));
            } catch (Exception e) {
                ViewUtils.showErrorMsg(this, "Error during simulation: " + e.getMessage());

                this.loadButton.setEnabled(true);
                this.mapButton.setEnabled(true);
                this.regionsButton.setEnabled(true);
                this.runButton.setEnabled(true);
                this.quitButton.setEnabled(true);

                this.stopped = true;
            }
        } else {
            this.loadButton.setEnabled(true);
            this.mapButton.setEnabled(true);
            this.regionsButton.setEnabled(true);
            this.runButton.setEnabled(true);
            this.quitButton.setEnabled(true);

            this.stopped = true;
        }
    }
}
