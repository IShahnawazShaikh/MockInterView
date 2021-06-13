package com.example.mockinterview.ui.createmeet;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mockinterview.R;
import com.example.mockinterview.Utilities.ApiUrl;
import com.example.mockinterview.adapter.ParticipantAdapter;
import com.example.mockinterview.adapter.RecyclerAdapter;
import com.example.mockinterview.all_interface.RecyclerViewInterFace;
import com.example.mockinterview.model.Meeting;
import com.example.mockinterview.model.Participant;
import com.example.mockinterview.ui.allmeet.AllMeetingFragment;
import com.example.mockinterview.view_models.FetchData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CreateMeetingFragment extends Fragment implements RecyclerViewInterFace,DatePickerDialog.OnDateSetListener , TimePickerDialog.OnTimeSetListener{

    private CreateMeetingViewModel dashboardViewModel;
    TextView start,end;
    Button startPick,endPick,search,create;
    int hour, minute;
    String myHour;
    String myMinute;
    boolean set=false;
    RecyclerView recyclerView2;
    LinearLayoutManager manager;
    private OkHttpClient okHttpClient;
    ParticipantAdapter ParticipantsAdapter;
    SwipeRefreshLayout refresh;
    List<Participant> ParticipantList;
    Set<String> part=null;
    String startT,endT;
    EditText agenda;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_meeting, container, false);

        part=new HashSet<>();

        start=view.findViewById(R.id.start);
        end=view.findViewById(R.id.end);
        agenda=view.findViewById(R.id.agenda);

        startPick = view.findViewById(R.id.startPick);
        endPick = view.findViewById(R.id.endPick);
        search=view.findViewById(R.id.search);
        create=view.findViewById(R.id.create);

        recyclerView2=(RecyclerView) view.findViewById(R.id.unschedule_recycler);

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);

        manager=new LinearLayoutManager(getContext());
        startPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                set=false;
                hour = c.get(Calendar.HOUR);
                minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), CreateMeetingFragment.this, hour, minute, DateFormat.is24HourFormat(getActivity()));
                timePickerDialog.show();
            }
        });
        endPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set=true;
                Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR);
                minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), CreateMeetingFragment.this, hour, minute, DateFormat.is24HourFormat(getActivity()));
                timePickerDialog.show();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FetchData model = ViewModelProviders.of(getActivity()).get(FetchData.class);
                startT=start.getText().toString();
                endT=end.getText().toString();
                startT = startT.replaceAll("\\s", "");
                endT = endT.replaceAll("\\s", "");

                if(startT.equals("") || endT.equals("")){
                   Toast.makeText(getActivity(),"Select Time First",Toast.LENGTH_SHORT).show();
                }
                else if(endT.compareTo(startT)<=0){
                  Toast.makeText(getActivity(),"End time can't be less than or equal to start time",Toast.LENGTH_SHORT).show();
                }

               else{
                    model.unschedule(startT,endT,getActivity()).observe(getActivity(), new Observer<List<Participant>>() {
                        @Override
                        public void onChanged(List<Participant> participants) {
                            //ParticipantList.clear();
                            ParticipantList= new ArrayList<Participant>(participants);
                            recyclerView2.setLayoutManager(manager);
                            ParticipantsAdapter =new ParticipantAdapter(ParticipantList,CreateMeetingFragment.this);
//
//                        participants.forEach((o1)-> {
//                            System.out.println("list:----------------  "+o1.getFname());
//                        });
                            recyclerView2.setAdapter(ParticipantsAdapter);
                            ParticipantsAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }
        });
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(start.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Select start time",Toast.LENGTH_SHORT).show();
                }
                else if(end.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Select end time",Toast.LENGTH_SHORT).show();
                }
                else if(part.size()<2){
                  Toast.makeText(getActivity(),"Select atleast 2 participant",Toast.LENGTH_SHORT).show();
                }
               else if(agenda.getText().toString().equals("")){
                    Toast.makeText(getActivity(),"Insert Ageneda",Toast.LENGTH_SHORT).show();
               }
               else{
                   StringBuilder sb=new StringBuilder("");
                   String id="";
                   int i=0;
                   for(String s: part){
                     if(i!=0){
                         sb.append(",");
                     }
                     sb.append(s);
                     i+=1;
                   }

                   //System.out.println("................."+sb.toString());
                   createMeeting(sb.toString());


               }
            }
        });
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            FetchData model = ViewModelProviders.of(getActivity()).get(FetchData.class);
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onRefresh() {
                model.unschedule(startT,endT,getActivity()).observe(getActivity(), new Observer<List<Participant>>() {
                    @Override
                    public void onChanged(List<Participant> participants) {
                        ParticipantList= new ArrayList<Participant>(participants);
                        recyclerView2.setLayoutManager(manager);
                        ParticipantsAdapter =new ParticipantAdapter(ParticipantList,CreateMeetingFragment.this);
                        recyclerView2.setAdapter(ParticipantsAdapter);
                        ParticipantsAdapter.notifyDataSetChanged();
                        refresh.setRefreshing(false);
                    }
                });
            }
        });
        return view;
    }
    private void createMeeting(String users) {
        new Thread(() -> {
                try {
                    String response = insertData(ApiUrl.ADD_MEETING,users);

                    JSONObject jsonObject = new JSONObject(response);

                    final boolean status = jsonObject.getBoolean("status");

                    if (status) {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "Created Successfully", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        getActivity().runOnUiThread(() -> {
                            Toast.makeText(getContext(), "" + "Something went wrong !!!", Toast.LENGTH_SHORT).show();
                        });
                    }
                } catch (Exception e) {

                    getActivity().runOnUiThread(() -> {
                        e.printStackTrace();
                    });
                }
            }).start();
    }
    private String insertData(String url,String users) throws Exception {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("start_time", startT)
                .addFormDataPart("end_time",endT)
                .addFormDataPart("agenda",agenda.getText().toString())
                .addFormDataPart("users",users)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build();
        try (Response response = okHttpClient.newCall(request).execute()) {
            return response.body().string();
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        myYear = year;
//        myday = dayOfMonth;
//        myMonth = month;
//        Calendar c = Calendar.getInstance();
//        hour = c.get(Calendar.HOUR);
//        minute = c.get(Calendar.MINUTE);
//        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), CreateMeetingFragment.this, hour, minute,true);
//        timePickerDialog.show();
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        StringBuilder sb1=new StringBuilder("");
        StringBuilder sb2=new StringBuilder("");
        if(hourOfDay<10){
            sb1.append('0');
        }
        if(minute<10){
           sb2.append('0');
        }
        sb1.append(hourOfDay);
        sb2.append(minute);
        myHour = sb1.toString();
        myMinute = sb2.toString();
        setDateandTime();
    }
    private void setDateandTime() {
        //Toast.makeText(getContext(),""+set+" "+myMonth,Toast.LENGTH_SHORT).show();
        if (set == false) {
            start.setText(myHour+": "+myMinute);
        } else {
            end.setText(myHour+": "+myMinute);
        }
    }
    @Override
    public void onItemClicked(int position, View itemView) {

        Participant participant=ParticipantList.get(position);

         if(part.contains(participant.getId())){
             Toast.makeText(getActivity(),"De-selected "+participant.getFname(),Toast.LENGTH_SHORT).show();
             part.remove(participant.getId());
             itemView.setBackgroundColor(Color.parseColor("#ffffff"));
         }
         else {
             itemView.setBackgroundColor(Color.parseColor("#57EC25"));
             Toast.makeText(getActivity(),"Selected "+participant.getFname(),Toast.LENGTH_SHORT).show();
             part.add(participant.getId());
         }
    }
}