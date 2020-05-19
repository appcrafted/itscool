package com.itscool.smartschool.adapters;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentAppyLeave;
import com.itscool.smartschool.students.StudentEditLeaveNew;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StudentApplyLeaveAdapter extends RecyclerView.Adapter<StudentApplyLeaveAdapter.MyViewHolder> {

    private StudentAppyLeave context;
    private ArrayList<String> nameList;
    private ArrayList<String> fromList;
    private ArrayList<String> toList;
    private ArrayList<String> statuslist;
    private ArrayList<String> idlist;
    private ArrayList<String> apply_datelist;
    private ArrayList<String> docslist;
    private ArrayList<String> reasonlist;
    private ArrayList<String> sapplylist;
    private ArrayList<String> sfromlist;
    private ArrayList<String> stolist;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();
    private boolean isapplyDateSelected = false;
    private boolean isfromDateSelected = false;
    private boolean istoDateSelected = false;
    String apply_date="";
    String from_date="";
    String to_date="";
    public String defaultDateFormat;
    long downloadID;

    public StudentApplyLeaveAdapter(StudentAppyLeave studentapplyleave, ArrayList<String> nameList, ArrayList<String> fromList,
                                    ArrayList<String> toList, ArrayList<String> statuslist, ArrayList<String> idlist,ArrayList<String> apply_datelist,ArrayList<String> docslist,
                                    ArrayList<String> reasonlist,ArrayList<String> sfromlist,ArrayList<String> stolist,ArrayList<String> sapplylist) {

        this.context = studentapplyleave;
        this.nameList = nameList;
        this.fromList = fromList;
        this.toList = toList;
        this.statuslist = statuslist;
        this.idlist = idlist;
        this.apply_datelist = apply_datelist;
        this.docslist = docslist;
        this.reasonlist = reasonlist;
        this.stolist = stolist;
        this.sfromlist = sfromlist;
        this.sapplylist = sapplylist;

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fromdate,todate,Pending,Approve,Applied_date;
        ImageView delete,edit,downloadBtn;
        RelativeLayout studentleave_headLayout;

        public MyViewHolder(View view) {
            super(view);
            fromdate = view.findViewById(R.id.fromdate);
            todate = view.findViewById(R.id.todate);
            Applied_date = view.findViewById(R.id.applied_date);
            studentleave_headLayout = view.findViewById(R.id.adapter_studentleave_headLayout);
            Approve = view.findViewById(R.id.Approve);
            delete = view.findViewById(R.id.delete);
            edit = view.findViewById(R.id.edit);
            downloadBtn = view.findViewById(R.id.downloadBtn);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_applyleavet, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.studentleave_headLayout.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));
        holder.fromdate.setText(fromList.get(position));
        holder.todate.setText(toList.get(position));
        holder.Applied_date.setText("Apply Date - "+apply_datelist.get(position));

       if(statuslist.get(position).equals("0")){
           holder.delete.setVisibility(View.VISIBLE);
           holder.edit.setVisibility(View.VISIBLE);
           holder.Approve.setText("Pending");
           holder.Approve.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.yellow_border));
       }else {
           holder.Approve.setText("Approved");
           holder.delete.setVisibility(View.GONE);
           holder.edit.setVisibility(View.GONE);
           holder.Approve.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_border));
       }

        if(docslist.get(position).equals("")){
            holder.downloadBtn.setVisibility(View.GONE);
        }else{
            holder.downloadBtn.setVisibility(View.VISIBLE);
        }

        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String urlStr = Utility.getSharedPreferences(context.getApplicationContext(), Constants.imagesUrl);
                urlStr += "uploads/student_leavedocuments/"+docslist.get(position);
                downloadID = Utility.beginDownload(context, docslist.get(position), urlStr);
            }
        });


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                builder.setCancelable(false);
                builder.setMessage(R.string.deleteMsg);
                builder.setTitle("");
                builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        params.put("leave_id", idlist.get(position));
                        JSONObject obj=new JSONObject(params);
                        Log.e("params ", obj.toString());
                        deleteDataFromApi(obj.toString());//Api Call
                        ((Activity)context).finish();

                        Intent intent = new Intent(context, StudentAppyLeave.class);
                        context.startActivity(intent);
                    }
                });
                builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        });
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context.getApplicationContext(), StudentEditLeaveNew.class);
                intent.putExtra("fromlist",sfromlist.get(position));
                intent.putExtra("tolist",stolist.get(position));
                intent.putExtra("applylist",sapplylist.get(position));
                intent.putExtra("reasonlist",reasonlist.get(position));
                intent.putExtra("idlist",idlist.get(position));
                context.startActivity(intent);
            }
        });
    }

    public BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.notification_logo)
                                .setContentTitle(context.getApplicationContext().getString(R.string.app_name))
                                .setContentText("All Download completed");


                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(455, mBuilder.build());

            }
        }
    };
    private void deleteDataFromApi (String bodyParams) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(context.getApplicationContext(), "apiUrl")+Constants.deleteLeaveUrl;
        Log.e("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);
                        String message = object.getString("msg");
                        pd.dismiss();
                        Toast.makeText(context.getApplicationContext()," "+message, Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(context); //Creating a Request Queue
        requestQueue.add(stringRequest);  //Adding request to the queue
    }

    private void showAddDialog(String fromList,String toList,String applylist,String reasonlist) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.add_leave_dialoug);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);

        RelativeLayout headerLay = (RelativeLayout) dialog.findViewById(R.id.addTask_dialog_header);
        RelativeLayout dateBtn = (RelativeLayout) dialog.findViewById(R.id.addTask_dialog_dateBtn);
        ImageView closeBtn = (ImageView) dialog.findViewById(R.id.addTask_dialog_crossIcon);


        final TextView applydate = dialog.findViewById(R.id.addLeave_dialog_apply_dateTV);
        final TextView fromdate = dialog.findViewById(R.id.addLeave_dialog_fromdateTV);
        final TextView todate = dialog.findViewById(R.id.addLeave_dialog_todateTV);
        final EditText reason = dialog.findViewById(R.id.reason);
        final Button upload_file = dialog.findViewById(R.id.upload_file);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        fromdate.setText(fromList);
        todate.setText(toList);
        applydate.setText(applylist);
        reason.setText(reasonlist);

        Button submitBtn = dialog.findViewById(R.id.addLeave_dialog_submitBtn);


        upload_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(context)
                        .withRequestCode(10)
                        .start();
            }
        });

        applydate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context.getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        apply_date=sdf.format(newDate.getTime());
                        SimpleDateFormat sdfdate = new SimpleDateFormat("dd-MM-yyyy");
                        applydate.setText(sdfdate.format(newDate.getTime()));
                        isapplyDateSelected=true;
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context.getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        from_date=sdf.format(newDate.getTime());
                        SimpleDateFormat sdfdate = new SimpleDateFormat("dd-MM-yyyy");
                        fromdate.setText(sdfdate.format(newDate.getTime()));
                        isfromDateSelected=true;
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(context.getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        to_date=sdf.format(newDate.getTime());
                        SimpleDateFormat sdfdate = new SimpleDateFormat("dd-MM-yyyy");
                        todate.setText(sdfdate.format(newDate.getTime()));
                        istoDateSelected=true;
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                }
        });
       /* closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });*/
        //headerLay.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.primaryColour)));
        submitBtn.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.primaryColour)));
        //DECORATE
        dialog.show();
    }
    @Override
    public int getItemCount() {
        return idlist.size();
    }
}