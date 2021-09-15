package com.hfad.travelx;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.auth.data.model.User;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;
import com.google.android.libraries.places.api.Places;


import java.util.Arrays;
import java.util.List;

import okhttp3.OkHttpClient;

import static android.content.ContentValues.TAG;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ExploreFragment extends Fragment implements PlaceSelectionListener{
     private RecyclerView monuments_recyclerview,foods_view,Beaches_recyclerview,nature_recyclerview,sacred_recyclerview;
     private RecyclerView.LayoutManager  LayoutManagerdest;
     private DatabaseReference Destinationref,exploreref,userref;
     private ProgressBar progressBar1,progressBar2,progressBar3,progress_bar4,progress_bar5;
     private FloatingActionButton addplacesfab;
    private DatabaseReference headerref;
    TextView txtPlaceDetails;
    TextView txtPlaceAttrib;
    private ImageView topheaderimage;
    private Toolbar exploretoolbar;
    private CollapsingToolbarLayout explorecollaspingtoolbar;
    private TextView username;
    private MaterialButton searchbutton;
    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View v = inflater.inflate(R.layout.fragment_explore, container, false);
         headerref = FirebaseDatabase.getInstance().getReference().child("header");
         userref = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
         explorecollaspingtoolbar = (CollapsingToolbarLayout)v.findViewById(R.id.explore_collaspingToolbar);
         exploretoolbar = (Toolbar)v.findViewById(R.id.explore_toolbar);
         username = (TextView)v.findViewById(R.id.yourname_text);
       ((AppCompatActivity)getActivity()).setSupportActionBar(exploretoolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.TravelX);
        searchbutton = (MaterialButton)v.findViewById(R.id.search);
        topheaderimage = (ImageView)v.findViewById(R.id.top_header_image);
        exploreref = FirebaseDatabase.getInstance().getReference().child("ExploreFragment");
        monuments_recyclerview =(RecyclerView)v.findViewById(R.id.monuments_recyclerview);
        foods_view =(RecyclerView)v.findViewById(R.id.foods_view);
        Beaches_recyclerview =(RecyclerView)v.findViewById(R.id.Beaches_recyclerview);
        nature_recyclerview =(RecyclerView)v.findViewById(R.id.nature_recyclerview);
        sacred_recyclerview =(RecyclerView)v.findViewById(R.id.Sacredplaces_view);
        addplacesfab = (FloatingActionButton)v.findViewById(R.id.explore_fab) ;
       progressBar1 = v.findViewById(R.id.progress_bar1);
       progressBar2 = v.findViewById(R.id.progress_bar2);
       progressBar3 = v.findViewById(R.id.progress_bar3);
       progress_bar4 = v.findViewById(R.id.progress_bar4);
       progress_bar5 = v.findViewById(R.id.progress_bar5);
       searchbutton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(getContext(),SearchcityActivity.class));
           }
       });
      headerref.addValueEventListener(new ValueEventListener() {
                                          @Override
                                          public void onDataChange(@NonNull DataSnapshot snapshot) {
                                              String headimage = snapshot.child("image").getValue().toString();
                                              if(headimage.equals("")){
                                                  topheaderimage.setImageResource(R.drawable.taj2);
                                              }if (!headimage.equals("")) {
                                                  Picasso.get().load(headimage).into(topheaderimage);
                                              }
                                          }
                                          @Override
                                          public void onCancelled(@NonNull DatabaseError error) {

                                          }
                                      });
