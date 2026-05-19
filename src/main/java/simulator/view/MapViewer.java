package simulator.view;

import simulator.model.AnimalPack.Animal;
import simulator.model.AnimalPack.AnimalInfo;
import simulator.model.RegionPack.MapInfo;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JLabel;

/*
 * An incomplete version of  the map viewer, to be completed by students.
 */

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

	// Anchura/altura de la simulación -- se supone que siempre van a ser
	// iguales al tamaño del componente
	//
	// Width/height of the simulation -- they will always be equal to the size
	// of the component
	//
	private int width;
	private int height;

	// Número de filas/columnas de la simulación (regiones)
	private int rows;
	private int cols;

	// Anchura/altura de una región
	int rWidth;
	int rHeight;

	// Mostramos sólo animales con este estado. Los posibles valores de currState
	// son null, y los valores de Animal.State.values(). Si es null mostramos todo.
	Animal.State currentState;

	// En estos atributos guardamos la lista de animales y el tiempo que hemos
	// recibido la última vez para dibujarlos.
	volatile private Collection<AnimalInfo> objs;
	volatile private Double time;

	// Una clase auxiliar para almacenar información sobre una especie.
	private static class SpeciesInfo {
		private Integer count;
		private Color color;

		SpeciesInfo(Color color) {
			count = 0;
			this.color = color;
		}
	}

	// Un mapa para la información sobre las especies.
	Map<String, SpeciesInfo> kindsInfo = new HashMap<>();

	// El font que usamos para dibujar texto.
	private Font textFont = new Font("Arial", Font.BOLD, 12);

	// Indica si mostramos el texto la ayuda o no.
	private boolean showHelp;

	public MapViewer() {
		initGUI();
	}

	private void initGUI() {

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 'h':
					showHelp = !showHelp;
					repaint();
					break;
				case 's':
    				Animal.State[] states = Animal.State.values();
    
					if (currentState == null) {
						currentState = states[0];
					} else {
						int nextIndex = currentState.ordinal() + 1;
						
						if (nextIndex < states.length) {
							currentState = states[nextIndex];
						} else {
							currentState = null;
						}
					}
					repaint();
				default:
				}
			}

		});

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				// Esto es necesario para capturar las teclas cuando el ratón está sobre este
				// componente.
				requestFocus();
			}
		});

		// Por defecto mostramos todos los animales.
		currentState = null;

		// Por defecto mostramos el texto de ayuda.
		showHelp = true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		Graphics2D gr = (Graphics2D) g;
		gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// Cambiar el font para dibujar texto.
		g.setFont(textFont);

		// Dibujar fondo blanco.
		gr.setBackground(Color.WHITE);
		gr.clearRect(0, 0, width, height);

		// Dibujar los animales, el tiempo, información sobre las especies, etc.
		if (objs != null)
			drawObjects(gr, objs, time);

		// Muestra el texto de ayuda si showHelp es true. El texto a mostrar es el
		//      siguiente (en 2 líneas):
		// h: toggle help
		// s: show animals of a specific state
		if (showHelp) {
			g.setColor(Color.RED); // Seleccionamos el color rojo para el texto
    
			// Dibujamos la primera línea
			g.drawString("h: toggle help", 10, 20); 
			
			// Dibujamos la segunda línea un poco más abajo (ej. 20 píxeles más)
			g.drawString("s: show animals of a specific state", 10, 40);
		}

	}

	private boolean visible(AnimalInfo a) {
		return a.getState() == currentState || currentState == null;
	}

	private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {

		// Dibuja el grid de regiones.



		// Dibujar los animales.
		for (AnimalInfo a : animals) {

			// Si no es visible saltamos la iteración.
			if (!visible(a))
				continue;

			// La información sobre la especie de 'a'.
			SpeciesInfo speciesInfo = kindsInfo.get(a.getGeneticCode());

			// Si espInfo es null, añade una entrada correspondiente al mapa. Para el
			//      color usa ViewUtils.getColor(a.getGeneticCode()).

			if (speciesInfo == null) {
				speciesInfo = new SpeciesInfo(ViewUtils.getColor(a.getGeneticCode()));
				kindsInfo.put(a.getGeneticCode(), speciesInfo);
			}

			// Incrementa el contador de la especie (es decir el contador dentro de
			//      speciesInfo).
			speciesInfo.count++;

			int size = (int) Math.round(a.getAge() / 2) + 2;
			int x = (int) a.getPosition().getX();
			int y = (int) a.getPosition().getY();
			g.setColor(speciesInfo.color);
			g.fillRoundRect(x - size / 2, y - size / 2, size, size, size, size);			
		}


		if (currentState != null) {
			drawStringWithRect(g, width - 150, 20, currentState.toString());
		}

		int line = 0; // Variable auxiliar para controlar la línea (posición vertical)
		for (Entry<String, SpeciesInfo> e : kindsInfo.entrySet()) {
			String speciesName = e.getKey();
    		SpeciesInfo info = e.getValue();
    
			// Calculamos la posición Y para que cada especie baje 20 píxeles
			int yPos = 60 + (20 * line);
			drawStringWithRect(g, width - 150, yPos, speciesName + ": " + info.count);
			// Ponemos el color de la especie para el texto
			g.setColor(info.color);
			g.drawString(speciesName + ": " + info.count, width - 150, yPos);
			e.getValue().count = 0; // Reseteamos el contador de la especie.
			line++; // Incrementamos la línea para la siguiente especie
		}
		g.setColor(Color.PINK);
		drawStringWithRect(g, width - 150, 60 + (20 * line), "Time: " + String.format("%.3f", time));
	}

	// Un método que dibuja un texto con un rectángulo.
	void drawStringWithRect(Graphics2D g, int x, int y, String s) {
		Rectangle2D rect = g.getFontMetrics().getStringBounds(s, g);
		g.drawString(s, x, y);
		g.drawRect(x - 1, y - (int) rect.getHeight(), (int) rect.getWidth() + 1, (int) rect.getHeight() + 5);
	}

	@Override
	public void update(List<AnimalInfo> objs, Double time) {
		// Almacena objs y time en los atributos correspondientes y llamar a
		//      repaint() para redibujar el componente.
		this.objs = objs;
		this.time = time;
		repaint();
	}

	@Override
	public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
		// Actualiza los atributos width, height, cols, rows, etc.
		this.width = map.getWidth();
		this.height = map.getHeight();
		this.cols = map.getCols();
		this.rows = map.getRows();
		this.rWidth = width / cols;
		this.rHeight = height / rows;
		this.objs = animals;
		this.time = time;

		// Esto cambia el tamaño del componente, y así cambia el tamaño de la ventana
		// porque en MapWindow llamamos a pack() después de llamar a reset.
		setPreferredSize(new Dimension(map.getWidth(), map.getHeight()));

		// Dibuja el estado.
		update(animals, time);
	}

}
