package app;

import java.util.Arrays;
import java.util.Scanner;

/**
 * Dependency-free self-test harness for the prototype program.
 */
public class PrototypeTests {

    private int passed;
    private int failed;

    private PipeNetwork network;
    private Spring spring;
    private Cistern cistern;
    private Pump pump;
    private Pipe pipe1;
    private Pipe pipe2;
    private ScoreBoard scoreBoard;
    private WaterFlowManager flow;

    public static void main(String[] args) {
        PrototypeTests tests = new PrototypeTests();
        tests.runAll();
    }

    private void runAll() {
        runDeliveredWaterScoresForPlumbers();
        runPuncturedPipeScoresForSaboteurs();
        runFreePipeEndScoresForSaboteurs();
        runBrokenPumpScoresForSaboteurs();
        runWrongPumpDirectionScoresForSaboteurs();
        runPlayersCannotMoveToSpringOrCistern();
        runPipeOccupancyIsExclusive();
        runPumpAllowsMultiplePlayers();
        runComponentGenerationCreatesInventory();
        runPlumberInsertsPumpIntoPipe();

        java.lang.System.out.println();
        java.lang.System.out.println("Prototype tests: " + passed + " passed, " + failed + " failed.");
        if (failed > 0) {
            throw new AssertionError("Prototype test suite failed.");
        }
    }

    private void runDeliveredWaterScoresForPlumbers() {
        try {
            createLinearFixture();
            flow.recalculateFlow();
            assertEquals(10, scoreBoard.getPlumberScore(), "plumber score");
            assertEquals(0, scoreBoard.getSaboteurScore(), "saboteur score");
            assertEquals(10, cistern.getStoredWater(), "stored water");
            pass("water reaches cistern and increases plumber score");
        } catch (Throwable t) {
            fail("water reaches cistern and increases plumber score", t);
        }
    }

    private void runPuncturedPipeScoresForSaboteurs() {
        try {
            createLinearFixture();
            pipe1.puncture();
            flow.recalculateFlow();
            assertEquals(0, scoreBoard.getPlumberScore(), "plumber score");
            assertEquals(10, scoreBoard.getSaboteurScore(), "saboteur score");
            pass("punctured pipe leaks and increases saboteur score");
        } catch (Throwable t) {
            fail("punctured pipe leaks and increases saboteur score", t);
        }
    }

    private void runFreePipeEndScoresForSaboteurs() {
        try {
            createLinearFixture();
            pipe2.disconnectEnd();
            flow.recalculateFlow();
            assertEquals(0, scoreBoard.getPlumberScore(), "plumber score");
            assertEquals(10, scoreBoard.getSaboteurScore(), "saboteur score");
            pass("free pipe end leaks water");
        } catch (Throwable t) {
            fail("free pipe end leaks water", t);
        }
    }

    private void runBrokenPumpScoresForSaboteurs() {
        try {
            createLinearFixture();
            pump.breakDown();
            flow.recalculateFlow();
            assertEquals(0, scoreBoard.getPlumberScore(), "plumber score");
            assertEquals(10, scoreBoard.getSaboteurScore(), "saboteur score");
            pass("broken pump blocks water");
        } catch (Throwable t) {
            fail("broken pump blocks water", t);
        }
    }

    private void runWrongPumpDirectionScoresForSaboteurs() {
        try {
            createLinearFixture();
            pump.setDirection(pipe2, pipe1);
            flow.recalculateFlow();
            assertEquals(0, scoreBoard.getPlumberScore(), "plumber score");
            assertEquals(10, scoreBoard.getSaboteurScore(), "saboteur score");
            pass("wrong pump direction blocks water");
        } catch (Throwable t) {
            fail("wrong pump direction blocks water", t);
        }
    }

    private void runPlayersCannotMoveToSpringOrCistern() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Player player = new Player("Ada", team, pump);
            assertFalse(player.moveTo(spring), "spring movement rejected");
            assertFalse(player.moveTo(cistern), "cistern movement rejected");
            pass("players move only to pipes and pumps");
        } catch (Throwable t) {
            fail("players move only to pipes and pumps", t);
        }
    }

    private void runPipeOccupancyIsExclusive() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Player first = new Player("Ada", team, pump);
            Player second = new Player("Grace", team, pump);
            assertTrue(first.moveTo(pipe1), "first player moves to pipe");
            assertFalse(second.moveTo(pipe1), "second player cannot enter occupied pipe");
            pass("only one player can occupy a pipe");
        } catch (Throwable t) {
            fail("only one player can occupy a pipe", t);
        }
    }

    private void runPumpAllowsMultiplePlayers() {
        try {
            createLinearFixture();
            Team team = new Team("Saboteurs");
            Player first = new Player("Linus", team, pipe1);
            Player second = new Player("Ken", team, pipe2);
            assertTrue(first.moveTo(pump), "first player moves to pump");
            assertTrue(second.moveTo(pump), "second player moves to same pump");
            pass("multiple players can stand on pump");
        } catch (Throwable t) {
            fail("multiple players can stand on pump", t);
        }
    }

    private void runComponentGenerationCreatesInventory() {
        try {
            createLinearFixture();
            System system = new System(Arrays.asList(new Team("Plumbers"), new Team("Saboteurs")),
                    network, new GameTimer(1), scoreBoard);
            system.setInputScanner(new Scanner("yes\nyes\n"));
            system.requestComponentGeneration();
            assertEquals(1, cistern.getAvailablePipes(), "generated pipe count");
            assertEquals(1, cistern.getAvailablePumps(), "generated pump count");
            pass("cistern generation prompts create spare components");
        } catch (Throwable t) {
            fail("cistern generation prompts create spare components", t);
        }
    }

    private void runPlumberInsertsPumpIntoPipe() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Plumber plumber = new Plumber("Ada", team, pump);
            cistern.generatePump();
            plumber.collectPumpFromCistern(cistern);

            Pump newPump = new Pump(50);
            plumber.insertPump(pipe2, newPump, network);

            assertFalse(network.getPipes().contains(pipe2), "original pipe removed");
            assertTrue(network.getPumps().contains(newPump), "new pump added");
            assertEquals(2, newPump.getConnectedPipes().size(), "new pump connected pipes");
            assertFalse(plumber.isCarryingPump(), "plumber no longer carries pump");
            pass("plumber can collect and insert pump into a pipe");
        } catch (Throwable t) {
            fail("plumber can collect and insert pump into a pipe", t);
        }
    }

    private void createLinearFixture() {
        network = new PipeNetwork();
        spring = new Spring(1);
        cistern = new Cistern(2);
        pump = new Pump(3);
        pipe1 = new Pipe(4);
        pipe2 = new Pipe(5);
        scoreBoard = new ScoreBoard(new Team("Plumbers"), new Team("Saboteurs"));
        flow = new WaterFlowManager(network, scoreBoard);

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
        pump.setDirection(pipe1, pipe2);
    }

    private void pass(String name) {
        passed++;
        java.lang.System.out.println("[PASS] " + name);
    }

    private void fail(String name, Throwable t) {
        failed++;
        java.lang.System.out.println("[FAIL] " + name + " - " + t.getMessage());
    }

    private void assertTrue(boolean value, String label) {
        if (!value) {
            throw new AssertionError(label + " expected true");
        }
    }

    private void assertFalse(boolean value, String label) {
        if (value) {
            throw new AssertionError(label + " expected false");
        }
    }

    private void assertEquals(int expected, int actual, String label) {
        if (expected != actual) {
            throw new AssertionError(label + " expected " + expected + " but was " + actual);
        }
    }
}
