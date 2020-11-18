package com.oufar.ems.PopUp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.oufar.ems.DialogListener.ExampleDialogListener;
import com.oufar.ems.R;

public class SearchPopUp extends AppCompatDialogFragment {

    private TextView title;
    private EditText input;
    private ExampleDialogListener listener;

    private String TITLE, INPUT;

    public void getData(String id, String username){

        //Toast.makeText(getContext(), "getData Method", Toast.LENGTH_SHORT).show();

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.pop_up_search, null);

        builder.setView(view);
        builder.setNegativeButton("close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        builder.setPositiveButton("search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String txt_input = input.getText().toString();
                Toast.makeText(getContext(), "\n" + txt_input, Toast.LENGTH_SHORT).show();
                //replyNotification(ADMIN_EMAIL, txt_notification);
            }
        });

        title = view.findViewById(R.id.title);
        //title.setText(ADMIN_NAME);
        input = view.findViewById(R.id.input);

        return builder.create();
    }
}
