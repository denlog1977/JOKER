package ru.it4u24.joker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
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
    private TextView loginAsTextView;
    private TextInputEditText passwordEditText, phoneEditText;
    private TextInputLayout nameTextInputLayout, phoneTextInputLayout;
    private ProgressBar loadingProgressBar;
    private Button loginButton;
    private boolean isUserNameEmail;
    boolean isRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        phoneEditText = findViewById(R.id.phone);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);
        loginAsTextView = findViewById(R.id.tvLogInAs);
        nameTextInputLayout = findViewById(R.id.tilName);
        phoneTextInputLayout = findViewById(R.id.tilPhone);

        isUserNameEmail = true;
        isRegistration = false;

        nameTextInputLayout.setVisibility(View.GONE);
        phoneTextInputLayout.setVisibility(View.GONE);

        if (!isUserNameEmail) {
            usernameEditText.setHint(R.string.prompt_username);
        }

        //SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences sPref = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        String service1cLog = sPref.getString("service1cLog", "");
        Log.d("myLogs", "LoginActivity:service1cLog = " + service1cLog);


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (!isloginDataChanged(username, password)) return;

                loginButton.setEnabled(false);
                loadingProgressBar.setVisibility(View.VISIBLE);

                KeystoreFirebase keystoreFirebase = App.getKeystoreFirebaseAuth();
                keystoreFirebase.runSignIn(LoginActivity.this, username, password);
            }
        });

        Slot[] slots = new PhoneNumberUnderscoreSlotsParser().parseSlots("+7 ___-___-__-__");
        MaskImpl mask = MaskImpl.createTerminated(slots);//PredefinedSlots.RUS_PHONE_NUMBER
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
            loginAsTextView.setText("Регистрация");
            isRegistration = false;
            nameTextInputLayout.setVisibility(View.GONE);
            phoneTextInputLayout.setVisibility(View.GONE);
        } else {
            loginButton.setText(R.string.action_sign_registration);
            loginAsTextView.setText("Вход");
            isRegistration = true;
            nameTextInputLayout.setVisibility(View.VISIBLE);
            phoneTextInputLayout.setVisibility(View.VISIBLE);
        }
    }

    public void updateSignIn (boolean successful) {
        if (successful) {
            Toast.makeText(this, "Добро пожаловать!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Ошибка авторизации!", Toast.LENGTH_SHORT).show();
        }
        loadingProgressBar.setVisibility(View.GONE);
        loginButton.setEnabled(true);
    }

    public boolean isloginDataChanged(String username, String password) {
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
