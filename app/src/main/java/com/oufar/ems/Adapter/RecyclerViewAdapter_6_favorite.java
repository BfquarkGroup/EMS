package com.oufar.ems.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oufar.ems.Menu;
import com.oufar.ems.R;

import java.util.ArrayList;

public class RecyclerViewAdapter_6_favorite extends RecyclerView.Adapter<RecyclerViewAdapter_6_favorite.ViewHolder> {

    private static final String TAG = "RecyclerViewAdapter_6_favorite";

    //vars
    private ArrayList<com.oufar.ems.Model.Favorite> favoritesList = new ArrayList<>();
    private Context mContext;

    private com.oufar.ems.Database.Favorite favorite;

    public RecyclerViewAdapter_6_favorite(Context context, ArrayList<com.oufar.ems.Model.Favorite> mFavoritesList) {
        favoritesList = mFavoritesList;
        mContext = context;
    }

    @Override
    public RecyclerViewAdapter_6_favorite.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_favorite, parent, false);
        return new RecyclerViewAdapter_6_favorite.ViewHolder(view);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(final RecyclerViewAdapter_6_favorite.ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        if (favoritesList.get(position).getStatus().equals("open")){

            holder.close.setVisibility(View.GONE);
            holder.open.setVisibility(View.VISIBLE);
        }else if (favoritesList.get(position).getStatus().equals("close")){

            holder.open.setVisibility(View.GONE);
            holder.close.setVisibility(View.VISIBLE);

        }


        if (favoritesList.get(position).getImageURL().equals("default")){

            holder.image.setImageResource(R.drawable.lollipop_wallpaper_1);
        }else {

            Glide.with(mContext)
                    .asBitmap()
                    .load(favoritesList.get(position).getImageURL())
                    .into(holder.image);
        }

        final Intent intent = new Intent(mContext, Menu.class);
        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("Store_id", favoritesList.get(position).getStoreId());// sending Store id
        intent.putExtra("Store_name", favoritesList.get(position).getName());// sending Store name
        intent.putExtra("Store_image", favoritesList.get(position).getImageURL());// sending Store image
        intent.putExtra("Store_status", favoritesList.get(position).getStatus());// sending Store status
        intent.putExtra("Store_email", favoritesList.get(position).getEmail());// sending Store email

        holder.box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mContext.startActivity(intent);
            }
        });

        favorite = new com.oufar.ems.Database.Favorite(mContext);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("You want to delete this ("+favoritesList.get(position).getName()+") from your favorite list ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                String id = favoritesList.get(position).getId();

                                favorite.deleteData(id, null, null, null, null, null, null, null, null);

                                notifyDataSetChanged();
                                favoritesList.remove(position);
                                favoritesList.clear();

                                Cursor cursor = favorite.viewData();

                                while (cursor.moveToNext()){

                                    com.oufar.ems.Model.Favorite store = new com.oufar.ems.Model.Favorite(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(8));

                                    favoritesList.add(store);
                                }
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();
            }
        });

        holder.name.setText(favoritesList.get(position).getName());
        //holder.phone.setText(favoritesList.get(position).getPhone());
        holder.description.setText(favoritesList.get(position).getDescription());

    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CardView box;
        ImageView image, delete;
        TextView name, phone, description;
        LinearLayout open, close;

        public ViewHolder(View itemView) {
            super(itemView);
            box = itemView.findViewById(R.id.box);
            delete = itemView.findViewById(R.id.delete);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.username);
            phone = itemView.findViewById(R.id.phone);
            description = itemView.findViewById(R.id.description);
            open = itemView.findViewById(R.id.open);
            close = itemView.findViewById(R.id.close);
        }
    }
}
