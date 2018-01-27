package com.nbn.cloudbasedpatientreferralsystem.patient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nbn.cloudbasedpatientreferralsystem.R;

public class PatientProfileFragment extends Fragment implements View.OnClickListener
{
    private static PatientProfileFragment patientProfileFragment;
    private Button btnEditProfile;
    private Button btnAddPhotos;

    public PatientProfileFragment() {}

    public static PatientProfileFragment getInstance() {
        if(patientProfileFragment!=null) {
            return patientProfileFragment;
        } else {
            return new PatientProfileFragment();
        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_patient_profile, container, false);
        btnEditProfile = (Button) rootView.findViewById(R.id.btn_edit_profile);
        btnAddPhotos = (Button) rootView.findViewById(R.id.btn_add_photos);
        btnAddPhotos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), EditProfilePatient.class);
                startActivity(intent);
            }
        });
        return rootView;

    }

    @Override
    public void onClick(View v)
    {
        Intent addIntent = new Intent(getActivity(),  AddDocument.class);
        startActivity(addIntent);
    }


}
