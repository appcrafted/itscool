package com.itscool.smartschool.adapters;

import android.graphics.Color;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.itscool.smartschool.R;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

import java.util.ArrayList;

public class StudentNotificationAdapter extends BaseAdapter {

    private FragmentActivity context;
    private ArrayList<String> noticeTitleId;
    private ArrayList<String> noticeTitleList;
    private ArrayList<String> noticeDateList;
    private ArrayList<String> noticeDescList;


    public StudentNotificationAdapter(FragmentActivity studentNoticeBoard, ArrayList<String> noticeTitleId,
                                      ArrayList<String> noticeTitleList, ArrayList<String> noticeDateList,
                                      ArrayList<String> noticeDescList) {

        this.context = studentNoticeBoard;
        this.noticeTitleId = noticeTitleId;
        this.noticeTitleList = noticeTitleList;
        this.noticeDateList = noticeDateList;
        this.noticeDescList = noticeDescList;

    }

    @Override
    public int getCount() {
        return noticeTitleId.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {
        View rowView = view;
        ViewHolder viewHolder = null;

        if (rowView == null) {

            LayoutInflater inflater = LayoutInflater.from(context);
            rowView = inflater.inflate(R.layout.adapter_student_notification, viewGroup, false);
            viewHolder = new ViewHolder();

            viewHolder.viewBtn = rowView.findViewById(R.id.studentNotificationAdapter_viewBtn);
            viewHolder.notifTitle = (TextView) rowView.findViewById(R.id.studentNotificationAdapter_titleTV);
            viewHolder.notifDate = (TextView) rowView.findViewById(R.id.studentNotificationAdapter_dateTV);

            viewHolder.viewBtn.setTag(position);
            viewHolder.notifTitle.setTag(position);
            viewHolder.notifDate.setTag(position);

        }else{
            viewHolder  = (ViewHolder) rowView.getTag();
        }

        viewHolder.notifTitle.setText(noticeTitleList.get(position));
        viewHolder.notifDate.setText(noticeDateList.get(position));

        //DECORATE
        viewHolder.notifTitle.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));



        viewHolder.viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View bottomSheet = context.getLayoutInflater().inflate(R.layout.fragment_notification_bottom_sheet, null);

                TextView headerTV = bottomSheet.findViewById(R.id.studentNotification_bottomSheet__header);
                ImageView crossBtn = bottomSheet.findViewById(R.id.studentNotification_bottomSheet__crossBtn);
                TextView notificationDetailTV = bottomSheet.findViewById(R.id.studentNotification_bottomSheet_descTV);


                headerTV.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));

                headerTV.setText(noticeTitleList.get(position));
                notificationDetailTV.setText(Html.fromHtml(noticeDescList.get(position)));

                final BottomSheetDialog dialog = new BottomSheetDialog(context);

                dialog.setContentView(bottomSheet);
                dialog.show();

                crossBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

            }
        });

        rowView.setTag(viewHolder);
        return rowView;

    }

    static class ViewHolder {
        public TextView notifTitle, notifDate;
        public LinearLayout viewBtn;
    }
}
