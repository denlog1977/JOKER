package ru.it4u24.joker;

public enum Keys {

    service1cLog ("Логин  для Web-Service 1c", 0),
    service1cPas ("Пароль для Web-Service 1c", 1);

    private String title;
    private int id;

    Keys(String title, int id) {
        this.title = title;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public int getId() {
        return id;
    }
}
