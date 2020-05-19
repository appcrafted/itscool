package com.itscool.smartschool.adapters;

import android.content.Intent;
import android.graphics.Color;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.Payment;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StudentFeesAdapter extends RecyclerView.Adapter<StudentFeesAdapter.MyViewHolder> {

    private FragmentActivity studentsFees;
    private ArrayList<String> feesIdList;
    private ArrayList<String> feesCodeList;
    private ArrayList<String> dueDateList;
    private ArrayList<String> amtList;
    private ArrayList<String> balanceAmtList;
    private ArrayList<String> paidAmtList;
    private ArrayList<String> depositId;
    private ArrayList<String> statusList;
    private ArrayList<String> feesDetails;
    private ArrayList<String> feesTypeId;
    private ArrayList<String> feesCatList;
    private ArrayList<String> discountNameList;
    private ArrayList<String> discountStatusList;
    private ArrayList<String> discountAmtList;
    private ArrayList<String> discountpayment_idList;

    public StudentFeesAdapter(FragmentActivity activity, ArrayList<String> feesIdList, ArrayList<String> feesCodeList,
                              ArrayList<String> dueDateList, ArrayList<String> amtList, ArrayList<String> paidAmtList,
                              ArrayList<String> balanceAmtList, ArrayList<String> feesDepositIdList, ArrayList<String> statusList,
                              ArrayList<String> feesDetails, ArrayList<String> feesTypeId, ArrayList<String> feesCatList,
                              ArrayList<String> discountNameList, ArrayList<String> discountAmtList,
                              ArrayList<String> discountStatusList, ArrayList<String> discountpayment_idList) {

        this.studentsFees = activity;
        this.feesIdList = feesIdList;
        this.feesCodeList = feesCodeList;
        this.dueDateList = dueDateList;
        this.amtList = amtList;
        this.balanceAmtList = balanceAmtList;
        this.paidAmtList = paidAmtList;
        this.depositId = feesDepositIdList;
        this.statusList = statusList;
        this.feesDetails = feesDetails;
        this.feesTypeId = feesTypeId;
        this.feesCatList = feesCatList;
        this.discountNameList = discountNameList;
        this.discountStatusList = discountStatusList;
        this.discountAmtList = discountAmtList;
        this.discountpayment_idList = discountpayment_idList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView feesCodeTV, feesDueDateTV, feesAmtTV, feesPaidAmtTV, feesDueAmtTV, feesStatusTV, payBtn,discountpayment_id;
        private LinearLayout viewContainer, viewBtn, feesLay, discountLay;
        private RelativeLayout header;
        private TextView discountNameTV, discountAmtTV;

        public MyViewHolder(View rowView) {
            super(rowView);

            viewContainer = rowView.findViewById(R.id.studentFeesAdapter);
            feesCodeTV = rowView.findViewById(R.id.studentFeesAdapter_feeCodeTV);
            feesDueDateTV = rowView.findViewById(R.id.studentFeesAdapter_feeDueDateTV);
            feesAmtTV = rowView.findViewById(R.id.studentFeesAdapter_feeAmtTV);
            feesPaidAmtTV = rowView.findViewById(R.id.studentFeesAdapter_feePaidAmtTV);
            feesDueAmtTV = rowView.findViewById(R.id.studentFeesAdapter_feeDueAmtTV);
            feesStatusTV = rowView.findViewById(R.id.feesAdapter_statusTV);
            header = rowView.findViewById(R.id.feesAdapter_nameHeader);
            viewBtn = rowView.findViewById(R.id.studentFeesAdapter_viewBtn);
            payBtn = rowView.findViewById(R.id.feesAdapter_payBtn);
            discountpayment_id = rowView.findViewById(R.id.studentFeesAdapter_discountpayment_idTV);
            feesLay = rowView.findViewById(R.id.studentFeesAdapter_feesLay);
            discountLay = rowView.findViewById(R.id.studentFeesAdapter_discountLay);
            discountAmtTV = rowView.findViewById(R.id.studentFeesAdapter_discountAmtTV);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_fees, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, final int position) {
        //FEES VIEW
        if(feesCatList.get(position).equals("fees")) {
            viewHolder.feesLay.setVisibility(View.VISIBLE);
            viewHolder.discountLay.setVisibility(View.GONE);
            showFeesCard(viewHolder, position);
        } else {
            viewHolder.feesLay.setVisibility(View.GONE);
            viewHolder.discountLay.setVisibility(View.VISIBLE);
            viewHolder.viewBtn.setVisibility(View.GONE);
            showDiscountCard(viewHolder, position);
        }
        Log.e("payBtn", Utility.getSharedPreferencesBoolean(studentsFees, Constants.showPaymentBtn)+"..");
        viewHolder.viewContainer.setOnClickListener(null);
    }

    private void showDiscountCard(MyViewHolder viewHolder, final int position) {

        viewHolder.feesCodeTV.setText(studentsFees.getString(R.string.paymentDiscount)+"-"+discountNameList.get(position));
        //viewHolder.discountpayment_id.setText(discountpayment_idList.get(position));
       // viewHolder.discountNameTV.setText(discountNameList.get(position));
        String discountMsg = studentsFees.getString(R.string.discountMsg) +" "+ Utility.getSharedPreferences(studentsFees, Constants.currency);
        discountMsg += discountAmtList.get(position) + " " + discountStatusList.get(position)/*+" - " + discountpayment_idList.get(position)*/;
        viewHolder.discountAmtTV.setText(discountMsg);
    }

    private void showFeesCard(MyViewHolder viewHolder, final int position) {
        viewHolder.feesCodeTV.setText(feesCodeList.get(position));
        String defaultDateFormat = Utility.getSharedPreferences(studentsFees.getApplicationContext(), "dateFormat");
        viewHolder.feesDueDateTV.setText(Utility.parseDate("yyyy-MM-dd", defaultDateFormat, dueDateList.get(position)));
        viewHolder.feesAmtTV.setText(amtList.get(position));
        viewHolder.feesPaidAmtTV.setText(paidAmtList.get(position));
        viewHolder.feesDueAmtTV.setText(balanceAmtList.get(position));
        viewHolder.feesStatusTV.setText(statusList.get(position));

        if(statusList.get(position).equals("Paid")) {
            viewHolder.feesStatusTV.setBackgroundResource(R.drawable.green_border);
            viewHolder.feesDueDateTV.setBackgroundResource(R.color.transparent);
//            viewHolder.payBtn.setVisibility(View.GONE);
            viewHolder.viewBtn.setVisibility(View.VISIBLE);
        } if (statusList.get(position).equals("Unpaid")) {
            viewHolder.feesStatusTV.setBackgroundResource(R.drawable.red_border);
//            viewHolder.payBtn.setVisibility(View.VISIBLE);
            viewHolder.viewBtn.setVisibility(View.GONE);

            if(checkDueDate(dueDateList.get(position))) {
                viewHolder.feesDueDateTV.setBackgroundResource(R.drawable.red_border);

                float scale = studentsFees.getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (5*scale + 0.5f);

                viewHolder.feesDueDateTV.setPadding(dpAsPixels, 0, dpAsPixels, 0);
                viewHolder.feesDueDateTV.setTextColor(Color.WHITE);

            } else {
                viewHolder.feesDueDateTV.setBackgroundResource(R.color.transparent);
            }
        } if (statusList.get(position).equals("Partial")) {
            viewHolder.feesStatusTV.setBackgroundResource(R.drawable.yellow_border);
//            viewHolder.payBtn.setVisibility(View.VISIBLE);
            viewHolder.viewBtn.setVisibility(View.VISIBLE);
            if(checkDueDate(dueDateList.get(position))) {
                viewHolder.feesDueDateTV.setBackgroundResource(R.drawable.red_border);

                float scale = studentsFees.getResources().getDisplayMetrics().density;
                int dpAsPixels = (int) (5*scale + 0.5f);

                viewHolder.feesDueDateTV.setPadding(dpAsPixels, 0, dpAsPixels, 0);
                viewHolder.feesDueDateTV.setTextColor(Color.WHITE);

            } else {
                viewHolder.feesDueDateTV.setBackgroundResource(R.color.transparent);
            }
        }

        if(Utility.getSharedPreferencesBoolean(studentsFees, Constants.showPaymentBtn))  {
            Log.e("testing", "testing 1");
            viewHolder.payBtn.setVisibility(View.VISIBLE);

            if(statusList.get(position).equals("Paid")) {
                viewHolder.payBtn.setVisibility(View.GONE);
            } if (statusList.get(position).equals("Unpaid")) {
                viewHolder.payBtn.setVisibility(View.VISIBLE);
            } if (statusList.get(position).equals("Partial")) {
                viewHolder.payBtn.setVisibility(View.VISIBLE);
            }
        } else {
            Log.e("testing", "testing 2");
            viewHolder.payBtn.setVisibility(View.GONE);
        }

        //DECORATE
        viewHolder.header.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(studentsFees.getApplicationContext(), Constants.secondaryColour)));
        viewHolder.payBtn.setText(Utility.getSharedPreferences(studentsFees.getApplicationContext(), Constants.currency) + " " + studentsFees.getApplicationContext().getString(R.string.pay));
        //DECORATE

        viewHolder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View clickview) {

                ArrayList<String> paymentId = new ArrayList<>();
                ArrayList<String> paymentDate = new ArrayList<>();
                ArrayList<String> paymentDiscount = new ArrayList<>();
                ArrayList<String> paymentFine = new ArrayList<>();
                ArrayList<String> paymentPaid = new ArrayList<>();
                ArrayList<String> paymentNote = new ArrayList<>();

                JSONObject feesDetailsJson = new JSONObject();
                try {
                    feesDetailsJson = new JSONObject(feesDetails.get(position));

                    feesDetailsJson.length();

                    for (int k = 1; k<=feesDetailsJson.length(); k++) {

                        JSONObject fee = feesDetailsJson.getJSONObject(k+"");

                        paymentId.add(depositId.get(position) + "/" + fee.getString("inv_no") + "(" + fee.getString("payment_mode") + ")" );
                        paymentDate.add(Utility.parseDate("yyyy-MM-dd", Utility.getSharedPreferences(studentsFees.getApplicationContext(), "dateFormat"), fee.getString("date")));
                        paymentDiscount.add(fee.getString("amount_discount"));
                        paymentFine.add(fee.getString("amount_fine"));
                        paymentPaid.add(fee.getString("amount"));
                        paymentNote.add(fee.getString("description"));
                    }
                } catch (JSONException je) {
                    Log.e("Error Parseing Data", je.toString());
                }

                View view = studentsFees.getLayoutInflater().inflate(R.layout.fragment_fees_bottom_sheet, null);
                view.setMinimumHeight(500);

                ImageView crossBtn = view.findViewById(R.id.fees_bottomSheet_crossBtn);
                TextView header = view.findViewById(R.id.fees_bottomSheet_header);
                RecyclerView hostelListView = view.findViewById(R.id.fees_bottomSheet_listview);

                header.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(studentsFees.getApplicationContext(), Constants.secondaryColour)));

                StudentFeesDetailAdapter adapter = new StudentFeesDetailAdapter(studentsFees, paymentId, paymentDate, paymentDiscount, paymentFine, paymentPaid, paymentNote);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(studentsFees.getApplicationContext());
                hostelListView.setLayoutManager(mLayoutManager);
                hostelListView.setItemAnimator(new DefaultItemAnimator());
                hostelListView.setAdapter(adapter);
                final BottomSheetDialog dialog = new BottomSheetDialog(studentsFees);

                dialog.setContentView(view);
                dialog.show();

                crossBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });
            }
        });
        viewHolder.payBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asd = new Intent(studentsFees, Payment.class);
                asd.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                asd.putExtra("feesId", feesIdList.get(position));
                asd.putExtra("feesTypeId", feesTypeId.get(position));
                studentsFees.startActivity(asd);
            }
        });
    }
    private boolean checkDueDate(String dueDateStr) {

        try {
            Date todayDate = new Date();
            Date dueDate =new SimpleDateFormat("yyyy-MM-dd").parse(dueDateStr);

            if(todayDate.after(dueDate)) {
                return true;
            } else {
                return  false;
            }

        } catch (ParseException e) {
            Log.e("Parse Exp", e.toString());
            return false;
        }
    }
    @Override
    public int getItemCount() {
        return feesIdList.size();
    }
}
