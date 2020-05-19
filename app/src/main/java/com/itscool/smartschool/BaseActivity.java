package com.itscool.smartschool;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
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
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class BaseActivity extends AppCompatActivity {

    public LinearLayout libraryBtn;
    public ImageView backBtn;
    public TextView titleTV;

    protected FrameLayout mDrawerLayout, actionBar;

    public Map<String, String>  headers = new HashMap<String, String>();
    public Map<String, String> params = new Hashtable<String, String>();
    public String defaultDateFormat, currency;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.base_activity);

        backBtn = findViewById(R.id.actionBar_backBtn);
        mDrawerLayout = findViewById(R.id.container);
        actionBar = findViewById(R.id.actionBarSecondary);
        titleTV = findViewById(R.id.actionBar_title);
        libraryBtn = findViewById(R.id.baseActivity_libraryBtn);

        defaultDateFormat = Utility.getSharedPreferences(getApplicationContext(), "dateFormat");
        currency = Utility.getSharedPreferences(getApplicationContext(), Constants.currency);

        params.put("site_url", Utility.getSharedPreferences(getApplicationContext(), Constants.imagesUrl));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());

        decorate();

        Utility.setLocale(getApplicationContext(), Utility.getSharedPreferences(getApplicationContext(), Constants.langCode));

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void decorate() {
        actionBar.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        }

    }

    private void logoutApi () {

        final ProgressDialog pd = new ProgressDialog(BaseActivity.this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();


        String url = Utility.getSharedPreferences(BaseActivity.this, "apiUrl")+ Constants.logoutUrl;
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
                            Utility.setSharedPreferenceBoolean(getApplicationContext(), "isLoggegIn", false);
                            Intent logout = new Intent(BaseActivity.this, Login.class);
                            logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            logout.putExtra("EXIT", true);
                            startActivity(logout);
                            finish();
                        } else {
                            Intent intent=new Intent(BaseActivity.this,TakeUrl.class);
                            startActivity(intent);
                           // Toast.makeText(BaseActivity.this, object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(BaseActivity.this, R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        pd.dismiss();
                        Log.e("Volley Error", volleyError.toString());
                        Toast.makeText(BaseActivity.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(BaseActivity.this, "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(BaseActivity.this, "accessToken"));
                Log.e("Headers", headers.toString());
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(BaseActivity.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getDataFromApi(String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        try {

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Utility.decryptMsg(Utility.encryptMsg(Utility.generateKey()), Utility.generateKey() ), new Response.Listener<String>() {
                @Override
                public void onResponse(String result) {
                    if (result != null) {
                        pd.dismiss();
                        try {

                            JSONObject object = new JSONObject(result);

                            if(object.getString("status").equals("0")) {
                                Utility.setSharedPreferenceBoolean(getApplicationContext(), Constants.isLoggegIn, false);

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(BaseActivity.this);
                                builder.setCancelable(false);
                                builder.setMessage(R.string.verificationMessage);
                                builder.setTitle("");
                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        logoutApi();
                                    }
                                });

                                android.app.AlertDialog alert = builder.create();
                                alert.show();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        pd.dismiss();
                        Toast.makeText(getApplicationContext(), R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                    }
                }
            },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            pd.dismiss();
                            Log.e("Volley Error", volleyError.toString());
                            Toast.makeText(BaseActivity.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
            //Creating a Request Queue
            RequestQueue requestQueue = Volley.newRequestQueue(BaseActivity.this);

            //Adding request to the queue
            requestQueue.add(stringRequest);


        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | InvalidParameterSpecException |
                IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException  exp) {
            Log.e("ENCRYPTION", exp.toString());
        }






    }

}
