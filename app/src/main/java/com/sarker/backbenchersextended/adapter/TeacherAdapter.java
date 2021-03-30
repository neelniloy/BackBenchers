package com.sarker.backbenchersextended.adapter;

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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sarker.backbenchersextended.R;
import com.sarker.backbenchersextended.model.TeacherInfo;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class TeacherAdapter extends RecyclerView.Adapter<TeacherAdapter.ViewHolder> {
    private Context nContext;
    private ArrayList<TeacherInfo> tinfo;
    private ProgressDialog progressDialog;
    private String current_user_id = " ";



    public TeacherAdapter(Context context, ArrayList<TeacherInfo> tLists) {
        nContext = context;
        tinfo = tLists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.show_teacher,parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final TeacherInfo info = tinfo.get(position);

        final String key = info.getKey();

        holder.name.setText(info.getName());
        holder.initial.setText("Initial : "+info.getInitial());
        holder.email.setText("Email : "+info.getEmail());
        holder.phone.setText("Phone : "+info.getPhone());
        holder.room.setText("Room : "+info.getRoom());

        if (!info.getDp().equals(" ")){
            Picasso.get().load(info.getDp()).placeholder(R.drawable.profile_icon).fit().centerCrop().into(holder.dp);
        }

        if(info.getUid().equals(current_user_id)){
            holder.delete.setVisibility(View.VISIBLE);
        }else {
            holder.delete.setVisibility(View.GONE);
        }

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
                        holder.tRef.child(info.getClassCode()).child("Teacher").child(key).removeValue();
                        notifyItemChanged(position);
                        notifyDataSetChanged();

                        Toast.makeText(nContext, "Successfully Deleted", Toast.LENGTH_SHORT).show();

                    }
                },1500);

            }
        });


    }

    @Override
    public int getItemCount() {
        return tinfo.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView dp;
        private TextView name,initial,email,phone,room;
        public ImageView delete;
        public DatabaseReference tRef;
        public FirebaseAuth mfirebaseAuth;


        ViewHolder(View itemView) {
            super(itemView);

            mfirebaseAuth = FirebaseAuth.getInstance();

            final FirebaseUser mFirebaseuser = mfirebaseAuth.getCurrentUser();

            if (mFirebaseuser != null) {

                current_user_id = mfirebaseAuth.getCurrentUser().getUid();

            } else {

            }


            name = (TextView) itemView.findViewById(R.id.t_name);
            initial = (TextView) itemView.findViewById(R.id.t_initial);
            email = (TextView) itemView.findViewById(R.id.t_email);
            phone = (TextView) itemView.findViewById(R.id.t_phone);
            room = (TextView) itemView.findViewById(R.id.t_room);
            dp = itemView.findViewById(R.id.t_dp);
            delete = itemView.findViewById(R.id.teacher_delete);

            tRef = FirebaseDatabase.getInstance().getReference().child("Classroom");


        }
    }


}
