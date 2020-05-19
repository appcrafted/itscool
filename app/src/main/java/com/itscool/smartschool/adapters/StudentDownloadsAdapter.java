package com.itscool.smartschool.adapters;

import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.itscool.smartschool.R;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;

import java.util.ArrayList;

public class StudentDownloadsAdapter extends BaseAdapter {

    private FragmentActivity context;
    private ArrayList<String> idList;
    private ArrayList<String> nameList;
    private ArrayList<String> dateList;
    private ArrayList<String> descList;
    private ArrayList<String> urlList;

    private long downloadID;

    public StudentDownloadsAdapter(FragmentActivity activity, ArrayList<String> idList, ArrayList<String> nameList,
                                   ArrayList<String> dateList, ArrayList<String> urlList, ArrayList<String> descList) {

        this.context = activity;
        this.idList = idList;
        this.nameList = nameList;
        this.dateList = dateList;
        this.descList = descList;
        this.urlList = urlList;
    }

    @Override
    public int getCount() {
        return idList.size();
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
            rowView = inflater.inflate(R.layout.adapter_fragment_studentdownload, viewGroup, false);
            viewHolder = new ViewHolder();

            viewHolder.viewContainer = (CardView) rowView.findViewById(R.id.adapter_fragment_studentdownload_syllabus);
            viewHolder.nameTV = (TextView) rowView.findViewById(R.id.adapter_fragment_studentdownload_syllabus_nameTV);
            viewHolder.dateTV = (TextView) rowView.findViewById(R.id.adapter_fragment_studentdownload_syllabus_dateTV);
            viewHolder.descTV = (TextView) rowView.findViewById(R.id.adapter_fragment_studentdownload_syllabus_descTV);

            viewHolder.downloadBtn = (ImageView) rowView.findViewById(R.id.adapter_fragment_studentdownload_syllabus_downloadBtn);

            viewHolder.nameLay = (RelativeLayout) rowView.findViewById(R.id.adapter_fragment_nameLay);

            viewHolder.viewContainer.setTag(position);
            viewHolder.nameTV.setTag(position);
            viewHolder.dateTV.setTag(position);
            viewHolder.descTV.setTag(position);

            context.registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        } else {
            viewHolder = (ViewHolder) rowView.getTag();
        }

        viewHolder.nameTV.setText(nameList.get(position));
        viewHolder.dateTV.setText(dateList.get(position));
        viewHolder.descTV.setText(descList.get(position));

        viewHolder.downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String urlStr = Utility.getSharedPreferences(context.getApplicationContext(), Constants.imagesUrl);
                urlStr += "/"+urlList.get(position);
                downloadID = Utility.beginDownload(context, urlList.get(position), urlStr);

            }
        });

        viewHolder.nameLay.setBackgroundColor(Color.parseColor(Utility.getSharedPreferences(context.getApplicationContext(), Constants.secondaryColour)));

        rowView.setTag(viewHolder);
        return rowView;

    }


    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
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


    private static class ViewHolder {
        private TextView nameTV, dateTV, descTV;
        private ImageView downloadBtn;
        private RelativeLayout nameLay;
        private CardView viewContainer;
    }



}