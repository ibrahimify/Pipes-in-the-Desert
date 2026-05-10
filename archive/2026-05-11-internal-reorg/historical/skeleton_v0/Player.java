package app;

/**
 * Player represents a human-controlled participant in the Pipes in the Desert game.
 *
 * <p>A Player has an identity ({@link #name}), a current location on the pipe
 * network ({@link #position}), a team membership ({@link #team}), and a flag
 * that tracks whether the player has already performed an action in the current
 * turn ({@link #hasActedThisTurn}).</p>
 *
 * <p>Player provides the shared movement and turn-management behaviour that is
 * common to both roles in the game. Role-specific actions (repair, puncture,
 * insert pump, etc.) belong exclusively to the subclasses {@code Plumber} and
 * {@code Saboteur} and must not be added here.</p>
 *
 * <p>According to the problem definition and Analysis Model (version 2):
 * <ul>
 *   <li>Players may only move onto {@link Pipe} or {@link Pump} elements
 *       (FR-15, FR-16).</li>
 *   <li>Springs and cisterns are not valid movement destinations.</li>
 *   <li>Only one player may occupy a pipe at a time (FR-17).</li>
 *   <li>Multiple players may occupy the same pump simultaneously (FR-18).</li>
 *   <li>Each player is limited to one action per turn (FR-20).</li>
 * </ul>
 * </p>
 *
 * <p>This is a skeleton-level implementation. Methods print their own names
 * and relevant state, and return simple skeleton values consistent with the
 * sequence diagrams in the Analysis Model.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 * @see Plumber
 * @see Saboteur
 */
public class Player {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /**
     * The display name of the player, used for console output and identification.
     */
    protected String name;

    /**
     * The current position of the player in the pipe network.
     *
     * <p>According to the problem definition, players can only stand on
     * {@link Pipe} or {@link Pump} elements. Springs and cisterns are not
     * valid positions.</p>
     */
    protected NetworkElement position;

    /**
     * The team this player belongs to (Plumber team or Saboteur team).
     */
    protected Team team;

    /**
     * Tracks whether the player has already performed an action in the current turn.
     *
     * <p>Set to {@code true} when the player acts or ends their turn (FR-20).
     * Reset to {@code false} at the start of each new turn via {@link #startTurn()}.</p>
     */
    protected boolean hasActedThisTurn;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Full constructor. Creates a Player with a name, team, and an initial position.
     *
     * <p>The initial position may be {@code null} if the player has not yet been
     * placed on the network; it should be set before any movement is attempted.</p>
     *
     * @param name     the display name of the player; must not be {@code null}
     * @param team     the team this player belongs to; must not be {@code null}
     * @param position the starting {@link NetworkElement} position, or {@code null}
     */
    public Player(String name, Team team, NetworkElement position) {
        this.name            = name;
        this.team            = team;
        this.position        = position;
        this.hasActedThisTurn = false;
    }

    /**
     * Simplified constructor. Creates a Player with a name and team but no
     * initial position.
     *
     * <p>The position will be {@code null} until explicitly set by
     * {@link #setPosition(NetworkElement)}. This constructor is provided as a
     * convenience for skeleton-level initialization where a starting position
     * is assigned separately (e.g., by {@code System.initializeGame()}).</p>
     *
     * @param name the display name of the player; must not be {@code null}
     * @param team the team this player belongs to; must not be {@code null}
     */
    public Player(String name, Team team) {
        this(name, team, null);
    }

    // -----------------------------------------------------------------------
    // Movement
    // -----------------------------------------------------------------------

