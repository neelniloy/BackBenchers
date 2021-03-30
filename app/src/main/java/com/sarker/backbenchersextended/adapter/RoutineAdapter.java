package com.sarker.backbenchersextended.adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sarker.backbenchersextended.R;
import com.sarker.backbenchersextended.model.RoutineInfo;
import com.sarker.backbenchersextended.SetAlarm;

import java.util.ArrayList;
import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class RoutineAdapter extends RecyclerView.Adapter<RoutineAdapter.NewsViewHolder> {

    private Context nContext;
    private ArrayList<RoutineInfo> rList;
    //private  int code;
    private int lastPosition = -1;


    public RoutineAdapter(Context context, ArrayList<RoutineInfo> rLists) {
        nContext = context;
        rList = rLists;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(nContext).inflate(R.layout.show_routine, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final NewsViewHolder holder, final int position) {

        final RoutineInfo info = rList.get(position);



        final String routineKey = info.getRoutineKey().toString();
        final String day = info.getDay();

        holder.courseTitle.setText(info.getCourseName());
        holder.courseCode.setText(info.getCourseCode());
        holder.roomNo.setText(info.getRoomNo());
        holder.courseTeacher.setText(info.getCourseTeacher());
        holder.classTime.setText(info.getClassTime());


        SharedPreferences preferences = nContext.getSharedPreferences("ROUTINE_FILE_NAME", Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        String key = preferences.getString(day+""+position, "");

        if(key.equals(info.routineKey)){
            holder.clock1.setVisibility(View.VISIBLE);
            holder.clock0.setVisibility(View.GONE);
        }else {
            holder.clock0.setVisibility(View.VISIBLE);
            holder.clock1.setVisibility(View.GONE);
        }


        holder.clock0.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                //code = Integer.parseInt(info.getRoutineKey())/1000;

                    int a,b;
                    String time = info.getClassTime().substring(0,2);
                    String time2 = info.getClassTime().substring(3,5);
                    String check = info.getClassTime().substring(6,8);

                    if(check.equals("PM") && !(time.equals("12"))){
                        a = Integer.parseInt(time);
                        a+=12;
                    }
                    else{
                        a = Integer.parseInt(time);
                    }

                    b = Integer.parseInt(time2);
                    b-=15;

                    if(b<0){
                        b+=60;
                        a-=1;
                    }else if (b==0){
                        b = 45;
                        a-=1;
                    }

                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.HOUR_OF_DAY, a);
                    c.set(Calendar.MINUTE, b);
                    c.set(Calendar.SECOND, 0);

                    editor.putString(day+""+position, info.routineKey);
                    editor.apply();

                holder.clock0.setVisibility(View.GONE);
                holder.clock1.setVisibility(View.VISIBLE);
                    startAlarm(c,Integer.parseInt(info.getRoutineKey())/1000);
                    Toast.makeText(nContext, "Alarm Activated", Toast.LENGTH_SHORT).show();



                }

        });


        holder.clock1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                //code = Integer.parseInt(routineKey)/1000;


                    cancelAlarm(Integer.parseInt(info.getRoutineKey())/1000);
                    holder.clock1.setVisibility(View.GONE);
                    holder.clock0.setVisibility(View.VISIBLE);


                    //editor.putString("key", info.routineKey);
                editor.putString(day+""+position, "");
                editor.apply();

                    Toast.makeText(nContext, "Alarm Canceled", Toast.LENGTH_SHORT).show();

            }

        });

        setAnimation(holder.itemView, position);




    }

    @Override
    public void onViewDetachedFromWindow(NewsViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
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
        return rList.size();
    }


    public class NewsViewHolder extends RecyclerView.ViewHolder {

        public TextView courseTitle, courseCode, courseTeacher, roomNo, classTime;
        public ImageView clock0,clock1;
        public DatabaseReference databaseReference;
        public FirebaseAuth mAuth;
        public String current_user_id;

        public NewsViewHolder(View itemView) {
            super(itemView);

            courseTitle = itemView.findViewById(R.id.course_title);
            courseCode = itemView.findViewById(R.id.course_code);
            courseTeacher = itemView.findViewById(R.id.course_teacher);
            roomNo = itemView.findViewById(R.id.room_no);
            classTime = itemView.findViewById(R.id.class_time);
            clock0 = itemView.findViewById(R.id.set_alarm0);
            clock1 = itemView.findViewById(R.id.set_alarm1);

            mAuth = FirebaseAuth.getInstance();
            current_user_id = mAuth.getCurrentUser().getUid();


            databaseReference = FirebaseDatabase.getInstance().getReference().child("Routine").child(current_user_id.substring(7,14));

        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void startAlarm(Calendar c,int key) {
        AlarmManager alarmManager = (AlarmManager) nContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(nContext, SetAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(nContext, key, intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 7);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(),7 * 24 * 60 * 60 * 1000, pendingIntent);
    }



    private void cancelAlarm(int key) {
        AlarmManager alarmManager = (AlarmManager) nContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(nContext, SetAlarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(nContext, key, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        Toast.makeText(nContext, "Alarm Canceled", Toast.LENGTH_SHORT).show();
    }

    private void setAnimation(View viewToAnimate, int position)
    {

        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(nContext, R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
        else if ( position < lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(nContext, R.anim.item_animation_fall_down);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

}
