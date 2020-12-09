package com.oufar.ems;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.oufar.ems.Adapter.BonAdapter;
import com.oufar.ems.Adapter.MenuAdapter;
import com.oufar.ems.Database.DB;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.Model.Store;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;

public class Menu extends AppCompatActivity {

    private ProgressBar progressBar;
    private AppBarLayout AppBarLayout2;
    private ImageView rate;
    private RelativeLayout ratingLayout;
    private RatingBar ratingBar;
    private TextView ratingTitle;
    private TextView nothing;
    private TextView totalPrice;
    private Button confirm, buy;
    private RadioButton currentAddress;
    private EditText searchInput;
    private MenuAdapter menuAdapter;
    private RecyclerView recyclerView, recyclerView2;
    private GifImageView animation, animation2;

    private String click = "1";
    private LocationManager locationManager;
    private LocationListener locationListener;
    private static String Client_email;
    private FirebaseFirestore firestore;
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;

    private ArrayList<Plate> mNames = new ArrayList<>();

    private String Store_id, Store_name, Store_image, Store_status, Store_email,Home_lat = "", Home_lng = "", Username, Store_rate;

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

        if(firebaseUser!=null){

            Client_email = firebaseUser.getEmail();

            //Toast.makeText(getContext(), ""+Client_email, Toast.LENGTH_SHORT).show();
        }

        firestore = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        Store_id = bundle.getString("Store_id");
        Store_name = bundle.getString("Store_name");
        Store_image = bundle.getString("Store_image");
        Store_status = bundle.getString("Store_status");
        Store_email = bundle.getString("Store_email");
        Store_rate = bundle.getString("Store_rate");
        //Home_lat = bundle.getString("Home_lat");
        //Home_lng = bundle.getString("Home_lng");
        //Username = bundle.getString("Username");



        //Toast.makeText(this, Store_rate, Toast.LENGTH_SHORT).show();

        AppBarLayout2 = findViewById(R.id.AppBarLayout2);
        TextView storeName = findViewById(R.id.storeName);
        CircleImageView storeImage = findViewById(R.id.storeImage);
        rate = findViewById(R.id.rate);
        ratingLayout = findViewById(R.id.ratingLayout);
        ratingTitle = findViewById(R.id.ratingTitle);
        ratingBar = findViewById(R.id.ratingBar);
        Button ratingButton = findViewById(R.id.ratingButton);
        CardView close = findViewById(R.id.close);
        confirm = findViewById(R.id.confirm);
        buy = findViewById(R.id.buy);
        RadioGroup radioGroup = findViewById(R.id.radioGroup);
        final RadioButton homeAddress = findViewById(R.id.homeAddress);
        currentAddress = findViewById(R.id.currentAddress);
        searchInput = findViewById(R.id.searchInput);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView2 = findViewById(R.id.recyclerView2);
        animation = findViewById(R.id.animation);
        animation2 = findViewById(R.id.animation2);
        nothing = findViewById(R.id.nothing);
        progressBar = findViewById(R.id.progressBar);
        TextView closeBon = findViewById(R.id.closeBon);
        final EditText latlng = findViewById(R.id.latlng);
        totalPrice = findViewById(R.id.totalPrice);
        ratingTitle.setEnabled(false);

        searchInput.setHint("Search in "+Store_name);

        storeName.setText(Store_name);

        Glide.with(this)
                .asBitmap()
                .load(Store_image)
                .into(storeImage);

        if (Store_status.equals("close")){

            close.setVisibility(View.VISIBLE);
        }

