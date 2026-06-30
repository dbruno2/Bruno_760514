package dao;

/*
 * Sebastiano Svezia 760462 VA
 * Davide Bruno 760514 VA 
 * Fancesco Vieri 761195 VA
 * Leonardo Bighetti 760015 VA
 */


/**
 * Classe DAO per la gestione delle funzionalita principali dell'applicazione TheKnife.
 * Fornisce metodi per l'aggiunta e gestione di ristoranti, recensioni, e preferiti.
 */

import java.io.*;
import java.sql.SQLException;
import java.nio.file.Paths;
import java.util.*;

import dto.Ristorante;
import sicurezzaPassword.Criptazione;

/**
 * Classe di utilita che gestisce le operazioni principali legate alla piattaforma TheKnife.
 * <p>
 * Contiene metodi statici per la gestione di ristoranti, recensioni, utenti e funzionalita
 * di login, preferiti, risposta alle recensioni, riepiloghi e altre funzionalita collegate.
 * Questa classe agisce come livello di business centrale per coordinare l'accesso ai dati
 * e la logica di controllo.
 *
 * <p>Questa classe non è progettata per essere istanziata.
 *
 * @version 1.0
 */
public class GestioneTheKnife {
    
    // Path dinamici

    /**
     * Percorso del file contenente i dati degli utenti.
     */
    public static final String fileUtentiPath = Paths.get("..", "dati", "utenti.txt").normalize().toString();


    /**
     * Percorso del file contenente i dati dei ristoranti.
     */
    public static final String fileRistorantiPath = Paths.get("..", "dati", "ristoranti.txt").normalize().toString();


    /**
     * Percorso del file contenente i dati delle recensioni.
     */
     public static final String fileRecensioniPath = Paths.get("..", "dati", "recensioni.txt").normalize().toString();

    static String url = "jdbc:postgresql://localhost:5432/theKnife";
    static String user = "postgres";
    static String pass =  "qwerty";


   static PostgresDB db = new PostgresDB(url, user, pass);
    private static double lat;
    private static double lon;

