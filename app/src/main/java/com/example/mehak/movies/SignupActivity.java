package com.example.mehak.movies;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.mehak.movies.Classes.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity{

    private TextInputEditText textInputEditTextEmail;
    private TextInputEditText textInputEditTextPassword;
    private TextInputEditText textInputEditTextDisplayName;

    private Button mCreateBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    String displayName;
    User user;

    //Progress Log
    private ProgressDialog mRegProgress;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mRegProgress = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        initViews();

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable()) {

                    displayName = textInputEditTextDisplayName.getText().toString().trim();
                    String email = textInputEditTextEmail.getText().toString().trim();
                    String password = textInputEditTextPassword.getText().toString().trim();

                    if (!TextUtils.isEmpty(displayName) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)) {

                        mRegProgress.setTitle("Registering User");
                        mRegProgress.setMessage("Please wait while your account is being registered");
                        mRegProgress.setCanceledOnTouchOutside(false);
                        mRegProgress.show();

                        registerUser(displayName, email, password);
                    }
                    else {

                        Toast.makeText(SignupActivity.this, "Please fill the desired fields.", Toast.LENGTH_SHORT).show();
                    }


                }
                else
                {
                    Toast.makeText(getApplicationContext(), "No Connection!\nCheck your Internet Connection", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void registerUser(String displayName, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            mRegProgress.dismiss();
                            createNewUser(task.getResult().getUser());

                            Intent mainIntent = new Intent(SignupActivity.this,DashboardActivity.class);
                            startActivity(mainIntent);
                            //user can't come back
                            finish();

                            Log.d("ok", "createUserWithEmail:success");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("ok", "createUserWithEmail:failure", task.getException());

                            mRegProgress.hide();

                            Toast.makeText(SignupActivity.this, "Can't signup length of password is too short or user already exists!",
                                    Toast.LENGTH_SHORT).show();

                        }


                    }
                });

    }

    private void createNewUser(UserInfo userFromRegistration) {
        user = new User();
        user.userName = displayName ;
        user.email = userFromRegistration.getEmail();
        String userId = userFromRegistration.getUid();

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(userId).setValue(user);
    }

    public void initViews(){

        textInputEditTextEmail = (TextInputEditText) findViewById(R.id.email_input);
        textInputEditTextPassword = (TextInputEditText) findViewById(R.id.pwd_input);
        textInputEditTextDisplayName = (TextInputEditText) findViewById(R.id.name_input);

        mCreateBtn = (Button) findViewById(R.id.reg_btn);
    }
}
