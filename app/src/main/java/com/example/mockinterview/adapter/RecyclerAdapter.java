package com.example.mockinterview.adapter;

import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mockinterview.R;
import com.example.mockinterview.all_interface.RecyclerViewInterFace;
import com.example.mockinterview.model.Meeting;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.VHolder> {
    @NonNull
    List<Meeting> list;
//
//    ONNoteListener mOnNotelistener;
    RecyclerViewInterFace recyclerViewInterFace;
    public RecyclerAdapter(@NonNull List<Meeting> data, RecyclerViewInterFace recyclerViewInterFace) {
        this.list = data;
        this.recyclerViewInterFace=recyclerViewInterFace;

    }

    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.recyclecontent,parent,false);
        return new VHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {

        Meeting meet= list.get(position);

        holder.subject.setText("Agenda: "+meet.getSubject());

        holder.start.setText("Start: "+meet.getStart_time());

       holder.end.setText("End: "+meet.getEnd_time());
        holder.meet_id.setText("Meeting ID: "+meet.getMeet_id());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VHolder extends RecyclerView.ViewHolder  {
        ImageView icon;
        TextView subject,start,end,meet_id;
        public VHolder(@NonNull View itemView) {
            super(itemView);
            subject=(TextView) itemView.findViewById(R.id.subject);
            start=(TextView) itemView.findViewById(R.id.start);
            end=(TextView) itemView.findViewById(R.id.end);
            meet_id=(TextView) itemView.findViewById(R.id.meet_id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      recyclerViewInterFace.onItemClicked(getAdapterPosition(),itemView);
                }
            });
         }
    }


}
