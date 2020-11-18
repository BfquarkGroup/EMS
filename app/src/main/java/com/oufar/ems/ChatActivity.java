package com.oufar.ems;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ChatActivity extends AppCompatActivity {


    private TextView storeName, status;
    private String Store_id, Store_name, Store_image, Store_status, Store_email;
    private ImageView statusIcon, send;
    private EditText message;

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


        Bundle bundle = getIntent().getExtras();
        Store_id = bundle.getString("Store_id");
        Store_name = bundle.getString("Store_name");
        Store_image = bundle.getString("Store_image");
        Store_status = bundle.getString("Store_status");
        Store_email = bundle.getString("Store_email");

        storeName = findViewById(R.id.storeName);
        status = findViewById(R.id.status);
        statusIcon = findViewById(R.id.statusIcon);
        send = findViewById(R.id.send);
        message = findViewById(R.id.message);

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

                Toast.makeText(ChatActivity.this, message.getText().toString(), Toast.LENGTH_SHORT).show();
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
}
