package com.oufar.ems;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.zxing.WriterException;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.onesignal.OneSignal;
import com.oufar.ems.Adapter.HomeAdapter;
import com.oufar.ems.Fragments.BasketFragment;
import com.oufar.ems.Fragments.FavoriteFragment;
import com.oufar.ems.Fragments.HomeFragment;
import com.oufar.ems.Fragments.ProfileFragment;
import com.oufar.ems.Model.Info;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.Model.Store;

import java.util.ArrayList;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class Home extends AppCompatActivity {

    private TextView dynamic_txt;
    private ImageView dynamic;
    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    private String QRCode = "", click = "1";
    private LinearLayout QRCodeLayout;
    private ImageView QRCodeOutput;



    private String Store_id, Store_name, Store_image, Store_status, Store_email;
    private String B;
    private String totalPrice = "";

    String txt_username = "";
    String txt_phone = "";
    String txt_wilaya = "";
    String txt_city = "";
    String txt_lat = "";
    String txt_lng = "";
    String home_lat = "";
    String home_lng = "";
    String txt_description = "";
    String profile_imageURL ="";
    String avatar = "";

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // OneSignal Initialization
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.white));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.orange));
        }

        setNavigationBarButtonsColor(this);


        Bundle bundle = getIntent().getExtras();
        final String Basket = bundle.getString("basket");
        Store_id = bundle.getString("Store_id");
        Store_name = bundle.getString("Store_name");
        Store_image = bundle.getString("Store_image");
        Store_status = bundle.getString("Store_status");
        Store_email = bundle.getString("Store_email");

        B = Basket;

        dynamic_txt = findViewById(R.id.dynamic_txt);
        ChipNavigationBar navigation_bar = findViewById(R.id.navigation_bar);
        dynamic = findViewById(R.id.dynamic);
        QRCodeLayout = findViewById(R.id.QRCodeLayout);
        QRCodeOutput = findViewById(R.id.QRCodeOutput);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        final HomeFragment homeFragment = new HomeFragment();
        final BasketFragment basketFragment = new BasketFragment();
        final FavoriteFragment favoriteFragment = new FavoriteFragment();
        final ProfileFragment profileFragment = new ProfileFragment();
        adapter.addFragment(homeFragment);
        adapter.addFragment(basketFragment);
        adapter.addFragment(favoriteFragment);
        adapter.addFragment(profileFragment);

        navigation_bar.setItemSelected(R.id.home, true);
        QRCode = "";

        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, homeFragment)
                .commit();

        navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {

                switch(id){

                    case R.id.home:

                        fragment = homeFragment;
                        QRCode = "";
                        break;

                    case R.id.basket:

                        fragment = basketFragment;
                        QRCode = "";
                        break;

                    case R.id.favorite:

                        fragment = favoriteFragment;
                        QRCode = "";
                        break;

                    case R.id.profile:

                        fragment = profileFragment;
                        QRCode = "QRCode";
                        break;
                }

                if (fragment != null){

                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainer, fragment)
                            .commit();


                    if (fragment == homeFragment){

                        dynamic_txt.setText("Stores");
                        dynamic.setImageResource(R.drawable.compass);


                    }else if (fragment == basketFragment){

                        dynamic_txt.setText(totalPrice);
                        dynamic.setImageResource(R.drawable.basket_3);
                        QRCodeLayout.setVisibility(View.GONE);
                        click = "1";

                    }else if (fragment == favoriteFragment){

                        dynamic_txt.setText("Best stores");
                        dynamic.setImageResource(R.drawable.heart_orange_2);
                        QRCodeLayout.setVisibility(View.GONE);
                        click = "1";

                    }else if (fragment == profileFragment){

                        dynamic_txt.setText("Profile");
                        dynamic.setImageResource(R.drawable.qrcode_orange);
                        QRCodeLayout.setVisibility(View.GONE);
                        click = "1";
                    }
                }
            }
        });

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String data = firebaseUser.getUid();
        if(data.isEmpty()){

            Toast.makeText(Home.this, "check your network", Toast.LENGTH_SHORT).show();
            //qrvalue.setError("Value Required.");
        }else {
            QRGEncoder qrgEncoder = new QRGEncoder(data,null, QRGContents.Type.TEXT,500);
            try {
                Bitmap qrBits = qrgEncoder.encodeAsBitmap();
                QRCodeOutput.setImageBitmap(qrBits);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        dynamic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (QRCode.equals("QRCode") && click.equals("1")){

                    QRCodeLayout.setVisibility(View.VISIBLE);
                    click = "2";
                }else if (QRCode.equals("QRCode") && click.equals("2")){

                    QRCodeLayout.setVisibility(View.GONE);
                    click = "1";
                }
            }
        });

        QRCodeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                QRCodeLayout.setVisibility(View.GONE);
            }
        });

        //home();
        checkBasket();
        //favorite();
        profile();

    }

    private void hideNavBar() {

        Home.this.getWindow().addFlags( WindowManager.LayoutParams.FLAG_LAYOUT_IN_OVERSCAN );

    }

