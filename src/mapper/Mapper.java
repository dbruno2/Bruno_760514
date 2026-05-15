package src.mapper;
/*
 * Sebastiano Svezia 760462 VA
 * Davide Bruno 760514 VA 
 * Fancesco Vieri 761195 VA
 * Leonardo Bighetti 760015 VA
 */


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import src.dto.Recensione;
import src.dto.Ristorante;
import src.dto.Utente;
import src.sicurezzaPassword.Criptazione;
/**
 * La classe {@code Mapper} è utilizzata per mappare oggetti di tipo {@link Utente}, {@link Ristorante} e {@link Recensione}
 * da e verso stringhe di testo. Queste stringhe sono solitamente utilizzate per la serializzazione e la deserializzazione
 * di oggetti a partire da file o database.
 * La classe include metodi per la conversione di oggetti in stringhe e viceversa, con particolare attenzione alla gestione
 * della criptazione delle password.
 */
public class Mapper {
  	/**
     * Mappa una stringa contenente i dati di un utente in un oggetto {@link Utente}.
     * 
     * @param linea La stringa contenente i dati dell'utente, separati da virgole.
     * @return Un oggetto {@link Utente} con i dati mappati dalla stringa.
     */
	public static Utente mapObjUtente(String linea) {
	    Utente u = new Utente();
	    String[] valori = linea.split(",");
	    u.setNome(valori[0]);
	    u.setCognome(valori[1]);
	    u.setUsername(valori[2]);
	    u.setPassword(Criptazione.decritta(valori[3]));

	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	    Calendar dat = Calendar.getInstance();
	    try {
	        dat.setTime(sdf.parse(valori[4]));
	    } catch (ParseException e) {
	        e.printStackTrace();
	    }
	    u.setData_nascita(dat);

	    u.setLuogo_domicilio(valori[5]);
	    u.setRuolo(valori[6]);

	    // Mappatura dei ristoranti preferiti
	    if (valori.length > 7 && valori[7] != null && !valori[7].isEmpty()) {
	        String[] ristorantiData = valori[7].split("\\."); // separa i ristoranti
	        Ristorante[] preferiti = new Ristorante[ristorantiData.length];

	        for (int i = 0; i < ristorantiData.length; i++) {
	            String[] dettagli = ristorantiData[i].split(";"); // separa i dettagli del Ristorante
	            if (dettagli.length >= 11) { // Ora almeno 11 campi
	                String nome = dettagli[0];
	                String username_ristoratore = dettagli[1];
	                String nazione = dettagli[2];
	                String citta = dettagli[3];
	                String indirizzo = dettagli[4];
	                int latitudine = Integer.parseInt(dettagli[5]);
	                int longitudine = Integer.parseInt(dettagli[6]);
	                int prezzo = Integer.parseInt(dettagli[7]);
	                boolean disponibilita_delivery = Boolean.parseBoolean(dettagli[8]);
	                boolean disponibilita_prenotazione = Boolean.parseBoolean(dettagli[9]);
	                String tipo_cucina = dettagli[10];

	                preferiti[i] = new Ristorante(nome, username_ristoratore, nazione, citta, indirizzo, latitudine,
	                        longitudine, prezzo, disponibilita_delivery, disponibilita_prenotazione, tipo_cucina);
	            }
	        }
	        u.setPreferiti(preferiti);
	    } else {
	        u.setPreferiti(new Ristorante[0]); // Nessun Ristorante
	    }

	    return u;
	}
	
