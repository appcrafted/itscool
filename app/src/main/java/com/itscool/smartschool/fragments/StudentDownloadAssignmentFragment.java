package com.itscool.smartschool.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
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
import com.itscool.smartschool.adapters.StudentDownloadsAdapter;
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


public class StudentDownloadAssignmentFragment extends Fragment {

    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    ListView listView;

    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList <String> dateList = new ArrayList<String>();
    ArrayList <String> descList = new ArrayList<String>();
    ArrayList <String> urlList = new ArrayList<String>();
    StudentDownloadsAdapter adapter;

    public StudentDownloadAssignmentFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        params.put("tag", "Assignments");
        params.put("classId", Utility.getSharedPreferences(getActivity().getApplicationContext(), "classId"));
        params.put("sectionId", Utility.getSharedPreferences(getActivity().getApplicationContext(), "sectionId"));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());

        getDataFromApi(obj.toString());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_student_download_assignment, container, false);

        listView = (ListView) mainView.findViewById(R.id.fragment_studentDownload_assignment_listview);
        adapter = new StudentDownloadsAdapter(getActivity(), idList, nameList, dateList, urlList, descList);
        listView.setAdapter(adapter);

        return mainView;
    }

    private void getDataFromApi (String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(getActivity());
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getActivity().getApplicationContext(), "apiUrl")+Constants.getDownloadsLinksUrl;
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

                            JSONArray dataArray = object.getJSONArray("data");
                            Log.e("length", dataArray.length()+"..");
                            for(int i = 0; i < dataArray.length(); i++) {
                                idList.add(dataArray.getJSONObject(i).getString("id"));
                                nameList.add(dataArray.getJSONObject(i).getString("title"));
                                dateList.add(Utility.parseDate("yyyy-MM-dd HH:mm:ss", Utility.getSharedPreferences(getActivity().getApplicationContext(), "dateFormat"), dataArray.getJSONObject(i).getString("created_at")));
                                descList.add(dataArray.getJSONObject(i).getString("note"));
                                urlList.add(dataArray.getJSONObject(i).getString("file"));
                            }
                            adapter.notifyDataSetChanged();
                        } else {
//                            Toast.makeText(getActivity().getApplicationContext(), object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
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
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

}
