
/**
 * This interface is mediator interface. This interface used to
 * communicate between individuals. Individual has access to this mediator object and
 * conversate each other through that object. No individual is allowed to communicate without this
 * mediator object. In simulation this interface is embedded to Simulaton class which is world.
 * @author Furkan Kalabalık
 *
 */
public interface Mediator {
	/**
	 * When a individual changes its position, it broadcast that change
	 * to other individuals using Mediator object. Individual call that function 
	 * with its internal mediator reference and check collision with other individuals.
	 * @param individual individual that interact with others
	 */
	public void locationUpdate(Individual individual);
}
