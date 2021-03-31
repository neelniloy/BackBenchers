package com.sarker.backbenchersextended;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sarker.backbenchersextended.adapter.TeacherAdapter;
import com.sarker.backbenchersextended.model.TeacherInfo;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class Teacher extends AppCompatActivity {

    private RecyclerView tRecyclerView;
    private TeacherAdapter tAdapter;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private StorageReference ref;
    private FirebaseAuth mAuth;
    private DatabaseReference tRef;
    private ArrayList<TeacherInfo> tList;
    private String classCode,user_id;
    private ImageView back;
    private TextView empty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_teacher);

        final Intent i = getIntent();
        classCode = i.getStringExtra("classCode");

        tRecyclerView = findViewById(R.id.teacher_rv);


        back = findViewById(R.id.back);
        empty = findViewById(R.id.empty_teacher);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        tRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(classCode).child("Teacher");


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        tRecyclerView.setLayoutManager(linearLayoutManager);

        tList = new ArrayList<>();
        tAdapter = new TeacherAdapter(this,tList);
        tRecyclerView.setAdapter(tAdapter);

        Query query  = tRef.orderByChild("name");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                tList.clear();
                tAdapter.notifyDataSetChanged();

                if (snapshot.exists()){

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        TeacherInfo info = postSnapshot.getValue(TeacherInfo.class);


                            info.setKey(postSnapshot.getKey());
                            info.setClassCode(classCode);
                            tList.add(info);


                    }
                    tAdapter.notifyDataSetChanged();

                }else {
                    empty.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
