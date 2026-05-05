package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a pump in the pipe network.
 * A pump can connect several pipes, store water temporarily,
 * transfer water from an active input pipe to an active output pipe,
 * break down, and be repaired.
 *
 * Pumps may be occupied by multiple players, unlike pipes.
 */
public class Pump extends NetworkElement {

    /**
     * Pipes directly connected to this pump.
     */
    private List<Pipe> connectedPipes;

    /**
     * Currently selected input pipe.
     */
    private Pipe activeInput;

    /**
     * Currently selected output pipe.
     */
    private Pipe activeOutput;

    /**
     * Indicates whether the pump is broken.
     */
    private boolean broken;

    /**
     * Water currently stored inside the pump.
     */
    private int buffer;

    /**
     * Maximum number of pipes that can be connected to this pump.
     */
    private int connectionLimit;

    /**
     * Players currently standing on this pump.
     * Multiple players are allowed on the same pump.
     */
    private List<Player> occupants;

    /**
     * Creates a pump with a specific connection limit.
     *
     * @param id unique pump identifier
     * @param connectionLimit maximum number of connected pipes
     */
    public Pump(int id, int connectionLimit) {
        super(id);
        this.connectedPipes = new ArrayList<>();
        this.activeInput = null;
        this.activeOutput = null;
        this.broken = false;
        this.buffer = 0;
        this.connectionLimit = Math.max(1, connectionLimit);
        this.occupants = new ArrayList<>();

        java.lang.System.out.println("[Pump:" + id + "] Created. connectionLimit: " + this.connectionLimit);
    }

    /**
     * Creates a pump with the default connection limit.
     *
     * @param id unique pump identifier
     */
    public Pump(int id) {
        this(id, 4);
    }

    /**
     * Sets the water direction of the pump.
     * The input and output pipes must both be connected to this pump.
     *
     * @param input selected input pipe
     * @param output selected output pipe
     * @return true if the direction was changed successfully
     */
    public boolean setDirection(Pipe input, Pipe output) {
        java.lang.System.out.println("[Pump:" + id + "] setDirection() called.");

        if (input == null || output == null) {
            java.lang.System.out.println("[Pump:" + id + "] Input or output is null, direction not changed.");
            return false;
        }

        if (input == output) {
            java.lang.System.out.println("[Pump:" + id + "] Input and output cannot be the same pipe.");
            return false;
        }

        if (broken) {
            java.lang.System.out.println("[Pump:" + id + "] Pump is broken, direction not changed.");
            return false;
        }

        if (!connectedPipes.contains(input) || !connectedPipes.contains(output)) {
            java.lang.System.out.println("[Pump:" + id + "] Input/output pipe is not connected to this pump.");
            return false;
        }

        activeInput = input;
        activeOutput = output;

        java.lang.System.out.println("[Pump:" + id + "] Direction updated successfully. Input: "
                + input.getId() + ", Output: " + output.getId());
        return true;
    }

    /**
     * Breaks the pump.
     * A broken pump cannot transfer water.
     */
    public void breakDown() {
        java.lang.System.out.println("[Pump:" + id + "] breakDown() called.");

        if (broken) {
            java.lang.System.out.println("[Pump:" + id + "] Pump is already broken.");
            return;
        }

        broken = true;
        java.lang.System.out.println("[Pump:" + id + "] Pump is now broken.");
    }

    /**
     * Repairs the pump.
     * After repair, the pump can transfer water again if direction is configured.
     */
    public void repair() {
        java.lang.System.out.println("[Pump:" + id + "] repair() called.");

        if (!broken) {
            java.lang.System.out.println("[Pump:" + id + "] Pump is already working.");
            return;
        }

        broken = false;
        java.lang.System.out.println("[Pump:" + id + "] Pump repaired and restored to working state.");
    }

    /**
     * Checks whether the pump is broken.
     *
     * @return true if the pump is broken
     */
    public boolean isBroken() {
        java.lang.System.out.println("[Pump:" + id + "] isBroken() called. broken: " + broken);
        return broken;
    }

    /**
     * Checks whether the pump can currently transfer water.
     *
     * @return true if the pump is working and input/output pipes are selected
     */
    public boolean canTransferWater() {
        java.lang.System.out.println("[Pump:" + id + "] canTransferWater() called.");

        boolean result = !broken && activeInput != null && activeOutput != null;
        java.lang.System.out.println("[Pump:" + id + "] canTransferWater = " + result);

        return result;
    }

    /**
     * Checks whether the pump can accept one more pipe connection.
     *
     * @return true if the connection limit has not been reached
     */
    public boolean canAcceptConnection() {
        java.lang.System.out.println("[Pump:" + id + "] canAcceptConnection() called. Connected: "
                + connectedPipes.size() + ", Limit: " + connectionLimit);

        return connectedPipes.size() < connectionLimit;
    }

