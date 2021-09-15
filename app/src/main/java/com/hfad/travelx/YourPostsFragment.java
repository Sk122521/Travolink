package com.hfad.travelx;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class YourPostsFragment extends Fragment {
    private static String Currentuserid,profileuserid;
    private static DatabaseReference likesref,userref,postref,seenref;
    private FirebaseRecyclerAdapter<Posts, PostsFragment.PostsViewHolder> firebaserecycleradapter;
    private LinearLayoutManager linearLayoutManager;
    private View postview;
    private RecyclerView mypostlist;
    private ProgressBar progressBar;
    @Override // androidx.fragment.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        postview = inflater.inflate(R.layout.fragment_your_posts, container, false);
        ProfileActivity activity = (ProfileActivity) getActivity();
        profileuserid = activity.getprofileuserid();
        Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userref = FirebaseDatabase.getInstance().getReference().child("Users");
        postref = FirebaseDatabase.getInstance().getReference().child("Posts");
        likesref = FirebaseDatabase.getInstance().getReference().child("likes");
        seenref = FirebaseDatabase.getInstance().getReference().child("seen");
        mypostlist = (RecyclerView) this.postview.findViewById(R.id.post_profile);
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mypostlist.setLayoutManager(linearLayoutManager);
        this.progressBar = (ProgressBar) postview.findViewById(R.id.progressbar_profile);
        DisplaymyPost(this.linearLayoutManager);
        return postview;
    }

    private void DisplaymyPost(LinearLayoutManager linearLayoutManager) {
        Query startAt = postref.orderByChild("filter").startAt(profileuserid+"_photo").endAt(profileuserid+"_photo");
        FirebaseRecyclerAdapter r1 = new FirebaseRecyclerAdapter<Posts,PostsViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(startAt,Posts.class).build()) {
            @Override
            public void onBindViewHolder(final PostsViewHolder holder, int position, Posts model) {
                progressBar.setVisibility(View.GONE);
                final String PostKey = getRef(position).getKey();
                holder.setFullname(model.getProfilename());
                holder.setProfileimage(model.getProfileimage());
                holder.setDateandtime(model.getDateandtime());
                holder.setPostdescription(model.getPostdescription());
                holder.setpost(model.getImages(), model.getType(), getContext());
                holder.setlikeButtonstatus(PostKey);
                if (model.getAddressline().equals(null)) {
                    holder.locationpost.setVisibility(View.GONE);
                } else {
                    holder.locationpost.setText(model.getAddressline());
                }
                postref.child(PostKey).addValueEventListener(new ValueEventListener() {
                    /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass1 */

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child("Comments").exists()) {
                            TextView textView = holder.comment_post_text;
                            textView.setText(Integer.toString((int) dataSnapshot.child("Comments").getChildrenCount()) + " comments");
                        }
                    }

                    @Override // com.google.firebase.database.ValueEventListener
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                holder.username.setOnClickListener(new View.OnClickListener() {
                    /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass2 */

                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        intent.putExtra("visit_user_id", profileuserid);
                        startActivity(intent);
                    }
                });
                holder.no_of_likes_text.setOnClickListener(new View.OnClickListener() {
                    /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass3 */

                    public void onClick(View view) {
                        Intent likeintent = new Intent(getActivity(), like_and_seen.class);
                        likeintent.putExtra("post_id", PostKey);
                        startActivity(likeintent);
                    }
                });
                holder.no_of_seen_text.setOnClickListener(new View.OnClickListener() {
                    /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass4 */

                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.seen_title);
                        builder.setMessage(R.string.seen_message);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.setCanceledOnTouchOutside(true);
                    }
                });
                holder.comment_post_button.setOnClickListener(new View.OnClickListener() {
                    /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass5 */

                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CommentActivity.class);
                        intent.putExtra("post_id", PostKey);
                        startActivity(intent);
                    }
                });
                holder.like_button.setOnLikeListener(new OnLikeListener() {
                    /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass6 */

                    @Override // com.like.OnLikeListener
                    public void liked(LikeButton likeButton) {
                        likeButton.setLiked(true);
                        holder.seen_button.setLiked(true);
                        likesref.child(PostKey).child(Currentuserid).child("uid").setValue(Currentuserid);
                        seenref.child(PostKey).child(Currentuserid).setValue(true);
                    }

                    @Override // com.like.OnLikeListener
                    public void unLiked(LikeButton likeButton) {
                        likeButton.setLiked(false);
                        likesref.child(PostKey).child(Currentuserid).removeValue();
                    }
                });
                holder.seen_button.setOnLikeListener(new OnLikeListener() {
                    /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass7 */

                    @Override // com.like.OnLikeListener
                    public void liked(LikeButton likeButton) {
                        likeButton.setLiked(true);
                        seenref.child(PostKey).child(Currentuserid).setValue(true);
                    }

                    @Override // com.like.OnLikeListener
                    public void unLiked(LikeButton likeButton) {
                        likeButton.setLiked(false);
                        holder.like_button.setLiked(false);
                        seenref.child(PostKey).child(Currentuserid).removeValue();
                        likesref.child(PostKey).child(Currentuserid).removeValue();
                    }
                });
                holder.mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (profileuserid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            Toast.makeText(getActivity(), "press long to delete", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                holder.mview.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (profileuserid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setCancelable(false);
                            builder.setTitle("Delete this Post");
                            builder.setItems(new CharSequence[]{"delete", "cancel"}, new DialogInterface.OnClickListener() {
                                /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass5.AnonymousClass1 */

                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (i == 0) {
                                        postref.child(PostKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            /* class com.hfad.travelx.ChatActivity.AnonymousClass8.AnonymousClass5.AnonymousClass1.AnonymousClass1 */

                                            @Override
                                            // com.google.android.gms.tasks.OnCompleteListener
                                            public void onComplete(Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Post Deleted", Toast.LENGTH_SHORT).show();
                                                    return;
                                                } else {
                                                    String exception = task.getException().getMessage();
                                                    Toast.makeText(getActivity(), "ERROR : " + exception, Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }
                                    if (i == 1) {
                                        dialogInterface.cancel();
                                    }
                                }
                            });
                            builder.show();
                            return true;
                        }
                        return true;
                    }
                });
        }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public PostsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                        return new PostsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.all_post_layout, parent, false));
                }

        };
        this.firebaserecycleradapter = r1;
        r1.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            /* class com.hfad.travelx.PostsFragment.AnonymousClass5 */

            @Override // androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = firebaserecycleradapter.getItemCount();
                int lastVisiblePosition = linearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition == -1 || (positionStart >= friendlyMessageCount - 1 && lastVisiblePosition == positionStart - 1)) {
                    mypostlist.scrollToPosition(positionStart);
                }
            }
        });
        mypostlist.setAdapter(this.firebaserecycleradapter);
        this.firebaserecycleradapter.startListening();
    //    mypostlist.getLayoutManager().smoothScrollToPosition(mypostlist, new RecyclerView.State(), mypostlist.getAdapter().getItemCount());
    }
    public static class PostsViewHolder extends RecyclerView.ViewHolder {
        ImageButton comment_post_button;
        TextView comment_post_text;
        int count_likes;
        int count_seen;
        LikeButton like_button;
        View mview;
        TextView no_of_comment;
        TextView no_of_likes_text;
        TextView no_of_seen_text;
        LikeButton seen_button;
        TextView username;
        TextView locationpost;
        RecyclerView recyclerView;

        public PostsViewHolder(View itemView) {
            super(itemView);
            this.mview = itemView;
            recyclerView = (RecyclerView)itemView.findViewById(R.id.images_rv);
            this.username = (TextView) itemView.findViewById(R.id.username_post);
            this.like_button = (LikeButton) itemView.findViewById(R.id.like_button);
            this.seen_button = (LikeButton) itemView.findViewById(R.id.seen_button);
            this.no_of_likes_text = (TextView) itemView.findViewById(R.id.like_text);
            this.no_of_seen_text = (TextView) itemView.findViewById(R.id.seen_text);
            this.comment_post_button = (ImageButton) itemView.findViewById(R.id.comment_post_button);
            this.comment_post_text = (TextView) itemView.findViewById(R.id.comment_post_text);
            locationpost = (TextView)itemView.findViewById(R.id.location_post);
        }

        public void setlikeButtonstatus(final String postkey) {
            likesref.addValueEventListener(new ValueEventListener() {
                /* class com.hfad.travelx.PostsFragment.PostsViewHolder.AnonymousClass1 */

                @Override // com.google.firebase.database.ValueEventListener
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postkey).hasChild(Currentuserid)) {
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
            seenref.addValueEventListener(new ValueEventListener() {
                /* class com.hfad.travelx.PostsFragment.PostsViewHolder.AnonymousClass2 */

                @Override // com.google.firebase.database.ValueEventListener
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postkey).hasChild(Currentuserid)) {
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

        public void setpost(ArrayList<String> urllist, String type, Context ctx) {
             if (type.equals("photo")) {
                 LinearLayoutManager layoutManager = new LinearLayoutManager(recyclerView.getContext(), LinearLayoutManager.HORIZONTAL, false);
                 recyclerView.setLayoutManager(layoutManager);
                 SelectedImageAdapter selectedImageAdapter = new SelectedImageAdapter(recyclerView.getContext(),urllist);
                 recyclerView.setAdapter(selectedImageAdapter);
                 recyclerView.setAdapter(selectedImageAdapter);
            } else if (type.equals("nothing")) {
                 recyclerView.setVisibility(View.GONE);
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
    }
}