   	/**
     * Mappa un oggetto {@link Utente} in una stringa di testo separata da virgole.
     * 
     * @param u L'oggetto {@link Utente} da mappare.
     * @return Una stringa contenente i dati dell'utente, separati da virgole.
     */
	public static String mapStrUtente(Utente u) {
		// Mappatura dell'utente
		String result = u.getNome() + "," + u.getCognome() + "," + u.getUsername() + ","
				+ Criptazione.critta(u.getPassword()) + ","; // Criptazione della password

		// Mappatura della data di nascita (in formato "yyyy-MM-dd")
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		result += sdf.format(u.getData_nascita().getTime()) + ",";

		result += u.getLuogo_domicilio() + "," + u.getRuolo() + ",";

		// Mappatura dei ristoranti preferiti
		if (u.getPreferiti() != null && u.getPreferiti().length > 0) {
			for (int i = 0; i < u.getPreferiti().length; i++) {
				Ristorante r = u.getPreferiti()[i];
				result += r.getNome() + ";" + r.getNazione() + ";" + r.getCitta() + ";" + r.getIndirizzo() + ";"
						+ r.getLatitudine() + ";" + r.getLongitudine() + ";" + r.getPrezzo() + ";"
						+ r.isDisponibilita_delivery() + ";" + r.isDisponibilita_prenotazione() + ";"
						+ r.getTipo_Cucina();

				if (i < u.getPreferiti().length - 1) {
					result += "."; // Aggiunge il punto per separare i ristoranti
				}
			}
		}

		return result;
	}
	
/**
 * Converte una stringa contenente i dati di un ristorante in un oggetto {@link Ristorante}.
 * <p>
 * La stringa in input deve contenere almeno 11 campi separati da punto e virgola (;), 
 * con i dati nell'ordine seguente:
 * <ol>
 *   <li>nome</li>
 *   <li>username ristoratore</li>
 *   <li>nazione</li>
 *   <li>città</li>
 *   <li>indirizzo</li>
 *   <li>latitudine (double)</li>
 *   <li>longitudine (double)</li>
 *   <li>prezzo (int)</li>
 *   <li>disponibilità delivery ("Si" per true)</li>
 *   <li>disponibilità prenotazione ("Si" per true)</li>
 *   <li>tipo di cucina</li>
 * </ol>
 * 
 * @param line La stringa con i dati del ristorante separati da punto e virgola.
 * @return Un oggetto {@link Ristorante} popolato con i dati mappati dalla stringa, oppure
 *         {@code null} se la stringa è nulla, vuota, incompleta o in caso di errore nel parsing.
 */
	public static Ristorante mapObjRistorante(String line) {
    if (line == null || line.trim().isEmpty()) return null;

    String[] campi = line.split(";");
    if (campi.length < 11) return null; // riga incompleta

    try {
        String nome = campi[0].trim();
        String username = campi[1].trim();
        String nazione = campi[2].trim();
        String citta = campi[3].trim();
        String indirizzo = campi[4].trim();
        double latitudine = Double.parseDouble(campi[5].trim());
        double longitudine = Double.parseDouble(campi[6].trim());
        int prezzo = Integer.parseInt(campi[7].trim());

        boolean delivery = campi[8].trim().equalsIgnoreCase("Si");
        boolean prenotazione = campi[9].trim().equalsIgnoreCase("Si");
        String tipoCucina = campi[10].trim();

        Ristorante r = new Ristorante();
        r.setNome(nome);
        r.setUsername_ristoratore(username);
        r.setNazione(nazione);
        r.setCitta(citta);
        r.setIndirizzo(indirizzo);
        r.setLatitudine(latitudine);
        r.setLongitudine(longitudine);
        r.setPrezzo(prezzo);
        r.setDisponibilita_delivery(delivery);
        r.setDisponibilita_prenotazione(prenotazione);
        r.setTipo_Cucina(tipoCucina);

        return r;
    } catch (Exception e) {
        System.err.println("Errore nel parsing della riga Ristorante: " + line);
        return null;
    }
}
	/**
     * Mappa un oggetto {@link Ristorante} in una stringa di testo separata da virgole.
     * 
     * @param r L'oggetto {@link Ristorante} da mappare.
     * @return Una stringa contenente i dati del Ristorante, separati da virgole.
     */
	public static String mapStrRistorante(Ristorante r) {
	    // restituisce stringa con i campi separati da virgola in ordine corretto
		String delivery;
		String prenotazione;
		if(r.isDisponibilita_delivery()){
			delivery = "si";
		}else{
			delivery = "no";
		}
		if(r.isDisponibilita_delivery()){
			prenotazione = "si";
		}else{
			prenotazione = "no";
		}
	    return r.getNome() + ";" 
	         + r.getUsername_ristoratore() + ";" 
	         + r.getNazione() + ";" 
	         + r.getCitta() + ";" 
	         + r.getIndirizzo() + ";" 
	         + r.getLatitudine() + ";" 
	         + r.getLongitudine() + ";" 
	         + r.getPrezzo() + ";" 
	         + delivery + ";" 
	         + prenotazione + ";" 
	         + r.getTipo_Cucina();
	}

    /**
     * Mappa una stringa contenente i dati di una recensione in un oggetto {@link Recensione}.
     * 
     * @param linea La stringa contenente i dati della recensione, separati da virgole.
     * @return Un oggetto {@link Recensione} con i dati mappati dalla stringa.
     */
    public static Recensione mapObjRecensioni(String linea) {
        Recensione r = new Recensione();
        String[] valori = linea.split(",");
        r.setUsername(valori[0]);
        r.setNomeRistorante(valori[1]);
        r.setValutazione(Integer.parseInt(valori[2]));
        r.setTesto(valori[3]);
        r.setRisposta(valori[4]);
        return r;
    }
    /**
     * Mappa un oggetto {@link Recensione} in una stringa di testo separata da virgole.
     * 
     * @param r L'oggetto {@link Recensione} da mappare.
     * @return Una stringa contenente i dati della recensione, separati da virgole.
     */
    public static String mapStrRecensione(Recensione r) {
        String s;
        String username = r.getUsername();
        String nomeRistorante = "" + r.getNomeRistorante();
        String valutazione = "" + r.getValutazione();
        String testo = "" + r.getTesto();
        String risposta = "" + r.getRisposta();
        s = username + "," + nomeRistorante + "," + valutazione + "," + testo + "," + risposta;
        return s;
    }

}
