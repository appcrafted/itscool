package com.itscool.smartschool.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentExamSchedule;
import com.itscool.smartschool.students.StudentReportCard_ExamListResult;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StudentExamListAdapter extends RecyclerView.Adapter<StudentExamListAdapter.MyViewHolder> {

    private FragmentActivity context;
    private ArrayList<String> examList;
    private ArrayList<String> exam_group_List;
    private ArrayList<String> publish_resultlist;
    private ArrayList<String> idlist;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();

    public StudentExamListAdapter(FragmentActivity studentonlineexam, ArrayList<String> examList, ArrayList<String> exam_group_List,
                                  ArrayList<String> publish_resultlist, ArrayList<String> idlist){
        this.context = studentonlineexam;
        this.examList = examList;
        this.exam_group_List = exam_group_List;
        this.publish_resultlist = publish_resultlist;
        this.idlist = idlist;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView examname;
        LinearLayout studentexam_schedule,studentexam_result,nameHeader;
        public MyViewHolder(View view) {
            super(view);

            examname = view.findViewById(R.id.adapter_student_examList_nameTV);
            studentexam_schedule = view.findViewById(R.id.adapter_studentexam_schedule);
            studentexam_result = view.findViewById(R.id.adapter_studentexam_result);
            nameHeader = view.findViewById(R.id.adapter_student_examList_nameHeader);

        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_exam_list, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.nameHeader.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));
        holder.examname.setText(examList.get(position));

        if(Utility.getSharedPreferences(context.getApplicationContext(), Constants.loginType).equals("parent")){
            holder.studentexam_schedule.setVisibility(View.GONE);
        }

        if(publish_resultlist.get(position).equals("0")){
            holder.studentexam_result.setVisibility(View.GONE);
        }else{
            holder.studentexam_result.setVisibility(View.VISIBLE);
        }

        holder.studentexam_schedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context.getApplicationContext(), StudentExamSchedule.class);
                intent.putExtra("Exam_group_Id",exam_group_List.get(position));
                context.startActivity(intent);
            }
        });

        holder.studentexam_result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Intent intent=new Intent(context.getApplicationContext(), StudentReportCard_ExamListResult.class);
                         intent.putExtra("Exam_group_Id",exam_group_List.get(position));
                        context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return idlist.size();
    }
}
