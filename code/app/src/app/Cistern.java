package app;

/**
 * Represents a cistern in the pipe network.
 * A cistern receives delivered water and stores generated pipes and pumps
 * that plumbers may later collect.
 */
public class Cistern extends NetworkElement {

    /**
     * Total amount of water delivered to this cistern.
     */
    private int storedWater;

    /**
     * Number of generated pipes currently available at this cistern.
     */
    private int availablePipes;

    /**
     * Number of generated pumps currently available at this cistern.
     */
    private int availablePumps;

    /**
     * Counter used for prototype pipe generation timing.
     */
    private int pipeGenerationCounter;

    /**
     * Counter used for prototype pump generation timing.
     */
    private int pumpGenerationCounter;

    /**
     * Number of round updates required before generating a pipe.
     */
    private int pipeGenerationInterval;

    /**
     * Number of round updates required before generating a pump.
     */
    private int pumpGenerationInterval;

    /**
     * Creates a cistern with the given identifier.
     *
     * @param id unique cistern identifier
     */
    public Cistern(int id) {
        super(id);
        this.storedWater = 0;
        this.availablePipes = 0;
        this.availablePumps = 0;
        this.pipeGenerationCounter = 0;
        this.pumpGenerationCounter = 0;
        this.pipeGenerationInterval = 2;
        this.pumpGenerationInterval = 4;

        java.lang.System.out.println("[Cistern:" + id + "] Created.");
    }

    /**
     * Receives delivered water and increases stored water.
     *
     * @param amount amount of water delivered to the cistern
     */
    public void receiveWater(int amount) {
        java.lang.System.out.println("[Cistern:" + id + "] receiveWater() called. Amount: " + amount);

        if (amount <= 0) {
            java.lang.System.out.println("[Cistern:" + id + "] Amount must be positive, skipping.");
            return;
        }

        storedWater += amount;
        java.lang.System.out.println("[Cistern:" + id + "] Water received successfully. Total stored water: " + storedWater);
    }

    /**
     * Generates one new pipe and makes it available for plumbers.
     */
    public void generatePipe() {
        java.lang.System.out.println("[Cistern:" + id + "] generatePipe() called.");
        availablePipes++;
        java.lang.System.out.println("[Cistern:" + id + "] New pipe generated. Available pipes: " + availablePipes);
    }

    /**
     * Generates one new pump and makes it available for plumbers.
     */
    public void generatePump() {
        java.lang.System.out.println("[Cistern:" + id + "] generatePump() called.");
        availablePumps++;
        java.lang.System.out.println("[Cistern:" + id + "] New pump generated. Available pumps: " + availablePumps);
    }

    /**
     * Updates component-generation counters.
     * This method is used by the prototype game loop to generate new components
     * automatically according to simple interval rules.
     */
    public void updateGeneration() {
        java.lang.System.out.println("[Cistern:" + id + "] updateGeneration() called.");

        pipeGenerationCounter++;
        pumpGenerationCounter++;

        if (pipeGenerationCounter >= pipeGenerationInterval) {
            generatePipe();
            pipeGenerationCounter = 0;
        }

        if (pumpGenerationCounter >= pumpGenerationInterval) {
            generatePump();
            pumpGenerationCounter = 0;
        }
    }

    /**
     * Checks whether at least one pipe is available.
     *
     * @return true if a pipe is available
     */
    public boolean hasAvailablePipe() {
        java.lang.System.out.println("[Cistern:" + id + "] hasAvailablePipe() called. Available pipes: " + availablePipes);
        return availablePipes > 0;
    }

    /**
     * Checks whether at least one pump is available.
     *
     * @return true if a pump is available
     */
    public boolean hasAvailablePump() {
        java.lang.System.out.println("[Cistern:" + id + "] hasAvailablePump() called. Available pumps: " + availablePumps);
        return availablePumps > 0;
    }

    /**
     * Takes one available pipe from the cistern.
     *
     * @return true if a pipe was successfully taken
     */
    public boolean takePipe() {
        java.lang.System.out.println("[Cistern:" + id + "] takePipe() called.");

        if (availablePipes <= 0) {
            java.lang.System.out.println("[Cistern:" + id + "] No pipe available.");
            return false;
        }

        availablePipes--;
        java.lang.System.out.println("[Cistern:" + id + "] Pipe taken successfully. Remaining pipes: " + availablePipes);
        return true;
    }

    /**
     * Takes one available pump from the cistern.
     *
     * @return true if a pump was successfully taken
     */
    public boolean takePump() {
        java.lang.System.out.println("[Cistern:" + id + "] takePump() called.");

        if (availablePumps <= 0) {
            java.lang.System.out.println("[Cistern:" + id + "] No pump available.");
            return false;
        }

        availablePumps--;
        java.lang.System.out.println("[Cistern:" + id + "] Pump taken successfully. Remaining pumps: " + availablePumps);
        return true;
    }

    /**
     * Returns stored water amount.
     *
     * @return stored water
     */
    public int getStoredWater() {
        return storedWater;
    }

    /**
     * Returns number of available pipes.
     *
     * @return available pipe count
     */
    public int getAvailablePipes() {
        return availablePipes;
    }

    /**
     * Returns number of available pumps.
     *
     * @return available pump count
     */
    public int getAvailablePumps() {
        return availablePumps;
    }

    /**
     * Sets the pipe generation interval.
     *
     * @param pipeGenerationInterval interval for pipe generation
     */
    public void setPipeGenerationInterval(int pipeGenerationInterval) {
        if (pipeGenerationInterval > 0) {
            this.pipeGenerationInterval = pipeGenerationInterval;
        }
    }

    /**
     * Sets the pump generation interval.
     *
     * @param pumpGenerationInterval interval for pump generation
     */
    public void setPumpGenerationInterval(int pumpGenerationInterval) {
        if (pumpGenerationInterval > 0) {
            this.pumpGenerationInterval = pumpGenerationInterval;
        }
    }
}