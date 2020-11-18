package com.oufar.ems;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

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
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.onesignal.OneSignal;
import com.oufar.ems.Database.DB;
import com.oufar.ems.Database.Favorite;
import com.oufar.ems.Fragments.Fragment_1;
import com.oufar.ems.Fragments.Fragment_2;
import com.oufar.ems.Fragments.Fragment_3;
import com.oufar.ems.Fragments.Fragment_Favorite;

import java.util.ArrayList;

public class Home extends AppCompatActivity {

    ProgressBar progressBar;

    private ChipNavigationBar navigation_bar;
    private ViewPager viewPager;
    private int index = 0;
    private String check = "";
    private DB db;

    private FirebaseFirestore firestore;
    FirebaseUser firebaseUser;
    private String Store_id, Store_name, Store_image, Store_status, Store_email;
    private String B;

    String txt_username = "";
    String txt_phone = "";
    String txt_wilaya = "";
    String txt_city = "";
    String txt_lat = "";
    String txt_lng = "";
    String home_lat = "";
    String home_lng = "";
    String txt_description = "";
    String imageURL = "";

     private Intent intent;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

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

        setNavigationBarButtonsColor(this, R.color.orange);


        Bundle bundle = getIntent().getExtras();
        final String Basket = bundle.getString("basket");
        Store_id = bundle.getString("Store_id");
        Store_name = bundle.getString("Store_name");
        Store_image = bundle.getString("Store_image");
        Store_status = bundle.getString("Store_status");
        Store_email = bundle.getString("Store_email");

        B = Basket;

        db = new DB(this);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        firestore = FirebaseFirestore.getInstance();

        //downloadInfo();
        downloadInfo_();

        navigation_bar = findViewById(R.id.navigation_bar);
        viewPager = findViewById(R.id.view_pager);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        /*if (Basket.equals("basket")){

            navigation_bar.setItemSelected(R.id.basket, true);
        }else if (Basket.equals("no")) {

            navigation_bar.setItemSelected(R.id.home, true);
        }*/

        //Toast.makeText(Home.this, Basket, Toast.LENGTH_SHORT).show();

        navigation_bar.setItemSelected(R.id.home, true);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPager.setAdapter(viewPagerAdapter);

        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        final Fragment_1 fragment_1 = new Fragment_1();
        final Fragment_2 fragment_2 = new Fragment_2();
        final Fragment_Favorite fragment_favorite = new Fragment_Favorite();
        final Fragment_3 fragment_3 = new Fragment_3();
        adapter.addFragment(fragment_1);
        adapter.addFragment(fragment_2);
        adapter.addFragment(fragment_favorite);
        adapter.addFragment(fragment_3);
        viewPager.setAdapter(adapter);

        index = 1;

        navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {

                switch(id){
                    case R.id.home:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.list:
                        viewPager.setCurrentItem(1);

                        if (!Basket.equals("basket")){

                            fragment_2.checkMenu();
                        }
                        //fragment_2.checkMenu();
                        break;
                    case R.id.favorite:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.profile:
                        viewPager.setCurrentItem(3);
                        break;
                }

            }
        });


        if (Basket.equals("basket")){

            viewPager.setCurrentItem(1);
            navigation_bar.setItemSelected(R.id.list, true);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 0){

                    navigation_bar.setItemSelected(R.id.home, true);
                    navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.list:
                                    viewPager.setCurrentItem(1);
                                    fragment_2.checkMenu();
                                    break;
                                case R.id.favorite:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 0;

                }else if (position == 1){

                    navigation_bar.setItemSelected(R.id.list, true);

                    if(index == 0 || index == 2){

                        //fragment_2.downloadPlates_2();
                    }
                    index = 1;
                    //Toast.makeText(Home.this, ""+index, Toast.LENGTH_SHORT).show();
                }else if (position == 2){

                    navigation_bar.setItemSelected(R.id.favorite, true);
                    navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.list:
                                    viewPager.setCurrentItem(1);
                                    fragment_2.checkMenu();
                                    break;
                                case R.id.favorite:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 2;

                }else if (position == 3){

                    navigation_bar.setItemSelected(R.id.profile, true);
                    navigation_bar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int id) {

                            switch(id){
                                case R.id.home:
                                    viewPager.setCurrentItem(0);
                                    break;
                                case R.id.list:
                                    viewPager.setCurrentItem(1);
                                    fragment_2.checkMenu();
                                    break;
                                case R.id.favorite:
                                    viewPager.setCurrentItem(2);
                                    break;
                                case R.id.profile:
                                    viewPager.setCurrentItem(3);
                                    break;
                            }
                        }
                    });

                    index = 3;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public  void  downloadInfo_(){

        if (!firebaseUser.getUid().isEmpty()) {

            firestore.collection("Client").document(firebaseUser.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

                    assert documentSnapshot != null;

                    if (documentSnapshot != null) {

                        progressBar.setVisibility(View.GONE);

                        txt_username = documentSnapshot.get("username").toString();
                        txt_phone = documentSnapshot.get("phone").toString();
                        txt_wilaya = documentSnapshot.get("wilaya").toString();
                        txt_city = documentSnapshot.get("city").toString();
                        txt_lat = documentSnapshot.get("lat").toString();
                        txt_lng = documentSnapshot.get("lng").toString();
                        home_lat = documentSnapshot.get("homeLat").toString();
                        home_lng = documentSnapshot.get("homeLng").toString();
                        txt_description = documentSnapshot.get("description").toString();
                        imageURL = documentSnapshot.get("imageURL").toString();

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
        return imageURL;
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {



        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter (FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();

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

        public void addFragment(Fragment fragment){
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
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
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
