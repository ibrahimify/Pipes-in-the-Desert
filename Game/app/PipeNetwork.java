package app;

import java.util.ArrayList;
import java.util.List;

/**
 * PipeNetwork stores the full topology of the pipe system.
 *
 * <p>It manages the collection of pipes and active elements, validates
 * adjacency and connections, and supports structural modifications such as
 * connecting a free pipe end or inserting a pump into a pipe.</p>
 *
 * <p>According to the Analysis Model (version 2, section 4.3.5), PipeNetwork
 * maintains separate typed lists for each element category alongside a master
 * list of all elements. This allows efficient lookup by type (e.g.
 * {@link #getWorkingPumps()}, {@link #getSprings()}, {@link #getCisterns()})
 * while still supporting uniform operations over the full topology.</p>
 *
 * <p>This is a skeleton-level implementation. Methods print their own names,
 * print relevant state, and perform the minimal structural updates needed for
 * sequence-diagram verification and test-scenario execution.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 * @see NetworkElement
 * @see Pipe
 * @see Pump
 * @see Spring
 * @see Cistern
 */
public class PipeNetwork {

    // -----------------------------------------------------------------------
    // Attributes (Analysis Model 4.3.5)
    // -----------------------------------------------------------------------

    /** All elements in the network. */
    private List<NetworkElement> elements;

    /** All pipes in the network. */
    private List<Pipe> pipes;

    /** All pumps in the network. */
    private List<Pump> pumps;

    /** All springs in the network. */
    private List<Spring> springs;

    /** All cisterns in the network. */
    private List<Cistern> cisterns;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates an empty PipeNetwork with no elements.
     */
    public PipeNetwork() {
        this.elements = new ArrayList<>();
        this.pipes    = new ArrayList<>();
        this.pumps    = new ArrayList<>();
        this.springs  = new ArrayList<>();
        this.cisterns = new ArrayList<>();
        java.lang.System.out.println("[PipeNetwork] Created empty network.");
    }

    // -----------------------------------------------------------------------
    // Element management
    // -----------------------------------------------------------------------

    /**
     * Adds an element to the network.
     *
     * <p>The element is placed in the master list and in the appropriate typed
     * list based on its runtime class.</p>
     *
     * @param element the {@link NetworkElement} to add; ignored if {@code null}
     */
    public void addElement(NetworkElement element) {
        java.lang.System.out.println("[PipeNetwork] addElement() called.");

        if (element == null) {
            java.lang.System.out.println("[PipeNetwork] addElement() – element is null, skipping.");
            return;
        }

        elements.add(element);

        if (element instanceof Pipe) {
            pipes.add((Pipe) element);
        } else if (element instanceof Pump) {
            pumps.add((Pump) element);
        } else if (element instanceof Spring) {
            springs.add((Spring) element);
        } else if (element instanceof Cistern) {
            cisterns.add((Cistern) element);
        }

        java.lang.System.out.println("[PipeNetwork] Element added: "
                + element.getClass().getSimpleName() + " (id: " + element.getId() + ")."
                + " Total elements: " + elements.size());
    }

    /**
     * Removes an element from the network.
     *
     * @param element the {@link NetworkElement} to remove; ignored if {@code null}
     */
    public void removeElement(NetworkElement element) {
        java.lang.System.out.println("[PipeNetwork] removeElement() called.");

        if (element == null) {
            java.lang.System.out.println("[PipeNetwork] removeElement() – element is null, skipping.");
            return;
        }

        elements.remove(element);

        if (element instanceof Pipe) {
            pipes.remove(element);
        } else if (element instanceof Pump) {
            pumps.remove(element);
        } else if (element instanceof Spring) {
            springs.remove(element);
        } else if (element instanceof Cistern) {
            cisterns.remove(element);
        }

        java.lang.System.out.println("[PipeNetwork] Element removed: "
                + element.getClass().getSimpleName() + " (id: " + element.getId() + ")."
                + " Total elements: " + elements.size());
    }

    // -----------------------------------------------------------------------
    // Adjacency
    // -----------------------------------------------------------------------

