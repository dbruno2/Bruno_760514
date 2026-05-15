package src.dto;
/*
 * Sebastiano Svezia 760462 VA
 * Davide Bruno 760514 VA 
 * Fancesco Vieri 761195 VA
 * Leonardo Bighetti 760015 VA
 */


import java.util.Calendar;

/**
 * La classe {@code Utente} rappresenta un utente che può essere un cliente o un ristoratore.
 * Contiene informazioni personali come nome, cognome, username, password, data di nascita,
 * luogo di domicilio, ruolo e una lista di ristoranti preferiti.
 * 
 * La classe include metodi per registrare un nuovo utente e per gestire le sue informazioni.
 */
public class Utente {

    /**
     * Nome dell'utente.
     */
    private String nome;

    /**
     * Cognome dell'utente.
     */
    private String cognome;

    /**
     * Username scelto dall'utente per accedere al sistema.
     */
    private String username;

    /**
     * Password associata all'account dell'utente.
     */
    private String password;

    /**
     * Data di nascita dell'utente.
     */
    private Calendar data_nascita;

    /**
     * Luogo di domicilio dell'utente (città o indirizzo generico).
     */
    private String luogo_domicilio;

    /**
     * Ruolo dell'utente nel sistema (es. "cliente", "ristoratore", "amministratore").
     */
    private String ruolo;

    /**
     * Elenco dei ristoranti preferiti dall'utente.
     */
    private Ristorante[] preferiti;

    
	/**
	 * Restituisce il nome dell'utente.
	 * 
	 * @return nome dell'utente
	 */
	public String getNome() {
		return nome;
	}
    /**
     * Imposta il nome dell'utente.
     * 
     * @param nome nome da impostare
     */
	public void setNome(String nome) {
		this.nome = nome;
	}
    /**
     * Restituisce il cognome dell'utente.
     * 
     * @return cognome dell'utente
     */
	public String getCognome() {
		return cognome;
	}
    /**
     * Imposta il cognome dell'utente.
     * 
     * @param cognome cognome da impostare
     */
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
    /**
     * Restituisce lo username dell'utente.
     * 
     * @return username dell'utente
     */
	public String getUsername() {
		return username;
	}
    /**
     * Imposta lo username dell'utente.
     * 
     * @param username username da impostare
     */
	public void setUsername(String username) {
		this.username = username;
	}

    /**
     * Restituisce la password dell'utente.
     * 
     * @return password dell'utente
     */
	public String getPassword() {
		return password;
	}
    /**
     * Imposta la password dell'utente.
     * 
     * @param password password da impostare
     */
	public void setPassword(String password) {
		this.password = password;
	}
    /**
     * Restituisce la data di nascita dell'utente.
     * 
     * @return data di nascita dell'utente
     */
	public Calendar getData_nascita() {
		return data_nascita;
	}
    /**
     * Imposta la data di nascita dell'utente.
     * 
     * @param data_nascita data di nascita da impostare
     */
	public void setData_nascita(Calendar data_nascita) {
		this.data_nascita = data_nascita;
	}
    /**
     * Restituisce il luogo di domicilio dell'utente.
     * 
     * @return luogo di domicilio dell'utente
     */
	public String getLuogo_domicilio() {
		return luogo_domicilio;
	}
    /**
     * Imposta il luogo di domicilio dell'utente.
     * 
     * @param luogo_domicilio luogo di domicilio da impostare
     */
	public void setLuogo_domicilio(String luogo_domicilio) {
		this.luogo_domicilio = luogo_domicilio;
	}
    /**
     * Restituisce il ruolo dell'utente (cliente o ristoratore).
     * 
     * @return ruolo dell'utente
     */
	public String getRuolo() {
		return ruolo;
	}
    /**
     * Imposta il ruolo dell'utente.
     * 
     * @param ruolo ruolo da impostare (cliente o ristoratore)
     */
	public void setRuolo(String ruolo) {
		this.ruolo = ruolo;
	}
    /**
     * Restituisce la lista dei ristoranti preferiti dell'utente.
     * 
     * @return array di ristoranti preferiti
     */
	public Ristorante[] getPreferiti() {
		return preferiti;
	}
    /**
     * Imposta la lista dei ristoranti preferiti dell'utente.
     * 
     * @param preferiti array di ristoranti da impostare come preferiti
     */
	public void setPreferiti(Ristorante[] preferiti) {
		this.preferiti = preferiti;
	}
}