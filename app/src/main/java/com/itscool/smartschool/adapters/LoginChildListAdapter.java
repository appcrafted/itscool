package com.itscool.smartschool.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.students.StudentDashboard;
import com.itscool.smartschool.utils.Constants;
import com.itscool.smartschool.utils.Utility;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class LoginChildListAdapter extends RecyclerView.Adapter<LoginChildListAdapter.MyViewHolder> {

    private Activity context;
    private ArrayList<String> childIdList;
    private ArrayList<String> childNameList;
    private ArrayList<String> childClassList;
    private ArrayList<String> childImageList;

    public LoginChildListAdapter(Activity login, ArrayList<String> childIdList, ArrayList<String> childNameList, ArrayList<String> childClassList, ArrayList<String> childImageList) {
        this.context = login;
        this.childIdList = childIdList;
        this.childNameList = childNameList;
        this.childClassList = childClassList;
        this.childImageList = childImageList;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView childNameTV, childClassTV;
        public ImageView childImageIV;
        CardView viewContainer;

        public MyViewHolder(View view) {
            super(view);
            childNameTV = (TextView) view.findViewById(R.id.parent_studentList_studentNameTV);
            childClassTV = (TextView) view.findViewById(R.id.parent_studentList_studentClassTV);
            childImageIV = (ImageView) view.findViewById(R.id.parent_studentList_studentImage);
            viewContainer = (CardView) view.findViewById(R.id.parent_studentList_viewContainer);
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_parent_student_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Log.e("child class adap", childClassList.get(position));
        holder.childNameTV.setText(childNameList.get(position));
        holder.childClassTV.setText(childClassList.get(position));
        String imgUrl = Utility.getSharedPreferences(context, Constants.imagesUrl) + childImageList.get(position);
        Log.e("child image adap", imgUrl);
        Picasso.with(context).load(imgUrl).placeholder(R.drawable.placeholder_user).into(holder.childImageIV);
        holder.viewContainer.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View view) {
        Utility.setSharedPreferenceBoolean(context, "isLoggegIn", true);
        Utility.setSharedPreference(context, Constants.classSection,  childClassList.get(position) );
        Utility.setSharedPreference(context, Constants.studentId, childIdList.get(position));
    //    Utility.setSharedPreference(context, Constants.userId, childIdList.get(position));
        Utility.setSharedPreference(context, "studentName",  childNameList.get(position));

        Intent asd = new Intent(context, StudentDashboard.class);
        context.startActivity(asd);
        context.finish();
        }
        });
        }
@Override
public int getItemCount() {
        return childIdList.size();
        }
        }

