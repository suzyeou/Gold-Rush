package edu.io.net.client;

import edu.io.net.command.Command;

import java.io.*;
import java.net.Socket;
import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.function.Consumer;

/**
 * Default implementation of {@link NetworkConnector} that uses TCP
 * sockets for communication between the client and the game server.
 * <p>
 * This class manages the socket connection, handles input and output
 * streams, and delivers received {@link Command} objects to a
 * registered listener.
 * <p>
 * The connector is thread-safe for concurrent send and receive
 * operations, but the connection lifecycle methods
 * ({@code connect()} and {@code disconnect()}) should not be called
 * concurrently.
 */
public class SocketConnector implements NetworkConnector {
    private Socket sock;
    private ObjectInputStream oIn;
    private ObjectOutputStream oOut;
    private final Semaphore semaphore = new Semaphore(1);

    @Override
    public void connect(URI uri) throws IOException {
        sock = new Socket(uri.getHost(), uri.getPort());
        oOut = new ObjectOutputStream(sock.getOutputStream());
        oIn = new ObjectInputStream(sock.getInputStream());
    }

    @Override
    public void disconnect() throws IOException {
        sock.close();
    }

    @Override
    public void sendToServer(Command cmd) throws IOException, InterruptedException {
        semaphore.acquire(1);
        oOut.writeObject(cmd);
        oOut.flush();
        semaphore.release(1);
    }

    @Override
    public void onAnyCmdFromServer(Consumer<Command> cmd) {
        Thread.startVirtualThread(() -> {
            try {
                while (sock.isConnected()) {
                    cmd.accept((Command) oIn.readObject());
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
