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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    static PostgresDB db;

    public GestioneTheKnife(PostgresDB db) {
        this.db = db;
    }

    public GestioneTheKnife() {

    }
    /**
 * Aggiunge un nuovo Ristorante al sistema, se i dati sono validi e non esiste gia un Ristorante con lo stesso nome e indirizzo.
 * <p>
 * La funzione valida i parametri in input, controlla la presenza di duplicati nel file, crea un nuovo oggetto

 *
 * @param nome                       il nome del Ristorante
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
      int x= db.execute(sql, nome, nazione, citta, indirizzo, latitudine, longitudine, prezzo, disponibilita_delivery, disponibilita_prenotazione, tipo_Cucina, idRistoratore);
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
    public static void visualizzaRiepilogo(int usernameRistoratore) {
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
public static void visualizzaRecensioniPerRistorante(String nomeRistorante) {
}

/**
 * Visualizza tutte le recensioni relative ai ristoranti gestiti da un determinato ristoratore.
 * <p>
 * Il metodo legge i file di ristoranti e recensioni, identifica i ristoranti appartenenti
 * all'utente specificato, e per ciascuno mostra tutte le recensioni disponibili, incluse
 * valutazione, testo della recensione e risposta (se presente). Inoltre, calcola la media delle
 * valutazioni per ogni Ristorante.
 * <p>
 * Se i file richiesti non sono configurati, o se il ristoratore non gestisce ristoranti,
 * viene mostrato un messaggio informativo.
 *
 * @param usernameLoggato lo username del ristoratore per cui visualizzare le recensioni
 */
