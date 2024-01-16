package com.example.proj.helperClasses;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proj.R;
import com.example.proj.ReportCardView;

import java.util.ArrayList;

public class MyReportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final Context mcontext;
    String phone;
    String name;
    String audio;
    String image;
    String txt;
    String lat;
    String lon;
    String date;
    String emergencyType;


    ArrayList<reportClass> fetchdata;

    /*public MyReportAdapter(  ArrayList<reportClass> fetchdata) {


    }*/

    public MyReportAdapter(Context mcontext, ArrayList<reportClass> fetchdata) {
        this.mcontext = mcontext;
        this.fetchdata = fetchdata;

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reportview, parent, false);


        return new ViewHolderClass(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {


        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        reportClass reportClass = fetchdata.get(position);


        viewHolderClass.emergencyTpe.setText(reportClass.getEmergencyType());
        viewHolderClass.phone.setText(reportClass.getPhone());
        viewHolderClass.date.setText(reportClass.getDate());
        viewHolderClass.name.setText(reportClass.getFname());


    }

    @Override
    public int getItemCount() {

        return fetchdata.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        TextView phone, emergencyTpe, date, name;


        public ViewHolderClass(@NonNull View itemView) {

            super(itemView);


            emergencyTpe = itemView.findViewById(R.id.emergencyTpe);
            date = itemView.findViewById(R.id.date);
            phone = itemView.findViewById(R.id.phone);
            name = itemView.findViewById(R.id.name);

            itemView.setOnClickListener(view -> {

                Intent i = new Intent(mcontext, ReportCardView.class);
                int position = getAdapterPosition();

                i.putExtra("audio", fetchdata.get(position).getAudio_report());
                i.putExtra("phone", fetchdata.get(position).getPhone());
                i.putExtra("image", fetchdata.get(position).getImage_Report());
                i.putExtra("txt", fetchdata.get(position).getText_report());
                i.putExtra("lat", fetchdata.get(position).getLatitude());
                i.putExtra("lon", fetchdata.get(position).getLongitude());
                i.putExtra("name", fetchdata.get(position).getFname());
                i.putExtra("date", fetchdata.get(position).getDate());
                i.putExtra("emergency", fetchdata.get(position).getEmergencyType());
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(i);

            });


        }
    }


}
