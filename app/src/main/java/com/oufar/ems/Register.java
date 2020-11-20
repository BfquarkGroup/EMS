package com.oufar.ems;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText username, wilaya, city, phone, email, password, confirm_password;
    Button btn_register;
    ProgressBar progressBar;

    FirebaseAuth auth;
    private FirebaseFirestore firestore;

    String txt_username;
    String txt_wilaya;
    String txt_city;
    String txt_phone;
    String txt_email;
    String txt_password;
    String txt_confirm_password;

    @SuppressLint({"NewApi", "PrivateResource"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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

        auth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        username = findViewById(R.id.username);
        wilaya = findViewById(R.id.wilaya);
        city = findViewById(R.id.city);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);

        progressBar = findViewById(R.id.progressBar);

        btn_register = findViewById(R.id.btn_register);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                txt_username = username.getText().toString();
                txt_wilaya = wilaya.getText().toString();
                txt_city = city.getText().toString();
                txt_phone = phone.getText().toString();
                txt_email = email.getText().toString();
                txt_password = password.getText().toString();
                txt_confirm_password = confirm_password.getText().toString();

                if (txt_username.isEmpty()){
                    String usr=getString(R.string.toast_usr);
                    Toast.makeText(Register.this, ""+usr, Toast.LENGTH_SHORT).show();
                    username.requestFocus();
                    return;
                }
                if (txt_wilaya.isEmpty()){
                    String w=getString(R.string.toast_addW);
                    Toast.makeText(Register.this, ""+w, Toast.LENGTH_SHORT).show();
                    wilaya.requestFocus();
                    return;
                }
                if (txt_city.isEmpty()){
                    String c=getString(R.string.toast_addC);
                    Toast.makeText(Register.this, ""+c, Toast.LENGTH_SHORT).show();
                    city.requestFocus();
                    return;
                }
                if (txt_phone.isEmpty()){
                    String ph=getString(R.string.toast_ph);
                    Toast.makeText(Register.this, ""+ph, Toast.LENGTH_SHORT).show();
                    phone.requestFocus();
                    return;
                }
                if (txt_email.isEmpty()){
                    String e=getString(R.string.toast_email);
                    Toast.makeText(Register.this, ""+e, Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(txt_email).matches()){
                    String ev=getString(R.string.toast_vemail);
                    Toast.makeText(Register.this, ""+ev, Toast.LENGTH_SHORT).show();
                    email.requestFocus();
                    return;
                }
                if (txt_password.isEmpty()){
                    String ps=getString(R.string.toast_ps);
                    Toast.makeText(Register.this, ""+ps, Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }
                if (txt_password.length()<6){
                    String pss=getString(R.string.toast_Min);
                    Toast.makeText(getApplicationContext(), ""+pss, Toast.LENGTH_SHORT).show();
                    password.requestFocus();
                    return;
                }
                if (txt_confirm_password.isEmpty()){
                    String cpw=getString(R.string.toast_cpsd);
                    Toast.makeText(Register.this, ""+cpw, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!txt_confirm_password.equals(txt_password)){
                    String  chk=getString(R.string.toast_chk);
                    Toast.makeText(Register.this, ""+chk, Toast.LENGTH_SHORT).show();
                    confirm_password.requestFocus();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                Register_2();
            }
        });

    }

    private void Register_2(){

        auth.createUserWithEmailAndPassword(txt_email,txt_password).
                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()){

                            FirebaseUser firebaseUser = auth.getCurrentUser();

                            assert firebaseUser != null;
                            String userid = firebaseUser.getUid();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.getText().toString());
                            hashMap.put("phone", phone.getText().toString());
                            hashMap.put("email", email.getText().toString());
                            hashMap.put("wilaya", wilaya.getText().toString());
                            hashMap.put("city", city.getText().toString());
                            hashMap.put("password", password.getText().toString());
                            hashMap.put("imageURL", "default");
                            hashMap.put("description", "nothing added yet");
                            hashMap.put("lat", "0.0");
                            hashMap.put("lng", "0.0");
                            hashMap.put("homeLat", "0.0");
                            hashMap.put("homeLng", "0.0");

                            Intent intent = new Intent(getBaseContext(), ChatActivity.class);
                            intent.putExtra("UserName", username.getText().toString());
                            startActivity(intent);
                            firestore.collection("Client").document(userid).set(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()){

                                        Intent intent = new Intent(Register.this, Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("basket", "no");
                                        startActivity(intent);
                                        finish();

                                    }else {
                                                String er=getString(R.string.Error);
                                        Toast.makeText(Register.this, ""+er, Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }else {
                            String cant=getString(R.string.toast_or);
                            Toast.makeText(Register.this, ""+cant, Toast.LENGTH_SHORT).show();
                        }
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
