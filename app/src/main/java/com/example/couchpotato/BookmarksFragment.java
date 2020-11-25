package com.example.couchpotato;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookmarksFragment extends Fragment {

    RecyclerView bookmarkRecyclerView;
    BookmarkAdapter bookmarkAdapter;
    ArrayList<BookmarkItem> bookmarkItems;

    public BookmarksFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bookmarkItems = new ArrayList<>();
        //bookmarkItems.add(new BookmarkItem(R.drawable.ic_baseline_live_tv_24, "Spuds 3", "2020", "*****"));

        // build recycler view
        final View rootView = inflater.inflate(R.layout.bookmarks_fragment, container, false);
        bookmarkRecyclerView = (RecyclerView) rootView.findViewById(R.id.bookmarksRecyclerView);
        bookmarkRecyclerView.setHasFixedSize(true);
        bookmarkAdapter = new BookmarkAdapter(bookmarkItems);
        bookmarkRecyclerView.setAdapter(bookmarkAdapter);
        bookmarkRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return rootView;
    }

}
