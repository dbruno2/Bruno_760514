package serverTK;

import dao.GestioneTheKnife;
import dto.Richiesta;
import dto.Risposta;

import java.io.EOFException;
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
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            System.err.println("[ServerSlave] Impossibile aprire gli stream di I/O con il client "
                    + socket.getRemoteSocketAddress()
                    + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace(System.err);
        }
        System.out.println("[ServerSlave] connessione stabilita con il client");
    }

    public void run() {
        try {
            while (!socket.isClosed()) {
                Object obj = in.readObject();
                if (!(obj instanceof Richiesta richiesta)) {
                    out.writeObject(new Risposta(false, "Richiesta non valida"));
                    out.flush();
                    continue;
                }
                Risposta risposta = gestisciRichiesta(richiesta);
                out.writeObject(risposta);
                out.flush();
                out.reset();
            }
        } catch (EOFException e) {
            // client disconnesso
        } catch (Exception e) {
            System.err.println("[ServerSlave] Errore durante la gestione del client "
                    + socket.getRemoteSocketAddress()
                    + " - " + e.getClass().getSimpleName() + ": " + e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }

    private Risposta gestisciRichiesta(Richiesta richiesta) {
        return switch (richiesta.getComando()) {
            case LOGIN -> gestisciLogin(richiesta.getArgomenti());
            case REGISTRA_UTENTE -> gestisciRegistrazione(richiesta.getArgomenti());
            default -> new Risposta(false);
        };
    }

    private Risposta gestisciLogin(Object[] argomenti) {
        if (argomenti == null || argomenti.length < 2) {
            return new Risposta(false, "false");
        }
        String username = String.valueOf(argomenti[0]);
        String password = String.valueOf(argomenti[1]);
        String risultato = GestioneTheKnife.login(username, password);
        boolean ok = risultato != null && risultato.startsWith("true");
        return new Risposta(ok, risultato);
    }

    private Risposta gestisciRegistrazione(Object[] argomenti) {
        if (argomenti == null || argomenti.length < 7) {
            return new Risposta(false, "Argomenti registrazione non validi");
        }
        String nome = String.valueOf(argomenti[0]);
        String cognome = String.valueOf(argomenti[1]);
        String username = String.valueOf(argomenti[2]);
        String password = String.valueOf(argomenti[3]);
        String dataNascita = (String) argomenti[4];
        String domicilio = String.valueOf(argomenti[5]);
        String ruolo = String.valueOf(argomenti[6]);
        String preferiti = argomenti.length > 7 && argomenti[7] != null ? String.valueOf(argomenti[7]) : "";
        boolean registrato = GestioneTheKnife.registraUtente(
                nome, cognome, username, password, dataNascita, domicilio, ruolo, preferiti
        );
        return new Risposta(registrato, registrato ? "true" : "false");
    }
}
