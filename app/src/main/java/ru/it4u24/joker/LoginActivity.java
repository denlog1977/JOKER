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

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText;
    EditText passwordEditText;
    boolean isUserNameEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        isUserNameEmail = true;

        if (!isUserNameEmail) {
            usernameEditText.setHint(R.string.prompt_username);
        }

        //SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences sPref = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
        String service1cLog = sPref.getString("service1cLog", "");
        Log.d("myLogs", "LoginActivity:service1cLog = " + service1cLog);

        //Firebase firebase = new Firebase();
        //String[] getServise = firebase.getServise();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (!isloginDataChanged(username, password)) return;

                loadingProgressBar.setVisibility(View.VISIBLE);
                //loginViewModel.login(usernameEditText.getText().toString(),
                //        passwordEditText.getText().toString());
            }
        });
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
