package theknife;

import gui.MainFrame;
import dto.Richiesta;
import dto.Risposta;
import serverTK.ServerTK;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Client TheKnife: thread che si connette al ServerTK via socket
 * e lancia la GUI Swing (MainFrame).
 */
public class ClientTK extends Thread {


    private final Socket socket;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public ClientTK() throws IOException {
        super("ClientTK");
        this.socket = new Socket(InetAddress.getByName(null), ServerTK.PORTA);
        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.out.flush();
        this.in = new ObjectInputStream(socket.getInputStream());
    }

    public synchronized Risposta inviaRichiesta(Richiesta richiesta) throws IOException, ClassNotFoundException {
        out.writeObject(richiesta);
        out.flush();
        out.reset();
        return (Risposta) in.readObject();
    }

    @Override
    public void run() {
        try {
            while (!socket.isClosed()) {
                Thread.sleep(250);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static void main(String[] args) {
        try {
            ClientTK client = new ClientTK();
            client.start();
            SwingUtilities.invokeLater(() -> new MainFrame(client));
        } catch (IOException e) {
            System.err.println("[ClientTK] Connessione fallita: " + e.getMessage());
        }
    }
}
