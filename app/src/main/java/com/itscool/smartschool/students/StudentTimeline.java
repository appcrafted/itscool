package com.itscool.smartschool.students;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itscool.smartschool.BaseActivity;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentTimelineAdapter;
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

public class StudentTimeline extends BaseActivity {

    StudentTimelineAdapter adapter;

    ArrayList<String> timeLineIdList = new ArrayList<>();
    ArrayList<String> timeLineDocumentList = new ArrayList<>();
    ArrayList<String> timeLineTitleList = new ArrayList<>();
    ArrayList<String> timeLineDescList = new ArrayList<>();
    ArrayList<String> timeLineDateList = new ArrayList<>();
    ArrayList<String> timeLineStatusList = new ArrayList<>();
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_timeline_activity, null, false);
        mDrawerLayout.addView(contentView, 0);

        titleTV.setText(getApplicationContext().getString(R.string.timeLine));

        RecyclerView listView = findViewById(R.id.student_timeline_listview);
        adapter = new StudentTimelineAdapter(StudentTimeline.this, timeLineIdList, timeLineDocumentList,
                timeLineTitleList, timeLineDescList, timeLineDateList, timeLineStatusList );

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(adapter);
        listView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), 0));
        params.put("studentId", Utility.getSharedPreferences(getApplicationContext(), "studentId"));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());

        getDataFromApi(obj.toString());

    }

    private void getDataFromApi (String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+ Constants.getTimelineUrl;
        Log.e("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONArray dataArray = new JSONArray(result);

                        if (dataArray.length() != 0) {

                            for(int i = 0; i < dataArray.length(); i++) {
                                timeLineIdList.add(dataArray.getJSONObject(i).getString("id"));
                                timeLineDocumentList.add(dataArray.getJSONObject(i).getString("document"));
                                timeLineTitleList.add(dataArray.getJSONObject(i).getString("title"));
                                timeLineDescList.add(dataArray.getJSONObject(i).getString("description"));
                                timeLineDateList.add(dataArray.getJSONObject(i).getString("timeline_date"));
                                timeLineStatusList.add(dataArray.getJSONObject(i).getString("status"));
                            }
                            adapter.notifyDataSetChanged();

                        } else {
                            Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.noData), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        pd.dismiss();
                        Log.e("Volley Error", volleyError.toString());
                        Toast.makeText(StudentTimeline.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getApplicationContext(), "accessToken"));
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
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(StudentTimeline.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }


}
