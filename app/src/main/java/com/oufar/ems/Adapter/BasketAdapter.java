package com.oufar.ems.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.oufar.ems.ChatActivity;
import com.oufar.ems.Database.DB;
import com.oufar.ems.Home;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.R;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.droidsonroids.gif.GifImageView;

public class BasketAdapter extends RecyclerView.Adapter<BasketAdapter.ViewHolder> {

    private static final String TAG = "BasketAdapter";

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;

    //vars
    private ArrayList<Plate> mNames = new ArrayList<>();
    private Context mContext;
    private String mImageURL;

    public BasketAdapter(Context context, ArrayList<Plate> names, String imageURL) {
        mNames = names;
        mContext = context;
        mImageURL = imageURL;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_in_list, parent, false);
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

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("You want to delete this ("+mNames.get(position).getPlate()+") ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String id = mNames.get(position).getId()+"";

                                reference.child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if (task.isSuccessful()){

                                        }else {

                                            Toast.makeText(mContext, "click again", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();
            }
        });

        holder.plate.setText(mNames.get(position).getPlate());
        holder.price.setText(mNames.get(position).getPrice());
        //holder.description.setText(mNames.get(position).getDescription());
        holder.counter_txt.setText(mNames.get(position).getNumber());

        if (mNames.get(position).getStatus().equals("on hold")){

            //holder.status_txt.setText("Order on hold");

            holder.status.setImageResource(R.drawable.waite);
            holder.chat.setVisibility(View.GONE);

        }else if (mNames.get(position).getStatus().equals("accepted")){

            //holder.status_txt.setText("Order accepted");
            holder.status.setImageResource(R.drawable.nike);
            holder.delete.setVisibility(View.GONE);

            holder.chat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent i = new Intent(mContext, ChatActivity.class);
                    i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP);
                    i.putExtra("Store_id", mNames.get(position).getStoreId());// sending Store id
                    i.putExtra("Client_image", mImageURL);// sending Client image
                    mContext.startActivity(i);
                }
            });


        }else if (mNames.get(position).getStatus().equals("refused")){

            //holder.status_txt.setText("Order refused");
            holder.status.setImageResource(R.drawable.x);
            holder.chat.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView box;
        ImageView delete, image, status, chat;
        TextView plate, price, description, counter_txt, status_txt;
        RelativeLayout container;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            delete = itemView.findViewById(R.id.delete);
            image = itemView.findViewById(R.id.image);
            plate = itemView.findViewById(R.id.plate);
            price = itemView.findViewById(R.id.price);
            //description = itemView.findViewById(R.id.description);
            counter_txt = itemView.findViewById(R.id.counter_txt);
            container = itemView.findViewById(R.id.container);
            status_txt = itemView.findViewById(R.id.status_txt);
            status = itemView.findViewById(R.id.status);
            chat = itemView.findViewById(R.id.chat);
        }
    }
}
