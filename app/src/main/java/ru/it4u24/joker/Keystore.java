package ru.it4u24.joker;


interface Keystore {
    String getLogin(String key);
    String getPassword(String key);
    void setLogin(String key, String login);
    void setPassword(String key, String password);

}