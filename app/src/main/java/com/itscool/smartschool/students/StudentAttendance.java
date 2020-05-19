package com.itscool.smartschool.students;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
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
import com.itscool.smartschool.adapters.StudentSubjectAttendenceAdapter;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.CustomCalendar;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class StudentAttendance extends BaseActivity implements CustomCalendar.RobotoCalendarListener {

    private CustomCalendar robotoCalendarView;
    RelativeLayout mainLay;
    LinearLayout no_data_layout,linear_layout,heading_layout;
    Calendar calendar;
    RecyclerView recyclerview;
    LinearLayout subjectwise_layout;
    String monthNo="",attendate="";
    TextView attendence_date;
    public int currentMonth=0, month = 0;
    private boolean isattenDateSelected = false;
    StudentSubjectAttendenceAdapter adapter;
    public List<String> dateListPresent1= new ArrayList<String>();
    public List<String> dateListAbsent1= new ArrayList<String>();
    public List<String> dateListLeave1= new ArrayList<String>();
    public List<String> dateListHalf1= new ArrayList<String>();
    public List<String> dateListHoliday1= new ArrayList<String>();
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    ArrayList<String> subjectList = new ArrayList<String>();
    ArrayList <String> time_toList = new ArrayList<String>();
    ArrayList <String> timeList = new ArrayList<String>();
    ArrayList <String> roomList = new ArrayList<String>();
    ArrayList <String> typeList = new ArrayList<String>();
    ArrayList <String> remarkList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_attendance_activity, null, false);
        mDrawerLayout.addView(contentView, 0);

        titleTV.setText(getApplicationContext().getString(R.string.attendance));

        calendar = Calendar.getInstance();
        currentMonth = calendar.getTime().getMonth()+1;
        monthNo = String.valueOf(currentMonth);

        robotoCalendarView = (CustomCalendar) findViewById(R.id.robotoCalendarPicker);
        mainLay = (RelativeLayout) findViewById(R.id.attendance_mainLay);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        subjectwise_layout = (LinearLayout) findViewById(R.id.subjectwise_layout);
        no_data_layout = (LinearLayout) findViewById(R.id.nodata_layout);
        linear_layout = (LinearLayout) findViewById(R.id.linear_layout);
        heading_layout = (LinearLayout) findViewById(R.id.heading_layout);
        heading_layout.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.secondaryColour)));
        attendence_date = (TextView) findViewById(R.id.attendence_date);
        attendence_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StudentAttendance.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        attendate=sdf.format(newDate.getTime());
                        SimpleDateFormat sdfdate = new SimpleDateFormat("dd-MM-yyyy");
                        attendence_date.setText(sdfdate.format(newDate.getTime()));
                        isattenDateSelected=true;

                        params.put("year", "");
                        params.put("month", "");
                        params.put("student_id", Utility.getSharedPreferences(getApplicationContext(), "studentId"));
                        params.put("date", attendate);
                        JSONObject obj=new JSONObject(params);
                        Log.e("params_Attendence ", obj.toString());
                        getDataFromApi(obj.toString());
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        String currentYear = ""+calendar.getTime().getYear();
        currentYear = "20"+currentYear.substring(1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());

        SimpleDateFormat sdfd = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdfd.format(new Date());
        attendence_date.setText(currentDate);

        params.put("year", currentYear);
        params.put("month", currentMonth+"");
        params.put("student_id", Utility.getSharedPreferences(getApplicationContext(), "studentId"));
        params.put("date", currentDateandTime);
        JSONObject obj=new JSONObject(params);
        Log.e("params_Attendence ", obj.toString());
        getDataFromApi(obj.toString());

        adapter = new StudentSubjectAttendenceAdapter(StudentAttendance.this,subjectList, timeList, roomList,typeList,remarkList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerview.setLayoutManager(mLayoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());
        recyclerview.setAdapter(adapter);

        // Set listener, in this case, the same activity
        robotoCalendarView.setRobotoCalendarListener(this);
        robotoCalendarView.setShortWeekDays(false);
        robotoCalendarView.showDateTitle(true);
        robotoCalendarView.updateView();
    }
    private void markDate() {
        Log.e("Status", "Entering markDate()");
        try {
            try {
                if (dateListPresent1.size() != 0) {
                    for (int a = 0; a < dateListPresent1.size(); a++) {
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateListPresent1.get(a)));
                        robotoCalendarView.markCircleImage1(calendar);
                        Date date = calendar.getTime();
                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
                        Log.e("DaTE", formattedDate+".");
                    }
                }
            } catch (Exception e) {
                Log.e("Mark date Exception 1", e.toString());
            }
            try {
                if (dateListAbsent1.size() != 0) {
                    for (int i = 0; i < dateListAbsent1.size(); i++) {
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateListAbsent1.get(i)));
                        robotoCalendarView.markCircleImage2(calendar);
                        Log.e("absent date", dateListAbsent1.get(i)+".");
                    }
                }
            } catch (Exception e) {
                Log.e("Mark date Exception 2", e.toString());
            }
            try {
                if (dateListLeave1.size() != 0) {
                    for (int b = 0; b < dateListLeave1.size(); b++) {
                        Log.e("dateListLeave1 markDate", dateListLeave1.get(b));
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateListLeave1.get(b)));
                        robotoCalendarView.markCircleImage3(calendar);
                    }
                }
            } catch (Exception e) {
                Log.e("Mark date Exception 3", e.toString());
            }
            try {
                if (dateListHalf1.size() != 0) {
                    for (int b = 0; b < dateListHalf1.size(); b++) {
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateListHalf1.get(b)));
                        robotoCalendarView.markCircleImage4(calendar);
                    }
                }
            } catch (Exception e) {
                Log.e("Mark date Exception 4", e.toString());
            }
            try {
                if (dateListHoliday1.size() != 0) {
                    for (int b = 0; b < dateListHoliday1.size(); b++) {
                        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateListHoliday1.get(b)));
                        robotoCalendarView.markCircleImage5(calendar);
                    }
                }
            } catch (Exception e) {
                Log.e("Mark date Exception 5", e.toString());
            }
        }catch(Exception e){
            Log.e("Mark date Exception 6", e.toString());
        }
    }
    @Override
    public void onDayClick(Calendar daySelectedCalendar) {
        String strYear = daySelectedCalendar.getTime().getYear()+"";
        String currentDate = 20+strYear.substring(1)+"-"+(daySelectedCalendar.getTime().getMonth()+1)+"-"+daySelectedCalendar.getTime().getDate();
    }
    @Override
    public void onDayLongClick(Calendar daySelectedCalendar) {
//        Toast.makeText(this, "onDayLongClick: " + daySelectedCalendar.getTime(), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onRightButtonClick() {
        month++;
        calendar.add(Calendar.MONTH, 1);
        currentMonth = calendar.getTime().getMonth()+1;
        String currentYear = ""+calendar.getTime().getYear();
        currentYear = "20"+currentYear.substring(1);
        params.put("year", currentYear);
        params.put("month", currentMonth+"");
        params.put("student_id", Utility.getSharedPreferences(getApplicationContext(), "studentId"));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }
    @Override
    public void onLeftButtonClick() {
        month--;
        calendar.add(Calendar.MONTH, -1);
        currentMonth = calendar.getTime().getMonth()+1;
        String currentYear = ""+calendar.getTime().getYear();
        currentYear = "20"+currentYear.substring(1);
        params.put("year", currentYear);
        params.put("month", currentMonth+"");
        params.put("student_id", Utility.getSharedPreferences(getApplicationContext(), "studentId"));
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
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getAttendanceUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {

                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);

                        if(object.getString("attendence_type").equals("0")) {
                            subjectwise_layout.setVisibility(View.GONE);
                            mainLay.setVisibility(View.VISIBLE);

                            ArrayList<String> presentDates = new ArrayList<>();
                            ArrayList<String> lateDates = new ArrayList<>();
                            ArrayList<String> holidayDates = new ArrayList<>();
                            ArrayList<String> absentDates = new ArrayList<>();
                            ArrayList<String> halfDayDates = new ArrayList<>();
                            ArrayList<String> excussLateDates = new ArrayList<>();

                            JSONArray dataArray = object.getJSONArray("data");
                            if (dataArray.length() != 0) {
                                for (int i = 0; i < dataArray.length(); i++) {
                                    if (dataArray.getJSONObject(i).getString("type").equals("Present")) {
                                        presentDates.add(getDayFromDate(dataArray.getJSONObject(i).getString("date")));
                                    } else if (dataArray.getJSONObject(i).getString("type").equals("Late")) {
                                        lateDates.add(getDayFromDate(dataArray.getJSONObject(i).getString("date")));
                                    } else if (dataArray.getJSONObject(i).getString("type").equals("Holiday")) {
                                        holidayDates.add(getDayFromDate(dataArray.getJSONObject(i).getString("date")));
                                    } else if (dataArray.getJSONObject(i).getString("type").equals("Absent")) {
                                        absentDates.add(getDayFromDate(dataArray.getJSONObject(i).getString("date")));
                                    } else if (dataArray.getJSONObject(i).getString("type").equals("Half Day")) {
                                        halfDayDates.add(getDayFromDate(dataArray.getJSONObject(i).getString("date")));
                                    } else if (dataArray.getJSONObject(i).getString("type").equals("Late With Excuse")) {
                                        excussLateDates.add(getDayFromDate(dataArray.getJSONObject(i).getString("date")));
                                    }
                                }
                                Log.e("presentDates", presentDates.toString());

                                String presentDateString = presentDates.toString().substring(1);
                                presentDateString = presentDateString.substring(0, presentDateString.length() - 1);

                                String lateDateString = lateDates.toString().substring(1);
                                lateDateString = lateDateString.substring(0, lateDateString.length() - 1);

                                String holidayDateString = holidayDates.toString().substring(1);
                                holidayDateString = holidayDateString.substring(0, holidayDateString.length() - 1);

                                String absentDateString = absentDates.toString().substring(1);
                                absentDateString = absentDateString.substring(0, absentDateString.length() - 1);

                                String halfDayDateString = halfDayDates.toString().substring(1);
                                halfDayDateString = halfDayDateString.substring(0, halfDayDateString.length() - 1);

                                String excussLateDateString = excussLateDates.toString().substring(1);
                                excussLateDateString = excussLateDateString.substring(0, excussLateDateString.length() - 1);

                                dateListPresent1 = Arrays.asList(presentDateString.split("\\s*, \\s*"));
                                dateListAbsent1 = Arrays.asList(absentDateString.split("\\s*, \\s*"));
                                dateListLeave1 = Arrays.asList(lateDateString.split("\\s*, \\s*"));
                                dateListHalf1 = Arrays.asList(halfDayDateString.split("\\s*, \\s*"));
                                dateListHoliday1 = Arrays.asList(holidayDateString.split("\\s*, \\s*"));

                                Log.e("present date list", dateListPresent1.toString() + ".");
                                Log.e("absent date list", dateListAbsent1.toString() + ".");
                                Log.e("late date list", dateListLeave1.toString() + ".");
                                Log.e("half date list", dateListHalf1.toString() + ".");
                                Log.e("holiday date list", dateListHoliday1.toString() + ".");
                                markDate();
                            } else {
                                Toast.makeText(getApplicationContext(), object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                            }
                        }else if(object.getString("attendence_type").equals("1")){

                            subjectwise_layout.setVisibility(View.VISIBLE);
                            mainLay.setVisibility(View.GONE);
                            JSONArray dataArray = object.getJSONArray("data");

                            subjectList.clear();
                            timeList.clear();
                            roomList.clear();
                            time_toList.clear();
                            typeList.clear();

                            if(dataArray.length()>0){
                                linear_layout.setVisibility(View.VISIBLE);
                                no_data_layout.setVisibility(View.GONE);
                               for(int i=0; i<dataArray.length(); i++) {
                                   JSONObject dataObject = dataArray.getJSONObject(i);
                                   subjectList.add(dataObject.getString("name")+"("+dataObject.getString("code")+")");
                                   timeList.add(dataObject.getString("time_from")+"-"+dataObject.getString("time_to"));
                                   roomList.add(dataObject.getString("room_no"));
                                   typeList.add(dataObject.getString("type"));
                                   remarkList.add(dataObject.getString("remark"));
                               }
                            }else{
                                linear_layout.setVisibility(View.GONE);
                                no_data_layout.setVisibility(View.VISIBLE);
                            }
                            adapter.notifyDataSetChanged();
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
                Toast.makeText(StudentAttendance.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentAttendance.this); //Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
    }

    public String getDayFromDate(String date) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd");
        try {
            Date myDate = input.parse(date);                 // parse input
            String newDate = output.format(myDate);
            return newDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}