package app;

/**
 * Cistern represents a destination node in the pipe network.
 *
 * <p>A Cistern receives water delivered through the network and stores it as
 * part of the plumber team's successful result. According to the Analysis
 * Model, the cistern is also responsible for generating new pipes and pumps
 * that plumbers may later collect and use in the network.</p>
 *
 * <p>This class extends {@link NetworkElement}, because a cistern is a physical
 * element in the pipe system and participates in connectivity just like pipes,
 * pumps, and springs.</p>
 *
 * <p>This is a skeleton-level implementation. Methods print their own names,
 * print relevant state, and perform simple state updates only.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 * @see NetworkElement
 * @see Plumber
 * @see Pipe
 * @see Pump
 */
public class Cistern extends NetworkElement {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /**
     * The total amount of water successfully delivered to this cistern.
     */
    private int storedWater;

    /**
     * The number of newly generated pipes currently available for pickup.
     */
    private int availablePipes;

    /**
     * The number of newly generated pumps currently available for pickup.
     */
    private int availablePumps;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates a Cistern with the given unique identifier.
     *
     * <p>All counters are initialised to zero. Generated components may later
     * be added through {@link #generatePipe()} and {@link #generatePump()}.</p>
     *
     * @param id the unique identifier of this cistern
     */
    public Cistern(int id) {
        super(id);
        this.storedWater = 0;
        this.availablePipes = 0;
        this.availablePumps = 0;

        java.lang.System.out.println("[Cistern:" + id + "] Created.");
    }

    // -----------------------------------------------------------------------
    // Water handling
    // -----------------------------------------------------------------------

    /**
     * Receives water delivered through the pipe network.
     *
     * <p>The delivered amount is added to the total stored water of this
     * cistern.</p>
     *
     * @param amount the amount of water to receive
     */
    public void receiveWater(int amount) {
        java.lang.System.out.println("[Cistern:" + id + "] receiveWater() called. "
                + "Amount: " + amount);

        if (amount <= 0) {
            java.lang.System.out.println("[Cistern:" + id
                    + "] receiveWater() – amount must be positive, skipping.");
            return;
        }

        storedWater += amount;

        java.lang.System.out.println("[Cistern:" + id
                + "] Water received successfully. Total stored water: "
                + storedWater);
    }

    // -----------------------------------------------------------------------
    // Component generation
    // -----------------------------------------------------------------------

    /**
     * Generates one new pipe and makes it available for pickup.
     */
    public void generatePipe() {
        java.lang.System.out.println("[Cistern:" + id + "] generatePipe() called.");
        availablePipes++;
        java.lang.System.out.println("[Cistern:" + id
                + "] New pipe generated. Available pipes: " + availablePipes);
    }

    /**
     * Generates one new pump and makes it available for pickup.
     */
    public void generatePump() {
        java.lang.System.out.println("[Cistern:" + id + "] generatePump() called.");
        availablePumps++;
        java.lang.System.out.println("[Cistern:" + id
                + "] New pump generated. Available pumps: " + availablePumps);
    }

    // -----------------------------------------------------------------------
    // Availability checks
    // -----------------------------------------------------------------------

    /**
     * Returns whether this cistern currently has a generated pipe available.
     *
     * @return {@code true} if at least one pipe is available; {@code false}
     *         otherwise
     */
    public boolean hasAvailablePipe() {
        java.lang.System.out.println("[Cistern:" + id + "] hasAvailablePipe() called. "
                + "Available pipes: " + availablePipes);
        return availablePipes > 0;
    }

    /**
     * Returns whether this cistern currently has a generated pump available.
     *
     * @return {@code true} if at least one pump is available; {@code false}
     *         otherwise
     */
    public boolean hasAvailablePump() {
        java.lang.System.out.println("[Cistern:" + id + "] hasAvailablePump() called. "
                + "Available pumps: " + availablePumps);
        return availablePumps > 0;
    }

    // -----------------------------------------------------------------------
    // Pickup operations
    // -----------------------------------------------------------------------

    /**
     * Removes one available pipe from this cistern if present.
     *
     * @return {@code true} if a pipe was successfully taken; {@code false} if
     *         no pipe was available
     */
    public boolean takePipe() {
        java.lang.System.out.println("[Cistern:" + id + "] takePipe() called.");

        if (availablePipes <= 0) {
            java.lang.System.out.println("[Cistern:" + id
                    + "] takePipe() – no pipe available.");
            return false;
        }

        availablePipes--;
        java.lang.System.out.println("[Cistern:" + id
                + "] Pipe taken successfully. Remaining pipes: "
                + availablePipes);
        return true;
    }

    /**
     * Removes one available pump from this cistern if present.
     *
     * @return {@code true} if a pump was successfully taken; {@code false} if
     *         no pump was available
     */
    public boolean takePump() {
        java.lang.System.out.println("[Cistern:" + id + "] takePump() called.");

        if (availablePumps <= 0) {
            java.lang.System.out.println("[Cistern:" + id
                    + "] takePump() – no pump available.");
            return false;
        }

        availablePumps--;
        java.lang.System.out.println("[Cistern:" + id
                + "] Pump taken successfully. Remaining pumps: "
                + availablePumps);
        return true;
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    /**
     * Returns the amount of water currently stored in this cistern.
     *
     * @return the stored water amount
     */
    public int getStoredWater() {
        return storedWater;
    }

    /**
     * Returns the number of available generated pipes.
     *
     * @return the number of available pipes
     */
    public int getAvailablePipes() {
        return availablePipes;
    }

    /**
     * Returns the number of available generated pumps.
     *
     * @return the number of available pumps
     */
    public int getAvailablePumps() {
        return availablePumps;
    }
}