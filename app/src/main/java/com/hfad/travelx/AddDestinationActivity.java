package com.hfad.travelx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static com.hfad.travelx.CreatepostActivity.REQUEST_CODE;
import static com.hfad.travelx.R.string.select_image;

public class AddDestinationActivity extends AppCompatActivity {
    String[] LocationType = new String[]{
            "Mountains","Hills","Historical","Monuments","Pilgrim","Wildlife Parks","Waterfalls"
    };
    private StorageReference mstorage;
    private MultiAutoCompleteTextView typetextview;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageButton addLocationImageButton;
    private EditText nameofdestinationview,GuideTextView,popular,Foodcult,backpack,quote;
    private TextInputEditText AboutDestinationView;
    private DatabaseReference userref;
    private String Currentuserid;
    private ProgressDialog progressDialog;
    private Button SaveDestinationButton;
    private MaterialButton getlocationButtton;
    private String latitude = null;
    private String longitude = null;
    private String Exactlocation = "";
    private String name,image;
    private DatabaseReference databaseReference;
    private TextView showlatitude,showlongitude,showaddressline;
    private Bundle bundle;
    static final int REQUEST_CODE_OK = 11;
    private ArrayList<String> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_destination);
        SaveDestinationButton = (Button)findViewById(R.id.save_destination_button);
        showlatitude = (TextView)findViewById(R.id.show_latitude);
        showlongitude = (TextView)findViewById(R.id.show_longitude);
        showaddressline = (TextView)findViewById(R.id.exact_addressline);
        progressDialog = new ProgressDialog(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        userref = databaseReference.child("Users");
        Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
         typetextview = findViewById(R.id.LocationTypeText);
         addLocationImageButton = findViewById(R.id.add_locationimage);
         nameofdestinationview = (EditText)findViewById(R.id.nameofdestination);
         AboutDestinationView =(TextInputEditText)findViewById(R.id.edittext_about);
         GuideTextView = (EditText)findViewById(R.id.guide);
         getlocationButtton = (MaterialButton)findViewById(R.id.getLocation_button);
         popular = (EditText)findViewById(R.id.popular);
         Foodcult = (EditText)findViewById(R.id.FoodCult);
         backpack = (EditText)findViewById(R.id.backpack);
         quote = (EditText)findViewById(R.id.quote);
        mstorage = FirebaseStorage.getInstance().getReference();
        databaseReference.child("Users").child(Currentuserid).addValueEventListener(new ValueEventListener() {
            @Override // com.google.firebase.database.ValueEventListener
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                image = dataSnapshot.child("image").getValue().toString();
            }
            @Override // com.google.firebase.database.ValueEventListener
            public void onCancelled(DatabaseError databaseError) {
            }
        });
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,LocationType);
       typetextview.setAdapter(adapter);
       typetextview.setThreshold(1);
        typetextview.setDropDownWidth(300);
        typetextview.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
       typetextview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
              typetextview.showDropDown();
                return false;
            }
        });
       addLocationImageButton.setOnClickListener(new View.OnClickListener() {
           @Override  public void onClick(View v){
               Intent intent = new Intent();
               intent.setType("image/*");
               intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
               intent.setAction(Intent.ACTION_GET_CONTENT);
               startActivityForResult(Intent.createChooser(intent, "Select Picture"),RESULT_LOAD_IMAGE);
               Toast.makeText(AddDestinationActivity.this, select_image, Toast.LENGTH_SHORT).show();
           }
       });
       getlocationButtton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent intent = new Intent(AddDestinationActivity.this,GeocodingActivity.class);
               startActivityForResult(intent, REQUEST_CODE_OK);
           }
       });
       SaveDestinationButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String name = nameofdestinationview.getText().toString();
               if (TextUtils.isEmpty(name)||list.isEmpty()||AboutDestinationView.getText().toString().isEmpty()||showaddressline.getText().toString().isEmpty()){
                   Toast.makeText(AddDestinationActivity.this, "required fields are destination name,destination location,your experiences and photos of destination should be filled...", Toast.LENGTH_LONG).show();
               }
               else{
                   progressDialog.setTitle("profile info ");
                   progressDialog.setMessage("Please,wait while Uploading your Destination information ....");
                   progressDialog.setCanceledOnTouchOutside(false);
                   progressDialog.show();
                   SaveDestinationInfo();
                  // startActivity(new Intent(AddDestinationActivity.this,SaharYaChetraActivity.class));
               }
           }
       });
    }

    private void SaveDestinationInfo() {

        String random = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm").format(Calendar.getInstance().getTime());
        byte[] fileInBytes = null;
        Bitmap bmp = null;
        for ( int i = 0 ; i < list.size(); i++){
            try {
                final int a = i;
                Uri uri  = Uri.parse(list.get(i));
                bmp = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 25, baos);
                fileInBytes = baos.toByteArray();
                StorageReference filepath = mstorage.child("Posts").child(Currentuserid).child(System.currentTimeMillis() + "."+"null");
                filepath.putBytes(fileInBytes).addOnSuccessListener((OnSuccessListener) new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                databaseReference.child("Posts").child(Currentuserid+random).child("images").child(Integer.toString(a)).setValue(uri.toString());
                            }
                        });
                    }
                }).addOnFailureListener((OnFailureListener) new OnFailureListener() {
                    @Override
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
        String nameofdest = nameofdestinationview.getText().toString();
        String type = typetextview.getText().toString();
        String about = AboutDestinationView.getText().toString();
        String guide = GuideTextView.getText().toString();
        String why = popular.getText().toString();
        String cult = Foodcult.getText().toString();
        String pack = backpack.getText().toString();
        String q = quote.getText().toString();
        HashMap usermap = new HashMap<>();
        usermap.put("profilename",name);
        usermap.put("profileimage",image);
        usermap.put("destname",nameofdest);
        usermap.put("uid",Currentuserid);
        usermap.put("postdescription","");
        usermap.put("your_experience",about);
        usermap.put("guide",guide);
        usermap.put("typedest",type);
        usermap.put("type","dest");
        usermap.put("longitude",longitude);
        usermap.put("latitude",latitude);
        usermap.put("dateandtime",random);
        usermap.put("posturl", "");
        usermap.put("addressline",Exactlocation);
        usermap.put("date",date);
        usermap.put("time",time);
        usermap.put("timestamp", ServerValue.TIMESTAMP);
        usermap.put("foodcult",cult);
        usermap.put("filter",Currentuserid+"_dest");
        usermap.put("whytravel",why);
        usermap.put("backpack",pack);
        usermap.put("quote",q);
        databaseReference.child("Posts").child(Currentuserid + random).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                   // userref.child(Currentuserid).child("dest_addded").child(Integer.toString(n+1)).setValue(profilemap);
                    progressDialog.dismiss();
                    startActivity(new Intent(AddDestinationActivity.this,MainActivity.class));
                }else{
                    progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    Toast.makeText(AddDestinationActivity.this, "ERROR :"+message, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
             if (data.getClipData()==null){
                 Toast.makeText(this, select_image, Toast.LENGTH_SHORT).show();
             }
             else if (data.getClipData() != null){
                int totalitemselected = data.getClipData().getItemCount();
                for (int i = 0; i<totalitemselected; i++){
                  list.add(data.getClipData().getItemAt(i).getUri().toString());
                }
                 RecyclerView recyclerView = findViewById(R.id.imagerecyclerview);
                 LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                 recyclerView.setLayoutManager(layoutManager);
                 SelectedImageAdapter selectedImageAdapter = new SelectedImageAdapter(this,list);
                 recyclerView.setAdapter(selectedImageAdapter);
            }
        }else if(data == null){
            Toast.makeText(this, "Select Multiple Files", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_CODE_OK){
            if (resultCode == RESULT_OK){
                    latitude = data.getStringExtra("latitude");
                    longitude = data.getStringExtra("longitude");
                    Exactlocation = data.getStringExtra("exactlocation");
                    showlatitude.setVisibility(View.VISIBLE);
                    showlongitude.setVisibility(View.VISIBLE);
                    showaddressline.setVisibility(View.VISIBLE);
                    showlatitude.setText(latitude);
                    showlongitude.setText(longitude);
                    showaddressline.setText(Exactlocation);
            }
        }
    }
}
