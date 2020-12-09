package com.oufar.ems.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oufar.ems.ChatActivity;
import com.oufar.ems.Database.Favorite;
import com.oufar.ems.Menu;
import com.oufar.ems.Model.Store;
import com.oufar.ems.R;

import java.util.ArrayList;
import java.util.Collection;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> implements Filterable {

    private static final String TAG = "RecyclerViewAdapter";

    //vars
    private ArrayList<Store> storesListFull = new ArrayList<>();
    private ArrayList<Store> mNames = new ArrayList<>();
    private Context mContext;

    private Favorite favorite_DB;
    private String click;

    public HomeAdapter(Context context, ArrayList<Store> names) {
        mNames = names;
        storesListFull = new ArrayList<>(mNames);
        mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = null;

        /*if (mSource.equals("HomeActivity")){



        }else if (mSource.equals("HomeFragment")){

            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_in_home, parent, false);

        }*/

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_in_search, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        //Toast.makeText(mContext, mNames.get(position).getProfession(), Toast.LENGTH_SHORT).show();

        if (mNames.get(position).getStatus().equals("open")){

            holder.close.setVisibility(View.GONE);
            holder.open.setVisibility(View.VISIBLE);
        }else if (mNames.get(position).getStatus().equals("close")){

            holder.open.setVisibility(View.GONE);
            holder.close.setVisibility(View.VISIBLE);

        }

        if (mNames.get(position).getImageURL().equals("default")){

            holder.image.setImageResource(R.drawable.default_photo);
        }else {

            Glide.with(mContext)
                    .asBitmap()
                    .load(mNames.get(position).getImageURL())
                    .into(holder.image);
        }

        final Intent intent = new Intent(mContext, Menu.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Store_id", mNames.get(position).getId());// sending Store id
        intent.putExtra("Store_name", mNames.get(position).getUsername());// sending Store name
        intent.putExtra("Store_image", mNames.get(position).getImageURL());// sending Store image
        intent.putExtra("Store_status", mNames.get(position).getStatus());// sending Store status
        intent.putExtra("Store_email", mNames.get(position).getEmail());// sending Store email
        intent.putExtra("Store_rate", mNames.get(position).getRate());// sending Store rate
        //intent.putExtra("Home_lat", mHomeLat);// sending Home_lat
        //intent.putExtra("Home_lng", mHomeLng);// sending Home_lng
        //intent.putExtra("Username", mUsername);// sending username

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(intent);
            }
        });

        holder.username.setText(mNames.get(position).getUsername());

        if(!mNames.get(position).getRate().equals("null") && mNames.get(position).getRate() != null) {
            holder.ratingBar.setRating(Float.parseFloat(mNames.get(position).getRate()));
        }else {
            //holder.ratingBar.setRating(Float.parseFloat("3"));
        }
        //holder.phone.setText(mNames.get(position).getPhone());
        holder.address.setText(mNames.get(position).getAddress());
        holder.description.setText(mNames.get(position).getDescription());

        holder.Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(intent);
            }
        });

        favorite_DB = new Favorite(mContext);
        Cursor cursor;

        cursor = favorite_DB.viewData();

        click = "not favorite";

        if (cursor.getCount() != 0){

            while (cursor.moveToNext()){

                if     (mNames.get(position).getId().equals(cursor.getString(1))){

                    holder.favorite.setImageResource(R.drawable.heart_orange);

                    click = "is favorite";
                }
            }
        }


        holder.favorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Cursor cursor;
                cursor = favorite_DB.viewData();
                String check = "";

                if (cursor.getCount() != 0){

                    while (cursor.moveToNext()){

                        if     (mNames.get(position).getId().equals(cursor.getString(1))){

                            holder.favorite.setImageResource(R.drawable.heart_orange);

                            check = "is favorite";
                        }
                    }

                    if (check == "is favorite"){

                        //Toast.makeText(mContext, "already added to favorite list", Toast.LENGTH_SHORT).show();
                    }else {

                        favorite_DB.insertData(mNames.get(position).getId(), mNames.get(position).getPhone(), mNames.get(position).getUsername(), mNames.get(position).getAddress(), mNames.get(position).getDescription(), mNames.get(position).getImageURL(), mNames.get(position).getStatus() , mNames.get(position).getEmail());

                        //Toast.makeText(mContext, "new store added to favorite list", Toast.LENGTH_SHORT).show();

                        holder.favorite.setImageResource(R.drawable.heart_orange);

                    }

                }else {

                    favorite_DB.insertData(mNames.get(position).getId(), mNames.get(position).getPhone(), mNames.get(position).getUsername(), mNames.get(position).getAddress(), mNames.get(position).getDescription(), mNames.get(position).getImageURL(), mNames.get(position).getStatus(), mNames.get(position).getEmail());

                    //Toast.makeText(mContext, "new store added to favorite list", Toast.LENGTH_SHORT).show();

                    holder.favorite.setImageResource(R.drawable.heart_orange);

                }

            }
        });

        holder.box.setOutlineAmbientShadowColor(Color.parseColor("#FF5722"));

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
            ArrayList<Store> filteredList = new ArrayList<>();
            if (charSequence == null || charSequence.length() == 0) {
                filteredList.addAll(storesListFull);

            } else {

                String filterPattern = charSequence.toString().toLowerCase().trim();

                for (Store store : storesListFull) {
                    if (store.getUsername().toLowerCase().contains(filterPattern)
                            || store.getPhone().toLowerCase().contains(filterPattern)) {

                        filteredList.add(store);
                    }
                }
            }
            Log.d("tst", "performFiltering: "+filteredList.size());
            FilterResults results = new FilterResults();
            /*if(filteredList.size() == 0){

                results.values = storesListFull;
            }else{
                results.values = filteredList;
            }*/
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            mNames.clear();
            mNames.addAll(( Collection<? extends Store> ) filterResults.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView box;
        ImageView image;
        ImageView favorite;
        TextView username, phone, address, description;
        RelativeLayout Layout;
        LinearLayout open, close;
        RatingBar ratingBar;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            image = itemView.findViewById(R.id.image);
            username = itemView.findViewById(R.id.username);
            phone = itemView.findViewById(R.id.phone);
            address = itemView.findViewById(R.id.address);
            description = itemView.findViewById(R.id.description);
            Layout = itemView.findViewById(R.id.Layout);
            favorite = itemView.findViewById(R.id.favorite);
            open = itemView.findViewById(R.id.open);
            close = itemView.findViewById(R.id.close);
            ratingBar = itemView.findViewById(R.id.ratingBar);
        }
    }
}
