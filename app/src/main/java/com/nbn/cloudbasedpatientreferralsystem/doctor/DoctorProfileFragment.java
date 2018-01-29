package com.nbn.cloudbasedpatientreferralsystem.doctor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.HomePageActivity;
import com.nbn.cloudbasedpatientreferralsystem.MainActivity;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.patient.EditProfilePatient;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;


public class DoctorProfileFragment extends Fragment
{

    String TAG = getClass().getSimpleName();
    private static DoctorProfileFragment doctorProfileFragment;
    private Button btnEditProfile;
    private TextView tvProfile;

    public DoctorProfileFragment()
    {
        // Required empty public constructor.
    }

    public static DoctorProfileFragment getInstance() {
        if(doctorProfileFragment!=null) {
            return doctorProfileFragment;
        } else {
            return new DoctorProfileFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_doctor_profile, container, false);
        tvProfile = (TextView) rootView.findViewById(R.id.tv_profile);
        btnEditProfile = (Button) rootView.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), EditProfileDoctor.class);
                startActivity(intent);
            }
        });
        new GetProfileTask(Constants.VALUE_LOGIN_INTENT_DOCTOR).execute();
        return rootView;
    }

    private class GetProfileTask extends AsyncTask<String, Void, Void> {

        String profile;
        FirebaseDatabase firebaseDatabase;
        DatabaseReference rootDatabaseReference;
        DatabaseReference databaseReference;
        FirebaseUser user;
        DoctorProfile doctorProfile;

        GetProfileTask(String profile) {
            this.profile = profile;
            firebaseDatabase = FirebaseDatabase.getInstance();
            rootDatabaseReference = firebaseDatabase.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            if(profile.equals(Constants.VALUE_LOGIN_INTENT_DOCTOR))
                databaseReference = rootDatabaseReference.child(Constants.ROOT_DOCTORS).child(user.getUid()).child(Constants.DOCTOR_INFO).getRef();
            else
                databaseReference = rootDatabaseReference.child(Constants.ROOT_PATIENTS).child(user.getUid()).child(Constants.PATIENT_INFO).getRef();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            databaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    doctorProfile = dataSnapshot.getValue(DoctorProfile.class);
                    tvProfile.setText(doctorProfile.toString());
                    Log.d(TAG, "onCreateView: "+doctorProfile.toString());
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
            return null;
        }


    }
}
