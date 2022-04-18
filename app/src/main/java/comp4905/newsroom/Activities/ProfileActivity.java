package comp4905.newsroom.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Arrays;

import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.Classes.Globals;
import comp4905.newsroom.R;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = ProfileActivity.class.getName();

    private ImageView mBackArrow;
    private ImageView mUserMenu;
    private ImageView mEditFavorites;
    private ImageView mChangePassword;

    private TextView mUserFullName;
    private EditText mUserName;
    private EditText mEmail;
    private EditText mPassword;

    private ListView mFavoriteTopics;
    private ArrayList<String> mTopics;
    private ArrayAdapter mTopicsArrayAdapter;
    private EditText mNewTopic;
    private Button mDoneAddingTopics;

    private ListView mOtherTopicsListView;
    private ArrayList<String> mOtherTopics;
    private ArrayAdapter mOtherTopicsArrayAdapter;

    //for firebase CRUD
    private FirebaseDatabaseHelper mDatabaseHelper = new FirebaseDatabaseHelper();

    private String[] topics = {"News", "Sport", "Technology", "World", "Finance", "Politics",
            "Business", "Economics", "Entertainment", "Beauty", "Gaming"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_profile);

        //inflate all UI elements
        mBackArrow = (ImageView) findViewById(R.id.profile_back_arrow);
        mUserMenu = (ImageView) findViewById(R.id.profile_user_menu);
        mEditFavorites = (ImageView) findViewById(R.id.profile_edit_favorites);
        mChangePassword = (ImageView) findViewById(R.id.profile_change_password);
        mUserFullName = (TextView) findViewById(R.id.profile_user_full_name);
        mUserName = (EditText) findViewById(R.id.profile_username);
        mEmail = (EditText) findViewById(R.id.profile_email);
        mPassword = (EditText) findViewById(R.id.profile_password);

        mFavoriteTopics = (ListView) findViewById(R.id.profile_favorite_topics);
        mOtherTopicsListView = (ListView) findViewById(R.id.profile_other_topics);
        mDoneAddingTopics = (Button) findViewById(R.id.profile_save_topics);

        //when activity starts, set all edit text to non editable
        disableTextViews();

        //setup the menu
        mUserMenu.setOnClickListener((View view) -> {setUpMenu();});

        //setup arrays and adapter
        setupArraysAndAdapters();

        //setup view user data
        setViewUserData();

        //onclick for back arrow
        mBackArrow.setOnClickListener((View view) ->
        {
            backToNewsActivity();
        });

        //long press topic to remove
        mFavoriteTopics.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                //remove topic at index position
                removeFavoriteTopic(position);
                return true;
            }
        });

        //edit topics
        mEditFavorites.setOnClickListener((View view) ->
        {
            //go into topics edit mode
            editTopicsMode(true);
        });

        //press to add topic
        mOtherTopicsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                addTopic(position);
            }
        });

        //done editing topics
        mDoneAddingTopics.setOnClickListener((View view) -> {
            //exit from topics edit mode
            editTopicsMode(false);

            //TODO: save new favorites to firebase
            mDatabaseHelper.updateFavoriteTopic(Globals.deviceUser.getUserName(), mTopics).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(ProfileActivity.this, "Favorite updated Successfully!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "doneAddingTopics() => Successfully Updated favorite topics");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "doneAddingTopics() => Failed to update favorite topics");
                }
            });
        });

        //onclick for change password
        mChangePassword.setOnClickListener((View view) -> {
            changePassword();
        });

    }

    //function to disable editing textviews
    private void disableTextViews()
    {
        //disable username edittext
        mUserName.setFocusable(false);
        mUserName.setClickable(false);

        //disable email edittext
        mEmail.setFocusable(false);
        mEmail.setClickable(false);

        //disable password edittext
        mPassword.setFocusable(false);
        mPassword.setClickable(false);
    }

    //function to set the textviews with users data
    private void setViewUserData()
    {
        String fullName = Globals.deviceUser.getFirstName() + " " + Globals.deviceUser.getLastName();
        mUserFullName.setText(fullName);
        mUserName.setText(Globals.deviceUser.getUserName());
        mEmail.setText(Globals.deviceUser.getEmail());
        mTopics.clear();
        mTopics.addAll(Globals.deviceUser.getFavorites());
        mTopicsArrayAdapter.notifyDataSetChanged();
    }

    //function to setup menu
    private void setUpMenu ()
    {
        //create instance of PopupMenu
        PopupMenu mainMenu = new PopupMenu(ProfileActivity.this, mUserMenu);

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
                else if( menuItem.getItemId() == R.id.user_profile_menu_option)
                {
                    //Do nothing
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

    //function to go back to news activity
    void backToNewsActivity()
    {
        Intent backToNews = new Intent(ProfileActivity.this, NewsActivity.class);
        startActivity(backToNews);

    }

    //initialize and setup array list
    private void setupArraysAndAdapters()
    {
        mTopics = new ArrayList<>();
        mOtherTopics = new ArrayList<>();

        //add elements to other topics
        for(String topic: topics)
        {
            if(!Globals.deviceUser.getFavorites().contains(topic))
            {
                mOtherTopics.add(topic);
            }
        }

        mTopicsArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mTopics);
        mOtherTopicsArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, mOtherTopics);

        mFavoriteTopics.setAdapter(mTopicsArrayAdapter);
        mOtherTopicsListView.setAdapter(mOtherTopicsArrayAdapter);
    }

    //function for long press to delete item from listview
    private void removeFavoriteTopic(int position)
    {
        String removedTopic = mTopics.remove(position);
        mTopicsArrayAdapter.notifyDataSetChanged();

        //add topic to the otherTopics list
        mOtherTopics.add(removedTopic);
        mOtherTopicsArrayAdapter.notifyDataSetChanged();
    }

    //functions to add other topics
    void editTopicsMode(boolean mode)
    {
        if(mode)
        {
            //make edit button invisible
            mEditFavorites.setVisibility(View.GONE);

            //make other topics appear
            mOtherTopicsListView.setVisibility(View.VISIBLE);

            //make save button appear
            mDoneAddingTopics.setVisibility(View.VISIBLE);
        }
        else
        {
            //make other topics invisible
            mOtherTopicsListView.setVisibility(View.GONE);

            //make save button invisible
            mDoneAddingTopics.setVisibility(View.GONE);

            //make edit butto appear
            mEditFavorites.setVisibility(View.VISIBLE);
        }

    }

    void addTopic(int position)
    {
        //remove topic clicked on from other topics
        String newTopic = mOtherTopics.remove(position);
        mOtherTopicsArrayAdapter.notifyDataSetChanged();

        //add topic to favorites
        mTopics.add(newTopic);
        mTopicsArrayAdapter.notifyDataSetChanged();

    }

    //function to change password
    private void changePassword()
    {
        //build an alert dialog to change the password
        AlertDialog.Builder changePassBuilder = new AlertDialog.Builder(ProfileActivity.this);

        //set the icon, cancelable and title
        changePassBuilder.setIcon(R.drawable.ic_action_warning);
        changePassBuilder.setCancelable(false);
        changePassBuilder.setTitle("Change Password");

        //view for the dialog
        final View changePassLayout = getLayoutInflater().inflate(R.layout.change_password_layout, null);
        EditText newPassText = changePassLayout.findViewById(R.id.new_password);
        EditText confirmNewPassText = changePassLayout.findViewById(R.id.confirm_new_password);
        TextView changePassMessage = changePassLayout.findViewById(R.id.change_pass_message);

        //set the view
        changePassBuilder.setView(changePassLayout);

        //set the positive button
        changePassBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newPassword = newPassText.getText().toString().trim();
                String confirmNewPassword = confirmNewPassText.getText().toString().trim();

                if(newPassword.equals(confirmNewPassword))
                {
                    changePassMessage.setText("Passwords match!");
                    mDatabaseHelper.savePassword(Globals.deviceUser.getUserName(), newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                Toast.makeText(ProfileActivity.this, "Password updates Successfully!", Toast.LENGTH_SHORT).show();
                                Log.i(TAG, "changePassword() => Successfully Changed Password");
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "changePassword() => Failed to save new password!");
                        }
                    });
                }
                else
                {
                    changePassMessage.setText("Passwords do not match. Please verify and try again!");

                }

            }
        });

        changePassBuilder.setNegativeButton("Cancel", null);

        //create the dialog and show
        AlertDialog changePassDialog = changePassBuilder.create();
        changePassDialog.show();

    }
}
