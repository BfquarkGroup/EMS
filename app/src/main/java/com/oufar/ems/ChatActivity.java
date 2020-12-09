package com.oufar.ems;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.oufar.ems.Model.Chat;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private CircleImageView avatar, avatar2;
    private TextView storeName, status, ratingTitle;
    private String Store_id, Client_image;
    private ImageView rate, send;
    private RelativeLayout ratingLayout;
    private RatingBar ratingBar;
    private Button ratingButton;
    private EditText message;
    static String Client_email;
    private String click = "1";

    private ArrayList<Chat> chatArrayList = new ArrayList();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

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

        avatar = findViewById(R.id.avatar);
        avatar2 = findViewById(R.id.avatar2);
        storeName = findViewById(R.id.storeName);
        status = findViewById(R.id.status);
        rate = findViewById(R.id.rate);
        ratingLayout = findViewById(R.id.ratingLayout);
        ratingTitle = findViewById(R.id.ratingTitle);
        ratingBar = findViewById(R.id.ratingBar);
        ratingButton = findViewById(R.id.ratingButton);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);



        Bundle bundle = getIntent().getExtras();
        Store_id = bundle.getString("Store_id");
        Client_image = bundle.getString("Client_image");
        Glide.with(ChatActivity.this)
                .asBitmap()
                .load(Client_image)
                .into(avatar2);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (click.equals("1")){

                    ratingLayout.setVisibility(View.VISIBLE);
                    click = "2";
                }else if (click.equals("2")){

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

                String totalStars = "Total Stars:: " + ratingBar.getNumStars();
                String rating = "Rating :: " + ratingBar.getRating();
                ratingLayout.setVisibility(View.GONE);
                click = "1";
                Toast.makeText(getApplicationContext(), totalStars + "\n" + rating, Toast.LENGTH_LONG).show();
            }
        });

        //getEmail of client
        final FirebaseUser firebaseUser=auth.getCurrentUser();
        assert firebaseUser !=null;

        if(firebaseUser!=null){

            Client_email=firebaseUser.getEmail();

        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (firebaseUser != null) {

            firestore.collection("Store").addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@com.google.firebase.database.annotations.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                    assert queryDocumentSnapshots != null;

                    if (firebaseUser != null && queryDocumentSnapshots != null) {

                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {

                            assert documentSnapshot != null;

                            String address = documentSnapshot.getString("city");
                            String delivery = documentSnapshot.getString("delivery");
                            String description = documentSnapshot.getString("description");
                            String email = documentSnapshot.getString("email");
                            String id = documentSnapshot.getString("id");
                            String imageURL = documentSnapshot.getString("imageURL");
                            String phone = documentSnapshot.getString("phone");
                            String username = documentSnapshot.getString("username");
                            String status = documentSnapshot.getString("status");
                            String profession = documentSnapshot.getString("profession");
                            String rate = documentSnapshot.getString("rate");

                            if (id.equals(Store_id)) {

                                load(imageURL, username, status);
                            }
                        }

                    }
                }
            });
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=message.getText().toString();

                //chattNoty(msg, Store_id,Store_email,Client_email);

            }
        });
    }

    private void load(String ImageURL, String Username, String Status) {

        storeName.setText(Username);
        status.setText(Status);
        Glide.with(ChatActivity.this)
                .asBitmap()
                .load(ImageURL)
                .into(avatar);
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

    private  void  chattNoty(String text ,String store_id ,String store_email,String clientEmail  ){

            reference = FirebaseDatabase.getInstance().getReference("chattNoty").child(firebaseUser.getUid()).push();

            HashMap<String, String> hashMap;
            hashMap = new HashMap<>();


            hashMap.put("clientId", firebaseUser.getUid());
            hashMap.put("storeId",store_id );
            hashMap.put("storeEmail",store_email );
            hashMap.put("clientEmail", clientEmail);
            hashMap.put("message",text );
            String UserName = getIntent().getStringExtra("UserName");
            String TEXT=UserName+ " :"+ text ;
                sendNotification(TEXT,store_email);

            reference.setValue(hashMap);
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
