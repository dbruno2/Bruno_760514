package serverTK;

import dao.GestioneTheKnife;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerSlave extends Thread {

    Socket socket;
    GestioneTheKnife gk;
    ObjectInputStream in;
    ObjectOutputStream out;

    public ServerSlave(Socket socket, GestioneTheKnife gk) {
        this.socket = socket;
        this.gk = gk;
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (Exception e) {
            System.err.println("[ServerSlave] Impossibile aprire gli stream di I/O con il client "
                    + socket.getRemoteSocketAddress()
                    + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void run() {

    }
}
