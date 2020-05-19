package com.itscool.smartschool.students;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
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
import com.itscool.smartschool.BaseActivity;
import com.itscool.smartschool.R;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StudentHomeworkDetails extends BaseActivity {

    CardView summeryHeader;
    LinearLayout summeryLayout;
    ImageView summeryIndicator;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();
    TextView statusTV, homeworkDateTV, submissionDateTV, evaluationDateTV, subjectTV, createdByTV, evaluatedByTV;
    Button downloadBtn;
    WebView descWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_homework_details_activity, null, false);
        mDrawerLayout.addView(contentView, 0);

        titleTV.setText(getApplicationContext().getString(R.string.homework));

        summeryHeader = (CardView) findViewById(R.id.student_homeworkDetails_summeryCard);
        summeryLayout = (LinearLayout) findViewById(R.id.student_homeworkDetails_summeryLayout);
        summeryIndicator = (ImageView) findViewById(R.id.student_homeworkDetails_summeryIndicator);

        statusTV = findViewById(R.id.student_homeworkDetails_statusTV);
        homeworkDateTV = findViewById(R.id.student_homeworkDetails_homeworkDateTV);
        submissionDateTV = findViewById(R.id.student_homeworkDetails_submissionDateTV);
        evaluationDateTV = findViewById(R.id.student_homeworkDetails_evaluationDateTV);
        subjectTV = findViewById(R.id.student_homeworkDetails_subjectTV);
        createdByTV = findViewById(R.id.student_homeworkDetails_createdByTV);
        evaluatedByTV = findViewById(R.id.student_homeworkDetails_evaluatedByTV);

        downloadBtn = findViewById(R.id.student_homeworkDetails_downloadBtn);
        descWebview = findViewById(R.id.student_homeworkDetails_descriptionWV);

        summeryHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            if(summeryLayout.getVisibility() == View.VISIBLE ) {
                summeryLayout.setVisibility(View.GONE);
                summeryIndicator.setImageResource(R.drawable.ic_down_arrow_blue);
            } else {
                summeryLayout.setVisibility(View.VISIBLE);
                summeryIndicator.setImageResource(R.drawable.ic_up_arrow_blue);
            }

            }
        });

        params.put("homeworkId", getIntent().getStringExtra("homeworkId"));
        params.put("classId", Utility.getSharedPreferences(getApplicationContext(), "classId"));
        params.put("sectionId", Utility.getSharedPreferences(getApplicationContext(), "sectionId"));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());
    }

    private void getDataFromApi (String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getHomeworkUrl;
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
                            JSONObject data = dataArray.getJSONObject(0);

                            statusTV.setText(data.getString("status"));
                            homeworkDateTV.setText(data.getString("homework_date"));
                            submissionDateTV.setText(data.getString("submit_date"));
                            evaluationDateTV.setText(data.getString("evaluation_date"));
                            subjectTV.setText(data.getString("subjectName"));
                            createdByTV .setText(data.getString("created_by_name") +" "+ data.getString("created_by_surname"));
                            evaluatedByTV.setText(data.getString("evaluated_by_name") +" "+ data.getString("evaluated_by_surname"));

                            String desc = data.getString("description");
                            descWebview.loadData(desc, "text/html", "UTF-8");

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
                Toast.makeText(StudentHomeworkDetails.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getApplicationContext(), "accessToken"));
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentHomeworkDetails.this);//Creating a Request Queue
        requestQueue.add(stringRequest); //Adding request to the queue
    }
}
