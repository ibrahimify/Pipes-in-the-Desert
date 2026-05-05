package app;

import java.util.ArrayList;
import java.util.List;

import app.Player;

/**
 * Team represents one competing side in the Pipes in the Desert game.
 *
 * <p>There are exactly two teams in each game session: the plumber team, whose
 * goal is to maximise water delivered to cisterns, and the saboteur team, whose
 * goal is to maximise water leaked from the network. {@code Team} groups the
 * {@link Player} objects that share the same goal and provides team-level
 * access for session management and winner declaration.</p>
 *
 * <p>Score comparison and winner determination are the responsibility of
 * {@code ScoreBoard}. Session-level validation (e.g. minimum team size FR-03)
 * is the responsibility of {@code System}. This class remains a lightweight
 * grouping object consistent with the Analysis Model (version 2) class
 * description for {@code Team}.</p>
 *
 * <p>This is a skeleton-level implementation. Methods print their own names
 * and relevant state, and perform simple collection management only.</p>
 *
 * @author Team A – E5
 * @version skeleton-1.0
 */
public class Team {

    // -----------------------------------------------------------------------
    // Attributes
    // -----------------------------------------------------------------------

    /**
     * The display name of this team (e.g. {@code "Plumbers"} or
     * {@code "Saboteurs"}).
     *
     * <p>Used for console output and winner declaration in {@code ScoreBoard}
     * and {@code System}.</p>
     */
    private String name;

    /**
     * The ordered list of {@link Player} objects belonging to this team.
     *
     * <p>Players are added via {@link #addMember(Player)} and retrieved via
     * {@link #getMembers()}. The list is initialised to an empty
     * {@link ArrayList} in the constructor.</p>
     */
    private List<Player> members;

    // -----------------------------------------------------------------------
    // Constructor
    // -----------------------------------------------------------------------

    /**
     * Creates a new Team with the given name and an empty member list.
     *
     * <p>The member list is initialised as an empty {@link ArrayList} so that
     * {@link #addMember(Player)} calls are always safe immediately after
     * construction.</p>
     *
     * @param name the display name of the team; must not be {@code null}
     */
    public Team(String name) {
        this.name    = name;
        this.members = new ArrayList<>();
        java.lang.System.out.println("[Team] Team created: " + this.name);
    }

    // -----------------------------------------------------------------------
    // Methods
    // -----------------------------------------------------------------------

    /**
     * Adds a {@link Player} to this team's member list.
     *
     * <p>If {@code player} is {@code null} the call is ignored and a trace
     * message is printed. No duplicate check is performed at skeleton level;
     * that responsibility belongs to the session setup logic in {@code System}
     * if required.</p>
     *
     * @param player the {@link Player} to add; ignored if {@code null}
     */
    public void addMember(Player player) {
        java.lang.System.out.println("[Team:" + name + "] addMember() called. "
                + "Player: "
                + (player != null ? player.getName() : "null"));

        if (player == null) {
            java.lang.System.out.println("[Team:" + name
                    + "] addMember() – player is null, skipping.");
            return;
        }

        members.add(player);
        java.lang.System.out.println("[Team:" + name + "] Member added: "
                + player.getName()
                + ". Team size now: " + members.size());
    }

    /**
     * Returns the list of {@link Player} objects belonging to this team.
     *
     * <p>The returned list is the live internal list; callers should treat it
     * as read-only at skeleton level to avoid unintended side effects.</p>
     *
     * @return the {@link List} of team members; never {@code null}, may be empty
     */
    public List<Player> getMembers() {
        java.lang.System.out.println("[Team:" + name + "] getMembers() called. "
                + "Member count: " + members.size());
        return members;
    }

    /**
     * Returns the number of players currently belonging to this team.
     *
     * <p>This value is used by {@code System.initializeGame()} to verify that
     * the minimum team-size requirement (FR-03: at least two players per team)
     * is satisfied before the game starts.</p>
     *
     * @return the number of members in this team as a non-negative {@code int}
     */
    public int getSize() {
        java.lang.System.out.println("[Team:" + name + "] getSize() called. "
                + "Size: " + members.size());
        return members.size();
    }

    /**
     * Returns the display name of this team.
     *
     * <p>Used by {@code ScoreBoard.determineWinner()} and {@code System.endGame()}
     * when printing the final result to the console.</p>
     *
     * @return the team name as a {@link String}; never {@code null}
     */
    public String getName() {
        java.lang.System.out.println("[Team:" + name + "] getName() called. "
                + "Name: " + name);
        return name;
    }

    // -----------------------------------------------------------------------
    // Object overrides
    // -----------------------------------------------------------------------

    /**
     * Returns a concise, human-readable description of this team suitable for
     * console output.
     *
     * <p>Format: {@code Team[name=Plumbers, size=2]}</p>
     *
     * @return a {@link String} summary of the team's name and current size
     */
    @Override
    public String toString() {
        return "Team[name=" + name + ", size=" + members.size() + "]";
    }
}
