package app;

/**
 * Manages the time limit of the game session using real elapsed time.
 *
 * <p>The timer starts when {@link #start()} is called and measures the
 * elapsed time using system time. It determines expiration automatically
 * without requiring manual ticking.</p>
 */
public class GameTimer {

    /** Game duration in milliseconds. */
    private long durationMillis;

    /** Timestamp when the timer was started. */
    private long startTime;

    /** Indicates whether the timer is currently running. */
    private boolean active;

    /**
     * Creates a timer with zero duration.
     */
    public GameTimer() {
        this(0);
    }

    /**
     * Creates a timer with the given duration in seconds.
     *
     * @param durationSeconds game duration in seconds
     */
    public GameTimer(int durationSeconds) {
        this.durationMillis = Math.max(0, durationSeconds) * 1000L;
        this.active = false;
    }

    /**
     * Starts the timer.
     */
    public void start() {
        this.startTime = java.lang.System.currentTimeMillis();
        this.active = true;
    }

    /**
     * Stops the timer.
     */
    public void stop() {
        this.active = false;
    }

    /**
     * Returns true if the timer has expired.
     *
     * @return true if time is over
     */
    public boolean hasExpired() {
        if (!active) {
            return false;
        }
        long elapsed = java.lang.System.currentTimeMillis() - startTime;
        return elapsed >= durationMillis;
    }

    /**
     * Returns remaining time in seconds.
     *
     * @return remaining time
     */
    public int getRemainingTime() {
        if (!active) {
            return (int) (durationMillis / 1000);
        }

        long elapsed = java.lang.System.currentTimeMillis() - startTime;
        long remaining = durationMillis - elapsed;

        return (int) Math.max(0, remaining / 1000);
    }

    /**
     * Resets the timer with a new duration.
     *
     * @param durationSeconds new duration in seconds
     */
    public void reset(int durationSeconds) {
        this.durationMillis = Math.max(0, durationSeconds) * 1000L;
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
     * Returns configured duration in seconds.
     *
     * @return max time
     */
    public int getMaxTime() {
        return (int) (durationMillis / 1000);
    }
}