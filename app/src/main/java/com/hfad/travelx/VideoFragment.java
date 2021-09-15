package com.hfad.travelx;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.api.Distribution;
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

import org.jetbrains.annotations.NotNull;

public class VideoFragment extends Fragment {
  private ViewPager2 viewPager2;
  private DatabaseReference videoref;
    private MediaController mediaController;
    private LinearLayoutManager llm;
    private DatabaseReference ref;
    private String Currentuid;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v  = inflater.inflate(R.layout.fragment_video, container, false);
        ref = FirebaseDatabase.getInstance().getReference();
        Currentuid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        viewPager2 = v.findViewById(R.id.View_pager);
        ShowAllVideos();
        return v;
    }

    private void ShowAllVideos() {
        Query query1 = ref.child("VideoPost").orderByChild("timestamp");
        FirebaseRecyclerAdapter r1 = new FirebaseRecyclerAdapter<Videopostmodel,MyviewHolder>(new FirebaseRecyclerOptions.Builder().setQuery(query1,Videopostmodel.class).build()) {
            @Override
            protected void onBindViewHolder(@NonNull @NotNull MyviewHolder holder, int position, @NonNull @NotNull Videopostmodel model) {
                final String PostKey = getRef(position).getKey();
                String profileuserid = model.getUid();
              if (mediaController == null){
                    mediaController = new MediaController(getContext());
                    mediaController.setAnchorView(holder.videoView);
                }
                holder.pb.setVisibility(View.GONE);
                holder.videoView.setMediaController(mediaController);
                holder.videoView.setVideoPath(model.getPosturl());
                holder.videoView.start();
                holder.name.setText(model.getProfilename());
                holder.postdesc.setText(model.getPostdescription());
                if (model.getProfileimage().isEmpty()){
                    Picasso.get().load(R.drawable.profile).into(holder.proimage);
                }else{
                    Picasso.get().load(model.getProfileimage()).into(holder.proimage);
                }
                holder.postloctn.setText(model.getAddressline());
                ref.child("videolike").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.child(PostKey).child(Currentuid).exists()){ holder.likebtn.setLiked(true);}
                        else{ holder.likebtn.setLiked(false); }
                        Integer count = (int)snapshot.child(PostKey).getChildrenCount();
                        holder.no_likes.setText(Integer.toString(count));
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                ref.child("VideoPost").child(PostKey).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        if (snapshot.child("Comments").exists()){
                            holder.no_comments.setText(Integer.toString((int)snapshot.child("Comments").getChildrenCount()));
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
                holder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                holder.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mp.start();
                    }
                });
                holder.likebtn.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                      likeButton.setLiked(true);
                       ref.child("videolike").child(PostKey).child(Currentuid).child("uid").setValue(Currentuid);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                       likeButton.setLiked(false);
                       ref.child("videolike").child(PostKey).child(Currentuid).removeValue();
                    }
                });
                holder.commentbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), CommentActivity.class);
                        intent.putExtra("post_id", PostKey);
                        intent.putExtra("from","video");
                        startActivity(intent);
                    }
                });
                holder.name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ProfileActivity.class);
                        intent.putExtra("visit_user_id", profileuserid);
                        startActivity(intent);
                    }
                });

            }
            @Override
            public MyviewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                return new MyviewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.single_video_row, parent, false));

            }
        };
        viewPager2.setAdapter(r1);
        r1.startListening();
    }
}