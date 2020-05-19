package com.itscool.smartschool.adapters;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentDocuments;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

import java.util.ArrayList;

public class StudentDocumentsAdapter extends RecyclerView.Adapter<StudentDocumentsAdapter.MyViewHolder> {

    private Context context;
    ArrayList<String> docTitleList;
    ArrayList<String> docUrlList;

    long downloadID;


    public StudentDocumentsAdapter(StudentDocuments studentDocuments, ArrayList<String> docTitleList, ArrayList<String> docUrlList) {
        this.context = studentDocuments;
        this.docTitleList = docTitleList;
        this.docUrlList = docUrlList;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView titleTV, fileNameTV;
        LinearLayout downloadBtn;
        RelativeLayout header;

        public MyViewHolder(View view) {
            super(view);
            titleTV = (TextView) view.findViewById(R.id.adapter_student_documents_documentNameTV);
            fileNameTV = (TextView) view.findViewById(R.id.adapter_student_documents_fileNameTV);
            downloadBtn = (LinearLayout) view.findViewById(R.id.adapter_student_documents_downloadBtn);
            header = (RelativeLayout) view.findViewById(R.id.adapter_student_documents_header);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_student_documents, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        //DECORATE
        holder.header.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));

        holder.titleTV.setText(docTitleList.get(position));
        holder.fileNameTV.setText(docUrlList.get(position));

        context.registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));


        holder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String urlStr = Utility.getSharedPreferences(context.getApplicationContext(), Constants.imagesUrl);
                urlStr += "/uploads/student_documents/"+Utility.getSharedPreferences(context, Constants.studentId)+"/"+docUrlList.get(position);
                downloadID = Utility.beginDownload(context, docUrlList.get(position), urlStr);
            }
        });

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
        return docTitleList.size();
    }
}
