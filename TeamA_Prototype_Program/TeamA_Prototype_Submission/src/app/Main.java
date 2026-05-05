package app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Console entry point for the Pipes in the Desert prototype.
 *
 * <p>The prototype is intentionally text based. It allows players to create
 * teams, start a complete game session, inspect the pipe network, and perform
 * all plumber and saboteur actions from the command line.</p>
 */
public class Main {

    /** Shared scanner for console or scripted input. */
    private static Scanner scanner = new Scanner(java.lang.System.in);

    /** True after scripted input reaches the end of file. */
    private static boolean inputExhausted = false;

    /**
     * Starts the program.
     *
     * @param args optional load/save arguments
     */
    public static void main(String[] args) {
        configureIoFromArgs(args);

        boolean running = true;
        while (running) {
            java.lang.System.out.println();
            java.lang.System.out.println("==========================================");
            java.lang.System.out.println("       PIPES IN THE DESERT PROTOTYPE");
            java.lang.System.out.println("==========================================");
            java.lang.System.out.println("1. Start Game");
            java.lang.System.out.println("2. Instructions");
            java.lang.System.out.println("3. Exit");
            java.lang.System.out.println("You may also type: load <file> or save <file>");
            java.lang.System.out.print("Choose an option: ");

            String choice = readLine().trim();
            if (handleIoCommand(choice)) {
                continue;
            }

            switch (choice) {
                case "1":
                    startGame();
                    break;
                case "2":
                    showInstructions();
                    break;
                case "3":
                case "0":
                    java.lang.System.out.println("Goodbye!");
                    running = false;
                    break;
                default:
                    if (inputExhausted && choice.isEmpty()) {
                        running = false;
                    } else {
                        java.lang.System.out.println("Invalid option.");
                    }
                    break;
            }
        }
    }

    /**
     * Starts one configured game.
     */
    private static void startGame() {
        Team plumberTeam = new Team("Plumbers");
        Team saboteurTeam = new Team("Saboteurs");

        java.lang.System.out.println();
        java.lang.System.out.println("--- Game Setup ---");
        int playerCount = readInt("How many players? Minimum 4: ", 4, 4);

        for (int i = 1; i <= playerCount; i++) {
            java.lang.System.out.print("Player " + i + " name: ");
            String name = readLine().trim();
            if (name.isEmpty()) {
                name = "Player" + i;
            }

            java.lang.System.out.println("Choose team for " + name + ":");
            java.lang.System.out.println("1. Plumber");
            java.lang.System.out.println("2. Saboteur");
            int teamChoice = readInt("Choice: ", 1, 1);

            if (teamChoice == 2) {
                saboteurTeam.addMember(new Saboteur(name, saboteurTeam));
            } else {
                plumberTeam.addMember(new Plumber(name, plumberTeam));
            }
        }

        if (plumberTeam.getSize() < 2 || saboteurTeam.getSize() < 2) {
            java.lang.System.out.println("Each team must have at least two players.");
            java.lang.System.out.println("Plumbers: " + plumberTeam.getSize()
                    + ", Saboteurs: " + saboteurTeam.getSize());
            return;
        }

        int duration = readInt("Game duration in rounds: ", 5, 1);
        List<Team> teams = new ArrayList<>();
        teams.add(plumberTeam);
        teams.add(saboteurTeam);

        PipeNetwork network = new PipeNetwork();
        GameTimer timer = new GameTimer(duration*60);
        ScoreBoard scoreBoard = new ScoreBoard(plumberTeam, saboteurTeam);
        app.System system = new app.System(teams, network, timer, scoreBoard);
        system.setInputScanner(scanner);
        system.startGame();

        runGameLoop(system);
    }

