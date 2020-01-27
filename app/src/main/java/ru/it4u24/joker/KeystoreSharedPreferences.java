package ru.it4u24.joker;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class KeystoreSharedPreferences implements Keystore {

    private SharedPreferences sharedPreferences;

    public final String KEY_LOG_SERVICE1C = "service1cLog";
    public final String KEY_PAS_SERVICE1C = "service1cPas";
    public final String KEY_USER_NAME = "userName";
    public final String KEY_USER_EMAIL = "userEmail";
    public final String KEY_USER_PHONE = "userPhone";
    public final String KEY_USER_PHOTO = "userPhoto";
    public final String KEY_STATUS_EMAIL = "userStatusEmail";
    public final String KEY_STATUS_PHONE = "userStatusPhone";
    private final String USER_IMAGE_NAME = "userphoto.jpg";

    public KeystoreSharedPreferences(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public String getLogin(String key) {
        return sharedPreferences.getString(key, "");
    }

    @Override
    public String getPassword(String key) {
        return sharedPreferences.getString(key, "");
    }

    @Override
    public void setLogin(String key, String login) {
        setString(key, login);
    }

    @Override
    public void setPassword(String key, String password) {
        setString(key, password);
    }

    public void setString(String key, String value) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(key, value);
        ed.apply();
        Log.d("myLogs", "Сохранено: " + key + "=" + value);
    }

    public String getString(String key, String defValue) {
        return sharedPreferences.getString(key, defValue);
    }

    public void setLoginPasswordService1c(String login, String password) {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putString(KEY_LOG_SERVICE1C, login);
        ed.putString(KEY_PAS_SERVICE1C, password);
        ed.apply();
        Log.d("myLogs", "Сохранено логин/пароль: " + login  + "/" + password);
    }

    public void setImageStorage(Context context, String key,
                                Bitmap bitmapImage, String nameImage) throws IOException {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File file = new File(directory, nameImage);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }

        setString(key, directory.getAbsolutePath());
    }

    public void setImageStorage(Context context, Bitmap bitmapImage) throws IOException {
        setImageStorage(context, KEY_USER_PHOTO, bitmapImage, USER_IMAGE_NAME);
    }

    public Bitmap getImageStorage(String key, String nameImage) {
        Bitmap bitmapImage = null;
        try {
            File file = new File(getLogin(key), nameImage);
            bitmapImage = BitmapFactory.decodeStream(new FileInputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmapImage;
    }

    public void loadImageFromStorage(ImageView img, int defIdRes) {
        Bitmap bitmap = getImageStorage(KEY_USER_PHOTO, USER_IMAGE_NAME);
        if (bitmap != null) {
            img.setImageBitmap(bitmap);
        } else
            img.setImageResource(defIdRes);
    }

}
