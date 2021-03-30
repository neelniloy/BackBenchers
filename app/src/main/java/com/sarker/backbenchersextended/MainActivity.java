package com.sarker.backbenchersextended;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button create,join;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference classRef,userRef;
    private String name,email,sid = "not mentioned",photo ,phone = "not mentioned",blood= "not mentioned",current_user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_main);

        create = findViewById(R.id.btn_create_class);
        join = findViewById(R.id.btn_join_class);

        progressDialog = new ProgressDialog(MainActivity.this);


        firebaseAuth = FirebaseAuth.getInstance();
        current_user_id = firebaseAuth.getCurrentUser().getUid();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        classRef = FirebaseDatabase.getInstance().getReference().child("Classroom");
        userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");

        SharedPreferences preferences = getSharedPreferences("CLASS_FILE_NAME", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        String key = preferences.getString("hasClass", "");


        if (key.equals("Yes") && user != null){
            startActivity(new Intent(MainActivity.this,Dashboard.class));
            finishAffinity();
        }else {

        }



        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText input = new EditText(MainActivity.this);
                input.setText("    ");


                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Enter A Classroom Name")
                        .setView(input)
                        .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String cName = input.getText().toString().trim();

                                if(cName.isEmpty()){
                                    Toast.makeText(MainActivity.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{

                                    progressDialog.setMessage("Creating...");
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                            if (account !=null){
                                                name = account.getDisplayName();
                                                email = account.getEmail();
                                                photo = account.getPhotoUrl().toString();

                                            }

                                            final String classCode = UUID.randomUUID().toString().replaceAll("[^a-zA-Z0-9]", "").substring(7,14);

                                            Map cc = new HashMap();

                                            cc.put("name", cName);
                                            cc.put("owner", current_user_id);

                                            classRef.child(classCode).updateChildren(cc).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    progressDialog.dismiss();
                                                    editor.putString("hasClass", "Yes");
                                                    editor.putString("classCode", classCode);
                                                    editor.apply();

                                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                            if (snapshot.child(current_user_id).exists()){

                                                                Map add = new HashMap();

                                                                add.put("name", name);
                                                                add.put("email", email);
                                                                add.put("photo", photo);
                                                                add.put("classCode", classCode);
                                                                add.put("uid", current_user_id);

                                                                userRef.child(current_user_id).updateChildren(add);

                                                                startActivity(new Intent(MainActivity.this,Dashboard.class));
                                                                finishAffinity();
                                                                Toast.makeText(MainActivity.this, "Classroom Created Successfully", Toast.LENGTH_SHORT).show();

                                                            }else {

                                                                Map add = new HashMap();

                                                                add.put("name", name);
                                                                add.put("id", sid);
                                                                add.put("email", email);
                                                                add.put("blood", blood);
                                                                add.put("phone", phone);
                                                                add.put("photo", photo);
                                                                add.put("classCode", classCode);
                                                                add.put("uid", current_user_id);

                                                                userRef.child(current_user_id).updateChildren(add);

                                                                startActivity(new Intent(MainActivity.this,Dashboard.class));
                                                                finishAffinity();
                                                                Toast.makeText(MainActivity.this, "Classroom Created Successfully", Toast.LENGTH_SHORT).show();

                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                    });


                                                }
                                            });

                                        }
                                    },1700);

                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();


            }
        });





        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final EditText input = new EditText(MainActivity.this);
                input.setText("    ");


                final AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setMessage("Enter A Classroom Code")
                        .setView(input)
                        .setPositiveButton("Join", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                final String code = input.getText().toString().trim();

                                if(code.isEmpty()){
                                    Toast.makeText(MainActivity.this, "Empty Field", Toast.LENGTH_SHORT).show();
                                }
                                else{

                                    progressDialog.setMessage("Joining...");
                                    progressDialog.setCanceledOnTouchOutside(false);
                                    progressDialog.show();
                                   // classRef.child("class").setValue(code);
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {

                                            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                                            if (account !=null){
                                                name = account.getDisplayName();
                                                email = account.getEmail();
                                                photo = account.getPhotoUrl().toString();

                                            }

                                            classRef.child(code).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if (snapshot.exists()){

                                                        progressDialog.dismiss();
                                                        editor.putString("hasClass", "Yes");
                                                        editor.putString("classCode", code);
                                                        editor.apply();

                                                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                                if (snapshot.child(current_user_id).exists()){

                                                                    Map add = new HashMap();

                                                                    add.put("name", name);
                                                                    add.put("email", email);
                                                                    add.put("photo", photo);
                                                                    add.put("classCode", code);
                                                                    add.put("uid", current_user_id);

                                                                    userRef.child(current_user_id).updateChildren(add);

                                                                    startActivity(new Intent(MainActivity.this,Dashboard.class));
                                                                    finishAffinity();
                                                                    Toast.makeText(MainActivity.this, "Join Successfully", Toast.LENGTH_SHORT).show();

                                                                }else {

                                                                    Map add = new HashMap();

                                                                    add.put("name", name);
                                                                    add.put("id", sid);
                                                                    add.put("email", email);
                                                                    add.put("blood", blood);
                                                                    add.put("phone", phone);
                                                                    add.put("photo", photo);
                                                                    add.put("classCode", code);
                                                                    add.put("uid", current_user_id);

                                                                    userRef.child(current_user_id).updateChildren(add);

                                                                    startActivity(new Intent(MainActivity.this,Dashboard.class));
                                                                    finishAffinity();
                                                                    Toast.makeText(MainActivity.this, "Join Successfully", Toast.LENGTH_SHORT).show();

                                                                }

                                                            }

                                                            @Override
                                                            public void onCancelled(@NonNull DatabaseError error) {

                                                            }
                                                        });


                                                    }else {
                                                        progressDialog.dismiss();
                                                        Toast.makeText(MainActivity.this, "Invalid Code", Toast.LENGTH_SHORT).show();
                                                    }

                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {

                                                }
                                            });



                                        }
                                    },1700);

                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();

            }
        });



    }


}