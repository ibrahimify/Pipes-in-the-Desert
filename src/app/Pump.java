package app;

import java.util.ArrayList;
import java.util.List;

import app.NetworkElement;
import app.Pipe;

/**
 * Pump represents an active routing element in the pipe network.
 *
 * <p>A Pump can have multiple connected {@link Pipe} objects, but only one
 * active input pipe and one active output pipe at a time. It may malfunction,
 * and when broken it cannot transfer water. It also has a connection limit and
 * an internal water buffer, both of which come directly from the problem
 * definition and the Analysis Model.</p>
 *
 * <p>This class extends {@link NetworkElement}, because a pump is a physical
 * network element connected to pipes and participating in the topology of the
 * game world.</p>
 *
 * <p>This is a skeleton-level implementation. Methods print their own names,
 * print relevant state, and perform minimal state changes consistent with the
 * documented class responsibilities and use cases.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 * @see NetworkElement
 * @see Pipe
 * @see Plumber
 * @see Saboteur
 */
public class Pump extends NetworkElement {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /**
     * The list of pipes currently attached to this pump.
     */
    private List<Pipe> connectedPipes;

    /**
     * The currently selected active input pipe.
     */
    private Pipe activeInput;

    /**
     * The currently selected active output pipe.
     */
    private Pipe activeOutput;

    /**
     * Indicates whether this pump is currently broken.
     */
    private boolean broken;

    /**
     * Internal temporary water storage of the pump.
     */
    private int buffer;

    /**
     * Maximum number of pipes that may be connected to this pump.
     */
    private int connectionLimit;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates a Pump with the given unique identifier and connection limit.
     *
     * <p>The pump starts in working condition, with no active direction
     * selected, an empty connected-pipes list, and an empty internal buffer.</p>
     *
     * @param id the unique identifier of this pump
     * @param connectionLimit the maximum number of connectable pipes
     */
    public Pump(int id, int connectionLimit) {
        super(id);
        this.connectedPipes = new ArrayList<>();
        this.activeInput = null;
        this.activeOutput = null;
        this.broken = false;
        this.buffer = 0;
        this.connectionLimit = connectionLimit;

        java.lang.System.out.println("[Pump:" + id + "] Created. "
                + "connectionLimit: " + connectionLimit);
    }

    /**
     * Creates a Pump with the given unique identifier and a default connection
     * limit.
     *
     * @param id the unique identifier of this pump
     */
    public Pump(int id) {
        this(id, 4);
    }

    // -----------------------------------------------------------------------
    // Direction management
    // -----------------------------------------------------------------------

    /**
     * Sets the active input and active output pipes of this pump.
     *
     * <p>This method corresponds to the Change Pump Direction use case, where a
     * plumber or saboteur selects a working pump and assigns new routing.</p>
     *
     * @param input the pipe to set as active input
     * @param output the pipe to set as active output
     */
    public void setDirection(Pipe input, Pipe output) {
        java.lang.System.out.println("[Pump:" + id + "] setDirection() called.");

        if (input == null || output == null) {
            java.lang.System.out.println("[Pump:" + id
                    + "] setDirection() – input or output is null, skipping.");
            return;
        }

        if (broken) {
            java.lang.System.out.println("[Pump:" + id
                    + "] setDirection() – pump is broken, direction not changed.");
            return;
        }

        if (!connectedPipes.contains(input) || !connectedPipes.contains(output)) {
            java.lang.System.out.println("[Pump:" + id
                    + "] setDirection() – input/output pipe is not connected "
                    + "to this pump, skipping.");
            return;
        }

        this.activeInput = input;
        this.activeOutput = output;

        java.lang.System.out.println("[Pump:" + id
                + "] Direction updated successfully.");
    }

    // -----------------------------------------------------------------------
    // Breakdown and repair
    // -----------------------------------------------------------------------

    /**
     * Changes this pump to broken state.
     *
     * <p>This corresponds to the Randomly Break Pump use case, where the
     * System performs a round update, selects a working pump, and then calls
     * {@code Pump.breakDown()}.</p>
     */
    public void breakDown() {
        java.lang.System.out.println("[Pump:" + id + "] breakDown() called.");
        this.broken = true;
        java.lang.System.out.println("[Pump:" + id
                + "] Pump is now broken.");
    }

