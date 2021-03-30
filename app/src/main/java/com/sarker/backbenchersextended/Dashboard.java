package com.sarker.backbenchersextended;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private CardView profileCard,routineCard,lectureCard,gcodeCard,teacherCard,studentCard;

    private long backPressedTime;
    private Toast backtoast;
    private DrawerLayout mdrawerLayout;
    FirebaseAuth mAuth;
    private GoogleSignInClient googleSignInClient;
    private DatabaseReference update,userRef,classRef;
    private String url,classCode,current_user_id;
    private ProgressDialog progressDialog;

    SharedPreferences preferences ;
    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_dashboard);


        final NavigationView navigationView = findViewById(R.id.nav_view);

        Toolbar toolbar = findViewById(R.id.tool_bar);
        //setSupportActionBar(toolbar);

        mdrawerLayout = findViewById(R.id.drawer);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mdrawerLayout,toolbar,R.string.open,R.string.close);

        mdrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        profileCard = findViewById(R.id.cv_profile);
        routineCard = findViewById(R.id.cv_routine);
        lectureCard = findViewById(R.id.cv_lecture);
        gcodeCard = findViewById(R.id.cv_gcode);
        teacherCard = findViewById(R.id.cv_teacher);
        studentCard = findViewById(R.id.cv_student);

        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        update = FirebaseDatabase.getInstance().getReference().child("CheckUpdate");
        userRef = FirebaseDatabase.getInstance().getReference().child("UsersData").child(current_user_id);
        classRef = FirebaseDatabase.getInstance().getReference().child("Classroom");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        preferences = getSharedPreferences("CLASS_FILE_NAME", Context.MODE_PRIVATE);
        editor = preferences.edit();




        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                classCode = snapshot.child("classCode").getValue(String.class);

                classRef.child(classCode).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists()){

                            String temp = snapshot.child("owner").getValue(String.class);

                            if (current_user_id.equals(temp)){

                                Menu nav_Menu = navigationView.getMenu();
                                nav_Menu.findItem(R.id.nav_admin).setVisible(true);
                            }else {
                                Menu nav_Menu = navigationView.getMenu();
                                nav_Menu.findItem(R.id.nav_admin).setVisible(false);
                            }

                        }else {
                            Menu nav_Menu = navigationView.getMenu();
                            nav_Menu.findItem(R.id.nav_admin).setVisible(false);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        profileCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              Intent  intent = new Intent(Dashboard.this, MyProfile.class);
              startActivity(intent);

            }
        });

        routineCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent  intent = new Intent(Dashboard.this, Routine.class);
                startActivity(intent);

            }
        });

        lectureCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent  intent = new Intent(Dashboard.this, Lecture.class);
                intent.putExtra("classCode",classCode);
                startActivity(intent);

            }
        });

        gcodeCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent  intent = new Intent(Dashboard.this, BLCLink.class);
                intent.putExtra("from","user");
                intent.putExtra("classCode",classCode);
                startActivity(intent);

            }
        });

        studentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent  intent = new Intent(Dashboard.this, Student.class);
                intent.putExtra("classCode",classCode);
                startActivity(intent);

            }
        });

        teacherCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent  intent = new Intent(Dashboard.this, Teacher.class);
                intent.putExtra("classCode",classCode);
                startActivity(intent);

            }
        });

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        Intent n;
        switch (menuItem.getItemId())
        {
            case R.id.nav_admin : n = new Intent(this,AdminPanel.class); startActivity(n);
                break;
            case R.id.nav_contribute :
                n = new Intent(this,AddLecture.class);
                n.putExtra("classCode",classCode);
                startActivity(n);
                break;

            case R.id.nav_news_feed :
                n = new Intent(this,FriendsFeed.class);
                n.putExtra("classCode",classCode);
                startActivity(n);
                break;
            case R.id.nav_write :
                n = new Intent(this,WriteSomething.class);
                n.putExtra("classCode",classCode);
                startActivity(n);
                break;

            case R.id.nav_need_blood : n = new Intent(this,NeedBlood.class);
                startActivity(n);
                break;
            case R.id.nav_about :
                AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
                builder.setTitle("About Backbenchers");
                builder.setIcon(R.drawable.ic_logo);
                builder.setCancelable(true);
                builder.setMessage("BackBenchers is a Class Management System that aims to simplify creating, distributing class materials. You can use BackBenchers in your school/college/varsity to streamline assignments, boost collaboration, and foster communication. Students can easily track classwork,can see their class routine, Course lectures,Teachers Information, Student Informations and many more...Also can share class lecture through Backbenchers.")
                        .setPositiveButton("Visit Us", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://github.com/neelniloy/BackBenchers"));
                                startActivity(intent);

                                dialog.cancel();

                            }
                        }).setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.cancel();
                    }

                });
                AlertDialog alert = builder.create();
                alert.show();
                break;

            case R.id.nav_check_update :

                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage("Checking...");
                progressDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        update.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){


                                    String version = dataSnapshot.child("version").getValue(String.class);
                                    url = dataSnapshot.child("url").getValue(String.class);

                                    String VersionName = BuildConfig.VERSION_NAME;

                                    if (version.equals(VersionName)) {

                                        Toast.makeText(Dashboard.this, "BackBenchers Is Up To Date", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();

                                    } else{
                                        progressDialog.dismiss();

                                        AlertDialog.Builder builder = new AlertDialog.Builder(Dashboard.this);
                                        builder.setTitle("New Version Available");
                                        builder.setIcon(R.drawable.logo);
                                        builder.setCancelable(true);
                                        builder.setMessage("Update BackBenchers For Better Experience")
                                                .setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setData(Uri.parse(url));
                                                        startActivity(intent);

                                                    }
                                                }).setNegativeButton("Not Now",new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {

                                                dialog.cancel();
                                            }

                                        });
                                        AlertDialog alert = builder.create();
                                        alert.show();
                                    }

                                }else {
                                    Toast.makeText(Dashboard.this, "BackBenchers Is Up To Date", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(Dashboard.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                },1500);

                break;

            case R.id.nav_logout :

                signOut();
                break;

            case R.id.nav_report_bug :

                String[] toemail = {"niloy64529@gmail.com"};

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL,toemail);
                intent.putExtra(Intent.EXTRA_SUBJECT, "Bug In \"BackBenchers\" App");
                intent.setType("message/rfc822");
                startActivity(Intent.createChooser(intent, "Choose an email client"));

                break;

        }
        return true;
    }


    @Override
    public void onBackPressed() {

        if (mdrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mdrawerLayout.closeDrawer(GravityCompat.START);
        }
        else if (backPressedTime + 2000 > System.currentTimeMillis()) {
            backtoast.cancel();
            super.onBackPressed();
            finish();
            return;
            }
        else {
            backtoast = Toast.makeText(Dashboard.this, "Press Again To Exit", Toast.LENGTH_SHORT);
            backtoast.show();
        }
        backPressedTime = System.currentTimeMillis();
    }

    private void signOut() {

        AlertDialog.Builder builder1 = new AlertDialog.Builder(Dashboard.this);
        builder1.setMessage("Are you sure you want to leave the classroom?");
        builder1.setCancelable(false);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        progressDialog = new ProgressDialog(Dashboard.this);
                        progressDialog.show();
                        progressDialog.setMessage("Leaving...");

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();

                                editor.putString("hasClass", "No");
                                editor.putString("classCode", "");
                                editor.apply();
                                googleSignInClient.signOut();
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(Dashboard.this,Login.class));
                                finishAffinity();

                            }
                        },1700);
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

//        Window view=((AlertDialog)alert11).getWindow();
//        //view.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        view.setBackgroundDrawableResource(R.drawable.dialog_shape);

        Button btnPositive = alert11.getButton(AlertDialog.BUTTON_POSITIVE);
        Button btnNegative = alert11.getButton(AlertDialog.BUTTON_NEGATIVE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) btnPositive.getLayoutParams();
        layoutParams.weight = 10;
        btnPositive.setLayoutParams(layoutParams);
        btnNegative.setLayoutParams(layoutParams);
    }

}
