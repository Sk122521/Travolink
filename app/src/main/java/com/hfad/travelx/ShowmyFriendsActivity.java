package com.hfad.travelx;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ShowmyFriendsActivity extends AppCompatActivity {
    private RecyclerView FriendlistView;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference userref,friendref;
    private String CurrentUserid,profileuserid,profilername;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showmy_friends);
        Bundle bundle = getIntent().getExtras();
        profileuserid = bundle.getString("friend_id");
        profilername = bundle.getString("name");
        CurrentUserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        FriendlistView = (RecyclerView)findViewById(R.id.myfriend_list);
        FriendlistView.setLayoutManager(linearLayoutManager);
        friendref = FirebaseDatabase.getInstance().getReference().child("Friends").child(profileuserid);
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        toolbar = (Toolbar) findViewById(R.id.myfriend_toolbar);
        toolbar.setTitle("Friends of "+profilername);
        toolbar.setNavigationIcon(R.drawable.back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.like_and_seen.AnonymousClass1 */

            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        DisplayFriends();
    }

    private void DisplayFriends() {
        FirebaseRecyclerAdapter<likes,ShowFriends> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<likes, ShowFriends>(new FirebaseRecyclerOptions.Builder().setQuery(friendref,likes.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull ShowFriends holder, int position, @NonNull likes model) {
                holder.accept_friends.setVisibility(View.GONE);
                String uid = model.getUid();
                userref.child(uid).addValueEventListener(new ValueEventListener() {
                    /* class com.hfad.travelx.like_and_seen.AnonymousClass2.AnonymousClass1 */

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString().trim();
                        holder.setname(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
                        holder.setimage(dataSnapshot.child("image").getValue().toString().trim());
                    }

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                holder.name_friends.setOnClickListener(new View.OnClickListener() {
                    /* class com.hfad.travelx.like_and_seen.AnonymousClass2.AnonymousClass2 */

                    public void onClick(View view) {
                        Intent intent = new Intent(ShowmyFriendsActivity.this, ProfileActivity.class);
                        intent.putExtra("visit_user_id", uid);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ShowFriends onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new ShowFriends(LayoutInflater.from(parent.getContext()).inflate(R.layout.friendslist, parent, false));
            }
        };
        FriendlistView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }
    public static class ShowFriends extends RecyclerView.ViewHolder {
        Button accept_friends;
        View mview;
        TextView name_friends;

        public ShowFriends(View itemView) {
            super(itemView);
            this.mview = itemView;
            this.accept_friends = (Button) itemView.findViewById(R.id.Accept_friends);
            this.name_friends = (TextView) itemView.findViewById(R.id.name_friends);
        }

        public void setname(String name) {
            this.name_friends.setText(name);
        }

        public void setimage(String image) {
            CircleImageView image_user_like = (CircleImageView) this.mview.findViewById(R.id.image_friends);
            if (TextUtils.isEmpty(image)) {
                image_user_like.setImageResource(R.drawable.profile);
            } else {
                Picasso.get().load(image).into(image_user_like);
            }
        }
    }
}