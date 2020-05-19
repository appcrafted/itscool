package com.itscool.smartschool.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.itscool.smartschool.adapters.StudentProfileCustomAdapter;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

@SuppressLint("ValidFragment")
public class StudentPersonalDetailNew extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    ArrayList<String> appointment_datelist = new ArrayList<String>();
    ArrayList<String> opd_noList = new ArrayList<String>();
    ArrayList<String> consultantList = new ArrayList<String>();
    ArrayList<String> refferencelist = new ArrayList<String>();
    ArrayList<String> symptomslist = new ArrayList<String>();
    private String visitid;
    SwipeRefreshLayout pullToRefresh;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();
    TextView rollno,bloodgroup,mobileno,religion,caste,admDate,dob,category,email,currentAdd,permanentAdd,height,weight,asOnDate;
    LinearLayout blood_layout,mobile_layout,religion_layout,Caste_layout,admDate_layout,dob_layout,
            category_layout,email_layout,currentAdd_layout,permanentAdd_layout,height_layout,weight_layout,asOnDate_layout,myLinearLayout1,myLinearLayout2;
    public String defaultDateFormat, currency;
    String key;
    RecyclerView recyclerview;
    StudentProfileCustomAdapter custom_adapter;
    @SuppressLint("ValidFragment")
    public StudentPersonalDetailNew() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadData();
    }

    private void loadData() {
        params.put("student_id", Utility.getSharedPreferences(getActivity(), "studentId"));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_list, container, false);
        defaultDateFormat = Utility.getSharedPreferences(getActivity(), "dateFormat");
        currency = Utility.getSharedPreferences(getActivity(), Constants.currency);
        category_layout=mainView.findViewById(R.id.category_layout);
        category=mainView.findViewById(R.id.category);


        email_layout=mainView.findViewById(R.id.email_layout);
        email=mainView.findViewById(R.id.email);

        bloodgroup=mainView.findViewById(R.id.bloodgroup);
        blood_layout=mainView.findViewById(R.id.blood_layout);

        mobile_layout=mainView.findViewById(R.id.mobile_layout);
        mobileno=mainView.findViewById(R.id.mobileno);

        religion_layout=mainView.findViewById(R.id.religion_layout);
        religion=mainView.findViewById(R.id.religion);

        caste=mainView.findViewById(R.id.caste);
        Caste_layout=mainView.findViewById(R.id.Caste_layout);

        admDate_layout=mainView.findViewById(R.id.admDate_layout);
        admDate=mainView.findViewById(R.id.admDate);

        currentAdd_layout=mainView.findViewById(R.id.currentAdd_layout);
        currentAdd=mainView.findViewById(R.id.currentAdd);

        dob_layout=mainView.findViewById(R.id.dob_layout);
        dob=mainView.findViewById(R.id.dob);

        permanentAdd_layout=mainView.findViewById(R.id.permanentAdd_layout);
        permanentAdd=mainView.findViewById(R.id.permanentAdd);

        height_layout=mainView.findViewById(R.id.height_layout);
        height=mainView.findViewById(R.id.height);

        weight_layout=mainView.findViewById(R.id.weight_layout);
        weight=mainView.findViewById(R.id.weight);

        asOnDate_layout=mainView.findViewById(R.id.asOnDate_layout);
        asOnDate=mainView.findViewById(R.id.asOnDate);


        pullToRefresh =mainView.findViewById(R.id.pullToRefresh);
        pullToRefresh.setOnRefreshListener(this);

        pullToRefresh.post(new Runnable() {
                               @Override
                               public void run() {
                                   pullToRefresh.setRefreshing(true);
                                   loadData();
                               }
                           }
        );
        return mainView;
    }

    @Override
    public void onRefresh() {
        loadData();
    }

    private void getDataFromApi (String bodyParams) {
        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(getActivity().getApplicationContext(), "apiUrl")+Constants.getStudentProfileUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                pullToRefresh.setRefreshing(false);
                if (result != null) {
                    try {
                        Log.e("Result", result);
                        JSONObject obj = new JSONObject(result);
                        JSONObject dataArray = obj.getJSONObject("student_result");
                        String defaultDateFormat = Utility.getSharedPreferences(getActivity().getApplicationContext(), "dateFormat");

                        admDate.setText(Utility.parseDate("yyyy-MM-dd", defaultDateFormat,dataArray.getString("admission_date")));
                        dob.setText(Utility.parseDate("yyyy-MM-dd", defaultDateFormat,dataArray.getString("dob")));
                        category.setText(dataArray.getString("category"));
                        mobileno.setText(dataArray.getString("mobileno"));
                        caste.setText(dataArray.getString("cast"));
                        religion.setText(dataArray.getString("religion"));
                        email.setText(dataArray.getString("email"));
                        currentAdd.setText(dataArray.getString("current_address"));
                        permanentAdd.setText(dataArray.getString("permanent_address"));
                        bloodgroup.setText(dataArray.getString("blood_group"));
                        height.setText(dataArray.getString("height"));
                        weight.setText(dataArray.getString("weight"));
                        asOnDate.setText(Utility.parseDate("yyyy-MM-dd", defaultDateFormat,dataArray.getString("measurement_date")));

                        JSONObject fieldsArray = obj.getJSONObject("student_fields");
                        System.out.println("fieldsArray=="+fieldsArray);
                        if(!fieldsArray.has("admission_date")){
                            admDate_layout.setVisibility(View.GONE);
                        } if(!fieldsArray.has("category")){
                            category_layout.setVisibility(View.GONE);
                        }if(!fieldsArray.has("mobile_no")){
                            mobile_layout.setVisibility(View.GONE);
                        }if(!fieldsArray.has("religion")){
                            religion_layout.setVisibility(View.GONE);
                        }if(!fieldsArray.has("student_email")){
                            email_layout.setVisibility(View.GONE);
                        }if(!fieldsArray.has("current_address")){
                            currentAdd_layout.setVisibility(View.GONE);
                        }if(!fieldsArray.has("permanent_address")){
                            permanentAdd_layout.setVisibility(View.GONE);
                        }if(!fieldsArray.has("blood_group")){
                            blood_layout.setVisibility(View.GONE);
                        }if(!fieldsArray.has("student_height")){
                            height_layout.setVisibility(View.GONE);
                        }if(!fieldsArray.has("student_weight")){
                            weight_layout.setVisibility(View.GONE);
                        }if(!fieldsArray.has("measurement_date")){
                            asOnDate_layout.setVisibility(View.GONE);
                        }

                        JSONObject customArray = obj.getJSONObject("custom_fields");
                        System.out.println("customArray=="+customArray.length());

                        /*JSONObject customArray = obj.getJSONObject("custom_fields");
                        System.out.println("customArray=="+customArray.length());
                        final int N = customArray.length();
                        final TextView[] myTextViews = new TextView[N];
                        for (int i = 0; i < N; i++) {
                            final TextView rowTextView = new TextView(getActivity());
                            rowTextView.setText("This is row #" + i);
                            myLinearLayout.addView(rowTextView);
                            myTextViews[i] = rowTextView;
                        }*/
                            //find the linear layout
                       /* JSONObject customArray = obj.getJSONObject("custom_fields");
                        System.out.println("customArray=="+customArray.length());
                        Iterator<String> iter = customArray.keys();
                        while(iter.hasNext()) {
                            key = iter.next();
                            System.out.println("key== " + key);
                            JSONObject object1 = customArray.getJSONObject(key);
                           *//* teacherNameList.add(object1.getString("staff_name") + " " + object1.getString("staff_surname") );
                            teacherContactList.add(object1.getString("contact_no"));*//*
                            custom_adapter.notifyDataSetChanged();
                        }*/
                            /*   myLinearLayout1.removeAllViews();
                            for (int i = 0; i < N; i++) { //looping to create 5 textviews

                                TextView textView = new TextView(getActivity()); //dynamically create textview
                                textView.setLayoutParams(new LinearLayout.LayoutParams( //select linearlayoutparam- set the width & height
                                        ViewGroup.LayoutParams.MATCH_PARENT, 48));
                                textView.setGravity(Gravity.CENTER_VERTICAL); //set the gravity too
                                textView.setText("Textview: " + i);  //adding text
                                myLinearLayout1.addView(textView); //inflating :)
                            }*/
                      /*  for (int i = 0; i < 2; i++) {
                            TextView tv = new TextView(getActivity()); // Prepare textview object programmatically
                            tv.setText("Dynamic TextView" + i);
                            tv.setId(i + 5);
                            myLinearLayout.addView(tv); // Add to your ViewGroup using this method
                        }*/

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(getActivity().getApplicationContext(), R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());//Creating a Request Queue
        requestQueue.add(stringRequest); //Adding request to the queue
    }
}