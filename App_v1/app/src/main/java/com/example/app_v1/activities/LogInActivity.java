package com.example.app_v1.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.app_v1.R;
import com.example.app_v1.viewmodels.LogInViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;

    private LogInViewModel logInViewModel;

    private EditText mEmail;
    private EditText mPassword;
    private CheckBox mCheckBox;
    private Button buttonLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        logInViewModel = ViewModelProviders.of(this).get(LogInViewModel.class);
        logInViewModel.init();

        // Views
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);

        //Buttons and CheckBox
        mCheckBox = findViewById(R.id.check_box);
        buttonLogIn = findViewById(R.id.log_in_button);

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save the checkbox preference
                if (mCheckBox.isChecked()) {
                    //set a checkbox when the application starts
                    mEditor.putString(getString(R.string.check_box), "True");
                    mEditor.commit();

                    //save email
                    String email = mEmail.getText().toString();
                    mEditor.putString(getString(R.string.email), email);
                    mEditor.commit();
                } else {
                    //set a checkbox when the application starts
                    mEditor.putString(getString(R.string.check_box), "False");
                    mEditor.commit();

                    //save email
                    mEditor.putString(getString(R.string.email), "");
                    mEditor.commit();
                }
                logIn();
            }
        });

        //Objects
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mPreferences.edit();

        checkSharedPreferences();
    }

    private void checkSharedPreferences() {
        String checkbox = mPreferences.getString(getString(R.string.check_box), "false");
        String email = mPreferences.getString(getString(R.string.email), "");

        mEmail.setText(email);

        if (checkbox.equals("True")) {
            mCheckBox.setChecked(true);
        } else {
            mCheckBox.setChecked(false);
        }
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // uses Firebase sign in method verification
        logInViewModel.getMFireBaseModel().getMFirebaseAuth()
                .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // if authentication is completed successfully than it will move to the next page
                if (task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    goToGreenhouseSelection();
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(LogInActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmail.setError("Required.");
            valid = false;
        } else {
            mEmail.setError(null);
        }

        String password = mPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPassword.setError("Required.");
            valid = false;
        } else {
            mPassword.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.log_in_button) {
            signIn(logInViewModel.geStringFromTextView(mEmail), logInViewModel.geStringFromTextView(mPassword));
        }
    }

    public void logIn()
    {
        signIn(logInViewModel.geStringFromTextView(mEmail), logInViewModel.geStringFromTextView(mPassword));
    }

    public void goToGreenhouseSelection(){
        Intent Login = new Intent(LogInActivity.this, GreenhouseSelectActivity.class);
        startActivity(Login);
    }
}