package ru.it4u24.joker;


import android.app.Application;

public class App extends Application {

    private static KeystoreSharedPreferens keystoreSharedPreferens;
    private static KeystoreFireBase keystoreFireBase;

    @Override
    public void onCreate() {
        super.onCreate();


        /* Конкретная реализация выбирается только здесь.
           Изменением одной строчки здесь,
           мы заменяем реализацию во всем приложении!
        */

        keystoreSharedPreferens = new KeystoreSharedPreferens(getSharedPreferences("MyPrefs", MODE_PRIVATE));
        keystoreFireBase = new KeystoreFireBase(this);

    }

    public static KeystoreSharedPreferens getKeystoreSharedPreferens() {
        return keystoreSharedPreferens;
    }
    public static KeystoreFireBase getKeystore() {
        return keystoreFireBase;
    }

}
