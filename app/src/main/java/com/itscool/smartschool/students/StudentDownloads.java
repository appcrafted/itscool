package com.itscool.smartschool.students;

import android.graphics.Color;
import android.support.design.widget.TabLayout;

import android.support.v4.app.FragmentPagerAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.itscool.smartschool.BaseActivity;
import com.itscool.smartschool.fragments.StudentDownloadAssignmentFragment;
import com.itscool.smartschool.fragments.StudentDownloadOthersFragment;
import com.itscool.smartschool.fragments.StudentDownloadStudyMaterialFragment;
import com.itscool.smartschool.fragments.StudentDownloadSyllabusFragment;
import com.itscool.smartschool.R;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

import java.util.ArrayList;
import java.util.List;

public class StudentDownloads extends BaseActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.student_downloads_activity, null, false);
        mDrawerLayout.addView(contentView, 0);

        titleTV.setText(getApplicationContext().getString(R.string.downloadCenter));

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(Utility.getSharedPreferences(getApplicationContext(), Constants.primaryColour)));

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new StudentDownloadAssignmentFragment(), getApplicationContext().getString(R.string.assignment));
        adapter.addFragment(new StudentDownloadStudyMaterialFragment(), getApplicationContext().getString(R.string.studyMaterial));
        adapter.addFragment(new StudentDownloadSyllabusFragment(), getApplicationContext().getString(R.string.syllabus));
        adapter.addFragment(new StudentDownloadOthersFragment(), getApplicationContext().getString(R.string.otherDownload));
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
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

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

}
