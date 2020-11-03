package com.example.couchpotato;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuthenticationManager firebaseAuthenticationManager;
    private Button signInButton;
    private EditText signInEmailField;
    private EditText signInPasswordField;
    private TextView linkToSingUpTextView;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuthenticationManager = new FirebaseAuthenticationManager();
        signInButton = findViewById(R.id.signInButton);
        signInButton.setOnClickListener(this);

        signInEmailField = findViewById(R.id.signInEmailField);
        signInPasswordField = findViewById(R.id.signInPasswordField);
        //The example used to display api functionality

//        TraktV2 trakt = new TraktV2("d07305deec2882a4e7b2bdafba909ed07cc94b00ffd82bf1919f67526dd550c5");
//        Shows traktShows = trakt.shows();
//        System.out.println("looking for Trakt");
//        try {
//            // Get trending shows
//            Response<List<TrendingShow>> response = traktShows.trending(1, null, Extended.FULL).execute();
//            if (response.isSuccessful()) {
//                System.out.println("Successfully reached Trakt");
//                List<TrendingShow> shows = response.body();
//                for (TrendingShow trending : shows) {
//                    System.out.println("Title: " + trending.show.title);
//                }
//            } else {
//                if (response.code() == 401) {
//                    // authorization required, supply a valid OAuth access token
//                    System.out.println("invalid token for Trakt");
//                } else {
//                    // the request failed for some other reason
//                    System.out.println("what happened Trakt");
//                }
//            }
//        } catch (Exception e) {
//            // see execute() javadoc
//        }


        linkToSingUpTextView = findViewById(R.id.linkToSignUpTextview);
        linkToSingUpTextView.setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signInButton:
                if (signInEmailField.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Invalid email", Toast.LENGTH_SHORT).show();
                } else if (signInPasswordField.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Invalid password", Toast.LENGTH_SHORT).show();
                } else {
                    signInUser(signInEmailField.getText().toString(), signInPasswordField.getText().toString());
                }

                break;
            case R.id.linkToSignUpTextview:
                openSignUpPage();
                break;

        }
    }

    public void signInUser(String email, String password) {
        firebaseAuthenticationManager.singInUser(email, password, new FirebaseCallback() {
            @Override
            public void callBack(Object status) {
                if (status == null) {
                    /*
                    sign in failed:
                        could be because the account doesn't exists
                        could be because the password or email was incorrect

                     let me know if you wan me to specify the reasons
                     Write below this comment whatever you want the app to do
                     when log in fails
                     */
                    Toast.makeText(MainActivity.this, "Invalid Email/Password", Toast.LENGTH_SHORT).show();

                } else {
                    /*
                    sing in successful

                    Write below this comment whatever you want the app to do
                     when log in is successful
                     */
                    Toast.makeText(MainActivity.this, "Log In Successful", Toast.LENGTH_SHORT).show();
                    openHomeFragmentPage();
                }
            }
        });
    }

    public void openHomeFragmentPage() {
        Intent intent = new Intent(this, nav_bar_layout.class);
        startActivity(intent);
        finish();
    }

    public void openSignUpPage() {
        Intent intent = new Intent(this, SignUp.class);
        startActivity(intent);
        finish();
    }

}