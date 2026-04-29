
package app;




/**
 * Manages the time limit of the game session.
 * It counts down from the configured duration and indicates when the game must end.
 */
public class GameTimer {

    /** Remaining game time. */
    private int remainingTime;

    /** Initial configured game duration. */
    private int maxTime;

    /** Indicates whether the timer is currently running. */
    private boolean active;

    /**
     * Creates a timer with zero duration.
     */
    public GameTimer() {
        this(0);
    }

    /**
     * Creates a timer with the given duration.
     *
     * @param maxTime initial game duration
     */
    public GameTimer(int maxTime) {
        this.maxTime = Math.max(0, maxTime);
        this.remainingTime = this.maxTime;
        this.active = false;
    }

    /**
     * Starts the timer.
     */
    public void start() {
        java.lang.System.out.println("GameTimer.start()");
        this.active = true;
    }

    /**
     * Decreases remaining time by one unit if the timer is active.
     */
    public void tick() {
        java.lang.System.out.println("GameTimer.tick()");
        if (active && remainingTime > 0) {
            remainingTime--;
        }
        if (remainingTime <= 0) {
            remainingTime = 0;
            active = false;
        }
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        java.lang.System.out.println("GameTimer.stop()");
        this.active = false;
    }

    /**
     * Returns true if time reached zero.
     *
     * @return true if expired
     */
    public boolean hasExpired() {
        java.lang.System.out.println("GameTimer.hasExpired()");
        return remainingTime <= 0;
    }

    /**
     * Returns remaining time.
     *
     * @return remaining time
     */
    public int getRemainingTime() {
        java.lang.System.out.println("GameTimer.getRemainingTime()");
        return remainingTime;
    }

    /**
     * Resets the timer to a new maximum value.
     *
     * @param maxTime new duration
     */
    public void reset(int maxTime) {
        java.lang.System.out.println("GameTimer.reset()");
        this.maxTime = Math.max(0, maxTime);
        this.remainingTime = this.maxTime;
        this.active = false;
    }

    /**
     * Returns whether the timer is active.
     *
     * @return true if running
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Returns the configured maximum time.
     *
     * @return max time
     */
    public int getMaxTime() {
        return maxTime;
    }
}