public static void visualizzaRecensioniPerRistoratore(int usernameLoggato) {
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
public static boolean rispondiRecensione(int usernameLoggato, String nomeRistorante, String usernameCliente, String risposta) {
    return false;
}

/**
 * Aggiunge un Ristorante alla lista dei preferiti di un utente, se non è gia presente.
 * <p>
 * Il metodo verifica che i dati in input siano validi, cerca l'utente nel file degli utenti,
 * controlla che il Ristorante non sia gia nei suoi preferiti, e in tal caso lo aggiunge.
 * I dati vengono poi salvati riscrivendo l'intero file.
 *
 * @param usernameCliente   lo username del cliente che sta effettuando l'aggiunta
 * @param nomeRistorante    il nome del Ristorante da aggiungere ai preferiti
 * @param luogoRistorante   la citta o localita del Ristorante
 * @return true se il Ristorante è stato aggiunto con successo, false in caso di input non valido,
 *         Ristorante gia presente o errore durante lettura/scrittura del file
 */
   /* public static boolean aggiungiPreferito(int id,) {    //aggiunge un Ristorante al campo preferiti dell'utente che ha effettuato il login

        List<String> utentiAggiornati = new ArrayList<>();
        boolean aggiornato = false;

        if (usernameCliente == null || nomeRistorante == null || luogoRistorante == null ||
            usernameCliente.isEmpty() || nomeRistorante.isEmpty() || luogoRistorante.isEmpty()) {   //se uno di questi campi non esiste il codice non può essere eseguito
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(fileUtentiPath))) {
            String linea;

            while ((linea = br.readLine()) != null) {
                String[] campi = linea.split(",", -1);

                if (campi.length < 8) {     //questa parte permette di evitare la IndexOutOfBoundsException
                    utentiAggiornati.add(linea);
                    continue;
                }

                if (campi[2].equalsIgnoreCase(usernameCliente)) {
                    String preferiti = campi[7].trim();
                    String nuovoPreferito = nomeRistorante + ";" + luogoRistorante;

                    boolean giaPresente = false;
                    if (!preferiti.isEmpty()) {
                        String[] ristoranti = preferiti.split("\\.");
                        for (String Ristorante : ristoranti) {      //se il Ristorante è gia tra i preferiti non viene inserito nuovamente
                            if (Ristorante.trim().equalsIgnoreCase(nuovoPreferito)) {
                                giaPresente = true;
                                break;
                            }
                        }
                    }

                    if (!giaPresente) {     //se tutto è andato a buon fine aggiungo il nuovo Ristorante preferito
                        if (preferiti.isEmpty()) {
                            preferiti = nuovoPreferito;
                        } else {
                            preferiti += "." + nuovoPreferito;
                        }
                        campi[7] = preferiti;
                        aggiornato = true;
                    }

                    String nuovaLinea = String.join(",", campi);
                    utentiAggiornati.add(nuovaLinea);
                } else {
                    utentiAggiornati.add(linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file utenti: " + e.getMessage());
            return false;
        }

        if (!aggiornato) {
            System.out.println("Utente non trovato o Ristorante gia nei preferiti.");
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileUtentiPath))) {
            for (String linea : utentiAggiornati) {
                writer.write(linea);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Errore durante la scrittura del file utenti: " + e.getMessage());
            return false;
        }

        return true;       //se tutto è andato a buon fine ritorno true
    }
*/
  /**
 * Rimuove un Ristorante dalla lista dei preferiti dell'utente specificato.
 * <p>
 * Il metodo verifica la validita dei parametri, legge il file degli utenti, trova l'utente corrispondente
 * e rimuove il Ristorante dai preferiti se presente. Infine, aggiorna il file con i dati modificati.
 * <p>
 * Se il Ristorante non è tra i preferiti o l'utente non è trovato, il metodo restituisce {@code false}.
 *
 * @param usernameCliente   lo username del cliente da cui rimuovere il Ristorante preferito
 * @param nomeRistorante    il nome del Ristorante da rimuovere
 * @param luogoRistorante   la citta o localita del Ristorante da rimuovere
 * @return true se il Ristorante è stato rimosso con successo, false se il Ristorante non era presente,
 *         l'utente non esiste o si è verificato un errore durante la lettura/scrittura del file
 */
    public static boolean rimuoviPreferito(String usernameCliente, String nomeRistorante, String luogoRistorante) {
        return false;
    }

    /**
     * Visualizza tutti i ristoranti preferiti dell'utente specificato.
     * @param usernameCliente username dell'utente
     */
    public static void visualizzaPreferiti(String usernameCliente) {
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
public static boolean aggiungiRecensione(String username, String nomeRistorante, String luogoRistorante, String valutazione, String testoRecensione) {
    return false;
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
            int rows = db.execute(sql, username, password, nome, cognome, ruolo, dataNascita, domicilio);
            return rows > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento nel DB: " + e.getMessage());
            return false;
        }



}
public static void showRecommended(){
        System.out.println("inserire località dalla quale si sta eseguendo theKnife");
        double[] coords= findCoordinates();
        String sql="SELECT *\n" +
                "FROM (\n" +
                "    SELECT *\n" +

                "        ,(\n" +
                "            6371 * ACOS(\n" +
                "                COS(RADIANS(?)) *\n" +
                "                COS(RADIANS(r.latitudine)) *\n" +
                "                COS(RADIANS(r.longitudine) - RADIANS(?)) +\n" +
                "                SIN(RADIANS(?)) *\n" +
                "                SIN(RADIANS(r.latitudine))\n" +
                "            )\n" +
                "        ) AS distanza_km,\n" +
                "        AVG(rec.valutazione) AS valutazione_media\n" +
                "    FROM ristoranti_the_knife r\n" +
                "    LEFT JOIN recensione rec\n" +
                "        ON rec.id_ristorante = r.id_ristorante\n" +
                "    GROUP BY r.id_ristorante, rec.id_recensione\n" +
                ") t\n" +
                "WHERE t.distanza_km <= ?\n" +
                "ORDER BY t.distanza_km;";
    List<Map<String, Object>> risultati=null ;
    try {
        risultati=db.executeSelect(sql,coords[0],coords[1],coords[0],10);
        if (risultati.isEmpty()) {System.out.println("non abbiamo ristoranti nella sua zona da consigliare...");}
        else if (!risultati.isEmpty()) {
            System.out.println("le consigliamo questi ristoranti:");
            for (Map<String, Object> ristorante : risultati) {
                System.out.println("Nome: " + ristorante.get("nome_ristorante"));
                System.out.println("paese: " + ristorante.get("nazione"));
                System.out.println("citta: " + ristorante.get("citta"));
                System.out.println("Tipo di cucina: " + ristorante.get("tipo_cucina"));
                System.out.println("Fascia di prezzo: " + ristorante.get("fascia_prezzo"));
                System.out.println("Delivery: " + (Boolean.TRUE.equals(ristorante.get("delivery")) ? "Si" : "No"));
                System.out.println("Prenotabile: " + (Boolean.TRUE.equals(ristorante.get("prenotabile")) ? "Si" : "No"));
                System.out.println("valutazione: " + ristorante.get("valutazione_media"));
                System.out.println("Distanza: " + Math.round((Double) ristorante.get("distanza_km")) + " km");
                System.out.println("----------------------------------------");
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException(e);
    }

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
    public static void cercaRistorantiAvanzata(
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
        String sql = "SELECT r.* " +
                "FROM ( " +
                "    SELECT r_inner.*, " +
                "           (6371 * 2 * ASIN( " +
                "               SQRT( " +
                "                   POWER(SIN(RADIANS(r_inner.latitudine - ?) / 2), 2) + " +
                "                   COS(RADIANS(?)) * " +
                "                   COS(RADIANS(r_inner.latitudine)) * " +
                "                   POWER(SIN(RADIANS(r_inner.longitudine - ?) / 2), 2) " +
                "               ) " +
                "           )) AS distanza_km " +
                "    FROM ristoranti_the_knife r_inner " +
                ") r " +
                "WHERE r.distanza_km <= ? " +
                "AND LOWER(r.tipo_cucina) = LOWER(COALESCE(?, r.tipo_cucina)) " +
                "AND LENGTH(r.fascia_prezzo) >= COALESCE(LENGTH(?), 1) " +
                "AND LENGTH(r.fascia_prezzo) <= COALESCE(LENGTH(?), 4) " +
                "AND r.delivery = COALESCE(?, r.delivery) " +
                "AND r.prenotabile = COALESCE(?, r.prenotabile) " +
                "AND ( " +
                "    SELECT COALESCE(AVG(rec.valutazione), 0) " +
                "    FROM recensione rec " +
                "    WHERE rec.id_ristorante = r.id_ristorante " +
                ") >= COALESCE(?, 0) " +
                "ORDER BY r.distanza_km ASC;";
        List<Map<String, Object>> risultati = null;
        try {
            risultati = db.executeSelect(sql,lat, lat, lon, rad, cucina,  prezzoMin,  prezzoMax,  delivery, prenotazione, stelleMin );
            if (risultati.isEmpty()) {System.out.println("Non sono stati trovati ristoranti secondo quei criteri");}
            else if (!risultati.isEmpty()) {
                System.out.println("Lista dei risultati:");
                for (Map<String, Object> ristorante : risultati) {
                    System.out.println("Nome: " + ristorante.get("nome_ristorante"));
                    System.out.println("paese: " + ristorante.get("nazione"));
                    System.out.println("citta: " + ristorante.get("citta"));
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

}

/**
 * Calcola la media delle stelle assegnate a un Ristorante, leggendo i voti dalle recensioni memorizzate in un file.
 * Ignora eventuali voti non validi o righe malformate.
 *
 * @param nomeRistorante Il nome del Ristorante di cui calcolare la media delle stelle.
 * @return              La media delle stelle assegnate al Ristorante, o 0.0 se non sono presenti recensioni valide.
 */
    public static double calcolaMediaStelle(String nomeRistorante) {
    return 0.0;
}
    
    /**
 * Elimina una recensione specifica dal file delle recensioni, identificandola tramite username, nome e luogo del Ristorante.
 * Se la recensione esiste, viene rimossa dal file; altrimenti, viene segnalato che non è stata trovata.
 *
 * @param username  L'username dell'utente che ha scritto la recensione.
 * @param nomeRis   Il nome del Ristorante associato alla recensione.
 * @param luogoRis  Il luogo del Ristorante associato alla recensione.
 */
    public static void eliminaRecensione(String username, String nomeRis, String luogoRis) {
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
public static void modificaRecensione(String username, String nomeRistorante, String luogoRis, int voto, String nuovaRec) {
}

/**
 * Verifica se un Ristorante esiste nel file dei ristoranti, confrontando nome e luogo.
 * Se il Ristorante viene trovato, restituisce {@code true} e stampa un messaggio di conferma.
 *
 * @param nome  Il nome del Ristorante da cercare.
 * @param luogo La citta in cui si trova il Ristorante.
 * @return      {@code true} se il Ristorante esiste nel file, {@code false} altrimenti.
 */
public static boolean esisteRistorante(String nome, String luogo) {
    return false;
}
 public static double[] findCoordinates() {
    double[] coord = new double[2];
     double lon;
     double lat;
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

    /**
     * Effettua il parsing della data di nascita inserita dall'utente.
     *
     * @param inputData data nel formato {@code dd/MM/yyyy}; se vuota ritorna 1/1/0000
     * @return {@link Calendar} rappresentante la data
     * @throws IllegalArgumentException se il formato della data non è valido
     */
    public static Calendar parseDataNascita(String inputData) {
        if (inputData == null || inputData.trim().isEmpty()) {
            // Data "vuota": impostiamo 1 gennaio anno 0
            return null;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(inputData));
                return cal;
            } catch (ParseException e) {
                System.out.println("Formato data non valido. Usa dd/MM/yyyy.");
                throw new IllegalArgumentException();

            }
        }
    }

}

