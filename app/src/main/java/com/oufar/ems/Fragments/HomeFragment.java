package com.oufar.ems.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.oufar.ems.Adapter.HomeAdapter;
import com.oufar.ems.Model.Store;
import com.oufar.ems.R;

import java.util.ArrayList;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class HomeFragment extends Fragment {

    private EditText searchInput;
    private LinearLayout filterLayout;
    private SwipeRefreshLayout swipe;
    private RecyclerView recyclerView;
    private GifImageView animation;
    private FloatingActionButton food, cafeteria, patisseries, others;
    private ArrayList<Store> foodList = new ArrayList<>();
    private ArrayList<Store> cafeteriaList = new ArrayList<>();
    private ArrayList<Store> patisseriesList = new ArrayList<>();
    private ArrayList<Store> othersList = new ArrayList<>();
    private ArrayList<Store> List = new ArrayList<>();
    private HomeAdapter recyclerViewAdapter;

    private String filter = "food";

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        loadlocale();

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        searchInput = view.findViewById(R.id.searchInput);
        filterLayout = view.findViewById(R.id.filter);
        swipe = view.findViewById(R.id.swipe);
        recyclerView = view.findViewById(R.id.recyclerView);
        final LinearLayout foodLayout = view.findViewById(R.id.foodLayout);
        final LinearLayout cafeteriaLayout = view.findViewById(R.id.cafeteriaLayout);
        final LinearLayout patisseriesLayout = view.findViewById(R.id.patisseriesLayout);
        final LinearLayout othersLayout = view.findViewById(R.id.othersLayout);
        food = view.findViewById(R.id.food);
        cafeteria = view.findViewById(R.id.cafeteria);
        patisseries = view.findViewById(R.id.patisseries);
        others = view.findViewById(R.id.others);
        animation = view.findViewById(R.id.animation);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!searchInput.getText().toString().equals("")){

                    foodLayout.setEnabled(false);
                    patisseriesLayout.setEnabled(false);
                    cafeteriaLayout.setEnabled(false);
                    othersLayout.setEnabled(false);
                    swipe.setEnabled(false);
                }else {

                    foodLayout.setEnabled(true);
                    patisseriesLayout.setEnabled(true);
                    cafeteriaLayout.setEnabled(true);
                    othersLayout.setEnabled(true);
                    swipe.setEnabled(true);
                }

                recyclerViewAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });



        foodLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                filter = "food";
                List = foodList;
                initRecyclerView(filter);

                food.setImageResource(R.drawable.omlette_white);
                patisseries.setImageResource(R.drawable.cake_gray);
                cafeteria.setImageResource(R.drawable.drink_gray);
                others.setImageResource(R.drawable.box_gray);
                food.setRippleColor(Color.parseColor("#FF5722"));
                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
            }
        });

        patisseriesLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                filter = "patisseries";
                List = patisseriesList;
                initRecyclerView(filter);

                food.setImageResource(R.drawable.omlette_gray);
                patisseries.setImageResource(R.drawable.cake_white);
                cafeteria.setImageResource(R.drawable.drink_gray);
                others.setImageResource(R.drawable.box_gray);
                patisseries.setRippleColor(Color.parseColor("#FF5722"));
                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));

            }
        });

        cafeteriaLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                filter = "cafeteria";
                List = cafeteriaList;
                initRecyclerView(filter);

                food.setImageResource(R.drawable.omlette_gray);
                patisseries.setImageResource(R.drawable.cake_gray);
                cafeteria.setImageResource(R.drawable.drink_white);
                others.setImageResource(R.drawable.box_gray);
                cafeteria.setRippleColor(Color.parseColor("#FF5722"));
                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));

            }
        });

        othersLayout.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                filter = "others";
                List = othersList;
                initRecyclerView(filter);

                food.setImageResource(R.drawable.omlette_gray);
                patisseries.setImageResource(R.drawable.cake_gray);
                cafeteria.setImageResource(R.drawable.drink_gray);
                others.setImageResource(R.drawable.box_white);
                others.setRippleColor(Color.parseColor("#FF5722"));
                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));

            }
        });

        downloadStores();

        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipe.setRefreshing(false);

                        switch (filter) {
                            case "food":

                                filter = "food";
                                List = foodList;
                                initRecyclerView(filter);

                                food.setImageResource(R.drawable.omlette_white);
                                patisseries.setImageResource(R.drawable.cake_gray);
                                cafeteria.setImageResource(R.drawable.drink_gray);
                                others.setImageResource(R.drawable.box_gray);
                                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));

                                break;
                            case "patisseries":

                                filter = "patisseries";
                                List = patisseriesList;
                                initRecyclerView(filter);

                                food.setImageResource(R.drawable.omlette_gray);
                                patisseries.setImageResource(R.drawable.cake_white);
                                cafeteria.setImageResource(R.drawable.drink_gray);
                                others.setImageResource(R.drawable.box_gray);
                                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));


                                break;
                            case "cafeteria":

                                filter = "cafeteria";
                                List = cafeteriaList;
                                initRecyclerView(filter);

                                food.setImageResource(R.drawable.omlette_gray);
                                patisseries.setImageResource(R.drawable.cake_gray);
                                cafeteria.setImageResource(R.drawable.drink_white);
                                others.setImageResource(R.drawable.box_gray);
                                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));

                                break;
                            case "others":

                                filter = "others";
                                List = othersList;
                                initRecyclerView(filter);

                                food.setImageResource(R.drawable.omlette_gray);
                                patisseries.setImageResource(R.drawable.cake_gray);
                                cafeteria.setImageResource(R.drawable.drink_gray);
                                others.setImageResource(R.drawable.box_white);
                                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ffffff")));
                                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));

                                break;
                        }

                    }
                }, 500);
            }
        });

        return view;
    }

    public void loadlocale(){
        SharedPreferences pref= getContext().getSharedPreferences("CommonPrefs", Activity.MODE_PRIVATE);
        String language=pref.getString("Language","");
        //  setLocale(language);
        setApplicationLocale(language);

    }

    private void setApplicationLocale(String locale) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(locale.toLowerCase()));
            saveLocale(locale);
        } else {
            config.locale = new Locale(locale.toLowerCase());
            saveLocale(locale);
        }
        resources.updateConfiguration(config, dm);
        /*SharedPreferences.Editor editor=  this.getActivity().getSharedPreferences("Settings",MODE_PRIVATE).edit();
        editor.putString("My_Lang",locale);
        editor.apply();
        editor.commit();*/
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs = getContext(). getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();

    }


    /*
    public void home(ArrayList<Store> allStores) {

        for (int i = 0; i<allStores.size(); i++){


            String address = allStores.get(i).getAddress();
            String delivery = allStores.get(i).getDelivery();
            String description = allStores.get(i).getDescription();
            String email = allStores.get(i).getEmail();
            String id = allStores.get(i).getId();
            String imageURL = allStores.get(i).getImageURL();
            String phone = allStores.get(i).getPhone();
            String username = allStores.get(i).getUsername();
            String status = allStores.get(i).getStatus();
            String profession = allStores.get(i).getProfession();

            if (profession.equals("food")){

                Store store = new Store(address, delivery, description, email,
                        id, imageURL, phone, username, status, profession);

                foodList.add(store);
            }
            if (profession.equals("cafeteria")){

                Store store = new Store(address, delivery, description, email,
                        id, imageURL, phone, username, status, profession);
                cafeteriaList.add(store);
            }
            if (profession.equals("patisseries")){

                Store store = new Store(address, delivery, description, email,
                        id, imageURL, phone, username, status, profession);
                patisseriesList.add(store);
            }
            if (profession.equals("others")){

                Store store = new Store(address, delivery, description, email,
                        id, imageURL, phone, username, status, profession);
                othersList.add(store);
            }
        }

        initRecyclerView(foodList);
    }*/


    private String address, delivery, description, email, id, imageURL, phone, username, status, profession, rate;

    private void downloadStores() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {

            firestore.collection("Store").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    animation.setVisibility(View.GONE);

                    foodList.clear();
                    cafeteriaList.clear();
                    patisseriesList.clear();
                    othersList.clear();

                    assert queryDocumentSnapshots != null;

                    if (firebaseUser != null && queryDocumentSnapshots != null) {

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {

                            assert doc != null;

                            address = doc.getString("city");
                            delivery = doc.getString("delivery");
                            description = doc.getString("description");
                            email = doc.getString("email");
                            id = doc.getString("id");
                            imageURL = doc.getString("imageURL");
                            phone = doc.getString("phone");
                            username = doc.getString("username");
                            status = doc.getString("status");
                            profession = doc.getString("profession");
                            rate = doc.getString("rate");


                            if (profession.equals("food")){

                                Store store = new Store(address, delivery, description, email,
                                        id, imageURL, phone, username, status, profession, rate);

                                foodList.add(store);
                            }
                            if (profession.equals("cafeteria")){

                                Store store = new Store(address, delivery, description, email,
                                        id, imageURL, phone, username, status, profession, rate);
                                cafeteriaList.add(store);
                            }
                            if (profession.equals("patisseries")){

                                Store store = new Store(address, delivery, description, email,
                                        id, imageURL, phone, username, status, profession, rate);
                                patisseriesList.add(store);
                            }
                            if (profession.equals("others")){

                                Store store = new Store(address, delivery, description, email,
                                        id, imageURL, phone, username, status, profession, rate);
                                othersList.add(store);
                            }

                        }
                    }

                    //initRecyclerView(filter);
                }
            });
        }

        initRecyclerView(filter);
    }

    private void initRecyclerView(String filter){



        switch (filter) {
            case "food":

                filter = "food";
                List = foodList;
                break;
            case "patisseries":

                filter = "patisseries";
                List = patisseriesList;
                break;
            case "cafeteria":

                filter = "cafeteria";
                List = cafeteriaList;
                break;
            case "others":

                filter = "others";
                List = othersList;
                break;
        }

        FirebaseUser firebaseUser;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){


            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerViewAdapter = new HomeAdapter(getContext(), List);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    /*private void initRecyclerView(ArrayList<Store> List){

        FirebaseUser firebaseUser;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){


            LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerViewAdapter = new HomeAdapter(getContext(), List);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }*/


}
