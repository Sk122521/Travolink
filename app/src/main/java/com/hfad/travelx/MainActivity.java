package com.hfad.travelx;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ActivityNavigator;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private String Currentuserid;
    private AppBarConfiguration appBarConfiguration;
    private BottomNavigationView bottomNavigationView;
    private FirebaseUser currentuser;
    private FirebaseAuth mauth;
    private NavController navcontroller;
    private DatabaseReference userref;
    private DrawerLayout drawerLayout;
    private String name;
    private File storage;
    private String[] storagePaths;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //FirebaseAnalytics.getInstance(this).logEvent("main_activity_ready",null);
        FirebaseAuth instance = FirebaseAuth.getInstance();
        mauth = instance;
        FirebaseUser currentUser = instance.getCurrentUser();
        currentuser = currentUser;
        Currentuserid = currentUser.getUid();
        userref = FirebaseDatabase.getInstance().getReference();
        drawerLayout = findViewById(R.id.drawable_layout);
        if (currentuser == null) {
            SendUsertoLoginActivity();
        }
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_nav_view);
        navcontroller = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment)).getNavController();
        appBarConfiguration = new AppBarConfiguration.Builder(R.id.Video,R.id.posts,R.id.addPost,R.id.travel,R.id.notification).setDrawerLayout(drawerLayout).build();
        NavigationUI.setupWithNavController(bottomNavigationView, navcontroller);
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(MainActivity.this, new OnSuccessListener<InstanceIdResult>() {
            public void onSuccess(InstanceIdResult instanceIdResult) {
                FirebaseDatabase.getInstance().getReference().child("Users").child(Currentuserid).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        name = snapshot.child("name").getValue().toString();
                        Map<String, String> instanceid = new HashMap<>();
                        instanceid.put("Notification_token", instanceIdResult.getToken());
                        instanceid.put("name",name);
                        FirebaseFirestore.getInstance().collection("Users").document(Currentuserid)
                                .set(instanceid).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (!task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        }
        else {
            super.onBackPressed();
        }
    }
    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStop() {
        super.onStop();
        // String time = new SimpleDateFormat("HH:mm").format(Calendar.getInstance().getTime());
        //  UpdatechatStatus("Chatoff");
    }

    private void UpdatechatStatus(String state) {
        HashMap<String, Object> OnlineStateMap = new HashMap<>();
        OnlineStateMap.put("onlinestatus", state);
        FirebaseDatabase.getInstance().getReference().child("Users").child(Currentuserid).child("Chatstate").updateChildren(OnlineStateMap);
    }
    @Override
    public void onStart() {
        super.onStart();
        if (currentuser == null) {
            SendUsertoLoginActivity();
            return;
        }else{
            UpdatechatStatus("Chaton");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        UpdatechatStatus("Chaton");
    }
    private void SendUsertoLoginActivity() {
        Intent loginintent = new Intent(this, LoginActivity.class);
        loginintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginintent);
        finish();
    }

    public void finish() {
        super.finish();
        UpdatechatStatus("Chaton");
        ActivityNavigator.applyPopAnimationsToPendingTransition(this);
    }
}