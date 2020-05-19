package com.itscool.smartschool.adapters;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentHostel;
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

public class StudentHostelAdapter extends RecyclerView.Adapter<StudentHostelAdapter.MyViewHolder> {

    private StudentHostel context;
    private ArrayList<String> hostelIdList;
    private ArrayList<String> hostelNameList;
    ArrayList<String> roomTypeList = new ArrayList<String>();
    ArrayList<String> roomNumberList = new ArrayList<String>();
    ArrayList<String> roomCostList = new ArrayList<String>();
    ArrayList<String> no_of_bedList = new ArrayList<String>();
    ArrayList<String> student_idList = new ArrayList<String>();
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    StudentHostelDetailAdapter adapter;

    public StudentHostelAdapter(StudentHostel studentHostel, ArrayList<String> hostelIdList, ArrayList<String> hostelNameList) {
        this.context = studentHostel;
        this.hostelIdList = hostelIdList;
        this.hostelNameList = hostelNameList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView hostelNameTV;
        public RelativeLayout containerView;
        public LinearLayout viewBtn;

        public MyViewHolder(View view) {
            super(view);
            hostelNameTV = (TextView) view.findViewById(R.id.adapter_studentHostel_hostelNameTV);
            containerView = view.findViewById(R.id.adapter_studentHostel_cardView);
            viewBtn = view.findViewById(R.id.adapter_studentHostel_viewBtn);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_hostel, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        decorate(holder);

        holder.hostelNameTV.setText(hostelNameList.get(position));

        holder.containerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                params.put("hostelId", hostelIdList.get(position));
                params.put("student_id", Utility.getSharedPreferences(context.getApplicationContext(), "studentId"));
                JSONObject obj=new JSONObject(params);
                Log.e("params ", obj.toString());
                getDataFromApi(obj.toString(), hostelNameList.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return hostelIdList.size();
    }

    private void decorate (MyViewHolder holder) {
        holder.containerView.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));
    }

    private void getDataFromApi (String bodyParams, final String hostelName) {

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(context.getApplicationContext(), "apiUrl")+ Constants.getHostelDetailUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);

                        String success = object.getString("success");
                        if (success.equals("1")) {

                            String currency = Utility.getSharedPreferences(context.getApplicationContext(), Constants.currency);

                            JSONArray dataArray = object.getJSONArray("data");
                            Log.e("length", dataArray.length()+"..");

                            roomTypeList.clear();
                            roomNumberList.clear();
                            roomCostList.clear();
                            no_of_bedList.clear();
                            student_idList.clear();

                            for(int i = 0; i < dataArray.length(); i++) {
                                roomTypeList.add(dataArray.getJSONObject(i).getString("room_type"));
                                roomNumberList.add(dataArray.getJSONObject(i).getString("room_no"));
                                roomCostList.add(currency + dataArray.getJSONObject(i).getString("cost_per_bed"));
                                no_of_bedList.add(dataArray.getJSONObject(i).getString("no_of_bed"));
                                student_idList.add(dataArray.getJSONObject(i).getString("student_id"));
                            }

                            View view = context.getLayoutInflater().inflate(R.layout.fragment_hostel_bottom_sheet, null);

                            TextView headerTV = view.findViewById(R.id.hostel_bottomSheet_header);
                            ImageView crossBtn = view.findViewById(R.id.hostel_bottomSheet_crossBtn);

                            headerTV.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));
                            RecyclerView hostelListView = view.findViewById(R.id.hostel_bottomSheet_listview);
                            headerTV.setText(hostelName);

                            adapter = new StudentHostelDetailAdapter(context, roomTypeList, roomNumberList, roomCostList,no_of_bedList,student_idList);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context.getApplicationContext());
                            hostelListView.setLayoutManager(mLayoutManager);
                            hostelListView.setItemAnimator(new DefaultItemAnimator());
                            hostelListView.setAdapter(adapter);

                            final BottomSheetDialog dialog = new BottomSheetDialog(context);

                            dialog.setContentView(view);
                            dialog.show();

                            crossBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

                        } else {
                            Toast.makeText(context.getApplicationContext(), object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(context.getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(context, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
