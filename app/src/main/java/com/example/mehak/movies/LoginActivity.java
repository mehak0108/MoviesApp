package com.example.mehak.movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import static java.security.AccessController.getContext;

public class LoginActivity extends AppCompatActivity {

    /*private TextInputLayout mLoginEmail;
    private TextInputLayout mLoginPassword;*/

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;

    private Button mLoginBtn;
    private ProgressDialog mLoginProgress;
    private FirebaseAuth mAuth;

    //google sign in
    SignInButton googleSignIn;
    private final static int RC_SIGN_IN = 2;
    GoogleApiClient mGoogleApiClient;

    //Facebook login
    private CallbackManager mCallbackManager;
    private ImageView mFbButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();

        mLoginProgress = new ProgressDialog(this);

        //======================================================================================

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.email_input);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.pwd_input);
        mLoginBtn = (Button) findViewById(R.id.login_btn);

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = textInputEditTextEmail.getText().toString().trim();
                String password = textInputEditTextPassword.getText().toString().trim();

                if (isNetworkAvailable()) {
                    if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                        mLoginProgress.setTitle("Loging in");
                        mLoginProgress.setMessage("Please wait while we check your credentials");
                        mLoginProgress.setCanceledOnTouchOutside(false);
                        mLoginProgress.show();

                        loginUser(email, password);
                    } else {
                        Toast.makeText(LoginActivity.this, "Please fill the desired fields.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "No Connection!\nCheck your Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });


        TextView sign = (TextView) findViewById(R.id.sign_up);
        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        //===========================================================================================

        //google sign in

        googleSignIn = (SignInButton) findViewById(R.id.googleBtn);

        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isNetworkAvailable()) {

                    signIn();
                } else {
                    Toast.makeText(getApplicationContext(), "No Connection!\nCheck your Internet Connection", Toast.LENGTH_LONG).show();
                }
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
                        Toast.makeText(LoginActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        //==================================================================================


        //Facebook Login

        mCallbackManager = CallbackManager.Factory.create();
        mFbButton = (ImageView) findViewById(R.id.logo_fb);
        mFbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable()) {

                    mFbButton.setEnabled(false);
                    mLoginProgress.setTitle("Loging in");
                    mLoginProgress.setMessage("Please wait while we check your credentials");
                    mLoginProgress.setCanceledOnTouchOutside(false);
                    mLoginProgress.show();

                    LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("email", "public_profile"));
                    LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
                        @Override
                        public void onSuccess(LoginResult loginResult) {
                            Log.d("ok", "facebook:onSuccess:" + loginResult);
                            handleFacebookAccessToken(loginResult.getAccessToken());
                        }

                        @Override
                        public void onCancel() {
                            Log.d("ok", "facebook:onCancel");

                        }

                        @Override
                        public void onError(FacebookException error) {
                            Log.d("ok", "facebook:onError", error);

                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "No Connection!\nCheck your Internet Connection", Toast.LENGTH_LONG).show();
                }
            }
        });

       /* mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton)findViewById(R.id.fb_button);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("ok", "facebook:onSuccess:" + loginResult);
                mLoginProgress.setTitle("Loging in");
                mLoginProgress.setMessage("Please wait while we check your credentials");
                mLoginProgress.setCanceledOnTouchOutside(false);
                mLoginProgress.show();
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("ok", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("ok", "facebook:onError", error);
                // ...
            }
        });*/


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    //---------------------------------------------------------------------------------------------

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("ok", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("ok", "signInWithCredential:success");


                            mLoginProgress.dismiss();
                            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(intent);
                            finish();


                            //mFbButton.setenabled(true);
                        } else {
                            // If sign in fails, display a message to the user.
                            mLoginProgress.hide();
                            Log.w("ok", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            //mFbButton.setenabled(true);
                        }
                    }
                });
    }


    //=======================================================================================

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
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);

            } else {
                Toast.makeText(LoginActivity.this, "Auth went wrong!", Toast.LENGTH_SHORT).show();
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                            Intent mainIntent = new Intent(LoginActivity.this, DashboardActivity.class);
                            startActivity(mainIntent);

                            //doesn't back until he logs out.
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("notok", "signInWithCredential:failure", task.getException());

                            mLoginProgress.hide();
                            Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show();

                        }


                    }
                });

    }


    //========================================================================================


    private void loginUser(String email, String password) {

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("ok", "signInWithEmail:success");

                    mLoginProgress.dismiss();
                    Intent mainIntent = new Intent(LoginActivity.this, DashboardActivity.class);
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

    //===============================================================================================
}
