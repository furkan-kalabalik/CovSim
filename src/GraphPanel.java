import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * This function used to draw dot graph on GUI.
 * It takes data points from simulation world and updated every second by View class.
 * Put dot for every value and gives information about some change of
 * simulation statistics.
 * @author Furkan Kalabalık
 *
 */
public class GraphPanel extends JPanel{
	/**
	 * Width of graph
	 */
	private int width;
	/**
	 * Height of graph
	 */
	private int height;
	/**
	 * Max value of data point, used to scale.
	 */
	private int maxValue;
	/**
	 * Color for graph dots.
	 */
	private Color color;
	/**
	 * Name of graph.
	 */
	private String graphName;
	/**
	 * Data points container.
	 */
	private ArrayList<Integer> dataPoints;
	
	/**
	 * Constructor for graph panel. It takes height, width, max value, name and color parameter from simulation
	 * view.
	 * 
	 * @param width Width of graph panel
	 * @param height Height of graph panel
	 * @param maxValue Maximum value that is presented on graph
	 * @param graphName name of graph
	 * @param color color of graph dots.
	 */
	public GraphPanel(int width, int height, int maxValue, String graphName, Color color) {
		this.color = color;
		this.graphName = graphName;
		this.width = width;
		this.height = height;
		this.maxValue = maxValue;
		dataPoints = new ArrayList<>();
		setSize(width, height);
		JLabel graphText = new JLabel(graphName);
		graphText.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		graphText.setFont(graphText.getFont().deriveFont(10.0f));
		graphText.setFont(graphText.getFont().deriveFont(1));
		this.add(graphText);
		this.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
		this.setVisible(true);
	}
	
	/**
	 * At current data point value to data points
	 * @param dataPoint current data point value
	 */
	public void addPoint(int dataPoint)
	{
		dataPoints.add(dataPoint);
	}
	
	/**
	 * If population increase, rescale must change, so update max value.
	 * @param updateAmount update amount in max value
	 */
	public void updateMaxValue(int updateAmount)
	{
		maxValue += updateAmount;
	}
	
	/**
	 * This process paints panel by data point, using 3,3 oval shapes.
	 * Represent every x value as timer for 1 second.
	 * Values draw on scaled are by using max value
	 */
	@Override
	protected void paintComponent(Graphics g) {
		int time = 0;
		super.paintComponent(g);
		for(Integer point: dataPoints)
		{
			g.setColor(color);
			g.fillOval(time++, height - ((point.intValue()*height)/maxValue) -3, 3, 3);
		}

	}
}
