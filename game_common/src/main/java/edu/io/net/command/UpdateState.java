package edu.io.net.command;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Represents a command used to transmit updated game state information
 * from the server to clients.
 *
 * <p>An {@code UpdateState} command aggregates multiple
 * {@link GameState} objects, each describing a particular portion of the
 * game state (e.g., board layout, player list, score updates, etc.).
 * Such commands are typically broadcast to all connected clients
 * whenever the authoritative game state changes.
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * UpdateState.Cmd cmd = new UpdateState.Cmd(UpdateState.Pack.AFTER_JOIN_GAME);
 * cmd.add(new GameState.BoardInfo(8));
 * server.broadcast(cmd);
 * }</pre>
 */
public class UpdateState {

    /**
     * Command sent from the server to clients containing updated
     * game state information.
     *
     * <p>Instances of this class are created by various server-side
     * components (e.g., via {@code UpdateStateFactory}) to reflect
     * changes in the global game state. The command carries a collection
     * of {@link GameState} objects that clients must interpret and apply
     * in order to stay synchronized with the server.
     */
    public static class Cmd extends Command implements Iterable<GameState> {

        /**
         * List of {@link GameState} components included in this update.
         * The list is ordered in the sequence in which the state elements
         * were added. Clients should process them in the same order.
         */
        private final List<GameState> stateInfoList = new ArrayList<>();

        /**
         * Specifies the category ("pack") of update being sent.
         *
         * <p>The field is {@code transient} because the pack value is not
         * intended to be serialized as part of the command object itself.
         * It is used server-side to help construct the correct update
         * contents, but clients derive all necessary information directly
         * from the included {@link GameState} objects.
         */
        private final transient GameState.Pack pack;

        /**
         * Creates a new update command associated with the given update
         * category.
         *
         * @param pack the update category that determines what kind of
         *             state information is included
         */
        public Cmd(GameState.Pack pack) {
            this.pack = pack;
        }

        /**
         * Returns the update category associated with this command.
         * This value is not transmitted over the network; it is used
         * only on the server side.
         *
         * @return the update category
         */
        public GameState.Pack pack() {
            return pack;
        }

        /**
         * Adds a {@link GameState} element to this update.
         *
         * @param stateInfo game state object to include
         * @return this {@code Cmd} instance for method chaining
         * @throws NullPointerException if {@code stateInfo} is null
         */
        public UpdateState.Cmd add(@NotNull GameState stateInfo) {
            Objects.requireNonNull(stateInfo, "stateInfo can't be null");
            stateInfoList.add(stateInfo);
            return this;
        }

        /**
         * Returns an iterator over the state elements included in this
         * update. Iteration order corresponds to insertion order.
         */
        @Override
        public @NotNull Iterator<GameState> iterator() {
            return stateInfoList.iterator();
        }

        @Override
        public void forEach(Consumer<? super GameState> action) {
            Iterable.super.forEach(action);
        }

        @Override
        public String toString() {
            return "UpdateStateCmd{%s}"
                    .formatted(stateInfoList.toString());
        }
    }

    /**
     * Response acknowledging the receipt and processing of an
     * {@link UpdateState.Cmd} by a client.
     *
     * <p>Normally, update-state broadcasts are one-way messages and do
     * not require acknowledgments. This class exists for symmetry with
     * the command framework and for optional debugging or reliability
     * mechanisms.
     */
    public static class CmdRe extends CommandRe {

        /**
         * Status of the update processing operation.
         */
        public enum Status implements Command.Status {
            /** Update was successfully applied on the receiving side. */
            OK("Updated");

            /** Human-readable message describing the status. */
            public final String msg;

            Status(String msg) {
                this.msg = msg;
            }
        }

        /**
         * Constructs a response command with the given status.
         *
         * @param status result of the update operation
         */
        public CmdRe(Command.Status status) {
            super(status);
        }

        @Override
        public String toString() {
            return "UpdateStateCmdRe{status='%s'}"
                    .formatted(status());
        }
    }
}