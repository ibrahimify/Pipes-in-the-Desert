package app; 

/**
 * Spring represents a water source node in the pipe network.
 *
 * <p>A spring produces water during the game. The produced water enters the
 * network through the spring's output pipe. Springs are network elements, but
 * players cannot move onto springs according to the movement rules of the game.</p>
 *
 * @author Team A - E5
 * @version prototype-1.0
 */
public class Spring extends NetworkElement {

    /**
     * Amount of water produced by this spring in one flow update.
     */
    private int waterOutput;

    /**
     * Pipe connected to this spring's output.
     */
    private Pipe outputPipe;

    /**
     * Creates a spring with the given id and water output.
     *
     * @param id unique spring identifier
     * @param waterOutput amount of water produced per flow update
     */
    public Spring(int id, int waterOutput) {
        super(id);
        this.waterOutput = Math.max(0, waterOutput);
        this.outputPipe = null;

        java.lang.System.out.println("[Spring:" + id + "] Created with waterOutput: "
                + this.waterOutput);
    }

    /**
     * Creates a spring with default water output.
     *
     * @param id unique spring identifier
     */
    public Spring(int id) {
        this(id, 10);
    }

    /**
     * Produces water for one flow-processing step.
     *
     * @return produced water amount
     */
    public int produceWater() {
        java.lang.System.out.println("[Spring:" + id + "] produceWater() called. Produced: "
                + waterOutput);
        return waterOutput;
    }

    /**
     * Returns the current water output.
     *
     * @return water output amount
     */
    public int getWaterOutput() {
        return waterOutput;
    }

    /**
     * Sets the water output value.
     * Negative values are converted to zero.
     *
     * @param waterOutput new water output amount
     */
    public void setWaterOutput(int waterOutput) {
        java.lang.System.out.println("[Spring:" + id + "] setWaterOutput() called. New value: "
                + waterOutput);
        this.waterOutput = Math.max(0, waterOutput);
    }

    /**
     * Returns the pipe connected to the spring output.
     *
     * @return output pipe, or null if no pipe is connected
     */
    public Pipe getOutputPipe() {
        return outputPipe;
    }

    /**
     * Sets the pipe connected to the spring output.
     * The method also creates bidirectional adjacency between the spring and pipe.
     *
     * @param outputPipe pipe that receives water from this spring
     * @return true if the output pipe was set successfully
     */
    public boolean setOutputPipe(Pipe outputPipe) {
        java.lang.System.out.println("[Spring:" + id + "] setOutputPipe() called.");

        if (outputPipe == null) {
            java.lang.System.out.println("[Spring:" + id + "] Output pipe is null.");
            return false;
        }

        this.outputPipe = outputPipe;
        addNeighbor(outputPipe);
        outputPipe.addNeighbor(this);

        java.lang.System.out.println("[Spring:" + id + "] Output pipe set to Pipe#"
                + outputPipe.getId());

        return true;
    }
}