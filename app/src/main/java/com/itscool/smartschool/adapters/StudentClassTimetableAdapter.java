package com.itscool.smartschool.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentClassTimetable;
import java.util.ArrayList;

public class StudentClassTimetableAdapter extends RecyclerView.Adapter<StudentClassTimetableAdapter.MyViewHolder> {

    private StudentClassTimetable context;
    private ArrayList<String> timeList;
    private ArrayList<String> subjectList;
    private ArrayList<String> roomNoList;

    public StudentClassTimetableAdapter(StudentClassTimetable studentClassTimetable, ArrayList<String> mondaySubject, ArrayList<String> mondayTime, ArrayList<String> mondayRoomNo) {

        this.context = studentClassTimetable;
        this.timeList = mondayTime;
        this.subjectList = mondaySubject;
        this.roomNoList = mondayRoomNo;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView timeTV, subjectTV, roomNoTV;

        public MyViewHolder(View view) {
            super(view);

            timeTV = (TextView) view.findViewById(R.id.adapter_student_classTimetable_timeTV);
            subjectTV = (TextView) view.findViewById(R.id.adapter_student_classTimetable_subjectTV);
            roomNoTV = (TextView) view.findViewById(R.id.adapter_student_classTimetable_roomNoTV);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_classtimetable, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.timeTV.setText(timeList.get(position));
        holder.subjectTV.setText(subjectList.get(position));
        holder.roomNoTV.setText(roomNoList.get(position));
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

}

