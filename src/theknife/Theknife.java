package src.theknife;
/*
 * Sebastiano Svezia 760462 VA
 * Davide Bruno 760514 VA 
 * Fancesco Vieri 761195 VA
 * Leonardo Bighetti 760015 VA
 */



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Scanner;
import src.dao.*;
import src.sicurezzaPassword.*;
/**
 * TheKnife – Applicazione console per la gestione di ristoranti.
 * 
 * Consente a clienti e ristoratori di registrarsi, effettuare il login
 * e usufruire di varie funzionalita come la ricerca avanzata, la gestione delle
 * recensioni ed i preferiti.
 * 
 * 
 *   I clienti possono: aggiungere/rimuovere ristoranti dai preferiti,
 *       scrivere/modificare/eliminare recensioni, consultare le proprie liste
 *       ed effettuare ricerche.
 *   I ristoratori possono: inserire ristoranti, visualizzare riepiloghi
 *       o singole recensioni e rispondere ad esse.
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
            System.out.println("1. Login Cliente");
            System.out.println("2. Login Ristoratore");
            System.out.println("3. Registrati");
            System.out.println("4. Cerca Ristorante (guest)");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");

            switch (scanner.nextLine()) {
                case "1" -> login("cliente");
                case "2" -> login("ristoratore");
                case "3" -> registrazione();
                case "4" -> cercaRistoranti();
                case "0" -> running = false;
                default -> System.out.println("Scelta non valida");
            }
        }
    }
    /**
     * Gestisce la procedura di login per clienti o ristoratori.
     * 
     * @param ruolo "cliente" oppure "ristoratore"
     * @return {@code true} se il login va a buon fine, altrimenti {@code false}
     */
    private static boolean login(String ruolo) {
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        String passwordCriptata = Criptazione.critta(password);

        boolean success = false;
        if ("cliente".equals(ruolo)) {
            success = GestioneTheKnife.loginUtenteU(username, passwordCriptata, ruolo);
        } else if ("ristoratore".equals(ruolo)) {
            success = GestioneTheKnife.loginUtenteR(username, passwordCriptata, ruolo);
        }

        if (success) {
            System.out.println("Login riuscito con successo!");
            if ("cliente".equals(ruolo)) {
                menuCliente(username);
            } else {
                menuRistoratore(username);
            }
        } else {
            System.out.println("Login fallito. Username o password errati.");
        }
        return success;
    }
    /**
     * Gestisce la procedura di registrazione di un nuovo utente.
     * <p>Richiede l'inserimento dei dati obbligatori e, in caso di successo,
     * delega alla classe {@code GestioneTheKnife} la persistenza delle
     * informazioni.</p>
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

        Calendar dataNascitaCal = parseDataNascita(dataNascitaInput);
        if (dataNascitaCal == null) {
            System.out.println("Registrazione annullata a causa di data non valida.");
            return;
        }

        System.out.print("Domicilio: ");
        String domicilio = scanner.nextLine().trim();
        System.out.print("Ruolo (cliente/ristoratore): ");
        String ruolo = scanner.nextLine().trim().toLowerCase();

        if (domicilio.isEmpty() || (!ruolo.equals("cliente") && !ruolo.equals("ristoratore"))) {
            System.out.println("Domicilio obbligatorio e ruolo deve essere 'cliente' o 'ristoratore'.");
            return;
        }

        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dataNascitaStr = outputFormat.format(dataNascitaCal.getTime());

        boolean registrato = GestioneTheKnife.registraUtente(
                nome, cognome, username, passwordCriptata,
                dataNascitaStr, domicilio, ruolo, ""
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
     * @return {@link Calendar} rappresentante la data, oppure {@code null} se il formato è errato
     */
    private static Calendar parseDataNascita(String inputData) {
        if (inputData == null || inputData.trim().isEmpty()) {
            // Data "vuota": impostiamo 1 gennaio anno 0
            return new GregorianCalendar(0, 0, 1);
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                sdf.setLenient(false);
                Calendar cal = Calendar.getInstance();
                cal.setTime(sdf.parse(inputData));
                return cal;
            } catch (ParseException e) {
                System.out.println("Formato data non valido. Usa dd/MM/yyyy.");
                return null;
            }
        }
    }
    /**
     * Mostra il menu dedicato ai clienti e gestisce le relative azioni.
     * 
     * @param username username del cliente autenticato
     */
    private static void menuCliente(String username) {
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
            System.out.println("0. Logout");
            System.out.print("Scelta: ");

            switch (scanner.nextLine()) {
                case "1" -> {
                    System.out.print("Nome Ristorante: ");
                    String nome = scanner.nextLine().trim();
                    System.out.print("Luogo: ");
                    String luogo = scanner.nextLine().trim();

                    if (!GestioneTheKnife.esisteRistorante(nome, luogo)) {
                        System.out.println("Errore: il Ristorante indicato non esiste.");
                    } else {
                        boolean aggiunto = GestioneTheKnife.aggiungiPreferito(username, nome, luogo);
                        if (aggiunto) System.out.println("Ristorante aggiunto ai preferiti.");
                        else System.out.println("Errore nell'aggiunta ai preferiti.");
                    }
                }
                case "2" -> {
                    System.out.print("Nome Ristorante: ");
                    String nome = scanner.nextLine();
                    System.out.print("Luogo: ");
                    String luogo = scanner.nextLine();
                    boolean rimosso = GestioneTheKnife.rimuoviPreferito(username, nome, luogo);
                    if (rimosso) System.out.println("Ristorante rimosso dai preferiti.");
                    else System.out.println("Errore nella rimozione dai preferiti.");
                }
                case "3" -> GestioneTheKnife.visualizzaPreferiti(username);
                case "4" -> {
                    System.out.print("Nome Ristorante: ");
                    String nome = scanner.nextLine();
                    System.out.print("Luogo Ristorante: ");
                    String luogo = scanner.nextLine();
                    int voto = leggiNumero("Voto (1-5): ");
                    System.out.print("Testo recensione: ");
                    String testo = scanner.nextLine();

                    boolean recensioneAggiunta = GestioneTheKnife.aggiungiRecensione(
                            username, nome, luogo, String.valueOf(voto), testo
                    );
                    if (recensioneAggiunta) {
                        System.out.println("Recensione aggiunta con successo.");
                    } else {
                        System.out.println("Errore nell'aggiunta della recensione.");
                    }
                }
                case "5" -> cercaRistoranti();
                case "6" -> {
                    System.out.println("inserisci il nome del Ristorante della recensione che vuoi modificare:");
                    String nomeRis = scanner.nextLine();
                    System.out.println("inserisci il luogo del Ristorante della recensione che vuoi modificare:");
                    String luogoRis = scanner.nextLine();
                    System.out.println("inserisci il nuovo voto");
                    int nuovoVoto = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("inserisci la nuova recensione");
                    String nuovaRec = scanner.nextLine();
                    GestioneTheKnife.modificaRecensione(username, nomeRis, luogoRis, nuovoVoto, nuovaRec);
                }
                case "7"->{ 
                    System.out.println("inserisci il nome del Ristorante della recensione che vuoi eliminare:");
                    String nomeRis = scanner.nextLine();
                    System.out.println("inserisci il luogo del Ristorante della recensione che vuoi eliminare:");
                    String luogoRis = scanner.nextLine();
                    GestioneTheKnife.eliminaRecensione(username, nomeRis, luogoRis);

                }
                case "0" -> back = true;
                default -> System.out.println("Scelta non valida");
            }
        }
    }

    /**
     * Mostra il menu dedicato ai ristoratori e gestisce le relative azioni.
     * 
     * @param username username del ristoratore autenticato
     */
    private static void menuRistoratore(String username) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Menu Ristoratore ---");
            System.out.println("1. Aggiungi Ristorante");
            System.out.println("2. Visualizza riepilogo recensioni");
            System.out.println("3. Visualizza recensioni");
            System.out.println("4. Rispondi a recensione");
            System.out.println("5. Cerca Ristorante");
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
                    int lat = leggiNumero("Latitudine: ");
                    int lon = leggiNumero("Longitudine: ");
                    int prezzo = leggiNumero("Prezzo medio: ");
                    System.out.print("Delivery (true/false): ");
                    boolean delivery = leggiBoolean();
                    System.out.print("Prenotazione online (true/false): ");
                    boolean prenotazione = leggiBoolean();
                    System.out.print("Tipo cucina: ");
                    String cucina = scanner.nextLine();

                    boolean aggiunto = GestioneTheKnife.aggiungiRistorante(
                            nome, username, nazione, citta, indirizzo, lat, lon, prezzo,
                            delivery, prenotazione, cucina
                    );

                    if (aggiunto) {
                        System.out.println("Ristorante aggiunto con successo.");
                    } else {
                        System.out.println("Errore nell'aggiunta del Ristorante.");
                    }
                }
                case "2" -> GestioneTheKnife.visualizzaRiepilogo(username);
                case "3" -> GestioneTheKnife.visualizzaRecensioniPerRistoratore(username);
                case "4" -> {
                    System.out.print("Nome Ristorante: ");
                    String nomeRistorante = scanner.nextLine();
                    System.out.print("Username cliente a cui rispondere: ");
                    String usernameCliente = scanner.nextLine();
                    System.out.print("Testo risposta: ");
                    String testoRisposta = scanner.nextLine();

                    boolean rispostaInserita = GestioneTheKnife.rispondiRecensione(
                            username, nomeRistorante, usernameCliente, testoRisposta
                    );

                    if (rispostaInserita) {
                        System.out.println("Risposta inserita con successo.");
                    } else {
                        System.out.println("Errore nell'inserimento della risposta o dati non corretti.");
                    }
                }
                case "5" -> cercaRistoranti();
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
        System.out.println("\n--- Ricerca avanzata ristoranti ---");

        System.out.print("Inserisci zona geografica (obbligatorio): ");
        String zona = scanner.nextLine().trim();
        if (zona.isEmpty()) {
            System.out.println("Zona geografica obbligatoria per la ricerca.");
            return;
        }

        System.out.print("Tipologia di cucina (facoltativo): ");
        String cucina = scanner.nextLine().trim();

        System.out.print("Prezzo minimo (facoltativo, premi Invio per saltare): ");
        Integer prezzoMin = leggiNumeroFacoltativo();

        System.out.print("Prezzo massimo (facoltativo, premi Invio per saltare): ");
        Integer prezzoMax = leggiNumeroFacoltativo();

        System.out.print("Servizio delivery richiesto? (true/false/Invio per no filtro): ");
        Boolean delivery = leggiBooleanFacoltativo();

        System.out.print("Prenotazione online richiesta? (true/false/Invio per no filtro): ");
        Boolean prenotazione = leggiBooleanFacoltativo();

        System.out.print("Valutazione media minima (stelle, 1-5, facoltativo): ");
        Double stelleMin = leggiDoubleFacoltativo();

        List<String> risultati = GestioneTheKnife.cercaRistorantiAvanzata(
                zona, cucina,
                prezzoMin, prezzoMax,
                delivery, prenotazione,
                stelleMin
        );

        if (risultati.isEmpty()) {
            System.out.println("Nessun Ristorante trovato con i criteri indicati.");
        } else {
            stampaRistoranti(risultati);
        }
    }

/**
 * Stampa l'elenco dei ristoranti trovati e richiama la visualizzazione delle recensioni per ogni Ristorante.
 * Ogni Ristorante viene formattato con separatori visivi per migliorarne la leggibilita.
 *
 * @param ristoranti La lista di descrizioni testuali dei ristoranti da stampare.
 */  
private static void stampaRistoranti(List<String> ristoranti) {
    System.out.println("\n--- Ristoranti trovati ---");
    for (String r : ristoranti) {
        System.out.println("------------------------------");
        System.out.println(r);
        System.out.println("------------------------------");

        String[] righe = r.split("\n");
        String primaRiga = righe[0];
        String nomeRistorante = primaRiga.split(",")[0].split("-")[0].trim();

        System.out.println();

        GestioneTheKnife.visualizzaRecensioniPerRistorante(nomeRistorante);

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
 * Legge un valore numerico facoltativo (double) dalla console. Se l'input è vuoto, restituisce {@code null}.
 * Se l'input non è un numero valido, mostra un messaggio di errore e restituisce {@code null}.
 *
 * @return Il valore {@code double} inserito dall'utente o {@code null} se l'input è vuoto o non valido.
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

}