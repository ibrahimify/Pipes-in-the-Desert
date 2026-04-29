
package app;



/**
 * Stores the scores of the two teams during the game.
 * One score represents water delivered to cisterns and the other represents water leaked from the network.
 */
public class ScoreBoard {

    /** Amount of water delivered to cisterns. */
    private int plumberScore;

    /** Amount of leaked water. */
    private int saboteurScore;

    /** Reference to the plumber team. */
    private Team plumberTeam;

    /** Reference to the saboteur team. */
    private Team saboteurTeam;

    /**
     * Creates an empty scoreboard.
     */
    public ScoreBoard() {
        this.plumberScore = 0;
        this.saboteurScore = 0;
    }

    /**
     * Creates a scoreboard with the participating teams.
     *
     * @param plumberTeam plumber team
     * @param saboteurTeam saboteur team
     */
    public ScoreBoard(Team plumberTeam, Team saboteurTeam) {
        this();
        this.plumberTeam = plumberTeam;
        this.saboteurTeam = saboteurTeam;
    }

    /**
     * Increases the plumber score.
     *
     * @param amount delivered water amount
     */
    public void addDeliveredWater(int amount) {
        java.lang.System.out.println("ScoreBoard.addDeliveredWater(" + amount + ")");
        if (amount > 0) {
            plumberScore += amount;
        }
    }

    /**
     * Increases the saboteur score.
     *
     * @param amount leaked water amount
     */
    public void addLeakedWater(int amount) {
        java.lang.System.out.println("ScoreBoard.addLeakedWater(" + amount + ")");
        if (amount > 0) {
            saboteurScore += amount;
        }
    }

    /**
     * Returns plumber score.
     *
     * @return plumber score
     */
    public int getPlumberScore() {
        java.lang.System.out.println("ScoreBoard.getPlumberScore()");
        return plumberScore;
    }

    /**
     * Returns saboteur score.
     *
     * @return saboteur score
     */
    public int getSaboteurScore() {
        java.lang.System.out.println("ScoreBoard.getSaboteurScore()");
        return saboteurScore;
    }

    /**
     * Returns the winning team based on final comparison.
     * Returns null in case of a draw or missing team configuration.
     *
     * @return winning team or null
     */
    public Team determineWinner() {
        java.lang.System.out.println("ScoreBoard.determineWinner()");
        if (plumberTeam == null || saboteurTeam == null) {
            return null;
        }
        if (plumberScore > saboteurScore) {
            return plumberTeam;
        }
        if (saboteurScore > plumberScore) {
            return saboteurTeam;
        }
        return null;
    }

    /**
     * Sets the plumber team.
     *
     * @param plumberTeam plumber team
     */
    public void setPlumberTeam(Team plumberTeam) {
        this.plumberTeam = plumberTeam;
    }

    /**
     * Sets the saboteur team.
     *
     * @param saboteurTeam saboteur team
     */
    public void setSaboteurTeam(Team saboteurTeam) {
        this.saboteurTeam = saboteurTeam;
    }
}
