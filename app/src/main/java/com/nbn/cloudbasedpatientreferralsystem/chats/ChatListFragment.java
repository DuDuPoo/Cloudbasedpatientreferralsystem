package com.nbn.cloudbasedpatientreferralsystem.chats;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.base.BaseFragment;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.chats.messages.ChatListAdapter;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatUID;
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import java.util.ArrayList;

import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.CHATS;
import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.USER_CHATS;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatListFragment extends BaseFragment
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
    private LinearLayoutManager linearLayoutManager;
    private ChatListAdapter adapter;

    private String type;
    private String TAG = getClass().getSimpleName();
    private static final String ARG_PARAM = "param";
    private ArrayList<ChatUID> chats = new ArrayList<>();

    private DatabaseReference userChatsRef;
    private FirebaseUser user;


    public ChatListFragment()
    {
        // Required empty public constructor
    }

    public static ChatListFragment getInstance(String parameter)
    {
        if (chatListFragment != null)
        {
            return chatListFragment;
        } else
        {
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
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        btnSearchDoctors = (ImageButton) v.findViewById(R.id.btn_search_doctors);
        user = FirebaseAuth.getInstance().getCurrentUser();
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new ChatListAdapter(getActivity(), chats);
        recyclerView.setAdapter(adapter);
        initChatList();
        if (type.equals(Constants.VALUE_LOGIN_INTENT_PATIENT))
        {

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
        } else
        {
            btnSearchDoctors.setVisibility(View.INVISIBLE);
            return v;
        }
    }

    private void initChatList()
    {
        userChatsRef = rootDatabaseReference.child(USER_CHATS).child(user.getUid()).getRef();
        userChatsRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                ArrayList<String> allChatUIDs = new ArrayList<String>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    // Get Patient Profile here
                    allChatUIDs.add(postSnapshot.getValue(String.class));
                }
                if (allChatUIDs.size() > 0)
                {
                    Log.d(TAG, "onDataChange: " + allChatUIDs.toString());
                    for (String cUID : allChatUIDs)
                    {
                        rootDatabaseReference.child(CHATS).child(cUID).addListenerForSingleValueEvent(new ValueEventListener()
                        {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot)
                            {
                                ChatUID chatUID = dataSnapshot.getValue(ChatUID.class);
                                chats.add(chatUID);
                                adapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError)
                            {

                            }
                        });
                    }
                } else
                {
//                    @TODO Show to the doctor that patient will appear here when he/she will text you.
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }
}
