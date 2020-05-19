package com.itscool.smartschool;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class AboutSchool extends BaseActivity {

    private TextView nameTV, addressTV, emailTV, phoneTV, schoolCodeTV, currentSessionTV, sessionStartMonthTV;
    private ImageView schoolLogoIV;
    private RelativeLayout nameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.about_school_activity, null, false);
        mDrawerLayout.addView(contentView, 0);

        titleTV.setText(getApplicationContext().getString(R.string.about));

        nameTV = findViewById(R.id.about_nameTV);
        addressTV = findViewById(R.id.about_addressTV);
        emailTV = findViewById(R.id.about_emailTV);
        phoneTV = findViewById(R.id.about_phoneTV);
        schoolLogoIV = findViewById(R.id.about_logoIV);
        schoolCodeTV = findViewById(R.id.about_schoolCodeTV);
        currentSessionTV = findViewById(R.id.about_currentSessionTV);
        sessionStartMonthTV = findViewById(R.id.about_sessionMonthTV);
        nameLayout = findViewById(R.id.about_nameLay);
        nameLayout.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.secondaryColour)));
        getDataFromApi();
    }

    private void getDataFromApi () {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+ Constants.getSchoolDetailsUrl;
        Log.e("URL", url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject data = new JSONObject(result);

                        nameTV.setText(data.getString("name"));
                        emailTV.setText(data.getString("email"));
                        phoneTV.setText(data.getString("phone"));
                        addressTV.setText(data.getString("address"));
                        schoolCodeTV.setText(data.getString("dise_code"));
                        currentSessionTV.setText(data.getString("session"));
                        sessionStartMonthTV.setText(data.getString("start_month_name"));

                        String logo = Utility.getSharedPreferences(getApplicationContext(), Constants.imagesUrl);
                        logo += "uploads/school_content/logo/";
                        logo += data.getString("image");

                        Picasso.with(getApplicationContext()).load(logo).fit().centerInside().into(schoolLogoIV);
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
                Toast.makeText(AboutSchool.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        };
        RequestQueue requestQueue = Volley.newRequestQueue(AboutSchool.this);//Creating a Request Queue
        requestQueue.add(stringRequest); //Adding request to the queue
    }
}
