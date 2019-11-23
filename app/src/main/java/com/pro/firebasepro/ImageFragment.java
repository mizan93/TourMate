package com.pro.firebasepro;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImageFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;
    private DatabaseReference expenseRef;
    private DatabaseReference uploadRef;
    private ImageViewAdapter imageViewAdapter;
    private String eventId;
    private List<Upload> uploads = new ArrayList<>();
    private Context context;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private GridLayoutManager gridLayoutManager;
    private ProgressBar mProgressCircle;
    private ImageView emptyimage;



    public ImageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        rootRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        userRef = rootRef.child(user.getUid());
        eventRef = userRef.child("Event");
        expenseRef = userRef.child("Expense");
        uploadRef = userRef.child("Upload");
        eventId = getArguments().getString("eventId");
        uploadRef = uploadRef.child(eventId);
        uploadRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                uploads.clear();

                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Upload upload = d.getValue(Upload.class);
                    uploads.add(upload);
                }
                imageViewAdapter.updateList(uploads);
                mProgressCircle.setVisibility(View.INVISIBLE);
  /*              if(uploads.size() == 0){
                    emptyimage.setVisibility(View.VISIBLE);
                }*/


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(context,"Failed!",Toast.LENGTH_LONG).show();

            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image, container, false);
        eventId = getArguments().getString("eventId");
        mProgressCircle = view.findViewById(R.id.progress_circle);
        recyclerView = view.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gridLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(gridLayoutManager);
        imageViewAdapter = new ImageViewAdapter(context, uploads);
/*
        emptyimage = view.findViewById(R.id.emptyimage);
        if(uploads.size() == 0){
            emptyimage.setVisibility(View.VISIBLE);
        }else{
            emptyimage.setVisibility(View.GONE);

        }*/
        recyclerView.setAdapter(imageViewAdapter);
        return view;
    }

}
