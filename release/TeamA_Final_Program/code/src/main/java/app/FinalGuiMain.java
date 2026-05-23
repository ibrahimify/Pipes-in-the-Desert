package app;

import java.awt.BorderLayout;
import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Graphical final version of the Pipes in the Desert game.
 *
 * <p>The class keeps the prototype domain model as the single source of truth
 * and adds a local multiplayer Swing interface for the final laboratory
 * presentation. The GUI uses the project image assets, rotates turns between
 * plumbers and saboteurs, exposes every required final feature, and keeps a
 * readable event log for demonstration and traceability.</p>
 */
public class FinalGuiMain extends JFrame {

    /** Name of the main menu screen inside the card layout. */
    private static final String MAIN_CARD = "main";

    /** Name of the setup screen inside the card layout. */
    private static final String SETUP_CARD = "setup";

    /** Name of the gameplay screen inside the card layout. */
    private static final String GAME_CARD = "game";

    /** Root layout used to switch between setup and gameplay. */
    private final CardLayout cards;

    /** Root panel controlled by {@link #cards}. */
    private final JPanel root;

    /** Pixel-art assets loaded from the shared project folder. */
    private final Map<String, BufferedImage> images;

    /** Player name inputs shown on the setup screen. */
    private final List<JTextField> playerNameFields;

    /** Role selectors shown on the setup screen. */
    private final List<JComboBox<String>> roleFields;

    /** Duration selector in minutes. */
    private final JSpinner durationMinutes;

    /** Canvas that draws the current pipe network. */
    private final NetworkCanvas canvas;

    /** Label showing the active player and role. */
    private final JLabel turnLabel;

    /** Label showing scores and remaining time. */
    private final JLabel scoreLabel;

    /** Details about the selected/current position. */
    private final JTextArea detailsArea;

    /** Event log used during the final presentation. */
    private final JTextArea logArea;

    /** Panel rebuilt on every turn with legal actions. */
    private final JPanel actionPanel;

    /** Bottom instruction bar that describes the currently selected action. */
    private final JLabel messageLabel;

    /** Last selected map element, set by clicking the center map. */
    private NetworkElement selectedElement;

    /** Active game coordinator from the prototype model. */
    private app.System gameSystem;

    /** Active pipe network. */
    private PipeNetwork network;

    /** Swing timer for refreshing remaining time. */
    private Timer refreshTimer;