    /**
     * Runs turns until the game ends.
     *
     * @param system active game system
     */
    private static void runGameLoop(app.System system) {
        while (system.isRunning()) {
            system.updateRoundEvents();

            Player current = system.getCurrentPlayer();
            if (current == null) {
                java.lang.System.out.println("No active player. Game stopped.");
                system.endGame();
                return;
            }

            boolean completed = false;
            while (!completed && system.isRunning()) {
                printHeader(current);
                printNetwork(system.getNetwork());
                completed = handlePlayerTurn(current, system);
                if (!completed && !inputExhausted) {
                    java.lang.System.out.println("Action was not completed. Choose again.");
                }
                if (inputExhausted) {
                    completed = true;
                }
            }

            system.nextTurn();
            printScores(system);

            if (system.checkEndCondition()) {
                java.lang.System.out.println();
                java.lang.System.out.println("========== GAME OVER ==========");
                system.endGame();
            }
        }
    }

    /**
     * Dispatches a turn to the correct role menu.
     *
     * @param player active player
     * @param system active game system
     * @return true when the turn may advance
     */
    private static boolean handlePlayerTurn(Player player, app.System system) {
        if (player instanceof Plumber) {
            return handlePlumberTurn((Plumber) player, system);
        }
        if (player instanceof Saboteur) {
            return handleSaboteurTurn((Saboteur) player, system);
        }
        return true;
    }

    /**
     * Handles one plumber menu selection.
     *
     * @param plumber active plumber
     * @param system active game system
     * @return true when the turn may advance
     */
    private static boolean handlePlumberTurn(Plumber plumber, app.System system) {
        PipeNetwork network = system.getNetwork();

        java.lang.System.out.println("Plumber actions:");
        java.lang.System.out.println("1. Move");
        java.lang.System.out.println("2. Repair current pipe");
        java.lang.System.out.println("3. Repair current pump");
        java.lang.System.out.println("4. Change current pump direction");
        java.lang.System.out.println("5. Connect a free pipe end");
        java.lang.System.out.println("6. Disconnect a pipe end");
        java.lang.System.out.println("7. Pick up pump from reachable cistern");
        java.lang.System.out.println("8. Pick up pipe from reachable cistern");
        java.lang.System.out.println("9. Insert carried pump into current pipe");
        java.lang.System.out.println("10. Place carried pipe");
        java.lang.System.out.println("0. End turn");
        int choice = readInt("Choose action: ", -1, -1);

        switch (choice) {
            case 1:
                return movePlayer(plumber, network);
            case 2:
                return repairPipe(plumber, system);
            case 3:
                return repairPump(plumber, system);
            case 4:
                return changePumpDirection(plumber, system);
            case 5:
                return connectPipeEnd(plumber, system);
            case 6:
                return disconnectPipeEnd(network, system);
            case 7:
                return collectPump(plumber, network);
            case 8:
                return collectPipe(plumber, network);
            case 9:
                return insertPump(plumber, network, system);
            case 10:
                return placePipe(plumber, network, system);
            case 0:
                return true;
            default:
                java.lang.System.out.println("Invalid plumber action.");
                return false;
        }
    }

    /**
     * Handles one saboteur menu selection.
     *
     * @param saboteur active saboteur
     * @param system active game system
     * @return true when the turn may advance
     */
    private static boolean handleSaboteurTurn(Saboteur saboteur, app.System system) {
        PipeNetwork network = system.getNetwork();

        java.lang.System.out.println("Saboteur actions:");
        java.lang.System.out.println("1. Move");
        java.lang.System.out.println("2. Puncture current pipe");
        java.lang.System.out.println("3. Change current pump direction");
        java.lang.System.out.println("0. End turn");
        int choice = readInt("Choose action: ", -1, -1);

        switch (choice) {
            case 1:
                return movePlayer(saboteur, network);
            case 2:
                return puncturePipe(saboteur, system);
            case 3:
                return changePumpDirection(saboteur, system);
            case 0:
                return true;
            default:
                java.lang.System.out.println("Invalid saboteur action.");
                return false;
        }
    }

    /**
     * Moves a player to an adjacent pipe or pump.
     *
     * @param player moving player
     * @param network active network
     * @return true if movement succeeded
     */
    private static boolean movePlayer(Player player, PipeNetwork network) {
        List<NetworkElement> destinations = getMoveDestinations(player.getPosition(), network);
        if (destinations.isEmpty()) {
            java.lang.System.out.println("No valid movement destinations.");
            return false;
        }

        java.lang.System.out.println("Available destinations:");
        printElementList(destinations);
        int targetId = readInt("Target ID: ", -1, -1);
        NetworkElement target = network.findElementById(targetId);
        return target != null && player.moveTo(target);
    }

