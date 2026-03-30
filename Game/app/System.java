package app;

import java.util.ArrayList;
import java.util.List;

/**
 * Coordinates one full game session.
 *
 * <p>The System class initializes the game, manages turn order, starts and
 * stops the timer, triggers periodic system events, and ends the game when
 * the timer expires. It does not replace the responsibilities of all other
 * classes, but it coordinates them.</p>
 *
 * <p>According to the Analysis Model (version 2, section 4.3.12), the System
 * owns references to the two teams, the pipe network, the game timer, the
 * scoreboard, and the current player. It also coordinates with
 * {@link WaterFlowManager} for water-flow recalculation after structural or
 * state changes.</p>
 *
 * <p>This is a skeleton-level implementation. Methods print their own names,
 * interact with the user through console I/O where required by the skeleton
 * UI plan, and delegate to collaborating objects in the order shown by the
 * sequence diagrams.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 */
public class System {

    /** The two competing teams. */
    private List<Team> teams;

    /** The active network. */
    private PipeNetwork network;

    /** Session timer. */
    private GameTimer timer;

    /** Current scores. */
    private ScoreBoard scoreBoard;

    /** Player whose turn is active. */
    private Player currentPlayer;

    /** Indicates whether the game session is active. */
    private boolean running;

    /** Flow manager used by the system. */
    private WaterFlowManager waterFlowManager;

    /** Flat turn-order list built from team members. */
    private List<Player> turnOrder;

    /** Current turn index. */
    private int currentTurnIndex;

    // -----------------------------------------------------------------------
    // Constructors
    // -----------------------------------------------------------------------

    /**
     * Creates an empty System with no preconfigured collaborators.
     */
    public System() {
        this.teams = new ArrayList<>();
        this.turnOrder = new ArrayList<>();
        this.currentTurnIndex = -1;
        this.running = false;
    }

    /**
     * Creates a System with preconfigured collaborators.
     *
     * @param teams      the teams in the session
     * @param network    the active network
     * @param timer      the game timer
     * @param scoreBoard the scoreboard
     */
    public System(List<Team> teams, PipeNetwork network,
                  GameTimer timer, ScoreBoard scoreBoard) {
        this();
        if (teams != null) {
            this.teams.addAll(teams);
        }
        this.network = network;
        this.timer = timer;
        this.scoreBoard = scoreBoard;
        this.waterFlowManager = new WaterFlowManager(network, scoreBoard);
        rebuildTurnOrder();
    }

    // -----------------------------------------------------------------------
    // Game lifecycle
    // -----------------------------------------------------------------------

    /**
     * Starts a new game session.
     *
     * <p>Corresponds to the Start Game sequence diagram: the System calls
     * {@link #initializeGame()}, starts the timer, and activates the first
     * player's turn.</p>
     */
    public void startGame() {
        java.lang.System.out.println("System.startGame()");
        running = true;
        initializeGame();

        if (timer != null) {
            timer.start();
        }

        if (!turnOrder.isEmpty()) {
            currentTurnIndex = 0;
            currentPlayer = turnOrder.get(0);
            currentPlayer.startTurn();
        }
    }

    /**
     * Terminates the session and triggers winner declaration.
     *
     * <p>Corresponds to the End Game / Declare Winner sequence diagram.</p>
     */
    public void endGame() {
        java.lang.System.out.println("System.endGame()");
        running = false;

        if (timer != null) {
            timer.stop();
        }

        Team winner = null;
        if (scoreBoard != null) {
            winner = scoreBoard.determineWinner();
        }

        if (winner == null) {
            java.lang.System.out.println("Game ended in a draw.");
        } else {
            java.lang.System.out.println("Winner: " + winner.getName());
        }
    }

    /**
     * Creates the initial network and prepares players.
     *
     * <p>At skeleton level, this method builds a simple linear network
     * consisting of one spring, two pipes, one pump, and one cistern
     * connected in series: Spring → Pipe1 → Pump → Pipe2 → Cistern.</p>
     */
    public void initializeGame() {
        java.lang.System.out.println("System.initializeGame()");

        if (network == null) {
            network = new PipeNetwork();
        }
        if (scoreBoard == null) {
            scoreBoard = new ScoreBoard();
        }
        if (waterFlowManager == null) {
            waterFlowManager = new WaterFlowManager(network, scoreBoard);
        }

        // Skeleton-level initial topology:
        // Spring(1) -> Pipe(1) -> Pump(1) -> Pipe(2) -> Cistern(1)

        
     Spring spring   = new Spring(network.generateId());
     Cistern cistern = new Cistern(network.generateId());
     Pump pump       = new Pump(network.generateId());
     Pipe pipe1      = new Pipe(network.generateId());
     Pipe pipe2      = new Pipe(network.generateId()); 

        network.addElement(spring);
        network.addElement(cistern);
        network.addElement(pump);
        network.addElement(pipe1);
        network.addElement(pipe2);

        network.connectElements(spring, pipe1);
        network.connectElements(pipe1, pump);
        network.connectElements(pump, pipe2);
        network.connectElements(pipe2, cistern);

        spring.setOutputPipe(pipe1);
        pump.addConnectedPipe(pipe1);
        pump.addConnectedPipe(pipe2);
        pump.setDirection(pipe1, pipe2);

        rebuildTurnOrder();



        // Yahya edit , assigning players to initial positions in the network 
        //  all players start at the pump
        // A safer skeleton approach is to just put everyone on the pump, since pumps allow multiple players

      for (Player player : turnOrder) {
           player.setPosition(pump);
           }
    }

