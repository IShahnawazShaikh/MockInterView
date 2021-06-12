package com.example.mockinterview.view_models;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.example.mockinterview.Utilities.ApiUrl;
import com.example.mockinterview.model.Meeting;
import com.example.mockinterview.model.Participant;
import com.example.mockinterview.model.Scheduled;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

public class FetchData extends ViewModel {

    private MutableLiveData<List<Meeting>> meeting=null;
    private MutableLiveData<List<Participant>> unscheduleList=null;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Meeting>> getMeeting(Context context) {

        meeting = null;
        meeting=new MutableLiveData();
            //we will load it asynchronously from server in this method
            // Log.d("DATA", category + subcategory + subject + "");
            getMeetings(context);
        //finally we will return the list
        return meeting;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public LiveData<List<Participant>> unschedule(String startTime, String endtTime, Context context) {

        unscheduleList=null;
        unscheduleList=new MutableLiveData();

        getUnscheduleData(context,startTime,endtTime);

        //finally we will return the list
        return unscheduleList;
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getMeetings(Context context) {

        StringRequest request = new StringRequest(Request.Method.GET, ApiUrl.ALL_MEETINGS,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray= new JSONArray(response);
                            List<Meeting> allContestBean = new ArrayList<Meeting>();
                             for(int i=0;i<jsonArray.length();i++) {
                               JSONObject obj = jsonArray.getJSONObject(i);

                               Meeting object = new Meeting("" + obj.optString("start_time"),
                                            "" + obj.optString("end_time"),
                                            obj.optString("subject"),

                                            obj.optString("meet_id"));
                                    allContestBean.add(object);
                                }
                            Collections.sort(allContestBean, new Comparator<Meeting>() {
                                @Override
                                public int compare(Meeting o1, Meeting o2) {
                                    return o1.getStart_time().compareTo(o2.getStart_time());
                                }
                            });
                            meeting.setValue(allContestBean);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(,"Response errorr", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getUnscheduleData(Context context, String startTime,String endTime) {
       // Toast.makeText(context,"3",Toast.LENGTH_SHORT).show();
        StringRequest request = new StringRequest(Request.Method.GET, ApiUrl.ALL_UNSCHEDULE,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onResponse(String response) {
                        try {

                            JSONArray jsonArray= new JSONArray(response);
                            Map<String,Participant> AllMap=new HashMap<>();
                            Map<String,Meeting> MeetingMap=new HashMap<>();
                            Map<String, ArrayList<String>> ScheduledMap=new HashMap<>();


                            JSONArray UserJSonarray=jsonArray.getJSONArray(0);
                            JSONArray MeetingsJsonArray=jsonArray.getJSONArray(1);
                            JSONArray ScheduledJsonArray=jsonArray.getJSONArray(2);


                            for(int j=0;j<UserJSonarray.length();j++){
                                   //Toast.makeText(context,"val: "+jsonArray2.getJSONObject(i),Toast.LENGTH_SHORT).show();
                                   JSONObject obj=UserJSonarray.getJSONObject(j);

                                   Participant participant=new Participant(obj.optString("fname"),obj.optString("lname"),
                                           obj.optString("id"));
                                AllMap.put(obj.optString("id"),participant);
                            }

                            for(int j=0;j<MeetingsJsonArray.length();j++){
                                JSONObject obj=MeetingsJsonArray.getJSONObject(j);
                                Meeting object = new Meeting("" + obj.optString("start_time"),
                                        "" + obj.optString("end_time"),
                                        obj.optString("subject"),
                                        obj.optString("meet_id"));

                                MeetingMap.put(obj.optString("meet_id"),object);
                            }

                            for(int j=0;j<ScheduledJsonArray.length();j++){
                                JSONObject obj=ScheduledJsonArray.getJSONObject(j);
                                Scheduled object = new Scheduled("" + obj.optString("user_id"),
                                        obj.optString("meet_id"));

                                if(ScheduledMap.containsKey(obj.optString("meet_id"))){
                                    ArrayList<String> list=ScheduledMap.get(obj.optString("meet_id"));
                                    list.add(obj.optString("user_id"));
                                }
                                else{
                                    ArrayList<String> list=new ArrayList<>();
                                    list.add(obj.optString("user_id"));
                                    ScheduledMap.put(obj.optString("meet_id"),list);
                                }

                            }

                           // System.out.println("-----------------------------"+startTime+" "+endTime);

                            for(Map.Entry<String,ArrayList<String>> entry: ScheduledMap.entrySet()){

                                Meeting obj=MeetingMap.get(entry.getKey());
                               // System.out.println("start End: "+obj.getStart_time()+" "+obj.getEnd_time());

                                if(obj.getEnd_time().compareTo(startTime)<0 || obj.getStart_time().compareTo(endTime)>0){
                                  continue;
                                }
                                else {
                                    System.out.println("--------------------else");
                                    ArrayList<String> mList = entry.getValue();

                                    for(String id: mList){
                                        if (AllMap.containsKey(id)){
                                            Participant p= AllMap.remove(id);
                                            System.out.println("Removed: "+p.getId()+" "+p.getFname());
                                        }
                                    }

                                }
                            }

                            List<Participant> part = new ArrayList<Participant>(AllMap.values());

                            unscheduleList.setValue(part);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(,"Response errorr", Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

}