package theknife;
/*
 * Sebastiano Svezia 760462 VA
 * Davide Bruno 760514 VA 
 * Fancesco Vieri 761195 VA
 * Leonardo Bighetti 760015 VA
 */




import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import dao.*;
import sicurezzaPassword.*;
/**
 * TheKnife – Applicazione console per la gestione di ristoranti.
 * 
 * Consente a clienti e ristoratori di registrarsi, effettuare il login
 * e usufruire di varie funzionalita come la ricerca avanzata, la gestione delle
 * recensioni e i preferiti.
 * 
 * 
 *   I clienti possono: aggiungere/rimuovere ristoranti dai preferiti,
 *       scrivere/modificare/eliminare recensioni, consultare le proprie liste
 *       ed effettuare ricerche.
 *   I ristoratori possono: inserire ristoranti, visualizzare riepiloghi
 *       o singole recensioni e rispondere a esse.
 * Tutta la logica di business fa capo alla classe {@code GestioneTheKnife} – qui
 * viene gestita unicamente l'interfaccia utente testuale.
 */

public class Theknife {
     /**
     * Scanner condiviso per la lettura dei comandi da console.
     */
    private static final Scanner scanner = new Scanner(System.in);
    /**
     * Punto di ingresso dell'applicazione.
     * 
     * @param args argomenti passati da riga di comando (non utilizzati)
     */
    public static void main(String[] args) {
           boolean running = true;
        while (running) {
            System.out.println("\n--- Benvenuto in TheKnife ---");
            System.out.println("1. Login");
            System.out.println("2. Registrati");
            System.out.println("3. Cerca Ristorante (guest)");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");

            switch (scanner.nextLine()) {
                case "1" -> login();
                case "2" -> registrazione();
                case "3" -> cercaRistoranti();
                case "0" -> running = false;
                default -> System.out.println("Scelta non valida");
            }
        }
    }
    /**
     * Gestisce la procedura di login per clienti o ristoratori.
     * 
     *
     * @return {@code true} se il login va a buon fine, altrimenti {@code false}
     */
    private static boolean login() {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();





           String success = GestioneTheKnife.login(username, password);


        if (success.contains("true,cliente")) {
            System.out.println("Login riuscito con successo!");
            int id= Integer.parseInt(success.split(",")[2]);
            menuCliente(username, id);

        }else if(success.contains("true,gestore")){
            System.out.println("Login riuscito con successo!");
            int id= Integer.parseInt(success.split(",")[2]);
            menuRistoratore(id);
        }else {
            System.out.println("Login fallito. Username o password errati.");
        }
        return false;
    }
    /**
     * Gestisce la procedura di registrazione di un nuovo utente.
     * <p>Richiede l'inserimento dei dati obbligatori e, in caso di successo,
     * delega alla classe {@code GestioneTheKnife} la persistenza delle
     * informazioni che a sua volta delega a PostGresDB la connessione al server e l'esecuzione della query.
     */
    private static void registrazione() {
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();
        System.out.print("Cognome: ");
        String cognome = scanner.nextLine().trim();
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        if (nome.isEmpty() || cognome.isEmpty() || username.isEmpty() || password.isEmpty()) {
            System.out.println("Tutti i campi obbligatori devono essere compilati.");
            return;
        }

        String passwordCriptata = Criptazione.critta(password);

        System.out.print("Data di nascita (dd/MM/yyyy) - facoltativa, premi Invio per saltare: ");
        String dataNascitaInput = scanner.nextLine().trim();
        Calendar dataNascitaCal;

        try {
            dataNascitaCal = parseDataNascita(dataNascitaInput);
        } catch (IllegalArgumentException e) {
            System.out.println("Registrazione annullata a causa di data non valida.");
            return;
        }
        System.out.print("Domicilio: ");
        String domicilio = scanner.nextLine().trim();
        System.out.print("Ruolo (cliente/gestore): ");
        String ruolo = scanner.nextLine().trim().toLowerCase();

        if (domicilio.isEmpty() || (!ruolo.equals("cliente") && !ruolo.equals("gestore"))) {
            System.out.println("Domicilio obbligatorio e ruolo deve essere 'cliente' o 'gestore'.");
            return;
        }
        String dataNascitaStr=null;
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(dataNascitaCal!=null){  dataNascitaStr = outputFormat.format(dataNascitaCal.getTime());}

        boolean registrato = GestioneTheKnife.registraUtente(
                nome, cognome, username, passwordCriptata,
                dataNascitaStr, domicilio, ruolo
        );

        if (registrato) {
            System.out.println("Registrazione completata con successo!");
        } else {
            System.out.println("Errore durante la registrazione. Riprova.");
        }
    }

