package com.hfad.travelx;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class FeedbackActivity extends AppCompatActivity {
    /* access modifiers changed from: protected */
    private Toolbar suggesttoolbar;
    private EditText suggetionsview;
    private DatabaseReference suggestref;
    private String Currentuserid;
    private AppCompatButton submitbuton;
    @Override // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        suggestref = FirebaseDatabase.getInstance().getReference().child("Suggests");
        Currentuserid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        suggesttoolbar = findViewById(R.id.suggest_toolbar);
        suggesttoolbar.setNavigationIcon(R.drawable.back);
        suggesttoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.like_and_seen.AnonymousClass1 */

            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        suggetionsview = findViewById(R.id.suggets_edittext);
        submitbuton = findViewById(R.id.suggest_button);
        submitbuton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String suggestions = suggetionsview.getText().toString();
                if (TextUtils.isEmpty(suggestions)){
                    Toast.makeText(FeedbackActivity.this, "There is no suggestions..", Toast.LENGTH_SHORT).show();
                }else{
                    suggestref.child(Currentuserid).setValue(suggestions).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(FeedbackActivity.this, "Thank u for your suggestions", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            }
                        }
                    });
                }
            }
        });

    }
}
