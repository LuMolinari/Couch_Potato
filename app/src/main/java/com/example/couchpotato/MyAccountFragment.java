package com.example.couchpotato;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

public class MyAccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.my_account_fragment, container, false);


        Button favorites = v.findViewById(R.id.favorites_button);
        favorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //switch fragments
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new FavoritesFragment());
                fragmentTransaction.commit();


            }
        });

        Button accountSettings = v.findViewById(R.id.account_settings_button);
        accountSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //switch fragments
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new AccountSettingsFragment());
                fragmentTransaction.commit();


            }
        });

        Button recommendButton = v.findViewById(R.id.recommend_button);
        Dialog dialog = new Dialog(getContext());
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        recommendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.custom_share_movie_popup);
                TextView linkTextView = dialog.findViewById(R.id.share_popup_link_text_view);
                ImageButton closeButton = dialog.findViewById(R.id.share_popup_close_button);
                ImageButton copyButton = dialog.findViewById(R.id.share_popup_copy_button);
                TextView title = dialog.findViewById(R.id.title_share_moivie_popup);

                title.setText("Thanks for Sharing");
                title.setTextSize(15);
                String link = "https://github.com/LuMolinari/Couch_Potato";
                linkTextView.setText(link);

                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                copyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ClipData clip = ClipData.newPlainText("label", link);
                        clipboard.setPrimaryClip(clip);
                        Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
                    }
                });


                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                dialog.show();
            }
        });

        return v;
    }

}
