package com.example.couchpotato;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class FirebaseAuthenticationManager {
    private FirebaseAuth mAuth;
    private String TAG;

    public FirebaseAuthenticationManager() {
        mAuth = FirebaseAuth.getInstance();
        TAG = "FirebaseAuthenticationManager";
    }

    public void signUpUser(String email, String password, final FirebaseCallback firebaseCallback) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "Sign up successful");
                firebaseCallback.callBack("Successful");

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Sign up failed: " + e);
                firebaseCallback.callBack(null);
            }
        });
    }

    public void singInUser(String email, String password, final FirebaseCallback firebaseCallback) {
        mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "Log in successful");
                firebaseCallback.callBack("Log in successful");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Log in failed: " + e);
                firebaseCallback.callBack(null);
            }
        });
    }
    
    public String getCurrentUserId(){
        return mAuth.getCurrentUser().getUid();
    }
}
