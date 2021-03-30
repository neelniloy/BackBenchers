package com.sarker.backbenchersextended;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProfile extends AppCompatActivity {

    private ImageView back,eID,eBlood,ePhone,editOn,editOff,copy;
    private CircleImageView profilePic;
    private TextView profileName,profileEmail,profileID,profileBloodGroup,profilePhone,classCode;
    private FirebaseAuth mfirebaseAuth;
    private DatabaseReference uRef;
    private String name,email,sid ,phone,blood,current_user_id,code,photo;

    private ProgressBar progressBar;
    private int iStatus = 0;
    private int fStatus = 0;
    private Handler handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_my_profile);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);




        profilePic = findViewById(R.id.sProfile);
        profileName = findViewById(R.id.sName);
        profileEmail = findViewById(R.id.sEmail);
        profileID = findViewById(R.id.sID);
        profileBloodGroup = findViewById(R.id.sBlood);
        profilePhone = findViewById(R.id.sPhone);
        classCode = findViewById(R.id.classCode);
        copy = findViewById(R.id.code_copy);

        back = findViewById(R.id.back);

        eID = findViewById(R.id.edit_id);
        eBlood = findViewById(R.id.edit_blood);
        ePhone = findViewById(R.id.edit_phone);

        editOn = findViewById(R.id.edit_mode_on);
        editOff = findViewById(R.id.edit_mode_off);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mfirebaseAuth = FirebaseAuth.getInstance();
        current_user_id = mfirebaseAuth.getCurrentUser().getUid();
        uRef = FirebaseDatabase.getInstance().getReference().child("UsersData").child(current_user_id);



        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if (account !=null){
            name = account.getDisplayName();
            email = account.getEmail();
            //photo = account.getPhotoUrl();

            profileName.setText(name);
            profileEmail.setText(email);


        }

        uRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){


                    sid = snapshot.child("id").getValue(String.class);
                    blood = snapshot.child("blood").getValue(String.class);
                    phone = snapshot.child("phone").getValue(String.class);
                    code = snapshot.child("classCode").getValue(String.class);
                    photo = snapshot.child("photo").getValue(String.class);


                    Picasso.get().load(name).fit().centerInside().placeholder(R.drawable.profile_icon).into(profilePic);
                    Picasso.get().load(photo).fit().centerInside().placeholder(R.drawable.profile_icon).into(profilePic);

                    profileID.setText(sid);
                    profileBloodGroup.setText(blood);
                    profilePhone.setText(phone);
                    classCode.setText("Classroom : "+code);

                    int count = 3;

                    if (!sid.equals("not mentioned")){
                        count+=1;
                    }
                    if (!blood.equals("not mentioned")){
                        count+=1;
                    }
                    if (!phone.equals("not mentioned")){
                        count+=1;
                    }

                    fStatus = (int) Math.round(16.67*count);


                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (iStatus <= fStatus) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressBar.setProgress(iStatus);
                                    }
                                });
                                try {
                                    Thread.sleep(50);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                iStatus++;
                            }

                            runOnUiThread(new Runnable() {
                                public void run() {
                                    if (fStatus<100){

                                        Snackbar.make(findViewById(android.R.id.content), "Your Profile Is "+fStatus+"% Complete!\nPlease Update Your Profile Information.", 5500).show();

                                    }else {
                                        Snackbar.make(findViewById(android.R.id.content), "Congratulation! Your Profile Is 100% Completed!", 3000).show();
                                    }


                                }
                            });

                        }
                    }).start();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MyProfile.this,databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        ePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(MyProfile.this);

                if (phone.equals("not mentioned")){
                    input.setText("    ");
                }else {
                    input.setText("    "+phone);
                }



                AlertDialog dialog = new AlertDialog.Builder(MyProfile.this)
                        .setMessage("Enter Number")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String num = input.getText().toString().trim();

                                if(num.isEmpty()){
                                    Toast.makeText(MyProfile.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    uRef.child("phone").setValue(num);

                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });

        eID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(MyProfile.this);
                if (sid.equals("not mentioned")){
                    input.setText("    ");
                }else {
                    input.setText("    "+sid);
                }


                AlertDialog dialog = new AlertDialog.Builder(MyProfile.this)
                        .setMessage("Enter Student ID")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String id = input.getText().toString().trim();

                                if(id.isEmpty()){
                                    Toast.makeText(MyProfile.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    uRef.child("id").setValue(id);

                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });


        eBlood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(MyProfile.this);
                if (blood.equals("not mentioned")){
                    input.setText("    ");
                }else {
                    input.setText("    "+blood);
                }


                AlertDialog dialog = new AlertDialog.Builder(MyProfile.this)
                        .setMessage("Enter Blood Group")
                        .setView(input)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String group = input.getText().toString().trim();

                                if(group.isEmpty()){
                                    Toast.makeText(MyProfile.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    uRef.child("blood").setValue(group);

                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });


        editOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editOn.setVisibility(View.GONE);
                editOff.setVisibility(View.VISIBLE);
                eID.setVisibility(View.VISIBLE);
                eBlood.setVisibility(View.VISIBLE);
                ePhone.setVisibility(View.VISIBLE);

                Toast.makeText(MyProfile.this, "Edit Mode Enabled", Toast.LENGTH_SHORT).show();

            }
        });

        editOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                editOff.setVisibility(View.GONE);
                eID.setVisibility(View.GONE);
                eBlood.setVisibility(View.GONE);
                ePhone.setVisibility(View.GONE);
                editOn.setVisibility(View.VISIBLE);

                Toast.makeText(MyProfile.this, "Edit Mode Disabled", Toast.LENGTH_SHORT).show();
            }
        });


        copy.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

                ClipboardManager cb=(ClipboardManager) v.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cd= ClipData.newPlainText("Label",code);
                cb.setPrimaryClip(cd);
                Toast.makeText(v.getContext(), "Copied "+code, Toast.LENGTH_SHORT).show();
            }
        });


    }
}