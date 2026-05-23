package app;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

/**
 * Swing entry point for the GUI phase of Pipes in the Desert.
 */
public class GuiMain {

    /**
     * Starts the graphical application.
     *
     * @param args ignored
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Default Swing look and feel is acceptable if the system one is unavailable.
            }

            JFrame frame = new JFrame("Pipes in the Desert");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setMinimumSize(new Dimension(980, 660));
            frame.setContentPane(new ViewManager());
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    /**
     * Switches between the GUI screens described in the GUI plan.
     */
    private static class ViewManager extends JPanel {

        private static final String MAIN = "main";
        private static final String SETUP = "setup";
        private static final String INSTRUCTIONS = "instructions";
        private static final String GAME = "game";
        private static final String END = "end";

        private final CardLayout cards;
        private final MainMenuView mainMenu;
        private final GameSetupView setup;
        private final InstructionsView instructions;
        private final GameView game;
        private final EndGameView endGame;

        ViewManager() {
            cards = new CardLayout();
            setLayout(cards);
            mainMenu = new MainMenuView(this);
            setup = new GameSetupView(this);
            instructions = new InstructionsView(this);
            game = new GameView(this);
            endGame = new EndGameView(this);
            add(mainMenu, MAIN);
            add(setup, SETUP);
            add(instructions, INSTRUCTIONS);
            add(game, GAME);
            add(endGame, END);
            showMainMenu();
        }

        void showMainMenu() {
            cards.show(this, MAIN);
        }

        void showGameSetup() {
            setup.reset();
            cards.show(this, SETUP);
        }

        void showInstructions() {
            cards.show(this, INSTRUCTIONS);
        }

        void startGame(GameSetup setupData) {
            game.start(setupData);
            cards.show(this, GAME);
        }

        void showEndGame(System system) {
            endGame.displayResult(system);
            cards.show(this, END);
        }
    }

    /**
     * Shared desert background panel. The background is only scenery; menus and
     * controls are real Swing components.
     */
    private static class DesertPanel extends JPanel {

        private final BufferedImage background;

        DesertPanel() {
            background = ImageAssets.load("gameplayscreen.png");
            setPreferredSize(new Dimension(1120, 720));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            if (background != null) {
                drawCoverImage(g2, background, getWidth(), getHeight());
            } else {
                g2.setColor(new Color(211, 134, 28));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
            g2.dispose();
        }

        private void drawCoverImage(Graphics2D g2, Image image, int width, int height) {
            int imageWidth = image.getWidth(null);
            int imageHeight = image.getHeight(null);
            double scale = Math.max(width / (double) imageWidth, height / (double) imageHeight);
            int drawWidth = (int) Math.round(imageWidth * scale);
            int drawHeight = (int) Math.round(imageHeight * scale);
            int x = (width - drawWidth) / 2;
            int y = (height - drawHeight) / 2;
            g2.drawImage(image, x, y, drawWidth, drawHeight, null);
        }
    }

    /**
     * Main menu screen with Start Game, Instructions, and Exit.
     */
    private static class MainMenuView extends DesertPanel {

        MainMenuView(ViewManager manager) {
            setLayout(new GridBagLayout());
            JPanel stack = transparentPanel(new GridBagLayout());
            GridBagConstraints item = new GridBagConstraints();
            item.gridx = 0;
            item.fill = GridBagConstraints.HORIZONTAL;
            item.insets = new Insets(8, 0, 8, 0);

            item.gridy = 0;
            stack.add(new TitleLabel("PIPES IN DESERT", 54), item);

            item.gridy = 1;
            item.insets = new Insets(0, 0, 24, 0);
            stack.add(menuSubtitle("MAIN MENU"), item);

            PixelButton startButton = new PixelButton("START GAME", new Color(142, 99, 44));
            startButton.addActionListener(event -> manager.showGameSetup());
            item.gridy = 2;
            item.insets = new Insets(6, 120, 6, 120);
            stack.add(startButton, item);

            PixelButton instructionsButton = new PixelButton("INSTRUCTIONS", new Color(142, 99, 44));
            instructionsButton.addActionListener(event -> manager.showInstructions());
            item.gridy = 3;
            stack.add(instructionsButton, item);

            PixelButton exitButton = new PixelButton("EXIT", new Color(142, 99, 44));
            exitButton.addActionListener(event -> SwingUtilities.getWindowAncestor(this).dispose());
            item.gridy = 4;
            stack.add(exitButton, item);

            GridBagConstraints root = new GridBagConstraints();
            root.anchor = GridBagConstraints.CENTER;
            add(stack, root);
        }
    }

    /**
     * Setup screen for player names, team assignment, and duration.
     */
    private static class GameSetupView extends DesertPanel {

        private final JTextField[] nameFields;
        private final JRadioButton[] plumberChoices;
        private final JRadioButton[] saboteurChoices;
        private final JRadioButton durationThree;
        private final JRadioButton durationFive;
        private final JRadioButton durationSeven;

        GameSetupView(ViewManager manager) {
            setLayout(new BorderLayout());
            nameFields = new JTextField[4];
            plumberChoices = new JRadioButton[4];
            saboteurChoices = new JRadioButton[4];

            JPanel content = transparentPanel(new GridBagLayout());
            content.setBorder(new EmptyBorder(22, 30, 22, 30));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridy = 0;
            content.add(new TitleLabel("PIPES IN DESERT", 44), gbc);

            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 12, 0);
            content.add(menuSubtitle("GAME SETUP"), gbc);

            JPanel form = parchmentPanel();
            form.setLayout(new GridBagLayout());
            GridBagConstraints formGbc = new GridBagConstraints();
            formGbc.insets = new Insets(8, 12, 8, 12);
            formGbc.fill = GridBagConstraints.HORIZONTAL;

            for (int i = 0; i < 4; i++) {
                formGbc.gridx = 0;
                formGbc.gridy = i;
                formGbc.weightx = 0.0;
                form.add(formLabel("PLAYER " + (i + 1) + " NAME:"), formGbc);

                formGbc.gridx = 1;
                formGbc.weightx = 1.0;
                nameFields[i] = textField("Player " + (i + 1));
                form.add(nameFields[i], formGbc);

                ButtonGroup teamGroup = new ButtonGroup();
                plumberChoices[i] = teamRadio("PLUMBER", new Color(43, 119, 173));
                saboteurChoices[i] = teamRadio("SABOTEUR", new Color(177, 45, 45));
                teamGroup.add(plumberChoices[i]);
                teamGroup.add(saboteurChoices[i]);
                JPanel teams = transparentPanel(new GridBagLayout());
                GridBagConstraints teamGbc = new GridBagConstraints();
                teamGbc.insets = new Insets(0, 0, 0, 10);
                teamGbc.gridx = 0;
                teams.add(plumberChoices[i], teamGbc);
                teamGbc.gridx = 1;
                teams.add(saboteurChoices[i], teamGbc);
                formGbc.gridx = 2;
                formGbc.weightx = 0.0;
                form.add(teams, formGbc);
            }

            formGbc.gridx = 0;
            formGbc.gridy = 4;
            formGbc.insets = new Insets(18, 12, 8, 12);
            form.add(formLabel("GAME DURATION:"), formGbc);

            JPanel durations = transparentPanel(new GridBagLayout());
            ButtonGroup durationGroup = new ButtonGroup();
            durationThree = durationButton("3 MINUTES", 3);
            durationFive = durationButton("5 MINUTES", 5);
            durationSeven = durationButton("7 MINUTES", 7);
            durationGroup.add(durationThree);
            durationGroup.add(durationFive);
            durationGroup.add(durationSeven);
            addDurationChoice(durations, durationThree, 0);
            addDurationChoice(durations, durationFive, 1);
            addDurationChoice(durations, durationSeven, 2);

