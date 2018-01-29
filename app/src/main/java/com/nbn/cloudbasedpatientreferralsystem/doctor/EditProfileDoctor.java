package com.nbn.cloudbasedpatientreferralsystem.doctor;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nbn.cloudbasedpatientreferralsystem.BaseActivity;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.PatientProfile;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileDoctor extends BaseActivity
{
    //@TODO Retrieve info and set after the activity loads.

    //UI
    private EditText etName;
    private EditText etEmail;
    private EditText etContact;
    private EditText etDob;
    private EditText etSpecialization;
    private CircleImageView imageView;
    private RadioGroup radioGroup;
    private ImageButton btnUpdate;

    //Misc
    private String gender = "";
    private String TAG = getClass().getSimpleName();

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_doctor);
        user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = rootDatabaseReference.child(Constants.ROOT_DOCTORS).child(user.getUid()).child(Constants.DOCTOR_INFO).getRef();
        initLayout();
    }

    private void initLayout()
    {
        etName = (EditText) findViewById(R.id.et_name);
        etEmail = (EditText) findViewById(R.id.et_mail);
        etContact = (EditText) findViewById(R.id.et_contact);
        etDob = (EditText) findViewById(R.id.et_dob);
        etSpecialization = (EditText) findViewById(R.id.et_specializationarea);
        radioGroup = (RadioGroup) findViewById(R.id.rg_gender);
        imageView = (CircleImageView) findViewById(R.id.imageViewHeader);
        btnUpdate = (ImageButton) findViewById(R.id.btn_update);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                if (checkedId == R.id.male)
                {
                    gender = "male";
                } else if (checkedId == R.id.female)
                {
                    gender = "female";
                } else
                {
                    gender = "";
                }
            }
        });

        Log.d(TAG, "initLayout: User" + user.getUid());

//        etEmail.setText(user.getEmail());
//        etName.setText(user.getDisplayName());
//        etContact.setText(user.getPhoneNumber());
//        imageView.setImageURI(user.getPhotoUrl());
        btnUpdate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updateFirebase();
            }
        });
    }

    private void updateFirebase()
    {

        if (!TextUtils.isEmpty(etName.getText().toString()))
        {
            if (!TextUtils.isEmpty(etEmail.getText().toString()))
            {
                if (!TextUtils.isEmpty(etContact.getText().toString()))
                {
                    if (!TextUtils.isEmpty(etDob.getText().toString()))
                    {
                        if (!gender.equals(""))
                        {
                            final DoctorProfile doctorProfile = new DoctorProfile();
                            doctorProfile.setContactNo(etContact.getText().toString());
                            doctorProfile.setDob(etDob.getText().toString());
                            doctorProfile.setEmail(user.getEmail());
                            doctorProfile.setFirebaseId(user.getUid());
                            doctorProfile.setGender(gender);
                            doctorProfile.setName(etName.getText().toString());
                            doctorProfile.setPhotoURL("");
                            doctorProfile.setSpecialization(etSpecialization.getText().toString());

                            Log.d(TAG, "updateFirebase: Doctor :: " + doctorProfile.toString());
                            Log.d(TAG, "updateFirebase: databaseRef :: "+databaseReference.toString());
//                            databaseRef.setValue(doctorProfile);

                            databaseReference.setValue(doctorProfile).addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        rootDoctorProfile = new DoctorProfile(doctorProfile);
                                        finish();
                                    }
                                    else
                                    {
                                        Log.d(TAG, "onComplete: "+task.isComplete());
                                        Log.d(TAG, "onComplete: " + task.getException());
                                        finish();
                                    }
                                }

                            });
//                            myRef.child(Constants.ROOT_PATIENTS).child(Constants.PATIENT_INFO).setValue(patient);

                        } else
                        {
                            Toast.makeText(this, "Please select the gender", Toast.LENGTH_SHORT).show();
                        }

                    } else
                    {
                        Toast.makeText(this, "Date of Birth can not be empty", Toast.LENGTH_SHORT).show();
                    }
                } else
                {
                    Toast.makeText(this, "Contact can not be empty", Toast.LENGTH_SHORT).show();
                }
            } else
            {
                Toast.makeText(this, "Email Id can not be empty", Toast.LENGTH_SHORT).show();
            }
        } else
        {
            Toast.makeText(this, "Name can not be empty", Toast.LENGTH_SHORT).show();
        }
    }

}
