package com.nbn.cloudbasedpatientreferralsystem.chats;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.BaseActivity;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import java.util.ArrayList;

public class DoctorListActivity extends BaseActivity
{

    private RecyclerView recyclerView;
    private DoctorListAdapter adapter;
    private ArrayList<DoctorProfile> doctors;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        doctors = new ArrayList<>();
        databaseReference = rootDatabaseReference
                .child(Constants.ROOT_DOCTORS)
                .getRef();
        new GetDoctorsTask(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class GetDoctorsTask extends AsyncTask<Void, Void, Void> {
        String TAG = getClass().getSimpleName();
        Context context;
        GetDoctorsTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            databaseReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    doctors.clear();
                    Log.d(TAG, "onDataChange: "+dataSnapshot.getChildrenCount());
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        DoctorProfile dp = postSnapshot.child(Constants.DOCTOR_INFO).getValue(DoctorProfile.class);
                        Log.i(TAG, "onDataChange: "+dp);
                        doctors.add(dp);
                    }
                    if(doctors.size()>0) {
                     adapter = new DoctorListAdapter(context, doctors);

                        recyclerView.setLayoutManager(new LinearLayoutManager(context));
                        recyclerView.setAdapter(adapter);
                        //@TODO Set Progress bar here
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