            formGbc.gridx = 1;
            formGbc.gridy = 4;
            formGbc.gridwidth = 2;
            formGbc.weightx = 1.0;
            form.add(durations, formGbc);

            JPanel buttons = transparentPanel(new GridBagLayout());
            PixelButton back = new PixelButton("BACK", new Color(142, 99, 44));
            back.addActionListener(event -> manager.showMainMenu());
            PixelButton start = new PixelButton("START GAME", new Color(55, 103, 137));
            start.addActionListener(event -> {
                GameSetup setup = readSetup();
                if (setup != null) {
                    manager.startGame(setup);
                }
            });
            addControl(buttons, back, 0);
            addControl(buttons, start, 1);
            formGbc.gridx = 0;
            formGbc.gridy = 5;
            formGbc.gridwidth = 3;
            formGbc.insets = new Insets(12, 12, 4, 12);
            form.add(buttons, formGbc);

            gbc.gridy = 2;
            gbc.weightx = 1.0;
            gbc.insets = new Insets(0, 40, 0, 40);
            content.add(form, gbc);
            add(content, BorderLayout.CENTER);
            reset();
        }

        void reset() {
            for (int i = 0; i < nameFields.length; i++) {
                nameFields[i].setText("Player " + (i + 1));
                plumberChoices[i].setSelected(i < 2);
                saboteurChoices[i].setSelected(i >= 2);
            }
            durationFive.setSelected(true);
        }

        private GameSetup readSetup() {
            List<String> names = new ArrayList<>();
            boolean[] plumbers = new boolean[nameFields.length];
            int plumberCount = 0;
            int saboteurCount = 0;
            for (int i = 0; i < nameFields.length; i++) {
                String name = nameFields[i].getText().trim();
                names.add(name.isEmpty() ? "Player " + (i + 1) : name);
                plumbers[i] = plumberChoices[i].isSelected();
                if (plumbers[i]) {
                    plumberCount++;
                } else {
                    saboteurCount++;
                }
            }
            if (plumberCount < 2 || saboteurCount < 2) {
                JOptionPane.showMessageDialog(this,
                        "Each team needs at least two players.",
                        "Invalid Teams", JOptionPane.WARNING_MESSAGE);
                return null;
            }

            int minutes = 5;
            if (durationThree.isSelected()) {
                minutes = 3;
            } else if (durationSeven.isSelected()) {
                minutes = 7;
            }
            return new GameSetup(names, plumbers, minutes);
        }
    }

    /**
     * Instruction screen required by the GUI plan.
     */
    private static class InstructionsView extends DesertPanel {

        InstructionsView(ViewManager manager) {
            setLayout(new GridBagLayout());
            JPanel panel = parchmentPanel();
            panel.setLayout(new BorderLayout(0, 14));
            panel.setPreferredSize(new Dimension(760, 500));
            panel.add(new TitleLabel("PIPES IN DESERT", 34), BorderLayout.NORTH);

            JTextArea text = new JTextArea();
            text.setEditable(false);
            text.setWrapStyleWord(true);
            text.setLineWrap(true);
            text.setOpaque(false);
            text.setForeground(new Color(43, 28, 15));
            text.setFont(pixelFont(15, Font.PLAIN));
            text.setText("Game rules\n\n"
                    + "Plumbers try to deliver as much water as possible from springs to cisterns. "
                    + "They can repair punctured pipes, repair broken pumps, change pump direction, "
                    + "connect free pipe ends, pick up generated components, place new pipes, and insert pumps.\n\n"
                    + "Saboteurs try to increase water loss. They can puncture pipes and change pump direction.\n\n"
                    + "Players move only on pipes and pumps. Pipes allow one player at a time; pumps can hold several players. "
                    + "After each action, the system updates round events, water flow, score, and turn order. "
                    + "The winner is decided when the timer ends.");
            JScrollPane scroll = new JScrollPane(text);
            scroll.setBorder(BorderFactory.createEmptyBorder());
            scroll.setOpaque(false);
            scroll.getViewport().setOpaque(false);
            panel.add(scroll, BorderLayout.CENTER);

            PixelButton back = new PixelButton("BACK", new Color(142, 99, 44));
            back.addActionListener(event -> manager.showMainMenu());
            JPanel footer = transparentPanel(new GridBagLayout());
            footer.add(back);
            panel.add(footer, BorderLayout.SOUTH);
            add(panel);
        }
    }

    /**
     * Main gameplay screen with score, network, and action panel.
     */
    private static class GameView extends DesertPanel {

        private final ViewManager manager;
        private final JLabel activePlayer;
        private final JLabel scoreLabel;
        private final JLabel timerLabel;
        private final BoardOverlay board;
        private final JPanel actionPanel;
        private final JPanel objectPanel;
        private final Timer uiTimer;
        private System gameSystem;

        GameView(ViewManager manager) {
            this.manager = manager;
            setLayout(new BorderLayout());

            JPanel hud = transparentPanel(new GridBagLayout());
            hud.setBorder(new EmptyBorder(12, 18, 8, 18));
            activePlayer = hudLabel("Current: -");
            scoreLabel = hudLabel("Plumbers 0  |  Saboteurs 0");
            timerLabel = hudLabel("Time: -");
            addHud(hud, activePlayer, 0);
            addHud(hud, scoreLabel, 1);
            addHud(hud, timerLabel, 2);
            add(hud, BorderLayout.NORTH);

            board = new BoardOverlay();
            add(board, BorderLayout.CENTER);

            objectPanel = transparentPanel(new GridBagLayout());
            objectPanel.setBorder(new EmptyBorder(18, 10, 16, 18));
            add(objectPanel, BorderLayout.EAST);

            actionPanel = transparentPanel(new GridBagLayout());
            actionPanel.setBorder(new EmptyBorder(8, 16, 16, 16));
            add(actionPanel, BorderLayout.SOUTH);

            uiTimer = new Timer(500, event -> {
                updateHud();
                if (gameSystem != null && gameSystem.checkEndCondition()) {
                    finishGame();
                }
            });
        }

        void start(GameSetup setup) {
            Team plumberTeam = new Team("Plumbers");
            Team saboteurTeam = new Team("Saboteurs");
            for (int i = 0; i < setup.names.size(); i++) {
                String name = setup.names.get(i);
                if (setup.plumbers[i]) {
                    plumberTeam.addMember(new Plumber(name, plumberTeam));
                } else {
                    saboteurTeam.addMember(new Saboteur(name, saboteurTeam));
                }
            }

            List<Team> teams = new ArrayList<>();
            teams.add(plumberTeam);
            teams.add(saboteurTeam);
            ScoreBoard score = new ScoreBoard(plumberTeam, saboteurTeam);
            gameSystem = new System(teams, new PipeNetwork(), new GameTimer(setup.minutes * 60), score);
            gameSystem.startGame();
            board.setSystem(gameSystem);
            uiTimer.start();
            refreshView();
        }

