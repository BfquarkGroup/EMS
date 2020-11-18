package com.oufar.ems.Fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oufar.ems.Adapter.RecyclerViewAdapter_3;
import com.oufar.ems.ConfirmedOrders;
import com.oufar.ems.Database.DB;
import com.oufar.ems.Home;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.R;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

import pl.droidsonroids.gif.GifImageView;

public class Fragment_2 extends Fragment {

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private DB db;
    private Home home;
    static String Client_email;
    private String check = "nothing";
    private TextView counter, lat, lng;
    private String txt_lat = "", txt_lng = "";
    private RecyclerView recyclerView_non_confirmed;
    private GifImageView nothing;
    private ImageView close;
    private RelativeLayout popUp;
    private CardView box;
    private FloatingActionButton btn_confirm;
    private Button confirm;
    private RelativeLayout confirmed_orders;
    private ProgressBar progressBar;
    private ArrayList<Plate> mNames = new ArrayList<>();
    private ArrayList<Plate> mNames_ = new ArrayList<>();
    private ArrayList<Plate> notificationList = new ArrayList<>();
    private RadioGroup radioGroup;
    private RadioButton homeAddress, currentAddress;
    private String documentId;

    private FusedLocationProviderClient client, fusedLocationProviderClient;

    private LatLng latLng;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Intent intent;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_fragment_2, container, false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        //getEmail of client
        FirebaseUser firebaseUser=auth.getCurrentUser();
        assert firebaseUser !=null;

        if(firebaseUser!=null){

            Client_email=firebaseUser.getEmail();

            //Toast.makeText(getContext(), ""+Client_email, Toast.LENGTH_SHORT).show();
        }

        db = new DB(getContext());
        home = (Home) getActivity();

        intent = new Intent(getContext(), ConfirmedOrders.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);

        counter = view.findViewById(R.id.counter);
        lat = view.findViewById(R.id.lat);
        lng = view.findViewById(R.id.lng);
        progressBar = view.findViewById(R.id.progressBar);
        recyclerView_non_confirmed = view.findViewById(R.id.recyclerView_non_confirmed);
        nothing = view.findViewById(R.id.nothing);
        popUp = view.findViewById(R.id.popUp);
        box = view.findViewById(R.id.box);
        close = view.findViewById(R.id.close);
        radioGroup = view.findViewById(R.id.radioGroup);
        homeAddress = view.findViewById(R.id.homeAddress);
        currentAddress = view.findViewById(R.id.currentAddress);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        confirm = view.findViewById(R.id.confirm);
        confirmed_orders = view.findViewById(R.id.confirmed_orders);

