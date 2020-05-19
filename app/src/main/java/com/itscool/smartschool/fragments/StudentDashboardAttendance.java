package com.itscool.smartschool.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentNotificationAdapter;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StudentDashboardAttendance extends Fragment {

    ListView notificationList;
    StudentNotificationAdapter adapter;

    ArrayList<String> noticeTitleId = new ArrayList<String>();
    ArrayList<String> noticeTitleList = new ArrayList<String>();
    ArrayList <String> noticeDateList = new ArrayList<String>();
    ArrayList <String> noticeDescList = new ArrayList<String>();

    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    public StudentDashboardAttendance() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    private void loadData() {

        adapter = new StudentNotificationAdapter(getActivity(), noticeTitleId, noticeTitleList, noticeDateList, noticeDescList);
        notificationList.setAdapter(adapter);
        params.put("type", Utility.getSharedPreferences(getContext(), Constants.loginType));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View mainView = inflater.inflate(R.layout.student_notice_board_activity, container, false);

        notificationList = (ListView) mainView.findViewById(R.id.studentNotice_listview);

        loadData();

        return mainView;
    }

    private void getDataFromApi (String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        String url = Utility.getSharedPreferences(getActivity().getApplicationContext(), "apiUrl")+Constants.getNotificationsUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);

                        String success = object.getString("success");
                        if (success.equals("1")) {

                            JSONArray dataArray = object.getJSONArray("data");
                            Log.e("length", dataArray.length()+"..");
                            for(int i = 0; i < dataArray.length(); i++) {
                                noticeTitleId.add(dataArray.getJSONObject(i).getString("id"));
                                noticeTitleList.add(dataArray.getJSONObject(i).getString("title"));

                                String defaultDateFormat = Utility.getSharedPreferences(getActivity().getApplicationContext(), "dateFormat");

                                noticeDateList.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getJSONObject(i).getString("date")));
                                noticeDescList.add(dataArray.getJSONObject(i).getString("message"));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        pd.dismiss();
                        Log.e("Volley Error", volleyError.toString());
                        Toast.makeText(getActivity(), R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(getActivity().getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getActivity().getApplicationContext(), "accessToken"));
                Log.e("Headers", headers.toString());
                return headers;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

}
