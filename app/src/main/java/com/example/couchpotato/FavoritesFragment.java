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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
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

public class FavoritesFragment extends Fragment implements BookmarkAdapter.ItemListener {

    ImageView btn_back;
    RecyclerView recyclerView;
    private DatabaseManager databaseManager;
    private FirebaseAuth mAuth;
    private final ArrayList<BookmarkItem> favoritesList = new ArrayList<>();
    private final ArrayList<MovieModelClass> movieModels = new ArrayList<>();
    MovieSingleton movieSingleton = MovieSingleton.getInstance();
    private String JSON_URL;
    int favoriteTotal;
    View passable;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    boolean isBookmarked = false;
    boolean isFavorite = true;
    Dialog dialog;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.favorites_fragment, container, false);
        btn_back = v.findViewById(R.id.back_button);
        recyclerView = v.findViewById(R.id.favoritesRecyclerView);

        databaseManager = new DatabaseManager();
        mAuth = FirebaseAuth.getInstance();
        dialog = new Dialog(getContext());
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
                    //because each call to get data is asynchronous each data call needs to be separate
                    new GetData().execute(JSON_URL);

                }
            }
        });

        passable = v;
        return v;

    }



    private void PutDataIntoRecyclerView(ArrayList<BookmarkItem> favoritesList) {
        BookmarkAdapter bookmarkAdapter = new BookmarkAdapter(favoritesList,this);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this.getContext(), 1);

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(bookmarkAdapter);

    }

    //these methods will handle my click events
    @Override
    public void onItemCLicked(int position) {
        String id = movieModels.get(position).getId();
        movieSingleton.setMovieId(id);
        movieSingleton.setMovieModelClass(movieModels.get(position));
        AppCompatActivity activity = (AppCompatActivity) passable.getContext();
        Fragment myFragment = new MovieProfileFragment();


        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myFragment).commit();
    }

    @Override
    public void favoriteClicked(int position) {
        //if theyre in favorites view and click the favorites button they remove it from favorites

        String collectionPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies";
        String documentName = "FavoriteMovies";

        DocumentReference documentReference = db.document(collectionPath + "/" + documentName);
        HashMap<String, Object> data = new HashMap<>();
        data.put(movieModels.get(position).getTitle(), FieldValue.delete());
        documentReference.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("Favorites Fragment", "Successfully deleted the field");

                Toast.makeText(getContext(), favoritesList.get(position).getMovieTitle() + " removed from favorites", Toast.LENGTH_SHORT).show();
                favoritesList.remove(position);
                PutDataIntoRecyclerView(favoritesList);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("FavoritesFragment", "Failed to delete the field");
            }
        });

    }

    @Override
    public void bookmarkClicked(int position) {
        String collectionPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies";
        String documentName = "BookmarkedMovies";
        if (movieModels.get(position).getBookMarked()) {
            DocumentReference documentReference = db.document(collectionPath + "/" + documentName);
            HashMap<String, Object> data = new HashMap<>();
            data.put(movieModels.get(position).getTitle(), FieldValue.delete());
            documentReference.update(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Favorites Fragment", "Successfully deleted the field");
                    movieModels.get(position).setBookMarked(false);
                    favoritesList.get(position).setBookMarked(false);
                    Toast.makeText(getContext(), favoritesList.get(position).getMovieTitle() + " removed from Bookmarks", Toast.LENGTH_SHORT).show();

                    PutDataIntoRecyclerView(favoritesList);

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

                    movieModels.get(position).setBookMarked(true);
                    favoritesList.get(position).setBookMarked(true);

                    Toast.makeText(getContext(), favoritesList.get(position).getMovieTitle() + " added to bookmarks", Toast.LENGTH_SHORT).show();

                    PutDataIntoRecyclerView(favoritesList);


                }
            });
        }
    }

    @Override
    public void shareClicked(int position) {
        dialog.setContentView(R.layout.custom_share_movie_popup);
        TextView linkTextView = dialog.findViewById(R.id.share_popup_link_text_view);
        ImageButton closeButton = dialog.findViewById(R.id.share_popup_close_button);
        ImageButton copyButton = dialog.findViewById(R.id.share_popup_copy_button);

        String search = favoritesList.get(position).getMovieTitle().replace(" ", "%20");
        String link;
        String dateReleased = favoritesList.get(position).getReleaseYear();
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

                MovieModelClass model = new MovieModelClass();

                model.setId(jObject.getString("id"));
                model.setTitle(jObject.getString("title"));
                model.setImg(jObject.getString("poster_path"));
                model.setImg2(jObject.getString("backdrop_path"));
                model.setReviewScore(jObject.getString("vote_average"));
                model.setDescription(jObject.getString("overview"));
                model.setDateReleased(jObject.getString("release_date"));
//

                System.out.println("Poster: "+jObject.getString("poster_path"));
                System.out.println("Title: "+jObject.getString("title"));
                System.out.println("release: "+jObject.getString("release_date"));
                System.out.println("score: "+jObject.getString("poster_path"));


                //check if movie is bookmarked
                String documentPath = "users/" + mAuth.getCurrentUser().getUid() + "/Movies/BookmarkedMovies";




                databaseManager.getDocumentSnapshot(documentPath, new FirebaseCallback() {
                    @Override
                    public void callBack(Object status) {
                        DocumentSnapshot snapshot = (DocumentSnapshot) status;
                        for (Object ds : snapshot.getData().values()) {
                            if (ds.toString().equals(model.getId())) {
                                isBookmarked = true;
                                break;
                            } else {
                                isBookmarked = false;
                            }
                        }
                        BookmarkItem item = null;
                        try {
                            item = new BookmarkItem(jObject.getString("poster_path"),
                                    jObject.getString("title"),jObject.getString("release_date"),jObject.getString("vote_average"),isBookmarked,isFavorite);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        model.setBookMarked(isBookmarked);
                        model.setFavoriteMovie(isFavorite);

                        favoritesList.add(item);
                        movieModels.add(model);


                        //when we have received all asynchronous json data then fill the view.
                        if (favoritesList.size() == favoriteTotal){
                            PutDataIntoRecyclerView(favoritesList);
                        }

                    }
                });



            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

}
