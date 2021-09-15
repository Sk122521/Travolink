package com.hfad.travelx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ShowStayActivity extends AppCompatActivity {
    private DatabaseReference stayref;
    private Bundle bundle = null;
    private FloatingActionButton mapButton,phonestay;
    private CollapsingToolbarLayout staytoolbar ;
    private TextView abouttext,amenitiestext,paymenttext,occupancytext,tenantstext,websitetext,typestay;
    private RecyclerView Roomimagerecyclerview,nearbyimagerecyclerview;
    private String name,about,amenities,payment,tenants,occupancy,website,topimage,latitude,longitude,contact,type;
    private LinearLayoutManager locationlinearlayout;
    private TextView tenantsheading,amenitiesheading,paymentsheading,occupancyheading,webheading,roomimghead,nearbyimghead;
    private MaterialButton EditStayInfo;
     private String CurrentUserId,OwnerId;
     private ImageView HeaderImage ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_stay);
        bundle = this.getIntent().getExtras();
        CurrentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        stayref= FirebaseDatabase.getInstance().getReference().child("DestinationCities").child(bundle.getString("city")).child("stay").child(bundle.getString("name"));
        staytoolbar = (CollapsingToolbarLayout) findViewById(R.id.staycollaspingToolbar);
        mapButton = (FloatingActionButton)findViewById(R.id.MapButtonstay);
        abouttext  = (TextView) findViewById(R.id.about_text_stay);
        Roomimagerecyclerview = (RecyclerView) findViewById(R.id.roomsImagesRecyclerView);
        nearbyimagerecyclerview= (RecyclerView) findViewById(R.id.nearbyImageRecyclerView);
        amenitiestext =(TextView) findViewById(R.id.Amenities);
        paymenttext = (TextView) findViewById(R.id.Payments_stay);
        occupancytext =(TextView) findViewById(R.id.Occupancy);
        tenantstext = (TextView) findViewById(R.id.Tenants);
        websitetext =(TextView) findViewById(R.id.websiteofstay);
        tenantsheading = (TextView)findViewById(R.id.Tenants_text);
        occupancyheading = (TextView)findViewById(R.id.occupancy_textview);
        amenitiesheading = (TextView)findViewById(R.id.AmenitiesTextView) ;
       paymentsheading = (TextView)findViewById(R.id.PaymentsTextView);
        roomimghead = (TextView)findViewById(R.id.stayImageText);
        nearbyimghead = (TextView)findViewById(R.id.exteriorTextView);
        paymentsheading = (TextView)findViewById(R.id.PaymentsTextView);
        webheading = (TextView)findViewById(R.id.WebsiteTextstay);
        typestay = (TextView)findViewById(R.id.type_stay);
        EditStayInfo = (MaterialButton)findViewById(R.id.edit_stayinfo_button);
        HeaderImage = (ImageView)findViewById(R.id.stayheaderimage);
        phonestay = (FloatingActionButton)findViewById(R.id.phone_stay);
        stayref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("name").getValue().toString();
                about = snapshot.child("about").getValue().toString();
                amenities = snapshot.child("amenities").getValue().toString();
                payment = snapshot.child("payments").getValue().toString();
                tenants = snapshot.child("tenants").getValue().toString();
                occupancy = snapshot.child("occupancy").getValue().toString();
                website= snapshot.child("website").getValue().toString();
                topimage = snapshot.child("mainimage").getValue().toString();
                latitude = snapshot.child("latitude").getValue().toString();
                longitude  = snapshot.child("longitude").getValue().toString();
                contact = snapshot.child("contact").getValue().toString();
                type = snapshot.child("type").getValue().toString();
                OwnerId = snapshot.child("ownerid").getValue().toString();
                if (TextUtils.isEmpty(amenities)){
                    amenitiestext.setVisibility(View.GONE);
                    amenitiesheading.setVisibility(View.GONE);
                }else{
                    amenitiestext.setText(amenities);
                }
                if (TextUtils.isEmpty(payment)){
                    paymenttext.setVisibility(View.GONE);
                    paymentsheading.setVisibility(View.GONE);
                }else{
                    paymenttext.setText(payment);
                }
                if (TextUtils.isEmpty(tenants)){
                   tenantstext.setVisibility(View.GONE);
                    tenantsheading.setVisibility(View.GONE);
                }else{
                    tenantstext.setText(tenants);
                }
                if (TextUtils.isEmpty(occupancy)){
                    occupancytext.setVisibility(View.GONE);
                    occupancyheading.setVisibility(View.GONE);
                }else{
                   occupancytext.setText(occupancy);
                }
                if (TextUtils.isEmpty(website)){
                    websitetext.setVisibility(View.GONE);
                    webheading.setVisibility(View.GONE);
                }else{
                    websitetext.setText(website);
                }
                if (TextUtils.isEmpty(topimage)){
                    HeaderImage.setVisibility(View.GONE);
                }else{
                     Picasso.get().load(topimage).into(HeaderImage);
                }
                abouttext.setText(about);
                staytoolbar.setTitle(name);
                typestay.setText(type);
                if (!TextUtils.isEmpty(contact)){
                    phonestay.setVisibility(View.VISIBLE);
                }
                if (CurrentUserId.equals(OwnerId)){
                    EditStayInfo.setVisibility(View.VISIBLE);
                    EditStayInfo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent  = new Intent(ShowStayActivity.this,EditStayActivity.class);
                            Bundle latlongbundle = new Bundle();
                            latlongbundle.putString("name",name);
                            latlongbundle.putString("city",bundle.getString("city"));
                            intent.putExtras(latlongbundle);
                            startActivity(intent);
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             /*   Intent intent  = new Intent(ShowStayActivity.this,MapsActivity.class);
                Bundle latlongbundle = new Bundle();
                latlongbundle.putString("latitude",latitude);
                latlongbundle.putString("longitude",longitude);
                intent.putExtras(latlongbundle);
                startActivity(intent);*/
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitude+","+longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });
        phonestay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+Uri.encode(contact.trim())));
                if (ActivityCompat.checkSelfPermission( ShowStayActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                    return;
                }
            }
        });
        InitialiseRecyclerView(Roomimagerecyclerview);
        InitialiseRecyclerView(nearbyimagerecyclerview);
        DisplayRoomsImage();
        DisplayNearbyImage();
    }
    private void InitialiseRecyclerView(RecyclerView recyclerView) {
        locationlinearlayout = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(locationlinearlayout);
    }
    private void DisplayRoomsImage() {
        DatabaseReference ref1 = stayref.child("roomsimage");
        FirebaseRecyclerAdapter adapter3 = new FirebaseRecyclerAdapter<DisplayImages, ShowStayActivity.DisplayImgesHolder>(new FirebaseRecyclerOptions.Builder().setQuery(ref1,DisplayImages.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull ShowStayActivity.DisplayImgesHolder holder, int position, @NonNull DisplayImages model) {
                if (model.getImg().isEmpty()&& position == 0){
                    Roomimagerecyclerview.setVisibility(View.GONE);
                    roomimghead.setVisibility(View.GONE);
                } else{
                    Picasso.get().load(model.getImg()).into(holder.imageView);
                }

            }

            @NonNull
            @Override
            public ShowStayActivity.DisplayImgesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ShowStayActivity.DisplayImgesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslayout,parent,false));
            }

        };
        Roomimagerecyclerview.setAdapter(adapter3);
        adapter3.startListening();
    }
    private void DisplayNearbyImage() {
        DatabaseReference ref1 = stayref.child("nearbyimage");
        FirebaseRecyclerAdapter adapter3 = new FirebaseRecyclerAdapter<DisplayImages, ShowStayActivity.DisplayImgesHolder>(new FirebaseRecyclerOptions.Builder().setQuery(ref1,DisplayImages.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull ShowStayActivity.DisplayImgesHolder holder, int position, @NonNull DisplayImages model) {
                if (model.getImg().isEmpty()&& position == 0){
                    nearbyimagerecyclerview.setVisibility(View.GONE);
                    nearbyimghead.setVisibility(View.GONE);
                } else{
                    Picasso.get().load(model.getImg()).into(holder.imageView);
                }

            }

            @NonNull
            @Override
            public ShowStayActivity.DisplayImgesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ShowStayActivity.DisplayImgesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslayout,parent,false));
            }

        };
        nearbyimagerecyclerview.setAdapter(adapter3);
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