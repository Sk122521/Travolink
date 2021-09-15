package com.hfad.travelx;

import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.like.LikeButton;

import org.jetbrains.annotations.NotNull;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyviewHolder extends RecyclerView.ViewHolder
    {
       VideoView videoView;
       TextView name, postdesc,postloctn,no_likes,no_comments;
        ProgressBar pb;
        CircleImageView proimage;
        LikeButton likebtn;
        ImageButton commentbtn;
        public MyviewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.videoView);
            name = itemView.findViewById(R.id.name);
            postdesc = itemView.findViewById(R.id.postDesc);
            pb= itemView.findViewById(R.id.pb);
            postloctn = itemView.findViewById(R.id.post_location);
            proimage = itemView.findViewById(R.id.profile_photo);
            likebtn = itemView.findViewById(R.id.like_btn);
            commentbtn = itemView.findViewById(R.id.comment_btn);
            no_likes = itemView.findViewById(R.id.like_txt);
            no_comments = itemView.findViewById(R.id.comment_txt);
        }
    }
