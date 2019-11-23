package com.pro.firebasepro;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import android.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class SingleItemListViewFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;
    private DatabaseReference expenseRef;
    private Context context;
    private List<Expense> expenses = new ArrayList<>();

    private UserAuthSingleIteam listenerf;

    private String rowId;
    private TextView ename, esdate, ebudget, eedate;

    public SingleItemListViewFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        listenerf = (UserAuthSingleIteam) context;
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
        rowId = getArguments().getString("rowId");
        eventRef.child(rowId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                ename.setText(event.getEventName());
                esdate.setText("Start Date: "+event.getStartDate());
                eedate.setText("End Date: "+event.getEndDate());
                ebudget.setText("Budget: "+Double.toString(event.getBudget()));
                //EventAdapter.updateList(event);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_single_item_list_view, container, false);

        ename = view.findViewById(R.id.edteventnames);
        esdate = view.findViewById(R.id.edteventsdates);
        ebudget = view.findViewById(R.id.edteventbudgets);
        eedate = view.findViewById(R.id.edteventedndate);

        rowId = getArguments().getString("rowId");
        final String pbudget = getArguments().getString("budget");

        final Button viewallexpense = view.findViewById(R.id.btnviewallexpense);
        final Button capturephoto = view.findViewById(R.id.btncapturephoto);
        final Button viewallimage = view.findViewById(R.id.btnviewallimage);

        capturephoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerf.onAuthCapImage(rowId);
            }
        });

        viewallimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenerf.onAuthViewCapImage(rowId);
            }
        });

        viewallexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listenerf.onAuthViewExpense(rowId, pbudget);

            }
        });


        return view;
    }

    interface UserAuthSingleIteam{
        void onAuthCapImage(String eventId);
        void onAuthViewCapImage(String eventId);
        void onAuthViewExpense(String eventId, String budget);

    }

}

