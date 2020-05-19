package com.itscool.smartschool.adapters;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.itscool.smartschool.students.StudentTeachersList;
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

public class StudentTeacherNewAdapter extends RecyclerView.Adapter<StudentTeacherNewAdapter.MyViewHolder> {
    private StudentTeachersList context;
    StudentTeacherDetailsAdapter adapter;
    private ArrayList<String> teacherContactList;
    private ArrayList<String> teacherNameList;
    private ArrayList<String> staff_idList;
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> dayList = new ArrayList<String>();
    ArrayList<String> time_List = new ArrayList<String>();
    ArrayList<String> subject_nameList = new ArrayList<String>();
    ArrayList<String> room_noList = new ArrayList<String>();
    ArrayList<String> teacheremailList = new ArrayList<String>();
    private ArrayList<String> class_teacher_idList;
    private ArrayList<String> ratingList;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    public StudentTeacherNewAdapter(StudentTeachersList studentClassTimetable,ArrayList<String> teacherNameList,ArrayList<String> class_teacher_idList, ArrayList<String> teacherContactList,
                                    ArrayList<String> staff_idList,ArrayList<String> ratingList,ArrayList<String> teacheremailList) {
        this.context = studentClassTimetable;
        this.teacherContactList = teacherContactList;
        this.teacherNameList = teacherNameList;
        this.class_teacher_idList = class_teacher_idList;
        this.teacheremailList = teacheremailList;
        this.ratingList = ratingList;
        this.staff_idList = staff_idList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name, contact,mail;
        LinearLayout viewdetail,rating_layout,add_rating,heading_layout,email_layout,contact_layout;
        RecyclerView recyclerview;
        TextView classteacher;
        RatingBar rating;
        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.studentTeacherAdapter_nameTV);
            contact = (TextView) view.findViewById(R.id.studentTeacherAdapter_contactTV);
            viewdetail = (LinearLayout) view.findViewById(R.id.viewdetail);
            rating_layout = (LinearLayout) view.findViewById(R.id.rating_layout);
            classteacher = (TextView) view.findViewById(R.id.classteacher);
            mail = (TextView) view.findViewById(R.id.studentTeacherAdapter_mailTV);
            add_rating = (LinearLayout) view.findViewById(R.id.add_rating);
            heading_layout = (LinearLayout) view.findViewById(R.id.heading_layout);
            email_layout = (LinearLayout) view.findViewById(R.id.email_layout);
            contact_layout = (LinearLayout) view.findViewById(R.id.contact_layout);
            rating = (RatingBar) view.findViewById(R.id.rating);
            add_rating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddRating(getAdapterPosition());
                }
            });
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_teacher_new, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.heading_layout.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));

        holder.name.setText(teacherNameList.get(position));
        holder.contact.setText(teacherContactList.get(position));
        holder.mail.setText(teacheremailList.get(position));
        if(Integer.valueOf(class_teacher_idList.get(position))>0){
            holder.classteacher.setVisibility(View.VISIBLE);
        }else{
            holder.classteacher.setVisibility(View.GONE);
        }

        if(ratingList.get(position).equals("0")){
            holder.add_rating.setVisibility(View.VISIBLE);
            holder.rating.setVisibility(View.GONE);
        }else {
            holder.rating.setVisibility(View.VISIBLE);
            holder.rating.setRating(Float.parseFloat(ratingList.get(position)));
            holder.add_rating.setVisibility(View.GONE);
        }
        if(teacherContactList.get(position).equals("")){
            holder.contact_layout.setVisibility(View.GONE);
        }else {
            holder.contact_layout.setVisibility(View.VISIBLE);
        }
        if(teacheremailList.get(position).equals("")){
            holder.email_layout.setVisibility(View.GONE);
        }else {
            holder.email_layout.setVisibility(View.VISIBLE);
        }

        holder.viewdetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("ratingList=="+ratingList.get(position));
                showAddDialog(staff_idList.get(position));
            }
        });
        holder.contact.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                String phone = teacherContactList.get(position);
                System.out.println("PHONE=="+teacherContactList.get(position));
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
                context.startActivity(intent);
            }
        });
    }
    private void showAddDialog(String staff_id) {

        View view = context.getLayoutInflater().inflate(R.layout.teacher_detail_dialog, null);
        view.setMinimumHeight(500);

        RelativeLayout headerLay = (RelativeLayout)view.findViewById(R.id.addTask_dialog_header);
        RecyclerView recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        ImageView closeBtn = (ImageView) view.findViewById(R.id.addTask_dialog_crossIcon);

        params.put("class_id", Utility.getSharedPreferences(context.getApplicationContext(), Constants.classId));
        params.put("section_id", Utility.getSharedPreferences(context.getApplicationContext(), Constants.sectionId));
        params.put("staff_id", staff_id);
        JSONObject obj = new JSONObject(params);
        Log.e("params_teacher_subject ", obj.toString());
        getDataFromApi(obj.toString());
        adapter = new StudentTeacherDetailsAdapter(context.getApplicationContext(), idList, dayList, room_noList,
                subject_nameList, time_List);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(adapter);
        headerLay.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.primaryColour)));
        final BottomSheetDialog dialog = new BottomSheetDialog(context);

        dialog.setContentView(view);
        dialog.show();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }

    private void AddRating(final int position) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_rating_dialog);
        RelativeLayout headerLay = (RelativeLayout) dialog.findViewById(R.id.addTask_dialog_header);
        final RatingBar ratingbar = (RatingBar) dialog.findViewById(R.id.rating);
        final EditText comment = (EditText) dialog.findViewById(R.id.comment);
        TextView submit = (TextView) dialog.findViewById(R.id.submit);
        submit.setBackgroundColor(Color
                .parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.primaryColour)));
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
                if(rating.equals("")){
                    Toast.makeText(context.getApplicationContext(), "Rating Required !", Toast.LENGTH_LONG).show();
                }else{
                    params.put("rate", rating);
                    params.put("comment",comment.getText().toString());
                    params.put("staff_id", staff_idList.get(position));
                    params.put("user_id", Utility.getSharedPreferences(context.getApplicationContext(), Constants.userId));
                    JSONObject obj=new JSONObject(params);
                    Log.e("params ", obj.toString());
                    AddStaffRating(obj.toString());
                    dialog.dismiss();
                    Intent intent=new Intent(context.getApplicationContext(),StudentTeachersList.class);
                    context.startActivity(intent);
                }
            //  Toast.makeText(context.getApplicationContext(), "rating= "+rating+" comment= "+comment.getText().toString(), Toast.LENGTH_LONG).show();

            }
        });

        headerLay.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.primaryColour)));
        dialog.show();
    }

    private void getDataFromApi(String bodyParams) {
        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(context.getApplicationContext(), "apiUrl")+Constants.getTeacherSubjectUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    try {
                        Log.e(" ", result);
                        JSONObject obj = new JSONObject(result);
                        JSONArray dataArray = obj.getJSONArray("result_list");
                        idList.clear();
                        dayList.clear();
                        time_List.clear();
                        subject_nameList.clear();
                        room_noList.clear();
                        if(dataArray.length()!= 0) {
                            for(int i = 0; i < dataArray.length(); i++) {
                                idList.add(dataArray.getJSONObject(i).getString("id"));
                                dayList.add(dataArray.getJSONObject(i).getString("day"));
                                time_List.add(dataArray.getJSONObject(i).getString("time_from")+"-"+dataArray.getJSONObject(i).getString("time_to"));
                                subject_nameList.add(dataArray.getJSONObject(i).getString("subject_name")+" ("+dataArray.getJSONObject(i).getString("code")+")");
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
                Toast.makeText(context.getApplicationContext(),R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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

    private void AddStaffRating(String bodyParams){
            final String requestBody = bodyParams;
            String url = Utility.getSharedPreferences(context.getApplicationContext(), "apiUrl") + Constants.addStaffRatingUrl;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String result) {
                    if (result != null) {
                        try {
                            Log.e("Result", result);
                            JSONObject obj = new JSONObject(result);
                            String msg = obj.getString("msg");
                            Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
    public int getItemCount(){
        return teacherNameList.size();
    }
}

