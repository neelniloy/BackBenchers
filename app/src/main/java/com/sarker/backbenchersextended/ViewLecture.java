package com.sarker.backbenchersextended;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarker.backbenchersextended.adapter.LectureAdapter;
import com.sarker.backbenchersextended.model.LectureInfo;

import java.util.ArrayList;

public class ViewLecture extends AppCompatActivity {

    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference CatRef;

    private RecyclerView lRecyclerView;
    private LectureAdapter lAdapter;
    private ArrayList<LectureInfo> lList;
    private String current_user_id = " ",course,classCode;
    private ImageView back;
    private TextView empty,tvCourse;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_view_lecture);



        tvCourse = findViewById(R.id.tv_lecture);
        empty = findViewById(R.id.empty_lecture);
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
        course = i.getStringExtra("course");
        classCode = i.getStringExtra("classCode");


        if (course.length()>28){
            tvCourse.setText(course.substring(0,25)+"...");
        }else {
            tvCourse.setText(course);
        }


        CatRef = FirebaseDatabase.getInstance().getReference("Classroom").child(classCode);


        lRecyclerView = findViewById(R.id.lecture_rv);
        lRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        //        linearLayoutManager.setReverseLayout(true);
        //        linearLayoutManager.setStackFromEnd(true);
        lRecyclerView.setLayoutManager(linearLayoutManager);

        lList = new ArrayList<>();
        lAdapter = new LectureAdapter(this,lList);
        lRecyclerView.setAdapter(lAdapter);

        CatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lList.clear();
                lAdapter.notifyDataSetChanged();

                if(dataSnapshot.child("Lecture").child(course).exists()){

                    for (DataSnapshot postSnapshot : dataSnapshot.child("Lecture").child(course).getChildren()) {

                        LectureInfo info = postSnapshot.getValue(LectureInfo.class);
                        info.setKey(postSnapshot.getKey());
                        info.setClassCode(classCode);
                        lList.add(info);


                    }
                    lAdapter.notifyDataSetChanged();

                }else {
                    empty.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ViewLecture.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


    }

}