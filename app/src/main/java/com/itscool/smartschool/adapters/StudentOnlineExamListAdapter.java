package com.itscool.smartschool.adapters;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentOnlineExamQuestionsNew;
import com.itscool.smartschool.students.StudentOnlineExamResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StudentOnlineExamListAdapter extends RecyclerView.Adapter<StudentOnlineExamListAdapter.MyViewHolder> {

    private FragmentActivity context;
    private ArrayList<String> examList;
    private ArrayList<String> exam_fromList;
    private ArrayList<String> exam_toList;
    private ArrayList<String> durationList;
    private ArrayList<String> attemptList;
    private ArrayList<String> attemptslist;
    private ArrayList<String> onlineexam_idlist;
    private ArrayList<String> publish_resultlist;
    private ArrayList<String> is_submittedlist;
    private ArrayList<String> onlineexam_student_idlist;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();

    public StudentOnlineExamListAdapter(FragmentActivity studentonlineexam, ArrayList<String> examList, ArrayList<String> exam_fromList,
                                        ArrayList<String> exam_toList, ArrayList<String> durationList, ArrayList<String> attemptList,
                                        ArrayList<String> attemptslist, ArrayList<String> onlineexam_idlist, ArrayList<String> publish_resultlist,ArrayList<String> is_submittedlist,ArrayList<String> onlineexam_student_idlist) {

        this.context = studentonlineexam;
        this.examList = examList;
        this.exam_fromList = exam_fromList;
        this.exam_toList = exam_toList;
        this.durationList = durationList;
        this.attemptList = attemptList;
        this.attemptslist = attemptslist;
        this.onlineexam_idlist = onlineexam_idlist;
        this.publish_resultlist = publish_resultlist;
        this.is_submittedlist = is_submittedlist;
        this.onlineexam_student_idlist = onlineexam_student_idlist;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView examname,datefrom,dateto,totalattempts,attempted,duration,status;
        LinearLayout startexam,viewresult;

        public MyViewHolder(View view) {
            super(view);
            startexam = view.findViewById(R.id.adapter_student_onlineexam_startexam);
            viewresult = view.findViewById(R.id.adapter_student_onlineexam_viewresult);
            examname = view.findViewById(R.id.adapter_student_onlineexam_name);
            datefrom = view.findViewById(R.id.datefrom);
            dateto = view.findViewById(R.id.dateto);
            totalattempts = view.findViewById(R.id.totalattempts);
            attempted = view.findViewById(R.id.attempted);
            duration = view.findViewById(R.id.duration);
            status = view.findViewById(R.id.status);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_examlist, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.examname.setText(examList.get(position));
        holder.datefrom.setText(exam_fromList.get(position));
        holder.dateto.setText(exam_toList.get(position));
        holder.duration.setText(durationList.get(position));
        holder.totalattempts.setText(attemptList.get(position));
        holder.attempted.setText(attemptslist.get(position));


        holder.viewresult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context.getApplicationContext(), StudentOnlineExamResult.class);
                intent.putExtra("OnlineExam_students_Id",onlineexam_student_idlist.get(position));
                intent.putExtra("exams_id",onlineexam_idlist.get(position));
                context.startActivity(intent);
            }
        });

        if(publish_resultlist.get(position).equals("1")){

            holder.status.setText("Result Published");
            holder.startexam.setVisibility(View.GONE);
            holder.viewresult.setVisibility(View.VISIBLE);
        }else{
            holder.viewresult.setVisibility(View.GONE);

            if (is_submittedlist.get(position).equals("1")){
                holder.status.setText("Available");
                holder.startexam.setVisibility(View.GONE);
               // holder.viewresult.setVisibility(View.VISIBLE);
            }else {
                holder.status.setText("Available");
                holder.startexam.setVisibility(View.VISIBLE);
               // holder.viewresult.setVisibility(View.GONE);
                holder.startexam.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*Date current = new Date();
                        //create a date one day before current date
                        Date to_date = new Date(exam_toList.get(position));
                        //compare both dates
                        if(to_date.before(current)){
                            Toast.makeText(context.getApplicationContext(), "Exam date is Passed", Toast.LENGTH_SHORT).show();
                        } else {*/
                        
                        
                        if(attemptList.get(position).equals(attemptslist.get(position))){
                            Toast.makeText(context, "You have reached total limits", Toast.LENGTH_SHORT).show();
                        }else{
                            Intent intent=new Intent(context.getApplicationContext(), StudentOnlineExamQuestionsNew.class);
                            intent.putExtra("Online_Exam_Id",onlineexam_idlist.get(position));
                            intent.putExtra("durationList",durationList.get(position));
                            intent.putExtra("onlineexam_student_idlist",onlineexam_student_idlist.get(position));
                            context.startActivity(intent);
                        }
                            
                    }
                });
            }
        }
    }
    @Override
    public int getItemCount() {
        return onlineexam_idlist.size();
    }
}
