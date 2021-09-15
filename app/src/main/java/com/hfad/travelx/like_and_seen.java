package com.hfad.travelx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class like_and_seen extends AppCompatActivity {
    private String Currentstate;
    private String current_user_id;
    private DatabaseReference friendref;
    private DatabaseReference friendreqref;
    private RecyclerView like_seen_list;
    private DatabaseReference likesref;
    private Toolbar liketoolbar;
    private String postid;
    private DatabaseReference userref;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_like_and_seen);
        this.postid = getIntent().getExtras().get("post_id").toString();
        this.userref = FirebaseDatabase.getInstance().getReference().child("Users");
        this.likesref = FirebaseDatabase.getInstance().getReference().child("likes").child(this.postid);
        this.current_user_id = FirebaseAuth.getInstance().getCurrentUser().getUid().toString();
        this.friendref = FirebaseDatabase.getInstance().getReference().child("Friends");
        this.friendreqref = FirebaseDatabase.getInstance().getReference().child("Friend_request");
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.like_seen_list);
        this.like_seen_list = recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Toolbar toolbar = (Toolbar) findViewById(R.id.liked_toolbar);
        this.liketoolbar = toolbar;
        toolbar.setTitle("Liked by");
        this.liketoolbar.setNavigationIcon(R.drawable.back);
        this.liketoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.like_and_seen.AnonymousClass1 */

            public void onClick(View v) {
                like_and_seen.this.startActivity(new Intent(like_and_seen.this.getApplicationContext(),MainActivity.class));
            }
        });
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        super.onStart();
        Displaylikes();
    }

    private void Displaylikes() {
        FirebaseRecyclerAdapter<likes, likeviewholder> firebaserecycleradapter = new FirebaseRecyclerAdapter<likes, likeviewholder>(new FirebaseRecyclerOptions.Builder().setQuery(this.likesref, likes.class).build()) {
            /* class com.hfad.travelx.like_and_seen.AnonymousClass2 */

            /* access modifiers changed from: protected */
            public void onBindViewHolder(final likeviewholder holder, int position, likes model) {
                final String uid = model.getUid();
                like_and_seen.this.userref.child(uid).addValueEventListener(new ValueEventListener() {
                    /* class com.hfad.travelx.like_and_seen.AnonymousClass2.AnonymousClass1 */

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name = dataSnapshot.child("name").getValue().toString().trim();
                        likeviewholder likeviewholder = holder;
                        likeviewholder.setname(name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase());
                        holder.setimage(dataSnapshot.child("image").getValue().toString().trim());
                    }

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                holder.name_user_like.setOnClickListener(new View.OnClickListener() {
                    /* class com.hfad.travelx.like_and_seen.AnonymousClass2.AnonymousClass2 */

                    public void onClick(View view) {
                        Intent intent = new Intent(like_and_seen.this, ProfileActivity.class);
                        intent.putExtra("visit_user_id", uid);
                        like_and_seen.this.startActivity(intent);
                    }
                });
                like_and_seen.this.friendref.addValueEventListener(new ValueEventListener() {
                    /* class com.hfad.travelx.like_and_seen.AnonymousClass2.AnonymousClass3 */

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        like_and_seen.this.friendreqref.addValueEventListener(new ValueEventListener() {
                            /* class com.hfad.travelx.like_and_seen.AnonymousClass2.AnonymousClass3.AnonymousClass1 */

                            @Override // com.google.firebase.database.ValueEventListener
                            public void onDataChange(DataSnapshot dataSnapshot1) {
                                if (dataSnapshot.child(like_and_seen.this.current_user_id).child(uid).exists() || dataSnapshot1.child(like_and_seen.this.current_user_id).child(uid).exists()) {
                                    holder.freind_user_like.setVisibility(View.INVISIBLE);
                                } else if (!uid.equals(like_and_seen.this.current_user_id)) {
                                    holder.freind_user_like.setVisibility(View.VISIBLE);
                                    holder.freind_user_like.setOnClickListener(new View.OnClickListener() {
                                        /* class com.hfad.travelx.like_and_seen.AnonymousClass2.AnonymousClass3.AnonymousClass1.AnonymousClass1 */

                                        public void onClick(View view) {
                                            like_and_seen.this.friendreqref.child(like_and_seen.this.current_user_id).child(uid).child("request_state").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                /* class com.hfad.travelx.like_and_seen.AnonymousClass2.AnonymousClass3.AnonymousClass1.AnonymousClass1.AnonymousClass1 */

                                                @Override // com.google.android.gms.tasks.OnCompleteListener
                                                public void onComplete(Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        like_and_seen.this.friendreqref.child(uid).child(like_and_seen.this.current_user_id).child("request_state").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            /* class com.hfad.travelx.like_and_seen.AnonymousClass2.AnonymousClass3.AnonymousClass1.AnonymousClass1.AnonymousClass1.AnonymousClass1 */

                                                            @Override // com.google.android.gms.tasks.OnCompleteListener
                                                            public void onComplete(Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(like_and_seen.this, "Friend Request Sent", Toast.LENGTH_LONG).show();
                                                                    holder.freind_user_like.setVisibility(View.INVISIBLE);
                                                                }
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            }

                            @Override // com.google.firebase.database.ValueEventListener
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public likeviewholder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new likeviewholder(LayoutInflater.from(parent.getContext()).inflate(R.layout.likeandseenby, parent, false));
            }
        };
        this.like_seen_list.setAdapter(firebaserecycleradapter);
        firebaserecycleradapter.startListening();
    }

    public static class likeviewholder extends RecyclerView.ViewHolder {
        Button freind_user_like;
        View mview;
        TextView name_user_like;

        public likeviewholder(View itemView) {
            super(itemView);
            this.mview = itemView;
            this.freind_user_like = (Button) itemView.findViewById(R.id.freind_user_like);
            this.name_user_like = (TextView) itemView.findViewById(R.id.name_user_like);
        }

        public void setname(String name) {
            this.name_user_like.setText(name);
        }

        public void setimage(String image) {
            CircleImageView image_user_like = (CircleImageView) this.mview.findViewById(R.id.image_user_like);
            if (TextUtils.isEmpty(image)) {
                image_user_like.setImageResource(R.drawable.profile);
            } else {
                Picasso.get().load(image).into(image_user_like);
            }
        }
    }
}
