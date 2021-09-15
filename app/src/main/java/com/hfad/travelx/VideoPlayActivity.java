package com.hfad.travelx;

import android.Manifest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;
import java.util.ArrayList;
public class VideoPlayActivity extends AppCompatActivity {
    VideoView videoView;
    SeekBar seekBar;
    int video_index = 0;
    String  total_duration;
    double current_pos;
    Handler mHandler,handler;
    boolean isVisible = true;
    RelativeLayout relativeLayout;
    public static final int PERMISSION_READ = 0;
    private ArrayList<VideoModel> videoArrayList;
    private MediaController mediaController;
    private Toolbar posttool ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play);
        posttool = findViewById(R.id.post_tool);
        setSupportActionBar(posttool);
        posttool.setNavigationIcon(R.drawable.back);
        posttool.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });
        video_index = getIntent().getExtras().getInt("pos");
        videoArrayList = VideoListFragment.videoArrayList;
        if (checkPermission()) {
            setVideo();
        }
    }
    public void setVideo() {
        videoView = (VideoView) findViewById(R.id.videoview);
        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        mHandler = new Handler();
        handler = new Handler();
        if (mediaController == null){
            mediaController = new MediaController(this);
            mediaController.setAnchorView(videoView);
        }
        videoView.setMediaController(mediaController);
        playVideo(video_index);
        hideLayout();
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                setPause();
            }
        });

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                setVideoProgress();
            }
        });
    }

    public void playVideo(int pos) {
            videoView.setVideoURI(videoArrayList.get(pos).getVideoUri());
            videoView.start();
    }

    public void setVideoProgress() {
        current_pos = videoView.getCurrentPosition();
        total_duration = videoArrayList.get(video_index).getVideoDuration();
        //seekBar.setMax(Integer.parseInt(total_duration));
        final Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    current_pos = videoView.getCurrentPosition();
                    handler.postDelayed(this, 1000);
                } catch (IllegalStateException ed){
                    ed.printStackTrace();
                }
            }
        };
        handler.postDelayed(runnable, 1000);

        //seekbar change listner
    }
    public void setPause() {
        if (videoView.isPlaying()) {
            videoView.pause();
        } else {
            videoView.start();
        }
    }
    public void hideLayout() {

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isVisible = false;
            }
        };
        handler.postDelayed(runnable, 5000);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.removeCallbacks(runnable);
                if (isVisible) {
                    isVisible = false;
                } else {
                    mHandler.postDelayed(runnable, 5000);
                    isVisible = true;
                }
            }
        });

    }
    public boolean checkPermission() {
        int READ_EXTERNAL_PERMISSION = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if ((READ_EXTERNAL_PERMISSION != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_READ);
            return false;
        }
        return true;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case  PERMISSION_READ: {
                if (grantResults.length > 0 && permissions[0].equals(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(getApplicationContext(), "Please allow storage permission", Toast.LENGTH_LONG).show();
                    } else {
                     setVideo();
                    }
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater  = getMenuInflater();
        menuInflater.inflate(R.menu.post,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.Next)
        {
            Intent intent = new Intent(this,PostCreateActivity.class);
            intent.putExtra("pos",video_index);
            startActivity(intent);
        }
        return true;
    }
}