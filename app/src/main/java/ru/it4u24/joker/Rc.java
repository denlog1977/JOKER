package ru.it4u24.joker;

public class Rc {

    private String name;
    private String UID;
    private int id;
    private int worktime;
    private int minut;

    public Rc(String name, String UID, int id, int worktime, int minut) {
        this.name = name;
        this.UID = UID;
        this.id = id;
        this.worktime = worktime;
        this.minut = minut;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorktime() {
        return worktime;
    }

    public void setWorktime(int worktime) {
        this.worktime = worktime;
    }

    public int getMinut() {
        return minut;
    }

    public void setMinut(int minut) {
        this.minut = minut;
    }

}
