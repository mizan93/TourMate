package com.pro.firebasepro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>{
    private Context context;
    private List<Expense> expenseList;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference expenseRef;

    public ExpenseAdapter(Context context, List<Expense> expenseList) {
        this.context = context;
        this.expenseList = expenseList;

    }

    @NonNull
    @Override
    public ExpenseAdapter.ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(
                R.layout.expense_row, parent, false);
        return new ExpenseAdapter.ExpenseViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseAdapter.ExpenseViewHolder holder, final int position) {
        holder.title.setText(expenseList.get(position).getExpenseTitle());
        holder.amount.setText(String.valueOf(expenseList.get(position).getExpenseAmount())+" Tk");
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
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


                        //Toast.makeText(context, "Long Clicked Yes", Toast.LENGTH_SHORT).show();
                        rootRef = FirebaseDatabase.getInstance().getReference();
                        auth = FirebaseAuth.getInstance();
                        user = auth.getCurrentUser();
                        userRef = rootRef.child(user.getUid());
                        expenseRef = userRef.child("Expense");
                        String eventId = expenseList.get(position).getEventId();
                        String expenseId = expenseList.get(position).getExpenseId();
                        expenseRef.child(eventId).child(expenseId).removeValue();

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView title, amount;
        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.tvexptitle);
            amount = itemView.findViewById(R.id.tvexpamount);
        }
    }

    public void updateList(List<Expense> expenses){
        this.expenseList = expenses;
        notifyDataSetChanged();
    }
}
