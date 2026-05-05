package app;

import app.Cistern;
import app.NetworkElement;
import app.Pipe;
import app.Player;

/**
 * Plumber is a specialized {@link Player} whose objective is to maximize the
 * amount of water delivered to cisterns in the Pipes in the Desert game.
 *
 * <p>A Plumber can perform the following role-specific actions, all of which
 * are defined in the Analysis Model (version 2) and the problem definition:</p>
 * <ul>
 *   <li>Repair a punctured {@link Pipe} (FR-23, FR-24).</li>
 *   <li>Repair a broken {@link Pump} (FR-21, FR-22).</li>
 *   <li>Change the active direction of a {@link Pump} (FR-25).</li>
 *   <li>Connect the free end of a {@link Pipe} to an adjacent network element
 *       (FR-28).</li>
 *   <li>Collect a generated {@link Pump} from a {@link Cistern} (FR-29).</li>
 *   <li>Insert a carried {@link Pump} into an existing {@link Pipe} by splitting
 *       it (FR-30).</li>
 *   <li>Collect a generated {@link Pipe} from a {@link Cistern} (FR-27 /
 *       Place New Pipe use case).</li>
 *   <li>Place a collected {@link Pipe} into the network and connect its ends
 *       (Place New Pipe use case).</li>
 * </ul>
 *
 * <p>All structural changes to the network (connections, insertions) are
 * delegated to the appropriate domain objects ({@link Pipe}, {@link Pump},
 * {@link PipeNetwork}) consistent with the sequence diagrams in the Analysis
 * Model. This class only represents the plumber's role-specific initiation of
 * those actions.</p>
 *
 * <p>This is a skeleton-level implementation. Methods print their own names,
 * print relevant state, and delegate to collaborating classes in the order
 * shown by the sequence diagrams.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 * @see Player
 * @see Saboteur
 */
public class Plumber extends Player {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /**
     * Indicates whether the plumber is currently carrying a pump collected from
     * a cistern.
     *
     * <p>Set to {@code true} by {@link #collectPumpFromCistern(Cistern)} and
     * reset to {@code false} after a successful {@link #insertPump(Pipe, Pump,
     * PipeNetwork)} call. Only one pump may be carried at a time at skeleton
     * level (FR-29).</p>
     */
    private boolean carriedPump;

    /**
     * Indicates whether the plumber is currently carrying a pipe collected from
     * a cistern.
     *
     * <p>Set to {@code true} by {@link #collectPipeFromCistern(Cistern)} and
     * reset to {@code false} after a successful
     * {@link #placeNewPipe(Pipe, NetworkElement, NetworkElement, PipeNetwork)}
     * call. Included to support the Place New Pipe use case (FR-27, FR-28).</p>
     */
    private boolean carriedPipe;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Full constructor. Creates a Plumber with a name, team, and an initial
     * position in the network.
     *
     * <p>Calls the {@link Player} superclass constructor and initialises all
     * plumber-specific fields to their safe default values.</p>
     *
     * @param name     the display name of this plumber; must not be {@code null}
     * @param team     the team this plumber belongs to; must not be {@code null}
     * @param position the starting {@link NetworkElement} position, or
     *                 {@code null} if not yet placed
     */
    public Plumber(String name, Team team, NetworkElement position) {
        super(name, team, position);
        this.carriedPump = false;
        this.carriedPipe = false;
    }

    /**
     * Simplified constructor. Creates a Plumber with a name and team but no
     * initial position.
     *
     * <p>The position will be {@code null} until set by
     * {@link Player#setPosition(NetworkElement)}. This matches the skeleton
     * setup pattern used in {@code System.initializeGame()}.</p>
     *
     * @param name the display name of this plumber; must not be {@code null}
     * @param team the team this plumber belongs to; must not be {@code null}
     */
    public Plumber(String name, Team team) {
        this(name, team, null);
    }

    // -----------------------------------------------------------------------
    // Repair actions
    // -----------------------------------------------------------------------

