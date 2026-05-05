package app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Entry point of the Pipes in the Desert skeleton program.
 *
 * <p>This class displays the welcome message, presents the main menu, collects
 * initial game configuration from the user, and launches the game session. It
 * corresponds to the skeleton UI plan described in section 5.2 and the test
 * scenarios in section 6.1.3 of the Skeleton Program document.</p>
 *
 * <p>The main menu offers three options:</p>
 * <ol>
 *   <li><b>Start Game</b> – collects player names, team assignments, and game
 *       duration, then starts and runs the game loop.</li>
 *   <li><b>Instructions</b> – displays a short summary of the game rules.</li>
 *   <li><b>Exit</b> – terminates the program.</li>
 * </ol>
 *
 * <p>During the game loop the program executes the Run Game sequence: round
 * events, water-flow recalculation, player actions, and end-condition checks.
 * Player-controlled actions are selected through numbered menus and confirmed
 * through yes/no prompts, consistent with the skeleton planning document.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 */
public class Main {

    /** Console scanner shared across all menu interactions. */
    private static final Scanner scanner = new Scanner(java.lang.System.in);

    // -----------------------------------------------------------------------
    // Entry point
    // -----------------------------------------------------------------------

    /**
     * Program entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            java.lang.System.out.println();
            java.lang.System.out.println("==========================================");
            java.lang.System.out.println("    Welcome to Pipes in the Desert!");
            java.lang.System.out.println("==========================================");
            java.lang.System.out.println("1. Start Game");
            java.lang.System.out.println("2. Instructions");
            java.lang.System.out.println("3. Exit");
            java.lang.System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    startGame();
                    break;
                case "2":
                    showInstructions();
                    break;
                case "3":
                    java.lang.System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    java.lang.System.out.println("Invalid option. Please enter 1, 2, or 3.");
                    break;
            }
        }
    }

    // -----------------------------------------------------------------------
    // Game setup
    // -----------------------------------------------------------------------

    /**
     * Collects player configuration and game duration, then starts and runs
     * the game session.
     *
     * <p>Matches TEST 1 (Start Game) from section 6.1.3 and the Start Game
     * use-case dialog from section 5.1.2.</p>
     */
     private static void startGame() {
        java.lang.System.out.println();
        java.lang.System.out.println("--- Game Setup ---");

        // Create teams
        Team plumberTeam  = new Team("Plumbers");
        Team saboteurTeam = new Team("Saboteurs");

        // Collect players
        java.lang.System.out.print("How many players? (minimum 4): ");
        int playerCount = readInt(4);

        for (int i = 1; i <= playerCount; i++) {
            java.lang.System.out.print("Player " + i + " name: ");
            String playerName = scanner.nextLine().trim();
            if (playerName.isEmpty()) {
                playerName = "Player" + i;
            }

            java.lang.System.out.println("Choose team for " + playerName + ":");
            java.lang.System.out.println("  1. Plumber");
            java.lang.System.out.println("  2. Saboteur");
            java.lang.System.out.print("Choice: ");
            String teamChoice = scanner.nextLine().trim();

            if (teamChoice.equals("2")) {
                Saboteur s = new Saboteur(playerName, saboteurTeam);
                saboteurTeam.addMember(s);
                java.lang.System.out.println(playerName + " joined the Saboteurs.");
            } else {
                Plumber p = new Plumber(playerName, plumberTeam);
                plumberTeam.addMember(p);
                java.lang.System.out.println(playerName + " joined the Plumbers.");
            }
        }

        // Validate minimum team sizes (FR-03)
        if (plumberTeam.getSize() < 2 || saboteurTeam.getSize() < 2) {
            java.lang.System.out.println("Each team must have at least 2 players. "
                    + "Plumbers: " + plumberTeam.getSize()
                    + ", Saboteurs: " + saboteurTeam.getSize());
            java.lang.System.out.println("Returning to main menu.");
            return;
        }

        // Choose game duration
        java.lang.System.out.println("Choose game duration:");
        java.lang.System.out.println("  1. 3 minutes (turns)");
        java.lang.System.out.println("  2. 5 minutes (turns)");
        java.lang.System.out.print("Choice: ");
        String durationChoice = scanner.nextLine().trim();
        int duration = durationChoice.equals("2") ? 5 : 3;

        // Build the system
        List<Team> teams = new ArrayList<>();
        teams.add(plumberTeam);
        teams.add(saboteurTeam);

        GameTimer timer       = new GameTimer(duration);
        ScoreBoard scoreBoard = new ScoreBoard(plumberTeam, saboteurTeam);
        PipeNetwork network   = new PipeNetwork();

        app.System system = new app.System(teams, network, timer, scoreBoard);

        java.lang.System.out.println();
        java.lang.System.out.println("--- Starting Game ---");
        system.startGame();

        // Enter the game loop
        runGameLoop(system);
    }

