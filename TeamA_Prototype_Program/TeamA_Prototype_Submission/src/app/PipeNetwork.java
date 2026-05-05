package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Stores and modifies the complete pipe-system topology.
 *
 * <p>The network owns every physical element in the game: pipes, pumps,
 * springs, and cisterns. It keeps typed lists for convenient game logic and a
 * master list for generic traversal. This class is responsible for keeping
 * connections consistent in both directions.</p>
 */
public class PipeNetwork {

    /** All physical elements in the network. */
    private final List<NetworkElement> elements;

    /** All pipes in the network. */
    private final List<Pipe> pipes;

    /** All pumps in the network. */
    private final List<Pump> pumps;

    /** All springs in the network. */
    private final List<Spring> springs;

    /** All cisterns in the network. */
    private final List<Cistern> cisterns;

    /** Next candidate identifier for newly created network elements. */
    private int nextId;

    /**
     * Creates an empty pipe network.
     */
    public PipeNetwork() {
        this.elements = new ArrayList<>();
        this.pipes = new ArrayList<>();
        this.pumps = new ArrayList<>();
        this.springs = new ArrayList<>();
        this.cisterns = new ArrayList<>();
        this.nextId = 1;
    }

    /**
     * Adds an element to the network if it is not already present.
     *
     * @param element element to register
     */
    public void addElement(NetworkElement element) {
        if (element == null || elements.contains(element)) {
            return;
        }

        elements.add(element);
        nextId = Math.max(nextId, element.getId() + 1);

        if (element instanceof Pipe) {
            pipes.add((Pipe) element);
        } else if (element instanceof Pump) {
            pumps.add((Pump) element);
        } else if (element instanceof Spring) {
            springs.add((Spring) element);
        } else if (element instanceof Cistern) {
            cisterns.add((Cistern) element);
        }
    }

    /**
     * Removes an element and all connections pointing to it.
     *
     * @param element element to remove
     */
    public void removeElement(NetworkElement element) {
        if (element == null || !elements.contains(element)) {
            return;
        }

        List<NetworkElement> neighbors = new ArrayList<>(element.getNeighbors());
        for (NetworkElement neighbor : neighbors) {
            disconnectElements(element, neighbor);
        }

        elements.remove(element);
        pipes.remove(element);
        pumps.remove(element);
        springs.remove(element);
        cisterns.remove(element);
    }

    /**
     * Checks direct adjacency between two elements.
     *
     * @param a first element
     * @param b second element
     * @return true when the two elements are directly connected
     */
    public boolean areAdjacent(NetworkElement a, NetworkElement b) {
        return a != null && b != null && a.isAdjacentTo(b) && b.isAdjacentTo(a);
    }

