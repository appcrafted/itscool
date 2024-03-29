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

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentApplyLeaveAdapter;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

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


public class StudentAddLeave extends AppCompatActivity {
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
    Button submit;
    ProgressBar progressBar;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    ImageView imageview,upload_image;
    String path;
    EditText reason;
    Uri uri;
    private static final String TAG = "StudentAddLeave";
    private String PathHolder = "";
    TextView todateTV, apply_dateTV, fromdateTV;
    private static final int READ_REQUEST_CODE = 42;

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

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleTV.setText(getApplicationContext().getString(R.string.applyleave));
        apply_dateTV = findViewById(R.id.addLeave_dialog_apply_dateTV);
        fromdateTV = findViewById(R.id.addLeave_dialog_fromdateTV);
        todateTV = findViewById(R.id.addLeave_dialog_todateTV);
        reason = findViewById(R.id.reason);
        submit = findViewById(R.id.addLeave_dialog_submitBtn);
        upload_image = (ImageView) findViewById(R.id.upload_image);

        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new MaterialFilePicker()
                        .withActivity(StudentAddLeave.this)
                        .withRequestCode(10)
                        .start();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!isapplyDateSelected) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.applyDateError), Toast.LENGTH_LONG).show();
                }else if(!isfromDateSelected) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.fromDateError), Toast.LENGTH_LONG).show();
                }else if(!istoDateSelected) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.toDateError), Toast.LENGTH_LONG).show();
                } else {
                    try {
                        uploadBitmap();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                
            }
        });

        apply_dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mDay   = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mYear  = mcurrentDate.get(Calendar.YEAR);
                DatePickerDialog datePickerDialog = new DatePickerDialog(StudentAddLeave.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        applydate=sdf.format(newDate.getTime());
                        SimpleDateFormat sdfdate = new SimpleDateFormat("dd-MM-yyyy");
                        apply_dateTV.setText(sdfdate.format(newDate.getTime()));
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(StudentAddLeave.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        fromdate=sdf.format(newDate.getTime());
                        SimpleDateFormat sdfdate = new SimpleDateFormat("dd-MM-yyyy");
                        fromdateTV.setText(sdfdate.format(newDate.getTime()));
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
                DatePickerDialog datePickerDialog = new DatePickerDialog(StudentAddLeave.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //month = month + 1;
                        Calendar newDate = Calendar.getInstance();
                        newDate.set(selectedyear, selectedmonth, selectedday);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        todate=sdf.format(newDate.getTime());
                        SimpleDateFormat sdfdate = new SimpleDateFormat("dd-MM-yyyy");
                        todateTV.setText(sdfdate.format(newDate.getTime()));
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

                if(!isapplyDateSelected) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.applyDateError), Toast.LENGTH_LONG).show();
                }else if(!isfromDateSelected) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.fromDateError), Toast.LENGTH_LONG).show();
                }else if(!istoDateSelected) {
                    Toast.makeText(getApplicationContext(), getApplicationContext().getString(R.string.toDateError), Toast.LENGTH_LONG).show();
                } else {
                    // uploadBitmap(bitmap);//Api Call
                    new MaterialFilePicker()
                            .withActivity(StudentAddLeave.this)
                            .withRequestCode(10)
                            .start();
                }


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
        final String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.addleaveUrl;
        if (requestCode == 10 && resultCode == RESULT_OK) {

            progress=new ProgressDialog(StudentAddLeave.this);
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
                           .addFormDataPart("to_date",todate)
                           .addFormDataPart("apply_date",applydate)
                           .addFormDataPart("from_date",fromdate)
                           .addFormDataPart("reason",reason.getText().toString())
                           .addFormDataPart("student_id", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId))
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
                       Intent intent=new Intent(StudentAddLeave.this,StudentAppyLeave.class);
                       startActivity(intent);

                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }
           });
           t.start();
        }
    }

    /*private void uploadBitmap() {
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();
        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.addleaveUrl;
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
                                Toast.makeText(StudentAddLeave.this, "Successfully Added !!!", Toast.LENGTH_SHORT).show();
                                finish();
                                Intent intent=new Intent(StudentAddLeave.this,StudentAppyLeave.class);
                                startActivity(intent);
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
                pars.put("to_date", todate);
                pars.put("apply_date", applydate);
                pars.put("student_id", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId));
                pars.put("reason", reason.getText().toString());
                pars.put("from_date", fromdate);
                Log.e("pars==", pars.toString());
                return pars;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }*/

    private void uploadBitmap() throws IOException{

        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        final String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.addleaveUrl;
        OkHttpClient client=new OkHttpClient();
        JSONObject postdata = new JSONObject();
        try {
            postdata.put("file", "");
            postdata.put("to_date", todate);
            postdata.put("apply_date", applydate);
            postdata.put("from_date", fromdate);
            postdata.put("reason", reason.getText().toString());
            postdata.put("student_id", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId));
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE, postdata.toString());

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
            Intent intent=new Intent(StudentAddLeave.this,StudentAppyLeave.class);
            startActivity(intent);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private String getMimeType(String path) {
        String extension= MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}



