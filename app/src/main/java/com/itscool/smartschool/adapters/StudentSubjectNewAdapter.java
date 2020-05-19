package com.itscool.smartschool.adapters;

import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentSubjectList;
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

public class StudentSubjectNewAdapter extends RecyclerView.Adapter<StudentSubjectNewAdapter.MyViewHolder> {
    private StudentSubjectList context;
    StudentSubjectDetailsAdapter adapter;
    private ArrayList<String> subject_idList;
    private ArrayList<String> subjectList;
    private ArrayList<String> codeList;
    private ArrayList<String> typeList;
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> dayList = new ArrayList<String>();
    ArrayList<String> time_List = new ArrayList<String>();
    ArrayList<String> staff_nameList = new ArrayList<String>();
    ArrayList<String> room_noList = new ArrayList<String>();

    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    public StudentSubjectNewAdapter(StudentSubjectList studentSubjectList, ArrayList<String> subject_idList, ArrayList<String> subjectList,
                                    ArrayList<String> typeList) {
        this.context = studentSubjectList;
        this.subject_idList = subject_idList;
        this.subjectList = subjectList;
        this.typeList = typeList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, subject_type;
        LinearLayout viewdetail;
        RecyclerView recyclerview;
        RelativeLayout classteacher;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.studentSubjectAdapter_nameTV);
            subject_type = (TextView) view.findViewById(R.id.studentSubjectAdapter_subjectType);
            viewdetail = (LinearLayout) view.findViewById(R.id.viewdetail);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_subject, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(subjectList.get(position));
        holder.subject_type.setText(typeList.get(position));
        holder.viewdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            showAddDialog(subject_idList.get(position));
            }
        });


    }
    private void showAddDialog(String subject_id) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.subject_detail_dialog);
        RelativeLayout headerLay = (RelativeLayout) dialog.findViewById(R.id.addTask_dialog_header);
        RecyclerView recyclerview = (RecyclerView) dialog.findViewById(R.id.recyclerview);
        ImageView closeBtn = (ImageView) dialog.findViewById(R.id.addTask_dialog_crossIcon);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        params.put("class_id", Utility.getSharedPreferences(context.getApplicationContext(), Constants.classId));
        params.put("section_id",Utility.getSharedPreferences(context.getApplicationContext(), Constants.sectionId));
        params.put("subject_id", subject_id);
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
        adapter = new StudentSubjectDetailsAdapter(context.getApplicationContext(), idList,dayList,room_noList,
                staff_nameList,time_List);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(adapter);
        headerLay.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.primaryColour)));
        dialog.show();
    }

    private void AddRating(int position ) {
        final Dialog dialog = new Dialog(context,position);
        dialog.setContentView(R.layout.add_rating_dialog);
        RelativeLayout headerLay = (RelativeLayout) dialog.findViewById(R.id.addTask_dialog_header);
        final RatingBar ratingbar = (RatingBar) dialog.findViewById(R.id.rating);
        final EditText comment = (EditText) dialog.findViewById(R.id.comment);
        Button submit = (Button) dialog.findViewById(R.id.submit);
        ImageView closeBtn = (ImageView) dialog.findViewById(R.id.addTask_dialog_crossIcon);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rating=String.valueOf(ratingbar.getRating());
                Toast.makeText(context.getApplicationContext(), "rating= "+rating+" comment= "+comment.getText().toString(), Toast.LENGTH_LONG).show();

            }
        });

        headerLay.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.primaryColour)));
        dialog.show();
    }

    private void getDataFromApi (String bodyParams) {
        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(context.getApplicationContext(), "apiUrl")+Constants.getSubjectTimetableUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    try {
                        Log.e("Result", result);
                        JSONObject obj = new JSONObject(result);
                        JSONArray dataArray = obj.getJSONArray("result_list");
                        idList.clear();
                        dayList.clear();
                        time_List.clear();
                        staff_nameList.clear();
                        room_noList.clear();
                        if(dataArray.length() != 0) {
                            for(int i = 0; i < dataArray.length(); i++) {
                                idList.add(dataArray.getJSONObject(i).getString("id"));
                                dayList.add(dataArray.getJSONObject(i).getString("day"));
                                time_List.add(dataArray.getJSONObject(i).getString("time_from")+"-"+dataArray.getJSONObject(i).getString("time_to"));
                                staff_nameList.add(dataArray.getJSONObject(i).getString("staff_name")+" "+dataArray.getJSONObject(i).getString("staff_surname"));
                                room_noList.add(dataArray.getJSONObject(i).getString("room_no"));
                            }
                            adapter.notifyDataSetChanged();
                        }
                        } catch (JSONException e) {
                        e.printStackTrace();
                }
                } else {
                    Toast.makeText(context.getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(context.getApplicationContext(), R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(context.getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(context.getApplicationContext(), "accessToken"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(context.getApplicationContext()); //Creating a Request Queue
        requestQueue.add(stringRequest); //Adding request to the queue
    }

    @Override
    public int getItemCount() {
        return subject_idList.size();
    }
}

