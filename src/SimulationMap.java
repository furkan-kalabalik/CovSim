import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * 
 * This class represent canvas of map of simulation. Individual drawed on that canvas.
 * General timer for simulation runs on that class and updates individuals one by one,
 * with location and time sense. And repaint them on canvas. It has function stop timer to 
 * stop view and timer of all world. It holds references to individuals in simulation.
 * @author Furkan Kalabalık
 *
 */
public class SimulationMap extends JPanel{
	/**
	 * Simulation world used to get references and updates.
	 */
	private Simulation world;
	/**
	 * General timer for GUI updates and timer update of individuals.
	 */
	private Timer timer;
	/**
	 * List of individuals in population
	 */
	private ArrayList<Individual> population;
	/**
	 * This constructor sets width and height of canvas, initialize timer and its action
	 * and start timer.
	 * @param simulation access the simulation parameters
	 */
	public SimulationMap(Simulation simulation) {
		this.world = simulation;
		this.setSize(world.getSIMULATION_WIDTH(), world.getSIMULATION_HEIGHT());
		this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.setVisible(true);
		this.setBackground(Color.WHITE);
		population = new ArrayList<Individual>();
		timer = new Timer(world.getRefreshTime(), new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(Individual individual: population)
					individual.update();
						
				repaint();
			}
			
		});
	}
	

	/**
	 * Add one individual reference to population list to update it.
	 * @param individual individual reference
	 */
	public void addIndividual(Individual individual)
	{
		population.add(individual);
	}
	
	/**
	 * Add population references to population list to update it.
	 * @param population newly added population
	 */
	public void addPopulation(ArrayList<Individual> population)
	{
		this.population.addAll(population);
	}
	
	/**
	 * This function overridden and draw shapes on canvas. Traverse all
	 * population list and draw them on canvas.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		for(Individual individual: population)
		{
				Graphics2D g2d = (Graphics2D) g.create();
				individual.paint(g2d);
				g2d.dispose();
		}
	}
	
	/**
	 * Stop view by stopping timer.
	 */
	public void stopView()
	{
		timer.stop();
	}
	
	/**
	 * Start view by starting timer.
	 */
	public void startView()
	{
		timer.start();
	}
}

