package comp4905.newsroom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.Classes.Globals;
import comp4905.newsroom.Classes.Group;
import comp4905.newsroom.Classes.Message;
import comp4905.newsroom.R;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getName();
    private TextView mChatTitleTextView;
    private ImageView mGoBack;
    private ImageView mChatInfo;
    private ImageView mSendMessageButton;
    private EditText mUserMessageInput;
    private ScrollView mMessageScroll;
    private TextView mDisplayMessageText;
    private LinearLayout mMessageView;
    private LinearLayout mEditMessageLayout;

    //UI FOR SETTINGS
    private ScrollView mGroupInfoView;
    private TextView mChatTopicView;
    private TextView mChatDescriptionView;
    private TextView mChatLinkView;

    private TextView mChatParticipantCountView;
    private TextView mChatParticipantView;

    //moderator controls
    private LinearLayout mAdminControlsView;
    private RadioGroup mChatChangeStatusRadio;
    private Button mChatSaveStatusButton;

    private TextView mChatRequestCount;
    private ImageView mChatShowRequests;
    private ListView mChatRequestList;

    private ImageView mChatShowBan;
    private ListView mChatUsersToBanList;

    private String mCurrentGroupAdmin;
    private String mCurrentGroupName;
    private String mCurrentGroupKey;
    private String mCurrentGroupStatus;
    private String mUserName;
    private String mFirstName;
    private String mCurrentDate;
    private String mCurrentTime;
    private ArrayList<String> mCurrentMembers;
    private ArrayList<String> mBanChoices;
    private ArrayAdapter mBanChoicesAdapter;
    private ArrayList mCurrentJoinRequest;
    private ArrayAdapter<String> mRequestAdapter;

    //deletechat
    TextView mDeleteChat;

    //the group
    private Group mCurrentGroup;

    private FirebaseDatabaseHelper mDatabaseHelper = new FirebaseDatabaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get the current username
        mUserName = Globals.deviceUser.getUserName();
        mFirstName = Globals.deviceUser.getFirstName();


        //get the group name and key
        mCurrentGroupName = getIntent().getExtras().get("group_name").toString();
        mCurrentGroupKey = getIntent().getExtras().get("group_key").toString();

        //initialize ui elements
        initializeElements();

        //set the chat title
        mChatTitleTextView.setText(mCurrentGroupName);

        //get the group info
        getGroupInfo();

        //go back to all chats
        mGoBack.setOnClickListener((View view) -> {
            Intent groupChatsIntent = new Intent(ChatActivity.this, GroupChatsActivity.class);
            startActivity(groupChatsIntent);
        });

        //send message
        mSendMessageButton.setOnClickListener((View view) -> {
            sendMessage();

            //clear the message input edit text
            mUserMessageInput.getText().clear();

            mMessageScroll.fullScroll(ScrollView.FOCUS_DOWN);

        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mMessageView.removeAllViews();

        mDatabaseHelper.getGroupMessages(mCurrentGroupKey).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                if(dataSnapshot.exists())
                {
                    Log.i(TAG,"onStart() => group messages key: " + dataSnapshot.getKey());
                    //mMessageView.removeAllViews();
                    displayMessage(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                if(dataSnapshot.exists())
                {
                    //mMessageView.removeAllViews();
                    displayMessage(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMessageView.removeAllViews();
    }

    private void initializeElements()
    {
        mChatTitleTextView = (TextView) findViewById(R.id.chat_title_textview);
        mGoBack = (ImageView) findViewById(R.id.chat_back_arrow);
        mChatInfo = (ImageView) findViewById(R.id.chat_settings_info);
        mSendMessageButton = (ImageView) findViewById(R.id.send_message_button);
        mUserMessageInput = (EditText) findViewById(R.id.chat_message_edit);
        mMessageScroll = (ScrollView) findViewById(R.id.chat_scroll_view);
        mMessageView = (LinearLayout) findViewById(R.id.message_view_layout);
        mEditMessageLayout = (LinearLayout) findViewById(R.id.edit_message_layout);

        mChatInfo.setOnClickListener((View view) -> {
            if(mGroupInfoView.getVisibility() == View.GONE)
            {
                //make the message scrollview and message edit layouts disappear
                mMessageScroll.setVisibility(View.GONE);
                mEditMessageLayout.setVisibility(View.GONE);

                //make the group info visible
                mGroupInfoView.setVisibility(View.VISIBLE);

                //set the info imageview resource and tag to chat
                mChatInfo.setImageResource(R.drawable.chat_bubble);

            }
            else
            {
                //make the message scrollview and message edit layouts appear
                mMessageScroll.setVisibility(View.VISIBLE);
                mEditMessageLayout.setVisibility(View.VISIBLE);

                //make the group info disappear
                mGroupInfoView.setVisibility(View.GONE);

                //set the info imageview resource and tag to chat
                mChatInfo.setImageResource(R.drawable.ic_info_24);
            }
        });

        //inflate chat settings elements
        mAdminControlsView = (LinearLayout) findViewById(R.id.chat_admin_controls);
        mGroupInfoView = (ScrollView) findViewById(R.id.chat_settings_layout);
        mChatTopicView = (TextView) findViewById(R.id.chat_settings_topic);;
        mChatDescriptionView = (TextView) findViewById(R.id.chat_settings_description);;
        mChatLinkView = (TextView) findViewById(R.id.chat_settings_link);;

        mChatParticipantCountView = (TextView) findViewById(R.id.chat_participant_count);;
        mChatParticipantView = (TextView) findViewById(R.id.chat_list_participants);

        //moderator controls
        mCurrentMembers = new ArrayList<>();
        mBanChoices = new ArrayList<>();
        mCurrentJoinRequest = new ArrayList<>();
        mChatChangeStatusRadio = (RadioGroup) findViewById(R.id.chat_settings_status);
        mChatSaveStatusButton = (Button) findViewById(R.id.chat_save_status);
        mDeleteChat = (TextView) findViewById(R.id.chat_delete);

        mDeleteChat.setOnClickListener((View view) -> {
            deleteChatAlert();
        });

        mChatSaveStatusButton.setOnClickListener((View view) -> {
            onChangedStatus(mCurrentGroupStatus);
        });

        mChatRequestCount = (TextView) findViewById(R.id.chat_request_count);
        mChatShowRequests = (ImageView) findViewById(R.id.chat_show_requests);
        mChatRequestList = (ListView) findViewById(R.id.chat_request_users);
        mRequestAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mCurrentJoinRequest);
        mChatRequestList.setAdapter(mRequestAdapter);

        //show request list
        mChatShowRequests.setOnClickListener((View view) -> {
            if(mChatRequestList.getVisibility() == View.GONE)
            {
                mChatRequestList.setVisibility(View.VISIBLE);

                mChatShowRequests.setImageResource(R.drawable.ic_arrow_up);
            }
            else
            {
                mChatRequestList.setVisibility(View.GONE);

                mChatShowRequests.setImageResource(R.drawable.ic_arrow_down);
            }
        });


        //onclick for the request list
        mChatRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String username = adapterView.getItemAtPosition(position).toString();
                onRequestClicked(username, position);
            }
        });

        mChatShowBan = (ImageView) findViewById(R.id.chat_ban_show_users);
        mChatUsersToBanList = (ListView) findViewById(R.id.chat_users_to_ban);
        mBanChoicesAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mBanChoices);
        mChatUsersToBanList.setAdapter(mBanChoicesAdapter);


        //show list to ban from
        mChatShowBan.setOnClickListener((View view) -> {
            if(mChatUsersToBanList.getVisibility() == View.GONE)
            {
                mChatUsersToBanList.setVisibility(View.VISIBLE);

                mChatShowBan.setImageResource(R.drawable.ic_arrow_up);
            }
            else
            {
                mChatUsersToBanList.setVisibility(View.GONE);

                mChatShowBan.setImageResource(R.drawable.ic_arrow_down);
            }
        });

        //on user banned clicked
        mChatUsersToBanList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                String username = adapterView.getItemAtPosition(position).toString();

                onBanUserClicked(username);
                return false;
            }
        });

        //status radio button on click
        mChatChangeStatusRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                RadioButton radioButton = (RadioButton) radioGroup.findViewById(checkedId);
                String newStatus = radioButton.getText().toString();
                mCurrentGroupStatus = newStatus;
            }
        });

    }

    private void getGroupInfo()
    {

        mDatabaseHelper.getSingleGroup(mCurrentGroupKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = String.valueOf(snapshot.child("name").getValue());
                String topic = String.valueOf(snapshot.child("topic").getValue());
                String description = String.valueOf(snapshot.child("description").getValue());
                String topicLink = String.valueOf(snapshot.child("topicLink").getValue());
                String groupAdmin = String.valueOf(snapshot.child("groupAdmin").getValue());
                String dateCreated = String.valueOf(snapshot.child("dateCreated").getValue());
                String uniqueKey = String.valueOf(snapshot.child("uniqueKey").getValue());
                String status = String.valueOf(snapshot.child("status").getValue());

                //clear arraylists
                mCurrentMembers.clear();
                mCurrentJoinRequest.clear();
                mBanChoices.clear();

                //set the admin
                mCurrentGroupAdmin = groupAdmin;

                //show the moderator controls
                if(TextUtils.equals(Globals.deviceUser.getUserName(), groupAdmin))
                {
                    mAdminControlsView.setVisibility(View.VISIBLE);
                }
                else
                {
                    mAdminControlsView.setVisibility(View.GONE);
                }

                //set the status
                mCurrentGroupStatus = status;

                mCurrentGroup = new Group(name, groupAdmin, description, status, topic, topicLink, dateCreated);
                mCurrentGroup.setUniqueKey(uniqueKey);

                //get the members list
                Iterable<DataSnapshot> members = snapshot.child("members").getChildren();

                for(DataSnapshot user: members)
                {
                    mCurrentMembers.add(String.valueOf(user.getValue()));
                    mBanChoices.add(String.valueOf(user.getValue()));
                }

                mCurrentGroup.setMembers(mCurrentMembers);

                //remove admin from banned choices
                mBanChoices.remove(mCurrentGroupAdmin);

                //get the join requests
                Iterable<DataSnapshot> requests = snapshot.child("joinRequests").getChildren();

                if(requests != null)
                {
                    for(DataSnapshot request: requests)
                    {
                        mCurrentJoinRequest.add(String.valueOf(request.getValue()));
                    }

                    mCurrentGroup.setJoinRequests(mCurrentJoinRequest);
                }



                //get the banned users

                Iterable<DataSnapshot> bannedUsers = snapshot.child("bannedMembers").getChildren();
                if(bannedUsers != null)
                {
                    ArrayList<String> banned = new ArrayList<>();
                    for(DataSnapshot user: bannedUsers)
                    {
                        banned.add(String.valueOf(user.getValue()));
                    }

                    //kick current user put if they are banned WHILST IN THE CHAT
                    if(banned.contains(Globals.deviceUser.getUserName()))
                    {
                        showBanned(Globals.deviceUser.getUserName());
                    }

                    mCurrentGroup.setBannedMembers(banned);


                }

                //set the chat info uis
                mChatTopicView.setText(topic);
                mChatDescriptionView.setText(description);
                mChatLinkView.setText(topicLink);
                mChatParticipantCountView.setText(mCurrentMembers.size() + " participants");
                String listParticipants = "";
                for(String user : mCurrentMembers)
                {
                    if(TextUtils.equals(user, mCurrentGroupAdmin))
                    {
                        listParticipants += user + " (Moderator)\n";
                    }
                    else
                    {
                        listParticipants += user + "\n";
                    }

                }
                mChatParticipantView.setText(listParticipants);


                //set the status
                if(TextUtils.equals(status,"Public"))
                {
                    mChatChangeStatusRadio.check(R.id.chat_status_public);
                }
                else
                {
                    mChatChangeStatusRadio.check(R.id.chat_status_private);
                }

                //set the number requests
                if(!mCurrentJoinRequest.isEmpty())
                {
                    mChatRequestCount.setText(mCurrentJoinRequest.size() + " requests");
                }
                else
                {
                    mChatRequestCount.setText("No new requests");
                }

                //notify data set changed for request listview
                mRequestAdapter.notifyDataSetChanged();

                //notify data set changed for ban user listview
                mBanChoicesAdapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //kick user out
    private void showBanned(String username)
    {
        AlertDialog.Builder banned = new AlertDialog.Builder(ChatActivity.this);
        banned.setIcon(R.drawable.ic_kicked_out);
        banned.setTitle("Banned from chat");
        banned.setMessage("Sorry. You were banned from this chat by the moderator");
        banned.setCancelable(false);
        banned.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mDatabaseHelper.removeBanNotification(mCurrentGroupKey, username);

                Intent backToGroupChats = new Intent(ChatActivity.this, GroupChatsActivity.class);
                startActivity(backToGroupChats);

            }
        });
        banned.show();
    }

    private void sendMessage()
    {
        String message = mUserMessageInput.getText().toString();
        String messageKey = mDatabaseHelper.getNewMessageKey(mCurrentGroupKey);
        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please enter a message first", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            mCurrentDate = currentDateFormat.format((calForDate.getTime()));

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            mCurrentTime = currentTimeFormat.format((calForTime.getTime()));

            Message currentMessage = new Message(message, mUserName, mFirstName, mCurrentDate, mCurrentTime, messageKey);

            //save to firebase
            mDatabaseHelper.saveMessage(currentMessage, mCurrentGroupKey).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Log.i(TAG,"sendMessage() => Message saved successfully!");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG,"sendMessage() => Failed to save message!");
                    Log.e(TAG,"sendMessage() => " + e);

                }
            });



        }
    }

    private void displayMessage(DataSnapshot dataSnapshot)
    {
        //clear the message views
        //mMessageView.removeAllViews();

        Iterable<DataSnapshot> messageSnapshots = dataSnapshot.getChildren();
        int count = 0;


        if(messageSnapshots != null)
        {

            for(DataSnapshot message: messageSnapshots)
            {
                count++;
                //Log.i(TAG,"displayMessage() => message key: " + message.getKey());
                String username = String.valueOf(message.child("sentBy").getValue());
                String firstName = String.valueOf(message.child("sentByFirstName").getValue());
                String text = String.valueOf(message.child("text").getValue());
                String date = String.valueOf(message.child("date").getValue());
                String time = String.valueOf(message.child("time").getValue());

                if(!TextUtils.equals(text, "null"))
                {

                    displayMessageFirstName(firstName, username);

                    displayMessageText(text, username);

                    displayMessageDateAndTime(date, time, username);

                    mMessageScroll.fullScroll(ScrollView.FOCUS_DOWN);
                }

            }

            Log.i(TAG,"displayMessage() => Message snapshot size = " + count);
        }

    }

    private void displayMessageFirstName(String firstName, String username)
    {
        //username firstname textView
        TextView firstNameTextView = new TextView(this);
        firstNameTextView.setText(firstName);
        firstNameTextView.setTextSize(20);
        //make firstname bold
        firstNameTextView.setTypeface(null, Typeface.BOLD);
        firstNameTextView.setPadding(5,5,5,5);



        LinearLayout.LayoutParams firstNameParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int parentWidth = mMessageView.getWidth();
        if(TextUtils.equals(mUserName, username))
        {
            firstNameParams.setMarginStart(parentWidth/2);
        }
        else
        {
            firstNameParams.setMarginStart(10);
        }
        firstNameTextView.setLayoutParams(firstNameParams);
        mMessageView.addView(firstNameTextView);

    }

    private void displayMessageText(String message, String username)
    {
        TextView messageTextView = new TextView(this);
        messageTextView.setText(message);
        messageTextView.setTextSize(16);
        messageTextView.setPadding(5,5,5,5);


        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        int parentWidth = mMessageView.getWidth();
        if(TextUtils.equals(mUserName, username))
        {
            messageParams.setMarginStart(parentWidth/2);
        }
        else
        {
            messageParams.setMarginStart(10);
        }

        messageTextView.setLayoutParams(messageParams);

        mMessageView.addView(messageTextView);

    }

    private void displayMessageDateAndTime(String date, String time, String username)
    {
        TextView datetimeTextView = new TextView(this);
        datetimeTextView.setText(date + " at " + time);
        datetimeTextView.setTextSize(12);
        datetimeTextView.setPadding(5,5,5,5);



        LinearLayout.LayoutParams dateTimeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        int parentWidth = mMessageView.getWidth();
        if(TextUtils.equals(mUserName, username))
        {

            datetimeTextView.setGravity(Gravity.RIGHT);
            dateTimeParams.setMargins(parentWidth/2, 1, 10,30);
        }
        else
        {
            dateTimeParams.setMargins(10, 1, 10,30);
        }



        datetimeTextView.setLayoutParams(dateTimeParams);

        mMessageView.addView(datetimeTextView);
    }

    //CHAT SETTINGS FUNCTIONS


    void onChangedStatus(String status)
    {
        if(!TextUtils.equals(mCurrentGroup.getStatus(), status))
        {
            mDatabaseHelper.updateStatus(mCurrentGroupKey, status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Log.i(TAG, "onChangedStatus() => Successfully updated the group status");
                        Toast.makeText(ChatActivity.this, "Group status changed to " + status, Toast.LENGTH_LONG).show();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onChangedStatus() => Failed to update the group status");

                }
            });
        }
        else
        {
            Toast.makeText(ChatActivity.this, "Group is already " + status, Toast.LENGTH_LONG).show();
        }

    }

    void onRequestClicked(String username, int index)
    {
        //retrieve username ban count
        mDatabaseHelper.retrieveUserBanCount(username).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        int banCount = Integer.valueOf(String.valueOf(task.getResult().getValue()));
                        requestAlert(username, banCount, index);
                    }
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    void requestAlert(String username, int banCount, int index)
    {
        AlertDialog.Builder requestBuilder = new AlertDialog.Builder(ChatActivity.this);
        requestBuilder.setTitle("Join request");
        if(banCount > 2)
        {
            requestBuilder.setMessage(username + " wants to join this group chat.\nThis user has been banned from " + banCount + " other groups");
        }
        else
        {
            requestBuilder.setMessage(username + " wants to join this group chat.");
        }
        requestBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //add member to group member
                mCurrentMembers.add(username);
                mDatabaseHelper.addUserToGroup(mCurrentGroupKey, mCurrentMembers, mCurrentMembers.size()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.i(TAG, "requestAlert() => User " + username + " added to group");
                            Toast.makeText(ChatActivity.this, "User " + username + " added to group!", Toast.LENGTH_SHORT).show();
                            mCurrentJoinRequest.remove(index);
                            mRequestAdapter.notifyDataSetChanged();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(TAG, "requestAlert() =>  failed to add user " + username + " to group");
                        Log.e(TAG, "requestAlert() => " + e);
                    }
                });

            }
        });

        requestBuilder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //remove user from request list
                mCurrentJoinRequest.remove(username);
                mDatabaseHelper.updateRequestList(mCurrentGroupKey,mCurrentJoinRequest).addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {

                        if(task.isSuccessful())
                        {
                            Log.w(TAG, "requestAlert() =>  removed " + username + " from request list");

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(TAG, "requestAlert() =>  failed to remove " + username + " from request list");
                        Log.e(TAG, "requestAlert() => " + e);

                    }
                });

            }
        });

        requestBuilder.show();

    }

    void onBanUserClicked(String username)
    {
        AlertDialog.Builder banUserAlert = new AlertDialog.Builder(ChatActivity.this);
        banUserAlert.setIcon(R.drawable.ic_action_warning);
        banUserAlert.setTitle("Ban User");
        banUserAlert.setMessage("You are about to ban user " + username + " from this group. This action is not irreversible");
        banUserAlert.setPositiveButton("Ban User", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                //remove user from members
                mCurrentMembers.remove(username);
                mDatabaseHelper.updateMembers(mCurrentGroupKey, mCurrentMembers, mCurrentMembers.size()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Log.w(TAG, "onBanUserClicked() =>  removed " + username + " from members list");
                            Toast.makeText(ChatActivity.this, "User " + username + " has been banned", Toast.LENGTH_SHORT).show();

                            mCurrentMembers.remove(username);
                            mBanChoices.remove(username);
                            mBanChoicesAdapter.notifyDataSetChanged();

                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.e(TAG, "onBanUserClicked() =>  failed to remove " + username + " from members list");
                        Log.e(TAG, "onBanUserClicked() => " + e);

                    }
                });

            }
        });
        banUserAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        banUserAlert.show();
    }

    //alert for chat deleting
    void deleteChatAlert()
    {
        AlertDialog.Builder deleteChat = new AlertDialog.Builder(ChatActivity.this);
        deleteChat.setIcon(R.drawable.ic_action_warning);
        deleteChat.setTitle("Delete Group Chat");
        deleteChat.setMessage("Are you sure you want to delete this group chat?");
        deleteChat.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                mDatabaseHelper.deleteGroupChat(mCurrentGroupKey);
                Intent intent = new Intent(ChatActivity.this, GroupChatsActivity.class);
                startActivity(intent);
            }
        });

        deleteChat.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        deleteChat.show();
    }
}