package com.sarker.backbenchersextended;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddLecture extends AppCompatActivity {

    private ImageView back,viewLectureimge,addLectureimage;
    private TextInputEditText  Description;
    private TextInputLayout  descriptionTextLayout;
    private Button btnAdd;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private String  url, course = " ",classCode,user_id;
    private DatabaseReference lectureRef;
    private ArrayList<String> cList = new ArrayList<>();

    StorageReference storageReference;

    StorageTask storageTask;
    Uri contentURI;

    private static final int PICK_FROM_GALLERY = 1;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_FROM_GALLERY && resultCode == RESULT_OK && data.getData() != null) {
            contentURI = data.getData();

            viewLectureimge.setImageURI(contentURI);
           // Picasso.get().load(contentURI).fit().centerInside().into(viewLectureimge);

        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_add_lecture);

        back = findViewById(R.id.back);
        addLectureimage = findViewById(R.id.add_lecture_image);
        viewLectureimge = findViewById(R.id.view_lecture_image);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAdd = findViewById(R.id.btn_add_lecture);

        Description = findViewById(R.id.et_description);
        descriptionTextLayout = findViewById(R.id.editTextDescriptionLayout);


        Intent i = getIntent();
        classCode = i.getStringExtra("classCode");


        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference().child("Lecture");

        lectureRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(classCode);


        lectureRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    for (DataSnapshot postSnapshot : snapshot.child("Course").getChildren()) {

                        cList.add(postSnapshot.getKey());

                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        final AutoCompleteTextView dropdown = findViewById(R.id.courseDropdown);

        final ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        this,
                        R.layout.support_simple_spinner_dropdown_item,
                        cList);
        dropdown.setAdapter(adapter);


        dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                course = adapter.getItem(position);
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String description = Description.getText().toString();


                if (description.isEmpty()) {
                    descriptionTextLayout.setError("*Description required");
                    Description.requestFocus();

                }else if ( course.equals(" ") ) {

                    Toast.makeText(AddLecture.this, "Choose A Course", Toast.LENGTH_SHORT).show();
                }
                else if (contentURI == null) {
                    Toast.makeText(AddLecture.this, "Select A Image", Toast.LENGTH_SHORT).show();
                }else if (!(description.isEmpty() && course.equals(" ") && (contentURI == null))) {

                    progressDialog = new ProgressDialog(AddLecture.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Adding Lecture...");
                    progressDialog.show();

                    if (contentURI != null) {
                        SaveChange();
                    } else {
                        Toast.makeText(AddLecture.this, "Select A Image", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    progressDialog.dismiss();
                    Toast.makeText(AddLecture.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }

        });

        addLectureimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PICK_FROM_GALLERY);
            }
        });

    }

    private void sendUserData() {

        progressDialog.dismiss();

        String description = Description.getText().toString();


        String saveCurrentDate, saveCurrentTime;


        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MMMM-yyyy");
        saveCurrentDate = currentDate.format(calFordDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calFordDate.getTime());




        Map add = new HashMap();

        add.put("postTime",""+ getDateInMillis(saveCurrentDate+" "+saveCurrentTime));
        add.put("lecture", description);
        add.put("course", course);
        add.put("image", url);
        add.put("uid", user_id);

        lectureRef.child("Lecture").child(course).push().updateChildren(add);


        Toast.makeText(AddLecture.this, "Lecture Successfully Added", Toast.LENGTH_SHORT).show();

        Intent i = new Intent(AddLecture.this, Dashboard.class);
        startActivity(i);
        finish();


    }

    public static long getDateInMillis(String srcDate) {

        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                "dd-MMMM-yyyy hh:mm a");

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void SaveChange() {


        UUID random = UUID.randomUUID();

        StorageReference fileReference = storageReference.child(String.valueOf(random));

        viewLectureimge.setDrawingCacheEnabled(true);
        viewLectureimge.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable)viewLectureimge.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);
        byte[] data = baos.toByteArray();

        storageTask = fileReference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> urlTask = taskSnapshot.getStorage().getDownloadUrl();
                while (!urlTask.isSuccessful()) ;
                Uri uri = urlTask.getResult();
                url = uri.toString();

                sendUserData();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}