    // -----------------------------------------------------------------------
    // Game loop
    // -----------------------------------------------------------------------

    /**
     * Executes the Run Game loop until the end condition is reached.
     *
     * <p>Each iteration corresponds to one game tick as described in the Run
     * Game sequence diagram and TEST 2 from section 6.1.3:</p>
     * <ol>
     *   <li>Update round events (component generation, random pump breakdown).</li>
     *   <li>Recalculate water flow.</li>
     *   <li>Let the current player perform an action.</li>
     *   <li>Advance to the next turn.</li>
     *   <li>Check the end condition.</li>
     * </ol>
     *
     * @param system the active game {@link app.System}
     */
private static void runGameLoop(app.System system) {
    while (system.isRunning()) {
        java.lang.System.out.println();
        java.lang.System.out.println("========== GAME TICK ==========");

        // Round events happen once per tick
        system.updateRoundEvents();

        // Let the SAME player keep acting until a valid action is completed
        Player current = system.getCurrentPlayer();
        boolean turnCompleted = false;

        while (!turnCompleted && system.isRunning()) {
            if (current != null) {
                java.lang.System.out.println();
                java.lang.System.out.println("--- " + current.getName() + "'s turn ("
                        + current.getClass().getSimpleName() + ") ---");
                turnCompleted = handlePlayerTurn(current, system);

                if (!turnCompleted) {
                    java.lang.System.out.println("Invalid or rejected action. "
                            + "Please choose again.");
                }
            } else {
                java.lang.System.out.println("No current player available.");
                break;
            }
        }

        // Advance turn only after the player successfully completes a turn
        system.nextTurn();

        // Display scores
        ScoreBoard sb = system.getScoreBoard();
        if (sb != null) {
            java.lang.System.out.println();
            java.lang.System.out.println("[Scores] Plumbers: " + sb.getPlumberScore()
                    + " | Saboteurs: " + sb.getSaboteurScore());
        }

        // Display remaining time
        GameTimer t = system.getTimer();
        if (t != null) {
            java.lang.System.out.println("[Timer] Remaining: " + t.getRemainingTime());
        }

        // Check end condition
        if (system.checkEndCondition()) {
            java.lang.System.out.println();
            java.lang.System.out.println("========== GAME OVER ==========");
            system.endGame();
            break;
        }
    }
}

    // -----------------------------------------------------------------------
    // Player turn handling
    // -----------------------------------------------------------------------

    /**
     * Presents the action menu for the current player and executes the chosen
     * action.
     *
     * <p>The available actions depend on whether the player is a {@link Plumber}
     * or a {@link Saboteur}, consistent with the use-case descriptions and the
     * skeleton UI plan.</p>
     *
     * @param player the current {@link Player}
     * @param system the active game {@link app.System}
     */
    private static boolean handlePlayerTurn(Player player, app.System system) {
        if (player instanceof Plumber) {
            return handlePlumberTurn((Plumber) player, system);
        } else if (player instanceof Saboteur) {
            return handleSaboteurTurn((Saboteur) player, system);
        } else {
            java.lang.System.out.println("Unknown player type. Skipping turn.");
            return true;
        }
    }

