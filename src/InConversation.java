
/**
 * This class resembles In conversation state. Implements State interface.
 * This class implies its individual is in conversation state. From there
 * individual can go in hospital, by access to hospital granted. Also may be
 * death timer is up before go hospital and individual can die. Also conversation may end
 * user goes into moving state to move in map.
 * 
 * This calls is valid for that state. Calling start conversation doesn't effect individual,
 *  since we already in communication state.
 * @author Furkan Kalabalık
 *
 */
public class InConversation implements State {	
	/**
	 * Individual that holds dead state.
	 */
	private Individual individual;
	
	/**
	 * Constructor for state.
	 * @param individual Individual that holds in conversation state.
	 */
	public InConversation(Individual individual) {
		this.individual = individual;
	}
	
	/**
	 *Since individual is already in conversation doesn't effect.
	 */
	@Override
	public void startConversation(int conversationTime) {
		return;
	}

	/**
	 *Death timer may finish. So individual is dead. Start death process and change state.
	 */
	@Override
	public void justDie() {
		individual.setCurrentState(individual.getDead());
		individual.burry();
	}

	/**
	 *User can have access to hospital and infected. Start hospitalization process and healing.
	 */
	@Override
	public void goHospital() {
		individual.setCurrentState(individual.getInHospital());
		individual.dontDie();
		individual.startHealing();
	}

	/**
	 *Conversation time is up. Return to moving freely in map.
	 */
	@Override
	public void moveInPopulation() {
		individual.randomizeDirection();
		individual.setCurrentState(individual.getMoving());
	}


}
