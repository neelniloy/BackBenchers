package com.sarker.backbenchersextended;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
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
import com.sarker.backbenchersextended.model.BLCInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.NewsViewHolder> {

    private Context pContext;
    private ArrayList<StudentInfo> bList;
    private ProgressDialog progressDialog;
    private String current_user_id = " ";



    public StudentAdapter(Context context, ArrayList<StudentInfo> Lists) {
        pContext = context;
        bList = Lists;
    }

    @NonNull
    @Override
    public StudentAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(pContext).inflate(R.layout.show_student, parent, false);
        return new StudentAdapter.NewsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final StudentAdapter.NewsViewHolder holder, final int position) {

        final StudentInfo info = bList.get(position);

        final String key = info.getKey();


        holder.name.setText(info.getName());
        holder.id.setText("ID:  "+info.getId());
        holder.email.setText("Email:  "+info.getEmail());
        holder.blood.setText("Blood:  "+info.getBlood());
        holder.phone.setText("Mobile:  "+info.getPhone());

        Picasso.get().load(info.getPhoto()).placeholder(R.drawable.student).fit().centerInside().into(holder.image);

        if (current_user_id.equals(info.getUid())){

            holder.you.setVisibility(View.VISIBLE);

        }else {

            holder.you.setVisibility(View.GONE);

        }

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
        return bList.size();
    }



    public class NewsViewHolder extends RecyclerView.ViewHolder {


        public TextView name,id,email,blood,phone,you;
        public ImageView image;
        public DatabaseReference userRef;
        public FirebaseAuth mfirebaseAuth;


        public NewsViewHolder(View itemView) {
            super(itemView);


            mfirebaseAuth = FirebaseAuth.getInstance();

            final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

            if (mFirebaseuser != null) {

                current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            } else {

            }

            name = itemView.findViewById(R.id.name);
            you = itemView.findViewById(R.id.you);
            id = itemView.findViewById(R.id.ID);
            email =itemView.findViewById(R.id.email);
            blood = itemView.findViewById(R.id.blood);
            phone = itemView.findViewById(R.id.phone);
            image =itemView.findViewById(R.id.sPic);

            userRef = FirebaseDatabase.getInstance().getReference().child("UsersData");

        }


    }



}
