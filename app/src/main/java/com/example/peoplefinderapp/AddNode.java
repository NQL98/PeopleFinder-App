package com.example.peoplefinderapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class AddNode extends AppCompatActivity {

    //permission constants
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    //image pick constants
    private static final int IMAGE_PICK_CAMERA_CODE = 300;
    private static final int IMAGE_PICK_GALLERY_CODE = 400;
    //permission arrays
    private String[] cameraPermissions;
    private String[] storagePermissions;
    //picked image uri
    private Uri image_uri = null;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //UI
    RelativeLayout activity_add_node;
    private EditText editText_FIO;
    //private ImageButton btn_uploadImage;
    private ImageView uploadedPhoto;
    private EditText editDescription;
    private ImageButton btn_uploadNode;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_node);

        //toolbar back arrow
        Toolbar toolbarAddNode;
        toolbarAddNode = findViewById(R.id.toolbarAddNode);
        setSupportActionBar(toolbarAddNode);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbarAddNode.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AddNode.this, MainActivity.class));
                finish();
            }
        });

        //init ui views
        uploadedPhoto = findViewById(R.id.uploadedPhoto);
        editText_FIO = findViewById(R.id.editText_FIO);
        editDescription = findViewById(R.id.editDescription);
        btn_uploadNode = findViewById(R.id.btn_uploadNode);
        //btn_uploadImage = findViewById(R.id.btn_uploadImage);

        //init permission arrays
        cameraPermissions = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        firebaseAuth = FirebaseAuth.getInstance();

        //pick image
        uploadedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImagePickDialog();
            }
        });

        //handle click event
        btn_uploadNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCreatingGroup();
                finish();
            }
        });
    }

    private void startCreatingGroup() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Создание записи");

        //input fio, description
        final String fio = editText_FIO.getText().toString().trim();
        final String description = editDescription.getText().toString().trim();
        //validation
        if (TextUtils.isEmpty(fio)){
            Toast.makeText(this, "Пожалуйста введите ФИО", Toast.LENGTH_SHORT).show();
            return; //dont procede further
        }

        progressDialog.show();

        //temestamp: for group icon, groupId, timeCreated etc
        final String g_timestamp = ""+System.currentTimeMillis();
        if (image_uri == null) {
            //creating group without image

            createGroup(
                    ""+g_timestamp,
                    ""+fio,
                    ""+description,
                    "");
        }
        else {
            //creating group with icon image
            //upload image name and path
            String fileNameAndPath = "Group_Imgs/" + "image" + g_timestamp;

            StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileNameAndPath);
            storageReference.putFile(image_uri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //image uploaded, get url
                            Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                            while(!p_uriTask.isSuccessful());
                            Uri p_downloadUri = p_uriTask.getResult();
                            if (p_uriTask.isSuccessful()) {
                                createGroup(
                                        ""+g_timestamp,
                                        ""+fio,
                                        ""+description,
                                        ""+p_downloadUri);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //failed uploadind img
                            progressDialog.dismiss();
                            Toast.makeText(AddNode.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void createGroup(final String g_timestamp, String fio, String description, String groupIcon) {
        //setup info a group
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", ""+g_timestamp);
        hashMap.put("fio", ""+fio);
        hashMap.put("description", ""+description);
        hashMap.put("groupIcon", ""+groupIcon);
        hashMap.put("timestamp", ""+g_timestamp);
        hashMap.put("createdBy", ""+firebaseAuth.getUid());

        //create group
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(g_timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //created successfuly

                //setup member inf (add current user in group's participants list)
                HashMap<String, String> hashMap1 = new HashMap<>();
                hashMap1.put("uid", firebaseAuth.getUid());
                hashMap1.put("role", "creator");
                hashMap1.put("timestamp", g_timestamp);

                DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                ref1.child(g_timestamp).child("Participants").child(firebaseAuth.getUid())
                        .setValue(hashMap1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                //participant added
                                progressDialog.dismiss();
                                Toast.makeText(AddNode.this, "Запись создана", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                //failed adding participant
                                progressDialog.dismiss();
                                Toast.makeText(AddNode.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //failed
                progressDialog.dismiss();
                Toast.makeText(AddNode.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showImagePickDialog() {
        //options to pick image from
        String[] options = {"Camera", "Gallery"};
        //dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выберите картинку:")
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //handle clicks
                        if (i == 0) {
                            //camera clicked
                            if (!checkCameraPermissions()){
                                requestCameraPermissions();
                            }
                            else{
                                pickFromCamera();
                            }
                        }
                        else {
                            //gallery clicked
                            if (!checkStoragePermission()){
                                requestStoragePermissions();
                            }
                            else{
                                pickFromGallery();
                            }
                        }
                    }
                }).show();
    }

    private void pickFromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_GALLERY_CODE);
    }

    private void pickFromCamera(){
        ContentValues cv = new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE, "Group Image Icon Title");
        cv.put(MediaStore.Images.Media.DESCRIPTION, "Group Image Icon Description");
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(intent, IMAGE_PICK_CAMERA_CODE);
    }

    private boolean checkStoragePermission(){

        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result;
    }

    private void requestStoragePermissions(){
        ActivityCompat.requestPermissions(this, storagePermissions, STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermissions(){
        boolean result = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);

        return result && result1;
    }

    private void requestCameraPermissions(){
        ActivityCompat.requestPermissions(this, cameraPermissions, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //handle permission result
        switch (requestCode){
            case CAMERA_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean storageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted && storageAccepted) {
                        //permission allowed
                        pickFromCamera();
                    }
                    else {
                        //both or one is denied
                        Toast.makeText(this, "Camera & Storage permission are required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
            case STORAGE_REQUEST_CODE:{
                if (grantResults.length > 0){
                    boolean storageAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (storageAccepted){
                        //permission allowed
                        pickFromGallery();
                    }
                    else{
                        //permission denied
                        Toast.makeText(this, "Storage permission required", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //handle image pick result
        if (resultCode == RESULT_OK){

            if (requestCode == IMAGE_PICK_GALLERY_CODE){
                //was picked from gallery
                image_uri = data.getData();
                //set to image view
                uploadedPhoto.setImageURI(image_uri);
            }
            else if (requestCode == IMAGE_PICK_CAMERA_CODE){
                //was picked from camera
                //set to image view
                uploadedPhoto.setImageURI(image_uri);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}


/*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            if (requestCode == PICK_IMAGE) {
                image_uri = data.getData();
                uploadedPhoto.setImageURI(image_uri);
            }
        }
    }*/


/* //UI init
        activity_add_node = findViewById(R.id.activity_add_node);
        editText_FIO = findViewById(R.id.editText_FIO);
        btn_uploadImage = findViewById(R.id.btn_uploadImage);
        uploadedPhoto = findViewById(R.id.uploadedPhoto);
        editDescription = findViewById(R.id.editDescription);
        btn_uploadNode = findViewById(R.id.btn_uploadNode);

        //pick image for node
        btn_uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, PICK_IMAGE);
            }
        });



        //click btn addNode
        btn_uploadNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createNode();
            }
        });
    }

    private void createNode() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Добавление записи");

        final String et_fio = editText_FIO.getText().toString().trim();
        final String et_desc = editDescription.getText().toString().trim();
        if (TextUtils.isEmpty(et_fio)) {
            Toast.makeText(this, "Заполните поля", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show();

        final String g_timestamp = ""+System.currentTimeMillis();

        String fileNameAndPath = "Group_Imgs/" + "image" + g_timestamp;
        StorageReference storageReference = FirebaseStorage.getInstance().getReference(fileNameAndPath);
        storageReference.putFile(image_uri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //img uploaded, get uri
                Task<Uri> p_uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!p_uriTask.isSuccessful());
                Uri p_downloadUri = p_uriTask.getResult();
                if (p_uriTask.isSuccessful()) {
                    uploadNode(""+et_fio, ""+et_desc, ""+p_downloadUri, ""+g_timestamp);
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //fail upload img
                progressDialog.dismiss();
                Toast.makeText(AddNode.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadNode(String fio, String desc, String photo, final String g_timestamp) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("groupId", ""+g_timestamp);
        hashMap.put("fio", ""+fio);
        hashMap.put("desc", ""+desc);
        hashMap.put("photo", ""+photo);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(g_timestamp).setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //succes
                /*progressDialog.dismiss();
                Toast.makeText(AddNode.this, "Запись добавлена", Toast.LENGTH_SHORT).show();*/

/*    //setup member inf (add current user in group's participants list)
    HashMap<String, String> hashMap1 = new HashMap<>();
                hashMap1.put("uid", FirebaseAuth.getInstance().getUid());
                        hashMap1.put("role", "creator");
                        hashMap1.put("timestamp", g_timestamp);

                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference("Groups");
                        ref1.child(g_timestamp).child("Participants").child(FirebaseAuth.getInstance().getUid())
                        .setValue(hashMap1)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
@Override
public void onSuccess(Void aVoid) {
        //participant added
        progressDialog.dismiss();
        Toast.makeText(AddNode.this, "Запись создана", Toast.LENGTH_LONG).show();
        }
        })
        .addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
        //failed adding participant
        progressDialog.dismiss();
        Toast.makeText(AddNode.this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
        }
        });
        }
        })
        .addOnFailureListener(new OnFailureListener() {
@Override
public void onFailure(@NonNull Exception e) {
        //fail
        progressDialog.dismiss();
        Toast.makeText(AddNode.this, "Не удалось создать", Toast.LENGTH_SHORT).show();
        }
        });
*/