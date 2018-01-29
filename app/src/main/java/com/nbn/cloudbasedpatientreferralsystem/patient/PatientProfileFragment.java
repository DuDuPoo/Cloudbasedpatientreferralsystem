package com.nbn.cloudbasedpatientreferralsystem.patient;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nbn.cloudbasedpatientreferralsystem.BaseActivity;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DocumentInfo;
import com.nbn.cloudbasedpatientreferralsystem.pojo.PatientProfile;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import java.util.ArrayList;

public class PatientProfileFragment extends Fragment
{
    private static PatientProfileFragment patientProfileFragment;
    private Button btnEditProfile;
    private Button btnAddPhotos;
    private TextView tvProfile;
    private RecyclerView recyclerView;
    private CustomRecyclerAdapter adapter;
    private ArrayList<DocumentInfo> docs;

    String TAG = getClass().getSimpleName();

    public PatientProfileFragment()
    {
    }

    public static PatientProfileFragment getInstance()
    {
        if (patientProfileFragment != null)
        {
            return patientProfileFragment;
        } else
        {
            return new PatientProfileFragment();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        docs = new ArrayList<>();
        View rootView = inflater.inflate(R.layout.fragment_patient_profile, container, false);
        btnEditProfile = (Button) rootView.findViewById(R.id.btn_edit_profile);
        tvProfile = (TextView) rootView.findViewById(R.id.tv_profile);
        btnAddPhotos = (Button) rootView.findViewById(R.id.btn_add_photos);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        btnAddPhotos.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent addIntent = new Intent(getActivity(), AddDocument.class);
                startActivity(addIntent);
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
        new GetProfileTask().execute();
        return rootView;
    }

    private class GetProfileTask extends AsyncTask<String, Void, Void>
    {
        FirebaseDatabase firebaseDatabase;
        DatabaseReference rootDatabaseReference;
        DatabaseReference databaseReference;
        DatabaseReference docsDatabaseReference;
        FirebaseUser user;
        PatientProfile patientProfile;

        GetProfileTask() {
            firebaseDatabase = FirebaseDatabase.getInstance();
            rootDatabaseReference = firebaseDatabase.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = rootDatabaseReference.child(Constants.ROOT_PATIENTS).child(user.getUid()).child(Constants.PATIENT_INFO).getRef();
            docsDatabaseReference = rootDatabaseReference.child(Constants.ROOT_PATIENTS).child(user.getUid()).child(Constants.PATIENT_DOCS).getRef();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            docsDatabaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    docs.clear();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        DocumentInfo doc = postSnapshot.getValue(DocumentInfo.class);
                        docs.add(doc);
                    }
                    if(getActivity()!=null) {
                        adapter = new CustomRecyclerAdapter(getActivity(), docs);
                        recyclerView.setAdapter(adapter);
                        //@TODO Set Progress bar here
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });

            databaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    patientProfile = dataSnapshot.getValue(PatientProfile.class);
                    tvProfile.setText(patientProfile.toString());
                    Log.d(TAG, "onCreateView: "+patientProfile.toString());
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
