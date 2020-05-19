package com.itscool.smartschool.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.itscool.smartschool.R;
import com.itscool.smartschool.adapters.StudentProfileAdapter;
import com.itscool.smartschool.utils.Utility;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;

public class StudentProfileParentFragment extends Fragment {
    TextView fatherName, fatherContact, fatherOccupation;
    TextView motherName, motherContact, motherOccupation;
    TextView guardianName, guardianContact, guardianEmail,guardianRelation, guardianOccupation, guardianAddress;
    ImageView fatherIV, motherIV, guardianIV;
    StudentProfileAdapter adapter;
    ArrayList<String> father_name= new ArrayList<String>();
    HashMap<String, String> parentsValues = new HashMap<>();


    public static StudentProfileParentFragment newInstance(HashMap<String,String> parentsValues) {
        StudentProfileParentFragment personalFragment = new StudentProfileParentFragment();
        Bundle args = new Bundle();
        args.putSerializable("data", parentsValues);
        personalFragment.setArguments(args);
        return personalFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentsValues = (HashMap<String, String>) getArguments().getSerializable("data");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View mainView = inflater.inflate(R.layout.fragment_student_profile_parents, container, false);

        fatherName = mainView.findViewById(R.id.student_profile_parent_fatherName);
        fatherContact = mainView.findViewById(R.id.student_profile_parent_fatherContact);
        fatherOccupation = mainView.findViewById(R.id.student_profile_parent_fatherOccupation);
        fatherIV = mainView.findViewById(R.id.student_profile_parent_fatherImage);

        motherName = mainView.findViewById(R.id.student_profile_parent_motherName);
        motherContact = mainView.findViewById(R.id.student_profile_parent_motherContact);
        motherOccupation = mainView.findViewById(R.id.student_profile_parent_motherOccupation);
        motherIV = mainView.findViewById(R.id.student_profile_parent_motherImage);

        guardianName = mainView.findViewById(R.id.student_profile_parent_guardianName);
        guardianContact = mainView.findViewById(R.id.student_profile_parent_guardianContact);
        guardianOccupation = mainView.findViewById(R.id.student_profile_parent_guardianOccupation);
        guardianEmail = mainView.findViewById(R.id.student_profile_parent_guardianEmail);
        guardianRelation = mainView.findViewById(R.id.student_profile_parent_guardianRelation);
        guardianAddress = mainView.findViewById(R.id.student_profile_parent_guardianAddress);
        guardianIV = mainView.findViewById(R.id.student_profile_parent_guardianImage);

        fatherName.setText(parentsValues.get("father_name"));
        fatherContact.setText(parentsValues.get("father_phone"));
        fatherOccupation.setText(parentsValues.get("father_occupation"));

        motherName.setText(parentsValues.get("mother_name"));
        motherContact.setText(parentsValues.get("mother_phone"));
        motherOccupation.setText(parentsValues.get("mother_occupation"));

        guardianName.setText(parentsValues.get("guardian_name"));
        guardianContact.setText(parentsValues.get("guardian_phone"));
        guardianOccupation.setText(parentsValues.get("guardian_occupation"));
        guardianEmail.setText(parentsValues.get("guardian_email"));
        guardianRelation.setText(parentsValues.get("guardian_relation"));
        guardianAddress.setText(parentsValues.get("guardian_address"));

        String fatherImage = Utility.getSharedPreferences(getActivity().getApplicationContext(), "imagesUrl") + parentsValues.get("father_image");
        String motherImage = Utility.getSharedPreferences(getActivity().getApplicationContext(), "imagesUrl") + parentsValues.get("mother_image");
        String guardianImage = Utility.getSharedPreferences(getActivity().getApplicationContext(), "imagesUrl") + parentsValues.get("guardian_image");

        Log.e("Father Image", fatherImage);
        Log.e("Mother Image", motherImage);
        Log.e("Guardian Image", guardianImage);

        Picasso.with(getActivity()).load(fatherImage).placeholder(R.drawable.placeholder_user).into(fatherIV);
        Picasso.with(getActivity()).load(motherImage).placeholder(R.drawable.placeholder_user).into(motherIV);
        Picasso.with(getActivity()).load(guardianImage).placeholder(R.drawable.placeholder_user).into(guardianIV);
        return mainView;
    }
}
