import edu.io.net.client.GameServerConnector;
import edu.io.net.client.SocketConnector;
import edu.io.net.Version;
import edu.io.net.command.*;
import edu.io.net.server.GameServer;
import edu.io.net.server.gameplay.Game;
import edu.io.net.server.tcp.Client;
import edu.io.net.server.tcp.TCPServer;
import org.junit.jupiter.api.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;

public class ServerTest {
    private static Thread thread;

    private static final int PORT = 1313;
    private static final String connStr = "tcp://localhost:" + PORT;
    private static GameServer gs;
    private static Game game;

    private GameServerConnector gsc;
    private AtomicReference<CommandRe> resp;
    private AtomicReference<Command> cmdFromSrv;
    private AtomicReference<String> testm;

    @BeforeAll
    static void beforeAll() {
        thread = Thread.startVirtualThread(() -> {
            game = new Game();
            gs = new GameServer(PORT);
            gs.assignGame(game);
            gs.start();
        });
    }

    @AfterAll
    static void afterAll() {
        thread.interrupt();
    }

    @BeforeEach
    void beforeEach() {
        gsc = new GameServerConnector(connStr, new SocketConnector());
        gsc.connect();
        resp = new AtomicReference<>();
        cmdFromSrv = new AtomicReference<>();
        testm = new AtomicReference<>();
    }

