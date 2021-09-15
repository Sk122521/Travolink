package com.hfad.travelx;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class AddStayActivity extends AppCompatActivity {
    private AutoCompleteTextView typeofstaytextview;
    private MultiAutoCompleteTextView FacilitiesTextView;
    private RecyclerView stayrecyclerview,nearbyrecyclerview;
    private ImageButton addImagestay,addImagenearby;
    private String ImageType;
    private List<String> fileNameList,fileNameList2;
    private List<String> fileDoneList,fileDoneList2;
    UploadImageAdapter uploadstayImageAdapter,uploadnearbyImageAdapter;
    private StorageReference mstorage;
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private CheckBox locationCheckBox;
    private ArrayList<String> Servicelist,Occupancylist,Tenantslist;
    private ArrayList<String> stayimageList,nearbyimagelist;
    private DatabaseReference Staydataref;
    private String Currentuserid;
    private ProgressDialog progressDialog;
    private Button saveinfobutton;
    private EditText nameofstay,locationofstay,phoneofstay,websitehotel;
    private TextInputEditText aboutstay;
    private double latitude,longitude;
    private int n;
    String[] Types = new String[]{
            "Hotel","Hostel","PGs","Home Stays"
    };
    String[] Facilities = new String[]{
           "Wifi","Swimming poll","parking facility","Food Facility","AC","TV","Power backup","CCTV Cameras","Reception","Security","Daily Housekeeping","First Aid",
            "Fire Extinguisher","Attached Bathroom","Mess","Laundry"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_stay);
        saveinfobutton = (Button)findViewById(R.id.save_info_button);
         typeofstaytextview = findViewById(R.id.type_stay);
         FacilitiesTextView = findViewById(R.id.facilities_Textview);
         addImagestay = (ImageButton)findViewById(R.id.add_image_stay);
         addImagenearby = (ImageButton)findViewById(R.id.add_nearby);
         stayrecyclerview = findViewById(R.id.Stayrecyclerview);
         nearbyrecyclerview = findViewById(R.id.nearbyPlacesview);
         locationCheckBox = findViewById(R.id.Location_Check);
         nameofstay = (EditText)findViewById(R.id.nameofstay);
         locationofstay = (EditText)findViewById(R.id.cityofstay);
         phoneofstay = (EditText)findViewById(R.id.phoneofstay);
         websitehotel = (EditText)findViewById(R.id.website_hotel);
         aboutstay = (TextInputEditText)findViewById(R.id.edittext_aboutstay);
         Staydataref = FirebaseDatabase.getInstance().getReference().child("Stay");
         Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
         progressDialog = new ProgressDialog(this);
        fileNameList = new ArrayList<String>();
        fileDoneList = new ArrayList<String>();
        fileDoneList2 = new ArrayList<String>();
        fileNameList2 = new ArrayList<String>();
        Servicelist = new ArrayList<>();
        Occupancylist = new ArrayList<>();
        Tenantslist = new ArrayList<>();
        stayimageList  = new ArrayList<>();
        nearbyimagelist = new ArrayList<>();
         mstorage = FirebaseStorage.getInstance().getReference();
        Staydataref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                n = (int) snapshot.child(Currentuserid).getChildrenCount(); }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        uploadnearbyImageAdapter = new UploadImageAdapter(fileNameList,fileDoneList);
        uploadstayImageAdapter  = new UploadImageAdapter(fileNameList2,fileDoneList2);
        setAdapterRecyclerView(stayrecyclerview,uploadstayImageAdapter);
        setAdapterRecyclerView(nearbyrecyclerview,uploadnearbyImageAdapter);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,Types);
        // ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,cuisinelist);
        typeofstaytextview.setAdapter(adapter);
        typeofstaytextview.setThreshold(1);
        typeofstaytextview.setDropDownWidth(300);
        typeofstaytextview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               typeofstaytextview.showDropDown();
                return false;
            }
        });
        ArrayAdapter<String> facilitiesadapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,Facilities);
        // ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,cuisinelist);
        FacilitiesTextView.setAdapter(facilitiesadapter);
        FacilitiesTextView.setThreshold(1);
        FacilitiesTextView.setDropDownWidth(300);
        FacilitiesTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        FacilitiesTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FacilitiesTextView.showDropDown();
                return false;
            }
        });
        addImagenearby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageType = "nearby";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_LOAD_IMAGE);
                Toast.makeText(AddStayActivity.this, R.string.select_image, Toast.LENGTH_SHORT).show();
            }
        });
        addImagestay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageType = "stay";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_LOAD_IMAGE);
            }
        });
        saveinfobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              String name = nameofstay.getText().toString();
              String location  = locationofstay.getText().toString();
              String about = aboutstay.getText().toString();
              String type  = typeofstaytextview.getText().toString();
              if (!locationCheckBox.isChecked()){
                  Toast.makeText(AddStayActivity.this, "You Must check the location Checkbox", Toast.LENGTH_SHORT).show();
              }else if(TextUtils.isEmpty(name)){
                  Toast.makeText(AddStayActivity.this, "You must provide name of stay", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(location)){
                  Toast.makeText(AddStayActivity.this, "You must provide city of stay", Toast.LENGTH_SHORT).show();
              }else if(TextUtils.isEmpty(about)){
                  Toast.makeText(AddStayActivity.this, "Write something unique about your stay..", Toast.LENGTH_SHORT).show();
              }
              else if(TextUtils.isEmpty(type)){
                  Toast.makeText(AddStayActivity.this, "Select or Write type of stay", Toast.LENGTH_SHORT).show();
              }else{
                  progressDialog.setTitle("profile info ");
                  progressDialog.setMessage("Please,wait while Uploading your Stay information ....");
                  progressDialog.setCanceledOnTouchOutside(false);
                  progressDialog.show();
                  SaveStayInfo();
              }
            }
        });
    }

    private void SaveStayInfo() {
        String name = nameofstay.getText().toString();
        String location  = locationofstay.getText().toString();
        String about = aboutstay.getText().toString();
        String phone = phoneofstay.getText().toString();
        String type  = typeofstaytextview.getText().toString();
        String Facilities = FacilitiesTextView.getText().toString();
        String Website = websitehotel.getText().toString();
        HashMap<String, String> usermap = new HashMap<>();
        usermap.put("name",name);
        usermap.put("Location",location);
        usermap.put("Longitude",Double.toString(longitude));
        usermap.put("Latitude",Double.toString(latitude));
        usermap.put("Contact",phone);
        usermap.put("Amenities",Facilities);
        usermap.put("about",about);
        usermap.put("website",Website);
        usermap.put("Type",type);
        HashMap<String,ArrayList> userdatamap = new HashMap<>();
        userdatamap.put("Occupancy",Occupancylist);
        userdatamap.put("Tenants",Tenantslist);
        userdatamap.put("Services",Servicelist);
        userdatamap.put("StayImage",stayimageList);
        userdatamap.put("Nearbyimagelist",nearbyimagelist);
        HashMap<String,HashMap> twohashmap = new HashMap<>();
        twohashmap.put("geninfo",usermap);
        twohashmap.put("listsinfo",userdatamap);
         Staydataref.child(Currentuserid).child(Integer.toString(n+1)).setValue(twohashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(AddStayActivity.this, R.string.toast_two, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddStayActivity.this,MainActivity.class));
                }else{
                    progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    Toast.makeText(AddStayActivity.this, "ERROR :"+message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setAdapterRecyclerView(RecyclerView imagerecyclerview, UploadImageAdapter uploadImageAdapter) {
        imagerecyclerview.setLayoutManager(new LinearLayoutManager(this));
//        imagerecyclerview.setHasFixedSize(true);
        imagerecyclerview.setAdapter(uploadImageAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            if (data.getClipData()==null){
                Toast.makeText(this, R.string.select_image, Toast.LENGTH_SHORT).show();
            }
           else if (data.getClipData() != null){
                progressDialog.setMessage("Wait! Image is uploading..");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                //  Toast.makeText(this, "Select Multiple files", Toast.LENGTH_SHORT).show();
                int totalitemselected = data.getClipData().getItemCount();
                switch(ImageType){
                    case "stay":
                        for (int i = 0; i<totalitemselected; i++ ){
                            Uri fileuri = data.getClipData().getItemAt(i).getUri();
                            String filename = getfileName(fileuri);
                            fileNameList2.add(filename);
                            fileDoneList2.add("Uploading");
                            uploadstayImageAdapter.notifyDataSetChanged();
                            StorageReference imageofambience = mstorage.child("restaurentImages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Stay_Image").child(filename);
                            final int finalI = i;
                            imageofambience.putFile(fileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    fileDoneList2.remove(finalI);
                                    fileDoneList2.add(finalI, "done");
                                    uploadstayImageAdapter.notifyDataSetChanged();
                                    if (task.isSuccessful()){
                                        imageofambience.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                stayimageList.add(uri.toString());
                                                int x = stayimageList.size();
                                                if (x == totalitemselected){
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        break;
                    case "nearby":
                        for (int i = 0; i<totalitemselected; i++ ) {
                            Uri fileuri = data.getClipData().getItemAt(i).getUri();
                            String filename = getfileName(fileuri);
                            fileNameList.add(filename);
                            fileDoneList.add("Uploading");
                           uploadnearbyImageAdapter.notifyDataSetChanged();
                            StorageReference imageoffood = mstorage.child("restaurentImages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Nearby_place").child(filename);
                            final int finalL = i;
                            imageoffood.putFile(fileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    fileDoneList.remove(finalL);
                                    fileDoneList.add(finalL, "done");
                                    uploadnearbyImageAdapter.notifyDataSetChanged();
                                    if (task.isSuccessful()){
                                        imageoffood.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                               nearbyimagelist.add(uri.toString());
                                               int x = nearbyimagelist.size();
                                               if (x == totalitemselected){
                                                   progressDialog.dismiss();
                                               }
                                            }
                                        });
                                    }
                                }
                            });
                        } break;
                        }
                }
            }else if(data == null){
                Toast.makeText(this, "Select Single File", Toast.LENGTH_SHORT).show();
            }
        }

    public String getfileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")){
            Cursor cursor =  getContentResolver().query(uri,null,null,null,null);
            try {
                if (cursor != null && cursor.moveToFirst()){
                    result  = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }finally {
                cursor.close();
            }
        }
        if (result == null){
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1){
                result = result.substring(cut+1);
            }
        }
        return  result;
    }
    public void onCheckboxClicked(View view) {
        boolean checked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.Location_Check:
                if (checked){
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AddStayActivity.this,new String[]{ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
                    }else{
                        getCurrentLocation();
                    }
                }
                // Put some meat on the sandwich
                else
                    break;
        }
    }
    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(AddStayActivity.this).requestLocationUpdates(locationRequest,
                new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(AddStayActivity.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocationindex = locationResult.getLocations().size() - 1;
                         latitude = locationResult.getLocations().get(latestlocationindex).getLatitude();
                         longitude = locationResult.getLocations().get(latestlocationindex).getLongitude();
                            Toast.makeText(AddStayActivity.this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                        }

                    } }, Looper.myLooper());
    }
    public void CheckboxListener(View view) {
        boolean ischecked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.Location_Check:
                if (ischecked){
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AddStayActivity.this,new String[]{ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
                    }else{
                        getCurrentLocation();
                    }
                }
                else
                    break;
            case R.id.checkbox1:
                if (ischecked){
                    Servicelist.add(((CheckBox) view).getText().toString());
                }else{
                    Servicelist.remove(((CheckBox) view).getText().toString());

                }break;
            case R.id.checkbox2:
                if (ischecked){
                    Servicelist.add(((CheckBox) view).getText().toString());
                }else{
                    Servicelist.remove(((CheckBox) view).getText().toString());

                }
                break;
            case R.id.checkbox3:
                if (ischecked)
                {
                    Servicelist.add(((CheckBox) view).getText().toString());
                }
                else
                {
                    Servicelist.remove(((CheckBox) view).getText().toString());
                }
                break;
            case R.id.checkbox4:
                if (ischecked)
                {Occupancylist.add(((CheckBox) view).getText().toString()); }
                else
                { Occupancylist.remove(((CheckBox) view).getText().toString()); }
                break;
            case R.id.checkbox5:
                if (ischecked)
                { Occupancylist.add(((CheckBox) view).getText().toString()); }
                else{
                   Occupancylist.remove(((CheckBox) view).getText().toString()); }
                break;
            case R.id.checkbox6:
                if (ischecked){
                   Occupancylist.add(((CheckBox) view).getText().toString());
                }
                else{
                    Occupancylist.remove(((CheckBox) view).getText().toString());
                }
                break;
            case R.id.checkbox7:
                if (ischecked){
                   Tenantslist.add(((CheckBox) view).getText().toString());
                }
                else{
                    Tenantslist.remove(((CheckBox) view).getText().toString());
                }break;
            case R.id.checkbox8:
                if (ischecked){
                   Tenantslist.add(((CheckBox) view).getText().toString());
                }
                else{
                    Tenantslist.remove(((CheckBox) view).getText().toString());
                }
                break;
            case R.id.checkbox9:
                if (ischecked){
                    Tenantslist.add(((CheckBox) view).getText().toString());
                }
                else{
                    Tenantslist.remove(((CheckBox) view).getText().toString());
                }
                break;
            case R.id.checkbox10:
                if (ischecked){
                   Tenantslist.add(((CheckBox) view).getText().toString());
                }
                else{
                   Tenantslist.remove(((CheckBox) view).getText().toString());
                }break;


        }
    }
}