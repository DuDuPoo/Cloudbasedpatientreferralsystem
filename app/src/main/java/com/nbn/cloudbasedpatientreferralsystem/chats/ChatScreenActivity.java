package com.nbn.cloudbasedpatientreferralsystem.chats;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.nbn.cloudbasedpatientreferralsystem.BaseActivity;
import com.nbn.cloudbasedpatientreferralsystem.R;
import com.nbn.cloudbasedpatientreferralsystem.pojo.DoctorProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.PatientProfile;
import com.nbn.cloudbasedpatientreferralsystem.pojo.chats.ChatUID;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nbn.cloudbasedpatientreferralsystem.utils.Constants.*;

public class ChatScreenActivity extends BaseActivity
{
    /*
    * Check the pseudocode file for the details.
    * */

    private FirebaseUser user;
    String TAG = getClass().getSimpleName();
    private EditText etChat;
    private ImageButton btnSend;
    private RecyclerView recyclerView;
    private DatabaseReference chatsRef;
    private DatabaseReference chatMessagesRef;
    private DatabaseReference userChatsRef;

    private String firstUID;
    private String secondUID;
    private static boolean isActiveChat;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initLayout();

        firstUID = getIntent().getBundleExtra(KEY_BUNDLE).getString(KEY_PROFILE);
        user = FirebaseAuth.getInstance().getCurrentUser();
        secondUID = user.getUid();
        Log.d(TAG, "onCreate: First UID :: " + firstUID + " :: secondUID :: " + user.getUid());
        if (user.getUid().equals(firstUID))
        {
            finish();
        } else
        {
            createNewChatThread();
        }
    }

    private void postNewChat(ChatUID chatUID)
    {
        String chatUIDKey = rootDatabaseReference
                .child(CHATS)
                .push()
                .getKey();
        rootDatabaseReference.child(CHATS).child(chatUIDKey).setValue(chatUID);
        userChatsRef.push().setValue(chatUIDKey);
    }

    private void createNewChatThread()
    {
        final ChatUID chatUID = new ChatUID();
        chatUID.setMembers(new ArrayList<String>(Arrays.asList(firstUID, secondUID)));
        chatUID.setLastMessageSent("");
        userChatsRef = rootDatabaseReference.child(USER_CHATS).child(user.getUid()).getRef();
        userChatsRef.addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (!dataSnapshot.getChildren().iterator().hasNext())
                {
                    postNewChat(chatUID);
                } else
                {
                    ArrayList<ChatUID> listOfChats = new ArrayList<ChatUID>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                    {
                        Log.d(TAG, "onDataChange: Key :: " + postSnapshot.getValue());
                        listOfChats.add(postSnapshot.getValue(ChatUID.class));
                    }
                    if (listOfChats.size() > 0)
                    {
                        for (int i = 0; i < listOfChats.size(); i++)
                        {
                            ChatUID cUID = listOfChats.get(i);
                            List<String> members = cUID.getMembers();

                            if ((members.get(0).equals(firstUID) &&
                                    members.get(1).equals(secondUID)) ||
                                    ((members.get(0).equals(secondUID) &&
                                            members.get(1).equals(firstUID))))
                            {
                                    /*
                                    * Found
                                    * */

                                isActiveChat = true;
                            } else {
                                postNewChat(chatUID);
                            }

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });

        Log.d(TAG, "createNewChatThread: Key :: ");
    }

    private void initLayout()
    {
        etChat = (EditText) findViewById(R.id.et_chat);
        btnSend = (ImageButton) findViewById(R.id.btn_send);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btnSend.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (TextUtils.isEmpty(etChat.getText().toString()))
                {
                    Toast.makeText(ChatScreenActivity.this, "Please type something", Toast.LENGTH_SHORT).show();
                } else
                {

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
