package com.hfad.travelx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ShowDestinationActivity extends AppCompatActivity {
   private TextView namedestination,typedestination,aboutdestination,dest_image;
   private CollapsingToolbarLayout dest_Collaspingtoolbar;
   private FloatingActionButton mapbuttondest;
   private DatabaseReference destinationref;
   private Bundle bundle;
   private String name,about,type,mainimage,latitude,longitude;
   private RecyclerView imagerecyclerview;
    private LinearLayoutManager locationlinearlayout;
    private ImageView headerimage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_destination);
        bundle = this.getIntent().getExtras();
        destinationref = FirebaseDatabase.getInstance().getReference().child("DestinationCities").child(bundle.getString("city")).child("Destinations").child(bundle.getString("name"));
        namedestination = (TextView)findViewById(R.id.name_destination);
        typedestination = (TextView)findViewById(R.id.type_destination);
        aboutdestination = (TextView)findViewById(R.id.about_destination);
        headerimage = (ImageView)findViewById(R.id.dest_headerimage);
        dest_image = (TextView)findViewById(R.id.destination_images);
        dest_Collaspingtoolbar = (CollapsingToolbarLayout) findViewById(R.id.dest_collaspingToolbar);
        imagerecyclerview = (RecyclerView)findViewById(R.id.DestinationImagesRecyclerView);
        locationlinearlayout = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        imagerecyclerview.setLayoutManager(locationlinearlayout);
        mapbuttondest = (FloatingActionButton)findViewById(R.id.MapButton_dest);
        destinationref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name  = snapshot.child("name").getValue().toString();
                type = snapshot.child("type").getValue().toString();
                about = snapshot.child("about").getValue().toString();
                mainimage = snapshot.child("mainimage").getValue().toString();
                latitude = snapshot.child("latitude").getValue().toString();
                longitude  = snapshot.child("longitude").getValue().toString();
                namedestination.setText(name);
                typedestination.setText(type);
                if (TextUtils.isEmpty(about)){
                    aboutdestination.setVisibility(View.GONE);
                }else{
                    aboutdestination.setText(about); }
                 if (TextUtils.isEmpty(mainimage)){
                   headerimage.setVisibility(View.GONE);
                 }else{
                    Picasso.get().load(mainimage).into(headerimage);
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        DisplayDestinationImages();
        mapbuttondest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent intent  = new Intent(ShowDestinationActivity.this,MapsActivity.class);
                Bundle latlongbundle = new Bundle();
                latlongbundle.putString("latitude",latitude);
                latlongbundle.putString("longitude",longitude);
                intent.putExtras(latlongbundle);
                startActivity(intent);

                Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+latitude+","+longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }*/
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
    }

    private void DisplayDestinationImages() {
        DatabaseReference ref1 = destinationref.child("destimages");
        FirebaseRecyclerAdapter adapter3 = new FirebaseRecyclerAdapter<DisplayImages, ShowDestinationActivity.DisplayImgesHolder>(new FirebaseRecyclerOptions.Builder().setQuery(ref1,DisplayImages.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull ShowDestinationActivity.DisplayImgesHolder holder, int position, @NonNull DisplayImages model) {
                if (model.getImg().isEmpty()&& position == 0){
                    imagerecyclerview.setVisibility(View.GONE);
                   dest_image.setVisibility(View.GONE);
                } else{
                    Picasso.get().load(model.getImg()).into(holder.imageView);
                }

            }

            @NonNull
            @Override
            public ShowDestinationActivity.DisplayImgesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ShowDestinationActivity.DisplayImgesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslayout,parent,false));
            }

        };
        imagerecyclerview.setAdapter(adapter3);
        adapter3.startListening();
    }
    public static class DisplayImgesHolder extends  RecyclerView.ViewHolder{
        View mview;
        ImageView imageView;
        public DisplayImgesHolder(@NonNull View itemView) {
            super(itemView);
            this.mview = itemView;
            this.imageView  = itemView.findViewById(R.id.listimages);
        }
    }
}