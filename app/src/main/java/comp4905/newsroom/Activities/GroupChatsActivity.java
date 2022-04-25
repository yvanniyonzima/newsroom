package comp4905.newsroom.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.stream.Collectors;

import comp4905.newsroom.Classes.ActiveGroupCardItem;
import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.Classes.Globals;
import comp4905.newsroom.Classes.Group;
import comp4905.newsroom.Classes.RecyclerAdapters.ActiveGroupRecyclerAdapter;
import comp4905.newsroom.R;

public class GroupChatsActivity extends AppCompatActivity {

    private static final String TAG = GroupChatsActivity.class.getName();

    private ImageView mBackButton;
    private ImageView groupsMenu;
    private Button mChatsButton;
    private Button mActiveChatsButton;
    private ListView mUserChatsListView;
    private ArrayList<String> mUserChatTitles;
    private ArrayList<Group> mGroups;
    private HashMap<String, String> mGroupsHashMap;
    private ArrayAdapter mUserChatTitlesAdapter;
    private ArrayList<String> mGroupMembers;

    //active chat recycler elements
    private RecyclerView mActiveGroupsRecyclerView;
    private RecyclerView.LayoutManager mActiveGroupsLayoutManager;
    private ActiveGroupRecyclerAdapter mActiveGroupRecycleAdapter;
    private ArrayList<ActiveGroupCardItem> mActiveGroupsCardItems;

