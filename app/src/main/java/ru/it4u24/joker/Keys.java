package ru.it4u24.joker;

import androidx.annotation.NonNull;

public enum Keys {

    service1cLog ("Логин  для Web-Service 1c"),
    service1cPas ("Пароль для Web-Service 1c");

    private String title;

    Keys(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    @NonNull
    @Override
    public String toString() {
        return title;
    }

}
