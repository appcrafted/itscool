package com.itscool.smartschool.students;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentApplyLeaveAdapter;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class StudentUploadHomework extends AppCompatActivity {
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
    String applydate="";
    String fromdate="";
    String filePath="";
    private static final int SELECT_PICTURE = 100;
    String todate="";
    ProgressDialog progress;
    private boolean isapplyDateSelected = false;
    private boolean isfromDateSelected = false;
    private boolean istoDateSelected = false;
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> fromList = new ArrayList<String>();
    ArrayList<String> toList = new ArrayList<String>();
    ArrayList<String> statuslist = new ArrayList<String>();
    ArrayList<String> idlist = new ArrayList<String>();
    ArrayList<String> apply_datelist = new ArrayList<String>();
    Bitmap bitmap;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST =1 ;
    TextView imageview;
    Integer SELECT_FILE1,SELECT_FILE2;
    EditText reason;
    String selectedPath1,selectedPath2;
    private String PathHolder="";
    Button upload_image;
    TextView todateTV,apply_dateTV,fromdateTV;
    String homework_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_homework);
        backBtn = findViewById(R.id.actionBar_backBtn);
        mDrawerLayout = findViewById(R.id.container);
        actionBar = findViewById(R.id.actionBarSecondary);
        titleTV = findViewById(R.id.actionBar_title);

        Bundle bundle = getIntent().getExtras();
        homework_id = bundle.getString("Homework_ID");

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
        titleTV.setText(getApplicationContext().getString(R.string.upload_homework));

        reason = findViewById(R.id.reason);

        upload_image =findViewById(R.id.upload_image);

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
                        .withActivity(StudentUploadHomework.this)
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
        final String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl") + Constants.uploadHomeworkUrl;
        if (requestCode == 10 && resultCode == RESULT_OK) {

            progress=new ProgressDialog(StudentUploadHomework.this);
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
                            .addFormDataPart("student_id", Utility.getSharedPreferences(getApplicationContext(), Constants.studentId))
                            .addFormDataPart("homework_id",homework_id)
                            .addFormDataPart("message",reason.getText().toString())
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
    private String getMimeType(String path) {
        String extension= MimeTypeMap.getFileExtensionFromUrl(path);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

}
