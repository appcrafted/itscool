package com.itscool.smartschool.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentHostel;
import java.util.ArrayList;

public class StudentHostelDetailAdapter extends RecyclerView.Adapter<StudentHostelDetailAdapter.MyViewHolder> {

    private StudentHostel context;
    private ArrayList<String> roomTypeList;
    private ArrayList<String> roomNumberList;
    private ArrayList<String> roomCostList;
    private ArrayList<String> no_of_bedList;
    private ArrayList<String> student_idList;

    public StudentHostelDetailAdapter(StudentHostel studentHostelDetail, ArrayList<String> roomTypeList,
                                      ArrayList<String> roomNumberList, ArrayList<String> roomCostList, ArrayList<String> no_of_bedList, ArrayList<String> student_idList) {

        this.context = studentHostelDetail;
        this.roomTypeList = roomTypeList;
        this.roomNumberList = roomNumberList;
        this.roomCostList = roomCostList;
        this.no_of_bedList = no_of_bedList;
        this.student_idList = student_idList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView typeTV, numberTV, costTV,bedno;

        public MyViewHolder(View view) {
            super(view);

            typeTV = (TextView) view.findViewById(R.id.adapter_student_hostelDetail_roomTypeTV);
            numberTV = (TextView) view.findViewById(R.id.adapter_student_hostelDetail_roomNoTV);
            costTV = (TextView) view.findViewById(R.id.adapter_student_hostelDetail_roomCostTV);
            bedno = (TextView) view.findViewById(R.id.adapter_student_hostelDetail_bednoTV);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_hostel_detail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.numberTV.setText(roomNumberList.get(position));
        holder.costTV.setText(roomCostList.get(position));
        holder.bedno.setText(no_of_bedList.get(position));

        if(Integer.parseInt(student_idList.get(position))>0){
            holder.typeTV.setText(roomTypeList.get(position) + " (Assigned)");
        }else{
            holder.typeTV.setText(roomTypeList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return roomTypeList.size();
    }

}

