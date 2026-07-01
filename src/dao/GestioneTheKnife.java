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

import java.math.BigDecimal;
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
 *
 */
public class GestioneTheKnife {
    

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
 * @param idRistoratore             l'id del ristoratore
 * @param nazione                   la nazione del Ristorante
 * @param citta                     la citta in cui si trova il Ristorante
 * @param indirizzo                 l'indirizzo del Ristorante
 * @param latitudine                la latitudine geografica del Ristorante
 * @param longitudine               la longitudine geografica del Ristorante
 * @param prezzo                    la fascia di prezzo media del Ristorante
 * @param disponibilita_delivery     true se il Ristorante offre consegna a domicilio
 * @param disponibilita_prenotazione true se è possibile prenotare online
 * @param tipo_Cucina               il tipo di cucina offerta
 * @return true se il Ristorante è stato aggiunto correttamente, false in caso di errore
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
 * Visualizza il riepilogo di tutti i ristoranti, quindi mostra nome, citta ristorante(per evitare omonimia) e media voti
    * @param idRistoratore id del ristoratore
    * @return lista contenente nome,citta e valutazione media di ogni ristorante
 */
    public static List<Map<String, Object>> visualizzaRiepilogo(int idRistoratore) {

        String sql = "SELECT r.id_ristorante, r.nome_ristorante, c.nome AS citta, AVG(rec.valutazione) AS media_stelle " +
                     "FROM ristoranti_the_knife r " +
                     "LEFT JOIN recensione rec ON r.id_ristorante = rec.id_ristorante JOIN citta c on r.id_citta=c.id " +
                     "WHERE r.id_utente = ? " +
                     "GROUP BY r.id_ristorante, r.nome_ristorante, c.nome;";

        try {
            List<Map<String, Object>> risultati = db.executeSelect(sql, idRistoratore);

            if (risultati.isEmpty()) {
                System.out.println("Nessun ristorante trovato per l'utente specificato.");
                return risultati;
            }

            System.out.println("=== RIEPILOGO RISTORANTI ===");
            for (Map<String, Object> ristorante : risultati) {
                System.out.println("Nome: " + ristorante.get("nome_ristorante"));
                System.out.println("Città: " + ristorante.get("citta"));
                if(ristorante.get("media_stelle")==null){System.out.println("media: 0.0");}
                else{
                   Double valutazione = ((BigDecimal) ristorante.get("media_stelle")).doubleValue();
                   System.out.println("Valutazione: " + valutazione);
                }
                System.out.println("----------------------------------------");
            }

            return risultati;
        } catch (SQLException e) {
            System.err.println("Errore durante la visualizzazione del riepilogo: " + e.getMessage());
            return null;
        }
}

/**
 * Visualizza recensioni ristorante e statistiche (numero recensioni e media voto)
 * @param idRistorante id del ristorante su cui effettuare la ricerca di recensioni
 * @return lista recensioni, numero recensioni e media voto
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

    String sql1 = "SELECT " +
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
            List<Map<String, Object>> statistiche = db.executeSelect(sql1, idRistorante);

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
    * @param idRecensione recensione alla quale rispondere
    * @param idRistoratoreAutore id del ristoratore che risponde
    * @param testo testo di risposta alla recensione
    * @return true se operazione ha successo, false in caso di errore o risposta già inserita
 */
public static boolean rispondiRecensione(int idRecensione, int idRistoratoreAutore, String testo) {

    // Validazione
    if (testo == null || testo.isEmpty()) {
        System.err.println("Il testo della risposta non può essere vuoto.");
        return false;
    }
    String sql = "INSERT INTO risposta_recensione (testo, id_recensione) " +
                          "VALUES (?,?);";
    try {
        int rCoinvolte = db.execute(sql, testo, idRecensione);

        if (rCoinvolte > 0) {
            System.out.println("Risposta aggiunta con successo.");
            return true;
        } else {
            System.err.println("hai gia risposto alla recensione.");
            return false;
        }
    } catch (SQLException e) {
        System.err.println("Errore durante l'aggiunta della risposta: " + e.getMessage());
        return false;
    }
}




