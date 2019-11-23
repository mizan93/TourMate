package com.pro.firebasepro;



import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import android.content.DialogInterface;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements EventAdapter.ItemActionListener {
    private FloatingActionButton addBtn;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;
    private DatabaseReference expenseRef;
    private DatabaseReference uploadRef;
    private Context context;
    private List<Event>events = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private EventAdapter eventAdapter;
    private DrawerLayout mDrawerLayout;
    private Toolbar toolbar;
    private ProgressBar progressBar;
    private Calendar calendar;
    private int year, month, dayofMonth;
    private String stratdate, enddate;
    private TextView esdate, esedate;
    private ImageView emptyimage;
    public HomeFragment() {
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
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                events.clear();
                for(DataSnapshot d : dataSnapshot.getChildren()){
                    Event event = d.getValue(Event.class);
                    events.add(event);
                }
                eventAdapter.updateList(events);
                if(events.size() == 0){
                    emptyimage.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                //Toast.makeText(context,"Error!",Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        addBtn = view.findViewById(R.id.addbtn);
        recyclerView = view.findViewById(R.id.eventList);
        layoutManager = new LinearLayoutManager(context);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        eventAdapter = new EventAdapter(context, events, this);
        recyclerView.setAdapter(eventAdapter);
        emptyimage = view.findViewById(R.id.emptyimage);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String keyId = userRef.push().getKey();
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                View view = getLayoutInflater().inflate(R.layout.edit_event_dialog, null);
                alertDialogBuilder.setTitle("Add New Event");
                alertDialogBuilder.setView(view);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                final EditText ename = view.findViewById(R.id.edteventname);
                final EditText ebudget = view.findViewById(R.id.edteventbudget);

                esdate = view.findViewById(R.id.edteventsdate);
                esedate = view.findViewById(R.id.edteventfdate);


                esdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog dialog
                                = new DatePickerDialog(context,listener,year,month,dayofMonth);
                        dialog.show();
                    }
                });

                esedate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        calendar = Calendar.getInstance();
                        year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                        DatePickerDialog dialog
                                = new DatePickerDialog(context,listener2,year,month,dayofMonth);
                        dialog.show();
                    }
                });

                Button cancel = view.findViewById(R.id.buttoncancel);
                Button save = view.findViewById(R.id.buttonsave);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ename.getText().toString().isEmpty()){
                            ename.setError("Enter Event Name!");
                        }
                        else if(esdate.getText().toString().isEmpty()){
                            esdate.setError("Select Start Date!");
                            //Toast.makeText(getApplicationContext(),"Empty Field!", Toast.LENGTH_LONG).show();
                        }
                        else if(esedate.getText().toString().isEmpty()){
                            esedate.setError("Select End Date!");
                            //Toast.makeText(getApplicationContext(),"Empty Field!", Toast.LENGTH_LONG).show();
                        }
                        else if(ebudget.getText().toString().isEmpty() && stratdate.isEmpty()){
                            ebudget.setError("Enter Event Budget!");
                            //Toast.makeText(getApplicationContext(),"Empty Field!", Toast.LENGTH_LONG).show();
                        }
                        else {
                            String eventname = ename.getText().toString();
                            String eventsdate = esdate.getText().toString();
                            String eventedate = esedate.getText().toString();
                            String eventbudget = ebudget.getText().toString();
                            Event event = new Event(keyId,eventname, stratdate, enddate, Double.parseDouble(eventbudget));
                            //userRef.child(keyId).setValue(event);
                            eventRef.child(keyId).setValue(event);
                            emptyimage.setVisibility(View.GONE);
                            alertDialog.dismiss();
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



    @Override
    public void onItemDelete(final String rowId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Confirmation!");
        builder.setMessage("Do you want to delete this?");
        LayoutInflater inflater = LayoutInflater.from(context);

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //userRef.child(rowId).removeValue();
                eventRef.child(rowId).removeValue();
                expenseRef.child(rowId).removeValue();
                uploadRef.child(rowId).removeValue();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onItemEdit(final String rowId) {
        //final int position=0;
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.edit_event_dialog, null);
        alertDialogBuilder.setTitle("Edit Event");
        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        final EditText ename = view.findViewById(R.id.edteventname);
        final EditText ebudget = view.findViewById(R.id.edteventbudget);
        esdate = view.findViewById(R.id.edteventsdate);
        esedate = view.findViewById(R.id.edteventfdate);

        esdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog
                        = new DatePickerDialog(context,listener,year,month,dayofMonth);
                dialog.show();
            }
        });
        esedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                dayofMonth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog
                        = new DatePickerDialog(context,listener2,year,month,dayofMonth);
                dialog.show();
            }
        });

        eventRef.child(rowId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Event event = dataSnapshot.getValue(Event.class);
                ename.setText(event.getEventName());
                esdate.setText(event.getStartDate());
                esedate.setText(event.getEndDate());
                ebudget.setText(Double.toString(event.getBudget()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Button cancel = view.findViewById(R.id.buttoncancel);
        Button save = view.findViewById(R.id.buttonsave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ename.getText().toString().isEmpty()){
                    ename.setError("Enter Event Name!");
                }
                else if(esdate.getText().toString().isEmpty() && stratdate.isEmpty()){
                    esdate.setError("Select Start Date!");
                    //Toast.makeText(getApplicationContext(),"Empty Field!", Toast.LENGTH_LONG).show();
                }
                else if(esedate.getText().toString().isEmpty() && enddate.isEmpty()){
                    esedate.setError("Select End Date!");
                    //Toast.makeText(getApplicationContext(),"Empty Field!", Toast.LENGTH_LONG).show();
                }
                else if(ebudget.getText().toString().isEmpty()){
                    ebudget.setError("Enter Event Budget!");
                    //Toast.makeText(getApplicationContext(),"Empty Field!", Toast.LENGTH_LONG).show();
                }
                else {
                    String eventname = ename.getText().toString();
                    String eventsdate = esdate.getText().toString();
                    String eventedate = esedate.getText().toString();
                    String eventbudget = ebudget.getText().toString();
                   // Event newEvent = new Event(rowId,eventname, stratdate, enddate, Double.parseDouble(eventbudget));
                    Event newEvent = new Event(rowId,eventname, eventsdate, eventedate, Double.parseDouble(eventbudget));
                    eventRef.child(rowId).setValue(newEvent);
                    alertDialog.dismiss();
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

    @Override
    public void onItemAddBudget(final String rowId, final String pbudget) {
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        View view = getLayoutInflater().inflate(R.layout.more_budget_add_dialog, null);
        alertDialogBuilder.setTitle("Add More Budget");
        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        final EditText ebudget = view.findViewById(R.id.edteventbudget);
        Button cancel = view.findViewById(R.id.buttoncancel);
        Button save = view.findViewById(R.id.buttonsave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ebudget.getText().toString().isEmpty()){
                    ebudget.setError("Empty Budget!");
                    //Toast.makeText(getApplicationContext(),"Empty Field!", Toast.LENGTH_LONG).show();
                }
                else {
                    //final String keyId = userRef.push().getKey();
                    Double previousbudget = Double.parseDouble(pbudget);
                    Double newbudget = Double.parseDouble(ebudget.getText().toString());
                    Double budget = previousbudget + newbudget;
                    //Event newEventbud = new Event(rowId, Double.parseDouble(String.valueOf(budget)));
                    //userRef.child("Event").child(rowId).child("budget").setValue(budget);
                    eventRef.child(rowId).child("budget").setValue(budget);
                    //userRef.child(keyId).child(rowId).setValue(newEventbud);
                    alertDialog.dismiss();

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

    private DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            final Calendar c = Calendar.getInstance();
            c.set(year,month,dayOfMonth);
            stratdate = sdf.format(c.getTime());
            esdate.setText(stratdate);
        }
    };
    private DatePickerDialog.OnDateSetListener listener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            final Calendar c = Calendar.getInstance();
            c.set(year,month,dayOfMonth);
            enddate = sdf.format(c.getTime());
            esedate.setText(enddate);
        }
    };
    @Override
    public void onResume() {
        super.onResume();
    }
}
