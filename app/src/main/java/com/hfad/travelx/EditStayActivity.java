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
import android.widget.TextView;
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

public class EditStayActivity extends AppCompatActivity {
    private MultiAutoCompleteTextView FacilitiesTextView;
    private RecyclerView stayrecyclerview,nearbyrecyclerview;
    private ImageButton addImagestay,addImagenearby;
    private String ImageType;
    private List<String> fileNameList,fileNameList2;
    private List<String> fileDoneList,fileDoneList2;
    UploadImageAdapter uploadstayImageAdapter,uploadnearbyImageAdapter;
    private StorageReference mstorage;
    private static final int RESULT_LOAD_IMAGE = 1;
    private ArrayList<String> Servicelist,Occupancylist,Tenantslist;
    private ArrayList<String> stayimageList,nearbyimagelist;
    private DatabaseReference Staydataref;
    private String Currentuserid,name,city;
    private ProgressDialog progressDialog;
    private Button saveinfobutton;
    private EditText phoneofstay,websitehotel,request_stay;
    private TextInputEditText aboutstay;
    private TextView Displayname;
    private  Bundle bundle;
    private int n;
    String[] Facilities = new String[]{
            "Wifi","Swimming poll","parking facility","Food Facility","AC","TV","Power backup","CCTV Cameras","Reception","Security","Daily Housekeeping","First Aid",
            "Fire Extinguisher","Attached Bathroom","Mess","Laundry"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_stay);
        bundle = getIntent().getExtras();
        name = bundle.getString("name");
        city = bundle.getString("city");
        Displayname = (TextView)findViewById(R.id.nameofstay_edit1);
        saveinfobutton = (Button)findViewById(R.id.save_info_button_edit1);
        FacilitiesTextView = findViewById(R.id.facilities_Textview_edit1);
        addImagestay = (ImageButton)findViewById(R.id.add_image_stay_edit1);
        addImagenearby = (ImageButton)findViewById(R.id.add_nearby_edit1);
        stayrecyclerview = findViewById(R.id.Stayrecyclerview_edit1);
        nearbyrecyclerview = findViewById(R.id.nearbyPlacesview_edit1);
        phoneofstay = (EditText)findViewById(R.id.phoneofstay_edit1);
        websitehotel = (EditText)findViewById(R.id.website_hotel_edit1);
        aboutstay = (TextInputEditText)findViewById(R.id.edittext_aboutstay_edit1);
        request_stay = (EditText)findViewById(R.id.request_stay_edit);
        Staydataref = FirebaseDatabase.getInstance().getReference().child("EditStay");
        Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Displayname.setText(name);
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
                    progressDialog.setTitle("profile info ");
                    progressDialog.setMessage("Please,wait while Uploading your Stay information ....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    SaveStayInfo();

            }
        });
    }

    private void SaveStayInfo() {
        String about = aboutstay.getText().toString();
        String phone = phoneofstay.getText().toString();
        String Facilities = FacilitiesTextView.getText().toString();
        String Website = websitehotel.getText().toString();
        String request = request_stay.getText().toString();
        HashMap<String, String> usermap = new HashMap<>();
        usermap.put("name",name);
        usermap.put("city",city);
        usermap.put("Contact",phone);
        usermap.put("Amenities",Facilities);
        usermap.put("about",about);
        usermap.put("website",Website);
        usermap.put("request",request);
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
                    Toast.makeText(EditStayActivity.this, "Your Hotel Info saved! it will be shown there in few hours..", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    Toast.makeText(EditStayActivity.this, "ERROR :"+message, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void setAdapterRecyclerView(RecyclerView imagerecyclerview, UploadImageAdapter uploadImageAdapter) {
        imagerecyclerview.setLayoutManager(new LinearLayoutManager(this));
        imagerecyclerview.setAdapter(uploadImageAdapter);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        progressDialog.setMessage("Wait! Image is uploading..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK){
            if (data.getClipData() != null){
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
        }else if(data.getClipData() != null){
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
    public void CheckboxListener(View view) {
        boolean ischecked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.checkbox1_edit1:
                if (ischecked){
                    Servicelist.add(((CheckBox) view).getText().toString());
                }else{
                    Servicelist.remove(((CheckBox) view).getText().toString());

                }break;
            case R.id.checkbox2_edit1:
                if (ischecked){
                    Servicelist.add(((CheckBox) view).getText().toString());
                }else{
                    Servicelist.remove(((CheckBox) view).getText().toString());

                }
                break;
            case R.id.checkbox3_edit1:
                if (ischecked)
                {
                    Servicelist.add(((CheckBox) view).getText().toString());
                }
                else
                {
                    Servicelist.remove(((CheckBox) view).getText().toString());
                }
                break;
            case R.id.checkbox4_edit1:
                if (ischecked)
                {Occupancylist.add(((CheckBox) view).getText().toString()); }
                else
                { Occupancylist.remove(((CheckBox) view).getText().toString()); }
                break;
            case R.id.checkbox5_edit1:
                if (ischecked)
                { Occupancylist.add(((CheckBox) view).getText().toString()); }
                else{
                    Occupancylist.remove(((CheckBox) view).getText().toString()); }
                break;
            case R.id.checkbox6_edit1:
                if (ischecked){
                    Occupancylist.add(((CheckBox) view).getText().toString());
                }
                else{
                    Occupancylist.remove(((CheckBox) view).getText().toString());
                }
                break;
            case R.id.checkbox7_edit1:
                if (ischecked){
                    Tenantslist.add(((CheckBox) view).getText().toString());
                }
                else{
                    Tenantslist.remove(((CheckBox) view).getText().toString());
                }break;
            case R.id.checkbox8_edit1:
                if (ischecked){
                    Tenantslist.add(((CheckBox) view).getText().toString());
                }
                else{
                    Tenantslist.remove(((CheckBox) view).getText().toString());
                }
                break;
            case R.id.checkbox9_edit1:
                if (ischecked){
                    Tenantslist.add(((CheckBox) view).getText().toString());
                }
                else{
                    Tenantslist.remove(((CheckBox) view).getText().toString());
                }
                break;
            case R.id.checkbox10_edit1:
                if (ischecked){
                    Tenantslist.add(((CheckBox) view).getText().toString());
                }
                else{
                    Tenantslist.remove(((CheckBox) view).getText().toString());
                }break;
        }
    }
}