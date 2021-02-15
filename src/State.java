
/**
 * This is state interface. Other states derived from this interface.
 * It has certain methods to change state of individual.
 * @author Furkan Kalabalık
 *
 */
public interface State {
	/**
	 * This function tries to start conversation in current state to change.
	 * @param conversationTime indicates time of communication in seconds
	 */
	public void startConversation(int conversationTime);
	/**
	 * This function is used to change state to moving state.
	 */
	public void moveInPopulation();
	/**
	 * This function is used to change state to dead state.
	 */
	public void justDie();
	/**
	 * This function is used to change state to in hospital state.
	 */
	public void goHospital();
}
