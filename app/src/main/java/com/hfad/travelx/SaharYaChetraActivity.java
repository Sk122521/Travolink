package com.hfad.travelx;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager2.widget.ViewPager2;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.paging.FirebaseRecyclerPagingAdapter;
import com.firebase.ui.database.paging.LoadingState;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.dynamic.ObjectWrapper;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.time.DayOfWeek;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SaharYaChetraActivity extends AppCompatActivity {
   private RecyclerView locationrecyclerview,foodrecyclerview,hotelrecyclerview,experiencesrecyclerview;
    private DatabaseReference Destinationref,foodref,userref;
    private LinearLayoutManager locationlinearlayout;
    private ProgressBar seherProgressbar1,seherProgressbar2,seherProgressbar3;
    private String Currentuserid;
    private CollapsingToolbarLayout citytoolbar;
    private TextView aboutcity;
    private FloatingActionButton fab;
    private String city;
    private ImageView saharimageview;
    private TextView foodtext,staytext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sahar_ya_chetra);
        city  = getIntent().getExtras().get("city").toString();
        Destinationref = FirebaseDatabase.getInstance().getReference().child("DestinationCities").child(city);
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        citytoolbar = (CollapsingToolbarLayout)findViewById(R.id.cityToolbar);
        locationrecyclerview = findViewById(R.id.location_recyclerview);
        foodrecyclerview = findViewById(R.id.food_recyclerview);
        hotelrecyclerview = findViewById(R.id.hotel_recyclerview);
        experiencesrecyclerview = findViewById(R.id.experiences_recyclerview);
        saharimageview = findViewById(R.id.sahar_image);
        aboutcity = findViewById(R.id.aboutcity);
        Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        seherProgressbar1 = findViewById(R.id.progressbar1);
        seherProgressbar2 = findViewById(R.id.progressbar2);
        seherProgressbar3 = findViewById(R.id.progressbar3);
        foodtext = findViewById(R.id.foodtext);
        staytext = findViewById(R.id.hoteltext);
        InitialiseRecyclerView(locationrecyclerview);
        InitialiseRecyclerView(foodrecyclerview);
        InitialiseRecyclerView(hotelrecyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        experiencesrecyclerview.setLayoutManager(linearLayoutManager);
        citytoolbar.setTitle("Explore "+city);
        DispalyDestinations();
        DisplayRestaurants();
        DisplayStays();
        DisplayExperiences();
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // this fab will give a user aceess to put datas related to city
                //like user can add places image,hotels information with special login,foods image,text about cities, and also his experience...
                //will use firebase machine learning to add photos of places ,food and texts
                Bundle bundle = new Bundle();
                bundle.putString("city", city );
                BottomSheetFragment bottomSheet = new BottomSheetFragment();
                bottomSheet.setArguments(bundle);
                bottomSheet.show(getSupportFragmentManager(),
                        "ModalBottomSheet");
            }
        });
       Destinationref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
                String about = snapshot.child("aboutcity").getValue().toString();
               if(about.contains("_n")){
                   String newName = about.replace("_n","\n");
                  aboutcity.setText(newName);
               }else{
                   aboutcity.setText(about);
               }

           }
           @Override
           public void onCancelled(@NonNull DatabaseError error) { }});
       Destinationref.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               String headerimage = snapshot.child("headerimage").getValue().toString();
             if(headerimage.equals("")){
                 saharimageview.setVisibility(View.GONE);
             }else{
                 saharimageview.setVisibility(View.VISIBLE);
                 Picasso.get().load(headerimage).into(saharimageview);
             }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
    private void InitialiseRecyclerView(RecyclerView recyclerView) {
        locationlinearlayout = new LinearLayoutManager(this,RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(locationlinearlayout);
    }
    private void DisplayExperiences() {
        DatabaseReference exprref = Destinationref.child("Experiences");
        FirebaseRecyclerAdapter adapter4 = new FirebaseRecyclerAdapter<Experiencesmodel,ExprViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(exprref,Experiencesmodel.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull ExprViewHolder holder, int position, @NonNull Experiencesmodel model) {
                DatabaseReference ref   = userref.child(model.getUid());
                ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        holder.visitorname.setText(snapshot.child("name").getValue().toString());
                        if (TextUtils.isEmpty(snapshot.child("image").getValue().toString())){
                           holder.visitorprofile.setImageResource(R.drawable.profile);
                        }else{
                            Picasso.get().load(snapshot.child("image").getValue().toString()).placeholder(R.drawable.profile).into(holder.visitorprofile);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                holder.visitorexpr.setText(model.getExp());
                holder.myview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SaharYaChetraActivity.this,ProfileActivity.class);
                        intent.putExtra("visit_user_id",model.getUid());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ExprViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ExprViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.exprienceslayout,parent,false));
            }

        };
        experiencesrecyclerview.setAdapter(adapter4);
        adapter4.startListening();

    }
    private void DisplayStays() {
        DatabaseReference  databaseReference2 = Destinationref.child("stay");
        FirebaseRecyclerAdapter adapter3 = new FirebaseRecyclerAdapter<FoodLocation,foodlocationViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(databaseReference2,FoodLocation.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull foodlocationViewHolder holder, int position, @NonNull FoodLocation model) {
                if (model.getName().isEmpty()&& position == 0){
                    staytext.setVisibility(View.GONE);
                    seherProgressbar3.setVisibility(View.GONE);
                    hotelrecyclerview.setVisibility(View.GONE);
                }else{
                    String name = getRef(position).getKey();
                    seherProgressbar3.setVisibility(View.GONE);
                    holder.Foodplace(model);
                    holder.foodview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View foodview) {
                            Intent intent = new Intent(SaharYaChetraActivity.this,ShowStayActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name",name);
                            bundle.putString("city",city);
                            intent.putExtras(bundle);
                            SaharYaChetraActivity.this.startActivity(intent);
                        }
                    });
                }
            }

            @NonNull
            @Override
            public foodlocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new foodlocationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurentandstreetfoodui,parent,false));
            }

        };
        hotelrecyclerview.setAdapter(adapter3);
        adapter3.startListening();
    }

    private void DisplayRestaurants() {
        DatabaseReference  databaseReference1 = Destinationref.child("Foodlocation");
        FirebaseRecyclerAdapter adapter2 = new FirebaseRecyclerAdapter<FoodLocation,foodlocationViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(databaseReference1,FoodLocation.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull foodlocationViewHolder holder, int position, @NonNull FoodLocation model) {
                if (model.getName().isEmpty()&& position == 0){
                    foodtext.setVisibility(View.GONE);
                    seherProgressbar2.setVisibility(View.GONE);
                    foodrecyclerview.setVisibility(View.GONE);
                }else{
                    String name = getRef(position).getKey();
                    seherProgressbar2.setVisibility(View.GONE);
                    holder.Foodplace(model);
                    holder.foodview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View foodview) {
                            Intent intent = new Intent(SaharYaChetraActivity.this,ShowRestaurantActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("name",name);
                            bundle.putString("city",city);
                            intent.putExtras(bundle);
                            SaharYaChetraActivity.this.startActivity(intent);
                        }
                    });
                }
            }

            @NonNull
            @Override
            public foodlocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new foodlocationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.restaurentandstreetfoodui,parent,false));
            }

        };
        foodrecyclerview.setAdapter(adapter2);
        adapter2.startListening();
    }

    private void DispalyDestinations() {
        DatabaseReference databaseReference = Destinationref.child("Destinations");
        FirebaseRecyclerAdapter adapter1 = new FirebaseRecyclerAdapter<TravelLocation,LocationViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(databaseReference,TravelLocation.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull LocationViewHolder holder, int position, @NonNull TravelLocation model) {
                String name = getRef(position).getKey();
                seherProgressbar1.setVisibility(View.GONE);
                holder.setlocation(model);
                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SaharYaChetraActivity.this,ShowDestinationActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name",name);
                        bundle.putString("city",city);
                        intent.putExtras(bundle);
                        SaharYaChetraActivity.this.startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new LocationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_location,parent,false));
            }

        };
        locationrecyclerview.setAdapter(adapter1);
        adapter1.startListening();
    }
    public class LocationViewHolder extends RecyclerView.ViewHolder{
        View mview;
        KenBurnsView kbvlocation;
        TextView titlelocation;
        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mview = itemView;
            this.kbvlocation = itemView.findViewById(R.id.kvlocation);
            this.titlelocation = itemView.findViewById(R.id.text_title);
        }
        void  setlocation(TravelLocation travelLocation){
            titlelocation.setText(travelLocation.getName());
            Picasso.get().load(travelLocation.getMainimage()).into(kbvlocation);
        }

    }
    public static class foodlocationViewHolder extends RecyclerView.ViewHolder{
       View foodview;
       ImageView foodimage;
       TextView restname,resttype;
        public foodlocationViewHolder(View itemView) {
            super(itemView);
            this.foodview = itemView;
            this.foodimage = itemView.findViewById(R.id.restaurent_image);
            this.restname = itemView.findViewById(R.id.restaurent_name);
            this.resttype = itemView.findViewById(R.id.restaurent_type);
        }
        void Foodplace(FoodLocation foodLocation){
           restname.setText(foodLocation.getName());
           resttype.setText(foodLocation.getType());
           Picasso.get().load(foodLocation.getMainimage()).into(foodimage);
        }
    }
    public static class ExprViewHolder extends RecyclerView.ViewHolder{
        View myview;
        CircleImageView visitorprofile;
        TextView visitorname,visitorexpr;
        public ExprViewHolder(View itemView) {
            super(itemView);
            this.myview= itemView;
            this.visitorprofile = itemView.findViewById(R.id.visitor_profile);
            this.visitorname = itemView.findViewById(R.id.visitor_name);
            this.visitorexpr = itemView.findViewById(R.id.visitor_expr);
        }
    }
}