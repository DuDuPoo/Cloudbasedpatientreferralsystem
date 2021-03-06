package com.nbn.cloudbasedpatientreferralsystem.doctor;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.ViewProfileActivity;
import com.nbn.cloudbasedpatientreferralsystem.base.BaseFragment;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Random;

import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.KEY_VIEW_ID;


public class DoctorProfileFragment extends BaseFragment
{

    String TAG = getClass().getSimpleName();
    private static DoctorProfileFragment doctorProfileFragment;
    private ImageButton btnEditProfile;
    private TextView tvProfile;
    private ImageButton ivEmergency;
    final String url = "tcp://iot.eclipse.org:1883";
    MqttAndroidClient mqttAndroidClient;

    public DoctorProfileFragment()
    {
        // Required empty public constructor.
    }

    public static DoctorProfileFragment getInstance()
    {
        if (doctorProfileFragment != null)
        {
            return doctorProfileFragment;
        } else
        {
            return new DoctorProfileFragment();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_doctor_profile, container, false);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = rootDatabaseReference.child(Constants.ROOT_DOCTORS).child(user.getUid()).child(Constants.DOCTOR_INFO).getRef();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Log.d(TAG, "onDataChange: ");
                if (dataSnapshot.getValue(DoctorProfile.class) == null)
                {
                    Log.d(TAG, "onDataChange: It's null");
                    Toast.makeText(getActivity(), "Your profile is empty, we suggest you to edit \" +\n" +
                            "                            \"and update your profile as a doctor, otherwise it won't be visible to anyone", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getActivity(), EditProfileDoctor.class);
                    startActivity(intent);
                } else
                {
                    init(rootView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
        return rootView;
    }

    private void init(View rootView)
    {
        String android_id = "";
        if (getActivity() != null)
            android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        if (android_id == null)
        {
            android_id = String.valueOf(new Random(10000).nextInt());
        }
        mqttAndroidClient = new MqttAndroidClient(getContext(), url, android_id);
        connectToPublisher();
        tvProfile = (TextView) rootView.findViewById(R.id.tv_profile);
        btnEditProfile = (ImageButton) rootView.findViewById(R.id.btn_edit_profile);
        ivEmergency = (ImageButton) rootView.findViewById(R.id.emergency);
        tvProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Go to ViewPatientProfileActivity
                Intent i = new Intent(getActivity(), ViewProfileActivity.class);
                Bundle b = new Bundle();
                b.putString(Constants.KEY_VIEW_ID, FirebaseAuth.getInstance().getCurrentUser().getUid());
                b.putString(Constants.KEY_LOGIN_INTENT, Constants.VALUE_LOGIN_INTENT_DOCTOR);
                i.putExtra(Constants.KEY_BUNDLE, b);
                startActivity(i);
            }
        });
        ivEmergency.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                subscribe();
            }
        });
        btnEditProfile.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getActivity(), EditProfileDoctor.class);
                startActivity(intent);
            }
        });

    }

    private void connectToPublisher()
    {

        mqttAndroidClient.setCallback(new MqttCallback()
        {
            @Override
            public void connectionLost(Throwable cause)
            {
                Log.d(TAG, "connectionLost: ");
                Toast.makeText(getActivity(), "We have lost connection, please try again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception
            {
                Log.d(TAG, "messageArrived: " + topic + " : " + message.toString());
                Intent i = new Intent(getActivity(), ViewProfileActivity.class);
                Bundle b = new Bundle();
                b.putString(KEY_VIEW_ID, message.toString());
                b.putString(Constants.KEY_LOGIN_INTENT, Constants.VALUE_LOGIN_INTENT_PATIENT);
                i.putExtra(Constants.KEY_BUNDLE, b);
                startActivity(i);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token)
            {

            }
        });
    }

    private synchronized void subscribe()
    {
        try
        {
            mqttAndroidClient.connect(null, new IMqttActionListener()
            {
                @Override
                public void onSuccess(IMqttToken asyncActionToken)
                {
                    try
                    {
                        int qos = 0;
                        mqttAndroidClient.subscribe("get_patient", qos);
                        Log.d(TAG, "onSuccess: ");

                    } catch (MqttException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                {
                    exception.printStackTrace();
                    Toast.makeText(getActivity(), "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e)
        {
            e.printStackTrace();
        }
    }

}
