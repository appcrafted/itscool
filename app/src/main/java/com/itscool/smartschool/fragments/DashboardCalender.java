package com.itscool.smartschool.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.DashboardBottomsheet;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.CustomCalendar;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
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

public class DashboardCalender extends Fragment implements CustomCalendar.RobotoCalendarListener {

    private CustomCalendar robotoCalendarView;
    RelativeLayout mainLay;
    Calendar calendar;
    String monthNo="";
    public int currentMonth=0, month = 0;
    JSONArray eventDetailsArray = new JSONArray();
    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    public List<String> taskDateList= new ArrayList<String>();
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_dashboard_calender, container, false);

        calendar = Calendar.getInstance();
        currentMonth = calendar.getTime().getMonth()+1;
        monthNo = String.valueOf(currentMonth);

        robotoCalendarView = (CustomCalendar) mainView.findViewById(R.id.robotoCalendarPicker);
        mainLay = (RelativeLayout) mainView.findViewById(R.id.attendance_mainLay);

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date begining = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date end = calendar.getTime();

        params.put("date_from", dateFormatter.format(begining));
        params.put("date_to", dateFormatter.format(end));
        params.put("student_id", Utility.getSharedPreferences(getActivity().getApplicationContext(), "studentId"));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());

        getDataFromApi(obj.toString());

        robotoCalendarView.setRobotoCalendarListener(this);
        robotoCalendarView.setShortWeekDays(false);
        robotoCalendarView.showDateTitle(true);
        robotoCalendarView.updateView();

        return mainView;
    }

    private void markDate() {
        try {
            if (taskDateList.size() != 0) {
                for (int a = 0; a < taskDateList.size(); a++) {
                    calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(getDayFromDate(taskDateList.get(a))));
                    robotoCalendarView.markCircleImage1(calendar);
                    Date date = calendar.getTime();
                    String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
                }
            }
        }catch(Exception e){
            Log.e("Mark date Exception 6", e.toString());
        }
    }

    @Override
    public void onDayClick(Calendar daySelectedCalendar) {
        //String strYear = daySelectedCalendar.getTime().getYear()+"";
        Date date=daySelectedCalendar.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate=dateFormat.format(date);
        showEventDetail(formattedDate);
    }

    private void showEventDetail(String dateSelected) {

        Log.e("Details Array ", eventDetailsArray.toString());

        ArrayList<String> eventIdList = new ArrayList<>();
        ArrayList<String> eventTitleList = new ArrayList<>();
        ArrayList<String> eventDateList = new ArrayList<>();
        ArrayList<String> eventStatusList = new ArrayList<>();
        ArrayList<String> eventTypeList = new ArrayList<>();
        ArrayList<String> eventDescList = new ArrayList<>();

        String defaultDateFormat = Utility.getSharedPreferences(getActivity().getApplicationContext(), "dateFormat");

        try {
            for (int i=0; i<eventDetailsArray.length(); i++) {
                String eventDateStr = eventDetailsArray.getJSONObject(i).getString("events_lists");
                List<String> eventDate = Arrays.asList(eventDateStr.split("\\s*,\\s*"));

                for (int j = 0; j<eventDate.size(); j++) {
                    if(eventDate.get(j).equals(dateSelected)) {

                        eventIdList.add(eventDetailsArray.getJSONObject(i).getString("id"));
                        eventTitleList.add(eventDetailsArray.getJSONObject(i).getString("event_title"));
                        eventTypeList.add(eventDetailsArray.getJSONObject(i).getString("event_type"));
                        eventDescList.add(eventDetailsArray.getJSONObject(i).getString("event_description"));

                        String startDate = Utility.parseDate("yyyy-MM-dd HH:mm:ss", defaultDateFormat, eventDetailsArray.getJSONObject(i).getString("start_date"));
                        String endDate = Utility.parseDate("yyyy-MM-dd HH:mm:ss", defaultDateFormat, eventDetailsArray.getJSONObject(i).getString("end_date"));

                        if(eventDetailsArray.getJSONObject(i).getString("event_type").equals("task")) {
                            eventDateList.add(startDate);
                        } else {
                            eventDateList.add(startDate + " - " + endDate);
                        }
                        eventStatusList.add(eventDetailsArray.getJSONObject(i).getString("is_active"));
                    }
                }
//                List<String> eventDate = Arrays.asList(eventDateStr.split("\\s*,\\s*"));
                Log.e("Date List", eventDate.toString());
            }
        }catch (Exception e) {
            Log.e("ERROR", e.toString());
        }

        Log.e("EVENT TITLE", eventTitleList.toString());

        if(eventIdList.size() != 0) {

            View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dashboard_bottom_sheet, null);
            view.setMinimumHeight(500);

            TextView headerTV = view.findViewById(R.id.dashboard_bottomSheet_header);
            ImageView crossBtn = view.findViewById(R.id.dashboard_bottomSheet_crossBtn);
            RecyclerView taskListView = view.findViewById(R.id.dashboard_bottomSheet_listview);

            headerTV.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getActivity().getApplicationContext(), Constants.secondaryColour)));

            headerTV.setText(getActivity().getString(R.string.taskAndEvents));

            DashboardBottomsheet adapter = new DashboardBottomsheet(getActivity(), eventIdList, eventTitleList,
                    eventStatusList, eventDateList, eventTypeList, eventDescList );

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
            taskListView.setLayoutManager(mLayoutManager);
            taskListView.setItemAnimator(new DefaultItemAnimator());
            taskListView.setAdapter(adapter);

            final BottomSheetDialog dialog = new BottomSheetDialog(getActivity());

            dialog.setContentView(view);
            dialog.show();

            crossBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onDayLongClick(Calendar daySelectedCalendar) {
        //Toast.makeText(getActivity(), "onDayLongClick: " + daySelectedCalendar.getTime(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRightButtonClick() {
        month++;
        calendar.add(Calendar.MONTH, 1);
        currentMonth = calendar.getTime().getMonth()+1;

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date begining = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date end = calendar.getTime();

        params.put("date_from", dateFormatter.format(begining));
        params.put("date_to", dateFormatter.format(end));
        params.put("student_id", Utility.getSharedPreferences(getActivity().getApplicationContext(), "studentId"));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }

    @Override
    public void onLeftButtonClick() {
        month--;
        calendar.add(Calendar.MONTH, -1);
        currentMonth = calendar.getTime().getMonth()+1;

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        Date begining = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date end = calendar.getTime();

        params.put("date_from", dateFormatter.format(begining));
        params.put("date_to", dateFormatter.format(end));
        params.put("student_id", Utility.getSharedPreferences(getActivity().getApplicationContext(), "studentId"));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());

        getDataFromApi(obj.toString());
    }

    private void getDataFromApi(String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getActivity().getApplicationContext(), "apiUrl")+ Constants.getDashboardUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result calender", result);
                        JSONObject object = new JSONObject(result);

                        //TODO success
                        String success = "200";//object.getString("status");
                        if (success.equals("200")) {
                            String dateList = object.getString("date_lists");
                            eventDetailsArray = object.getJSONArray("public_events");
                            taskDateList = Arrays.asList(dateList.split("\\s*,\\s*"));
                            markDate();
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    pd.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pd.dismiss();
                Log.e("Volley Error", volleyError.toString());
                Toast.makeText(getActivity(), R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(getActivity().getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getActivity().getApplicationContext(), "accessToken"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());//Creating a Request Queue
        requestQueue.add(stringRequest);//Adding request to the queue
    }

    public String getDayFromDate(String date) {
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat output = new SimpleDateFormat("dd");
        try {
            Date myDate = input.parse(date);// parse input
            String newDate = output.format(myDate);
            return newDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }
}
