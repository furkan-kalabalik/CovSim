import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.Timer;

/**
 * This class resemble one particular individual. It extends Rectangle class
 * so it hold 5x5 square place on GUI. It has additional parameters determined
 * randomly. It constructor takes simulation class as a parameter. Simulation class stands for whole
 * world parameters and communications that is needed for every individual. Individuals share
 * same world. Individual has constant speed determined randomly between [1,500] that will be update
 * amount of x, y coordinates every update of GUI. It has a social distance[0,9], mask indicator parameters masked(1.0)
 * or not masked(0.2) and these are used to determine in communication, if individual will be infected or not.
 * To utilize color of infected individual it has a health state enumeration healthy or infected.
 * Also every individual has conversation time for itself. This time is used to determine conversation time
 * between two individual.
 * 
 * Individual uses state pattern to determine its state. Its state changes by itself. So it maintain
 * state by self. By that it doesn't effect any other object other than itself. 
 * An individual can be in 4 different state. If individual is "moving", it is open for communication.
 * If there is a collision with other individual, individual change state to "in conversation". This also effects
 * other side of communication. In state of "in conversation", individual is closed for communication with other
 * individual. In conversation individual can be infected or can be infect other side of communication. This
 * infection determined with both parameters of communicating individuals. 
 * 
 * "In conversation" to simulate conversation there must be spend some time. After that time individual 
 * can be again in "moving" state. In state changes, individual uses its PlayPauseTimer class. This class holds 
 * remaining time in ms format. After that when GUI's main timer traverse all individuals and update them, 
 * this timer values updated and decreased by amount of refresh time of GUI. 
 * The reason behind that it, maintain one timer that updates all timers, so
 * when user pause simulation, all individual keep same state and position. Individual maintains different timers for
 * different states. When it is in conversation, it spend some time in conversation, that is maximum of two communicating
 * individual. When it timer is end, it tries to change its state is moving, if it is possible. 
 * 
 * If individual is infected, it death timer is start. In that time, if individual can't access to hospital,
 * it will die and change state to dead. From there user cannot interact with simulation.
 * 
 * If individual can access to hospital, know it stops all timers and go hospital. Hospital cure for every patient.
 * It also spend some time in hospital. After that time, individual returns to population with healthy state.
 * 
 * Timer events happens asynchronously, for that reason state changing functions is put in synchronized blocks.
 * When a timer is finishes it removes itself from timer list to prevent taking updates again. Also timers has
 * actions when timer is finished. These actions determines what timer does when it is finished. 
 * 
 * Individual also holds a thread. That is used when individual needs hospitalizing. When Individual get sick,
 * after some time it tries to go hospital but if hospital is full, it returns to population. So individual must 
 * move in population when hospital is full but also must try to go hospital to get heal. For that reason, this process
 * needs another thread and this thread does process of trying to go hospital.
 * @author Furkan Kalabalık
 *
 */
public class Individual extends Rectangle{
	/**
	 * Used to generate random values.
	 */
	private Random random = new Random();
	/**
	 * This is reference is whole world. Individual communicate with world
	 * using this reference.
	 */
	private Simulation world;
	/**
	 * Speed of individual on map
	 */
	private int speed;
	/**
	 * X,y components of speed. Used to update position.
	 */
	private int xSpeed, ySpeed;
	/**
	 * X, y coordinates
	 */
	private int x,y;
	/**
	 * Social distance factor of individual
	 */
	private int socialDistance;
	/**
	 * Mask indicator
	 */
	private double maskIndicator;
	/**
	 * Conversation time for this individual but it is determined by using both 
	 * individual.
	 */
	private int conversationTime;
	/**
	 * Predefined size
	 */
	private int individualSize;
	/**
	 * Indicate health status of individual.
	 */
	private HealthStatus healthStatus;
	
	/**
	 * Moving state reference
	 */
	private State moving;
	/**
	 * In conversation state reference
	 */
	private State inConversation;
	/**
	 * Hospital state reference
	 */
	private State inHospital;
	/**
	 * Dead state reference
	 */
	private State dead;
	/**
	 * Holds current state by using above reference
	 */
	private State currentState;
	
