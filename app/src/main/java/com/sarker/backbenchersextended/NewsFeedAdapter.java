package com.sarker.backbenchersextended;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.NewsViewHolder> {

    private static String mdate;
    private Context nContext;
    private ProgressDialog progressDialog;
    private ArrayList<Posts> postsList;
    private String current_user_id,name;

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


    public NewsFeedAdapter(Context context, ArrayList<Posts> postsLists) {
        nContext = context;
        postsList = postsLists;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(nContext).inflate(R.layout.show_newsfeed, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position) {
        final Posts post = postsList.get(position);

        final String key = post.getKey();
        mdate = post.getDate();

        holder.description.setText(post.getDescription());

        if (post.getPostimage().equals("")){
            holder.postimage.setVisibility(View.GONE);
        }else {
            Picasso.get().load(post.getPostimage()).placeholder(R.drawable.logo).fit().centerCrop().into(holder.postimage);
        }


        holder.date.setText(getTimeAgo(post.getDate()));


        holder.setLikebtnstatus(key,post.getClassCode());

        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                holder.LikeChecker = true;

                holder.LikeRef.child(post.getClassCode()).child("Posts").child(key).child("Like").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                       if(holder.LikeChecker.equals(true)){

                           if(dataSnapshot.child(key).hasChild(current_user_id)){

                               holder.LikeRef.child(post.getClassCode()).child("Posts").child(key).child("Like").child(key).child(current_user_id).removeValue();
                               holder.LikeChecker = false;
                           }
                           else {

                               holder.LikeRef.child(post.getClassCode()).child("Posts").child(key).child("Like").child(key).child(current_user_id).child("uid").setValue(current_user_id);
                               holder.LikeChecker = false;
                           }

                       }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(v.getContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });



        holder.userRef.child(post.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.exists()) {

                    String name = dataSnapshot.child("name").getValue().toString();

                    String url = dataSnapshot.child("photo").getValue().toString();


                    holder.fullname.setText(name);

                    Picasso.get().load(url).placeholder(R.drawable.logo).fit().centerCrop().into(holder.profileimage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


        holder.ComRef.child(post.getClassCode()).child("Comment").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){

                    long replyCount = 0;

                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                        String key = postSnapshot.getKey();

                        if (dataSnapshot.child(key).child("Reply").exists()){

                            long count = dataSnapshot.child(key).child("Reply").getChildrenCount();
                            replyCount+=count;
                        }
                    }

                    holder.comcount = dataSnapshot.getChildrenCount();

                    holder.commentCount.setText(String.valueOf(holder.comcount+replyCount));

                }
                else {
                    holder.commentCount.setText(String.valueOf(holder.comcount));
                }



                if (holder.comcount==0) {

                    holder.commentCount.setVisibility(View.GONE);
                }
                else {

                    holder.commentCount.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        holder.cmntbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_key = key;

                Intent intent = new Intent(v.getContext(), Comments.class);
                intent.putExtra("comment_key", comment_key);
                intent.putExtra("classCode", post.getClassCode());
                nContext.startActivity(intent);
            }
        });



        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog = new ProgressDialog(nContext);
                progressDialog.show();
                progressDialog.setMessage("Deleting...");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.dismiss();
                        holder.postRef.child(post.getClassCode()).child("Posts").child(key).removeValue();
                        notifyItemChanged(position);

                        Toast.makeText(nContext, "Successfully Deleted", Toast.LENGTH_SHORT).show();

                    }
                },1500);

            }
        });




    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }



    public class NewsViewHolder extends RecyclerView.ViewHolder {

        Boolean LikeChecker = false;
        public CardView like, cmntbtn;
        private ImageView likebtn, postimage,delete;
        public CircleImageView profileimage;
        public TextView date, description, fullname, numOfLikes, commentCount;
        DatabaseReference LikeRef, userRef, postRef, ComRef;
        FirebaseAuth mAuth;
        int countLike;
        private long comcount = 0;


        public NewsViewHolder(View itemView) {
            super(itemView);

            LikeRef = FirebaseDatabase.getInstance().getReference("Classroom");
            userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");
            ComRef = FirebaseDatabase.getInstance().getReference().child("Classroom");
            postRef = FirebaseDatabase.getInstance().getReference().child("Classroom");

            mAuth = FirebaseAuth.getInstance();
            current_user_id = mAuth.getCurrentUser().getUid();
            numOfLikes = itemView.findViewById(R.id.numoflikes);
            likebtn = itemView.findViewById(R.id.likebtn);
            like = itemView.findViewById(R.id.cv_like);
            cmntbtn = itemView.findViewById(R.id.cv_comment);
            postimage = itemView.findViewById(R.id.post_image);
            fullname = itemView.findViewById(R.id.tv_news_name);
            date = itemView.findViewById(R.id.tv_date);
            delete = itemView.findViewById(R.id.newsfeed_delete);
            description = itemView.findViewById(R.id.tv_news_feed);
            profileimage = itemView.findViewById(R.id.newsfeed_pic);


            commentCount = itemView.findViewById(R.id.commentCount);


        }


        public void setLikebtnstatus(final String postkey, String classCode) {

            LikeRef.child(classCode).child("Posts").child(postkey).child("Like").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postkey).hasChild(current_user_id)) {

                        countLike = (int) dataSnapshot.child(postkey).getChildrenCount();
                        likebtn.setImageResource(R.drawable.like);
                        numOfLikes.setText(Integer.toString(countLike));
                    } else {

                        countLike = (int) dataSnapshot.child(postkey).getChildrenCount();
                        likebtn.setImageResource(R.drawable.dislike);
                        numOfLikes.setText(Integer.toString(countLike));
                    }

                    if (countLike == 0) {
                        numOfLikes.setVisibility(View.GONE);
                    } else {
                        numOfLikes.setVisibility(View.VISIBLE);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

    }
}
