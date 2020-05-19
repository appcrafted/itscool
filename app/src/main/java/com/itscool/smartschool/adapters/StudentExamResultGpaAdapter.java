package com.itscool.smartschool.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentReportCard_ExamListResult;

import java.util.ArrayList;

public class StudentExamResultGpaAdapter extends RecyclerView.Adapter<StudentExamResultGpaAdapter.MyViewHolder> {

    private StudentReportCard_ExamListResult context;
    private ArrayList<String> examType;
    private ArrayList<String> examSubjectList;
    private ArrayList<String> exam_grade_pointList;
    private ArrayList<String> examcredit_hoursList;
    private ArrayList<String> exammin_marksList;
    private ArrayList<String> exam_gradeList;

    public StudentExamResultGpaAdapter(StudentReportCard_ExamListResult studentReportCard_examResult,
                                       ArrayList<String> examType, ArrayList<String> examSubjectList, ArrayList<String> exam_grade_pointList,
                                       ArrayList<String> examcredit_hoursList, ArrayList<String> exammin_marksList, ArrayList<String> exam_gradeList) {
        this.context = studentReportCard_examResult;
        this.examSubjectList = examSubjectList;
        this.examType = examType;
        this.exam_grade_pointList = exam_grade_pointList;
        this.examcredit_hoursList = examcredit_hoursList;
        this.exammin_marksList = exammin_marksList;
        this.exam_gradeList = exam_gradeList;

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subjectNameTV, passngMarksTV, obtainedMarksTV, resultnoteTV,subject,gradepoint,credit,quality,note,resultTV;
        LinearLayout gpa_layout,basic_layout;

        public MyViewHolder(View view) {
            super(view);

            subject = (TextView) view.findViewById(R.id.subject);
            gradepoint = (TextView) view.findViewById(R.id.gradepoint);
            credit = (TextView) view.findViewById(R.id.credit);
            quality = (TextView) view.findViewById(R.id.quality);
            note = (TextView) view.findViewById(R.id.note);
            gpa_layout = (LinearLayout) view.findViewById(R.id.gpa_adapterlayout);

        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_report_card_exam_gparesult, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

            holder.subject.setText(examSubjectList.get(position));
            System.out.println("examSubjectList="+examSubjectList.get(position));
            System.out.println("exam_grade_pointList="+exam_grade_pointList.get(position));
            System.out.println("examcredit_hoursList="+examcredit_hoursList.get(position));
            System.out.println("exammin_marksList="+exammin_marksList.get(position));
            holder.gradepoint.setText(exam_grade_pointList.get(position));
            holder.credit.setText(examcredit_hoursList.get(position));
            holder.quality.setText(exammin_marksList.get(position));
            holder.note.setText(exam_gradeList.get(position));

    }

    @Override
    public int getItemCount() {
        return examSubjectList.size();
    }
}