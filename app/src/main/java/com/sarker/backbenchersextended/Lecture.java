package com.sarker.backbenchersextended;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarker.backbenchersextended.adapter.CourseAdapter;
import com.sarker.backbenchersextended.model.CourseInfo;

import java.util.ArrayList;

public class Lecture extends AppCompatActivity {


    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference CatRef;

    private RecyclerView cRecyclerView;
    private CourseAdapter cAdapter;
    private ArrayList<CourseInfo> cList;
    private String current_user_id = " ",classCode;
    private ImageView back;
    private TextView empty;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_lecture);

        back = findViewById(R.id.back);

        empty = findViewById(R.id.empty_lecture);
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


        CatRef = FirebaseDatabase.getInstance().getReference("Classroom");


        cRecyclerView = findViewById(R.id.lecture_rv);
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

                }else {
                    empty.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Lecture.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


    }


}
