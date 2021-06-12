package com.example.mockinterview.adapter;

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
import com.example.mockinterview.model.Participant;

import java.util.List;

public class ParticipantAdapter extends RecyclerView.Adapter<ParticipantAdapter.VHolder> {
    @NonNull
    List<Participant> list;
//
//    ONNoteListener mOnNotelistener;
    RecyclerViewInterFace recyclerViewInterFace;
    public ParticipantAdapter(@NonNull List<Participant> data, RecyclerViewInterFace recyclerViewInterFace) {
        this.list = data;
        this.recyclerViewInterFace=recyclerViewInterFace;
    }
    @Override
    public VHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater= LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.unschedule_recycler,parent,false);
        return new VHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull VHolder holder, int position) {

        Participant part= list.get(position);

       holder.fname.setText(part.getFname());
       holder.lname.setText(part.getLname());
       holder.id.setText(part.getId());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VHolder extends RecyclerView.ViewHolder  {
        TextView fname,lname,id;
        public VHolder(@NonNull View itemView) {
            super(itemView);
            fname=(TextView) itemView.findViewById(R.id.fname);
            lname=(TextView) itemView.findViewById(R.id.lname);
            id=(TextView) itemView.findViewById(R.id.id);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                      recyclerViewInterFace.onItemClicked(getAdapterPosition());
                }
            });
         }
    }


}
