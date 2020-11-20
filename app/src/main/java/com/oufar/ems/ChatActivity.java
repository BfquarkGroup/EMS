package com.oufar.ems;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;

public class ChatActivity extends AppCompatActivity {
    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    private TextView storeName, status;
    private String Store_id, Store_name, Store_image, Store_status, Store_email;
    private ImageView statusIcon, send;
    private EditText message;
    static String Client_email;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.orange));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getColor(R.color.orange));
        }

        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.orange));
        }
        setNavigationBarButtonsColor(this, R.color.orange);

        storeName = findViewById(R.id.storeName);
        status = findViewById(R.id.status);
        statusIcon = findViewById(R.id.statusIcon);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);



        Bundle bundle = getIntent().getExtras();
        Store_id = bundle.getString("Store_id");
        Store_name = bundle.getString("Store_name");
        Store_image = bundle.getString("Store_image");
        Store_status = bundle.getString("Store_status");
        Store_email = bundle.getString("Store_email");
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



        storeName.setText(Store_name);
        status.setText(Store_status);

        if (Store_status.equals("open")){

            statusIcon.setImageResource(R.drawable.online);
        }else if (Store_status.equals("close")){

            statusIcon.setImageResource(R.drawable.offline);
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg=message.getText().toString();

                chattNoty(msg, Store_id,Store_email,Client_email);

                Toast.makeText(ChatActivity.this, Store_email, Toast.LENGTH_SHORT).show();
            }
        });





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
