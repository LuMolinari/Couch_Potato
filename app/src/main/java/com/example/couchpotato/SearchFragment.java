package com.example.couchpotato;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class SearchFragment extends androidx.fragment.app.Fragment {
    private EditText searchField;
    private TextView noResultTextView;
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

    private String search;
    private final String TAG = "Search Fragment";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.search_fragment, container, false);

        searchField = v.findViewById(R.id.searchField);
        recyclerView = v.findViewById(R.id.resultsRecyclerView);
        noResultTextView = v.findViewById(R.id.no_results_text_view);

        databaseManager = new DatabaseManager();
        mAuth = FirebaseAuth.getInstance();

        nextButton = v.findViewById(R.id.next_Button);
        previousButton = v.findViewById(R.id.previous_Button);

        nextButton.setBackgroundResource(R.drawable.square_rounded_512_dark);
        previousButton.setBackgroundResource(R.drawable.square_rounded_512_dark);

        spinner = v.findViewById(R.id.progress_Bar);
        //spinner.setVisibility(View.VISIBLE);
        currentPage = 1;

        pageTextView = v.findViewById(R.id.page_TextView);

        nextButton.setEnabled(false);
        previousButton.setEnabled(false);
        pageTextView.setText("0");


        getData = new GetData();

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinner.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.INVISIBLE);
                currentPage++;
                //pageTextView.setText(String.valueOf(currentPage) + "/" + totalPages);
                if (currentPage == totalPages) {
                    nextButton.setEnabled(false);
                }
                JSON_URL = "https://api.themoviedb.org/3/search/movie?api_key=4517228c3cc695f9dfa1dcb4c4979152&language=en-US&query=" + search + "&page=" + currentPage + "&include_adult=false";
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
                recyclerView.setVisibility(View.INVISIBLE);
                currentPage--;
                //pageTextView.setText(String.valueOf(currentPage) + "/" + totalPages);
                if (currentPage == 1) {
                    previousButton.setEnabled(false);
                }
                JSON_URL = "https://api.themoviedb.org/3/search/movie?api_key=4517228c3cc695f9dfa1dcb4c4979152&language=en-US&query=" + search + "&page=" + currentPage + "&include_adult=false";
                getData.cancel(true);
                getData = new GetData();
                getData.execute();
                if (nextButton.isEnabled() == false) {
                    nextButton.setEnabled(true);
                }
            }
        });

        searchField.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    spinner.setVisibility(View.VISIBLE);
                    noResultTextView.setVisibility(View.INVISIBLE);
                    search = searchField.getText().toString();
                    search = search.replace(" ", "%20");
                    Log.d(TAG, "Search: " + search);
                    JSON_URL = "https://api.themoviedb.org/3/search/movie?api_key=4517228c3cc695f9dfa1dcb4c4979152&language=en-US&query=" + search + "&page=1&include_adult=false&certification_country=US&certification.lte=G";
                    Log.d(TAG, "JSON URL: " + JSON_URL);
                    getData = new GetData();
                    getData.execute();
                }
                return false;
            }
        });

        //add back button functionality

        return v;

    }

    private void setActionListenersToNextAndPreviousButtons() {

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
                totalPages = jsonObject.getInt("total_pages");

                if (currentPage == totalPages) {
                    nextButton.setEnabled(false);
                    nextButton.setBackgroundResource(R.drawable.square_rounded_512_dark);
                } else {
                    nextButton.setEnabled(true);
                    nextButton.setBackgroundResource(R.drawable.square_rounded_512);
                }
                if (currentPage == 1) {
                    previousButton.setEnabled(false);
                    previousButton.setBackgroundResource(R.drawable.square_rounded_512_dark);
                } else {
                    previousButton.setEnabled(true);
                    previousButton.setBackgroundResource(R.drawable.square_rounded_512);
                }

                if (totalPages == 0) {
                    //no results
                    spinner.setVisibility(View.INVISIBLE);
                    noResultTextView.setVisibility(View.VISIBLE);
                    getData.cancel(true);
                } else {
                    //show the results
                    pageTextView.setText(currentPage + "/" + totalPages);
                    setActionListenersToNextAndPreviousButtons();
                    //getData.execute();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                    Log.d(TAG, "Image is: " + model.getImg());
                    model.setImg2(jsonObject1.getString("backdrop_path"));
                    model.setReviewScore(jsonObject1.getString("vote_average"));
                    model.setDescription(jsonObject1.getString("overview"));
                    model.setDateReleased(jsonObject1.getString("release_date"));
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
        recyclerView.setVisibility(View.VISIBLE);
        spinner.setVisibility(View.GONE);
    }



}