    /**
     * Attempts to move the player to the specified target {@link NetworkElement}.
     *
     * <p>Movement rules enforced by this skeleton method (consistent with
     * FR-15, FR-16, FR-17, FR-18 and the Move Player sequence diagram):</p>
     * <ol>
     *   <li>The player must still be able to act this turn ({@link #canAct()}).</li>
     *   <li>The target must be a {@link Pipe} or a {@link Pump}; any other element
     *       type (spring, cistern) is rejected.</li>
     *   <li>If the target is a {@link Pipe}, it must not already be occupied by
     *       another player ({@link Pipe#isOccupied()}).</li>
     *   <li>If the target is a {@link Pump}, multiple players are allowed, so no
     *       occupancy check is needed.</li>
     *   <li>If all checks pass, the player's position is updated and the occupancy
     *       state of the pipe (if applicable) is set.</li>
     * </ol>
     *
     * <p>Adjacency validation is intentionally left to {@link PipeNetwork} and
     * {@code System}, as shown in the Move Player sequence diagram. This method
     * only enforces element-type and occupancy rules at the player level.</p>
     *
     * @param target the {@link NetworkElement} the player wants to move to
     * @return {@code true} if the move succeeded; {@code false} if it was rejected
     */
    public boolean moveTo(NetworkElement target) {
        java.lang.System.out.println("[Player:" + name + "] moveTo() called. "
                + "Target: " + (target != null ? target.getClass().getSimpleName()
                                               : "null"));

        // Guard: player must be able to act this turn
        if (!canAct()) {
            java.lang.System.out.println("[Player:" + name + "] moveTo() rejected – "
                    + "player has already acted this turn.");
            return false;
        }

        // Guard: target must not be null
        if (target == null) {
            java.lang.System.out.println("[Player:" + name + "] moveTo() rejected – "
                    + "target is null.");
            return false;
        }

        // Rule FR-15 / FR-16: only Pipe or Pump are valid destinations
        if (target instanceof Pipe) {
            Pipe targetPipe = (Pipe) target;

            // Rule FR-17: only one player may occupy a pipe at a time
            if (targetPipe.isOccupied()) {
                java.lang.System.out.println("[Player:" + name + "] moveTo() rejected – "
                        + "target Pipe is already occupied.");
                return false;
            }

            // Clear occupant on previous pipe if the player was on one
            if (position instanceof Pipe) {
                ((Pipe) position).clearOccupant();
                java.lang.System.out.println("[Player:" + name
                        + "] Cleared occupant on previous Pipe.");
            }

            // Move to target pipe and register occupancy
            position = targetPipe;
            targetPipe.setOccupant(this);
            java.lang.System.out.println("[Player:" + name + "] Moved to Pipe. "
                    + "Pipe is now occupied.");
            return true;

        } else if (target instanceof Pump) {
            // Rule FR-18: multiple players may share a pump – no occupancy check needed

            // Clear occupant on previous pipe if the player was on one
            if (position instanceof Pipe) {
                ((Pipe) position).clearOccupant();
                java.lang.System.out.println("[Player:" + name
                        + "] Cleared occupant on previous Pipe.");
            }

            position = target;
            java.lang.System.out.println("[Player:" + name + "] Moved to Pump. "
                    + "Multiple players allowed here.");
            return true;

        } else {
            // Springs and cisterns are not valid movement destinations
            java.lang.System.out.println("[Player:" + name + "] moveTo() rejected – "
                    + "target is not a Pipe or Pump (springs and cisterns are "
                    + "not valid destinations).");
            return false;
        }
    }

    // -----------------------------------------------------------------------
    // Position management
    // -----------------------------------------------------------------------

    /**
     * Returns the current position of this player in the pipe network.
     *
     * <p>Corresponds to the {@code getPosition()} call shown in the
     * Move Player sequence diagram.</p>
     *
     * @return the current {@link NetworkElement} position, or {@code null} if
     *         not yet placed
     */
    public NetworkElement getPosition() {
        java.lang.System.out.println("[Player:" + name + "] getPosition() called. "
                + "Position: "
                + (position != null ? position.getClass().getSimpleName() : "null"));
        return position;
    }

