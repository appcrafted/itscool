package com.itscool.smartschool.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentAttendance;
import java.util.ArrayList;

public class StudentSubjectAttendenceAdapter extends RecyclerView.Adapter<StudentSubjectAttendenceAdapter.MyViewHolder> {

    private StudentAttendance context;
    private ArrayList<String> subjectList;
    private ArrayList<String> time_toList;
    private ArrayList<String> timeList;
    private ArrayList<String> roomList;
    private ArrayList<String> typeList;
    private ArrayList<String> remarkList;

    public StudentSubjectAttendenceAdapter(StudentAttendance studentExamSchedule, ArrayList<String> subjectList, ArrayList<String> timeList, ArrayList<String> roomList, ArrayList<String> typeList, ArrayList<String> remarkList) {
        this.context = studentExamSchedule;
        this.subjectList = subjectList;
        this.timeList = timeList;
        this.roomList = roomList;
        this.typeList = typeList;
        this.remarkList = remarkList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subject,time,roomno,attendence,remark;

        public MyViewHolder(View view) {
            super(view);

            subject = view.findViewById(R.id.subject);
            time = (TextView) view.findViewById(R.id.time);
            roomno = (TextView) view.findViewById(R.id.roomno);
            attendence = (TextView) view.findViewById(R.id.attendence);
            remark = (TextView) view.findViewById(R.id.remark);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_subject_atten, parent, false);
        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {


        holder.subject.setText(subjectList.get(position));
        holder.time.setText(timeList.get(position));
        holder.roomno.setText(roomList.get(position));

        if(typeList.get(position).equals("Present")){
            holder.attendence.setText("P");
            holder.attendence.setTextColor(Color.parseColor("#66aa18"));
            holder.remark.setVisibility(View.GONE);
        }else if(typeList.get(position).equals("Absent")){
            holder.attendence.setText("A");
            holder.attendence.setTextColor(Color.parseColor("#FA0000"));
            holder.remark.setVisibility(View.GONE);
        }else if(typeList.get(position).equals("Half Day")){
            holder.attendence.setText("F");
            holder.attendence.setTextColor(Color.parseColor("#ff9500"));
            holder.remark.setVisibility(View.VISIBLE);
            holder.remark.setText(remarkList.get(position));
        }else if(typeList.get(position).equals("Late")){
            holder.attendence.setText("L");
            holder.attendence.setTextColor(Color.parseColor("#5A3429"));
            holder.remark.setVisibility(View.VISIBLE);
            holder.remark.setText(remarkList.get(position));
        }else if(typeList.get(position).equals("Holiday")){
            holder.attendence.setText("H");
            holder.attendence.setTextColor(Color.parseColor("#5b5b5b"));
            holder.remark.setVisibility(View.VISIBLE);
            holder.remark.setText(remarkList.get(position));
        }else{
            holder.attendence.setText("-");
            holder.remark.setVisibility(View.GONE);
        }

    }
    @Override
    public int getItemCount() {
        return subjectList.size();
    }
}
