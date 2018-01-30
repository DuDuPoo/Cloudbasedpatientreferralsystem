package com.nbn.cloudbasedpatientreferralsystem.chats;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends Fragment
{
    /*
    * Upload the thread first and then get the key of the thread
    * Add the same key to the active chats in the patient_info thread so that to show in active chats
    *
    * */

    /*
    * Each member will have its own CHAT(Along with info and photos) thread in which all the chatUID
    * will be listed.
    * chatMessages will be another thread alltogether with patients and doctors.
    *   chatMessages will contain chatUID followed by messages
    * */

    private RecyclerView recyclerView;
    private ImageButton btnSearchDoctors;
    private static ChatListFragment chatListFragment;
    private String type;
    private static final String ARG_PARAM = "param";


    public ChatListFragment()
    {
        // Required empty public constructor
    }

    public static ChatListFragment getInstance(String parameter) {
        if(chatListFragment!=null) {
            return chatListFragment;
        } else {
            ChatListFragment fragment = new ChatListFragment();
            Bundle args = new Bundle();
            args.putString(ARG_PARAM, parameter);
            fragment.setArguments(args);
            return fragment;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        Bundle bundle = getArguments();
        type = bundle.getString(ARG_PARAM);
        if(type.equals(Constants.VALUE_LOGIN_INTENT_PATIENT))
        {
            View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
            recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
            btnSearchDoctors = (ImageButton) v.findViewById(R.id.btn_search_doctors);
            btnSearchDoctors.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(getActivity(), DoctorListActivity.class);
                    startActivity(intent);
                }
            });
            return v;
        } else {
            View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
            recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
            return v;
        }
    }
}
