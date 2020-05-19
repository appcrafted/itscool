package com.itscool.smartschool.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

import java.util.ArrayList;

public class StudentReportCard_ExamListAdapter extends RecyclerView.Adapter<StudentReportCard_ExamListAdapter.MyViewHolder> {

    Context context;
    ArrayList<String> examNameList;
    ArrayList<String> examIdList;

    ArrayList<String> totalList;
    ArrayList<String> percentList;
    ArrayList<String> gradeList;
    ArrayList<String> statusList;




    public StudentReportCard_ExamListAdapter(Context applicationContext, ArrayList<String> examIdList, ArrayList<String> examNameList, ArrayList<String> totalList,
                                             ArrayList<String> percentList, ArrayList<String> gradeList, ArrayList<String> statusList) {

        this.examNameList = examNameList;
        this.examIdList = examIdList;
        this.totalList = totalList;
        this.percentList = percentList;
        this.gradeList = gradeList;
        this.statusList = statusList;
        this.context = applicationContext;

    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView examNameTV, gradeTV, gradeHeaderTV, percentageTV, totalTV, statusTV;
    public CardView viewContainer;

    public MyViewHolder(View view) {
        super(view);
        examNameTV = (TextView) view.findViewById(R.id.adapter_reportCard_examList_nameTV);

        gradeTV = (TextView) view.findViewById(R.id.adapter_reportCard_examList_gradeTV);
        gradeHeaderTV = (TextView) view.findViewById(R.id.adapter_reportCard_examList_gradeHeaderTV);
        percentageTV = (TextView) view.findViewById(R.id.adapter_reportCard_examList_percentageTV);
        totalTV = (TextView) view.findViewById(R.id.adapter_reportCard_examList_totalTV);
        statusTV = (TextView) view.findViewById(R.id.adapter_reportCard_examList_statusTV);

        viewContainer = (CardView) view.findViewById(R.id.adapter_report_card_exam_list);
    }
}

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_report_card_exam_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.examNameTV.setText(examNameList.get(position));

        holder.totalTV.setText(totalList.get(position));
        holder.percentageTV.setText(percentList.get(position));
        holder.gradeTV.setText(gradeList.get(position));

        String grade = gradeList.get(position);
        if(grade.equals("empty list")) {
            holder.gradeTV.setVisibility(View.GONE);
            holder.gradeHeaderTV.setVisibility(View.GONE);
        } else {
            holder.gradeTV.setVisibility(View.VISIBLE);
            holder.gradeHeaderTV.setVisibility(View.VISIBLE);
        }
        holder.gradeTV.setText(grade);



        holder.statusTV.setText(statusList.get(position));

        if(statusList.get(position).equals("Pass")) {
            holder.statusTV.setBackgroundResource(R.drawable.green_border);
        } else if (statusList.get(position).equals("Fail")){
            holder.statusTV.setBackgroundResource(R.drawable.red_border);
        }

        holder.examNameTV.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));

    }

    @Override
    public int getItemCount() {
        return examIdList.size();
    }
}