package app;

/**
 * Pipe represents a water-carrying segment in the pipe network.
 *
 * <p>A Pipe may be intact or punctured, and it may also have a free end.
 * According to the Analysis Model, water leaks to the desert when the pipe is
 * punctured or when water reaches a free end. Only one player may occupy a
 * pipe at a time. Pipes may also be split later when a plumber inserts a new
 * pump into the network.</p>
 *
 * <p>This class extends {@link NetworkElement}, because a pipe is a physical
 * network element participating in connectivity and adjacency inside the pipe
 * system.</p>
 *
 * <p>This is a skeleton-level implementation. Methods print their own names,
 * print relevant state, and perform only the minimal state changes needed for
 * scenario checking and sequence-diagram verification.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 * @see NetworkElement
 * @see Player
 * @see Plumber
 * @see Saboteur
 */
public class Pipe extends NetworkElement {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /**
     * Indicates whether this pipe is currently punctured and leaking.
     */
    private boolean punctured;

    /**
     * Indicates whether one end of this pipe is currently free (not connected).
     */
    private boolean freeEnd;

    /**
     * The amount of water currently inside this pipe.
     */
    private int currentWater;

    /**
     * The maximum amount of water this pipe can hold or transfer.
     */
    private int capacity;

    /**
     * The player currently standing on this pipe, if any.
     */
    private Player occupiedBy;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a Pipe with the given unique identifier and capacity.
     *
     * <p>The pipe starts intact, connected on both ends by default, with no
     * occupant and no water currently inside it.</p>
     *
     * @param id the unique identifier of this pipe
     * @param capacity the maximum amount of water this pipe can hold or transfer
     */
    public Pipe(int id, int capacity) {
        super(id);
        this.punctured = false;
        this.freeEnd = false;
        this.currentWater = 0;
        this.capacity = capacity;
        this.occupiedBy = null;

        java.lang.System.out.println("[Pipe:" + id + "] Created. "
                + "capacity: " + capacity);
    }

    /**
     * Creates a Pipe with the given unique identifier and a default capacity.
     *
     * @param id the unique identifier of this pipe
     */
    public Pipe(int id) {
        this(id, 10);
    }

    // -----------------------------------------------------------------------
    // Damage state
    // -----------------------------------------------------------------------

    /**
     * Changes this pipe to punctured state.
     *
     * <p>This corresponds to the saboteur puncturing an intact pipe. After this,
     * water reaching the pipe is considered leaked until a plumber repairs it.</p>
     */
    public void puncture() {
        java.lang.System.out.println("[Pipe:" + id + "] puncture() called.");
        this.punctured = true;
        java.lang.System.out.println("[Pipe:" + id
                + "] Pipe is now punctured and leaking.");
    }

    /**
     * Restores this pipe to intact state.
     *
     * <p>This corresponds to the plumber repairing a punctured pipe.</p>
     */
    public void repair() {
        java.lang.System.out.println("[Pipe:" + id + "] repair() called.");
        this.punctured = false;
        java.lang.System.out.println("[Pipe:" + id
                + "] Pipe repaired and restored to intact state.");
    }

    /**
     * Returns whether this pipe is currently punctured.
     *
     * @return {@code true} if the pipe is punctured; {@code false} otherwise
     */
    public boolean isPunctured() {
        java.lang.System.out.println("[Pipe:" + id + "] isPunctured() called. "
                + "punctured: " + punctured);
        return punctured;
    }

    // -----------------------------------------------------------------------
    // Connection state
    // -----------------------------------------------------------------------

    /**
     * Returns whether this pipe currently has a free end.
     *
     * @return {@code true} if one end is unconnected; {@code false} otherwise
     */
    public boolean hasFreeEnd() {
        java.lang.System.out.println("[Pipe:" + id + "] hasFreeEnd() called. "
                + "freeEnd: " + freeEnd);
        return freeEnd;
    }

    /**
     * Connects the free end of this pipe to another network element.
     *
     * <p>At skeleton level, this method marks the pipe as no longer having a
     * free end and adds the target as a neighbor if it is not null. Structural
     * consistency across both elements is primarily managed by
     * {@code PipeNetwork.connectElements(...)}.</p>
     *
     * @param target the element to connect the free end to
     */
    public void connectFreeEnd(NetworkElement target) {
        java.lang.System.out.println("[Pipe:" + id + "] connectFreeEnd() called.");

        if (target == null) {
            java.lang.System.out.println("[Pipe:" + id
                    + "] connectFreeEnd() – target is null, skipping.");
            return;
        }

        if (!freeEnd) {
            java.lang.System.out.println("[Pipe:" + id
                    + "] connectFreeEnd() – pipe has no free end, skipping.");
            return;
        }

        this.freeEnd = false;
        addNeighbor(target);

        java.lang.System.out.println("[Pipe:" + id
                + "] Free end connected to "
                + target.getClass().getSimpleName() + ".");
    }

    /**
     * Disconnects one end of this pipe.
     *
     * <p>At skeleton level, this method marks the pipe as having a free end.
     * The exact opposite-side structural update is handled by
     * {@code PipeNetwork.disconnectElements(...)} in the broader sequence.</p>
     */
    public void disconnectEnd() {
        java.lang.System.out.println("[Pipe:" + id + "] disconnectEnd() called.");
        this.freeEnd = true;
        java.lang.System.out.println("[Pipe:" + id
                + "] One end of the pipe is now free.");
    }

    // -----------------------------------------------------------------------
    // Occupancy state
    // -----------------------------------------------------------------------

    /**
     * Sets the player currently occupying this pipe.
     *
     * @param player the player standing on this pipe
     */
    public void setOccupant(Player player) {
        java.lang.System.out.println("[Pipe:" + id + "] setOccupant() called. "
                + "Player: " + (player != null ? player.getName() : "null"));

        this.occupiedBy = player;
    }

    /**
     * Removes the current occupant from this pipe.
     */
    public void clearOccupant() {
        java.lang.System.out.println("[Pipe:" + id + "] clearOccupant() called.");
        this.occupiedBy = null;
        java.lang.System.out.println("[Pipe:" + id
                + "] Pipe is now unoccupied.");
    }

    /**
     * Returns whether this pipe is currently occupied by a player.
     *
     * @return {@code true} if a player is standing on the pipe;
     *         {@code false} otherwise
     */
    public boolean isOccupied() {
        java.lang.System.out.println("[Pipe:" + id + "] isOccupied() called. "
                + "occupied: " + (occupiedBy != null));
        return occupiedBy != null;
    }

    // -----------------------------------------------------------------------
    // Water state helpers
    // -----------------------------------------------------------------------

    /**
     * Returns the current amount of water inside this pipe.
     *
     * @return the current water amount
     */
    public int getCurrentWater() {
        return currentWater;
    }

    /**
     * Sets the current amount of water inside this pipe.
     *
     * @param currentWater the new water amount
     */
    public void setCurrentWater(int currentWater) {
        java.lang.System.out.println("[Pipe:" + id + "] setCurrentWater() called. "
                + "New currentWater: " + currentWater);
        this.currentWater = currentWater;
    }

    /**
     * Returns the capacity of this pipe.
     *
     * @return the capacity
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Returns the player currently occupying this pipe, if any.
     *
     * @return the occupying player, or {@code null} if unoccupied
     */
    public Player getOccupant() {
        return occupiedBy;
    }
}
