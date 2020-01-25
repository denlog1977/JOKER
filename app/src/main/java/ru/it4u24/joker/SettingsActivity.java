package ru.it4u24.joker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    private ImageView imageView;
    private KeystoreSharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        imageView = findViewById(R.id.ivPhoto);
        CardView cardView = findViewById(R.id.cvPhoto);
        registerForContextMenu(cardView);

        sPref = App.getKeystoreSharedPreferens();
        sPref.loadImageFromStorage(imageView, R.drawable.joker);

        TextView textViewName = findViewById(R.id.tvUserName);
        textViewName.setText(sPref.getString(sPref.KEY_USER_NAME, getString(R.string.prompt_name)));
        //sPref.
        /*cardView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
             }
        });*/
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Выбран фото", Toast.LENGTH_SHORT).show();
            }
        });
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "Выбрано меню", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cardView.setOnContextClickListener(new View.OnContextClickListener() {
                @Override
                public boolean onContextClick(View v) {

                    Toast.makeText(getApplicationContext(), "Выбрано меню c индексом " + v.getId(), Toast.LENGTH_SHORT).show();
                    return false;
                }
            });
        }*/
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        switch (v.getId()) {
            case R.id.cvPhoto:
                menu.add(0, 1, 0, "Выбрать фото");
                menu.add(0, 2, 0, "Удалить фото");
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                Toast.makeText(getApplicationContext(), "Выбрано меню Выбрать фото", Toast.LENGTH_SHORT).show();
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
            case 2:
                Toast.makeText(getApplicationContext(), "Выбрано меню Удалить фото", Toast.LENGTH_SHORT).show();
                imageView.setImageResource(R.drawable.joker);
                break;
        }
        return super.onContextItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bitmap;

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    if (selectedImage == null) return;
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            bitmap = ImageDecoder.decodeBitmap(
                                    ImageDecoder.createSource(getContentResolver(), selectedImage));
                        } else {
                            bitmap = MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), selectedImage);
                        }
                        sPref.setImageStorage(this, bitmap);
                        imageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }
}
