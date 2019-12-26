package ru.it4u24.joker;

public class Rc {

    private String name;
    private int id;
    private int worktime;
    private int minut;

    public Rc(String name, int id, int worktime, int minut) {
        this.name = name;
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
