package com.oufar.ems.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oufar.ems.Database.DB;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.R;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter_3 extends RecyclerView.Adapter<RecyclerViewAdapter_3.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter_3";

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    private DB db;
    private int i = 0;

    //vars
    private ArrayList<Plate> mNames = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter_3(Context context, ArrayList<Plate> names) {
        mNames = names;
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_local_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Orders").child(firebaseUser.getUid());


        Glide.with(mContext)
                .asBitmap()
                .load(mNames.get(position).getImageURL())
                .into(holder.image);


        db = new DB(mContext);

        i = 0;

        i = Integer.parseInt(holder.counter_txt.getText().toString());

        if (i == 0){

            //holder.counter.setVisibility(View.GONE);
        }

        holder.plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //holder.counter.setVisibility(View.VISIBLE);

                i = Integer.parseInt(holder.counter_txt.getText().toString());

                i++;

                if (i != 0) {

                    Cursor cursor = db.viewData();

                    if (cursor.getCount() != 0){

                        while (cursor.moveToNext()){

                            if     (mNames.get(position).getStoreId().equals(cursor.getString(2)) &&
                                    mNames.get(position).getPlate().equals(cursor.getString(3)) &&
                                    mNames.get(position).getImageURL().equals(cursor.getString(4)) &&
                                    mNames.get(position).getDescription().equals(cursor.getString(5)) &&
                                    mNames.get(position).getPrice().equals(cursor.getString(7)) &&
                                    mNames.get(position).getStoreEmail().equals(cursor.getString(8))){

                                db.deleteData(cursor.getString(0), null, null, null, null, null, null, null, null);

                            }
                        }
                    }

                    db.insertData(firebaseUser.getUid(), mNames.get(position).getStoreId(),
                                mNames.get(position).getPlate(), mNames.get(position).getImageURL(),
                                mNames.get(position).getDescription(), i+"",
                                mNames.get(position).getPrice(), mNames.get(position).getStoreEmail());


                    holder.counter_txt.setText(i + "");

                }
            }
        });

        holder.minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                i = Integer.parseInt(holder.counter_txt.getText().toString());

                Cursor cursor = db.viewData();

                if (i == 0){

                    return;
                }else {

                    i--;

                    if (i != 0) {

                        if (cursor.getCount() != 0){

                            while (cursor.moveToNext()){

                                if     (mNames.get(position).getStoreId().equals(cursor.getString(2)) &&
                                        mNames.get(position).getPlate().equals(cursor.getString(3)) &&
                                        mNames.get(position).getImageURL().equals(cursor.getString(4)) &&
                                        mNames.get(position).getDescription().equals(cursor.getString(5)) &&
                                        mNames.get(position).getPrice().equals(cursor.getString(7)) &&
                                        mNames.get(position).getStoreEmail().equals(cursor.getString(8))){

                                    db.deleteData(cursor.getString(0), null, null, null, null, null, null, null, null);

                                }
                            }

                            db.insertData(firebaseUser.getUid(), mNames.get(position).getStoreId(),
                                        mNames.get(position).getPlate(), mNames.get(position).getImageURL(),
                                        mNames.get(position).getDescription(), i+"",
                                        mNames.get(position).getPrice(), mNames.get(position).getStoreEmail());
                        }

                    }

                    if (i == 0){

                        /*if (cursor.getCount() != 0){

                            while (cursor.moveToNext()){

                                if     (mNames.get(position).getStoreId().equals(cursor.getString(2)) &&
                                        mNames.get(position).getPlate().equals(cursor.getString(3)) &&
                                        mNames.get(position).getImageURL().equals(cursor.getString(4)) &&
                                        mNames.get(position).getDescription().equals(cursor.getString(5)) &&
                                        mNames.get(position).getPrice().equals(cursor.getString(7))){

                                    db.deleteData(cursor.getString(0), null, null, null, null, null, null, null);

                                }
                            }
                        }*/

                        //holder.counter.setVisibility(View.GONE);

                        String id = mNames.get(position).getId()+"";

                        db.deleteData(id, null, null, null, null, null, null, null, null);

                        notifyDataSetChanged();
                        mNames.remove(position);



                /*reference.child(mNames.get(position).getId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()){

                            Toast.makeText(mContext, "order_deleted", Toast.LENGTH_SHORT).show();
                        }else {

                            Toast.makeText(mContext, "click again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });*/

                        mNames.clear();

                        cursor = db.viewData();

                        while (cursor.moveToNext()){

                            Plate plate = new Plate(cursor.getString(0), cursor.getString(3),
                                                    cursor.getString(7), cursor.getString(5),
                                                    cursor.getString(4), cursor.getString(2),
                                                    cursor.getString(0), cursor.getString(6),
                                                    "", cursor.getString(8));

                            mNames.add(plate);
                        }
                    }

                    holder.counter_txt.setText(i+"");
                }
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("You want to delete this ("+mNames.get(position).getPlate()+") ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String id = mNames.get(position).getId()+"";

                                db.deleteData(id, null, null, null, null, null, null, null, null);

                                notifyDataSetChanged();
                                mNames.remove(position);
                                mNames.clear();

                                Cursor cursor = db.viewData();

                                while (cursor.moveToNext()){

                                    Plate plate = new Plate(cursor.getString(0), cursor.getString(3),
                                                            cursor.getString(7), cursor.getString(5),
                                                            cursor.getString(4), cursor.getString(2),
                                                            cursor.getString(0), cursor.getString(6),
                                                            "", cursor.getString(8));

                                    mNames.add(plate);
                                }
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();

            }
        });

        holder.plate.setText(mNames.get(position).getPlate());
        holder.price.setText(mNames.get(position).getPrice());
        holder.description.setText(mNames.get(position).getDescription());
        holder.counter_txt.setText(mNames.get(position).getNumber());

        //addToFireBase(position);
    }

    private void addToFireBase(int position) {

        HashMap<String, String> hashMap;
        hashMap = new HashMap<>();

        hashMap.put("id", reference.getKey());
        hashMap.put("plate", mNames.get(position).getPlate());
        hashMap.put("price", mNames.get(position).getPrice());
        hashMap.put("description", mNames.get(position).getDescription());
        hashMap.put("imageURL", mNames.get(position).getImageURL());
        hashMap.put("clientId", firebaseUser.getUid());
        hashMap.put("storeId", mNames.get(position).getStoreId());

        reference.setValue(hashMap);
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        ImageView delete;
        TextView plate, price, description, counter_txt;
        LinearLayout counter_, plus, minus;

        public ViewHolder(View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete);
            image = itemView.findViewById(R.id.image);
            plate = itemView.findViewById(R.id.plate);
            price = itemView.findViewById(R.id.price);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            description = itemView.findViewById(R.id.description);
            counter_txt = itemView.findViewById(R.id.counter_txt);
        }
    }
}
