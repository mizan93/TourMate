package com.pro.firebasepro;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment ;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{
    private Context context;
    private List<Event> eventList;
    private ItemActionListener listener;
    private SingleItemActionListener singleItemActionListener;
    private FragmentManager manager;
    private Calendar calendar;
    private String day, month, year;
    private String currentDate;
    private long diff, diffover, dayremain=0, dayover=0;

    public EventAdapter(Context context, List<Event> eventList, Fragment fragment) {
        this.context = context;
        this.eventList = eventList;
        listener = (ItemActionListener) fragment;
        singleItemActionListener = (SingleItemActionListener) context;

    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(
                R.layout.event_row, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, final int position) {
        SimpleDateFormat myFormat = new SimpleDateFormat("dd/MM/yyyy");
        calendar = Calendar.getInstance();
        int iyear = calendar.get(Calendar.YEAR);
        int imonth = calendar.get(Calendar.MONTH)+1;
        int idayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        year = String.valueOf(iyear);
        month = String.valueOf(imonth);
        day = String.valueOf(idayOfMonth);
        currentDate = day+"/"+month+"/"+year;
        //String startDate = "27/12/2018";
        //System.out.println("CurrentDate: " + currentDate);
        try {
            Date date1 = myFormat.parse(currentDate);
            Date date2 = myFormat.parse(eventList.get(position).getStartDate());
            Date dateover = myFormat.parse(eventList.get(position).getEndDate());
            diff = date2.getTime() - date1.getTime();
            diffover = dateover.getTime() - date1.getTime();
            dayremain = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
            dayover = TimeUnit.DAYS.convert(diffover, TimeUnit.MILLISECONDS);
            //System.out.println("Remaining Day " + TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(dayremain<0){
            holder.eventNameTV.setText("Event Name: "+eventList.get(position).getEventName()+
                    "\nRemaining Day: Event Started");
            if(dayover<0){
                holder.eventNameTV.setText("Event Name: "+eventList.get(position).getEventName()+
                        "\nRemaining Day: Event Over");
            }
        }else{
            holder.eventNameTV.setText("Event Name: "+eventList.get(position).getEventName()+
                    "\nRemaining Day: " + dayremain);
        }


        holder.budgetTV.setText("Start Date: "+eventList.get(position).getStartDate()+
                "\nEnd Date: "+eventList.get(position).getEndDate()+"\nBudget: "+String.valueOf(eventList.get(position).getBudget())+" Tk");
        holder.menuTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, v);
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(R.menu.event_menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        final String id = eventList.get(position).getEventId();
                        String budgetpp = Double.toString(eventList.get(position).getBudget());
                        switch (item.getItemId()){
                            case R.id.menu_edit:
                                listener.onItemEdit(id);
                                break;
                            case R.id.menu_delete:
                                listener.onItemDelete(id);
                                break;
                            case R.id.menu_add_budget:
                                listener.onItemAddBudget(id, budgetpp);
                                break;
                        }
                        return false;
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rowId = eventList.get(position).getEventId();
                String eventName = eventList.get(position).getEventName();
                String startDate = eventList.get(position).getStartDate();
                String endDate = eventList.get(position).getEndDate();
                String budget = Double.toString(eventList.get(position).getBudget());
                singleItemActionListener.onSingleIteamListener(rowId, eventName, startDate, endDate, budget);

            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        TextView eventNameTV, budgetTV, menuTV;
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);

            eventNameTV = itemView.findViewById(R.id.row_event_name);
            budgetTV = itemView.findViewById(R.id.row_event_budget);
            menuTV = itemView.findViewById(R.id.row_event_menu);
        }
    }

    public void updateList(List<Event> events){
        this.eventList = events;
        notifyDataSetChanged();
    }

    interface ItemActionListener {
        void onItemDelete(String rowId);
        void onItemEdit(String rowId);
        void onItemAddBudget(String rowId, String budget);
    }

    interface SingleItemActionListener {
        void onSingleIteamListener(String rowId, String eventName,
                                   String startDate, String endDate, String budget);
    }
}