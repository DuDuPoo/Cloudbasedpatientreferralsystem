package com.nbn.cloudbasedpatientreferralsystem;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.base.BaseActivity;
import com.nbn.cloudbasedpatientreferralsystem.chats.ChatListFragment;
import com.nbn.cloudbasedpatientreferralsystem.chats.NotificationService;
import com.nbn.cloudbasedpatientreferralsystem.doctor.DoctorProfileFragment;
import com.nbn.cloudbasedpatientreferralsystem.patient.PatientProfileFragment;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import java.util.ArrayList;

import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.*;

public class HomePageActivity extends BaseActivity
{
    String TAG = getClass().getSimpleName();
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.setupWithViewPager(mViewPager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        startNotificationService();
    }

    private void startNotificationService() {
        final Context context = this;
        rootDatabaseReference
                .child(USER_CHATS)
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        ArrayList<String> userChats = new ArrayList<>();
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            Log.d(TAG, "onDataChange: "+ds.getValue(String.class));
                            userChats.add(ds.getValue(String.class));
                        }
                        if(userChats.size()>0) {
                            Intent serviceIntent = new Intent(context, NotificationService.class);
                            serviceIntent.putStringArrayListExtra(NOTIFICATION_SERVICE_ARRAY, userChats);
                            startService(serviceIntent);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.action_signout)
        {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {

        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int position)
        {
            /*
            *  If the pref.getProfile() is "patient"
            *       if the position == 0
            *           then load PatientProfileFragment()
            *       else
            *           load chat recycler for patient(which will show the list of doctors)
            *  else (assuming the pref.getProfile is "doctor")
            *       if the position == 0
            *           then load DoctorProfileFragment()\
            *       else
            *           load chat recycler for doctors(which will show the list of patients)
            * */

            if (prefManager.getProfile().equals(VALUE_LOGIN_INTENT_PATIENT))
            {
                if (position == 0)
                {
                    return PatientProfileFragment.getInstance();
                } else if (position == 1)
                {
                    return ChatListFragment.getInstance(prefManager.getProfile());
                }
            } else if (prefManager.getProfile().equals(VALUE_LOGIN_INTENT_DOCTOR))
            {
                if (position == 0)
                {
                    return DoctorProfileFragment.getInstance();
                } else if (position == 1)
                {
                    return ChatListFragment.getInstance(prefManager.getProfile());
                }
            }
            //@TODO Create a error fragment which will show something went wrong and return it from here.
            return null;
        }

        @Override
        public int getCount()
        {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "Profile";
                case 1:
                    if (prefManager.getProfile().equals(Constants.VALUE_LOGIN_INTENT_DOCTOR))
                        return "Patients";
                    return "Doctors";
            }
            return null;
        }

    }
}