        private void rebuildActions() {
            actionPanel.removeAll();
            if (gameSystem == null || !gameSystem.isRunning()) {
                return;
            }

            Player current = gameSystem.getCurrentPlayer();
            List<JButton> buttons = new ArrayList<>();
            buttons.add(actionButton("MOVE", this::performMove));
            buttons.add(actionButton("WATER FLOW", event -> performWaterFlow()));

            if (current instanceof Plumber) {
                Plumber plumber = (Plumber) current;
                buttons.add(actionButton("PICK UP PIPE", event -> performCollectPipe(plumber)));
                buttons.add(actionButton("PICK UP PUMP", event -> performCollectPump(plumber)));
                if (plumber.isCarryingPipe()) {
                    buttons.add(actionButton("PLACE PIPE", event -> performPlacePipe(plumber)));
                }
                if (plumber.isCarryingPump()) {
                    buttons.add(actionButton("INSERT PUMP", event -> performInsertPump(plumber)));
                }
                buttons.add(actionButton("REMOVE PIPE", event -> performRemovePipe(plumber)));
                buttons.add(actionButton("CONNECT PIPE", event -> performConnectPipe(plumber)));
                buttons.add(actionButton("DISCONNECT PIPE", event -> performDisconnect(plumber)));
                buttons.add(actionButton("ROTATE PIPE", event -> performRotatePipe()));
                buttons.add(actionButton("REPAIR PIPE", event -> performRepairPipe(plumber)));
                buttons.add(actionButton("REPAIR PUMP", event -> performRepairPump(plumber)));
                buttons.add(actionButton("REMOVE PUMP", event -> performRemovePump(plumber)));
                buttons.add(actionButton("CHANGE DIRECTION", event -> performChangeDirection(plumber)));
            } else if (current instanceof Saboteur) {
                Saboteur saboteur = (Saboteur) current;
                buttons.add(actionButton("PUNCTURE PIPE", event -> performPuncture(saboteur)));
                buttons.add(actionButton("BREAK PUMP", event -> performBreakPump(saboteur)));
                buttons.add(actionButton("CHANGE DIRECTION", event -> performChangeDirection(saboteur)));
            }

            buttons.add(actionButton("END TURN", event -> advanceTurn()));
            buttons.add(actionButton("END GAME", event -> performEndGame()));
            buttons.add(actionButton("MAIN MENU", event -> manager.showMainMenu()));

            for (int i = 0; i < buttons.size(); i++) {
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridx = i % 6;
                gbc.gridy = i / 6;
                gbc.insets = new Insets(5, 6, 5, 6);
                actionPanel.add(buttons.get(i), gbc);
            }
            actionPanel.revalidate();
            actionPanel.repaint();
            rebuildObjectPanel();
        }

        private void rebuildObjectPanel() {
            objectPanel.removeAll();
            if (gameSystem == null || !gameSystem.isRunning()) {
                objectPanel.revalidate();
                objectPanel.repaint();
                return;
            }

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(5, 0, 5, 0);

            JLabel title = hudLabel("OBJECTS");
            title.setHorizontalAlignment(SwingConstants.CENTER);
            gbc.gridy = 0;
            objectPanel.add(title, gbc);

            gbc.gridy = 1;
            objectPanel.add(actionButton("ADD PIPE", event -> performAddPipe()), gbc);

            gbc.gridy = 2;
            objectPanel.add(actionButton("ADD PUMP", event -> performAddPump()), gbc);

            gbc.gridy = 3;
            gbc.weighty = 1.0;
            objectPanel.add(transparentPanel(new GridBagLayout()), gbc);

            objectPanel.revalidate();
            objectPanel.repaint();
        }

        private JButton actionButton(String text, java.awt.event.ActionListener listener) {
            PixelButton button = new PixelButton(text, new Color(103, 74, 39));
            button.setFont(pixelFont(12, Font.BOLD));
            button.addActionListener(listener);
            return button;
        }

        private void performMove(ActionEvent event) {
            Player player = gameSystem.getCurrentPlayer();
            List<NetworkElement> targets = playableNeighbors(player.getPosition(), player);
            NetworkElement target = chooseElement("Move target", targets);
            if (target == null) {
                return;
            }
            boolean before = player.hasActedThisTurn();
            player.moveTo(target);
            afterModelAction(!before && player.hasActedThisTurn(), "Move was not possible.");
        }

        private void performRepairPipe(Plumber plumber) {
            List<Pipe> pipes = reachablePipes(plumber.getPosition());
            pipes.removeIf(pipe -> !pipe.isPunctured());
            Pipe pipe = choosePipe("Repair pipe", pipes);
            if (pipe == null) {
                return;
            }
            if (!plumber.canAct()) {
                afterModelAction(false, "This plumber has already acted.");
                return;
            }
            pipe.repair();
            plumber.endTurn();
            afterModelAction(true, "The selected pipe cannot be repaired.");
        }

        private void performRepairPump(Plumber plumber) {
            List<Pump> pumps = reachablePumps(plumber.getPosition());
            pumps.removeIf(pump -> !pump.isBroken());
            Pump pump = choosePump("Repair pump", pumps);
            if (pump == null) {
                return;
            }
            if (!plumber.canAct()) {
                afterModelAction(false, "This plumber has already acted.");
                return;
            }
            pump.repair();
            plumber.endTurn();
            afterModelAction(true, "The selected pump cannot be repaired.");
        }

        private void performPuncture(Saboteur saboteur) {
            List<Pipe> pipes = reachablePipes(saboteur.getPosition());
            pipes.removeIf(Pipe::isPunctured);
            Pipe pipe = choosePipe("Puncture pipe", pipes);
            if (pipe == null) {
                return;
            }
            if (!saboteur.canAct()) {
                afterModelAction(false, "This saboteur has already acted.");
                return;
            }
            pipe.puncture();
            saboteur.endTurn();
            afterModelAction(true, "The selected pipe cannot be punctured.");
        }

        private void performChangeDirection(Player player) {
            Pump pump = choosePump("Select pump", workingReachablePumps(player.getPosition()));
            if (pump == null) {
                return;
            }
            List<Pipe> connected = new ArrayList<>(pump.getConnectedPipes());
            if (connected.size() < 2) {
                showMessage("This pump needs at least two connected pipes.");
                return;
            }
            Pipe input = choosePipe("Select input pipe", connected);
            if (input == null) {
                return;
            }
            List<Pipe> outputs = new ArrayList<>(connected);
            outputs.remove(input);
            Pipe output = choosePipe("Select output pipe", outputs);
            if (output == null) {
                return;
            }
            if (!player.canAct()) {
                afterModelAction(false, "This player has already acted.");
                return;
            }
            if (player.getPosition() == pump && player instanceof Plumber) {
                ((Plumber) player).changePumpDirection(pump, input, output);
            } else if (player.getPosition() == pump && player instanceof Saboteur) {
                ((Saboteur) player).changePumpDirection(pump, input, output);
            } else if (pump.setDirection(input, output)) {
                player.endTurn();
            }
            afterModelAction(player.hasActedThisTurn(), "Pump direction could not be changed.");
        }

        private void performAddPipe() {
            Cistern cistern = chooseCistern("Select cistern to manufacture a pipe",
                    new ArrayList<>(gameSystem.getNetwork().getCisterns()));
            if (cistern == null) {
                return;
            }
            cistern.generatePipe();
            refreshView();
        }

        private void performAddPump() {
            Cistern cistern = chooseCistern("Select cistern to manufacture a pump",
                    new ArrayList<>(gameSystem.getNetwork().getCisterns()));
            if (cistern == null) {
                return;
            }
            cistern.generatePump();
            refreshView();
        }

        private void performRemovePipe(Plumber plumber) {
            Pipe pipe = choosePipe("Remove pipe", reachablePipes(plumber.getPosition()));
            if (pipe == null) {
                return;
            }
            if (!plumber.canAct() || pipe == plumber.getPosition() || pipe.isOccupied()) {
                afterModelAction(false, "The selected pipe cannot be removed while occupied.");
                return;
            }
            gameSystem.getNetwork().removeElement(pipe);
            plumber.endTurn();
            afterModelAction(true, "The selected pipe could not be removed.");
        }

        private void performConnectPipe(Plumber plumber) {
            performConnectFreeEnd(plumber);
        }