    /**
     * Sets the current position of the player directly to the given target.
     *
     * <p>This method is intended for skeleton-level placement (e.g., initial
     * setup in {@code System.initializeGame()}) and does not enforce adjacency.
     * It should only be called with a valid {@link Pipe} or {@link Pump} target
     * to remain consistent with the movement rules of the problem definition.</p>
     *
     * <p>To keep object state consistent with {@link #moveTo(NetworkElement)},
     * this method also maintains pipe occupancy:</p>
     * <ul>
     *   <li>If the player's current position is a {@link Pipe}, its occupant is
     *       cleared before the position changes.</li>
     *   <li>If the new target is a {@link Pipe}, this player is registered as its
     *       occupant after the position is set.</li>
     * </ul>
     *
     * <p>Corresponds to the {@code setPosition(target)} call shown in the
     * Move Player sequence diagram.</p>
     *
     * @param target the {@link NetworkElement} to place the player on;
     *               should be a {@link Pipe} or {@link Pump} at runtime
     */
    public void setPosition(NetworkElement target) {
        java.lang.System.out.println("[Player:" + name + "] setPosition() called. "
                + "New position: "
                + (target != null ? target.getClass().getSimpleName() : "null"));

        // Clear occupancy on the previous pipe, if applicable
        if (position instanceof Pipe) {
            ((Pipe) position).clearOccupant();
            java.lang.System.out.println("[Player:" + name
                    + "] setPosition() – cleared occupant on previous Pipe.");
        }

        this.position = target;

        // Register occupancy on the new pipe, if applicable
        if (target instanceof Pipe) {
            ((Pipe) target).setOccupant(this);
            java.lang.System.out.println("[Player:" + name
                    + "] setPosition() – registered as occupant on new Pipe.");
        }
    }

    // -----------------------------------------------------------------------
    // Turn management
    // -----------------------------------------------------------------------

    /**
     * Returns whether the player is still allowed to act in the current turn.
     *
     * <p>A player may act only if {@link #hasActedThisTurn} is {@code false}.
     * This enforces the one-action-per-turn rule (FR-20).</p>
     *
     * <p>Corresponds to the {@code canAct()} call shown in the
     * Move Player sequence diagram.</p>
     *
     * @return {@code true} if the player has not yet acted this turn;
     *         {@code false} otherwise
     */
    public boolean canAct() {
        java.lang.System.out.println("[Player:" + name + "] canAct() called. "
                + "hasActedThisTurn: " + hasActedThisTurn
                + " -> canAct: " + !hasActedThisTurn);
        return !hasActedThisTurn;
    }

    /**
     * Resets the player's per-turn action state so that the player may act again.
     *
     * <p>Should be called by {@code System.nextTurn()} at the beginning of this
     * player's new turn. Sets {@link #hasActedThisTurn} to {@code false}.</p>
     *
     * <p>Corresponds to the {@code startTurn()} call shown in the
     * Move Player sequence diagram.</p>
     */
    public void startTurn() {
        java.lang.System.out.println("[Player:" + name + "] startTurn() called. "
                + "Resetting hasActedThisTurn to false.");
        this.hasActedThisTurn = false;
    }

    /**
     * Marks the player's turn as finished by setting {@link #hasActedThisTurn}
     * to {@code true}.
     *
     * <p>Should be called by {@code System.nextTurn()} when advancing to the
     * next player, or by the player itself after completing an action.</p>
     *
     * <p>Corresponds to the {@code endTurn()} call shown in the
     * Move Player sequence diagram.</p>
     */
    public void endTurn() {
        java.lang.System.out.println("[Player:" + name + "] endTurn() called. "
                + "Setting hasActedThisTurn to true.");
        this.hasActedThisTurn = true;
    }

    // -----------------------------------------------------------------------
    // Simple getters
    // -----------------------------------------------------------------------

    /**
     * Returns the display name of this player.
     *
     * @return the player's name as a {@link String}
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the team this player belongs to.
     *
     * @return the player's {@link Team}
     */
    public Team getTeam() {
        return team;
    }
}
