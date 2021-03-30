package com.sarker.backbenchersextended;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Comments extends AppCompatActivity {

    private String comment_post_key,current_user_id,postOwner,classCode;
    private EditText writeComment;
    private ImageView sendComent,backspace;
    private RecyclerView crecyclerView;
    private FirebaseAuth mfirebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference Comment,UserRef;
    private ArrayList<CommentInfo> cList;
    private CommentAdapter cAdapter;
    private TextView emptytext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_comments);

        writeComment = findViewById(R.id.write_comment);
        sendComent = findViewById(R.id.send_comment);
        crecyclerView = findViewById(R.id.comment_recycler);

        emptytext = findViewById(R.id.empty_comment);

        backspace = findViewById(R.id.back);


        crecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        //linearLayoutManager.setStackFromEnd(true);
        crecyclerView.setLayoutManager(linearLayoutManager);

        cList = new ArrayList<>();

        cAdapter = new CommentAdapter(this, cList);

        crecyclerView.setAdapter(cAdapter);

        mfirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        current_user_id = mfirebaseAuth.getCurrentUser().getUid();

        comment_post_key = getIntent().getExtras().get("comment_key").toString();
        classCode = getIntent().getExtras().get("classCode").toString();

        Comment = firebaseDatabase.getReference().child("Classroom").child(classCode).child("Posts").child(comment_post_key).child("Comment").child(comment_post_key);
        UserRef = firebaseDatabase.getReference().child("UsersData");

        backspace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        sendComent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(writeComment.getWindowToken(), 0);

                emptytext.setVisibility(View.GONE);

                UserRef.child(current_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()){

                            String fname = dataSnapshot.child("userFirstName").getValue(String.class);
                            String lname = dataSnapshot.child("userLastName").getValue(String.class);
                            String name = fname+" "+lname;

                            setComment(name);
                            writeComment.setText("");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });



        Comment.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()){

                    cList.clear();
                    cAdapter.notifyDataSetChanged();

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        CommentInfo posts = postSnapshot.getValue(CommentInfo.class);
                        posts.setCoomentKey(postSnapshot.getKey());
                        posts.setCommentPostKey(comment_post_key);
                        posts.setClassCode(classCode);
                        cList.add(posts);
                    }
                    cAdapter.notifyDataSetChanged();

                }
                else {
                    emptytext.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Comments.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();

            }

        });



    }

    private void setComment(String name) {

        String commentText = writeComment.getText().toString().trim();

        if(TextUtils.isEmpty(commentText)){
            Toast.makeText(this, "write comment first!", Toast.LENGTH_SHORT).show();
        }
        else {

            Calendar calFordDate = Calendar.getInstance();
            SimpleDateFormat currentDate = new SimpleDateFormat("d MMMM yyyy, hh:mm aa");
            String date = currentDate.format(calFordDate.getTime());

            String saveCurrentDate = String.valueOf(getDateInMillis(date));

            HashMap comment = new HashMap();

            comment.put("comment",commentText);
            comment.put("date",saveCurrentDate);
            comment.put("uid",current_user_id);
            comment.put("commentPostKey",comment_post_key);

            final String commentKey = Comment.push().getKey();

            Comment.child(commentKey).updateChildren(comment)
                    .addOnCompleteListener(new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {

                            if(task.isSuccessful()){

                                Toast.makeText(Comments.this, "Comment Added", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Toast.makeText(Comments.this, "something went wrong. try again!", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }

    }


    public static long getDateInMillis(String srcDate) {
        SimpleDateFormat desiredFormat = new SimpleDateFormat(
                "d MMMM yyyy, hh:mm aa");

        long dateInMillis = 0;
        try {
            Date date = desiredFormat.parse(srcDate);
            dateInMillis = date.getTime();
            return dateInMillis;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }


}
