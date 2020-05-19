package com.itscool.smartschool.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentExamListAdapter;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StudentDashboardReportCard extends Fragment {

    RecyclerView examListview;
    ArrayList<String> examList = new ArrayList<String>();
    ArrayList<String> exam_group_List = new ArrayList<String>();
    ArrayList<String> publish_resultlist = new ArrayList<String>();
    ArrayList<String> idlist = new ArrayList<String>();
    StudentExamListAdapter adapter;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    public StudentDashboardReportCard() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void loadData() {

        adapter = new StudentExamListAdapter(getActivity(), examList,exam_group_List,
                publish_resultlist,idlist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        examListview.setLayoutManager(mLayoutManager);
        examListview.setItemAnimator(new DefaultItemAnimator());
        examListview.setAdapter(adapter);

        params.put("student_id", Utility.getSharedPreferences(getActivity(), Constants.studentId));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.student_report_card_exam_list_activity, container, false);
        examListview = (RecyclerView) mainView.findViewById(R.id.studentReportCard_examListview);
        loadData();
        return mainView;
    }

    private void getDataFromApi (String bodyParams) {
        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(getActivity(), "apiUrl")+Constants.getExamListUrl;
        Log.e("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject obj = new JSONObject(result);
                        JSONArray dataArray = obj.getJSONArray("examSchedule");
                        examList.clear();
                        publish_resultlist.clear();
                        exam_group_List.clear();
                        idlist.clear();
                        if(dataArray.length() != 0) {
                            for(int i = 0; i < dataArray.length(); i++) {
                                examList.add(dataArray.getJSONObject(i).getString("exam"));
                                publish_resultlist.add(dataArray.getJSONObject(i).getString("result_publish"));
                                exam_group_List .add(dataArray.getJSONObject(i).getString("exam_group_class_batch_exam_id"));
                                idlist.add(dataArray.getJSONObject(i).getString("id"));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            //Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.noData), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(getActivity(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
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
                headers.put("User-ID", Utility.getSharedPreferences(getActivity(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getActivity(), "accessToken"));
                Log.e("Headers", headers.toString());
                return headers;
            }
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity()); //Creating a Request Queue
        requestQueue.add(stringRequest);  //Adding request to the queue
    }

}