    /**
     * Repairs the pipe under the plumber.
     */
    private static boolean repairPipe(Plumber plumber, app.System system) {
        if (!(plumber.getPosition() instanceof Pipe)) {
            java.lang.System.out.println("You are not standing on a pipe.");
            return false;
        }
        boolean wasAble = plumber.canAct();
        plumber.repairPipe((Pipe) plumber.getPosition());
        return afterAction(plumber, wasAble, system);
    }

    /**
     * Repairs the pump under the plumber.
     */
    private static boolean repairPump(Plumber plumber, app.System system) {
        if (!(plumber.getPosition() instanceof Pump)) {
            java.lang.System.out.println("You are not standing on a pump.");
            return false;
        }
        boolean wasAble = plumber.canAct();
        plumber.repairPump((Pump) plumber.getPosition());
        return afterAction(plumber, wasAble, system);
    }

    /**
     * Punctures the pipe under the saboteur.
     */
    private static boolean puncturePipe(Saboteur saboteur, app.System system) {
        if (!(saboteur.getPosition() instanceof Pipe)) {
            java.lang.System.out.println("You are not standing on a pipe.");
            return false;
        }
        boolean wasAble = saboteur.canAct();
        saboteur.puncturePipe((Pipe) saboteur.getPosition());
        return afterAction(saboteur, wasAble, system);
    }

    /**
     * Changes pump direction for a plumber.
     */
    private static boolean changePumpDirection(Plumber plumber, app.System system) {
        if (!(plumber.getPosition() instanceof Pump)) {
            java.lang.System.out.println("You are not standing on a pump.");
            return false;
        }
        Pump pump = (Pump) plumber.getPosition();
        Pipe[] selected = selectDirection(pump);
        if (selected == null) {
            return false;
        }

        boolean wasAble = plumber.canAct();
        plumber.changePumpDirection(pump, selected[0], selected[1]);
        return afterAction(plumber, wasAble, system);
    }

    /**
     * Changes pump direction for a saboteur.
     */
    private static boolean changePumpDirection(Saboteur saboteur, app.System system) {
        if (!(saboteur.getPosition() instanceof Pump)) {
            java.lang.System.out.println("You are not standing on a pump.");
            return false;
        }
        Pump pump = (Pump) saboteur.getPosition();
        Pipe[] selected = selectDirection(pump);
        if (selected == null) {
            return false;
        }

        boolean wasAble = saboteur.canAct();
        saboteur.changePumpDirection(pump, selected[0], selected[1]);
        return afterAction(saboteur, wasAble, system);
    }

    /**
     * Connects one free pipe end.
     */
    private static boolean connectPipeEnd(Plumber plumber, app.System system) {
        PipeNetwork network = system.getNetwork();
        List<Pipe> freePipes = new ArrayList<>();
        for (Pipe pipe : network.getPipes()) {
            if (pipe.hasFreeEnd()) {
                freePipes.add(pipe);
            }
        }
        if (freePipes.isEmpty()) {
            java.lang.System.out.println("There are no pipes with a free end.");
            return false;
        }

        java.lang.System.out.println("Free pipes:");
        printElementList(new ArrayList<NetworkElement>(freePipes));
        Pipe pipe = asPipe(network.findElementById(readInt("Pipe ID: ", -1, -1)));
        NetworkElement target = network.findElementById(readInt("Target element ID: ", -1, -1));
        if (pipe == null || target == null) {
            return false;
        }

        boolean wasAble = plumber.canAct();
        plumber.connectPipeEnd(pipe, target);
        if (!plumber.canAct()) {
            network.connectElements(pipe, target);
        }
        return afterAction(plumber, wasAble, system);
    }

