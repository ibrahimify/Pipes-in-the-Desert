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
        runPumpOccupantListIsUpdated();
        runFullOutputPipeBlocksTransferWithoutLeak();
        runPumpBuffersOverflowWater();
        runFreeEndConnectionUpdatesPumpMetadata();
        runFreeEndConnectionRejectsParallelRoute();
        runCisternManualGenerationIncrementsInventory();
        runPlumberCannotCollectGeneratedPipeFromTwoLinksAway();
        runPlumberCollectsGeneratedPipeFromReachableCistern();
        runPlumberCollectsGeneratedPumpFromReachableCistern();
        runPlumberCannotPlacePipeToRemoteEndpoint();
        runPlumberCanPlacePipeBetweenLocalEndpoints();
        runPlumberCannotPlacePipeBetweenAlreadyConnectedEndpoints();
        runPlumberCanPlacePipeWithFreeEnd();
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

    private void runPumpOccupantListIsUpdated() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Player first = new Player("Ada", team, pump);
            Player second = new Player("Grace", team, pump);
            assertEquals(2, pump.getOccupants().size(), "pump occupants after placement");
            first.moveTo(pipe1);
            assertEquals(1, pump.getOccupants().size(), "pump occupants after player leaves");
            assertTrue(pump.getOccupants().contains(second), "remaining player stays on pump");
            pass("pump occupant list tracks players on pumps");
        } catch (Throwable t) {
            fail("pump occupant list tracks players on pumps", t);
        }
    }

    private void runFullOutputPipeBlocksTransferWithoutLeak() {
        try {
            createLinearFixture();
            pipe2.setCurrentWater(pipe2.getCapacity());
            flow.recalculateFlow();
            assertEquals(0, scoreBoard.getPlumberScore(), "plumber score");
            assertEquals(0, scoreBoard.getSaboteurScore(), "saboteur score");
            assertEquals(10, pipe1.getCurrentWater(), "input pipe keeps water");
            assertEquals(pipe2.getCapacity(), pipe2.getCurrentWater(), "output pipe remains full");
            pass("full output pipe blocks pump transfer without immediate leak");
        } catch (Throwable t) {
            fail("full output pipe blocks pump transfer without immediate leak", t);
        }
    }

    private void runPumpBuffersOverflowWater() {
        try {
            createLinearFixture();
            pipe2.setCurrentWater(5);
            flow.recalculateFlow();
            assertEquals(10, scoreBoard.getPlumberScore(), "plumber score");
            assertEquals(0, scoreBoard.getSaboteurScore(), "saboteur score");
            assertEquals(5, pump.getBuffer(), "pump buffer");
            assertEquals(0, pipe1.getCurrentWater(), "input pipe drained");
            assertEquals(0, pipe2.getCurrentWater(), "output pipe delivered to cistern");
            pass("pump buffer stores water that cannot fit into output pipe");
        } catch (Throwable t) {
            fail("pump buffer stores water that cannot fit into output pipe", t);
        }
    }

    private void runFreeEndConnectionUpdatesPumpMetadata() {
        try {
            createLinearFixture();
            Pipe spare = new Pipe(99);
            spare.disconnectEnd();
            assertTrue(spare.connectFreeEnd(pump), "free end connects to pump");
            assertFalse(spare.hasFreeEnd(), "pipe no longer has free end");
            assertTrue(pump.getConnectedPipes().contains(spare), "pump knows connected pipe");
            assertTrue(pump.isAdjacentTo(spare), "pump adjacency updated");
            pass("free-end connection keeps pump metadata consistent");
        } catch (Throwable t) {
            fail("free-end connection keeps pump metadata consistent", t);
        }
    }

    private void runFreeEndConnectionRejectsParallelRoute() {
        try {
            createLinearFixture();
            Pipe spare = new Pipe(100);
            network.addElement(spare);
            network.connectElements(spare, pump);
            spare.disconnectEnd();

            assertFalse(spare.connectFreeEnd(cistern), "parallel free-end route is rejected");
            assertTrue(spare.hasFreeEnd(), "rejected pipe keeps free end");
            assertFalse(spare.isAdjacentTo(cistern), "rejected pipe is not connected to target");
            pass("free-end connection rejects duplicate parallel route");
        } catch (Throwable t) {
            fail("free-end connection rejects duplicate parallel route", t);
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

    private void runCisternManualGenerationIncrementsInventory() {
        try {
            createLinearFixture();
            cistern.generatePipe();
            cistern.generatePump();
            assertEquals(1, cistern.getAvailablePipes(), "manual pipe generation count");
            assertEquals(1, cistern.getAvailablePumps(), "manual pump generation count");
            pass("manual cistern generation increments spare inventory");
        } catch (Throwable t) {
            fail("manual cistern generation increments spare inventory", t);
        }
    }

    private void runPlumberCollectsGeneratedPipeFromReachableCistern() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Plumber plumber = new Plumber("Ada", team, pipe2);
            cistern.generatePipe();
            plumber.collectPipeFromCistern(cistern);
            assertTrue(plumber.isCarryingPipe(), "plumber carrying pipe");
            assertEquals(0, cistern.getAvailablePipes(), "pipe inventory consumed");
            pass("plumber can collect generated pipe from reachable cistern");
        } catch (Throwable t) {
            fail("plumber can collect generated pipe from reachable cistern", t);
        }
    }

    private void runPlumberCannotCollectGeneratedPipeFromTwoLinksAway() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Plumber plumber = new Plumber("Ada", team, pump);
            cistern.generatePipe();
            plumber.collectPipeFromCistern(cistern);
            assertFalse(plumber.isCarryingPipe(), "plumber cannot collect pipe from two links away");
            assertEquals(1, cistern.getAvailablePipes(), "pipe inventory remains");
            pass("plumber cannot collect generated pipe from two links away");
        } catch (Throwable t) {
            fail("plumber cannot collect generated pipe from two links away", t);
        }
    }

    private void runPlumberCollectsGeneratedPumpFromReachableCistern() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Plumber plumber = new Plumber("Ada", team, pipe2);
            cistern.generatePump();
            plumber.collectPumpFromCistern(cistern);
            assertTrue(plumber.isCarryingPump(), "plumber carrying pump");
            assertEquals(0, cistern.getAvailablePumps(), "pump inventory consumed");
            pass("plumber can collect generated pump from reachable cistern");
        } catch (Throwable t) {
            fail("plumber can collect generated pump from reachable cistern", t);
        }
    }

    private void runPlumberCannotPlacePipeToRemoteEndpoint() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Plumber plumber = new Plumber("Ada", team, pipe2);
            cistern.generatePipe();
            plumber.collectPipeFromCistern(cistern);
            plumber.startTurn();

            Pipe remotePipe = new Pipe(60);
            plumber.placeNewPipe(remotePipe, pump, spring, network);

            assertFalse(network.getPipes().contains(remotePipe), "remote endpoint pipe was rejected");
            assertTrue(plumber.isCarryingPipe(), "plumber still carries rejected pipe");
            pass("plumber cannot place pipe to remote endpoint");
        } catch (Throwable t) {
            fail("plumber cannot place pipe to remote endpoint", t);
        }
    }

    private void runPlumberCanPlacePipeBetweenLocalEndpoints() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Plumber plumber = new Plumber("Ada", team, pipe2);
            cistern.generatePipe();
            plumber.collectPipeFromCistern(cistern);
            plumber.startTurn();

            Pipe localPipe = new Pipe(61);
            plumber.placeNewPipe(localPipe, pump, cistern, network);

            assertTrue(network.getPipes().contains(localPipe), "local pipe was added");
            assertFalse(plumber.isCarryingPipe(), "plumber used carried pipe");
            pass("plumber can place pipe between local endpoints");
        } catch (Throwable t) {
            fail("plumber can place pipe between local endpoints", t);
        }
    }

    private void runPlumberCannotPlacePipeBetweenAlreadyConnectedEndpoints() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Plumber plumber = new Plumber("Ada", team, pipe2);
            cistern.generatePipe();
            plumber.collectPipeFromCistern(cistern);
            plumber.startTurn();

            Pipe duplicatePipe = new Pipe(62);
            plumber.placeNewPipe(duplicatePipe, pump, pipe2, network);

            assertFalse(network.getPipes().contains(duplicatePipe), "duplicate pipe was rejected");
            assertTrue(plumber.isCarryingPipe(), "plumber still carries rejected duplicate");
            pass("plumber cannot place pipe between already connected endpoints");
        } catch (Throwable t) {
            fail("plumber cannot place pipe between already connected endpoints", t);
        }
    }

    private void runPlumberCanPlacePipeWithFreeEnd() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Plumber plumber = new Plumber("Ada", team, pipe2);
            cistern.generatePipe();
            plumber.collectPipeFromCistern(cistern);
            plumber.startTurn();

            Pipe freePipe = new Pipe(63);
            plumber.placeNewPipe(freePipe, pump, network);

            assertTrue(network.getPipes().contains(freePipe), "free-end pipe was added");
            assertTrue(network.areAdjacent(freePipe, pump), "free-end pipe connected to endpoint");
            assertTrue(freePipe.hasFreeEnd(), "free-end pipe kept one loose end");
            assertFalse(plumber.isCarryingPipe(), "plumber used carried pipe");
            pass("plumber can place pipe with a free end");
        } catch (Throwable t) {
            fail("plumber can place pipe with a free end", t);
        }
    }

    private void runPlumberInsertsPumpIntoPipe() {
        try {
            createLinearFixture();
            Team team = new Team("Plumbers");
            Plumber plumber = new Plumber("Ada", team, pipe2);
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
