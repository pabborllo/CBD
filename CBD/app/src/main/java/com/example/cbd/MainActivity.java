package com.example.cbd;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private String bg_uri = "https://firebasestorage.googleapis.com/v0/b/proyecto-cbd.appspot.com/o/birras%2F32GP.jpg?alt=media&token=d9163434-f9f9-4c5e-9ac5-aa8f2142206e";
    private ImageView beer_bg;

    private DatabaseManager db = new DatabaseManager();
    private GoogleSignInClient mGoogleSignInClient;
    private SignInButton loginButton;
    private static final String TAG = "SignInActivity";
    private static final int RC_SIGN_IN = 5001;

    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Usuario userInfo;

    private Button create;
    private Button gallery;
    //private Button historical;
    private Button logOutButton;
    private Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        beer_bg = findViewById(R.id.bg_beer);
        Glide.with(MainActivity.this)
                .load(Uri.parse(bg_uri))
                .into(beer_bg);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        loginButton = findViewById(R.id.sign_in_button);

        mAuth = FirebaseAuth.getInstance();

        create = findViewById(R.id.create);
        gallery = findViewById(R.id.gallery);
        //historical = findViewById(R.id.historical);
        logOutButton = findViewById(R.id.logOutButton);
        signOutButton = findViewById(R.id.signOutButton);

        loginButton.setOnClickListener(this);
        create.setOnClickListener(this);
        gallery.setOnClickListener(this);
        //historical.setOnClickListener(this);
        logOutButton.setOnClickListener(this);
        signOutButton.setOnClickListener(this);

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.sign_in_button:
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
                break;
            case R.id.create:
                Bundle bundleCreate = new Bundle();
                bundleCreate.putString("ID", userInfo.getId());
                Intent nuevaBirra = new Intent(MainActivity.this, CreateBirra.class);
                nuevaBirra.putExtras(bundleCreate);
                startActivity(nuevaBirra);
                break;
            case R.id.gallery:
                Bundle bundleGallery = new Bundle();
                bundleGallery.putString("ID", userInfo.getId());
                Intent galeria = new Intent(MainActivity.this, BirraGallery.class);
                galeria.putExtras(bundleGallery);
                startActivity(galeria);
                break;
            case R.id.logOutButton:
                desloguea();
                break;
            case R.id.signOutButton:
                getFotosIDs();
                break;
            default:
                break;
        }
    }

    private void seteaVistaLogIn(){
        loginButton.setClickable(false);
        loginButton.setVisibility(View.GONE);

        create.setVisibility(View.VISIBLE);
        create.setClickable(true);

        gallery.setVisibility(View.VISIBLE);
        gallery.setClickable(true);

        //historical.setVisibility(View.VISIBLE);
        //historical.setClickable(true);

        logOutButton.setVisibility(View.VISIBLE);
        logOutButton.setClickable(true);

        signOutButton.setVisibility(View.VISIBLE);
        signOutButton.setClickable(true);
    }

    private void seteaVistaLogOut(){
        create.setClickable(false);
        create.setVisibility(View.GONE);

        gallery.setClickable(false);
        gallery.setVisibility(View.GONE);

        //historical.setClickable(false);
        //historical.setVisibility(View.GONE);

        logOutButton.setClickable(false);
        logOutButton.setVisibility(View.GONE);

        signOutButton.setClickable(false);
        signOutButton.setVisibility(View.GONE);

        loginButton.setVisibility(View.VISIBLE);
        loginButton.setClickable(true);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        //GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        mUser = mAuth.getCurrentUser();
        if(mUser != null){
            seteaVistaLogIn();
            db.getUsersRef()
                    .child(mUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    userInfo = dataSnapshot.getValue(Usuario.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        //updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            }catch(ApiException e){
                Log.w(TAG, "Google sign in failed", e);
            }

            //handleSignInResult(task);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            mUser = mAuth.getCurrentUser();
                            userInfo = new Usuario(mUser.getUid(), mUser.getEmail());
                            db.getUsersRef()
                                    .child(userInfo.getId())
                                    .setValue(userInfo);
                            seteaVistaLogIn();
                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            seteaVistaLogIn();
            userInfo = new Usuario(account.getId(), account.getEmail());
            db.getUsersRef()
                    .child(userInfo.getId())
                    .setValue(userInfo);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("ERROR", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "Error al iniciar sesión", Toast.LENGTH_LONG).show();
        }
    }

    private void desloguea(){
        try{
            seteaVistaLogOut();
            mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getApplicationContext(), "Deslogueado", Toast.LENGTH_LONG).show();
                }
            });
        }catch(Exception e){
            Toast.makeText(getApplicationContext(), "Error al desloguear", Toast.LENGTH_LONG).show();
        }
    }

    private void getFotosIDs(){
        //RECOPILAR IDS DE FOTOS
        final List<String> fotosIDs = new ArrayList<>();
        db.realTimeRef().child(userInfo.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    fotosIDs.add(ds.getKey());
                }
                borrarCuenta(fotosIDs);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void borrarCuenta(List<String> fotosIDs){
        try{
            //BORRADO DE FOTOS
            for(String foto: fotosIDs) {
                db.mStorageRef().child(userInfo.getId()).child(foto).delete();
            }
            //BORRADO DE BIRRAS
            db.realTimeRef().child(userInfo.getId()).removeValue();
            //BORRADO DE TABLA USUARIO
            db.getUsersRef().child(userInfo.getId()).removeValue();
            //BORRADO DE TOKEN DE FIREBASE
            mUser.delete();
            //BORRADO DE TOKEN DE GOOGLE
            mGoogleSignInClient.revokeAccess();

            Toast.makeText(getApplicationContext(), "Cuenta borrada", Toast.LENGTH_LONG).show();
            seteaVistaLogOut();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "Error al borrar cuenta", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
