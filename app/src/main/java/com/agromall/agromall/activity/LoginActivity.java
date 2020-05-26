package com.agromall.agromall.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.agromall.agromall.R;
import com.agromall.agromall.preference.Session;
import com.agromall.agromall.utility.Utility;
import com.agromall.agromall.utility.Validator;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private EditText mEmailView, mPasswordView;
    private ProgressDialog progressDialog;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new Session(this);

        if (session.isLoggedIn()) {
            startActivity();
            return;
        }

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setIndeterminateDrawable(getDrawable(R.drawable.custom_progress_bar_indeterminate));
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Checking credentials. Please wait...");

        // Set up the login form.
        mEmailView = findViewById(R.id.email);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");

        if (message != null) toastMessage(message);

        JSONObject userData = session.getAccountInfo();

        if (userData.has("email")) {
            try {
                mEmailView.setText(userData.getString("email"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {

        if (!Validator.isNetworkConnected(LoginActivity.this)) {
            toastMessage("Login failed, please check your network connection and try again.");
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username address.
        if (TextUtils.isEmpty(email) || !Validator.isValidEmail(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        else if (TextUtils.isEmpty(password) || !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(mEmailView.getWindowToken(), 0);
            imm.hideSoftInputFromWindow(mPasswordView.getWindowToken(), 0);

            toggleProgress(true);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (!Objects.equals(email, "test@theagromall.com")
                            || !Objects.equals(password, "password")) {
                        toggleProgress(false);
                        toastMessage("Login failed. Invalid credentials");
                        return;
                    }

                    JSONObject userData = new JSONObject();
                    try {
                        userData.put("email", "test@theagromall.com");
                        session.setAccountInfo(userData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    toastMessage("Login successful");
                    session.setLoggedIn(true);
                    startActivity();
                }
            }, 1500);
        }
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI.
     */
    private void toggleProgress(final boolean show) {
        if (show) progressDialog.show();
        else progressDialog.dismiss();
    }


    public void toastMessage(String message) {
        Utility.toastMessage(this, message);
    }

    private void startActivity() {
        finish();
        startActivity(new Intent(LoginActivity.this, Splash.class));
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (session.isLoggedIn()) finish();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (session.isLoggedIn()) finish();
    }

}