    /**
     * Starts the final graphical program.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        muteModelDebugOutput();
        SwingUtilities.invokeLater(() -> {
            FinalGuiMain frame = new FinalGuiMain();
            frame.setVisible(true);
        });
    }

    /**
     * Creates the frame and prepares all screens.
     */
    public FinalGuiMain() {
        super("Pipes in the Desert - Final Program");
        this.cards = new CardLayout();
        this.root = new JPanel(cards);
        this.images = loadImageAssets();
        this.playerNameFields = new ArrayList<>();
        this.roleFields = new ArrayList<>();
        this.durationMinutes = new JSpinner(new SpinnerNumberModel(5, 1, 30, 1));
        this.canvas = new NetworkCanvas();
        this.turnLabel = new JLabel("No game running");
        this.scoreLabel = new JLabel("Scores: -");
        this.detailsArea = new JTextArea(8, 28);
        this.logArea = new JTextArea(8, 70);
        this.actionPanel = new JPanel(new GridLayout(0, 3, 8, 8));
        this.actionPanel.setOpaque(false);
        this.messageLabel = new JLabel("Select an object, then click a valid place on the map.");
        this.selectedElement = null;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1180, 800));
        setLocationByPlatform(true);

        root.add(createMainMenuPanel(), MAIN_CARD);
        root.add(createSetupPanel(), SETUP_CARD);
        root.add(createGamePanel(), GAME_CARD);
        add(root, BorderLayout.CENTER);
        cards.show(root, MAIN_CARD);

        pack();
    }

    /**
     * Hides verbose prototype debug prints during the graphical final program.
     */
    private static void muteModelDebugOutput() {
        java.lang.System.setOut(new PrintStream(OutputStream.nullOutputStream()));
    }

    /**
     * Builds the asset-backed main menu screen.
     *
     * @return configured main menu panel
     */
    private JPanel createMainMenuPanel() {
        BufferedImage background = images.containsKey("mainpage") ? images.get("mainpage") : images.get("mainmenu");
        BackgroundPanel panel = new BackgroundPanel(background);
        panel.setLayout(new GridBagLayout());

        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 14));
        buttons.setOpaque(false);
        JButton start = pixelButton("START GAME");
        JButton instructions = pixelButton("INSTRUCTIONS");
        JButton exit = pixelButton("EXIT");
        start.addActionListener(e -> cards.show(root, SETUP_CARD));
        instructions.addActionListener(e -> showInstructions());
        exit.addActionListener(e -> dispose());
        buttons.add(start);
        buttons.add(instructions);
        buttons.add(exit);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(185, 0, 0, 0);
        panel.add(buttons, gbc);
        return panel;
    }

    /**
     * Builds the setup screen where local multiplayer roles are assigned.
     *
     * @return configured setup panel
     */
    private JPanel createSetupPanel() {
        JPanel panel = new BackgroundPanel(images.get("gamesetup"));
        panel.setLayout(new BorderLayout(18, 18));
        panel.setBorder(BorderFactory.createEmptyBorder(24, 34, 24, 34));

        JLabel title = new JLabel("Pipes in the Desert");
        title.setForeground(new Color(255, 226, 77));
        title.setFont(title.getFont().deriveFont(Font.BOLD, 34f));
        panel.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(true);
        form.setBackground(new Color(197, 145, 58, 220));
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(58, 38, 18), 4),
                BorderFactory.createEmptyBorder(18, 18, 18, 18)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addSetupHeader(form, gbc);
        addPlayerRows(form, gbc);
        addDurationRow(form, gbc);

        panel.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton start = pixelButton("START FINAL PROGRAM");
        JButton back = pixelButton("BACK");
        JButton instructions = pixelButton("INSTRUCTIONS");
        start.addActionListener(this::startGame);
        back.addActionListener(e -> cards.show(root, MAIN_CARD));
        instructions.addActionListener(e -> showInstructions());
        buttons.add(start);
        buttons.add(back);
        buttons.add(instructions);
        panel.add(buttons, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Adds column labels to the setup form.
     *
     * @param form setup form panel
     * @param gbc reusable grid constraints
     */
    private void addSetupHeader(JPanel form, GridBagConstraints gbc) {
        gbc.gridy = 0;
        gbc.gridx = 0;
        form.add(new JLabel("Player"), gbc);
        gbc.gridx = 1;
        form.add(new JLabel("Role"), gbc);
    }

    /**
     * Adds default player rows with two plumbers and two saboteurs.
     *
     * @param form setup form panel
     * @param gbc reusable grid constraints
     */
    private void addPlayerRows(JPanel form, GridBagConstraints gbc) {
        String[] defaults = {"Ibrahim", "Arda", "Yahya", "Aasif"};
        String[] roles = {"Plumber", "Plumber", "Saboteur", "Saboteur"};
        for (int i = 0; i < defaults.length; i++) {
            JTextField name = new JTextField(defaults[i], 20);
            JComboBox<String> role = new JComboBox<>(new String[] {"Plumber", "Saboteur"});
            name.setFont(name.getFont().deriveFont(Font.BOLD, 15f));
            role.setSelectedItem(roles[i]);
            playerNameFields.add(name);
            roleFields.add(role);

            gbc.gridy = i + 1;
            gbc.gridx = 0;
            form.add(name, gbc);
            gbc.gridx = 1;
            form.add(role, gbc);
        }
    }

    /**
     * Adds the game duration row to the setup form.
     *
     * @param form setup form panel
     * @param gbc reusable grid constraints
     */
    private void addDurationRow(JPanel form, GridBagConstraints gbc) {
        gbc.gridy = playerNameFields.size() + 1;
        gbc.gridx = 0;
        form.add(new JLabel("Duration in minutes"), gbc);
        gbc.gridx = 1;
        form.add(durationMinutes, gbc);
    }

    /**
     * Builds the gameplay screen.
     *
     * @return configured gameplay panel
     */
    private JPanel createGamePanel() {
        JPanel panel = new BackgroundPanel(images.get("gameplayscreen"));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel top = new JPanel(new GridLayout(1, 2, 8, 8));
        top.setOpaque(false);
        turnLabel.setFont(turnLabel.getFont().deriveFont(Font.BOLD, 16f));
        scoreLabel.setFont(scoreLabel.getFont().deriveFont(Font.BOLD, 16f));
        styleHudLabel(turnLabel);
        styleHudLabel(scoreLabel);
        top.add(turnLabel);
        top.add(scoreLabel);
        panel.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);

        canvas.setMapClickHandler(this::handleMapClick);

        JPanel leftPanel = new JPanel(new BorderLayout(6, 6));
        leftPanel.setOpaque(false);
        JLabel leftTitle = new JLabel("STATUS / INFO");
        styleHudLabel(leftTitle);
        leftPanel.add(leftTitle, BorderLayout.NORTH);
        detailsArea.setEditable(false);
        detailsArea.setBackground(new Color(20, 20, 20));
        detailsArea.setForeground(new Color(239, 220, 164));
        detailsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        leftPanel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(285, 460));

        center.add(leftPanel, BorderLayout.WEST);
        center.add(canvas, BorderLayout.CENTER);
        center.add(createRightToolbar(), BorderLayout.EAST);
        panel.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(10, 10));
        bottom.setOpaque(false);
        styleHudLabel(messageLabel);
        bottom.add(messageLabel, BorderLayout.NORTH);
        logArea.setEditable(false);
        logArea.setBackground(new Color(20, 20, 20));
        logArea.setForeground(new Color(174, 219, 255));
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        bottom.add(new JScrollPane(logArea), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    /**
     * Builds the right-side object/action toolbar requested for the final GUI.
     *
     * @return toolbar panel
     */
    private JPanel createRightToolbar() {
        JPanel toolbar = new JPanel(new GridLayout(0, 1, 6, 6));
        toolbar.setOpaque(true);
        toolbar.setBackground(new Color(18, 18, 18, 220));
        toolbar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(91, 62, 35), 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        toolbar.setPreferredSize(new Dimension(230, 460));

        addToolbarSection(toolbar, "OBJECTS");
        addToolbarButton(toolbar, "Add Pipe", e -> addPipeTool());
        addToolbarButton(toolbar, "Add Pump", e -> addPumpTool());
        addToolbarSection(toolbar, "PIPE TOOLS");
        addToolbarButton(toolbar, "Rotate Pipe", e -> setMessage("Pipe rotation selected. Click a pipe on the map."));
        addToolbarButton(toolbar, "Remove Pipe", e -> disconnectPipe());
        addToolbarButton(toolbar, "Repair Pipe", e -> repairPipe());
        addToolbarSection(toolbar, "PUMP TOOLS");
        addToolbarButton(toolbar, "Rotate Direction", e -> setPumpDirection(gameSystem.getCurrentPlayer()));
        addToolbarButton(toolbar, "Remove Pump", e -> removePumpTool());
        addToolbarButton(toolbar, "Repair Pump", e -> repairPump());
        addToolbarSection(toolbar, "PLAYER ACTIONS");
        addToolbarButton(toolbar, "Move", e -> moveCurrentPlayer());
        addToolbarButton(toolbar, "End Turn", e -> endCurrentTurn());
        addToolbarSection(toolbar, "DEMO");
        addToolbarButton(toolbar, "Break Pump", e -> breakPumpEvent());
        addToolbarButton(toolbar, "Water Flow", e -> recalculateWater());
        addToolbarButton(toolbar, "End Game", e -> finishGame());
        return toolbar;
    }

    /**
     * Adds one section label to the toolbar.
     *
     * @param toolbar parent toolbar
     * @param title section title
     */
    private void addToolbarSection(JPanel toolbar, String title) {
        JLabel label = new JLabel(title);
        label.setForeground(new Color(255, 226, 77));
        label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        toolbar.add(label);
    }

    /**
     * Adds one action button to the toolbar.
     *
     * @param toolbar parent toolbar
     * @param text button text
     * @param action button action
     */
    private void addToolbarButton(JPanel toolbar, String text, java.awt.event.ActionListener action) {
        JButton button = pixelButton(text);
        button.addActionListener(action);
        toolbar.add(button);
    }

    /**
     * Creates the domain objects and starts a local multiplayer game.
     *
     * @param event button event
     */
    private void startGame(ActionEvent event) {
        Team plumbers = new Team("Plumbers");
        Team saboteurs = new Team("Saboteurs");

        for (int i = 0; i < playerNameFields.size(); i++) {
            String name = playerNameFields.get(i).getText().trim();
            if (name.isEmpty()) {
                name = "Player" + (i + 1);
            }
            if ("Saboteur".equals(roleFields.get(i).getSelectedItem())) {
                saboteurs.addMember(new Saboteur(name, saboteurs));
            } else {
                plumbers.addMember(new Plumber(name, plumbers));
            }
        }

        if (plumbers.getSize() < 2 || saboteurs.getSize() < 2) {
            JOptionPane.showMessageDialog(this,
                    "The final game needs at least two plumbers and two saboteurs.",
                    "Invalid team setup", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Team> teams = new ArrayList<>();
        teams.add(plumbers);
        teams.add(saboteurs);

        network = new PipeNetwork();
        ScoreBoard scoreBoard = new ScoreBoard(plumbers, saboteurs);
        int durationSeconds = ((Integer) durationMinutes.getValue()) * 60;
        gameSystem = new app.System(teams, network, new GameTimer(durationSeconds), scoreBoard);
        gameSystem.startGame();
        network = gameSystem.getNetwork();
        selectedElement = null;

        logArea.setText("");
        log("Game started with " + plumbers.getSize() + " plumbers and "
                + saboteurs.getSize() + " saboteurs.");
        log("Initial network: Spring -> Pipe -> Pump -> Pipe -> Cistern.");

        startRefreshTimer();
        cards.show(root, GAME_CARD);
        refreshGameView();
    }

    /**
     * Starts or restarts the small timer that updates the score label.
     */
    private void startRefreshTimer() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        refreshTimer = new Timer(1000, e -> {
            if (gameSystem != null && gameSystem.isRunning()) {
                refreshGameView();
                if (gameSystem.checkEndCondition()) {
                    finishGame();
                }
            }
        });
        refreshTimer.start();
    }

    /**
     * Displays concise game instructions.
     */
    private void showInstructions() {
        JOptionPane.showMessageDialog(this,
                "Local turns rotate between all plumbers and saboteurs.\n"
                        + "Players move only on pipes and pumps.\n"
                        + "Use the action buttons to demonstrate the required final cases.",
                "Instructions", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Refreshes labels, canvas, details, and enabled action buttons.
     */
    private void refreshGameView() {
        if (gameSystem == null) {
            return;
        }

        Player current = gameSystem.getCurrentPlayer();
        if (current == null) {
            turnLabel.setText("No active player");
        } else {
            turnLabel.setText("Turn: " + current.getName() + " (" + current.getClass().getSimpleName() + ")");
        }

        ScoreBoard scoreBoard = gameSystem.getScoreBoard();
        scoreLabel.setText("Scores - Plumbers: " + scoreBoard.getPlumberScore()
                + " | Saboteurs: " + scoreBoard.getSaboteurScore()
                + " | Time: " + gameSystem.getTimer().getRemainingTime() + "s");

        detailsArea.setText(buildDetailsText());
        rebuildActions();
        canvas.setState(network, gameSystem.getTeams(), current);
    }

    /**
     * Handles clicks on the center map.
     *
     * @param point clicked pixel coordinate inside the map panel
     */
    private void handleMapClick(Point point) {
        NetworkElement clicked = canvas.findElementAt(point);
        if (clicked == null) {
            setMessage("Empty grid tile selected. Choose a toolbar action or click an object.");
            return;
        }
        selectedElement = clicked;
        setMessage("Selected " + describe(clicked) + ". Valid adjacent actions are available from the toolbar.");
        refreshGameView();
    }

    /**
     * Updates the bottom instruction bar and event log together.
     *
     * @param message instruction text
     */
    private void setMessage(String message) {
        messageLabel.setText(message);
        log(message);
    }

    /**
     * Builds a compact textual state summary for the side panel.
     *
     * @return state summary text
     */
    private String buildDetailsText() {
        StringBuilder builder = new StringBuilder();
        Player current = gameSystem.getCurrentPlayer();
        if (current != null) {
            builder.append("Current player: ").append(current.getName()).append('\n');
            builder.append("Position: ").append(describe(current.getPosition())).append("\n\n");
        }
        builder.append("Selected: ").append(describe(selectedElement)).append("\n\n");

        builder.append("Pipes\n");
        for (Pipe pipe : network.getPipes()) {
            builder.append(" - ").append(describe(pipe));
            if (pipe.isPunctured()) {
                builder.append(" PUNCTURED");
            }
            if (pipe.hasFreeEnd()) {
                builder.append(" FREE_END");
            }
            if (pipe.isOccupied()) {
                builder.append(" occupied by ").append(pipe.getOccupant().getName());
            }
            builder.append('\n');
        }

        builder.append("\nPumps\n");
        for (Pump pump : network.getPumps()) {
            builder.append(" - ").append(describe(pump));
            builder.append(pump.isBroken() ? " BROKEN" : " OK");
            builder.append(" input=").append(describe(pump.getActiveInput()));
            builder.append(" output=").append(describe(pump.getActiveOutput()));
            builder.append('\n');
        }

        builder.append("\nCisterns\n");
        for (Cistern cistern : network.getCisterns()) {
            builder.append(" - ").append(describe(cistern))
                    .append(" water=").append(cistern.getStoredWater())
                    .append(" pipes=").append(cistern.getAvailablePipes())
                    .append(" pumps=").append(cistern.getAvailablePumps())
                    .append('\n');
        }
        return builder.toString();
    }

    /**
     * Rebuilds the button area for the active player and demo events.
     */
    private void rebuildActions() {
        actionPanel.removeAll();
        if (gameSystem == null || !gameSystem.isRunning()) {
            addAction("New Game", e -> cards.show(root, SETUP_CARD));
            actionPanel.revalidate();
            actionPanel.repaint();
            return;
        }

        Player player = gameSystem.getCurrentPlayer();
        addAction("Move", e -> moveCurrentPlayer());
        addAction("End Turn", e -> endCurrentTurn());

        if (player instanceof Plumber) {
            addAction("Repair Pipe", e -> repairPipe());
            addAction("Repair Pump", e -> repairPump());
            addAction("Set Pump Direction", e -> setPumpDirection(player));
            addAction("Disconnect Pipe", e -> disconnectPipe());
            addAction("Connect Free Pipe", e -> connectFreePipe());
            addAction("Pick Up Pump", e -> pickUpPump());
            addAction("Pick Up Pipe", e -> pickUpPipe());
            addAction("Insert Pump", e -> insertPump());
            addAction("Place Pipe", e -> placePipe());
        } else if (player instanceof Saboteur) {
            addAction("Puncture Pipe", e -> puncturePipe());
            addAction("Set Pump Direction", e -> setPumpDirection(player));
        }

        addAction("Generate Parts", e -> generateParts());
        addAction("Break Pump Event", e -> breakPumpEvent());
        addAction("Recalculate Water", e -> recalculateWater());
        addAction("End Game", e -> finishGame());
        actionPanel.revalidate();
        actionPanel.repaint();
    }

    /**
     * Adds one button to the action panel.
     *
     * @param text button text
     * @param action action to execute
     */
    private void addAction(String text, java.awt.event.ActionListener action) {
        JButton button = pixelButton(text);
        button.addActionListener(action);
        actionPanel.add(button);
    }

    /**
     * Adds a generated pipe from a cistern with one free end.
     */
    private void addPipeTool() {
        if (gameSystem == null || !gameSystem.isRunning()) {
            return;
        }
        Cistern cistern = chooseCistern();
        if (cistern == null) {
            return;
        }
        Pipe pipe = new Pipe(network.generateId());
        network.addElement(pipe);
        network.connectElements(cistern, pipe);
        pipe.disconnectEnd();
        setMessage("Added " + describe(pipe) + " at " + describe(cistern)
                + ". One end is free and leaks until connected.");
        recalculateWater();
    }

    /**
     * Inserts a new pump into a selected pipe.
     */
    private void addPumpTool() {
        if (gameSystem == null || !gameSystem.isRunning()) {
            return;
        }
        Pipe pipe = selectedElement instanceof Pipe
                ? (Pipe) selectedElement
                : (Pipe) chooseElement("Pipe for new pump", new ArrayList<NetworkElement>(network.getPipes()));
        if (pipe == null) {
            return;
        }
        Pump pump = new Pump(network.generateId());
        network.insertPump(pipe, pump);
        selectedElement = pump;
        setMessage("Added " + describe(pump) + " by inserting it into " + describe(pipe) + ".");
        refreshGameView();
    }

    /**
     * Removes a selected pump from the network for demonstration.
     */
    private void removePumpTool() {
        if (gameSystem == null || !gameSystem.isRunning()) {
            return;
        }
        Pump pump = selectedElement instanceof Pump
                ? (Pump) selectedElement
                : (Pump) chooseElement("Pump to remove", new ArrayList<NetworkElement>(network.getPumps()));
        if (pump == null) {
            return;
        }
        network.removeElement(pump);
        selectedElement = null;
        setMessage("Removed " + describe(pump) + ". Disconnected pipes now leak from free ends.");
        recalculateWater();
    }

    /**
     * Creates a dark pixel-style button that fits the final art direction.
     *
     * @param text label text
     * @return styled button
     */
    private JButton pixelButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(61, 43, 27));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(16, 13, 11), 3),
                BorderFactory.createEmptyBorder(8, 18, 8, 18)));
        button.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        return button;
    }

    /**
     * Applies the dark HUD label style used on the gameplay screen.
     *
     * @param label label to style
     */
    private void styleHudLabel(JLabel label) {
        label.setOpaque(true);
        label.setBackground(new Color(18, 18, 18, 220));
        label.setForeground(new Color(255, 229, 113));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(91, 62, 35), 3),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
    }

    /**
     * Moves the active player to a reachable pipe or pump.
     */
    private void moveCurrentPlayer() {
        Player player = gameSystem.getCurrentPlayer();
        List<NetworkElement> candidates = new ArrayList<>();
        for (NetworkElement neighbor : network.getNeighbors(player.getPosition())) {
            if (neighbor instanceof Pump) {
                candidates.add(neighbor);
            } else if (neighbor instanceof Pipe) {
                Pipe pipe = (Pipe) neighbor;
                if (!pipe.isOccupied() || pipe.getOccupant() == player) {
                    candidates.add(pipe);
                }
            }
        }

        NetworkElement target = chooseElement("Move target", candidates);
        if (target == null) {
            return;
        }

        boolean moved = player.moveTo(target);
        if (moved) {
            log(player.getName() + " moved to " + describe(target) + ".");
            completeTurn();
        } else {
            log("Move rejected.");
            refreshGameView();
        }
    }

    /**
     * Repairs the pipe under the active plumber.
     */
    private void repairPipe() {
        Plumber plumber = currentPlumber();
        if (plumber == null || !(plumber.getPosition() instanceof Pipe)) {
            log("Repair pipe requires a plumber standing on a pipe.");
            return;
        }
        plumber.repairPipe((Pipe) plumber.getPosition());
        finishIfActionUsed(plumber, "Pipe repaired.");
    }

    /**
     * Repairs the pump under the active plumber.
     */
    private void repairPump() {
        Plumber plumber = currentPlumber();
        if (plumber == null || !(plumber.getPosition() instanceof Pump)) {
            log("Repair pump requires a plumber standing on a pump.");
            return;
        }
        plumber.repairPump((Pump) plumber.getPosition());
        finishIfActionUsed(plumber, "Pump repaired.");
    }

    /**
     * Punctures the pipe under the active saboteur.
     */
    private void puncturePipe() {
        Player player = gameSystem.getCurrentPlayer();
        if (!(player instanceof Saboteur) || !(player.getPosition() instanceof Pipe)) {
            log("Puncture requires a saboteur standing on a pipe.");
            return;
        }
        ((Saboteur) player).puncturePipe((Pipe) player.getPosition());
        finishIfActionUsed(player, "Pipe punctured; leakage can now be demonstrated.");
    }

    /**
     * Sets pump input and output for the active player.
     *
     * @param player active player
     */
    private void setPumpDirection(Player player) {
        if (!(player.getPosition() instanceof Pump)) {
            log("Pump direction can be set only while standing on a pump.");
            return;
        }
        Pump pump = (Pump) player.getPosition();
        List<Pipe> connected = pump.getConnectedPipes();
        Pipe input = (Pipe) chooseElement("Input pipe", new ArrayList<NetworkElement>(connected));
        Pipe output = (Pipe) chooseElement("Output pipe", new ArrayList<NetworkElement>(connected));
        if (input == null || output == null) {
            return;
        }

        if (player instanceof Plumber) {
            ((Plumber) player).changePumpDirection(pump, input, output);
        } else if (player instanceof Saboteur) {
            ((Saboteur) player).changePumpDirection(pump, input, output);
        }
        finishIfActionUsed(player, "Pump direction set to " + input.getId() + " -> " + output.getId() + ".");
    }

    /**
     * Disconnects a pipe from one of its neighbors.
     */
    private void disconnectPipe() {
        Plumber plumber = currentPlumber();
        if (plumber == null || !plumber.canAct()) {
            return;
        }
        Pipe pipe = (Pipe) chooseElement("Pipe to disconnect", new ArrayList<NetworkElement>(network.getPipes()));
        if (pipe == null || pipe.getNeighbors().isEmpty()) {
            return;
        }
        NetworkElement target = chooseElement("Disconnect from", pipe.getNeighbors());
        if (target == null) {
            return;
        }
        network.disconnectElements(pipe, target);
        plumber.endTurn();
        log(describe(pipe) + " disconnected from " + describe(target) + "; free end leaks water.");
        completeTurn();
    }

    /**
     * Connects a free pipe end to another element.
     */
    private void connectFreePipe() {
        Plumber plumber = currentPlumber();
        if (plumber == null || !plumber.canAct()) {
            return;
        }
        List<NetworkElement> freePipes = new ArrayList<>();
        for (Pipe pipe : network.getPipes()) {
            if (pipe.hasFreeEnd()) {
                freePipes.add(pipe);
            }
        }
        Pipe pipe = (Pipe) chooseElement("Free pipe", freePipes);
        if (pipe == null) {
            return;
        }
        NetworkElement target = chooseElement("Connect to", new ArrayList<>(network.getElements()));
        if (target == null || target == pipe) {
            return;
        }
        plumber.connectPipeEnd(pipe, target);
        finishIfActionUsed(plumber, describe(pipe) + " connected to " + describe(target) + ".");
    }

    /**
     * Lets the active plumber pick up a pump from a reachable cistern.
     */
    private void pickUpPump() {
        Plumber plumber = currentPlumber();
        Cistern cistern = chooseCistern();
        if (plumber == null || cistern == null) {
            return;
        }
        plumber.collectPumpFromCistern(cistern);
        finishIfActionUsed(plumber, "Pump picked up from " + describe(cistern) + ".");
    }

    /**
     * Lets the active plumber pick up a pipe from a reachable cistern.
     */
    private void pickUpPipe() {
        Plumber plumber = currentPlumber();
        Cistern cistern = chooseCistern();
        if (plumber == null || cistern == null) {
            return;
        }
        plumber.collectPipeFromCistern(cistern);
        finishIfActionUsed(plumber, "Pipe picked up from " + describe(cistern) + ".");
    }

    /**
     * Inserts a carried pump into a selected pipe.
     */
    private void insertPump() {
        Plumber plumber = currentPlumber();
        if (plumber == null || !plumber.isCarryingPump()) {
            log("The plumber must carry a pump first.");
            return;
        }
        Pipe pipe = (Pipe) chooseElement("Pipe to split", new ArrayList<NetworkElement>(network.getPipes()));
        if (pipe == null) {
            return;
        }
        Pump pump = new Pump(network.generateId());
        plumber.insertPump(pipe, pump, network);
        finishIfActionUsed(plumber, "Inserted pump " + pump.getId() + " into " + describe(pipe) + ".");
    }

    /**
     * Places a carried pipe between two selected endpoints.
     */
    private void placePipe() {
        Plumber plumber = currentPlumber();
        if (plumber == null || !plumber.isCarryingPipe()) {
            log("The plumber must carry a pipe first.");
            return;
        }
        NetworkElement first = chooseElement("First endpoint", new ArrayList<>(network.getElements()));
        NetworkElement second = chooseElement("Second endpoint", new ArrayList<>(network.getElements()));
        if (first == null || second == null || first == second) {
            return;
        }
        Pipe pipe = new Pipe(network.generateId());
        plumber.placeNewPipe(pipe, first, second, network);
        finishIfActionUsed(plumber, "Placed new " + describe(pipe) + " between "
                + describe(first) + " and " + describe(second) + ".");
    }

    /**
     * Generates spare pipes and pumps at every cistern for presentation.
     */
    private void generateParts() {
        for (Cistern cistern : network.getCisterns()) {
            cistern.generatePipe();
            cistern.generatePump();
        }
        log("Generated one pipe and one pump at every cistern.");
        refreshGameView();
    }

    /**
     * Deterministically breaks the first working pump.
     */
    private void breakPumpEvent() {
        for (Pump pump : network.getPumps()) {
            if (!pump.isBroken()) {
                pump.breakDown();
                log("Breakdown event: " + describe(pump) + " is broken.");
                recalculateWater();
                return;
            }
        }
        log("No working pump is available to break.");
    }

    /**
     * Recalculates water flow and updates scores.
     */
    private void recalculateWater() {
        gameSystem.getWaterFlowManager().recalculateFlow();
        log("Water flow recalculated.");
        refreshGameView();
    }

    /**
     * Ends the current player's turn without a role action.
     */
    private void endCurrentTurn() {
        Player player = gameSystem.getCurrentPlayer();
        if (player != null) {
            player.endTurn();
            log(player.getName() + " ended the turn.");
            completeTurn();
        }
    }

    /**
     * Completes a successful action, processes flow, and advances the turn.
     */
    private void completeTurn() {
        gameSystem.getWaterFlowManager().recalculateFlow();
        if (gameSystem.checkEndCondition()) {
            finishGame();
            return;
        }
        gameSystem.nextTurn();
        refreshGameView();
    }

    /**
     * Completes the turn if the model consumed the active player's action.
     *
     * @param player player that attempted the action
     * @param successMessage message logged when the action succeeds
     */
    private void finishIfActionUsed(Player player, String successMessage) {
        if (player != null && player.hasActedThisTurn()) {
            log(successMessage);
            completeTurn();
        } else {
            log("Action rejected by the game rules.");
            refreshGameView();
        }
    }

    /**
     * Ends the game and shows the winner.
     */
    private void finishGame() {
        if (gameSystem == null) {
            return;
        }
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        ScoreBoard scoreBoard = gameSystem.getScoreBoard();
        Team winner = scoreBoard.determineWinner();
        gameSystem.endGame();
        String result = winner == null ? "Game ended in a draw." : "Winner: " + winner.getName();
        log(result);
        JOptionPane.showMessageDialog(this,
                result + "\nPlumbers: " + scoreBoard.getPlumberScore()
                        + "\nSaboteurs: " + scoreBoard.getSaboteurScore(),
                "Game Over", JOptionPane.INFORMATION_MESSAGE);
        refreshGameView();
    }

    /**
     * Returns the active player as a plumber when possible.
     *
     * @return active plumber, or null
     */
    private Plumber currentPlumber() {
        Player player = gameSystem.getCurrentPlayer();
        return player instanceof Plumber ? (Plumber) player : null;
    }

    /**
     * Lets the presenter choose one cistern.
     *
     * @return selected cistern, or null
     */
    private Cistern chooseCistern() {
        return (Cistern) chooseElement("Cistern", new ArrayList<NetworkElement>(network.getCisterns()));
    }

    /**
     * Displays a choice dialog for network elements.
     *
     * @param title dialog title
     * @param elements candidate elements
     * @return selected element, or null
     */
    private NetworkElement chooseElement(String title, List<? extends NetworkElement> elements) {
        if (elements == null || elements.isEmpty()) {
            log("No candidates for: " + title + ".");
            return null;
        }

        Map<String, NetworkElement> choices = new LinkedHashMap<>();
        for (NetworkElement element : elements) {
            choices.put(describe(element), element);
        }

        Object selected = JOptionPane.showInputDialog(this, title, title,
                JOptionPane.PLAIN_MESSAGE, null, choices.keySet().toArray(), choices.keySet().iterator().next());
        return selected == null ? null : choices.get(selected.toString());
    }

    /**
     * Appends one line to the event log.
     *
     * @param message message to append
     */
    private void log(String message) {
        logArea.append(message + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    /**
     * Returns a readable element description.
     *
     * @param element element to describe
     * @return text such as Pipe#4
     */
    private String describe(NetworkElement element) {
        if (element == null) {
            return "-";
        }
        return element.getClass().getSimpleName() + "#" + element.getId();
    }

    /**
     * Loads all PNG images from the project asset folder.
     *
     * @return image map keyed by lower-case file name without extension
     */
    private static Map<String, BufferedImage> loadImageAssets() {
        Map<String, BufferedImage> loaded = new HashMap<>();
        for (Path folder : candidateAssetFolders()) {
            File dir = folder.toFile();
            if (!dir.isDirectory()) {
                continue;
            }
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
            if (files == null) {
                continue;
            }
            for (File file : files) {
                try {
                    String key = file.getName().replaceFirst("(?i)\\.png$", "").toLowerCase();
                    loaded.put(key, ImageIO.read(file));
                } catch (IOException ignored) {
                    // Missing images are tolerated; every visual has a simple fallback.
                }
            }
            if (!loaded.isEmpty()) {
                return loaded;
            }
        }
        return loaded;
    }

    /**
     * Returns possible asset locations for running from repo root or code.
     *
     * @return candidate folders
     */
    private static List<Path> candidateAssetFolders() {
        List<Path> folders = new ArrayList<>();
        folders.add(Paths.get("assets", "images"));
        folders.add(Paths.get("..", "assets", "images"));
        folders.add(Paths.get("..", "..", "assets", "images"));
        return folders;
    }

    /**
     * Panel that paints a scaled pixel-art background image.
     */
    private static final class BackgroundPanel extends JPanel {

        /** Background image, or null for a painted fallback. */
        private final BufferedImage image;

        /**
         * Creates a background panel.
         *
         * @param image image to draw
         */
        private BackgroundPanel(BufferedImage image) {
            this.image = image;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics;
            if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            } else {
                g.setColor(new Color(197, 145, 58));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    /**
     * Draws the game network with the existing GUI image assets.
     */
    private static final class NetworkCanvas extends JPanel {

        /** Tile size used by the simple click grid. */
        private static final int TILE_SIZE = 64;

        /** Current network to draw. */
        private PipeNetwork network;

        /** Current teams, used for player token rendering. */
        private List<Team> teams;

        /** Active player highlighted on the canvas. */
        private Player currentPlayer;

        /** Loaded image assets by file stem. */
        private final Map<String, BufferedImage> images;

        /** Last drawn element positions for click lookup. */
        private Map<NetworkElement, Point> lastLayout;

        /** Callback invoked when the user clicks on the grid map. */
        private MapClickHandler mapClickHandler;

        /**
         * Creates a canvas and loads assets.
         */
        private NetworkCanvas() {
            this.network = null;
            this.teams = new ArrayList<>();
            this.currentPlayer = null;
            this.images = FinalGuiMain.loadImageAssets();
            this.lastLayout = new HashMap<>();
            this.mapClickHandler = null;
            setPreferredSize(new Dimension(850, 500));
            setBackground(new Color(238, 224, 184));
            setBorder(BorderFactory.createLineBorder(new Color(32, 24, 16), 4));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (mapClickHandler != null) {
                        mapClickHandler.clicked(event.getPoint());
                    }
                }
            });
        }

        /**
         * Sets the click callback used by the enclosing frame.
         *
         * @param handler click handler
         */
        private void setMapClickHandler(MapClickHandler handler) {
            this.mapClickHandler = handler;
        }

        /**
         * Updates the canvas state.
         *
         * @param network active network
         * @param teams active teams
         * @param currentPlayer active player
         */
        private void setState(PipeNetwork network, List<Team> teams, Player currentPlayer) {
            this.network = network;
            this.teams = teams == null ? new ArrayList<>() : teams;
            this.currentPlayer = currentPlayer;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            BufferedImage background = images.get("gameplayscreen");
            if (background != null) {
                g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
            }

            if (network == null) {
                drawCentered(g, "Start a game to see the pipe system.");
                return;
            }

            Map<NetworkElement, Point> layout = computeLayout();
            lastLayout = layout;
            drawGrid(g);
            drawConnections(g, layout);
            drawElements(g, layout);
            drawPlayers(g, layout);
        }

        /**
         * Returns the element closest to a clicked point.
         *
         * @param point clicked point
         * @return element near the point, or null
         */
        private NetworkElement findElementAt(Point point) {
            if (point == null || lastLayout == null) {
                return null;
            }
            NetworkElement closest = null;
            double best = Double.MAX_VALUE;
            for (Map.Entry<NetworkElement, Point> entry : lastLayout.entrySet()) {
                double distance = entry.getValue().distance(point);
                if (distance < best) {
                    best = distance;
                    closest = entry.getKey();
                }
            }
            return best <= TILE_SIZE ? closest : null;
        }

        /**
         * Computes deterministic positions for all elements.
         *
         * @return element-position map
         */
        private Map<NetworkElement, Point> computeLayout() {
            Map<NetworkElement, Point> layout = new HashMap<>();
            List<NetworkElement> elements = new ArrayList<>(network.getElements());
            int w = getWidth();
            int h = getHeight();
            for (NetworkElement element : elements) {
                if (element instanceof Spring) {
                    layout.put(element, new Point((int) (w * 0.10), (int) (h * 0.17)));
                } else if (element instanceof Pump && element.getId() == 3) {
                    layout.put(element, new Point((int) (w * 0.49), (int) (h * 0.20)));
                } else if (element instanceof Cistern) {
                    layout.put(element, new Point((int) (w * 0.87), (int) (h * 0.22)));
                } else if (element instanceof Pipe && element.getId() == 4) {
                    layout.put(element, new Point((int) (w * 0.31), (int) (h * 0.20)));
                } else if (element instanceof Pipe && element.getId() == 5) {
                    layout.put(element, new Point((int) (w * 0.68), (int) (h * 0.20)));
                }
            }

            int columns = 5;
            int xGap = Math.max(120, w / (columns + 1));
            int yGap = 120;
            int extraIndex = 0;
            for (int i = 0; i < elements.size(); i++) {
                if (layout.containsKey(elements.get(i))) {
                    continue;
                }
                int row = extraIndex / columns;
                int col = extraIndex % columns;
                layout.put(elements.get(i), new Point((col + 1) * xGap, (int) (h * 0.47) + row * yGap));
                extraIndex++;
            }
            return layout;
        }

        /**
         * Draws a subtle fixed-size grid for valid click positions.
         *
         * @param g graphics context
         */
        private void drawGrid(Graphics2D g) {
            g.setColor(new Color(255, 237, 170, 45));
            for (int x = 0; x < getWidth(); x += TILE_SIZE) {
                g.drawLine(x, 0, x, getHeight());
            }
            for (int y = 0; y < getHeight(); y += TILE_SIZE) {
                g.drawLine(0, y, getWidth(), y);
            }
        }

        /**
         * Draws network connections.
         *
         * @param g graphics context
         * @param layout positions
         */
        private void drawConnections(Graphics2D g, Map<NetworkElement, Point> layout) {
            g.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(72, 55, 38, 180));
            for (NetworkElement element : network.getElements()) {
                Point a = layout.get(element);
                for (NetworkElement neighbor : element.getNeighbors()) {
                    if (element.getId() < neighbor.getId()) {
                        Point b = layout.get(neighbor);
                        if (a != null && b != null) {
                            g.drawLine(a.x, a.y, b.x, b.y);
                        }
                    }
                }
            }
            g.setStroke(new BasicStroke(1f));
        }

        /**
         * Draws physical network elements.
         *
         * @param g graphics context
         * @param layout positions
         */
        private void drawElements(Graphics2D g, Map<NetworkElement, Point> layout) {
            for (NetworkElement element : network.getElements()) {
                Point p = layout.get(element);
                if (p == null) {
                    continue;
                }
                BufferedImage image = images.get(imageKey(element));
                if (image != null) {
                    Dimension size = assetSize(element);
                    Image scaled = image.getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
                    g.drawImage(scaled, p.x - size.width / 2, p.y - size.height / 2, null);
                } else {
                    g.setColor(Color.WHITE);
                    g.fillOval(p.x - 26, p.y - 26, 52, 52);
                    g.setColor(Color.BLACK);
                    g.drawOval(p.x - 26, p.y - 26, 52, 52);
                }

                if (element == currentPosition()) {
                    g.setColor(new Color(255, 215, 0));
                    g.setStroke(new BasicStroke(3f));
                    g.drawOval(p.x - 48, p.y - 46, 96, 92);
                    g.setStroke(new BasicStroke(1f));
                }

                g.setColor(new Color(10, 10, 10, 190));
                g.fillRoundRect(p.x - 42, p.y + 42, 84, 18, 6, 6);
                g.setColor(new Color(255, 239, 174));
                g.drawString(element.getClass().getSimpleName() + "#" + element.getId(), p.x - 38, p.y + 56);
            }
        }

        /**
         * Draws player tokens around their current elements.
         *
         * @param g graphics context
         * @param layout positions
         */
        private void drawPlayers(Graphics2D g, Map<NetworkElement, Point> layout) {
            Map<NetworkElement, Integer> offsets = new HashMap<>();
            for (Team team : teams) {
                for (Player player : team.getMembers()) {
                    Point p = layout.get(player.getPosition());
                    if (p == null) {
                        continue;
                    }
                    int index = offsets.getOrDefault(player.getPosition(), 0);
                    offsets.put(player.getPosition(), index + 1);
                    int x = p.x - 42 + index * 34;
                    int y = p.y + 28;
                    boolean plumber = player instanceof Plumber;
                    BufferedImage token = images.get(plumber ? "plumber" : "saboteur");
                    if (token != null) {
                        g.drawImage(token.getScaledInstance(54, 48, Image.SCALE_SMOOTH), x, y, null);
                    } else {
                        g.setColor(plumber ? new Color(37, 111, 182) : new Color(175, 63, 55));
                        g.fillOval(x, y, 24, 24);
                    }
                    if (player == currentPlayer) {
                        g.setColor(Color.YELLOW);
                        g.setStroke(new BasicStroke(3f));
                        g.drawRoundRect(x - 4, y - 4, 62, 56, 12, 12);
                        g.setStroke(new BasicStroke(1f));
                    }
                }
            }
        }

        /**
         * Chooses a display size for each type of asset.
         *
         * @param element element whose image will be drawn
         * @return width and height
         */
        private Dimension assetSize(NetworkElement element) {
            if (element instanceof Spring) {
                return new Dimension(86, 70);
            }
            if (element instanceof Cistern) {
                return new Dimension(92, 116);
            }
            if (element instanceof Pump) {
                return new Dimension(118, 86);
            }
            if (element instanceof Pipe) {
                return new Dimension(126, 64);
            }
            return new Dimension(70, 58);
        }

        /**
         * Returns the active player's current position.
         *
         * @return active position or null
         */
        private NetworkElement currentPosition() {
            return currentPlayer == null ? null : currentPlayer.getPosition();
        }

        /**
         * Chooses the best asset for an element state.
         *
         * @param element element to draw
         * @return image map key
         */
        private String imageKey(NetworkElement element) {
            if (element instanceof Spring) {
                return "spring";
            }
            if (element instanceof Cistern) {
                return "cistern";
            }
            if (element instanceof Pump) {
                return ((Pump) element).isBroken() ? "brokenpump" : "pump";
            }
            if (element instanceof Pipe) {
                Pipe pipe = (Pipe) element;
                if (pipe.isPunctured() || pipe.hasFreeEnd()) {
                    return "brokenpaper";
                }
                return "pipe" + ((pipe.getId() % 4) + 1);
            }
            return "";
        }

        /**
         * Draws fallback text when no game is active.
         *
         * @param g graphics context
         * @param text text to draw
         */
        private void drawCentered(Graphics2D g, String text) {
            g.setColor(Color.DARK_GRAY);
            g.drawString(text, getWidth() / 2 - 90, getHeight() / 2);
        }

        /**
         * Loads all PNG images from the project asset folder.
         *
         * @return image map
         */
        private Map<String, BufferedImage> loadImages() {
            Map<String, BufferedImage> loaded = new HashMap<>();
            for (Path folder : candidateAssetFolders()) {
                File dir = folder.toFile();
                if (!dir.isDirectory()) {
                    continue;
                }
                File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".png"));
                if (files == null) {
                    continue;
                }
                for (File file : files) {
                    try {
                        String key = file.getName().replaceFirst("(?i)\\.png$", "").toLowerCase();
                        loaded.put(key, ImageIO.read(file));
                    } catch (IOException ignored) {
                        // Missing images are tolerated; the canvas has vector fallbacks.
                    }
                }
                if (!loaded.isEmpty()) {
                    return loaded;
                }
            }
            return loaded;
        }

        /**
         * Returns possible asset locations for running from repo root or code.
         *
         * @return candidate folders
         */
        private List<Path> candidateAssetFolders() {
            List<Path> folders = new ArrayList<>();
            folders.add(Paths.get("assets", "images"));
            folders.add(Paths.get("..", "assets", "images"));
            folders.add(Paths.get("..", "..", "assets", "images"));
            return folders;
        }
    }

    /**
     * Functional interface used to report map clicks from the canvas.
     */
    private interface MapClickHandler {

        /**
         * Called when the center map is clicked.
         *
         * @param point clicked pixel coordinate
         */
        void clicked(Point point);
    }
}
