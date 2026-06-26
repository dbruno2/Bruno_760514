package serverTK;

import dao.PostgresDB;
import java.util.List;
import java.util.Map;

public class serverTK {

    /**
    giusto per farci 2 prove della classe che interagisce con db, questo sarebbe il server nell'architettura master slave in realtà, giusto?
     */
    public static void main(String[] args) {
        String url = "jdbc:postgresql://localhost:5432/theKnife";
        String user = "postgres";
        String pass =  "qwerty";
/*nota: non so se deve girare in locale o meno, eventualmente basterebbe solo mettere il mio indirzzo ip al posto di localhost e abilitare l'accesso su postgres*/


        PostgresDB db = new PostgresDB(url, user, pass);

        try {
            List<Map<String, Object>> rows = db.executeSelect("SELECT version() AS pg_version", (Object[]) null);
            System.out.println("Risultato test connessione:");
            for (Map<String, Object> row : rows) {
                System.out.println(row);
            }
        } catch (Exception e) {
            System.err.println("Errore durante test connessione al DB: " + e.getMessage());
            e.printStackTrace();
        }
    }

}