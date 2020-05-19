package com.itscool.smartschool.students;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
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
import com.itscool.smartschool.adapters.StudentApplyLeaveAdapter;
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

public class StudentAppyLeave extends AppCompatActivity {
    public ImageView backBtn;
    public String defaultDateFormat, currency;
    RecyclerView recyclerView;
    LinearLayout nodata_layout;
    FloatingActionButton add_leave;
    StudentApplyLeaveAdapter adapter;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();
    SwipeRefreshLayout pullToRefresh;
    public TextView titleTV;
    protected FrameLayout mDrawerLayout, actionBar;
    String applydate="";
    String fromdate="";
    String todate="";
    private boolean isapplyDateSelected = false;
    private boolean isfromDateSelected = false;
    private boolean istoDateSelected = false;
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> fromList = new ArrayList<String>();
    ArrayList<String> toList = new ArrayList<String>();
    ArrayList<String> statuslist = new ArrayList<String>();
    ArrayList<String> idlist = new ArrayList<String>();
    ArrayList<String> reasonlist = new ArrayList<String>();
    ArrayList<String> sfromlist = new ArrayList<String>();
    ArrayList<String> stolist = new ArrayList<String>();
    ArrayList<String> sapplylist = new ArrayList<String>();
    ArrayList<String> apply_datelist = new ArrayList<String>();
    ArrayList<String> docslist = new ArrayList<String>();
    Bitmap bitmap;
    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_apply_leave);
        backBtn = findViewById(R.id.actionBar_backBtn);
        mDrawerLayout = findViewById(R.id.container);
        actionBar = findViewById(R.id.actionBarSecondary);
        titleTV = findViewById(R.id.actionBar_title);
        loaddata();
        add_leave = findViewById(R.id.studentLeave_fab);
        add_leave.setBackgroundTintList(ColorStateList.valueOf(Color
                .parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour))));

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
        titleTV.setText(getApplicationContext().getString(R.string.applyleave));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        nodata_layout = (LinearLayout) findViewById(R.id.nodata_layout);
        adapter = new StudentApplyLeaveAdapter(StudentAppyLeave.this, nameList, fromList,
                toList, statuslist, idlist,apply_datelist,docslist,reasonlist,sfromlist,stolist,sapplylist);
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

        add_leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(StudentAppyLeave.this,StudentAddLeaveNew.class);
               startActivity(intent);
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    private void decorate() {
        actionBar.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        }
    }
    public void loaddata() {
        params.put("student_id", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId));
        JSONObject obj = new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }

    @Override
    public void onRestart() {
        super.onRestart();
        loaddata();

    }


    private void getDataFromApi(String bodyParams) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.getApplyLeaveUrl;
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
                        JSONArray dataArray = obj.getJSONArray("result_array");
                        fromList.clear();
                        toList.clear();
                        statuslist.clear();
                        idlist.clear();
                        sfromlist.clear();
                        sapplylist.clear();
                        nameList.clear();
                        reasonlist.clear();
                        stolist.clear();
                        docslist.clear();
                        apply_datelist.clear();
                        if (dataArray.length() != 0) {
                            for (int i = 0; i < dataArray.length(); i++) {
                                nameList.add(dataArray.getJSONObject(i).getString("firstname") +""+ dataArray.getJSONObject(i).getString("lastname"));
                                fromList.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getJSONObject(i).getString("from_date")));
                                toList.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getJSONObject(i).getString("to_date")));
                                statuslist.add(dataArray.getJSONObject(i).getString("status"));
                                idlist.add(dataArray.getJSONObject(i).getString("id"));
                                reasonlist.add(dataArray.getJSONObject(i).getString("reason"));
                                apply_datelist.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat,dataArray.getJSONObject(i).getString("apply_date")));
                                docslist.add(dataArray.getJSONObject(i).getString("docs"));
                                sfromlist.add(dataArray.getJSONObject(i).getString("from_date"));
                                stolist.add(dataArray.getJSONObject(i).getString("to_date"));
                                sapplylist.add(dataArray.getJSONObject(i).getString("apply_date"));
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
                Toast.makeText(StudentAppyLeave.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentAppyLeave.this); //Creating a Request Queue
        requestQueue.add(stringRequest);  //Adding request to the queue
    }



}
