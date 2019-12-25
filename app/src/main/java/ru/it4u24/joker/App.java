package ru.it4u24.joker;


import android.app.Application;

public class App extends Application {

    private static NoteRepository noteRepository;
    private static Keystore keystore;
    private static Keystore keystore;

    @Override
    public void onCreate() {
        super.onCreate();


        /* Конкретная реализация выбирается только здесь.
           Изменением одной строчки здесь,
           мы заменяем реализацию во всем приложении!
        */

        noteRepository = new FileNoteRepository(this); /// Хранение заметок в файле
        /noteRepository = new SqliteNoteRepository(this); /// Хранение заметок в sqlite
        keystore = new SimpleKeystore(getSharedPreferences("MyPrefs", MODE_PRIVATE));
        /keystore = new HashedKeystore(this);
    }

    // Возвращаем интерфейс, а не конкретную реализацию!
    public static NoteRepository getNoteRepository() {
        return noteRepository;
    }
    // Возвращаем интерфейс, а не конкретную реализацию!
    public static Keystore getKeystore() {
        return keystore;
    }

}
