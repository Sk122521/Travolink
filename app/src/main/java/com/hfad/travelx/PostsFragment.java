package com.hfad.travelx;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.PostMessageService;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
public class PostsFragment extends Fragment {
    private static String Currentuserid;
    private static DatabaseReference likesref,postref,seenref,userref;
    private View PostView;
    private FirebaseRecyclerAdapter<Posts, PostsViewHolder> firebaserecycleradapter;
    private LinearLayoutManager linearLayoutManager;
    private RecyclerView postlist;
    private ProgressBar progressBar;
    private FloatingActionButton message_fab;
    private Toolbar mtoolbar;
    private FirebaseAuth mauth;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final int TYPE_IMAGE = 1111;
    private static final int TYPE_VIDEO = 2222;
    private TextView navprofileuserview;
    private CircleImageView navprofileimage;
    private static final int DEST_ADDED = 3333;
    private static final int PHOTOS_POST= 6666;
    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View inflate = inflater.inflate(R.layout.fragment_posts, container, false);
        this.PostView = inflate;
        setHasOptionsMenu(true);
        mauth = FirebaseAuth.getInstance();
        Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        postref = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesref = FirebaseDatabase.getInstance().getReference().child("likes");
        seenref = FirebaseDatabase.getInstance().getReference().child("seen");
        mtoolbar = (Toolbar)PostView.findViewById(R.id.app_main_toolb);
        ((MainActivity)getActivity()).setSupportActionBar(mtoolbar);
        ((MainActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) PostView.findViewById(R.id.drawable_layout1);
        navigationView = (NavigationView) PostView.findViewById(R.id.navigation_view);
        ActionBarDrawerToggle actionBarDrawerToggle2 = new ActionBarDrawerToggle(getActivity(), this.drawerLayout, R.string.Drawer_open, R.string.Drawer_closed);
        actionBarDrawerToggle = actionBarDrawerToggle2;
        drawerLayout.addDrawerListener(actionBarDrawerToggle2);
        actionBarDrawerToggle.syncState();
        View navview = navigationView.inflateHeaderView(R.layout.nav_header);
        navprofileimage = (CircleImageView) navview.findViewById(R.id.profile_image);
        navprofileuserview = (TextView) navview.findViewById(R.id.nav_text);
        postlist= (RecyclerView) this.PostView.findViewById(R.id.all_user_post_list);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(getContext());
        this.linearLayoutManager = linearLayoutManager2;
        linearLayoutManager2.setReverseLayout(true);
        this.linearLayoutManager.setStackFromEnd(true);
        this.postlist.setLayoutManager(this.linearLayoutManager);
        this.progressBar = (ProgressBar) this.PostView.findViewById(R.id.progressBar);
        message_fab = (FloatingActionButton)PostView.findViewById(R.id.message_fab);
        DisplayAllUserPost(this.linearLayoutManager);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            /* class com.hfad.travelx.MainActivity.AnonymousClass2 */

            @Override
            // com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                usermenuselector(menuItem);
                return false;
            }
        });
        message_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(),MessageActivity.class));
            }
        });
        return this.PostView;
    }
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.options_menu,menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() != R.id.search) {
            if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                drawerLayout.closeDrawer(Gravity.LEFT);
            } else {
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        }else{
            startActivity(new Intent(getContext(), SearchableActivity.class));
        }
        return true;
    }
    private void usermenuselector(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.nav_Message:
                startActivity(new Intent(getContext(), MessageActivity.class));
                return;
            case R.id.nav_findfreinds:
                startActivity(new Intent(getContext(), SearchableActivity.class));
            case R.id.nav_host_fragment /*{ENCODED_INT: 2131362315}*/:
            case R.id.nav_host_fragment_container /*{ENCODED_INT: 2131362316}*/:
            default:
                return;
            case R.id.nav_feedback /*{ENCODED_INT: 2131362312}*/:
             //   startActivity(new Intent(getContext(), SplashActivity.class));
            userref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                 snapshot.getRef().child("hdrimg").setValue("");

                }

                @Override
                public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                }

                @Override
                public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

                }

                @Override
                public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
                return;
            case R.id.nav_help /*{ENCODED_INT: 2131362314}*/:
                startActivity(new Intent(getContext(),HelpActivity.class));
                return;
            case R.id.nav_logout /*{ENCODED_INT: 2131362317}*/:
                //  UpdateUserStatus("Offline");
                this.mauth.signOut();
                LoginManager.getInstance().logOut();
                SendUsertoLoginActivity();
                UpdatechatStatus("Chatoff");
                return;
            case R.id.nav_profile /*{ENCODED_INT: 2131362318}*/:
                Intent intent = new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("visit_user_id", this.Currentuserid);
                startActivity(intent);
                return;
            case R.id.nav_friendrequest:
                startActivity(new Intent(getContext(),MyFriendActivity.class));
                return;
            case R.id.nav_policy:
                Uri uri = Uri.parse("https://www.freeprivacypolicy.com/live/c9cb9b51-3e9a-4b02-8f0d-6629118a31eb"); // missing 'http://' will cause crashed
                Intent intent12 = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent12);
                return;
        }
    }
    private void DisplayAllUserPost(final LinearLayoutManager linearLayoutManager2) {
        Query query1 = postref.orderByChild("timestamp");
        FirebaseRecyclerAdapter r1 = new FirebaseRecyclerAdapter<Posts, RecyclerView.ViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(query1, Posts.class).build()) {
            @Override
            public int getItemViewType(int position) {
                if (((Posts)getItem(position)).getType().equals("dest")){
                    return DEST_ADDED;
                }else{
                    return PHOTOS_POST;
                }
            }
            public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position, Posts model) {
                final String PostKey = getRef(position).getKey();
                final String profileuserid = model.getUid();
                progressBar.setVisibility(View.GONE);
                if(model.getType().equals("dest")){
                    ((DestViewHolder)holder)
                    .setValues(model.getProfilename(),model.getProfileimage(),model.getAddressline(),model.getDateandtime(),model.getImages(),model.getDestname(),model.getYour_experience(),model.getFoodcult());
                     PostsFragment.postref.child(PostKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Comments").exists()) {
                                TextView textView = ((DestViewHolder) holder).comment_post_text;
                                textView.setText(Integer.toString((int) dataSnapshot.child("Comments").getChildrenCount()) + " comments");
                            }
                        }
                        @Override // com.google.firebase.database.ValueEventListener
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    ((DestViewHolder) holder).setlikeButtonstatus(PostKey);
                    ((DestViewHolder) holder).name.setOnClickListener(new View.OnClickListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass2 */

                        public void onClick(View view) {
                            Intent intent = new Intent(PostsFragment.this.getActivity(), ProfileActivity.class);
                            intent.putExtra("visit_user_id", profileuserid);
                            PostsFragment.this.startActivity(intent);
                        }
                    });
                    ((DestViewHolder) holder).no_of_likes_text.setOnClickListener(new View.OnClickListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass3 */

                        public void onClick(View view) {
                            Intent likeintent = new Intent(PostsFragment.this.getActivity(), like_and_seen.class);
                            likeintent.putExtra("post_id", PostKey);
                            PostsFragment.this.startActivity(likeintent);
                        }
                    });
                    ((DestViewHolder) holder).no_of_seen_text.setOnClickListener(new View.OnClickListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass4 */

                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PostsFragment.this.getContext());
                            builder.setTitle(R.string.seen_title);
                            builder.setMessage(R.string.seen_message);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            alertDialog.setCanceledOnTouchOutside(true);
                        }
                    });
                    ((DestViewHolder) holder).comment_post_button.setOnClickListener(new View.OnClickListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass5 */

                        public void onClick(View view) {
                            Intent intent = new Intent(PostsFragment.this.getContext(), CommentActivity.class);
                            intent.putExtra("post_id", PostKey);
                            intent.putExtra("from", "photo");
                            PostsFragment.this.startActivity(intent);
                        }
                    });
                    ((DestViewHolder) holder).like_button.setOnLikeListener(new OnLikeListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass6 */

                        @Override // com.like.OnLikeListener
                        public void liked(LikeButton likeButton) {
                            likeButton.setLiked(true);
                            ((DestViewHolder) holder).seen_button.setLiked(true);
                            PostsFragment.likesref.child(PostKey).child(PostsFragment.Currentuserid).child("uid").setValue(PostsFragment.Currentuserid);
                            PostsFragment.seenref.child(PostKey).child(PostsFragment.Currentuserid).setValue(true);
                        }

                        @Override // com.like.OnLikeListener
                        public void unLiked(LikeButton likeButton) {
                            likeButton.setLiked(false);
                            PostsFragment.likesref.child(PostKey).child(PostsFragment.Currentuserid).removeValue();
                        }
                    });
                    ((DestViewHolder) holder).seen_button.setOnLikeListener(new OnLikeListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass7 */

                        @Override // com.like.OnLikeListener
                        public void liked(LikeButton likeButton) {
                            likeButton.setLiked(true);
                            PostsFragment.seenref.child(PostKey).child(PostsFragment.Currentuserid).setValue(true);
                        }

                        @Override // com.like.OnLikeListener
                        public void unLiked(LikeButton likeButton) {
                            likeButton.setLiked(false);
                            ((DestViewHolder) holder).like_button.setLiked(false);
                            PostsFragment.seenref.child(PostKey).child(PostsFragment.Currentuserid).removeValue();
                            PostsFragment.likesref.child(PostKey).child(PostsFragment.Currentuserid).removeValue();
                        }
                    });
                    ((DestViewHolder)holder).lil.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getContext(),EachDestActivity.class);
                            intent.putExtra("postkey",PostKey);
                            intent.putExtra("photo",model.getImages());
                            startActivity(intent);
                        }
                    });

                }else{
                    ((PostsViewHolder) holder).setFullname(model.getProfilename());
                    ((PostsViewHolder) holder).setProfileimage(model.getProfileimage());
                    ((PostsViewHolder) holder).setDateandtime(model.getDateandtime());
                    ((PostsViewHolder) holder).setPostdescription(model.getPostdescription());
                    ((PostsViewHolder) holder).setpost(model.getImages(), model.getType(), PostsFragment.this.getContext());
                    ((PostsViewHolder) holder).setlikeButtonstatus(PostKey);
                    if (model.getAddressline().equals(null)) {
                        ((PostsViewHolder) holder).locationpost.setVisibility(View.GONE);
                    } else {
                        ((PostsViewHolder) holder).locationpost.setText(model.getAddressline());
                    }
                    PostsFragment.postref.child(PostKey).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Comments").exists()) {
                                TextView textView = ((PostsViewHolder) holder).comment_post_text;
                                textView.setText(Integer.toString((int) dataSnapshot.child("Comments").getChildrenCount()) + " comments");
                            }
                        }
                        @Override // com.google.firebase.database.ValueEventListener
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    ((PostsViewHolder) holder).username.setOnClickListener(new View.OnClickListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass2 */

                        public void onClick(View view) {
                            Intent intent = new Intent(PostsFragment.this.getActivity(), ProfileActivity.class);
                            intent.putExtra("visit_user_id", profileuserid);
                            PostsFragment.this.startActivity(intent);
                        }
                    });
                    ((PostsViewHolder) holder).no_of_likes_text.setOnClickListener(new View.OnClickListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass3 */

                        public void onClick(View view) {
                            Intent likeintent = new Intent(PostsFragment.this.getActivity(), like_and_seen.class);
                            likeintent.putExtra("post_id", PostKey);
                            PostsFragment.this.startActivity(likeintent);
                        }
                    });
                    ((PostsViewHolder) holder).no_of_seen_text.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(PostsFragment.this.getContext());
                            builder.setTitle(R.string.seen_title);
                            builder.setMessage(R.string.seen_message);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                            alertDialog.setCanceledOnTouchOutside(true);
                        }
                    });
                    ((PostsViewHolder) holder).comment_post_button.setOnClickListener(new View.OnClickListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass5 */

                        public void onClick(View view) {
                            Intent intent = new Intent(PostsFragment.this.getContext(), CommentActivity.class);
                            intent.putExtra("post_id", PostKey);
                            intent.putExtra("from", "photo");
                            PostsFragment.this.startActivity(intent);
                        }
                    });
                    ((PostsViewHolder) holder).like_button.setOnLikeListener(new OnLikeListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass6 */

                        @Override // com.like.OnLikeListener
                        public void liked(LikeButton likeButton) {
                            likeButton.setLiked(true);
                            ((PostsViewHolder) holder).seen_button.setLiked(true);
                            PostsFragment.likesref.child(PostKey).child(PostsFragment.Currentuserid).child("uid").setValue(PostsFragment.Currentuserid);
                            PostsFragment.seenref.child(PostKey).child(PostsFragment.Currentuserid).setValue(true);
                        }

                        @Override // com.like.OnLikeListener
                        public void unLiked(LikeButton likeButton) {
                            likeButton.setLiked(false);
                            PostsFragment.likesref.child(PostKey).child(PostsFragment.Currentuserid).removeValue();
                        }
                    });
                    ((PostsViewHolder) holder).seen_button.setOnLikeListener(new OnLikeListener() {
                        /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass7 */

                        @Override // com.like.OnLikeListener
                        public void liked(LikeButton likeButton) {
                            likeButton.setLiked(true);
                            PostsFragment.seenref.child(PostKey).child(PostsFragment.Currentuserid).setValue(true);
                        }

                        @Override // com.like.OnLikeListener
                        public void unLiked(LikeButton likeButton) {
                            likeButton.setLiked(false);
                            ((PostsViewHolder) holder).like_button.setLiked(false);
                            PostsFragment.seenref.child(PostKey).child(PostsFragment.Currentuserid).removeValue();
                            PostsFragment.likesref.child(PostKey).child(PostsFragment.Currentuserid).removeValue();
                        }
                    });
                }
            }
            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                if (viewType == DEST_ADDED){
                    return new DestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post2, parent, false));
                }else{
                    return new PostsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout, parent, false));
                }

            }

        };
        this.firebaserecycleradapter = r1;
        r1.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            /* class com.hfad.travelx.PostsFragment.AnonymousClass5 */

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = PostsFragment.this.firebaserecycleradapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager2.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= friendlyMessageCount - 1 && lastVisiblePosition == positionStart - 1)) {
                    PostsFragment.this.postlist.scrollToPosition(positionStart);
                }
            }
        });
        this.postlist.setAdapter(this.firebaserecycleradapter);
        this.firebaserecycleradapter.startListening();
