package com.example.mehak.movies;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;
    private Button mLoginBtn;
    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;

    //google sign in
    SignInButton googleSignIn;
    private final static int RC_SIGN_IN = 2;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);


        mLoginEmail = (TextInputLayout) findViewById(R.id.usernameWrapper);
        mLoginPassword = (TextInputLayout) findViewById(R.id.passwordWrapper);
        mLoginBtn = (Button) findViewById(R.id.login_btn);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mLoginEmail.getEditText().getText().toString().trim();
                String password = mLoginPassword.getEditText().getText().toString().trim();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mLoginProgress.setTitle("Loging in");
                    mLoginProgress.setMessage("Please wait while we check your credentials");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    loginUser(email, password);
                }
            }
        });




        TextView sign = (TextView)findViewById(R.id.sign_up);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        //google sign in

        googleSignIn = (SignInButton)findViewById(R.id.googleBtn);

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });


        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(LoginActivity.this,"Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



    }

    private void signIn() {
        mLoginProgress.setTitle("Loging in");
        mLoginProgress.setMessage("Please wait while we check your credentials");
        mLoginProgress.setCanceledOnTouchOutside(false);
        mLoginProgress.show();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()){
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            }else
            {
                Toast.makeText(LoginActivity.this,"Auth went wrong!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ok", "signInWithCredential:success");

                            mLoginProgress.dismiss();
                            Intent mainIntent = new Intent(LoginActivity.this,DashboardActivity.class);
                            startActivity(mainIntent);

                            //doesn't back until he logs out.
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("notok", "signInWithCredential:failure", task.getException());

                            mLoginProgress.hide();
                            Toast.makeText(LoginActivity.this,"Authentication failed!", Toast.LENGTH_SHORT).show();

                        }


                    }
                });

    }




    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("ok", "signInWithEmail:success");

                    mLoginProgress.dismiss();
                    Intent mainIntent = new Intent(LoginActivity.this,DashboardActivity.class);
                    startActivity(mainIntent);

                    //doesn't back until he logs out.
                    finish();

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("not ok", "signInWithEmail:failure", task.getException());
                    mLoginProgress.hide();
                    Toast.makeText(LoginActivity.this, "Can't sign in! Check your email and password.", Toast.LENGTH_SHORT).show();

                }


            }
        });
    }
}
