package com.oufar.ems;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.oufar.ems.Adapter.RecyclerViewAdapter;
import com.oufar.ems.Adapter.RecyclerViewAdapter_2;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.Model.UnselectedPlate;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Menu extends AppCompatActivity {

    private ProgressBar progressBar;
    private CircleImageView storeImage;
    private TextView nothing;
    private CardView close;
    private Button basket;
    private EditText searchInput;
    private RecyclerView recyclerView;
    private FloatingActionButton btn_add_plate;
    private MaterialEditText plate, price, description;

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    private HashMap<String, String> hashMap;

    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private String imageURL;
    private StorageReference storageReference;
    private StorageTask uploadTask;

    private RecyclerViewAdapter_2 recyclerViewAdapter_2;
    private int SEARCH = 1;

    //vars
    private ArrayList<Plate> mNames = new ArrayList<>();

    private TextView storeName;
    private String Store_id, Store_name, Store_image, Store_status, Store_email;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.white));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }
        setNavigationBarButtonsColor(this, R.color.white);


        Bundle bundle = getIntent().getExtras();
        Store_id = bundle.getString("Store_id");
        Store_name = bundle.getString("Store_name");
        Store_image = bundle.getString("Store_image");
        Store_status = bundle.getString("Store_status");
        Store_email = bundle.getString("Store_email");


        //Toast.makeText(this, Store_email, Toast.LENGTH_SHORT).show();

        storeName = findViewById(R.id.storeName);
        storeImage = findViewById(R.id.storeImage);
        close = findViewById(R.id.close);
        basket = findViewById(R.id.basket);
        searchInput = findViewById(R.id.searchInput);
        recyclerView = findViewById(R.id.recyclerView);
        nothing = findViewById(R.id.nothing);
        progressBar = findViewById(R.id.progressBar);

        storeName.setText(Store_name);

        Glide.with(this)
                .asBitmap()
                .load(Store_image)
                .into(storeImage);

        if (Store_status.equals("close")){

            close.setVisibility(View.VISIBLE);
        }


        //downloadPlates();
        checkMenu();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                recyclerViewAdapter_2.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        basket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Intent intent = new Intent(Menu.this, Home.class);
                intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("basket", "basket");
                intent.putExtra("Store_id", Store_id);// sending Store id
                intent.putExtra("Store_name", Store_name);// sending Store name
                intent.putExtra("Store_image", Store_image);// sending Store image
                intent.putExtra("Store_status", Store_status);// sending Store status
                intent.putExtra("Store_email", Store_email);// sending Store email
                startActivity(intent);
            }
        });

    }

    private void checkMenu(){

        progressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("Store").document(Store_id).
                collection("StoreMenu").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                progressBar.setVisibility(View.GONE);

                mNames.clear();

                assert queryDocumentSnapshots != null;

                if (Store_id != null  && queryDocumentSnapshots != null) {

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {

                        assert doc != null;

                        nothing.setVisibility(View.GONE);

                        String plate = doc.getString("plate");
                        String price = doc.getString("price");
                        String description = doc.getString("description");
                        String imageURL = doc.getString("imageURL");

                        Plate plate_ = new Plate("", plate, price, description, imageURL, Store_id, "", "", "", Store_email);
                        mNames.add(plate_);

                    }
                }

                initRecyclerView();

            }
        });
    }

    private void initRecyclerView(){

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerViewAdapter_2 = new RecyclerViewAdapter_2(Menu.this, mNames);
        recyclerView.setAdapter(recyclerViewAdapter_2);
    }

    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Menu.this, Home.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("basket", "no");
        intent.putExtra("Store_id", Store_id);// sending Store id
        intent.putExtra("Store_name", Store_name);// sending Store name
        intent.putExtra("Store_image", Store_image);// sending Store image
        startActivity(intent);
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
