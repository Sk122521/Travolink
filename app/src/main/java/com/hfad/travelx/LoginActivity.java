package com.hfad.travelx;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.test.filters.LargeTest;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.SignInAccount;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.crashlytics.internal.model.CrashlyticsReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";
    private ProgressDialog loadingbar;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mauth;
    public String username, photourl;
    private DatabaseReference userref;
    private CallbackManager mcallbackmanager;
    private LoginButton loginButton;
    private static final String TAGGED = "FacebookAuthentication";
   private AccessTokenTracker accessTokenTracker;
  // private LinearLayout fb;

    /* access modifiers changed from: protected */
    @Test
    @LargeTest
    @Override
    // androidx.activity.ComponentActivity, androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loadingbar = new ProgressDialog(this);
        mauth = FirebaseAuth.getInstance();
        mcallbackmanager = CallbackManager.Factory.create();
        userref = FirebaseDatabase.getInstance().getReference();
        loginButton = findViewById(R.id.fb_login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingbar.setTitle("Facebook Sign in");
                loadingbar.setMessage("Please wait a while! You are signing in travolink through Facebook....");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email","public_profile"));
                LoginManager.getInstance().registerCallback(mcallbackmanager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() { }
                    @Override
                    public void onError(FacebookException error) { }
                });
            }
        });
        SignInButton signInButton = (SignInButton) findViewById(R.id.google_login_button);
        signInButton.setSize(0);
        mGoogleSignInClient = GoogleSignIn.getClient((Activity) this, new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build());
        signInButton.setOnClickListener(new View.OnClickListener() {
            /* class com.hfad.travelx.LoginActivity.AnonymousClass1 */

            public void onClick(View view) {
                SignIn();
            }
        });
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void SignIn() {
        startActivityForResult(this.mGoogleSignInClient.getSignInIntent(), 1);
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            try {
                loadingbar.setTitle("Google Sign in");
                loadingbar.setMessage("Please wait a while! you are signing in travolink through Google....");
                loadingbar.setCanceledOnTouchOutside(false);
                loadingbar.show();
                GoogleSignInAccount account = GoogleSignIn.getSignedInAccountFromIntent(data).getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken(), account);
            } catch (ApiException e) {
                Toast.makeText(this, "", Toast.LENGTH_LONG).show();
            }
        }
        mcallbackmanager.onActivityResult(requestCode,resultCode,data);
    }

    private void firebaseAuthWithGoogle(String Idtoken, GoogleSignInAccount account) {
        mauth.signInWithCredential(GoogleAuthProvider.getCredential(Idtoken, null)).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            /* class com.hfad.travelx.LoginActivity.AnonymousClass2 */

            @Override // com.google.android.gms.tasks.OnCompleteListener
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    loadingbar.dismiss();
                    username = mauth.getCurrentUser().getDisplayName();
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (!dataSnapshot.exists()) {
                                saveAccountsetupInformation(username);
                            } else {
                                SendUserToMainActivity();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Log.d(TAG, databaseError.getMessage()); //Don't ignore errors!
                        }
                    };
                    userref.child("Users").child(mauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(eventListener);
                    return;
                } else {
                    loadingbar.dismiss();
                    LoginActivity.this.sendusertologinactivity();
                    Toast.makeText(LoginActivity.this, "not authenticated try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStart() {
        super.onStart();
        if (mauth.getCurrentUser() != null) {
            SendUserToMainActivity();
        }
    }
    private void handleFacebookAccessToken(AccessToken token) {
        //Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mauth.getCurrentUser();
                            ValueEventListener eventListener = new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (!dataSnapshot.exists()) {
                                        saveAccountsetupInformation(user.getDisplayName());
                                    } else {
                                      SendUserToMainActivity();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Log.d(TAG, databaseError.getMessage());
                                    //Don't ignore errors!
                                }
                            };
                            userref.child("Users").child(mauth.getCurrentUser().getUid()).addListenerForSingleValueEvent(eventListener);
                            ;
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void saveAccountsetupInformation(String username) {
        HashMap<String, String> usermap = new HashMap<>();
        usermap.put("name", username.toLowerCase());
        usermap.put("address", "");
        usermap.put("website", "");
        usermap.put(NotificationCompat.CATEGORY_STATUS, "");
        usermap.put("image", "");
        usermap.put("bio", "");
        usermap.put("hdrimg","");
        usermap.put("uid", mauth.getCurrentUser().getUid());
        FirebaseDatabase.getInstance().getReference().child("Users").child(mauth.getCurrentUser().getUid()).setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            /* class com.hfad.travelx.SetUpActivity.AnonymousClass2 */

            @Override // com.google.android.gms.tasks.OnCompleteListener
            public void onComplete(Task<Void> task) {
                if (task.isSuccessful()) {
                    SendUserToMainActivity();
                    Toast.makeText(LoginActivity.this, "Your Account Created...", Toast.LENGTH_SHORT).show();
                   return;
                }
                String message = task.getException().toString();
                Toast.makeText(LoginActivity.this, "ERROR : " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void SendUserToMainActivity() {
        Intent mainintent = new Intent(LoginActivity.this, MainActivity.class);
        //mainintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainintent);
      //  finish();
    }
    private void sendusertologinactivity() {
        startActivity(new Intent(this, LoginActivity.class));}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (loadingbar != null && loadingbar.isShowing()) {
            loadingbar.cancel();
        }
    }

}