// Initialize the AutocompleteSupportFragment.

              //LayoutManagerdest = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
              //Trending_recyclerview.setLayoutManager(LayoutManagerdest);
              //DisplayDestinationsadapter();
              InitialiseRecyclerView(monuments_recyclerview, "monuments");
              InitialiseRecyclerView(nature_recyclerview,"natures");
              InitialiseRecyclerView(Beaches_recyclerview,"beaches");
              InitialiseRecyclerView(foods_view,"food");
              InitialiseRecyclerView(sacred_recyclerview,"sacredplaces");
              addplacesfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("city", "ExploreFragment" );
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getParentFragmentManager(),
                        "ModalBottomSheet");
            }
        });
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                username.setText("Hi! "+ name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return v;
    }
    private void InitialiseRecyclerView(RecyclerView recyclerView,String s) {
        LayoutManagerdest = new LinearLayoutManager(getContext(),RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(LayoutManagerdest);
        DisplayDestinationsadapter(recyclerView,s);
    }

    private void DisplayDestinationsadapter(RecyclerView recyclerView,String s) {
           switch (s){
               case "monuments":
                  DatabaseReference databaseReference = exploreref.child("monuments");
                   FirebaseRecyclerAdapter r2 = new FirebaseRecyclerAdapter<Destination,DestinationViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(databaseReference, Destination.class).build()) {
                       @Override
                       protected void onBindViewHolder(@NonNull DestinationViewHolder holder, int position, @NonNull Destination model) {
                           progressBar1.setVisibility(View.GONE);
                           holder.destinationname.setText(model.getName());
                           holder.destinationlocation.setText(model.getLocation());
                           Picasso.get().load(model.getImage()).into(holder.destinationimage);
                           holder.mview.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent = new Intent(getActivity(),ShowDestinationActivity.class);
                                   Bundle bundle = new Bundle();
                                   bundle.putString("name",model.getName());
                                   bundle.putString("city",model.getCity());
                                   intent.putExtras(bundle);
                                   startActivity(intent);
                               }
                           });
                       }
                       @NonNull
                       @Override
                       public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                           return new ExploreFragment.DestinationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_explore,parent,false));
                       }
                   };
                   recyclerView.setAdapter(r2);
                   r2.startListening();
                   return;
               case "natures":
                   databaseReference = exploreref.child("natures");
                   FirebaseRecyclerAdapter r3 = new FirebaseRecyclerAdapter<Destination,DestinationViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(databaseReference, Destination.class).build()) {
                       @Override
                       protected void onBindViewHolder(@NonNull DestinationViewHolder holder, int position, @NonNull Destination model) {
                           progressBar2.setVisibility(View.GONE);
                           holder.destinationname.setText(model.getName());
                           holder.destinationlocation.setText(model.getLocation());
                           Picasso.get().load(model.getImage()).into(holder.destinationimage);
                           holder.mview.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent = new Intent(getActivity(),ShowDestinationActivity.class);
                                   Bundle bundle = new Bundle();
                                   bundle.putString("name",model.getName());
                                   bundle.putString("city",model.getCity());
                                   intent.putExtras(bundle);
                                   startActivity(intent);
                               }
                           });
                       }
                       @NonNull
                       @Override
                       public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                           return new ExploreFragment.DestinationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_explore,parent,false));
                       }
                   };
                   recyclerView.setAdapter(r3);
                   r3.startListening();
                   return;
               case "beaches":
                   databaseReference = exploreref.child("beaches");
                   FirebaseRecyclerAdapter r4 = new FirebaseRecyclerAdapter<Destination,DestinationViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(databaseReference, Destination.class).build()) {
                       @Override
                       protected void onBindViewHolder(@NonNull DestinationViewHolder holder, int position, @NonNull Destination model) {
                           progressBar3.setVisibility(View.GONE);
                           holder.destinationname.setText(model.getName());
                           holder.destinationlocation.setText(model.getLocation());
                           Picasso.get().load(model.getImage()).into(holder.destinationimage);
                           holder.mview.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent = new Intent(getActivity(),ShowDestinationActivity.class);
                                   Bundle bundle = new Bundle();
                                   bundle.putString("name",model.getName());
                                   bundle.putString("city",model.getCity());
                                   intent.putExtras(bundle);
                                   startActivity(intent);
                               }
                           });
                       }
                       @NonNull
                       @Override
                       public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                           return new ExploreFragment.DestinationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_explore,parent,false));
                       }
                   };
                   recyclerView.setAdapter(r4);
                   r4.startListening();
                   return;
               case "food":
                   databaseReference = exploreref.child("food");
                   FirebaseRecyclerAdapter r5 = new FirebaseRecyclerAdapter<Destination,DestinationViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(databaseReference, Destination.class).build()) {
                       @Override
                       protected void onBindViewHolder(@NonNull DestinationViewHolder holder, int position, @NonNull Destination model) {
                           progress_bar4.setVisibility(View.GONE);
                           holder.destinationname.setText(model.getName());
                           holder.destinationlocation.setText(model.getLocation());
                           Picasso.get().load(model.getImage()).into(holder.destinationimage);
                       }
                       @NonNull
                       @Override
                       public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                           return new ExploreFragment.DestinationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_explore,parent,false));
                       }
                   };
                   recyclerView.setAdapter(r5);
                   r5.startListening();
                   return;
               case "sacredplaces":
                   databaseReference = exploreref.child("sacredplaces");
                   FirebaseRecyclerAdapter r6= new FirebaseRecyclerAdapter<Destination,DestinationViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(databaseReference, Destination.class).build()) {
                       @Override
                       protected void onBindViewHolder(@NonNull DestinationViewHolder holder, int position, @NonNull Destination model) {
                           progress_bar5.setVisibility(View.GONE);
                           holder.destinationname.setText(model.getName());
                           holder.destinationlocation.setText(model.getLocation());
                           Picasso.get().load(model.getImage()).into(holder.destinationimage);
                           holder.mview.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View v) {
                                   Intent intent = new Intent(getActivity(),ShowDestinationActivity.class);
                                   Bundle bundle = new Bundle();
                                   bundle.putString("name",model.getName());
                                   bundle.putString("city",model.getCity());
                                   intent.putExtras(bundle);
                                   startActivity(intent);
                               }
                           });
                       }
                       @NonNull
                       @Override
                       public DestinationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                           return new ExploreFragment.DestinationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_explore,parent,false));
                       }
                   };
                   recyclerView.setAdapter(r6);
                   r6.startListening();
                   return;
           }
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {

    }

    @Override
    public void onError(@NonNull Status status) {

    }

    public static  class DestinationViewHolder extends RecyclerView.ViewHolder{
        ImageView destinationimage;
        TextView destinationname,destinationlocation;
        View mview;
        public DestinationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mview = itemView;
            this.destinationimage = itemView.findViewById(R.id.destination_image);
            this.destinationname = itemView.findViewById(R.id.destination_name);
            this.destinationlocation = itemView.findViewById(R.id.destination_location);
        }
    }
}
