package com.example.couchpotato;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    FirebaseAuthenticationManager firebaseAuthenticationManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuthenticationManager = new FirebaseAuthenticationManager();

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
                     //TODO I think we could have a toast come up saying account not found, retry or signup
                     */

                } else {
                    /*
                    sing in successful

                    Write below this comment whatever you want the app to do
                     when log in is successful
                     //todo from here we open up the main discover window filled with generally popular movies. i know trakt shows trending shows or movies
                     */

                }
            }
        });
    }

}