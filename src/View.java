import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.Timer;

/**
 * This is main GUI class. This class holds all GUI related components, create frame and add them to frame.
 * It have buttons that communicate with Simulation and make changes on that simulation references. Also
 * it has timers inside that updates statistics takes from simulation and show them on GUI.
 *
 */
public class View extends JFrame{
	/**
	 * Reference to simulation to get updates
	 */
	private Simulation simulation;
	/**
	 * State to play/pause feature.
	 */
	private boolean running;
	
	/**
	 * This one big constructor initalize all GUI related components, gets Simulation map canvas from simulation
	 * and add it onto frame, add usable buttons and labels about statistics of simulation.
	 * Add graph panel to GUI and draw them dot by dot in real time.
	 * @param world simulation reference
	 */
	public View(Simulation world) {
		this.setTitle("CovSim");
		running = false;
		this.simulation = world;
		
		GraphPanel infectedGraph = new GraphPanel(260, 100, simulation.getPO(), "Infect/Time", Color.RED);
		infectedGraph.setBounds(1010, 290, 260, 100);
		infectedGraph.setVisible(true);
		GraphPanel healthGraph = new GraphPanel(260, 100, simulation.getPO(), "Health/Time", Color.YELLOW);
		healthGraph.setBounds(1010, 400, 260, 100);
		healthGraph.setVisible(true);
		GraphPanel deathGraph = new GraphPanel(260, 100, simulation.getPO(), "Death/Time", Color.BLACK);
		deathGraph.setBounds(1010, 510, 260, 100);
		deathGraph.setVisible(true);
		
		JButton addIndividual = new JButton("Add individual");
		addIndividual.setBounds(1130, 0, 120, 30);
		addIndividual.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				infectedGraph.updateMaxValue(1);
				healthGraph.updateMaxValue(1);
				deathGraph.updateMaxValue(1);
				simulation.addIndividual();
			}
		});
		
		JLabel populationLabel = new JLabel("Population: "+Integer.valueOf(0).toString());
		populationLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		populationLabel.setBounds(1010,40, 120,20);
		JLabel deadLabel = new JLabel("Dead: "+Integer.valueOf(0).toString());
		deadLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		deadLabel.setBounds(1010,70, 120,20);
		JLabel inflectedLabel = new JLabel("Inflected: "+Integer.valueOf(0).toString());
		inflectedLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		inflectedLabel.setBounds(1010,100, 120,20);
		JLabel mortalityLabel = new JLabel("Mortality: "+Integer.valueOf(0).toString());
		mortalityLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		mortalityLabel.setBounds(1140,40, 120,20);
		JLabel spreadingLabel = new JLabel("Spreading: "+Integer.valueOf(0).toString());
		spreadingLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		spreadingLabel.setBounds(1140,70, 120,20);
		JLabel healthyLabel = new JLabel("Healthy: "+Integer.valueOf(0).toString());
		healthyLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		healthyLabel.setBounds(1140,100, 120,20);
		JLabel hospitalizedLabel = new JLabel("Hospitalized: "+Integer.valueOf(0).toString());
		hospitalizedLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		hospitalizedLabel.setBounds(1010,130, 120,20);
		JLabel ventilatorLabel = new JLabel("Total Ventialtor: "+Integer.valueOf(0).toString());
		ventilatorLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		ventilatorLabel.setBounds(1140,130, 120,20);
		JLabel averageSocialDistanceLabel = new JLabel("Avg. Social Dist: "+Integer.valueOf(0).toString());
		averageSocialDistanceLabel.setFont(averageSocialDistanceLabel.getFont().deriveFont(12.0f));
		averageSocialDistanceLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		averageSocialDistanceLabel.setBounds(1010,160, 120,20);
		JLabel maskUsageLabel = new JLabel("Mask usage: %"+Integer.valueOf(0).toString());
		maskUsageLabel.setFont(averageSocialDistanceLabel.getFont().deriveFont(12.0f));
		maskUsageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		maskUsageLabel.setBounds(1140,160, 120,20);
		
		JLabel populationAddLabel = new JLabel("Add population: ");
		JTextField populationAddText = new JTextField();
		populationAddLabel.setBounds(1010,190, 120,20);
		populationAddText.setBounds(1115, 190, 50, 20);
		
		JLabel mortalityRateLabel = new JLabel("Mortality rate: ");
		JTextField mortalityRateText = new JTextField();
		mortalityRateLabel.setBounds(1010,220, 120,20);
		mortalityRateText.setBounds(1115, 220, 50, 20);
		
		JLabel spreadingFactorLabel = new JLabel("Spreading factor: ");
		JTextField spreadingFactorText = new JTextField();
		spreadingFactorLabel.setBounds(1010,250, 120,20);
		spreadingFactorText.setBounds(1115, 250, 50, 20);
		
		
		JButton applyChange = new JButton("Apply!");
		applyChange.setBounds(1180, 190, 90, 80);
		applyChange.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!populationAddText.getText().isEmpty())
				{
					simulation.addPopulation(Integer.valueOf(populationAddText.getText()).intValue());
					infectedGraph.updateMaxValue(Integer.valueOf(populationAddText.getText()).intValue());
					healthGraph.updateMaxValue(Integer.valueOf(populationAddText.getText()).intValue());
					deathGraph.updateMaxValue(Integer.valueOf(populationAddText.getText()).intValue());
				}
				if(!spreadingFactorText.getText().isEmpty())
					simulation.setSpreadingFactor(Double.valueOf(spreadingFactorText.getText()).doubleValue());
				if(!mortalityRateText.getText().isEmpty())
					simulation.setMortalityRate(Double.valueOf(mortalityRateText.getText()).doubleValue());
			}
		});
		
		Timer updateCanvas = new Timer(10, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				populationLabel.setText("Population: "+ Integer.valueOf(simulation.getPO()).toString());
				deadLabel.setText("Dead: "+ Integer.valueOf(simulation.getDeadCount()).toString());
				inflectedLabel.setText("Inflected: "+ Integer.valueOf(simulation.getInfectedCount()).toString());
				mortalityLabel.setText("Mortality: "+String.format("%.1f", simulation.getMortalityRate()));
				spreadingLabel.setText("Spreading: "+String.format("%.1f", simulation.getSpreadingFactor()));
				healthyLabel.setText("Healthy: "+Integer.valueOf(simulation.getHealthyCount()).toString());
				hospitalizedLabel.setText("Hospitalized: "+Integer.valueOf(simulation.getHospitalizedCount()).toString());
				ventilatorLabel.setText("Total Ventilator: "+Integer.valueOf(simulation.getHospital().getFreeVentCount()).toString());
				averageSocialDistanceLabel.setText("Avg. Social Dist: "+String.format("%.1f", simulation.getAverageSocialDistance()));
				maskUsageLabel.setText("Mask usage: %"+String.format("%.1f", simulation.getMaskUsagePercentage()));
			}
		});
		updateCanvas.start();
		
		Timer updateGraph = new Timer(1000, new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				infectedGraph.addPoint(simulation.getInfectedCount());
				infectedGraph.repaint();
				healthGraph.addPoint(simulation.getHealthyCount());
				healthGraph.repaint();
				deathGraph.addPoint(simulation.getDeadCount());
				deathGraph.repaint();
			}
		});
		

		JButton pauseButton = new JButton("Play/Pause");
		pauseButton.setBounds(1010, 0, 120, 30);
		pauseButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(running)
				{
					simulation.pauseSimulation();
					updateGraph.stop();
					running = false;
				}
				else
				{
					simulation.playSimulation();
					updateGraph.start();
					running = true;
				}
				
			}
		});
		
		this.pack();
		this.setSize(1280, 720);
		this.setResizable(false);
		this.setLayout(null);
		this.add(simulation.getSimulationMap());
		this.add(averageSocialDistanceLabel);
		this.add(maskUsageLabel);
		this.add(deathGraph);
		this.add(infectedGraph);
		this.add(healthGraph);
		this.add(hospitalizedLabel);
		this.add(ventilatorLabel);
		this.add(applyChange);
		this.add(healthyLabel);
		this.add(spreadingFactorLabel);
		this.add(spreadingFactorText);
		this.add(mortalityRateLabel);
		this.add(mortalityRateText);
		this.add(mortalityLabel);
		this.add(spreadingLabel);
		this.add(pauseButton);
		this.add(addIndividual);
		this.add(populationLabel);
		this.add(deadLabel);
		this.add(inflectedLabel);
		this.add(populationAddText);
		this.add(populationAddLabel);
		this.setVisible(true);
		this.setBackground(Color.BLACK);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
