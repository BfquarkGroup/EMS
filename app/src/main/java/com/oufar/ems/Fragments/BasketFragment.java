package com.oufar.ems.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.oufar.ems.Adapter.BasketAdapter;
import com.oufar.ems.Home;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.R;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class BasketFragment extends Fragment {

    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
    private GifImageView animation;

    private Home homeClass;


    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_basket, container, false);

        swipe = view.findViewById(R.id.swipe);
        recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView = view.findViewById(R.id.recyclerView);
        animation = view.findViewById(R.id.animation);

        homeClass = (Home) getActivity();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);

                        //price.setText(homeClass.totalPrice());
                        initRecyclerView(homeClass.L(), homeClass.ImageURL());

                    }
                }, 500);
            }
        });

        initRecyclerView(homeClass.L(), homeClass.ImageURL());

        return view;
    }



    private void initRecyclerView(ArrayList<Plate> arrayList, String ImageURL){

        if (arrayList.size() == 0){

            animation.setVisibility(View.VISIBLE);
        }else {

            animation.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        BasketAdapter adapter = new BasketAdapter(getContext(), arrayList, ImageURL);
        recyclerView.setAdapter(adapter);
    }

}
