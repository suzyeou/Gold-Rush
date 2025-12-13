package edu.io.net.command;

public class RequestMove {
    public static class Cmd extends Command {


        @Override
        public String toString() {
            return "RequestMoveCmd{}";
        }
    }

    public static class CmdRe extends CommandRe {

        public enum Status implements Command.Status {
            /** Update applied successfully. */
            OK("OK"),
            TIMEOUT("Timeout"),
            ;
            /** Human-readable status message. */
            public final String msg;
            Status(String msg) {
                this.msg = msg;
            }
        }

        /**
         *
         * @param status status of the response
         */
        public CmdRe(Command.Status status) {
            super(status);
        }

        public String toString() {
            return "RequestMoveCmdRe{status='%s'}"
                    .formatted(status());
        }
    }
}
