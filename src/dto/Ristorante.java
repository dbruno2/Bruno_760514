package src.dto;
/*
 * Sebastiano Svezia 760462 VA
 * Davide Bruno 760514 VA 
 * Fancesco Vieri 761195 VA
 * Leonardo Bighetti 760015 VA
 */

/**
 * La classe {@code Ristorante} rappresenta un Ristorante registrato nel sistema.
 * Contiene informazioni relative al nome, posizione, fascia di prezzo, servizi disponibili
 * (delivery e prenotazione), tipo di cucina e username del ristoratore.
 * 
 * Include metodi per accedere e modificare i dati del Ristorante, visualizzare i dettagli
 * e cercare ristoranti in base a diversi criteri.
 */
public class Ristorante {

  /**
     * Nome del ristorante.
     */
    private String nome;

    /**
     * Nome utente del ristoratore che gestisce il ristorante.
     */
    private String username_ristoratore;

    /**
     * Nazione in cui si trova il ristorante.
     */
    private String nazione;

    /**
     * Città in cui si trova il ristorante.
     */
    private String citta;

    /**
     * Indirizzo completo del ristorante.
     */
    private String indirizzo;

    /**
     * Latitudine della posizione geografica del ristorante.
     */
    private double latitudine;

    /**
     * Longitudine della posizione geografica del ristorante.
     */
    private double longitudine;

    /**
     * Fascia di prezzo del ristorante (espressa in scala numerica, es. da 1 a 3).
     */
    private int prezzo;

    /**
     * Indica se il ristorante offre il servizio di consegna a domicilio.
     */
    private boolean disponibilita_delivery;

    /**
     * Indica se il ristorante accetta prenotazioni.
     */
    private boolean disponibilita_prenotazione;

    /**
     * Tipo di cucina offerta dal ristorante (es. Italiana, Cinese, Messicana, ecc.).
     */
    private String tipo_Cucina;


    /**
 * Costruttore completo per creare un nuovo oggetto Ristorante con tutti i parametri specificati.
 *
 * @param nome                     il nome del Ristorante
 * @param username_ristoratore    lo username associato al ristoratore
 * @param nazione                 la nazione dove si trova il Ristorante
 * @param citta                   la citta in cui si trova il Ristorante
 * @param indirizzo               l'indirizzo completo del Ristorante
 * @param latitudine              la latitudine geografica del Ristorante
 * @param longitudine             la longitudine geografica del Ristorante
 * @param prezzo                  il prezzo medio indicativo del Ristorante
 * @param disponibilita_delivery  true se il Ristorante offre servizio di consegna a domicilio
 * @param disponibilita_prenotazione true se è possibile prenotare un tavolo
 * @param tipo_Cucina             il tipo di cucina offerta (es. italiana, giapponese, vegana)
 */
    public Ristorante(String nome, String username_ristoratore, String nazione, String citta, String indirizzo,
                      double latitudine, double longitudine, int prezzo, boolean disponibilita_delivery,
                      boolean disponibilita_prenotazione, String tipo_Cucina) {

        this.nome = nome;
        this.username_ristoratore = username_ristoratore;
        this.nazione = nazione;
        this.citta = citta;
        this.indirizzo = indirizzo;
        this.latitudine = latitudine;
        this.longitudine = longitudine;
        this.prezzo = prezzo;
        this.disponibilita_delivery = disponibilita_delivery;
        this.disponibilita_prenotazione = disponibilita_prenotazione;
        this.tipo_Cucina = tipo_Cucina;
    }