    /**
     * Returns the neighbors of one element.
     *
     * @param element element whose neighbors are requested
     * @return copy of the neighbor list
     */
    public List<NetworkElement> getNeighbors(NetworkElement element) {
        if (element == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(element.getNeighbors());
    }

    /**
     * Creates a valid bidirectional connection between two elements.
     *
     * <p>Only meaningful pipe-system connections are accepted: a pipe may be
     * connected to a pump, spring, cistern, or another pipe. Pumps enforce their
     * own connection limit for attached pipes.</p>
     *
     * @param a first element
     * @param b second element
     */
    public void connectElements(NetworkElement a, NetworkElement b) {
        if (a == null || b == null || a == b || areAdjacent(a, b)) {
            return;
        }

        if (!isConnectionAllowed(a, b)) {
            return;
        }

        Pump pump = getPumpSide(a, b);
        Pipe pipe = getPipeSide(a, b);
        if (pump != null && pipe != null && !pump.getConnectedPipes().contains(pipe)) {
            if (!pump.canAcceptConnection()) {
                return;
            }
            pump.addConnectedPipe(pipe);
        }

        a.addNeighbor(b);
        b.addNeighbor(a);

        if (a instanceof Spring && b instanceof Pipe) {
            ((Spring) a).setOutputPipe((Pipe) b);
        } else if (b instanceof Spring && a instanceof Pipe) {
            ((Spring) b).setOutputPipe((Pipe) a);
        }
    }

    /**
     * Removes a bidirectional connection between two elements.
     *
     * @param a first element
     * @param b second element
     */
 public void disconnectElements(NetworkElement a, NetworkElement b) {
        if (a == null || b == null) {
            return;
        }

        a.removeNeighbor(b);
        b.removeNeighbor(a);

        Pump pump = getPumpSide(a, b);
        Pipe pipe = getPipeSide(a, b);
        if (pump != null && pipe != null) {
            pump.removeConnectedPipe(pipe);
        }

        // FIX: If a pipe is disconnected from something, it now has a free end!
        // BUG 1 FIX
        if (a instanceof Pipe) {
            ((Pipe) a).disconnectEnd();
        }
        if (b instanceof Pipe) {
            ((Pipe) b).disconnectEnd();
        }
    }

    /**
     * Inserts a pump into the middle of a pipe.
     *
     * <p>The old pipe is removed, two replacement pipe segments are created,
     * the original endpoints are reconnected to those segments, and the new
     * pump is placed between them. If a player stood on the old pipe, that
     * player is moved to the inserted pump.</p>
     *
     * @param targetPipe pipe to split
     * @param pump pump to insert
     */
    public void insertPump(Pipe targetPipe, Pump pump) {
        if (targetPipe == null || pump == null || !pipes.contains(targetPipe)) {
            return;
        }

        List<NetworkElement> oldNeighbors = new ArrayList<>(targetPipe.getNeighbors());
        Player occupant = targetPipe.getOccupant();
        int capacity = targetPipe.getCapacity();
        int water = targetPipe.getCurrentWater();

        removeElement(targetPipe);

        Pipe firstSegment = new Pipe(generateId(), capacity);
        Pipe secondSegment = new Pipe(generateId(), capacity);
        firstSegment.setCurrentWater(water / 2);
        secondSegment.setCurrentWater(water - firstSegment.getCurrentWater());

        addElement(pump);
        addElement(firstSegment);
        addElement(secondSegment);

        connectElements(firstSegment, pump);
        connectElements(pump, secondSegment);

        if (oldNeighbors.size() > 0) {
            connectElements(oldNeighbors.get(0), firstSegment);
        }
        if (oldNeighbors.size() > 1) {
            connectElements(oldNeighbors.get(1), secondSegment);
        }

        pump.setDirection(firstSegment, secondSegment);

        for (Spring spring : springs) {
            if (spring.getOutputPipe() == targetPipe) {
                spring.setOutputPipe(firstSegment);
            }
        }

        if (occupant != null) {
            occupant.setPosition(pump);
        }
    }

    /**
     * Finds an element by its identifier.
     *
     * @param id identifier to search
     * @return matching element, or null if no element has this id
     */
    public NetworkElement findElementById(int id) {
        for (NetworkElement element : elements) {
            if (element.getId() == id) {
                return element;
            }
        }
        return null;
    }

    /**
     * Generates an identifier that is not currently used in the network.
     *
     * @return unused positive identifier
     */
    public int generateId() {
        while (findElementById(nextId) != null) {
            nextId++;
        }
        return nextId++;
    }

    /**
     * Returns all working pumps.
     *
     * @return pumps that are not broken
     */
    public List<Pump> getWorkingPumps() {
        List<Pump> working = new ArrayList<>();
        for (Pump pump : pumps) {
            if (!pump.isBroken()) {
                working.add(pump);
            }
        }
        return working;
    }

    /**
     * Returns all elements in insertion order.
     *
     * @return read-only element list
     */
    public List<NetworkElement> getElements() {
        return Collections.unmodifiableList(elements);
    }

    /**
     * Returns all pipes.
     *
     * @return read-only pipe list
     */
    public List<Pipe> getPipes() {
        return Collections.unmodifiableList(pipes);
    }

    /**
     * Returns all pumps.
     *
     * @return read-only pump list
     */
    public List<Pump> getPumps() {
        return Collections.unmodifiableList(pumps);
    }

    /**
     * Returns all springs.
     *
     * @return read-only spring list
     */
    public List<Spring> getSprings() {
        return Collections.unmodifiableList(springs);
    }

    /**
     * Returns all cisterns.
     *
     * @return read-only cistern list
     */
    public List<Cistern> getCisterns() {
        return Collections.unmodifiableList(cisterns);
    }

    /**
     * Checks if two elements may be connected in this game model.
     *
     * @param a first element
     * @param b second element
     * @return true if the connection is meaningful
     */
    private boolean isConnectionAllowed(NetworkElement a, NetworkElement b) {
        return a instanceof Pipe
                || b instanceof Pipe;
    }

    /**
     * Returns the pump side of a pump-pipe pair.
     *
     * @param a first element
     * @param b second element
     * @return pump side, or null when no pump is present
     */
    private Pump getPumpSide(NetworkElement a, NetworkElement b) {
        if (a instanceof Pump) {
            return (Pump) a;
        }
        if (b instanceof Pump) {
            return (Pump) b;
        }
        return null;
    }

    /**
     * Returns the pipe side of a pump-pipe pair.
     *
     * @param a first element
     * @param b second element
     * @return pipe side, or null when no pipe is present
     */
    private Pipe getPipeSide(NetworkElement a, NetworkElement b) {
        if (a instanceof Pipe) {
            return (Pipe) a;
        }
        if (b instanceof Pipe) {
            return (Pipe) b;
        }
        return null;
    }
}
