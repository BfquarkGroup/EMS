package com.oufar.ems;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oufar.ems.Adapter.RecyclerViewAdapter;
import com.oufar.ems.Adapter.RecyclerViewAdapter_5;
import com.oufar.ems.Database.DB;
import com.oufar.ems.Fragments.Fragment_2;
import com.oufar.ems.Model.Info;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.Model.Store;

import java.util.ArrayList;
import java.util.HashMap;

import pl.droidsonroids.gif.GifImageView;

public class ConfirmedOrders extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    private TextView nothing_txt, store, price;
    private RecyclerView recyclerView;
    private GifImageView nothing;
    private ImageView info, closeUp;
    private RelativeLayout storeLayout, priceLayout;
    //private ProgressBar progressBar;
    private ArrayList<Plate> mNames = new ArrayList<>();
    private ArrayList<Info> infoList = new ArrayList<>();

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmed_orders);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.custom_green));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.custom_green));
        }
        setNavigationBarButtonsColor(this, R.color.custom_green);

        recyclerView = findViewById(R.id.recyclerView);
        nothing = findViewById(R.id.nothing);
        nothing_txt = findViewById(R.id.nothing_txt);
        store = findViewById(R.id.store);
        price = findViewById(R.id.price);
        info = findViewById(R.id.info);
        closeUp = findViewById(R.id.closeUp);
        storeLayout = findViewById(R.id.storeLayout);
        priceLayout = findViewById(R.id.priceLayout);
        //progressBar = findViewById(R.id.progressBar);

        closeUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeUp.setVisibility(View.GONE);
                //storeLayout.setVisibility(View.GONE);
                priceLayout.setVisibility(View.GONE);
            }
        });

        checkMenu();
    }

    private void checkMenu() {

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        //progressBar.setVisibility(View.VISIBLE);
        nothing.setVisibility(View.VISIBLE);

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                closeUp.setVisibility(View.VISIBLE);
                //storeLayout.setVisibility(View.VISIBLE);
                priceLayout.setVisibility(View.VISIBLE);
            }
        });

        reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(firebaseUser.getUid())){

                    downloadMenu();

                }else {

                    if (mNames.size() == 0) {

                        price.setText("0,00 DA");
                    }

                    //progressBar.setVisibility(View.GONE);
                    nothing.setVisibility(View.VISIBLE);
                    nothing_txt.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadMenu() {

        nothing.setVisibility(View.GONE);
        nothing_txt.setVisibility(View.GONE);

        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //progressBar.setVisibility(View.GONE);

                mNames.clear();
                infoList.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    String Id = snapshot.child("id").getValue().toString();
                    String Plate = snapshot.child("plate").getValue().toString();
                    String Price = snapshot.child("price").getValue().toString();
                    String Description = snapshot.child("description").getValue().toString();
                    String ImageURL = snapshot.child("imageURL").getValue().toString();
                    String StoreId = snapshot.child("storeId").getValue().toString();
                    String Number = snapshot.child("number").getValue().toString();
                    String Status = snapshot.child("status").getValue().toString();
                    String Store = snapshot.child("storeName").getValue().toString();
                    String StoreEmail = snapshot.child("storeName").getValue().toString();

                    Plate plate = new Plate(Id, Plate, Price, Description, ImageURL, StoreId,"", Number, Status, StoreEmail);

                    Info info = new Info(Id, StoreId, Store, Price, Number);

                    price.setText("0,00 DA");

                    mNames.add(plate);

                    if (Status.equals("accepted")) {
                        infoList.add(info);
                    }
                }

                initRecyclerView();
                deleteOccurrenceValues(infoList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteOccurrenceValues(ArrayList<Info> arrayList) {

        for(int i = 0; i < arrayList.size();i++){

            if (i != 0) {

                if (arrayList.get(i).equals(arrayList.get(i - 1))) {
                    arrayList.remove(i--);
                }
            }
        }

        showInfo(arrayList);

    }

    private void showInfo(ArrayList<Info> arrayList) {

        int total = 0;

        for(int i = 0; i < arrayList.size();i++){

            total += Integer.parseInt(arrayList.get(i).getPrice()) * Integer.parseInt(arrayList.get(i).getNumber());

            price.setText(total+",00 DA");

        }
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter_5 adapter = new RecyclerViewAdapter_5(this, mNames);
        recyclerView.setAdapter(adapter);
    }


    private void setNavigationBarButtonsColor(Activity activity, int navigationBarColor) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isColorLight(navigationBarColor)) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    private boolean isColorLight(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        return darkness < 0.5;
    }

}