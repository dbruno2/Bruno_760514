
package sicurezzaPassword;
import org.mindrot.jbcrypt.BCrypt;

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


	private static final int CHIAVE = 6;

	/**critta una password utilizzando l'algoritmo bcrypt, passando come argomenti la password in chiaro e un salt generato con fattore di lavoro (cost) pari a 12.
	il cost factor determina il tempo che bcrypt ci impiega per calcolare l'hash, piu è alto più sarà difficile eseguire un attacco bruteforce, 12 è il valore comunemente usato
	 nonchè buon compromesso tra prestazioni e sicurezza.
	 * @param testoChiaro password da hashare
	 * @return password hashata*/
	public static String critta(String testoChiaro) {

		String pswHashata = BCrypt.hashpw(testoChiaro, BCrypt.gensalt(12));


		return pswHashata;
	}

	/**
	 * Confronta una password inserita dall'utente con una password hashata memorizzata nel database.
	 *
	 * @param pswUtente La password inserita dall'utente.
	 * @param pswDb La password hashata memorizzata nel database.
	 * @return {@code true} se le password corrispondono, {@code false} altrimenti.
	 */
	 public static boolean confronta(String pswUtente,String pswDb){

		return BCrypt.checkpw(pswUtente, pswDb);
	 }

	 /*non serve piu dal momento che non abbiamo bisogno di decrittare le password(non la cancello ancora perche
	 devo vedere se mapper non serve piu, per quel poco che ho visto serve solo per serializzare/deserializzare)
	 per bcrypt c'è matches per confrontare quello che inserisce l'utente come psw
	 * (dopo aver crittato quella psw) con quella nel db, si usa ad esempio:
	 * boolean ok = encoder.matches("password", hash);*/
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