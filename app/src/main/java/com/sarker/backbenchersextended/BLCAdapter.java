package com.sarker.backbenchersextended;

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
import com.sarker.backbenchersextended.adapter.CourseAdapter;
import com.sarker.backbenchersextended.model.BLCInfo;
import com.sarker.backbenchersextended.model.CourseInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class BLCAdapter extends RecyclerView.Adapter<BLCAdapter.NewsViewHolder> {

    private Context pContext;
    private ArrayList<BLCInfo> bList;
    private ProgressDialog progressDialog;
    private String current_user_id = " ";



    public BLCAdapter(Context context, ArrayList<BLCInfo> Lists) {
        pContext = context;
        bList = Lists;
    }

    @NonNull
    @Override
    public BLCAdapter.NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(pContext).inflate(R.layout.blc_item, parent, false);
        return new BLCAdapter.NewsViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final BLCAdapter.NewsViewHolder holder, final int position) {

        final BLCInfo info = bList.get(position);

        final String key = info.getKey();


        holder.title.setText(info.getTitle());
        holder.des.setText(info.getDes());

        if(info.getFrom().equals("admin")){
            holder.delete.setVisibility(View.VISIBLE);
        }else {
            holder.delete.setVisibility(View.GONE);
        }


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
                        holder.CourseRef.child(info.getClassCode()).child("BLC").child(key).removeValue();
                        notifyItemChanged(position);
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
        return bList.size();
    }



    public class NewsViewHolder extends RecyclerView.ViewHolder {


        public TextView title,des;
        public ImageView delete;
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

            title = itemView.findViewById(R.id.title);
            delete = itemView.findViewById(R.id.blc_delete);
            des =itemView.findViewById(R.id.des);

            CourseRef = FirebaseDatabase.getInstance().getReference().child("Classroom");

        }


    }



}