    /**
     * Adds a pipe to the pump connection list and creates adjacency.
     *
     * @param pipe pipe to connect
     * @return true if the pipe was connected successfully
     */
    public boolean addConnectedPipe(Pipe pipe) {
        java.lang.System.out.println("[Pump:" + id + "] addConnectedPipe() called.");

        if (pipe == null) {
            java.lang.System.out.println("[Pump:" + id + "] Pipe is null, connection skipped.");
            return false;
        }

        if (connectedPipes.contains(pipe)) {
            java.lang.System.out.println("[Pump:" + id + "] Pipe already connected.");
            return false;
        }

        if (!canAcceptConnection()) {
            java.lang.System.out.println("[Pump:" + id + "] Connection limit reached.");
            return false;
        }

        connectedPipes.add(pipe);

        addNeighbor(pipe);
        pipe.addNeighbor(this);

        java.lang.System.out.println("[Pump:" + id + "] Pipe connected successfully. Connected pipe count: "
                + connectedPipes.size());
        return true;
    }

    /**
     * Removes a connected pipe from the pump.
     *
     * @param pipe pipe to disconnect
     * @return true if the pipe was disconnected
     */
    public boolean removeConnectedPipe(Pipe pipe) {
        java.lang.System.out.println("[Pump:" + id + "] removeConnectedPipe() called.");

        if (pipe == null) {
            return false;
        }

        boolean removed = connectedPipes.remove(pipe);

        if (removed) {
            removeNeighbor(pipe);
            pipe.removeNeighbor(this);

            if (activeInput == pipe) {
                activeInput = null;
            }

            if (activeOutput == pipe) {
                activeOutput = null;
            }

            java.lang.System.out.println("[Pump:" + id + "] Pipe disconnected: " + pipe.getId());
        }

        return removed;
    }

    /**
     * Performs one water transfer step.
     * First, the pump tries to push its stored buffer to the output pipe.
     * Then it tries to draw water from the input pipe into the buffer.
     *
     * @return amount of water successfully transferred to the output pipe
     */
    public int transferWater() {
        java.lang.System.out.println("[Pump:" + id + "] transferWater() called.");

        if (!canTransferWater()) {
            java.lang.System.out.println("[Pump:" + id + "] Cannot transfer water.");
            return 0;
        }

        int deliveredToOutput = 0;

        if (buffer > 0) {
            deliveredToOutput = activeOutput.addWater(buffer);
            buffer -= deliveredToOutput;
            java.lang.System.out.println("[Pump:" + id + "] Water pushed to output: " + deliveredToOutput);
        }

        if (buffer == 0) {
            int pulled = activeInput.drainWater();
            buffer += pulled;
            java.lang.System.out.println("[Pump:" + id + "] Water pulled from input: " + pulled);
        }

        return deliveredToOutput;
    }

    /**
     * Adds a player to the pump occupants.
     *
     * @param player player standing on the pump
     */
    public void addOccupant(Player player) {
        java.lang.System.out.println("[Pump:" + id + "] addOccupant() called.");

        if (player != null && !occupants.contains(player)) {
            occupants.add(player);
            java.lang.System.out.println("[Pump:" + id + "] Player added to pump: " + player.getName());
        }
    }

    /**
     * Removes a player from the pump occupants.
     *
     * @param player player leaving the pump
     */
    public void removeOccupant(Player player) {
        java.lang.System.out.println("[Pump:" + id + "] removeOccupant() called.");

        if (player != null && occupants.remove(player)) {
            java.lang.System.out.println("[Pump:" + id + "] Player removed from pump: " + player.getName());
        }
    }

    /**
     * Returns the connected pipes.
     *
     * @return unmodifiable list of connected pipes
     */
    public List<Pipe> getConnectedPipes() {
        return Collections.unmodifiableList(connectedPipes);
    }

    /**
     * Returns the active input pipe.
     *
     * @return active input pipe
     */
    public Pipe getActiveInput() {
        return activeInput;
    }

    /**
     * Returns the active output pipe.
     *
     * @return active output pipe
     */
    public Pipe getActiveOutput() {
        return activeOutput;
    }

    /**
     * Returns the current pump buffer.
     *
     * @return stored water amount
     */
    public int getBuffer() {
        return buffer;
    }

    /**
     * Sets the pump buffer.
     * Negative values are converted to zero.
     *
     * @param buffer new buffer value
     */
    public void setBuffer(int buffer) {
        java.lang.System.out.println("[Pump:" + id + "] setBuffer() called. New buffer: " + buffer);
        this.buffer = Math.max(0, buffer);
    }

    /**
     * Returns the pump connection limit.
     *
     * @return connection limit
     */
    public int getConnectionLimit() {
        return connectionLimit;
    }

    /**
     * Returns the current pump occupants.
     *
     * @return unmodifiable list of players on the pump
     */
    public List<Player> getOccupants() {
        return Collections.unmodifiableList(occupants);
    }
}