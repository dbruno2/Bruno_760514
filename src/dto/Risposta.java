package dto;

import java.io.Serializable;

public class Risposta implements Serializable {
    private static final long serialVersionUID = 1L;

    private final boolean confermaSuccesso;
    private final String messaggio;
    private final Object[] argomenti;

    public Risposta(boolean confermaSuccesso, Object... argomenti) {
        this(confermaSuccesso, null, argomenti);
    }

    public Risposta(boolean confermaSuccesso, String messaggio, Object... argomenti) {
        this.confermaSuccesso = confermaSuccesso;
        this.messaggio = messaggio;
        this.argomenti = argomenti;
    }

    public Risposta(boolean confermaSuccesso) {
        this(confermaSuccesso, null, (Object[]) null);
    }

    public boolean isConfermaSuccesso() {
        return confermaSuccesso;
    }

    public String getMessaggio() {
        return messaggio;
    }

    public Object[] getArgomenti() {
        return argomenti;
    }
}
