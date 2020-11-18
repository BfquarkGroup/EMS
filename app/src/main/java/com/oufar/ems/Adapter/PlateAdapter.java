package com.oufar.ems.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.oufar.ems.Model.Plate;
import com.oufar.ems.R;

import java.util.ArrayList;
import java.util.List;

public class PlateAdapter extends BaseAdapter {


    private List<Plate> plateList;
    private Context mContext;
    private LayoutInflater inflater;
    private ArrayList<Plate> arrayList;


    public PlateAdapter(Context context, List<Plate> modellist) {
        this.mContext = context;
        this.plateList = modellist;
        inflater = LayoutInflater.from(mContext);
        this.arrayList = new ArrayList<Plate>();
        this.arrayList.addAll(modellist);
    }

    public static class ViewHolder{

        TextView plate, price, description;

    }

    @Override
    public int getCount() {
        return plateList.size();
    }

    @Override
    public Object getItem(int i) {
        return plateList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    @Override
    public View getView(int position, View view, ViewGroup parent) {

        PlateAdapter.ViewHolder holder;

        if (view==null){
            holder = new PlateAdapter.ViewHolder();
            view = inflater.inflate(R.layout.row_in_list, null);

            // locate the view in row_announcement xml
            holder.plate = view.findViewById(R.id.plate);
            holder.price = view.findViewById(R.id.price);
            holder.description = view.findViewById(R.id.description);

            view.setTag(holder);
        }else {

            holder = ( PlateAdapter.ViewHolder )view.getTag();
        }

        // set the results on textView
        holder.plate.setText( plateList.get(position).getPlate());
        holder.price.setText(plateList.get(position).getPrice());
        holder.description.setText(plateList.get(position).getDescription());
        //final String id = announcementList.get(position).getId();


        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog Alert = new AlertDialog.Builder(mContext)
                        .setMessage("Remove this plate ?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation

                                Toast.makeText(mContext, "Plate removed", Toast.LENGTH_SHORT).show();

                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton("no", null)
                        .show();

                return true;
            }
        });

        return view;
    }
}
