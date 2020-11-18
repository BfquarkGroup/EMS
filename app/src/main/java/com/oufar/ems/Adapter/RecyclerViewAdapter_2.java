package com.oufar.ems.Adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.oufar.ems.Database.DB;
import com.oufar.ems.Menu;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.Model.Store;
import com.oufar.ems.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerViewAdapter_2 extends RecyclerView.Adapter<RecyclerViewAdapter_2.ViewHolder> implements Filterable {

    private static final String TAG = "RecyclerViewAdapter_2";

    private DatabaseReference reference;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private String key;

    private DB db;

    private int i = 0;

    private HashMap<String, String> hashMap;

    //vars
    private ArrayList<Plate> storesListFull = new ArrayList<>();
    private ArrayList<Plate> mNames = new ArrayList<>();
    private Context mContext;

    public RecyclerViewAdapter_2(Context context, ArrayList<Plate> names) {
        mNames = names;
        storesListFull = new ArrayList<>(mNames);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_in_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();

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
                            mNames.get(position).getDescription(), i+"", mNames.get(position).getPrice(),
                            mNames.get(position).getStoreEmail());


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
                                    mNames.get(position).getDescription(), i+"", mNames.get(position).getPrice(),
                                    mNames.get(position).getStoreEmail());
                        }

                    }

                    if (i == 0){

                        if (cursor.getCount() != 0){

                            while (cursor.moveToNext()){

                                if     (mNames.get(position).getStoreId().equals(cursor.getString(2)) &&
                                        mNames.get(position).getPlate().equals(cursor.getString(3)) &&
                                        mNames.get(position).getImageURL().equals(cursor.getString(4)) &&
                                        mNames.get(position).getDescription().equals(cursor.getString(5)) &&
                                        mNames.get(position).getPrice().equals(cursor.getString(7))){

                                    db.deleteData(cursor.getString(0), null, null, null, null, null, null, null, null);

                                }
                            }
                        }

                        //holder.counter.setVisibility(View.GONE);
                    }

                    holder.counter_txt.setText(i+"");
                }
            }
        });

        holder.minus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                i = Integer.parseInt(holder.counter_txt.getText().toString());

                Cursor cursor = db.viewData();



                return true;
            }
        });

        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                auth = FirebaseAuth.getInstance();
                firebaseUser = auth.getCurrentUser();

                db.insertData(firebaseUser.getUid(), mNames.get(position).getStoreId(),
                              mNames.get(position).getPlate(), mNames.get(position).getImageURL(),
                              mNames.get(position).getDescription(), "0", mNames.get(position).getPrice(), mNames.get(position).getStoreEmail());


            }
        });

        holder.plate.setText(mNames.get(position).getPlate());
        holder.price.setText(mNames.get(position).getPrice());
        holder.description.setText(mNames.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    @Override
    public Filter getFilter() {
        return storesFilter;
    }

    private Filter storesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Plate> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(storesListFull);

            } else {

                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Plate plate : storesListFull) {
                    if (plate.getPlate().toLowerCase().contains(filterPattern)
                            || plate.getPrice().toLowerCase().contains(filterPattern)) {

                        filteredList.add(plate);
                    }
                }
            }
            Log.d("tst", "performFiltering: "+filteredList.size());
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mNames.clear();
            mNames.addAll(( Collection<? extends Plate> ) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView box;
        ImageView image;
        TextView plate, price, description, counter_txt;
        LinearLayout counter_, plus, minus;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            image = itemView.findViewById(R.id.image);
            plate = itemView.findViewById(R.id.plate);
            price = itemView.findViewById(R.id.price);
            description = itemView.findViewById(R.id.description);
            plus = itemView.findViewById(R.id.plus);
            minus = itemView.findViewById(R.id.minus);
            counter_ = itemView.findViewById(R.id.counter);
            counter_txt = itemView.findViewById(R.id.counter_txt);
        }
    }
}
