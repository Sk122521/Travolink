package com.hfad.travelx;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import de.hdodenhof.circleimageview.CircleImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class CreatepostActivity extends AppCompatActivity {
    private String Currentuserid;
    private StorageReference Poststorageref;
    private MaterialButton addlocationbutton;
    private String download_url;
    private StorageReference filepath;
    private String image;
    private FirebaseAuth mauth;
    private Uri downloaduri;
     private MediaController mediaController;
    private Toolbar mtoolbar;
    private String name;
    private ImageButton photobutton;
    private TextView postbutton;
    private AppCompatEditText post_addresline;
    private AppCompatEditText postdescription;
    private String postrabdomnumber;
    private ShapeableImageView postsimage;
    private DatabaseReference postsref;
    private VideoView postvideo;
    private CircleImageView profileimage;
    private TextView profilename;
    private ProgressDialog progressDialog;
    private Uri resulturi = null;
    private String type,postsdescription;
    private DatabaseReference userref;
    private Double latitude = null,longitude = null ;
    private String addressline = "";
    static final int REQUEST_CODE = 11;
    /* access modifiers changed from: protected */
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createpost);
        this.userref = FirebaseDatabase.getInstance().getReference().child("Users");
        this.postsref = FirebaseDatabase.getInstance().getReference().child("Posts");
        FirebaseAuth instance = FirebaseAuth.getInstance();
        mauth = instance;
        Currentuserid = instance.getCurrentUser().getUid();
        progressDialog = new ProgressDialog(this);
        profileimage = (CircleImageView) findViewById(R.id.create_post_profileimage);
        profilename = (TextView) findViewById(R.id.create_post_profilename);
        addlocationbutton = (MaterialButton) findViewById(R.id.Addlocation_button);
        postsimage = (ShapeableImageView) findViewById(R.id.post_image);
        postvideo = (VideoView) findViewById(R.id.post_video);
        postdescription = (AppCompatEditText) findViewById(R.id.post_description);
        photobutton = (ImageButton) findViewById(R.id.add_photo_button);
        postbutton = (TextView) findViewById(R.id.post_button);
        Poststorageref = FirebaseStorage.getInstance().getReference().child("Posts");
        post_addresline = (AppCompatEditText) findViewById(R.id.post_addressline);
        mediaController = new MediaController(this);
        postvideo.setMediaController(mediaController);
        mediaController.setAnchorView(this.postvideo);
        postvideo.start();
        Toolbar toolbar = (Toolbar) findViewById(R.id.create_post_toolbar);
        mtoolbar = toolbar;
        toolbar.setTitle(R.string.create_post);
        mtoolbar.setNavigationIcon(R.drawable.back);
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CreatepostActivity.this.startActivity(new Intent(CreatepostActivity.this.getApplicationContext(), MainActivity.class));
            }
        });
        this.userref.child(this.Currentuserid).addValueEventListener(new ValueEventListener() {
            @Override // com.google.firebase.database.ValueEventListener
            public void onDataChange(DataSnapshot dataSnapshot) {
                CreatepostActivity.this.name = dataSnapshot.child("name").getValue().toString();
                CreatepostActivity.this.image = dataSnapshot.child("image").getValue().toString();
                TextView textView = CreatepostActivity.this.profilename;
                textView.setText(CreatepostActivity.this.name.substring(0, 1).toUpperCase() + CreatepostActivity.this.name.substring(1).toLowerCase());
                if (CreatepostActivity.this.image.isEmpty()) {
                    CreatepostActivity.this.profileimage.setImageResource(R.drawable.profile);
                } else {
                    Picasso.get().load(CreatepostActivity.this.image).into(CreatepostActivity.this.profileimage);
                }
            }

            @Override // com.google.firebase.database.ValueEventListener
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        this.photobutton.setOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.CreatepostActivity.AnonymousClass3 */

            public void onClick(View view) {
                CropImage.startPickImageActivity(CreatepostActivity.this);
                type = "photo";
            }
        });
        this.addlocationbutton.setOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.CreatepostActivity.AnonymousClass5 */

            public void onClick(View view) {
                Intent intent = new Intent(CreatepostActivity.this,GeocodingActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });
        this.postbutton.setOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.CreatepostActivity.AnonymousClass6 */

            public void onClick(View view) {
                CreatepostActivity.this.ValidatePostInfo();
            }
        });
    }
    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void ValidatePostInfo() {
        String obj = this.postdescription.getText().toString();
        this.postsdescription = obj;
        if (this.resulturi != null) {
            this.progressDialog.setTitle("Post");
            this.progressDialog.setMessage("wait while, post is uploading ....");
            this.progressDialog.setCanceledOnTouchOutside(false);
            this.progressDialog.show();
            StoringImageToFirebaseStorage();
        } else if(this.resulturi == null) {
            if (TextUtils.isEmpty(this.postsdescription)) {
                Toast.makeText(this, "Select files to post or write something...", Toast.LENGTH_LONG).show();
            }
            else {
            DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
            DateFormat dates = new SimpleDateFormat("d MMM yyyy");
            DateFormat times = new SimpleDateFormat("HH:mm");
            this.postrabdomnumber = df.format(Calendar.getInstance().getTime());
            String date = dates.format(Calendar.getInstance().getTime());
            String time = times.format(Calendar.getInstance().getTime());
            HashMap usermap = new HashMap<>();
            usermap.put("date", date);
            usermap.put("time", time);
            usermap.put("type", "nothing");
            usermap.put("profilename", this.name);
            usermap.put("timestamp", ServerValue.TIMESTAMP);
            usermap.put("profileimage", this.image);
            usermap.put("uid", this.Currentuserid);
            usermap.put("dateandtime", this.postrabdomnumber);
            usermap.put("posturl", "");
            usermap.put("addressline",post_addresline.getText().toString());
            usermap.put("postdescription", this.postdescription.getText().toString().trim());
            DatabaseReference databaseReference = this.postsref;
            databaseReference.child(this.Currentuserid + this.postrabdomnumber).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                /* class com.hfad.travelx.CreatepostActivity.AnonymousClass7 */

                @Override // com.google.android.gms.tasks.OnCompleteListener
                public void onComplete(Task<Void> task) {
                    if (task.isSuccessful()) {
                        CreatepostActivity.this.progressDialog.dismiss();
                        CreatepostActivity.this.SendUsertoMainActivity();
                        Toast.makeText(CreatepostActivity.this, "Post Uploded successfully", Toast.LENGTH_LONG).show();
                        return;
                    }
                    CreatepostActivity.this.progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    CreatepostActivity createpostActivity = CreatepostActivity.this;
                    Toast.makeText(createpostActivity, "ERROR : " + message, Toast.LENGTH_LONG).show();
                }
            });
        }
        }
    }

    private void StoringImageToFirebaseStorage() {
        byte[] fileInBytes = null;
        this.postrabdomnumber = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm").format(Calendar.getInstance().getTime());
        filepath = Poststorageref.child(System.currentTimeMillis() + "." + getfileext(resulturi));
            Bitmap bmp = null;
            try {
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), resulturi);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
            fileInBytes = baos.toByteArray();
            filepath.putBytes(fileInBytes).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<UploadTask.TaskSnapshot>() {
                /* class com.hfad.travelx.CreatepostActivity.AnonymousClass9 */

                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    CreatepostActivity.this.filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        /* class com.hfad.travelx.CreatepostActivity.AnonymousClass9.AnonymousClass1 */

                        public void onSuccess(Uri uri) {
                            DateFormat dates = new SimpleDateFormat("d MMM yyyy");
                            DateFormat times = new SimpleDateFormat("HH:mm");
                            String date = dates.format(Calendar.getInstance().getTime());
                            String time = times.format(Calendar.getInstance().getTime());
                            CreatepostActivity.this.downloaduri = uri;
                            CreatepostActivity.this.download_url = uri.toString();
                            HashMap usermap = new HashMap<>();
                            usermap.put("date", date);
                            usermap.put("time", time);
                            usermap.put("type", type);
                            usermap.put("timestamp", ServerValue.TIMESTAMP);
                            usermap.put("profilename", CreatepostActivity.this.name);
                            usermap.put("profileimage", CreatepostActivity.this.image);
                            usermap.put("uid", CreatepostActivity.this.Currentuserid);
                            usermap.put("dateandtime", CreatepostActivity.this.postrabdomnumber);
                            usermap.put("addressline",post_addresline.getText().toString());
                            usermap.put("posturl", CreatepostActivity.this.download_url);
                            usermap.put("postdescription", CreatepostActivity.this.postdescription.getText().toString().trim());
                            DatabaseReference databaseReference = CreatepostActivity.this.postsref;
                            databaseReference.child(CreatepostActivity.this.Currentuserid + CreatepostActivity.this.postrabdomnumber).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                /* class com.hfad.travelx.CreatepostActivity.AnonymousClass9.AnonymousClass1.AnonymousClass1 */

                                @Override // com.google.android.gms.tasks.OnCompleteListener
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        CreatepostActivity.this.progressDialog.dismiss();
                                        CreatepostActivity.this.SendUsertoMainActivity();
                                        Toast.makeText(CreatepostActivity.this, "Post Uploded successfully", Toast.LENGTH_LONG).show();
                                        return;
                                    }
                                    CreatepostActivity.this.progressDialog.dismiss();
                                    String message = task.getException().getMessage();
                                    CreatepostActivity createpostActivity = CreatepostActivity.this;
                                    Toast.makeText(createpostActivity, "ERROR : " + message, Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener((OnFailureListener) new OnFailureListener() {
                /* class com.hfad.travelx.CreatepostActivity.AnonymousClass8 */

                @Override // com.google.android.gms.tasks.OnFailureListener
                public void onFailure(Exception e) {
                    CreatepostActivity.this.progressDialog.dismiss();
                    Toast.makeText(CreatepostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void SendUsertoMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && requestCode != -1) {
            Uri imageuri = CropImage.getPickImageResultUri(this, data);
            if (CropImage.isReadExternalStoragePermissionsRequired(this, imageuri)) {
               requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 0);
            } else {
                startCrop(imageuri);
            }
        }
        if (requestCode == 203) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == -1) {
                this.postsimage.setVisibility(View.VISIBLE);
                this.postvideo.setVisibility(View.GONE);
                Uri uri = result.getUri();
                this.resulturi = uri;
                this.postsimage.setImageURI(uri);
                type = "photo";
            }
        }
        if (requestCode == REQUEST_CODE){
            if (resultCode==RESULT_OK){
                  //  latitude = data.getDoubleExtra("latitude");
                    //longitude = data.getDoubleExtra("longitude");
                    addressline = data.getStringExtra("exactlocation");
                    post_addresline.setVisibility(View.VISIBLE);
                    post_addresline.setText(addressline);

            }
        }
    }

    private String getfileext(Uri resulturis) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(getContentResolver().getType(resulturis));
    }

    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).start(this);
    }

}
