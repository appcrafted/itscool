package com.itscool.smartschool.students;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.itscool.smartschool.adapters.StudentOnlineExamListAdapter;
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

public class StudentOnlineExam extends AppCompatActivity {
    public ImageView backBtn;
    public String defaultDateFormat, currency;
    RecyclerView recyclerView;
    LinearLayout nodata_layout;
    StudentOnlineExamListAdapter adapter;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();
    SwipeRefreshLayout pullToRefresh;
    public TextView titleTV;
    protected FrameLayout mDrawerLayout, actionBar;
    ArrayList<String> examList = new ArrayList<String>();
    ArrayList<String> exam_fromList = new ArrayList<String>();
    ArrayList<String> exam_toList = new ArrayList<String>();
    ArrayList<String> durationList = new ArrayList<String>();
    ArrayList<String> attemptList = new ArrayList<String>();
    ArrayList<String> attemptslist = new ArrayList<String>();
    ArrayList<String> onlineexam_idlist = new ArrayList<String>();
    ArrayList<String> onlineexam_student_idlist = new ArrayList<String>();
    ArrayList<String> publish_resultlist = new ArrayList<String>();
    ArrayList<String> is_submittedlist = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_online_exam);
        backBtn = findViewById(R.id.actionBar_backBtn);
        mDrawerLayout = findViewById(R.id.container);
        actionBar = findViewById(R.id.actionBarSecondary);
        titleTV = findViewById(R.id.actionBar_title);
        loaddata();
        defaultDateFormat = Utility.getSharedPreferences(getApplicationContext(), "dateFormat");
        currency = Utility.getSharedPreferences(getApplicationContext(), Constants.currency);

        decorate();
        Utility.setLocale(getApplicationContext(), Utility.getSharedPreferences(getApplicationContext(), Constants.langCode));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleTV.setText(getApplicationContext().getString(R.string.onlineexam));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        nodata_layout = (LinearLayout) findViewById(R.id.nodata_layout);
        adapter = new StudentOnlineExamListAdapter(StudentOnlineExam.this, examList,exam_fromList,
                exam_toList,durationList,attemptList,attemptslist,onlineexam_idlist,publish_resultlist,is_submittedlist,onlineexam_student_idlist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        pullToRefresh = findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh.setRefreshing(true);
                loaddata();
            }
        });
        loaddata();
    }
    private void decorate() {
        actionBar.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        }
    }
    public  void  loaddata(){
        params.put("student_id", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();   // call super back pressed method
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        loaddata();
        // do some stuff here
    }

    private void getDataFromApi (String bodyParams) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getOnlineExamUrl;
        Log.e("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                pullToRefresh.setRefreshing(false);
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject obj = new JSONObject(result);
                        JSONArray dataArray = obj.getJSONArray("onlineexam");
                        examList.clear();
                        attemptList.clear();
                        examList.clear();
                        exam_fromList.clear();
                        durationList.clear();
                        exam_toList.clear();
                        attemptslist.clear();
                        onlineexam_idlist.clear();
                        onlineexam_student_idlist.clear();
                        publish_resultlist.clear();
                        is_submittedlist.clear();
                        if(dataArray.length() != 0) {
                            for(int i = 0; i < dataArray.length(); i++) {
                                examList.add(dataArray.getJSONObject(i).getString("exam"));
                                durationList.add(dataArray.getJSONObject(i).getString("duration"));
                                attemptList .add(dataArray.getJSONObject(i).getString("attempt"));
                                exam_fromList.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getJSONObject(i).getString("exam_from")));
                                exam_toList.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getJSONObject(i).getString("exam_to")));
                                attemptslist.add(dataArray.getJSONObject(i).getString("attempts"));
                                onlineexam_idlist.add(dataArray.getJSONObject(i).getString("id"));
                                onlineexam_student_idlist.add(dataArray.getJSONObject(i).getString("onlineexam_student_id"));
                                publish_resultlist.add(dataArray.getJSONObject(i).getString("publish_result"));
                                is_submittedlist.add(dataArray.getJSONObject(i).getString("is_submitted"));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            pullToRefresh.setVisibility(View.GONE);
                            nodata_layout.setVisibility(View.VISIBLE);
                            //Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.noData), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(StudentOnlineExam.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentOnlineExam.this); //Creating a Request Queue
        requestQueue.add(stringRequest);  //Adding request to the queue
    }
}
