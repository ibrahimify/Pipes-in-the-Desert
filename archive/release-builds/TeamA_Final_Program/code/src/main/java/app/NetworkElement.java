package app; 

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract base class for all physical elements in the pipe network.
 * Pipes, pumps, springs, and cisterns inherit from this class.
 * The class stores a unique identifier and the directly connected neighbors.
 */
public abstract class NetworkElement {

    /**
     * Unique identifier of the network element.
     */
    protected int id;

    /**
     * Directly connected neighboring network elements.
     */
    protected List<NetworkElement> neighbors;

    /**
     * Creates a network element with the given identifier.
     *
     * @param id unique identifier of the element
     */
    public NetworkElement(int id) {
        this.id = id;
        this.neighbors = new ArrayList<>();
        java.lang.System.out.println("[NetworkElement] Created element with id: " + id);
    }

    /**
     * Adds a neighboring element if the connection is valid and not already present.
     *
     * @param neighbor element to add as neighbor
     * @return true if the neighbor was added successfully
     */
    public boolean addNeighbor(NetworkElement neighbor) {
        java.lang.System.out.println("[NetworkElement:" + id + "] addNeighbor() called.");

        if (neighbor == null) {
            java.lang.System.out.println("[NetworkElement:" + id + "] Cannot add null neighbor.");
            return false;
        }

        if (neighbor == this) {
            java.lang.System.out.println("[NetworkElement:" + id + "] Cannot add itself as neighbor.");
            return false;
        }

        if (neighbors.contains(neighbor)) {
            java.lang.System.out.println("[NetworkElement:" + id + "] Neighbor already exists: " + neighbor.getId());
            return false;
        }

        neighbors.add(neighbor);
        java.lang.System.out.println("[NetworkElement:" + id + "] Neighbor added: " + neighbor.getId());
        return true;
    }

    /**
     * Removes a neighboring element.
     *
     * @param neighbor element to remove
     * @return true if the neighbor was removed successfully
     */
    public boolean removeNeighbor(NetworkElement neighbor) {
        java.lang.System.out.println("[NetworkElement:" + id + "] removeNeighbor() called.");

        if (neighbor == null) {
            java.lang.System.out.println("[NetworkElement:" + id + "] Cannot remove null neighbor.");
            return false;
        }

        boolean removed = neighbors.remove(neighbor);

        if (removed) {
            java.lang.System.out.println("[NetworkElement:" + id + "] Neighbor removed: " + neighbor.getId());
        } else {
            java.lang.System.out.println("[NetworkElement:" + id + "] Neighbor not found: " + neighbor.getId());
        }

        return removed;
    }

    /**
     * Returns the directly connected neighbors.
     *
     * @return unmodifiable list of neighboring elements
     */
    public List<NetworkElement> getNeighbors() {
        java.lang.System.out.println("[NetworkElement:" + id + "] getNeighbors() called.");
        return Collections.unmodifiableList(neighbors);
    }

    /**
     * Checks whether this element is directly adjacent to another element.
     *
     * @param element target element
     * @return true if the target is a direct neighbor
     */
    public boolean isAdjacentTo(NetworkElement element) {
        java.lang.System.out.println("[NetworkElement:" + id + "] isAdjacentTo() called.");

        if (element == null) {
            return false;
        }

        boolean adjacent = neighbors.contains(element);
        java.lang.System.out.println("[NetworkElement:" + id + "] Adjacent to " + element.getId() + ": " + adjacent);
        return adjacent;
    }

    /**
     * Returns the identifier of this element.
     *
     * @return element identifier
     */
    public int getId() {
        return id;
    }

    /**
     * Returns a readable text form of the element.
     *
     * @return class name and identifier
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "#" + id;
    }
}