package com.oufar.ems.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.oufar.ems.Adapter.RecyclerViewAdapter;
import com.oufar.ems.Model.Store;
import com.oufar.ems.R;

import java.util.ArrayList;

import pl.droidsonroids.gif.GifImageView;

public class Fragment_1 extends Fragment {

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView recyclerView;
    private EditText searchInput;
    private ProgressBar progressBar;
    private GifImageView animation;
    private FloatingActionButton food, cafeteria, patisseries, others;
    private TextView txt_food, txt_cafeteria, txt_patisseries, txt_others, nothing_txt;

    //FireBase
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;

    //vars
    //private ArrayList<Store> mNames = new ArrayList<>();
    //private ArrayList<Store> mNamesAll = new ArrayList<>();
    private ArrayList<Store> foodList = new ArrayList<>();
    private ArrayList<Store> cafeteriaList = new ArrayList<>();
    private ArrayList<Store> patisseriesList = new ArrayList<>();
    private ArrayList<Store> othersList = new ArrayList<>();
    private String Filter = "food";

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_fragment_1, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        recyclerView = view.findViewById(R.id.recyclerView);
        searchInput = view.findViewById(R.id.searchInput);
        progressBar = view.findViewById(R.id.progressBar);
        food = view.findViewById(R.id.food);
        cafeteria = view.findViewById(R.id.cafeteria);
        patisseries = view.findViewById(R.id.patisseries);
        others = view.findViewById(R.id.others);
        txt_food = view.findViewById(R.id.txt_food);
        txt_cafeteria = view.findViewById(R.id.txt_cafeteria);
        txt_patisseries = view.findViewById(R.id.txt_patisseries);
        txt_others = view.findViewById(R.id.txt_others);
        nothing_txt = view.findViewById(R.id.nothing_txt);
        animation = view.findViewById(R.id.animation);

        food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
        food.setRippleColor(Color.parseColor("#FF2D08"));
        cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
        patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
        others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
        txt_food.setTextColor(Color.parseColor("#FF2D08"));
        txt_cafeteria.setTextColor(Color.parseColor("#8C8A8A"));
        txt_patisseries.setTextColor(Color.parseColor("#8C8A8A"));
        txt_others.setTextColor(Color.parseColor("#8C8A8A"));

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                (( Activity ) getContext()).getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {

                        Rect r = new Rect();
                        ((Activity) getContext()).getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                        int screenHeight = ((Activity) getContext()).getWindow().getDecorView().getRootView().getHeight();

                        int keypadHeight = screenHeight - r.bottom;

                        //Log.d(TAG, "keypadHeight = " + keypadHeight);

                        if (keypadHeight > screenHeight * 0.15) {
                            //Keyboard is opened
                            hideNavBar(); // call function
                        }
                        else {
                            // keyboard is closed

                            //unHideNavBar();
                        }
                    }
                });

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                recyclerViewAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        food.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                Filter = "food";
                initRecyclerView(foodList, "Restaurant");

                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                food.setRippleColor(Color.parseColor("#FF2D08"));
                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                txt_food.setTextColor(Color.parseColor("#FF2D08"));
                txt_cafeteria.setTextColor(Color.parseColor("#8C8A8A"));
                txt_patisseries.setTextColor(Color.parseColor("#8C8A8A"));
                txt_others.setTextColor(Color.parseColor("#8C8A8A"));
            }
        });

        cafeteria.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                Filter = "cafeteria";
                initRecyclerView(cafeteriaList, "Cafeteria");

                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                cafeteria.setRippleColor(Color.parseColor("#FF2D08"));
                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                txt_food.setTextColor(Color.parseColor("#8C8A8A"));
                txt_cafeteria.setTextColor(Color.parseColor("#FF2D08"));
                txt_patisseries.setTextColor(Color.parseColor("#8C8A8A"));
                txt_others.setTextColor(Color.parseColor("#8C8A8A"));

            }
        });

        patisseries.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                Filter = "patisseries";
                initRecyclerView(patisseriesList, "Patisseries");

                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                patisseries.setRippleColor(Color.parseColor("#FF2D08"));
                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                txt_food.setTextColor(Color.parseColor("#8C8A8A"));
                txt_cafeteria.setTextColor(Color.parseColor("#8C8A8A"));
                txt_patisseries.setTextColor(Color.parseColor("#FF2D08"));
                txt_others.setTextColor(Color.parseColor("#8C8A8A"));

            }
        });

        others.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                Filter = "others";
                initRecyclerView(othersList, "Store");

                others.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
                others.setRippleColor(Color.parseColor("#FF2D08"));
                food.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                cafeteria.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                patisseries.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#fffafafa")));
                txt_food.setTextColor(Color.parseColor("#8C8A8A"));
                txt_cafeteria.setTextColor(Color.parseColor("#8C8A8A"));
                txt_patisseries.setTextColor(Color.parseColor("#8C8A8A"));
                txt_others.setTextColor(Color.parseColor("#FF2D08"));

            }
        });

        downloadStores();

        return view;
    }

    private String address, delivery, description, email, id, imageURL, phone, username, status, profession;

    private void downloadStores() {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null) {

            firestore.collection("Store").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    progressBar.setVisibility(View.GONE);

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

                            /*Store store = new Store(address, delivery, description, email,
                                    id, imageURL, phone, username, status, profession);
                            mNames.add(store);
                            mNamesAll.add(store);*/

                        }
                    }

                    initRecyclerView(foodList, "Restaurant");
                }
            });
        }

    }

    private void initRecyclerView(ArrayList<Store> List, String Store){

        if (List.size() == 0){

            animation.setVisibility(View.VISIBLE);
            nothing_txt.setVisibility(View.VISIBLE);
            nothing_txt.setText("There is no "+Store+" in your city");
            searchInput.setEnabled(false);
            searchInput.setHint("There is no "+Store);
        }else {

            searchInput.setHint("Search");
            searchInput.setEnabled(true);
            animation.setVisibility(View.GONE);
            nothing_txt.setVisibility(View.GONE);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter = new RecyclerViewAdapter(getContext(), List, Filter);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void hideNavBar() {

        getActivity().getWindow().addFlags( WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN );

    }

}
