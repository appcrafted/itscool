package com.itscool.smartschool.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentExamSchedule;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import java.util.ArrayList;

public class StudentExamScheduleAdapter extends RecyclerView.Adapter<StudentExamScheduleAdapter.MyViewHolder> {

    private StudentExamSchedule context;
    private ArrayList<String> subjectList;
    private ArrayList<String> dateList;
    private ArrayList<String> timeList;
    private ArrayList<String> roomList;
    private ArrayList<String> durationList;
    private ArrayList<String> max_marksList;
    private ArrayList<String> min_marksList;
    private ArrayList<String> credit_hoursList;

    public StudentExamScheduleAdapter(StudentExamSchedule studentExamSchedule,ArrayList<String> subjectList,
                                      ArrayList<String> dateList,ArrayList<String> timeList,ArrayList<String> roomList,
                                      ArrayList<String> durationList,ArrayList<String> max_marksList,ArrayList<String> min_marksList,ArrayList<String> credit_hoursList){
        this.context = studentExamSchedule;
        this.subjectList = subjectList;
        this.dateList = dateList;
        this.timeList = timeList;
        this.roomList = roomList;
        this.durationList = durationList;
        this.max_marksList = max_marksList;
        this.min_marksList = min_marksList;
        this.credit_hoursList = credit_hoursList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectNameTV, dateTV, timeTV,duration,roomNoTV,max,min,CreditHours;
        private RelativeLayout subjectNameHeader;

        public MyViewHolder(View view) {
            super(view);

            subjectNameHeader = view.findViewById(R.id.adapter_student_libraryBook_nameView);
            subjectNameTV = (TextView) view.findViewById(R.id.adapter_student_examSchedule_subjectTV);
            dateTV = (TextView) view.findViewById(R.id.adapter_student_examSchedule_dateTV);
            timeTV = (TextView) view.findViewById(R.id.adapter_student_examSchedule_timeTV);
            duration = (TextView) view.findViewById(R.id.adapter_student_examSchedule_duration);
            roomNoTV = (TextView) view.findViewById(R.id.adapter_student_examSchedule_roomTV);
            max = (TextView) view.findViewById(R.id.adapter_student_examSchedule_max);
            min = (TextView) view.findViewById(R.id.adapter_student_examSchedule_min);
            CreditHours = (TextView) view.findViewById(R.id.adapter_student_examSchedule_CreditHours);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_exam_schedule, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.subjectNameHeader.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));
        holder.subjectNameTV.setText(subjectList.get(position));
        holder.dateTV.setText(dateList.get(position));
        holder.timeTV.setText(timeList.get(position));
        holder.roomNoTV.setText(roomList.get(position));
        holder.duration.setText(durationList.get(position));
        holder.max.setText(max_marksList.get(position));
        holder.min.setText(min_marksList.get(position));
        holder.CreditHours.setText(credit_hoursList.get(position));
    }
    @Override
    public int getItemCount() {
        return subjectList.size();
    }
}
