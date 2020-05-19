package com.itscool.smartschool.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentSubjectList;
import java.util.ArrayList;

public class StudentClassSubjectListNewAdapter extends RecyclerView.Adapter<StudentClassSubjectListNewAdapter.MyViewHolder> {
    private StudentSubjectList context;
    private ArrayList<String> timeList;
    private ArrayList<String> subjectList;
    private ArrayList<String> roomNoList;
    private ArrayList<String> staffList;

    public StudentClassSubjectListNewAdapter(StudentSubjectList studentClassTimetable, ArrayList<String> mondaySubject, ArrayList<String> mondayTime,
                                             ArrayList<String> mondayRoomNo,ArrayList<String> mondaystaff) {
        this.context = studentClassTimetable;
        this.timeList = mondayTime;
        this.subjectList = mondaySubject;
        this.roomNoList = mondayRoomNo;
        this.staffList = mondaystaff;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView timeTV, subjectTV, roomNoTV,staffTV;
        public MyViewHolder(View view) {
            super(view);
            timeTV = (TextView) view.findViewById(R.id.adapter_student_classTimetable_timeTV);
            subjectTV = (TextView) view.findViewById(R.id.adapter_student_classTimetable_subjectTV);
            roomNoTV = (TextView) view.findViewById(R.id.adapter_student_classTimetable_roomNoTV);
            staffTV = (TextView) view.findViewById(R.id.adapter_student_staffname);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_subjectlistnew, parent, false);
        return new MyViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.timeTV.setText(timeList.get(position));
        holder.subjectTV.setText(subjectList.get(position));
        holder.roomNoTV.setText(roomNoList.get(position));
        holder.staffTV.setText(staffList.get(position));
    }
    @Override
    public int getItemCount() {
        return subjectList.size();
    }
}

