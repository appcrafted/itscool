package com.itscool.smartschool.adapters;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentLibraryBookIssued;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import java.util.ArrayList;

public class StudentLibraryBookIssuedAdapter extends RecyclerView.Adapter<StudentLibraryBookIssuedAdapter.MyViewHolder> {

    private StudentLibraryBookIssued context;
    private ArrayList<String> bookNameList;
    private ArrayList<String> authorNameList;
    
    private ArrayList<String> bookNoList;
    private ArrayList<String> issueDateList;

    private ArrayList<String> returnDateList;
    private ArrayList<String> statusList;
    private ArrayList<String> due_return_dateList;


    public StudentLibraryBookIssuedAdapter(StudentLibraryBookIssued studentLibraryBookIssued, ArrayList<String> bookNameList, ArrayList<String> authorNameList,
                                           ArrayList<String> bookNoList, ArrayList<String> issueDateList, ArrayList<String> returnDateList, ArrayList<String> statusList, ArrayList<String> due_return_dateList) {

        this.context = studentLibraryBookIssued;
        this.bookNameList = bookNameList;
        this.authorNameList = authorNameList;
        this.bookNoList = bookNoList;
        this.issueDateList = issueDateList;
        this.returnDateList = returnDateList;
        this.statusList = statusList;
        this.due_return_dateList = due_return_dateList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView bookNameTV, authorNameTV, issuedOnTV, bookNoTV, returnDateTV, statusTV,duereturnDate ;
        RelativeLayout nameView;
        LinearLayout returnView;

        public MyViewHolder(View view) {
            super(view);

            nameView = (RelativeLayout) view.findViewById(R.id.adapter_student_libraryBookIssue_nameView);

            bookNameTV = (TextView) view.findViewById(R.id.adapter_student_libraryBookIssue_bookNameTV);
            authorNameTV = (TextView) view.findViewById(R.id.adapter_student_libraryBookIssue_authorNameTV);

            issuedOnTV = (TextView) view.findViewById(R.id.adapter_student_libraryBookIssued_issueDateTV);
            bookNoTV = (TextView) view.findViewById(R.id.adapter_student_libraryBookIssued_bookNoTV);
            returnDateTV = (TextView) view.findViewById(R.id.adapter_student_libraryBookIssued_returnDateTV);
            duereturnDate = (TextView) view.findViewById(R.id.adapter_student_libraryBookIssued_duereturnDateTV);
            statusTV = (TextView) view.findViewById(R.id.adapter_student_libraryBookIssued_statusTV);

            returnView = (LinearLayout) view.findViewById(R.id.adapter_student_libraryBookIssued_returnLayout);
        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_library_book_issued, parent, false);

        return new MyViewHolder(itemView);
    }

    @NonNull
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.bookNameTV.setText(bookNameList.get(position));
        holder.authorNameTV.setText(authorNameList.get(position));
        holder.issuedOnTV.setText(issueDateList.get(position));
        holder.bookNoTV.setText(bookNoList.get(position));
        holder.returnDateTV.setText(returnDateList.get(position));
        holder.duereturnDate.setText(due_return_dateList.get(position));

        if(statusList.get(position).equals("1")) {
            holder.statusTV.setText(context.getString(R.string.returned));
            holder.statusTV.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.green_border));
        } else {
            holder.statusTV.setText(context.getString(R.string.notReturned));
            holder.statusTV.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.red_border));
        }
        //DECORATE
        holder.nameView.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));
    }

    @Override
    public int getItemCount() {
        return bookNameList.size();
    }

}
