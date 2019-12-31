package ru.it4u24.joker;

public class ElectronicQueue {

    private String name;
    private String time;
    private String chassis;

    public ElectronicQueue(String name, String chassis) {
        this.name = name;
        this.chassis = chassis;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChassis() {
        return chassis;
    }

    public void setChassis(String chassis) {
        this.chassis = chassis;
    }
}
