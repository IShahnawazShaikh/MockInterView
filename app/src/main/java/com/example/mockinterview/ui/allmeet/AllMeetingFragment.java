package com.example.mockinterview.ui.allmeet;

import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mockinterview.R;
import com.example.mockinterview.Utilities.ApiUrl;
import com.example.mockinterview.adapter.RecyclerAdapter;
import com.example.mockinterview.all_interface.RecyclerViewInterFace;
import com.example.mockinterview.model.Meeting;
import com.example.mockinterview.ui.createmeet.CreateMeetingFragment;
import com.example.mockinterview.view_models.FetchData;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AllMeetingFragment extends Fragment implements RecyclerViewInterFace {
    View view;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    RecyclerAdapter recyclerAdapter;
    SwipeRefreshLayout refresh;
    List<Meeting> meetingList;
    ProgressBar progress_bar;
    Button Update;
    private OkHttpClient okHttpClient;
    @RequiresApi(api = Build.VERSION_CODES.O)
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_meetings, container, false);
        Update=view.findViewById(R.id.update);
        recyclerView=(RecyclerView) view.findViewById(R.id.meeting_recycler);
        progress_bar=(ProgressBar) view.findViewById(R.id.progressBar);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.refresh);
        manager=new LinearLayoutManager(getContext());

        updLoadData();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            FetchData model = ViewModelProviders.of(getActivity()).get(FetchData.class);
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onRefresh() {
                model.getMeeting(getActivity()).observe(getActivity(), new Observer<List<Meeting>>() {
                    @Override
                    public void onChanged(List<Meeting> meet) {
                        meetingList.clear();
                        meetingList= new ArrayList<Meeting>(meet);
                        recyclerView.setLayoutManager(manager);
                        recyclerAdapter =new RecyclerAdapter(meet, AllMeetingFragment.this);
                        recyclerView.setAdapter(recyclerAdapter);
                        recyclerAdapter.notifyDataSetChanged();
                        refresh.setRefreshing(false);
                    }
                });
            }
        });

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updLoadData() {
        FetchData model = ViewModelProviders.of(this).get(FetchData.class);
        model.getMeeting(getActivity()).observe(getActivity(), new Observer<List<Meeting>>() {
            @Override
            public void onChanged(List<Meeting> meet) {
                progress_bar.setVisibility(View.INVISIBLE);
                meetingList= new ArrayList<Meeting>(meet);
                recyclerView.setLayoutManager(manager);
                recyclerAdapter =new RecyclerAdapter(meetingList,AllMeetingFragment.this);
                recyclerView.setAdapter(recyclerAdapter);
                recyclerAdapter.notifyDataSetChanged();
            }
        });
    }
//    public void refershMeeting(){
//        //Toast.makeText(getContext(),"Refreshing",Toast.LENGTH_SHORT).show();
//        ;
//
//    }

    @Override
    public void onItemClicked(int position) {

        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        View view=getLayoutInflater().inflate(R.layout.update,null);

        final EditText subject=view.findViewById(R.id.subject);
        final EditText start=view.findViewById(R.id.start);
        final EditText end=view.findViewById(R.id.end);

        Meeting obj=meetingList.get(position);

        subject.setText(obj.getSubject());
        start.setText(obj.getStart_time());
        end.setText(obj.getEnd_time());

        final Button update=view.findViewById(R.id.update);

        String subjectinit=obj.getSubject();
        String startint=obj.getStart_time();

        String endinit=obj.getEnd_time();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(subject.getText().toString().equals(subjectinit) && start.getText().toString().equals(startint) &&
                        end.getText().toString().equals(endinit)){
                    Toast.makeText(getActivity(),"Change atleat one thing",Toast.LENGTH_SHORT).show();
                }
                else{
                   update(subject.getText().toString(),start.getText().toString(),end.getText().toString(),obj.getMeet_id());
                }


            }
        });
        builder.setView(view);
        final AlertDialog dialog=builder.create();


        // dialog.setCanceledOnTouchOutside(false);
        //dialog.setCancelable(false);
        dialog.show();
    }

    private void update(String sub, String start, String end,String meet_id) {

        new Thread(() -> {
            try {
                String response = updateDetail(ApiUrl.UPDATE_MEETING,start,end,sub,meet_id);
                JSONObject jsonObject = new JSONObject(response);
                final boolean status = jsonObject.getBoolean("status");

                if (status) {
                    getActivity().runOnUiThread(() -> {
                       Toast.makeText(getContext(), "Updated Successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "" + "Something went wrong !!!", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                Toast.makeText(getContext(),"catch",Toast.LENGTH_SHORT).show();
                getActivity().runOnUiThread(() -> {
                    e.printStackTrace();
                });
            }
        }).start();
        //update
    }
    private String updateDetail(String url,String start,String end,String agenda,String meet_id) throws Exception {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("start", start)
                .addFormDataPart("end", end)
                .addFormDataPart("agenda", agenda)
                .addFormDataPart("meet_id", meet_id)
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
    }
