package com.nbn.cloudbasedpatientreferralsystem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.base.BaseActivity;
import com.nbn.cloudbasedpatientreferralsystem.patient.CustomRecyclerAdapter;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DocumentInfo;
import com.nbn.cloudbasedpatientreferralsystem.pojo.PatientProfile;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.*;

public class ViewProfileActivity extends BaseActivity
{
    String TAG = getClass().getSimpleName();
    String uid = "";
    FirebaseUser firebaseUser;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_contact)
    TextView tvContact;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_dob)
    TextView tvDob;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    LinearLayoutManager linearLayoutManager;
    private ArrayList<DocumentInfo> docs = new ArrayList<>();
    private CustomRecyclerAdapter adapter;

    private DoctorProfile doctorProfile;
    private PatientProfile patientProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        ButterKnife.bind(this);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        Intent i = getIntent();
        Bundle b = i.getBundleExtra(Constants.KEY_BUNDLE);
        String intentString = b.getString(Constants.KEY_LOGIN_INTENT);
        uid = b.getString(Constants.KEY_VIEW_ID);
        Log.d(TAG, "onCreate: " + uid);
        final ProgressDialog dialog = ProgressDialog.show(this, "",
                "Loading. Please wait...", true);
        dialog.show();
        Log.d(TAG, "onCreate: Intent String :: "+intentString);
        if(intentString!=null) {
            if(intentString.equals(VALUE_LOGIN_INTENT_PATIENT)) {
                recyclerView.setVisibility(View.VISIBLE);
                rootDatabaseReference.child(ROOT_PATIENTS).child(uid).child(PATIENT_INFO).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.getValue(PatientProfile.class) != null)
                        {
                            patientProfile = dataSnapshot.getValue(PatientProfile.class);
                            if (patientProfile != null)
                            {
                                tvName.setText("Name : " + patientProfile.getName());
                                tvContact.setText("Contact : " + patientProfile.getContactNo());
                                tvEmail.setText("Email ID : " + patientProfile.getEmail());
                                tvGender.setText("Gender : " + patientProfile.getGender());
                                tvDob.setText("Date of Birth : " + patientProfile.getDob());
                                Log.d(TAG, "onDataChange: " + patientProfile);
                            }
                            rootDatabaseReference
                                    .child(ROOT_PATIENTS)
                                    .child(uid)
                                    .child(PATIENT_DOCS)
                                    .addValueEventListener(new ValueEventListener()
                                    {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot)
                                        {
                                            docs.clear();
                                            Log.d(TAG, "onPhotoDataChange: " + dataSnapshot.getChildrenCount());
                                            for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                                            {
                                                DocumentInfo doc = postSnapshot.getValue(DocumentInfo.class);
                                                docs.add(doc);
                                            }
                                            adapter = new CustomRecyclerAdapter(ViewProfileActivity.this, docs);
                                            recyclerView.setAdapter(adapter);
                                            //@TODO Set Progress bar here
                                            dialog.hide();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError)
                                        {

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            } else {
                rootDatabaseReference.child(ROOT_DOCTORS).child(uid).child(DOCTOR_INFO).addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.getValue(DoctorProfile.class) != null)
                        {
                            doctorProfile = dataSnapshot.getValue(DoctorProfile.class);
                            if (doctorProfile != null)
                            {
                                tvName.setText("Name : " + doctorProfile.getName());
                                tvContact.setText("Contact : " + doctorProfile.getContactNo());
                                tvEmail.setText("Email ID : " + doctorProfile.getEmail());
                                tvGender.setText("Gender : " + doctorProfile.getGender());
                                tvDob.setText("Specialization : " + doctorProfile.getSpecialization());
                            }
                            recyclerView.setVisibility(View.INVISIBLE);
                            dialog.hide();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
        }




        /*if (patientProfile != null && doctorProfile != null)
        {
            Log.d(TAG, "onCreate: Found in both the roots");
        } else if (patientProfile != null)
        {
            recyclerView.setVisibility(View.VISIBLE);
            initImages();
        } else if (doctorProfile != null)
        {
            initDoctorProfile();
        }*/

    }

    private void initDoctorProfile()
    {
        new GetDoctorProfileTask().execute();
    }

    private void initImages()
    {
        new GetProfileTask().execute();
    }


    private class GetProfileTask extends AsyncTask<String, Void, Void>
    {
        FirebaseDatabase firebaseDatabase;
        DatabaseReference rootDatabaseReference;
        DatabaseReference databaseReference;
        DatabaseReference docsDatabaseReference;
        FirebaseUser user;

        GetProfileTask()
        {
            firebaseDatabase = FirebaseDatabase.getInstance();
            rootDatabaseReference = firebaseDatabase.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = rootDatabaseReference.child(Constants.ROOT_PATIENTS).child(uid).child(Constants.PATIENT_INFO).getRef();
            docsDatabaseReference = rootDatabaseReference.child(Constants.ROOT_PATIENTS).child(uid).child(Constants.PATIENT_DOCS).getRef();
        }

        @Override
        protected Void doInBackground(String... params)
        {
            databaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    PatientProfile patientProfile = dataSnapshot.getValue(PatientProfile.class);
                    if (patientProfile != null)
                    {
                        tvName.setText("Name : " + patientProfile.getName());
                        tvContact.setText("Contact : " + patientProfile.getContactNo());
                        tvEmail.setText("Email ID : " + patientProfile.getEmail());
                        tvGender.setText("Gender : " + patientProfile.getGender());
                        tvDob.setText("Date of Birth : " + patientProfile.getDob());
                        Log.d(TAG, "onDataChange: " + patientProfile);
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
            docsDatabaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    docs.clear();
                    Log.d(TAG, "onPhotoDataChange: " + dataSnapshot.getChildrenCount());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        DocumentInfo doc = postSnapshot.getValue(DocumentInfo.class);
                        docs.add(doc);
                    }
                    adapter = new CustomRecyclerAdapter(ViewProfileActivity.this, docs);
                    recyclerView.setAdapter(adapter);
                    //@TODO Set Progress bar here

                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });

            return null;
        }


    }


    private class GetDoctorProfileTask extends AsyncTask<String, Void, Void>
    {

        FirebaseDatabase firebaseDatabase;
        DatabaseReference rootDatabaseReference;
        DatabaseReference databaseReference;
        FirebaseUser user;
        DoctorProfile doctorProfile;

        GetDoctorProfileTask()
        {
            firebaseDatabase = FirebaseDatabase.getInstance();
            rootDatabaseReference = firebaseDatabase.getReference();
            user = FirebaseAuth.getInstance().getCurrentUser();
            databaseReference = rootDatabaseReference.child(Constants.ROOT_DOCTORS).child(uid).child(Constants.DOCTOR_INFO).getRef();
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
                    Log.d(TAG, "onDataChange: " + doctorProfile);
                    if (doctorProfile != null)
                    {
                        tvName.setText("Name : " + doctorProfile.getName());
                        tvContact.setText("Contact : " + doctorProfile.getContactNo());
                        tvEmail.setText("Email ID : " + doctorProfile.getEmail());
                        tvGender.setText("Gender : " + doctorProfile.getGender());
                        tvDob.setText("Specialization : " + doctorProfile.getSpecialization());
                    }

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
