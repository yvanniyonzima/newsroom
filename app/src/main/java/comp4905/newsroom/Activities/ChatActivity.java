package comp4905.newsroom.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.icu.text.Edits;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.Classes.Globals;
import comp4905.newsroom.Classes.Message;
import comp4905.newsroom.R;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = ChatActivity.class.getName();
    private TextView mChatTitleTextView;
    private ImageView mGoBack;
    private ImageView mChatSettings;
    private ImageView mSendMessageButton;
    private EditText mUserMessageInput;
    private ScrollView mMessageScroll;
    private TextView mDisplayMessageText;
    private LinearLayout mMessageView;

    private String mCurrentGroupName, mUserName, mFirstName, mCurrentDate, mCurrentTime;

    private FirebaseDatabaseHelper mDatabaseHelper = new FirebaseDatabaseHelper();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //get the current username
        mUserName = Globals.deviceUser.getUserName();
        mFirstName = Globals.deviceUser.getFirstName();


        //get the group name
        mCurrentGroupName = getIntent().getExtras().get("group_name").toString();

        //initialize ui elements
        initializeElements();

        //set the chat title
        mChatTitleTextView.setText(mCurrentGroupName);

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

            //clear message view
            mDisplayMessageText.setText("");
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mDatabaseHelper.getGroupMessages(mCurrentGroupName).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                if(dataSnapshot.exists())
                {
                    displayMessage(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {

                if(dataSnapshot.exists())
                {
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

    private void initializeElements()
    {
        mChatTitleTextView = (TextView) findViewById(R.id.chat_title_textview);
        mGoBack = (ImageView) findViewById(R.id.chat_back_arrow);
        mChatSettings = (ImageView) findViewById(R.id.chat_settings_menu);
        mSendMessageButton = (ImageView) findViewById(R.id.send_message_button);
        mUserMessageInput = (EditText) findViewById(R.id.chat_message_edit);
        mMessageScroll = (ScrollView) findViewById(R.id.chat_scroll_view);
        mMessageView = (LinearLayout) findViewById(R.id.message_view_layout);

    }

    private void sendMessage()
    {
        String message = mUserMessageInput.getText().toString();
        String messageKey = mDatabaseHelper.getNewMessageKey(mCurrentGroupName);

        if (TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Please enter a message first", Toast.LENGTH_SHORT);
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
            mDatabaseHelper.saveMessage(currentMessage, mCurrentGroupName).addOnCompleteListener(new OnCompleteListener<Void>() {
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
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while(iterator.hasNext())
        {
            String date = (String) ((DataSnapshot)iterator.next()).getValue();
            String key = (String) ((DataSnapshot)iterator.next()).getValue();
            String username = (String) ((DataSnapshot)iterator.next()).getValue();
            String firstName = (String) ((DataSnapshot)iterator.next()).getValue();
            String message = (String) ((DataSnapshot)iterator.next()).getValue();
            String time = (String) ((DataSnapshot)iterator.next()).getValue();


            displayMessageFirstName(firstName, username);

            displayMessageText(message, username);

            displayMessageDateAndTime(date, time, username);


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

        if(TextUtils.equals(mUserName, username))
        {
            int parentWidth = mMessageView.getWidth();
            firstNameParams.setMarginStart(parentWidth/2);
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

        if(TextUtils.equals(mUserName, username))
        {
            int parentWidth = mMessageView.getWidth();
            messageParams.setMarginStart(parentWidth/2);
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

        if(TextUtils.equals(mUserName, username))
        {
            int parentWidth = mMessageView.getWidth();
            datetimeTextView.setGravity(Gravity.RIGHT);
            dateTimeParams.setMargins(parentWidth/2, 1, 10,30);
        }

        datetimeTextView.setLayoutParams(dateTimeParams);

        mMessageView.addView(datetimeTextView);
    }
}