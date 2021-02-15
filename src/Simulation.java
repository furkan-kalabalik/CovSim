import java.util.ArrayList;
import java.util.Random;

/**
 * This is class for whole simulation. It holds world's specific constants,
 * And objects of whole world.
 * It has population size and other statistics of simulation. It is also creates GUI and
 * general panel of simulation. It creates hospital hold its reference and present that to
 * individuals. It has GUI specific variables used from GUI.
 * It is also mediator of simulation. Individual communicates through this simulation class.
 * Every individual has access to its simulation world so it communicate with other individuals and
 * keep updates about itself to whole simulation.
 * @author Furkan Kalabalık
 *
 */
public class Simulation implements Mediator{
	/**
	 * Used to generate random values.
	 */
	private Random random = new Random();
	/**
	 * Total population size
	 */
	private int PO; 
	/**
	 * Hospitalized individual count
	 */
	private int hospitalizedCount;
	/**
	 * Healthy individual count
	 */
	private int healthyCount;
	/**
	 * Infected individual count
	 */
	private int infectedCount;
	/**
	 * Total ventilator count
	 */
	private int ventilatorCount;
	/**
	 * Dead individual count
	 */
	private int deadCount;
	/**
	 * List of individual in the simulation.
	 * It is dynamic new one can be added.
	 */
	private ArrayList<Individual> population;
	/**
	 * Spreading factor of simulation world
	 */
	private double spreadingFactor;
	/**
	 * Mortality rate of simulation world
	 */
	private double mortalityRate;
	/**
	 * With and height of total GUI.
	 */
	private int SIMULATION_WIDTH, SIMULATION_HEIGHT;
	/**
	 * Refresh time of Simulation map and location updates in ms.
	 */
	private int refreshTime;
	/**
	 * Canvas that draws individuals and making updates about individuals.
	 */
	private SimulationMap simulationMap;
	/**
	 * Hospital object reference.
	 */
	private Hospital hospital;
	/**
	 * Mask usage percentage statistic
	 */
	private double maskUsagePercentage;
	/**
	 * Average social distance statistic
	 */
	private double averageSocialDistance;
	
	/**
	 * This constructor initialize whole simulation. It takes initial population width
	 * total size of GUI. Randomly select mortality rate and spreading factor. Creates individual and 
	 * add them to its list while holding their statistics. Sets refresh time of GUI and creates Simulation map
	 * that used to present individuals and canvas of map. Also add population to Simulation map, so map can access to
	 * references of individuals and infects first individual to start spreading disease.
	 * @param initialPopulation initial population size.
	 * @param SIMULATION_WIDTH width of total gui
	 * @param SIMULATION_HEIGHT height of total gui
	 */
	public Simulation(int initialPopulation, int SIMULATION_WIDTH, int SIMULATION_HEIGHT) {
		PO = initialPopulation;
		hospitalizedCount = 0;
		infectedCount = 0;
		deadCount = 0;
		maskUsagePercentage = 0;
		averageSocialDistance = 0;
		this.SIMULATION_HEIGHT = SIMULATION_HEIGHT;
		this.SIMULATION_WIDTH = SIMULATION_WIDTH;
		spreadingFactor = random.nextDouble()*(1.0-0.5)+0.5;
		mortalityRate = random.nextDouble()*(0.9-0.1)+0.1;
		population = new ArrayList<>();
		for(int i = 0; i < PO; i++)
		{		
			Individual individual = new Individual(this);
			population.add(individual);
			if(individual.getMaskIndicator() == 1.0)
				maskUsagePercentage += 1;
			averageSocialDistance += individual.getSocialDistance();
		}
		refreshTime = 100;
		simulationMap = new SimulationMap(this);
		simulationMap.addPopulation(population);
		hospital = new Hospital(PO);
		
		population.get(0).getCovid();
	}

	/**
	 * Used to add one particular individual to simulation.
	 * Just like constructor process add individual, counts its statistics,
	 * add it to individual list of both simulation and view. Also updates hospital ventilator
	 * count and look for any change in hospital.
	 */
	public void addIndividual()
	{
		Individual individual = new Individual(this);
		if(individual.getMaskIndicator() == 1.0)
			maskUsagePercentage += 1;
		averageSocialDistance += individual.getSocialDistance();
		population.add(individual);
		simulationMap.addIndividual(individual);
		hospital.updatePopulation(1);
		PO++;
		int newVentilator = PO / 100;
		if(ventilatorCount != newVentilator)
			ventilatorCount = newVentilator;
	}
	
	
	/**
	 * Used to add some amount of individual to simulation.
	 * Just like constructor process add individual, counts its statistics,
	 * add them to individual list of both simulation and view. Also updates hospital ventilator
	 * count and look for any change in hospital.
	 * @param populationSize newly added population size
	 */
	public void addPopulation(int populationSize)
	{
		Individual individual;
		for(int i = 0; i < populationSize; i++)
		{
			individual = new Individual(this);
			if(individual.getMaskIndicator() == 1.0)
				maskUsagePercentage += 1;
			averageSocialDistance += individual.getSocialDistance();
			PO++;
			simulationMap.addIndividual(individual);
		}
		hospital.updatePopulation(populationSize);
	}

	/**
	 * Getter for spreading factor
	 * @return spreading factor
	 */
	public double getSpreadingFactor() {
		return spreadingFactor;
	}

	/**
	 * Setter for spreading factor
	 * @param spreadingFactor new spreading factor.
	 */
	public void setSpreadingFactor(double spreadingFactor) {
		this.spreadingFactor = spreadingFactor;
	}

