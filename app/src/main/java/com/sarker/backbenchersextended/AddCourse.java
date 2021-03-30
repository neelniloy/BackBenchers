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
import com.sarker.backbenchersextended.model.CourseInfo;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddCourse extends AppCompatActivity {


    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference CatRef;

    private RecyclerView cRecyclerView;
    private CourseAdapter cAdapter;
    private ArrayList<CourseInfo> cList;
    private String current_user_id = " ",url,name,classCode;
    private Button addCat,choose;
    private ImageView back,showImage;

    private static final int PICK_IMAGE_REQUEST = 101;
    private EditText cName;

    private ProgressDialog loadingBar;
    private Uri mImageUri,resultUri;
    private StorageReference ImagesRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_add_course);


        addCat = findViewById(R.id.add_course);
        choose =findViewById(R.id.choose_course);
        showImage = findViewById(R.id.show_course_image);
        cName = findViewById(R.id.et_course_name);

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


        ImagesRef = FirebaseStorage.getInstance().getReference().child("Course");
        CatRef = FirebaseDatabase.getInstance().getReference("Classroom");

        loadingBar = new ProgressDialog(AddCourse.this);

        cRecyclerView = findViewById(R.id.add_course_rv);
        cRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 = Number of Columns
        cRecyclerView.setLayoutManager(gridLayoutManager);

        cList = new ArrayList<>();
        cAdapter = new CourseAdapter(this, cList);
        cRecyclerView.setAdapter(cAdapter);

        CatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                cList.clear();
                cAdapter.notifyDataSetChanged();

                if(dataSnapshot.child(classCode).child("Course").exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.child(classCode).child("Course").getChildren()) {

                        CourseInfo info = postSnapshot.getValue(CourseInfo.class);
                        info.setKey(postSnapshot.getKey());
                        info.setClassCode(classCode);
                        cList.add(info);


                    }
                    cAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddCourse.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });


        addCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uploadFile();

            }
        });


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

    private void uploadFile() {

        loadingBar.setMessage("Please wait...");
        loadingBar.show();
        loadingBar.setCanceledOnTouchOutside(false);

        name = cName.getText().toString();

        if(mImageUri==null ) {

            Toast.makeText(this, "Add Image First", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();

        }
        else if(name.isEmpty()){
            Toast.makeText(AddCourse.this, "Empty Field!", Toast.LENGTH_SHORT).show();
            loadingBar.dismiss();
        }

        else if(mImageUri!=null && !name.isEmpty()) {

            uploadImage();

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


                Map add = new HashMap();

                add.put("courseName", name);
                add.put("courseImage", url);

                CatRef.child(classCode).child("Course").child(name.replaceAll("[^a-zA-Z0-9]", " ")).updateChildren(add);

                Toast.makeText(AddCourse.this, "Course Successfully Added", Toast.LENGTH_SHORT).show();

                Intent n = new Intent(AddCourse.this,AddCourse.class);
                n.putExtra("classCode",classCode);
                startActivity(n);
                finish();
                loadingBar.dismiss();
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
            }
        });


    }
}