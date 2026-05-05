package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Coordinates one full playable prototype session.
 *
 * <p>The system owns the game-level objects, creates the initial pipe network,
 * manages turn order, starts and stops the timer, handles automatic round
 * events, and checks the end condition.</p>
 */
public class System {

    /** Teams participating in the game. */
    private List<Team> teams;

    /** Active pipe network. */
    private PipeNetwork network;

    /** Game timer. */
    private GameTimer timer;

    /** Score holder. */
    private ScoreBoard scoreBoard;

    /** Player whose turn is currently active. */
    private Player currentPlayer;

    /** True while the game loop should continue. */
    private boolean running;

    /** Water-flow calculator used after state changes. */
    private WaterFlowManager waterFlowManager;

    /** Flat player order built from all teams. */
    private List<Player> turnOrder;

    /** Index of the current player in turn order. */
    private int currentTurnIndex;

    /** Scanner retained for compatibility with existing CLI setup. */
    private Scanner inputScanner;

    /** Random source for automatic pump breakdown events. */
    private Random random;

    /** Number of complete rounds started so far. */
    private int roundNumber;

    /** Last round for which automatic events were processed. */
    private int lastProcessedRound;

    /**
     * Creates an empty system.
     */
    public System() {
        this.teams = new ArrayList<>();
        this.turnOrder = new ArrayList<>();
        this.currentTurnIndex = -1;
        this.running = false;
        this.inputScanner = new Scanner(java.lang.System.in);
        this.random = new Random();
        this.roundNumber = 0;
        this.lastProcessedRound = -1;
    }

    /**
     * Creates a system with already prepared collaborators.
     *
     * @param teams participating teams
     * @param network active network
     * @param timer game timer
     * @param scoreBoard scoreboard
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

    /**
     * Starts a new playable session.
     */
    public void startGame() {
        initializeGame();
        running = !turnOrder.isEmpty();

        if (timer != null) {
            timer.start();
        }

        if (running) {
            currentTurnIndex = 0;
            currentPlayer = turnOrder.get(0);
            currentPlayer.startTurn();
            roundNumber = 1;
            lastProcessedRound = 0;
        }
    }

    /**
     * Ends the current game and prints the winner.
     */
    public void endGame() {
        running = false;

        if (timer != null) {
            timer.stop();
        }

        Team winner = scoreBoard != null ? scoreBoard.determineWinner() : null;
        if (winner == null) {
            java.lang.System.out.println("Game ended in a draw.");
        } else {
            java.lang.System.out.println("Winner: " + winner.getName());
        }
    }

    /**
     * Prepares the network, support objects, turn order, and player positions.
     */
    public void initializeGame() {
        if (network == null) {
            network = new PipeNetwork();
        }

        configureScoreBoard();

        if (timer == null) {
            timer = new GameTimer(10);
        }

        if (waterFlowManager == null) {
            waterFlowManager = new WaterFlowManager(network, scoreBoard);
        } else {
            waterFlowManager.setNetwork(network);
            waterFlowManager.setScoreBoard(scoreBoard);
        }

        if (network.getElements().isEmpty()) {
            buildInitialNetwork();
        }

        rebuildTurnOrder();
        placePlayersAtStartingPump();
    }

    /**
     * Advances to the next player and ticks the timer after a full round.
     */
    public void nextTurn() {
        if (turnOrder.isEmpty()) {
            currentPlayer = null;
            running = false;
            return;
        }

        if (currentPlayer != null) {
            currentPlayer.endTurn();
        }

        currentTurnIndex = (currentTurnIndex + 1) % turnOrder.size();
        if (currentTurnIndex == 0) {
            roundNumber++;
        }


        currentPlayer = turnOrder.get(currentTurnIndex);
        currentPlayer.startTurn();

        if (checkEndCondition()) {
            running = false;
        }
    }

    /**
     * Returns the active player.
     *
     * @return current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Checks whether the game should end.
     *
     * @return true if timer expired or the game is not runnable
     */
    public boolean checkEndCondition() {
        return timer != null && timer.hasExpired();
    }

    /**
     * Processes automatic round events and recalculates water flow.
     */
    public void updateRoundEvents() {
        if (!running || network == null) {
            return;
        }

        if (roundNumber != lastProcessedRound) {
            requestComponentGeneration();
            randomlyBreakPump();
            lastProcessedRound = roundNumber;
        }

        if (waterFlowManager != null) {
            waterFlowManager.recalculateFlow();
        }
    }

    /**
     * Generates new parts at every cistern.
     */
    public void requestComponentGeneration() {
        if (network == null) {
            return;
        }

        for (Cistern cistern : network.getCisterns()) {
            cistern.generatePipe();
            if (roundNumber % 2 == 0) {
                cistern.generatePump();
            }
        }
    }

    /**
     * Returns the participating teams.
     *
     * @return team list
     */
    public List<Team> getTeams() {
        return teams;
    }

