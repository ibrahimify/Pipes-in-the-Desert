package app;

/**
 * Pipe represents a water-carrying segment in the pipe network.
 *
 * <p>A pipe may be intact or punctured, and it may also have a free end.
 * Water is lost when it reaches a punctured pipe or a free pipe end.
 * Only one player may occupy a pipe at a time.</p>
 *
 * @author Team A - E5
 * @version prototype-1.0
 */
public class Pipe extends NetworkElement {

    /**
     * Indicates whether this pipe is punctured and leaking.
     */
    private boolean punctured;

    /**
     * Indicates whether one end of this pipe is free.
     */
    private boolean freeEnd;

    /**
     * Amount of water currently inside this pipe.
     */
    private int currentWater;

    /**
     * Maximum amount of water this pipe can hold.
     */
    private int capacity;

    /**
     * Player currently standing on this pipe.
     */
    private Player occupiedBy;

    /**
     * Creates a pipe with the given id and capacity.
     *
     * @param id unique pipe identifier
     * @param capacity maximum water capacity
     */
    public Pipe(int id, int capacity) {
        super(id);
        this.punctured = false;
        this.freeEnd = false;
        this.currentWater = 0;
        this.capacity = Math.max(1, capacity);
        this.occupiedBy = null;

        java.lang.System.out.println("[Pipe:" + id + "] Created. capacity: " + this.capacity);
    }

    /**
     * Creates a pipe with default capacity.
     *
     * @param id unique pipe identifier
     */
    public Pipe(int id) {
        this(id, 10);
    }

    /**
     * Punctures the pipe.
     */
    public void puncture() {
        java.lang.System.out.println("[Pipe:" + id + "] puncture() called.");

        if (punctured) {
            java.lang.System.out.println("[Pipe:" + id + "] Pipe is already punctured.");
            return;
        }

        punctured = true;
        java.lang.System.out.println("[Pipe:" + id + "] Pipe is now punctured and leaking.");
    }

    /**
     * Repairs the pipe.
     */
    public void repair() {
        java.lang.System.out.println("[Pipe:" + id + "] repair() called.");

        if (!punctured) {
            java.lang.System.out.println("[Pipe:" + id + "] Pipe is already intact.");
            return;
        }

        punctured = false;
        java.lang.System.out.println("[Pipe:" + id + "] Pipe repaired and restored to intact state.");
    }

    /**
     * Returns whether the pipe is punctured.
     *
     * @return true if punctured
     */
    public boolean isPunctured() {
        return punctured;
    }

    /**
     * Returns whether the pipe has a free end.
     *
     * @return true if one end is free
     */
    public boolean hasFreeEnd() {
        return freeEnd;
    }

    /**
     * Connects the free end of this pipe to a target element.
     *
     * @param target element to connect to
     * @return true if connection succeeded
     */
    public boolean connectFreeEnd(NetworkElement target) {
        java.lang.System.out.println("[Pipe:" + id + "] connectFreeEnd() called.");

        if (target == null) {
            java.lang.System.out.println("[Pipe:" + id + "] Target is null.");
            return false;
        }

        if (!freeEnd) {
            java.lang.System.out.println("[Pipe:" + id + "] Pipe has no free end.");
            return false;
        }

        addNeighbor(target);
        target.addNeighbor(this);
        freeEnd = false;

        java.lang.System.out.println("[Pipe:" + id + "] Free end connected to "
                + target.getClass().getSimpleName() + "#" + target.getId() + ".");

        return true;
    }

    /**
     * Marks one end of the pipe as disconnected.
     */
    public void disconnectEnd() {
        java.lang.System.out.println("[Pipe:" + id + "] disconnectEnd() called.");

        if (freeEnd) {
            java.lang.System.out.println("[Pipe:" + id + "] Pipe already has a free end.");
            return;
        }

        freeEnd = true;
        java.lang.System.out.println("[Pipe:" + id + "] One end of the pipe is now free.");
    }

    /**
     * Sets the player currently occupying this pipe.
     * A pipe may only contain one player.
     *
     * @param player player to place on the pipe
     * @return true if the player was placed successfully
     */
    public boolean setOccupant(Player player) {
        java.lang.System.out.println("[Pipe:" + id + "] setOccupant() called.");

        if (player == null) {
            occupiedBy = null;
            return true;
        }

        if (occupiedBy != null && occupiedBy != player) {
            java.lang.System.out.println("[Pipe:" + id + "] Pipe is already occupied.");
            return false;
        }

        occupiedBy = player;
        java.lang.System.out.println("[Pipe:" + id + "] Occupied by " + player.getName() + ".");
        return true;
    }

    /**
     * Removes the current occupant from this pipe.
     */
    public void clearOccupant() {
        java.lang.System.out.println("[Pipe:" + id + "] clearOccupant() called.");
        occupiedBy = null;
    }

    /**
     * Returns whether the pipe is occupied.
     *
     * @return true if occupied
     */
    public boolean isOccupied() {
        return occupiedBy != null;
    }

    /**
     * Adds water to this pipe without exceeding capacity.
     *
     * @param amount amount of water to add
     * @return amount actually accepted
     */
    public int addWater(int amount) {
        java.lang.System.out.println("[Pipe:" + id + "] addWater(" + amount + ") called.");

        if (amount <= 0) {
            return 0;
        }

        int accepted = Math.min(amount, capacity - currentWater);
        currentWater += accepted;

        java.lang.System.out.println("[Pipe:" + id + "] Accepted water: " + accepted
                + ", currentWater: " + currentWater);

        return accepted;
    }

    /**
     * Removes and returns all water from this pipe.
     *
     * @return drained water amount
     */
    public int drainWater() {
        java.lang.System.out.println("[Pipe:" + id + "] drainWater() called.");

        int drained = currentWater;
        currentWater = 0;

        java.lang.System.out.println("[Pipe:" + id + "] Drained water: " + drained);
        return drained;
    }

    /**
     * Returns the current amount of water.
     *
     * @return current water
     */
    public int getCurrentWater() {
        return currentWater;
    }

    /**
     * Sets the current water amount.
     *
     * @param currentWater new water amount
     */
    public void setCurrentWater(int currentWater) {
        this.currentWater = Math.max(0, Math.min(currentWater, capacity));
    }

    /**
     * Returns the free capacity of the pipe.
     *
     * @return remaining capacity
     */
    public int getFreeCapacity() {
        return capacity - currentWater;
    }

    /**
     * Returns pipe capacity.
     *
     * @return capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the player occupying this pipe.
     *
     * @return occupant or null
     */
    public Player getOccupant() {
        return occupiedBy;
    }

}