    /**
     * Effettua il parsing della data di nascita inserita dall'utente.
     * 
     * @param inputData data nel formato {@code dd/MM/yyyy}; se vuota ritorna 1/1/0000
     * @return {@link Calendar} rappresentante la data
     * @throws IllegalArgumentException se il formato della data non è valido
     */
    private static Calendar parseDataNascita(String inputData) {
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
    /**
     * Mostra il menu dedicato ai clienti e gestisce le relative azioni.
     * 
     * @param username username del cliente autenticato
     */
    private static void menuCliente(String username, int id) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Menu Cliente ---");
            System.out.println("1. Aggiungi Ristorante ai preferiti");
            System.out.println("2. Rimuovi Ristorante dai preferiti");
            System.out.println("3. Visualizza preferiti");
            System.out.println("4. Aggiungi recensione");
            System.out.println("5. Cerca Ristorante");
            System.out.println("6. Modifica recensione");
            System.out.println("7. Elimina recensione");
            System.out.println("8. Visualizza recensioni");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            switch (scanner.nextLine()) {
                case "1" -> {
                    // Ora usiamo gli ID: chiediamo l'id del ristorante e aggiungiamo ai preferiti
                    int idRistoranteAggiungi = leggiNumero("ID Ristorante da aggiungere ai preferiti: ");
                    boolean aggiunto = GestioneTheKnife.aggiungiPreferito(id, idRistoranteAggiungi);
                    if (aggiunto) System.out.println("Ristorante aggiunto ai preferiti.");
                    else System.out.println("Errore nell'aggiunta ai preferiti.");
                }
                case "2" -> {
                    List<Map<String, Object>> risultati =GestioneTheKnife.visualizzaPreferiti(id);
                    if(risultati.isEmpty()){

                        break;
                    }
                    int idRistoranteRimuovi = leggiNumero("ID Ristorante da rimuovere dai preferiti: ");
                    boolean rimosso = GestioneTheKnife.rimuoviPreferito(id, idRistoranteRimuovi);
                    if (rimosso) System.out.println("Ristorante rimosso dai preferiti.");
                    else System.out.println("Errore nella rimozione dai preferiti.");
                }
                case "3" -> GestioneTheKnife.visualizzaPreferiti(id);
                case "4" -> {
                    int idRistoranteRec = leggiNumero("ID Ristorante su cui lasciare la recensione: ");
                    int voto = leggiNumero("Voto (1-5): ");
                    System.out.print("Testo recensione: ");
                    String testo = scanner.nextLine();

                    boolean recensioneAggiunta = GestioneTheKnife.aggiungiRecensione(testo, voto, id, idRistoranteRec);
                    if (recensioneAggiunta) {
                        System.out.println("Recensione aggiunta con successo.");
                    } else {
                        System.out.println("Errore nell'aggiunta della recensione.");
                    }
                }
                case "5" -> cercaRistoranti();
                case "6" -> {
                    int idRecToMod = leggiNumero("ID recensione da modificare: ");
                    int nuovoVoto = leggiNumero("Inserisci il nuovo voto (1-5): ");
                    System.out.println("Inserisci il nuovo testo della recensione:");
                    String nuovaRec = scanner.nextLine();
                    GestioneTheKnife.modificaRecensione(idRecToMod, id, nuovaRec, nuovoVoto);
                }
                case "7"->{ 
                    int idRecToDel = leggiNumero("ID recensione da eliminare: ");
                    GestioneTheKnife.eliminaRecensione(idRecToDel, id);

                }
                case "8" -> {
                    int idRistorante = leggiNumero("ID Ristorante per cui visualizzare le recensioni: ");
                    GestioneTheKnife.visualizzaRecensioniPerRistorante(idRistorante);
                }
                case "0" -> back = true;
                default -> System.out.println("Scelta non valida");
            }
        }
    }

    /**
     * Mostra il menu dedicato ai ristoratori e gestisce le relative azioni.
     * 
     * @param id id del ristoratore autenticato
     */
    private static void menuRistoratore(int id) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Menu Ristoratore ---");
            System.out.println("1. Aggiungi Ristorante");
            System.out.println("2. Visualizza riepilogo recensioni");
            System.out.println("3. Visualizza recensioni");
            System.out.println("4. Rispondi a recensione");
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            switch (scanner.nextLine()) {
                case "1" -> {
                    System.out.print("Nome Ristorante: ");
                    String nome = scanner.nextLine();
                    System.out.print("Nazione: ");
                    String nazione = scanner.nextLine();
                    System.out.print("Citta: ");
                    String citta = scanner.nextLine();
                    System.out.print("Indirizzo: ");
                    String indirizzo = scanner.nextLine();
                    double[] coords = GestioneTheKnife.findCoordinates();
                    double lat =  coords[0];
                    double lon = coords[1];
                    String prezzo = leggiFasciaPrezzo();
                    System.out.print("Delivery (true/false): ");
                    boolean delivery = leggiBoolean();
                    System.out.print("Prenotazione online (true/false): ");
                    boolean prenotazione = leggiBoolean();
                    System.out.print("Tipo cucina: ");
                    String cucina = scanner.nextLine();
                    String codiceNazione=GestioneTheKnife.selectRegion();


                    boolean aggiunto = GestioneTheKnife.aggiungiRistorante(
                            nome, id,citta, indirizzo, lat, lon, prezzo,
                            delivery, prenotazione, cucina, codiceNazione
                    );

                    if (aggiunto) {
                        System.out.println("Ristorante aggiunto con successo.");
                    } else {
                        System.out.println("Errore nell'aggiunta del Ristorante.");
                    }
                }
                case "2" -> GestioneTheKnife.visualizzaRiepilogo(id);
                case "3" -> {
                    // Chiediamo l'ID del ristorante di cui visualizzare le recensioni
                    int idRistorantePerRec = leggiNumero("ID Ristorante di cui visualizzare le recensioni: ");
                    GestioneTheKnife.visualizzaRecensioniPerRistorante(idRistorantePerRec);
                }
                case "4" -> {
                    // Per rispondere chiediamo l'ID della recensione
                    int idRecensione = leggiNumero("ID recensione a cui rispondere: ");
                    System.out.print("Testo risposta: ");
                    String testoRisposta = scanner.nextLine();

                    boolean rispostaInserita = GestioneTheKnife.rispondiRecensione(idRecensione, id, testoRisposta);

                    if (rispostaInserita) {
                        System.out.println("Risposta inserita con successo.");
                    } else {
                        System.out.println("Errore nell'inserimento della risposta o dati non corretti.");
                    }
                }
                case "0" -> back = true;
                default -> System.out.println("Scelta non valida");
            }
        }
    }

