package com.nbn.cloudbasedpatientreferralsystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.VALUE_LOGIN_INTENT_DOCTOR;
import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.VALUE_LOGIN_INTENT_PATIENT;

public class MainActivity extends BaseActivity
{

    private CardView cvPatient;
    private CardView cvDoctor;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cvPatient = (CardView) findViewById(R.id.patientCard);
        cvDoctor = (CardView) findViewById(R.id.doctorCard);

        cvPatient.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, CheckUserActivity.class);
                prefManager.setProfile(VALUE_LOGIN_INTENT_PATIENT);
                startActivity(intent);
            }
        });

        cvDoctor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, CheckUserActivity.class);
                prefManager.setProfile(VALUE_LOGIN_INTENT_DOCTOR);
                startActivity(intent);
            }
        });
    }
}
