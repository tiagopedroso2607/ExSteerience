package com.example.grupo7.daam.myexsteerience;

    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.net.ConnectivityManager;
    import android.net.NetworkInfo;
    import android.os.Bundle;
    import android.view.View;

    import com.example.grupo7.daam.myexsteerience.Accessory.BaseActivity;
    import com.example.grupo7.daam.myexsteerience.Objects.User;
    import com.example.grupo7.daam.myexsteerience.Objects.UserInfo;
    import com.google.android.gms.auth.api.Auth;
    import android.content.Intent;
    import android.support.annotation.NonNull;
    import android.widget.TextView;
    import android.widget.Toast;
    import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
    import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
    import com.google.android.gms.auth.api.signin.GoogleSignInResult;
    import com.google.android.gms.common.ConnectionResult;
    import com.google.android.gms.common.api.GoogleApiClient;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.AuthCredential;
    import com.google.firebase.auth.AuthResult;
    import com.google.firebase.auth.FirebaseAuth;
    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.auth.GoogleAuthProvider;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.io.IOException;
    import java.util.Calendar;

public class LoginActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private GoogleApiClient nGoogleApiClient;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private static  final int RC_SIGN_IN = 1;

    private User user;

    private Boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                mUser = firebaseAuth.getCurrentUser();
                if (mUser != null) {
                    signInApp(mUser);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        findViewById(R.id.button).setOnClickListener( this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        nGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null)
            mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                hideProgressDialog();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
// Clearing default account every time so that the account picker dialog can be enforced
        if ( nGoogleApiClient.isConnected()) {
            nGoogleApiClient.clearDefaultAccountAndReconnect();
        }
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            Toast.makeText(LoginActivity.this, getString(R.string.erro_authentication),
                                    Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Toast.makeText(this, getString(R.string.erro_google_play), Toast.LENGTH_SHORT).show();
        hideProgressDialog();
    }

    @Override
    public void onBackPressed() {
        if (exit) {
            finish(); // finish activity
        } else {
            Toast.makeText(this, getString(R.string.log_back),
                    Toast.LENGTH_SHORT).show();
            exit = true;


        }
    }

    @Override
    public void onClick(View v) {
        methodInternet();

    }

    private void signInGoogle() {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(nGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    private void signInApp(FirebaseUser user) {

       DatabaseReference ref  = mDatabase.child("users").child(user.getUid());

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    registarNovoUser();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void registarNovoUser() {
        user = new User(mUser.getEmail(), mUser.getDisplayName(), "0.0");
        DatabaseReference ref  = mDatabase.child("users");
        ref.child(mUser.getUid()).setValue(user);

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        String date = day + "/" + (month + 1)  + "/" + year;
        UserInfo userInfo = new UserInfo("0.0", "0.0", date, "0.0", "0.0", "0.0");
        DatabaseReference ref1  = mDatabase.child("user-info");
        ref1.child(mUser.getUid()).setValue(userInfo);
    }



    private void methodInternet() {

        if(!verifyInternet()){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(false);

            builder.setTitle(getString(R.string.net_title));

            final TextView text = new TextView(this);
            text.setText("       " + getString(R.string.net_msg));
            builder.setView(text);

            builder.setPositiveButton(getString(R.string.net_botao), new DialogInterface.OnClickListener(){

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    methodInternet();
                }
            });

            builder.show();

        }else{
            signInGoogle();
            showProgressDialog();
        }
    }

    private boolean verifyInternet() {
        try {
            if (isConnected() )
                return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isConnected() throws InterruptedException, IOException
    {
       ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();

        if(isWifiConn || isMobileConn){
            return true;
        }else{
            return  false;
        }
    }

}


