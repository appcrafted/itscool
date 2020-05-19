package com.itscool.smartschool.adapters;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.itscool.smartschool.students.StudentTransportRoutes;
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

public class StudentTransportRouteAdapter extends BaseAdapter {

    private StudentTransportRoutes context;
    private ArrayList<String> routeNameList;
    ArrayList<String> vehicleArray;

    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();
    public StudentTransportRouteAdapter(StudentTransportRoutes studentTransportRoutes,
                                        ArrayList<String> routeNameList, ArrayList<String> vehicleArray) {

        this.context = studentTransportRoutes;
        this.routeNameList = routeNameList;
        this.vehicleArray = vehicleArray;
    }
    @Override
    public int getCount() {
        return routeNameList.size();
    }
    @Override
    public Object getItem(int i) {
        return null;
    }
    @Override
    public long getItemId(int i) {
        return 0;
    }
    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;

        if(null==view) {

            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.adapter_student_transport, viewGroup, false);
            viewHolder = new ViewHolder();

            viewHolder.nameHeader = view.findViewById(R.id.studentTransportAdapter_nameHeader);
            viewHolder.vehicleTable = view.findViewById(R.id.studentTransportAdapter_vehicleTable);
            viewHolder.routeNameTV = (TextView) view.findViewById(R.id.studentTransportAdapter_routeNameTV);
            viewHolder.routeNameTV.setTag(position);

        }else{
            viewHolder  = (ViewHolder) view.getTag();
        }

        viewHolder.routeNameTV.setText(routeNameList.get(position));

        viewHolder.nameHeader.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));

        Log.e("DATA", vehicleArray.toString());

        try {
            final JSONArray dataArray = new JSONArray(vehicleArray.get(position));

            for(int i=0; i<dataArray.length(); i++) {

                final TableRow tr = (TableRow) context.getLayoutInflater().inflate(R.layout.adapter_student_transport_vehicle, null);

                TextView vehicleTV,assigned;
                LinearLayout viewBtn;

                viewBtn = tr.findViewById(R.id.studentTransportAdapter_detailsBtn);
                vehicleTV = (TextView) tr.findViewById(R.id.studentTransportAdapter_vehicleTV);
                assigned = (TextView) tr.findViewById(R.id.assigned);

                vehicleTV.setText(dataArray.getJSONObject(i).getString("vehicle_no"));

                if(dataArray.getJSONObject(i).getString("assigned").equals("yes")) {
                    viewBtn.setVisibility(View.VISIBLE);
                    assigned.setVisibility(View.VISIBLE);
                } else {
                    viewBtn.setVisibility(View.GONE);
                    assigned.setVisibility(View.GONE);
                }

                final String vehicleId = dataArray.getJSONObject(i).getString("id");
                final String vehicleName = dataArray.getJSONObject(i).getString("vehicle_no");

                viewBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        params.put("vehicleId", vehicleId); // vehicleId.get(position));
                        JSONObject obj=new JSONObject(params);
                        Log.e("params ", obj.toString());

                        getDataFromApi(obj.toString(), vehicleName);
                    }
                });

                viewHolder.vehicleTable.addView(tr);

                context.registerForContextMenu(tr);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        view.setTag(viewHolder);
        return view;
    }

    static class ViewHolder {
        private TextView routeNameTV;
        private LinearLayout nameHeader;
        private TableLayout vehicleTable;
    }

    private void getDataFromApi (String bodyParams, final String routeName) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(context.getApplicationContext(),"apiUrl")+ Constants.getTransportVehicleDetailsUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONArray array = new JSONArray(result);
                        JSONObject object = array.getJSONObject(0);

                            View view = context.getLayoutInflater().inflate(R.layout.fragment_transport_route_bottom_sheet, null);

                            TextView headerTV = view.findViewById(R.id.transportRoute_bottomSheet_header);
                            headerTV.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));
                            headerTV.setText(routeName);

                            TextView vehicleNoTV = view.findViewById(R.id.transportRoute_bottomSheet_vehicleNo);
                            TextView vehicleModelTV = view.findViewById(R.id.transportRoute_bottomSheet_vehicleModel);
                            TextView vehicleMadeTV = view.findViewById(R.id.transportRoute_bottomSheet_vehicleMade);
                            TextView driverNameTV = view.findViewById(R.id.transportRoute_bottomSheet_driverName);
                            TextView driverLicenceTV = view.findViewById(R.id.transportRoute_bottomSheet_driverLicence);
                            TextView driverContactTV = view.findViewById(R.id.transportRoute_bottomSheet_driverContact);
                            ImageView crossBtn = view.findViewById(R.id.transportRoute_bottomSheet_crossBtn);

                            vehicleNoTV.setText(object.getString("vehicle_no"));
                            vehicleModelTV.setText(object.getString("vehicle_model"));
                            vehicleMadeTV.setText(object.getString("manufacture_year"));
                            driverNameTV.setText(object.getString("driver_name"));
                            driverLicenceTV.setText(object.getString("driver_licence"));
                            driverContactTV.setText(object.getString("driver_contact"));

                            final BottomSheetDialog dialog = new BottomSheetDialog(context);
                            dialog.setContentView(view);
                            dialog.show();

                            crossBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.dismiss();
                                }
                            });

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
