package com.hfad.travelx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class EachDestActivity extends AppCompatActivity {
    private TextView name,type,guide,backpack,exp,whytravel,foodcult,pn,t2,t3,t4,t5,t6,quote;
    private ImageView imageView;
    private CircleImageView pi;
    private CollapsingToolbarLayout ctl;
    private RecyclerView rv;
    private DatabaseReference ref;
    private String postkey;
    private FloatingActionButton map;
    private String la,lo;
    private ArrayList<String> image = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_each_dest);
        InitialiseFields();
        setValues();
    }
    public void ShowOnMap(View v){
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+la+","+lo);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
    private void setValues() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                String n1 = snapshot.child("destname").getValue().toString();
                String n2 = snapshot.child("typedest").getValue().toString();
                String n3 = snapshot.child("your_experience").getValue().toString();
                String n4 = snapshot.child("whytravel").getValue().toString();
                String n5 = snapshot.child("quote").getValue().toString();
                String n6 = snapshot.child("profilename").getValue().toString();
                String n7 = snapshot.child("profileimage").getValue().toString();
                String n8 = snapshot.child("guide").getValue().toString();
                String n9 = snapshot.child("foodcult").getValue().toString();
                String a = snapshot.child("backpack").getValue().toString();
                la = snapshot.child("latitude").getValue().toString();
                lo = snapshot.child("longitude").toString();

             //   ArrayList<String> img = (ArrayList<String>) snapshot.child("images").getValue();
                name.setText(n1);
                exp.setText(n3);
                if (image.isEmpty()){
                    rv.setVisibility(View.GONE);
                    t3.setVisibility(View.GONE);
                }else{
                    Picasso.get().load(image.get(0)).into(imageView);
                    SelectedImageAdapter selectedImageAdapter = new SelectedImageAdapter(rv.getContext(),image);
                    rv.setAdapter(selectedImageAdapter);
                }
                if (n2.isEmpty()){ type.setVisibility(View.GONE);}else{ type.setText(n2);}
                if (n4.isEmpty()){whytravel.setVisibility(View.GONE); t2.setVisibility(View.GONE);}else{whytravel.setText(n4);}
                if (n8.isEmpty()){guide.setVisibility(View.GONE); t6.setVisibility(View.GONE);}else{guide.setText(n8);}
                if (n9.isEmpty()){foodcult.setVisibility(View.GONE); t4.setVisibility(View.GONE);}else {foodcult.setText(n9);}
                if (a.isEmpty()){backpack.setVisibility(View.GONE); t5.setVisibility(View.GONE);}else{backpack.setText(a);}
                if (n5.isEmpty()){quote.setVisibility(View.GONE);}else {quote.setText(n5);}
                if (n7.isEmpty()){
                    Picasso.get().load(R.drawable.profile).into(pi);
                }else{
                    Picasso.get().load(n7).into(pi);
                }
                pn.setText(n6);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
    private void InitialiseFields() {
        postkey = getIntent().getStringExtra("postkey");
        image = getIntent().getStringArrayListExtra("photo");
        ref = FirebaseDatabase.getInstance().getReference().child("Posts").child(postkey);
        quote = (TextView)findViewById(R.id.q);
        name = findViewById(R.id.Name_Dest);
        type = findViewById(R.id.type_dest);
        exp = findViewById(R.id.my_exp);
        whytravel = findViewById(R.id.whytravel);
        rv= findViewById(R.id.rv);
        foodcult = findViewById(R.id.FC);
        backpack = findViewById(R.id.BA);
        guide = findViewById(R.id.guid);
        ctl = findViewById(R.id.ctl);
        imageView = findViewById(R.id.headerimage);
        pi  = findViewById(R.id.pi);
        pn = findViewById(R.id.pn);
        t2 = findViewById(R.id.text2);
        t3 = findViewById(R.id.text3);
        t4 = findViewById(R.id.text4);
        t5 = findViewById(R.id.text5);
        t6 = findViewById(R.id.text6);
        map = findViewById(R.id.MapButton);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv.setLayoutManager(llm);
    }
}