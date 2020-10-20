package com.example.couchpotato;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    FirebaseAuthenticationManager firebaseAuthenticationManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuthenticationManager = new FirebaseAuthenticationManager();


    }

    public void signUpUser(String email, String password) {
        firebaseAuthenticationManager.signUpUser(email, password, new FirebaseCallback() {
            @Override
            public void callBack(Object status) {
                if (status.equals(null)) {
                    /*
                    sing up failed:
                        could be because the user already linked to another
                        could be because the email provided was badly formatted (invalid email)
                        could be because the password was less than 6 characters

                     let me know if you wan me to specify the reasons
                     Write below this comment whatever you want the app to do
                     when signing up fails
                     */

                } else {
                    /*
                    sign up was successful. Write below this comment
                    whatever you want the app to do when sign up is successful
                     */

                }
            }
        });
    }

    public void signInUser(String email, String password) {
        firebaseAuthenticationManager.singInUser(email, password, new FirebaseCallback() {
            @Override
            public void callBack(Object status) {
                if (status.equals(null)) {
                    /*
                    sign in failed:
                        could be because the account doesn't exists
                        could be because the password or email was incorrect

                     let me know if you wan me to specify the reasons
                     Write below this comment whatever you want the app to do
                     when log in fails
                     */

                } else {
                    /*
                    sing in successful

                    Write below this comment whatever you want the app to do
                     when log in is successful
                     */

                }
            }
        });
    }

}