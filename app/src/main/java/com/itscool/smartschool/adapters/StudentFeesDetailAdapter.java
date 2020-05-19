package com.itscool.smartschool.adapters;

import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itscool.smartschool.R;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

import java.util.ArrayList;

public class StudentFeesDetailAdapter extends RecyclerView.Adapter<StudentFeesDetailAdapter.MyViewHolder> {

    private FragmentActivity context;
    private ArrayList<String> paymentIdList;
    private ArrayList<String> paymentDateList;
    private ArrayList<String> paymentDiscountList;
    private ArrayList<String> paymentFineList;
    private ArrayList<String> paymentPaidList;
    private ArrayList<String> paymentNoteList;

    public StudentFeesDetailAdapter(FragmentActivity studentsFees, ArrayList<String> paymentId,
                                    ArrayList<String> paymentDate, ArrayList<String> paymentDiscount,
                                    ArrayList<String> paymentFine, ArrayList<String> paymentPaid, ArrayList<String> paymentNote) {

        this.context = studentsFees;
        this.paymentIdList = paymentId;
        this.paymentDateList = paymentDate;
        this.paymentDiscountList = paymentDiscount;
        this.paymentFineList = paymentFine;
        this.paymentPaidList = paymentPaid;
        this.paymentNoteList = paymentNote;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView paymentId, paymentDate, paymentDiscount, paymentFine, paymentPaid, paymentNote;
        LinearLayout viewContainer;

        public MyViewHolder(View view) {
            super(view);

            paymentId = (TextView) view.findViewById(R.id.adapter_student_feesDetail_paymentId);
            paymentDate = (TextView) view.findViewById(R.id.adapter_student_feesDetail_paymentDate);
            paymentDiscount = (TextView) view.findViewById(R.id.adapter_student_feesDetail_paymentDiscount);
            paymentFine = (TextView) view.findViewById(R.id.adapter_student_feesDetail_paymentFine);
            paymentPaid = (TextView) view.findViewById(R.id.adapter_student_feesDetail_paymentPaid);
            paymentNote = (TextView) view.findViewById(R.id.adapter_student_feesDetail_paymentNoteTV);
            viewContainer = (LinearLayout) view.findViewById(R.id.adapter_student_feesDetail_viewContainer);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_fees_detail, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        String currency = Utility.getSharedPreferences(context.getApplicationContext(), Constants.currency);

        holder.paymentId.setText(paymentIdList.get(position));
        holder.paymentDate.setText(paymentDateList.get(position));
        holder.paymentDiscount.setText(currency + paymentDiscountList.get(position));
        holder.paymentFine.setText(currency + paymentFineList.get(position));
        holder.paymentPaid.setText(currency + paymentPaidList.get(position));
        holder.paymentNote.setText(paymentNoteList.get(position));

        holder.viewContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.paymentNote.getVisibility() == View.VISIBLE) {
                    holder.paymentNote.setVisibility(View.GONE);
                } else {
                    holder.paymentNote.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return paymentIdList.size();
    }

}

