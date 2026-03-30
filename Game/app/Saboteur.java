package app;

import app.NetworkElement;
import app.Pipe;
import app.Player;

/**
 * Saboteur is a specialized {@link Player} whose objective is to maximize water
 * leakage from the pipe network in the Pipes in the Desert game.
 *
 * <p>A Saboteur can perform the following role-specific actions, both of which
 * are defined in the Analysis Model (version 2) and the problem definition:</p>
 * <ul>
 *   <li>Puncture an intact {@link Pipe}, causing water to leak to the desert
 *       (FR-31, FR-32).</li>
 *   <li>Change the active input/output routing of a {@link Pump} to divert
 *       water away from cisterns (FR-26).</li>
 * </ul>
 *
 * <p>All state changes are delegated to the appropriate domain objects
 * ({@link Pipe#puncture()}, {@link Pump#setDirection(Pipe, Pipe)}) consistent
 * with the Puncture Pipe and Change Pump Direction sequence diagrams in the
 * Analysis Model. Water-flow recalculation and score updates belong to
 * {@code WaterFlowManager} and {@code ScoreBoard} respectively, and are not
 * performed here.</p>
 *
 * <p>This is a skeleton-level implementation. Methods print their own names,
 * print relevant state, and delegate actual state changes to collaborating
 * domain objects.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 * @see Player
 * @see Plumber
 */
public class Saboteur extends Player {

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Full constructor. Creates a Saboteur with a name, team, and an initial
     * position in the network.
     *
     * <p>Calls the {@link Player} superclass constructor and leaves all
     * saboteur-specific behaviour to the action methods. No additional
     * attributes are required at skeleton level beyond those inherited from
     * {@link Player}.</p>
     *
     * @param name     the display name of this saboteur; must not be {@code null}
     * @param team     the team this saboteur belongs to; must not be {@code null}
     * @param position the starting {@link NetworkElement} position, or
     *                 {@code null} if not yet placed on the network
     */
    public Saboteur(String name, Team team, NetworkElement position) {
        super(name, team, position);
    }

    /**
     * Simplified constructor. Creates a Saboteur with a name and team but no
     * initial position.
     *
     * <p>The position will be {@code null} until set by
     * {@link Player#setPosition(NetworkElement)}. This constructor matches the
     * skeleton setup pattern used in {@code System.initializeGame()}.</p>
     *
     * @param name the display name of this saboteur; must not be {@code null}
     * @param team the team this saboteur belongs to; must not be {@code null}
     */
    public Saboteur(String name, Team team) {
        this(name, team, null);
    }

    // -----------------------------------------------------------------------
    // Saboteur-specific actions
    // -----------------------------------------------------------------------

    /**
     * Punctures an intact {@link Pipe} that the saboteur is standing on,
     * causing water to leak to the desert.
     *
     * <p>If the pipe is not already punctured, this method delegates the state
     * change to {@link Pipe#puncture()}, which marks the pipe as leaking
     * (FR-31). From that point on, any water passing through the pipe spills
     * out to the desert and is counted as lost water until a {@code Plumber}
     * repairs it (FR-32).</p>
     *
     * <p>Water-flow recalculation and saboteur-score updates are the
     * responsibility of {@code WaterFlowManager} and {@code ScoreBoard}
     * respectively, consistent with the Puncture Pipe sequence diagram, and
     * are not performed here.</p>
     *
     * @param pipe the {@link Pipe} to puncture; must not be {@code null}
     */
    public void puncturePipe(Pipe pipe) {
        java.lang.System.out.println("[Saboteur:" + name + "] puncturePipe() called. "
                + "Pipe punctured already: "
                + (pipe != null && pipe.isPunctured()));

        if (pipe == null) {
            java.lang.System.out.println("[Saboteur:" + name
                    + "] puncturePipe() – pipe is null, skipping.");
            return;
        }

        if (pipe.isPunctured()) {
            java.lang.System.out.println("[Saboteur:" + name
                    + "] puncturePipe() – pipe is already punctured, "
                    + "no action taken.");
            return;
        }

        pipe.puncture();
        java.lang.System.out.println("[Saboteur:" + name
                + "] Pipe punctured successfully. "
                + "Water will now leak to the desert.");
    }

    /**
     * Changes the active input/output routing of the given {@link Pump} to
     * divert water away from cisterns.
     *
     * <p>Delegates the routing change to {@link Pump#setDirection(Pipe, Pipe)},
     * which updates the pump's active input and output pipes (FR-26). This is
     * consistent with the Change Pump Direction sequence diagram, where the
     * saboteur initiates the action and the pump manages its own direction
     * state.</p>
     *
     * <p>Water-flow recalculation after the direction change is the
     * responsibility of {@code WaterFlowManager} and is not performed here.</p>
     *
     * @param pump   the {@link Pump} whose direction is to be changed;
     *               must not be {@code null}
     * @param input  the {@link Pipe} to set as the active input;
     *               must not be {@code null}
     * @param output the {@link Pipe} to set as the active output;
     *               must not be {@code null}
     */
    public void changePumpDirection(Pump pump, Pipe input, Pipe output) {
        java.lang.System.out.println("[Saboteur:" + name
                + "] changePumpDirection() called.");

        if (pump == null || input == null || output == null) {
            java.lang.System.out.println("[Saboteur:" + name
                    + "] changePumpDirection() – one or more arguments are null, "
                    + "skipping.");
            return;
        }

        if (pump.isBroken()) {
            java.lang.System.out.println("[Saboteur:" + name
                    + "] changePumpDirection() – pump is broken, "
                    + "direction cannot be changed.");
            return;
        }

        pump.setDirection(input, output);
        java.lang.System.out.println("[Saboteur:" + name
                + "] Pump direction changed successfully. "
                + "Water flow may now be diverted away from cisterns.");
    }
}
