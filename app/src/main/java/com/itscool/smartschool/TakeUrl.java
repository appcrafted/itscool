package com.itscool.smartschool;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.ClientError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.MyApp;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Locale;

public class TakeUrl extends Activity {
    EditText urlET;
    Button submitBtn;
    Locale myLocale;
    String langCode = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.take_url_activity);

        urlET = findViewById(R.id.et_domain_takeUrl);
        submitBtn = findViewById(R.id.btn_submit_takeUrl);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String appDomain = urlET.getText().toString();
               getDataFromApi(appDomain);
               Utility.setSharedPreference(getApplicationContext(), Constants.appDomain, appDomain);
               /* Utility.setSharedPreferenceBoolean(getApplicationContext(), "isUrlTaken", true);
               Utility.setSharedPreference(getApplicationContext(), Constants.apiUrl, "http://192.168.1.91/ss520api/");
                Utility.setSharedPreference(getApplicationContext(), Constants.imagesUrl, "http://192.168.1.91/ss520api/");//object.getString("system_url"));
                Utility.setSharedPreference(getApplicationContext(), Constants.appLogo, "https://upload.wikimedia.org/wikipedia/commons/thumb/2/2f/Logo_TV_2015.svg/1200px-Logo_TV_2015.svg.png"); //object.getString("system_url") + object.getString("app_logo"));
                Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, "#5b5b5b");
                Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, "#ffffff");
                Intent asd = new Intent(getApplicationContext(), Login.class);
                startActivity(asd);*/
            }
        });
    }

    public void setLocale(String localeName) {

        if(localeName.isEmpty() || localeName.equals("null")) {
            localeName = "en";
            Log.e("localName status", "empty");
        }
        Locale myLocale = new Locale(localeName);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Log.e("Status", "Locale updated!");
        Utility.setSharedPreferenceBoolean(getApplicationContext(), Constants.isLocaleSet, true);
        Utility.setSharedPreference(getApplicationContext(), Constants.currentLocale, localeName);
    }

     private void getDataFromApi (String domain) {
         final ProgressDialog pd = new ProgressDialog(this);
         pd.setMessage("Loading");
         pd.setCancelable(false);
         pd.show();

         if(!domain.endsWith("/")) {
             domain += "/";
         }

         final String url = domain+"app";
         Log.e("Verification Url", url);

         StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
             @Override
             public void onResponse(String result) {
                 Log.e("Result", result);
                 if (result != null) {
                     pd.dismiss();
                     try {
                         JSONObject object = new JSONObject(result);

                         String success = "200"; //object.getString("status");
                         if (success.equals("200")) {
                             Utility.setSharedPreferenceBoolean(getApplicationContext(), "isUrlTaken", true);
                             Utility.setSharedPreference(MyApp.getContext(), Constants.apiUrl, object.getString("url"));
                             Utility.setSharedPreference(MyApp.getContext(), Constants.imagesUrl, object.getString("site_url"));
                             String appLogo = object.getString("site_url") + "uploads/school_content/logo/app_logo/" + object.getString("app_logo");
                             Utility.setSharedPreference(MyApp.getContext(), Constants.appLogo, appLogo );

                             String secColour = object.getString("app_secondary_color_code");
                             String primaryColour = object.getString("app_primary_color_code");

                             if(secColour.length() == 7 && primaryColour.length() == 7) {
                                 Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, secColour);
                                 Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, primaryColour);
                             }else {
                                 Utility.setSharedPreference(getApplicationContext(), Constants.secondaryColour, Constants.defaultSecondaryColour);
                                 Utility.setSharedPreference(getApplicationContext(), Constants.primaryColour, Constants.defaultPrimaryColour);
                             }
                             Log.e("apiUrl Utility", Utility.getSharedPreferences(getApplicationContext(), "apiUrl"));
                             langCode = object.getString("lang_code");
                             Utility.setSharedPreference(getApplicationContext(), Constants.langCode,langCode);

                             if(!langCode.isEmpty()) {
                                 setLocale(langCode);
                             }
                             Intent asd = new Intent(getApplicationContext(), Login.class);
                             startActivity(asd);
                             finish();

                         } else {
                             Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                         }
                     } catch (JSONException e) {
                         langCode = "";
                         e.printStackTrace();
                     }
                 } else {
                     pd.dismiss();
                     Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                 }
             }
         }, new Response.ErrorListener() {
             @Override
             public void onErrorResponse(VolleyError error) {
                 pd.dismiss();
                 try {
                     int  statusCode = error.networkResponse.statusCode;
                     NetworkResponse response = error.networkResponse;
                     Log.e("Volley Error",""+statusCode+" "+response.data.toString());
                     if( error instanceof ClientError) {
                         Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                     } else {
                         Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                     }
                 } catch (NullPointerException npe) {
                     Toast.makeText(getApplicationContext(), "Invalid Domain.", Toast.LENGTH_SHORT).show();
                 }
             }
         });
         RequestQueue requestQueue = Volley.newRequestQueue(TakeUrl.this); //Creating a Request Queue
         requestQueue.add(stringRequest);//Adding request to the queue
     }
}
