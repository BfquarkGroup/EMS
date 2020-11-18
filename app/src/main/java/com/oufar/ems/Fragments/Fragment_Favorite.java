package com.oufar.ems.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.oufar.ems.Adapter.RecyclerViewAdapter_3;
import com.oufar.ems.Adapter.RecyclerViewAdapter_6_favorite;
import com.oufar.ems.ConfirmedOrders;
import com.oufar.ems.Database.DB;
import com.oufar.ems.Model.Favorite;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.R;

import java.util.ArrayList;

public class Fragment_Favorite extends Fragment {

    private com.oufar.ems.Database.Favorite favorite;

    private RecyclerView recyclerView;
    private ImageView nothing;
    private ProgressBar progressBar;
    private ArrayList<Favorite> favoriteList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment__favorite, container, false);

        favorite = new com.oufar.ems.Database.Favorite(getContext());

        progressBar = view.findViewById(R.id.progressBar);
        recyclerView = view.findViewById(R.id.recyclerView);
        nothing = view.findViewById(R.id.nothing);

        checkMenu();

        return view;
    }

    // favorite List ----------------------------------------------------------------------------------

    public void checkMenu(){

        favoriteList.clear();

        progressBar.setVisibility(View.VISIBLE);

        Cursor cursor = favorite.viewData();

        if (cursor.getCount() == 0){

            progressBar.setVisibility(View.GONE);
            nothing.setVisibility(View.VISIBLE);

        }else {

            favoriteList.clear();

            while (cursor.moveToNext()){

                progressBar.setVisibility(View.GONE);
                nothing.setVisibility(View.GONE);

                Favorite store = new Favorite(cursor.getString(0) ,cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));

                Favorite test = new Favorite("", "", "", "", "", "", "", "", "");

                favoriteList.add(store);
            }

            initRecyclerView();
        }

    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter_6_favorite adapter = new RecyclerViewAdapter_6_favorite(getContext(), favoriteList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Save state
        Parcelable recyclerViewState;
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();
        // Restore state
        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    // end of favorite List ---------------------------------------------------------------------------

}





