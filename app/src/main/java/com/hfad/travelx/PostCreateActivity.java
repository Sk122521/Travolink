package com.hfad.travelx;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.internal.BoltsMeasurementEventListener;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PostCreateActivity extends AppCompatActivity {
  private String type,currentUid,name,image;
  private TextInputEditText Tif;
  private ImageView videoimg;
  private RecyclerView imgRV;
  private Button postbutton;
  private DatabaseReference databaseReference;
  private ProgressDialog progressDialog;
  private StorageReference Poststorageref;
  private int pos;
  private String addressline = "";
  private String latitude = "";
  private String longitude ="" ;
  static final int REQUEST_CODE = 11;
  private EditText a;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);
        postbutton = findViewById(R.id.postButton);
        Tif = (TextInputEditText)findViewById(R.id.Tif);
        videoimg = (ImageView)findViewById(R.id.videoimg);
        videoimg = (ImageView)findViewById(R.id.videoimg);
        videoimg = (ImageView)findViewById(R.id.videoimg);
        imgRV = (RecyclerView)findViewById(R.id.imgRecyclerView);
        a  = findViewById(R.id.location);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        Poststorageref = FirebaseStorage.getInstance().getReference();
        currentUid = FirebaseAuth.getInstance().getUid();
        progressDialog = new ProgressDialog(this);
        pos = getIntent().getExtras().getInt("pos");
        databaseReference.child("Users").child(currentUid).addValueEventListener(new ValueEventListener() {
            @Override // com.google.firebase.database.ValueEventListener
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                image = dataSnapshot.child("image").getValue().toString();
            }
            @Override // com.google.firebase.database.ValueEventListener
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        if (pos == -5)
        {
            videoimg.setVisibility(View.GONE);
            if (PhotoListFragment.selectedImageList.isEmpty())
            {
                type = "nothing";
                imgRV.setVisibility(View.GONE);
            }else
            {
                type = "photo";
                imgRV.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                imgRV.setLayoutManager(layoutManager);
                SelectedImageAdapter selectedImageAdapter = new SelectedImageAdapter(this,PhotoListFragment.selectedImageList);
                imgRV.setAdapter(selectedImageAdapter);
            }
        }
        else
        {
          type = "video";
          imgRV.setVisibility(View.GONE);
          videoimg.setVisibility(View.VISIBLE);
          videoimg.setImageBitmap(VideoListFragment.videoArrayList.get(pos).getBitmap());
        }
 }
    public void SetPost(View V){
        String postdscrptn = Tif.getText().toString();
        switch (type){
            case "photo" :
                ValidatePostInfo();
                break;
            case "video":
                ValidateVideoPost();
                break;
            case "nothing":
                if (postdscrptn.isEmpty()){
                    Toast.makeText(this, "Your Post is empty...", Toast.LENGTH_SHORT).show();
                }else{
                 ValidateTextPost();
                }break;
        }
    }

    private void ValidateTextPost() {
        this.progressDialog.setTitle("Post");
        this.progressDialog.setMessage("wait while, post is uploading ....");
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
        StoringTextToFirebaseStorage();
    }
    private void StoringTextToFirebaseStorage() {
        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
        DateFormat dates = new SimpleDateFormat("d MMM yyyy");
        DateFormat times = new SimpleDateFormat("HH:mm");
        String postrabdomnumber = df.format(Calendar.getInstance().getTime());
        String date = dates.format(Calendar.getInstance().getTime());
        String time = times.format(Calendar.getInstance().getTime());
        HashMap<Object, Object> usermap = new HashMap<>();
        usermap.put("date", date);
        usermap.put("time", time);
        usermap.put("type", "nothing");
        usermap.put("profilename", this.name);
        usermap.put("timestamp", ServerValue.TIMESTAMP);
        usermap.put("profileimage", this.image);
        usermap.put("uid",currentUid);
        usermap.put("dateandtime", postrabdomnumber);
        usermap.put("posturl", "");
        usermap.put("filter",currentUid+"_photo");
        usermap.put("postdescription", Tif.getText().toString());
        usermap.put("latitude",latitude);
        usermap.put("longitude",longitude);
        usermap.put("addressline",addressline);
        databaseReference.child("Posts").child(currentUid + postrabdomnumber).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            /* class com.hfad.travelx.CreatepostActivity.AnonymousClass7 */

            @Override // com.google.android.gms.tasks.OnCompleteListener
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    startActivity(new Intent(PostCreateActivity.this,MainActivity.class));
                    Toast.makeText(PostCreateActivity.this, "Post Uploded successfully", Toast.LENGTH_LONG).show();
                    return;
                }
                progressDialog.dismiss();
                String message = task.getException().getMessage();
                Toast.makeText(PostCreateActivity.this, "ERROR : " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ValidateVideoPost() {
        this.progressDialog.setTitle("Post");
        this.progressDialog.setMessage("wait while, post is uploading ....");
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
        StoringVideoToFirebaseStorage();
    }

    private void StoringVideoToFirebaseStorage() {
        String postrabdomnumber = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm").format(Calendar.getInstance().getTime());
        StorageReference filepath = Poststorageref.child("Videopost").child(currentUid).child((System.currentTimeMillis()+"."+ getfileext(VideoListFragment.videoArrayList.get(pos).getVideoUri())));
        filepath.putFile(VideoListFragment.videoArrayList.get(pos).getVideoUri()).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<UploadTask.TaskSnapshot>() {
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    public void onSuccess(Uri uri) {
                        DateFormat dates = new SimpleDateFormat("d MMM yyyy");
                        DateFormat times = new SimpleDateFormat("HH:mm");
                        String date = dates.format(Calendar.getInstance().getTime());
                        String time = times.format(Calendar.getInstance().getTime());
                        HashMap usermap = new HashMap<>();
                        usermap.put("date", date);
                        usermap.put("time", time);
                        usermap.put("type", type);
                        usermap.put("timestamp", ServerValue.TIMESTAMP);
                        usermap.put("profilename", name);
                        usermap.put("profileimage", image);
                        usermap.put("uid",currentUid);
                        usermap.put("dateandtime", postrabdomnumber);
                        usermap.put("posturl", uri.toString());
                        usermap.put("postdescription", Tif.getText().toString());
                        usermap.put("latitude",latitude);
                        usermap.put("longitude",longitude);
                        usermap.put("addressline",addressline);
                        databaseReference.child("VideoPost").child(currentUid + postrabdomnumber).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override // com.google.android.gms.tasks.OnCompleteListener
                            public void onComplete(Task<Void> task) {
                                if (task.isSuccessful()) {
                                     progressDialog.dismiss();
                                     startActivity(new Intent(PostCreateActivity.this,MainActivity.class));
                                    Toast.makeText(PostCreateActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    progressDialog.dismiss();
                                    String message = task.getException().getMessage();
                                    Toast.makeText(PostCreateActivity.this, "ERROR : " + message, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        }).addOnFailureListener((OnFailureListener) new OnFailureListener() {
            /* class com.hfad.travelx.CreatepostActivity.AnonymousClass8 */

            @Override // com.google.android.gms.tasks.OnFailureListener
            public void onFailure(Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PostCreateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ValidatePostInfo() {
        this.progressDialog.setTitle("Post");
        this.progressDialog.setMessage("wait while, post is uploading ....");
        this.progressDialog.setCanceledOnTouchOutside(false);
        this.progressDialog.show();
        StoringImageToFirebaseStorage();
    }
    private void StoringImageToFirebaseStorage() {
        String random = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm").format(Calendar.getInstance().getTime());
        ArrayList<String> arrayList  = PhotoListFragment.selectedImageList;
        byte[] fileInBytes = null;
        Bitmap bmp = null;
        for ( int i = 0 ; i < arrayList.size(); i++){
            try {
                final int a = i;
                Uri uri  = Uri.parse(arrayList.get(i));
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                fileInBytes = baos.toByteArray();
                StorageReference filepath = Poststorageref.child("Posts").child(currentUid).child(System.currentTimeMillis() + "."+"null");
                filepath.putBytes(fileInBytes).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                databaseReference.child("Posts").child(currentUid+random).child("images").child(Integer.toString(a)).setValue(uri.toString());
                                if (a == 0){
                                    databaseReference.child("Posts").child(currentUid+random).child("posturl").setValue(uri.toString());
                                }
                            }
                        });
                    }
                }).addOnFailureListener((OnFailureListener) new OnFailureListener() {
                    /* class com.hfad.travelx.CreatepostActivity.AnonymousClass8 */

                    @Override // com.google.android.gms.tasks.OnFailureListener
                    public void onFailure(Exception e) {
                        progressDialog.dismiss();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        DateFormat dates = new SimpleDateFormat("d MMM yyyy");
        DateFormat times = new SimpleDateFormat("HH:mm");
        String date = dates.format(Calendar.getInstance().getTime());
        String time = times.format(Calendar.getInstance().getTime());
        HashMap usermap = new HashMap<>();
        usermap.put("date", date);
        usermap.put("time", time);
        usermap.put("type", type);
        usermap.put("profilename", name);
        usermap.put("profileimage", image);
        usermap.put("uid",currentUid);
        usermap.put("dateandtime",random);
        usermap.put("timestamp", ServerValue.TIMESTAMP);
        usermap.put("addressline",addressline);
        usermap.put("filter",currentUid+"_photo");
        usermap.put("latitude",latitude);
        usermap.put("longitude",longitude);
        usermap.put("postdescription",Tif.getText().toString());
        databaseReference.child("Posts").child(currentUid+random).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    progressDialog.dismiss();
                    Toast.makeText(PostCreateActivity.this, "Post Uploaded", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PostCreateActivity.this,MainActivity.class));
                    return;
                }
                progressDialog.dismiss();
                String message = task.getException().getMessage();
                Toast.makeText(PostCreateActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void AddLocation(View v){
        Intent intent = new Intent(PostCreateActivity.this,GeocodingActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            if (resultCode==RESULT_OK){
                addressline = data.getStringExtra("exactlocation");
                latitude = data.getStringExtra("latitude");
                longitude = data.getStringExtra("longitude");
                a.setVisibility(View.VISIBLE);
                a.setText(addressline);
            }
        }
    }
    private String getfileext(Uri resulturis) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(resulturis));
    }
}