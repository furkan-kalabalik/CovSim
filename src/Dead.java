
/**
 * This is dead state. Implements State interface.
 * This state indicate that its individual is dead. 
 * In call of starting conversation or move in population is not 
 * valid. May be due to asynchronous events individuals hospital timer
 * is called go hospital timer. If hospital access acquired in dead state
 * immediately exit hospital and open place for other individuals.
 * 
 * Also calling to dead state on dead state doesn't effect individual state.
 * @author Furkan Kalabalık
 *
 */
public class Dead implements State {
	/**
	 * Individual that holds dead state.
	 */
	private Individual individual;
	
	
	/**
	 * Constructor for state.
	 * @param individual Individual that holds dead state.
	 */
	public Dead(Individual individual) {
		this.individual = individual;
	}
	
	/**
	 * Not valid for dead state.
	 */
	@Override
	public void startConversation(int conversationTime) {
		return;
		
	}

	/**
	 * Not valid since individual is already dead.
	 */
	@Override
	public void justDie() {
		return;

	}

	/**
	 * In case of async call for hospital access. Return ventilator
	 * immediately.
	 */
	@Override
	public void goHospital() {
		individual.exitHospital();
	}

	/**
	 * Not valid for dead state.
	 */
	@Override
	public void moveInPopulation() {
		return;
		
	}


}
