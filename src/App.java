import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Main app class
 * @author Furkan Kalabalık
 *
 */
public class App {
	/**
	 * Main function
	 * @param args commandline arguments
	 */
	public static void main(String[] args){
		Simulation simulation = new Simulation(200, 1000, 600);
		View frame = new View(simulation);
	}

}
