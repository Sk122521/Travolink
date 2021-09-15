package com.hfad.travelx;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.NotificationCompat;
import androidx.viewpager.widget.ViewPager;
import com.google.android.gms.actions.SearchIntents;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;

public class ProfileActivity extends AppCompatActivity{
    private String Currentstate,followstate;
    private MaterialButton cancel_Request_button;
    private DatabaseReference chatref;
    private FirebaseUser currentuser;
    private String currentuserid;
    private DatabaseReference friendref,followingref,followerref;
    private DatabaseReference friendrequestref;
    private FirebaseAuth mauth;
    private ProfilepagerAdapter myprofilePagerAdapter;
    private String name;
    private TextView no_of_friends,no_of_destinations;
    private CircleImageView profileimage;
    private TabLayout profiletab;
    private ViewPager profiletabpager;
    private String profileuserid;
    private TextView userbio;
    private MaterialTextView userdob;
    private MaterialTextView userintrest;
    private AppCompatTextView username,Worktitle,useraddress,userwebsite,no_folors,no_floing,no_frnds;
    private DatabaseReference userref;
    private String website,Userimage;
    private ImageButton sendmessagebutton;
    private FloatingActionButton mainfab,messagefab,friendfab;
    private Boolean clicked = false;
    private Animation rotateopen,rotateclose,frombottom,tobottom;
    private ImageButton editprofilebutton;
    private AppCompatImageView Himgvw;
    private MaterialButton followbutton;
    private int no_YourFrnds,no_userfrnds,no_following,no_followers;

    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        profileuserid = getIntent().getExtras().get("visit_user_id").toString();
        this.Currentstate = "new";
        followstate = "notfollowing";
        rotateopen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateclose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        frombottom= AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        tobottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);
        FirebaseAuth instance = FirebaseAuth.getInstance();
        this.mauth = instance;
        FirebaseUser currentUser = instance.getCurrentUser();
        this.currentuser = currentUser;
        this.currentuserid = currentUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        this.userref = reference.child("Users");
        this.friendref = reference.child("Friends");
        this.friendrequestref = reference.child("Friend_request");
        this.chatref = reference.child("Chats");
        followerref = reference.child("Followers");
        followingref = reference.child("Following");
        this.profiletab = (TabLayout) findViewById(R.id.profile_tabs);
        this.profiletabpager = (ViewPager) findViewById(R.id.profile_tabs_pager);
        ProfilepagerAdapter profilepagerAdapter = new ProfilepagerAdapter(getSupportFragmentManager());
        this.myprofilePagerAdapter = profilepagerAdapter;
        this.profiletabpager.setAdapter(profilepagerAdapter);
        this.profiletab.setupWithViewPager(this.profiletabpager);
        editprofilebutton = (ImageButton) findViewById(R.id.edit_profile);
        mainfab = findViewById(R.id.main_fab);
        friendfab = findViewById(R.id.friend_fab);
        messagefab = findViewById(R.id.message_fab);
        followbutton = (MaterialButton) findViewById(R.id.follow_btn);
        if (this.profileuserid.equals(this.currentuserid)) {
            editprofilebutton.setVisibility(View.VISIBLE);
            mainfab.setVisibility(GONE);
            followbutton.setVisibility(GONE);
            editprofilebutton.setClickable(true);
            mainfab.setClickable(false);
            followbutton.setClickable(false);
            this.Currentstate = "Your_profile";
            followstate = "your_profile";
        }else{
            editprofilebutton.setVisibility(GONE);
            followbutton.setVisibility(View.VISIBLE);
            editprofilebutton.setClickable(false);
            followbutton.setClickable(true);
            this.friendrequestref.child(this.currentuserid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(ProfileActivity.this.profileuserid).exists()) {
                        String request_state = dataSnapshot.child(ProfileActivity.this.profileuserid).child("request_state").getValue().toString();
                        if (request_state.equals("sent")) {
                            friendfab.setImageResource(R.drawable.request_sent_icon);
                            ProfileActivity.this.Currentstate = "request_sent";
                        } else if (request_state.equals("received")) {
                            friendfab.setImageResource(R.drawable.request_recvd_icon);
                            ProfileActivity.this.Currentstate = "request_received";
                        }
                    } else {
                        ProfileActivity.this.friendref.child(ProfileActivity.this.currentuserid).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild(ProfileActivity.this.profileuserid)) {
                                    friendfab.setImageResource(R.drawable.are_friends_icon);
                                    ProfileActivity.this.Currentstate = "friends";
                                }
                            }
                            @Override // com.google.firebase.database.ValueEventListener
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
          followingref.child(currentuserid).addValueEventListener(new ValueEventListener() {
              @Override
              public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
               if(snapshot.hasChild(profileuserid)){
                  followbutton.setText("Following");
                  followstate = "following";
                 }
              }
              @Override
              public void onCancelled(@NonNull @NotNull DatabaseError error) {}});
        }
        Himgvw = (AppCompatImageView)findViewById(R.id.hdr_img);
        this.profileimage = (CircleImageView) findViewById(R.id.profile_circle);
        mainfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked = !clicked;
                onAddButtonClicked();
            }
        });
        this.username = (AppCompatTextView) findViewById(R.id.name_user);
        Worktitle = (AppCompatTextView) findViewById(R.id.job_User);
        this.userbio = (AppCompatTextView) findViewById(R.id.bio_user);
        this.useraddress = (AppCompatTextView) findViewById(R.id.User_location);
        this.userwebsite = (AppCompatTextView) findViewById(R.id.links);
        no_frnds = (AppCompatTextView)findViewById(R.id.no_of_friends);
        no_floing = (AppCompatTextView) findViewById(R.id.no_following);
        no_folors = (AppCompatTextView) findViewById(R.id.no_followers);
        this.userref.child(profileuserid).addValueEventListener(new ValueEventListener() {
           @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                String Addrsess = dataSnapshot.child("address").getValue().toString();
                String bio = dataSnapshot.child("bio").getValue().toString();
                website = dataSnapshot.child("website").getValue().toString();
                Userimage = dataSnapshot.child("image").getValue().toString();
                String headerimg  = dataSnapshot.child("hdrimg").getValue().toString();
                String job = dataSnapshot.child("status").getValue().toString();
                username.setText(ProfileActivity.this.name.substring(0, 1).toUpperCase() + ProfileActivity.this.name.substring(1));
                if (!bio.isEmpty()) {
                    userbio.setVisibility(View.VISIBLE);
                    ProfileActivity.this.userbio.setText(bio);
                }
                if (!TextUtils.isEmpty(Addrsess)) {
                    useraddress.setVisibility(View.VISIBLE);
                    useraddress.setText(" "+Addrsess.substring(0, 1).toUpperCase() + Addrsess.substring(1).toLowerCase());
                   // ProfileActivity.this.userwebsite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.locationicon, 0, 0, 0);
                }
                if (!TextUtils.isEmpty(ProfileActivity.this.website)) {
                    userwebsite.setVisibility(View.VISIBLE);
                    userwebsite.setText("   " + ProfileActivity.this.website);
                  //  ProfileActivity.this.userwebsite.setCompoundDrawablesWithIntrinsicBounds(R.drawable.link_icon, 0, 0, 0);
                }
               if (!job.isEmpty()) {
                   Worktitle.setVisibility(View.VISIBLE);
                   Worktitle.setText(job);
               }
                if (Userimage.isEmpty()) {
                    ProfileActivity.this.profileimage.setImageResource(R.drawable.profile);
                } else {
                    Picasso.get().load(Userimage).into(ProfileActivity.this.profileimage);
                }
                if (headerimg.isEmpty()){
                    Himgvw.setImageResource(R.drawable.nanital);
                }else{
                    Picasso.get().load(headerimg).into(Himgvw);
                }
            }

            @Override // com.google.firebase.database.ValueEventListener
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        this.friendref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(currentuserid).exists()) {
                    no_YourFrnds = (int) dataSnapshot.child(currentuserid).getChildrenCount();
                }
                else {
                    no_YourFrnds = 0;
                }
                if (dataSnapshot.child(ProfileActivity.this.profileuserid).exists()) {
                    no_userfrnds = (int) dataSnapshot.child(profileuserid).getChildrenCount();
                    no_frnds.setText(Integer.toString(no_userfrnds));
                } else {
                    no_userfrnds = 0;
                    ProfileActivity.this.no_frnds.setText("0");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
      /*  userref.child(profileuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("dest_addded").exists()){
                    no_of_destinations.setText(Integer.toString((int)snapshot.child("dest_addded").getChildrenCount()));
                }else{
                    no_of_destinations.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });*/
        followingref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.child(profileuserid).exists()) {
                    no_following = (int) snapshot.child(profileuserid).getChildrenCount();
                    no_floing.setText(Integer.toString(no_following));
                } else {
                    no_following = 0;
                    ProfileActivity.this.no_floing.setText("0");
                }
            }@Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}});
        followerref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.child(profileuserid).exists()){
                    no_followers = (int)snapshot.child(profileuserid).getChildrenCount();
                    no_folors.setText(Integer.toString(no_followers));
                }else{
                    no_followers = 0;
                    no_folors.setText("0");
                }
            }
            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {}});
        this.userwebsite.setOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.ProfileActivity.AnonymousClass5 */

            public void onClick(View view) {
                String url = ProfileActivity.this.website;
                Intent intent = new Intent("android.intent.action.WEB_SEARCH");
                intent.putExtra(SearchIntents.EXTRA_QUERY, url);
                if (intent.resolveActivity(ProfileActivity.this.getPackageManager()) != null) {
                    ProfileActivity.this.startActivity(intent);
                }
            }
        });
    }
    private void onAddButtonClicked() {
        setVisibilities(clicked);
        //setAnimation(clicked);
    }
    private void setVisibilities(Boolean clicked) {
        if (clicked){
               messagefab.setVisibility(View.VISIBLE);
               friendfab.setVisibility(View.VISIBLE);
        }else{
            messagefab.setVisibility(View.INVISIBLE);
            friendfab.setVisibility(View.INVISIBLE);
        }
    }
    private void setAnimation(Boolean clicked) {
        if (clicked){
            messagefab.setClickable(true);
            friendfab.setClickable(true);
            messagefab.startAnimation(frombottom);
            friendfab.startAnimation(frombottom);
            mainfab.startAnimation(rotateopen);
        }else{
            messagefab.setClickable(false);
            friendfab.setClickable(false);
           messagefab.startAnimation(tobottom);
           friendfab.startAnimation(tobottom);
           mainfab.startAnimation(rotateclose);
        }
    }
    public void EditProfile(View v){
        if (ProfileActivity.this.profileuserid.equals(ProfileActivity.this.currentuserid)) {
        ProfileActivity.this.startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class));
        }}
    public void SendMessage(View v){
        ProfileActivity.this.chatref.child(ProfileActivity.this.currentuserid).child(ProfileActivity.this.profileuserid).child("uid").setValue(ProfileActivity.this.profileuserid);
        ProfileActivity.this.chatref.child(ProfileActivity.this.profileuserid).child(ProfileActivity.this.currentuserid).child("uid").setValue(ProfileActivity.this.currentuserid);
        Intent chatintent = new Intent(ProfileActivity.this,ChatActivity.class);
        chatintent.putExtra("chat_user_id", profileuserid);
        chatintent.putExtra("chat_name", name);
        chatintent.putExtra("chat_image", Userimage);
        startActivity(chatintent);
    }
    public void SendRequest(View v){

            if (ProfileActivity.this.Currentstate.equals("new")) {
                ProfileActivity.this.SendFriendRequest();
            } else if (ProfileActivity.this.Currentstate.equals("request_sent")) {
                ProfileActivity.this.CancelFriendRequest();
            } else if (ProfileActivity.this.Currentstate.equals("request_received")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Friend Request received");
                builder.setMessage("Friends limit is only up to 101 for each user. Hence, Accept request of your close friends only...");
                builder.setNegativeButton("Cancel Request", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ProfileActivity.this.friendrequestref.child(ProfileActivity.this.currentuserid).child(ProfileActivity.this.profileuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                    ProfileActivity.this.friendrequestref.child(ProfileActivity.this.profileuserid).child(ProfileActivity.this.currentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                ProfileActivity.this.Currentstate = "new";
                                                friendfab.setImageResource(R.drawable.sent_request_icon);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
                builder.setPositiveButton("Accept Request", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                     if (no_userfrnds < 101 && no_YourFrnds < 101 ){
                         ProfileActivity.this.AcceptFriendRequest();
                     }else{
                         Toast.makeText(ProfileActivity.this, "Sorry! limit of 101 friends has reached from either of any of one side", Toast.LENGTH_LONG).show();
                     }
                    }
                });
                AlertDialog alert = builder.create();
                alert.setCanceledOnTouchOutside(true);
                alert.show();
            } else if (ProfileActivity.this.Currentstate.equals("friends")) {
                ProfileActivity.this.UndoFriendship();
            }
    }
    public void FollowUser(View v){
        if (followstate.equals("notfollowing")){
            followingref.child(currentuserid).child(profileuserid).child("state").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    followerref.child(profileuserid).child(currentuserid).child("state").setValue("true").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ProfileActivity.this, "Account followed", Toast.LENGTH_SHORT).show();
                                followbutton.setText("following");
                                followstate = "following";
                            }else{
                                String message = task.getException().getMessage();
                                Toast.makeText(ProfileActivity.this,"Error : "+message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }else if (followstate.equals("following")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you want to unfollow this account....");
            builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                 followingref.child(currentuserid).child(profileuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull @NotNull Task<Void> task) {
                         followerref.child(profileuserid).child(currentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull @NotNull Task<Void> task) {
                                 if (task.isSuccessful()){
                                     followbutton.setText("+ Follow");
                                     followstate = "notfollowing";
                                     Toast.makeText(ProfileActivity.this, "Account unfollowed", Toast.LENGTH_SHORT).show();
                                 }else{
                                     String msg  = task.getException().getMessage();
                                     Toast.makeText(ProfileActivity.this, msg, Toast.LENGTH_SHORT).show();
                                 }
                             }
                         });
                     }
                 });
                }
            });
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(true);
            alert.show();
        }
    }
    private void UndoFriendship() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Undo Friendship");
        builder.setMessage("Do you really want to cancel friendship with " + this.name);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.friendref.child(ProfileActivity.this.currentuserid).child(ProfileActivity.this.profileuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            ProfileActivity.this.friendref.child(ProfileActivity.this.profileuserid).child(ProfileActivity.this.currentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        ProfileActivity.this.Currentstate = "new";
                                        friendfab.setImageResource(R.drawable.sent_request_icon);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }
    private void AcceptFriendRequest() {
        this.friendref.child(this.currentuserid).child(this.profileuserid).child("uid").setValue(this.profileuserid).addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    ProfileActivity.this.friendref.child(ProfileActivity.this.profileuserid).child(ProfileActivity.this.currentuserid).child("uid").setValue(ProfileActivity.this.currentuserid).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                ProfileActivity.this.friendrequestref.child(ProfileActivity.this.currentuserid).child(ProfileActivity.this.profileuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                  @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            ProfileActivity.this.friendrequestref.child(ProfileActivity.this.profileuserid).child(ProfileActivity.this.currentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                                public void onComplete(Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        ProfileActivity.this.chatref.child(ProfileActivity.this.currentuserid).child(ProfileActivity.this.profileuserid).child("uid").setValue(ProfileActivity.this.profileuserid);
                                                        ProfileActivity.this.chatref.child(ProfileActivity.this.profileuserid).child(ProfileActivity.this.currentuserid).child("uid").setValue(ProfileActivity.this.currentuserid);
                                                        ProfileActivity.this.Currentstate = "friends";
                                                        friendfab.setImageResource(R.drawable.are_friends_icon);
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
    private void CancelFriendRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.friend_req2);
        builder.setMessage("Do you really want to cancel friend request");
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                ProfileActivity.this.friendrequestref.child(ProfileActivity.this.currentuserid).child(ProfileActivity.this.profileuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if (task.isSuccessful()) {
                            ProfileActivity.this.friendrequestref.child(ProfileActivity.this.profileuserid).child(ProfileActivity.this.currentuserid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        ProfileActivity.this.Currentstate = "new";
                                        friendfab.setImageResource(R.drawable.sent_request_icon);
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
        alert.setCanceledOnTouchOutside(true);
    }
    private void SendFriendRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Your Travel partners");
        builder.setMessage("Friends limit is only up to 101 for each user. Send request to your friends with whom you travelled and do wish to travel....");
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                if (no_YourFrnds < 101 && no_userfrnds < 101){
                    friendrequestref.child(currentuserid).child(profileuserid).child("request_state").setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            if (task.isSuccessful()) {
                                ProfileActivity.this.friendrequestref.child(ProfileActivity.this.profileuserid).child(ProfileActivity.this.currentuserid).child("request_state").setValue("received").addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            friendfab.setImageResource(R.drawable.request_sent_icon);
                                            ProfileActivity.this.Currentstate = "request_sent";
                                            return;
                                        }
                                        String message = task.getException().getMessage();
                                        ProfileActivity profileActivity = ProfileActivity.this;
                                        Toast.makeText(profileActivity, "ERROR :" + message, Toast.LENGTH_LONG).show();
                                    }
                                });
                                return;
                            }
                            String message = task.getException().getMessage();
                            ProfileActivity profileActivity = ProfileActivity.this;
                            Toast.makeText(profileActivity, "ERROR :" + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(ProfileActivity.this, "Sorry! limit of 101 friends has reached from either of any of one side", Toast.LENGTH_LONG).show();
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(true);
        alert.show();
    }
    public void UserFrnds(View v) {
        Intent intent = new Intent(ProfileActivity.this, MyFriendActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putString("friend_id", this.profileuserid);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public String getprofileuserid() {
        return profileuserid;
    }
    public void UserFollower(View v){
        Intent intent = new Intent(ProfileActivity.this, MyFriendActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putString("friend_id", this.profileuserid);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    public void UserFollows(View v){
        Intent intent = new Intent(ProfileActivity.this, MyFriendActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name",name);
        bundle.putString("friend_id", this.profileuserid);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
