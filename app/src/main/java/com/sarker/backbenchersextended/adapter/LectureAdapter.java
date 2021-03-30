package com.sarker.backbenchersextended.adapter;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarker.backbenchersextended.model.LectureInfo;
import com.sarker.backbenchersextended.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LectureAdapter extends RecyclerView.Adapter<LectureAdapter.NewsViewHolder> {


    private Context pContext;
    private ArrayList<LectureInfo> cList;
    private ProgressDialog progressDialog;
    private String current_user_id = " ";
    private static String mdate;


    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;




    private static long currentDate() {
        Calendar calFordDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("d MMMM yyyy, hh:mm aa");
        String date = currentDate.format(calFordDate.getTime());
        return getDateInMillis(date);
    }

    public static String getTimeAgo(String date) {
        long time = Long.parseLong(date);
        if (time < 1000000000000L) {
            time *= 1000;
        }

        long now = currentDate();
        if (time > now || time <= 0) {
            return "in the future";
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a min ago";
        } else if (diff < 60 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " min ago";
        } else if (diff < 2 * HOUR_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hrs ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else if (diff < 72 * HOUR_MILLIS) {
            return diff / DAY_MILLIS + " days ago";
        } else {
            return convertDate(mdate,"hh:mm a, d MMMM yyyy");
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

    public static String convertDate(String dateInMilliseconds,String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public LectureAdapter(Context context, ArrayList<LectureInfo> productLists) {
        pContext = context;
        cList = productLists;
    }

    @NonNull
    @Override
    public LectureAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(pContext).inflate(R.layout.lecture_item, parent, false);
        return new LectureAdapter.NewsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final LectureAdapter.NewsViewHolder holder, final int position) {
        final LectureInfo info = cList.get(position);

        final String key = info.getKey();

        mdate = info.getPostTime();
        holder.lecture.setText(info.getLecture());
        holder.postTime.setText(getTimeAgo(info.getPostTime()));

        Picasso.get().load(info.getImage()).placeholder(R.drawable.ic_logo).fit().centerInside().into(holder.image);


        holder.userRef.child(info.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String n = snapshot.child("name").getValue(String.class);
                    String url = snapshot.child("photo").getValue(String.class);

                    holder.name.setText(n);
                    Picasso.get().load(url).placeholder(R.drawable.ic_logo).fit().centerInside().into(holder.proPic);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        //visibility off
        if (info.getUid().equals(current_user_id)){
            holder.delete.setVisibility(View.VISIBLE);
        }else {
            holder.delete.setVisibility(View.GONE);
        }




//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Intent intent = new Intent(v.getContext(), ViewLecture.class);
//                intent.putExtra("course", key);
//                pContext.startActivity(intent);
//            }
//        });

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
                        holder.CourseRef.child(info.getClassCode()).child("Lecture").child(info.getCourse()).child(key).removeValue();
                        notifyItemChanged(position);

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


        public TextView name,postTime,lecture;
        public ImageView delete,proPic,image;
        public DatabaseReference CourseRef,userRef;
        public FirebaseAuth mfirebaseAuth;


        public NewsViewHolder(View itemView) {
            super(itemView);


            mfirebaseAuth = FirebaseAuth.getInstance();

            final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

            if (mFirebaseuser != null) {

                current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            } else {

            }

            name = itemView.findViewById(R.id.owner_name);
            postTime = itemView.findViewById(R.id.post_time);
            lecture = itemView.findViewById(R.id.lecture);
            proPic = itemView.findViewById(R.id.profile_image);
            delete = itemView.findViewById(R.id.lecture_delete);
            image =itemView.findViewById(R.id.lecture_image);

            CourseRef = FirebaseDatabase.getInstance().getReference().child("Classroom");
            userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");




        }


    }


}
