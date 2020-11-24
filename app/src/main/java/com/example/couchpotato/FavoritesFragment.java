package com.example.couchpotato;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    ImageView btn_back;
    RecyclerView recyclerView;
    private DatabaseManager databaseManager;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.favorites_fragment, container, false);
        btn_back = v.findViewById(R.id.back_button);
        recyclerView = v.findViewById(R.id.favoritesRecyclerView);

        databaseManager = new DatabaseManager();
        mAuth = FirebaseAuth.getInstance();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new MyAccountFragment());
                fragmentTransaction.commit();
            }
        });

        ArrayList<BookmarkItem> favoritesList = new ArrayList<>();
        //get list of movies in MovieFavorites Directory.
        String documentPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies/FavoriteMovies";
        databaseManager.getDocumentSnapshot(documentPath, new FirebaseCallback() {
            @Override
            public void callBack(Object status) {
                DocumentSnapshot snapshot = (DocumentSnapshot) status;
                //loop through movie ids in document
                ArrayList<BookmarkItem> favoritesList = new ArrayList<>();
                for (Object ds : snapshot.getData().values()) {
                    //call api based on movie id

                    String JSON_URL = "https://api.themoviedb.org/3/movie/"+  ds.toString() +"?api_key=4517228c3cc695f9dfa1dcb4c4979152&language=en-US&";
                    try {
                        //todo fix the way json is accessed
                        JSONObject jsonObject = new JSONObject(JSON_URL);

                        favoritesList.add(new BookmarkItem(jsonObject.getInt("poster_path"), jsonObject.getString("title"),
                                jsonObject.getString("release_date"), jsonObject.getString("vote_average")));


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        PutDataIntoRecyclerView(favoritesList);



        //use id to get model by calling api

        //fill relevant fields in model

        //pass the model to create item bookmark

        //load bookmark into recycler view













        return v;

    }






    private void PutDataIntoRecyclerView(ArrayList<BookmarkItem> favoritesList) {
        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(favoritesList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(bookmarkAdapter);


    }


}
