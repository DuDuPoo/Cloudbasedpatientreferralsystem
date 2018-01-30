package com.nbn.cloudbasedpatientreferralsystem.chats;

import android.support.v7.app.AppCompatActivity;
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
import com.nbn.cloudbasedpatientreferralsystem.utils.Constants;

import java.util.ArrayList;
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
    private ArrayList<String> chatUIDs;
    private DoctorProfile doctorProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        /*Here we get the one UID either patient or doctor
        * To get the other one we will have to get it from the intent.
        * */
        if(prefManager.getProfile().equals(VALUE_LOGIN_INTENT_PATIENT)) {
            doctorProfile = (DoctorProfile) getIntent().getBundleExtra(KEY_BUNDLE).getSerializable(KEY_DOCTOR_PROFILE);
        } else {
            //@TODO Get the patient profile here.
        }
        user = FirebaseAuth.getInstance().getCurrentUser();
        chatUIDs = new ArrayList<>();
    }

    /*Step 1*/
    private void initDatabaseRef()
    {
        user = FirebaseAuth.getInstance().getCurrentUser();
        userChatsRef = rootDatabaseReference
                .child(USER_CHATS)
                .child(user.getUid())
                .getRef();

    }

    /*Step 2*/
    private void retrieveChatUID()
    {
        userChatsRef.addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                chatUIDs.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren())
                {
                    chatUIDs.add((String) postSnapshot.getValue());
                    Log.d(TAG, "onDataChange: chatUIDs : " + chatUIDs.toString());
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError)
            {

            }
        });
    }

    /*Step 3*/
    private boolean isChatUIDActive()
    {
        boolean result = false;
        if (chatUIDs.size() > 0)
        {
            for (String cUID : chatUIDs)
            {
                chatsRef.child(cUID).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        ChatUID chatUID = dataSnapshot.getValue(ChatUID.class);
                        if (chatUID != null)
                        {
                            List<String> members = chatUID.getMembers();
                            for (String memberUID : members)
                            {

                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError)
                    {

                    }
                });
            }
        } else
        {
            /*Create a new Chat thread*/
        }
        return result;
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
