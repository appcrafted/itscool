package com.itscool.smartschool.students;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.itscool.smartschool.AboutSchool;
import com.itscool.smartschool.Login;
import com.itscool.smartschool.R;
import com.itscool.smartschool.TakeUrl;
import com.itscool.smartschool.adapters.LoginChildListAdapter;
import com.itscool.smartschool.fragments.StudentDashboardAttendance;
import com.itscool.smartschool.fragments.StudentDashboardFees;
import com.itscool.smartschool.fragments.StudentDashboardHomeWork;
import com.itscool.smartschool.fragments.StudentDashboardFragment;
import com.itscool.smartschool.fragments.StudentDashboardReportCard;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.DrawerArrowDrawable;
import com.itscool.smartschool.utils.Utility;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Random;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import static android.view.Gravity.START;

public class StudentDashboard extends AppCompatActivity {

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    String[] permissionsRequired = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CALL_PHONE};
    private boolean sentToSettings = false;
    public DrawerArrowDrawable drawerArrowDrawable;
    ImageView drawerIndicator;
    public float offset;
    public boolean flipped;
    public DrawerLayout drawer;
    protected FrameLayout mDrawerLayout, actionBar;
    ImageView actionBarLogo;
    BottomNavigationView bottomNavigation;
    //Navigation Menu and Header
    private NavigationView navigationView;
    private RelativeLayout drawerHead;
    private TextView classTV, nameTV, childDetailsTV,hotlist_hot;
    private ImageView profileImageIV;
    private LinearLayout switchChildBtn;
    ArrayList<String> moduleCodeList = new ArrayList<String>();
    ArrayList<String> moduleStatusList = new ArrayList<String>();
    public Map<String, String> headers = new HashMap<String, String>();
    FrameLayout viewContainer;
    LayoutInflater inflater;
    LinearLayout chatBtn,notification_alert;
    View contentView;
    boolean doubleBackToExitPressedOnce = false;
    ArrayList<String> childIdList = new ArrayList<String>();
    ArrayList<String> childNameList = new ArrayList<String>();
    ArrayList<String> childClassList = new ArrayList<String>();
    ArrayList<String> childImageList = new ArrayList<String>();
    public Map<String, String> params = new Hashtable<String, String>();
    LoginChildListAdapter studentListAdapter;
    JSONArray modulesJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.student_dashboard_activity);

        drawerIndicator = findViewById(R.id.drawer_indicator);
        actionBar = findViewById(R.id.actionBar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        actionBarLogo = findViewById(R.id.actionBar_logo);
      //  chatBtn = findViewById(R.id.chatBtn);
       notification_alert = findViewById(R.id.notification_alert);

        prepareNavList();
        setUpDrawer();
        decorate();
        setUpPermission();

        params.put("site_url", Utility.getSharedPreferences(getApplicationContext(), Constants.imagesUrl));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
         getDataFromApi(obj.toString());

        if(Utility.getSharedPreferences(getApplicationContext(), "role").equals("parent")) {
            if(Utility.getSharedPreferencesBoolean(getApplicationContext(), "hasMultipleChild")) {
               switchChildBtn.setVisibility(View.VISIBLE);
            } else {
                switchChildBtn.setVisibility(View.GONE);
            }
        } else {
            switchChildBtn.setVisibility(View.GONE);
        }

        switchChildBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showChildList();
            }
        });
        /*chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StudentDashboard.this,StudentChat.class);
                startActivity(intent);
            }
        });*/

        notification_alert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(StudentDashboard.this,NotificationList.class);
                startActivity(intent);
            }
        });


        viewContainer = findViewById(R.id.studentDashboard_frame);
        bottomNavigation = (BottomNavigationView) findViewById(R.id.navigation);

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.navigation_home:
                        loadFragment(new StudentDashboardFragment());
                        drawer.closeDrawer(START);
                        return true;

                    case R.id.navigation_homework:
                        loadFragment(new StudentDashboardHomeWork());
                        drawer.closeDrawer(START);
                        return true;

                    case R.id.navigation_fees:
                        loadFragment(new StudentDashboardFees());
                        drawer.closeDrawer(START);
                        return true;

                    case R.id.navigation_noticeBoard:
                        loadFragment(new StudentDashboardAttendance());
                        drawer.closeDrawer(START);
                        return true;

                    case R.id.navigation_reportCard:
                        loadFragment(new StudentDashboardReportCard());
                        drawer.closeDrawer(START);
                        return true;
                }
                return false;
            }
        });
        loadFragment(new StudentDashboardFragment());
    }

    private void showChildList() {

        View view = getLayoutInflater().inflate(R.layout.fragment_login_bottom_sheet, null);
        view.setMinimumHeight(500);

        TextView headerTV = view.findViewById(R.id.login_bottomSheet_header);
        ImageView crossBtn = view.findViewById(R.id.login_bottomSheet_crossBtn);
        RecyclerView childListView = view.findViewById(R.id.login_bottomSheet_listview);

        headerTV.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.secondaryColour)));
        headerTV.setText(getString(R.string.childList));

        Log.e("ImageList", childImageList.toString());
        Log.e("Class List", childClassList.toString());
        Log.e("ID List", childIdList.toString());

        studentListAdapter = new LoginChildListAdapter(StudentDashboard.this, childIdList, childNameList, childClassList, childImageList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        childListView.setLayoutManager(mLayoutManager);
        childListView.setItemAnimator(new DefaultItemAnimator());
        childListView.setAdapter(studentListAdapter);

        final BottomSheetDialog dialog = new BottomSheetDialog(StudentDashboard.this);

        dialog.setContentView(view);
        dialog.show();

        crossBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();

            }
        });

        params.put("parent_id", Utility.getSharedPreferences(getApplicationContext(), "userId"));

        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());

        getStudentsListFromApi(obj.toString());

        Log.e("Child Name", childNameList.toString());
    }

    private void getStudentsListFromApi (String bodyParams) {

        childIdList.clear(); childNameList.clear(); childClassList.clear(); childImageList.clear();

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.parent_getStudentList;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Result", result);
                        JSONObject object = new JSONObject(result);

                        JSONArray dataArray = object.getJSONArray("childs");
                        if (dataArray.length() != 0) {

                            for(int i = 0; i < dataArray.length(); i++) {
                                childIdList.add(dataArray.getJSONObject(i).getString("id"));
                                childNameList.add(dataArray.getJSONObject(i).getString("firstname") + " " +  dataArray.getJSONObject(i).getString("lastname") );
                                childClassList.add(dataArray.getJSONObject(i).getString("class") + "-" +  dataArray.getJSONObject(i).getString("section"));
                                childImageList.add(dataArray.getJSONObject(i).getString("image"));
                            }
                            studentListAdapter.notifyDataSetChanged();
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
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        pd.dismiss();
                        Log.e("Volley Error", volleyError.toString());
                        Toast.makeText(StudentDashboard.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(StudentDashboard.this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void getModulesFromApi (String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getModuleUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("Modules Result", result);
                        JSONObject object = new JSONObject(result);

                        modulesJson = object.getJSONArray("module_list");
                        Utility.setSharedPreference(getApplicationContext(), Constants.modulesArray, modulesJson.toString());
                        if (modulesJson.length() != 0) {
                            for(int i = 0; i < modulesJson.length(); i++) {
                                moduleCodeList.add(modulesJson.getJSONObject(i).getString("short_code"));
                                moduleStatusList.add(modulesJson.getJSONObject(i).getString("is_active"));
                            }
                            setMenu(navigationView.getMenu(), bottomNavigation.getMenu());
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
                Toast.makeText(StudentDashboard.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentDashboard.this);//Creating a Request Queue
        requestQueue.add(stringRequest); //Adding request to the queue
    }

    private void setUpPermission() {
        if(ActivityCompat.checkSelfPermission(StudentDashboard.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(StudentDashboard.this, permissionsRequired[1]) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(StudentDashboard.this, permissionsRequired[2]) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(StudentDashboard.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(StudentDashboard.this,permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(StudentDashboard.this,permissionsRequired[2])){
                ActivityCompat.requestPermissions(StudentDashboard.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
            } else {
                ActivityCompat.requestPermissions(StudentDashboard.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
            }
            Utility.setSharedPreferenceBoolean(getApplicationContext(), Constants.permissionStatus, true);
        }
    }

    private void setUpDrawer() {

        //HEADER
        View headerLayout = navigationView.getHeaderView(0);
        classTV = headerLayout.findViewById(R.id.drawer_userClass);
        nameTV = headerLayout.findViewById(R.id.drawer_userName);
        profileImageIV = headerLayout.findViewById(R.id.drawer_logo);
        drawerHead = headerLayout.findViewById(R.id.drawer_head);
        switchChildBtn = headerLayout.findViewById(R.id.drawer_switchChildBtn);
        childDetailsTV = headerLayout.findViewById(R.id.drawer_studentDetailsTV);
        //HEADER

        Resources resources = getResources();
        drawerArrowDrawable = new DrawerArrowDrawable(resources);
        drawerArrowDrawable.setStrokeColor(resources.getColor(R.color.drawerIndicatorColour));

        drawerIndicator.setImageDrawable(drawerArrowDrawable);

        drawer.setDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                offset = slideOffset;
                // Sometimes slideOffset ends up so close to but not quite 1 or 0.
                if (slideOffset >= .995) {
                    flipped = true;
                    drawerArrowDrawable.setFlip(flipped);
                } else if (slideOffset <= .005) {
                    flipped = false;
                    drawerArrowDrawable.setFlip(flipped);
                }
                drawerArrowDrawable.setParameter(offset);
            }
        });
        drawerIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(START)) {
                    drawer.closeDrawer(START);
                } else {
                    drawer.openDrawer(START);
//                    drawer.closeDrawer(drawerRight);
                }
            }
        });
    }
    private void decorate() {

        Utility.setLocale(getApplicationContext(), Utility.getSharedPreferences(getApplicationContext(), Constants.langCode));
        String appLogo = Utility.getSharedPreferences(this, Constants.appLogo)+"?"+new Random().nextInt(11);

        Picasso.with(getApplicationContext()).load(Utility.getSharedPreferences(this, "userImage")).placeholder(R.drawable.placeholder_user).into(profileImageIV);
        Picasso.with(getApplicationContext()).load(appLogo).fit().centerInside().placeholder(null).into(actionBarLogo);

        nameTV.setText(Utility.getSharedPreferences(this, Constants.userName));
        classTV.setText(Utility.getSharedPreferences(this, Constants.classSection));
        childDetailsTV.setText("Child - " + Utility.getSharedPreferences(getApplicationContext(), "studentName")
                + "\n" + Utility.getSharedPreferences(this, Constants.classSection)  );

        if(Utility.getSharedPreferences(getApplicationContext(), Constants.loginType).equals("parent")) {
            classTV.setVisibility(View.GONE);
            childDetailsTV.setVisibility(View.VISIBLE);
        } else {
            classTV.setVisibility(View.VISIBLE);
            childDetailsTV.setVisibility(View.GONE);
        }

        actionBar.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));

        drawerHead.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.secondaryColour)));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        }
    }

    private void prepareNavList() {

        params.put("user", Utility.getSharedPreferences(getApplicationContext(), Constants.loginType));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());

        getModulesFromApi(obj.toString());

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (menuItem.getItemId()) {

                    case R.id.nav_home:
                        Intent dashboard = new Intent(StudentDashboard.this, StudentDashboard.class);
                        startActivity(dashboard);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_profile:
                        Intent profile = new Intent(StudentDashboard.this, StudentProfileDetailsNew.class);
                        startActivity(profile);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_fees:
                        Intent fees = new Intent(StudentDashboard.this, StudentFees.class);
                        startActivity(fees);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_timetable:
                        Intent classTimeTable = new Intent(StudentDashboard.this, StudentClassTimetable.class);
                        startActivity(classTimeTable);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_homework:
                        Intent homework = new Intent(StudentDashboard.this, StudentHomework.class);
                        startActivity(homework);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_onlineexam:
                        Intent onlineexam = new Intent(StudentDashboard.this, StudentOnlineExam.class);
                        startActivity(onlineexam);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_applyleave:
                        Intent applyleave = new Intent(StudentDashboard.this, StudentAppyLeave.class);
                        startActivity(applyleave);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_downloadCenter:
                        Intent download = new Intent(StudentDashboard.this, StudentDownloads.class);
                        startActivity(download);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_attendance:
                        Intent attendance = new Intent(StudentDashboard.this, StudentAttendance.class);
                        startActivity(attendance);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_examination:
                        Intent examination = new Intent(StudentDashboard.this, StudentExaminationList.class);
                        startActivity(examination);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_noticeBoard:
                        Intent notice = new Intent(StudentDashboard.this, StudentNoticeBoard.class);
                        startActivity(notice);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_timeLine:
                        Intent timeline = new Intent(StudentDashboard.this, StudentTimeline.class);
                        startActivity(timeline);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_documents:
                        Intent doc = new Intent(StudentDashboard.this, StudentDocuments.class);
                        startActivity(doc);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_teachers:
                        Intent teacher = new Intent(StudentDashboard.this, StudentTeachersList.class);
                        startActivity(teacher);
                        drawer.closeDrawer(START);
                        break;


                    case R.id.nav_library:
                        Intent booksIssued = new Intent(StudentDashboard.this, StudentLibraryBookIssued.class);
                        startActivity(booksIssued);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_transportRoute:
                        Intent transport = new Intent(StudentDashboard.this, StudentTransportRoutes.class);
                        startActivity(transport);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_hostel:
                        Intent hostel = new Intent(StudentDashboard.this, StudentHostel.class);
                        startActivity(hostel);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_about:
                        Intent about = new Intent(StudentDashboard.this, AboutSchool.class);
                        startActivity(about);
                        drawer.closeDrawer(START);
                        break;

                    case R.id.nav_logout:

                        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StudentDashboard.this);
                        builder.setCancelable(false);
                        builder.setMessage(R.string.logoutMsg);
                        builder.setTitle("");
                        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                loginOutApi();
                            }
                        });
                        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        android.app.AlertDialog alert = builder.create();
                        alert.show();
                        break;
                }
                return false;
            }
        });
    }
    private void setMenu(Menu navMenu, Menu bottomNavMenu) {

        for (int i = 0; i<moduleCodeList.size(); i++) {

            if (moduleCodeList.get(i).equals("fees") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_fees).setVisible(false);
                bottomNavMenu.findItem(R.id.navigation_fees).setVisible(false);
            } if (moduleCodeList.get(i).equals("student_attendance") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_attendance).setVisible(false);
            } if (moduleCodeList.get(i).equals("examinations") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_examination).setVisible(false);
                bottomNavMenu.findItem(R.id.navigation_reportCard).setVisible(false);
            } if (moduleCodeList.get(i).equals("download_center") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_downloadCenter).setVisible(false);
            } if (moduleCodeList.get(i).equals("library") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_library).setVisible(false);
            } if (moduleCodeList.get(i).equals("transport_routes") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_transportRoute).setVisible(false);
            } if (moduleCodeList.get(i).equals("hostel_rooms") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_hostel).setVisible(false);
            } if (moduleCodeList.get(i).equals("homework") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_homework).setVisible(false);
                bottomNavMenu.findItem(R.id.navigation_homework).setVisible(false);
            } if (moduleCodeList.get(i).equals("communicate") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_noticeBoard).setVisible(false);
                bottomNavMenu.findItem(R.id.navigation_noticeBoard).setVisible(false);
            }if (moduleCodeList.get(i).equals("online_examination") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_onlineexam).setVisible(false);
            }if (moduleCodeList.get(i).equals("class_timetable") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_timetable).setVisible(false);
            }if (moduleCodeList.get(i).equals("attendance") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_attendance).setVisible(false);
            }if (moduleCodeList.get(i).equals("teachers_rating") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_teachers).setVisible(false);
            }if (moduleCodeList.get(i).equals("notice_board") && moduleStatusList.get(i).equals("0")) {
                navMenu.findItem(R.id.nav_noticeBoard).setVisible(false);
            }if (Utility.getSharedPreferences(getApplicationContext(), Constants.loginType).equals("parent")) {
                navMenu.findItem(R.id.nav_onlineexam).setVisible(false);
                navMenu.findItem(R.id.nav_applyleave).setVisible(false);
                navMenu.findItem(R.id.nav_documents).setVisible(false);
            }
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.studentDashboard_frame, fragment);
        transaction.commit();
    }

    //RUNTIME PERMISSIONS
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_CALLBACK_CONSTANT){

            if(ActivityCompat.shouldShowRequestPermissionRationale(StudentDashboard.this,permissionsRequired[0])
                    || ActivityCompat.shouldShowRequestPermissionRationale(StudentDashboard.this,permissionsRequired[1])
                    || ActivityCompat.shouldShowRequestPermissionRationale(StudentDashboard.this,permissionsRequired[2])){

                AlertDialog.Builder builder = new AlertDialog.Builder(StudentDashboard.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs to access to your storage and call permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(StudentDashboard.this,permissionsRequired,PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            } else {

                if(Build.MANUFACTURER.equalsIgnoreCase("xiaomi")) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(StudentDashboard.this);
                    builder.setTitle("Allow Notifications");
                    builder.setMessage("For smooth functioning of app, please provide Auto-Start permission and allow notification access.");
                    builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            goToSettings();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();


                } else {
                    goToSettings();
                }

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("STATUS", "1");
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (ActivityCompat.checkSelfPermission(StudentDashboard.this, permissionsRequired[0]) == PackageManager.PERMISSION_GRANTED) {

            } else {
                Log.e("PERMISSION MANAGER", "PERMISSION MISSING");
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sentToSettings) {
            if (ActivityCompat.checkSelfPermission(StudentDashboard.this, permissionsRequired[0]) != PackageManager.PERMISSION_GRANTED) {
                Log.e("PERMISSION MANAGER", "PERMISSION MISSING");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    private void goToSettings() {

        sentToSettings = true;
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

    private void loginOutApi () {

        final ProgressDialog pd = new ProgressDialog(StudentDashboard.this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();


        String url = Utility.getSharedPreferences(StudentDashboard.this, "apiUrl")+ Constants.logoutUrl;
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
                            Intent logout = new Intent(StudentDashboard.this, Login.class);
                            logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            logout.putExtra("EXIT", true);
                            startActivity(logout);
                            finish();
                        } else {
                            Intent intent=new Intent(StudentDashboard.this, TakeUrl.class);
                            startActivity(intent);
                           // Toast.makeText(StudentDashboard.this, object.getString("errorMsg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(StudentDashboard.this, R.string.noInternetMsg, Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        pd.dismiss();
                        Log.e("Volley Error", volleyError.toString());
                        Toast.makeText(StudentDashboard.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                headers.put("Client-Service", Constants.clientService);
                headers.put("Auth-Key", Constants.authKey);
                headers.put("Content-Type", Constants.contentType);
                headers.put("User-ID", Utility.getSharedPreferences(StudentDashboard.this, "userId"));
                headers.put("Authorization", Utility.getSharedPreferences(StudentDashboard.this, "accessToken"));
                Log.e("Headers", headers.toString());
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(StudentDashboard.this);

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

                                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(StudentDashboard.this);
                                builder.setCancelable(false);
                                builder.setMessage(R.string.verificationMessage);
                                builder.setTitle("");
                                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        loginOutApi();
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
                            Toast.makeText(StudentDashboard.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
            RequestQueue requestQueue = Volley.newRequestQueue(StudentDashboard.this);

            //Adding request to the queue
            requestQueue.add(stringRequest);


        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException | InvalidKeyException | InvalidParameterSpecException |
                IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException | InvalidAlgorithmParameterException  exp) {
            Log.e("ENCRYPTION", exp.toString());
        }
    }

}
