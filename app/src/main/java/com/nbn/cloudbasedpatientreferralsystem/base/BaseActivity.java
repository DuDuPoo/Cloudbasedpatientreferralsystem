package com.nbn.cloudbasedpatientreferralsystem.base;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.PatientProfile;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;
import com.nbn.cloudbasedpatientreferralsystem.utils.PrefManager;

public class BaseActivity extends AppCompatActivity
{
    protected PrefManager prefManager;
    protected FirebaseDatabase firebaseDatabase;
    protected DatabaseReference rootDatabaseReference;
    protected DatabaseReference databaseReference;
    protected StorageReference rootStorageReference;
    public static PatientProfile rootPatientProfile;
    public static DoctorProfile rootDoctorProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootDatabaseReference = firebaseDatabase.getReference();
        rootStorageReference = FirebaseStorage.getInstance().getReference();
    }

    public DatabaseReference getRootDatabaseReference()
    {
        return rootDatabaseReference;
    }

    public void setRootDatabaseReference(DatabaseReference rootDatabaseReference)
    {
        this.rootDatabaseReference = rootDatabaseReference;
    }

    /*
    * When user goes to HomePageActivity with haphazard profile,
    * 1- Ask him to update his profile by using editProfile button.
    * 2- Map the object created in EditDocProfile/EditPatientProfile to the
    *    object from BaseActivity.
    * */

    //@TODO Do not allow patient to upload document without filling the complete profile.
}