        ratingTitle.setText("Rate "+Store_name);

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (click.equals("1")){

                    ratingLayout.setVisibility(View.VISIBLE);
                    animation2.setVisibility(View.VISIBLE);
                    click = "2";

                    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                    firestore.collection("Store").document(Store_id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                            assert documentSnapshot != null;

                            animation2.setVisibility(View.GONE);

                            if (documentSnapshot != null) {

                                Store_rate = documentSnapshot.get("rate").toString();
                                if (!Store_rate.equals("null") && Store_rate != null) {
                                    ratingBar.setRating(Float.parseFloat(Store_rate));
                                }else {

                                    ratingBar.setRating(Float.parseFloat("3"));
                                }
                            }

                        }
                    });

                }else if (click.equals("2")){

                    animation2.setVisibility(View.GONE);
                    ratingLayout.setVisibility(View.GONE);
                    click = "1";
                }

            }
        });

        ratingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ratingLayout.setVisibility(View.GONE);
                click = "1";            }
        });

        // perform click event on button
        ratingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //String totalStars = "" + ratingBar.getNumStars();
                String rating = "" + ratingBar.getRating();
                Float r = null;
                HashMap<String, Object> map = new HashMap<>();

                if (!Store_rate.equals("null") && Store_rate != null) {
                    r = (Float.parseFloat(Store_rate) + Float.parseFloat(rating)) / 2;
                }else {

                    r = (Float.parseFloat("3") + Float.parseFloat(rating)) / 2;
                }


                //Toast.makeText(Menu.this, r+"", Toast.LENGTH_SHORT).show();



                final ProgressDialog progressDialog = new ProgressDialog(Menu.this);
                progressDialog.setMessage("Uploading...");
                progressDialog.show();

                map.put("rate", r+"");

                firestore.collection("Store").document(Store_id).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            progressDialog.dismiss();

                            //updatePage();
                        }else {

                            Toast.makeText(Menu.this, "an error occurred", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }

                    }
                });




                ratingLayout.setVisibility(View.GONE);
                click = "1";
                //Toast.makeText(getApplicationContext(), totalStars + "\n" + rating, Toast.LENGTH_LONG).show();
            }
        });



        //downloadPlates();
        checkMenu();

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                menuAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppBarLayout2.setVisibility(View.VISIBLE);
                searchInput.setVisibility(View.GONE);
                checkMenu2();
                confirm.setVisibility(View.GONE);
                buy.setVisibility(View.VISIBLE);
                rate.setEnabled(false);
                initRecyclerView();
            }
        });

        closeBon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppBarLayout2.setVisibility(View.GONE);
                searchInput.setVisibility(View.VISIBLE);
                rate.setEnabled(true);
                confirm.setVisibility(View.VISIBLE);
                buy.setVisibility(View.GONE);
            }
        });

        final String[] Lat = new String[1];
        final String[] Lon = new String[1];
        locationManager = ( LocationManager ) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                Lat[0] = location.getLatitude()+"";
                Lon[0] = location.getLongitude()+"";

                latlng.setText("current coordinates \n"+location.getLatitude()+", "+location.getLongitude());

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        currentAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (currentAddress.isChecked()) {

                    getLocation();

                    animation.setVisibility(View.VISIBLE);

                    if (ActivityCompat.checkSelfPermission(Menu.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Menu.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);

                    latlng.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                            animation.setVisibility(View.GONE);
                        }

                        @Override
                        public void afterTextChanged(Editable editable) {

                        }
                    });
                }

                if (!currentAddress.isChecked()){

                    animation.setVisibility(View.GONE);
                }
            }
        });

        buy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                if (!totalPrice.getText().toString().equals("Total :: 0.0 DA")){

                    if (currentAddress.isChecked() && !latlng.getText().toString().equals("0.00, 0.00")) {

                        AlertDialog Alert = new AlertDialog.Builder(Menu.this)
                                .setMessage("Buy selected products and deliver order to current location ?")
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {


                                        sendOrder(Lat[0], Lon[0]);
                                    }
                                })
                                .setNegativeButton("no", null)
                                .show();
                    } else if (homeAddress.isChecked()) {

                        animation.setVisibility(View.VISIBLE);

                        AlertDialog Alert = new AlertDialog.Builder(Menu.this)
                                .setMessage("Buy selected products and deliver order to home ?")
                                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {


                                        if (!homeLat().equals("") && !homeLng().equals("")){

                                            sendOrder(homeLat(), homeLng());
                                        }else {

                                            Toast.makeText(Menu.this, "Check your home address", Toast.LENGTH_SHORT).show();
                                        }


                                    }
                                })
                                .setNegativeButton("no", null)
                                .show();
                    }

                }
            }
        });

        getHomeLocation();

    }

    private void getHomeLocation() {

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        if (!firebaseUser.getUid().isEmpty()) {

            firestore.collection("Client").document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    assert documentSnapshot != null;

                    animation.setVisibility(View.GONE);

                    if (documentSnapshot != null) {

                        Username = documentSnapshot.get("username").toString();
                        Home_lat = documentSnapshot.get("homeLat").toString();
                        Home_lng = documentSnapshot.get("homeLng").toString();

                        //homeLat(Home_lat);
                        //homeLng(Home_lng);

                    }

                }
            });
        }

    }

    private String homeLng() {

        return Home_lng;
    }

    private String homeLat() {

        return Home_lat;
    }

    @SuppressLint("NewApi")
    private void getLocation() {

        //animation.setVisibility(View.GONE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);

            return;
        }

        if (ActivityCompat.checkSelfPermission(Menu.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Menu.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);

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
        menuAdapter = new MenuAdapter(Menu.this, mNames);
        recyclerView.setAdapter(menuAdapter);
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

    // 2

    private ArrayList<Plate> mNames2 = new ArrayList<>();
    private ArrayList<Plate> notificationList = new ArrayList<>();
    private DB db;
    public void checkMenu2(){

        db = new DB(this);

        mNames2.clear();
        //double total = 0;

        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){

            //totalPrice.setText("Total :: "+total+" DA");

        }else {

            mNames2.clear();

            while (cursor.moveToNext()){


                if (Store_id.equals(cursor.getString(2))){

                    Plate plate = new Plate(cursor.getString(0), cursor.getString(3),
                            cursor.getString(7), cursor.getString(5),
                            cursor.getString(4), cursor.getString(2),
                            cursor.getString(0), cursor.getString(6),
                            "", cursor.getString(8));

                    Plate plate1 = new Plate(cursor.getString(0), cursor.getString(3), cursor.getString(7), "", cursor.getString(4), Store_id, "", cursor.getString(6), "", "");

                    double num2 = Double.parseDouble(cursor.getString(6));
                    if (num2 > 0) {

                        mNames2.add(plate1);
                    }
                }

                //totalPrice.setText("Total :: "+total+" DA");
                initRecyclerView2(mNames2);

            }
        }
    }

    double total = 0;
    public void initRecyclerView2(ArrayList<Plate> mNames2 ){

        total = 0;

        for (int i = 0; i < mNames2.size(); i++) {

            double d = Double.parseDouble(mNames2.get(i).getPrice());

            double num = Double.parseDouble(mNames2.get(i).getNumber());

            total = total + (d * num);

        }

        totalPrice.setText("Total :: "+total+" DA");

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView2.setLayoutManager(layoutManager);
        BonAdapter adapter = new BonAdapter(this, mNames2, Store_id, new BonAdapter.OnQuantityChangeListener() {
            @Override
            public void onQuantityChange(String change) {

                totalPrice.setText("Total :: "+change+" DA");
            }
        });
        recyclerView2.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Save state
        Parcelable recyclerViewState;
        recyclerViewState = recyclerView2.getLayoutManager().onSaveInstanceState();
        // Restore state
        recyclerView2.getLayoutManager().onRestoreInstanceState(recyclerViewState);
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



    private void sendOrder(String lat, String lon) {

        //Toast.makeText(Menu.this, "Sending order to current location\n"+ Lat+"\n"+Lon, Toast.LENGTH_SHORT).show();

        mNames2.clear();

        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){

            Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show();

        }else {

            mNames2.clear();

            notificationList.clear();

            while (cursor.moveToNext()){

                int num = Integer.parseInt(cursor.getString(6));

                if (num == 0 ){

                    db.deleteData(cursor.getString(0), null,null,null,null,null,null,null, null);

                }

                animation.setVisibility(View.VISIBLE);


                Plate plate = new Plate(cursor.getString(0), cursor.getString(3),
                        cursor.getString(7), cursor.getString(5),
                        cursor.getString(4), cursor.getString(2),
                        cursor.getString(0), cursor.getString(6),
                        "", cursor.getString(8));

                mNames2.add(plate);

                reference = FirebaseDatabase.getInstance().getReference("Orders").child(firebaseUser.getUid()).push();

                HashMap<String, String> hashMap;
                hashMap = new HashMap<>();

                hashMap.put("id", reference.getKey());
                hashMap.put("plate", cursor.getString(3));
                hashMap.put("price", cursor.getString(7));
                hashMap.put("description", cursor.getString(5));
                hashMap.put("imageURL", cursor.getString(4));
                hashMap.put("clientId", firebaseUser.getUid());
                hashMap.put("storeId", cursor.getString(2));
                hashMap.put("number", cursor.getString(6));
                hashMap.put("storeEmail", cursor.getString(8));
                hashMap.put("clientEmail", Client_email);
                hashMap.put("status", "on hold");
                hashMap.put("delivery", "on hold");
                hashMap.put("storeName", "on hold");

                //int price = Integer.parseInt(cursor.getString(6)) * Integer.parseInt(cursor.getString(7));


                String msg=" Order from : "+Username+" \n"+cursor.getString(6)+" "+cursor.getString(3)+" ("+totalPrice.getText().toString()+") DA";

                Toast.makeText(Menu.this, msg, Toast.LENGTH_SHORT).show();

                sendNotification(msg,cursor.getString(8));
                reference.setValue(hashMap);


                Plate notify = new Plate(reference.getKey(), cursor.getString(3),
                        cursor.getString(7), cursor.getString(5),
                        cursor.getString(4), cursor.getString(2),
                        "", cursor.getString(6),
                        "", cursor.getString(8));

                notificationList.add(notify);

            }

            animation.setVisibility(View.GONE);
        }


        //todo: after adding a lat_lng field for each client info we will edit the value by using client's gps data then click on confirm button to upload the new info

        HashMap<String, String> hashMap_ = new HashMap<>();
        hashMap_.put("lat", "0.0");
        hashMap_.put("lng", "0.0");

        firestore.collection("Client").document(firebaseUser.getUid())
                .update("lat", lat,"lng", lon).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    clear_();
                    initRecyclerView2(mNames2);

                    //recyclerView2.setVisibility(View.GONE);

                    for(int i = 0; i < notificationList.size();i++){

                        if (i != 0) {

                            if (notificationList.get(i).equals(notificationList.get(i - 1))) {
                                notificationList.remove(i--);
                            }
                        }
                    }

                    //organizeNotification(notificationList);

                }else {

                    Toast.makeText(Menu.this, "ERROR", Toast.LENGTH_SHORT).show();
                }

            }
        });

        AppBarLayout2.setVisibility(View.GONE);
        searchInput.setVisibility(View.VISIBLE);
        rate.setEnabled(true);
        confirm.setVisibility(View.VISIBLE);
        buy.setVisibility(View.GONE);
    }

    private void clear_() {

        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){


        }else {

            while (cursor.moveToNext()) {

                if (Store_id.equals(cursor.getString(2))){

                    db.deleteData(cursor.getString(0), firebaseUser.getUid(),
                            cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getString(5),
                            "", cursor.getString(7),
                            cursor.getString(8));
                }

            }
        }

        checkMenu2();

        totalPrice.setText("Total :: 0.0 DA");

    }

    private void sendNotification(final String msg , final String send_email) {

        // Toast.makeText(this, "Connade envoiyer)", Toast.LENGTH_SHORT).show();


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                int SDK_INT = android.os.Build.VERSION.SDK_INT;
                if (SDK_INT > 8) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                            .permitAll().build();
                    StrictMode.setThreadPolicy(policy);



                    //This is a Simple Logic to Send Notification different Device Programmatically....
                    /**if (MainActivity.LoggedIn_User_Email.equals("ouss@mail.com")) {
                     send_email = "farouk@mail.com";
                     } else {
                     send_email = "ouss@mail.com";
                     }*/

                    try {
                        String jsonResponse;

                        URL url = new URL("https://onesignal.com/api/v1/notifications");
                        HttpURLConnection con = (HttpURLConnection) url.openConnection();
                        con.setUseCaches(false);
                        con.setDoOutput(true);
                        con.setDoInput(true);

                        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                        con.setRequestProperty("Authorization", "Basic ZDU5NmMyN2ItOTIyMy00N2VjLWEyNzMtMzFiYTAwODQ1Nzlh");
                        con.setRequestMethod("POST");

                        String strJsonBody = "{"
                                + "\"app_id\": \"74db2f5e-d598-45b8-9e6d-516ed161427b\","
                                + "\"included_segments\": [\"All\"],"
                                + "\"filters\": [{\"field\": \"tag\", \"key\": \"User_ID\", \"relation\": \"=\", \"value\": \"" + send_email + "\"}],"


                                + "\"data\": {\"foo\": \"bar\"},"
                                + "\"contents\": {\"en\": \""+ msg +"\"}"
                                //    + "\"buttons\": [{\"id\": \"id1\", \"text\": \"button1\", \"icon\": \"ic_menu_share\"}, {\"id\": \"id2\", \"text\": \"button2\", \"icon\": \"ic_menu_send\"}]}"
                                + "}";


                        System.out.println("strJsonBody:\n" + strJsonBody);

                        byte[] sendBytes = strJsonBody.getBytes("UTF-8");
                        con.setFixedLengthStreamingMode(sendBytes.length);

                        OutputStream outputStream = con.getOutputStream();
                        outputStream.write(sendBytes);

                        int httpResponse = con.getResponseCode();
                        System.out.println("httpResponse: " + httpResponse);

                        if (httpResponse >= HttpURLConnection.HTTP_OK
                                && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
                            Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        } else {
                            Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
                            jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
                            scanner.close();
                        }
                        System.out.println("jsonResponse:\n" + jsonResponse);

                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        });

    }

}
