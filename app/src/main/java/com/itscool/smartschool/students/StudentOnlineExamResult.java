package com.itscool.smartschool.students;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.itscool.smartschool.adapters.StudentOnlineExamResultAdapter;
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

public class StudentOnlineExamResult extends AppCompatActivity {
    public ImageView backBtn;
    public String defaultDateFormat, currency;
    RecyclerView recyclerView;
    LinearLayout nodata_layout;
    StudentOnlineExamResultAdapter adapter;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();
    SwipeRefreshLayout pullToRefresh;
    public TextView titleTV;
    TextView exam,fromdate,attempt,todate,duration,percent,total_quest,correct,wrong,notattempt,score;
    protected FrameLayout mDrawerLayout, actionBar;
    ArrayList<String> idlist = new ArrayList<String>();
    ArrayList<String> questionlist = new ArrayList<String>();
    ArrayList<String> subject_namelist = new ArrayList<String>();
    ArrayList<String> select_optionlist = new ArrayList<String>();
    ArrayList<String> correctlist = new ArrayList<String>();
    String OnlineExam_student_Id,exam_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_online_exam_result);
        backBtn = findViewById(R.id.actionBar_backBtn);
        mDrawerLayout = findViewById(R.id.container);
        actionBar = findViewById(R.id.actionBarSecondary);
        titleTV = findViewById(R.id.actionBar_title);
        OnlineExam_student_Id = getIntent().getExtras().getString("OnlineExam_students_Id");
        exam_id = getIntent().getExtras().getString("exams_id");
        exam = findViewById(R.id.exam);
        fromdate = findViewById(R.id.fromdate);
        attempt = findViewById(R.id.attempt);
        todate = findViewById(R.id.todate);
        duration = findViewById(R.id.duration);
        percent = findViewById(R.id.percent);
        total_quest = findViewById(R.id.total_quest);
        correct = findViewById(R.id.correct);
        wrong = findViewById(R.id.wrong);
        notattempt = findViewById(R.id.notattempt);
        score = findViewById(R.id.score);

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
        titleTV.setText(getApplicationContext().getString(R.string.onlineexamresult));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        nodata_layout = (LinearLayout) findViewById(R.id.nodata_layout);
        adapter = new StudentOnlineExamResultAdapter(StudentOnlineExamResult.this,questionlist,subject_namelist,
                select_optionlist,correctlist,idlist);
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
        params.put("onlineexam_student_id",OnlineExam_student_Id);
        params.put("exam_id",exam_id);
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed(); // call super back pressed method
    }

    @Override
    public void onRestart() {
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
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getOnlineExamResultUrl;
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
                        JSONObject dataArray = obj.getJSONObject("result");
                        JSONObject examArray = dataArray.getJSONObject("exam");
                        questionlist.clear();
                        subject_namelist.clear();
                        select_optionlist.clear();
                        correctlist.clear();

                        if(dataArray.length() != 0) {

                            try {
                                exam.setText(examArray.getString("exam"));
                                duration.setText(examArray.getString("duration"));
                                attempt .setText(examArray.getString("attempt"));
                                fromdate.setText(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, examArray.getString("exam_from")));
                                todate.setText(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, examArray.getString("exam_to")));
                                percent.setText(examArray.getString("passing_percentage"));
                                total_quest.setText(examArray.getString("total_question"));
                                correct.setText(examArray.getString("correct_ans"));
                                wrong.setText(examArray.getString("wrong_ans"));
                                notattempt.setText(examArray.getString("not_attempted"));
                                score.setText(examArray.getString("score"));

                            JSONArray resultArray = dataArray.getJSONArray("question_result");
                            if(resultArray.length() != 0) {
                                for(int i = 0; i < resultArray.length(); i++) {
                                    idlist.add(resultArray.getJSONObject(i).getString("id"));
                                    questionlist.add(resultArray.getJSONObject(i).getString("question"));
                                    subject_namelist.add(resultArray.getJSONObject(i).getString("subject_name"));
                                    select_optionlist.add(resultArray.getJSONObject(i).getString("select_option"));
                                    correctlist.add(resultArray.getJSONObject(i).getString("correct"));
                                }
                                adapter.notifyDataSetChanged();
                            }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

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
                Toast.makeText(StudentOnlineExamResult.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentOnlineExamResult.this); //Creating a Request Queue
        requestQueue.add(stringRequest);  //Adding request to the queue
    }
}