    /**
     * Disconnects a selected pipe from a selected neighbor.
     */
    private static boolean disconnectPipeEnd(PipeNetwork network, app.System system) {
        Pipe pipe = asPipe(network.findElementById(readInt("Pipe ID to disconnect: ", -1, -1)));
        if (pipe == null || pipe.isOccupied() || pipe.getNeighbors().isEmpty()) {
            java.lang.System.out.println("Pipe cannot be disconnected.");
            return false;
        }

        java.lang.System.out.println("Connected elements:");
        printElementList(pipe.getNeighbors());
        NetworkElement target = network.findElementById(readInt("Disconnect from element ID: ", -1, -1));
        if (target == null || !pipe.isAdjacentTo(target)) {
            return false;
        }

        network.disconnectElements(pipe, target);
        recalculate(system);
        return true;
    }

    /**
     * Collects a pump from a reachable cistern.
     */
private static boolean collectPump(Plumber plumber, PipeNetwork network) {
    Cistern cistern = selectReachableCistern(plumber, network);
    if (cistern == null) {
        return false;
    }

    boolean hadPumpBefore = plumber.isCarryingPump();
    plumber.collectPumpFromCistern(cistern);

    return !hadPumpBefore && plumber.isCarryingPump();
}

    /**
     * Collects a pipe from a reachable cistern.
     */
    private static boolean collectPipe(Plumber plumber, PipeNetwork network) {
        Cistern cistern = selectReachableCistern(plumber, network);
        if (cistern == null) {
            return false;
        }
        boolean wasAble = plumber.canAct();
        plumber.collectPipeFromCistern(cistern);
        return wasAble && !plumber.canAct();
    }

    /**
     * Inserts a carried pump into the current pipe.
     */
    private static boolean insertPump(Plumber plumber, PipeNetwork network, app.System system) {
        if (!plumber.isCarryingPump() || !(plumber.getPosition() instanceof Pipe)) {
            java.lang.System.out.println("You must carry a pump and stand on a pipe.");
            return false;
        }

        boolean wasAble = plumber.canAct();
        Pump pump = new Pump(network.generateId());
        plumber.insertPump((Pipe) plumber.getPosition(), pump, network);
        return afterAction(plumber, wasAble, system);
    }

    /**
     * Places a carried pipe between two selected elements.
     */
    private static boolean placePipe(Plumber plumber, PipeNetwork network, app.System system) {
        if (!plumber.isCarryingPipe()) {
            java.lang.System.out.println("You are not carrying a pipe.");
            return false;
        }

        NetworkElement first = network.findElementById(readInt("First endpoint ID: ", -1, -1));
        NetworkElement second = network.findElementById(readInt("Second endpoint ID: ", -1, -1));
        if (first == null || second == null || first == second) {
            return false;
        }

        boolean wasAble = plumber.canAct();
        Pipe pipe = new Pipe(network.generateId());
        plumber.placeNewPipe(pipe, first, second, network);
        return afterAction(plumber, wasAble, system);
    }

    /**
     * Selects input and output pipes from a pump.
     */
    private static Pipe[] selectDirection(Pump pump) {
        List<Pipe> connected = pump.getConnectedPipes();
        if (connected.size() < 2) {
            java.lang.System.out.println("The pump needs at least two connected pipes.");
            return null;
        }

        java.lang.System.out.println("Connected pipes:");
        for (int i = 0; i < connected.size(); i++) {
            java.lang.System.out.println(i + ". Pipe " + connected.get(i).getId());
        }

        int input = readInt("Input pipe index: ", -1, -1);
        int output = readInt("Output pipe index: ", -1, -1);
        if (input < 0 || output < 0
                || input >= connected.size()
                || output >= connected.size()
                || input == output) {
            java.lang.System.out.println("Invalid direction.");
            return null;
        }

        return new Pipe[] { connected.get(input), connected.get(output) };
    }

