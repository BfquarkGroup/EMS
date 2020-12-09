package com.oufar.ems.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.oufar.ems.Database.DB;
import com.oufar.ems.Model.Plate;
import com.oufar.ems.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class BonAdapter extends RecyclerView.Adapter<BonAdapter.ViewHolder> {

    private static final String TAG = "BonAdapter";

    private DB db;
    private OnQuantityChangeListener mListener;

    //vars
    private ArrayList<Plate> mNames = new ArrayList<>();
    private Context mContext;
    private String mStore_Id;

    public BonAdapter(Context context, ArrayList<Plate> names, String Store_Id, OnQuantityChangeListener listener) {
        mNames = names;
        mContext = context;
        mStore_Id = Store_Id;
        mListener = listener;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_local_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mNames.get(position).getImageURL())
                .into(holder.image);


        db = new DB(mContext);

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


                                    if (mStore_Id.equals(cursor.getString(2)) && !cursor.getString(6).equals("0")){

                                        Plate plate = new Plate(cursor.getString(0), cursor.getString(3),
                                                cursor.getString(7), cursor.getString(5),
                                                cursor.getString(4), cursor.getString(2),
                                                cursor.getString(0), cursor.getString(6),
                                                "", cursor.getString(8));

                                        mNames.add(plate);
                                    }
                                }

                                double total = 0;

                                for (int i = 0; i < mNames.size(); i++) {

                                    double d = Double.parseDouble(mNames.get(i).getPrice());

                                    double num = Double.parseDouble(mNames.get(i).getNumber());

                                    total = total + (d * num);

                                }

                                mListener.onQuantityChange( total+"" );
                                notifyItemChanged( position );
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();

            }
        });

        holder.plate.setText(mNames.get(position).getPlate());
        holder.price.setText(mNames.get(position).getPrice());
        holder.counter_txt.setText(mNames.get(position).getNumber());

    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView image;
        ImageView delete;
        TextView plate, price, counter_txt;

        ViewHolder(View itemView) {
            super(itemView);
            delete = itemView.findViewById(R.id.delete);
            image = itemView.findViewById(R.id.image);
            plate = itemView.findViewById(R.id.plate);
            price = itemView.findViewById(R.id.price);
            counter_txt = itemView.findViewById(R.id.counter_txt);
        }
    }


    public interface OnQuantityChangeListener {
        void onQuantityChange( String change );
    }
}
