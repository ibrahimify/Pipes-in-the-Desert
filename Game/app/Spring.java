package app;

/**
 * Spring represents a water source node in the pipe network.
 *
 * <p>A Spring is the starting point of water flow in the Pipes in the Desert
 * game. According to the Analysis Model, it is responsible for producing water
 * into the connected system during game progression.</p>
 *
 * <p>This class extends {@link NetworkElement}, because a spring is a physical
 * part of the network and participates in connectivity just like pipes, pumps,
 * and cisterns.</p>
 *
 * <p>This is a skeleton-level implementation. Methods print their own names,
 * print relevant state, and return simple values to support scenario checking
 * and sequence-diagram verification.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 * @see NetworkElement
 * @see WaterFlowManager
 */
public class Spring extends NetworkElement {

    /**
     * The amount of water this spring produces in one flow step.
     */
    private int waterOutput;

    /**
     * The pipe connected to this spring's output.
     *
     * <p>According to the Analysis Model (4.3.11), the Spring has an
     * {@code outputPipe: Pipe} attribute representing the pipe through
     * which produced water enters the network.</p>
     */
    private Pipe outputPipe;

    /**
     * Creates a Spring with the given unique identifier and water output.
     *
     * @param id          the unique identifier of this spring
     * @param waterOutput the amount of water produced in one flow step
     */
    public Spring(int id, int waterOutput) {
        super(id);
        this.waterOutput = waterOutput;
        this.outputPipe = null;
        java.lang.System.out.println("[Spring:" + id + "] Created with waterOutput: "
                + waterOutput);
    }

    /**
     * Creates a Spring with the given unique identifier and a default water
     * output value.
     *
     * @param id the unique identifier of this spring
     */
    public Spring(int id) {
        this(id, 10);
    }

    /**
     * Produces water into the network.
     *
     * @return the amount of water produced in this step
     */
    public int produceWater() {
        java.lang.System.out.println("[Spring:" + id + "] produceWater() called. "
                + "Produced water: " + waterOutput);
        return waterOutput;
    }

    /**
     * Returns the current water output of this spring.
     *
     * @return the spring's water output
     */
    public int getWaterOutput() {
        java.lang.System.out.println("[Spring:" + id + "] getWaterOutput() called. "
                + "waterOutput: " + waterOutput);
        return waterOutput;
    }

    /**
     * Sets the water output of this spring.
     *
     * @param waterOutput the new water output value
     */
    public void setWaterOutput(int waterOutput) {
        java.lang.System.out.println("[Spring:" + id + "] setWaterOutput() called. "
                + "New value: " + waterOutput);
        this.waterOutput = waterOutput;
    }

    /**
     * Returns the pipe connected to this spring's output.
     *
     * <p>Corresponds to the {@code getOutputPipe()} call in the
     * Water Leakage / Flow Logic sequence diagram.</p>
     *
     * @return the output {@link Pipe}, or {@code null} if not connected
     */
    public Pipe getOutputPipe() {
        java.lang.System.out.println("[Spring:" + id + "] getOutputPipe() called.");
        return outputPipe;
    }

    /**
     * Sets the pipe connected to this spring's output.
     *
     * @param outputPipe the {@link Pipe} to connect as output
     */
    public void setOutputPipe(Pipe outputPipe) {
        java.lang.System.out.println("[Spring:" + id + "] setOutputPipe() called.");
        this.outputPipe = outputPipe;
    }
}