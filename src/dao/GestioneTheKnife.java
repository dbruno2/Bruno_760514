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

import mapper.Mapper;
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

    PostgresDB db;

    public GestioneTheKnife(PostgresDB db) {
        this.db = db;
    }

    public GestioneTheKnife() {

    }
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
    public static void visualizzaRiepilogo(int usernameRistoratore) {
    if (fileRistorantiPath == null || fileRecensioniPath == null) {
        System.err.println("Errore: file non configurati.");
        return;
    }

    try (
        BufferedReader brRistoranti = new BufferedReader(new FileReader(fileRistorantiPath));
        BufferedReader brRecensioni = new BufferedReader(new FileReader(fileRecensioniPath))
    ) {
        // Mappa Ristorante -> citta
        Map<String, String> ristorantiDelRistoratore = new HashMap<>();
        String lineaRistorante;
        while ((lineaRistorante = brRistoranti.readLine()) != null) {
            String[] campi = lineaRistorante.split(";", -1); // <-- USA ; come separatore corretto
            if (campi.length >= 4) {
                String nomeRistorante = campi[0].trim();
                String proprietario = campi[1].trim();

                if (proprietario.equalsIgnoreCase(String.valueOf(usernameRistoratore))) {
                    String citta = campi[3].trim();
                    ristorantiDelRistoratore.put(nomeRistorante, citta);
                }
            }
        }

        if (ristorantiDelRistoratore.isEmpty()) {
            System.out.println("Non hai ancora registrato ristoranti.");
            return;
        }

        // Inizializza riepiloghi
        Map<String, Integer> sommaStelle = new HashMap<>();
        Map<String, Integer> conteggioRecensioni = new HashMap<>();

        String lineaRecensione;
        while ((lineaRecensione = brRecensioni.readLine()) != null) {
            String[] campi = lineaRecensione.split(",", -1);
            if (campi.length >= 3) {
                String nomeRistRec = campi[1].split(";")[0].trim(); // nome del Ristorante
                int stelle = 0;
                try {
                    stelle = Integer.parseInt(campi[2].trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                if (ristorantiDelRistoratore.containsKey(nomeRistRec)) {
                    sommaStelle.put(nomeRistRec, sommaStelle.getOrDefault(nomeRistRec, 0) + stelle);
                    conteggioRecensioni.put(nomeRistRec, conteggioRecensioni.getOrDefault(nomeRistRec, 0) + 1);
                }
            }
        }

        for (String nomeRist : ristorantiDelRistoratore.keySet()) {
            int totale = sommaStelle.getOrDefault(nomeRist, 0);
            int count = conteggioRecensioni.getOrDefault(nomeRist, 0);
            double media = (count == 0) ? 0 : (double) totale / count;

            System.out.println(nomeRist + " - " + ristorantiDelRistoratore.get(nomeRist));
            System.out.println("Media valutazioni: " + String.format("%.2f", media) + " su " + count + " recensioni");
            System.out.println("------------------------------------");
        }

    } catch (IOException e) {
        System.err.println("Errore durante la lettura dei file: " + e.getMessage());
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
public static void visualizzaRecensioniPerRistorante(String nomeRistorante) {
    if (fileRecensioniPath == null) {
        System.err.println("Errore: path file recensioni non configurato.");
        return;
    }

    boolean trovate = false;
    int totaleStelle = 0;
    int numeroRecensioni = 0;

    try (BufferedReader brRecensioni = new BufferedReader(new FileReader(fileRecensioniPath))) {
        String linea;
        while ((linea = brRecensioni.readLine()) != null) {
            String[] campi = linea.split(",", -1);
            if (campi.length >= 4) {
                String nomeRistoranteRecensione = campi[1].split(";")[0].trim(); // prendi solo prima parte
                if (nomeRistoranteRecensione.equalsIgnoreCase(nomeRistorante.trim())) {
                    trovate = true;
                    numeroRecensioni++;
                    try {
                        totaleStelle += Integer.parseInt(campi[2]);
                    } catch (NumberFormatException e) {
                        // ignora voto non valido
                    }

                    System.out.println("== Recensione per: " + campi[1] + " ==");
                    System.out.println("Utente: " + campi[0]);
                    System.out.println("Valutazione: " + campi[2] + "/5");
                    System.out.println("Testo: " + campi[3]);
                    String risposta = campi.length >= 5 && campi[4] != null && !campi[4].trim().isEmpty() && !campi[4].trim().equalsIgnoreCase("null")
                                      ? campi[4] : "Nessuna";
                    System.out.println("Risposta: " + risposta);
                    System.out.println("----------------------------------------");
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Errore nella lettura del file delle recensioni");
    }

    if (!trovate) {
        System.out.println("Nessuna recensione per il Ristorante: " + nomeRistorante);
    } else {
        double media = numeroRecensioni > 0 ? (double) totaleStelle / numeroRecensioni : 0;
        System.out.println("Numero recensioni: " + numeroRecensioni);
        System.out.printf("Media stelle: %.2f\n", media);
    }
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

    if (fileRistorantiPath == null || fileRecensioniPath == null) {
        System.err.println("Errore: i path dei file non sono stati configurati.");
        return;
    }

    List<String> ristorantiGestiti = new LinkedList<>();

    try (BufferedReader brRistoranti = new BufferedReader(new FileReader(fileRistorantiPath))) {
        String linea;
        while ((linea = brRistoranti.readLine()) != null) {
            String[] campi = linea.split(";", -1);
            if (campi.length > 1 && campi[1].equals(usernameLoggato)) {
                ristorantiGestiti.add(campi[0]);
            }
        }
    } catch (IOException e) {
        System.err.println("Errore nella lettura del file dei ristoranti");
        return;
    }

    if (ristorantiGestiti.isEmpty()) {
        System.out.println("Non gestisci alcun Ristorante.");
        return;
    }

    for (String Ristorante : ristorantiGestiti) {
        int numeroRecensioni = 0;
        int totaleStelle = 0;
        boolean trovate = false;

        System.out.println("\nRecensioni per Ristorante: " + Ristorante);

        try (BufferedReader brRecensioni = new BufferedReader(new FileReader(fileRecensioniPath))) {
            String linea;
            while ((linea = brRecensioni.readLine()) != null) {
                String[] campi = linea.split(",", -1);
                if (campi.length >= 4) {
                    String nomeRistoranteRecensione = campi[1].split(";")[0].trim();
                    if (nomeRistoranteRecensione.equalsIgnoreCase(Ristorante.trim())) {
                        trovate = true;
                        numeroRecensioni++;
                        try {
                            totaleStelle += Integer.parseInt(campi[2]);
                        } catch (NumberFormatException e) {
                            // ignora voto non valido
                        }
                        System.out.println("Utente: " + campi[0]);
                        System.out.println("Valutazione: " + campi[2] + "/5");
                        System.out.println("Testo: " + campi[3]);
                        String risposta = campi.length >= 5 && campi[4] != null && !campi[4].trim().isEmpty() && !campi[4].trim().equalsIgnoreCase("null")
                                          ? campi[4] : "Nessuna";
                        System.out.println("Risposta: " + risposta);
                        System.out.println("----------------------------------------");
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file delle recensioni");
        }

        if (!trovate) {
            System.out.println("Non ci sono recensioni per questo Ristorante.");
        } else {
            double media = numeroRecensioni > 0 ? (double) totaleStelle / numeroRecensioni : 0;
            System.out.println("Numero recensioni: " + numeroRecensioni);
            System.out.printf("Media stelle: %.2f\n", media);
        }
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
public static boolean rispondiRecensione(int usernameLoggato, String nomeRistorante, String usernameCliente, String risposta) {

    boolean ristoranteTrovato = false;

    try (BufferedReader br = new BufferedReader(new FileReader(fileRistorantiPath))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            String[] campi = linea.split(";", -1);
            if (campi.length >= 2) {
                String nomeRis = campi[0].trim();
                String usernameProprietario = campi[1].trim();

            }
        }
    } catch (IOException e) {
        System.err.println("Errore durante la lettura del file ristoranti.");
        return false;
    }

    if (!ristoranteTrovato) {
        System.err.println("Errore: Ristorante non trovato o utente non autorizzato.");
        return false;
    }

    List<String> recensioniAggiornate = new ArrayList<>();
    boolean rispostaAggiunta = false;

    try (BufferedReader br = new BufferedReader(new FileReader(fileRecensioniPath))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            String[] campi = linea.split(",", -1);
            if (campi.length == 5) {
                String user = campi[0].trim();
                String[] nomeELuogo = campi[1].split(";", 2);
                if (nomeELuogo.length == 2) {
                    String nomeRis = nomeELuogo[0].trim();
                    if (user.equalsIgnoreCase(usernameCliente.trim()) && nomeRis.equalsIgnoreCase(nomeRistorante.trim())) {
                        String rispostaEsistente = campi[4].trim();
                        if (rispostaEsistente.equalsIgnoreCase("Nrisposta") || rispostaEsistente.isEmpty()) {
                            campi[4] = risposta;
                            rispostaAggiunta = true;
                        } else {
                            System.err.println("Errore: questa recensione ha gia una risposta. Non è possibile aggiungerne un'altra.");
                            return false;
                        }
                        String nuovaLinea = String.join(",", campi);
                        recensioniAggiornate.add(nuovaLinea);
                        continue;
                    }
                }
            }
            recensioniAggiornate.add(linea);
        }
    } catch (IOException e) {
        System.err.println("Errore durante la lettura del file recensioni.");
        return false;
    }

    if (!rispostaAggiunta) {
        System.err.println("Errore: recensione specificata non trovata.");
        return false;
    }

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileRecensioniPath, false))) {
        for (String riga : recensioniAggiornate) {
            bw.write(riga);
            bw.newLine();
        }
    } catch (IOException e) {
        System.err.println("Errore durante la scrittura del file recensioni.");
        return false;
    }

    return true;
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
    public static boolean aggiungiPreferito(String usernameCliente, String nomeRistorante, String luogoRistorante) {    //aggiunge un Ristorante al campo preferiti dell'utente che ha effettuato il login

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
    public static boolean rimuoviPreferito(String usernameCliente, String nomeRistorante, String luogoRistorante) {     //rimuove un Ristorante al campo preferiti dell'utente che ha effettuato il login

        List<String> utentiAggiornati = new ArrayList<>();
        boolean aggiornato = false;

        if (usernameCliente == null || nomeRistorante == null || luogoRistorante == null ||
            usernameCliente.isEmpty() || nomeRistorante.isEmpty() || luogoRistorante.isEmpty()) {       //se uno di questi campi non esiste il codice non può essere eseguito
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
                    String daRimuovere = nomeRistorante + ";" + luogoRistorante;

                    if (!preferiti.isEmpty()) {     //se il campo preferiti è vuoto allora non può esserci un Ristorante da rimuovere
                        String[] ristoranti = preferiti.split("\\.");
                        List<String> preferitiAggiornati = new ArrayList<>();

                        for (String Ristorante : ristoranti) {
                            if (!Ristorante.trim().equalsIgnoreCase(daRimuovere)) {
                                preferitiAggiornati.add(Ristorante);
                            } else {
                                aggiornato = true; //in questo caso il Ristorante è stato trovato e rimosso
                            }
                        }

                        campi[7] = String.join(".", preferitiAggiornati);
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
            System.out.println("Utente non trovato o Ristorante non presente nei preferiti.");
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

        return true;
    }

    /**
     * Visualizza tutti i ristoranti preferiti dell'utente specificato.
     * @param usernameCliente username dell'utente
     */
    public static void visualizzaPreferiti(String usernameCliente) {        //permetti di visualizzare tutti i preferiti dell'utente che ha effettuato l'accesso
    
        if (usernameCliente == null || usernameCliente.isEmpty()) {     //se questo campo è vuoto o è null non si può eseguire il codice
            System.out.println("Username non valido.");
            return;
        }
    
        try (BufferedReader br = new BufferedReader(new FileReader(fileUtentiPath))) {
            String linea;
            boolean trovato = false;
    
            while ((linea = br.readLine()) != null) {
                String[] campi = linea.split(",", -1);
    
                if (campi.length < 8) continue;
    
                if (campi[2].equalsIgnoreCase(usernameCliente)) {
                    trovato = true;
                    String preferiti = campi[7].trim();
    
                    if (preferiti.isEmpty()) {      //se preferiti è vuoto allora stampa che non ci sono preferiti
                        System.out.println("Nessun Ristorante preferito trovato.");
                    } else {
                        String[] ristoranti = preferiti.split("\\.");
                        System.out.println("Ristoranti preferiti di " + usernameCliente + ":");
                        for (String Ristorante : ristoranti) {
                            String[] dettagli = Ristorante.split(";");
                            if (dettagli.length == 2) {     //non dovrebbe succedere ma, se c'è solo il nome del Ristorante senza il luogo, allora stampa solo il nome
                                System.out.println("- " + dettagli[0].trim() + " (" + dettagli[1].trim() + ")");
                            } else {
                                System.out.println("- " + Ristorante.trim());
                            }
                        }
                    }
                    break;
                }
            }
    
            if (!trovato) {
                System.out.println("Utente non trovato.");
            }
    
        } catch (IOException e) {
            System.err.println("Errore durante la lettura del file utenti: " + e.getMessage());
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
public static boolean aggiungiRecensione(String username, String nomeRistorante, String luogoRistorante, String valutazione, String testoRecensione) {

    if (username == null || nomeRistorante == null || luogoRistorante == null ||
        valutazione == null || testoRecensione == null ||
        username.isEmpty() || nomeRistorante.isEmpty() || luogoRistorante.isEmpty() ||
        valutazione.isEmpty() || testoRecensione.isEmpty()) {
        System.out.println("Campi non validi.");
        return false;
    }

    // Verifica che il Ristorante esista
    boolean esiste = false;
    try (BufferedReader reader = new BufferedReader(new FileReader(fileRistorantiPath))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] campi = linea.split(";", -1);
            if (campi.length >= 5) {
                String nomeFile = campi[0].trim().toLowerCase();
                String cittaFile = campi[3].trim().toLowerCase();

                if (nomeFile.equals(nomeRistorante.trim().toLowerCase()) &&
                    cittaFile.equals(luogoRistorante.trim().toLowerCase())) {
                    esiste = true;
                    break;
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Errore nella lettura del file ristoranti: " + e.getMessage());
        return false;
    }

    if (!esiste) {
        System.out.println("Errore: il Ristorante specificato non esiste.");
        return false;
    }

    // Controlla se l'utente ha gia recensito questo Ristorante nello stesso luogo
    try (BufferedReader br = new BufferedReader(new FileReader(fileRecensioniPath))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            String[] parti = linea.split(",", -1);
            if (parti.length >= 3) {
                String usernameFile = parti[0].trim();
                String[] ristoranteLuogo = parti[1].split(";", -1);
                if (ristoranteLuogo.length >= 2) {
                    String nomeFile = ristoranteLuogo[0].trim();
                    String luogoFile = ristoranteLuogo[1].trim();

                    if (usernameFile.equalsIgnoreCase(username.trim()) &&
                        nomeFile.equalsIgnoreCase(nomeRistorante.trim()) &&
                        luogoFile.equalsIgnoreCase(luogoRistorante.trim())) {
                        System.out.println("Errore: l'utente ha gia inserito una recensione per questo Ristorante.");
                        return false;
                    }
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Errore nella lettura del file recensioni: " + e.getMessage());
        return false;
    }

    // Se tutto ok, aggiungi la recensione
    String Ristorante = nomeRistorante + ";" + luogoRistorante;
    String nuovaRecensione = String.join(",", username, Ristorante, valutazione, testoRecensione, "Nrisposta");

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileRecensioniPath, true))) {
        writer.write(nuovaRecensione);
        writer.newLine();
    } catch (IOException e) {
        System.err.println("Errore durante la scrittura del file recensioni: " + e.getMessage());
        return false;
    }

    return true;
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
    double media = 0.0;
    int sommaVoti = 0;
    int conteggio = 0;

    try (BufferedReader recReader = new BufferedReader(new FileReader(fileRecensioniPath))) {
        String linea;
        while ((linea = recReader.readLine()) != null) {
            String[] recCampi = linea.split(",", -1);
            if (recCampi.length > 2 && recCampi[1].equalsIgnoreCase(nomeRistorante)) {
                try {
                    int voto = Integer.parseInt(recCampi[2]);
                    sommaVoti += voto;
                    conteggio++;
                } catch (NumberFormatException e) {
                    // Ignora voti non validi
                }
            }
        }
    } catch (IOException e) {
        System.err.println("Errore nella lettura delle recensioni");
    }

    if (conteggio > 0) {
        media = (double) sommaVoti / conteggio;
    }
    return media;
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

    List<String> recensioniAggiornate = new ArrayList<>();
    boolean eliminata = false;

    try (BufferedReader br = new BufferedReader(new FileReader(fileRecensioniPath))) {
        String linea;
        while ((linea = br.readLine()) != null) {
            String[] parti = linea.split(",", 6);
            if (parti.length < 3) continue;

            String fileUsername = parti[0].trim();
            String[] nomeELuogo = parti[1].split(";", 2);
            if (nomeELuogo.length < 2) continue;

            String fileNomeRis = nomeELuogo[0].trim();
            String fileLuogoRis = nomeELuogo[1].trim();

            if (fileUsername.equalsIgnoreCase(username.trim()) &&
                fileNomeRis.equalsIgnoreCase(nomeRis.trim()) &&
                fileLuogoRis.equalsIgnoreCase(luogoRis.trim())) {
                eliminata = true;
            } else {
                recensioniAggiornate.add(linea);
            }
        }
    } catch (IOException e) {
        System.err.println("Errore durante la lettura del file recensioni: " + e.getMessage());
        return;
    }

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileRecensioniPath, false))) {
        for (String riga : recensioniAggiornate) {
            bw.write(riga);
            bw.newLine();
        }
        if (eliminata) {
            System.out.println("Recensione eliminata con successo.");
        } else {
            System.out.println("Nessuna recensione trovata corrispondente ai parametri forniti.");
        }
    } catch (IOException e) {
        System.err.println("Errore durante la scrittura del file recensioni: " + e.getMessage());
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
public static void modificaRecensione(String username, String nomeRistorante, String luogoRis, int voto, String nuovaRec) {

    File file = new File(fileRecensioniPath);

    if (!file.exists()) {
        System.out.println("Il file delle recensioni non esiste.");
        return;
    }

    List<String> recensioniAggiornate = new ArrayList<>();
    boolean trovata = false;

    String ristoranteTarget = nomeRistorante.trim().toLowerCase() + ";" + luogoRis.trim().toLowerCase();

    try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
        String linea;
        while ((linea = reader.readLine()) != null) {
            String[] campi = linea.split(",", -1);

            if (campi.length >= 5) {
                String usernameRec = campi[0].trim();
                String ristoranteRec = campi[1].trim().toLowerCase();

                if (usernameRec.equals(username.trim()) && ristoranteRec.equals(ristoranteTarget)) {
                    // Trovata: aggiorna la riga
                    String nuovaLinea = String.join(",", username, campi[1], String.valueOf(voto), nuovaRec, campi[4]);
                    recensioniAggiornate.add(nuovaLinea);
                    trovata = true;
                } else {
                    recensioniAggiornate.add(linea);
                }
            } else {
                recensioniAggiornate.add(linea); // linea corrotta, lasciala com'è
            }
        }
    } catch (IOException e) {
        System.err.println("Errore durante la lettura del file recensioni: " + e.getMessage());
        return;
    }

    if (!trovata) {
        System.out.println("Recensione non trovata. Verifica username, Ristorante e luogo.");
        return;
    }

    try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
        for (String riga : recensioniAggiornate) {
            writer.write(riga);
            writer.newLine();
        }
    } catch (IOException e) {
        System.err.println("Errore durante la scrittura del file recensioni: " + e.getMessage());
        return;
    }

    System.out.println("Recensione modificata con successo.");
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
    try (BufferedReader br = new BufferedReader(new FileReader(fileRistorantiPath))) {
        String line;
        while ((line = br.readLine()) != null) {
            String[] campi = line.split(";");
            if (campi.length < 5) continue;

            String nomeRistorante = campi[0].trim();
            String citta = campi[3].trim();

            if (nomeRistorante.equalsIgnoreCase(nome.trim()) && citta.equalsIgnoreCase(luogo.trim())) {
                System.out.println("Ristorante trovato!");
                return true;
            }
        }
    } catch (IOException e) {
        System.err.println("Errore lettura file ristoranti: " + e.getMessage());
    }
    return false;
}
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

