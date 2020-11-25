package com.example.couchpotato;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class FavoritesFragment extends Fragment {

    ImageView btn_back;
    RecyclerView recyclerView;
    private DatabaseManager databaseManager;
    private FirebaseAuth mAuth;
    private final ArrayList<String> movieId = new ArrayList<>();
    private final ArrayList<BookmarkItem> favoritesList = new ArrayList<>();
    ProgressDialog pd;
    private String JSON_URL;
    private GetData getData;
    int favoriteTotal;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.favorites_fragment, container, false);
        btn_back = v.findViewById(R.id.back_button);
        recyclerView = v.findViewById(R.id.favoritesRecyclerView);

        databaseManager = new DatabaseManager();
        mAuth = FirebaseAuth.getInstance();
        getData = new GetData();

        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new MyAccountFragment());
                fragmentTransaction.commit();
            }
        });

        //get list of movies in MovieFavorites Directory.
        String documentPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies/FavoriteMovies";
        databaseManager.getDocumentSnapshot(documentPath, new FirebaseCallback() {
            @Override
            public void callBack(Object status) {
                DocumentSnapshot snapshot = (DocumentSnapshot) status;
                //loop through movie ids in document
                favoriteTotal = snapshot.getData().size();

                for (Object ds : snapshot.getData().values()) {

                    //create query for api with id string.
                     JSON_URL = "https://api.themoviedb.org/3/movie/"+  ds.toString() +"?api_key=4517228c3cc695f9dfa1dcb4c4979152&language=en-US&";
                     //call asynchronous class that will fetch the data
                     getData.execute(JSON_URL);

                }
            }
        });

        return v;

    }



    private void PutDataIntoRecyclerView(ArrayList<BookmarkItem> favoritesList) {
        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(favoritesList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(bookmarkAdapter);

    }



    //create private getdata class for private json calls
    public class GetData extends AsyncTask<String, String, String> {



        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            System.out.println(result);
            //result is our actual json data.
            // Now turn it into a json object and use the objects to fill the favorites page.
            try {
                JSONObject jObject = new JSONObject(result);

         //       BookmarkItem (int moviePoster, String movieTitle, String releaseYear, String reviewScore)
//                model.setId(jsonObject1.getString("id"));
//                model.setTitle(jsonObject1.getString("title"));
//                model.setImg(jsonObject1.getString("poster_path"));
//                model.setImg2(jsonObject1.getString("backdrop_path"));
//                model.setReviewScore(jsonObject1.getString("vote_average"));
//                model.setDescription(jsonObject1.getString("overview"));
                System.out.println("Poster: "+jObject.getString("poster_path"));
                System.out.println("Title: "+jObject.getString("title"));
                System.out.println("release: "+jObject.getString("release_date"));
                System.out.println("score: "+jObject.getString("poster_path"));

                BookmarkItem item = new BookmarkItem(jObject.getInt("poster_path"),
                        jObject.getString("title"),jObject.getString("release_date"),jObject.getString("poster_path"));

                favoritesList.add(item);

                //when we have received all asynchronous json data then fill the view.
                if (favoritesList.size() == favoriteTotal){
                    PutDataIntoRecyclerView(favoritesList);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
