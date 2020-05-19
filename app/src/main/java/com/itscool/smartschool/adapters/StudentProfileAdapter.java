package com.itscool.smartschool.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import java.util.ArrayList;
import java.util.HashMap;

public class StudentProfileAdapter extends RecyclerView.Adapter<StudentProfileAdapter.MyViewHolder> {

    private Context context;
    private int[] othersHeaderArray;
    private ArrayList<String> othersValues;
    HashMap<String, String> personaldata;

    public StudentProfileAdapter(Context applicationContext, int[] personalHeaderArray, ArrayList<String> personalValues, HashMap<String, String> personaldata) {

        this.context = applicationContext;
        this.othersHeaderArray = personalHeaderArray;
        this.othersValues = personalValues;
        this.personaldata = personaldata;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView headerTV, valueTV;

        public MyViewHolder(View view) {
            super(view);
            headerTV = (TextView) view.findViewById(R.id.adapter_student_profile_head);
            valueTV = (TextView) view.findViewById(R.id.adapter_student_profile_value);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_profile, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        Utility.setLocale(context, Utility.getSharedPreferences(context.getApplicationContext(), Constants.langCode));
        holder.headerTV.setText(context.getString(othersHeaderArray[position]));
        holder.valueTV.setText(othersValues.get(position));
          System.out.println("BLOOD GROUP="+personaldata.get(0));

    }

    @Override
    public int getItemCount() {
        return othersValues.size();
    }
}