    /**
     * Repairs a punctured {@link Pipe} that the plumber is standing on.
     *
     * <p>If the pipe is punctured, this method delegates the state change to
     * {@link Pipe#repair()}, which restores the pipe to its intact state and
     * stops water from leaking (FR-23, FR-24).</p>
     *
     * <p>Corresponds to the {@code repairPipe(pipe)} call in the
     * Repair Pipe / Pump sequence diagram. The {@code repair()} call on the pipe
     * is followed by occupancy management visible in that diagram; occupancy is
     * already handled by the {@link Player#moveTo(NetworkElement)} layer.</p>
     *
     * @param pipe the {@link Pipe} to repair; must not be {@code null}
     */
    public void repairPipe(Pipe pipe) {
        java.lang.System.out.println("[Plumber:" + name + "] repairPipe() called. "
                + "Pipe punctured: " + (pipe != null && pipe.isPunctured()));

        if (pipe == null) {
            java.lang.System.out.println("[Plumber:" + name + "] repairPipe() – "
                    + "pipe is null, skipping.");
            return;
        }

        if (pipe.isPunctured()) {
            pipe.repair();
            java.lang.System.out.println("[Plumber:" + name + "] Pipe repaired. "
                    + "Water leakage stopped.");
        } else {
            java.lang.System.out.println("[Plumber:" + name + "] repairPipe() – "
                    + "pipe is not punctured, no action taken.");
        }
    }

    /**
     * Repairs a broken {@link Pump} that the plumber is standing on.
     *
     * <p>If the pump is broken, this method delegates the state change to
     * {@link Pump#repair()}, which restores the pump to working order so that
     * water transfer can resume (FR-21, FR-22).</p>
     *
     * <p>Corresponds to the {@code repairPump(pump)} call in the
     * Repair Pipe / Pump sequence diagram.</p>
     *
     * @param pump the {@link Pump} to repair; must not be {@code null}
     */
    public void repairPump(Pump pump) {
        java.lang.System.out.println("[Plumber:" + name + "] repairPump() called. "
                + "Pump broken: " + (pump != null && pump.isBroken()));

        if (pump == null) {
            java.lang.System.out.println("[Plumber:" + name + "] repairPump() – "
                    + "pump is null, skipping.");
            return;
        }

        if (pump.isBroken()) {
            pump.repair();
            java.lang.System.out.println("[Plumber:" + name + "] Pump repaired. "
                    + "Water transfer can resume.");
        } else {
            java.lang.System.out.println("[Plumber:" + name + "] repairPump() – "
                    + "pump is not broken, no action taken.");
        }
    }

    // -----------------------------------------------------------------------
    // Pump direction
    // -----------------------------------------------------------------------

