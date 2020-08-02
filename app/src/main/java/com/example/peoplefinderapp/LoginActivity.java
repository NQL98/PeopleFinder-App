package com.example.peoplefinderapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private static int SIGN_IN_CODE = 1;
    private TextView tv_login;
    private TextView tv_to_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tv_login = findViewById(R.id.tv_login);
        tv_to_main = findViewById(R.id.tv_to_main);

        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
        else{
            tv_login.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (FirebaseAuth.getInstance().getCurrentUser() == null)
                        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(), SIGN_IN_CODE);
                    else Toast.makeText(LoginActivity.this, "Вы авторизованы!", Toast.LENGTH_SHORT).show();
                }
            });
            tv_to_main.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (FirebaseAuth.getInstance().getCurrentUser() == null){
                        Toast.makeText(LoginActivity.this, "Вы не авторизованы!", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                }
            });
        }
    }
}