    /**
 * Costruttore vuoto per la creazione di un oggetto Ristorante senza inizializzazione immediata.
 * Necessario ad esempio per operazioni di deserializzazione o framework che richiedono un costruttore predefinito.
 */
    public Ristorante() {

    }
    /**
 * Restituisce il nome del Ristorante.
 *
 * @return Il nome del Ristorante.
 */

public String getNome() {
    return nome;
}

/**
 * Imposta il nome del Ristorante.
 *
 * @param nome Il nome da assegnare al Ristorante.
 */
public void setNome(String nome) {
    this.nome = nome;
}

/**
 * Restituisce l'username del ristoratore.
 *
 * @return L'username del ristoratore.
 */
public String getUsername_ristoratore() {
    return username_ristoratore;
}

/**
 * Imposta l'username del ristoratore.
 *
 * @param username_ristoratore L'username da assegnare al ristoratore.
 */
public void setUsername_ristoratore(String username_ristoratore) {
    this.username_ristoratore = username_ristoratore;
}

/**
 * Restituisce la nazione del Ristorante.
 *
 * @return La nazione del Ristorante.
 */
public String getNazione() {
    return nazione;
}

/**
 * Imposta la nazione del Ristorante.
 *
 * @param nazione La nazione da assegnare al Ristorante.
 */
public void setNazione(String nazione) {
    this.nazione = nazione;
}

/**
 * Restituisce la citta del Ristorante.
 *
 * @return La citta del Ristorante.
 */
public String getCitta() {
    return citta;
}

/**
 * Imposta la citta del Ristorante.
 *
 * @param citta La citta da assegnare al Ristorante.
 */
public void setCitta(String citta) {
    this.citta = citta;
}

/**
 * Restituisce l'indirizzo del Ristorante.
 *
 * @return L'indirizzo del Ristorante.
 */
public String getIndirizzo() {
    return indirizzo;
}

/**
 * Imposta l'indirizzo del Ristorante.
 *
 * @param indirizzo L'indirizzo da assegnare al Ristorante.
 */
public void setIndirizzo(String indirizzo) {
    this.indirizzo = indirizzo;
}

/**
 * Restituisce la latitudine del Ristorante.
 *
 * @return La latitudine del Ristorante.
 */
public double getLatitudine() {
    return latitudine;
}

/**
 * Imposta la latitudine del Ristorante.
 *
 * @param latitudine La latitudine da assegnare al Ristorante.
 */
public void setLatitudine(double latitudine) {
    this.latitudine = latitudine;
}

/**
 * Restituisce la longitudine del Ristorante.
 *
 * @return La longitudine del Ristorante.
 */
public double getLongitudine() {
    return longitudine;
}

/**
 * Imposta la longitudine del Ristorante.
 *
 * @param longitudine La longitudine da assegnare al Ristorante.
 */
public void setLongitudine(double longitudine) {
    this.longitudine = longitudine;
}

/**
 * Restituisce il prezzo medio del Ristorante.
 *
 * @return Il prezzo medio del Ristorante.
 */
public int getPrezzo() {
    return prezzo;
}

/**
 * Imposta il prezzo medio del Ristorante.
 *
 * @param prezzo Il prezzo medio da assegnare al Ristorante.
 */
public void setPrezzo(int prezzo) {
    this.prezzo = prezzo;
}

/**
 * Indica se il Ristorante offre servizio di delivery.
 *
 * @return {@code true} se il Ristorante offre delivery, {@code false} altrimenti.
 */
public boolean isDisponibilita_delivery() {
    return disponibilita_delivery;
}

/**
 * Imposta la disponibilita del servizio di delivery.
 *
 * @param disponibilita_delivery {@code true} se il Ristorante offre delivery, {@code false} altrimenti.
 */
public void setDisponibilita_delivery(boolean disponibilita_delivery) {
    this.disponibilita_delivery = disponibilita_delivery;
}

/**
 * Indica se il Ristorante accetta prenotazioni online.
 *
 * @return {@code true} se il Ristorante accetta prenotazioni, {@code false} altrimenti.
 */
public boolean isDisponibilita_prenotazione() {
    return disponibilita_prenotazione;
}

/**
 * Imposta la disponibilita del servizio di prenotazione.
 *
 * @param disponibilita_prenotazione {@code true} se il Ristorante accetta prenotazioni, {@code false} altrimenti.
 */
public void setDisponibilita_prenotazione(boolean disponibilita_prenotazione) {
    this.disponibilita_prenotazione = disponibilita_prenotazione;
}

/**
 * Restituisce il tipo di cucina del Ristorante.
 *
 * @return Il tipo di cucina del Ristorante.
 */
public String getTipo_Cucina() {
    return tipo_Cucina;
}

/**
 * Imposta il tipo di cucina del Ristorante.
 *
 * @param tipo_Cucina Il tipo di cucina da assegnare al Ristorante.
 */
public void setTipo_Cucina(String tipo_Cucina) {
    this.tipo_Cucina = tipo_Cucina;
}

}