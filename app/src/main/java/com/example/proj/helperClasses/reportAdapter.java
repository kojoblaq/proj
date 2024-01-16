package com.example.proj.helperClasses;/*
package com.example.ixendpersonnel.helperClasses;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ixendpersonnel.Home;
import com.example.ixendpersonnel.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import static androidx.core.content.ContextCompat.startActivities;
import static androidx.core.content.ContextCompat.startActivity;

public class reportAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context mcontext;

    List<reportClass> fetchReport;

    public reportAdapter(Context mcontext) {
        this.mcontext = mcontext;
    }

    public reportAdapter(List<reportClass> fetchReport) {

        this.fetchReport = fetchReport;
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder {
        TextView fname, msg, phone, Latitude, Longitude, audio;
        ImageView image;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);

            fname = itemView.findViewById(R.id.fname);
            phone = itemView.findViewById(R.id.phone);
            msg = itemView.findViewById(R.id.message);
            Latitude = itemView.findViewById(R.id.lat);
            Longitude = itemView.findViewById(R.id.lon);
            image = itemView.findViewById(R.id.picture);
            audio = itemView.findViewById(R.id.audio);

        }
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.reportview, parent, true);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);

        return viewHolderClass;
    }



    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        reportClass reportClass = fetchReport.get(position);
        viewHolderClass.fname.setText(reportClass.getFname());
        viewHolderClass.phone.setText(reportClass.getPhone());
        viewHolderClass.msg.setText(reportClass.getMessage());
        viewHolderClass.audio.setText(reportClass.getAudio_Link());
        viewHolderClass.Latitude.setText(reportClass.getLatitude());
        viewHolderClass.Longitude.setText(reportClass.getLongitude());
        viewHolderClass.image.setImageResource(reportClass.getImage());





    }
    @Override
    public int getItemCount() {
        return fetchReport.size();
    }


}*/