    private void issueCommandAndThen(Command cmd, Consumer<CommandRe> code) {
        gsc.issueCommand(cmd, resp::set);
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
                    code.accept(resp.get());
                });
    }

    private void broadcastFromSrv(
            Command cmd,
            BiConsumer<Client, CommandRe> onResponse)
            throws NoSuchFieldException, IllegalAccessException {
        // ugh... dirty hack with reflection (only for test purposes)
        var serverField = GameServer.class.getDeclaredField("server");
        serverField.setAccessible(true);
        ((TCPServer)serverField.get(gs)).broadcast(cmd, onResponse);
    }

    @Test
    void can_connect_to_server() {
        Assertions.assertTrue(gsc.isConnected());
    }

    @Test
    void echo_responds() {
        var cmd = new Echo.Cmd("hello");
        gsc.issueCommand(cmd, resp::set);
        await().atMost(1, SECONDS)
            .untilAsserted(() -> {
                if (resp.get() instanceof Echo.CmdRe echoCmdRe) {
                    Assertions.assertEquals(cmd.msg, echoCmdRe.msg);
                }
                else Assertions.fail();
            });
    }

    @Test
    void handshake_successful() {
        var cmd = new Handshake.Cmd(
                "%d.%d".formatted(Version.MAJOR, Version.MINOR));
        issueCommandAndThen(cmd, cmdRe -> {
            Assertions.assertEquals(
                    Handshake.CmdRe.Status.OK,
                    cmdRe.status()
            );
        });
    }

    @Test
    void handshake_failed_when_major_version_is_too_low() {
        var cmd = new Handshake.Cmd("0.1");
        issueCommandAndThen(cmd, cmdRe -> {
            Assertions.assertEquals(
                    Handshake.CmdRe.Status.LIB_VERSION_TOO_LOW,
                    cmdRe.status()
            );
        });
    }

    @Test
    void handshake_failed_when_minor_version_is_too_low() {
        var cmd = new Handshake.Cmd("1.0");
        issueCommandAndThen(cmd, cmdRe -> {
            Assertions.assertEquals(
                    Handshake.CmdRe.Status.LIB_VERSION_TOO_LOW,
                    cmdRe.status()
            );
        });
    }

    @Test
    void handshake_failed_when_version_is_malformed() {
        var cmd = new Handshake.Cmd("3");
        issueCommandAndThen(cmd, cmdRe -> {
            Assertions.assertEquals(
                    Handshake.CmdRe.Status.LIB_VERSION_MALFORMED,
                    cmdRe.status()
            );
        });
    }

    @Test
    void join_player_successful() {
        var cmd = new JoinGame.Cmd("ziutek");
        issueCommandAndThen(cmd, cmdRe -> {
            Assertions.assertEquals(
                    JoinGame.CmdRe.Status.OK,
                    cmdRe.status()
            );
        });
    }

    @Test
    void cannot_join_twice() {
        var cmd = new JoinGame.Cmd("ziutek");
        issueCommandAndThen(cmd, cmdRe -> {});
        issueCommandAndThen(cmd, cmdRe -> {
            Assertions.assertEquals(
                    JoinGame.CmdRe.Status.ALREADY_CONNECTED,
                    cmdRe.status()
            );
        });
    }

    @Test
    void after_joining_player_get_clientId() {
        var cmd = new JoinGame.Cmd("ziutek");
        issueCommandAndThen(cmd, cmdRe -> {
            if (cmdRe instanceof JoinGame.CmdRe joinGameCmdRe) {
                Assertions.assertNotNull(joinGameCmdRe.clientId);
                Assertions.assertFalse(joinGameCmdRe.clientId.isBlank());
            }
        });
    }

    @Test
    void player_can_leave_game() {
        var joinCmd = new JoinGame.Cmd("ziutek");
        issueCommandAndThen(joinCmd, _ -> {
            var leaveCmd = new LeaveGame.Cmd();
            issueCommandAndThen(leaveCmd, cmdRe -> {
                Assertions.assertEquals(
                        LeaveGame.CmdRe.Status.OK,
                        cmdRe.status()
                );
            });
        });
    }

    @Test
    void cannot_leave_game_if_not_connected() {
        issueCommandAndThen(new JoinGame.Cmd("ziutek"), joinAck -> {
            var leaveCmd = new LeaveGame.Cmd();
            issueCommandAndThen(leaveCmd, _ -> {
                issueCommandAndThen(leaveCmd, cmdRe -> {
                    Assertions.assertEquals(
                            LeaveGame.CmdRe.Status.CLIENT_NOT_CONNECTED,
                            cmdRe.status());
                });
            });
        });
    }

    @Test
    void can_get_info_after_join() {
        issueCommandAndThen(new JoinGame.Cmd("ziutek"), joinAck -> {
            issueCommandAndThen(new GetInfo.Cmd(), cmdRe -> {
                Assertions.assertEquals(
                        CommandRe.Status.OK,
                        cmdRe.status()
                );
            });
        });
    }

    @Test
    void cannot_get_info_if_not_joined() {
        issueCommandAndThen(new GetInfo.Cmd(), cmdRe -> {
            Assertions.assertEquals(
                    CommandRe.Status.CLIENT_NOT_FOUND,
                    cmdRe.status()
            );
            Assertions.assertEquals(
                    GetInfo.CmdRe.Info.EMPTY,
                    ((GetInfo.CmdRe)cmdRe).info
            );
        });
    }

    @Test
    void srv_can_send_cmd()
            throws NoSuchFieldException, IllegalAccessException, InterruptedException {
        testm.set("");
        var cnt = new CountDownLatch(1);
        gsc.onCommandFromServer(cmd -> {
            if (cmd instanceof Echo.Cmd echoCmd) {
                testm.set(echoCmd.msg);
                cnt.countDown();
            }
            return CommandAck.NO_ACK;
        });

        gs.startGame();
        broadcastFromSrv(new Echo.Cmd("hello!"), (_,_) -> {});
        cnt.await(1, SECONDS);
        Assertions.assertEquals("hello!", testm.get());
    }

    @Test
    void after_JoinGame_server_send_UpdateState() {
        gsc.onCommandFromServer((cmd) -> {
            cmdFromSrv.set(cmd);
            return new CommandAck(
                    cmd,
                    new UpdateState.CmdRe(UpdateState.CmdRe.Status.OK)
            );
        });
        gsc.issueCommand(new JoinGame.Cmd("ziutek"));
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
                    Assertions.assertInstanceOf(
                            UpdateState.Cmd.class,
                            cmdFromSrv.get());
                });
    }

    @Test
    void server_can_receive_resp_from_client() throws InterruptedException, NoSuchFieldException, IllegalAccessException {
        var cnt = new CountDownLatch(2);

        gsc.onCommandFromServer(cmd -> {
            if (cmd instanceof RequestMove.Cmd reqMoveCmd) {
                cmdFromSrv.set(reqMoveCmd);
                cnt.countDown();
                return new CommandAck(
                        reqMoveCmd,
                        new RequestMove.CmdRe(RequestMove.CmdRe.Status.OK)
                );
            }
            else return CommandAck.NO_ACK;
        });

        broadcastFromSrv(new RequestMove.Cmd(), (_, cmdRe) -> {
            resp.set(cmdRe);
            cnt.countDown();
        });

        cnt.await(1, SECONDS);
        Assertions.assertInstanceOf(RequestMove.Cmd.class, cmdFromSrv.get());
        Assertions.assertEquals(
                RequestMove.CmdRe.Status.OK,
                resp.get().status());
    }

}
