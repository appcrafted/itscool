package com.itscool.smartschool.students;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.ViewPagerAdapter;
import com.itscool.smartschool.fragments.StudentPersonalDetailNew;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

public class StudentProfileNew extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;
    private int[] tabIcons = {
            R.drawable.ic_calender_cross,
            R.drawable.ic_calender_cross,
            R.drawable.ic_calender_cross,
    };
    public LinearLayout libraryBtn;
    public ImageView backBtn;
    public String defaultDateFormat, currency;
    public TextView titleTV;
    protected FrameLayout mDrawerLayout, actionBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile_new);
        backBtn = findViewById(R.id.actionBar_backBtn);
        mDrawerLayout = findViewById(R.id.container);
        actionBar = findViewById(R.id.actionBarSecondary);
        titleTV = findViewById(R.id.actionBar_title);
        libraryBtn = findViewById(R.id.baseActivity_libraryBtn);

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
        titleTV.setText(getApplicationContext().getString(R.string.profile));
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        String visitid = getIntent().getStringExtra("visit_id");

        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),this);
        viewPagerAdapter.addFragment(new StudentPersonalDetailNew(), "Visit",tabIcons[0]);
        viewPagerAdapter.addFragment(new StudentPersonalDetailNew(), "Charges",tabIcons[1]);
        viewPagerAdapter.addFragment(new StudentPersonalDetailNew(), "Payment",tabIcons[2]);

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));
        highLightCurrentTab(0); // for initial selected tab view
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }
            @Override
            public void onPageSelected(int position) {
                highLightCurrentTab(position); // for tab change
            }
            @Override
            public void onPageScrollStateChanged(int state) { }
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
    private void highLightCurrentTab(int position) {
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            assert tab != null;
            tab.setCustomView(null);
            tab.setCustomView(viewPagerAdapter.getTabView(i));
        }
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        assert tab != null;
        tab.setCustomView(null);
        tab.setCustomView(viewPagerAdapter.getSelectedTabView(position));
    }
    }