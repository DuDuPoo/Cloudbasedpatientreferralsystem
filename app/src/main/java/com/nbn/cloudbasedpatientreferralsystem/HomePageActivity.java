package com.nbn.cloudbasedpatientreferralsystem;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.nbn.cloudbasedpatientreferralsystem.doctor.DoctorProfileFragment;
import com.nbn.cloudbasedpatientreferralsystem.patient.PatientProfileFragment;

import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.*;

public class HomePageActivity extends BaseActivity
{
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
                /*if (position == 0)
                {*/
                    return PatientProfileFragment.getInstance();
//                }
            } else if (prefManager.getProfile().equals(VALUE_LOGIN_INTENT_DOCTOR))
            {
                /*if (position == 0)
                {*/
                    return DoctorProfileFragment.getInstance();
//                }
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
                    return "Doctors";
            }
            return null;
        }

    }
}
