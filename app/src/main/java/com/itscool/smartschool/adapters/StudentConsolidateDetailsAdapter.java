package com.itscool.smartschool.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itscool.smartschool.R;

import java.util.ArrayList;

public class StudentConsolidateDetailsAdapter extends RecyclerView.Adapter<StudentConsolidateDetailsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> examList;
    private ArrayList<String> percentageList;
    private ArrayList<String> idList;

    public StudentConsolidateDetailsAdapter(Context applicationContext,ArrayList<String> examList,ArrayList<String> percentageList,ArrayList<String> idList) {
        this.context = applicationContext;

        this.examList = examList;
        this.percentageList = percentageList;
        this.idList = idList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView Exam, Percentage,subject,Roomno;
        LinearLayout viewdetail;
        public MyViewHolder(View view) {
            super(view);
            Exam = (TextView) view.findViewById(R.id.Exam);
            Percentage = (TextView) view.findViewById(R.id.Percentage);
            subject = (TextView) view.findViewById(R.id.subject);
            Roomno = (TextView) view.findViewById(R.id.Roomno);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_consolidate_detail, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.Exam.setText(examList.get(position));
        holder.Percentage.setText(percentageList.get(position));


    }

    @Override
    public int getItemCount() {
        return idList.size();
    }
}

