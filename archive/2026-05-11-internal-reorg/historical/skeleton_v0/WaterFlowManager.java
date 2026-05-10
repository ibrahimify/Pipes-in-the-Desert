

package app;

import java.util.List;

import app.Cistern;
import app.NetworkElement;
import app.Pipe;

/**
 * Evaluates how water moves through the current network configuration.
 * It determines whether water is delivered to cisterns, blocked by broken pumps,
 * leaked through punctured pipes, or lost through free pipe ends.
 */
public class WaterFlowManager {

    /** The network being evaluated. */
    private PipeNetwork network;

    /** Score holder updated according to flow result. */
    private ScoreBoard scoreBoard;

    /**
     * Creates a flow manager.
     *
     * @param network network being evaluated
     * @param scoreBoard scoreboard to update
     */
    public WaterFlowManager(PipeNetwork network, ScoreBoard scoreBoard) {
        this.network = network;
        this.scoreBoard = scoreBoard;
    }

    /**
     * Recalculates water movement in the network.
     */
    public void recalculateFlow() {
        java.lang.System.out.println("WaterFlowManager.recalculateFlow()");
        if (network == null) {
            return;
        }

        List<Spring> springs = network.getSprings();
        if (springs == null) {
            return;
        }

        for (Spring spring : springs) {
            processSpring(spring);
        }
    }

    /**
     * Starts flow processing from a spring.
     *
     * @param spring spring to process
     */




    public void processSpring(Spring spring) {
    java.lang.System.out.println("WaterFlowManager.processSpring(spring)");


    if (spring == null) return;

    int water = spring.produceWater();
    Pipe currentPipe = spring.getOutputPipe();

    if (currentPipe == null) {
        registerLeakedWater(water);
        return;
    }

    // Trace water through the network step by step
    while (currentPipe != null) {

        // Check if this pipe leaks
        // explanation : If the pipe is punctured or has a free end,
        //  the full 10 water produced by the spring gets registered as leaked. 
        // So saboteur team score goes up by 10.
        if (currentPipe.isPunctured() || currentPipe.hasFreeEnd()) {
            registerLeakedWater(water);
            return;
        }

        // Look at what this pipe connects to (skip the element we came from)
        NetworkElement nextElement = null;
        for (NetworkElement neighbor : currentPipe.getNeighbors()) {
           
            // currentPipe.getNeighbors() returns every element directly connected to that pipe. 
            // For example, Pipe 4 has two neighbors: Spring(1) and Pump(3). 
            // The loop checks each one to figure out where the water should go next.


            if (neighbor instanceof Cistern) {
                // Water reached a cistern — delivered!
                // If it's a Cistern → water arrived at its destination, count as delivered

                ((Cistern) neighbor).receiveWater(water);
                registerDeliveredWater(water);
                return;
            }
            if (neighbor instanceof Pump) {

                // If it's a Pump → try to pass water through it to the next pipe
                nextElement = neighbor;
            }
        }

        // If we found a pump, try to pass water through it
        if (nextElement instanceof Pump) {
            Pump pump = (Pump) nextElement;

            if (pump.isBroken() || !pump.canTransferWater()) {
                registerLeakedWater(water);
                return;
            }

            // Follow the pump's active output to the next pipe
            currentPipe = pump.getActiveOutput();

            // If active output is the pipe we're already on, use input instead
            if (currentPipe != null && currentPipe == pump.getActiveInput()) {
                currentPipe = pump.getActiveOutput();
            }
        } else {
            // No pump, no cistern — water has nowhere to go
            registerLeakedWater(water);
            return;
        }
    }

    // If we exited the loop with no pipe, water is lost
    registerLeakedWater(water);
}



    /**
     * Records delivered water.
     *
     * @param amount delivered water amount
     */
    public void registerDeliveredWater(int amount) {
        java.lang.System.out.println("WaterFlowManager.registerDeliveredWater(" + amount + ")");
        if (scoreBoard != null) {
            scoreBoard.addDeliveredWater(amount);
        }
    }

    /**
     * Records leaked water.
     *
     * @param amount leaked water amount
     */
    public void registerLeakedWater(int amount) {
        java.lang.System.out.println("WaterFlowManager.registerLeakedWater(" + amount + ")");
        if (scoreBoard != null) {
            scoreBoard.addLeakedWater(amount);
        }
    }

    public PipeNetwork getNetwork() {
        return network;
    }

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    public void setNetwork(PipeNetwork network) {
        this.network = network;
    }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
    }
}