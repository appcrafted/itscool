package com.itscool.smartschool.adapters;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentTimeline;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StudentTimelineAdapter extends RecyclerView.Adapter<StudentTimelineAdapter.MyViewHolder> {

    private StudentTimeline context;
    private List<String> timeLineIdList;
    private List<String> timeLineDocumentList;
    private List<String> timeLineTitleList;
    private List<String> timeLineDescList;
    private List<String> timeLineDateList;
    private List<String> timeLineStatusList;

    long downloadID;

    public StudentTimelineAdapter( StudentTimeline studentTimeline, ArrayList<String> timeLineIdList,
                                  ArrayList<String> timeLineDocumentList, ArrayList<String> timeLineTitleList,
                                  ArrayList<String> timeLineDescList, ArrayList<String> timeLineDateList,
                                  ArrayList<String> timeLineStatusList) {

        this.context = studentTimeline;
        this.timeLineIdList = timeLineIdList;
        this.timeLineDocumentList = timeLineDocumentList;
        this.timeLineTitleList = timeLineTitleList;
        this.timeLineDescList = timeLineDescList;
        this.timeLineDateList = timeLineDateList;
        this.timeLineStatusList = timeLineStatusList;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView dateTV, monthTV, yearTV, titleTV, descTV;
        public ImageView downloadBtn;
        View lineView;

        public MyViewHolder(View view) {
            super(view);
            dateTV = view.findViewById(R.id.adapter_studentTimeline_dateTV);
//            monthTV = view.findViewById(R.id.adapter_studentTimeline_MonthTV);
//            yearTV = view.findViewById(R.id.adapter_studentTimeline_yearTV);
            titleTV = view.findViewById(R.id.adapter_studentTimeline_titleTV);
            descTV = view.findViewById(R.id.adapter_studentTimeline_subtitleTV);
            lineView = view.findViewById(R.id.adapter_studentTimeline_line);
            downloadBtn = view.findViewById(R.id.adapter_studentTimeline_downloadBtn);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_timeline, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.dateTV.setText(Utility.parseDate("yyyy-MM-dd", Utility.getSharedPreferences(context.getApplicationContext(), "dateFormat"), timeLineDateList.get(position)));
//        holder.dateTV.setText(sliceDate(timeLineDateList.get(position), "dd"));
//        holder.monthTV.setText(sliceDate(timeLineDateList.get(position), "MMM"));
//        holder.yearTV.setText(sliceDate(timeLineDateList.get(position), "yyyy"));
        holder.titleTV.setText(timeLineTitleList.get(position));
        holder.descTV.setText(timeLineDescList.get(position));

        context.registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        if(timeLineDocumentList.get(position).equals("")){
            holder.downloadBtn.setVisibility(View.GONE);
        }else{
            holder.downloadBtn.setVisibility(View.VISIBLE);
        }

        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String urlStr = Utility.getSharedPreferences(context.getApplicationContext(), Constants.imagesUrl);
                urlStr += "uploads/student_timeline/"+timeLineDocumentList.get(position);

                downloadID = Utility.beginDownload(context, timeLineDocumentList.get(position), urlStr);

            }
        });

        if(position == timeLineIdList.size()-1) {
            holder.lineView.setVisibility(View.GONE);
        }

    }

    private String sliceDate(String dateStr, String type) {

        try {
            Date date=new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
            SimpleDateFormat sdf; //= new SimpleDateFormat(strDateFormat);

            sdf = new SimpleDateFormat(type);
            String newDate = sdf.format(date);

            return newDate;
        } catch (ParseException pe) {
            return "";
        }
    }

    public BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.notification_logo)
                                .setContentTitle(context.getApplicationContext().getString(R.string.app_name))
                                .setContentText("All Download completed");


                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(455, mBuilder.build());

            }
        }
    };

    @Override
    public int getItemCount() {
        return timeLineIdList.size();
    }
}