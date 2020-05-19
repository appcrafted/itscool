package com.itscool.smartschool.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itscool.smartschool.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StudentOnlineExamResultAdapter extends RecyclerView.Adapter<StudentOnlineExamResultAdapter.MyViewHolder> {

    private FragmentActivity context;
    private ArrayList<String> questionlist;
    private ArrayList<String> subject_namelist;
    private ArrayList<String> select_optionlist;
    private ArrayList<String> correctlist;
    private ArrayList<String> idlist;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();

    public StudentOnlineExamResultAdapter(FragmentActivity studentonlineexam, ArrayList<String> questionlist, ArrayList<String> subject_namelist,
                                          ArrayList<String> select_optionlist, ArrayList<String> correctlist, ArrayList<String> idlist){
        this.context = studentonlineexam;
        this.questionlist = questionlist;
        this.subject_namelist = subject_namelist;
        this.select_optionlist = select_optionlist;
        this.correctlist = correctlist;
        this.idlist = idlist;

    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView question,subject,answer,option,attempted,duration,status;
        ImageView wrong,right;

        public MyViewHolder(View view) {
            super(view);

            question = view.findViewById(R.id.question);
            subject = view.findViewById(R.id.subject);
            answer = view.findViewById(R.id.answer);


        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_exam_result, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.question.setText(questionlist.get(position));
        holder.subject.setText(subject_namelist.get(position));

       if(select_optionlist.get(position).equals("null") || select_optionlist.get(position).equals("")){
           holder.answer.setText(R.string.not_attempt);
           holder.answer.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.yellow_border));
       }else{
           if(select_optionlist.get(position).equals(correctlist.get(position))){
               holder.answer.setText(R.string.correct);
               holder.answer.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_border));
           }else{
               holder.answer.setText(R.string.incorrect);
               holder.answer.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.red_border));
           }
       }
    }

    @Override
    public int getItemCount() {
        return questionlist.size();
    }
}