        locationManager = ( LocationManager ) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                lat.setText(location.getLatitude() + "");
                lng.setText(location.getLongitude() + "");

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

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                if (check.equals("something")) {

                    getLocation();

                    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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


                    popUp.setVisibility(View.VISIBLE);
                    btn_confirm.setVisibility(View.GONE);

                    count();
                }
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                popUp.setVisibility(View.GONE);
                btn_confirm.setVisibility(View.VISIBLE);
            }
        });

        box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        popUp.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {

                popUp.setVisibility(View.GONE);
                btn_confirm.setVisibility(View.VISIBLE);
            }
        });

        /*confirm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {


            }
        });*/

        confirmed_orders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(intent);
            }
        });

        checkMenu();

        return view;
    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);

            return;
        }else {

            configure();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case 10:

                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    configure();

                return;
        }
    }

    private void configure() {

        confirm.setOnClickListener(new View.OnClickListener() {
            @SuppressLint({"MissingPermission", "RestrictedApi", "ResourceAsColor"})
            @Override
            public void onClick(View v) {

                //locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);

                // minTime 5000

                if (!lat.getText().toString().equals("0.0") && !lng.getText().toString().equals("0.0")) {

                    if (homeAddress.isChecked()){

                        txt_lat = home.homeLat();
                        txt_lng = home.homeLng();

                        confirmation(txt_lat, txt_lng);
                        popUp.setVisibility(View.GONE);
                        btn_confirm.setVisibility(View.VISIBLE);

                        //Toast.makeText(getContext(), "Home", Toast.LENGTH_SHORT).show();
                    }else if (currentAddress.isChecked()){

                        txt_lat = lat.getText().toString();
                        txt_lng = lng.getText().toString();

                        confirmation(txt_lat, txt_lng);
                        popUp.setVisibility(View.GONE);
                        btn_confirm.setVisibility(View.VISIBLE);

                        //Toast.makeText(getContext(), "Current", Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(getContext(),lat.getText().toString()+"\n"+lng.getText().toString() , Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // local list ----------------------------------------------------------------------------------

    public void checkMenu(){

        mNames.clear();

        progressBar.setVisibility(View.VISIBLE);

        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){

            progressBar.setVisibility(View.GONE);
            nothing.setVisibility(View.VISIBLE);
            check = "nothing";

        }else {

            mNames.clear();

            while (cursor.moveToNext()){

                progressBar.setVisibility(View.GONE);
                nothing.setVisibility(View.GONE);
                check = "something";


                Plate plate = new Plate(cursor.getString(0), cursor.getString(3),
                                        cursor.getString(7), cursor.getString(5),
                                        cursor.getString(4), cursor.getString(2),
                                        cursor.getString(0), cursor.getString(6),
                                        "", cursor.getString(8));

                Plate tst = new Plate("id", cursor.getString(3), cursor.getString(6), cursor.getString(5), cursor.getString(4), "d", "", "", "", "");

                Plate test = new Plate("id", "cursor.getString(3)", "cursor.getString(6)", "cursor.getString(5)", "cursor.getString(4)", "d", "", "", "", "");

                double num = Double.parseDouble(cursor.getString(6));
                if (num > 0) {

                    mNames.add(plate);
                }
            }

            initRecyclerView();
        }
    }

    private void initRecyclerView(){

        mNames_.clear();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView_non_confirmed.setLayoutManager(layoutManager);
        RecyclerViewAdapter_3 adapter = new RecyclerViewAdapter_3(getContext(), mNames);
        recyclerView_non_confirmed.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Save state
        Parcelable recyclerViewState;
        recyclerViewState = recyclerView_non_confirmed.getLayoutManager().onSaveInstanceState();
        // Restore state
        recyclerView_non_confirmed.getLayoutManager().onRestoreInstanceState(recyclerViewState);
    }

    private void count(){

        double total = 0;

        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){

            counter.setText(total+"");

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Toast.makeText(getContext(), "There is no element in your basket", Toast.LENGTH_SHORT).show();

                    popUp.setVisibility(View.GONE);
                    nothing.setVisibility(View.VISIBLE);
                }
            });

        }else {

            while (cursor.moveToNext()){

                double d = Double.parseDouble(cursor.getString(7));

                double num = Double.parseDouble(cursor.getString(6));

                //int d = Integer.parseInt(cursor.getString(6));

                total = total + (d*num);
            }

            counter.setText(total+"");
        }

        mNames_.clear();
    }


    // end of local list ---------------------------------------------------------------------------

    // ---------------------------------------------------------------------------------------------

    // uploading database to FireBase --------------------------------------------------------------

    @SuppressLint("RestrictedApi")
    private void confirmation(String txt_lat, String txt_lng) {

        mNames_.clear();

        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){

            progressBar.setVisibility(View.GONE);
            nothing.setVisibility(View.VISIBLE);
            check = "nothing";

        }else {

            mNames_.clear();

            notificationList.clear();

            while (cursor.moveToNext()){

                int num = Integer.parseInt(cursor.getString(6));

                if (num == 0 ){

                    db.deleteData(cursor.getString(0), null,null,null,null,null,null,null, null);
                    //Toast.makeText(getContext(), "confirm again", Toast.LENGTH_SHORT).show();
                    //return;
                }

                progressBar.setVisibility(View.VISIBLE);
                nothing.setVisibility(View.GONE);
                check = "something";

                Plate plate = new Plate(cursor.getString(0), cursor.getString(3),
                                        cursor.getString(7), cursor.getString(5),
                                        cursor.getString(4), cursor.getString(2),
                                        cursor.getString(0), cursor.getString(6),
                                        "", cursor.getString(8));

                Plate plawte = new Plate("", "", "", "", "", "", "", "", "", "");


                mNames_.add(plate);

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

                int price = Integer.parseInt(cursor.getString(6)) * Integer.parseInt(cursor.getString(7));

                String msg=" Order from : "+home.Username()+" \n"+cursor.getString(6)+" "+cursor.getString(3)+" ("+price+") DA";

                sendNotification(msg,cursor.getString(8));
                            reference.setValue(hashMap);


                Plate notify = new Plate(reference.getKey(), cursor.getString(3),
                                        cursor.getString(7), cursor.getString(5),
                                        cursor.getString(4), cursor.getString(2),
                                        "", cursor.getString(6),
                                        "", cursor.getString(8));

                notificationList.add(notify);

            }

            progressBar.setVisibility(View.GONE);
        }


        //todo: after adding a lat_lng field for each client info we will edit the value by using client's gps data then click on confirm button to upload the new info

        HashMap<String, String> hashMap_ = new HashMap<>();
        hashMap_.put("lat", "0.0");
        hashMap_.put("lng", "0.0");

        firestore.collection("Client").document(firebaseUser.getUid())
                .update("lat", txt_lat,"lng", txt_lng).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    clear_();

                    recyclerView_non_confirmed.setVisibility(View.GONE);

                    // xxxxxx

                    for(int i = 0; i < notificationList.size();i++){

                        if (i != 0) {

                            if (notificationList.get(i).equals(notificationList.get(i - 1))) {
                                notificationList.remove(i--);
                            }
                        }
                    }

                    organizeNotification(notificationList);

                    //xxxxxxx

                }else {

                    Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //clear_();

    }

    private void confirmation_2(String txt_lat, String txt_lng){

        mNames_.clear();

        final Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){

            progressBar.setVisibility(View.GONE);
            nothing.setVisibility(View.VISIBLE);
            check = "nothing";

        }else {

            mNames_.clear();

            notificationList.clear();

            while (cursor.moveToNext()){

                int num = Integer.parseInt(cursor.getString(6));

                if (num == 0 ){

                    db.deleteData(cursor.getString(0), null,null,null,null,null,null,null, null);
                }

                progressBar.setVisibility(View.VISIBLE);
                nothing.setVisibility(View.GONE);
                check = "something";

                Plate plate = new Plate(cursor.getString(0), cursor.getString(3),
                        cursor.getString(7), cursor.getString(5),
                        cursor.getString(4), cursor.getString(2),
                        cursor.getString(0), cursor.getString(6),
                        "", cursor.getString(8));

                mNames_.add(plate);

                final FirebaseFirestore firestore_;
                firestore_ = FirebaseFirestore.getInstance();

                HashMap<String, String> hashMap;
                hashMap = new HashMap<>();

                //hashMap.put("id", reference.getKey());
                hashMap.put("id", "id");
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



                int price = Integer.parseInt(cursor.getString(6)) * Integer.parseInt(cursor.getString(7));

                String msg=" Order from : "+home.Username()+" \n"+cursor.getString(6)+" "+cursor.getString(3)+" ("+price+") DA";

                //sendNotification(msg,cursor.getString(8));

                firestore_.collection("Store").document(cursor.getString(2)).
                        collection("Orders").add(hashMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {

                        if (task.isSuccessful()){

                            Toast.makeText(getContext(), "Order sent", Toast.LENGTH_SHORT).show();

                        }else {

                            Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                // getting Id
                final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
                fStore.collection("Store").document(cursor.getString(2)).
                        collection("Orders").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {


                        //mNames.clear();

                        assert queryDocumentSnapshots != null;

                        if (firebaseUser != null && queryDocumentSnapshots != null) {

                            for (DocumentSnapshot doc : queryDocumentSnapshots) {

                                assert doc != null;

                                String ClientId = doc.getString("clientId");
                                String StoreId = doc.getString("storeId");
                                String Number = doc.getString("number");
                                String Plate = doc.getString("plate");
                                String Price = doc.getString("price");
                                String ImageURL = doc.getString("imageURL");

                                if (Plate.equals(cursor.getString(3)) && Price.equals(cursor.getString(7)) &&
                                        ClientId.equals(firebaseUser.getUid()) && ImageURL.equals(cursor.getString(4)) &&
                                        StoreId.equals(cursor.getString(2)) && Number.equals(cursor.getString(6))){

                                    documentId = doc.getId();

                                    final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                                    firebaseFirestore.collection("Store").document(firebaseUser.getUid()).
                                            collection("Orders").document(documentId).update("id",documentId).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){


                                            }else {

                                                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }

                                //Plate plate_ = new Plate("", plate, price, description, imageURL, "", "", "");
                                //mNames.add(plate_);

                            }
                        }
                    }
                });

                Plate notify = new Plate(documentId, cursor.getString(3),
                        cursor.getString(7), cursor.getString(5),
                        cursor.getString(4), cursor.getString(2),
                        "", cursor.getString(6),
                        "", cursor.getString(8));

                notificationList.add(notify);

            }

            progressBar.setVisibility(View.GONE);
        }


        //todo: after adding a lat_lng field for each client info we will edit the value by using client's gps data then click on confirm button to upload the new info

        HashMap<String, String> hashMap_ = new HashMap<>();
        hashMap_.put("lat", "0.0");
        hashMap_.put("lng", "0.0");

        firestore.collection("Client").document(firebaseUser.getUid())
                .update("lat", txt_lat,"lng", txt_lng).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    clear_();

                    recyclerView_non_confirmed.setVisibility(View.GONE);

                    // xxxxxx

                    for(int i = 0; i < notificationList.size();i++){

                        if (i != 0) {

                            if (notificationList.get(i).equals(notificationList.get(i - 1))) {
                                notificationList.remove(i--);
                            }
                        }
                    }

                    organizeNotification(notificationList);

                    //xxxxxxx

                }else {

                    Toast.makeText(getContext(), "ERROR", Toast.LENGTH_SHORT).show();
                }

            }
        });

        //clear_();

    }

    private void organizeNotification(ArrayList<Plate> notificationList) {


        for(int i = 0; i < notificationList.size();i++){

            Toast.makeText(getContext(), Client_email+"\n"+notificationList.get(i).getStoreEmail()+"\n new Order", Toast.LENGTH_SHORT).show();


            // sendNotification(notificationList.get(i).getStoreEmail(), " new Order");
        }

    }

    private void clear_() {

        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){

            check = "nothing";

            nothing.setVisibility(View.VISIBLE);

        }else {

            while (cursor.moveToNext()){

                progressBar.setVisibility(View.GONE);
                nothing.setVisibility(View.GONE);
                check = "something";

                db.deleteData(cursor.getString(0), firebaseUser.getUid(),
                            cursor.getString(2), cursor.getString(3),
                            cursor.getString(4), cursor.getString(5),
                            "", cursor.getString(7),
                            cursor.getString(8));

            }
            check = "something";

            nothing.setVisibility(View.VISIBLE);
        }

        checkMenu();
    }

    // end of uploading database to FireBase -------------------------------------------------------

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





