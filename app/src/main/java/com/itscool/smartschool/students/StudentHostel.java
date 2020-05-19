package com.itscool.smartschool.students;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itscool.smartschool.BaseActivity;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentHostelAdapter;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StudentHostel extends BaseActivity {

    RecyclerView hostelListView;
    ArrayList<String> hostelIdList = new ArrayList<String>();
    ArrayList<String> hostelNameList = new ArrayList<String>();
    StudentHostelAdapter adapter;

    public Map<String, String> headers = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_hostel_activity, null, false);
        mDrawerLayout.addView(contentView, 0);

        titleTV.setText(getApplicationContext().getString(R.string.hostel));

        hostelListView = (RecyclerView) findViewById(R.id.studentHostel_listview);

        adapter = new StudentHostelAdapter(StudentHostel.this, hostelIdList, hostelNameList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        hostelListView.setLayoutManager(mLayoutManager);
        hostelListView.setItemAnimator(new DefaultItemAnimator());
        hostelListView.setAdapter(adapter);

        getDataFromApi();

    }

    public void getDataFromApi(){
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getHostelListUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
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
                                hostelIdList.add(dataArray.getJSONObject(i).getString("id"));
                                hostelNameList.add(dataArray.getJSONObject(i).getString("hostel_name"));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getApplicationContext(), object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(StudentHostel.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(StudentHostel.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);

    }
}
