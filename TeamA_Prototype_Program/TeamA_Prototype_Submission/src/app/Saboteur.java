package app;

/**
 * Represents a saboteur player.
 *
 * <p>Saboteurs try to increase water loss from the pipe system. They can
 * puncture pipes and change pump directions to redirect water away from the
 * cisterns.</p>
 */
public class Saboteur extends Player {

    /**
     * Creates a saboteur with a starting position.
     *
     * @param name saboteur name shown in the CLI
     * @param team saboteur team
     * @param position starting position
     */
    public Saboteur(String name, Team team, NetworkElement position) {
        super(name, team, position);
    }

    /**
     * Creates a saboteur without a starting position.
     *
     * @param name saboteur name shown in the CLI
     * @param team saboteur team
     */
    public Saboteur(String name, Team team) {
        this(name, team, null);
    }

    /**
     * Punctures the pipe where the saboteur is standing.
     *
     * @param pipe pipe to puncture
     */
    public void puncturePipe(Pipe pipe) {
        if (!canAct() || pipe == null || position != pipe || pipe.isPunctured()) {
            return;
        }

        pipe.puncture();
        endTurn();
    }

    /**
     * Changes the active direction of a pump.
     *
     * @param pump pump to configure
     * @param input selected input pipe
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
                || !pump.getConnectedPipes().contains(input)
                || !pump.getConnectedPipes().contains(output)) {
            return;
        }

        pump.setDirection(input, output);
        endTurn();
    }
}