    /**
     * Handles a Plumber's turn by presenting the plumber-specific action menu.
     */
    private static boolean handlePlumberTurn(Plumber plumber, app.System system) {
        PipeNetwork network = system.getNetwork();

        java.lang.System.out.println();
        java.lang.System.out.println("=== Network Status ===");
        for (NetworkElement e : network.getElements()) {
            if (e instanceof Pipe) {
                Pipe p = (Pipe) e;
                String status = "";
                if (p.isPunctured()) status += " [PUNCTURED]";
                if (p.hasFreeEnd()) status += " [FREE END]";
                if (p.isOccupied()) status += " [Occupied by: " + p.getOccupant().getName() + "]";
                if (status.isEmpty()) status = " [OK]";
                java.lang.System.out.println("  Pipe (ID:" + e.getId() + ")" + status);
            } else if (e instanceof Pump) {
                Pump pump = (Pump) e;
                String status = pump.isBroken() ? " [BROKEN]" : " [OK]";
                java.lang.System.out.println("  Pump (ID:" + e.getId() + ")" + status);
            } else if (e instanceof Spring) {
                java.lang.System.out.println("  Spring (ID:" + e.getId() + ")");
            } else if (e instanceof Cistern) {
                Cistern c = (Cistern) e;
                java.lang.System.out.println("  Cistern (ID:" + e.getId() + ")"
                    + " [Pipes:" + c.getAvailablePipes()
                    + " Pumps:" + c.getAvailablePumps() + "]");
            }
        }
        java.lang.System.out.println("======================");

        NetworkElement plumberPosition = plumber.getPosition();
        java.lang.System.out.println("Current position: "
                + (plumberPosition != null ? plumberPosition.getClass().getSimpleName() + " (ID: " + plumberPosition.getId() + ")" : "None"));

        java.lang.System.out.println("Actions:");
        java.lang.System.out.println("  1. Move");
        java.lang.System.out.println("  2. Repair Pipe");
        java.lang.System.out.println("  3. Repair Pump");
        java.lang.System.out.println("  4. Change Pump Direction");
        java.lang.System.out.println("  5. Connect Pipe End");
        java.lang.System.out.println("  6. Disconnect Pipe End");
        java.lang.System.out.println("  7. Pick Up Pump from Cistern");
        java.lang.System.out.println("  8. Pick Up Pipe from Cistern");
        java.lang.System.out.println("  9. Insert Pump into Pipe");
        java.lang.System.out.println(" 10. Place New Pipe");
        java.lang.System.out.println("  0. End Turn (no action)");
        java.lang.System.out.print("Choose action: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1": { // Move
                java.lang.System.out.println("Available destinations:");
                for (NetworkElement e : network.getElements()) {
                    if (e instanceof Pipe) {
                        Pipe p = (Pipe) e;
                        if (!p.isOccupied()) {
                            java.lang.System.out.println("  Pipe (ID: " + e.getId() + ")");
                        }
                    } else if (e instanceof Pump) {
                        java.lang.System.out.println("  Pump (ID: " + e.getId() + ")");
                    }
                }

                java.lang.System.out.print("Enter target element ID: ");
                int targetId = readInt(-1);

                NetworkElement target = findElementById(network, targetId);
                if (target != null) {
                    boolean moved = plumber.moveTo(target);
                    if (!moved) {
                        java.lang.System.out.println("Invalid move, try another!");
                        return false;
                    }
                    return true;
                } else {
                    java.lang.System.out.println("Element not found.");
                    return false;
                }
            }

            case "2": { // Repair Pipe
                NetworkElement pos = plumber.getPosition();
                if (pos instanceof Pipe) {
                    java.lang.System.out.print("Repair the pipe? (yes/no): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                        plumber.repairPipe((Pipe) pos);
                        if (system.getWaterFlowManager() != null) {
                            system.getWaterFlowManager().recalculateFlow();
                        }
                        return true;
                    }
                    return false;
                } else {
                    java.lang.System.out.println("You are not standing on a pipe.");
                    return false;
                }
            }

            case "3": { // Repair Pump
                NetworkElement pos = plumber.getPosition();
                if (pos instanceof Pump) {
                    java.lang.System.out.print("Repair the pump? (yes/no): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                        plumber.repairPump((Pump) pos);
                        if (system.getWaterFlowManager() != null) {
                            system.getWaterFlowManager().recalculateFlow();
                        }
                        return true;
                    }
                    return false;
                } else {
                    java.lang.System.out.println("You are not standing on a pump.");
                    return false;
                }
            }

            case "4": { // Change Pump Direction
                NetworkElement pos = plumber.getPosition();
                if (pos instanceof Pump) {
                    Pump pump = (Pump) pos;
                    List<Pipe> connected = pump.getConnectedPipes();
                    if (connected.size() >= 2) {
                        java.lang.System.out.println("Connected pipes:");
                        for (int i = 0; i < connected.size(); i++) {
                            java.lang.System.out.println("  " + i + ": Pipe " + connected.get(i).getId());
                        }
                        java.lang.System.out.print("Select input pipe index: ");
                        int inIdx = readInt(0);
                        java.lang.System.out.print("Select output pipe index: ");
                        int outIdx = readInt(1);
                        if (inIdx >= 0 && inIdx < connected.size()
                                && outIdx >= 0 && outIdx < connected.size()
                                && inIdx != outIdx) {
                            plumber.changePumpDirection(pump, connected.get(inIdx), connected.get(outIdx));
                            if (system.getWaterFlowManager() != null) {
                                system.getWaterFlowManager().recalculateFlow();
                            }
                            return true;
                        } else {
                            java.lang.System.out.println("Invalid pipe selection.");
                            return false;
                        }
                    } else {
                        java.lang.System.out.println("Not enough connected pipes to change direction.");
                        return false;
                    }
                } else {
                    java.lang.System.out.println("You are not standing on a pump.");
                    return false;
                }
            }

            case "5": { // Connect Pipe End
                java.lang.System.out.print("Enter pipe ID to connect: ");
                int pipeId = readInt(-1);
                java.lang.System.out.print("Enter target element ID: ");
                int targetId = readInt(-1);
                NetworkElement pipeElem = findElementById(network, pipeId);
                NetworkElement target = findElementById(network, targetId);
                if (pipeElem instanceof Pipe && target != null) {
                    plumber.connectPipeEnd((Pipe) pipeElem, target);
                    network.connectElements(pipeElem, target);
                    if (system.getWaterFlowManager() != null) {
                        system.getWaterFlowManager().recalculateFlow();
                    }
                    return true;
                } else {
                    java.lang.System.out.println("Invalid pipe or target.");
                    return false;
                }
            }

            case "6": { // Disconnect Pipe End
                java.lang.System.out.print("Enter pipe ID to disconnect: ");
                int pipeId = readInt(-1);
                NetworkElement pipeElem = findElementById(network, pipeId);
                if (pipeElem instanceof Pipe) {
                    Pipe pipe = (Pipe) pipeElem;
                    if (!pipe.isOccupied()) {
                        pipe.disconnectEnd();
                        java.lang.System.out.println("Pipe end disconnected.");
                        if (system.getWaterFlowManager() != null) {
                            system.getWaterFlowManager().recalculateFlow();
                        }
                        return true;
                    } else {
                        java.lang.System.out.println("Pipe is occupied, cannot disconnect.");
                        return false;
                    }
                } else {
                    java.lang.System.out.println("Invalid pipe ID.");
                    return false;
                }
            }

            case "7": { // Pick Up Pump from Cistern
                List<Cistern> cisterns = network.getCisterns();
                if (!cisterns.isEmpty()) {
                    Cistern cistern = cisterns.get(0);
                    java.lang.System.out.print("Pick up pump from cistern? (yes/no): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                        plumber.collectPumpFromCistern(cistern);
                        return true;
                    }
                    return false;
                } else {
                    java.lang.System.out.println("No cisterns available.");
                    return false;
                }
            }

            case "8": { // Pick Up Pipe from Cistern
                List<Cistern> cisterns = network.getCisterns();
                if (!cisterns.isEmpty()) {
                    Cistern cistern = cisterns.get(0);
                    java.lang.System.out.print("Pick up pipe from cistern? (yes/no): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                        plumber.collectPipeFromCistern(cistern);
                        return true;
                    }
                    return false;
                } else {
                    java.lang.System.out.println("No cisterns available.");
                    return false;
                }
            }

            case "9": { // Insert Pump into Pipe
                if (!plumber.isCarryingPump()) {
                    java.lang.System.out.println("You are not carrying a pump.");
                    return false;
                } else {
                    java.lang.System.out.print("Enter target pipe ID to split: ");
                    int pipeId = readInt(-1);
                    NetworkElement target = findElementById(network, pipeId);
                    if (target instanceof Pipe) {
                        Pump newPump = new Pump(pipeId * 10 + 1);
                        plumber.insertPump((Pipe) target, newPump, network);
                        if (system.getWaterFlowManager() != null) {
                            system.getWaterFlowManager().recalculateFlow();
                        }
                        return true;
                    } else {
                        java.lang.System.out.println("Invalid pipe ID.");
                        return false;
                    }
                }
            }

            case "10": { // Place New Pipe
                if (!plumber.isCarryingPipe()) {
                    java.lang.System.out.println("You are not carrying a pipe.");
                    return false;
                } else {
                    java.lang.System.out.print("Enter first connection element ID: ");
                    int aId = readInt(-1);
                    java.lang.System.out.print("Enter second connection element ID: ");
                    int bId = readInt(-1);
                    NetworkElement a = findElementById(network, aId);
                    NetworkElement b = findElementById(network, bId);
                    if (a != null && b != null) {
                        int newPipeId = network.getPipes().size() + 100;
                        Pipe newPipe = new Pipe(newPipeId);
                        plumber.placeNewPipe(newPipe, a, b, network);
                        if (system.getWaterFlowManager() != null) {
                            system.getWaterFlowManager().recalculateFlow();
                        }
                        return true;
                    } else {
                        java.lang.System.out.println("Invalid element IDs.");
                        return false;
                    }
                }
            }

            case "0":
                java.lang.System.out.println("Turn ended without action.");
                return true;

            default:
                java.lang.System.out.println("Invalid choice. Turn skipped.");
                return false;
        }
    }
    /**
     * Handles a Saboteur's turn by presenting the saboteur-specific action menu.
     */
    private static boolean handleSaboteurTurn(Saboteur saboteur, app.System system) {
        PipeNetwork network = system.getNetwork();

        java.lang.System.out.println();
        java.lang.System.out.println("=== Network Status ===");
        for (NetworkElement e : network.getElements()) {
            if (e instanceof Pipe) {
                Pipe p = (Pipe) e;
                String status = "";
                if (p.isPunctured()) status += " [PUNCTURED]";
                if (p.hasFreeEnd()) status += " [FREE END]";
                if (p.isOccupied()) status += " [Occupied by: " + p.getOccupant().getName() + "]";
                if (status.isEmpty()) status = " [OK]";
                java.lang.System.out.println("  Pipe (ID:" + e.getId() + ")" + status);
            } else if (e instanceof Pump) {
                Pump pump = (Pump) e;
                String status = pump.isBroken() ? " [BROKEN]" : " [OK]";
                java.lang.System.out.println("  Pump (ID:" + e.getId() + ")" + status);
            } else if (e instanceof Spring) {
                java.lang.System.out.println("  Spring (ID:" + e.getId() + ")");
            } else if (e instanceof Cistern) {
                Cistern c = (Cistern) e;
                java.lang.System.out.println("  Cistern (ID:" + e.getId() + ")"
                    + " [Pipes:" + c.getAvailablePipes()
                    + " Pumps:" + c.getAvailablePumps() + "]");
            }
        }
        java.lang.System.out.println("======================");

        NetworkElement saboteurPosition = saboteur.getPosition();
        java.lang.System.out.println("Current position: "
                + (saboteurPosition != null ? saboteurPosition.getClass().getSimpleName() + " (ID: " + saboteurPosition.getId() + ")" : "None"));

        java.lang.System.out.println("Actions:");
        java.lang.System.out.println("  1. Move");
        java.lang.System.out.println("  2. Puncture Pipe");
        java.lang.System.out.println("  3. Change Pump Direction");
        java.lang.System.out.println("  0. End Turn (no action)");
        java.lang.System.out.print("Choose action: ");

        String choice = scanner.nextLine().trim();

        switch (choice) {
            case "1": { // Move
                java.lang.System.out.println("Available destinations:");
                for (NetworkElement e : network.getElements()) {
                    if (e instanceof Pipe) {
                        Pipe p = (Pipe) e;
                        if (!p.isOccupied()) {
                            java.lang.System.out.println("  Pipe (ID: " + e.getId() + ")");
                        }
                    } else if (e instanceof Pump) {
                        java.lang.System.out.println("  Pump (ID: " + e.getId() + ")");
                    }
                }

                java.lang.System.out.print("Enter target element ID: ");
                int targetId = readInt(-1);
                NetworkElement target = findElementById(network, targetId);
                if (target != null) {
                    boolean moved = saboteur.moveTo(target);
                    if (!moved) {
                        java.lang.System.out.println("Invalid move, try another!");
                        return false;
                    }
                    return true;
                } else {
                    java.lang.System.out.println("Element not found.");
                    return false;
                }
            }

            case "2": { // Puncture Pipe
                NetworkElement pos = saboteur.getPosition();
                if (pos instanceof Pipe) {
                    java.lang.System.out.print("Puncture the pipe? (yes/no): ");
                    if (scanner.nextLine().trim().equalsIgnoreCase("yes")) {
                        saboteur.puncturePipe((Pipe) pos);
                        if (system.getWaterFlowManager() != null) {
                            system.getWaterFlowManager().recalculateFlow();
                        }
                        return true;
                    }
                    return false;
                } else {
                    java.lang.System.out.println("You are not standing on a pipe.");
                    return false;
                }
            }

            case "3": { // Change Pump Direction
                NetworkElement pos = saboteur.getPosition();
                if (pos instanceof Pump) {
                    Pump pump = (Pump) pos;
                    List<Pipe> connected = pump.getConnectedPipes();
                    if (connected.size() >= 2) {
                        java.lang.System.out.println("Connected pipes:");
                        for (int i = 0; i < connected.size(); i++) {
                            java.lang.System.out.println("  " + i + ": Pipe " + connected.get(i).getId());
                        }
                        java.lang.System.out.print("Select input pipe index: ");
                        int inIdx = readInt(0);
                        java.lang.System.out.print("Select output pipe index: ");
                        int outIdx = readInt(1);
                        if (inIdx >= 0 && inIdx < connected.size()
                                && outIdx >= 0 && outIdx < connected.size()
                                && inIdx != outIdx) {
                            saboteur.changePumpDirection(pump, connected.get(inIdx), connected.get(outIdx));
                            if (system.getWaterFlowManager() != null) {
                                system.getWaterFlowManager().recalculateFlow();
                            }
                            return true;
                        } else {
                            java.lang.System.out.println("Invalid pipe selection.");
                            return false;
                        }
                    } else {
                        java.lang.System.out.println("Not enough connected pipes.");
                        return false;
                    }
                } else {
                    java.lang.System.out.println("You are not standing on a pump.");
                    return false;
                }
            }

            case "0":
                java.lang.System.out.println("Turn ended without action.");
                return true;

            default:
                java.lang.System.out.println("Invalid choice. Turn skipped.");
                return false;
        }
    }
    // -----------------------------------------------------------------------
    // Utility methods
    // -----------------------------------------------------------------------

