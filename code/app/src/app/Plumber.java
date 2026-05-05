package app;

/**
 * Represents a plumber player.
 *
 * <p>
 * Plumbers try to keep the pipe system working. They can repair damaged
 * elements, set pump directions, connect pipe ends, collect new parts from
 * cisterns, insert pumps into pipes, and place new pipes into the network.
 * </p>
 */
public class Plumber extends Player {

    /** True if this plumber currently carries a pump. */
    private boolean carriedPump;

    /** True if this plumber currently carries a pipe. */
    private boolean carriedPipe;

    /**
     * Creates a plumber with a starting position.
     *
     * @param name     plumber name shown in the CLI
     * @param team     plumber team
     * @param position starting position
     */
    public Plumber(String name, Team team, NetworkElement position) {
        super(name, team, position);
        this.carriedPump = false;
        this.carriedPipe = false;
    }

    /**
     * Creates a plumber without a starting position.
     *
     * @param name plumber name shown in the CLI
     * @param team plumber team
     */
    public Plumber(String name, Team team) {
        this(name, team, null);
    }

    /**
     * Repairs a punctured pipe.
     *
     * @param pipe pipe to repair
     */
    public void repairPipe(Pipe pipe) {
        if (!canAct() || pipe == null || position != pipe || !pipe.isPunctured()) {
            return;
        }

        pipe.repair();
        endTurn();
    }

    /**
     * Repairs a broken pump.
     *
     * @param pump pump to repair
     */
    public void repairPump(Pump pump) {
        if (!canAct() || pump == null || position != pump || !pump.isBroken()) {
            return;
        }

        pump.repair();
        endTurn();
    }

    /**
     * Changes the active direction of a pump.
     *
     * @param pump   pump to configure
     * @param input  selected input pipe
     * @param output selected output pipe
     */
    public void changePumpDirection(Pump pump, Pipe input, Pipe output) {
        if (!canAct()
                || pump == null
                || input == null
                || output == null
                || input == output
                || position != pump
                || pump.isBroken()
                // --- NEW FIX:
                // BUG 2 FIX
                || !pump.getConnectedPipes().contains(input)
                || !pump.getConnectedPipes().contains(output)) {
            return;
        }

        pump.setDirection(input, output);
        endTurn();
    }

    /**
     * Connects a free pipe end to another network element.
     *
     * @param pipe   pipe with the free end
     * @param target target element
     */
    public void connectPipeEnd(Pipe pipe, NetworkElement target) {
        if (!canAct()
                || pipe == null
                || target == null
                || target == pipe
                || !pipe.hasFreeEnd()
                || !isAtOrAdjacentTo(pipe)) {
            return;
        }

        pipe.connectFreeEnd(target);
        endTurn();
    }

    /**
     * Collects one available pump from a neighboring cistern.
     *
     * @param cistern cistern that stores generated pumps
     */
    public void collectPumpFromCistern(Cistern cistern) {
        if (!canAct()
                || cistern == null
                || carriedPump
                || !canReachCistern(cistern)
                || !cistern.hasAvailablePump()) {
            return;
        }

        if (cistern.takePump()) {
            carriedPump = true;
        }
    }

    /**
     * Inserts the carried pump into an existing pipe.
     *
     * @param targetPipe pipe to split
     * @param pump       pump to insert
     * @param network    active pipe network
     */
    public void insertPump(Pipe targetPipe, Pump pump, PipeNetwork network) {
        if (!canAct()
                || targetPipe == null
                || pump == null
                || network == null
                || !carriedPump
                || !isAtOrAdjacentTo(targetPipe)) {
            return;
        }

        network.insertPump(targetPipe, pump);
        carriedPump = false;
        endTurn();
    }

    /**
     * Checks whether the plumber can reach a cistern.
     *
     * <p>
     * The cistern is reachable if the plumber is standing on it, next to it,
     * or next to one of the cistern's neighboring elements.
     * </p>
     *
     * @param cistern cistern to check
     * @return true if the cistern can be reached
     */
    private boolean canReachCistern(Cistern cistern) {
        if (isAtOrAdjacentTo(cistern)) {
            return true;
        }

        if (position == null || cistern == null) {
            return false;
        }

        for (NetworkElement neighbor : cistern.getNeighbors()) {
            if (position == neighbor || position.isAdjacentTo(neighbor)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Collects one available pipe from a neighboring cistern.
     *
     * @param cistern cistern that stores generated pipes
     */
    public void collectPipeFromCistern(Cistern cistern) {
        if (!canAct()
                || cistern == null
                || carriedPipe
                || !isAtOrAdjacentTo(cistern)
                || !cistern.hasAvailablePipe()) {
            return;
        }

        if (cistern.takePipe()) {
            carriedPipe = true;
            endTurn();
        }
    }

    /**
     * Places the carried pipe into the network between two elements.
     *
     * @param pipe    new pipe to place
     * @param a       first endpoint
     * @param b       second endpoint
     * @param network active pipe network
     */
    public void placeNewPipe(Pipe pipe, NetworkElement a, NetworkElement b,
            PipeNetwork network) {
        if (!canAct()
                || pipe == null
                || a == null
                || b == null
                || a == b
                || network == null
                || !carriedPipe
                || !isAtOrAdjacentTo(a)) {
            return;
        }

        network.addElement(pipe);
        network.connectElements(pipe, a);
        network.connectElements(pipe, b);
        carriedPipe = false;
        endTurn();
    }

    /**
     * Returns whether this plumber carries a pump.
     *
     * @return true if carrying a pump
     */
    public boolean isCarryingPump() {
        return carriedPump;
    }

    /**
     * Returns whether this plumber carries a pipe.
     *
     * @return true if carrying a pipe
     */
    public boolean isCarryingPipe() {
        return carriedPipe;
    }

    /**
     * Checks if the plumber stands on or next to an element.
     *
     * @param element element to check
     * @return true if the action is locally reachable
     */
    private boolean isAtOrAdjacentTo(NetworkElement element) {
        return position == element
                || (position != null && position.isAdjacentTo(element));
    }
}