        private void performDisconnect(Plumber plumber) {
            List<Pipe> pipes = reachablePipes(plumber.getPosition());
            Pipe pipe = choosePipe("Select pipe to disconnect", pipes);
            if (pipe == null) {
                return;
            }
            List<NetworkElement> localNeighbors = new ArrayList<>(pipe.getNeighbors());
            localNeighbors.removeIf(neighbor -> !isAtOrAdjacentTo(plumber.getPosition(), neighbor));
            NetworkElement neighbor = chooseElement("Disconnect from", localNeighbors);
            if (neighbor == null) {
                return;
            }
            gameSystem.getNetwork().disconnectElements(pipe, neighbor);
            plumber.endTurn();
            afterModelAction(true, "Pipe could not be disconnected.");
        }

        private void performRotatePipe() {
            Pipe pipe = choosePipe("Rotate pipe", reachablePipes(gameSystem.getCurrentPlayer().getPosition()));
            if (pipe == null) {
                return;
            }
            board.rotatePipe(pipe);
            refreshView();
        }

        private void performConnectFreeEnd(Plumber plumber) {
            List<Pipe> freePipes = new ArrayList<>();
            for (Pipe pipe : gameSystem.getNetwork().getPipes()) {
                if (pipe.hasFreeEnd() && canWorkOnPipe(plumber.getPosition(), pipe)) {
                    freePipes.add(pipe);
                }
            }
            Pipe pipe = choosePipe("Select free pipe", freePipes);
            if (pipe == null) {
                return;
            }
            List<NetworkElement> targets = reachableElements(plumber.getPosition());
            targets.remove(pipe);
            targets.removeIf(target -> gameSystem.getNetwork().areAdjacent(pipe, target));
            targets.removeIf(target -> createsParallelPipeRoute(pipe, target));
            NetworkElement target = chooseElement("Connect to", targets);
            if (target == null) {
                return;
            }
            boolean beforeFree = pipe.hasFreeEnd();
            if (!plumber.canAct()) {
                afterModelAction(false, "This plumber has already acted.");
                return;
            }
            if (isAtOrAdjacentTo(plumber.getPosition(), pipe)) {
                plumber.connectPipeEnd(pipe, target);
            } else if (pipe.connectFreeEnd(target)) {
                plumber.endTurn();
            }
            afterModelAction(beforeFree && !pipe.hasFreeEnd(), "Free pipe end could not be connected.");
        }

        private void performCollectPipe(Plumber plumber) {
            Cistern cistern = chooseCistern("Select cistern with pipe", reachableCisterns(plumber.getPosition(), true, false));
            if (cistern == null) {
                return;
            }
            boolean before = plumber.isCarryingPipe();
            plumber.collectPipeFromCistern(cistern);
            afterModelAction(!before && plumber.isCarryingPipe(), "No pipe could be collected.");
        }

        private void performCollectPump(Plumber plumber) {
            Cistern cistern = chooseCistern("Select cistern with pump", reachableCisterns(plumber.getPosition(), false, true));
            if (cistern == null) {
                return;
            }
            boolean before = plumber.isCarryingPump();
            plumber.collectPumpFromCistern(cistern);
            boolean success = !before && plumber.isCarryingPump();
            if (success) {
                plumber.endTurn();
            }
            afterModelAction(success, "No pump could be collected.");
        }

        private void performInsertPump(Plumber plumber) {
            Pipe target = choosePipe("Insert pump into pipe", reachablePipes(plumber.getPosition()));
            if (target == null) {
                return;
            }
            boolean before = plumber.isCarryingPump();
            plumber.insertPump(target, new Pump(gameSystem.getNetwork().generateId()), gameSystem.getNetwork());
            afterModelAction(before && !plumber.isCarryingPump(), "Pump could not be inserted.");
        }

        private void performPlacePipe(Plumber plumber) {
            List<NetworkElement> localEndpoints = reachableElements(plumber.getPosition());
            NetworkElement a = chooseElement("First endpoint", localEndpoints);
            if (a == null) {
                return;
            }
            List<NetworkElement> secondEndpoints = new ArrayList<>(localEndpoints);
            secondEndpoints.remove(a);
            secondEndpoints.removeIf(endpoint -> gameSystem.getNetwork().areAdjacent(a, endpoint));
            EndpointChoice b = chooseEndpointChoice("Second endpoint", secondEndpoints);
            if (b == null) {
                return;
            }
            boolean before = plumber.isCarryingPipe();
            Pipe newPipe = new Pipe(gameSystem.getNetwork().generateId());
            if (b.freeEnd) {
                plumber.placeNewPipe(newPipe, a, gameSystem.getNetwork());
            } else {
                plumber.placeNewPipe(newPipe, a, b.element, gameSystem.getNetwork());
            }
            afterModelAction(before && !plumber.isCarryingPipe(), "Pipe could not be placed.");
        }

        private void performRemovePump(Plumber plumber) {
            List<Pump> pumps = reachablePumps(plumber.getPosition());
            pumps.removeIf(pump -> !pump.getOccupants().isEmpty());
            Pump pump = choosePump("Remove pump", pumps);
            if (pump == null) {
                return;
            }
            if (!plumber.canAct() || gameSystem.getNetwork().getPumps().size() <= 1) {
                afterModelAction(false, "The selected pump cannot be removed.");
                return;
            }
            gameSystem.getNetwork().removeElement(pump);
            plumber.endTurn();
            afterModelAction(true, "The selected pump could not be removed.");
        }

        private void performBreakPump(Saboteur saboteur) {
            List<Pump> pumps = reachablePumps(saboteur.getPosition());
            pumps.removeIf(Pump::isBroken);
            Pump pump = choosePump("Break pump", pumps);
            if (pump == null) {
                return;
            }
            if (!saboteur.canAct()) {
                afterModelAction(false, "This saboteur has already acted.");
                return;
            }
            pump.breakDown();
            saboteur.endTurn();
            afterModelAction(true, "The selected pump could not be broken.");
        }

        private void performWaterFlow() {
            if (gameSystem != null && gameSystem.getWaterFlowManager() != null) {
                gameSystem.getWaterFlowManager().recalculateFlow();
                refreshView();
            }
        }

        private void performEndGame() {
            finishGame();
        }

        private void advanceTurn() {
            if (gameSystem == null || !gameSystem.isRunning()) {
                return;
            }
            gameSystem.updateRoundEvents();
            gameSystem.nextTurn();
            checkEndOrRefresh();
        }

        private void afterModelAction(boolean success, String failureMessage) {
            if (!success) {
                showMessage(failureMessage);
                refreshView();
                return;
            }
            advanceTurn();
        }

        private void checkEndOrRefresh() {
            if (gameSystem == null) {
                return;
            }
            if (gameSystem.checkEndCondition() || !gameSystem.isRunning()) {
                finishGame();
            } else {
                refreshView();
            }
        }

        private void finishGame() {
            if (gameSystem == null) {
                return;
            }
            uiTimer.stop();
            gameSystem.endGame();
            manager.showEndGame(gameSystem);
        }

        private void refreshView() {
            updateHud();
            rebuildActions();
            board.repaint();
        }

        private void updateHud() {
            if (gameSystem == null) {
                return;
            }
            Player player = gameSystem.getCurrentPlayer();
            String actor = player == null ? "-" : player.getName() + " (" + player.getTeam().getName() + ")";
            if (player instanceof Plumber) {
                Plumber plumber = (Plumber) player;
                actor += "  Carrying: "
                        + (plumber.isCarryingPipe() ? "pipe " : "")
                        + (plumber.isCarryingPump() ? "pump" : "");
            }
            activePlayer.setText("Current: " + actor.trim());
            ScoreBoard score = gameSystem.getScoreBoard();
            scoreLabel.setText("Plumbers " + score.getPlumberScore()
                    + "  |  Saboteurs " + score.getSaboteurScore());
            timerLabel.setText("Time: " + gameSystem.getTimer().getRemainingTime() + "s");
        }

