package com.nbn.cloudbasedpatientreferralsystem.doctor;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.patient.EditProfilePatient;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DoctorProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class DoctorProfileFragment extends Fragment
{

    private OnFragmentInteractionListener mListener;
    private static DoctorProfileFragment doctorProfileFragment;
    private Button btnEditProfile;


    public DoctorProfileFragment()
    {
        // Required empty public constructor
    }

    public static DoctorProfileFragment getInstance() {
        if(doctorProfileFragment!=null) {
            return doctorProfileFragment;
        } else {
            return new DoctorProfileFragment();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_doctor_profile, container, false);
        btnEditProfile = (Button) rootView.findViewById(R.id.btn_edit_profile);
        btnEditProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), EditProfileDoctor.class);
                startActivity(intent);
            }
        });
        return rootView;
    }


/*

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener)
        {
            mListener = (OnFragmentInteractionListener) context;
        } else
        {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }
*/

    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
