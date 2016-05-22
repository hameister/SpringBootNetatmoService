package org.hameister.model;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by hameister on 14.05.16.
 */
@Entity
@Table(name = "Temperatur")
public class Temperatur {

    @Id
    @GeneratedValue
    Long id;

    @Column(name = "temperatur")
    String temperatur;

    @Column(name = "date")
    Date date;

    public Temperatur() {
    }


    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTemperatur() {
        return temperatur;
    }

    public void setTemperatur(String temperatur) {
        this.temperatur = temperatur;
    }
}