	/**
	 * Getter for mortality rate
	 * @return mortality rate
	 */
	public double getMortalityRate() {
		return mortalityRate;
	}
	
	/**
	 * Setter mortality rate
	 * @param mortalityRate new mortality rate.
	 */
	public void setMortalityRate(double mortalityRate) {
		this.mortalityRate = mortalityRate;
	}

	/**
	 * Getter for total simulation width
	 * @return Simulation GUI width
	 */
	public int getSIMULATION_WIDTH() {
		return SIMULATION_WIDTH;
	}
	
	/**
	 * Getter for total simulation height
	 * @return Simulation GUI height
	 */
	public int getSIMULATION_HEIGHT() {
		return SIMULATION_HEIGHT;
	}

	/**
	 * This function is override from mediator interface. With this function Individuals can
	 * communicate with other individuals. An individual need to communicate with another individual for
	 * the collision detection reason. If collision detected, individual can communicate with other individual,
	 * can be infect, and conversate with that individual. For that reason, when an individual changes its location,
	 * it broadcast this change to all other individuals using mediator class which is simulation itself. 
	 * 
	 * After that individual, look for other individuals in the population. If it collide with other individual that is moving
	 * and their bounds are intersect, they match with each other for conversation. After that they can infect each other
	 * according to their parameters and simulation world parameters. Then they start conversation in maximum of conversation time
	 * of each other. Then individual change state and get in in conversation state.
	 */
	@Override
	public void locationUpdate(Individual individual) {
		int conversationTime;
		for(Individual otherIndividual: population)
		{
			if(otherIndividual != individual && individual.collision(otherIndividual) &&
					individual.getCurrentState() == individual.getMoving() &&
					otherIndividual.getCurrentState() == otherIndividual.getMoving())
			{
				if(otherIndividual.getHealthStatus() == HealthStatus.INFLECTED && 
						individual.getHealthStatus() == HealthStatus.HEALTHY)
					individual.checkInflected(otherIndividual);
				else if(otherIndividual.getHealthStatus() == HealthStatus.HEALTHY && 
						individual.getHealthStatus() == HealthStatus.INFLECTED)
					otherIndividual.checkInflected(individual);
				conversationTime = Math.max(individual.getConversationTime(), otherIndividual.getConversationTime());
				individual.startConversation(conversationTime);
				otherIndividual.startConversation(conversationTime);
			}
		}
	}

	/**
	 * getter for map panel reference
	 * @return map canvas
	 */
	public SimulationMap getSimulationMap() {
		return simulationMap;
	}

	/**
	 * Getter for refresh time of GUI. This also general timer for whole Individual states.
	 * @return refresh time of timer
	 */
	public int getRefreshTime() {
		return refreshTime;
	}
	
	/**
	 * Stop timer of simulation map view. This will stops swing timer of simulation map and
	 * freeze timers and images of individuals.
	 */
	public void pauseSimulation()
	{
		simulationMap.stopView();
	}
	
	/**
	 * Starts timer of simulation map view. Individual and their timers continue executing.
	 */
	public void playSimulation()
	{
		simulationMap.startView();
	}
	
	/**
	 * Used by individual to inform world about infected individual
	 */
	public void infect()
	{
		infectedCount++;
		healthyCount--;
	}
	
	/**
	 * Used by individual to inform world about healed individual
	 */
	public void heal()
	{
		infectedCount--;
		healthyCount++;
	}
	
	/**
	 * Used by individual to inform world about dead individual
	 */
	public void death()
	{
		deadCount++;
		infectedCount--;
	}
	
	/**
	 * Used by individual to inform world about healthy individual in simulation
	 */
	public void healthy()
	{
		healthyCount++;
	}

	/**
	 * Getter for healthy count
	 * @return healthy count
	 */
	public int getHealthyCount() {
		return healthyCount;
	}
	
	/**
	 * Getter for infected count
	 * @return infected count
	 */
	public int getInfectedCount() {
		return infectedCount;
	}
	
	/**
	 * Getter for dead count
	 * @return dead count
	 */
	public int getDeadCount() {
		return deadCount;
	}
	
	/**
	 * Getter for population size
	 * @return population size
	 */
	public int getPO() {
		return PO;
	}
		
	/**
	 * Getter for hospital reference to use of individuals.
	 * @return hospital reference
	 */
	public Hospital getHospital() {
		return hospital;
	}
	
	/**
	 * Getter for hospitalized count
	 * @return hospitalized count
	 */
	public int getHospitalizedCount() {
		return hospitalizedCount;
	}
	
	/**
	 * Used by individual to inform world about an individual at hospital
	 */
	public void hospitalizedIndividual()
	{
		hospitalizedCount++;
	}
	
	/**
	 * Used by individual to inform world about an individual exit hospital
	 */
	public void dischargedIndividual()
	{
		hospitalizedCount--;
	}
	

	/**
	 * Getter for ventiltor count
	 * @return ventiltor count
	 */
	public int getVentilatorCount() {
		return ventilatorCount;
	}
	
	/**
	 * Calculates mask usage percentage and returns
	 * @return mask usage percentage
	 */
	public double getMaskUsagePercentage() {
		return (100*maskUsagePercentage) / PO;
	}

	/**
	 * Calculates average social distance and returns
	 * @return average social distance
	 */
	public double getAverageSocialDistance() {
		return averageSocialDistance / PO;
	}
}
