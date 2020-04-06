package wabilytics;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "eventos")
public class Eventos {

    @DatabaseField
    private String mensaje;

    public Eventos() {
        // ORMLite needs a no-arg constructor
    }

    public Eventos(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    

    

}