    /**
     * Changes the active input/output routing of the given {@link Pump}.
     *
     * <p>Delegates the routing change to {@link Pump#setDirection(Pipe, Pipe)},
     * which selects the new active input and output pipes. This is consistent
     * with FR-25 and the Change Pump Direction sequence diagram, where the plumber
     * initiates the action and the pump manages its own direction state.</p>
     *
     * @param pump   the {@link Pump} whose direction is to be changed;
     *               must not be {@code null}
     * @param input  the {@link Pipe} to set as the active input; must not be
     *               {@code null}
     * @param output the {@link Pipe} to set as the active output; must not be
     *               {@code null}
     */
    public void changePumpDirection(Pump pump, Pipe input, Pipe output) {
        java.lang.System.out.println("[Plumber:" + name
                + "] changePumpDirection() called.");

        if (pump == null || input == null || output == null) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] changePumpDirection() – one or more arguments are null, "
                    + "skipping.");
            return;
        }

        if (pump.isBroken()) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] changePumpDirection() – pump is broken, "
                    + "direction cannot be changed.");
            return;
        }

        pump.setDirection(input, output);
        java.lang.System.out.println("[Plumber:" + name
                + "] Pump direction changed. "
                + "Input: " + input.getClass().getSimpleName()
                + ", Output: " + output.getClass().getSimpleName());
    }

    // -----------------------------------------------------------------------
    // Pipe connection
    // -----------------------------------------------------------------------

    /**
     * Connects the free end of the given {@link Pipe} to the specified target
     * {@link NetworkElement}.
     *
     * <p>The plumber initiates this action; the structural change is delegated
     * to {@link Pipe#connectFreeEnd(NetworkElement)}, keeping responsibility
     * for connection state inside the {@code Pipe} class. This is consistent
     * with FR-28 and the Place New Pipe sequence diagram.</p>
     *
     * @param pipe   the {@link Pipe} whose free end is to be connected;
     *               must not be {@code null}
     * @param target the {@link NetworkElement} to connect the free end to;
     *               must not be {@code null}
     */
    public void connectPipeEnd(Pipe pipe, NetworkElement target) {
        java.lang.System.out.println("[Plumber:" + name + "] connectPipeEnd() called. "
                + "Target: "
                + (target != null ? target.getClass().getSimpleName() : "null"));

        if (pipe == null || target == null) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] connectPipeEnd() – pipe or target is null, skipping.");
            return;
        }

        if (!pipe.hasFreeEnd()) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] connectPipeEnd() – pipe has no free end, skipping.");
            return;
        }

        pipe.connectFreeEnd(target);
        java.lang.System.out.println("[Plumber:" + name
                + "] Free end of pipe connected to "
                + target.getClass().getSimpleName() + ".");
    }

    // -----------------------------------------------------------------------
    // Cistern interactions – pump
    // -----------------------------------------------------------------------

    /**
     * Collects a newly generated pump from the given {@link Cistern}.
     *
     * <p>The plumber must be standing adjacent to the cistern. This method
     * calls {@link Cistern#takePump()} to mark the pump as collected and sets
     * {@link #carriedPump} to {@code true}. Only one pump may be carried at a
     * time at skeleton level (FR-29).</p>
     *
     * <p>Corresponds to the {@code collectPumpFromCistern(cistern)} call in the
     * Pick Up Pump at Cistern sequence diagram.</p>
     *
     * @param cistern the {@link Cistern} from which to collect the pump;
     *                must not be {@code null}
     */
    public void collectPumpFromCistern(Cistern cistern) {
        java.lang.System.out.println("[Plumber:" + name
                + "] collectPumpFromCistern() called.");

        if (cistern == null) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] collectPumpFromCistern() – cistern is null, skipping.");
            return;
        }

        if (carriedPump) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] collectPumpFromCistern() – already carrying a pump, "
                    + "skipping.");
            return;
        }

        if (!cistern.hasAvailablePump()) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] collectPumpFromCistern() – no pump available at cistern.");
            return;
        }

        boolean taken = cistern.takePump();
        if (taken) {
            carriedPump = true;
            java.lang.System.out.println("[Plumber:" + name
                    + "] Pump collected from cistern. carriedPump = true.");
        } else {
            java.lang.System.out.println("[Plumber:" + name
                    + "] collectPumpFromCistern() – takePump() returned false.");
        }
    }

    /**
     * Inserts the carried pump into an existing pipe, splitting that pipe into
     * two segments (FR-30).
     *
     * <p>The structural modification is delegated entirely to
     * {@link PipeNetwork#insertPump(Pipe, Pump)}, which removes the target pipe,
     * disconnects its ends, places the pump between the two resulting segments,
     * and attaches the new pipe segments to the pump. After a successful
     * insertion {@link #carriedPump} is reset to {@code false}.</p>
     *
     * <p>Corresponds to the {@code insertPump(targetPipe, pump, network)} call
     * in the Insert New Pump sequence diagram.</p>
     *
     * @param targetPipe the {@link Pipe} to split and insert the pump into;
     *                   must not be {@code null}
     * @param pump       the {@link Pump} to insert; must not be {@code null}
     * @param network    the active {@link PipeNetwork}; must not be {@code null}
     */
    public void insertPump(Pipe targetPipe, Pump pump, PipeNetwork network) {
        java.lang.System.out.println("[Plumber:" + name + "] insertPump() called.");

        if (targetPipe == null || pump == null || network == null) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] insertPump() – one or more arguments are null, skipping.");
            return;
        }

        if (!carriedPump) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] insertPump() – not carrying a pump, skipping.");
            return;
        }

        network.insertPump(targetPipe, pump);
        carriedPump = false;
        java.lang.System.out.println("[Plumber:" + name
                + "] Pump inserted into pipe. Network updated. "
                + "carriedPump = false.");
    }

    // -----------------------------------------------------------------------
    // Cistern interactions – pipe
    // -----------------------------------------------------------------------

    /**
     * Collects a newly generated pipe from the given {@link Cistern}.
     *
     * <p>The plumber must be adjacent to the cistern. This method calls
     * {@link Cistern#takePipe()} to mark the pipe as collected and sets
     * {@link #carriedPipe} to {@code true}. Supports the Place New Pipe use
     * case (FR-27).</p>
     *
     * @param cistern the {@link Cistern} from which to collect the pipe;
     *                must not be {@code null}
     */
    public void collectPipeFromCistern(Cistern cistern) {
        java.lang.System.out.println("[Plumber:" + name
                + "] collectPipeFromCistern() called.");

        if (cistern == null) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] collectPipeFromCistern() – cistern is null, skipping.");
            return;
        }

        if (carriedPipe) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] collectPipeFromCistern() – already carrying a pipe, "
                    + "skipping.");
            return;
        }

        if (!cistern.hasAvailablePipe()) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] collectPipeFromCistern() – no pipe available at cistern.");
            return;
        }

        boolean taken = cistern.takePipe();
        if (taken) {
            carriedPipe = true;
            java.lang.System.out.println("[Plumber:" + name
                    + "] Pipe collected from cistern. carriedPipe = true.");
        } else {
            java.lang.System.out.println("[Plumber:" + name
                    + "] collectPipeFromCistern() – takePipe() returned false.");
        }
    }

    /**
     * Places the carried pipe into the network and connects its two ends to the
     * specified network elements.
     *
     * <p>This method adds the pipe as a new element in the {@link PipeNetwork}
     * and creates connections between the pipe and the two given elements.
     * After placement {@link #carriedPipe} is reset to {@code false}. Supports
     * the Place New Pipe use case (FR-27, FR-28).</p>
     *
     * <p>Corresponds to the {@code connectElements(pipe, target)} call in the
     * Place New Pipe sequence diagram.</p>
     *
     * @param pipe    the {@link Pipe} to place into the network;
     *                must not be {@code null}
     * @param a       the first {@link NetworkElement} end to connect to;
     *                must not be {@code null}
     * @param b       the second {@link NetworkElement} end to connect to;
     *                must not be {@code null}
     * @param network the active {@link PipeNetwork}; must not be {@code null}
     */
    public void placeNewPipe(Pipe pipe, NetworkElement a, NetworkElement b,
                              PipeNetwork network) {
        java.lang.System.out.println("[Plumber:" + name + "] placeNewPipe() called. "
                + "Connecting between: "
                + (a != null ? a.getClass().getSimpleName() : "null")
                + " and "
                + (b != null ? b.getClass().getSimpleName() : "null"));

        if (pipe == null || a == null || b == null || network == null) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] placeNewPipe() – one or more arguments are null, skipping.");
            return;
        }

        if (!carriedPipe) {
            java.lang.System.out.println("[Plumber:" + name
                    + "] placeNewPipe() – not carrying a pipe, skipping.");
            return;
        }

        network.addElement(pipe);
        network.connectElements(pipe, a);
        network.connectElements(pipe, b);
        carriedPipe = false;
        java.lang.System.out.println("[Plumber:" + name
                + "] New pipe placed into network and connected. "
                + "carriedPipe = false.");
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    /**
     * Returns whether this plumber is currently carrying a pump.
     *
     * @return {@code true} if the plumber has collected a pump from a cistern
     *         and has not yet inserted it; {@code false} otherwise
     */
    public boolean isCarryingPump() {
        return carriedPump;
    }

    /**
     * Returns whether this plumber is currently carrying a pipe.
     *
     * @return {@code true} if the plumber has collected a pipe from a cistern
     *         and has not yet placed it; {@code false} otherwise
     */
    public boolean isCarryingPipe() {
        return carriedPipe;
    }
}
