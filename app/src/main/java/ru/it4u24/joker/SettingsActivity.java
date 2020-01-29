package ru.it4u24.joker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Guideline;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class SettingsActivity extends AppCompatActivity {

    CardView cvPhoto;
    private ImageView ivPhoto;
    private KeystoreSharedPreferences sPref;
    private TextView tvStatusEmail, tvStatusPhone, tvConfirmEmail, tvPhone, tvConfirmPhone;
    private EditText etPhone, etName;
    private Button btnEditing, btnCancel, btnExit;
    private Guideline glButton;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        ivPhoto = findViewById(R.id.ivPhoto);
        cvPhoto = findViewById(R.id.cvPhoto);
        registerForContextMenu(cvPhoto);

        sPref = App.getKeystoreSharedPreferens();
        sPref.loadImageFromStorage(ivPhoto, R.mipmap.ic_joker_foreground);//R.drawable.joker

        name = sPref.getString(sPref.KEY_USER_NAME, getString(R.string.prompt_name));
        String statusEmail = sPref.getString(sPref.KEY_STATUS_EMAIL, "");
        //TextView tvName = findViewById(R.id.tvUserName);
        //tvName.setText();
        TextView tvEmail = findViewById(R.id.tvSettingEmail);
        tvEmail.setText(sPref.getString(sPref.KEY_USER_EMAIL, ""));
        tvStatusEmail = findViewById(R.id.tvSettingStatusEmail);
        tvStatusEmail.setText(statusEmail);
        tvPhone = findViewById(R.id.tvSettingPhone);
        tvPhone.setText(sPref.getString(sPref.KEY_USER_PHONE, ""));
        tvStatusPhone = findViewById(R.id.tvSettingStatusPhone);
        tvStatusPhone.setText(sPref.getString(sPref.KEY_STATUS_PHONE, ""));

        etName = findViewById(R.id.etUserName);
        etName.setText(name);
        etPhone = findViewById(R.id.etSettingPhone);
        etPhone.setVisibility(View.GONE);

        boolean isEnabled = App.getKeystoreFirebaseAuth().isSignInUser();
        boolean isEnabledEmail = true;

        tvConfirmEmail = findViewById(R.id.tvSettingConfirmEmail);
        tvConfirmPhone = findViewById(R.id.tvSettingConfirmPhone);

        glButton = findViewById(R.id.glButton);
        glButton.setGuidelinePercent(1);

        btnEditing = findViewById(R.id.btnEditing);
        btnCancel = findViewById(R.id.btnCancel);
        btnCancel.setVisibility(View.GONE);
        btnExit = findViewById(R.id.btnExit);

        if (statusEmail.equals(sPref.STATUS_CONFIRMATION_PENDING)) {
            KeystoreFirebase keystoreFirebaseAuth = App.getKeystoreFirebaseAuth();
            if (keystoreFirebaseAuth.isEmailVerified()) {
                sPref.setString(sPref.KEY_STATUS_EMAIL, sPref.STATUS_CONFIRMED);
                tvStatusEmail.setText(sPref.STATUS_CONFIRMED);
                tvConfirmEmail.setVisibility(View.INVISIBLE);
            } else
                tvConfirmEmail.setText("Повторить");
        } else if (statusEmail.equals(sPref.STATUS_CONFIRMED)) {
            isEnabledEmail = false;
        } else {
            tvConfirmEmail.setText("Отправить");
        }

        setEnabledObjects(isEnabled, isEnabledEmail);

        cvPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Выбран фото", Toast.LENGTH_SHORT).show();
            }
        });
        cvPhoto.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getApplicationContext(), "Выбрано меню", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        MaskImpl mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        mask.setHideHardcodedHead(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(etPhone);

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
                menu.add(0, 3, 0, "Отменить");
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
                break;
            case 2:
                sPref.setString(sPref.KEY_USER_PHOTO, "");
                ivPhoto.setImageResource(R.mipmap.ic_joker_foreground);//R.drawable.joker
                Toast.makeText(getApplicationContext(), "Фото удалено", Toast.LENGTH_SHORT).show();
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
                            bitmap = getBitmap(getContentResolver(), selectedImage);
                        }
                        sPref.setImageStorage(this, bitmap);
                        ivPhoto.setImageBitmap(bitmap);
                        Toast.makeText(getApplicationContext(), "Фото установлено", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),
                                "Ошибка приложения!\nНеудалось загрузить фото", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public static final Bitmap getBitmap(ContentResolver cr, Uri url) throws NullPointerException, IOException {
        InputStream input = cr.openInputStream(url);
        Bitmap bitmap = BitmapFactory.decodeStream(input);
        input.close();
        return bitmap;
    }

    private void setEnabledObjects(boolean isEnabled, boolean isEnabledEmail) {
        int viewVisibility = isEnabled ? View.VISIBLE : View.INVISIBLE;
        int viewVisibilityEmail = isEnabled && isEnabledEmail ? View.VISIBLE : View.INVISIBLE;

        cvPhoto.setEnabled(isEnabled);

        tvConfirmEmail.setVisibility(viewVisibilityEmail);
        tvConfirmPhone.setVisibility(viewVisibility);

        btnEditing.setEnabled(isEnabled);
        btnExit.setEnabled(isEnabled);
    }

    private void setEdintingObjects() {

        LinearLayout.MarginLayoutParams layoutParams = (LinearLayout.MarginLayoutParams) btnEditing.getLayoutParams();
        layoutParams.rightMargin = 0;

        glButton.setGuidelinePercent(1);
        btnEditing.setText(getString(R.string.action_editing));
        btnCancel.setVisibility(View.GONE);
        etName.setEnabled(false);
        //etName.setTextColor(R.attr.colorPrimaryDark);
        etPhone.setVisibility(View.GONE);
        tvPhone.setVisibility(View.VISIBLE);
    }

    private void hideKeyboard(View view) {
        // Скрыть клавиатуру
        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void onClickConfirmEmail(View view) {

        KeystoreFirebase keystoreFirebaseAuth = App.getKeystoreFirebaseAuth();
        keystoreFirebaseAuth.sendVerificationEmail(null);

        sPref.setString(sPref.KEY_STATUS_EMAIL, sPref.STATUS_CONFIRMATION_PENDING);
        tvStatusEmail.setText(sPref.getString(sPref.KEY_STATUS_EMAIL, ""));
        tvConfirmEmail.setVisibility(View.INVISIBLE);

        Toast.makeText(this, "Запрос на подтверждение отправлен на электронную почту",
                Toast.LENGTH_LONG).show();
    }

    public void onClickConfirmPhone(View view) {

        sPref.setString(sPref.KEY_STATUS_PHONE, sPref.STATUS_CONFIRMATION_PENDING);
        tvStatusPhone.setText(sPref.getString(sPref.KEY_STATUS_PHONE, ""));
        Toast.makeText(this, "Запрос на подтверждение отправлен на телефон",
                Toast.LENGTH_LONG).show();
    }

    public void onClickEditing(View view) {

        if (btnEditing.getText().toString() == getString(R.string.action_editing)) {
            LinearLayout.MarginLayoutParams layoutParams = (LinearLayout.MarginLayoutParams) btnEditing.getLayoutParams();
            layoutParams.rightMargin = 5;

            glButton.setGuidelinePercent((float) 0.5);
            btnEditing.setText(getString(R.string.action_saving));
            btnCancel.setVisibility(View.VISIBLE);
            etName.setEnabled(true);
            //etName.setTextColor(R.attr.editTextColor);
            etPhone.setVisibility(View.VISIBLE);
            etPhone.setText(tvPhone.getText().toString());
            tvPhone.setVisibility(View.GONE);
        } else {
            name = etName.getText().toString();
            tvPhone.setText(etPhone.getText().toString());
            setEdintingObjects();
        }

        hideKeyboard(view);
     }

    public void onClickCancel(View view) {

        etName.setText(name);
        setEdintingObjects();
        hideKeyboard(view);
    }

    public void onClickExit(View view) {

        KeystoreFirebase keystoreFirebase = App.getKeystoreFirebaseAuth();
        keystoreFirebase.signOut();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
        //setEnabledObjects(false);
        //hideKeyboard(view);
    }
}
