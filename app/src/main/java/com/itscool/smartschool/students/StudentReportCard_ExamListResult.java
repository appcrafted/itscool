package com.itscool.smartschool.students;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.itscool.smartschool.BaseActivity;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentConsolidateDetailsAdapter;
import com.itscool.smartschool.adapters.StudentExamResultAdapter;
import com.itscool.smartschool.adapters.StudentExamResultGpaAdapter;
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

public class StudentReportCard_ExamListResult extends BaseActivity {

    RecyclerView examResultview;
    TextView examNameTV,exam_quality_pointsTV, totalTV, percentageTV, gradeTV, gradeHeaderTV, statusTV,divisionTV;
    ArrayList<String> examSubjectList = new ArrayList<String>();
    ArrayList<String> examType = new ArrayList<String>();
    ArrayList<String> examcredit_hoursList = new ArrayList<String>();
    ArrayList<String> exammin_marksList = new ArrayList<String>();
    ArrayList<String> exam_grade_pointList = new ArrayList<String>();
    ArrayList<String> examPassingMarksList = new ArrayList<String>();
    ArrayList<String> examget_marksList = new ArrayList<String>();
    ArrayList<String> exam_gradeList = new ArrayList<String>();
    ArrayList<String> credit_hoursList = new ArrayList<String>();
    ArrayList<String> min_marksList = new ArrayList<String>();
    ArrayList<String> examObtainedMarksList = new ArrayList<String>();
    ArrayList<String> examResultList = new ArrayList<String>();
    TextView consolidate_statusTV,consolidate_text,consolidate_marks,consolidate_divisionTV;
    ArrayList<String> examList = new ArrayList<String>();
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> percentageList = new ArrayList<String>();
    ArrayList<String> cons_marks = new ArrayList<String>();
    ArrayList<String> cons_statusTV = new ArrayList<String>();
    ArrayList<String> resultlist = new ArrayList<String>();
    ArrayList<String> gradelist = new ArrayList<String>();
    ArrayList<String> result_statuslist = new ArrayList<String>();
    StudentExamResultAdapter adapter;
    StudentExamResultGpaAdapter gpa_adapter;
    StudentConsolidateDetailsAdapter consol_adapter;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();
    String passedArg;
    TextView consolidate_layout,studentReportCard_credit,studentReportCard_qualitypoints;
    LinearLayout basic_layout,gpa_layout,gpa_bottom_layout,basic_bottom_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_reportcard_activity, null, false);
        mDrawerLayout.addView(contentView, 0);
        passedArg = getIntent().getExtras().getString("Exam_group_Id");
        titleTV.setText(getApplicationContext().getString(R.string.reportCard));

        examResultview = (RecyclerView) findViewById(R.id.studentReportCard_examResultListview);

        examNameTV = findViewById(R.id.studentReportCard_examResultNameTV);
        statusTV = findViewById(R.id.studentReportCard_statusTV);
        totalTV = findViewById(R.id.studentReportCard_examResulTotalTV);
        percentageTV = findViewById(R.id.studentReportCard_percentageTV);
        divisionTV = findViewById(R.id.studentReportCard_division);
        gradeTV = findViewById(R.id.studentReportCard_gradeTV);
        gradeHeaderTV = findViewById(R.id.studentReportCard_gradeHeaderTV);
        consolidate_layout = findViewById(R.id.consolidate_layout);
        studentReportCard_credit = findViewById(R.id.studentReportCard_credit);
        studentReportCard_qualitypoints = findViewById(R.id.studentReportCard_qualitypoints);
        gpa_layout = findViewById(R.id.gpa_layout);
        basic_layout = findViewById(R.id.basic_layout);
        gpa_bottom_layout = findViewById(R.id.gpa_bottom_layout);
        basic_bottom_layout = findViewById(R.id.basic_bottom_layout);
        examNameTV.setText(getIntent().getStringExtra("examName"));
        examNameTV.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.secondaryColour)));

        adapter = new StudentExamResultAdapter(StudentReportCard_ExamListResult.this,
                examType, examSubjectList, examPassingMarksList, examObtainedMarksList, examResultList,examget_marksList);
        gpa_adapter = new StudentExamResultGpaAdapter(StudentReportCard_ExamListResult.this,
                examType,examSubjectList,exam_grade_pointList,examcredit_hoursList, exammin_marksList,exam_gradeList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        examResultview.setLayoutManager(mLayoutManager);
        examResultview.setItemAnimator(new DefaultItemAnimator());

        consolidate_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog(StudentReportCard_ExamListResult.this);
            }
        });

        params.put("student_id",Utility.getSharedPreferences(getApplicationContext(), "studentId"));
        params.put("exam_group_class_batch_exam_id",passedArg);
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }

    private void showAddDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.consolidate_detail_dialog);
        RelativeLayout headerLay = (RelativeLayout) dialog.findViewById(R.id.addTask_dialog_header);
        RecyclerView recyclerview = (RecyclerView) dialog.findViewById(R.id.recyclerview);
        ImageView closeBtn = (ImageView) dialog.findViewById(R.id.addTask_dialog_crossIcon);
         consolidate_text = dialog.findViewById(R.id.consolidate_text);
         consolidate_text.setText("Consolidate");
         consolidate_statusTV = dialog.findViewById(R.id.consolidate_statusTV);
        consolidate_divisionTV = dialog.findViewById(R.id.consolidate_divisionTV);
         consolidate_marks = dialog.findViewById(R.id.consolidate_marks);


        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        params.put("student_id",Utility.getSharedPreferences(getApplicationContext(), "studentId"));
        params.put("exam_group_class_batch_exam_id",passedArg);
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getConsolidateDataFromApi(obj.toString());

        consol_adapter = new StudentConsolidateDetailsAdapter(getApplicationContext(),examList,percentageList,idList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(consol_adapter);
        headerLay.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        dialog.show();
    }

    private void getDataFromApi (String bodyParams) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getExamResultUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);
                        JSONObject exam = object.getJSONObject("exam");
                            examNameTV.setText(exam.getString("exam"));
                            examType.add(exam.getString("exam_type"));

                            if(exam.getString("exam_type").equals("gpa")){
                                gpa_layout.setVisibility(View.VISIBLE);
                                gpa_bottom_layout.setVisibility(View.VISIBLE);
                                basic_layout.setVisibility(View.GONE);
                                basic_bottom_layout.setVisibility(View.GONE);
                                studentReportCard_credit.setText(exam.getString("exam_credit_hour"));

                                Float value = Float.parseFloat(exam.getString("exam_quality_points"))/Float.parseFloat(exam.getString("exam_credit_hour"));

                                studentReportCard_qualitypoints.setText(String.valueOf(value));
                                JSONArray dataArray = exam.getJSONArray("subject_result");
                                for(int i=0; i<dataArray.length(); i++) {
                                    examSubjectList.add(dataArray.getJSONObject(i).getString("name"));
                                    examcredit_hoursList.add(dataArray.getJSONObject(i).getString("credit_hours"));
                                    exammin_marksList.add(dataArray.getJSONObject(i).getString("exam_quality_points"));
                                    exam_grade_pointList.add(dataArray.getJSONObject(i).getString("exam_grade_point"));
                                    exam_gradeList.add(dataArray.getJSONObject(i).getString("note"));
                                    System.out.println("examResultList="+examResultList);
                                }
                                gpa_adapter.notifyDataSetChanged();
                                examResultview.setAdapter(gpa_adapter);
                                System.out.println("dataArray="+dataArray.toString());
                                String consolidate = exam.getString("is_consolidate");
                                System.out.println("is_consolidate= "+consolidate);
                                if(consolidate.equals("0")){
                                    consolidate_layout.setVisibility(View.GONE);
                                }else {
                                    consolidate_layout.setVisibility(View.VISIBLE);
                                }

                            }else{
                                basic_layout.setVisibility(View.VISIBLE);
                                basic_bottom_layout.setVisibility(View.VISIBLE);
                                gpa_layout.setVisibility(View.GONE);
                                gpa_bottom_layout.setVisibility(View.GONE);
                                totalTV.setText(" : "+exam.getString("total_get_marks")+ "/" + exam.getString("total_max_marks"));
                                percentageTV.setText(" : "+exam.getString("percentage"));
                                divisionTV.setText(" : "+exam.getString("division"));
                              //  percentageTV.setText(exam.getString("exam_credit_hour"));
                                JSONArray dataArray = exam.getJSONArray("subject_result");
                                for(int i=0; i<dataArray.length(); i++) {
                                    examSubjectList.add(dataArray.getJSONObject(i).getString("name"));
                                    examPassingMarksList.add(dataArray.getJSONObject(i).getString("min_marks"));
                                    examget_marksList.add(dataArray.getJSONObject(i).getString("get_marks"));

                                    if(dataArray.getJSONObject(i).getString("attendence").equals("absent")) {
                                        examObtainedMarksList.add(getApplicationContext().getString(R.string.absent)+"/"+dataArray.getJSONObject(i).getString("max_marks"));
                                    } else {
                                        examObtainedMarksList.add(dataArray.getJSONObject(i).getString("get_marks")+"/"+dataArray.getJSONObject(i).getString("max_marks"));
                                    }
                                    examResultList.add(dataArray.getJSONObject(i).getString("note"));
                                }
                                System.out.println("dataArray="+dataArray.toString());
                                String status = exam.getString("exam_result_status");
                                if(status.toLowerCase().equals("pass")) {
                                    statusTV.setBackgroundResource(R.drawable.green_border);
                                } else {
                                    statusTV.setBackgroundResource(R.drawable.red_border);
                                }
                                statusTV.setText(status);

                                String consolidate = exam.getString("is_consolidate");
                                System.out.println("is_consolidate= "+consolidate);
                                if(consolidate.equals("0")){
                                    consolidate_layout.setVisibility(View.GONE);
                                }else {
                                    consolidate_layout.setVisibility(View.VISIBLE);
                                }
                                adapter.notifyDataSetChanged();
                                examResultview.setAdapter(adapter);
                            }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(StudentReportCard_ExamListResult.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getApplicationContext(), "accessToken"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentReportCard_ExamListResult.this); //Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
    }

    private void getConsolidateDataFromApi (String bodyParams) {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getExamResultUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);
                        JSONObject exam = object.getJSONObject("exam");

                        examList.clear();
                        percentageList.clear();
                        idList.clear();
                        result_statuslist.clear();
                        examSubjectList.clear();
                        examPassingMarksList.clear();
                        examObtainedMarksList.clear();
                        examResultList.clear();


                        String success = object.getString("status");
                        if (success.equals("200")) {
                            totalTV.setText(exam.getString("total_get_marks") + "/" + exam.getString("total_max_marks"));
                            //  percentageTV.setText(exam.getString("percentage"));
                            examNameTV.setText(exam.getString("exam"));

                            String status = exam.getString("exam_result_status");
                            if(status.toLowerCase().equals("pass")) {
                                statusTV.setBackgroundResource(R.drawable.green_border);
                            } else {
                                statusTV.setBackgroundResource(R.drawable.red_border);
                            }
                            statusTV.setText(status);

                            String consolidate = exam.getString("is_consolidate");
                            if(consolidate.equals("1")){
                                consolidate_layout.setVisibility(View.VISIBLE);
                            }else {
                                consolidate_layout.setVisibility(View.GONE);
                            }

                            JSONArray dataArray = exam.getJSONArray("subject_result");
                            for(int i=0; i<dataArray.length(); i++) {
                                examSubjectList.add(dataArray.getJSONObject(i).getString("name"));
                                examPassingMarksList.add(dataArray.getJSONObject(i).getString("min_marks"));

                                if(dataArray.getJSONObject(i).getString("attendence").equals("absent")) {
                                    examObtainedMarksList.add(getApplicationContext().getString(R.string.absent)+"/"+dataArray.getJSONObject(i).getString("max_marks"));
                                } else {
                                    examObtainedMarksList.add(dataArray.getJSONObject(i).getString("get_marks")+"/"+dataArray.getJSONObject(i).getString("max_marks"));
                                }
                                examResultList.add(dataArray.getJSONObject(i).getString("note"));
                            }

                            JSONObject consolidate_object = exam.getJSONObject("consolidated_exam_result");
                            JSONArray examarray = consolidate_object.getJSONArray("exam_array");
                            for(int i=0; i<examarray.length(); i++) {
                                examList.add(examarray.getJSONObject(i).getString("exam"));
                                percentageList.add(examarray.getJSONObject(i).getString("percentage"));
                                idList.add(examarray.getJSONObject(i).getString("id"));
                            }
                             JSONObject consolidate_result = consolidate_object.getJSONObject("consolidate_result");
                            consolidate_marks.setText(consolidate_result.getString("result")+"("+consolidate_result.getString("grade")+")");
                            consolidate_statusTV.setText(consolidate_result.getString("result_status"));
                            consolidate_divisionTV.setText(" : "+consolidate_result.getString("division"));

                            if(consolidate_result.getString("result_status").equals("fail")){
                                consolidate_statusTV.setBackgroundColor(Color.parseColor("#ed5c5c"));
                            }else{
                                consolidate_statusTV.setBackgroundColor(Color.parseColor("#66aa18"));
                            }

                            consol_adapter.notifyDataSetChanged();

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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(StudentReportCard_ExamListResult.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getApplicationContext(), "accessToken"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentReportCard_ExamListResult.this); //Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
    }
}
