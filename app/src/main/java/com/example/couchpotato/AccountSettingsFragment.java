package com.example.couchpotato;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccountSettingsFragment extends Fragment {

    ImageView btn_back;
    Button saveButton;
    EditText newPassword;
    EditText newEmail;
    EditText newName;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.account_settings_fragment, container, false);
        btn_back = v.findViewById(R.id.back_button);
        newPassword = v.findViewById(R.id.newPasswordEditText);
        newEmail = v.findViewById(R.id.newEmailEditText);
        newName = v.findViewById(R.id.newNameEditText);

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new MyAccountFragment());
                fragmentTransaction.commit();
            }
        });

        saveButton = v.findViewById(R.id.saveAccountSettingsButtton);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open current user instance
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String TAG = AccountSettingsFragment.class.getName();

                //make sure user was found.
                if (user != null) {
                    boolean settingsChanged = false;

                    if(!newPassword.getText().toString().isEmpty()){


                        if (newPassword.getText().toString().length() >=8) {
                            //change password from textview
                            user.updatePassword(newPassword.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User password updated.");
                                            }
                                        }
                                    });
                            settingsChanged = true;
                        } else{
                            Toast.makeText(getActivity(), "Password Must Be Longer Than 8 Characters.", Toast.LENGTH_SHORT).show();

                        }
                    }
                    if(!newName.getText().toString().isEmpty()){

                        //create profile request
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(newName.getText().toString())
                                .build();

                        //update user profile
                        user.updateProfile(profileUpdates)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d(TAG, "User profile updated.");
                                        }
                                    }
                                });

                        settingsChanged=true;
                    }
                    if(!newEmail.getText().toString().isEmpty()){
                        //make sure new email is valid before sending it to firebase
                        if (isValidEmail(newEmail.getText().toString())) {

                            //update firebase email
                            user.updateEmail(newEmail.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d(TAG, "User email address updated.");
                                            }
                                        }
                                    });
                            settingsChanged = true;
                        } else {
                            //return error message
                            Toast.makeText(getActivity(), "Email was invalid", Toast.LENGTH_SHORT).show();
                        }
                    }

                    //display if changes were made
                    if (settingsChanged) {
                        Toast.makeText(getActivity(), "User Settings Updated", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity(), "No Settings Changed", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //no user was found
                    Toast.makeText(getActivity(), "No Active User Found", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return v;
    }

    private boolean isValidEmail(String string) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(string);

        return mat.matches();
    }
}
