package dto;

import java.io.Serializable;

public class Richiesta implements Serializable {
    TipoOperazione comando;
    Object[] argomenti;

}
