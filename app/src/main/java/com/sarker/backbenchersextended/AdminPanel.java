package com.sarker.backbenchersextended;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AdminPanel extends AppCompatActivity {

    private ImageView back;
    private FirebaseAuth mAuth;
    private CardView addRoutine,manageRoutine,addCourse,addBLC,addTeacher;
    private String current_user_id,classCode;
    private DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_admin_panel);

        addRoutine = findViewById(R.id.cv_add_routine);
        manageRoutine = findViewById(R.id.cv_manage_routine);
        addCourse = findViewById(R.id.cv_add_course);
        addBLC = findViewById(R.id.cv_blc_code);
        addTeacher = findViewById(R.id.cv_add_teacher);

        back = findViewById(R.id.ic_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("UsersData").child(current_user_id);


                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){

                            classCode = snapshot.child("classCode").getValue(String.class);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        addRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminPanel.this, AddRoutine.class);
                startActivity(n);

            }
        });


        manageRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminPanel.this, ManageRoutine.class);
                startActivity(n);

            }
        });


        addCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminPanel.this, AddCourse.class);
                n.putExtra("classCode",classCode);
                startActivity(n);

            }
        });

        addBLC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminPanel.this, BLCLink.class);
                n.putExtra("from","admin");
                n.putExtra("classCode",classCode);
                startActivity(n);

            }
        });

        addTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent n = new Intent(AdminPanel.this, AddTeacher.class);
                n.putExtra("classCode",classCode);
                startActivity(n);

            }
        });
    }
}