/**
 * Gestisce la ricerca avanzata di ristoranti interagendo con l'utente tramite input da console.
 * Permette di filtrare i risultati in base a zona geografica, tipologia di cucina, fascia di prezzo,
 * disponibilita di delivery, prenotazione e valutazione media minima.
 * Se vengono trovati ristoranti corrispondenti ai criteri, li stampa a schermo.
 */
    private static void cercaRistoranti() {
        GestioneTheKnife.showRecommended();
        System.out.println("\n--- Ricerca avanzata ristoranti ---");

        double[] coord = new double[2];
        coord=GestioneTheKnife.findCoordinates();

        System.out.print("Tipologia di cucina (facoltativo): ");
        String cucina = scanner.nextLine().trim();
        if(cucina.isEmpty()){
            cucina = null;}
        System.out.print("Prezzo minimo (facoltativo, premi Invio per saltare, se inserito deve essere compreso tra $ e $$$$): ");
        String prezzoMin = leggiFasciaPrezzo();

        System.out.print("Prezzo massimo (facoltativo, premi Invio per saltare, se inserito deve essere compreso tra $ e $$$$): ");
        String prezzoMax = leggiFasciaPrezzo();

        System.out.print("Servizio delivery richiesto? (true/false/Invio per no filtro): ");
        Boolean delivery = leggiBooleanFacoltativo();

        System.out.print("Prenotazione online richiesta? (true/false/Invio per no filtro): ");
        Boolean prenotazione = leggiBooleanFacoltativo();

        System.out.print("Valutazione media minima (stelle, 1-5, facoltativo): ");
        Double stelleMin = leggiDoubleFacoltativo();

        System.out.println("inserire raggio di ricerca (è in km)");
        int rad=scanner.nextInt();

        System.out.println(coord[0] + ", " + coord[1] + ", " + cucina + ", " + prezzoMin + ", " + prezzoMax + ", " + delivery + ", " + prenotazione + ", " + stelleMin + ", " + rad);
        GestioneTheKnife.cercaRistorantiAvanzata(coord[0], coord[1], cucina, prezzoMin, prezzoMax, delivery, prenotazione, stelleMin, rad);


    }

/**
 * Stampa l'elenco dei ristoranti trovati e richiama la visualizzazione delle recensioni per ogni Ristorante.
 * Ogni Ristorante viene formattato con separatori visivi per migliorarne la leggibilita.
 *
 * @param ristoranti La lista di descrizioni testuali dei ristoranti da stampare.
 */  
