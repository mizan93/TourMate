package com.pro.firebasepro.nearbyplace;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pro.firebasepro.R;

import java.util.ArrayList;
import java.util.List;
public class NearByPlaceAdapter extends RecyclerView.Adapter<NearByPlaceAdapter.NearByItemVH>{
    private List<Result> results = new ArrayList<>();
    private Context context;
    private OnSingleItemMapListener onSingleItemMapListener;

    public NearByPlaceAdapter(Context context,List<Result> results) {
        this.results = results;
        this.context = context;
        onSingleItemMapListener = (OnSingleItemMapListener) context;
    }

    @NonNull
    @Override
    public NearByItemVH onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_nearby_place_layout,viewGroup,false);
        return new NearByItemVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NearByItemVH nearByItemVH, final int i) {
        nearByItemVH.nameTV.setText(results.get(i).getName());
        nearByItemVH.descTV.setText(results.get(i).getVicinity());
        nearByItemVH.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSingleItemMapListener.googleMapWithItemDetails(results.get(i));
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class NearByItemVH extends RecyclerView.ViewHolder{
        TextView nameTV,descTV;
        public NearByItemVH(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.name_tv);
            descTV = itemView.findViewById(R.id.desc_tv);
        }
    }

    public interface OnSingleItemMapListener {
        void googleMapWithItemDetails(Result result);
    }

}

















/*
public class NearByPlaceAdapter extends RecyclerView.Adapter<NearByPlaceAdapter.NearByPlaceItem>{
    private List<Result> results = new ArrayList<>();
    private Context context;
    //private GoToMapListener goToMapListener;

    public NearByPlaceAdapter(Context context,List<Result> results) {
        this.results = results;
        this.context = context;
        //goToMapListener = (GoToMapListener) context;
    }

    @NonNull
    @Override
    public NearByPlaceAdapter.NearByPlaceItem onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_nearby_place_layout,viewGroup,false);
        return new NearByPlaceAdapter.NearByPlaceItem(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NearByPlaceItem nearByPlaceItem, final int i) {
        nearByPlaceItem.nameTV.setText(results.get(i).getName());
        nearByPlaceItem.descTV.setText(results.get(i).getVicinity());
        */
/*nearByPlaceItem.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //goToMapListener.goToMapWithItemDesc(results.get(i));
            }
        });*//*

    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    class NearByPlaceItem extends RecyclerView.ViewHolder{
        TextView nameTV,descTV;
        public NearByPlaceItem(@NonNull View itemView) {
            super(itemView);
            nameTV = itemView.findViewById(R.id.name_tv);
            descTV = itemView.findViewById(R.id.desc_tv);
        }
    }
}*/
