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
            case AGGIUNGI_RISTORANTE -> gestisciAggiungiRistorante(richiesta.getArgomenti());
            case CERCA_RISTORANTI -> gestisciCercaRistoranti(richiesta.getArgomenti());
            case AGGIUNGI_RECENSIONE -> gestisciAggiungiRecensione(richiesta.getArgomenti());
           // case AGGIUNGI_PREFERITO -> gestisciAggiungiPreferito(richiesta.getArgomenti());
            case RIMUOVI_PREFERITO -> gestisciRimuoviPreferito(richiesta.getArgomenti());
            case VISUALIZZA_PREFERITI -> gestisciVisualizzaPreferiti(richiesta.getArgomenti());
            case RISPONDI_RECENSIONE -> gestisciRispondiRecensione(richiesta.getArgomenti());
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

    private Risposta gestisciAggiungiRistorante(Object[] argomenti) {
        if (argomenti == null || argomenti.length < 11) {
            return new Risposta(false, "Argomenti aggiunta ristorante non validi");
        }
        try {
            String nome = String.valueOf(argomenti[0]);
            int idRistoratore = Integer.parseInt(String.valueOf(argomenti[1]));
            String nazione = String.valueOf(argomenti[2]);
            String citta = String.valueOf(argomenti[3]);
            String indirizzo = String.valueOf(argomenti[4]);
            int latitudine = Integer.parseInt(String.valueOf(argomenti[5]));
            int longitudine = Integer.parseInt(String.valueOf(argomenti[6]));
            String prezzo = String.valueOf(argomenti[7]);
            boolean disponibilitaDelivery = Boolean.parseBoolean(String.valueOf(argomenti[8]));
            boolean disponibilitaPrenotazione = Boolean.parseBoolean(String.valueOf(argomenti[9]));
            String tipoCucina = String.valueOf(argomenti[10]);

            boolean aggiunto = GestioneTheKnife.aggiungiRistorante(
                    nome, idRistoratore, nazione, citta, indirizzo, latitudine, longitudine,
                    prezzo, disponibilitaDelivery, disponibilitaPrenotazione, tipoCucina
            );
            return new Risposta(aggiunto, aggiunto ? "true" : "false");
        } catch (Exception e) {
            System.err.println("[ServerSlave] Argomenti non validi per AGGIUNGI_RISTORANTE - "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Risposta(false, "false");
        }
    }

    private Risposta gestisciCercaRistoranti(Object[] argomenti) {
        if (argomenti == null || argomenti.length < 9) {
            return new Risposta(false, "Argomenti ricerca non validi");
        }
        try {
            Double lat = argomenti[0] != null ? Double.valueOf(String.valueOf(argomenti[0])) : null;
            Double lon = argomenti[1] != null ? Double.valueOf(String.valueOf(argomenti[1])) : null;
            String cucina = argomenti[2] != null ? String.valueOf(argomenti[2]) : null;
            String prezzoMin = argomenti[3] != null ? String.valueOf(argomenti[3]) : null;
            String prezzoMax = argomenti[4] != null ? String.valueOf(argomenti[4]) : null;
            Boolean delivery = argomenti[5] != null ? Boolean.valueOf(String.valueOf(argomenti[5])) : null;
            Boolean prenotazione = argomenti[6] != null ? Boolean.valueOf(String.valueOf(argomenti[6])) : null;
            Double stelleMin = argomenti[7] != null ? Double.valueOf(String.valueOf(argomenti[7])) : null;
            int rad = Integer.parseInt(String.valueOf(argomenti[8]));

            GestioneTheKnife.cercaRistorantiAvanzata(
                    lat, lon, cucina, prezzoMin, prezzoMax, delivery, prenotazione, stelleMin, rad
            );
            return new Risposta(true, "true");
        } catch (Exception e) {
            System.err.println("[ServerSlave] Argomenti non validi per CERCA_RISTORANTI - "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Risposta(false, "false");
        }
    }

    private Risposta gestisciAggiungiRecensione(Object[] argomenti) {
        if (argomenti == null || argomenti.length < 5) {
            return new Risposta(false, "Argomenti aggiunta recensione non validi");
        }
        try {
            String username = String.valueOf(argomenti[0]);
            String nomeRistorante = String.valueOf(argomenti[1]);
            String luogoRistorante = String.valueOf(argomenti[2]);
            String valutazione = String.valueOf(argomenti[3]);
            String testoRecensione = String.valueOf(argomenti[4]);
            boolean aggiunta = GestioneTheKnife.aggiungiRecensione(
                    username, nomeRistorante, luogoRistorante, valutazione, testoRecensione
            );
            return new Risposta(aggiunta, aggiunta ? "true" : "false");
        } catch (Exception e) {
            System.err.println("[ServerSlave] Argomenti non validi per AGGIUNGI_RECENSIONE - "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Risposta(false, "false");
        }
    }

    /*private Risposta gestisciAggiungiPreferito(Object[] argomenti) {
        if (argomenti == null || argomenti.length < 3) {
            return new Risposta(false, "Argomenti aggiunta preferito non validi");
        }
        try {
            String usernameCliente = String.valueOf(argomenti[0]);
            String nomeRistorante = String.valueOf(argomenti[1]);
            String luogoRistorante = String.valueOf(argomenti[2]);

            java.lang.reflect.Method metodo = GestioneTheKnife.class.getMethod(
                    "aggiungiPreferito", String.class, String.class, String.class
            );
            Object esito = metodo.invoke(null, usernameCliente, nomeRistorante, luogoRistorante);
            boolean aggiunto = esito instanceof Boolean && (Boolean) esito;
            return new Risposta(aggiunto, aggiunto ? "true" : "false");
        } catch (NoSuchMethodException e) {
            return new Risposta(false, "false");
        } catch (Exception e) {
            System.err.println("[ServerSlave] Errore durante AGGIUNGI_PREFERITO - "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Risposta(false, "false");
        }
    }
*/
    private Risposta gestisciRimuoviPreferito(Object[] argomenti) {
        if (argomenti == null || argomenti.length < 3) {
            return new Risposta(false, "Argomenti rimozione preferito non validi");
        }
        try {
            String usernameCliente = String.valueOf(argomenti[0]);
            String nomeRistorante = String.valueOf(argomenti[1]);
            String luogoRistorante = String.valueOf(argomenti[2]);
            boolean rimosso = GestioneTheKnife.rimuoviPreferito(
                    usernameCliente, nomeRistorante, luogoRistorante
            );
            return new Risposta(rimosso, rimosso ? "true" : "false");
        } catch (Exception e) {
            System.err.println("[ServerSlave] Argomenti non validi per RIMUOVI_PREFERITO - "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Risposta(false, "false");
        }
    }

    private Risposta gestisciVisualizzaPreferiti(Object[] argomenti) {
        if (argomenti == null || argomenti.length < 1) {
            return new Risposta(false, "Argomenti visualizzazione preferiti non validi");
        }
        try {
            String usernameCliente = String.valueOf(argomenti[0]);
            GestioneTheKnife.visualizzaPreferiti(usernameCliente);
            return new Risposta(true, "true");
        } catch (Exception e) {
            System.err.println("[ServerSlave] Argomenti non validi per VISUALIZZA_PREFERITI - "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Risposta(false, "false");
        }
    }

    private Risposta gestisciRispondiRecensione(Object[] argomenti) {
        if (argomenti == null || argomenti.length < 4) {
            return new Risposta(false, "Argomenti risposta recensione non validi");
        }
        try {
            int usernameLoggato = Integer.parseInt(String.valueOf(argomenti[0]));
            String nomeRistorante = String.valueOf(argomenti[1]);
            String usernameCliente = String.valueOf(argomenti[2]);
            String risposta = String.valueOf(argomenti[3]);
            boolean risposto = GestioneTheKnife.rispondiRecensione(
                    usernameLoggato, nomeRistorante, usernameCliente, risposta
            );
            return new Risposta(risposto, risposto ? "true" : "false");
        } catch (Exception e) {
            System.err.println("[ServerSlave] Argomenti non validi per RISPONDI_RECENSIONE - "
                    + e.getClass().getSimpleName() + ": " + e.getMessage());
            return new Risposta(false, "false");
        }
    }
}
