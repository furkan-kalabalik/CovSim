/**
 * This class resembles individual's moving state. In this state individual
 * is moving and changing location in simulation map. Since location change
 * individual can collide with other individuals and starts conversation and change state
 * to in conversation. Individual also can be infected, and access to hospital is granted, 
 * so individual can go hospital and stop dying. Individual also can't reach to hospital 
 * in time, so individual can die.
 * 
 * Since individual is moving state, moveInPopulation() call doesn't effect individual.
 * @author Furkan Kalabalık
 *
 */
public class Moving implements State {
	/**
	 * Individual that holds dead state.
	 */
	private Individual individual;
	/**
	 * Constructor for state.
	 * @param individual Individual that holds in moving state.
	 */
	public Moving(Individual individual) {
		this.individual = individual;
	}
	
	/**
	 *Individual may collide with other individual. They start to communicate
	 *with max conversation time of each other. Gets into in conversation state.
	 */
	@Override
	public void startConversation(int conversationTime) {
		individual.setCurrentState(individual.getInConversation());
		individual.startConversationTimer(conversationTime);
	}

	/**
	 *Individual may not be reach hospital in time and death timer finish.
	 *Individual's death process start and gets into dead state.
	 */
	@Override
	public void justDie() {
		individual.setCurrentState(individual.getDead());
		individual.burry();
	}

	/**
	 *Individual has access to hospital in time. Stop dying and goes into
	 *in hospital state.
	 */
	@Override
	public void goHospital() {
		individual.setCurrentState(individual.getInHospital());
		individual.dontDie();
		individual.startHealing();
	}

	/**
	 *Since individual is moving in population, doens't effect calling that function.
	 */
	@Override
	public void moveInPopulation() {
		return;
	}

}
