package com.nbn.cloudbasedpatientreferralsystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;
import com.nbn.cloudbasedpatientreferralsystem.utils.PrefManager;

public class BaseActivity extends AppCompatActivity
{
    protected PrefManager prefManager;
    protected FirebaseDatabase firebaseDatabase;
    protected DatabaseReference rootDatabaseReference;
    protected DatabaseReference databaseReference;
    protected StorageReference rootStorageReference;
    protected StorageReference storageReference;
    protected FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        firebaseDatabase = FirebaseDatabase.getInstance();
        rootDatabaseReference = firebaseDatabase.getReference();
        rootStorageReference = FirebaseStorage.getInstance().getReference();
        user = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = rootStorageReference.child(user.getUid());
    }
}
