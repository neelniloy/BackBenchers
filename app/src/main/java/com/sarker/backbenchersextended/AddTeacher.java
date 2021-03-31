package com.sarker.backbenchersextended;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sarker.backbenchersextended.adapter.CourseAdapter;
import com.sarker.backbenchersextended.adapter.TeacherAdapter;
import com.sarker.backbenchersextended.model.CourseInfo;
import com.sarker.backbenchersextended.model.TeacherInfo;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class AddTeacher extends AppCompatActivity {

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference tRef;

    private String current_user_id = " ",url = " ",name,classCode;
    private Button btnAdd;
    private ImageView back,showImage,addImage;

    private static final int PICK_IMAGE_REQUEST = 101;
    private TextInputEditText Name,Initial,Email,Phone,Room;
    private TextInputLayout nameTextLayout,initialTextLayout,emailTextLayout,phoneTextLayout,roomTextLayout;

    private ProgressDialog loadingBar;
    private Uri mImageUri;
    private StorageReference ImagesRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_add_teacher);


        addImage = findViewById(R.id.add_photo);
        btnAdd =findViewById(R.id.cv_add_teacher);
        showImage = findViewById(R.id.show_teacher_image);

        Name = findViewById(R.id.et_name);
        Initial = findViewById(R.id.et_initial);
        Email = findViewById(R.id.et_email);
        Phone = findViewById(R.id.et_phone);
        Room = findViewById(R.id.et_room);

        nameTextLayout = findViewById(R.id.editTextNameLayout);
        initialTextLayout = findViewById(R.id.editTextInitialLayout);
        emailTextLayout = findViewById(R.id.editTextEmailLayout);
        phoneTextLayout = findViewById(R.id.editTextPhoneLayout);
        roomTextLayout = findViewById(R.id.editTextRoomLayout);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

        mfirebaseAuth = FirebaseAuth.getInstance();

        final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

        if (mFirebaseuser != null) {

            current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        }
        Intent i = getIntent();
        classCode = i.getStringExtra("classCode");


        ImagesRef = FirebaseStorage.getInstance().getReference().child("Teacher");
        tRef = FirebaseDatabase.getInstance().getReference("Classroom").child(classCode);

        loadingBar = new ProgressDialog(AddTeacher.this);


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String name = Name.getText().toString();
                final String initial = Initial.getText().toString();
                final String email = Email.getText().toString();
                final String phone = Phone.getText().toString();
                final String room = Room.getText().toString();


               if (name.isEmpty() || initial.isEmpty() || email.isEmpty() || phone.isEmpty() || room.isEmpty()){

                    if (name.isEmpty()) {
                        nameTextLayout.setError("empty field");
                        Name.requestFocus();
                    }else {
                        nameTextLayout.setErrorEnabled(false);
                    }

                    if (initial.isEmpty()) {
                        initialTextLayout.setError("empty field");
                        Initial.requestFocus();
                    }else {
                        initialTextLayout.setErrorEnabled(false);
                    }

                    if (email.isEmpty()) {
                        emailTextLayout.setError("empty field");
                        Email.requestFocus();
                    }
                    else {
                        emailTextLayout.setErrorEnabled(false);
                    }

                    if (phone.isEmpty()) {
                        phoneTextLayout.setError("empty field");
                        Phone.requestFocus();
                    }
                    else {
                        phoneTextLayout.setErrorEnabled(false);
                    }


                   if (room.isEmpty()) {
                       roomTextLayout.setError("empty field");
                       Room.requestFocus();
                   }
                   else {
                       roomTextLayout.setErrorEnabled(false);
                   }

                }
                else {

                   loadingBar.setMessage("Please wait...");
                   loadingBar.show();
                   loadingBar.setCanceledOnTouchOutside(false);


                   if(mImageUri==null ) {

                       loadingBar.dismiss();
                       sendData();


                   }else {
                       uploadImage();
                   }

                }

            }
        });


    }

    private void sendData() {

         String name = Name.getText().toString();
         String initial = Initial.getText().toString();
         String email = Email.getText().toString();
         String phone = Phone.getText().toString();
         String room = Room.getText().toString();



        Map add = new HashMap();

        add.put("name", name);
        add.put("initial", initial);
        add.put("email", email);
        add.put("phone", phone);
        add.put("room", room);
        add.put("dp", url);
        add.put("uid", current_user_id);

        tRef.child("Teacher").push().updateChildren(add);

        Toast.makeText(AddTeacher.this, "Teacher Added Successfully", Toast.LENGTH_SHORT).show();

        loadingBar.dismiss();
        Intent intent = new Intent(AddTeacher.this, AddTeacher.class);
        intent.putExtra("classCode", classCode);
        startActivity(intent);
        finish();

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data.getData() != null) {
            mImageUri = data.getData();
            showImage.setVisibility(View.VISIBLE);
            Picasso.get().load(mImageUri).fit().centerInside().into(showImage);
        }



    }


    public void uploadImage(){

        StorageReference fileReference = ImagesRef.child(System.currentTimeMillis()
                + ".jpg");

        showImage.setDrawingCacheEnabled(true);
        showImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable)showImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = fileReference.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri downloadUrl = urlTask.getResult();

                url = downloadUrl.toString();
                sendData();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });


    }
}