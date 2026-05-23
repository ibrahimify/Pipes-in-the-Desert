package app;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
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
import javax.swing.ImageIcon;
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
import javax.swing.border.AbstractBorder;

/**
 * Final graphical user interface for the Pipes in the Desert game.
 *
 * <p>The class implements the final Swing presentation layer required by the
 * course: a JFrame-based application, a custom JPanel map renderer, a 64 by 64
 * grid system, sprite rendering through BufferedImage, a top HUD, a right-side
 * toolbar, and a bottom instruction bar. The existing prototype model remains
 * the source of truth for rules and scoring.</p>
 */
public class FinalGuiMain extends JFrame {

    /** Screen card for the main menu. */
    private static final String MENU_CARD = "menu";

    /** Screen card for the setup form. */
    private static final String SETUP_CARD = "setup";

    /** Screen card for the instructions view. */
    private static final String INSTRUCTIONS_CARD = "instructions";

    /** Screen card for gameplay. */
    private static final String GAME_CARD = "game";

    /** Width and height of one map tile in pixels. */
    private static final int TILE_SIZE = 64;

    /** Number of columns in the playable grid. */
    private static final int GRID_COLS = 11;

    /** Number of rows in the playable grid. */
    private static final int GRID_ROWS = 7;

    /** Card layout used to switch screens. */
    private final CardLayout cards;

    /** Root panel managed by the card layout. */
    private final JPanel root;

    /** Loaded pixel-art assets. */
    private final Map<String, BufferedImage> images;

    /** Pixel font used by menu, HUD, buttons, and game messages. */
    private final Font pixelFont;

    /** Player-name fields in the setup screen. */
    private final List<JTextField> playerNameFields;

    /** Role selectors in the setup screen. */
    private final List<JComboBox<String>> roleFields;

    /** Game duration selector. */
    private final JSpinner durationMinutes;

    /** Custom tile-map renderer. */
    private final GameMapPanel mapPanel;

    /** Top HUD label for turn and active player. */
    private final JLabel turnLabel;

    /** Top HUD label for timer. */
    private final JLabel timerLabel;

    /** Top HUD label for scores. */
    private final JLabel scoreLabel;

    /** Score value label for plumbers. */
    private final JLabel plumberScoreLabel;

    /** Score value label for saboteurs. */
    private final JLabel saboteurScoreLabel;

    /** Left status panel text. */
    private final JTextArea statusArea;

    /** Bottom contextual instruction bar. */
    private final JLabel messageBar;

    /** Active game system from the prototype model. */
    private app.System gameSystem;

    /** Active pipe network. */
    private PipeNetwork network;

    /** Element selected by the most recent map click. */
    private NetworkElement selectedElement;

    /** Empty tile selected by the most recent map click. */
    private Tile selectedTile;

    /** Currently selected toolbar action. */
    private Tool activeTool;

    /** Tile positions for visible model elements. */
    private final Map<NetworkElement, Tile> elementTiles;

    /** Visual rotation state for pipe sprites. */
    private final Map<Pipe, Integer> pipeRotations;

    /** Timer that refreshes the HUD. */
    private Timer refreshTimer;

