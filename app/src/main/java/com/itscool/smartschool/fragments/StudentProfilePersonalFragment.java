package com.itscool.smartschool.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentProfileAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

public class StudentProfilePersonalFragment extends Fragment {

    RecyclerView listView;
    int[] personalHeaderArray;
    ArrayList <String> personalValues = new ArrayList<String>();
    HashMap<String, String> personaldata = new  HashMap<>();
    StudentProfileAdapter adapter;

    public static StudentProfilePersonalFragment newInstance(int[] personalHeaderArray, ArrayList<String> personalValues ,
                                                             HashMap<String, String> personaldata) {

        StudentProfilePersonalFragment personalFragment = new StudentProfilePersonalFragment();
        Bundle args = new Bundle();
        args.putIntArray("heads", personalHeaderArray);
        args.putStringArrayList("values", personalValues);
        args.putSerializable("field", personaldata);
        personalFragment.setArguments(args);
        return personalFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        personalHeaderArray = getArguments().getIntArray("heads");
        personalValues = getArguments().getStringArrayList("values");
        personaldata = (HashMap<String, String>) getArguments().getSerializable("field");


        Locale current = getResources().getConfiguration().locale;
        Log.e("current locale fragment", current.getDisplayName()+"..");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_student_profile, container, false);

        listView = (RecyclerView) mainView.findViewById(R.id.studentProfileFragment_listview);
        adapter = new StudentProfileAdapter(getActivity().getApplicationContext(),personalHeaderArray,personalValues,personaldata);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        listView.setLayoutManager(mLayoutManager);
        listView.setItemAnimator(new DefaultItemAnimator());
        listView.setAdapter(adapter);
        return mainView;
    }
}