	/**
	 * Conversation timer to simulate conversation
	 * Determined by both user.
	 */
	private PlayPauseTimer conversationTimer;
	/**
	 * Death timer starts after infected. Time
	 * of this timer determined by mortality rate of
	 * simulation.
	 */
	private PlayPauseTimer deathTimer;
	/**
	 * Go hospital timer used to start hospitalize need
	 * after 25 second of infection. This timer start thread 
	 * of hospital need.
	 */
	private PlayPauseTimer goHospitalTimer;
	/**
	 * If individual accepted by hospital this timer start 
	 * to simulate healing time at hospital. After that timer
	 * user return to population.
	 */
	private PlayPauseTimer inHospitalTimer;
	
	/**
	 * Thread object for hospital need.
	 */
	private Thread hospitalNeed;
	/**
	 * Timer list of individual. Updated by every GUI refresh.
	 */
	private ArrayList<PlayPauseTimer> timerList;
	
	/**
	 * Constructs one individual. Takes Simulation class as parameter. By
	 * using world, it has access to shared properties of world.
	 * Initialize individual with random values.
	 * Initial current state is moving state. When individual is created, it
	 * is healthy, so set state to healthy and inform world to you are healthy. 
	 * @param world Simulation parameter that is used to access shared parameters and
	 * communication with world.
	 */
	public Individual(Simulation world) {
		moving = new Moving(this);
		inConversation = new InConversation(this);
		inHospital = new InHospital(this);
		dead = new Dead(this);
		currentState = moving;
		speed = random.nextInt(25)+1;
		xSpeed = (int) (Math.pow(-1, random.nextInt(2)) * speed * Math.cos(Math.PI/4));
		ySpeed = (int) (Math.pow(-1, random.nextInt(2)) * speed * Math.cos(Math.PI/4));
		socialDistance = random.nextInt(10);
		conversationTime = random.nextInt(5) + 1;
		double[] maskChoice = {0.2, 1};
		maskIndicator = maskChoice[random.nextInt(2)];
		individualSize = 5;
		healthStatus = HealthStatus.HEALTHY;
		this.world = world;
		x = random.nextInt(world.getSIMULATION_WIDTH() - individualSize);
		y = random.nextInt(world.getSIMULATION_HEIGHT() - individualSize);
		timerList = new ArrayList<>();
		
		hospitalNeed = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					world.getHospital().acceptIndividual();
					synchronized (this) {
						currentState.goHospital();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		world.healthy();
	}
	
	/**
	 * Social distance getter
	 * @return social distance of individual
	 */
	public int getSocialDistance() {
		return socialDistance;
	}
	
	/**
	 * Mask indicator getter
	 * @return Mask indicator of individual
	 */
	public double getMaskIndicator() {
		return maskIndicator;
	}
	
	/**
	 * Conversation time getter
	 * @return Conversation time of individual
	 */
	public int getConversationTime() {
		return conversationTime;
	}
	
	/**
	 * Health status getter
	 * @return Health status of individual
	 */
	public HealthStatus getHealthStatus() {
		return healthStatus;
	}
	
	/**
	 * Health status setter
	 * @param healthStatus health status to be setted.
	 */
	public void setHealthStatus(HealthStatus healthStatus) {
		this.healthStatus = healthStatus;
	}
	
	/**
	 * This is update function of individual. This function called by simulation map view. 
	 * Every update of GUI, if current state of individual is "moving", it is position
	 * increased by x and y speeds. If individual reaches edge of map dimension, it changes its direction
	 * against current direction, if individual already exceeds dimension, it return back to visible place.
	 * 
	 * Also this swing timer update, timer list timers so, individual can change state according to
	 * this timer updates.
	 * 
	 * Also this update method broadcast to world its location update and world uses that information
	 * to check any collision with another individual.
	 */
	public void update()
	{
		if(currentState == moving)
		{
			x += xSpeed;
			y += ySpeed;
			
			if(x < 0 || x > world.getSIMULATION_WIDTH() - individualSize)
				randomizeDirection();
			if(y < 0 || y > world.getSIMULATION_HEIGHT() - individualSize)
				randomizeDirection();
			
			if(x < 0)
				x = 0;
			if(x > world.getSIMULATION_WIDTH() - individualSize)
				x = world.getSIMULATION_WIDTH() - individualSize;
			
			if(y < 0)
				y = 0;
			if(y > world.getSIMULATION_HEIGHT() - individualSize)
				y = world.getSIMULATION_HEIGHT() - individualSize;
		}
		for(PlayPauseTimer timer: timerList)
			timer.update();
		world.locationUpdate(this);
	}
	
	/**
	 * This function used check any collision with another individual.
	 * It uses rectangle bound of individual and using rectangle method
	 * intersects to check collision.
	 * @param otherIndividual other individual to check collision.
	 * @return If there is collision with other individual return true, otherwise false.
	 */
	public boolean collision(Individual otherIndividual)
	{
		Rectangle other = otherIndividual.bounds();
		
		if(this.bounds().intersects(other))
			return true;
		else
			return false;
	}
	
	/**
	 * Gets bounds of current individual
	 * @return bounds of individual
	 */
	public Rectangle bounds()
	{
		return (new Rectangle(x, y, 10, 10));
	}
	
	/**
	 * This method used to draw individual on simulation map.
	 * If current state is moving or in conversation, individual must appear
	 * on map. It uses health status to give more information about health state of
	 * individual. Black used for healthiness, red used for infected.
	 * @param g2d Graphics parameter used for drawing.
	 */
	public void paint(Graphics2D g2d)
	{
		if(currentState == moving || currentState == inConversation)
		{
			switch (healthStatus) {
			case HEALTHY:
				g2d.setColor(Color.BLACK);
				break;
			case INFLECTED:
				g2d.setColor(Color.RED);
				break;
			default:
				break;
			}
	
			g2d.fillRect(x, y, individualSize, individualSize);
		}
	}
	
	/**
	 * After collision and investigation, if individual is get infected, its states and timers
	 * updated by that event. First health status is changed to infected to infected other individuals and
	 * indicate its color.
	 * Then individual's death timer is starts with according to current mortality rate of world.
	 * This timer has action that call justDie() function to set state to dead and parameters and removes timer
	 * from timer list of individual.
	 * 
	 * It also sets up hospital need thread to prepare for starting when goHospital timer is finished.
	 * This thread function uses world's hospital reference and tries to go hospital. If hospital is available
	 * individual will accepted and tries to set up and change state to "in hospital" state by using 
	 * goHospital() function.
	 * 
	 * Also for start that thread there must be some time to spend. This time 25 second. After 25 second individual
	 * understand it is infected and tries to go hospital by starting hospitalNeed thread.
	 * 
	 * At the end individual inform word with its infection to update statistics.
	 */
	public void getCovid()
	{
		setHealthStatus(HealthStatus.INFLECTED);
		deathTimer = new PlayPauseTimer((int) (100*(1 - world.getMortalityRate())), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				justDie();
				timerList.remove(deathTimer);
			}
			
		}, world.getRefreshTime());
		timerList.add(deathTimer);
		
