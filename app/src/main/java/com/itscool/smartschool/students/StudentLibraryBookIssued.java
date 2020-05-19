package com.itscool.smartschool.students;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
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
import com.itscool.smartschool.adapters.StudentLibraryBookIssuedAdapter;
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

public class StudentLibraryBookIssued extends BaseActivity {

    RecyclerView bookListView;
    ArrayList<String> bookNameList = new ArrayList<String>();
    ArrayList<String> authorNameList = new ArrayList<String>();
    ArrayList<String> bookNoList = new ArrayList<String>();
    ArrayList<String> issueDateList = new ArrayList<String>();
    ArrayList<String> returnDateList = new ArrayList<String>();
    ArrayList<String> due_return_dateList = new ArrayList<String>();
    ArrayList<String> statusList = new ArrayList<String>();
    LinearLayout nodata;
    SwipeRefreshLayout pullToRefresh;
    StudentLibraryBookIssuedAdapter adapter;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_library_book_issued_activity, null, false);
        mDrawerLayout.addView(contentView, 0);

        titleTV.setText(getApplicationContext().getString(R.string.booksIssued));
        libraryBtn.setVisibility(View.VISIBLE);

        libraryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), StudentLibraryBook.class));
                finish();
            }
        });

        bookListView = (RecyclerView) findViewById(R.id.student_libraryBookIssued_listview);
        nodata = (LinearLayout) findViewById(R.id.nodata);

        adapter = new StudentLibraryBookIssuedAdapter(StudentLibraryBookIssued.this,
                bookNameList, authorNameList, bookNoList, issueDateList, returnDateList, statusList,due_return_dateList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        bookListView.setLayoutManager(mLayoutManager);
        bookListView.setItemAnimator(new DefaultItemAnimator());
        bookListView.setAdapter(adapter);

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
    public  void  loaddata(){

        params.put("studentId", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }


    private void getDataFromApi(String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getLibraryBookIssuedListUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                pullToRefresh.setRefreshing(false);
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONArray dataArray = new JSONArray(result);
                        System.out.println("Result==="+result);

                        bookNameList.clear();
                        authorNameList.clear();
                        bookNoList.clear();
                        issueDateList.clear();
                        statusList.clear();
                        returnDateList.clear();

                        if (dataArray.length() != 0) {
                            nodata.setVisibility(View.GONE);
                            bookListView.setVisibility(View.VISIBLE);
                            for(int i = 0; i < dataArray.length(); i++) {
                                bookNameList.add(dataArray.getJSONObject(i).getString("book_title"));
                                authorNameList.add(dataArray.getJSONObject(i).getString("author"));
                                bookNoList.add(dataArray.getJSONObject(i).getString("book_no"));
                                issueDateList.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getJSONObject(i).getString("issue_date")));
                                returnDateList.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getJSONObject(i).getString("return_date")));
                                due_return_dateList.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getJSONObject(i).getString("due_return_date")));
                                statusList.add(dataArray.getJSONObject(i).getString("is_returned"));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
                            JSONObject dataArray1 = new JSONObject(result);
                            dataArray1.getString("errorMsg");
                            Toast.makeText(getApplicationContext(), dataArray1.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    pullToRefresh.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(StudentLibraryBookIssued.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentLibraryBookIssued.this); //Creating a Request Queue
        requestQueue.add(stringRequest);
    }
}
