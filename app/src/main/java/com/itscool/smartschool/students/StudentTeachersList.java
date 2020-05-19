package com.itscool.smartschool.students;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itscool.smartschool.BaseActivity;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentTeacherNewAdapter;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

public class StudentTeachersList extends BaseActivity implements  SwipeRefreshLayout.OnRefreshListener{

    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();
    RecyclerView recyclerview;
    String key;
    JSONArray listArray = new JSONArray();
    ArrayList <String> teacherNameList = new ArrayList<String>();
    ArrayList <String> teacherContactList = new ArrayList<String>();
    ArrayList <String> teacheremailList = new ArrayList<String>();
    ArrayList<String> class_teacher_idList = new ArrayList<>();
    ArrayList<String> staff_idList = new ArrayList<>();
    ArrayList<String> ratingList = new ArrayList<>();
    StudentTeacherNewAdapter adapter;
    SwipeRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_teacher_activity_new, null, false);
        mDrawerLayout.addView(contentView, 0);
        titleTV.setText(getApplicationContext().getString(R.string.teachers));
        recyclerview = findViewById(R.id.recyclerview);

        params.put("class_id",  Utility.getSharedPreferences(getApplicationContext(), Constants.classId));
        params.put("section_id", Utility.getSharedPreferences(getApplicationContext(), Constants.sectionId));
        params.put("user_id", Utility.getSharedPreferences(getApplicationContext(), Constants.userId));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
        pullToRefresh =findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(this);

        pullToRefresh.post(new Runnable() {
                               @Override
                               public void run() {
                                   pullToRefresh.setRefreshing(true);
                                   params.put("class_id",  Utility.getSharedPreferences(getApplicationContext(), Constants.classId));
                                   params.put("section_id", Utility.getSharedPreferences(getApplicationContext(), Constants.sectionId));
                                   params.put("user_id", Utility.getSharedPreferences(getApplicationContext(), Constants.userId));
                                   JSONObject obj=new JSONObject(params);
                                   Log.e("params ", obj.toString());
                                   getDataFromApi(obj.toString());
                               }
                           }
        );
        adapter = new StudentTeacherNewAdapter(StudentTeachersList.this, teacherNameList,class_teacher_idList,
                 teacherContactList,staff_idList,ratingList,teacheremailList);
        RecyclerView.LayoutManager mondayLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(mondayLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(adapter);
    }

    public void getDataFromApi (String bodyParams) {

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getTeacherListUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                pullToRefresh.setRefreshing(false);
                if (result != null) {

                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);
                        JSONObject dataObject = object.getJSONObject("result_list");
                        System.out.println("DATAOBJECT length- "+dataObject.length());
                        teacherNameList.clear();
                        teacherContactList.clear();
                        staff_idList.clear();
                        ratingList.clear();
                        class_teacher_idList.clear();
                       Iterator<String> iter = dataObject.keys();
                       while(iter.hasNext()){
                          key = iter.next();
                            System.out.println("DATAOBJECT-"+key);
                            JSONObject object1 = dataObject.getJSONObject(key);
                            teacherNameList.add(object1.getString("staff_name") + " " + object1.getString("staff_surname")+ " (" + object1.getString("employee_id")+")" );
                            teacherContactList.add(object1.getString("contact_no"));
                            teacheremailList.add(object1.getString("email"));
                            class_teacher_idList.add(object1.getString("class_teacher_id"));
                            staff_idList.add(object1.getString("staff_id"));
                            ratingList.add(object1.getString("rate"));
                            adapter.notifyDataSetChanged();
                      }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                    Toast.makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(StudentTeachersList.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentTeachersList.this);//Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
    }

    @Override
    public void onRefresh() {
        params.put("class_id",  Utility.getSharedPreferences(getApplicationContext(), Constants.classId));
        params.put("section_id", Utility.getSharedPreferences(getApplicationContext(), Constants.sectionId));
        params.put("user_id", Utility.getSharedPreferences(getApplicationContext(), Constants.userId));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }
}