    /**
     * Reads an integer from the console, returning a default value on parse
     * failure.
     *
     * @param defaultValue the value to return if parsing fails
     * @return the parsed integer, or {@code defaultValue}
     */
    private static int readInt(int defaultValue) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Searches the network for an element with the given ID.
     *
     * @param network the {@link PipeNetwork} to search
     * @param id      the element ID to look for
     * @return the matching {@link NetworkElement}, or {@code null} if not found
     */
    private static NetworkElement findElementById(PipeNetwork network, int id) {
        if (network == null) return null;
        for (NetworkElement e : network.getElements()) {
            if (e.getId() == id) return e;
        }
        return null;
    }

    /**
     * Displays a brief summary of the game rules.
     */
    private static void showInstructions() {
        java.lang.System.out.println();
        java.lang.System.out.println("==========================================");
        java.lang.System.out.println("           GAME INSTRUCTIONS");
        java.lang.System.out.println("==========================================");
        java.lang.System.out.println("Pipes in the Desert is a two-team game.");
        java.lang.System.out.println();
        java.lang.System.out.println("PLUMBER TEAM: Transfer as much water as");
        java.lang.System.out.println("possible from springs to cisterns.");
        java.lang.System.out.println("  - Repair broken pumps and punctured pipes");
        java.lang.System.out.println("  - Set pump directions");
        java.lang.System.out.println("  - Connect and place new pipes");
        java.lang.System.out.println("  - Pick up and insert new pumps");
        java.lang.System.out.println();
        java.lang.System.out.println("SABOTEUR TEAM: Leak as much water as");
        java.lang.System.out.println("possible from the pipe system.");
        java.lang.System.out.println("  - Puncture pipes");
        java.lang.System.out.println("  - Change pump directions");
        java.lang.System.out.println();
        java.lang.System.out.println("RULES:");
        java.lang.System.out.println("  - Players move on pipes and pumps only");
        java.lang.System.out.println("  - Only one player per pipe");
        java.lang.System.out.println("  - Multiple players can share a pump");
        java.lang.System.out.println("  - One action per turn");
        java.lang.System.out.println("  - Game ends when the timer expires");
        java.lang.System.out.println("  - Team with more water wins");
        java.lang.System.out.println("==========================================");
    }
}