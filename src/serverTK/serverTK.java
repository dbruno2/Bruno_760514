package serverTK;

import dao.GestioneTheKnife;

import java.net.ServerSocket;
import java.net.Socket;

public class serverTK {

    ServerSocket serverSocket;
    GestioneTheKnife gk;
    public static final int PORTA = 4444;

    public serverTK() {
        try {
            serverSocket = new ServerSocket(PORTA);
        } catch (Exception e) {
            System.err.println("[serverTK] Impossibile creare il ServerSocket sulla porta " + PORTA
                    + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void exc(){
        System.out.println("server in esecuzione");
        Socket socket;
        try {
        while(true){
            System.out.println("server in attesa di connessione");
               socket= serverSocket.accept();
               new ServerSlave(socket,gk);
        }
    } catch (Exception e) {
        System.err.println("[serverTK] Errore durante l'attesa/accettazione di una connessione client"
                + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
        e.printStackTrace(System.err);
    }
    }
    /**
     * giusto per farci 2 prove della classe che interagisce con db, questo sarebbe il server nell'architettura master slave in realtà, giusto?
     */
    public static void main(String[] args) {

        new serverTK().exc();

    }
}