package com.example.couchpotato;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;

    private static String JSON_URL;
    private int totalPages;
    private int currentPage;

    private Button nextButton;
    private Button previousButton;

    private ProgressBar spinner;

    private GetData getData;
    private TextView pageTextView;
    private MovieSingleton movieSingleton;
    private DatabaseManager databaseManager;
    private FirebaseAuth mAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.home_fragment, container, false);

        //switch fragment when account button is clicked
        ImageButton accountButton = v.findViewById(R.id.accountsImageButton);
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //switch fragments
                FragmentTransaction fragmentTransaction = getActivity()
                        .getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, new MyAccountFragment());
                fragmentTransaction.commit();


            }



        });


        databaseManager = new DatabaseManager();
        mAuth = FirebaseAuth.getInstance();

        nextButton = v.findViewById(R.id.nextButton);
        previousButton = v.findViewById(R.id.previousButton);

        spinner = v.findViewById(R.id.progressBar);
        spinner.setVisibility(View.VISIBLE);
        currentPage = 1;

        pageTextView = v.findViewById(R.id.pageTextView);

        getData = new GetData();

        recyclerView = v.findViewById(R.id.recyclerview);
        //the following url returns all the movies that should be in discovery section in JSON format
        JSON_URL = "https://api.themoviedb.org/3/discover/movie?api_key=4517228c3cc695f9dfa1dcb4c4979152&language=en-US&sort_by=popularity.desc&include_adult=false&include_video=false&page=1";
        try {
            JSONObject jsonObject = new JSONObject(JSON_URL);
            totalPages = jsonObject.getInt("total_pages");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                currentPage++;
                pageTextView.setText(String.valueOf(currentPage));
                if (currentPage == totalPages) {
                    nextButton.setEnabled(false);
                }
                JSON_URL = "https://api.themoviedb.org/3/discover/" +
                        "movie?api_key=4517228c3cc695f9dfa1dcb4c4979152&language=en-US&sort_by" +
                        "=popularity.desc&include_adult=false&include_video=false&page="
                        + currentPage;
                getData.cancel(true);
                getData = new GetData();
                getData.execute();
                if (previousButton.isEnabled() == false) {
                    previousButton.setEnabled(true);
                }
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                currentPage--;
                pageTextView.setText(String.valueOf(currentPage));
                if (currentPage == 1) {
                    previousButton.setEnabled(false);
                }
                JSON_URL = "https://api.themoviedb.org/3/discover/" +
                        "movie?api_key=4517228c3cc695f9dfa1dcb4c4979152&language=en-US&sort_by" +
                        "=popularity.desc&include_adult=false&include_video=false&page="
                        + currentPage;
                getData.cancel(true);
                getData = new GetData();
                getData.execute();
                if (nextButton.isEnabled() == false) {
                    nextButton.setEnabled(true);
                }
            }
        });

        if (currentPage == totalPages) {
            nextButton.setEnabled(false);
        }
        if (currentPage == 1) {
            previousButton.setEnabled(false);
        }

        getData.execute();

        return v;
    }

    public class GetData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {

            String current = "";

            try {
                URL url;
                HttpURLConnection urlConnection = null;

                try {
                    url = new URL(JSON_URL);
                    urlConnection = (HttpURLConnection) url.openConnection();

                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is);

                    int data = isr.read();

                    while (data != -1) {
                        current += (char) data;
                        data = isr.read();

                    }
                    return current;

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    if(urlConnection != null) {
                        urlConnection.disconnect();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return current;
        }

        @Override
        protected void onPostExecute(String s) {

            try {
                JSONObject jsonObject = new JSONObject(s);

                JSONArray jsonArray = jsonObject.getJSONArray("results");

                List<MovieModelClass> movieList = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                    MovieModelClass model = new MovieModelClass();

                    model.setId(jsonObject1.getString("id"));
                    model.setTitle(jsonObject1.getString("title"));
                    model.setImg(jsonObject1.getString("poster_path"));
                    model.setImg2(jsonObject1.getString("backdrop_path"));
                    model.setReviewScore(jsonObject1.getString("vote_average"));
                    model.setDescription(jsonObject1.getString("overview"));

                    //check if movie is book marked
                    String documentPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies/BookmarkedMovies";
                    int finalI = i;

                    databaseManager.getDocumentSnapshot(documentPath, new FirebaseCallback() {
                        @Override
                        public void callBack(Object status) {
                            DocumentSnapshot snapshot = (DocumentSnapshot) status;
                            for (Object ds : snapshot.getData().values()) {
                                if (ds.toString().equals(model.getId())) {
                                    model.setBookMarked(true);
                                    break;
                                } else {
                                    model.setBookMarked(false);
                                }
                            }

                            //check if movie is favorite
                            String favoriteMoviesDocPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies/FavoriteMovies";
                            databaseManager.getDocumentSnapshot(favoriteMoviesDocPath, new FirebaseCallback() {
                                @Override
                                public void callBack(Object status) {
                                    DocumentSnapshot snapshot = (DocumentSnapshot) status;
                                    for (Object ds: snapshot.getData().values()) {
                                        if (ds.toString().equals(model.getId())) {
                                            model.setFavoriteMovie(true);
                                            break;
                                        } else {
                                            model.setFavoriteMovie(false);
                                        }
                                    }
                                    movieList.add(model);
                                    if (finalI == jsonArray.length() - 1) {
                                        PutDataIntoRecyclerView(movieList);
                                    }
                                }
                            });
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void PutDataIntoRecyclerView(List<MovieModelClass> movieList) {
        Adaptery adaptery = new Adaptery(this.getContext(), movieList);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 3);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adaptery);
        spinner.setVisibility(View.GONE);
    }


}
