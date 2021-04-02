package com.sarker.backbenchersextended;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.sarker.backbenchersextended.adapter.NewsFeedAdapter;
import com.sarker.backbenchersextended.model.Posts;

import java.util.ArrayList;

public class FriendsFeed extends AppCompatActivity {

    private RecyclerView nRecyclerView;
    private NewsFeedAdapter nAdapter;
    private FirebaseStorage firebaseStorage;
    private DatabaseReference postRef;
    private ArrayList<Posts> postsList;
    private FirebaseAuth mAuth;
    private  String current_user_id,classCode;
    private ProgressBar mProgressCircle;
    private ProgressDialog progressDialog;
    private TextView emptytext;
    private ImageView back;
    private Boolean check = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_friends_feed);

        Intent i = getIntent();
        classCode = i.getStringExtra("classCode");

        nRecyclerView = findViewById(R.id.feed_rv);
        nRecyclerView.setHasFixedSize(true);

        emptytext = findViewById(R.id.empty_review);

        back = findViewById(R.id.back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();

            }
        });

//        nRecyclerView.setItemViewCacheSize(20);
//       nRecyclerView.setDrawingCacheEnabled(true);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(FriendsFeed.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        nRecyclerView.setLayoutManager(linearLayoutManager);

        mProgressCircle = findViewById(R.id.nprogress_circle);

        postsList = new ArrayList<>();

        nAdapter = new NewsFeedAdapter(FriendsFeed.this, postsList);

        nRecyclerView.setAdapter(nAdapter);


        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();

        firebaseStorage = FirebaseStorage.getInstance();
        postRef = FirebaseDatabase.getInstance().getReference().child("Classroom").child(classCode).child("Posts");


        postRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                postsList.clear();
                nAdapter.notifyDataSetChanged();

                if (snapshot.exists()){

                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                        Posts info = postSnapshot.getValue(Posts.class);
                        info.setKey(postSnapshot.getKey());
                        info.setClassCode(classCode);
                        postsList.add(info);


                    }
                    mProgressCircle.setVisibility(View.GONE);
                    nAdapter.notifyDataSetChanged();

                }else {
                    mProgressCircle.setVisibility(View.GONE);
                    emptytext.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    public static boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }
}