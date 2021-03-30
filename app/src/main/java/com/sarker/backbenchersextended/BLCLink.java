package com.sarker.backbenchersextended;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarker.backbenchersextended.model.BLCInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BLCLink extends AppCompatActivity {

    private ScrollView scrollView;
    private String classCode,from;
    private ImageView back;
    private TextView empty;
    private TextInputEditText Title,Description;
    private TextInputLayout titleTextLayout,descriptionTextLayout;
    private Button btnAdd;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private String  user_id;
    private DatabaseReference blcRef;

    private RecyclerView bRecyclerView;
    private BLCAdapter bAdapter;

    private ArrayList<BLCInfo> bList ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_b_l_c_link);

        scrollView = findViewById(R.id.blc_scroll);
        Intent i = getIntent();
        classCode = i.getStringExtra("classCode");
        from = i.getStringExtra("from");

        empty= findViewById(R.id.empty_blc);
        bRecyclerView = findViewById(R.id.blc_rv);

        if (from.equals("admin")){
            scrollView.setVisibility(View.VISIBLE);
        }else {
            scrollView.setVisibility(View.GONE);
        }

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btnAdd = findViewById(R.id.btn_add_blc);

        Title = findViewById(R.id.et_title);
        titleTextLayout = findViewById(R.id.editTextTitleLayout);

        Description = findViewById(R.id.et_description);
        descriptionTextLayout = findViewById(R.id.editTextDescriptionLayout);


        mAuth = FirebaseAuth.getInstance();

        user_id = mAuth.getCurrentUser().getUid();

        blcRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(classCode);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        bRecyclerView.setLayoutManager(linearLayoutManager);

        bList = new ArrayList<>();
        bAdapter = new BLCAdapter(this,bList);
        bRecyclerView.setAdapter(bAdapter);



        blcRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                bList.clear();
                bAdapter.notifyDataSetChanged();

                if (snapshot.child("BLC").exists()){

                    for (DataSnapshot postSnapshot : snapshot.child("BLC").getChildren()) {

                        BLCInfo info = postSnapshot.getValue(BLCInfo.class);
                        info.setKey(postSnapshot.getKey());
                        info.setClassCode(classCode);
                        info.setFrom(from);
                        bList.add(info);

                    }
                    bAdapter.notifyDataSetChanged();

                }else {
                    empty.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String title = Title.getText().toString();
                final String description = Description.getText().toString();


                if (title.isEmpty()) {
                    titleTextLayout.setError("*Title required");
                    Title.requestFocus();

                }else if ( description.isEmpty() ) {

                    descriptionTextLayout.setError("*Description required");
                    Description.requestFocus();
                }
               else if (!(title.isEmpty() && description.isEmpty())) {

                    progressDialog = new ProgressDialog(BLCLink.this);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.setMessage("Adding...");
                    progressDialog.show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            progressDialog.dismiss();

                            Map add = new HashMap();

                            add.put("des", description);
                            add.put("title", title);

                            blcRef.child("BLC").push().updateChildren(add);


                            Toast.makeText(BLCLink.this, "Successfully Added", Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(BLCLink.this, BLCLink.class);
                            i.putExtra("classCode",classCode);
                            i.putExtra("from","admin");
                            startActivity(i);
                            finish();

                        }
                    },1000);



                } else {
                    progressDialog.dismiss();
                    Toast.makeText(BLCLink.this, "Error Occurred!", Toast.LENGTH_SHORT).show();
                }
            }

        });


    }
}