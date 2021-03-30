package com.sarker.backbenchersextended;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarker.backbenchersextended.adapter.ManageRoutineAdapter;
import com.sarker.backbenchersextended.model.RoutineInfo;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ManageRoutine extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private ManageRoutineAdapter mAdapter;
    private DatabaseReference mDatabaseRef,userRef;
    private ArrayList<RoutineInfo> mList;
    private ProgressBar mProgressCircle;
    private ImageView back;
    private TextView emptyRoutine;
    private FirebaseAuth mAuth;
    private String current_user_id,classCode;
    private ProgressDialog progressDialog;

    private String[] day = { "Saturday", "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday" };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_manage_routine);


        emptyRoutine = findViewById(R.id.empty_routine);
        mRecyclerView = findViewById(R.id.mrecycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mList = new ArrayList<>();
        mAdapter = new ManageRoutineAdapter(this,mList);
        mRecyclerView.setAdapter(mAdapter);


        mProgressCircle = findViewById(R.id.mprogress_circle);
        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("Classroom");

        userRef = FirebaseDatabase.getInstance().getReference().child("UsersData").child(current_user_id);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                classCode = snapshot.child("classCode").getValue(String.class);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        mDatabaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long count = dataSnapshot.getChildrenCount();

                    if(dataSnapshot.exists()){

                        mList.clear();
                        mAdapter.notifyDataSetChanged();

                        for (int i=0;i<6;i++){

                            String days_of_week = day[i];

                            for (DataSnapshot postSnapshot : dataSnapshot.child(classCode).child("Routine").child(days_of_week).getChildren()) {

                                RoutineInfo info = postSnapshot.getValue(RoutineInfo.class);
                                info.setRoutineKey(postSnapshot.getKey());
                                mList.add(info);

                            }

                        }

                        mAdapter.notifyDataSetChanged();
                        mProgressCircle.setVisibility(View.INVISIBLE);

                    }else {
                        emptyRoutine.setVisibility(View.VISIBLE);
                        mProgressCircle.setVisibility(View.INVISIBLE);
                    }




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ManageRoutine.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressCircle.setVisibility(View.INVISIBLE);
            }

        });



    }
}