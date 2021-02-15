import java.util.concurrent.Semaphore;

/**
 * This class represent hospital. It has certain amount of capacity for
 * infected patients. It has simulate hospital queue by using counting semaphore.
 * It has counting semaphore with proportional to initial population size. In
 * hospital for every 100 individual there is one ventilator. When one of the waiting individual
 * accepted to hospital, individual starts healing process and when hospital time is up
 * individual release hospital semaphore for use of other individuals. Since user can change population, ventilator
 * count must have update ability.
 * 
 * @author Furkan Kalabalık
 *
 */
public class Hospital {
	/**
	 * Counting semaphore to simulate hospital queue.
	 */
	private Semaphore semaphore;
	/**
	 * Free ventilator count.
	 */
	private int freeVentCount;
	/**
	 * Total ventilator count
	 */
	private int ventilatorCount;
	/**
	 * Total population size
	 */
	private int populationSize;
	/**
	 * Hospital constructor with initial population size.
	 * Initialize ventilator count and semaphore.
	 * Semaphore is also fair with FIFO policy.
	 * @param populationSize initial population size.
	 */
	public Hospital(int populationSize) {
		this.populationSize = populationSize;
		if(populationSize < 100)
			ventilatorCount = 1;
		else
			ventilatorCount = populationSize / 100;
		freeVentCount = ventilatorCount;
		semaphore = new Semaphore(ventilatorCount, true);
	}
	
	/**
	 * This function called by individual who wants to get in hospital.
	 * If this call success, free ventilator count decrease.
	 * @throws InterruptedException throws when interrupted
	 */
	public void acceptIndividual() throws InterruptedException
	{
		semaphore.acquire();
		synchronized (this) {
			freeVentCount--;
		}
	}
	
	/**
	 * When individual exit hospital, calls this function and open place for
	 * other waiting individuals. Increases free ventilator count.
	 */
	public void dischargeIndividual()
	{
		semaphore.release();
		synchronized (this) {
			freeVentCount++;
		}
	}
	
	/**
	 * When user changes population dynamically, ventialor count must change.
	 * Update of ventilator count done by that function.
	 * @param addedIndividual newly added individual count
	 */
	public void updatePopulation(int addedIndividual)
	{
		synchronized (this) {
			populationSize += addedIndividual;
			int newVentilator = populationSize / 100;
			if(ventilatorCount != newVentilator)
			{
				semaphore.release(newVentilator - ventilatorCount);
				freeVentCount += newVentilator - ventilatorCount;
			}
		}
	}
	
	/**
	 * Returns free ventilator count
	 * @return freeVentCount
	 */
	public int getFreeVentCount()
	{
		return freeVentCount;
	}
}