private static void stampaRistoranti(List<Map<String, Object>> ristoranti) {
    System.out.println("\n--- Ristoranti trovati ---");
    for (Map<String, Object> r : ristoranti) {
        System.out.println("------------------------------");
        System.out.println("Nome: " + r.get("nome_ristorante"));
        System.out.println("Città: " + r.get("citta"));
        System.out.println("Fascia prezzo: " + r.get("fascia_prezzo"));
        System.out.println("Tipo cucina: " + r.get("tipo_cucina"));
        System.out.println("------------------------------");

        // Recupera l'id in modo sicuro (può essere Integer, Long, ecc.)
        Object idObj = r.get("id_ristorante");
        int idRistorante = 0;
        if (idObj instanceof Number) {
            idRistorante = ((Number) idObj).intValue();
        } else if (idObj != null) {
            try {
                idRistorante = Integer.parseInt(idObj.toString());
            } catch (NumberFormatException ignored) {}
        }

        System.out.println();
        if (idRistorante > 0) {
            GestioneTheKnife.visualizzaRecensioniPerRistorante(idRistorante);
        } else {
            System.out.println("ID ristorante non disponibile, impossibile mostrare le recensioni.");
        }

        System.out.println();
    }
}
/**
 * Legge un numero intero dalla console, ripetendo la richiesta fino a ottenere un input valido.
 *
 * @param messaggio Il messaggio da mostrare all'utente prima della richiesta di input.
 * @return          Il numero intero inserito dall'utente.
 */
    private static int leggiNumero(String messaggio) {
        while (true) {
            System.out.print(messaggio);
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un numero valido.");
            }
        }
    }
/**
 * Legge un numero intero facoltativo dalla console. Se l'input è vuoto, restituisce {@code null}.
 * Se l'input non è un numero valido, mostra un messaggio di errore e restituisce {@code null}.
 *
 * @return Il numero intero inserito dall'utente o {@code null} se l'input è vuoto o non valido.
 */
    private static Integer leggiNumeroFacoltativo() {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            System.out.println("Numero non valido, campo ignorato.");
            return null;
        }
    }
/**
 * Legge un valore booleano dalla console, accettando esclusivamente "true" o "false" come input.
 * Continua a richiedere un valore valido finché non viene fornito un input corretto.
 *
 * @return {@code true} se l'utente inserisce "true", {@code false} se inserisce "false".
 */
    private static boolean leggiBoolean() {
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true")) return true;
            if (input.equals("false")) return false;
            System.out.print("Inserisci 'true' o 'false': ");
        }
    }
/**
 * Legge un valore booleano facoltativo dalla console. Se l'input è vuoto, restituisce {@code null}.
 * Accetta "true" o "false" come valori validi. Se l'input non è valido, mostra un messaggio e restituisce {@code null}.
 *
 * @return {@code true} se l'utente inserisce "true", {@code false} se inserisce "false", {@code null} se l'input è vuoto o non valido.
 */
    private static Boolean leggiBooleanFacoltativo() {
        String input = scanner.nextLine().trim().toLowerCase();
        if (input.isEmpty()) return null;
        if (input.equals("true")) return true;
        if (input.equals("false")) return false;
        System.out.println("Valore non valido, filtro ignorato.");
        return null;
    }
/**
 * Legge un valore numerico facoltativo (double) dalla console. Se il valore in ingresso è vuoto, restituisce {@code null}.
 * Se il valore in ingresso non è un numero valido, mostra un messaggio di errore e restituisce {@code null}.
 *
 * @return Il valore {@code double} inserito dall'utente o {@code null} se il valore in ingresso è vuoto o non valido.
 */
    private static Double leggiDoubleFacoltativo() {
        String input = scanner.nextLine().trim();
        if (input.isEmpty()) return null;
        try {
            return Double.valueOf(input);
        } catch (NumberFormatException e) {
            System.out.println("Numero non valido, filtro ignorato.");
            return null;
        }
    }
    private static String  leggiFasciaPrezzo() {
        System.out.println("Inserisci la fascia di prezzo, tra $ e $$$$, altrimenti premere invio per saltare il criterio: ");
        while(true) {
            String input = scanner.nextLine().trim();
            if(input.isEmpty()){ return null; }
        if(input.equals("$")||input.equals("$$")||input.equals("$$$")||input.equals("$$$$")) return input;
        else System.err.println("il valore inserito deve essere tra $ e $$$$!");}



    }

}