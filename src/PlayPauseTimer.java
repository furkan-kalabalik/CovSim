import java.awt.event.ActionListener;

import javax.swing.Timer;

/**
 * This is pause play timer. Used to implement state changing timers of individual.
 * This timer takes duration in second and action to indicate what must be done when 
 * timer is up. After that this duration second converted ms. Since updates on timer done by 
 * swing timers and these are works in ms term. In every update of timer, using updateRate, timer
 * duration is decreased. When duration is become 0, the action is fired using dummy swing timer with 
 * duration 0 ms and action is processed.
 * 
 * The existence reason of that timer is when user pause gui, stopping swing timer. When this timer stops,
 *  individual stops updating itself and internal timers of individual is also stops.
 * @author Furkan Kalabalık
 *
 */
public class PlayPauseTimer {
	/**
	 * Dummy timer to process action
	 */
	private Timer timer;
	/**
	 * Remaining time value for action to happen in ms.
	 */
	private long remainingTime;
	/**
	 * Every update how much ms should be substract from remaining time.
	 */
	private int updateRate;
	/**
	 * Action to be placed when time is up.
	 */
	private ActionListener action;
	/**
	 * Constructor for that timer. It takes remaining time in second and update rate of timer with action.
	 * Initialize timer.
	 * @param remainingTime Initial remaining time of timer in second
	 * @param action action to be performed when time is finished
	 * @param updateRate update rate in ms, that will decrease remaining time every update
	 */
	public PlayPauseTimer(int remainingTime, ActionListener action, int updateRate) {
		this.updateRate = updateRate;
		this.remainingTime = remainingTime*1000;
		this.action = action;
	}
	
	/**
	 * When remaining time is zero, action is fired up with that function.
	 * It uses dummy timer.
	 */
	public void fireTimer()
	{
			timer = new Timer((int) remainingTime, action);
			timer.setRepeats(false);
			timer.start();
	}
	
	/**
	 * Updates timer with update rate. 
	 * When remaining time become zero, fire the action.
	 */
	public void update()
	{
		remainingTime -= updateRate;
		if(remainingTime == 0)
		{
			fireTimer();
		}
	}
}
