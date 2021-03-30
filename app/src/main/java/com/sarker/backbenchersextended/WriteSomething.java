package com.sarker.backbenchersextended;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class WriteSomething extends AppCompatActivity{
    private static final int PICK_IMAGE_REQUEST = 1;

    private Button UpdatePostButton;
    private EditText PostDescription;
    private DatabaseReference UsersRef, PostsRef;
    private FirebaseAuth mAuth;
    private String Description;
    private String saveCurrentDate, saveCurrentTime, postRandomName, postURL = "", current_user_id,classCode;
    private ProgressDialog loadingBar;
    private ImageView back;
    private Button mButtonChooseImage;
    private ImageView postImageView;
    private Uri mImageUri;

    private StorageReference PostsImagesRefrence;


    private StorageTask mUploadTask;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_write_something);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        Intent i = getIntent();
        classCode = i.getStringExtra("classCode");

        PostsImagesRefrence = FirebaseStorage.getInstance().getReference().child("Posts");
        UsersRef = FirebaseDatabase.getInstance().getReference().child("UsersData");
        PostsRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(classCode).child("Posts");

        mButtonChooseImage = findViewById(R.id.btn_add_photo);
        UpdatePostButton = (Button) findViewById(R.id.btn_post);
        PostDescription =(EditText) findViewById(R.id.edit_text_post);
        postImageView = findViewById(R.id.post_image_view);
        loadingBar = new ProgressDialog(this);


        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });



        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        UpdatePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Description = PostDescription.getText().toString();

                if(TextUtils.isEmpty(Description))
                {
                    Toast.makeText(WriteSomething.this, "Write Something First", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    loadingBar.setMessage("Creating New Post");
                    loadingBar.show();
                    loadingBar.setCanceledOnTouchOutside(true);

                    uploadFile();

                }

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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).fit().centerCrop().into(postImageView);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {

        if(mImageUri!=null) {


                StorageReference fileReference = PostsImagesRefrence.child(System.currentTimeMillis()
                        + "." + getFileExtension(mImageUri));

                mUploadTask = fileReference.putFile(mImageUri)

                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                //Toast.makeText(WriteSomething.this, "Upload successful", Toast.LENGTH_LONG).show();

                                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                                while (!urlTask.isSuccessful()) ;
                                Uri downloadUrl = urlTask.getResult();

                                postURL = downloadUrl.toString();

                                StoringImageToFirebaseStorage();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(WriteSomething.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });



        }

        else {

            StoringImageToFirebaseStorage();

        }


    }


    private void StoringImageToFirebaseStorage()
    {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMMM yyyy, hh:mm aa");
        String date = currentDate.format(calFordDate.getTime());

        saveCurrentDate = String.valueOf(getDateInMillis(date));

        SavingPostInformationToDatabase();
    }

    private void SavingPostInformationToDatabase()
    {


                    HashMap postsMap = new HashMap();
                    postsMap.put("uid", current_user_id);
                    postsMap.put("date", saveCurrentDate);
                    postsMap.put("description", Description);
                    postsMap.put("postimage", postURL);


                    PostsRef.push().updateChildren(postsMap)
                            .addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if(task.isSuccessful())
                                    {

                                        Toast.makeText(WriteSomething.this, "Post Is Updated Successfully.", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                        Intent n = new Intent(WriteSomething.this,FriendsFeed.class);
                                        n.putExtra("classCode",classCode);
                                        startActivity(n);
                                        finish();
                                    }
                                    else
                                    {
                                        Toast.makeText(WriteSomething.this, "Something Went Wrong", Toast.LENGTH_SHORT).show();
                                        loadingBar.dismiss();
                                    }
                                }
                            });

    }

    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                "d MMMM yyyy, hh:mm aa");

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }


}
