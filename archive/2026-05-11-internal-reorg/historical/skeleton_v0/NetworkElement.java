package app;

import java.util.ArrayList;
import java.util.List;


/**
 * NetworkElement is the abstract base class representing all physical elements
 * in the pipe network (Pipe, Pump, Spring, Cistern).
 *
 * <p>It provides shared connectivity behavior, allowing elements to be linked
 * together and queried for adjacency relationships.</p>
 *
 * <p>According to the Analysis Model (4.3.3), all network elements maintain
 * a list of neighboring elements and support basic connection operations.</p>
 *
 * <p>This is a skeleton-level implementation. Methods print their names and
 * perform minimal logic required to support structure verification.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 */
public abstract class NetworkElement {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /**
     * Unique identifier of this network element.
     */
    protected int id;

    /**
     * List of directly connected neighboring elements.
     */
    protected List<NetworkElement> neighbors;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates a NetworkElement with a given ID.
     *
     * @param id unique identifier of the element
     */
    public NetworkElement(int id) {
        this.id = id;
        this.neighbors = new ArrayList<>();
        java.lang.System.out.println("[NetworkElement] Created element with id: " + id);
    }

    // -----------------------------------------------------------------------
    // Connectivity methods
    // -----------------------------------------------------------------------

    /**
     * Adds a neighbor to this element.
     *
     * @param element the neighbor to add
     */
    public void addNeighbor(NetworkElement element) {
        java.lang.System.out.println("[NetworkElement:" + id + "] addNeighbor() called.");

        if (element == null) {
            java.lang.System.out.println("[NetworkElement:" + id + "] Cannot add null neighbor.");
            return;
        }

        neighbors.add(element);
        java.lang.System.out.println("[NetworkElement:" + id + "] Neighbor added: "
                + element.getId());
    }

    /**
     * Removes a neighbor from this element.
     *
     * @param element the neighbor to remove
     */
    public void removeNeighbor(NetworkElement element) {
        java.lang.System.out.println("[NetworkElement:" + id + "] removeNeighbor() called.");

        if (element == null) {
            java.lang.System.out.println("[NetworkElement:" + id + "] Cannot remove null neighbor.");
            return;
        }

        neighbors.remove(element);
        java.lang.System.out.println("[NetworkElement:" + id + "] Neighbor removed: "
                + element.getId());
    }

    /**
     * Returns the list of neighboring elements.
     *
     * @return list of neighbors
     */
    public List<NetworkElement> getNeighbors() {
        java.lang.System.out.println("[NetworkElement:" + id + "] getNeighbors() called.");
        return neighbors;
    }

    /**
     * Checks whether this element is adjacent to another element.
     *
     * @param element the element to check adjacency with
     * @return true if adjacent, false otherwise
     */
    public boolean isAdjacentTo(NetworkElement element) {
        java.lang.System.out.println("[NetworkElement:" + id + "] isAdjacentTo() called.");

        if (element == null) {
            return false;
        }

        boolean result = neighbors.contains(element);
        java.lang.System.out.println("[NetworkElement:" + id + "] Adjacent to "
                + element.getId() + ": " + result);

        return result;
    }

    // -----------------------------------------------------------------------
    // Getters
    // -----------------------------------------------------------------------

    /**
     * Returns the ID of this element.
     *
     * @return element ID
     */
    public int getId() {
        return id;
    }



    
}