        private List<NetworkElement> playableNeighbors(NetworkElement position, Player player) {
            List<NetworkElement> result = new ArrayList<>();
            if (position == null) {
                return result;
            }
            for (NetworkElement element : gameSystem.getNetwork().getNeighbors(position)) {
                if (element instanceof Pump) {
                    result.add(element);
                } else if (element instanceof Pipe) {
                    Pipe pipe = (Pipe) element;
                    if (!pipe.isOccupied() || pipe.getOccupant() == player) {
                        result.add(element);
                    }
                }
            }
            return result;
        }

        private List<NetworkElement> reachableElements(NetworkElement position) {
            List<NetworkElement> result = new ArrayList<>();
            if (position != null) {
                result.add(position);
                result.addAll(gameSystem.getNetwork().getNeighbors(position));
            }
            return result;
        }

        private List<Pipe> reachablePipes(NetworkElement position) {
            List<Pipe> result = new ArrayList<>();
            for (NetworkElement element : reachableElements(position)) {
                if (element instanceof Pipe) {
                    result.add((Pipe) element);
                }
            }
            return result;
        }

        private List<Pump> reachablePumps(NetworkElement position) {
            List<Pump> result = new ArrayList<>();
            for (NetworkElement element : reachableElements(position)) {
                if (element instanceof Pump) {
                    result.add((Pump) element);
                }
            }
            return result;
        }

        private List<Pump> workingReachablePumps(NetworkElement position) {
            List<Pump> result = reachablePumps(position);
            result.removeIf(Pump::isBroken);
            return result;
        }

        private List<Cistern> reachableCisterns(NetworkElement position, boolean needsPipe, boolean needsPump) {
            List<Cistern> result = new ArrayList<>();
            for (Cistern cistern : gameSystem.getNetwork().getCisterns()) {
                boolean hasInventory = (!needsPipe || cistern.hasAvailablePipe())
                        && (!needsPump || cistern.hasAvailablePump());
                if (hasInventory && isAtOrNearCistern(position, cistern)) {
                    result.add(cistern);
                }
            }
            return result;
        }

        private boolean isAtOrNearCistern(NetworkElement position, Cistern cistern) {
            return isAtOrAdjacentTo(position, cistern);
        }

        private boolean isAtOrAdjacentTo(NetworkElement position, NetworkElement element) {
            return position == element || (position != null && position.isAdjacentTo(element));
        }

        private boolean canWorkOnPipe(NetworkElement position, Pipe pipe) {
            return isAtOrAdjacentTo(position, pipe);
        }