    /**
     * Aggiunge un ristorante alla lista dei preferiti dell'utente.
     * @param idUtente id utente
     * @param idRistorante id ristorante da aggiungere ai preferiti
     * @return true se operazione ha successo, false se il ristorante è già nei preferiti o in caso di errore
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
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Errore durante l'aggiunta del preferito: " + e.getMessage());
            return false;
        }
    }

    /**
     * Visualizza tutti i ristoranti preferiti dell'utente specificato.
     * @param idUtente id utente del quale si vuole visualizzare preferiti
     * @return lista dei ristoranti preferiti
     */
    public static List<Map<String, Object>> visualizzaPreferiti(int idUtente) {
        String sql = "SELECT r.id_ristorante, r.nome_ristorante, c.nome AS citta, r.indirizzo, r.tipo_cucina, r.fascia_prezzo " +
                     "FROM ristoranti_the_knife r " +
                     "JOIN preferiti p ON r.id_ristorante = p.id_ristorante JOIN citta c ON r.id_citta = c.id " +
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
     * @param idUtente l'ID dell'utente
     * @param idRistorante l'ID del ristorante da rimuovere dai preferiti
     * @return true se operazione ha successo altrimenti false
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
  * Aggiunge una recensione a un ristorante
  * @param testo testo della recensione
  * @param valutazione voto recensione
  * @param idUtenteAutore id utente che effettua recensione
  * @param idRistorante id ristorante che riceve recensione
  * @return true se operazione ha successo altrimenti false*/
public static boolean aggiungiRecensione(String testo, int valutazione, int idUtenteAutore, int idRistorante) {

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
 * Permette a un utente di effettuare il login verificando le credenziali fornite.
   * @param username nome utente
   * @param password password
   * @return stringa composta da esitoOperazione+ruoloUtente+idUtente
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
 * Registra un nuovo utente inserendolo nel db
 * @param nome nome dell'utente
 * @param cognome cognome dell'utente
 * @param username username dell'utente
 * @param password password dell'utente
 * @param dataNascita data di nascita dell'utente
 * @param domicilio domicilio dell'utente
 * @param ruolo ruolo dell'utente
 *
 * @return true se l'utente è stato registrato con successo altrimenti false
 */
    public static boolean registraUtente(String nome, String cognome, String username, String password, String dataNascita, String domicilio, String ruolo) {

        String sql = "INSERT INTO utenti (username, password, nome, cognome, ruolo, data_nascita, indirizzo) VALUES (?, ?, ?, ?, ?::tipo_ruolo, ?::date, ?)";
        try {
            int r = db.execute(sql, username, password, nome, cognome, ruolo, dataNascita, domicilio);
            return r > 0;
        } catch (SQLException e) {
            System.err.println("Errore durante l'inserimento nel DB: " + e.getMessage());
            return false;
        }



}
/**
 * Mostra all'utente i ristoranti consigliati in base alla sua posizione in un raggio di 10km
 * @return lista di ristoranti consigliati
 */
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
                Double media = ((BigDecimal) ristorante.get("valutazione_media")).doubleValue();
                System.out.println("Media voti: " +media);
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
 * @return lista di ristoranti che soddisfano i criteri
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

                        Double media = ((BigDecimal) ristorante.get("media_valutazione")).doubleValue();
                        System.out.println("Media voti: " +media);
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
    * Cancella una recensione identificandola tramite idRecensione e idUtenteAutore.
    * @param idRecensione L'ID della recensione da eliminare.
    * @param idUtenteAutore L'ID dell'utente autore della recensione.
    * @return true se la recensione è stata eliminata con successo, false altrimenti.
    * */
    public static Boolean eliminaRecensione(int idRecensione, int idUtenteAutore) {

    String sql = "DELETE FROM recensione " +
                 "WHERE id_recensione = ? AND id_utente_autore = ?;";

    try {
        int rCoinvolte = db.execute(sql, idRecensione, idUtenteAutore);

        if (rCoinvolte > 0) {
            System.out.println("Recensione eliminata con successo.");
            return true;
        } else {
            System.err.println("Errore: nessuna recensione trovata ");
            return false;
        }
    } catch (SQLException e) {
        System.err.println("Errore durante l'eliminazione della recensione: " + e.getMessage());
        return false;
    }
}
    
/**
 * Modifica una recensione identificandola tramite l'ID della recensione e l'ID dell'utente autore.
 * @param idRecensione L'ID della recensione da modificare.
 * @param idUtenteAutore L'ID dell'utente autore della recensione.
 * @param testo Il nuovo testo della recensione.
 * @param valutazione La nuova valutazione della recensione.
 * @return true se la recensione è stata modificata con successo, false altrimenti.
 */
public static Boolean modificaRecensione(int idRecensione, int idUtenteAutore, String testo, int valutazione) {

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
            System.err.println("Errore: nessuna recensione trovata");
            return false;
        }
    } catch (SQLException e) {
        System.err.println("Errore durante la modifica della recensione: " + e.getMessage());
        return false;
    }
}


/**Metodo per la ricerca delle coordinate geografiche di una città, utile per la ricerca dei ristoranti nelle vicinanze
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

