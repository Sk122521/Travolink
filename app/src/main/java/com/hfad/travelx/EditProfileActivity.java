package com.hfad.travelx;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.NotificationCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import de.hdodenhof.circleimageview.CircleImageView;
import java.util.Calendar;
import java.util.HashMap;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_REQUEST_CODE = 1000;
    private static int gallerypick = 1;
    private TextInputEditText Addresstextprofile;
    private String Currentuserid;
    private TextInputLayout Statusprofile;
    private AutoCompleteTextView Stausdropdownprofile;
    private TextInputEditText biotextprofile;
    private Calendar calender;
    private AppCompatImageButton camerabuttonprofile;
    private DatePicker datePicker;
    private TextView dateview;
    private int day;
    private StorageReference filepath,imageref;
    private TextInputEditText fullnametextprofile;
    private String images,btntype,headerimage;
    ProgressDialog loadbar;
    private FirebaseAuth mauth;
    private int month;
    private DatabaseReference postref;
    private CircleImageView profileimageview;
    private ProgressDialog progressDialog;
    private Button saveButton;
    private DatabaseReference userref;
    private TextInputEditText websitetextprofile;
    private int year;
    private AppCompatImageView hdrimageview;
    private AppCompatImageButton hdrbtn_pro;
   @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        FirebaseAuth instance = FirebaseAuth.getInstance();
        this.mauth = instance;
        this.Currentuserid = instance.getCurrentUser().getUid();
        this.userref = FirebaseDatabase.getInstance().getReference();
        this.postref = FirebaseDatabase.getInstance().getReference().child("Posts");
        this.imageref = FirebaseStorage.getInstance().getReference();
        this.Statusprofile = (TextInputLayout) findViewById(R.id.status_profile);
        this.progressDialog = new ProgressDialog(this);
        this.profileimageview = (CircleImageView) findViewById(R.id.image_profile);
        this.saveButton = (Button) findViewById(R.id.save_button_for_profile);
        this.fullnametextprofile = (TextInputEditText) findViewById(R.id.fullname_text_profile);
        this.Stausdropdownprofile = (AutoCompleteTextView) findViewById(R.id.dropdown_status_profile);
        this.Addresstextprofile = (TextInputEditText) findViewById(R.id.Address_text_profile);
        this.websitetextprofile = (TextInputEditText) findViewById(R.id.website_text_profile);
        this.camerabuttonprofile = (AppCompatImageButton) findViewById(R.id.camera_button_profile);
        this.biotextprofile = (TextInputEditText) findViewById(R.id.bio_text_profile);
        hdrbtn_pro = (AppCompatImageButton) findViewById(R.id.hdr_btn_profile);
        hdrimageview = (AppCompatImageView) findViewById(R.id.hdrimg_profile);
        this.Stausdropdownprofile.setAdapter(new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, new String[]{"travelling", "Guide","food and cuisine","Sporting events", "travel entusiastic", "Adventure", "photography", "Travel Blogging", "Solo travel", "national study", "Historical tourism", "LGBTQ travel","Cultures", "Others"}));
        this.saveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditProfileActivity.this.progressDialog.setTitle("profile info ");
                EditProfileActivity.this.progressDialog.setMessage("Please,wait while Uploading your profile information ....");
                EditProfileActivity.this.progressDialog.setCanceledOnTouchOutside(true);
                EditProfileActivity.this.progressDialog.show();
                EditProfileActivity.this.SetyourProfileInformation();
            }
        });
        this.camerabuttonprofile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                CropImage.startPickImageActivity(EditProfileActivity.this);
                btntype = "profileimg";
            }
        });
        this.userref.child("Users").child(this.Currentuserid).addValueEventListener(new ValueEventListener() {
            @Override // com.google.firebase.database.ValueEventListener
            public void onDataChange(DataSnapshot dataSnapshot) {
                String username = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child(NotificationCompat.CATEGORY_STATUS).getValue().toString();
                String address = dataSnapshot.child("address").getValue().toString();
                String websites = dataSnapshot.child("website").getValue().toString();
                String bio = dataSnapshot.child("bio").getValue().toString();
                images = dataSnapshot.child("image").getValue().toString();
                headerimage = dataSnapshot.child("hdrimg").getValue().toString();
                TextInputEditText textInputEditText = EditProfileActivity.this.fullnametextprofile;
                textInputEditText.setText(username.substring(0, 1).toUpperCase() + username.substring(1).toLowerCase());
                EditProfileActivity.this.Stausdropdownprofile.setText(status);
                if (!address.isEmpty()) {
                    TextInputEditText textInputEditText2 = EditProfileActivity.this.Addresstextprofile;
                    textInputEditText2.setText(address.substring(0, 1).toUpperCase() + address.substring(1).toLowerCase());
                }
                EditProfileActivity.this.websitetextprofile.setText(websites);
                EditProfileActivity.this.biotextprofile.setText(bio);
                if (EditProfileActivity.this.images.isEmpty()) {
                    profileimageview.setImageResource(R.drawable.profile);
                } else {
                    Picasso.get().load(images).into(profileimageview);
                }
                if (headerimage.isEmpty()){
                    hdrimageview.setImageResource(R.drawable.nanital);
                }else{
                    Picasso.get().load(headerimage).into(hdrimageview);
                }
            }

            @Override // com.google.firebase.database.ValueEventListener
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public void ChangeHeaderImage(View v){
        CropImage.startPickImageActivity(EditProfileActivity.this);
        btntype = "hdrimage";
    }
   @Override
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
                if (btntype.equals("profileimg")){
                    this.progressDialog.setTitle("Profile image Upload");
                    this.progressDialog.setMessage("Your new profile image is uploading....");
                    this.progressDialog.setCanceledOnTouchOutside(false);
                    this.progressDialog.show();
                    uploadImagetoFirebase(result.getUri());
                }else if (btntype.equals("hdrimage")){
                    this.progressDialog.setMessage("Your new cover photo...");
                    this.progressDialog.setCanceledOnTouchOutside(false);
                    this.progressDialog.show();
                    UploadHeaderimage(result.getUri());
                }
            }
        }
    }
    private void startCrop(Uri imageuri) {
        CropImage.activity(imageuri).setGuidelines(CropImageView.Guidelines.ON).setMultiTouchEnabled(true).start(this);
    }
    private void UploadHeaderimage(Uri uri) {
        filepath = this.imageref.child("Headerimg").child(this.Currentuserid + uri.getLastPathSegment());
        filepath.putFile(uri).addOnCompleteListener((OnCompleteListener) new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    EditProfileActivity.this.filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        public void onSuccess(Uri uri) {
                            final String download_url = uri.toString();
                            EditProfileActivity.this.userref.child("Users").child(EditProfileActivity.this.Currentuserid).child("hdrimg").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        EditProfileActivity.this.progressDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this,"Header image set successfully", Toast.LENGTH_SHORT).show();
                                        sendUserToEditprofileActivity();
                                        return;
                                    }else{
                                        EditProfileActivity.this.progressDialog.dismiss();
                                        String message = task.getException().getMessage();
                                        EditProfileActivity editProfileActivity = EditProfileActivity.this;
                                        Toast.makeText(editProfileActivity, "ERROR : " + message, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                    return;
                }else{
                    EditProfileActivity.this.progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    EditProfileActivity editProfileActivity = EditProfileActivity.this;
                    Toast.makeText(editProfileActivity, "ERROR : " + message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadImagetoFirebase(Uri resulturi) {
        StorageReference child = this.imageref.child("profileimage");
        StorageReference child2 = child.child(this.Currentuserid + resulturi.getLastPathSegment());
        this.filepath = child2;
        child2.putFile(resulturi).addOnCompleteListener((OnCompleteListener) new OnCompleteListener<UploadTask.TaskSnapshot>() {
          @Override
            public void onComplete(Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    EditProfileActivity.this.filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        public void onSuccess(Uri uri) {
                            final String download_url = uri.toString();
                            EditProfileActivity.this.userref.child("Users").child(EditProfileActivity.this.Currentuserid).child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                                public void onComplete(Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        EditProfileActivity.this.postref.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                                    if (ds.child("uid").getValue().toString().equals(EditProfileActivity.this.Currentuserid)) {
                                                        String parent = ds.getRef().getKey();
                                                        EditProfileActivity.this.postref.child(parent).child("profileimage").setValue(download_url);
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                        });
                                        EditProfileActivity.this.progressDialog.dismiss();
                                        Toast.makeText(EditProfileActivity.this,"profile image set successfully", Toast.LENGTH_SHORT).show();
                                        sendUserToEditprofileActivity();
                                        return;
                                    }
                                    EditProfileActivity.this.progressDialog.dismiss();
                                    String message = task.getException().getMessage();
                                    EditProfileActivity editProfileActivity = EditProfileActivity.this;
                                    Toast.makeText(editProfileActivity, "ERROR : " + message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                    return;
                }
                EditProfileActivity.this.progressDialog.dismiss();
                String message = task.getException().getMessage();
                EditProfileActivity editProfileActivity = EditProfileActivity.this;
                Toast.makeText(editProfileActivity, "ERROR : " + message, Toast.LENGTH_LONG).show();
            }
        });
    }
    private void SetyourProfileInformation() {
        String username = this.fullnametextprofile.getText().toString();
        String status = this.Stausdropdownprofile.getText().toString();
        String Address = this.Addresstextprofile.getText().toString();
        String website = this.websitetextprofile.getText().toString();
        String bio = this.biotextprofile.getText().toString();
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Enter your name....", Toast.LENGTH_LONG).show();
            return;
        }
        HashMap<String, String> usermap = new HashMap<>();
        usermap.put("name", username.toLowerCase());
        usermap.put("status", status);
        usermap.put("address", Address.toLowerCase());
        usermap.put("website", website);
        usermap.put("image", this.images);
        usermap.put("bio", bio);
        usermap.put("uid", this.Currentuserid);
        usermap.put("hdrimg",headerimage);
        this.userref.child("Users").child(this.Currentuserid).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            /* class com.hfad.travelx.EditProfileActivity.AnonymousClass5 */

            @Override // com.google.android.gms.tasks.OnCompleteListener
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    EditProfileActivity.this.progressDialog.dismiss();
                    Toast.makeText(EditProfileActivity.this, (int) R.string.Accountupdated, Toast.LENGTH_LONG).show();
                    return;
                }
                EditProfileActivity.this.progressDialog.dismiss();
                String message = task.getException().getMessage();
                EditProfileActivity editProfileActivity = EditProfileActivity.this;
                Toast.makeText(editProfileActivity, "ERROR : " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void sendUserToEditprofileActivity() {
        Intent intent = new Intent(this,EditProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }
}
