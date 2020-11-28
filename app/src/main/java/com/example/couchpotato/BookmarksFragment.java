package com.example.couchpotato;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
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

public class BookmarksFragment extends Fragment {

    private RecyclerView bookmarkRecyclerView;
    private BookmarkAdapter bookmarkAdapter;
    private ArrayList<BookmarkItem> bookmarkItems= new ArrayList<>();;
    private DatabaseManager databaseManager;
    private FirebaseAuth mAuth;
    private String JSON_URL;
    private int bookmarkTotal;

    public BookmarksFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        databaseManager = new DatabaseManager();
        mAuth = FirebaseAuth.getInstance();

        //bookmarkItems.add(new BookmarkItem(R.drawable.ic_baseline_live_tv_24, "Spuds 3", "2020", "*****"));

        // build recycler view
        final View rootView = inflater.inflate(R.layout.bookmarks_fragment, container, false);
        bookmarkRecyclerView = (RecyclerView) rootView.findViewById(R.id.bookmarksRecyclerView);
        bookmarkRecyclerView.setHasFixedSize(true);
        bookmarkAdapter = new BookmarkAdapter(bookmarkItems);
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);
        bookmarkRecyclerView.setItemAnimator(new DefaultItemAnimator());

        // Firestore directory path to current users bookmarked movies
        String documentPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies/BookmarkedMovies";
        // get a snapshot of the documents in this collection
        databaseManager.getDocumentSnapshot(documentPath, new FirebaseCallback() {
            @Override
            public void callBack(Object status) {
                // do stuff with data documents here
                DocumentSnapshot documentSnapshot = (DocumentSnapshot) status;
                // number of bookmarks
                bookmarkTotal = documentSnapshot.getData().size();
                // get value of each bookmark field from each document in the bookmark collection
                for(Object movieId: documentSnapshot.getData().values()){
                    //create query for api with id string.
                    JSON_URL = "https://api.themoviedb.org/3/movie/"+  movieId.toString() +"?api_key=4517228c3cc695f9dfa1dcb4c4979152&language=en-US&";
                    new GetData().execute(JSON_URL);
                }
            }
        });
        return rootView;
    }

    private void PutDataIntoRecyclerView(ArrayList<BookmarkItem> bookmarks) {
        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(bookmarkItems);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);
        bookmarkRecyclerView.setLayoutManager(mLayoutManager);
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);
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
            // result is our actual json data.
            // Now turn it into a json object and use the objects to fill the favorites page.
            try {
                JSONObject jObject = new JSONObject(result);

                // BookmarkItem (int moviePoster, String movieTitle, String releaseYear, String reviewScore)
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

                BookmarkItem item = new BookmarkItem(jObject.getString("poster_path"),
                        jObject.getString("title"),jObject.getString("release_date"),jObject.getString("vote_average"));

                bookmarkItems.add(item);

                //when we have received all asynchronous json data then fill the view.
                if (bookmarkItems.size() == bookmarkTotal){
                    PutDataIntoRecyclerView(bookmarkItems);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}


