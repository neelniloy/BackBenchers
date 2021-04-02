package com.sarker.backbenchersextended;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sarker.backbenchersextended.adapter.StudentAdapter;
import com.sarker.backbenchersextended.model.StudentInfo;

import java.util.ArrayList;

public class Student extends AppCompatActivity {

    private String classCode;
    private ImageView back;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private String  user_id;
    private DatabaseReference userRef;

    private RecyclerView sRecyclerView;
    private StudentAdapter sAdapter;

    private ArrayList<StudentInfo> sList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_student);


        final Intent i = getIntent();
        classCode = i.getStringExtra("classCode");

        sRecyclerView = findViewById(R.id.student_rv);


        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        sRecyclerView.setLayoutManager(linearLayoutManager);

        sList = new ArrayList<>();
        sAdapter = new StudentAdapter(this,sList);
        sRecyclerView.setAdapter(sAdapter);

        Query query  = userRef.orderByChild("id");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                sList.clear();
                sAdapter.notifyDataSetChanged();

                if (snapshot.exists()){

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        StudentInfo info = postSnapshot.getValue(StudentInfo.class);

                        String temp = snapshot.child(postSnapshot.getKey()).child("classCode").getValue().toString();

                        if (temp.equals(classCode)){

                            info.setKey(postSnapshot.getKey());
                            info.setClassCode(classCode);
                            sList.add(info);

                        }


                    }
                    sAdapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });




    }

}