    /**
     * Checks whether two elements are directly connected in the network.
     *
     * <p>Two elements are considered adjacent if one appears in the other's
     * neighbor list.</p>
     *
     * @param a the first element
     * @param b the second element
     * @return {@code true} if the elements are adjacent; {@code false} otherwise
     */
    public boolean areAdjacent(NetworkElement a, NetworkElement b) {
        java.lang.System.out.println("[PipeNetwork] areAdjacent() called.");

        if (a == null || b == null) {
            java.lang.System.out.println("[PipeNetwork] areAdjacent() – one or both elements are null.");
            return false;
        }

        boolean result = a.isAdjacentTo(b);
        java.lang.System.out.println("[PipeNetwork] areAdjacent() = " + result);
        return result;
    }

    /**
     * Returns the neighboring elements of the given element.
     *
     * @param element the element whose neighbors to return
     * @return the neighbor list, or an empty list if the element is {@code null}
     */
    public List<NetworkElement> getNeighbors(NetworkElement element) {
        java.lang.System.out.println("[PipeNetwork] getNeighbors() called.");

        if (element == null) {
            return new ArrayList<>();
        }

        return element.getNeighbors();
    }

    // -----------------------------------------------------------------------
    // Connection management
    // -----------------------------------------------------------------------

    /**
     * Creates a bidirectional connection between two network elements.
     *
     * <p>Each element is added to the other's neighbor list. This is the
     * standard way to wire up the initial network topology and to connect
     * new pipes placed by plumbers.</p>
     *
     * @param a the first element
     * @param b the second element
     */
    public void connectElements(NetworkElement a, NetworkElement b) {
        java.lang.System.out.println("[PipeNetwork] connectElements() called.");

        if (a == null || b == null) {
            java.lang.System.out.println("[PipeNetwork] connectElements() – one or both elements are null, skipping.");
            return;
        }

        a.addNeighbor(b);
        b.addNeighbor(a);

        java.lang.System.out.println("[PipeNetwork] Connected "
                + a.getClass().getSimpleName() + " (id: " + a.getId() + ") <-> "
                + b.getClass().getSimpleName() + " (id: " + b.getId() + ").");
    }

    /**
     * Removes the bidirectional connection between two network elements.
     *
     * <p>Each element is removed from the other's neighbor list.</p>
     *
     * @param a the first element
     * @param b the second element
     */
    public void disconnectElements(NetworkElement a, NetworkElement b) {
        java.lang.System.out.println("[PipeNetwork] disconnectElements() called.");

        if (a == null || b == null) {
            java.lang.System.out.println("[PipeNetwork] disconnectElements() – one or both elements are null, skipping.");
            return;
        }

        a.removeNeighbor(b);
        b.removeNeighbor(a);

        java.lang.System.out.println("[PipeNetwork] Disconnected "
                + a.getClass().getSimpleName() + " (id: " + a.getId() + ") <-> "
                + b.getClass().getSimpleName() + " (id: " + b.getId() + ").");
    }

    // -----------------------------------------------------------------------
    // Pump insertion
    // -----------------------------------------------------------------------

