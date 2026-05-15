
package src.sicurezzaPassword;
/*
 * Sebastiano Svezia 760462 VA
 * Davide Bruno 760514 VA 
 * Fancesco Vieri 761195 VA
 * Leonardo Bighetti 760015 VA
 */

/**
 * La classe {@code Criptazione} fornisce metodi per la criptazione e la decriptazione di stringhe
 * utilizzando un semplice algoritmo di cifratura basato sullo spostamento dei caratteri nel loro 
 * valore ASCII di una quantita fissa (CHIAVE).
 * 
 * <p>Questo tipo di cifratura è una versione semplificata e non sicura di cifratura, 
 * utilizzata solo come esempio o per applicazioni non critiche.</p>
 */
public class Criptazione {
	    /**
     * La chiave utilizzata per la criptazione e decriptazione. In questo caso, la chiave è 6, 
     * il che significa che ogni carattere del testo verra spostato di 6 posizioni nel suo valore ASCII.
     */

	private static final int CHIAVE = 6;
	/**
     * Cripta una stringa di testo chiaro (non criptato) spostando ogni carattere di un numero fisso 
     * di posizioni nel suo valore ASCII.
     * 
     * @param testoChiaro La stringa di testo da criptare.
     * @return Una stringa criptata, dove ogni carattere è stato spostato nel suo valore ASCII.
     */
	public static String critta(String testoChiaro) {
		String risultato = new String();
		 // Itera attraverso ogni carattere della stringa e applica lo spostamento
		for (int i = 0; i < testoChiaro.length(); i++) {
			char c = testoChiaro.charAt(i);
			char carattereCriptato = (char) (c + CHIAVE);
			risultato = risultato + carattereCriptato;
		}

		return risultato;
	}
	 /**
     * Decripta una stringa di testo criptata spostando ogni carattere nel suo valore ASCII di 
     * un numero fisso di posizioni in senso opposto (contrario alla criptazione).
     * 
     * @param testoCriptato La stringa di testo criptata da decriptare.
     * @return Una stringa decriptata, dove ogni carattere è stato riportato alla sua forma originale.
     */
	public static String decritta(String testoCriptato) {
		String risultato = new String();
		 // Itera attraverso ogni carattere della stringa criptata e applica lo spostamento inverso
		for (int i = 0; i < testoCriptato.length(); i++) {
			char c = testoCriptato.charAt(i);
			char carattereDecriptato = (char) (c - CHIAVE);
			risultato = risultato + carattereDecriptato;
		}

		return risultato;
	}

}