    private FirebaseDatabaseHelper mDatabaseHelper = new FirebaseDatabaseHelper();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_group_chats);

        //inflate ui elements and setup recyclers and adapters
        initializeFields();

        //get all the groups that the user is a part of
        retrieveGroups();

        mUserChatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                viewChat(position);
            }
        });
    }

    private void initializeFields()
    {
        //initialize all arraylists and maps
        mUserChatTitles = new ArrayList<>();
        mGroupsHashMap = new HashMap<>();
        mGroups = new ArrayList<>();
        mActiveGroupsCardItems = new ArrayList<>();
        mGroupMembers = new ArrayList<>();

        mBackButton = (ImageView) findViewById(R.id.active_group_back);
        groupsMenu = (ImageView) findViewById(R.id.group_chats_menu);
        mChatsButton = (Button) findViewById(R.id.my_chats_button);
        mActiveChatsButton = (Button) findViewById(R.id.active_chats_button);
        mUserChatsListView = (ListView) findViewById(R.id.user_chats_listview);

        mUserChatTitlesAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mUserChatTitles);
        mUserChatsListView.setAdapter(mUserChatTitlesAdapter);

        //go back to news activity
        mBackButton.setOnClickListener((View view) ->{
            Intent newsIntent = new Intent(GroupChatsActivity.this, NewsActivity.class);
            startActivity(newsIntent);
            finish();
        });

        //onclick for my chats buttons
        mChatsButton.setOnClickListener((View view) -> {
            //hide recycler
            mActiveGroupsRecyclerView.setVisibility(View.GONE);

            //show user chats
            mUserChatsListView.setVisibility(View.VISIBLE);

            mChatsButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.checked_button));
            mActiveChatsButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.unchecked_button));
        });

        //onclick for active chats button
        mActiveChatsButton.setOnClickListener((View view) ->{
            //hide user chats listview
            mUserChatsListView.setVisibility(View.GONE);

            //show active chats recycler
            mActiveGroupsRecyclerView.setVisibility(View.VISIBLE);

            mChatsButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.unchecked_button));
            mActiveChatsButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.checked_button));
        });

        //setup main menu
        groupsMenu.setOnClickListener((View view) -> { setUpMenu(); });

        //build the recycler view
        buildActiveGroupRecyclerView();

    }

    private void buildActiveGroupRecyclerView()
    {
        //setup active groups recycle adapter
        mActiveGroupsRecyclerView = (RecyclerView) findViewById(R.id.active_groups_recycler);
        mActiveGroupsRecyclerView.setHasFixedSize(true);
        mActiveGroupsLayoutManager = new LinearLayoutManager(GroupChatsActivity.this);
        mActiveGroupRecycleAdapter = new ActiveGroupRecyclerAdapter(mActiveGroupsCardItems);
        mActiveGroupsRecyclerView.setLayoutManager(mActiveGroupsLayoutManager);
        mActiveGroupsRecyclerView.setAdapter(mActiveGroupRecycleAdapter);

        //implement the onclick
        mActiveGroupRecycleAdapter.setOnItemClickListener(new ActiveGroupRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onExternalLinkClick(int position) {
                if(!TextUtils.isEmpty(mGroups.get(position).getTopicLink()))
                {
                    String link = mGroups.get(position).getTopicLink();
                    Uri uri = Uri.parse(link);
                    Log.i(TAG,"buildActiveGroupRecyclerView() => following link: " + mGroups.get(position).getTopicLink());
                    Intent visitArticlePage = new Intent(Intent.ACTION_VIEW, uri);

                    try {
                        startActivity(visitArticlePage);
                    }catch (ActivityNotFoundException exception)
                    {
                        Toast.makeText(GroupChatsActivity.this, "No browswer found to open webpage", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(GroupChatsActivity.this, "No link associated to group", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onGroupJoinClick(int position) {
                //get all the necessary fields
                //mActiveGroupsCardItems.get(position).
                Group groupToJoin = mActiveGroupsCardItems.get(position).getRelatedGroup();
                Log.i(TAG, "group status = " + groupToJoin.getStatus());
                if (TextUtils.equals(groupToJoin.getStatus(),"Public"))
                {
                    String groupKey = groupToJoin.getUniqueKey();
                    int groupNumMembers = groupToJoin.getNumMembers() + 1;
                    ArrayList<String> members = groupToJoin.getMembers();

                    Log.i(TAG, "Current members = " + members);

                    members.add(Globals.deviceUser.getUserName());


                    mDatabaseHelper.addUserToGroup(groupKey, members, groupNumMembers).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                //retrieve all groups again
                                //retrieveGroups();

                                //hide active groups recycler
                                mActiveGroupsRecyclerView.setVisibility(View.GONE);

                                //show users groups listview
                                mUserChatsListView.setVisibility(View.VISIBLE);
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "buildActiveGroupRecyclerView() => Failed to add member to public group");

                        }
                    });


                }
                else //the group chat is private
                {
                    Log.i(TAG, "Groups join requests: " + groupToJoin.getJoinRequests());
                    Log.i(TAG, "Device global user: " + Globals.deviceUser.getUserName());
                    if(groupToJoin.getJoinRequests().contains(Globals.deviceUser.getUserName()))
                    {
                        //the user has already sent a request
                        AlertDialog.Builder requestAlreadySent = new AlertDialog.Builder(GroupChatsActivity.this);
                        requestAlreadySent.setTitle("Request already sent!");
                        requestAlreadySent.setMessage("You have already sent a request to join this group");
                        requestAlreadySent.setCancelable(true);
                        requestAlreadySent.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        requestAlreadySent.show();
                    }
                    else
                    {
                        //send a request to join group
                        AlertDialog.Builder requestBuilder = new AlertDialog.Builder(GroupChatsActivity.this);
                        requestBuilder.setTitle("Request to join group");
                        requestBuilder.setMessage("The group " + groupToJoin.getName() + " is private. Send a request to join the group?");
                        requestBuilder.setCancelable(false);
                        requestBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mDatabaseHelper.saveJoinRequest(Globals.deviceUser.getUserName(), groupToJoin.getUniqueKey());

                                retrieveGroups();
                            }
                        });
                        requestBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        requestBuilder.show();
                    }

                }

            }
        });
    }

    private void viewChat(int position)
    {

        String groupName = mUserChatTitles.get(position);
        //String groupKey = mGroupsHashMap.values().stream().collect(Collectors.toCollection(ArrayList::new)).get(position);
        String groupKey = String.valueOf(mGroupsHashMap.values().toArray()[position]);

        Intent chatActivityIntent = new Intent(GroupChatsActivity.this, ChatActivity.class);
        chatActivityIntent.putExtra("group_name", groupName);
        chatActivityIntent.putExtra("group_key", groupKey);
        startActivity(chatActivityIntent);
        finish();

        Log.i(TAG, "viewChat() => viewing group with name " + groupName + " and key " + groupKey);

    }

    private void retrieveGroups()
    {

        Log.i(TAG,"getting baned notifications 1");
        mDatabaseHelper.getGroups().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> groupsSnapshot = dataSnapshot.getChildren();

                mGroupsHashMap.clear();
                mUserChatTitles.clear();
                mGroups.clear();
                mActiveGroupsCardItems.clear();

                for(DataSnapshot group: groupsSnapshot)
                {
                    //get all group information
                    String currentGroupKey = group.getKey();
                    String currentGroupName = String.valueOf(group.child("name").getValue());
                    String currentGroupStatus = String.valueOf(group.child("status").getValue());
                    String currentGroupAdmin = String.valueOf(group.child("groupAdmin").getValue());
                    String currentGroupNumMembers = String.valueOf(group.child("numMembers").getValue());
                    String currentGroupCreateDate = String.valueOf(group.child("dateCreated").getValue());
                    String currentGroupTopic = String.valueOf(group.child("topic").getValue());
                    String currentGroupTopicLink = String.valueOf(group.child("topicLink").getValue());
                    String currentGroupDescription = String.valueOf(group.child("description"));
                    Iterable<DataSnapshot> iterableMembers = group.child("members").getChildren();
                    Iterable<DataSnapshot> iterableRequests = group.child("joinRequests").getChildren();
                    Iterable<DataSnapshot> iterableBanned = group.child("bannedMembers").getChildren();
                    Iterable<DataSnapshot> iterableBanNotifications = group.child("banNotification").getChildren();

                    //Log.i(TAG,"link retrieved: " + currentGroupTopicLink);
                    //get the group members
                    ArrayList<String> currentGroupMembers = new ArrayList<>();
                    for(DataSnapshot member: iterableMembers)
                    {
                        currentGroupMembers.add(String.valueOf(member.getValue()));
                    }

                    //Log.i(TAG, "Retrieved members of " + currentGroupName + " = " + currentGroupMembers);
                    //get the group requests
                    ArrayList<String> currentGroupRequests = new ArrayList<>();
                    for(DataSnapshot request: iterableRequests)
                    {
                        currentGroupRequests.add(String.valueOf(request.getValue()));
                    }

                    //get the group banned members
                    ArrayList<String> currentGroupBanned = new ArrayList<>();
                    for(DataSnapshot banned: iterableBanned)
                    {
                        currentGroupBanned.add(String.valueOf(banned.getValue()));
                    }

                    //add group name and key to hashmap if current logged user is part of group
                    if(currentGroupMembers.contains(Globals.deviceUser.getUserName()))
                    {
                        if(currentGroupRequests.size() > 0 && TextUtils.equals(currentGroupAdmin, Globals.deviceUser.getUserName()))
                        {
                            String groupMapKey = currentGroupName + "(" + currentGroupRequests.size() + " join request)";
                            mGroupsHashMap.put(groupMapKey, currentGroupKey);
                        }
                        else
                        {
                            mGroupsHashMap.put(currentGroupName, currentGroupKey);
                        }

                    }

                    //get the banNotification list
                    ArrayList<String> bannedNotification = new ArrayList<>();
                    Log.i(TAG,"getting baned notifications 2");
                    for(DataSnapshot baned:iterableBanNotifications)
                    {

                        bannedNotification.add(String.valueOf(baned.getValue()));
                    }

                    //check if current user in the banned notification list
                    if(bannedNotification.contains(Globals.deviceUser.getUserName()))
                    {
                        Log.i(TAG,"getting baned notifications 3");
                        bannedNotification.remove(Globals.deviceUser.getUserName());
                        notifyBan(Globals.deviceUser.getUserName(), currentGroupKey, bannedNotification);


                    }

                    //make group object
                    Group currentGroup = new Group(currentGroupName, currentGroupAdmin, currentGroupDescription,
                                                    currentGroupStatus, currentGroupTopic, currentGroupTopicLink, currentGroupCreateDate);
                    currentGroup.setNumMembers(Integer.valueOf(currentGroupNumMembers));
                    currentGroup.setUniqueKey(currentGroupKey);
                    currentGroup.setMembers(currentGroupMembers);
                    currentGroup.setJoinRequests(currentGroupRequests);
                    currentGroup.setBannedMembers(currentGroupBanned);

                    //add to groups arraylist
                    mGroups.add(currentGroup);

                    //make and set card item info for active groups recycler only if current user is not part or banned
                    if(!currentGroupBanned.contains(Globals.deviceUser.getUserName())) {
                        //notify user they have been banned via dialog

                        //make card item if user in not a member
                        if (!currentGroupMembers.contains(Globals.deviceUser.getUserName())) {
                            String currentGroupInfo = "<ul>" +
                                    "<li> &nbsp; Moderator:&nbsp; " + currentGroupAdmin + "</li>" +
                                    "<li>&nbsp;" + currentGroupNumMembers + " participants </li>" +
                                    "<li> &nbsp;Started on:&nbsp; " + currentGroupCreateDate + "</li>" +
                                    "</ul>";

                            ActiveGroupCardItem currentGroupCardItem = new ActiveGroupCardItem(currentGroupName, currentGroupStatus, currentGroupInfo, currentGroup);

                            if (TextUtils.isEmpty(currentGroupTopic)) {
                                currentGroupCardItem.setTopic("No assigned topic");
                            } else {
                                currentGroupCardItem.setTopic(currentGroupTopic);
                            }

                            mActiveGroupsCardItems.add(currentGroupCardItem);
                        }
                    }

                }

                //add chat titles to array for user chat titles display
                mUserChatTitles.addAll(mGroupsHashMap.keySet());

                //notify user chats changed
                mUserChatTitlesAdapter.notifyDataSetChanged();

                //notify active groups recycler view
                mActiveGroupRecycleAdapter.notifyDataSetChanged();

                //Log.i(TAG, "retrieveGroups() => retrieved groups: " + mUserChatTitles);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //notify user ban
    private void notifyBan(String groupName, String groupKey, ArrayList<String> bannedNotification)
    {
        AlertDialog.Builder userBanned = new AlertDialog.Builder(GroupChatsActivity.this);
        userBanned.setTitle("Banned from group");
        userBanned.setIcon(R.drawable.ic_kicked_out);
        userBanned.setMessage("You have been banned from " + groupName + " the group admin");
        userBanned.setCancelable(true);
        userBanned.setPositiveButton("Dismiss", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                Thread updateBan = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mDatabaseHelper.updateBanedNotifications(groupKey, bannedNotification);
                    }
                });

                updateBan.start();


            }
        });
        userBanned.show();
    }

    private void setUpMenu ()
    {
        //create instance of PopupMenu
        PopupMenu mainMenu = new PopupMenu(GroupChatsActivity.this, groupsMenu);

        //inflate the popup menu with xml file
        mainMenu.getMenuInflater().inflate(R.menu.main_menu, mainMenu.getMenu());

        //register menu with OnMenuItemClickListener
        mainMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.user_profile_menu_option)
                {
                    Intent userProfileIntent = new Intent(GroupChatsActivity.this, ProfileActivity.class);
                    //TODO: Pass the user information in the intent
                    startActivity(userProfileIntent);
                    finish();
                }
                else if (menuItem.getItemId() == R.id.new_group_menu_option)
                {
                    //TODO:create new group
                    groupCreation();
                }
                else
                {
                    Intent logout = new Intent(GroupChatsActivity.this, LoginActivity.class);
                    startActivity(logout);
                    finish();
                }
                return true;
            }
        });

        mainMenu.show();
    }

    //function to create a new group
    private void groupCreation()
    {
        AlertDialog.Builder newGroupBuilder = new AlertDialog.Builder(GroupChatsActivity.this);

        newGroupBuilder.setTitle("Enter group details");

        final View groupCreationView = getLayoutInflater().inflate(R.layout.create_group_layout, null);

        EditText groupNameField = groupCreationView.findViewById(R.id.new_group_name);
        EditText groupDescriptionField = groupCreationView.findViewById(R.id.new_group_description);
        EditText groupTopicField = groupCreationView.findViewById(R.id.new_group_topic);
        EditText groupTopicUrlField = groupCreationView.findViewById(R.id.new_group_topic_url);
        final RadioGroup groupStatusRadio = groupCreationView.findViewById(R.id.new_group_status_choices);
        final String[] checkedStatus = new String[1];

        groupStatusRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup statusGroup, int checkedSort)
            {
                //get the selected radiobutton
                RadioButton selectedSorting = (RadioButton) statusGroup.findViewById(checkedSort);

                //get the text of the selected id
                checkedStatus[0] = selectedSorting.getText().toString();
            }
        });


        //set the view for this Alert
        newGroupBuilder.setView(groupCreationView);

        newGroupBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();
                String groupDescription = groupDescriptionField.getText().toString();
                String groupTopic = groupTopicField.getText().toString();
                String groupTopicUrl = groupTopicUrlField.getText().toString();
                String statusChoice = checkedStatus[0];

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(GroupChatsActivity.this, "Please write group name", Toast.LENGTH_LONG).show();
                }

                else if(TextUtils.isEmpty(statusChoice))
                {
                    Toast.makeText(GroupChatsActivity.this, "Please choose group status", Toast.LENGTH_LONG).show();
                }
                else
                {
                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    String groupCreationDate = currentDateFormat.format((calForDate.getTime()));

                    Group createGroup = new Group(groupName, Globals.deviceUser.getUserName(), groupDescription, statusChoice, groupTopic, groupTopicUrl, groupCreationDate);
                    createNewGroup(createGroup);
                }
            }
        });

        newGroupBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();

            }
        });

        newGroupBuilder.show();

    }

    private void createNewGroup(Group group)
    {
        mDatabaseHelper.saveNewGroup(group).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(GroupChatsActivity.this, group.getName() + " is created successfully", Toast.LENGTH_SHORT);
                    Log.i(TAG, "createNewGroup() => group name " + group.getName() + " created successfully");
                    mDatabaseHelper.bannedAndRequestsInit(group.getUniqueKey());

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatsActivity.this, group.getName() + " failed to created", Toast.LENGTH_SHORT);
                Log.e(TAG, "createNewGroup() => group name " + group.getName() + " was not created");
                Log.e(TAG, "createNewGroup() => : " + e);
            }
        });
    }

}