        private boolean createsParallelPipeRoute(Pipe pipe, NetworkElement target) {
            if (pipe == null || target == null) {
                return false;
            }
            for (NetworkElement currentNeighbor : pipe.getNeighbors()) {
                if (currentNeighbor == target) {
                    return true;
                }
                for (NetworkElement targetNeighbor : target.getNeighbors()) {
                    if (targetNeighbor instanceof Pipe
                            && targetNeighbor != pipe
                            && targetNeighbor.isAdjacentTo(currentNeighbor)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private Pipe choosePipe(String title, List<Pipe> pipes) {
            List<NetworkElement> elements = new ArrayList<>(pipes);
            NetworkElement chosen = chooseElement(title, elements);
            return chosen instanceof Pipe ? (Pipe) chosen : null;
        }

        private Cistern chooseCistern(String title, List<Cistern> cisterns) {
            List<NetworkElement> elements = new ArrayList<>(cisterns);
            NetworkElement chosen = chooseElement(title, elements);
            return chosen instanceof Cistern ? (Cistern) chosen : null;
        }

        private Pump choosePump(String title, List<Pump> pumps) {
            List<NetworkElement> elements = new ArrayList<>(pumps);
            NetworkElement chosen = chooseElement(title, elements);
            return chosen instanceof Pump ? (Pump) chosen : null;
        }

        private NetworkElement chooseElement(String title, List<? extends NetworkElement> elements) {
            if (elements == null || elements.isEmpty()) {
                showMessage("No valid options are available.");
                return null;
            }
            Choice[] choices = new Choice[elements.size()];
            for (int i = 0; i < elements.size(); i++) {
                choices[i] = new Choice(elements.get(i));
            }
            Choice chosen = (Choice) JOptionPane.showInputDialog(this, title, "Select",
                    JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
            return chosen == null ? null : chosen.element;
        }

        private EndpointChoice chooseEndpointChoice(String title, List<? extends NetworkElement> elements) {
            List<EndpointChoice> choices = new ArrayList<>();
            choices.add(EndpointChoice.freeEnd());
            if (elements != null) {
                for (NetworkElement element : elements) {
                    choices.add(EndpointChoice.connected(element));
                }
            }

            EndpointChoice chosen = (EndpointChoice) JOptionPane.showInputDialog(this, title, "Select",
                    JOptionPane.QUESTION_MESSAGE, null, choices.toArray(), choices.get(0));
            return chosen;
        }

        private void showMessage(String message) {
            JOptionPane.showMessageDialog(this, message, "Action", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * End-game screen with winner and final score.
     */
    private static class EndGameView extends DesertPanel {

        private final JLabel winnerLabel;
        private final JLabel scoresLabel;

        EndGameView(ViewManager manager) {
            setLayout(new GridBagLayout());
            JPanel panel = parchmentPanel();
            panel.setLayout(new GridBagLayout());
            panel.setPreferredSize(new Dimension(620, 360));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(8, 20, 8, 20);

            gbc.gridy = 0;
            panel.add(new TitleLabel("GAME OVER", 42), gbc);
            winnerLabel = menuSubtitle("Winner: -");
            gbc.gridy = 1;
            panel.add(winnerLabel, gbc);
            scoresLabel = hudLabel("Final score");
            gbc.gridy = 2;
            panel.add(scoresLabel, gbc);

            JPanel buttons = transparentPanel(new GridBagLayout());
            PixelButton again = new PixelButton("NEW GAME", new Color(55, 103, 137));
            again.addActionListener(event -> manager.showGameSetup());
            PixelButton menu = new PixelButton("MAIN MENU", new Color(142, 99, 44));
            menu.addActionListener(event -> manager.showMainMenu());
            addControl(buttons, again, 0);
            addControl(buttons, menu, 1);
            gbc.gridy = 3;
            panel.add(buttons, gbc);
            add(panel);
        }

        void displayResult(System system) {
            ScoreBoard score = system.getScoreBoard();
            Team winner = score.determineWinner();
            winnerLabel.setText(winner == null ? "Winner: DRAW" : "Winner: " + winner.getName());
            scoresLabel.setText("Plumbers " + score.getPlumberScore()
                    + "  |  Saboteurs " + score.getSaboteurScore());
        }
    }

    /**
     * Draws the active pipe network from the model.
     */
    private static class BoardOverlay extends JComponent {

        private final BufferedImage springImage = ImageAssets.load("spring.png");
        private final BufferedImage cisternImage = ImageAssets.load("cistern.png");
        private final BufferedImage pumpImage = ImageAssets.load("pump.png");
        private final BufferedImage brokenPumpImage = ImageAssets.load("brokenpump.png");
        private final BufferedImage plumberImage = ImageAssets.load("plumber.png");
        private final BufferedImage saboteurImage = ImageAssets.load("Saboteur.png");
        private final BufferedImage pipeImage = ImageAssets.load("pipe1.png");
        private final BufferedImage puncturedImage = ImageAssets.load("brokenpaper.png");
        private final Map<Integer, Integer> pipeRotations = new HashMap<>();
        private final Map<Integer, Point> stableNodePoints = new HashMap<>();
        private System gameSystem;

        void setSystem(System gameSystem) {
            this.gameSystem = gameSystem;
            pipeRotations.clear();
            stableNodePoints.clear();
        }

        void rotatePipe(Pipe pipe) {
            if (pipe == null) {
                return;
            }
            int next = (pipeRotations.getOrDefault(pipe.getId(), 0) + 1) % 4;
            pipeRotations.put(pipe.getId(), next);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (gameSystem == null || gameSystem.getNetwork() == null) {
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Map<NetworkElement, Point> points = layoutNetwork();
            drawConnections(g2, points);
            drawElements(g2, points);
            drawPlayers(g2, points);
            drawLegend(g2);
            g2.dispose();
        }

        private Map<NetworkElement, Point> layoutNetwork() {
            PipeNetwork network = gameSystem.getNetwork();
            Map<NetworkElement, Point> points = new HashMap<>();
            int width = Math.max(1, getWidth());
            int height = Math.max(1, getHeight());
            int centerY = height / 2;
            boolean initialLayout = stableNodePoints.isEmpty();

            int springIndex = 0;
            for (Spring spring : network.getSprings()) {
                Point fallback = new Point(width / 9, spreadY(springIndex, network.getSprings().size(), centerY, 150,
                        height));
                points.put(spring, stablePoint(spring, fallback, width, height));
                springIndex++;
            }

            int cisternIndex = 0;
            for (Cistern cistern : network.getCisterns()) {
                Point fallback = new Point(width * 17 / 20,
                        spreadY(cisternIndex, network.getCisterns().size(), centerY, 220, height));
                points.put(cistern, stablePoint(cistern, fallback, width, height));
                cisternIndex++;
            }

            List<Pump> pumps = network.getPumps();
            for (int i = 0; i < pumps.size(); i++) {
                int x;
                int y;
                if (pumps.size() == 1) {
                    x = width * 9 / 20;
                    y = centerY;
                } else if (pumps.size() == 2) {
                    x = i == 0 ? width * 2 / 5 : width * 3 / 5;
                    y = centerY + (i == 0 ? -85 : 85);
                } else if (i == 0) {
                    x = width * 2 / 5;
                    y = centerY;
                } else {
                    x = width * 3 / 5;
                    y = spreadY(i - 1, pumps.size() - 1, centerY, 260, height);
                }
                Pump pump = pumps.get(i);
                Point fallback = initialLayout ? new Point(x, y) : inferredPumpPoint(pump, points, width, height);
                points.put(pump, stablePoint(pump, fallback, width, height));
            }

            int pipeFallback = 0;
            Map<String, Integer> routeCounts = new HashMap<>();
            for (Pipe pipe : network.getPipes()) {
                List<NetworkElement> anchors = positionedPipeAnchors(pipe, points);
                if (anchors.size() > 1) {
                    String route = routeKey(anchors.get(0), anchors.get(1));
                    routeCounts.put(route, routeCounts.getOrDefault(route, 0) + 1);
                }
            }
            Map<String, Integer> routeIndexes = new HashMap<>();
            for (Pipe pipe : network.getPipes()) {
                List<Point> anchors = new ArrayList<>();
                List<NetworkElement> anchorElements = new ArrayList<>();
                for (NetworkElement neighbor : pipe.getNeighbors()) {
                    Point point = points.get(neighbor);
                    if (point != null) {
                        anchors.add(point);
                        anchorElements.add(neighbor);
                    }
                }
                if (anchors.size() > 1) {
                    String route = routeKey(anchorElements.get(0), anchorElements.get(1));
                    int routeIndex = routeIndexes.getOrDefault(route, 0);
                    routeIndexes.put(route, routeIndex + 1);
                    int routeCount = routeCounts.getOrDefault(route, 1);
                    points.put(pipe, pipeBetweenPoint(anchors.get(0), anchors.get(1), routeIndex, routeCount,
                            width, height));
                } else if (anchors.size() == 1) {
                    points.put(pipe, freeEndPoint(pipe, anchorElements.get(0), anchors.get(0), width, height));
                } else {
                    points.put(pipe, new Point(width / 3 + pipeFallback * 80, centerY + 150));
                    pipeFallback++;
                }
            }
            return points;
        }

        private Point stablePoint(NetworkElement element, Point fallback, int width, int height) {
            Point point = stableNodePoints.get(element.getId());
            if (point == null) {
                point = fallback;
                stableNodePoints.put(element.getId(), point);
            }
            return new Point(clamp(point.x, 80, width - 80), clamp(point.y, 100, height - 120));
        }

        private Point inferredPumpPoint(Pump pump, Map<NetworkElement, Point> points, int width, int height) {
            List<Point> anchors = new ArrayList<>();
            for (Pipe pipe : pump.getConnectedPipes()) {
                for (NetworkElement neighbor : pipe.getNeighbors()) {
                    if (neighbor != pump && points.containsKey(neighbor)) {
                        anchors.add(points.get(neighbor));
                    }
                }
            }
            if (anchors.size() >= 2) {
                Point a = anchors.get(0);
                Point b = anchors.get(1);
                return new Point(clamp((a.x + b.x) / 2, 100, width - 100),
                        clamp((a.y + b.y) / 2, 120, height - 140));
            }
            if (anchors.size() == 1) {
                Point anchor = anchors.get(0);
                return new Point(clamp(anchor.x + 90, 100, width - 100),
                        clamp(anchor.y, 120, height - 140));
            }
            return new Point(width / 2, height / 2);
        }

        private List<NetworkElement> positionedPipeAnchors(Pipe pipe, Map<NetworkElement, Point> points) {
            List<NetworkElement> anchors = new ArrayList<>();
            for (NetworkElement neighbor : pipe.getNeighbors()) {
                if (points.containsKey(neighbor)) {
                    anchors.add(neighbor);
                }
            }
            return anchors;
        }

        private String routeKey(NetworkElement a, NetworkElement b) {
            int low = Math.min(a.getId(), b.getId());
            int high = Math.max(a.getId(), b.getId());
            return low + ":" + high;
        }

        private int spreadY(int index, int count, int centerY, int totalSpan, int height) {
            if (count <= 1) {
                return centerY;
            }
            int top = centerY - totalSpan / 2;
            int step = totalSpan / Math.max(1, count - 1);
            return clamp(top + index * step, 105, height - 130);
        }

        private Point pipeBetweenPoint(Point a, Point b, int routeIndex, int routeCount, int width, int height) {
            int midX = (a.x + b.x) / 2;
            int midY = (a.y + b.y) / 2;
            double dx = b.x - a.x;
            double dy = b.y - a.y;
            double length = Math.max(1.0, Math.sqrt(dx * dx + dy * dy));
            double centeredIndex = routeIndex - (routeCount - 1) / 2.0;
            int offset = (int) Math.round(centeredIndex * 42);
            int x = (int) Math.round(midX + (-dy / length) * offset);
            int y = (int) Math.round(midY + (dx / length) * offset);
            return new Point(clamp(x, 80, width - 80), clamp(y, 100, height - 120));
        }

        private Point freeEndPoint(Pipe pipe, NetworkElement neighbor, Point anchor, int width, int height) {
            int offsetX = 0;
            int offsetY = ((pipe.getId() % 3) - 1) * 38;
            if (neighbor instanceof Spring) {
                offsetX = 120;
            } else if (neighbor instanceof Cistern) {
                offsetX = -120;
            } else if (neighbor instanceof Pump) {
                offsetX = pipe.getId() % 2 == 0 ? -120 : 120;
                offsetY = pipe.getId() % 4 < 2 ? -72 : 72;
            } else {
                offsetX = 95;
            }
            return new Point(clamp(anchor.x + offsetX, 70, width - 70),
                    clamp(anchor.y + offsetY, 90, height - 90));
        }

        private int clamp(int value, int min, int max) {
            return Math.max(min, Math.min(max, value));
        }

        private void drawConnections(Graphics2D g2, Map<NetworkElement, Point> points) {
            Set<String> drawn = new LinkedHashSet<>();
            for (NetworkElement element : gameSystem.getNetwork().getElements()) {
                Point a = points.get(element);
                if (a == null) {
                    continue;
                }
                for (NetworkElement neighbor : element.getNeighbors()) {
                    Point b = points.get(neighbor);
                    if (b == null) {
                        continue;
                    }
                    String key = Math.min(element.getId(), neighbor.getId()) + ":"
                            + Math.max(element.getId(), neighbor.getId());
                    if (drawn.add(key)) {
                        boolean bad = element instanceof Pipe && ((Pipe) element).isPunctured()
                                || neighbor instanceof Pipe && ((Pipe) neighbor).isPunctured();
                        g2.setStroke(new BasicStroke(14, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.setColor(new Color(55, 34, 18, 220));
                        g2.drawLine(a.x, a.y, b.x, b.y);
                        g2.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                        g2.setColor(bad ? new Color(144, 43, 33, 230) : new Color(176, 124, 51, 230));
                        g2.drawLine(a.x, a.y, b.x, b.y);
                    }
                }
            }
        }

        private void drawElements(Graphics2D g2, Map<NetworkElement, Point> points) {
            for (NetworkElement element : gameSystem.getNetwork().getElements()) {
                Point point = points.get(element);
                if (point != null && element instanceof Pipe) {
                    drawPipeNode(g2, (Pipe) element, point);
                }
            }
            for (NetworkElement element : gameSystem.getNetwork().getElements()) {
                Point point = points.get(element);
                if (point == null || element instanceof Pipe) {
                    continue;
                }
                if (element instanceof Spring) {
                    drawSprite(g2, springImage, point, 80, 104, new Color(42, 129, 46), label(element));
                } else if (element instanceof Cistern) {
                    Cistern cistern = (Cistern) element;
                    drawSprite(g2, cisternImage, point, 88, 96, new Color(121, 91, 54),
                            label(element) + " P" + cistern.getAvailablePipes() + "/M" + cistern.getAvailablePumps());
                } else if (element instanceof Pump) {
                    Pump pump = (Pump) element;
                    drawSprite(g2, pump.isBroken() ? brokenPumpImage : pumpImage, point, 86, 76,
                            pump.isBroken() ? new Color(130, 42, 38) : new Color(72, 92, 124),
                            label(element) + (pump.isBroken() ? " BROKEN" : ""));
                    drawPumpDirection(g2, pump, point, points);
                }
            }
        }

        private void drawPipeNode(Graphics2D g2, Pipe pipe, Point point) {
            int width = 74;
            int height = 44;
            if (pipeImage != null) {
                Graphics2D rotated = (Graphics2D) g2.create();
                int turns = pipeRotations.getOrDefault(pipe.getId(), 0);
                rotated.rotate(Math.toRadians(turns * 90), point.x, point.y);
                rotated.drawImage(pipeImage, point.x - width / 2, point.y - height / 2, width, height, null);
                rotated.dispose();
            } else {
                g2.setColor(new Color(151, 101, 44));
                g2.fillRoundRect(point.x - width / 2, point.y - height / 2, width, height, 12, 12);
            }
            if (pipe.getCurrentWater() > 0) {
                g2.setColor(new Color(68, 164, 213, 180));
                int fill = Math.max(6, (int) ((pipe.getCurrentWater() / (double) pipe.getCapacity()) * (width - 12)));
                g2.fillRoundRect(point.x - width / 2 + 6, point.y - 7, fill, 14, 8, 8);
            }
            if (pipe.isPunctured()) {
                if (puncturedImage != null) {
                    g2.drawImage(puncturedImage, point.x - 20, point.y - 38, 40, 40, null);
                }
                g2.setColor(new Color(174, 45, 35));
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(point.x - 24, point.y - 24, 48, 48);
            }
            if (pipe.hasFreeEnd()) {
                g2.setColor(new Color(255, 219, 75));
                g2.setStroke(new BasicStroke(4));
                g2.drawRoundRect(point.x - width / 2 - 5, point.y - height / 2 - 5, width + 10, height + 10, 12, 12);
            }
            drawLabel(g2, label(pipe), point.x, point.y + 44);
        }

        private void drawPumpDirection(Graphics2D g2, Pump pump, Point pumpPoint,
                                       Map<NetworkElement, Point> points) {
            Pipe input = pump.getActiveInput();
            Pipe output = pump.getActiveOutput();
            if (input == null || output == null || pump.isBroken()) {
                return;
            }
            Point in = points.get(input);
            Point out = points.get(output);
            if (in == null || out == null) {
                return;
            }
            g2.setColor(new Color(55, 148, 205, 220));
            g2.setStroke(new BasicStroke(3));
            g2.drawLine(in.x, in.y, pumpPoint.x, pumpPoint.y);
            g2.drawLine(pumpPoint.x, pumpPoint.y, out.x, out.y);
            g2.fillOval(out.x - 5, out.y - 5, 10, 10);
        }

        private void drawPlayers(Graphics2D g2, Map<NetworkElement, Point> points) {
            Map<NetworkElement, Integer> counts = new HashMap<>();
            for (Team team : gameSystem.getTeams()) {
                for (Player player : team.getMembers()) {
                    Point base = points.get(player.getPosition());
                    if (base == null) {
                        continue;
                    }
                    int index = counts.getOrDefault(player.getPosition(), 0);
                    counts.put(player.getPosition(), index + 1);
                    Point point = new Point(base.x - 36 + index * 28, base.y + 54);
                    BufferedImage image = player instanceof Plumber ? plumberImage : saboteurImage;
                    drawSprite(g2, image, point, 48, 62,
                            player instanceof Plumber ? new Color(53, 121, 171) : new Color(160, 54, 46),
                            player.getName());
                }
            }
        }

        private void drawLegend(Graphics2D g2) {
            int x = 22;
            int y = 18;
            RoundRectangle2D box = new RoundRectangle2D.Double(x, y, 330, 80, 14, 14);
            g2.setColor(new Color(55, 34, 18, 185));
            g2.fill(box);
            g2.setColor(new Color(235, 193, 104));
            g2.setStroke(new BasicStroke(2));
            g2.draw(box);
            g2.setColor(Color.WHITE);
            g2.setFont(pixelFont(12, Font.BOLD));
            g2.drawString("States: red = punctured, gold outline = free end", x + 14, y + 30);
            g2.drawString("Blue route shows active pump direction", x + 14, y + 55);
        }

        private void drawSprite(Graphics2D g2, BufferedImage image, Point center,
                                int width, int height, Color fallback, String text) {
            int x = center.x - width / 2;
            int y = center.y - height / 2;
            if (image != null) {
                g2.drawImage(image, x, y, width, height, null);
            } else {
                g2.setColor(fallback);
                g2.fillRoundRect(x, y, width, height, 16, 16);
            }
            drawLabel(g2, text, center.x, y + height + 16);
        }

        private void drawLabel(Graphics2D g2, String text, int centerX, int y) {
            g2.setFont(pixelFont(11, Font.BOLD));
            FontMetrics metrics = g2.getFontMetrics();
            int x = centerX - metrics.stringWidth(text) / 2;
            g2.setColor(new Color(43, 28, 15));
            g2.drawString(text, x + 2, y + 2);
            g2.setColor(Color.WHITE);
            g2.drawString(text, x, y);
        }

        private String label(NetworkElement element) {
            if (element instanceof Spring) {
                return "Spring#" + element.getId();
            }
            if (element instanceof Cistern) {
                return "Cistern#" + element.getId();
            }
            if (element instanceof Pump) {
                return "Pump#" + element.getId();
            }
            if (element instanceof Pipe) {
                return "Pipe#" + element.getId();
            }
            return element.toString();
        }
    }

    /**
     * Setup values collected from the setup screen.
     */
    private static class GameSetup {

        private final List<String> names;
        private final boolean[] plumbers;
        private final int minutes;

        GameSetup(List<String> names, boolean[] plumbers, int minutes) {
            this.names = names;
            this.plumbers = plumbers;
            this.minutes = minutes;
        }
    }

    /**
     * Choice wrapper for dialogs.
     */
    private static class Choice {

        private final NetworkElement element;

        Choice(NetworkElement element) {
            this.element = element;
        }

        @Override
        public String toString() {
            String text = element.getClass().getSimpleName() + "#" + element.getId();
            if (element instanceof Pipe) {
                Pipe pipe = (Pipe) element;
                text += " water=" + pipe.getCurrentWater() + "/" + pipe.getCapacity();
                if (pipe.isPunctured()) {
                    text += " punctured";
                }
                if (pipe.hasFreeEnd()) {
                    text += " free-end";
                }
                if (pipe.isOccupied()) {
                    text += " occupied";
                }
            } else if (element instanceof Pump) {
                Pump pump = (Pump) element;
                text += pump.isBroken() ? " broken" : " working";
                text += " pipes=" + pump.getConnectedPipes().size();
            } else if (element instanceof Cistern) {
                Cistern cistern = (Cistern) element;
                text += " pipes=" + cistern.getAvailablePipes() + " pumps=" + cistern.getAvailablePumps();
            }
            return text;
        }
    }

    /**
     * Choice wrapper for optional pipe endpoints.
     */
    private static class EndpointChoice {

        private final boolean freeEnd;
        private final NetworkElement element;

        private EndpointChoice(boolean freeEnd, NetworkElement element) {
            this.freeEnd = freeEnd;
            this.element = element;
        }

        static EndpointChoice freeEnd() {
            return new EndpointChoice(true, null);
        }

        static EndpointChoice connected(NetworkElement element) {
            return new EndpointChoice(false, element);
        }

        @Override
        public String toString() {
            return freeEnd ? "Free end" : new Choice(element).toString();
        }
    }

    /**
     * Resolves image assets from common launch directories.
     */
    private static class ImageAssets {

        static BufferedImage load(String name) {
            for (Path candidate : candidates(name)) {
                File file = candidate.toFile();
                if (file.isFile()) {
                    try {
                        return ImageIO.read(file);
                    } catch (IOException ignored) {
                        return null;
                    }
                }
            }
            return null;
        }

        private static List<Path> candidates(String name) {
            Path cwd = Paths.get(java.lang.System.getProperty("user.dir")).toAbsolutePath();
            List<Path> paths = new ArrayList<>();
            paths.add(cwd.resolve("assets").resolve("images").resolve(name));
            paths.add(cwd.resolve("..").resolve("assets").resolve("images").resolve(name).normalize());
            paths.add(cwd.resolve("..").resolve("..").resolve("assets").resolve("images").resolve(name).normalize());
            paths.add(cwd.resolve(name));
            if ("gameplayscreen.png".equals(name)) {
                paths.add(Paths.get(java.lang.System.getProperty("user.home"), "Desktop", "gameplayscreen.png"));
            }
            return paths;
        }
    }

    private static JRadioButton durationButton(String text, int minutes) {
        JRadioButton button = new JRadioButton(text);
        button.setActionCommand(Integer.toString(minutes));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(121, 83, 39));
        button.setFont(pixelFont(14, Font.BOLD));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setOpaque(true);
        return button;
    }

    private static void addDurationChoice(JPanel panel, JRadioButton radio, int x) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.insets = new Insets(0, 6, 0, 6);
        panel.add(radio, gbc);
    }

    private static void addHud(JPanel panel, JLabel label, int x) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 8, 0, 8);
        panel.add(label, gbc);
    }

    private static void addControl(JPanel panel, JButton button, int x) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.insets = new Insets(0, 8, 0, 8);
        panel.add(button, gbc);
    }

    private static JPanel transparentPanel(java.awt.LayoutManager layout) {
        JPanel panel = new JPanel(layout);
        panel.setOpaque(false);
        return panel;
    }

    private static JPanel parchmentPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(new Color(190, 137, 61, 235));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(55, 34, 18), 4),
                new EmptyBorder(18, 18, 18, 18)));
        return panel;
    }

    private static JLabel menuSubtitle(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setForeground(Color.WHITE);
        label.setFont(pixelFont(25, Font.BOLD));
        label.setBorder(new EmptyBorder(4, 0, 4, 0));
        return label;
    }

    private static JLabel formLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(new Color(43, 28, 15));
        label.setFont(pixelFont(16, Font.BOLD));
        return label;
    }

