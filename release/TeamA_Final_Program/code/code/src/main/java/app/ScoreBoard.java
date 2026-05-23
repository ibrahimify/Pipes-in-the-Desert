package app;

/**
 * Stores and manages the score values of the Plumber and Saboteur teams.
 *
 * <p>The Plumber score represents the total amount of water delivered to
 * cisterns. The Saboteur score represents the total amount of water leaked from
 * the network. Winner determination is based on these two accumulated values.</p>
 */
public class ScoreBoard {

    /** Total water delivered to cisterns. */
    private int plumberScore;

    /** Total water leaked from the pipe network. */
    private int saboteurScore;

    /** Team representing the plumbers. */
    private Team plumberTeam;

    /** Team representing the saboteurs. */
    private Team saboteurTeam;

    /**
     * Creates an empty scoreboard without assigned teams.
     */
    public ScoreBoard() {
        this(null, null);
    }

    /**
     * Creates a scoreboard for the given teams.
     *
     * @param plumberTeam team of plumbers
     * @param saboteurTeam team of saboteurs
     */
    public ScoreBoard(Team plumberTeam, Team saboteurTeam) {
        this.plumberScore = 0;
        this.saboteurScore = 0;
        this.plumberTeam = plumberTeam;
        this.saboteurTeam = saboteurTeam;
    }

    /**
     * Adds delivered water to the Plumber score.
     *
     * @param amount amount of water delivered
     */
    public void addDeliveredWater(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Delivered water amount cannot be negative.");
        }
        plumberScore += amount;
    }

    /**
     * Adds leaked water to the Saboteur score.
     *
     * @param amount amount of leaked water
     */
    public void addLeakedWater(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Leaked water amount cannot be negative.");
        }
        saboteurScore += amount;
    }

    /**
     * Returns the current Plumber score.
     *
     * @return delivered water total
     */
    public int getPlumberScore() {
        return plumberScore;
    }

    /**
     * Returns the current Saboteur score.
     *
     * @return leaked water total
     */
    public int getSaboteurScore() {
        return saboteurScore;
    }

    /**
     * Determines the winning team.
     *
     * @return plumber team if delivered water is greater, saboteur team if leaked
     *         water is greater, or {@code null} if the result is a draw
     */
    public Team determineWinner() {
        if (plumberScore > saboteurScore) {
            return plumberTeam;
        }
        if (saboteurScore > plumberScore) {
            return saboteurTeam;
        }
        return null;
    }

    /**
     * Assigns the plumber team.
     *
     * @param plumberTeam plumber team
     */
    public void setPlumberTeam(Team plumberTeam) {
        this.plumberTeam = plumberTeam;
    }

    /**
     * Assigns the saboteur team.
     *
     * @param saboteurTeam saboteur team
     */
    public void setSaboteurTeam(Team saboteurTeam) {
        this.saboteurTeam = saboteurTeam;
    }

    /**
     * Resets both scores to zero.
     */
    public void reset() {
        plumberScore = 0;
        saboteurScore = 0;
    }
}