package com.pro.firebasepro;


import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class ExpenseDetailsFragment extends Fragment {
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;
    private DatabaseReference expenseRef;
    private Query userRefq;
    private Context context;
    private List<Expense> expenses = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private ExpenseAdapter expenseAdapter;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private ProgressBar budgetProgressBar;
    private double remainingbudget=0.0, sum=0.0;
    private int i;
    private String eventId;
    private String pbudget;
    private FloatingActionButton addnewexpense;
    private TextView budgetnremaining, budgetusedstatus;
    private ImageView emptyimage;
    public ExpenseDetailsFragment() {
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
        eventId = getArguments().getString("eventId");
        pbudget = getArguments().getString("budget");
        expenseRef = expenseRef.child(eventId);
        expenseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                expenses.clear();
                sum = 0.0;
                remainingbudget =0.0;
                i=0;
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Expense expense = d.getValue(Expense.class);
                    expenses.add(expense);
                }
                expenseAdapter.updateList(expenses);
                if(expenses.size() == 0){
                    emptyimage.setVisibility(View.VISIBLE);
                }
                for (i = 0; i < expenses.size(); i++) {
                    sum += expenses.get(i).getExpenseAmount();
                }

                remainingbudget = Double.parseDouble(pbudget)-sum;
                budgetnremaining.setText("Budget: "+remainingbudget+"/"+pbudget);

                if (Double.parseDouble(pbudget)!=0){
                    double part = sum/Double.parseDouble(pbudget);
                    int percentage = (int) (part*100);
                    budgetusedstatus.setText(""+percentage+"%");
                    budgetProgressBar.setProgress(percentage);
                }
                else {
                    Toast.makeText(context,"No Budget!", Toast.LENGTH_LONG).show();
                    budgetProgressBar.setProgress(0);
                }
               // Toast.makeText(context,"Total Expense: "+sum +"Remaining: "+remainingbudget, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //Toast.makeText(context,"Failed!", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_expense_details, container, false);
        eventId = getArguments().getString("eventId");
        pbudget = getArguments().getString("budget");
        recyclerView = view.findViewById(R.id.expenseList);
        emptyimage = view.findViewById(R.id.emptyimage);


        addnewexpense = view.findViewById(R.id.expaddf);
        budgetnremaining = view.findViewById(R.id.budgetnremaining);
        budgetusedstatus = view.findViewById(R.id.budget_used_status);
        budgetProgressBar = view.findViewById(R.id.budget_progress_bar);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        expenseAdapter = new ExpenseAdapter(context, expenses);
        recyclerView.setAdapter(expenseAdapter);


        addnewexpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String expenseId = userRef.push().getKey();
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                View view = getLayoutInflater().inflate(R.layout.add_more_expense, null);
                alertDialogBuilder.setTitle("Add More Expense");
                alertDialogBuilder.setView(view);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                final EditText title = view.findViewById(R.id.edtexpensetitle);
                final EditText amount = view.findViewById(R.id.edtexpenseamount);
                Button cancel = view.findViewById(R.id.buttoncancel);
                Button save = view.findViewById(R.id.buttonsave);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(title.getText().toString().isEmpty()){
                            title.setError("Empty Title!");
                            //Toast.makeText(getApplicationContext(),"Empty Field!", Toast.LENGTH_LONG).show();
                        }
                        else if(amount.getText().toString().isEmpty()){
                            amount.setError("Empty Expense!");
                            //Toast.makeText(getApplicationContext(),"Empty Field!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            //final String keyId = userRef.push().getKey();
                            //userRef.child(expenseId).setValue(event);
                            String expensetitle = title.getText().toString();
                            Double expenseamount = Double.parseDouble(amount.getText().toString());
                            Expense expenseadd = new Expense(expenseId, eventId, expensetitle, Double.parseDouble(String.valueOf(expenseamount)));
                            expenseRef.child(expenseId).setValue(expenseadd);
                            alertDialog.dismiss();
                            emptyimage.setVisibility(View.GONE);


                        }


                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

            }
        });

        return view;
    }

}