    /**
     * Restores this pump to working state.
     *
     * <p>This corresponds to the Repair Pipe / Pump use case when a plumber
     * repairs a broken pump.</p>
     */
    public void repair() {
        java.lang.System.out.println("[Pump:" + id + "] repair() called.");
        this.broken = false;
        java.lang.System.out.println("[Pump:" + id
                + "] Pump repaired and restored to working state.");
    }

    /**
     * Returns whether this pump is currently broken.
     *
     * @return {@code true} if the pump is broken; {@code false} otherwise
     */
    public boolean isBroken() {
        java.lang.System.out.println("[Pump:" + id + "] isBroken() called. "
                + "broken: " + broken);
        return broken;
    }

    // -----------------------------------------------------------------------
    // Water transfer support
    // -----------------------------------------------------------------------

    /**
     * Checks whether water may currently pass through this pump.
     *
     * <p>At skeleton level, water may be transferred only if the pump is not
     * broken and both an active input and active output pipe have been set.</p>
     *
     * @return {@code true} if the pump can transfer water; {@code false}
     *         otherwise
     */
    public boolean canTransferWater() {
        java.lang.System.out.println("[Pump:" + id + "] canTransferWater() called.");

        boolean result = !broken && activeInput != null && activeOutput != null;

        java.lang.System.out.println("[Pump:" + id
                + "] canTransferWater = " + result);
        return result;
    }

    /**
     * Checks whether another pipe may be connected to this pump.
     *
     * @return {@code true} if the number of connected pipes is below the
     *         connection limit; {@code false} otherwise
     */
    public boolean canAcceptConnection() {
        java.lang.System.out.println("[Pump:" + id + "] canAcceptConnection() called. "
                + "Connected: " + connectedPipes.size()
                + ", Limit: " + connectionLimit);

        return connectedPipes.size() < connectionLimit;
    }

    /**
     * Attaches a pipe to this pump if the connection limit allows it.
     *
     * @param pipe the pipe to connect
     */
    public void addConnectedPipe(Pipe pipe) {
        java.lang.System.out.println("[Pump:" + id + "] addConnectedPipe() called.");

        if (pipe == null) {
            java.lang.System.out.println("[Pump:" + id
                    + "] addConnectedPipe() – pipe is null, skipping.");
            return;
        }

        if (connectedPipes.contains(pipe)) {
            java.lang.System.out.println("[Pump:" + id
                    + "] addConnectedPipe() – pipe already connected.");
            return;
        }

        if (!canAcceptConnection()) {
            java.lang.System.out.println("[Pump:" + id
                    + "] addConnectedPipe() – connection limit reached.");
            return;
        }

        connectedPipes.add(pipe);
        java.lang.System.out.println("[Pump:" + id
                + "] Pipe connected successfully. Connected pipe count: "
                + connectedPipes.size());
    }

    // -----------------------------------------------------------------------
    // Getters and setters
    // -----------------------------------------------------------------------

    /**
     * Returns the list of pipes connected to this pump.
     *
     * @return the connected pipes list
     */
    public List<Pipe> getConnectedPipes() {
        return connectedPipes;
    }

    /**
     * Returns the active input pipe.
     *
     * @return the active input pipe, or {@code null} if none is selected
     */
    public Pipe getActiveInput() {
        return activeInput;
    }

    /**
     * Returns the active output pipe.
     *
     * @return the active output pipe, or {@code null} if none is selected
     */
    public Pipe getActiveOutput() {
        return activeOutput;
    }

    /**
     * Returns the current amount of water stored in the internal buffer.
     *
     * @return the buffer amount
     */
    public int getBuffer() {
        return buffer;
    }

    /**
     * Sets the internal water buffer.
     *
     * @param buffer the new buffer value
     */
    public void setBuffer(int buffer) {
        java.lang.System.out.println("[Pump:" + id + "] setBuffer() called. "
                + "New buffer: " + buffer);
        this.buffer = buffer;
    }

    /**
     * Returns the maximum number of connectable pipes.
     *
     * @return the connection limit
     */
    public int getConnectionLimit() {
        return connectionLimit;
    }
}