// homeFragment

    private ArrayList<Store> STORES = new ArrayList<>();
    private ArrayList<Store> allStores = new ArrayList<>();
    public void home() {


        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {

            firestore.collection("Store").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    allStores.clear();

                    assert queryDocumentSnapshots != null;

                    if (firebaseUser != null && queryDocumentSnapshots != null) {

                        for (DocumentSnapshot doc : queryDocumentSnapshots) {

                            assert doc != null;

                            String address = doc.getString("city");
                            String delivery = doc.getString("delivery");
                            String description = doc.getString("description");
                            String email = doc.getString("email");
                            String id = doc.getString("id");
                            String imageURL = doc.getString("imageURL");
                            String phone = doc.getString("phone");
                            String username = doc.getString("username");
                            String status = doc.getString("status");
                            String profession = doc.getString("profession");
                            String rate = doc.getString("rate");


                            Store s = new Store(address, delivery, description, email,
                                    id, imageURL, phone, username, status, profession, "");

                            allStores.add(s);
                        }
                    }

                    getStores(allStores);
                }
            });
        }

    }


    private void getStores(ArrayList<Store> allStores){

        STORES = allStores;
    }

    public ArrayList<Store> stores() {

        return STORES;
    }

    // basketFragment

    public ArrayList<Plate> mNames = new ArrayList<>();
    public ArrayList<Info> infoList = new ArrayList<>();

    public EditText price;
    public String Price;

    public void checkBasket() {

        DatabaseReference reference;
        final FirebaseUser firebaseUser;
        FirebaseAuth auth;

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Orders");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(firebaseUser.getUid())){

                    downloadMenu();

                }else {

                    if (mNames.size() == 0) {

                        Price = " 0 DA";
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void downloadMenu() {


        final DatabaseReference reference;
        final FirebaseUser firebaseUser;
        FirebaseAuth auth;

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference("Orders");

        reference.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

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

                    if (Number.equals("0")){

                        reference.child(firebaseUser.getUid()).child(Id).removeValue();
                    }

                    com.oufar.ems.Model.Plate plate = new Plate(Id, Plate, Price, Description, ImageURL, StoreId,"", Number, Status, StoreEmail);

                    com.oufar.ems.Model.Plate p = new Plate("", "", "", "", "", "", "", "", "", "");

                    Info info = new Info(Id, StoreId, Store, Price, Number);

                    Price = " 0 DA";

                    mNames.add(plate);

                    if (Status.equals("accepted")) {
                        infoList.add(info);
                    }
                }

                L();
                totalPrice(infoList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void totalPrice(ArrayList<Info> infoList){

        int total = 0;

        for(int i = 0; i < infoList.size();i++){

            total += Integer.parseInt(infoList.get(i).getPrice()) * Integer.parseInt(infoList.get(i).getNumber());

            totalPrice = "Total "+total+" DA";

        }
    }

    public ArrayList<Plate> L (){

        return mNames;
    }

    //profileFragment

    public  void  profile(){

        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (!firebaseUser.getUid().isEmpty()) {

            firestore.collection("Client").document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    assert documentSnapshot != null;

                    if (documentSnapshot != null) {

                        txt_username = documentSnapshot.get("username").toString();
                        txt_phone = documentSnapshot.get("phone").toString();
                        txt_wilaya = documentSnapshot.get("wilaya").toString();
                        txt_city = documentSnapshot.get("city").toString();
                        txt_lat = documentSnapshot.get("lat").toString();
                        txt_lng = documentSnapshot.get("lng").toString();
                        home_lat = documentSnapshot.get("homeLat").toString();
                        home_lng = documentSnapshot.get("homeLng").toString();
                        txt_description = documentSnapshot.get("description").toString();
                        profile_imageURL = documentSnapshot.get("imageURL").toString();

                    }
                    //Toast();

                }
            });
        }
    }

    public String Username() {
        return txt_username;
    }
    public String Phone() {
        return txt_phone;
    }
    public String Wilaya() {
        return txt_wilaya;
    }
    public String City() {
        return txt_city;
    }
    public String Lat() {
        return txt_lat;
    }
    public String Lng() {
        return txt_lng;
    }
    public String homeLat() {
        return home_lat;
    }
    public String homeLng() {
        return home_lng;
    }
    public String Description() {
        return txt_description;
    }
    public String ImageURL() {
        return profile_imageURL;
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {



        private ArrayList<Fragment> fragments;

        ViewPagerAdapter (FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();

        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        void addFragment(Fragment fragment){
            fragments.add(fragment);

        }

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_BACK && !B.equals("basket"))
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        else if (B.equals("basket")){

            Intent intent = new Intent(Home.this, Menu.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("Store_id", Store_id);// sendingBack Store id
            intent.putExtra("Store_name", Store_name);// sendingBack Store name
            intent.putExtra("Store_image", Store_image);// sendingBack Store image Store_email
            intent.putExtra("Store_status", Store_status);// sendingBack Store status
            intent.putExtra("Store_email", Store_email);// sendingBack Store email
            B = "no";
            startActivity(intent);
        }
        return false;
    }

    private void setNavigationBarButtonsColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = activity.getWindow().getDecorView();
            int flags = decorView.getSystemUiVisibility();
            if (isColorLight()) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }
            decorView.setSystemUiVisibility(flags);
        }
    }

    private boolean isColorLight() {
        double darkness = 1 - (0.299 * Color.red(R.color.orange) + 0.587 * Color.green(R.color.orange) + 0.114 * Color.blue(R.color.orange)) / 255;
        return darkness < 0.5;
    }

}
