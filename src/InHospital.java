
/**
 * This state resembles individual is current in hospital and healing.
 * This class implements State interface. Since individual is in hospital,
 * it cannot start conversation with any other individual. Also cannot die, 
 * because it is in hospital and healing, this means death timer is stopped.
 * When individual's hospital time is end, it change its state to moving state by
 * calling moveInPopulation() and individual return to map with health.
 * 
 * Calling go hospital doesn't effect individual in this state since it is already in hospital.
 * @author Furkan Kalabalık
 *
 */
public class InHospital implements State{
	private Individual individual;
	
	
	public InHospital(Individual individual) {
		this.individual = individual;
	}
	
	@Override
	public void startConversation(int conversationTime) {
		return;
		
	}

	@Override
	public void justDie() {
		return;
	}

	@Override
	public void goHospital() {
		return;

	}

	@Override
	public void moveInPopulation() {
		individual.randomizeDirection();
		individual.setCurrentState(individual.getMoving());
	}


}
