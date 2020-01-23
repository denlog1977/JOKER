package ru.it4u24.joker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import ru.tinkoff.decoro.MaskImpl;
import ru.tinkoff.decoro.parser.PhoneNumberUnderscoreSlotsParser;
import ru.tinkoff.decoro.parser.UnderscoreDigitSlotsParser;
import ru.tinkoff.decoro.slots.PredefinedSlots;
import ru.tinkoff.decoro.slots.Slot;
import ru.tinkoff.decoro.watchers.FormatWatcher;
import ru.tinkoff.decoro.watchers.MaskFormatWatcher;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private TextView loginAsTextView, nameTextView;
    private TextInputEditText passwordEditText, nameEditText, phoneEditText;
    private TextInputLayout nameTextInputLayout, phoneTextInputLayout;
    private ProgressBar loadingProgressBar;
    private Button loginButton;
    private boolean isUserNameEmail;
    boolean isRegistration, isOut;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        nameEditText = findViewById(R.id.name);
        phoneEditText = findViewById(R.id.phone);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);
        loginAsTextView = findViewById(R.id.tvLogInAs);
        nameTextInputLayout = findViewById(R.id.tilName);
        phoneTextInputLayout = findViewById(R.id.tilPhone);
        nameTextView = findViewById(R.id.tvName);

        KeystoreFirebase keystoreFirebaseUser = App.getKeystoreFirebaseAuth();

        isUserNameEmail = true;
        isRegistration = false;
        isOut = keystoreFirebaseUser.isSignInUser();

        nameTextInputLayout.setVisibility(View.GONE);
        phoneTextInputLayout.setVisibility(View.GONE);
        //loginAsTextView.setPaintFlags(loginAsTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //Текст с подчеркиванием

        if (!isUserNameEmail) {
            usernameEditText.setHint(R.string.prompt_username);
        }
        if (isOut) {
            loginAsTextView.setText(R.string.action_sign_out);
            KeystoreSharedPreferences sPref = App.getKeystoreSharedPreferens();
            nameTextView.setText(sPref.getLogin(sPref.KEY_USER_NAME));
        }

        SharedPreferences sPref = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        String service1cLog = sPref.getString("service1cLog", "");
        Log.d("myLogs", "LoginActivity:SharedPreferences:service1cLog = " + service1cLog);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Скрыть клавиатуру
                InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keyboard.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                final String email = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();
                final String phone = phoneEditText.getText().toString();
                final String name = nameEditText.getText().toString();

                if (!isloginDataChanged(email, password, name, phone)) return;

                loginButton.setEnabled(false);
                loadingProgressBar.setVisibility(View.VISIBLE);

                KeystoreFirebase keystoreFirebase = App.getKeystoreFirebaseAuth();

                if (isRegistration) {
                    keystoreFirebase.registration(LoginActivity.this, email, password, name, phone);
                } else {
                    keystoreFirebase.signIn(LoginActivity.this, email, password);
                }
            }
        });

        //Slot[] slots = new PhoneNumberUnderscoreSlotsParser().parseSlots("+7 (___) ___-__-__");
        MaskImpl mask = MaskImpl.createTerminated(PredefinedSlots.RUS_PHONE_NUMBER);
        mask.setHideHardcodedHead(true);
        FormatWatcher formatWatcher = new MaskFormatWatcher(mask);
        formatWatcher.installOn(phoneEditText);

        /*FormatWatcher formatWatcher2 = new MaskFormatWatcher(
                MaskImpl.createTerminated(PredefinedSlots.RUS_PASSPORT)); // маска для серии и номера
        formatWatcher.installOn(passportEditText);*/
    }

    public void onClickLoginAs(View view) {

        if (isRegistration) {
            loginButton.setText(R.string.action_sign_in_short);
            loginAsTextView.setText(R.string.action_registration);
            isRegistration = false;
            nameTextInputLayout.setVisibility(View.GONE);
            phoneTextInputLayout.setVisibility(View.GONE);
        } else if (isOut) {
            KeystoreFirebase keystoreFirebase = App.getKeystoreFirebaseAuth();
            keystoreFirebase.signOut();
            loginAsTextView.setText(R.string.action_registration);
            nameTextView.setText("");
            isOut = false;
        } else {
            loginButton.setText(R.string.action_sign_registration);
            loginAsTextView.setText(R.string.action_sign_in);
            isRegistration = true;
            nameTextInputLayout.setVisibility(View.VISIBLE);
            phoneTextInputLayout.setVisibility(View.VISIBLE);
        }
    }

    public void updateSignIn (boolean successful) {
        if (successful) {
            KeystoreSharedPreferences sPref = App.getKeystoreSharedPreferens();
            String name = "Добро пожаловать\n" + sPref.getLogin(sPref.KEY_USER_NAME) + "!";
            Toast.makeText(this, name, Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Ошибка авторизации!", Toast.LENGTH_SHORT).show();
        }
        loadingProgressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
    }

    public boolean isloginDataChanged(String username, String password, String name, String phone) {
        if (!isUserNameValid(username)) {
            int iderror = isUserNameEmail ?
                    R.string.invalid_username_email : R.string.invalid_username;
            usernameEditText.setError(getString(iderror));
            return false;
            //loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            passwordEditText.setError(getString(R.string.invalid_password));
            return false;
            //loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else if (isRegistration && name.trim().isEmpty()) {
            nameEditText.setError("Заполните имя");
            return false;
        } else if (isRegistration && phone.trim().length() < 18) {
            phoneEditText.setError("Заполните телефон");
            return false;
        } else {
            return true;
            //loginFormState.setValue(new LoginFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@") || isUserNameEmail) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }

    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
