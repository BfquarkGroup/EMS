package com.oufar.ems.PopUp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oufar.ems.DialogListener.ExampleDialogListener;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.R;

import java.util.HashMap;

public class ConfirmPopUp extends AppCompatDialogFragment {

    private DatabaseReference clean, reference_;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    private TextView title;
    private TextView input;
    private ExampleDialogListener listener;
    private Switch current_location, custom_location;

    private String storeId, INPUT = "";

    public void getData(String STATUS){

        //Toast.makeText(getContext(), "getData Method", Toast.LENGTH_SHORT).show();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_confirm, null);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

        builder.setView(view);
        builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                clean = FirebaseDatabase.getInstance().getReference("Store_Notification");
                //clean.child(storeId).removeValue();

                confirmOrders();

                if (!INPUT.equals("")){

                    Toast.makeText(getContext(), INPUT, Toast.LENGTH_SHORT).show();
                    INPUT = "";
                }
            }
        });

        current_location = view.findViewById(R.id.current_location);
        custom_location = view.findViewById(R.id.custom_location);

        current_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    // this part is specifically for localisation latitude and longitude

                    custom_location.setEnabled(false);
                    INPUT = "111111111111";
                }else {
                    custom_location.setEnabled(true);
                }
            }
        });

        custom_location.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    // this part is specifically for localisation latitude and longitude

                    current_location.setEnabled(false);
                    INPUT = "2222222222222";
                }else {
                    current_location.setEnabled(true);
                }
            }
        });

        //title = view.findViewById(R.id.title);
        //title.setText(ADMIN_NAME);
        input = view.findViewById(R.id.input);



        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                INPUT = "testttttttttttt";
            }
        });

        return builder.create();
    }

    private void confirmOrders(){

        final String status = "2";

        reference_ = FirebaseDatabase.getInstance().getReference("Orders").child(firebaseUser.getUid());

        reference_.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                    String Id = snapshot.child("id").getValue().toString();
                    //String Status = snapshot.child("status").getValue().toString();
                    reference_.child(Id).child("status").setValue(status);
                    //changeStatus(Id, Status);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
