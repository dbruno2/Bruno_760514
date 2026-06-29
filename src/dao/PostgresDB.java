package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * classe che si occupa della comunicazione tra applicazione e db
 */
public class PostgresDB {

    private final String url;
    private final String user;
    private final String password;
    /**
     * costruttore oggetto, valori in input:
     * @param url indirizzo del database, ad esempio "jdbc:postgresql://localhost:5432/theKnife"
     * @param user nome utente per l'accesso al database
     * @param password password per l'accesso al database
     */
    public PostgresDB(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }
    /**
     * metodo che restituisce una connessione al database
     * @throws SQLException se si verifica un errore nell'accesso al database
     * @return connessione al db
     * */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, user, password);
    }

 /**
  * metodo per eseguire query SQL select, query quindi che restituiscono dati (non un intero che indica il numero di righe interessate all'operazione, come per insert/update/delete)
  * @param sql query SQL da eseguire, con placeholder per i parametri
  * @param params valori da associare ai placeholder della query
  * @return lista di mappe, dove ogni mappa rappresenta una riga del risultato della query, con chiavi i nomi delle colonne e valori i valori corrispondenti
  * @throws SQLException se si verifica un errore nell'accesso al database
  * */
    public List<Map<String, Object>> executeSelect(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData md = rs.getMetaData();
                int cols = md.getColumnCount();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    for (int i = 1; i <= cols; i++) {
                        String colName = md.getColumnLabel(i);
                        Object value = rs.getObject(i);
                        row.put(colName, value);
                    }
                    results.add(row);
                }
            }
        }

        return results;
    }

    /**
     * metodo per l'esecuzione di insert/update/delete
     * @param sql query SQL da eseguire, con placeholder per i parametri
     * @param params valori da associare ai placeholder della query
     * @throws SQLException se si verifica un errore nell'accesso al database
     * @return numero di righe modificate/cancellate/inserite dall'operazione
     */
    public int execute(String sql, Object... params) throws SQLException {
        try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            setParameters(ps, params);
            return ps.executeUpdate();
        }
    }
/**
* metodo che associa i parametri dall'array di parametri con i placeholder della query in questione
* si possono in alternativa creare le query concatenando i parametri con la query, ma questo puo portare ad errori
* nelle costruzioni delle query, oltre ad avere un codice piu difficile da manutenere e diciamo piu fragile
 * @param ps PreparedStatement su cui impostare i parametri
 *  @param params valori da associare ai placeholder della query
 *  @throws SQLException se si verifica un errore nell'accesso al database
* */
    private void setParameters(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;

        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }

}

