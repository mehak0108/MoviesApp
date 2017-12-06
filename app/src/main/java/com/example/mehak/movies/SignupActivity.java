package com.example.mehak.movies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final TextInputLayout usernameWrapper = (TextInputLayout) findViewById(R.id.usernameWrapper);
        final TextInputLayout passwordWrapper = (TextInputLayout) findViewById(R.id.passwordWrapper);
        final TextInputLayout passwordWrapper1 = (TextInputLayout) findViewById(R.id.passwordWrapper1);
        usernameWrapper.setHint("Email Id");
        passwordWrapper.setHint("Password");
        passwordWrapper1.setHint("Confirm Password");
    }
}
