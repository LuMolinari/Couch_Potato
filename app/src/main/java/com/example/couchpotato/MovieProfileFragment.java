package com.example.couchpotato;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
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

public class MovieProfileFragment extends Fragment {

    private final MovieSingleton movieSingleton = MovieSingleton.getInstance();
    private ImageView moviePic;
    private TextView movieTitle;
    private TextView movieDescription;
    private TextView movieRate;
    private RecyclerView recyclerView;
    private String similarMoviesUrl;
    private ImageButton bookmarkImageButton;
    private ImageButton favoriteImageButton;

    private final String TAG = "MovieProfileFragment";

    private DatabaseManager databaseManager;
    private FirebaseAuth mAuth;

    private TextView noSimilarMovies;
    private ProgressBar progressBar;

    private ImageButton shareButton;
    private ClipboardManager clipboard;

    private Dialog dialog;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.movie_profile_fragment, container, false);

        moviePic = v.findViewById(R.id.contentPosterImageView);
        movieTitle = v.findViewById(R.id.contentTitleTextView);
        movieDescription = v.findViewById(R.id.contentSummaryTextView);
        movieRate = v.findViewById(R.id.reviewScoreTextView);
        recyclerView = v.findViewById(R.id.contentRecyclerView);
        bookmarkImageButton = v.findViewById(R.id.bookmarkImageButton);
        favoriteImageButton = v.findViewById(R.id.favoriteImageButton);

        noSimilarMovies = v.findViewById(R.id.no_similar_movies_available);
        progressBar = v.findViewById(R.id.similar_movies_progress_bar);

        shareButton = v.findViewById(R.id.shareImageButton);

        dialog = new Dialog(getContext());

        clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        databaseManager = new DatabaseManager();
        mAuth = FirebaseAuth.getInstance();

        String movieId = movieSingleton.getMovieId();
        MovieModelClass movieModelClass = movieSingleton.getMovieModelClass();

        if (movieModelClass.getBookMarked()) {
            bookmarkImageButton.setImageResource(R.drawable.ic_baseline_bookmark_blue_24);
        }

        if (movieModelClass.getFavoriteMovie()) {
            favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_red_24);
        }

        Log.d("MovieProfileFrag", " Title: " + movieModelClass.getTitle());

        String title = movieModelClass.getTitle() + "";

        movieTitle.setText(title);
        if (movieModelClass.getDescription().equals("")) {
            movieDescription.setText("Description Not Available");
        } else {
            movieDescription.setText(movieModelClass.getDescription() + "");
        }

        if (movieRate.equals("")) {
            movieRate.setText("Not Ratings available");
        } else {
            movieRate.setText(movieModelClass.getReviewScore() + "");
        }


        if (movieModelClass.getImg2().equals("null")) {
            if (movieModelClass.getImg().equals("null")) {
                Glide.with(this)
                        .load("https://merccapital.com.au/application/files/8114/5984/0563/image-not-available.jpg")
                        .into(moviePic);
            } else {
                Glide.with(this)
                        .load("https://image.tmdb.org/t/p/w500" + movieModelClass.getImg())
                        .into(moviePic);
            }
        } else {
            Glide.with(this)
                    .load("https://image.tmdb.org/t/p/w500" + movieModelClass.getImg2())
                    .into(moviePic);
        }

        similarMoviesUrl = "https://api.themoviedb.org/3/movie/" + movieId + "/similar?api_key=4517228c3cc695f9dfa1dcb4c4979152&language=en-US&page=1";

        bookmarkImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (movieModelClass.getBookMarked()) {
                    String collectionPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies";
                    String documentName = "BookmarkedMovies";
                    databaseManager.deleteField(collectionPath + "/" + documentName, movieModelClass.getTitle());
                    Toast.makeText(getContext(), "Bookmark removed", Toast.LENGTH_SHORT).show();
                    bookmarkImageButton.setImageResource(R.drawable.ic_baseline_bookmark_border_24);
                    movieModelClass.setBookMarked(false);
                } else {
                    String collectionPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies";
                    String documentName = "BookmarkedMovies";

                    databaseManager.checkIfThisDocumentExists(collectionPath + "/" + documentName, new FirebaseCallback() {
                        @Override
                        public void callBack(Object status) {
                            Boolean documentExists = (Boolean) status;
                            if (documentExists) {
                                databaseManager.createNewField(collectionPath, documentName, movieModelClass.getTitle(), movieModelClass.getId());
                            } else {
                                databaseManager.createDocument(collectionPath, documentName, movieModelClass.getTitle(), movieModelClass.getId());
                            }
                            bookmarkImageButton.setImageResource(R.drawable.ic_baseline_bookmark_blue_24);
                            movieModelClass.setBookMarked(true);
                            Toast.makeText(getContext(), "Bookmark saved", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        favoriteImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieModelClass.getFavoriteMovie()) {
                    String collectionPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies";
                    String documentName = "FavoriteMovies";
                    databaseManager.deleteField(collectionPath + "/" + documentName, movieModelClass.getTitle());
                    Toast.makeText(getContext(), "Favorite removed", Toast.LENGTH_SHORT).show();
                    favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    movieModelClass.setFavoriteMovie(false);
                } else {
                    String collectionPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies";
                    String documentName = "FavoriteMovies";

                    databaseManager.checkIfThisDocumentExists(collectionPath + "/" + documentName, new FirebaseCallback() {
                        @Override
                        public void callBack(Object status) {
                            Boolean documentExists = (Boolean) status;
                            if (documentExists) {
                                databaseManager.createNewField(collectionPath, documentName, movieModelClass.getTitle(), movieModelClass.getId());
                            } else {
                                databaseManager.createDocument(collectionPath, documentName, movieModelClass.getTitle(), movieModelClass.getId());
                            }
                            favoriteImageButton.setImageResource(R.drawable.ic_baseline_favorite_red_24);
                            movieModelClass.setFavoriteMovie(true);
                            Toast.makeText(getContext(), "Favorite saved", Toast.LENGTH_SHORT).show();
                        }
                    });


                }
            }
        });

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.custom_share_movie_popup);
                TextView linkTextView = dialog.findViewById(R.id.share_popup_link_text_view);
                ImageButton closeButton = dialog.findViewById(R.id.share_popup_close_button);
                ImageButton copyButton = dialog.findViewById(R.id.share_popup_copy_button);

                String search = movieModelClass.getTitle().replace(" ", "%20");
                String link;
                String dateReleased = movieModelClass.getDateReleased();
                if (dateReleased.equals("")) {
                    link = "https://www.google.com/search?q=" + search;

                    linkTextView.setText(link);
                } else {
                    String yearReleased = dateReleased.substring(0, 4);

                    link = "https://www.google.com/search?q=" + search + "%20(" + yearReleased + ")";

                    linkTextView.setText(link);
                }

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


        GetData getData = new GetData();
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
                    url = new URL(similarMoviesUrl);
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

                if (jsonArray.length() == 0 ) {
                    Log.d(TAG, "JsonArrayList: " + jsonArray.length());
                    noSimilarMovies.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    return;
                }

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

    private void showNoSimilarMovies() {

    }

    private void PutDataIntoRecyclerView(List<MovieModelClass> movieList) {
        Adaptery adaptery = new Adaptery(this.getContext(), movieList);
        //recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 3);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(adaptery);
        progressBar.setVisibility(View.GONE);
    }
}
