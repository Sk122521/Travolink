package com.hfad.travelx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import java.security.acl.Owner;
import java.util.ArrayList;
//Edit features to add for owner of restaurant

public class ShowRestaurantActivity extends AppCompatActivity {
    private DatabaseReference restaurentref;
    private Bundle bundle = null;
    private FloatingActionButton mapButton,phonerest;
    private CollapsingToolbarLayout restaurenttoolbar ;
    private TextView abouttext,cuisinestext,paymenttext,Daystext,Servicestext,websitetext;
    private RecyclerView ambiencerecyclerview,dishesrecyclerview,menurecyclerview;
    private String name,about,cuisines,payment,opendays,services,website,topimage,latitude,longitude,contact,CurrentUserid,OwnerId;
    private LinearLayoutManager locationlinearlayout;
    private MaterialButton editrestinfobutton;
    private ImageView headerImage;
    private TextView ambienceheading,cuisineheading,serviceheading,daysopenheading,paymentsheading,dishesheading,menusheading,webheading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_restaurant);
        bundle = this.getIntent().getExtras();
        CurrentUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        restaurentref = FirebaseDatabase.getInstance().getReference().child("DestinationCities").child(bundle.getString("city")).child("Foodlocation").child(bundle.getString("name"));
        restaurenttoolbar = (CollapsingToolbarLayout) findViewById(R.id.restcollaspingToolbar);
        mapButton = (FloatingActionButton)findViewById(R.id.MapButton);
        abouttext  = (TextView) findViewById(R.id.about_text);
        ambiencerecyclerview = (RecyclerView) findViewById(R.id.AmbienceImagesRecyclerView);
        dishesrecyclerview = (RecyclerView) findViewById(R.id.DishesImageRecyclerView);
        menurecyclerview = (RecyclerView) findViewById(R.id.MenuImageRecyclerView);
        cuisinestext =(TextView) findViewById(R.id.Cuisines);
        paymenttext = (TextView) findViewById(R.id.Payments);
        Servicestext =(TextView) findViewById(R.id.Services);
        Daystext = (TextView) findViewById(R.id.Days);
        websitetext =(TextView) findViewById(R.id.websiteofrest);
        ambienceheading = (TextView)findViewById(R.id.AmbienceImageText);
        dishesheading = (TextView)findViewById(R.id.DishesImageTextView);
        menusheading = (TextView)findViewById(R.id.MenuImageTextView) ;
        cuisineheading = (TextView)findViewById(R.id.CuisineTextView);
        serviceheading = (TextView)findViewById(R.id.ServicesTextView);
        daysopenheading = (TextView)findViewById(R.id.daysTextView);
        paymentsheading = (TextView)findViewById(R.id.PaymentsTextView);
        webheading = (TextView)findViewById(R.id.WebsiteTextView);
        headerImage = (ImageView)findViewById(R.id.restheaderimage);
        phonerest = (FloatingActionButton)findViewById(R.id.phone_rest);
        editrestinfobutton = (MaterialButton)findViewById(R.id.edit_restinfo_button);
        restaurentref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name = snapshot.child("name").getValue().toString();
                about = snapshot.child("about").getValue().toString();
                cuisines = snapshot.child("cuisine").getValue().toString();
                payment = snapshot.child("payments").getValue().toString();
                opendays = snapshot.child("daysOpen").getValue().toString();
                services = snapshot.child("services").getValue().toString();
                website= snapshot.child("website").getValue().toString();
                topimage = snapshot.child("mainimage").getValue().toString();
                latitude = snapshot.child("latitude").getValue().toString();
                longitude  = snapshot.child("longitude").getValue().toString();
                contact = snapshot.child("contact").getValue().toString();
                OwnerId = snapshot.child("ownerid").getValue().toString();
                if (TextUtils.isEmpty(cuisines)){
                    cuisinestext.setVisibility(View.GONE);
                    cuisineheading.setVisibility(View.GONE);
                }else{
                    cuisinestext.setText(cuisines);
                }
                if (TextUtils.isEmpty(payment)){
                     paymenttext.setVisibility(View.GONE);
                     paymentsheading.setVisibility(View.GONE);
                }else{
                    paymenttext.setText(payment);
                }
                if (TextUtils.isEmpty(opendays)){
                    Daystext.setVisibility(View.GONE);
                    daysopenheading.setVisibility(View.GONE);
                }else{
                    Daystext.setText(opendays);
                }
                if (TextUtils.isEmpty(services)){
                     Servicestext.setVisibility(View.GONE);
                     serviceheading.setVisibility(View.GONE);
                }else{
                    Servicestext.setText(services);
                }
                if (TextUtils.isEmpty(website)){
                         websitetext.setVisibility(View.GONE);
                         webheading.setVisibility(View.GONE);
                }else{
                    websitetext.setText(website);
                }
                if (TextUtils.isEmpty(topimage)){
                     headerImage.setVisibility(View.GONE);
                }else{
                    Picasso.get().load(topimage).into(headerImage);
                }
                abouttext.setText(about);
                restaurenttoolbar.setTitle(name);
                if (!TextUtils.isEmpty(contact)){
                    phonerest.setVisibility(View.VISIBLE);
                }
                if (CurrentUserid.equals(OwnerId)){
                    editrestinfobutton.setVisibility(View.VISIBLE);
                    editrestinfobutton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent  = new Intent(ShowRestaurantActivity.this,EditRestaurantActivity.class);
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
             /*   Intent intent  = new Intent(ShowRestaurantActivity.this,MapsActivity.class);
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
        phonerest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:"+Uri.encode(contact.trim())));
                if (ActivityCompat.checkSelfPermission( ShowRestaurantActivity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    startActivity(intent);
                    return;
                }
            }
        });
        InitialiseRecyclerView(ambiencerecyclerview);
        InitialiseRecyclerView(dishesrecyclerview);
        InitialiseRecyclerView(menurecyclerview);
        DispalyMenus();
        DisplayDishes();
        DisplayAmbience();
    }


    private void InitialiseRecyclerView(RecyclerView recyclerView) {
        locationlinearlayout = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(locationlinearlayout);
    }
    private void DisplayAmbience() {
        DatabaseReference ref1 = restaurentref.child("ambience");
        FirebaseRecyclerAdapter adapter3 = new FirebaseRecyclerAdapter<DisplayImages, ShowRestaurantActivity.DisplayImgesHolder>(new FirebaseRecyclerOptions.Builder().setQuery(ref1,DisplayImages.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull ShowRestaurantActivity.DisplayImgesHolder holder, int position, @NonNull DisplayImages model) {
                   if (model.getImg().isEmpty()&& position == 0){
                       ambiencerecyclerview.setVisibility(View.GONE);
                       ambienceheading.setVisibility(View.GONE);
                   } else{
                    Picasso.get().load(model.getImg()).into(holder.imageView);
                }

            }

            @NonNull
            @Override
            public ShowRestaurantActivity.DisplayImgesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ShowRestaurantActivity.DisplayImgesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslayout,parent,false));
            }

        };
        ambiencerecyclerview.setAdapter(adapter3);
        adapter3.startListening();
    }

    private void DisplayDishes() {
        DatabaseReference ref2 = restaurentref.child("dishes");
        FirebaseRecyclerAdapter adapter2 = new FirebaseRecyclerAdapter<DisplayImages, ShowRestaurantActivity.DisplayImgesHolder>(new FirebaseRecyclerOptions.Builder().setQuery(ref2,DisplayImages.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull ShowRestaurantActivity.DisplayImgesHolder holder, int position, @NonNull DisplayImages model) {
                if (model.getImg().isEmpty()&& position == 0){
                   dishesrecyclerview.setVisibility(View.GONE);
                   dishesheading.setVisibility(View.GONE);
                } else{
                    Picasso.get().load(model.getImg()).into(holder.imageView);
                }
            }

            @NonNull
            @Override
            public ShowRestaurantActivity.DisplayImgesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ShowRestaurantActivity.DisplayImgesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslayout,parent,false));
            }

        };
        dishesrecyclerview.setAdapter(adapter2);
        adapter2.startListening();
    }

    private void DispalyMenus() {
        DatabaseReference ref3 = restaurentref.child("menus");
        FirebaseRecyclerAdapter adapter1 = new FirebaseRecyclerAdapter<DisplayImages, ShowRestaurantActivity.DisplayImgesHolder>(new FirebaseRecyclerOptions.Builder().setQuery(ref3,DisplayImages.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull ShowRestaurantActivity.DisplayImgesHolder holder, int position, @NonNull DisplayImages model) {
                if (model.getImg().isEmpty()&& position == 0){
                    menurecyclerview.setVisibility(View.GONE);
                    menusheading.setVisibility(View.GONE);
                } else{
                    Picasso.get().load(model.getImg()).into(holder.imageView);
                }
            }

            @NonNull
            @Override
            public ShowRestaurantActivity.DisplayImgesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ShowRestaurantActivity.DisplayImgesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.imageslayout,parent,false));
            }

        };
        menurecyclerview.setAdapter(adapter1);
        adapter1.startListening();
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