package comp4905.newsroom.Activities;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.R;

public class GroupChatsActivity extends AppCompatActivity {

    private static final String TAG = GroupChatsActivity.class.getName();

    private ImageView groupsMenu;
    private Button mChatsButton;
    private Button mActiveChatsButton;
    private ListView mChatsListView;
    private ArrayList<String> mChatTitles;
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
                String groupName = adapterView.getItemAtPosition(position).toString();

                Intent chatActivityIntent = new Intent(GroupChatsActivity.this, ChatActivity.class);
                chatActivityIntent.putExtra("group_name", groupName);
                startActivity(chatActivityIntent);
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
        mChatTitlesAdapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, mChatTitles);
        mChatsListView.setAdapter(mChatTitlesAdapter);

        //setup main menu
        groupsMenu.setOnClickListener((View view) -> { setUpMenu(); });

        //Hide Group chats menu item


    }

    private void retrieveGroups()
    {
        mDatabaseHelper.getGroups().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //hash set to make sure not to display duplicate group names
                Set<String> set = new HashSet<>();
                Iterator iterator = dataSnapshot.getChildren().iterator();

                while(iterator.hasNext())
                {
                    set.add(((DataSnapshot)iterator.next()).getKey());
                    //TODO: only add chats that the user is part of
                }

                mChatTitles.clear();
                mChatTitles.addAll(set);
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
                    requestNewManualGroup();
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
    private void requestNewManualGroup()
    {
        AlertDialog.Builder newGroupBuilder = new AlertDialog.Builder(GroupChatsActivity.this);

        newGroupBuilder.setTitle("Enter Group Name : ");

        final EditText groupNameField = new EditText(GroupChatsActivity.this);
        groupNameField.setHint("e.g: Cyber truck news");
        newGroupBuilder.setView(groupNameField);

        newGroupBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(GroupChatsActivity.this, "Please Write Group Name", Toast.LENGTH_LONG);
                }
                else
                {
                    createNewGroup(groupName);
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

    private void createNewGroup(String groupName)
    {
        mDatabaseHelper.saveNewGroup(groupName).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(GroupChatsActivity.this, groupName + " is created successfully", Toast.LENGTH_SHORT);
                    Log.i(TAG, "createNewGroup() => group name " + groupName + " created successfully");
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GroupChatsActivity.this, groupName + " failed to created", Toast.LENGTH_SHORT);
                Log.e(TAG, "createNewGroup() => group name " + groupName + " was not created");
                Log.e(TAG, "createNewGroup() => : " + e);
            }
        });
    }

}
