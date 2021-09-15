package com.hfad.travelx;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Toast;

import com.google.android.exoplayer2.C;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.*;
import static android.location.LocationManager.*;

public class AddRestaurentActivity extends AppCompatActivity {
    private ImageButton addimage,addfoodimage,addmenuImage;
    private RecyclerView Imagerecyclerview,FoodRecyclerView,MenuRecyclerView;
    private MultiAutoCompleteTextView multiAutoCompleteTextView;
    private static final int RESULT_LOAD_IMAGE = 1;
    private List<String> fileNameList,fileNameList2,fileNameList3;
    private List<String> fileDoneList,fileDoneList2,fileDonelist3;
    UploadImageAdapter uploadImageAdapter,uploadfoodAdapter,uploadMenuAdapter;
    private StorageReference mstorage;
    private String imagetype  = null;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;
    private String imageurl;
    private EditText name,city,phone;
    private TextInputEditText aboutrestview;
    private EditText WebsiteView ;
    String[] Colors = new String[]{
            "Kashmiri","Gujrati","Bengali",
            "Bihari","marathi","Udipi","Sindhi","Kumaoni","Naga","Malvani","Parsi","Kathiawadi","Mappila","Telangana"
            ,"Odia","Malenadu","Konkani","Adivasi","Saraswat","Mizo","Punjabi","Rajasthani","Awadhi","Kumauni","Italian",
            "Chinese","American","Mexican","French","Thai","Emirati"
    };
    private DatabaseReference RestaurentDataref;
    private String Currentuserid,timeanddate;
    private int n;
    private  Button SaveButton;
    private ProgressDialog progressDialog;
    private MultiAutoCompleteTextView cusinetextview;
    private  ArrayList<String> serviceslist,paylist,dayslist;
    private ArrayList<String> ambienceimageslist,foodimageslist,menuimageslist;
    private double longitude,latitude;
    private CheckBox locationbox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_restaurent);
        SaveButton = findViewById(R.id.save_restarentData);
        Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        RestaurentDataref = FirebaseDatabase.getInstance().getReference();
        name = findViewById(R.id.nameofrestaurent);
        city = findViewById(R.id.cityofrestaurant);
        phone = findViewById(R.id.phoneofreataurant);
        cusinetextview = findViewById(R.id.CuisineTextView);
        aboutrestview = findViewById(R.id.edittext_yourself);
        WebsiteView = findViewById(R.id.website_restaurant);
        locationbox = (CheckBox)findViewById(R.id.location_checkbox);
        serviceslist = new ArrayList<String>();
        paylist = new ArrayList<String>();
        dayslist = new ArrayList<String>();
        ambienceimageslist = new ArrayList<String>();
        foodimageslist = new ArrayList<String>();
        menuimageslist = new ArrayList<String>();
        RestaurentDataref.child("Restaurant").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                 n = (int) snapshot.child(Currentuserid).getChildrenCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        addimage = findViewById(R.id.add_image);
        addfoodimage = findViewById(R.id.add_food);
        addmenuImage = findViewById(R.id.add_menu);
        Imagerecyclerview = (RecyclerView) findViewById(R.id.Imagerecyclerview);
        FoodRecyclerView = (RecyclerView) findViewById(R.id.foodrecyclerview);
        MenuRecyclerView = (RecyclerView) findViewById(R.id.menurecyclerview);
        fileNameList = new ArrayList<String>();
        fileDoneList = new ArrayList<String>();
        fileDoneList2 = new ArrayList<String>();
        fileNameList2 = new ArrayList<String>();
        fileDonelist3 = new ArrayList<String>();
        fileNameList3 = new ArrayList<String>();
        progressDialog = new ProgressDialog(this);
        mstorage = FirebaseStorage.getInstance().getReference();
        uploadImageAdapter = new UploadImageAdapter(fileNameList,fileDoneList);
        uploadfoodAdapter  = new UploadImageAdapter(fileNameList2,fileDoneList2);
        uploadMenuAdapter = new UploadImageAdapter(fileNameList3,fileDonelist3);
        setAdapterRecyclerView(Imagerecyclerview,uploadImageAdapter);
        setAdapterRecyclerView(FoodRecyclerView,uploadfoodAdapter);
        setAdapterRecyclerView(MenuRecyclerView,uploadMenuAdapter);
        addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagetype = "ambience";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_LOAD_IMAGE);
            }
        });
        addfoodimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagetype = "food";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_LOAD_IMAGE);
                Toast.makeText(AddRestaurentActivity.this, "Select Multiple Images", Toast.LENGTH_SHORT).show();
            }
        });
        addmenuImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagetype = "menu";
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"),RESULT_LOAD_IMAGE);
                Toast.makeText(AddRestaurentActivity.this, "Select Multiple Images", Toast.LENGTH_SHORT).show();
            }
        });
        multiAutoCompleteTextView  = findViewById(R.id.CuisineTextView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.simple_list_item_1,Colors);
       // ArrayAdapter<String> cuisineAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,cuisinelist);
        multiAutoCompleteTextView.setAdapter(adapter);
        multiAutoCompleteTextView.setThreshold(1);
        multiAutoCompleteTextView.setDropDownWidth(300);
        multiAutoCompleteTextView.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        multiAutoCompleteTextView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                multiAutoCompleteTextView.showDropDown();
                return false;
            }
        });
        SaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameofrest = name.getText().toString();
                String cityofrest = city.getText().toString();
                String Aboutrest = aboutrestview.getText().toString();
                if (!locationbox.isChecked()) {
                    Toast.makeText(AddRestaurentActivity.this, "Location CheckBox Must Checked", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(nameofrest)) {
                    Toast.makeText(AddRestaurentActivity.this, "You must provide name of restaurant", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(cityofrest)) {
                    Toast.makeText(AddRestaurentActivity.this, "You must provide location of Restaurant", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(Aboutrest)) {
                    Toast.makeText(AddRestaurentActivity.this, "Write something About your Restaurant", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.setTitle("profile info ");
                    progressDialog.setMessage("Please,wait while Uploading your Restaurant information ....");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    SaveRestaurantInfo();
                }
            }
// will give feature for users to add their restaurant,stay ,dest from top main activity also
            // will put explore fragment at first and post fragment at second
            // for big cities like delhi,mumbai,pathna,banglore etc they will be break in to parts such that user can serch particular areas and retaurant in that cities directly
            //we will later(may be in update) will give feature to popularise or advertise to particular on the basis of some condition or money.
        // add mutipleimage of restaurent,menu image odf restaurant,location,otp verification such that they could login any time later on..
        // there will be button on restaurant activity visible to only owner of that restaurant page such that he can change any thing on that page by doing otp verification..
    });
    }

    private void SaveRestaurantInfo() {
        String nameofrest  = name.getText().toString();
        String cityofrest = city.getText().toString();
        String phoneofrest = phone.getText().toString();
        String Cuisinetext = cusinetextview.getText().toString();
        String Aboutrest = aboutrestview.getText().toString();
        String Website = WebsiteView.getText().toString();
        HashMap<String, String> usermap = new HashMap<>();
        usermap.put("name",nameofrest);
        usermap.put("Location",cityofrest);
        usermap.put("Longitude",Double.toString(longitude));
        usermap.put("Latitude",Double.toString(latitude));
        usermap.put("Contact",phoneofrest);
        usermap.put("Cuisine",Cuisinetext);
        usermap.put("about",Aboutrest);
        usermap.put("website",Website);
        HashMap<String,ArrayList> userdatamap = new HashMap<>();
           userdatamap.put("daysOpen",dayslist);
           userdatamap.put("services",serviceslist);
           userdatamap.put("pay",paylist);
           userdatamap.put("AmbienceImages",ambienceimageslist);
           userdatamap.put("FoodImages",foodimageslist);
           userdatamap.put("menuImages",menuimageslist);
           HashMap<String,HashMap> twohashmap = new HashMap<>();
            twohashmap.put("geninfo",usermap);
            twohashmap.put("listsinfo",userdatamap);
        FirebaseDatabase.getInstance().getReference().child("Restaurant").child(Currentuserid).child(Integer.toString(n+1)).setValue(twohashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(AddRestaurentActivity.this, "Your Hotel Info saved ! it will be shown there in few hours..", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddRestaurentActivity.this,MainActivity.class));
                }else{
                    progressDialog.dismiss();
                    String message = task.getException().getMessage();
                    Toast.makeText(AddRestaurentActivity.this, "ERROR :"+message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onCheckboxClicked(View view) {
        boolean ischecked = ((CheckBox) view).isChecked();
        switch(view.getId()) {
            case R.id.location_checkbox:
                if (ischecked){
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                        ActivityCompat.requestPermissions(AddRestaurentActivity.this,new String[]{ACCESS_FINE_LOCATION},REQUEST_CODE_LOCATION_PERMISSION);
                    }else{
                        getCurrentLocation();
                    }
                }
               else
                   break;
            case R.id.checkbox11:
                if (ischecked){
                      serviceslist.add(((CheckBox) view).getText().toString());
                }else{
                    serviceslist.remove(((CheckBox) view).getText().toString());

                }break;
            case R.id.checkbox12:
                if (ischecked){
                   serviceslist.add(((CheckBox) view).getText().toString());
                }else{
                    serviceslist.remove(((CheckBox) view).getText().toString());

                }
                break;
            case R.id.checkbox13:
                if (ischecked)
                 {
                     serviceslist.add(((CheckBox) view).getText().toString());
                 }
                else
                    {
                        serviceslist.remove(((CheckBox) view).getText().toString());
                    }
                break;
            case R.id.checkbox14:
                if (ischecked)
                {serviceslist.add(((CheckBox) view).getText().toString()); }
                else
                    { serviceslist.remove(((CheckBox) view).getText().toString()); }
                break;
            case R.id.checkbox15:
                if (ischecked)
                { serviceslist.add(((CheckBox) view).getText().toString()); }
                else{
                        serviceslist.remove(((CheckBox) view).getText().toString()); }
                break;
            case R.id.checkbox21:
                if (ischecked){
                    paylist.add(((CheckBox) view).getText().toString());
                }
                else{
                        paylist.remove(((CheckBox) view).getText().toString());
                    }
                break;
            case R.id.checkbox22:
                if (ischecked){
                    paylist.add(((CheckBox) view).getText().toString());
                }
                else{
                        paylist.remove(((CheckBox) view).getText().toString());
                }break;
            case R.id.checkbox23:
                if (ischecked){
                    paylist.add(((CheckBox) view).getText().toString());
                }
                else{
                        paylist.remove(((CheckBox) view).getText().toString());
                    }
                break;
            case R.id.checkbox31:
                if (ischecked){
                    dayslist.add(((CheckBox) view).getText().toString());
                }
                else{
                        dayslist.remove(((CheckBox) view).getText().toString());
                    }
                break;
            case R.id.checkbox32:
                if (ischecked){
                    dayslist.add(((CheckBox) view).getText().toString());
                }
                else{
                        dayslist.remove(((CheckBox) view).getText().toString());
                }break;
            case R.id.checkbox33:
                if (ischecked){
                    dayslist.add(((CheckBox) view).getText().toString());
                }
                else{
                        dayslist.remove(((CheckBox) view).getText().toString());
                    }
                break;
            case R.id.checkbox34:
                if (ischecked){
                    dayslist.add(((CheckBox) view).getText().toString());
                }
                else{
                        dayslist.remove(((CheckBox) view).getText().toString());
                }break;
            case R.id.checkbox35:
                if (ischecked){
                    dayslist.add(((CheckBox) view).getText().toString());
                }
                else{
                        dayslist.remove(((CheckBox) view).getText().toString());
                    }
                break;
            case R.id.checkbox36:
                if (ischecked){
                    dayslist.add(((CheckBox) view).getText().toString());
                }
                else{
                        dayslist.remove(((CheckBox) view).getText().toString());
                    }
                break;
            case R.id.checkbox37:
                if (ischecked){
                    dayslist.add(((CheckBox) view).getText().toString());
                }
                else{
                        dayslist.remove(((CheckBox) view).getText().toString());
                }
             break;

        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length>0){
            if (grantResults[0] == PERMISSION_GRANTED){
                getCurrentLocation();
            }else{
                Toast.makeText(this, "permission_denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getCurrentLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.getFusedLocationProviderClient(AddRestaurentActivity.this).requestLocationUpdates(locationRequest,
                new LocationCallback(){
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(AddRestaurentActivity.this).removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestlocationindex = locationResult.getLocations().size() - 1;
                           latitude = locationResult.getLocations().get(latestlocationindex).getLatitude();
                           longitude = locationResult.getLocations().get(latestlocationindex).getLongitude();
                            Toast.makeText(AddRestaurentActivity.this, latitude + " " + longitude, Toast.LENGTH_SHORT).show();
                        }

                    } }, Looper.myLooper());
    }

    private void setAdapterRecyclerView(RecyclerView imagerecyclerview, UploadImageAdapter uploadImageAdapter) {
        imagerecyclerview.setLayoutManager(new LinearLayoutManager(this));
        imagerecyclerview.setAdapter(uploadImageAdapter);
    }

    @Override
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
                  switch(imagetype){
                          case "ambience":
                              for (int i = 0; i<totalitemselected; i++ ){
                                  Uri fileuri = data.getClipData().getItemAt(i).getUri();
                                  String filename = getfileName(fileuri);
                              fileNameList.add(filename);
                              fileDoneList.add("Uploading");
                              uploadImageAdapter.notifyDataSetChanged();

                              StorageReference imageofambience = mstorage.child("restaurentImages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("ambience").child(filename);
                              final int finalI = i;
                              imageofambience.putFile(fileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                  @Override
                                  public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                      fileDoneList.remove(finalI);
                                      fileDoneList.add(finalI, "done");
                                      uploadImageAdapter.notifyDataSetChanged();
                                      if (task.isSuccessful()){
                                         imageofambience.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                             @Override
                                             public void onSuccess(Uri uri) {
                                              ambienceimageslist.add(uri.toString());
                                                 int x =  ambienceimageslist.size();
                                                 if(x == totalitemselected){
                                                     progressDialog.dismiss();
                                                 }
                                             }
                                         });
                                      }
                                  }
                              });
                      }
                              break;
                      case "food":
                          for (int i = 0; i<totalitemselected; i++ ) {
                              Uri fileuri = data.getClipData().getItemAt(i).getUri();
                              String filename = getfileName(fileuri);
                              fileNameList2.add(filename);
                              fileDoneList2.add("Uploading");
                              uploadfoodAdapter.notifyDataSetChanged();
                              StorageReference imageoffood = mstorage.child("restaurentImages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("food").child(filename);
                              final int finalL = i;
                              imageoffood.putFile(fileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                  @Override
                                  public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                      fileDoneList2.remove(finalL);
                                      fileDoneList2.add(finalL, "done");
                                      uploadfoodAdapter.notifyDataSetChanged();
                                      if (task.isSuccessful()){
                                        imageoffood.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                               foodimageslist.add(uri.toString());
                                                int x =  foodimageslist.size();
                                                if(x == totalitemselected){
                                                    progressDialog.dismiss();
                                                }
                                            }
                                        });
                                      }
                                  }
                              });
                          } break;
                      case "menu":
                          for (int i = 0; i<totalitemselected; i++ ){
                              Uri fileuri = data.getClipData().getItemAt(i).getUri();
                              String filename = getfileName(fileuri);
                          fileNameList3.add(filename);
                          fileDonelist3.add("Uploading");
                          uploadMenuAdapter.notifyDataSetChanged();
                          StorageReference imageofmenu = mstorage.child("restaurentImages").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("menu").child(filename);
                          final  int finalM = i;
                          imageofmenu.putFile(fileuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                              @Override
                              public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                  fileDonelist3.remove(finalM);
                                  fileDonelist3.add(finalM,"done");
                                  uploadMenuAdapter.notifyDataSetChanged();
                                  if (task.isSuccessful()){
                                      imageofmenu.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                          @Override
                                          public void onSuccess(Uri uri) {
                                             menuimageslist.add(uri.toString());
                                              int x =  menuimageslist.size();
                                              if(x == totalitemselected){
                                                  progressDialog.dismiss();
                                              }
                                          }
                                      });
                                  }
                              }
                          });
                  }
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
}