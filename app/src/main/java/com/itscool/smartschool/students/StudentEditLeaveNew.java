package com.itscool.smartschool.students;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentApplyLeaveAdapter;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import com.itscool.smartschool.utils.VolleyMultipartRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class StudentEditLeaveNew extends AppCompatActivity {
    public ImageView backBtn;
    public String defaultDateFormat, currency;
    RecyclerView recyclerView;
    LinearLayout nodata_layout;
    TextView add_leave;
    private static final String IMAGE_DIRECTORY = "/demonuts";
    StudentApplyLeaveAdapter adapter;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();
    SwipeRefreshLayout pullToRefresh;
    public TextView titleTV;
    protected FrameLayout mDrawerLayout, actionBar;
    String applydate = "";
    String fromdate = "";
    String filePath;
    ProgressDialog progress;
    private static final int SELECT_PICTURE = 100;
    String todate = "";
    private boolean isapplyDateSelected = false;
    private boolean isfromDateSelected = false;
    private boolean istoDateSelected = false;
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> fromList = new ArrayList<String>();
    ArrayList<String> toList = new ArrayList<String>();
    ArrayList<String> statuslist = new ArrayList<String>();
    ArrayList<String> idlist = new ArrayList<String>();
    ArrayList<String> apply_datelist = new ArrayList<String>();
    private final int IMG_REQUEST = 1;
    Bitmap bitmap;
    Button  submit;
    ProgressBar progressBar;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView upload_image;
    String path;
    EditText reason;
    Uri uri;
    private static final String TAG = "StudentAddLeave";
    private String PathHolder = "";
    TextView todateTV, apply_dateTV, fromdateTV;
    private static final int READ_REQUEST_CODE = 42;
    String fromlist,tolist,applylist,reasonlist,leaveid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.uploadfile);
        backBtn = findViewById(R.id.actionBar_backBtn);
        mDrawerLayout = findViewById(R.id.container);
        actionBar = findViewById(R.id.actionBarSecondary);
        titleTV = findViewById(R.id.actionBar_title);

        defaultDateFormat = Utility.getSharedPreferences(getApplicationContext(), "dateFormat");
        currency = Utility.getSharedPreferences(getApplicationContext(), Constants.currency);

        decorate();
        Utility.setLocale(getApplicationContext(), Utility.getSharedPreferences(getApplicationContext(), Constants.langCode));

        Bundle bundle = getIntent().getExtras();
         fromlist = bundle.getString("fromlist");
         tolist = bundle.getString("tolist");
         applylist = bundle.getString("applylist");
         reasonlist = bundle.getString("reasonlist");
        leaveid = bundle.getString("idlist");

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleTV.setText(getApplicationContext().getString(R.string.editleave));
        apply_dateTV = findViewById(R.id.addLeave_dialog_apply_dateTV);
        fromdateTV = findViewById(R.id.addLeave_dialog_fromdateTV);
        todateTV = findViewById(R.id.addLeave_dialog_todateTV);
        reason = findViewById(R.id.reason);
        submit = findViewById(R.id.addLeave_dialog_submitBtn);
        upload_image = (ImageView) findViewById(R.id.upload_image);

        fromdateTV.setText(fromlist);
        apply_dateTV.setText(applylist);
        todateTV.setText(tolist);
        reason.setText(reasonlist);

        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(StudentEditLeaveNew.this)
                        .withRequestCode(10)
                        .start();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadBitmap();
            }
        });

        apply_dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StudentEditLeaveNew.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        applydate=sdf.format(newDate.getTime());
                        SimpleDateFormat sdfdate = new SimpleDateFormat("dd-MM-yyyy");
                        apply_dateTV.setText(applydate);
                        isapplyDateSelected=true;
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        fromdateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StudentEditLeaveNew.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        fromdate=sdf.format(newDate.getTime());
                        SimpleDateFormat sdfdate = new SimpleDateFormat("dd-MM-yyyy");
                        fromdateTV.setText(fromdate);
                        isfromDateSelected=true;
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        todateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StudentEditLeaveNew.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        todate=sdf.format(newDate.getTime());
                        SimpleDateFormat sdfdate = new SimpleDateFormat("dd-MM-yyyy");
                        todateTV.setText(todate);
                        istoDateSelected=true;
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.
                    READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},100);
                return;

            }
        }
        enablebutton();
    }

    private void decorate() {
        actionBar.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        }
    }
    public void enablebutton(){

        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    new MaterialFilePicker()
                            .withActivity(StudentEditLeaveNew.this)
                            .withRequestCode(10)
                            .start();

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==100 && (grantResults[0]==PackageManager.PERMISSION_GRANTED)){
            enablebutton();
        }else{
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.updateLeaveUrl;
        if (requestCode == 10 && resultCode == RESULT_OK) {

            progress=new ProgressDialog(StudentEditLeaveNew.this);
            progress.setTitle("uploading");
            progress.setMessage("Please Wait....");
            progress.show();

           Thread t=new Thread(new Runnable() {
               @Override
               public void run() {
                   File f=new File(data.getStringExtra(FilePickerActivity.RESULT_FILE_PATH));
                   String content_type=getMimeType(f.getPath());

                   String file_path=f.getAbsolutePath();
                   OkHttpClient client=new OkHttpClient();
                  // RequestBody file_body=RequestBody.create(MediaType.parse(content_type),f);
                   String mimeType= URLConnection.guessContentTypeFromName(f.getName());
                   RequestBody file_body = RequestBody.create(MediaType.parse(mimeType),f);

                   RequestBody requestBody=new MultipartBody.Builder()
                           .setType(MultipartBody.FORM)
                           .addFormDataPart("file",file_path.substring(file_path.lastIndexOf("/")+1),file_body)
                           .addFormDataPart("to_date",todateTV.getText().toString())
                           .addFormDataPart("apply_date",apply_dateTV.getText().toString())
                           .addFormDataPart("from_date",fromdateTV.getText().toString())
                           .addFormDataPart("reason",reason.getText().toString())
                           .addFormDataPart("id", leaveid)
                           .build();

                   Request request=new Request.Builder()
                           .url(url)
                           .header("Client-Service", Constants.clientService)
                           .header("Auth-Key", Constants.authKey)
                           .header("User-ID",Utility.getSharedPreferences(getApplicationContext(), "userId"))
                           .header("Authorization",Utility.getSharedPreferences(getApplicationContext(), "accessToken"))
                           .post(requestBody)
                           .build();

                   Response response= null;
                   try {
                       response = client.newCall(request).execute();

                       if(!response.isSuccessful()){
                           throw  new IOException("Error :"+response);
                       }
                       progress.dismiss();
                       finish();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           });
           t.start();
        }
    }


    private void uploadBitmap() {


      System.out.println("APPLY DATE= "+apply_dateTV.getText().toString());
      System.out.println("FROM DATE= "+fromdateTV.getText().toString());
      System.out.println("TO DATE= "+todateTV.getText().toString());
      System.out.println("REASON= "+ reason.getText().toString());
      System.out.println("leaveid= "+ leaveid);

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.updateLeaveUrl;
        Log.e("URL", url);
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(com.android.volley.Request.Method.POST, url,
                new com.android.volley.Response.Listener<NetworkResponse>()
                {
                    @Override
                    public void onResponse(NetworkResponse result)
                    {
                        pd.dismiss();
                        if (result != null) {
                            try{
                                Log.e("Result", String.valueOf(result));
                                JSONObject jsonObject = new JSONObject(new String(result.data));
                                String  status = jsonObject.getString("status");
                                Toast.makeText(StudentEditLeaveNew.this, jsonObject.getString("msg"), Toast.LENGTH_SHORT).show();

                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        pd.dismiss();
                    }
                },
                new com.android.volley.Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("User-ID", Utility.getSharedPreferences(getApplicationContext(), "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(getApplicationContext(), "accessToken"));
                Log.e("Headers", headers.toString());
                return headers;
            }


            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> pars = new HashMap<String, String>();
                pars.put("to_date",todateTV.getText().toString());
                pars.put("apply_date",apply_dateTV.getText().toString());
                pars.put("id",leaveid );
                pars.put("reason", reason.getText().toString());
                pars.put("from_date",fromdateTV.getText().toString());
                Log.e("pars==", pars.toString());
                return pars;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }
    private String getMimeType(String path) {
        String extension= MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}



