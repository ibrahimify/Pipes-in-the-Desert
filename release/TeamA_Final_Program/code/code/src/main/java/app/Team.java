package app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents one competing team in the Pipes in the Desert game.
 *
 * <p>A team groups players who share the same objective. The Plumber team tries
 * to maximize delivered water, while the Saboteur team tries to maximize leaked
 * water. Score handling is performed by {@link ScoreBoard}; this class only
 * manages team membership and team identity.</p>
 */
public class Team {

    /**
     * Display name of the team, for example "Plumbers" or "Saboteurs".
     */
    private final String name;

    /**
     * Players belonging to this team.
     */
    private final List<Player> members;

    /**
     * Creates a team with the given display name.
     *
     * @param name team name
     */
    public Team(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Team name must not be empty.");
        }
        this.name = name;
        this.members = new ArrayList<>();
    }

    /**
     * Adds a player to the team.
     *
     * @param player player to add
     */
    public void addMember(Player player) {
        if (player == null) {
            throw new IllegalArgumentException("Player must not be null.");
        }
        if (!members.contains(player)) {
            members.add(player);
        }
    }

    /**
     * Returns the team members as a read-only list.
     *
     * @return unmodifiable list of members
     */
    public List<Player> getMembers() {
        return Collections.unmodifiableList(members);
    }

    /**
     * Returns the number of players in the team.
     *
     * @return team size
     */
    public int getSize() {
        return members.size();
    }

    /**
     * Returns the team name.
     *
     * @return team name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a readable representation of the team.
     *
     * @return team summary
     */
    @Override
    public String toString() {
        return "Team[name=" + name + ", size=" + members.size() + "]";
    }
}