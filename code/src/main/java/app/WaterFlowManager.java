package app;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Calculates water movement in the pipe network and updates the scoreboard.
 *
 * <p>For each spring, water is sent into the spring's output pipe. The flow
 * continues through connected pipes and pumps until it either reaches a cistern
 * or becomes leaked. Water leaks when it reaches a punctured pipe, a pipe with
 * a free end, a broken pump, an invalid pump direction, or a dead end.</p>
 */
public class WaterFlowManager {

    /** Network whose elements are processed. */
    private PipeNetwork network;

    /** Scoreboard updated with delivered and leaked water. */
    private ScoreBoard scoreBoard;

    /**
     * Creates a water-flow manager.
     *
     * @param network network to evaluate
     * @param scoreBoard scoreboard to update
     */
    public WaterFlowManager(PipeNetwork network, ScoreBoard scoreBoard) {
        this.network = network;
        this.scoreBoard = scoreBoard;
    }

    /**
     * Recalculates water flow for all springs in the network.
     */
    public void recalculateFlow() {
        if (network == null || scoreBoard == null) {
            return;
        }

        List<Spring> springs = network.getSprings();
        if (springs == null || springs.isEmpty()) {
            return;
        }

        for (Spring spring : springs) {
            processSpring(spring);
        }
    }

    /**
     * Processes the water produced by one spring.
     *
     * @param spring spring to process
     */
    public void processSpring(Spring spring) {
        if (spring == null) {
            return;
        }

        int waterAmount = spring.produceWater();
        if (waterAmount <= 0) {
            return;
        }

        Pipe currentPipe = spring.getOutputPipe();
        NetworkElement previousElement = spring;
        Set<NetworkElement> visited = new HashSet<>();

        if (currentPipe == null) {
            registerLeakedWater(waterAmount);
            return;
        }

        while (currentPipe != null) {
            if (!visited.add(currentPipe)) {
                registerLeakedWater(waterAmount);
                return;
            }

            if (currentPipe.isPunctured() || currentPipe.hasFreeEnd()) {
                registerLeakedWater(waterAmount);
                return;
            }

            Cistern targetCistern = findConnectedCistern(currentPipe, previousElement);
            if (targetCistern != null) {
                targetCistern.receiveWater(waterAmount);
                registerDeliveredWater(waterAmount);
                return;
            }

            Pump nextPump = findConnectedPump(currentPipe, previousElement);
            if (nextPump == null || !visited.add(nextPump)) {
                registerLeakedWater(waterAmount);
                return;
            }

            if (!canFlowThroughPump(nextPump, currentPipe)) {
                registerLeakedWater(waterAmount);
                return;
            }

            previousElement = nextPump;
            currentPipe = nextPump.getActiveOutput();
        }

        registerLeakedWater(waterAmount);
    }

    /**
     * Finds a cistern connected to the given pipe, excluding the previous element.
     *
     * @param pipe current pipe
     * @param previous previous element in the traversal
     * @return connected cistern, or null if none exists
     */
    private Cistern findConnectedCistern(Pipe pipe, NetworkElement previous) {
        for (NetworkElement neighbor : pipe.getNeighbors()) {
            if (neighbor == previous) {
                continue;
            }
            if (neighbor instanceof Cistern) {
                return (Cistern) neighbor;
            }
        }
        return null;
    }

    /**
     * Finds a pump connected to the given pipe, excluding the previous element.
     *
     * @param pipe current pipe
     * @param previous previous element in the traversal
     * @return connected pump, or null if none exists
     */
    private Pump findConnectedPump(Pipe pipe, NetworkElement previous) {
        for (NetworkElement neighbor : pipe.getNeighbors()) {
            if (neighbor == previous) {
                continue;
            }
            if (neighbor instanceof Pump) {
                return (Pump) neighbor;
            }
        }
        return null;
    }

    /**
     * Checks whether water can pass from the incoming pipe through the pump.
     *
     * @param pump pump to check
     * @param incomingPipe pipe from which water reaches the pump
     * @return true if the pump can transfer water to its active output
     */
    private boolean canFlowThroughPump(Pump pump, Pipe incomingPipe) {
        return !pump.isBroken()
                && pump.canTransferWater()
                && pump.getActiveInput() == incomingPipe
                && pump.getActiveOutput() != null
                && pump.getActiveOutput() != incomingPipe;
    }

    /**
     * Registers delivered water on the scoreboard.
     *
     * @param amount delivered amount
     */
    public void registerDeliveredWater(int amount) {
        if (amount > 0 && scoreBoard != null) {
            scoreBoard.addDeliveredWater(amount);
        }
    }

    /**
     * Registers leaked water on the scoreboard.
     *
     * @param amount leaked amount
     */
    public void registerLeakedWater(int amount) {
        if (amount > 0 && scoreBoard != null) {
            scoreBoard.addLeakedWater(amount);
        }
    }

    /**
     * Returns the managed network.
     *
     * @return current pipe network
     */
    public PipeNetwork getNetwork() {
        return network;
    }

    /**
     * Returns the scoreboard.
     *
     * @return current scoreboard
     */
    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    /**
     * Updates the network reference.
     *
     * @param network new network
     */
    public void setNetwork(PipeNetwork network) {
        this.network = network;
    }

    /**
     * Updates the scoreboard reference.
     *
     * @param scoreBoard new scoreboard
     */
    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }
}