    /**
     * Selects a cistern reachable from the plumber's current position.
     */
private static Cistern selectReachableCistern(Plumber plumber, PipeNetwork network) {
    List<Cistern> reachable = new ArrayList<>();
    NetworkElement position = plumber.getPosition();

    for (Cistern cistern : network.getCisterns()) {
        boolean canReach = position == cistern
                || (position != null && position.isAdjacentTo(cistern));

        if (!canReach && position != null) {
            for (NetworkElement neighbor : cistern.getNeighbors()) {
                if (position == neighbor || position.isAdjacentTo(neighbor)) {
                    canReach = true;
                    break;
                }
            }
        }

        if (canReach) {
            reachable.add(cistern);
        }
    }

    if (reachable.isEmpty()) {
        java.lang.System.out.println("No reachable cistern.");
        return null;
    }

    java.lang.System.out.println("Reachable cisterns:");
    printElementList(new ArrayList<NetworkElement>(reachable));
    return asCistern(network.findElementById(readInt("Cistern ID: ", -1, -1)));
}

    /**
     * Handles post-action water recalculation.
     */
    private static boolean afterAction(Player player, boolean wasAble, app.System system) {
        boolean completed = wasAble && !player.canAct();
        if (completed) {
            recalculate(system);
        }
        return completed;
    }

    /**
     * Recalculates water flow if available.
     */
    private static void recalculate(app.System system) {
        if (system.getWaterFlowManager() != null) {
            system.getWaterFlowManager().recalculateFlow();
        }
    }

    /**
     * Prints one turn header.
     */
    private static void printHeader(Player player) {
        java.lang.System.out.println();
        java.lang.System.out.println("---------- " + player.getName()
                + " (" + player.getClass().getSimpleName() + ") ----------");
        NetworkElement position = player.getPosition();
        java.lang.System.out.println("Position: " + describeElement(position));
    }

    /**
     * Prints the current network state.
     */
    private static void printNetwork(PipeNetwork network) {
        java.lang.System.out.println();
        java.lang.System.out.println("Network:");
        for (NetworkElement element : network.getElements()) {
            java.lang.System.out.println("  " + describeElement(element)
                    + " -> " + describeNeighbors(element));
        }
    }

    /**
     * Prints current scores and timer state.
     */
    private static void printScores(app.System system) {
        ScoreBoard scoreBoard = system.getScoreBoard();
        GameTimer timer = system.getTimer();
        if (scoreBoard != null) {
            java.lang.System.out.println("Scores | Plumbers: "
                    + scoreBoard.getPlumberScore()
                    + " | Saboteurs: " + scoreBoard.getSaboteurScore());
        }
        if (timer != null) {
            java.lang.System.out.println("Remaining time: " + timer.getRemainingTime() + " seconds");
        }
    }

    /**
     * Returns valid move targets.
     */
    private static List<NetworkElement> getMoveDestinations(NetworkElement current,
                                                            PipeNetwork network) {
        List<NetworkElement> result = new ArrayList<>();
        if (current == null) {
            return result;
        }

        for (NetworkElement element : network.getElements()) {
            if (!(element instanceof Pipe) && !(element instanceof Pump)) {
                continue;
            }
            if (!current.isAdjacentTo(element)) {
                continue;
            }
            if (element instanceof Pipe && ((Pipe) element).isOccupied()) {
                continue;
            }
            result.add(element);
        }
        return result;
    }

    /**
     * Prints a compact element list.
     */
    private static void printElementList(List<? extends NetworkElement> elements) {
        for (NetworkElement element : elements) {
            java.lang.System.out.println("  " + describeElement(element));
        }
    }

    /**
     * Describes one element for the CLI.
     */
    private static String describeElement(NetworkElement element) {
        if (element == null) {
            return "None";
        }
        String text = element.getClass().getSimpleName() + " " + element.getId();
        if (element instanceof Pipe) {
            Pipe pipe = (Pipe) element;
            if (pipe.isPunctured()) {
                text += " [punctured]";
            }
            if (pipe.hasFreeEnd()) {
                text += " [free end]";
            }
            if (pipe.isOccupied()) {
                text += " [occupied by " + pipe.getOccupant().getName() + "]";
            }
        } else if (element instanceof Pump) {
            Pump pump = (Pump) element;
            text += pump.isBroken() ? " [broken]" : " [working]";
            if (pump.getActiveInput() != null && pump.getActiveOutput() != null) {
                text += " [in " + pump.getActiveInput().getId()
                        + " -> out " + pump.getActiveOutput().getId() + "]";
            }
        } else if (element instanceof Cistern) {
            Cistern cistern = (Cistern) element;
            text += " [pipes " + cistern.getAvailablePipes()
                    + ", pumps " + cistern.getAvailablePumps() + "]";
        }
        return text;
    }