    /**
     * Starts the final graphical program.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        mutePrototypeDebugOutput();
        SwingUtilities.invokeLater(() -> new FinalGuiMain().setVisible(true));
    }

    /**
     * Creates the frame, screens, and shared UI state.
     */
    public FinalGuiMain() {
        super("Pipes in the Desert - Final GUI");
        this.cards = new CardLayout();
        this.root = new JPanel(cards);
        this.images = loadImages();
        this.pixelFont = loadPixelFont();
        this.playerNameFields = new ArrayList<>();
        this.roleFields = new ArrayList<>();
        this.durationMinutes = new JSpinner(new SpinnerNumberModel(5, 1, 20, 1));
        this.mapPanel = new GameMapPanel();
        this.turnLabel = new JLabel("TURN: -");
        this.timerLabel = new JLabel("TIMER: -");
        this.scoreLabel = new JLabel("SCORE: -");
        this.plumberScoreLabel = new JLabel("0");
        this.saboteurScoreLabel = new JLabel("0");
        this.statusArea = new JTextArea(20, 24);
        this.messageBar = new JLabel("Select an object, then click a valid place on the map.");
        this.selectedElement = null;
        this.selectedTile = null;
        this.activeTool = Tool.SELECT;
        this.elementTiles = new LinkedHashMap<>();
        this.pipeRotations = new HashMap<>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 720));
        setPreferredSize(new Dimension(1920, 1080));

        root.add(createMenuPanel(), MENU_CARD);
        root.add(createSetupPanel(), SETUP_CARD);
        root.add(createInstructionsPanel(), INSTRUCTIONS_CARD);
        root.add(createGamePanel(), GAME_CARD);
        add(root, BorderLayout.CENTER);
        cards.show(root, MENU_CARD);
        pack();
        setSize(new Dimension(1920, 1080));
        setLocationRelativeTo(null);
    }

    /**
     * Suppresses the prototype's method-level trace prints in GUI mode.
     */
    private static void mutePrototypeDebugOutput() {
        java.lang.System.setOut(new PrintStream(OutputStream.nullOutputStream()));
    }

    /**
     * Creates the pixel-art main menu.
     *
     * @return menu screen
     */
    private JPanel createMenuPanel() {
        BackgroundPanel panel = new BackgroundPanel(screenBackground());
        panel.setLayout(new GridBagLayout());

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);

        JLabel title = titleLabel("PIPES IN THE DESERT", 48);
        JLabel subtitle = subtitleLabel("MAIN MENU");
        JPanel buttons = new JPanel(new GridLayout(0, 1, 0, 12));
        buttons.setOpaque(false);
        JButton start = menuButton("START GAME");
        JButton instructions = menuButton("INSTRUCTIONS");
        JButton exit = menuButton("EXIT");
        start.addActionListener(e -> cards.show(root, SETUP_CARD));
        instructions.addActionListener(e -> showInstructions());
        exit.addActionListener(e -> dispose());
        buttons.add(start);
        buttons.add(instructions);
        buttons.add(exit);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 12, 0);
        content.add(title, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 24, 0);
        content.add(subtitle, gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(buttons, gbc);

        panel.add(content, new GridBagConstraints());
        return panel;
    }

    /**
     * Creates an in-app instructions screen instead of a modal dialog.
     *
     * @return instructions screen
     */
    private JPanel createInstructionsPanel() {
        BackgroundPanel panel = new BackgroundPanel(screenBackground());
        panel.setLayout(new GridBagLayout());

        JPanel card = new JPanel(new BorderLayout(18, 18));
        card.setPreferredSize(new Dimension(760, 520));
        card.setBackground(new Color(28, 20, 14, 232));
        card.setBorder(retroPanelBorder(4, 24));

        JLabel title = titleLabel("HOW TO PLAY", 34);
        title.setHorizontalAlignment(JLabel.CENTER);
        card.add(title, BorderLayout.NORTH);

        JTextArea text = new JTextArea(instructionText());
        text.setEditable(false);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setOpaque(false);
        text.setForeground(new Color(248, 231, 181));
        text.setFont(readableFont(16f, Font.PLAIN));
        text.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        card.add(text, BorderLayout.CENTER);

        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        JButton back = menuButton("BACK");
        back.setPreferredSize(new Dimension(180, 48));
        back.addActionListener(e -> cards.show(root, MENU_CARD));
        bottom.add(back);
        card.add(bottom, BorderLayout.SOUTH);

        panel.add(card, new GridBagConstraints());
        return panel;
    }

    /**
     * Creates the setup screen with local role assignment.
     *
     * @return setup screen
     */
    private JPanel createSetupPanel() {
        BackgroundPanel panel = new BackgroundPanel(screenBackground());
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(24, 36, 24, 36));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(new Color(195, 139, 55, 232));
        form.setBorder(retroPanelBorder(4, 24));

        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(7, 10, 7, 10);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        addSetupRows(form, formGbc);

        JPanel buttons = new JPanel();
        buttons.setOpaque(false);
        JButton start = menuButton("START GAME");
        JButton back = menuButton("BACK");
        back.setPreferredSize(new Dimension(150, 48));
        start.addActionListener(this::startGame);
        back.addActionListener(e -> cards.show(root, MENU_CARD));
        buttons.add(start);
        buttons.add(back);

        JPanel content = new JPanel(new GridBagLayout());
        content.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 10, 0);
        content.add(titleLabel("PIPES IN THE DESERT", 40), gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 18, 0);
        content.add(subtitleLabel("GAME SETUP"), gbc);
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 18, 0);
        content.add(form, gbc);
        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 0, 0);
        content.add(buttons, gbc);

        panel.add(content, new GridBagConstraints());
        return panel;
    }

    /**
     * Adds player and duration rows to the setup form.
     *
     * @param form form panel
     * @param gbc reusable constraints
     */
    private void addSetupRows(JPanel form, GridBagConstraints gbc) {
        String[] defaults = {"Ibrahim", "Arda", "Yahya", "Aasif"};
        String[] roles = {"Plumber", "Plumber", "Saboteur", "Saboteur"};
        for (int i = 0; i < defaults.length; i++) {
            JTextField name = new JTextField(defaults[i], 18);
            JComboBox<String> role = new JComboBox<>(new String[] {"Plumber", "Saboteur"});
            role.setSelectedItem(roles[i]);
            styleInput(name);
            styleInput(role);
            name.setPreferredSize(new Dimension(300, 34));
            role.setPreferredSize(new Dimension(170, 34));
            playerNameFields.add(name);
            roleFields.add(role);

            gbc.gridy = i;
            gbc.gridx = 0;
            JLabel playerIcon = new JLabel(scaledIcon(
                    "Saboteur".equals(roles[i]) ? image("saboteur") : image("plumber"), 28, 28));
            form.add(playerIcon, gbc);
            gbc.gridx = 1;
            form.add(retroLabel("PLAYER " + (i + 1)), gbc);
            gbc.gridx = 2;
            form.add(name, gbc);
            gbc.gridx = 3;
            form.add(role, gbc);
        }

        gbc.gridy = defaults.length + 1;
        gbc.gridx = 0;
        form.add(retroLabel(""), gbc);
        gbc.gridx = 1;
        form.add(retroLabel("Duration"), gbc);
        gbc.gridx = 2;
        styleInput(durationMinutes);
        durationMinutes.setPreferredSize(new Dimension(100, 34));
        form.add(durationMinutes, gbc);
        gbc.gridx = 3;
        form.add(retroLabel("minutes"), gbc);
    }

    /**
     * Creates the main gameplay screen.
     *
     * @return gameplay screen
     */
    private JPanel createGamePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(95, 56, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(createTopBar(), BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);
        center.add(createLeftStatusPanel(), BorderLayout.WEST);
        center.add(mapPanel, BorderLayout.CENTER);
        center.add(createRightToolbar(), BorderLayout.EAST);
        panel.add(center, BorderLayout.CENTER);

        styleHudLabel(messageBar);
        messageBar.setFont(readableFont(15f, Font.BOLD));
        panel.add(messageBar, BorderLayout.SOUTH);
        return panel;
    }

    /**
     * Creates the top HUD bar.
     *
     * @return top bar
     */
    private JPanel createTopBar() {
        JPanel top = new JPanel(new GridLayout(1, 3, 10, 0));
        top.setOpaque(false);
        top.add(wrapHud(turnLabel));
        top.add(wrapHud(new JLabel(clockIcon(26, 26), JLabel.CENTER), timerLabel));
        top.add(scoreHud());
        return top;
    }

    /**
     * Wraps one or more labels in a centered HUD panel.
     *
     * @param labels labels to place
     * @return styled HUD panel
     */
    private JPanel wrapHud(JLabel... labels) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(24, 19, 14));
        panel.setBorder(retroHudBorder());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 5, 0, 5);
        for (int i = 0; i < labels.length; i++) {
            JLabel label = labels[i];
            styleHudText(label);
            gbc.gridx = i;
            panel.add(label, gbc);
        }
        return panel;
    }

    /**
     * Creates a HUD panel with separate team icons and scores.
     *
     * @return score HUD panel
     */
    private JPanel scoreHud() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(24, 19, 14));
        panel.setBorder(retroHudBorder());

        JLabel plumberIcon = new JLabel(scaledIcon(image("plumber"), 26, 26));
        JLabel plumberText = new JLabel("PLUMBERS:");
        JLabel separator = new JLabel("|");
        JLabel saboteurIcon = new JLabel(scaledIcon(image("saboteur"), 26, 26));
        JLabel saboteurText = new JLabel("SABOTEURS:");
        JLabel[] labels = {plumberText, plumberScoreLabel, separator, saboteurText, saboteurScoreLabel};
        for (JLabel label : labels) {
            styleHudText(label);
        }
        plumberText.setForeground(new Color(72, 177, 255));
        saboteurText.setForeground(new Color(244, 72, 57));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 4, 0, 4);
        gbc.gridx = 0;
        panel.add(plumberIcon, gbc);
        gbc.gridx = 1;
        panel.add(plumberText, gbc);
        gbc.gridx = 2;
        panel.add(plumberScoreLabel, gbc);
        gbc.gridx = 3;
        panel.add(separator, gbc);
        gbc.gridx = 4;
        panel.add(saboteurIcon, gbc);
        gbc.gridx = 5;
        panel.add(saboteurText, gbc);
        gbc.gridx = 6;
        panel.add(saboteurScoreLabel, gbc);
        return panel;
    }

    /**
     * Creates the left status panel.
     *
     * @return status panel
     */
    private JPanel createLeftStatusPanel() {
        JPanel left = new JPanel(new BorderLayout(6, 6));
        left.setPreferredSize(new Dimension(230, GRID_ROWS * TILE_SIZE));
        left.setBackground(new Color(21, 18, 14));
        left.setBorder(retroPanelBorder(3, 10));
        JLabel title = retroLabel("STATUS / INFO");
        title.setForeground(new Color(255, 226, 77));
        statusArea.setEditable(false);
        statusArea.setBackground(new Color(14, 14, 13));
        statusArea.setForeground(new Color(239, 220, 164));
        statusArea.setFont(readableFont(13f, Font.PLAIN));
        left.add(title, BorderLayout.NORTH);
        left.add(new JScrollPane(statusArea), BorderLayout.CENTER);
        return left;
    }

    /**
     * Creates the right action toolbar.
     *
     * @return toolbar panel
     */
    private JPanel createRightToolbar() {
        JPanel toolbar = new JPanel(new GridLayout(0, 1, 5, 5));
        toolbar.setPreferredSize(new Dimension(230, GRID_ROWS * TILE_SIZE));
        toolbar.setBackground(new Color(21, 18, 14));
        toolbar.setBorder(retroPanelBorder(3, 10));

        addToolbarLabel(toolbar, "OBJECTS");
        addTool(toolbar, "Add Pipe", Tool.ADD_PIPE);
        addTool(toolbar, "Add Pump", Tool.ADD_PUMP);
        addToolbarLabel(toolbar, "PIPE TOOLS");
        addTool(toolbar, "Remove Pipe", Tool.REMOVE_PIPE);
        addTool(toolbar, "Connect Pipe", Tool.CONNECT_PIPE);
        addTool(toolbar, "Disconnect Pipe", Tool.DISCONNECT_PIPE);
        addTool(toolbar, "Rotate Pipe", Tool.ROTATE_PIPE);
        addTool(toolbar, "Repair Pipe", Tool.REPAIR_PIPE);
        addTool(toolbar, "Puncture Pipe", Tool.PUNCTURE_PIPE);
        addToolbarLabel(toolbar, "PUMP TOOLS");
        addTool(toolbar, "Remove Pump", Tool.REMOVE_PUMP);
        addTool(toolbar, "Repair Pump", Tool.REPAIR_PUMP);
        addTool(toolbar, "Break Pump", Tool.BREAK_PUMP);
        addTool(toolbar, "Change Pump Direction", Tool.CHANGE_DIRECTION);
        addToolbarLabel(toolbar, "PLAYER ACTIONS");
        addTool(toolbar, "Move", Tool.MOVE);

        JButton flow = pixelButton("Water Flow");
        flow.addActionListener(e -> {
            recalculateFlow();
            setMessage("Water flow recalculated. Scores updated.");
        });
        toolbar.add(flow);

        JButton endTurn = pixelButton("End Turn");
        endTurn.addActionListener(e -> endTurn());
        toolbar.add(endTurn);

        JButton endGame = pixelButton("End Game");
        endGame.addActionListener(e -> finishGame());
        toolbar.add(endGame);
        return toolbar;
    }

    /**
     * Adds a section label to the right toolbar.
     *
     * @param toolbar toolbar panel
     * @param title section title
     */
    private void addToolbarLabel(JPanel toolbar, String title) {
        JLabel label = retroLabel(title);
        label.setForeground(new Color(255, 226, 77));
        toolbar.add(label);
    }

    /**
     * Adds one selectable map tool button.
     *
     * @param toolbar toolbar panel
     * @param text button text
     * @param tool tool to activate
     */
    private void addTool(JPanel toolbar, String text, Tool tool) {
        JButton button = pixelButton(text);
        button.addActionListener(e -> activateTool(tool));
        toolbar.add(button);
    }

    /**
     * Starts a game from the setup screen.
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
            JOptionPane.showMessageDialog(this, "Use at least two plumbers and two saboteurs.");
            return;
        }

        List<Team> teams = new ArrayList<>();
        teams.add(plumbers);
        teams.add(saboteurs);
        network = new PipeNetwork();
        ScoreBoard scoreBoard = new ScoreBoard(plumbers, saboteurs);
        int seconds = ((Integer) durationMinutes.getValue()) * 60;
        gameSystem = new app.System(teams, network, new GameTimer(seconds), scoreBoard);
        gameSystem.startGame();
        network = gameSystem.getNetwork();

        selectedElement = null;
        selectedTile = null;
        activeTool = Tool.SELECT;
        initializeGridPositions();
        startRefreshTimer();
        setMessage("Select an object, then click a valid place on the map.");
        cards.show(root, GAME_CARD);
        refreshView();
    }

    /**
     * Places initial elements on fixed 64-pixel grid tiles.
     */
    private void initializeGridPositions() {
        elementTiles.clear();
        pipeRotations.clear();
        for (NetworkElement element : network.getElements()) {
            if (element instanceof Spring) {
                elementTiles.put(element, new Tile(1, 3));
            } else if (element instanceof Cistern) {
                elementTiles.put(element, new Tile(9, 3));
            } else if (element instanceof Pump) {
                elementTiles.put(element, new Tile(5, 3));
            } else if (element instanceof Pipe && element.getId() == 4) {
                elementTiles.put(element, new Tile(3, 3));
                pipeRotations.put((Pipe) element, 0);
            } else if (element instanceof Pipe && element.getId() == 5) {
                elementTiles.put(element, new Tile(7, 3));
                pipeRotations.put((Pipe) element, 0);
            }
        }
    }

    /**
     * Starts a small HUD refresh timer.
     */
    private void startRefreshTimer() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        refreshTimer = new Timer(1000, e -> {
            if (gameSystem != null && gameSystem.isRunning()) {
                if (gameSystem.checkEndCondition()) {
                    finishGame();
                } else {
                    refreshView();
                }
            }
        });
        refreshTimer.start();
    }

    /**
     * Selects a toolbar tool.
     *
     * @param tool selected tool
     */
    private void activateTool(Tool tool) {
        activeTool = tool;
        selectedElement = null;
        selectedTile = null;
        setMessage(instructionFor(tool));
        refreshView();
    }

    /**
     * Returns the instruction text for one tool.
     *
     * @param tool selected tool
     * @return contextual instruction
     */
    private String instructionFor(Tool tool) {
        switch (tool) {
            case ADD_PIPE:
                return "Select an object, then click an adjacent empty grid tile to add a pipe.";
            case ADD_PUMP:
                return "Click a pipe to insert a pump into it.";
            case REMOVE_PIPE:
                return "Click a pipe to remove it.";
            case CONNECT_PIPE:
                return "Click a free-end pipe, then click an adjacent element to connect it.";
            case DISCONNECT_PIPE:
                return "Click a pipe, then click one connected neighbor to disconnect it.";
            case ROTATE_PIPE:
                return "Click a pipe to rotate its sprite.";
            case REPAIR_PIPE:
                return "Click a broken/leaking pipe to repair it.";
            case PUNCTURE_PIPE:
                return "Click a pipe to puncture it.";
            case REMOVE_PUMP:
                return "Click a pump to remove it from the network.";
            case REPAIR_PUMP:
                return "Click a broken pump to repair it.";
            case BREAK_PUMP:
                return "Click a pump to trigger a breakdown.";
            case CHANGE_DIRECTION:
                return "Click a pump to rotate its input-output direction.";
            case MOVE:
                return "Click an adjacent pipe or pump to move the current player.";
            default:
                return "Select an object, then click a valid place on the map.";
        }
    }

    /**
     * Handles clicks from the map panel.
     *
     * @param tile clicked tile
     * @param element element on the tile, or null
     */
    private void handleMapClick(Tile tile, NetworkElement element) {
        selectedTile = tile;
        if (activeTool == Tool.SELECT) {
            selectedElement = element;
            setMessage(element == null ? "Empty tile selected." : "Selected " + describe(element) + ".");
            refreshView();
            return;
        }

        executeTool(tile, element);
    }

    /**
     * Executes the currently selected tool.
     *
     * @param tile clicked tile
     * @param element clicked element
     */
    private void executeTool(Tile tile, NetworkElement element) {
        switch (activeTool) {
            case ADD_PIPE:
                addPipeAt(tile, element);
                break;
            case ADD_PUMP:
                addPumpAt(element);
                break;
            case REMOVE_PIPE:
                removePipe(element);
                break;
            case CONNECT_PIPE:
                connectPipe(element);
                break;
            case DISCONNECT_PIPE:
                disconnectPipe(element);
                break;
            case ROTATE_PIPE:
                rotatePipe(element);
                break;
            case REPAIR_PIPE:
                repairPipe(element);
                break;
            case PUNCTURE_PIPE:
                puncturePipe(element);
                break;
            case REMOVE_PUMP:
                removePump(element);
                break;
            case REPAIR_PUMP:
                repairPump(element);
                break;
            case BREAK_PUMP:
                breakPump(element);
                break;
            case CHANGE_DIRECTION:
                changePumpDirection(element);
                break;
            case MOVE:
                moveCurrentPlayer(element);
                break;
            default:
                break;
        }
    }

    /**
     * Adds a pipe to an adjacent empty tile.
     *
     * @param tile target tile
     * @param element clicked element
     */
    private void addPipeAt(Tile tile, NetworkElement element) {
        if (!requireCurrentPlayer("Add Pipe", Plumber.class)) {
            return;
        }
        if (element != null) {
            if (!(element instanceof Cistern)) {
                setMessage("New pipes are manufactured at cisterns. Select the cistern first.");
                return;
            }
            selectedElement = element;
            setMessage("Cistern selected. Click an adjacent empty tile to place the new pipe.");
            return;
        }
        if (!(selectedElement instanceof Cistern) || tile == null
                || !isAdjacent(tile, elementTiles.get(selectedElement))) {
            setMessage("Invalid placement. Select the cistern and then an adjacent empty tile.");
            return;
        }

        Pipe pipe = new Pipe(network.generateId());
        network.addElement(pipe);
        network.connectElements(selectedElement, pipe);
        pipe.disconnectEnd();
        elementTiles.put(pipe, tile);
        pipeRotations.put(pipe, 0);
        selectedElement = pipe;
        consumeCurrentTurn("Pipe added. It has one free end until connected.");
    }

    /**
     * Inserts a pump into a clicked pipe.
     *
     * @param element clicked element
     */
    private void addPumpAt(NetworkElement element) {
        if (!requireCurrentPlayer("Add Pump", Plumber.class)) {
            return;
        }
        if (!(element instanceof Pipe)) {
            setMessage("Invalid placement. Click a pipe to insert a pump.");
            return;
        }
        if (!requireCurrentPosition(element, "Insert Pump")) {
            return;
        }
        Pipe oldPipe = (Pipe) element;
        Tile baseTile = elementTiles.get(oldPipe);
        Pump pump = new Pump(network.generateId());
        network.insertPump(oldPipe, pump);
        elementTiles.remove(oldPipe);
        pipeRotations.remove(oldPipe);

        elementTiles.put(pump, baseTile);
        selectedElement = pump;
        placeNewPipeSegmentsAround(pump, baseTile);
        consumeCurrentTurn("Pump added into the selected pipe.");
    }

    /**
     * Assigns tiles to the two pipe segments created by pump insertion.
     *
     * @param pump inserted pump
     * @param baseTile pump tile
     */
    private void placeNewPipeSegmentsAround(Pump pump, Tile baseTile) {
        for (Pipe pipe : pump.getConnectedPipes()) {
            if (!elementTiles.containsKey(pipe)) {
                Tile tile = firstFreeAdjacent(baseTile);
                elementTiles.put(pipe, tile);
                pipeRotations.put(pipe, 0);
            }
        }
    }

    /**
     * Removes a pipe from the network.
     *
     * @param element clicked element
     */
    private void removePipe(NetworkElement element) {
        if (!requireCurrentPlayer("Remove Pipe", Plumber.class)) {
            return;
        }
        if (!(element instanceof Pipe)) {
            setMessage("Select a pipe to remove.");
            return;
        }
        network.removeElement(element);
        elementTiles.remove(element);
        pipeRotations.remove(element);
        selectedElement = null;
        consumeCurrentTurn("Pipe removed.");
    }

    /**
     * Connects a free-end pipe to a neighboring clicked element.
     *
     * @param element clicked element
     */
    private void connectPipe(NetworkElement element) {
        if (!requireCurrentPlayer("Connect Pipe", Plumber.class)) {
            return;
        }
        if (element instanceof Pipe) {
            selectedElement = element;
            setMessage("Free-end pipe selected. Click an adjacent element to connect it.");
            return;
        }
        if (!(selectedElement instanceof Pipe) || element == null) {
            setMessage("Select a free-end pipe, then an adjacent element.");
            return;
        }

        Pipe pipe = (Pipe) selectedElement;
        if (!pipe.hasFreeEnd()) {
            setMessage("Selected pipe has no free end.");
            return;
        }
        if (!isAdjacent(elementTiles.get(pipe), elementTiles.get(element))) {
            setMessage("Invalid connection. Elements must be adjacent on the grid.");
            return;
        }

        network.connectElements(pipe, element);
        if (network.areAdjacent(pipe, element) && pipe.connectFreeEnd(element)) {
            consumeCurrentTurn("Free pipe end connected.");
        } else {
            setMessage("Connection failed.");
        }
    }

    /**
     * Disconnects a selected pipe from one neighboring element.
     *
     * @param element clicked element
     */
    private void disconnectPipe(NetworkElement element) {
        if (!requireCurrentPlayer("Disconnect Pipe", Plumber.class)) {
            return;
        }
        if (element instanceof Pipe && !(selectedElement instanceof Pipe)) {
            selectedElement = element;
            setMessage("Pipe selected. Click one connected neighbor to disconnect it.");
            return;
        }
        if (!(selectedElement instanceof Pipe) || element == null) {
            setMessage("Select a pipe, then click a connected neighbor.");
            return;
        }

        Pipe pipe = (Pipe) selectedElement;
        if (!network.areAdjacent(pipe, element)) {
            setMessage("That element is not connected to the selected pipe.");
            return;
        }
        network.disconnectElements(pipe, element);
        consumeCurrentTurn("Pipe disconnected. A free end is now leaking.");
    }

    /**
     * Rotates the visual orientation of a pipe.
     *
     * @param element clicked element
     */
    private void rotatePipe(NetworkElement element) {
        if (!(element instanceof Pipe)) {
            setMessage("Select a pipe to rotate.");
            return;
        }
        Pipe pipe = (Pipe) element;
        pipeRotations.put(pipe, (pipeRotations.getOrDefault(pipe, 0) + 1) % 4);
        setMessage("Pipe rotated.");
        refreshView();
    }

    /**
     * Repairs a clicked pipe.
     *
     * @param element clicked element
     */
    private void repairPipe(NetworkElement element) {
        if (!requireCurrentPlayer("Repair Pipe", Plumber.class)) {
            return;
        }
        if (!(element instanceof Pipe)) {
            setMessage("Select a pipe to repair.");
            return;
        }
        if (!requireCurrentPosition(element, "Repair Pipe")) {
            return;
        }
        ((Pipe) element).repair();
        consumeCurrentTurn("Pipe repaired.");
    }

    /**
     * Punctures a clicked pipe for saboteur demonstration.
     *
     * @param element clicked element
     */
    private void puncturePipe(NetworkElement element) {
        if (!requireCurrentPlayer("Puncture Pipe", Saboteur.class)) {
            return;
        }
        if (!(element instanceof Pipe)) {
            setMessage("Select a pipe to puncture.");
            return;
        }
        if (!requireCurrentPosition(element, "Puncture Pipe")) {
            return;
        }
        ((Pipe) element).puncture();
        consumeCurrentTurn("Pipe punctured. Leakage is visible and counted.");
    }

    /**
     * Removes a pump from the network.
     *
     * @param element clicked element
     */
    private void removePump(NetworkElement element) {
        if (!requireCurrentPlayer("Remove Pump", Plumber.class)) {
            return;
        }
        if (!(element instanceof Pump)) {
            setMessage("Select a pump to remove.");
            return;
        }
        network.removeElement(element);
        elementTiles.remove(element);
        selectedElement = null;
        consumeCurrentTurn("Pump removed. Adjacent pipes now have free ends.");
    }

    /**
     * Repairs a clicked pump.
     *
     * @param element clicked element
     */
    private void repairPump(NetworkElement element) {
        if (!requireCurrentPlayer("Repair Pump", Plumber.class)) {
            return;
        }
        if (!(element instanceof Pump)) {
            setMessage("Select a pump to repair.");
            return;
        }
        if (!requireCurrentPosition(element, "Repair Pump")) {
            return;
        }
        ((Pump) element).repair();
        consumeCurrentTurn("Pump repaired.");
    }

    /**
     * Breaks a clicked pump.
     *
     * @param element clicked element
     */
    private void breakPump(NetworkElement element) {
        if (!(element instanceof Pump)) {
            setMessage("Select a pump to break.");
            return;
        }
        ((Pump) element).breakDown();
        recalculateFlow();
        setMessage("Pump broken. Water cannot pass through it.");
    }

    /**
     * Changes a pump direction by cycling through connected pipes.
     *
     * @param element clicked element
     */
    private void changePumpDirection(NetworkElement element) {
        if (!requireCurrentPlayer("Change Pump Direction", Plumber.class, Saboteur.class)) {
            return;
        }
        if (!(element instanceof Pump)) {
            setMessage("Select a pump to change direction.");
            return;
        }
        if (!requireCurrentPosition(element, "Change Pump Direction")) {
            return;
        }
        Pump pump = (Pump) element;
        List<Pipe> connected = pump.getConnectedPipes();
        if (connected.size() < 2) {
            setMessage("Pump needs two connected pipes.");
            return;
        }
        Pipe input = pump.getActiveInput() == connected.get(0) ? connected.get(1) : connected.get(0);
        Pipe output = input == connected.get(0) ? connected.get(1) : connected.get(0);
        pump.setDirection(input, output);
        consumeCurrentTurn("Pump direction changed.");
    }

    /**
     * Moves the active player to a clicked adjacent element.
     *
     * @param element clicked element
     */
    private void moveCurrentPlayer(NetworkElement element) {
        Player player = gameSystem.getCurrentPlayer();
        if (player == null || element == null) {
            setMessage("Select a valid pipe or pump to move.");
            return;
        }
        if (player.moveTo(element)) {
            completeTurn("Player moved.");
        } else {
            setMessage("Invalid move. Players move only to adjacent pipes or pumps.");
        }
    }

    /**
     * Ends the active player's turn.
     */
    private void endTurn() {
        Player player = gameSystem.getCurrentPlayer();
        if (player != null) {
            player.endTurn();
            completeTurn("Turn ended.");
        }
    }

    /**
     * Validates the current player role and turn availability.
     *
     * @param action action name to show in messages
     * @param allowedRoles accepted player classes
     * @return true when the current player can perform the action
     */
    private boolean requireCurrentPlayer(String action, Class<?>... allowedRoles) {
        Player player = gameSystem.getCurrentPlayer();
        if (player == null) {
            setMessage("No active player.");
            return false;
        }
        if (!player.canAct()) {
            setMessage(player.getName() + " has already acted. Use End Turn.");
            return false;
        }
        for (Class<?> role : allowedRoles) {
            if (role.isInstance(player)) {
                return true;
            }
        }
        setMessage(action + " is not allowed for " + player.getClass().getSimpleName() + ".");
        return false;
    }

    /**
     * Checks that the current player stands on the clicked target.
     *
     * @param element clicked target
     * @param action action name
     * @return true if the player is positioned on the target
     */
    private boolean requireCurrentPosition(NetworkElement element, String action) {
        Player player = gameSystem.getCurrentPlayer();
        if (player == null || player.getPosition() != element) {
            setMessage(action + " requires the current player to stand on the target.");
            return false;
        }
        return true;
    }

    /**
     * Marks the active player as done and advances to the next turn.
     *
     * @param message message to show after advancing
     */
    private void consumeCurrentTurn(String message) {
        Player player = gameSystem.getCurrentPlayer();
        if (player != null) {
            player.endTurn();
        }
        completeTurn(message);
    }

    /**
     * Completes a turn and refreshes flow and HUD state.
     *
     * @param message message to display
     */
    private void completeTurn(String message) {
        recalculateFlow();
        if (gameSystem.checkEndCondition()) {
            finishGame();
            return;
        }
        gameSystem.nextTurn();
        setMessage(message);
        refreshView();
    }

    /**
     * Recalculates flow after state changes.
     */
    private void recalculateFlow() {
        if (gameSystem != null && gameSystem.getWaterFlowManager() != null) {
            gameSystem.getWaterFlowManager().recalculateFlow();
        }
        refreshView();
    }

    /**
     * Ends the game and shows final scores.
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
        String result = winner == null ? "Game ended in a draw." : winner.getName() + " win!";
        setMessage(result);
        showEndScreen(result, scoreBoard);
        refreshView();
    }

    /**
     * Shows a custom final-score dialog styled like the end screen reference.
     *
     * @param result final winner text
     * @param scoreBoard scoreboard to display
     */
    private void showEndScreen(String result, ScoreBoard scoreBoard) {
        JPanel overlay = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics graphics) {
                Graphics2D g = (Graphics2D) graphics;
                g.setColor(new Color(0, 0, 0, 135));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        overlay.setOpaque(false);

        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(620, 390));
        card.setBackground(new Color(13, 15, 18, 244));
        card.setBorder(retroPanelBorder(4, 22));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.insets = new Insets(4, 0, 8, 0);
        card.add(titleLabel(result.toUpperCase(), 30), gbc);

        gbc.gridy = 1;
        card.add(subtitleLabel("FINAL SCORES"), gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(12, 0, 4, 0);
        card.add(scoreLine("PLUMBERS:", scoreBoard.getPlumberScore(),
                new Color(66, 176, 255), image("plumber")), gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(4, 0, 16, 0);
        card.add(scoreLine("SABOTEURS:", scoreBoard.getSaboteurScore(),
                new Color(239, 62, 47), image("saboteur")), gbc);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 16, 0));
        buttons.setOpaque(false);
        JButton restart = menuButton("RESTART");
        JButton exit = menuButton("EXIT");
        restart.setPreferredSize(new Dimension(170, 48));
        exit.setPreferredSize(new Dimension(170, 48));
        restart.addActionListener(e -> {
            getGlassPane().setVisible(false);
            cards.show(root, SETUP_CARD);
        });
        exit.addActionListener(e -> {
            getGlassPane().setVisible(false);
            dispose();
        });
        buttons.add(restart);
        buttons.add(exit);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(buttons, gbc);

        overlay.add(card, new GridBagConstraints());
        setGlassPane(overlay);
        overlay.setVisible(true);
    }

    /**
     * Builds one final-score row.
     *
     * @param label row label
     * @param score score value
     * @param color label color
     * @return score row panel
     */
    private JPanel scoreLine(String label, int score, Color color, BufferedImage icon) {
        JPanel row = new JPanel(new GridBagLayout());
        row.setOpaque(false);
        JLabel name = subtitleLabel(label);
        JLabel value = subtitleLabel(String.valueOf(score));
        JLabel sprite = new JLabel(scaledIcon(icon, 38, 38));
        name.setForeground(color);
        value.setForeground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(0, 0, 0, 12);
        row.add(sprite, gbc);
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 0, 24);
        row.add(name, gbc);
        gbc.gridx = 2;
        gbc.insets = new Insets(0, 0, 0, 0);
        row.add(value, gbc);
        return row;
    }

    /**
     * Refreshes all UI fields from the model.
     */
    private void refreshView() {
        if (gameSystem == null) {
            return;
        }
        Player current = gameSystem.getCurrentPlayer();
        turnLabel.setText(current == null ? "TURN: -" : "TURN: " + current.getName()
                + " (" + current.getClass().getSimpleName() + ")");
        turnLabel.setIcon(current == null ? null : scaledIcon(playerSprite(current), 28, 28));
        timerLabel.setText("TIMER: " + gameSystem.getTimer().getRemainingTime() + "s");
        ScoreBoard scoreBoard = gameSystem.getScoreBoard();
        scoreLabel.setText("PLUMBERS: " + scoreBoard.getPlumberScore()
                + " | SABOTEURS: " + scoreBoard.getSaboteurScore());
        plumberScoreLabel.setText(String.valueOf(scoreBoard.getPlumberScore()));
        saboteurScoreLabel.setText(String.valueOf(scoreBoard.getSaboteurScore()));
        statusArea.setText(buildStatusText());
        mapPanel.repaint();
    }

    /**
     * Builds status text for the left panel.
     *
     * @return status text
     */
    private String buildStatusText() {
        StringBuilder builder = new StringBuilder();
        Player current = gameSystem.getCurrentPlayer();
        builder.append("Selected: ").append(describe(selectedElement)).append('\n');
        builder.append("Tile: ").append(selectedTile == null ? "-" : selectedTile).append('\n');
        builder.append("Tool: ").append(activeTool.label).append("\n\n");
        if (current != null) {
            builder.append("Current player:\n");
            builder.append(current.getName()).append('\n');
            builder.append(describe(current.getPosition())).append("\n\n");
        }
        builder.append("Pipes:\n");
        for (Pipe pipe : network.getPipes()) {
            builder.append(describe(pipe));
            if (pipe.isPunctured()) {
                builder.append(" LEAK");
            }
            if (pipe.hasFreeEnd()) {
                builder.append(" FREE");
            }
            builder.append('\n');
        }
        builder.append("\nPumps:\n");
        for (Pump pump : network.getPumps()) {
            builder.append(describe(pump)).append(pump.isBroken() ? " BROKEN" : " OK").append('\n');
        }
        return builder.toString();
    }

    /**
     * Updates the bottom message bar.
     *
     * @param message message text
     */
    private void setMessage(String message) {
        messageBar.setText(message);
    }

    /**
     * Shows a concise instructions dialog.
     */
    private void showInstructions() {
        cards.show(root, INSTRUCTIONS_CARD);
    }

    /**
     * Returns player-facing game rules for the instructions screen.
     *
     * @return formatted instruction text
     */
    private String instructionText() {
        return "OBJECTIVE\n"
                + "Plumbers score by delivering water from the spring to the cistern. "
                + "Saboteurs score when water leaks into the desert.\n\n"
                + "TURN FLOW\n"
                + "Players act one at a time. The top-left HUD shows whose turn it is. "
                + "Move, repair, puncture, connect, disconnect, add, remove, and direction actions "
                + "consume the current turn. End Turn skips the action.\n\n"
                + "MOVEMENT\n"
                + "Players can stand only on pipes and pumps. Multiple players can share a pump. "
                + "Only one player can stand on a pipe.\n\n"
                + "PLUMBER ACTIONS\n"
                + "Plumbers repair leaking pipes, repair broken pumps, add pipes, insert pumps, "
                + "connect pipe ends, disconnect pipes, and remove pipe-system elements.\n\n"
                + "SABOTEUR ACTIONS\n"
                + "Saboteurs puncture pipes and change pump direction. A punctured pipe visibly leaks "
                + "and sends water to the saboteur score.\n\n"
                + "MAP CONTROLS\n"
                + "Select an action on the right toolbar, then click a valid map element or adjacent "
                + "64x64 grid tile. The bottom bar explains the next required click.";
    }

    /**
     * Checks if two tiles are orthogonally adjacent.
     *
     * @param a first tile
     * @param b second tile
     * @return true if adjacent
     */
    private boolean isAdjacent(Tile a, Tile b) {
        return a != null && b != null
                && Math.abs(a.col - b.col) + Math.abs(a.row - b.row) == 1;
    }

    /**
     * Finds an empty neighboring tile.
     *
     * @param origin origin tile
     * @return free tile near origin
     */
    private Tile firstFreeAdjacent(Tile origin) {
        int[][] offsets = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        for (int[] offset : offsets) {
            Tile tile = new Tile(origin.col + offset[0], origin.row + offset[1]);
            if (tile.isInside() && elementAt(tile) == null) {
                return tile;
            }
        }
        return new Tile(Math.min(GRID_COLS - 1, origin.col + 1), origin.row);
    }

    /**
     * Returns the element located on a tile.
     *
     * @param tile tile to inspect
     * @return element or null
     */
    private NetworkElement elementAt(Tile tile) {
        for (Map.Entry<NetworkElement, Tile> entry : elementTiles.entrySet()) {
            if (entry.getValue().equals(tile)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * Returns all players in turn order.
     *
     * @return player list
     */
    private List<Player> allPlayers() {
        List<Player> players = new ArrayList<>();
        if (gameSystem != null) {
            for (Team team : gameSystem.getTeams()) {
                players.addAll(team.getMembers());
            }
        }
        return players;
    }

    /**
     * Describes a network element.
     *
     * @param element element to describe
     * @return readable identifier
     */
    private String describe(NetworkElement element) {
        if (element == null) {
            return "-";
        }
        return element.getClass().getSimpleName() + "#" + element.getId();
    }

    /**
     * Creates a retro-style button.
     *
     * @param text button text
     * @return styled button
     */
    private JButton pixelButton(String text) {
        JButton button = new GameButton(text);
        button.setFocusPainted(false);
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(96, 58, 28));
        button.setBorder(BorderFactory.createEmptyBorder(7, 12, 9, 12));
        button.setFont(uiFont(10f, Font.BOLD));
        return button;
    }

    /**
     * Creates a large button for menu, setup, and end screens.
     *
     * @param text button text
     * @return styled large button
     */
    private JButton menuButton(String text) {
        JButton button = pixelButton(text);
        button.setPreferredSize(new Dimension(320, 54));
        button.setFont(uiFont(14f, Font.BOLD));
        button.setBackground(new Color(112, 74, 36));
        return button;
    }

    /**
     * Creates a retro text label.
     *
     * @param text label text
     * @return styled label
     */
    private JLabel retroLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(35, 24, 13));
        label.setFont(uiFont(10f, Font.BOLD));
        return label;
    }

    /**
     * Creates a large readable title label.
     *
     * @param text title text
     * @param size font size
     * @return title label
     */
    private JLabel titleLabel(String text, int size) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(255, 207, 65));
        label.setFont(uiFont(size, Font.BOLD));
        label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
        return label;
    }

    /**
     * Creates the white secondary heading used on menu screens.
     *
     * @param text subtitle text
     * @return subtitle label
     */
    private JLabel subtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(244, 238, 211));
        label.setFont(uiFont(18f, Font.BOLD));
        return label;
    }

    /**
     * Applies readable retro styling to setup inputs.
     *
     * @param component input component
     */
    private void styleInput(javax.swing.JComponent component) {
        component.setForeground(new Color(28, 21, 14));
        component.setBackground(new Color(246, 225, 178));
        component.setFont(uiFont(10f, Font.BOLD));
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(56, 35, 18), 2),
                BorderFactory.createEmptyBorder(4, 6, 4, 6)));
    }

    /**
     * Creates a consistent dark/brown border for framed UI panels.
     *
     * @param lineWidth outside line width
     * @param padding inner padding
     * @return compound border
     */
    private javax.swing.border.Border retroPanelBorder(int lineWidth, int padding) {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(34, 21, 10), lineWidth),
                BorderFactory.createEmptyBorder(padding, padding, padding, padding));
    }

    /**
     * Styles an opaque HUD label.
     *
     * @param label label to style
     */
    private void styleHudLabel(JLabel label) {
        label.setOpaque(true);
        label.setBackground(new Color(24, 19, 14));
        label.setForeground(new Color(255, 226, 77));
        label.setBorder(retroHudBorder());
        label.setFont(uiFont(10f, Font.BOLD));
        label.setHorizontalAlignment(JLabel.LEFT);
    }

    /**
     * Styles a label inside a HUD panel.
     *
     * @param label label to style
     */
    private void styleHudText(JLabel label) {
        label.setForeground(new Color(255, 226, 77));
        label.setFont(uiFont(10f, Font.BOLD));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setVerticalAlignment(JLabel.CENTER);
    }

    /**
     * Creates the standard HUD border.
     *
     * @return HUD border
     */
    private javax.swing.border.Border retroHudBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(69, 42, 20), 3),
                BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    /**
     * Derives a UI font from the bundled pixel font.
     *
     * @param size font size
     * @param style font style
     * @return derived UI font
     */
    private Font uiFont(float size, int style) {
        return pixelFont.deriveFont(style, size);
    }

    /**
     * Returns a readable font for longer text blocks.
     *
     * @param size font size
     * @param style font style
     * @return standard readable font
     */
    private Font readableFont(float size, int style) {
        return new Font(Font.SANS_SERIF, style, Math.round(size));
    }

    /**
     * Returns the clean desert background used behind menu-like screens.
     *
     * @return screen background image
     */
    private BufferedImage screenBackground() {
        return image("gameplayscreen_new", "gameplayscreen", "mainmenu");
    }

    /**
     * Loads the bundled pixel font and falls back to a monospaced font.
     *
     * @return game UI font
     */
    private static Font loadPixelFont() {
        for (Path folder : fontFolders()) {
            File file = folder.resolve("PressStart2P-Regular.ttf").toFile();
            if (!file.isFile()) {
                continue;
            }
            try {
                Font loaded = Font.createFont(Font.TRUETYPE_FONT, file);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(loaded);
                return loaded;
            } catch (FontFormatException | IOException ignored) {
                // The fallback below keeps the game runnable on lab machines.
            }
        }
        return new Font(Font.MONOSPACED, Font.BOLD, 12);
    }

    /**
     * Returns possible font folders for repo and release execution.
     *
     * @return candidate folders
     */
    private static List<Path> fontFolders() {
        List<Path> folders = new ArrayList<>();
        folders.add(Paths.get("assets", "fonts"));
        folders.add(Paths.get("..", "assets", "fonts"));
        folders.add(Paths.get("..", "..", "assets", "fonts"));
        return folders;
    }

    /**
     * Creates a scaled icon with nearest-neighbor pixel rendering.
     *
     * @param source source image
     * @param width target width
     * @param height target height
     * @return scaled icon, or null if the image is missing
     */
    private static ImageIcon scaledIcon(BufferedImage source, int width, int height) {
        if (source == null) {
            return null;
        }
        BufferedImage scaled = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        drawPixelImage(g, source, 0, 0, width, height);
        g.dispose();
        return new ImageIcon(scaled);
    }

    /**
     * Returns the sprite matching a player's role.
     *
     * @param player player to inspect
     * @return plumber or saboteur sprite
     */
    private BufferedImage playerSprite(Player player) {
        return player instanceof Plumber ? image("plumber") : image("saboteur");
    }

    /**
     * Creates one compact icon showing both scoring teams.
     *
     * @return combined score icon
     */
    private ImageIcon combinedScoreIcon() {
        BufferedImage icon = new BufferedImage(58, 28, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        drawPixelImage(g, image("plumber"), 0, 0, 26, 28);
        drawPixelImage(g, image("saboteur"), 30, 0, 26, 28);
        g.dispose();
        return new ImageIcon(icon);
    }

    /**
     * Creates a small clock icon for the timer HUD.
     *
     * @param width icon width
     * @param height icon height
     * @return generated clock icon
     */
    private ImageIcon clockIcon(int width, int height) {
        BufferedImage icon = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = icon.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setColor(new Color(255, 226, 77));
        g.fillOval(3, 3, width - 6, height - 6);
        g.setColor(new Color(24, 19, 14));
        g.drawOval(3, 3, width - 7, height - 7);
        g.drawLine(width / 2, height / 2, width / 2, 7);
        g.drawLine(width / 2, height / 2, width - 8, height / 2);
        g.dispose();
        return new ImageIcon(icon);
    }

    /**
     * Draws a sprite with crisp pixel-art scaling.
     *
     * @param g graphics context
     * @param image image to draw
     * @param x x coordinate
     * @param y y coordinate
     * @param width draw width
     * @param height draw height
     */
    private static void drawPixelImage(Graphics2D g, BufferedImage image,
            int x, int y, int width, int height) {
        if (image == null) {
            return;
        }
        Object oldInterpolation = g.getRenderingHint(RenderingHints.KEY_INTERPOLATION);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(image, x, y, width, height, null);
        if (oldInterpolation != null) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, oldInterpolation);
        }
    }

    /**
     * Returns the first available image among the requested keys.
     *
     * @param keys image keys
     * @return image or null
     */
    private BufferedImage image(String... keys) {
        for (String key : keys) {
            BufferedImage found = images.get(key.toLowerCase());
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Loads image assets from the project asset folder.
     *
     * @return image map keyed by lowercase file stem
     */
    private static Map<String, BufferedImage> loadImages() {
        Map<String, BufferedImage> loaded = new HashMap<>();
        for (Path folder : assetFolders()) {
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
                    // A missing optional image falls back to simple painted geometry.
                }
            }
            if (!loaded.isEmpty()) {
                return loaded;
            }
        }
        return loaded;
    }

    /**
     * Returns possible asset folders for repo and release execution.
     *
     * @return candidate folders
     */
    private static List<Path> assetFolders() {
        List<Path> folders = new ArrayList<>();
        folders.add(Paths.get("assets", "images"));
        folders.add(Paths.get("..", "assets", "images"));
        folders.add(Paths.get("..", "..", "assets", "images"));
        return folders;
    }

    /**
     * Draws the grid-based map and all sprites.
     */
    private final class GameMapPanel extends JPanel {

        /**
         * Creates the custom map renderer.
         */
        private GameMapPanel() {
            setPreferredSize(new Dimension(GRID_COLS * TILE_SIZE, GRID_ROWS * TILE_SIZE));
            setMinimumSize(new Dimension(GRID_COLS * TILE_SIZE, GRID_ROWS * TILE_SIZE));
            setBorder(BorderFactory.createLineBorder(new Color(32, 24, 16), 4));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    Tile tile = new Tile(event.getX() / TILE_SIZE, event.getY() / TILE_SIZE);
                    if (tile.isInside()) {
                        handleMapClick(tile, elementAt(tile));
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            drawDesert(g);
            drawGrid(g);
            drawConnections(g);
            drawWaterFlow(g);
            drawElements(g);
            drawPlayers(g);
        }

        /**
         * Draws the desert background.
         *
         * @param g graphics context
         */
        private void drawDesert(Graphics2D g) {
            BufferedImage background = image("gameplayscreen_new", "gameplayscreen");
            if (background != null) {
                drawPixelImage(g, background, 0, 0, getWidth(), getHeight());
                return;
            }
            g.setColor(new Color(211, 132, 31));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(180, 104, 22));
            for (int y = 0; y < getHeight(); y += 26) {
                g.drawLine(0, y, getWidth(), y + 40);
            }
        }

        /**
         * Draws the fixed 64-pixel grid.
         *
         * @param g graphics context
         */
        private void drawGrid(Graphics2D g) {
            g.setColor(new Color(255, 238, 173, 35));
            for (int x = 0; x <= getWidth(); x += TILE_SIZE) {
                g.drawLine(x, 0, x, getHeight());
            }
            for (int y = 0; y <= getHeight(); y += TILE_SIZE) {
                g.drawLine(0, y, getWidth(), y);
            }
        }

        /**
         * Draws connection guides between adjacent elements.
         *
         * @param g graphics context
         */
        private void drawConnections(Graphics2D g) {
            if (network == null) {
                return;
            }
            g.setStroke(new BasicStroke(7f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(70, 54, 38, 170));
            for (NetworkElement element : network.getElements()) {
                Tile a = elementTiles.get(element);
                if (a == null) {
                    continue;
                }
                for (NetworkElement neighbor : element.getNeighbors()) {
                    Tile b = elementTiles.get(neighbor);
                    if (b != null && element.getId() < neighbor.getId()) {
                        Point pa = a.center();
                        Point pb = b.center();
                        g.drawLine(pa.x, pa.y, pb.x, pb.y);
                    }
                }
            }
            g.setStroke(new BasicStroke(1f));
        }

        /**
         * Draws blue route hints for the active water-flow path.
         *
         * @param g graphics context
         */
        private void drawWaterFlow(Graphics2D g) {
            if (network == null) {
                return;
            }
            g.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.setColor(new Color(72, 185, 255, 210));
            for (Spring spring : network.getSprings()) {
                Pipe output = spring.getOutputPipe();
                if (output != null && isFlowingPipe(output)) {
                    drawFlowSegment(g, spring, output);
                }
            }
            for (Pump pump : network.getPumps()) {
                if (pump.isBroken()) {
                    continue;
                }
                Pipe input = pump.getActiveInput();
                Pipe output = pump.getActiveOutput();
                if (isFlowingPipe(input)) {
                    drawFlowSegment(g, input, pump);
                }
                if (isFlowingPipe(output)) {
                    drawFlowSegment(g, pump, output);
                    for (NetworkElement neighbor : output.getNeighbors()) {
                        if (neighbor instanceof Cistern) {
                            drawFlowSegment(g, output, neighbor);
                        }
                    }
                }
            }
            g.setStroke(new BasicStroke(1f));
        }

        /**
         * Checks if a pipe can carry visible flow.
         *
         * @param pipe pipe to inspect
         * @return true if the pipe is visually flowing
         */
        private boolean isFlowingPipe(Pipe pipe) {
            return pipe != null && !pipe.isPunctured() && !pipe.hasFreeEnd();
        }

        /**
         * Draws one blue flow segment between two positioned elements.
         *
         * @param g graphics context
         * @param from first element
         * @param to second element
         */
        private void drawFlowSegment(Graphics2D g, NetworkElement from, NetworkElement to) {
            Tile a = elementTiles.get(from);
            Tile b = elementTiles.get(to);
            if (a == null || b == null) {
                return;
            }
            Point pa = a.center();
            Point pb = b.center();
            g.drawLine(pa.x, pa.y, pb.x, pb.y);
        }

        /**
         * Draws springs, cisterns, pumps, and pipes.
         *
         * @param g graphics context
         */
        private void drawElements(Graphics2D g) {
            for (Map.Entry<NetworkElement, Tile> entry : elementTiles.entrySet()) {
                NetworkElement element = entry.getKey();
                Tile tile = entry.getValue();
                BufferedImage sprite = spriteFor(element);
                Dimension size = spriteSize(element);
                Point center = tile.center();
                if (sprite != null) {
                    drawPixelImage(g, sprite, center.x - size.width / 2,
                            center.y - size.height / 2, size.width, size.height);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillOval(center.x - 20, center.y - 20, 40, 40);
                }
                if (element == selectedElement) {
                    g.setColor(Color.YELLOW);
                    g.setStroke(new BasicStroke(3f));
                    g.drawRect(tile.col * TILE_SIZE + 3, tile.row * TILE_SIZE + 3, TILE_SIZE - 6, TILE_SIZE - 6);
                    g.setStroke(new BasicStroke(1f));
                }
            }
        }

        /**
         * Draws players on top of their current pipe or pump tile.
         *
         * @param g graphics context
         */
        private void drawPlayers(Graphics2D g) {
            Map<NetworkElement, Integer> offsets = new HashMap<>();
            for (Player player : allPlayers()) {
                NetworkElement position = player.getPosition();
                Tile tile = elementTiles.get(position);
                if (tile == null) {
                    continue;
                }
                int offset = offsets.getOrDefault(position, 0);
                offsets.put(position, offset + 1);
                BufferedImage sprite = player instanceof Plumber ? image("plumber") : image("saboteur");
                Point center = tile.center();
                int x = center.x - 22 + offset * 18;
                int y = center.y - 52;
                if (sprite != null) {
                    drawPixelImage(g, sprite, x, y, 42, 46);
                } else {
                    g.setColor(player instanceof Plumber ? Color.BLUE : Color.DARK_GRAY);
                    g.fillOval(x, y, 24, 24);
                }
                drawPlayerName(g, player.getName(), x + 21, y - 4);
            }
        }

        /**
         * Draws a small readable name tag above a player.
         *
         * @param g graphics context
         * @param name player name
         * @param centerX horizontal center
         * @param y text baseline area
         */
        private void drawPlayerName(Graphics2D g, String name, int centerX, int y) {
            String text = name == null ? "" : name;
            g.setFont(readableFont(8f, Font.BOLD));
            int width = g.getFontMetrics().stringWidth(text) + 6;
            int height = 12;
            g.setColor(new Color(0, 0, 0, 170));
            g.fillRoundRect(centerX - width / 2, y - height, width, height, 5, 5);
            g.setColor(Color.WHITE);
            g.drawString(text, centerX - width / 2 + 3, y - 3);
        }

        /**
         * Returns the sprite for a network element.
         *
         * @param element element to draw
         * @return sprite image
         */
        private BufferedImage spriteFor(NetworkElement element) {
            if (element instanceof Spring) {
                return image("spring");
            }
            if (element instanceof Cistern) {
                return image("cistern");
            }
            if (element instanceof Pump) {
                return ((Pump) element).isBroken() ? image("brokenpump", "pump") : image("pump");
            }
            if (element instanceof Pipe) {
                Pipe pipe = (Pipe) element;
                if (pipe.isPunctured() || pipe.hasFreeEnd()) {
                    return image("brokenpaper", "fixedpipe");
                }
                int rotation = pipeRotations.getOrDefault(pipe, 0);
                switch (rotation) {
                    case 1:
                        return image("pipe1");
                    case 2:
                        return image("pipe2");
                    case 3:
                        return image("pipe3");
                    default:
                        return image("pipe4", "fixedpipe");
                }
            }
            return null;
        }

        /**
         * Returns draw size for each sprite type.
         *
         * @param element element to size
         * @return sprite dimension
         */
        private Dimension spriteSize(NetworkElement element) {
            if (element instanceof Spring) {
                return new Dimension(64, 54);
            }
            if (element instanceof Cistern) {
                return new Dimension(60, 74);
            }
            if (element instanceof Pump) {
                return new Dimension(76, 60);
            }
            if (element instanceof Pipe) {
                return new Dimension(78, 48);
            }
            return new Dimension(52, 52);
        }
    }

    /**
     * Panel that paints a scaled background image.
     */
    private static final class BackgroundPanel extends JPanel {

        /** Optional background image. */
        private final BufferedImage background;

        /**
         * Creates a background panel.
         *
         * @param background background image
         */
        private BackgroundPanel(BufferedImage background) {
            this.background = background;
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics;
            if (background != null) {
                drawPixelImage(g, background, 0, 0, getWidth(), getHeight());
            } else {
                g.setColor(new Color(211, 132, 31));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    /**
     * Button with rounded corners, shadow, and pressed offset animation.
     */
    private static final class GameButton extends JButton {

        /** Rounded shape used for hit-testing. */
        private Shape shape;

        /**
         * Creates a styled game button.
         *
         * @param text button text
         */
        private GameButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setRolloverEnabled(true);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            int offset = getModel().isPressed() ? 2 : 0;
            int arc = 12;
            int width = getWidth() - 4;
            int height = getHeight() - 6;
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(new Color(0, 0, 0, 110));
            g.fillRoundRect(4, 5, width, height, arc, arc);
            Color base = getBackground();
            if (getModel().isRollover()) {
                base = base.brighter();
            }
            g.setColor(base);
            g.fillRoundRect(1, 1 + offset, width, height, arc, arc);
            g.setColor(new Color(26, 16, 8));
            g.setStroke(new BasicStroke(3f));
            g.drawRoundRect(1, 1 + offset, width, height, arc, arc);
            g.dispose();
            super.paintComponent(graphics);
        }

        @Override
        public boolean contains(int x, int y) {
            if (shape == null || !shape.getBounds().equals(getBounds())) {
                shape = new java.awt.geom.RoundRectangle2D.Float(1, 1,
                        getWidth() - 4, getHeight() - 6, 12, 12);
            }
            return shape.contains(x, y);
        }
    }

    /**
     * One 64-pixel map tile.
     */
    private static final class Tile {

        /** Tile column. */
        private final int col;

        /** Tile row. */
        private final int row;

        /**
         * Creates a tile coordinate.
         *
         * @param col column index
         * @param row row index
         */
        private Tile(int col, int row) {
            this.col = col;
            this.row = row;
        }

        /**
         * Returns the tile center in pixels.
         *
         * @return center point
         */
        private Point center() {
            return new Point(col * TILE_SIZE + TILE_SIZE / 2, row * TILE_SIZE + TILE_SIZE / 2);
        }

        /**
         * Checks whether the tile is inside the board.
         *
         * @return true if valid
         */
        private boolean isInside() {
            return col >= 0 && col < GRID_COLS && row >= 0 && row < GRID_ROWS;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Tile)) {
                return false;
            }
            Tile other = (Tile) object;
            return col == other.col && row == other.row;
        }

        @Override
        public int hashCode() {
            return col * 31 + row;
        }

        @Override
        public String toString() {
            return "(" + col + "," + row + ")";
        }
    }

    /**
     * Toolbar action identifiers.
     */
    private enum Tool {
        /** Select-only mode. */
        SELECT("Select"),
        /** Adds a pipe to the grid. */
        ADD_PIPE("Add Pipe"),
        /** Inserts a pump into a pipe. */
        ADD_PUMP("Add Pump"),
        /** Removes a pipe. */
        REMOVE_PIPE("Remove Pipe"),
        /** Connects a free pipe end. */
        CONNECT_PIPE("Connect Pipe"),
        /** Disconnects one pipe end. */
        DISCONNECT_PIPE("Disconnect Pipe"),
        /** Rotates pipe sprite orientation. */
        ROTATE_PIPE("Rotate Pipe"),
        /** Repairs a pipe. */
        REPAIR_PIPE("Repair Pipe"),
        /** Punctures a pipe. */
        PUNCTURE_PIPE("Puncture Pipe"),
        /** Removes a pump. */
        REMOVE_PUMP("Remove Pump"),
        /** Repairs a pump. */
        REPAIR_PUMP("Repair Pump"),
        /** Breaks a pump. */
        BREAK_PUMP("Break Pump"),
        /** Changes pump direction. */
        CHANGE_DIRECTION("Change Pump Direction"),
        /** Moves the active player. */
        MOVE("Move");

        /** Human-readable label. */
        private final String label;

        /**
         * Creates a tool enum value.
         *
         * @param label display label
         */
        Tool(String label) {
            this.label = label;
        }
    }
}
