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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

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
import java.util.HashMap;

public class BookmarksFragment extends Fragment implements BookmarkAdapter.ItemListener {

    private RecyclerView bookmarkRecyclerView;
    private BookmarkAdapter bookmarkAdapter;
    private final ArrayList<BookmarkItem> bookmarkItems= new ArrayList<>();
    private final ArrayList<MovieModelClass> movieModels = new ArrayList<>();
    private DatabaseManager databaseManager;
    private FirebaseAuth mAuth;
    private String JSON_URL;
    private int bookmarkTotal;
    MovieSingleton movieSingleton = MovieSingleton.getInstance();
    View v;
    boolean isBookmarked = true;
    boolean isFavorite = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Dialog dialog;

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
        bookmarkRecyclerView = rootView.findViewById(R.id.bookmarksRecyclerView);
        bookmarkRecyclerView.setHasFixedSize(true);
        bookmarkAdapter = new BookmarkAdapter(bookmarkItems, this);
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);
        bookmarkRecyclerView.setItemAnimator(new DefaultItemAnimator());
        dialog = new Dialog(getContext());
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
        v = rootView;
        return rootView;
    }

    private void PutDataIntoRecyclerView(ArrayList<BookmarkItem> bookmarks) {
        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(bookmarkItems, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);
        bookmarkRecyclerView.setLayoutManager(mLayoutManager);
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);
    }

    @Override
    public void onItemCLicked(int position) {
        String id = movieModels.get(position).getId();
        movieSingleton.setMovieId(id);
        movieSingleton.setMovieModelClass(movieModels.get(position));
        AppCompatActivity activity = (AppCompatActivity) v.getContext();
        Fragment myFragment = new MovieProfileFragment();


        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).commit();

    }

    @Override
    public void favoriteClicked(int position) {
        String collectionPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies";
        String documentName = "FavoriteMovies";
        if (movieModels.get(position).getFavoriteMovie()) {
            DocumentReference documentReference = db.document(collectionPath + "/" + documentName);
            HashMap<String, Object> data = new HashMap<>();
            data.put(movieModels.get(position).getTitle(), FieldValue.delete());
            documentReference.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Favorites Fragment", "Successfully deleted the field");

                    Toast.makeText(getContext(), bookmarkItems.get(position).getMovieTitle() + " removed from favorites", Toast.LENGTH_SHORT).show();


                    movieModels.get(position).setFavoriteMovie(false);
                    bookmarkItems.get(position).setFavoriteMovie(false);

                    PutDataIntoRecyclerView(bookmarkItems);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("FavoritesFragment", "Failed to delete the field");
                }
            });
        } else {

            databaseManager.checkIfThisDocumentExists(collectionPath + "/" + documentName, new FirebaseCallback() {
                @Override
                public void callBack(Object status) {
                    Boolean documentExists = (Boolean) status;
                    if (documentExists) {
                        databaseManager.createNewField(collectionPath, documentName, movieModels.get(position).getTitle(), movieModels.get(position).getId());
                    } else {
                        databaseManager.createDocument(collectionPath, documentName, movieModels.get(position).getTitle(), movieModels.get(position).getId());
                    }
                   // favoriteImageButton.setBackground(getResources().getDrawable(android.R.drawable.ic_notification_overlay));
                    movieModels.get(position).setFavoriteMovie(true);
                    bookmarkItems.get(position).setFavoriteMovie(true);
                    Toast.makeText(getContext(), bookmarkItems.get(position).getMovieTitle()+ " Bookmarked", Toast.LENGTH_SHORT).show();

                    PutDataIntoRecyclerView(bookmarkItems);

                }
            });
        }

    }

    @Override
    public void bookmarkClicked(int position) {
        //if theyre in favorites view and click the favorites button they remove it from favorites

        String collectionPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies";
        String documentName = "BookmarkedMovies";
        DocumentReference documentReference = db.document(collectionPath + "/" + documentName);
        HashMap<String, Object> data = new HashMap<>();
        data.put(bookmarkItems.get(position).getMovieTitle(), FieldValue.delete());
        documentReference.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Favorites Fragment", "Successfully deleted the field");

                Toast.makeText(getContext(), bookmarkItems.get(position).getMovieTitle() + " removed from Bookmarks", Toast.LENGTH_SHORT).show();

                bookmarkItems.remove(position);
                PutDataIntoRecyclerView(bookmarkItems);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FavoritesFragment", "Failed to delete the field");
            }
        });
    }

    @Override
    public void shareClicked(int position) {
        dialog.setContentView(R.layout.custom_share_movie_popup);
        TextView linkTextView = dialog.findViewById(R.id.share_popup_link_text_view);
        ImageButton closeButton = dialog.findViewById(R.id.share_popup_close_button);
        ImageButton copyButton = dialog.findViewById(R.id.share_popup_copy_button);

        String search = bookmarkItems.get(position).getMovieTitle().replace(" ", "%20");
        String link;
        String dateReleased = bookmarkItems.get(position).getReleaseYear();
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
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.show();
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
                    Log.d("Response: ", "> " + line);

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
            //also make movie model in case they click on it and open movie profile.
            try {
                JSONObject jObject = new JSONObject(result);

                MovieModelClass model = new MovieModelClass();

                model.setId(jObject.getString("id"));
                model.setTitle(jObject.getString("title"));
                model.setImg(jObject.getString("poster_path"));
                model.setImg2(jObject.getString("backdrop_path"));
                model.setReviewScore(jObject.getString("vote_average"));
                model.setDescription(jObject.getString("overview"));
                model.setDateReleased(jObject.getString("release_date"));

                System.out.println("Poster: "+jObject.getString("poster_path"));
                System.out.println("Title: "+jObject.getString("title"));
                System.out.println("release: "+jObject.getString("release_date"));
                System.out.println("score: "+jObject.getString("poster_path"));

                //check if movie is favorite
                String favoriteMoviesDocPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies/FavoriteMovies";
                databaseManager.getDocumentSnapshot(favoriteMoviesDocPath, new FirebaseCallback() {
                    @Override
                    public void callBack(Object status) {
                        DocumentSnapshot snapshot = (DocumentSnapshot) status;
                        for (Object ds: snapshot.getData().values()) {
                            if (ds.toString().equals(model.getId())) {
                                isFavorite = true;
                                break;
                            } else {
                                isFavorite = false;
                            }
                        }


                        model.setBookMarked(isBookmarked);
                        model.setFavoriteMovie(isFavorite);

                        BookmarkItem item = null;
                        try {
                            item = new BookmarkItem(jObject.getString("poster_path"),
                                    jObject.getString("title"),jObject.getString("release_date"),jObject.getString("vote_average"),isBookmarked,isFavorite );
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        bookmarkItems.add(item);
                        movieModels.add(model);

                        //when we have received all asynchronous json data then fill the view.
                        if (bookmarkItems.size() == bookmarkTotal){
                            PutDataIntoRecyclerView(bookmarkItems);
                        }

                    }


                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}