    /**
     * Inserts a pump into an existing pipe by splitting the pipe into two
     * segments.
     *
     * <p>According to the Insert New Pump sequence diagram, this method:</p>
     * <ol>
     *   <li>Removes the target pipe from the network.</li>
     *   <li>Disconnects the target pipe from its neighbors.</li>
     *   <li>Creates two new pipe segments to replace the original.</li>
     *   <li>Connects the first segment to the pump and the second segment
     *       to the pump.</li>
     *   <li>Reconnects the original neighbors to the new segments.</li>
     *   <li>Registers the new pipes on the pump.</li>
     * </ol>
     *
     * <p>At skeleton level the two new pipe segments are created with
     * auto-generated IDs based on the target pipe's ID.</p>
     *
     * @param targetPipe the {@link Pipe} to split
     * @param pump       the {@link Pump} to insert between the two new segments
     */
    public void insertPump(Pipe targetPipe, Pump pump) {
        java.lang.System.out.println("[PipeNetwork] insertPump() called.");

        if (targetPipe == null || pump == null) {
            java.lang.System.out.println("[PipeNetwork] insertPump() – targetPipe or pump is null, skipping.");
            return;
        }

        // Capture the original neighbors before removing connections
        List<NetworkElement> originalNeighbors = new ArrayList<>(targetPipe.getNeighbors());


       // Relocate any player standing on the target pipe to the new pump
if (targetPipe instanceof Pipe) {
    Player occupant = ((Pipe) targetPipe).getOccupant();
    if (occupant != null) {
        occupant.setPosition(pump);
        ((Pipe) targetPipe).clearOccupant();
        java.lang.System.out.println("[PipeNetwork] Relocated player "
                + occupant.getName() + " from split pipe to Pump:" + pump.getId());
    }
}



        // Step 1: Remove the target pipe from the network
        removeElement(targetPipe);

        // Step 2: Disconnect the target pipe from all its neighbors
        for (NetworkElement neighbor : originalNeighbors) {
            targetPipe.removeNeighbor(neighbor);
            neighbor.removeNeighbor(targetPipe);
        }

        // Step 3: Create two new pipe segments
        int baseId = targetPipe.getId();
        Pipe segment1 = new Pipe(baseId * 100 + 1, targetPipe.getCapacity());
        Pipe segment2 = new Pipe(baseId * 100 + 2, targetPipe.getCapacity());

        // Step 4: Add the new elements to the network
        addElement(pump);
        addElement(segment1);
        addElement(segment2);

        // Step 5: Connect segments to the pump
        connectElements(segment1, pump);
        connectElements(pump, segment2);

        // Step 6: Reconnect original neighbors to the new segments
        if (originalNeighbors.size() >= 1) {
            connectElements(originalNeighbors.get(0), segment1);
        }
        if (originalNeighbors.size() >= 2) {
            connectElements(originalNeighbors.get(1), segment2);
        }

        // Step 7: Register pipes on the pump
        pump.addConnectedPipe(segment1);
        pump.addConnectedPipe(segment2);


        pump.setDirection(segment1, segment2);


        java.lang.System.out.println("[PipeNetwork] Pump inserted successfully. "
                + "Original pipe (id: " + baseId + ") split into segments "
                + segment1.getId() + " and " + segment2.getId() + ".");





                // Update spring output pipe if the split pipe was its output
         for (Spring s : springs) {
          if (s.getOutputPipe() == targetPipe) {
        s.setOutputPipe(segment1);
        java.lang.System.out.println("[PipeNetwork] Updated Spring output pipe to Pipe:" + segment1.getId());

        }
}
    }

    // -----------------------------------------------------------------------
    // Typed queries
    // -----------------------------------------------------------------------

    /**
     * Returns the pumps currently in working state.
     *
     * <p>Used by {@code System.updateRoundEvents()} for the Randomly Break
     * Pump use case.</p>
     *
     * @return a list of non-broken pumps; may be empty, never {@code null}
     */
    public List<Pump> getWorkingPumps() {
        java.lang.System.out.println("[PipeNetwork] getWorkingPumps() called.");

        List<Pump> working = new ArrayList<>();
        for (Pump pump : pumps) {
            if (!pump.isBroken()) {
                working.add(pump);
            }
        }

        java.lang.System.out.println("[PipeNetwork] Working pumps: " + working.size()
                + " out of " + pumps.size() + " total.");
        return working;
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    /**
     * Returns all elements in the network.
     *
     * @return the master element list
     */
    public List<NetworkElement> getElements() {
        return elements;
    }

    /**
     * Returns all pipes in the network.
     *
     * @return the pipe list
     */
    public List<Pipe> getPipes() {
        return pipes;
    }

    /**
     * Returns all pumps in the network.
     *
     * @return the pump list
     */
    public List<Pump> getPumps() {
        return pumps;
    }

    /**
     * Returns all springs in the network.
     *
     * @return the spring list
     */
    public List<Spring> getSprings() {
        return springs;
    }

    /**
     * Returns all cisterns in the network.
     *
     * @return the cistern list
     */
    public List<Cistern> getCisterns() {
        return cisterns;
    }

       // yahya edit , add id generator to avoid duplicate id issues when adding elements to the network
    private int nextId = 1;

    public int generateId() {
       return nextId++;
     }







}