//        this.postlist.getLayoutManager().smoothScrollToPosition(this.postlist, new RecyclerView.State(), this.postlist.getAdapter().getItemCount());
    }

    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        View mview;
        ImageButton comment_post_button;
        TextView comment_post_text;
        int count_likes;
        int count_seen;
        LikeButton like_button;
        TextView no_of_comment;
        TextView no_of_likes_text,no_of_seen_text;
        LikeButton seen_button;
        TextView username;
        TextView locationpost;
        RecyclerView recyclerView;

        public PostsViewHolder(View itemView) {
            super(itemView);
            this.mview = itemView;
            this.username = (TextView) itemView.findViewById(R.id.username_post);
            this.like_button = (LikeButton) itemView.findViewById(R.id.like_button);
            this.seen_button = (LikeButton) itemView.findViewById(R.id.seen_button);
            this.no_of_likes_text = (TextView) itemView.findViewById(R.id.like_text);
            this.no_of_seen_text = (TextView) itemView.findViewById(R.id.seen_text);
            this.comment_post_button = (ImageButton) itemView.findViewById(R.id.comment_post_button);
            this.comment_post_text = (TextView) itemView.findViewById(R.id.comment_post_text);
            locationpost = (TextView)itemView.findViewById(R.id.location_post);
            recyclerView = (RecyclerView)itemView.findViewById(R.id.images_rv);
        }

        public void setlikeButtonstatus(final String postkey) {
            PostsFragment.likesref.addValueEventListener(new ValueEventListener() {
                /* class com.hfad.travelx.PostsFragment.PostsViewHolder.AnonymousClass1 */

                @Override // com.google.firebase.database.ValueEventListener
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postkey).hasChild(PostsFragment.Currentuserid)) {
                        PostsViewHolder.this.count_likes = (int) dataSnapshot.child(postkey).getChildrenCount();
                        PostsViewHolder.this.like_button.setLiked(true);
                        TextView textView = PostsViewHolder.this.no_of_likes_text;
                        textView.setText(Integer.toString(PostsViewHolder.this.count_likes) + " likes");
                        return;
                    }
                    PostsViewHolder.this.count_likes = (int) dataSnapshot.child(postkey).getChildrenCount();
                    PostsViewHolder.this.like_button.setLiked(false);
                    TextView textView2 = PostsViewHolder.this.no_of_likes_text;
                    textView2.setText(Integer.toString(PostsViewHolder.this.count_likes) + " likes");
                }

                @Override // com.google.firebase.database.ValueEventListener
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            PostsFragment.seenref.addValueEventListener(new ValueEventListener() {
                /* class com.hfad.travelx.PostsFragment.PostsViewHolder.AnonymousClass2 */

                @Override // com.google.firebase.database.ValueEventListener
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postkey).hasChild(PostsFragment.Currentuserid)) {
                        PostsViewHolder.this.count_seen = (int) dataSnapshot.child(postkey).getChildrenCount();
                        PostsViewHolder.this.seen_button.setLiked(true);
                        TextView textView = PostsViewHolder.this.no_of_seen_text;
                        textView.setText(Integer.toString(PostsViewHolder.this.count_seen) + " seen");
                        return;
                    }
                    PostsViewHolder.this.count_seen = (int) dataSnapshot.child(postkey).getChildrenCount();
                    PostsViewHolder.this.seen_button.setLiked(false);
                    TextView textView2 = PostsViewHolder.this.no_of_seen_text;
                    textView2.setText(Integer.toString(PostsViewHolder.this.count_seen) + " seen");
                }

                @Override // com.google.firebase.database.ValueEventListener
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        public void setFullname(String fullname) {
            TextView textView = this.username;
            textView.setText(fullname.substring(0, 1).toUpperCase() + fullname.substring(1).toLowerCase());
        }
        public void setDateandtime(String Timeanddate) {
            ((TextView) this.mview.findViewById(R.id.time_post)).setText(Timeanddate);
        }
        public void setPostdescription(String description) {
            TextView postDescription = (TextView) this.mview.findViewById(R.id.description_post);
            if (description.isEmpty()) {
                postDescription.setVisibility(View.GONE);
            } else {
                postDescription.setText(description);
            }
        }
        public void setProfileimage(String profileimage) {
            CircleImageView Postprofileimage = (CircleImageView) this.mview.findViewById(R.id.profile_image_Post);
            if (profileimage.isEmpty()) {
                Postprofileimage.setImageResource(R.drawable.profile);
            } else {
                Picasso.get().load(profileimage).into(Postprofileimage);
            }
        }
        public void setpost(ArrayList<String> urllist, String type, Context context) {
            if (type.equals("photo")) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                recyclerView.setLayoutManager(layoutManager);
                SelectedImageAdapter selectedImageAdapter = new SelectedImageAdapter(recyclerView.getContext(),urllist);
                recyclerView.setAdapter(selectedImageAdapter);
            } else if (type.equals("nothing")) {
                recyclerView.setVisibility(View.GONE);
            }
        }
    }

    public static class DestViewHolder extends RecyclerView.ViewHolder {
        View mview;
        CircleImageView p;
        TextView time,name,locationpost,name_dest,add_dest,exp,foodcult,no_of_likes_text,comment_post_text,no_of_seen_text;
        ImageView topimg;
        LikeButton like_button;
        ImageButton comment_post_button;
        LikeButton seen_button;
        RecyclerView recyclerView;
        int count_likes,count_seen;
        private LinearLayout lil;

        public DestViewHolder(View itemView) {
            super(itemView);
            this.mview = itemView;
            this.name = (TextView) itemView.findViewById(R.id.username_post);
            p = (CircleImageView)itemView.findViewById(R.id.profile_image_Post);
            time = (TextView)itemView.findViewById(R.id.time_post);
            locationpost = (TextView) itemView.findViewById(R.id.location_post);
            topimg = (ImageView)itemView.findViewById(R.id.top_image);
            name_dest = (TextView)itemView.findViewById(R.id.name_dest);
            add_dest = (TextView)itemView.findViewById(R.id.address_dest);
            exp = (TextView)itemView.findViewById(R.id.experience);
            foodcult = (TextView)itemView.findViewById(R.id.foodcult);
            this.like_button = (LikeButton) itemView.findViewById(R.id.like_button);
            this.seen_button = (LikeButton) itemView.findViewById(R.id.seen_button);
            this.no_of_likes_text = (TextView) itemView.findViewById(R.id.like_text);
            this.no_of_seen_text = (TextView) itemView.findViewById(R.id.seen_text);
            this.comment_post_button = (ImageButton) itemView.findViewById(R.id.comment_post_button);
            this.comment_post_text = (TextView) itemView.findViewById(R.id.comment_post_text);
            recyclerView = (RecyclerView) itemView.findViewById(R.id.images_rv);
            lil = (LinearLayout)itemView.findViewById(R.id.Linear_Layout3);
        }
        public void setValues(String profilename, String profileimage, String addressline, String dateandtime, ArrayList<String> images, String destname, String your_experience, String foodCult) {
        name.setText(profilename);
        if (profileimage.isEmpty()){Picasso.get().load(R.drawable.profile).into(p);} else {Picasso.get().load(profileimage).into(p);}
        time.setText(dateandtime);
        locationpost.setText(addressline);
        if (images.isEmpty()){
            Picasso.get().load(R.drawable.nanital).into(topimg);
        }else{
            Picasso.get().load(images.get(0)).into(topimg);
        }
        name_dest.setText(destname.substring(0,1).toUpperCase()+destname.substring(1));
        add_dest.setText(addressline);
        exp.setText(your_experience);
        if (foodCult.isEmpty()){
            foodcult.setVisibility(View.GONE);
        }else{ foodcult.setText(foodCult);}
        }
        public void setlikeButtonstatus(final String postkey) {
           PostsFragment.likesref.addValueEventListener(new ValueEventListener() {
                /* class com.hfad.travelx.PostsFragment.PostsViewHolder.AnonymousClass1 */

                @Override // com.google.firebase.database.ValueEventListener
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postkey).hasChild(PostsFragment.Currentuserid)) {
                        count_likes = (int) dataSnapshot.child(postkey).getChildrenCount();
                        like_button.setLiked(true);
                        TextView textView = no_of_likes_text;
                        textView.setText(Integer.toString(count_likes) + " likes");
                        return;
                    }
                    count_likes = (int) dataSnapshot.child(postkey).getChildrenCount();
                    like_button.setLiked(false);
                    TextView textView2 = no_of_likes_text;
                    textView2.setText(Integer.toString(count_likes) + " likes");
                }

                @Override // com.google.firebase.database.ValueEventListener
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            PostsFragment.seenref.addValueEventListener(new ValueEventListener() {
                /* class com.hfad.travelx.PostsFragment.PostsViewHolder.AnonymousClass2 */

                @Override // com.google.firebase.database.ValueEventListener
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postkey).hasChild(PostsFragment.Currentuserid)) {
                        count_seen = (int) dataSnapshot.child(postkey).getChildrenCount();
                        seen_button.setLiked(true);
                        TextView textView = no_of_seen_text;
                        textView.setText(Integer.toString(count_seen) + " seen");
                        return;
                    }
                    count_seen = (int) dataSnapshot.child(postkey).getChildrenCount();
                    seen_button.setLiked(false);
                    TextView textView2 = no_of_seen_text;
                    textView2.setText(Integer.toString(count_seen) + " seen");
                }

                @Override // com.google.firebase.database.ValueEventListener
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
    @Override // androidx.fragment.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override // androidx.fragment.app.Fragment
    public void onStart() {
        super.onStart();
    }

    @Override // androidx.fragment.app.Fragment
    public void onDestroy() {
        super.onDestroy();
    }
    private void SendUsertoLoginActivity() {
        Intent loginintent = new Intent(getContext(), LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        getActivity().finish();
    }
    private void UpdatechatStatus(String state) {
        HashMap<String, Object> OnlineStateMap = new HashMap<>();
        OnlineStateMap.put("onlinestatus", state);
        FirebaseDatabase.getInstance().getReference().child("Users").child(Currentuserid).child("Chatstate").updateChildren(OnlineStateMap);
    }
}
