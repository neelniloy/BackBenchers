package com.sarker.backbenchersextended.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sarker.backbenchersextended.AddCourse;
import com.sarker.backbenchersextended.model.CourseInfo;
import com.sarker.backbenchersextended.R;
import com.sarker.backbenchersextended.ViewLecture;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.NewsViewHolder> {

    private Context pContext;
    private ArrayList<CourseInfo> cList;
    private ProgressDialog progressDialog;
    private String current_user_id = " ";



    public CourseAdapter(Context context, ArrayList<CourseInfo> productLists) {
        pContext = context;
        cList = productLists;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(pContext).inflate(R.layout.course_item, parent, false);
        return new NewsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final NewsViewHolder holder, int position) {
        final CourseInfo info = cList.get(position);

        final String key = info.getKey();

        holder.courseName.setText(info.getCourseName());
        Picasso.get().load(info.getCourseImage()).placeholder(R.drawable.ic_logo).fit().centerInside().into(holder.couseImage);



        if(pContext instanceof AddCourse){
            holder.delete.setVisibility(View.VISIBLE);
        }else {
            holder.delete.setVisibility(View.GONE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(v.getContext(), ViewLecture.class);
                intent.putExtra("classCode", info.getClassCode());
                intent.putExtra("course", key);
                pContext.startActivity(intent);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(pContext);
                progressDialog.show();
                progressDialog.setMessage("Deleting...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();
                        holder.CourseRef.child(info.getClassCode()).child("Course").child(key).removeValue();
                        notifyDataSetChanged();

                        Toast.makeText(pContext, "Successfully Deleted", Toast.LENGTH_SHORT).show();

                    }
                },1500);

            }
        });




    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return cList.size();
    }



    public class NewsViewHolder extends RecyclerView.ViewHolder {


        public TextView courseName;
        public ImageView delete,couseImage;
        public DatabaseReference CourseRef;
        public FirebaseAuth mfirebaseAuth;


        public NewsViewHolder(View itemView) {
            super(itemView);


            mfirebaseAuth = FirebaseAuth.getInstance();

            final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

            if (mFirebaseuser != null) {

                current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            } else {

            }

            courseName = itemView.findViewById(R.id.course_name);
            delete = itemView.findViewById(R.id.course_delete);
            couseImage =itemView.findViewById(R.id.course_image);

            CourseRef = FirebaseDatabase.getInstance().getReference().child("Classroom");




        }


        }



}
