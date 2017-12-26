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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignupActivity extends AppCompatActivity{

    private TextInputLayout mDispalyName;
    private TextInputLayout mEmail;
    private TextInputLayout mPassword;
    private Button mCreateBtn;
    private FirebaseAuth mAuth;

    //Progress Log
    private ProgressDialog mRegProgress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mRegProgress = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();


        mDispalyName = (TextInputLayout) findViewById(R.id.reg_display_name);
        mEmail = (TextInputLayout) findViewById(R.id.reg_email);
        mPassword = (TextInputLayout) findViewById(R.id.reg_password);
        mCreateBtn = (Button) findViewById(R.id.reg_btn);

        mCreateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String displayName = mDispalyName.getEditText().getText().toString().trim();
                String email = mEmail.getEditText().getText().toString().trim();
                String password = mPassword.getEditText().getText().toString().trim();

                if (!TextUtils.isEmpty(displayName) || !TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)){

                    mRegProgress.setTitle("Registering User");
                    mRegProgress.setMessage("Please wait while your account is being registered");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();

                    registerUser(displayName, email, password);
                }



            }
        });

    }

    private void registerUser(String displayName, String email, String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information

                            mRegProgress.dismiss();

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
}
