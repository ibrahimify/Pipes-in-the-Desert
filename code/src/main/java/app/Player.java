package app;

/**
 * Represents a participant standing on the pipe network.
 *
 * <p>Player contains the common state and behaviour shared by plumbers and
 * saboteurs. A player has a name, belongs to a team, has one current position,
 * and may perform only one action during a turn. Role-specific actions are
 * implemented in {@link Plumber} and {@link Saboteur}.</p>
 */
public class Player {

    /** Display name of the player. */
    protected String name;

    /** Current place of the player on the network. */
    protected NetworkElement position;

    /** Team that owns this player. */
    protected Team team;

    /** True after the player has already acted in the current turn. */
    protected boolean hasActedThisTurn;

    /**
     * Creates a player with a starting position.
     *
     * @param name player name shown in the CLI
     * @param team team of the player
     * @param position starting position, usually a pipe or pump
     */
    public Player(String name, Team team, NetworkElement position) {
        this.name = name;
        this.team = team;
        this.position = null;
        this.hasActedThisTurn = false;
        setPosition(position);
    }

    /**
     * Creates a player without a starting position.
     *
     * @param name player name shown in the CLI
     * @param team team of the player
     */
    public Player(String name, Team team) {
        this(name, team, null);
    }

    /**
     * Moves the player to another reachable playable element.
     *
     * <p>The game allows players to stand on pipes and pumps only. Pipes may
     * contain only one player, while pumps may contain several players. This
     * method also marks the player as having acted for the current turn.</p>
     *
     * @param target target pipe or pump
     * @return true if the move was performed
     */
    public boolean moveTo(NetworkElement target) {
        if (!canAct() || !isValidPosition(target)) {
            return false;
        }

        if (target instanceof Pipe) {
            Pipe targetPipe = (Pipe) target;
            if (targetPipe.isOccupied() && targetPipe.getOccupant() != this) {
                return false;
            }
        }

        if (position != null && !position.isAdjacentTo(target) && position != target) {
            return false;
        }

        setPosition(target);
        endTurn();
        return true;
    }

    /**
     * Returns the player's current position.
     *
     * @return current network element, or null if not placed yet
     */
    public NetworkElement getPosition() {
        return position;
    }

    /**
     * Places the player directly on a network element.
     *
     * <p>This is used by game setup and topology changes. It does not consume
     * the player's turn, but it still keeps pipe occupancy consistent.</p>
     *
     * @param target new position, usually a pipe or pump
     */
    public void setPosition(NetworkElement target) {
        if (target != null && !isValidPosition(target)) {
            return;
        }

        if (target instanceof Pipe) {
            Pipe targetPipe = (Pipe) target;
            if (targetPipe.isOccupied() && targetPipe.getOccupant() != this) {
                return;
            }
        }

        clearCurrentPipeOccupation();
        position = target;

        if (position instanceof Pipe) {
            ((Pipe) position).setOccupant(this);
        }
    }

    /**
     * Checks whether the player can still perform an action this turn.
     *
     * @return true if no action has been performed this turn
     */
    public boolean canAct() {
        return !hasActedThisTurn;
    }

    /**
     * Starts the player's turn.
     */
    public void startTurn() {
        hasActedThisTurn = false;
    }

    /**
     * Ends the player's turn.
     */
    public void endTurn() {
        hasActedThisTurn = true;
    }

    /**
     * Returns the player's name.
     *
     * @return player name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the player's team.
     *
     * @return team object
     */
    public Team getTeam() {
        return team;
    }

    /**
     * Checks whether the player has already acted this turn.
     *
     * @return true if the player has used the current turn
     */
    public boolean hasActedThisTurn() {
        return hasActedThisTurn;
    }

    /**
     * Checks whether a target is a valid place for a player.
     *
     * @param target target element
     * @return true for pipes and pumps
     */
    protected boolean isValidPosition(NetworkElement target) {
        return target instanceof Pipe || target instanceof Pump;
    }

    /**
     * Removes this player from the current pipe occupancy field.
     */
    private void clearCurrentPipeOccupation() {
        if (position instanceof Pipe) {
            Pipe currentPipe = (Pipe) position;
            if (currentPipe.getOccupant() == this) {
                currentPipe.clearOccupant();
            }
        }
    }
}
