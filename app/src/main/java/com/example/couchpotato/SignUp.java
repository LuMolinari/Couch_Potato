package com.example.couchpotato;

import android.app.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.w3c.dom.Text;

public class SignUp extends Activity implements View.OnClickListener {
    private FirebaseAuthenticationManager firebaseAuthenticationManager;
    private Button signUPButton;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;
    private EditText passwordField;
    private TextView linkToLoginTextview;
    private DatabaseManager databaseManager = new DatabaseManager();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        firebaseAuthenticationManager = new FirebaseAuthenticationManager();

        signUPButton = findViewById(R.id.signUpButton);
        signUPButton.setOnClickListener(this);
        firstNameField = findViewById(R.id.signUpFirstNameField);
        lastNameField = findViewById(R.id.signUpLastNameField);
        emailField = findViewById(R.id.signUpEmailAddressField);
        passwordField = findViewById(R.id.signUpPasswordField);
        linkToLoginTextview = findViewById(R.id.linkToLoginTextview);
        linkToLoginTextview.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signUpButton:
                if (firstNameField.getText().toString().isEmpty()) {
                    Toast.makeText(SignUp.this, "Invalid First Name", Toast.LENGTH_SHORT).show();
                } else if (lastNameField.getText().toString().isEmpty()) {
                    Toast.makeText(SignUp.this, "Invalid Last Name", Toast.LENGTH_SHORT).show();
                } else if (emailField.getText().toString().isEmpty()) {
                    Toast.makeText(SignUp.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                } else if (passwordField.getText().toString().isEmpty()) {
                    Toast.makeText(SignUp.this, "Invalid password", Toast.LENGTH_SHORT).show();
                } else if (passwordField.getText().toString().length() < 6) {
                    Toast.makeText(SignUp.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    signUpUser(emailField.getText().toString(), passwordField.getText().toString());
                }
                break;
            case R.id.linkToLoginTextview:
                openSignInPage();
                break;
        }
    }

    public void signUpUser(String email, String password) {
        firebaseAuthenticationManager.signUpUser(email, password, new FirebaseCallback() {
            @Override
            public void callBack(Object status) {
                if (status == null) {
                    /*
                    sing up failed:
                        could be because the user already linked to another
                        could be because the email provided was badly formatted (invalid email)
                        could be because the password was less than 6 characters

                     let me know if you wan me to specify the reasons
                     Write below this comment whatever you want the app to do
                     when signing up fails
                     */
                    Toast.makeText(SignUp.this, "Email is registered or badly formatted", Toast.LENGTH_SHORT).show();
                } else {
                    /*
                    sign up was successful. Write below this comment
                    whatever you want the app to do when sign up is successful
                     */
                    String userId = firebaseAuthenticationManager.getCurrentUserId();
                    databaseManager.createDocument("users", userId, "First Name", firstNameField.getText().toString());
                    databaseManager.createNewField("users", userId, "Last Name", lastNameField.getText().toString());
                    databaseManager.createNewField("users", userId, "Email", emailField.getText().toString());
                    Toast.makeText(SignUp.this, "Sign up Successful", Toast.LENGTH_SHORT).show();
                    openHomeFragmentPage();
                }
            }
        });
    }

    public void openHomeFragmentPage() {
        Intent intent = new Intent(this, HomeFragmentPage.class);
        startActivity(intent);
        finish();
    }

    public void openSignInPage() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