    /**
     * Returns the active network.
     *
     * @return pipe network
     */
    public PipeNetwork getNetwork() {
        return network;
    }

    /**
     * Returns the game timer.
     *
     * @return game timer
     */
    public GameTimer getTimer() {
        return timer;
    }

    /**
     * Returns the scoreboard.
     *
     * @return scoreboard
     */
    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    /**
     * Returns whether the session is active.
     *
     * @return true if game loop should continue
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Returns the flow manager.
     *
     * @return water flow manager
     */
    public WaterFlowManager getWaterFlowManager() {
        return waterFlowManager;
    }

    /**
     * Sets participating teams.
     *
     * @param teams new team list
     */
    public void setTeams(List<Team> teams) {
        this.teams = teams != null ? teams : new ArrayList<>();
        rebuildTurnOrder();
    }

    /**
     * Sets the active network.
     *
     * @param network new network
     */
    public void setNetwork(PipeNetwork network) {
        this.network = network;
        if (waterFlowManager != null) {
            waterFlowManager.setNetwork(network);
        }
    }

    /**
     * Sets the game timer.
     *
     * @param timer new timer
     */
    public void setTimer(GameTimer timer) {
        this.timer = timer;
    }

    /**
     * Sets the scoreboard.
     *
     * @param scoreBoard new scoreboard
     */
    public void setScoreBoard(ScoreBoard scoreBoard) {
        this.scoreBoard = scoreBoard;
        if (waterFlowManager != null) {
            waterFlowManager.setScoreBoard(scoreBoard);
        }
    }

    /**
     * Sets the water flow manager.
     *
     * @param waterFlowManager new flow manager
     */
    public void setWaterFlowManager(WaterFlowManager waterFlowManager) {
        this.waterFlowManager = waterFlowManager;
    }

    /**
     * Sets the input scanner used by CLI callers.
     *
     * @param scanner scanner to use
     */
    public void setInputScanner(Scanner scanner) {
        if (scanner != null) {
            this.inputScanner = scanner;
        }
    }

    /**
     * Rebuilds the flat turn order from all teams.
     */
    private void rebuildTurnOrder() {
        turnOrder.clear();
        for (Team team : teams) {
            if (team != null && team.getMembers() != null) {
                turnOrder.addAll(team.getMembers());
            }
        }
    }

    /**
     * Creates the default prototype map.
     */
    private void buildInitialNetwork() {
        Spring spring = new Spring(network.generateId());
        Cistern cistern = new Cistern(network.generateId());
        Pump pump = new Pump(network.generateId());
        Pipe inputPipe = new Pipe(network.generateId());
        Pipe outputPipe = new Pipe(network.generateId());

        network.addElement(spring);
        network.addElement(cistern);
        network.addElement(pump);
        network.addElement(inputPipe);
        network.addElement(outputPipe);

        network.connectElements(spring, inputPipe);
        network.connectElements(inputPipe, pump);
        network.connectElements(pump, outputPipe);
        network.connectElements(outputPipe, cistern);

        spring.setOutputPipe(inputPipe);
        pump.setDirection(inputPipe, outputPipe);
    }

    /**
     * Places every player on the first pump in the network.
     */
    private void placePlayersAtStartingPump() {
        if (network.getPumps().isEmpty()) {
            return;
        }

        Pump startingPump = network.getPumps().get(0);
        for (Player player : turnOrder) {
            player.setPosition(startingPump);
        }
    }

    /**
     * Configures the scoreboard with known teams.
     */
    private void configureScoreBoard() {
        if (scoreBoard == null) {
            scoreBoard = new ScoreBoard(findTeamByPlayerType(Plumber.class),
                    findTeamByPlayerType(Saboteur.class));
        } else {
            Team plumberTeam = findTeamByPlayerType(Plumber.class);
            Team saboteurTeam = findTeamByPlayerType(Saboteur.class);
            if (plumberTeam != null) {
                scoreBoard.setPlumberTeam(plumberTeam);
            }
            if (saboteurTeam != null) {
                scoreBoard.setSaboteurTeam(saboteurTeam);
            }
        }
    }

    /**
     * Finds the team that contains a specific player subtype.
     *
     * @param playerClass class to search for
     * @return matching team, or null if not found
     */
    private Team findTeamByPlayerType(Class<?> playerClass) {
        for (Team team : teams) {
            if (team == null || team.getMembers() == null) {
                continue;
            }
            for (Player player : team.getMembers()) {
                if (playerClass.isInstance(player)) {
                    return team;
                }
            }
        }
        return null;
    }

    /**
     * Breaks one random working pump occasionally.
     */
    private void randomlyBreakPump() {
        List<Pump> workingPumps = network.getWorkingPumps();
        if (workingPumps.isEmpty() || roundNumber % 3 != 0) {
            return;
        }

        Pump selected = workingPumps.get(random.nextInt(workingPumps.size()));
        selected.breakDown();
    }
}
