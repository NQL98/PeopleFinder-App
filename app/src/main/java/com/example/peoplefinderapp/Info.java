package com.example.peoplefinderapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.security.spec.ECField;

public class Info extends AppCompatActivity {

    private String groupId;

    private TextView textViewInfo_fioFilled;
    private ImageView infoPhoto;
    private TextView textView_info_descriptionFilled;

    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //back arrow toolbar
        Toolbar toolbar = findViewById(R.id.toolbarInfo);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(Info.this, MainActivity.class));
                finish();
            }
        });

        textViewInfo_fioFilled = findViewById(R.id.textViewInfo_fioFilled);
        infoPhoto = findViewById(R.id.infoPhoto);
        textView_info_descriptionFilled = findViewById(R.id.textView_info_descriptionFilled);

        groupId = getIntent().getStringExtra("groupId");

        firebaseAuth = FirebaseAuth.getInstance();

        loadGroupInfo();
    }

    private void loadGroupInfo() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    //get group info
                    String groupId = "" + ds.child("groupId").getValue();
                    String fio = "" + ds.child("fio").getValue();
                    String description = "" + ds.child("description").getValue();
                    String groupIcon = "" + ds.child("groupIcon").getValue();

                    //set group info
                    textViewInfo_fioFilled.setText(fio);
                    textView_info_descriptionFilled.setText(description);

                    try {
                        Picasso.get().load(groupIcon).placeholder(R.drawable.no_image).into(infoPhoto);
                    }
                    catch (Exception e){
                        infoPhoto.setImageResource(R.drawable.no_image);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
