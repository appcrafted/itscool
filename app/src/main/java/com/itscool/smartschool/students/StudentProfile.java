package com.itscool.smartschool.students;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itscool.smartschool.BaseActivity;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentProfileAdapter;
import com.itscool.smartschool.fragments.StudentProfileParentFragment;
import com.itscool.smartschool.fragments.StudentProfilePersonalFragment;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import com.squareup.picasso.Picasso;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StudentProfile extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    int[] personalHeaderArray = {R.string.admDate, R.string.dob, R.string.category, R.string.mobileNo,
            R.string.caste, R.string.religion, R.string.email, R.string.currentAdd, R.string.permanentAdd, R.string.bloodGroup,
            R.string.height,R.string.weight, R.string.asOnDate};

    int[] othersHeaderArray = {R.string.previousSchool, R.string.nationalIdNo, R.string.localIdNo,
            R.string.bankAcNo, R.string.bankName, R.string.ifscCode, R.string.rte, R.string.studentHouse, R.string.vehicleRoute,
            R.string.vehicleNo, R.string.driverName, R.string.driverContact, R.string.hostel,
            R.string.hostelRoomNo, R.string.hostelRoomType};

    ArrayList<String> personalValues = new ArrayList<String>();
    ArrayList<String> blood_groupValues = new ArrayList<String>();
    ArrayList<String> student_houseValues = new ArrayList<String>();
    ArrayList<String> categoryValues = new ArrayList<String>();
    ArrayList<String> religionValues = new ArrayList<String>();
    ArrayList<String> castValues = new ArrayList<String>();
    ArrayList<String> mobile_noValues = new ArrayList<String>();
    ArrayList<String> student_emailValues = new ArrayList<String>();
    ArrayList<String> admission_dateValues = new ArrayList<String>();
    ArrayList<String> lastnameValues = new ArrayList<String>();
    ArrayList<String> student_photoValues = new ArrayList<String>();
    ArrayList<String> student_heightValues = new ArrayList<String>();
    ArrayList<String> student_weightValues = new ArrayList<String>();
    ArrayList<String> measurement_dateValues = new ArrayList<String>();
    ArrayList<String> father_nameValues = new ArrayList<String>();
    ArrayList<String> father_phoneValues = new ArrayList<String>();
    ArrayList<String> father_occupationValues = new ArrayList<String>();
    ArrayList<String> father_picValues = new ArrayList<String>();
    ArrayList<String> mother_nameValues = new ArrayList<String>();
    ArrayList<String> fieldValues = new ArrayList<String>();
    ArrayList<String> mother_occupationValues = new ArrayList<String>();
    ArrayList<String> mother_picValues = new ArrayList<String>();
    ArrayList<String> guardian_relationValues = new ArrayList<String>();
    ArrayList<String> guardian_addressValues = new ArrayList<String>();
    ArrayList<String> current_addressValues = new ArrayList<String>();
    ArrayList<String> permanent_addressValues = new ArrayList<String>();
    ArrayList<String> route_listValues = new ArrayList<String>();
    ArrayList<String> hostel_idValues = new ArrayList<String>();
    ArrayList<String> bank_account_noValues = new ArrayList<String>();
    ArrayList<String> national_identification_noValues = new ArrayList<String>();
    ArrayList<String> local_identification_noValues = new ArrayList<String>();
    ArrayList<String> stufieldvalues = new ArrayList<String>();

    HashMap<String, String> parentsValues = new HashMap<>();
    HashMap<String, String> fieldvalues = new HashMap<>();
    ArrayList<String> othersValues = new ArrayList<String>();
    TextView nameTV, admissionNoTV, classTV, rollNoTV;
    ImageView profileIV;
    RelativeLayout headerLayout;
    public Map<String, String> headers = new HashMap<String, String>();
    public Map<String, String> params = new Hashtable<String, String>();
    StudentProfileAdapter adapter;
    ProfileViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_profile_activity, null, false);
        mDrawerLayout.addView(contentView, 0);
        titleTV.setText(getApplicationContext().getString(R.string.profile));
        viewPager = (ViewPager) findViewById(R.id.profileViewPager);
        profileIV = (ImageView) findViewById(R.id.studentProfile_profileImageview);
        nameTV = (TextView) findViewById(R.id.studentProfile_nameTV);
        admissionNoTV = (TextView) findViewById(R.id.studentProfile_admissionNoTV);
        rollNoTV = (TextView) findViewById(R.id.studentProfile_rollNoTV);
        classTV = (TextView) findViewById(R.id.studentProfile_classTV);
        headerLayout = findViewById(R.id.profile_headerLayout);

        viewPagerAdapter = new ProfileViewPagerAdapter(getSupportFragmentManager());

        tabLayout = (TabLayout) findViewById(R.id.profileTabLayout);
        tabLayout.setupWithViewPager(viewPager);

        params.put("student_id", Utility.getSharedPreferences(getApplicationContext(), "studentId"));
        JSONObject obj=new JSONObject(params);
        Log.e("params ", obj.toString());
        getDataFromApi(obj.toString());

        decorate();
        Locale current = getResources().getConfiguration().locale;
        Log.e("current locale", current.getDisplayName()+"..");

    }

    private void decorate () {
        headerLayout.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.secondaryColour)));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
    }

    private void getDataFromApi (String bodyParams) {

        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCancelable(false);
        pd.show();

        final String requestBody = bodyParams;

        String url = Utility.getSharedPreferences(getApplicationContext(), "apiUrl")+Constants.getStudentProfileUrl;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                if (result != null) {
                    pd.dismiss();
                    try {
                        Log.e("StudentProfileResult", result);
                        JSONObject object = new JSONObject(result);

                            JSONObject dataArray = object.getJSONObject("student_result");
                            JSONObject fieldsArray = object.getJSONObject("student_fields");
                            System.out.println("fieldsArray== "+fieldsArray);

                            nameTV.setText(dataArray.getString("firstname") + " " + dataArray.getString("lastname") );
                            admissionNoTV.setText(dataArray.getString("admission_no"));
                            rollNoTV.setText(dataArray.getString("roll_no"));
                            classTV.setText(dataArray.getString("class") + " - " + dataArray.getString("section"));

                            personalValues.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getString("admission_date")));
                            personalValues.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getString("dob")));
                            personalValues.add(checkData(dataArray.getString("category")));
                            personalValues.add(checkData(dataArray.getString("mobileno")));
                            personalValues.add(checkData(dataArray.getString("cast")));
                            personalValues.add(checkData(dataArray.getString("religion")));
                            personalValues.add(checkData(dataArray.getString("email")));
                            personalValues.add(checkData(dataArray.getString("current_address")));
                            personalValues.add(checkData(dataArray.getString("permanent_address")));
                            personalValues.add(checkData(dataArray.getString("blood_group")));
                            personalValues.add(checkData(dataArray.getString("height")));
                            personalValues.add(checkData(dataArray.getString("weight")));
                            personalValues.add(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dataArray.getString("measurement_date")));

                            parentsValues.put("father_name", checkData(dataArray.getString("father_name")));
                            parentsValues.put("father_phone", checkData(dataArray.getString("father_phone")));
                            parentsValues.put("father_occupation", checkData(dataArray.getString("father_occupation")));
                            parentsValues.put("father_image", checkData(dataArray.getString("father_pic")));
                            parentsValues.put("mother_name", checkData(dataArray.getString("mother_name")));
                            parentsValues.put("mother_phone", checkData(dataArray.getString("mother_phone")));
                            parentsValues.put("mother_occupation", checkData(dataArray.getString("mother_occupation")));
                            parentsValues.put("mother_image", checkData(dataArray.getString("mother_pic")));
                            parentsValues.put("guardian_name", checkData(dataArray.getString("guardian_name")));
                            parentsValues.put("guardian_email", checkData(dataArray.getString("guardian_email")));
                            parentsValues.put("guardian_relation", checkData(dataArray.getString("guardian_relation")));
                            parentsValues.put("guardian_phone", checkData(dataArray.getString("guardian_phone")));
                            parentsValues.put("guardian_occupation", checkData(dataArray.getString("guardian_occupation")));
                            parentsValues.put("guardian_address", checkData(dataArray.getString("guardian_address")));
                            parentsValues.put("guardian_image", checkData(dataArray.getString("guardian_pic")));

                            //name, mobile, profession, relation, email, address
                            othersValues.add(checkData(dataArray.getString("previous_school")));
                            othersValues.add(checkData(dataArray.getString("adhar_no")));
                            othersValues.add(checkData(dataArray.getString("samagra_id")));
                            othersValues.add(checkData(dataArray.getString("bank_account_no")));
                            othersValues.add(checkData(dataArray.getString("bank_name")));
                            othersValues.add(checkData(dataArray.getString("ifsc_code")));
                            othersValues.add(checkData(dataArray.getString("rte")));
                            othersValues.add(checkData(dataArray.getString("house_name")));
                            othersValues.add(checkData(dataArray.getString("route_title")));
                            othersValues.add(checkData(dataArray.getString("vehicle_no")));
                            othersValues.add(checkData(dataArray.getString("driver_name")));
                            othersValues.add(checkData(dataArray.getString("driver_contact")));
                            othersValues.add(checkData(dataArray.getString("hostel_name")));
                            othersValues.add(checkData(dataArray.getString("room_no")));
                            othersValues.add(checkData(dataArray.getString("room_type")));

                            String imgUrl = Utility.getSharedPreferences(getApplicationContext(), "imagesUrl") + dataArray.getString("image");
                            Picasso.with(getApplicationContext()).load(imgUrl).placeholder(R.drawable.placeholder_user).into(profileIV);

                       fieldvalues.put("blood_group",checkData(fieldsArray.getString("blood_group")));
                       fieldvalues.put("student_house", checkData(fieldsArray.getString("student_house")));
                       fieldvalues.put("category", checkData(fieldsArray.getString("category")));
                       fieldvalues.put("religion", checkData(fieldsArray.getString("religion")));
                       fieldvalues.put("cast", checkData(fieldsArray.getString("cast")));
                       fieldvalues.put("mobile_no", checkData(fieldsArray.getString("mobile_no")));
                       fieldvalues.put("student_email", checkData(fieldsArray.getString("student_email")));
                       fieldvalues.put("admission_date", checkData(fieldsArray.getString("admission_date")));
                       fieldvalues.put("lastname", checkData(fieldsArray.getString("lastname")));
                       fieldvalues.put("student_photo", checkData(fieldsArray.getString("student_photo")));
                       fieldvalues.put("student_height", checkData(fieldsArray.getString("student_height")));
                       fieldvalues.put("student_weight", checkData(fieldsArray.getString("student_weight")));
                       fieldvalues.put("measurement_date", checkData(fieldsArray.getString("measurement_date")));
                       fieldvalues.put("current_address", checkData(fieldsArray.getString("current_address")));
                       fieldvalues.put("permanent_address", checkData(fieldsArray.getString("permanent_address")));
                       fieldvalues.put("route_list", checkData(fieldsArray.getString("route_list")));
                       fieldvalues.put("hostel_id", checkData(fieldsArray.getString("hostel_id")));
                       fieldvalues.put("bank_account_no", checkData(fieldsArray.getString("bank_account_no")));
                       fieldvalues.put("national_identification_no", checkData(fieldsArray.getString("national_identification_no")));
                       fieldvalues.put("local_identification_no", checkData(fieldsArray.getString("local_identification_no")));
                       fieldvalues.put("rte", checkData(fieldsArray.getString("rte")));

                     /*   stufieldvalues.add( checkData(fieldsArray.getString("father_name")));
                        stufieldvalues.add( checkData(fieldsArray.getString("father_phone")));
                        stufieldvalues.add( checkData(fieldsArray.getString("father_occupation")));
                        stufieldvalues.add( checkData(fieldsArray.getString("father_pic")));
                        stufieldvalues.add( checkData(fieldsArray.getString("mother_name")));
                        stufieldvalues.add( checkData(fieldsArray.getString("mother_phone")));
                        stufieldvalues.add( checkData(fieldsArray.getString("mother_occupation")));
                        stufieldvalues.add( checkData(fieldsArray.getString("mother_pic")));
                        stufieldvalues.add( checkData(fieldsArray.getString("guardian_relation")));
                        stufieldvalues.add( checkData(fieldsArray.getString("guardian_address")));

                        System.out.println("stufieldvalues== "+stufieldvalues);*/

                        System.out.println("fieldvalues== "+fieldvalues);
                        setupViewPager(viewPager);

                      /*   fieldvalues.put("blood_group", checkData(fieldsArray.getString("blood_group")));
                         fieldvalues.put("student_house", checkData(fieldsArray.getString("student_house")));
                         fieldvalues.put("category", checkData(fieldsArray.getString("category")));
                         fieldvalues.put("religion", checkData(fieldsArray.getString("religion")));
                         fieldvalues.put("cast", checkData(fieldsArray.getString("cast")));
                         fieldvalues.put("mobile_no", checkData(fieldsArray.getString("mobile_no")));
                         fieldvalues.put("student_email", checkData(fieldsArray.getString("student_email")));
                         fieldvalues.put("admission_date", checkData(fieldsArray.getString("admission_date")));
                         fieldvalues.put("lastname", checkData(fieldsArray.getString("lastname")));
                         fieldvalues.put("student_photo", checkData(fieldsArray.getString("student_photo")));
                         fieldvalues.put("student_height", checkData(fieldsArray.getString("student_height")));
                         fieldvalues.put("student_weight", checkData(fieldsArray.getString("student_weight")));
                         fieldvalues.put("measurement_date", checkData(fieldsArray.getString("measurement_date")));
                         fieldvalues.put("father_name", checkData(fieldsArray.getString("father_name")));
                         fieldvalues.put("father_phone", checkData(fieldsArray.getString("father_phone")));
                         fieldvalues.put("father_occupation", checkData(fieldsArray.getString("father_occupation")));
                         fieldvalues.put("father_pic", checkData(fieldsArray.getString("father_pic")));
                         fieldvalues.put("mother_name", checkData(fieldsArray.getString("mother_name")));
                         fieldvalues.put("mother_phone", checkData(fieldsArray.getString("mother_phone")));
                         fieldvalues.put("mother_occupation", checkData(fieldsArray.getString("mother_occupation")));
                         fieldvalues.put("mother_pic", checkData(fieldsArray.getString("mother_pic")));
                         fieldvalues.put("guardian_relation", checkData(fieldsArray.getString("guardian_relation")));
                         fieldvalues.put("guardian_address", checkData(fieldsArray.getString("guardian_address")));
                         fieldvalues.put("current_address", checkData(fieldsArray.getString("current_address")));
                         fieldvalues.put("permanent_address", checkData(fieldsArray.getString("permanent_address")));
                         fieldvalues.put("route_list", checkData(fieldsArray.getString("route_list")));
                         fieldvalues.put("hostel_id", checkData(fieldsArray.getString("hostel_id")));
                         fieldvalues.put("bank_account_no", checkData(fieldsArray.getString("bank_account_no")));
                         fieldvalues.put("national_identification_no", checkData(fieldsArray.getString("national_identification_no")));
                         fieldvalues.put("local_identification_no", checkData(fieldsArray.getString("local_identification_no")));
                         fieldvalues.put("rte", checkData(fieldsArray.getString("rte")));

                        System.out.println("fieldValues== "+fieldvalues);*/

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
                Toast.makeText(StudentProfile.this, R.string.slowInternetMsg, Toast.LENGTH_LONG).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(StudentProfile.this);//Creating a Request Queue
        requestQueue.add(stringRequest); //Adding request to the queue
    }

    private String checkData(String key) {
        if(key.equals("null")) {
            return "";
        } else {
            return key;
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        viewPagerAdapter.addFragment(new StudentProfilePersonalFragment().newInstance(personalHeaderArray, personalValues,fieldvalues),getApplicationContext().getString(R.string.personalDetail));
        viewPagerAdapter.addFragment(new StudentProfileParentFragment().newInstance(parentsValues), getApplicationContext().getString(R.string.parentsDetails));
        viewPagerAdapter.addFragment(new StudentProfilePersonalFragment().newInstance(othersHeaderArray, othersValues,fieldvalues), getApplicationContext().getString(R.string.otherDetails));
        viewPager.setAdapter(viewPagerAdapter);
    }

    class ProfileViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ProfileViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment,String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);

        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}


