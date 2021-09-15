package com.hfad.travelx;

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
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFriendActivity extends AppCompatActivity {
   private RecyclerView FriendlistView;
   private LinearLayoutManager linearLayoutManager;
   private DatabaseReference friendreqref,userref,friendref,chatref;
   private String CurrentUserid;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_friend);
        FriendlistView = (RecyclerView)findViewById(R.id.friend_list);
        CurrentUserid  = FirebaseAuth.getInstance().getCurrentUser().getUid();
        linearLayoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        FriendlistView.setLayoutManager(linearLayoutManager);
        friendreqref = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        friendref = FirebaseDatabase.getInstance().getReference().child("Friends");
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        chatref = FirebaseDatabase.getInstance().getReference().child("Chats");
        toolbar = (Toolbar) findViewById(R.id.friend_toolbar);
        toolbar.setTitle("Friend requests");
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
        Query startat = friendreqref.child(CurrentUserid).orderByChild("request_state").startAt("received").endAt("received");
        FirebaseRecyclerAdapter<requests,ShowFriends> firebaserecycleradapter = new FirebaseRecyclerAdapter<requests,ShowFriends>(new FirebaseRecyclerOptions.Builder().setQuery(startat, requests.class).build()) {
            /* class com.hfad.travelx.like_and_seen.AnonymousClass2 */

            /* access modifiers changed from: protected */
            public void onBindViewHolder(final ShowFriends holder, int position, requests model) {
                //final String type = model.getRequesttype();
                final String uid = getRef(position).getKey().toString();
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
                        Intent intent = new Intent(MyFriendActivity.this, ProfileActivity.class);
                        intent.putExtra("visit_user_id", uid);
                        startActivity(intent);
                    }
                });
             holder.accept_friends.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                   //  holder.mview.setVisibility(View.GONE);
                  friendref.child(CurrentUserid).child(uid).child("uid").setValue(uid).addOnCompleteListener(new OnCompleteListener<Void>() {
                         /* class com.hfad.travelx.ProfileActivity.AnonymousClass8 */

                         @Override // com.google.android.gms.tasks.OnCompleteListener
                         public void onComplete(Task<Void> task) {
                             if (task.isSuccessful()) {
                                friendref.child(uid).child(CurrentUserid).child("uid").setValue(CurrentUserid).addOnCompleteListener(new OnCompleteListener<Void>() {
                                     /* class com.hfad.travelx.ProfileActivity.AnonymousClass8.AnonymousClass1 */

                                     @Override // com.google.android.gms.tasks.OnCompleteListener
                                     public void onComplete(Task<Void> task) {
                                         if (task.isSuccessful()) {
                                             friendreqref.child(CurrentUserid).child(uid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 /* class com.hfad.travelx.ProfileActivity.AnonymousClass8.AnonymousClass1.AnonymousClass1 */

                                                 @Override // com.google.android.gms.tasks.OnCompleteListener
                                                 public void onComplete(Task<Void> task) {
                                                     if (task.isSuccessful()) {
                                                         friendreqref.child(uid).child(CurrentUserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             /* class com.hfad.travelx.ProfileActivity.AnonymousClass8.AnonymousClass1.AnonymousClass1.AnonymousClass1 */

                                                             @Override // com.google.android.gms.tasks.OnCompleteListener
                                                             public void onComplete(Task<Void> task) {
                                                                 if (task.isSuccessful()) {
                                                                    chatref.child(CurrentUserid).child(uid).child("uid").setValue(uid);
                                                                    chatref.child(uid).child(CurrentUserid).child("uid").setValue(CurrentUserid);
                                                                 }
                                                             }
                                                         });
                                                     }
                                                 }
                                             });
                                         }
                                     }
                                 });
                             }
                         }
                     });
                 }
             });
            }
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public ShowFriends onCreateViewHolder(ViewGroup parent, int viewType) {
                return new ShowFriends(LayoutInflater.from(parent.getContext()).inflate(R.layout.friendslist, parent, false));
            }
        };
        this.FriendlistView.setAdapter(firebaserecycleradapter);
        firebaserecycleradapter.startListening();
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