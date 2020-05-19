package com.itscool.smartschool.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import com.itscool.smartschool.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class StudentQuestionsListAdapter extends RecyclerView.Adapter<StudentQuestionsListAdapter.MyViewHolder> {

    private FragmentActivity context;
    private ArrayList<String> result_statusList;
    private ArrayList<String> attempt_statusList;
    private ArrayList<String> questionList;
    private ArrayList<String> question_idList;
    private ArrayList<String> onlineexam_idList;
    private ArrayList<String> opt_aList;
    private ArrayList<String> opt_bList;
    private ArrayList<String> opt_cList;
    private ArrayList<String> opt_dList;
    private ArrayList<String> opt_eList;
    private ArrayList<String> correctlist;
    public Map<String, String> params = new Hashtable<String, String>();
    public Map<String, String> headers = new HashMap<String, String>();

    public StudentQuestionsListAdapter(FragmentActivity studentonlineexam, ArrayList<String> result_statusList, ArrayList<String> attempt_statusList,
                                       ArrayList<String> questionList, ArrayList<String> question_idList, ArrayList<String> onlineexam_idList,
                                       ArrayList<String> opt_aList, ArrayList<String> opt_bList,ArrayList<String> opt_cList,ArrayList<String> opt_dList,ArrayList<String> opt_eList,ArrayList<String> correctlist) {

        this.context = studentonlineexam;
        this.result_statusList = result_statusList;
        this.attempt_statusList = attempt_statusList;
        this.questionList = questionList;
        this.question_idList = question_idList;
        this.onlineexam_idList = onlineexam_idList;
        this.opt_aList = opt_aList;
        this.opt_bList = opt_bList;
        this.opt_cList = opt_cList;
        this.opt_dList = opt_dList;
        this.opt_eList = opt_eList;
        this.correctlist = correctlist;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RadioButton option1, option2, option3, option4;
        TextView questions,sno;

        public MyViewHolder(View view) {
            super(view);

            questions = view.findViewById(R.id.questions);
            sno = view.findViewById(R.id.sno);
            option1 = (RadioButton) view.findViewById(R.id.option1);
            option2 = (RadioButton) view.findViewById(R.id.option2);
            option3 = (RadioButton) view.findViewById(R.id.option3);
            option4 = (RadioButton) view.findViewById(R.id.option4);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_onlineexam_questions, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.sno.setText("" +(position+1)+".");
        holder.questions.setText(questionList.get(position));
        holder.option1.setText(opt_aList.get(position));
        holder.option2.setText(opt_bList.get(position));
        holder.option3.setText(opt_cList.get(position));
        holder.option4.setText(opt_dList.get(position));
    }

    @Override
    public int getItemCount() {
        return question_idList.size();
    }
}
