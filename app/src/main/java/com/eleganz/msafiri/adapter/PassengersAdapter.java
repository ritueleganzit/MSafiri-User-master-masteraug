package com.eleganz.msafiri.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eleganz.msafiri.PassengerData;
import com.eleganz.msafiri.R;

import java.util.ArrayList;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.MyViewHolder>
{
    Context context;
    ArrayList<PassengerData> arrayList;

    public PassengersAdapter(Context context, ArrayList<PassengerData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.passenger_roww,viewGroup,false);
        MyViewHolder myViewHolder=new MyViewHolder(view);

        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        PassengerData passengerData=arrayList.get(i);
        myViewHolder.psnamestatus.setText(passengerData.getStatus());
        myViewHolder.psname.setText(passengerData.getUsername());


    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        TextView psname,psnamestatus;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            psname=itemView.findViewById(R.id.psname);
            psnamestatus=itemView.findViewById(R.id.psnamestatus);

        }
    }


}