		hospitalNeed = new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					world.getHospital().acceptIndividual();
					goHospital();
				} catch (InterruptedException e) {
				}
				
			}
		});
		
		goHospitalTimer = new PlayPauseTimer(25, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				hospitalNeed.start();
				timerList.remove(goHospitalTimer);
			}
			
		}, world.getRefreshTime());
		timerList.add(goHospitalTimer);
		world.infect();
	}
	
	/**
	 * Go hospital called when individual is accepted by hospital.
	 * It also inform world to its hospitalization.
	 * Since state can change, it happens in synchronized block. 
	 * Current state define action. If user is already dead, hospitalization
	 * doesn't effect anything and individual exits hospital immediately.
	 * Otherwise in case moving or conversation, individual is get into hospital 
	 * immediately to start healing process.
	 */
	public void goHospital()
	{
		synchronized (this) {
		world.hospitalizedIndividual();
		currentState.goHospital();
		}
	}
	
	/**
	 * Removes death timer from timer list to prevent individual to death.
	 * Called in case of succesfully get into hospital.
	 */
	public void dontDie()
	{
		timerList.remove(deathTimer);
	}
	
	/**
	 * When individual is healed, it discharge from hospital to open place
	 * for other individuals. Also a user can get into hospital which is already
	 * dead. It immediately call that function to open place for already living individuals.
	 */
	public void exitHospital()
	{
		world.getHospital().dischargeIndividual();
		world.dischargedIndividual();
	}
	
	/**
	 * This function used to start healing process when individual is get into hospital
	 * right in time. It removes all timer list of individual, and starts a 10 second healing
	 * process. After that healing, individual inform world of its healthiness, change state to "moving" to
	 * return back to population and exits hospital to open place for other infected individuals.
	 */
	public void startHealing()
	{
		timerList.clear();
		inHospitalTimer = new PlayPauseTimer(10, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				world.heal();
				x = random.nextInt(world.getSIMULATION_WIDTH() - individualSize);
				y = random.nextInt(world.getSIMULATION_HEIGHT() - individualSize); 
				moveInPopulation();
				exitHospital();
				healthStatus = HealthStatus.HEALTHY;
				timerList.remove(inHospitalTimer);
			}
			
		}, world.getRefreshTime());
		timerList.add(inHospitalTimer);
	}

	/**
	 * Gets current state reference of individual
	 * @return current state reference
	 */
	public State getCurrentState() {
		return currentState;
	}

	/**
	 * Sets current state reference of individual. 
	 * @param currentState state reference to be setted
	 */
	public void setCurrentState(State currentState) {
		this.currentState = currentState;
	}
	
	/**
	 * Gets moving state reference of individual
	 * @return moving state reference
	 */
	public State getMoving() {
		return moving;
	}
	
	/**
	 * Gets in conversation state reference of individual
	 * @return in conversation state reference
	 */
	public State getInConversation() {
		return inConversation;
	}
	
	/**
	 * Gets in hospital state reference of individual
	 * @return in hospital state reference
	 */
	public State getInHospital() {
		return inHospital;
	}
	
	/**
	 * Gets dead state reference of individual
	 * @return dead state reference
	 */
	public State getDead() {
		return dead;
	}
	
	/**
	 * This process used change current state to in conversation state
	 * @param conversationTime conversation time determined by both communicating individual.
	 */
	public void startConversation(int conversationTime)
	{
		synchronized (this) {
		currentState.startConversation(conversationTime);
		}
	}
	
	/**
	 * If individual successfully change its state to conversation, 
	 * it starts conversation timer. This timer's duration determined by both 
	 * communicating individual. After time is up, individual tries to change 
	 * state to again moving by calling moveInPopulation() function.
	 * 
	 * Timer also added to timer list to get updates.
	 * @param conversationTime spended time on conversation in seconds.
	 */
	public void startConversationTimer(int conversationTime)
	{
		conversationTimer = new PlayPauseTimer(conversationTime, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				moveInPopulation();
				timerList.remove(conversationTimer);
			}
			
		}, world.getRefreshTime());
		timerList.add(conversationTimer);
	}
	
	/**
	 * This function tries to change current state to moving state.
	 * If change is valid, individual starts moving on simulation map.
	 */
	public void moveInPopulation()
	{
		synchronized (this) {
		currentState.moveInPopulation();
		}
	}
	
	/**
	 * This function tries to change current state to dead state.
	 * If this change is valid, individual start dead process.
	 */
	public void justDie()
	{
		synchronized (this) {
		currentState.justDie();
		}
	}
	
	/**
	 * When individual is died, it state must be removed from simulation.
	 * Its timers must be stopped, its death must be broadcasted to world.
	 * Its hospital need thread must end if it is alive.
	 */
	public void burry()
	{
		world.death();
		timerList.clear();
		if(hospitalNeed != null && hospitalNeed.isAlive())
			hospitalNeed.interrupt();
	}
	
	/**
	 * This function randomize speed of individual.
	 */
	public void randomizeDirection()
	{
		xSpeed = (int) (Math.pow(-1, random.nextInt(2)) * speed);
		ySpeed = (int) (Math.pow(-1, random.nextInt(2)) * speed);
	}
	
	/**
	 * This function check if healthy individual is get infected or not, by using
	 * parameters of both communicating individuals and world's current spreading factor.
	 * If probability is bigger than 0 user is get infected by calling getCovid() function.
	 * @param other already infected individual.
	 */
	public void checkInflected(Individual other)
	{
		double probability = world.getSpreadingFactor() * (1+(Math.max(other.getConversationTime(), getConversationTime()) / 10))
				* getMaskIndicator() * other.getMaskIndicator() * (1-(Math.min(other.getSocialDistance(), getSocialDistance()) / 10));
		if(Math.min(probability, 1) > 0.3)
			getCovid();
			
	}
}
