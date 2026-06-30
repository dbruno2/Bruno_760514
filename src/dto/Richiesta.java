package dto;

import java.io.Serializable;

public class Richiesta implements Serializable {


    private final TipoOperazione comando;
    private final Object[] argomenti;

    public Richiesta(TipoOperazione comando, Object... argomenti) {
        this.comando = comando;
        this.argomenti = argomenti;
    }

    public TipoOperazione getComando() {
        return comando;
    }

    public Object[] getArgomenti() {
        return argomenti;
    }
}