    // -----------------------------------------------------------------------
    // Turn management
    // -----------------------------------------------------------------------

    /**
     * Advances control to the next player.
     *
     * <p>Ends the current player's turn, increments the turn index, starts the
     * next player's turn, and ticks the game timer.</p>
     */
    public void nextTurn() {
        java.lang.System.out.println("System.nextTurn()");

        if (turnOrder.isEmpty()) {
            currentPlayer = null;
            return;
        }

        if (currentPlayer != null) {
            currentPlayer.endTurn();
        }

        currentTurnIndex = (currentTurnIndex + 1) % turnOrder.size();
        currentPlayer = turnOrder.get(currentTurnIndex);
        currentPlayer.startTurn();


        // Yahya edit:
        // The only change is adding currentTurnIndex == 0 to the condition.
        //  When currentTurnIndex wraps back to 0, it means we've gone through all players and are starting a new round.
        //  So with 4 players and duration 5, you get 5 full rounds where every player acts
        // that's 20 total turns instead of just 5.
  if (currentTurnIndex == 0 && timer != null && timer.isActive()) {
                timer.tick();
        }
    }

    /**
     * Returns the active player.
     *
     * @return current player
     */
    public Player getCurrentPlayer() {
        java.lang.System.out.println("System.getCurrentPlayer()");
        return currentPlayer;
    }

    /**
     * Checks whether the timer expired.
     *
     * @return {@code true} if the game should end
     */
    public boolean checkEndCondition() {
        java.lang.System.out.println("System.checkEndCondition()");
        return timer != null && timer.hasExpired();
    }

    // -----------------------------------------------------------------------
    // Round events
    // -----------------------------------------------------------------------

    /**
     * Signals a round update so elements can trigger their internal events.
     *
     * <p>At skeleton level, this method triggers component generation, offers
     * a random pump breakdown through a console prompt, and recalculates
     * water flow.</p>
     */
    public void updateRoundEvents() {
        java.lang.System.out.println("System.updateRoundEvents()");

        requestComponentGeneration();

        // Randomly Break Pump (skeleton: prompt the user)
        List<Pump> workingPumps = network != null ? network.getWorkingPumps() : null;
        if (workingPumps != null && !workingPumps.isEmpty()) {
            java.util.Scanner scanner = new java.util.Scanner(java.lang.System.in);
            java.lang.System.out.print("Trigger random pump breakdown? (yes/no): ");
            String answer = scanner.nextLine().trim().toLowerCase();
            if (answer.equals("yes")) {
                workingPumps.get(0).breakDown();
            }
        }

        if (waterFlowManager != null) {
            waterFlowManager.recalculateFlow();
        }
    }

    /**
     * Asks cisterns to generate new pipes or pumps when generation conditions
     * are met.
     *
     * <p>At skeleton level the generation condition is modeled as a yes/no
     * console prompt for each cistern, consistent with the skeleton UI plan
     * (section 5.2).</p>
     */
    public void requestComponentGeneration() {
        java.lang.System.out.println("System.requestComponentGeneration()");

        if (network == null) return;

        List<Cistern> cisterns = network.getCisterns();
        if (cisterns == null) return;

        java.util.Scanner scanner = new java.util.Scanner(java.lang.System.in);

        for (Cistern cistern : cisterns) {
            java.lang.System.out.print("Generate new pipe at cistern? (yes/no): ");
            String pipeAnswer = scanner.nextLine().trim().toLowerCase();
            if (pipeAnswer.equals("yes")) {
                cistern.generatePipe();
            }

            java.lang.System.out.print("Generate new pump at cistern? (yes/no): ");
            String pumpAnswer = scanner.nextLine().trim().toLowerCase();
            if (pumpAnswer.equals("yes")) {
                cistern.generatePump();
            }
        }
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /**
     * Rebuilds the flat turn-order list from team members.
     */
    private void rebuildTurnOrder() {
        turnOrder.clear();
        for (Team team : teams) {
            if (team != null && team.getMembers() != null) {
                turnOrder.addAll(team.getMembers());
            }
        }
    }

    // -----------------------------------------------------------------------
    // Getters and setters
    // -----------------------------------------------------------------------

    public List<Team> getTeams()                     { return teams; }
    public PipeNetwork getNetwork()                  { return network; }
    public GameTimer getTimer()                      { return timer; }
    public ScoreBoard getScoreBoard()                { return scoreBoard; }
    public boolean isRunning()                       { return running; }
    public WaterFlowManager getWaterFlowManager()    { return waterFlowManager; }

    public void setTeams(List<Team> teams) {
        this.teams = teams != null ? teams : new ArrayList<>();
        rebuildTurnOrder();
    }

    public void setNetwork(PipeNetwork network) {
        this.network = network;
        if (waterFlowManager != null) {
            waterFlowManager.setNetwork(network);
        }
    }

    public void setTimer(GameTimer timer)            { this.timer = timer; }

    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
        if (waterFlowManager != null) {
            waterFlowManager.setScoreBoard(scoreBoard);
        }
    }

    public void setWaterFlowManager(WaterFlowManager wfm) {
        this.waterFlowManager = wfm;
    }
}