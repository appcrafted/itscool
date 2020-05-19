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

public class StudentTeacherDetailsAdapter extends RecyclerView.Adapter<StudentTeacherDetailsAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<String> teacherdayList;
    private ArrayList<String> teachersubject_nameList;
    private ArrayList<String> teacherroom_noList;
    private ArrayList<String> time_List;
    private ArrayList<String> idList;

    public StudentTeacherDetailsAdapter(Context applicationContext,ArrayList<String> idList ,ArrayList<String> teacherdayList, ArrayList<String> teacherroom_noList,
                                        ArrayList<String> teachersubject_nameList,ArrayList<String> time_List) {
        this.context = applicationContext;
        this.teacherdayList = teacherdayList;
        this.teachersubject_nameList = teachersubject_nameList;
        this.teacherroom_noList = teacherroom_noList;
        this.time_List = time_List;
        this.idList = idList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView Time, day,subject,Roomno;
        LinearLayout viewdetail;
        public MyViewHolder(View view) {
            super(view);
            Time = (TextView) view.findViewById(R.id.Time);
            day = (TextView) view.findViewById(R.id.day);
            subject = (TextView) view.findViewById(R.id.subject);
            Roomno = (TextView) view.findViewById(R.id.Roomno);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_teacher_detail, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.Time.setText(time_List.get(position));
        holder.day.setText(teacherdayList.get(position));
        holder.subject.setText(teachersubject_nameList.get(position));
        holder.Roomno.setText(teacherroom_noList.get(position));
    }

    @Override
    public int getItemCount() {
        return idList.size();
    }
}

