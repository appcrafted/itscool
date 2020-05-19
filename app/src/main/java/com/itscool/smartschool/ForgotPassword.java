package com.itscool.smartschool;

import android.app.ProgressDialog;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import static android.widget.Toast.makeText;

public class ForgotPassword extends AppCompatActivity {

    ImageView logoIV;
    EditText emailET;
    Button submitBtn;
    RadioGroup typeRadiogroup;
    RadioButton rb_Student, rb_Present;
    String loginType = "";
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String>  headers = new HashMap<String, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_activity);

        submitBtn = (Button)findViewById(R.id.btn_submit_fp);
        emailET = (EditText)findViewById(R.id.et_email_fp);
        typeRadiogroup = (RadioGroup)findViewById(R.id.fp_Rg);
        rb_Student = (RadioButton)findViewById(R.id.rb_Student_fp);
        rb_Present = (RadioButton)findViewById(R.id.rb_Parent_fp);
        logoIV = findViewById(R.id.fp_logo);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.textHeading));
        }
        String appLogo = Utility.getSharedPreferences(this, Constants.appLogo)+"?"+new Random().nextInt(11);
        Log.e("appLogo", appLogo);
        Picasso.with(getApplicationContext()).load(appLogo).into(logoIV);

        typeRadiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId != -1) {
                    RadioButton rbValue = (RadioButton)findViewById(checkedId);
                    loginType = (rbValue.getText()).toString();
                    if(loginType.equals("Student")){
                        rb_Student.setChecked(true);
                    }else if(loginType.equals("Present")){
                        rb_Present.setChecked(true);
                    }
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailId = emailET.getText().toString().trim();

                if (emailId.length() == 0) {
                    makeText(getApplicationContext(),"Please enter registred email id", Toast.LENGTH_SHORT).show();
                } else if (loginType.equals("")) {
                    makeText(getApplicationContext(), "Please select login type", Toast.LENGTH_SHORT).show();
                } else {
                    if (Utility.isConnectingToInternet(ForgotPassword.this)) {
                        params.put("email", emailId);
                        params.put("usertype", loginType.toLowerCase());
                        params.put("site_url", Utility.getSharedPreferences(getApplicationContext(), Constants.imagesUrl));
                        JSONObject obj=new JSONObject(params);
                        Log.e("params ", obj.toString());

                        getDataFromApi(obj.toString());
                    } else {
                        makeText(getApplicationContext(),"Please connect to internet and try again", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void getDataFromApi (String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.forgotPasswordUrl;
        Log.e("Forgot Password Url", url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);

                        String success = object.getString("status");
                        if (success.equals("200")) {
                            Toast.makeText(getApplicationContext(),object.getString("message"),Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(),object.getString("message"),Toast.LENGTH_SHORT).show();
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
                Log.e("Volley Error 1", volleyError.toString());
                Toast.makeText(ForgotPassword.this, R.string.invalidUsername, Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", "smartschool");
                headers.put("Auth-Key", "schoolAdmin@");
                headers.put("Content-Type", "application/json");
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
        //SETTING RETRY Policy
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(ForgotPassword.this);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