    private static JLabel hudLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setOpaque(true);
        label.setBackground(new Color(55, 34, 18, 210));
        label.setForeground(Color.WHITE);
        label.setFont(pixelFont(14, Font.BOLD));
        label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 193, 104), 2),
                new EmptyBorder(8, 12, 8, 12)));
        return label;
    }

    private static JTextField textField(String text) {
        JTextField field = new JTextField(text, 16);
        field.setFont(pixelFont(15, Font.PLAIN));
        field.setForeground(new Color(246, 220, 156));
        field.setCaretColor(new Color(246, 220, 156));
        field.setBackground(new Color(58, 39, 20));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(24, 17, 11), 3),
                new EmptyBorder(8, 10, 8, 10)));
        return field;
    }

    private static JRadioButton teamRadio(String text, Color color) {
        JRadioButton radio = new JRadioButton(text);
        radio.setOpaque(false);
        radio.setForeground(color);
        radio.setFont(pixelFont(14, Font.BOLD));
        return radio;
    }

    private static Font pixelFont(int size, int style) {
        Font font = new Font("Consolas", style, size);
        if (!"Consolas".equals(font.getFamily())) {
            font = new Font(Font.MONOSPACED, style, size);
        }
        return font;
    }

    /**
     * Chunky button styled after the provided mockups.
     */
    private static class PixelButton extends JButton {

        private final Color base;

        PixelButton(String text, Color base) {
            super(text);
            this.base = base;
            setForeground(Color.WHITE);
            setFont(pixelFont(18, Font.BOLD));
            setFocusPainted(false);
            setContentAreaFilled(false);
            setBorder(new EmptyBorder(12, 20, 12, 20));
            setAlignmentX(Component.CENTER_ALIGNMENT);
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = getModel().isPressed() ? base.darker() : base;
            if (getModel().isRollover()) {
                fill = fill.brighter();
            }
            g2.setColor(new Color(43, 28, 15));
            g2.fillRoundRect(3, 6, getWidth() - 6, getHeight() - 6, 8, 8);
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 8, 8, 8);
            g2.setColor(new Color(235, 193, 104));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(2, 2, getWidth() - 10, getHeight() - 12, 8, 8);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    /**
     * Pixel-style title with a dark shadow and gold fill.
     */
    private static class TitleLabel extends JLabel {

        TitleLabel(String text, int size) {
            super(text, SwingConstants.CENTER);
            setFont(new Font("Rockwell Extra Bold", Font.BOLD, size));
            setForeground(new Color(255, 215, 79));
            setBorder(new EmptyBorder(8, 12, 4, 12));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            FontMetrics metrics = g2.getFontMetrics(getFont());
            int x = (getWidth() - metrics.stringWidth(getText())) / 2;
            int y = (getHeight() + metrics.getAscent() - metrics.getDescent()) / 2;
            g2.setFont(getFont());
            g2.setColor(new Color(43, 28, 15));
            g2.drawString(getText(), x + 4, y + 5);
            g2.setColor(new Color(114, 63, 17));
            g2.drawString(getText(), x + 2, y + 2);
            g2.setColor(getForeground());
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }
}
