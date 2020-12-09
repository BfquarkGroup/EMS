package com.oufar.ems.Fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.oufar.ems.Adapter.FavoriteAdapter;
import com.oufar.ems.Home;
import com.oufar.ems.Model.Favorite;
import com.oufar.ems.R;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class FavoriteFragment extends Fragment {

    private com.oufar.ems.Database.Favorite favorite;

    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
    private GifImageView animation;
    private ArrayList<Favorite> favoriteList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_favorite, container, false);

        favorite = new com.oufar.ems.Database.Favorite(getContext());

        swipe = view.findViewById(R.id.swipe);
        recyclerView = view.findViewById(R.id.recyclerView);

        animation = view.findViewById(R.id.animation);swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);

                        //price.setText(homeClass.totalPrice());
                        checkMenu();

                    }
                }, 500);
            }
        });

        checkMenu();

        return view;
    }


    public void checkMenu(){

        Home homeClass;
        homeClass = (Home) getActivity();

        homeClass.home();

        String Status = "open";

        favoriteList.clear();

        Cursor cursor = favorite.viewData();

        if (cursor.getCount() == 0){

            animation.setVisibility(View.VISIBLE);

        }else {

            favoriteList.clear();

            while (cursor.moveToNext()){

                animation.setVisibility(View.GONE);

                for (int i = 0; i< homeClass.stores().size(); i++){

                    if (cursor.getString(1).equals(homeClass.stores().get(i).getId())){

                        Status = homeClass.stores().get(i).getStatus();
                    }
                }

                Favorite store = new Favorite(cursor.getString(0) ,cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), Status, cursor.getString(8));

                Favorite test = new Favorite("", "", "", "", "", "", "", "", "");

                favoriteList.add(store);
            }

            initRecyclerView();
        }

    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        FavoriteAdapter adapter = new FavoriteAdapter(getContext(), favoriteList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        Parcelable recyclerViewState;
        recyclerViewState = recyclerView.getLayoutManager().onSaveInstanceState();

        recyclerView.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }
}





