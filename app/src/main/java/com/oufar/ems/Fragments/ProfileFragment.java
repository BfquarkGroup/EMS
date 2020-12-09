package com.oufar.ems.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.oufar.ems.Database.DB;
import com.oufar.ems.Database.Favorite;
import com.oufar.ems.Home;
import com.oufar.ems.MainActivity;
import com.oufar.ems.R;

import java.util.HashMap;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private static final int IMAGE_REQUEST = 1;

    private ImageView language_picture;
    private ProgressBar progressBar;
    private TextView t_username, t_phone, t_city, language_txt, wilaya, btn_logout;
    private EditText username, city, phone, description, homeAddressInput;
    private CircleImageView profile_picture;
    private Button btn_confirm;
    private RelativeLayout homeAddress;

    private LocationManager locationManager;
    private LocationListener locationListener;

    private Uri imageUri;
    private String imageURL;

    private Home home;

    private HashMap<String, Object> map = new HashMap<>();
    private FirebaseFirestore firestore;
    private FirebaseUser firebaseUser;

    private StorageReference storageReference;
    private StorageTask uploadTask;

    private String txt_home_lat = "", txt_home_lng = "";
    private Intent intent;
    private DB db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore = FirebaseFirestore.getInstance();
        //reference = FirebaseDatabase.getInstance().getReference().child(firebaseUser.getUid());
        //reference = FirebaseDatabase.getInstance().getReference("Client_Data").child(firebaseUser.getUid());
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        home = (Home) getActivity();
        db = new DB(getContext());

        profile_picture = view.findViewById(R.id.profile_picture);
        t_username = view.findViewById(R.id.t_username);
        t_phone = view.findViewById(R.id.t_phone);
        t_city = view.findViewById(R.id.t_city);
        progressBar = view.findViewById(R.id.progressBar);
        username = view.findViewById(R.id.username);
        wilaya = view.findViewById(R.id.wilaya);
        city = view.findViewById(R.id.city);
        homeAddressInput = view.findViewById(R.id.homeAddressInput);
        phone = view.findViewById(R.id.phone);
        description = view.findViewById(R.id.description);
        language_picture = view.findViewById(R.id.language_picture);
        language_txt = view.findViewById(R.id.language_txt);
        btn_confirm = view.findViewById(R.id.btn_confirm);
        btn_logout = view.findViewById(R.id.btn_logout);
        homeAddress = view.findViewById(R.id.homeAddress);

        homeAddressInput.setEnabled(false);

        Spinner mySpinner = view.findViewById(R.id.spinner1);

        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 1) {
                    Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                } else if (i == 2) {
                    Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                }else if (i == 3) {
                    Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                }else if (i == 4) {
                    Toast.makeText(getContext(), "4", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //progressBar.setVisibility(View.VISIBLE);

        downloadInfo();

        profile_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        language_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ShowChangeLaguageDialog();
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((username.getText().toString().isEmpty() || username.getText().toString().equals(home.Username()))   &&
                        (wilaya.getText().toString().isEmpty() || wilaya.getText().toString().equals(home.Wilaya()))  &&
                        (city.getText().toString().isEmpty() || city.getText().toString().equals(home.City()))  &&
                        (txt_home_lat == null || txt_home_lng == null || txt_home_lat.equals(home.homeLat()) || txt_home_lng.equals(home.homeLng()) || txt_home_lat.equals("null") || txt_home_lng.equals("null")) &&
                        (phone.getText().toString().isEmpty() || phone.getText().toString().equals(home.Phone()))  &&
                        (description.getText().toString().isEmpty() || description.getText().toString().equals(home.Description())))
                {

                    return;
                }

                uploadInfo();
            }
        });


        locationManager = ( LocationManager ) getActivity().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                txt_home_lat = location.getLatitude()+"";
                txt_home_lng = location.getLongitude()+"";
                homeAddressInput.setText(txt_home_lat+"\n"+txt_home_lng);

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

        homeAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getLocation();

                AlertDialog Alert = new AlertDialog.Builder(getContext())
                        .setMessage("set your current location as your home address by updating coordinates")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

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

                            }
                        })
                        .setNegativeButton("no", null)
                        .show();
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseAuth.getInstance().signOut();
                home.finish();
                intent = new Intent(getContext(), MainActivity.class);
                clear();
                home.overridePendingTransition(0,0);
            }
        });

        return view;
    }

    private void uploadInfo() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (!username.getText().toString().isEmpty() || !username.getText().toString().equals(home.Username())){

            map.put("username", username.getText().toString());
            //username.setText(edit_username.getText().toString());
        }
        if (!phone.getText().toString().isEmpty() || !phone.getText().toString().equals(home.Phone())){

            map.put("phone", phone.getText().toString());
            //address.setText(edit_address.getText().toString());
        }
        if (!wilaya.getText().toString().isEmpty() || !wilaya.getText().toString().equals(home.Wilaya())){

            map.put("wilaya", wilaya.getText().toString());
            //address.setText(edit_address.getText().toString());
        }
        if (!city.getText().toString().isEmpty() || !city.getText().toString().equals(home.City())){

            map.put("city", city.getText().toString());
            //address.setText(edit_address.getText().toString());
        }
        if (!txt_home_lat.isEmpty() || !txt_home_lng.isEmpty() || !txt_home_lat.equals(home.homeLat()) || !txt_home_lng.equals(home.homeLng())){

            map.put("homeLat", txt_home_lat);
            map.put("homeLng", txt_home_lng);
        }
        if (!description.getText().toString().isEmpty() || !description.getText().toString().equals(home.Description())){

            map.put("description", description.getText().toString());
            description.setHint(description.getText().toString());
        }

        firestore.collection("Client").document(firebaseUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isSuccessful()){

                    progressDialog.dismiss();

                    //updatePage();
                }else {

                    Toast.makeText(getContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }

            }
        });

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){

        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    String current_image;

    public void uploadImage(){

        current_image = imageURL;

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (imageUri != null){

            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    +"."+getFileExtension(imageUri));

            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }

                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String mUri = downloadUri.toString();

                        //reference = FirebaseDatabase.getInstance().getReference("Client_Data").child(firebaseUser.getUid());

                        map.put("imageURL", mUri);

                        /*reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                    deleteOldImage();

                                    progressDialog.dismiss();

                                }else {

                                    Toast.makeText(getContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });*/

                        firestore.collection("Client").document(firebaseUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()){

                                    deleteOldImage();

                                    progressDialog.dismiss();

                                }else {

                                    Toast.makeText(getContext(), "an error occurred", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                            }
                        });


                    }else {
                        Toast.makeText(getContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(), "No image selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteOldImage() {

        if (!current_image.equals("default")) {

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageReference_2 = firebaseStorage.getReferenceFromUrl(current_image);

            storageReference_2.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    // File deleted successfully

                    //Toast.makeText(getContext(), "old image has been deleted", Toast.LENGTH_SHORT).show();

                    imageURL = home.ImageURL();

                    if (imageURL.equals("default")) {

                        profile_picture.setImageResource(R.drawable.user_2);
                    } else {
                        Glide.with(getContext()).load(imageURL).into(profile_picture);
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Uh-oh, an error occurred!

                    Toast.makeText(getContext(), "an error occurred!", Toast.LENGTH_SHORT).show();
                }
            });
        }else {

            imageURL = home.ImageURL();

            if (imageURL.equals("default")) {

                profile_picture.setImageResource(R.drawable.user_2);
            } else {
                Glide.with(getContext()).load(imageURL).into(profile_picture);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();

            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            }else {
                uploadImage();
                /*
                 we will add a method named confirm and uploadImage(); gonna be included in it so we can reduce the number of writ on firebase

                 and this method contain the edited username and address and ..
                */
            }

        }

    }

    public void downloadInfo(){


        t_username.setText(home.Username());
        t_phone.setText(home.Phone());
        t_city.setText(home.City());

        username.setText(home.Username());
        phone.setText(home.Phone());
        wilaya.setText(home.Wilaya());
        city.setText(home.City());
        txt_home_lat = home.homeLat();
        txt_home_lng = home.homeLng();
        homeAddressInput.setText(home.homeLat()+"\n"+home.homeLng());
        description.setText(home.Description());
        imageURL = home.ImageURL();

        if (imageURL.equals("default")){

            profile_picture.setImageResource(R.drawable.user_2);
            progressBar.setVisibility(View.GONE);
        }else {

            Glide.with(getContext()).load(imageURL).into(profile_picture);
        }
    }


    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.INTERNET
            }, 10);

            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){

            case 10:

                if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)

                 /*                   if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
                */

                    Toast.makeText(getContext(), "this app request to access your location data", Toast.LENGTH_SHORT).show();

                return;
        }
    }

    private void clear() {

        Cursor cursor = db.viewData();

        if (cursor.getCount() == 0){

            clear_();

        }else {

            while (cursor.moveToNext()){

                db.deleteData(cursor.getString(0), firebaseUser.getUid(),
                        cursor.getString(2), cursor.getString(3),
                        cursor.getString(4), cursor.getString(5),
                        "", cursor.getString(7),
                        cursor.getString(8));

            }
        }

        clear_();

    }
    private void clear_() {

        Favorite favorite = new Favorite(getContext());

        Cursor cursor = favorite.viewData();

        if (cursor.getCount() == 0){

        }else {

            while (cursor.moveToNext()){

                progressBar.setVisibility(View.GONE);

                favorite.deleteData(cursor.getString(0),"","","","","","", "", "");

            }
        }

        startActivity(intent);

    }


    private void ShowChangeLaguageDialog() {
        final String[] listItems={"English","العربية","Français"};
        android.app.AlertDialog.Builder mBuilder=new android.app.AlertDialog.Builder(getContext());

        final Intent intent = new Intent(getContext(), MainActivity.class);

        String Title=getString(R.string.ChooseLang);
        mBuilder.setTitle(Title);
        mBuilder.setSingleChoiceItems(listItems, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(i==0){

                    setApplicationLocale("en");
                    startActivity(intent);

                }else if(i==1){

                    setApplicationLocale("ar");
                    startActivity(intent);

                }else if(i==2){

                    setApplicationLocale("fr");
                    startActivity(intent);

                }

            }
        });
        android.app.AlertDialog mDialog=mBuilder.create();
        mDialog.show();
    }

    private void setApplicationLocale(String locale) {
        Resources resources = getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(locale.toLowerCase()));
            saveLocale(locale);
        } else {
            config.locale = new Locale(locale.toLowerCase());
            saveLocale(locale);
        }
        resources.updateConfiguration(config, dm);
    }

    public void saveLocale(String lang) {
        String langPref = "Language";
        SharedPreferences prefs =this.getActivity(). getSharedPreferences("CommonPrefs",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(langPref, lang);
        editor.commit();

    }

}
