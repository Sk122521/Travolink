package com.hfad.travelx;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BottomSheetFragment extends BottomSheetDialogFragment {
    private MaterialButton Dest_button,Rest_button,Stay_button,Expr_button;
    private String CurrentUserId;
    private DatabaseReference cityref;
    private Bundle bundle;
    private String city;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_bottom_sheet, container, false);
          bundle = this.getArguments();
          city = bundle.getString("city");
          Expr_button = v.findViewById(R.id.addexperiences_button);
          if (city.equals("ExploreFragment")){
             Expr_button.setVisibility(View.GONE);
          }
        Dest_button = v.findViewById(R.id.adddestination_button);
        Rest_button = v.findViewById(R.id.addrestaurent_button);
        Stay_button = v.findViewById(R.id.addstay_button);
          cityref = FirebaseDatabase.getInstance().getReference().child("DestinationCities").child(city);
          CurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

         Dest_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {      Intent intent = new Intent(getContext(),AddDestinationActivity.class);
                startActivity(intent);
            }
        });

        Rest_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startActivity(new Intent(getContext(),AddRestaurentActivity.class));

            }
        });
        Stay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),AddStayActivity.class));
            }
        });
        Expr_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Share your Experiences in city");
                final EditText input = new EditText(getActivity());
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (input.getText().toString().equals("")){
                            Toast.makeText(getActivity(), "Write Your Experiences", Toast.LENGTH_SHORT).show();
                        }else {
                            cityref.child("Experiences").child(CurrentUserId).child("exp").setValue(input.getText().toString());
                            cityref.child("Experiences").child(CurrentUserId).child("uid").setValue(CurrentUserId);
                        }
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       dialogInterface.cancel();
                    }
                });
                AlertDialog alert = alertDialog.create();
                alert.show();
                alert.setCanceledOnTouchOutside(true);
            }
        });
        return v;
    }

    }
