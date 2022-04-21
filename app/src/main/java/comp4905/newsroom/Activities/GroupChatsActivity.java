package comp4905.newsroom.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.Classes.Globals;
import comp4905.newsroom.Classes.Group;
import comp4905.newsroom.R;

public class GroupChatsActivity extends AppCompatActivity {

    private static final String TAG = GroupChatsActivity.class.getName();

    private ImageView groupsMenu;
    private Button mChatsButton;
    private Button mActiveChatsButton;
    private ListView mChatsListView;
    private ArrayList<String> mChatTitles;
    private HashMap<String, String> mGroups;
    private ArrayAdapter mChatTitlesAdapter;

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

        mChatsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                viewChat(position);
            }
        });
    }

    private void initializeFields()
    {
        groupsMenu = (ImageView) findViewById(R.id.group_chats_menu);
        mChatsButton = (Button) findViewById(R.id.my_chats_button);
        mActiveChatsButton = (Button) findViewById(R.id.active_chats_button);
        mChatsListView = (ListView) findViewById(R.id.user_chats_listview);

        mChatTitles = new ArrayList<>();
        mGroups = new HashMap<>();
        mChatTitlesAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mChatTitles);
        mChatsListView.setAdapter(mChatTitlesAdapter);

        //setup main menu
        groupsMenu.setOnClickListener((View view) -> { setUpMenu(); });

    }

    private void viewChat(int position)
    {

        String groupName = mChatTitles.get(position);
        String groupKey = mGroups.values().stream().collect(Collectors.toCollection(ArrayList::new)).get(position);

        groupKey = String.valueOf(mGroups.values().toArray()[position]);

        Intent chatActivityIntent = new Intent(GroupChatsActivity.this, ChatActivity.class);
        chatActivityIntent.putExtra("group_name", groupName);
        chatActivityIntent.putExtra("group_key", groupKey);
        startActivity(chatActivityIntent);

        Log.i(TAG, "viewChat() => viewing group with name " + groupName + " and key " + groupKey);

    }

    private void retrieveGroups()
    {
        mDatabaseHelper.getGroups().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Iterable<DataSnapshot> groupsSnapshot = dataSnapshot.getChildren();

                mGroups.clear();
                mChatTitles.clear();

                for(DataSnapshot group: groupsSnapshot)
                {
                    String currentGroupKey = group.getKey();
                    String currentGroupName = String.valueOf(group.child("title").getValue());

                    mGroups.put(currentGroupName, currentGroupKey);
                }

                mChatTitles.addAll(mGroups.keySet());


                //mChatTitles.addAll(set);
                mChatTitlesAdapter.notifyDataSetChanged();

                Log.i(TAG, "retrieveGroups() => retrieved groups: " + mChatTitles);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
                if(menuItem.getItemId() == R.id.about_menu_option)
                {
                    //TODO:present popup of abouts page
                }
                else if(menuItem.getItemId() == R.id.user_profile_menu_option)
                {
                    Intent userProfileIntent = new Intent(GroupChatsActivity.this, ProfileActivity.class);
                    //TODO: Pass the user information in the intent
                    startActivity(userProfileIntent);
                }
                else if (menuItem.getItemId() == R.id.new_group_menu_option)
                {
                    //TODO:create new group
                    groupCreation();
                }
                else
                {
                    //TODO: logout
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
        EditText groupTopicField = groupCreationView.findViewById(R.id.new_group_topic_url);
        final RadioGroup groupStatusRadio = groupCreationView.findViewById(R.id.new_group_status_choices);
        final String[] checkedStatus = new String[1];

        groupStatusRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup statusGroup, int checkedSort)
            {
                //get the selected radiobutton
                RadioButton selectedSorting = (RadioButton) statusGroup.findViewById(checkedSort);

                //get the text of the selected id
                checkedStatus[0] = selectedSorting.getText().toString().toLowerCase();
            }
        });


        //set the view for this Alert
        newGroupBuilder.setView(groupCreationView);

        newGroupBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();
                String groupDescription = groupDescriptionField.getText().toString();
                String groupTopicUrl = groupTopicField.getText().toString();
                String statusChoice = checkedStatus[0].toLowerCase();

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(GroupChatsActivity.this, "Please write group name", Toast.LENGTH_LONG);
                }

                else if(TextUtils.isEmpty(statusChoice))
                {
                    Toast.makeText(GroupChatsActivity.this, "Please choose group status", Toast.LENGTH_LONG);
                }
                else
                {
                    Group createGroup = new Group(groupName, Globals.deviceUser.getUserName(), groupDescription, statusChoice, groupTopicUrl);
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
