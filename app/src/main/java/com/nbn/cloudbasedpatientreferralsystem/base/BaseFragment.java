package com.nbn.cloudbasedpatientreferralsystem.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.PatientProfile;
import com.nbn.cloudbasedpatientreferralsystem.utils.PrefManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class BaseFragment extends Fragment
{

    protected PrefManager prefManager;
    protected FirebaseDatabase firebaseDatabase;
    protected DatabaseReference rootDatabaseReference;
    protected DatabaseReference databaseReference;
    protected StorageReference rootStorageReference;
    public static PatientProfile rootPatientProfile;
    public static DoctorProfile rootDoctorProfile;
    public BaseFragment()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(getActivity());
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootDatabaseReference = firebaseDatabase.getReference();
        rootStorageReference = FirebaseStorage.getInstance().getReference();
    }
}
