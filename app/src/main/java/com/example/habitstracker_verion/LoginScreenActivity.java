package com.example.habitstracker_verion;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.habitstracker_verion.models.Entry;
import com.example.habitstracker_verion.models.FirebaseEntryParam;
import com.example.habitstracker_verion.models.FirebaseParam;
import com.example.habitstracker_verion.models.Track;
import com.example.habitstracker_verion.utils.AppUtils;
import com.example.habitstracker_verion.utils.Constants;
import com.example.habitstracker_verion.views.DashboardActivity;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmList;

public class LoginScreenActivity extends AppCompatActivity {
    private static final String TAG = "LoginScreenActivity";
    SignInButton signInButton;
    //    private GoogleApiClient googleApiClient;
    private GoogleSignInClient mSignInClient;

    private static final int RC_SIGN_IN = 1;
    String name, email;
    String idToken;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private DatabaseReference mDatabase;
    ArrayList<FirebaseParam> paramArrayList = new ArrayList<>();
    Realm mRealm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getInit();
        launchHomeScreen();
    }

    private void launchHomeScreen() {
        boolean isLogin = AppUtils.getBooleanPreference(this, Constants.isLogin);
        if (!isLogin) {
//            startActivity(new Intent(LoginScreenActivity.this, LoginScreenActivity.class));
//            finish();
        } else {
            startActivity(new Intent(LoginScreenActivity.this, DashboardActivity.class));
            finish();
        }
    }

    private void getInit() {
        mRealm = Realm.getDefaultInstance();
        firebaseAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        authStateListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                // Get signedIn user
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                //  getDataFromFirebase(user.getUid());
//                //if user is signed in, we call a helper method to save the user details to Firebase
//                if (user != null) {
//                    // User is signed in
//                    // you could place other firebase code
//                    //logic to save the user details to Firebase
//                    Log.e(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
//                } else {
//                    // User is signed out
//                    Log.e(TAG, "onAuthStateChanged:signed_out");
//                }
//            }
//        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        mSignInClient = GoogleSignIn.getClient(this, gso);

        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
//                startActivityForResult(intent, RC_SIGN_IN);
                Intent intent = mSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
    }

    private void getDataFromFirebase(String uid) {
        mDatabase.child("Tracks").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()) {
                    //Toast.makeText(LoginScreenActivity.this, "Successfull", Toast.LENGTH_SHORT).show();
                    for (DataSnapshot snapshot : task.getResult().getChildren()) {
                        FirebaseParam param = snapshot.getValue(FirebaseParam.class);
                        paramArrayList.add(param);
                    }
                    Toast.makeText(LoginScreenActivity.this, "Data fetched " + paramArrayList.size(), Toast.LENGTH_SHORT).show();
                    addInDatabase(paramArrayList);
                } else {
                    // Toast.makeText(LoginScreenActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    //Log.e("FirebaseError",task.getException().getMessage());
                }
            }
        });
    }

    private void addInDatabase(ArrayList<FirebaseParam> paramArrayList) {
        Realm realm = null;

        try {
            realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    for (int i = 0; i < paramArrayList.size(); i++) {
                        //realm.beginTransaction();
                        RealmList<Entry> entries = new RealmList<>();
                        FirebaseParam param = paramArrayList.get(i);
                        for (int j = 0; j < param.getEntries().size(); j++) {
                            FirebaseEntryParam firebaseEntryParam = param.getEntries().get(j);
                            Entry entry = realm.createObject(Entry.class, getNextEntryKey());
                            //Entry entry = new Entry();
                            entry.setValue(firebaseEntryParam.getValue());
                            entry.setDate(firebaseEntryParam.getDate());
                            entries.add(entry);
                        }

                        Track track = realm.createObject(Track.class, getNextKey());
                        //Track track = new Track();
                        track.setName(param.getName());
                        track.setUnit(param.getUnit());
                        track.setEntries(entries);
                        track.setColor(param.getColor());
                        realm.insert(track);
                        //   realm.commitTransaction();
                    }

                    Toast.makeText(LoginScreenActivity.this, "Data added " + paramArrayList.size(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Exception :- " + e.getMessage());
        } finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    public int getNextKey() {
        try {
            Number number = mRealm.where(Track.class).max("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    public int getNextEntryKey() {
        try {
            Number number = mRealm.where(Entry.class).max("id");
            if (number != null) {
                return number.intValue() + 1;
            } else {
                return 0;
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            getDataFromFirebase(result.getSignInAccount().getId());
            GoogleSignInAccount account = result.getSignInAccount();
            idToken = account.getIdToken();
            name = account.getDisplayName();
            email = account.getEmail();
            AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
            firebaseAuthWithGoogle(credential);
            AppUtils.setStringPreference(this, Constants.UserEmail, email);
            AppUtils.setStringPreference(this, Constants.UserName, name);
            AppUtils.setStringPreference(this, Constants.IDToken, idToken);
            AppUtils.setStringPreference(this, Constants.UserId, result.getSignInAccount().getId());
        } else {
            Log.e(TAG, "Login Unsuccessful. " + result.getStatus().toString());
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(AuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()) {
                            //  Toast.makeText(LoginScreenActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            gotoProfile();
                        } else {
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(LoginScreenActivity.this, "Authentication failed! Please try again",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void gotoProfile() {
        AppUtils.setBooleanPreference(this, Constants.isLogin, true);
        Intent intent = new Intent(LoginScreenActivity.this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        if (authStateListener != null) {
//            FirebaseAuth.getInstance().signOut();
//        }
//        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
//        if (authStateListener != null) {
//            firebaseAuth.removeAuthStateListener(authStateListener);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

}