    /**
 * Aggiunge un nuovo Ristorante al sistema, se i dati sono validi e non esiste gia un Ristorante con lo stesso nome e indirizzo.
 * <p>
 * La funzione valida i parametri in input, controlla la presenza di duplicati nel file, crea un nuovo oggetto
 * {@link Ristorante} e lo salva nel file di archiviazione in formato testuale.
 *
 * @param nome                       il nome del Ristorante
 * @param usernameRistoratore        lo username del ristoratore associato
 * @param nazione                   la nazione del Ristorante
 * @param citta                     la citta in cui si trova il Ristorante
 * @param indirizzo                 l'indirizzo del Ristorante
 * @param latitudine                la latitudine geografica del Ristorante
 * @param longitudine               la longitudine geografica del Ristorante
 * @param prezzo                    la fascia di prezzo media del Ristorante
 * @param disponibilita_delivery     true se il Ristorante offre consegna a domicilio
 * @param disponibilita_prenotazione true se è possibile prenotare online
 * @param tipo_Cucina               il tipo di cucina offerta
 * @return true se il Ristorante è stato aggiunto correttamente, false in caso di errore o dati duplicati
 */

public static boolean aggiungiRistorante(String nome, int idRistoratore, String nazione, String citta, String indirizzo, int latitudine,
    int longitudine, String prezzo, boolean disponibilita_delivery, boolean disponibilita_prenotazione,
    String tipo_Cucina  ) {

    if (nome == null || nome.isEmpty() ||
        nazione == null || nazione.isEmpty() ||
        citta == null || citta.isEmpty() ||
        indirizzo == null || indirizzo.isEmpty() ||
        tipo_Cucina == null || tipo_Cucina.isEmpty())
        return false;

    try {
      String sql="INSERT INTO ristoranti_the_knife (\n" +
              "    nome_ristorante,\n" +
              "    nazione,\n" +
              "    citta,\n" +
              "    indirizzo,\n" +
              "    latitudine,\n" +
              "    longitudine,\n" +
              "    fascia_prezzo,\n" +
              "    delivery,\n" +
              "    prenotabile,\n" +
              "    tipo_cucina,\n" +
              "    id_utente\n" +
              ")\n" +
              "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
      int x=db.execute(sql, nome, nazione, citta, indirizzo, latitudine, longitudine, prezzo, disponibilita_delivery, disponibilita_prenotazione, tipo_Cucina, idRistoratore);
      if(x<=0){return false;}
      else {return true;}
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


   /**
 * Visualizza un riepilogo dei ristoranti registrati dall'utente specificato,
 * mostrando per ciascuno il nome, la citta e la media delle valutazioni ricevute.
 * <p>
 * I dati vengono letti da due file: uno contenente i ristoranti e l'altro le recensioni.
 * Il metodo calcola la media delle stelle ricevute per ogni Ristorante associato all'utente
 * e stampa i risultati sulla console.
 *
 * @param usernameRistoratore lo username del ristoratore di cui visualizzare il riepilogo
 */
    public static List<Map<String, Object>> visualizzaRiepilogo(int usernameRistoratore) {

        String sql = "SELECT r.id_ristorante, r.nome_ristorante, r.citta, AVG(rec.valutazione) AS media_stelle " +
                     "FROM ristoranti_the_knife r " +
                     "LEFT JOIN recensione rec ON r.id_ristorante = rec.id_ristorante " +
                     "WHERE r.id_utente = ? " +
                     "GROUP BY r.id_ristorante, r.nome_ristorante, r.citta;";

        try {
            List<Map<String, Object>> risultati = db.executeSelect(sql, usernameRistoratore);

            if (risultati.isEmpty()) {
                System.out.println("Nessun ristorante trovato per l'utente specificato.");
                return risultati;
            }

            System.out.println("=== RIEPILOGO RISTORANTI ===");
            for (Map<String, Object> ristorante : risultati) {
                System.out.println("Nome: " + ristorante.get("nome_ristorante"));
                System.out.println("Città: " + ristorante.get("citta"));
                System.out.println("Media valutazioni: " + (ristorante.get("media_stelle") != null ? ristorante.get("media_stelle") : "Nessuna recensione"));
                System.out.println("----------------------------------------");
            }

            return risultati;
        } catch (SQLException e) {
            System.err.println("Errore durante la visualizzazione del riepilogo: " + e.getMessage());
            return null;
        }
}

/**
 * Visualizza tutte le recensioni disponibili per un Ristorante specifico.
 * <p>
 * Il metodo legge il file delle recensioni, filtra quelle associate al Ristorante specificato,
 * stampa ogni recensione con i dettagli dell'utente, valutazione, testo e risposta,
 * e calcola la media delle valutazioni.
 * <p>
 * Se il file non è configurato o non ci sono recensioni, stampa un messaggio informativo.
 *
 * @param nomeRistorante il nome del Ristorante di cui visualizzare le recensioni
 */
public static Map<String, Object> visualizzaRecensioniPerRistorante(int idRistorante) {
    Map<String, Object> risultato = new HashMap<>();

    String sql = "SELECT " +
                           "    rec.id_recensione, " + " utenti.username, "+
                           "    rec.testo AS recensione, " +
                           "    rec.valutazione, " +
                           "    rec.data_recensione, " +
                           "    rr.testo AS risposta " +
                           "FROM recensione rec " +
                           "LEFT JOIN risposta_recensione rr " +
                           "    ON rec.id_recensione = rr.id_recensione " + " JOIN utenti on utenti.id_utente=rec.id_utente_autore "+
                           "WHERE rec.id_ristorante = ? " +
                           "ORDER BY rec.data_recensione DESC;";

    String sqlStatistiche = "SELECT " +
                            "    COUNT(*) AS numero_recensioni, " +
                            "    ROUND(AVG(valutazione), 2) AS media_stelle " +
                            "FROM recensione " +
                            "WHERE id_ristorante = ?;";

    try {
        List<Map<String, Object>> recensioni = db.executeSelect(sql, idRistorante);
        risultato.put("recensioni", recensioni);

        if (recensioni.isEmpty()) {
            System.out.println("Nessuna recensione trovata per questo ristorante.");
            risultato.put("numero_recensioni", 0);
            risultato.put("media_stelle", 0.0);
        } else {
            List<Map<String, Object>> statistiche = db.executeSelect(sqlStatistiche, idRistorante);

            if (!statistiche.isEmpty()) {
                Map<String, Object> stats = statistiche.get(0);
                risultato.put("numero_recensioni", stats.get("numero_recensioni"));
                risultato.put("media_stelle", stats.get("media_stelle"));
            }

            System.out.println("=== RECENSIONI ===");
            for (Map<String, Object> rec : recensioni) {
                System.out.println("ID: " + rec.get("id_recensione"));
                System.out.println("username: " + rec.get("username"));
                System.out.println("Valutazione: " + rec.get("valutazione") + "/5");
                System.out.println("Data: " + rec.get("data_recensione"));
                System.out.println("Testo: " + rec.get("recensione"));
                Object risposta = rec.get("risposta");
                if (risposta != null && !risposta.toString().isEmpty()) {
                    System.out.println("Risposta: " + risposta);
                } else {
                    System.out.println("Risposta: Nessuna");
                }
                System.out.println("----------------------------------------");
            }

            System.out.println("=== STATISTICHE ===");
            System.out.println("Numero recensioni: " + risultato.get("numero_recensioni"));
            System.out.println("Media valutazioni: " + risultato.get("media_stelle") + " stelle");
        }

        return risultato;
    } catch (SQLException e) {
        System.err.println("Errore durante la visualizzazione delle recensioni: " + e.getMessage());
        return null;
    }
}


   /**
 * Permette a un ristoratore di rispondere a una recensione ricevuta su uno dei suoi ristoranti.
 * <p>
 * Il metodo verifica che il Ristorante appartenga all'utente loggato, cerca la recensione corrispondente
 * nel file delle recensioni, e se non ha ancora ricevuto una risposta, aggiunge il testo fornito.
 * L'intero file viene riscritto con la modifica applicata.
 *
 * @param usernameLoggato   lo username del ristoratore loggato
 * @param nomeRistorante    il nome del Ristorante a cui appartiene la recensione
 * @param usernameCliente   lo username del cliente che ha scritto la recensione
 * @param risposta          il testo della risposta del ristoratore
 * @return true se la risposta è stata aggiunta con successo, false in caso di errore, Ristorante non valido,
 *         recensione inesistente o gia risposto
 */
public static boolean rispondiRecensione(int idRecensione, int idRistoratoreAutore, String testo) {

    // Validazione
    if (testo == null || testo.isEmpty()) {
        System.err.println("Il testo della risposta non può essere vuoto.");
        return false;
    }

    // Query per verificare che il ristoratore sia proprietario del ristorante associato alla recensione
    String sqlVerifica = "SELECT r.id_utente FROM recensione rec " +
                         "JOIN ristoranti_the_knife r ON rec.id_ristorante = r.id_ristorante " +
                         "WHERE rec.id_recensione = ? AND r.id_utente = ?;";

    // Query per aggiungere la risposta
    String sqlInserisci = "INSERT INTO risposta_recensione (testo, id_recensione) " +
                          "VALUES (?, ?);";

    try {
        // Verifica autorizzazione
        List<Map<String, Object>> verifica = db.executeSelect(sqlVerifica, idRecensione, idRistoratoreAutore);

        if (verifica.isEmpty()) {
            System.err.println("Errore: non sei il proprietario di questo ristorante o la recensione non esiste.");
            return false;
        }

        // Aggiungi la risposta
        int rCoinvolte = db.execute(sqlInserisci, testo, idRecensione);

        if (rCoinvolte > 0) {
            System.out.println("Risposta aggiunta con successo.");
            return true;
        } else {
            System.err.println("Errore: impossibile aggiungere la risposta.");
            return false;
        }
    } catch (SQLException e) {
        System.err.println("Errore durante l'aggiunta della risposta: " + e.getMessage());
        return false;
    }
}




    /**
     * Aggiunge un ristorante alla lista dei preferiti dell'utente.
     * <p>
     * Se il ristorante è già nei preferiti dell'utente, la query non farà nulla
     * grazie alla clausola ON CONFLICT DO NOTHING.
     *
     * @param idUtente l'ID dell'utente
     * @param idRistorante l'ID del ristorante da aggiungere ai preferiti
     * @return true se il preferito è stato aggiunto (o era già presente), false in caso di errore
     */
    public static boolean aggiungiPreferito(int idUtente, int idRistorante) {
        String sql = "INSERT INTO preferiti (id_utente, id_ristorante) " +
                     "VALUES (?, ?) " +
                     "ON CONFLICT (id_utente, id_ristorante) DO NOTHING;";
        
        try {
            int rCoinvolte = db.execute(sql, idUtente, idRistorante);
            
            if (rCoinvolte > 0) {
                System.out.println("Preferito aggiunto con successo.");
                return true;
            } else {
                System.out.println("Il ristorante è già nei tuoi preferiti.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiunta del preferito: " + e.getMessage());
            return false;
        }
    }

    /**
     * Visualizza tutti i ristoranti preferiti dell'utente specificato dal database.
     * <p>
     * Esegue una query JOIN tra la tabella ristoranti_the_knife e preferiti
     * per recuperare i dettagli dei ristoranti preferiti dell'utente.
     *
     * @param idUtente l'ID dell'utente di cui visualizzare i preferiti
     * @return una lista di Map contenente i dati dei ristoranti preferiti
     *         (id_ristorante, nome_ristorante, citta, indirizzo, tipo_cucina, fascia_prezzo)
     */
    public static List<Map<String, Object>> visualizzaPreferiti(int idUtente) {
        String sql = "SELECT r.id_ristorante, r.nome_ristorante, r.citta, r.indirizzo, r.tipo_cucina, r.fascia_prezzo " +
                     "FROM ristoranti_the_knife r " +
                     "JOIN preferiti p ON r.id_ristorante = p.id_ristorante " +
                     "WHERE p.id_utente = ?;";

        try {
            List<Map<String, Object>> risultati = db.executeSelect(sql, idUtente);

            if (risultati.isEmpty()) {
                System.out.println("Nessun ristorante preferito trovato.");
                return risultati;
            }

            System.out.println("Ristoranti preferiti:");
            for (Map<String, Object> ristorante : risultati) {
                System.out.println("idRistorante: "+ristorante.get("id_ristorante"));
                System.out.println("Nome: " + ristorante.get("nome_ristorante"));
                System.out.println("Città: " + ristorante.get("citta"));
                System.out.println("Indirizzo: " + ristorante.get("indirizzo"));
                System.out.println("Tipo di cucina: " + ristorante.get("tipo_cucina"));
                System.out.println("Fascia di prezzo: " + ristorante.get("fascia_prezzo"));
                System.out.println("----------------------------------------");
            }

            return risultati;
        } catch (SQLException e) {
            System.err.println("Errore durante la visualizzazione dei preferiti: " + e.getMessage());
            return null;
        }
    }

    /**
     * Rimuove un ristorante dalla lista dei preferiti dell'utente.
     * <p>
     * Esegue una query DELETE sulla tabella preferiti usando l'ID dell'utente e l'ID del ristorante.
     *
     * @param idUtente l'ID dell'utente
     * @param idRistorante l'ID del ristorante da rimuovere dai preferiti
     * @return true se il preferito è stato rimosso con successo, false in caso di errore
     */
    public static boolean rimuoviPreferito(int idUtente, int idRistorante) {
        String sql = "DELETE FROM preferiti " +
                     "WHERE id_utente = ? AND id_ristorante = ?;";

        try {
            int rCoinvolte = db.execute(sql, idUtente, idRistorante);

            if (rCoinvolte > 0) {
                System.out.println("Preferito rimosso con successo.");
                return true;
            } else {
                System.out.println("Nessun preferito trovato con i parametri forniti.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Errore durante la rimozione del preferito: " + e.getMessage());
            return false;
        }
    }

   /**
 * Aggiunge una nuova recensione a un Ristorante specificato, se non gia presente per l'utente.
 * <p>
 * Il metodo verifica che i dati siano validi, controlla l'esistenza del Ristorante, verifica che l'utente
 * non abbia gia recensito lo stesso Ristorante nella stessa localita e, in caso positivo, aggiunge
 * la nuova recensione al file delle recensioni.
 *
 * @param username          lo username del cliente che lascia la recensione
 * @param nomeRistorante    il nome del Ristorante recensito
 * @param luogoRistorante   la citta o localita in cui si trova il Ristorante
 * @param valutazione       il punteggio assegnato (es. da 1 a 5)
 * @param testoRecensione   il testo della recensione
 * @return true se la recensione è stata aggiunta correttamente, false in caso di errore o se la recensione
 *         è gia presente
 */
public static boolean aggiungiRecensione(String testo, int valutazione, int idUtenteAutore, int idRistorante) {

    // Validazione client-side
    if (testo == null || testo.isEmpty()) {
        System.err.println("Il testo della recensione non può essere vuoto.");
        return false;
    }

    if (valutazione < 1 || valutazione > 5) {
        System.err.println("La valutazione deve essere tra 1 e 5.");
        return false;
    }

    String sql = "INSERT INTO recensione (testo, valutazione, data_recensione, id_utente_autore, id_ristorante) " +
                 "VALUES (?, ?, CURRENT_DATE, ?, ?);";

    try {
        int rCoinvolte = db.execute(sql, testo, valutazione, idUtenteAutore, idRistorante);

        if (rCoinvolte > 0) {
            System.out.println("Recensione aggiunta con successo.");
            return true;
        } else {
            System.err.println("Errore: nessuna riga inserita.");
            return false;
        }
    } catch (SQLException e) {
        System.err.println("Errore durante l'aggiunta della recensione: " + e.getMessage());
        return false;
    }
}
    

  /**
 * Verifica le credenziali di accesso di un utente, controllando username, password e ruolo.
 * <p>
 * Il metodo legge il file degli utenti, cerca una corrispondenza per username, confronta
 * la password (in chiaro o cifrata, a seconda dell’implementazione) e il ruolo.
 * Restituisce {@code true} solo se tutti e tre i dati corrispondono.
 * <p>
 * In caso di errore di lettura file o credenziali non valide, stampa un messaggio sulla console
 * e restituisce {@code false}.
 *
 * @param username  lo username inserito dall’utente
 * @param password  la password associata all’utente
 * @return {@code true} se login riuscito, {@code false} in caso di errore o credenziali non valide
 */
    public static String login(String username, String password) {

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            System.out.println("Username o password vuoti.");
            return "false";
        }



        try {
            String sql = "SELECT password,ruolo,id_utente FROM utenti WHERE username = ?;";
            List<Map<String, Object>> risultati = db.executeSelect(sql, username);

            if (risultati.isEmpty()) {
                System.out.println("Username non trovato.");
                return "false";
            }

            Map<String, Object> utente = risultati.get(0); //dal momento che la query restituisce (se c'è) 1 utente solo
            //visto che abbiamo messo unique per il nome utente non c'è bisogno di scorrere tutta la lista


            if (Criptazione.confronta(password, (String) utente.get("password"))) {
                return "true,"+utente.get("ruolo")+","+utente.get("id_utente");
            }

            System.out.println("Credenziali non valide.");
            return "false";
        } catch (SQLException e) {
            System.err.println("Errore durante l'accesso al database: " + e.getMessage());
            return "false";
        }

    }



/**
 * Registra un nuovo utente nel file specificato. Se il file non esiste, viene creato.
 * Verifica che l'username non sia gia presente prima di aggiungere il nuovo utente.
 *
 * @param nome        Il nome dell'utente.
 * @param cognome     Il cognome dell'utente.
 * @param username    L'username dell'utente.
 * @param password    La password dell'utente.
 * @param dataNascita La data di nascita dell'utente.
 * @param domicilio   Il domicilio dell'utente.
 * @param ruolo       Il ruolo dell'utente.
 * @param preferiti   Le preferenze dell'utente.
 * @return            {@code true} se l'utente è stato registrato con successo, {@code false} se l'username esiste gia o si è verificato un errore.
 */
    public static boolean registraUtente(String nome, String cognome, String username, String password, String dataNascita, String domicilio, String ruolo, String preferiti) {

        String sql = "INSERT INTO utenti (username, password, nome, cognome, ruolo, data_nascita, indirizzo) VALUES (?, ?, ?, ?, ?::tipo_ruolo, ?::date, ?)";
        try {
            int r = db.execute(sql, username, password, nome, cognome, ruolo, dataNascita, domicilio);
            return r > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento nel DB: " + e.getMessage());
            return false;
        }



}
public static List<Map<String, Object>> showRecommended(){
        System.out.println("inserire località dalla quale si sta eseguendo theKnife");
        double[] coords= findCoordinates();
    String sql = "SELECT t.*,\n" +
            "       t.nome_citta,\n" +
            "       t.nazione,\n" +
            "       COALESCE(t.valutazione_media, 0) AS valutazione_media\n" +
            "FROM (\n" +
            "    SELECT r.*,\n" +
            "           dc.nome AS nome_citta,\n" +
            "           dc.country_code AS nazione,\n" +
            "           (\n" +
            "               6371 * 2 * ACOS(\n" +
            "                   COS(RADIANS(?)) *\n" +
            "                   COS(RADIANS(dc.lat)) *\n" +
            "                   COS(RADIANS(dc.lon) - RADIANS(?)) +\n" +
            "                   SIN(RADIANS(?)) *\n" +
            "                   SIN(RADIANS(dc.lat))\n" +
            "               )\n" +
            "           ) AS distanza_km,\n" +
            "           COALESCE(AVG(rec.valutazione), 0) AS valutazione_media\n" +
            "    FROM ristoranti_the_knife r\n" +
            "    JOIN citta dc \n" +
            "        ON r.id_citta = dc.id\n" +
            "    LEFT JOIN recensione rec\n" +
            "        ON rec.id_ristorante = r.id_ristorante\n" +
            "    GROUP BY r.id_ristorante, dc.nome, dc.country_code, dc.lat, dc.lon\n" +
            ") t\n" +
            "WHERE t.distanza_km <= ?\n" +
            "ORDER BY t.distanza_km;";
    List<Map<String, Object>> risultati=null ;
    try {
        risultati=db.executeSelect(sql,coords[0],coords[1],coords[0],10);
        if (risultati.isEmpty()) {System.out.println("non abbiamo ristoranti nella sua zona da consigliare..."); return risultati;}
        else if (!risultati.isEmpty()) {
            System.out.println("le consigliamo questi ristoranti:");
            for (Map<String, Object> ristorante : risultati) {
                System.out.println("Nome: " + ristorante.get("nome_ristorante"));
                System.out.println("paese: " + ristorante.get("nazione"));
                System.out.println("citta: " + ristorante.get("nome_citta"));
                System.out.println("Tipo di cucina: " + ristorante.get("tipo_cucina"));
                float media = ((Number) ristorante.get("valutazione_media")).floatValue();
                System.out.println("Media voti: " + String.format("%.2f", media));
                System.out.println("Fascia di prezzo: " + ristorante.get("fascia_prezzo"));
                System.out.println("Delivery: " + (Boolean.TRUE.equals(ristorante.get("delivery")) ? "Si" : "No"));
                System.out.println("Prenotabile: " + (Boolean.TRUE.equals(ristorante.get("prenotabile")) ? "Si" : "No"));
                System.out.println("Distanza: " + Math.round((Double) ristorante.get("distanza_km")) + " km");
                System.out.println("----------------------------------------");
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

    return risultati;
}
/**
 * Cerca ristoranti nel db in base a criteri(opzionali tranne il raggio di ricerca) scelti dall'utente.
 * @param lat latitudine
 * @param lon longitudine
 * @param cucina tipo di cucina
 * @param prezzoMin prezzo minimo
 * @param prezzoMax prezzo massimo
 * @param delivery disponibilità di delivery
 * @param prenotazione disponibilità di prenotazione
 * @param stelleMin numero minimo di stelle
 * @param rad raggio di ricerca
 */
    public static List<Map<String, Object>> cercaRistorantiAvanzata(
    Double lat,
    Double lon,
    String cucina,
    String prezzoMin,
    String prezzoMax,
    Boolean delivery,
    Boolean prenotazione,
    Double stelleMin,
    int rad
) {
        String sql = "SELECT r.*,\n" +
                "       r.nome_citta \n" +
                "FROM (\n" +
                "    SELECT r_inner.*,\n" +
                "           dc.nome AS nome_citta,\n" +
                "           dc.country_code AS nazione,\n" +
                "           (6371 * 2 * ASIN(SQRT(POWER(SIN(RADIANS(dc.lat - ?) / 2), 2) + COS(RADIANS(?)) * COS(RADIANS(dc.lat)) * POWER(SIN(RADIANS(dc.lon - ?) / 2), 2)))) AS distanza_km,\n" +
                "           COALESCE(AVG(rec.valutazione), 0) AS media_valutazione\n" +
                "    FROM ristoranti_the_knife r_inner\n" +
                "    JOIN citta dc ON r_inner.id_citta = dc.id\n" +
                "    LEFT JOIN recensione rec ON r_inner.id_ristorante = rec.id_ristorante\n" +
                "    GROUP BY r_inner.id_ristorante, dc.nome, dc.country_code, dc.lat, dc.lon -- Includiamo i nuovi campi nel GROUP BY\n" +
                ") r\n" +
                "WHERE r.distanza_km <= ?\n" +
                "  AND LOWER(r.tipo_cucina) = LOWER(COALESCE(?, r.tipo_cucina))\n" +
                "  AND LENGTH(r.fascia_prezzo) >= COALESCE(LENGTH(?), 1)\n" +
                "  AND LENGTH(r.fascia_prezzo) <= COALESCE(LENGTH(?), 4)\n" +
                "  AND r.delivery = COALESCE(?, r.delivery)\n" +
                "  AND r.prenotabile = COALESCE(?, r.prenotabile)\n" +
                "  AND r.media_valutazione >= COALESCE(?, 0)\n" +
                "ORDER BY r.distanza_km ASC;";
        List<Map<String, Object>> risultati = null;
        try {
            risultati = db.executeSelect(sql,lat, lat, lon, rad, cucina,  prezzoMin,  prezzoMax,  delivery, prenotazione, stelleMin );
            if (risultati.isEmpty()) {System.out.println("Non sono stati trovati ristoranti secondo quei criteri"); return risultati;}
            else if (!risultati.isEmpty()) {
                System.out.println("Lista dei risultati:");
                for (Map<String, Object> ristorante : risultati) {
                    System.out.println("Nome: " + ristorante.get("nome_ristorante"));

                        float media = ((Number) ristorante.get("media_valutazione")).floatValue();
                        System.out.println("Media voti: " + String.format("%.2f", media));
                    System.out.println("paese: " + ristorante.get("nazione"));
                    System.out.println("citta: " + ristorante.get("nome_citta"));
                    System.out.println("Tipo di cucina: " + ristorante.get("tipo_cucina"));
                    System.out.println("Fascia di prezzo: " + ristorante.get("fascia_prezzo"));
                    System.out.println("Delivery: " + (Boolean.TRUE.equals(ristorante.get("delivery")) ? "Si" : "No"));
                    System.out.println("Prenotabile: " + (Boolean.TRUE.equals(ristorante.get("prenotabile")) ? "Si" : "No"));
                    System.out.println("Distanza: " + Math.round((Double) ristorante.get("distanza_km")) + " km");
                    System.out.println("----------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'esecuzione della query: " + e.getMessage());
        }
return risultati;
}


    
    /**
 * Elimina una recensione specifica dal file delle recensioni, identificandola tramite username, nome e luogo del Ristorante.
 * Se la recensione esiste, viene rimossa dal file; altrimenti, viene segnalato che non è stata trovata.
 *
 * @param username  L'username dell'utente che ha scritto la recensione.
 * @param nomeRis   Il nome del Ristorante associato alla recensione.
 * @param luogoRis  Il luogo del Ristorante associato alla recensione.
 */
    public static Boolean eliminaRecensione(int idRecensione, int idUtenteAutore) {

    String sql = "DELETE FROM recensione " +
                 "WHERE id_recensione = ? AND id_utente_autore = ?;";

    try {
        int rCoinvolte = db.execute(sql, idRecensione, idUtenteAutore);

        if (rCoinvolte > 0) {
            System.out.println("Recensione eliminata con successo.");
            return true;
        } else {
            System.err.println("Errore: nessuna recensione trovata o non sei l'autore.");
            return false;
        }
    } catch (SQLException e) {
        System.err.println("Errore durante l'eliminazione della recensione: " + e.getMessage());
        return false;
    }
}
    
/**
 * Modifica una recensione esistente nel file delle recensioni, identificandola tramite username,
 * nome e luogo del Ristorante. Aggiorna il voto e il testo della recensione se la voce è trovata.
 *
 * @param username       L'username dell'utente che ha scritto la recensione.
 * @param nomeRistorante Il nome del Ristorante associato alla recensione.
 * @param luogoRis       Il luogo del Ristorante associato alla recensione.
 * @param voto           Il nuovo voto assegnato al Ristorante.
 * @param nuovaRec       Il nuovo testo della recensione.
 */
public static Boolean modificaRecensione(int idRecensione, int idUtenteAutore, String testo, int valutazione) {

    // Validazione client-side
    if (testo == null || testo.isEmpty()) {
        System.err.println("Il testo della recensione non può essere vuoto.");
        return false;
    }

    if (valutazione < 1 || valutazione > 5) {
        System.err.println("La valutazione deve essere tra 1 e 5.");
        return false;
    }

    String sql = "UPDATE recensione " +
                 "SET testo = ?, valutazione = ?, data_recensione = CURRENT_DATE " +
                 "WHERE id_recensione = ? AND id_utente_autore = ?;";

    try {
        int rCoinvolte = db.execute(sql, testo, valutazione, idRecensione, idUtenteAutore);

        if (rCoinvolte > 0) {
            System.out.println("Recensione modificata con successo.");
            return true;
        } else {
            System.err.println("Errore: nessuna recensione trovata o non sei l'autore.");
            return false;
        }
    } catch (SQLException e) {
        System.err.println("Errore durante la modifica della recensione: " + e.getMessage());
        return false;
    }
}


/**metodo per la ricerca delle coordinate geografiche di una città, utile per la ricerca dei ristoranti nelle vicinanze
 * se la citta indicata dall'utente è nel db vengono restituite le coordinate, altrimenti l'utente può inserire manualmente le coordinate
 * @return un array di double contenente latitudine e longitudine
 */

public static double[] findCoordinates() {
    double[] coord = new double[2];
    System.out.println("al fine di operare con coordinate geografiche corrette inserire nome citta(in inglese se sono grandi città (non varese)) e codice paese(es. IT per italia)");
    Scanner scanner = new Scanner(System.in);
    System.out.println("inserire citta");
    String nomeCitta = scanner.nextLine().trim();
    System.out.println("inserire codice paese");
    String codicePaese = scanner.nextLine().trim();

    String sql = "SELECT\n" +
            "    c.nome,\n" +
            "    c.country_code,\n" +
            "    r.nome AS regione,\n" +
            "   \n" +
            "    c.lat,\n" +
            "    c.lon\n" +
            "FROM citta c\n" +
            "LEFT JOIN regioni r\n" +
            "    ON r.codice = c.country_code || '.' || c.admin1_code\n" +
            "\n" +
            "WHERE c.nome ILIKE ?\n" +
            "  AND c.country_code ILIKE ?";
    List<Map<String, Object>> risultati = null;
    try {
        risultati = db.executeSelect(sql, nomeCitta, codicePaese);
        if (risultati.size() == 1) {
            Map<String, Object> citta = risultati.get(0);
            lat = (Double) citta.get("lat");
            lon = (Double) citta.get("lon");
        } else if (risultati.size() > 1) {
            System.out.println("Trovate più città corrispondenti. Seleziona la città corretta:");
            for (int i = 0; i < risultati.size(); i++) {
                Map<String, Object> citta = risultati.get(i);
                String nome = (String) citta.get("nome");
                String regione = (String) citta.get("regione");
                String countryCode = (String) citta.get("country_code");
                System.out.printf("%d: %s, %s (%s)%n", i + 1, nome, regione, countryCode);
            }
            while(true) {
                try {
                    int scelta = scanner.nextInt();
                    if (scelta >= 1 && scelta <= risultati.size()) {
                        Map<String, Object> cittaScelta = risultati.get(scelta - 1);
                        lat = (Double) cittaScelta.get("lat");
                        lon = (Double) cittaScelta.get("lon");
                        break;
                    } else {
                        System.out.println("Scelta non valida. Riprova.");
                    }
                }
                catch(InputMismatchException e) {
                    System.err.println("inserire un valore valido");
                    scanner.next();
                }
            }
        } else {
            while(true) {
                try {
                    System.out.println("Città non trovata. Inserire coordinate manualmente.");
                    System.out.println("Inserire latitudine:");
                    lat = scanner.nextDouble();
                    if(lat<-90 || lat>90) {throw new InputMismatchException();}
                    System.out.println("Inserire longitudine:");
                    lon = scanner.nextDouble();
                    if(lon<-180 || lon>180) {throw new InputMismatchException();}

                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Input non valido. Assicurati di inserire numeri validi per latitudine e longitudine.");
                    scanner.nextLine();
                }
            }
        }
        coord[0] = lat;
        coord[1] = lon;
        System.out.println("le coordinate sono lat:"+coord[0]+", lon:"+coord[1]);

    } catch (SQLException e) {
        System.err.println("Errore durante l'esecuzione della query: " + e.getMessage());

    }
    return coord;
}

}

