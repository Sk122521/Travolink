package com.hfad.travelx;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.flaviofaria.kenburnsview.KenBurnsView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class YourPhotosFragment extends Fragment {
    private RecyclerView destrecyclerview;
    private View destview;
    private static DatabaseReference ref,postref,likesref,seenref;
    private static  String profileuserid,Currentuid;
    private LinearLayoutManager mlinearLayoutManager;
    @Override // androidx.fragment.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        destview = inflater.inflate(R.layout.fragment_your_photos, container, false);
        ProfileActivity activity = (ProfileActivity) getActivity();
        profileuserid = activity.getprofileuserid();
        Currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        ref = FirebaseDatabase.getInstance().getReference();
        postref = ref.child("Posts");
        likesref = ref.child("likes");
        seenref = ref.child("seen");
        destrecyclerview = (RecyclerView)destview.findViewById(R.id.dest_profile);
        mlinearLayoutManager = new LinearLayoutManager(getActivity(),RecyclerView.VERTICAL,false);
        destrecyclerview.setLayoutManager(mlinearLayoutManager);
        DisplayDestinations();
        return destview;
    }

    private void DisplayDestinations() {
        Query startAt = postref.orderByChild("filter").startAt(profileuserid+"_dest").endAt(profileuserid+"_dest");
        FirebaseRecyclerAdapter<Posts,DestViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Posts,DestViewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(startAt,Posts.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull DestViewHolder holder, int position, @NonNull Posts model) {
                final String PostKey = getRef(position).getKey();
                ((DestViewHolder)holder)
                        .setValues(model.getProfilename(),model.getProfileimage(),model.getAddressline(),model.getDateandtime(),model.getImages(),model.getDestname(),model.getYour_experience(),model.getFoodcult());
                postref.child(PostKey).addValueEventListener(new ValueEventListener() {
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
                ((DestViewHolder) holder).no_of_likes_text.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent likeintent = new Intent(getActivity(), like_and_seen.class);
                        likeintent.putExtra("post_id", PostKey);
                        startActivity(likeintent);
                    }
                });
                ((DestViewHolder) holder).no_of_seen_text.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle(R.string.seen_title);
                        builder.setMessage(R.string.seen_message);
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        alertDialog.setCanceledOnTouchOutside(true);
                    }
                });
                ((DestViewHolder) holder).comment_post_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), CommentActivity.class);
                        intent.putExtra("post_id", PostKey);
                        intent.putExtra("from", "photo");
                        startActivity(intent);
                    }
                });
                ((DestViewHolder) holder).like_button.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        likeButton.setLiked(true);
                        ((DestViewHolder) holder).seen_button.setLiked(true);
                        likesref.child(PostKey).child(Currentuid).child("uid").setValue(Currentuid);
                        seenref.child(PostKey).child(Currentuid).setValue(true);
                    }

                    @Override // com.like.OnLikeListener
                    public void unLiked(LikeButton likeButton) {
                        likeButton.setLiked(false);
                        likesref.child(PostKey).child(Currentuid).removeValue();
                    }
                });
                ((DestViewHolder) holder).seen_button.setOnLikeListener(new OnLikeListener() {
                    /* class com.hfad.travelx.PostsFragment.AnonymousClass4.AnonymousClass7 */

                    @Override // com.like.OnLikeListener
                    public void liked(LikeButton likeButton) {
                        likeButton.setLiked(true);
                        seenref.child(PostKey).child(Currentuid).setValue(true);
                    }

                    @Override // com.like.OnLikeListener
                    public void unLiked(LikeButton likeButton) {
                        likeButton.setLiked(false);
                        ((DestViewHolder) holder).like_button.setLiked(false);
                        seenref.child(PostKey).child(Currentuid).removeValue();
                        likesref.child(PostKey).child(Currentuid).removeValue();
                    }
                });
                ((DestViewHolder)holder).mview.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),EachDestActivity.class);
                        startActivity(intent);
                    }
                });
                holder.lil.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (profileuserid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            Intent intent = new Intent(getContext(),EachDestActivity.class);
                            intent.putExtra("postkey",PostKey);
                            intent.putExtra("photo",model.getImages());
                            startActivity(intent);
                            Toast.makeText(getActivity(), "press long to delete", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                holder.lil.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (profileuserid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
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
                                                }else{
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
            @NonNull
            @Override
            public DestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new DestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.post2,parent,false));
            }
        };
       destrecyclerview.setAdapter(firebaseRecyclerAdapter);
       firebaseRecyclerAdapter.startListening();
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
        private LinearLayout lil ;

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
            likesref.addValueEventListener(new ValueEventListener() {
                /* class com.hfad.travelx.PostsFragment.PostsViewHolder.AnonymousClass1 */

                @Override // com.google.firebase.database.ValueEventListener
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(postkey).hasChild(Currentuid)) {
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
                    if (dataSnapshot.child(postkey).hasChild(Currentuid)) {
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
}
