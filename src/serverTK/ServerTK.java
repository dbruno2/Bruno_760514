package serverTK;

import dao.GestioneTheKnife;
import dao.PostgresDB;

import java.net.ServerSocket;
import java.net.Socket;

public class ServerTK {

    ServerSocket serverSocket;
    GestioneTheKnife gk;
    public static final int PORTA = 4444;
    static PostgresDB db;

    static String url = "jdbc:postgresql://localhost:5432/theKnife";
    static String user = "postgres";
    static String pass =  "qwerty";


    public ServerTK() {
        try {
            PostgresDB db = new PostgresDB(url, user, pass);
            gk = new GestioneTheKnife(db);
            serverSocket = new ServerSocket(PORTA);
        } catch (Exception e) {
            System.err.println("[ServerTK] Impossibile creare il ServerSocket sulla porta " + PORTA
                    + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void exc(){
        System.out.println("[ServerTK] server in esecuzione");
        Socket socket;
        try {
        while(true){
            System.out.println("[ServerTK] server in attesa di connessione");
               socket= serverSocket.accept();
               System.out.println("[ServerTK] connessione accettata");
               new ServerSlave(socket,gk).start();
        }
    } catch (Exception e) {
        System.err.println("[ServerTK] Errore durante l'attesa/accettazione di una connessione client"
                + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
        e.printStackTrace(System.err);
    }
    }
    /**
     * giusto per farci 2 prove della classe che interagisce con db, questo sarebbe il server nell'architettura master slave in realtà, giusto?
     */
    public static void main(String[] args) {

        new ServerTK().exc();

    }
}