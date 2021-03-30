package com.sarker.backbenchersextended.dayfragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarker.backbenchersextended.R;
import com.sarker.backbenchersextended.adapter.RoutineAdapter;
import com.sarker.backbenchersextended.model.RoutineInfo;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Sunday extends Fragment {

    private RecyclerView rRecyclerView;
    private RoutineAdapter rAdapter;
    private DatabaseReference rDatabaseRef,userRef;
    private ArrayList<RoutineInfo> rList;
    private ProgressBar rProgressCircle;
    private ImageView noClass;
    private FirebaseAuth mAuth;
    private String current_user_id,classCode ;


    public Sunday() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sunday, container, false);

        noClass = view.findViewById(R.id.no_classes);
        rRecyclerView = view.findViewById(R.id.recycler_view2);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rRecyclerView.setLayoutManager(linearLayoutManager);
        rList = new ArrayList<>();
        rAdapter = new RoutineAdapter(getContext(),rList);
        rRecyclerView.setAdapter(rAdapter);


        rProgressCircle = view.findViewById(R.id.progress_circle2);


        mAuth = FirebaseAuth.getInstance();
        current_user_id = mAuth.getCurrentUser().getUid();
        rDatabaseRef = FirebaseDatabase.getInstance().getReference("Classroom");
        userRef = FirebaseDatabase.getInstance().getReference("UsersData").child(current_user_id);


        rDatabaseRef.keepSynced(true);
        userRef.keepSynced(true);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    classCode = snapshot.child("classCode").getValue(String.class);

                    rDatabaseRef.child(classCode).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(final DataSnapshot dataSnapshot) {


                            if(dataSnapshot.child("Routine").child("Sunday").exists()){

                                noClass.setVisibility(View.GONE);

                                rList.clear();
                                rAdapter.notifyDataSetChanged();

                                for (DataSnapshot postSnapshot : dataSnapshot.child("Routine").child("Sunday").getChildren()) {

                                    RoutineInfo info = postSnapshot.getValue(RoutineInfo.class);
                                    info.setRoutineKey(postSnapshot.getKey());
                                    rList.add(info);

                                }
                                rAdapter.notifyDataSetChanged();
                                rProgressCircle.setVisibility(View.INVISIBLE);

                            }
                            else {

                                rProgressCircle.setVisibility(View.INVISIBLE);
                                noClass.setVisibility(View.VISIBLE);

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            rProgressCircle.setVisibility(View.INVISIBLE);
                        }

                    });

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        return view;
    }


}
