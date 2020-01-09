package ru.it4u24.joker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ElectronicQueue {

    private String datetime;
    private String time;
    private String chassis;
    private Date date;
    private boolean enabled;

    public ElectronicQueue(String datetime, String chassis) {
        this.datetime = datetime;
        this.time = datetime.substring(11, 16);
        this.chassis = chassis;

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());

        try {
            this.date = ft.parse(datetime);
        }catch (ParseException e) {
            System.out.println("Нераспаршена с помощью " + ft);
            e.printStackTrace();
        }
    }

    public String getDatetime() {
        return datetime;
    }

    public Date getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getChassis() {
        return chassis;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setChassis(String chassis) {
        this.chassis = chassis;
    }
}
