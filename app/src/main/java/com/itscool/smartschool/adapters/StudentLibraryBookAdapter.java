package com.itscool.smartschool.adapters;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentLibraryBook;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

import java.util.ArrayList;

public class StudentLibraryBookAdapter extends RecyclerView.Adapter<StudentLibraryBookAdapter.MyViewHolder> {

    private StudentLibraryBook context;
    private ArrayList<String> bookNameList;
    private ArrayList<String> authorNameList;

    private ArrayList<String> subjectNameList;
    private ArrayList<String> publisherNameList;
    private ArrayList<String> rackNoList;
    private ArrayList<String> quantityList;
    private ArrayList<String> priceList;
    private ArrayList<String> postDate;

    public StudentLibraryBookAdapter(StudentLibraryBook studentLibraryBook, ArrayList<String> bookNameList,
                                     ArrayList<String> authorNameList, ArrayList<String> subjectNameList,
                                     ArrayList<String> publisherNameList, ArrayList<String> rackNoList,
                                     ArrayList<String> quantityList, ArrayList<String> priceList, ArrayList<String> postDate) {

        this.context = studentLibraryBook;
        this.bookNameList = bookNameList;
        this.authorNameList = authorNameList;
        this.subjectNameList = subjectNameList;
        this.publisherNameList = publisherNameList;
        this.rackNoList = rackNoList;
        this.quantityList = quantityList;
        this.priceList = priceList;
        this.postDate = postDate;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView bookNameTV, authorNameTV, subjectNameTV, publisherNameTV, rackTV, qtyTV, priceTV, postDateTV;
        public RelativeLayout nameView;
        public ImageView indicatorIV;
        public LinearLayout detailsView;

        public MyViewHolder(View view) {
            super(view);
            nameView = (RelativeLayout) view.findViewById(R.id.adapter_student_libraryBook_nameView);
            indicatorIV = (ImageView) view.findViewById(R.id.adapter_student_libraryBook_indicatorIV);
            bookNameTV = (TextView) view.findViewById(R.id.adapter_student_libraryBook_bookNameTV);

            detailsView = (LinearLayout) view.findViewById(R.id.adapter_student_libraryBook_detailView);
            authorNameTV = (TextView) view.findViewById(R.id.adapter_student_libraryBook_authorNameTV);
            subjectNameTV = (TextView) view.findViewById(R.id.adapter_student_libraryBook_subjectNameTV);
            publisherNameTV = (TextView) view.findViewById(R.id.adapter_student_libraryBook_publisherNameTV);
            rackTV = (TextView) view.findViewById(R.id.adapter_student_libraryBook_rackTV);
            qtyTV = (TextView) view.findViewById(R.id.adapter_student_libraryBook_qtyTV);
            priceTV = (TextView) view.findViewById(R.id.adapter_student_libraryBook_priceTV);
            postDateTV = (TextView) view.findViewById(R.id.adapter_student_libraryBook_postDateTV);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_library_book, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.bookNameTV.setText(bookNameList.get(position));

        holder.authorNameTV.setText("Author: "+authorNameList.get(position));
        holder.subjectNameTV.setText(subjectNameList.get(position));
        holder.publisherNameTV.setText("P" +
                "ublisher: "+publisherNameList.get(position));
        holder.rackTV.setText(rackNoList.get(position));
        holder.qtyTV.setText(quantityList.get(position));
        holder.priceTV.setText(priceList.get(position));
        holder.postDateTV.setText(postDate.get(position));

        holder.nameView.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));

    }

    @Override
    public int getItemCount() {
        return bookNameList.size();
    }


}