    /**
     * Describes neighbors of one element.
     */
    private static String describeNeighbors(NetworkElement element) {
        List<NetworkElement> neighbors = element.getNeighbors();
        if (neighbors.isEmpty()) {
            return "no neighbors";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < neighbors.size(); i++) {
            if (i > 0) {
                builder.append(", ");
            }
            NetworkElement neighbor = neighbors.get(i);
            builder.append(neighbor.getClass().getSimpleName())
                    .append(" ")
                    .append(neighbor.getId());
        }
        return builder.toString();
    }

    /**
     * Shows the rules.
     */
    private static void showInstructions() {
        java.lang.System.out.println();
        java.lang.System.out.println("Plumbers repair pipes and pumps, set pump directions,");
        java.lang.System.out.println("connect new pipes, and insert new pumps.");
        java.lang.System.out.println("Saboteurs puncture pipes and change pump directions.");
        java.lang.System.out.println("Players can stand only on pipes and pumps.");
        java.lang.System.out.println("Only one player may stand on a pipe.");
        java.lang.System.out.println("Several players may stand on the same pump.");
        java.lang.System.out.println("The team with more collected water score wins.");
    }

    /**
     * Configures load/save arguments.
     */
    private static void configureIoFromArgs(String[] args) {
        for (int i = 0; args != null && i < args.length; i++) {
            if ("load".equalsIgnoreCase(args[i]) && i + 1 < args.length) {
                loadInput(args[++i]);
            } else if ("save".equalsIgnoreCase(args[i]) && i + 1 < args.length) {
                saveOutput(args[++i]);
            }
        }
    }

    /**
     * Handles load/save commands typed in the main menu.
     */
    private static boolean handleIoCommand(String command) {
        if (command == null) {
            return false;
        }
        String[] parts = command.trim().split("\\s+", 2);
        if (parts.length != 2) {
            return false;
        }
        if ("load".equalsIgnoreCase(parts[0])) {
            loadInput(parts[1]);
            return true;
        }
        if ("save".equalsIgnoreCase(parts[0])) {
            saveOutput(parts[1]);
            return true;
        }
        return false;
    }

    /**
     * Redirects input from a file.
     */
    private static void loadInput(String path) {
        try {
            scanner = new Scanner(new FileInputStream(path));
            inputExhausted = false;
            java.lang.System.out.println("Input loaded from: " + path);
        } catch (FileNotFoundException e) {
            java.lang.System.out.println("Could not load input file: " + path);
        }
    }

    /**
     * Redirects output to a file.
     */
    private static void saveOutput(String path) {
        try {
            java.lang.System.setOut(new PrintStream(path));
            java.lang.System.out.println("Output saved to: " + path);
        } catch (FileNotFoundException e) {
            java.lang.System.err.println("Could not open output file: " + path);
        }
    }

    /**
     * Reads an integer with lower-bound validation.
     */
    private static int readInt(String prompt, int defaultValue, int minimum) {
        java.lang.System.out.print(prompt);
        String line = readLine();
        try {
            int value = Integer.parseInt(line.trim());
            if (value < minimum) {
                return defaultValue;
            }
            return value;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Reads one line from input.
     */
    private static String readLine() {
        if (scanner != null && scanner.hasNextLine()) {
            return scanner.nextLine();
        }
        inputExhausted = true;
        return "";
    }

    /**
     * Cast helper for pipes.
     */
    private static Pipe asPipe(NetworkElement element) {
        return element instanceof Pipe ? (Pipe) element : null;
    }

    /**
     * Cast helper for cisterns.
     */
    private static Cistern asCistern(NetworkElement element) {
        return element instanceof Cistern ? (Cistern) element : null;
    }
}
