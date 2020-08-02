package com.example.peoplefinderapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;


public class MainActivity extends AppCompatActivity {

    //UI
    private ImageButton btn_add_node;
    private RelativeLayout activity_main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //init UI
        btn_add_node = findViewById(R.id.btn_add_node);
        activity_main = findViewById(R.id.activity_main);

        addListenerOnButton ();

    }

   public void addListenerOnButton () {
        btn_add_node.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, AddNode.class));
            }
        });

    }
}
