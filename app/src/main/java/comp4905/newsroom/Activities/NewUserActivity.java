package comp4905.newsroom.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.Collections;

import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.Classes.Globals;
import comp4905.newsroom.Classes.User;
import comp4905.newsroom.R;

public class NewUserActivity extends AppCompatActivity {

    //for logging
    private static final String TAG = NewUserActivity.class.getName();

    //layout variables
    private ImageView backToLogin;
    private EditText firstNameTextbox;
    private EditText lastNameTextbox;
    private EditText emailNameTextbox;
    private EditText userNameTextbox;
    private EditText passwordTextbox;
    private TextView selectTopicTextview;
    private Button registerButton;

    //user details
    private String mFirstName;
    private String mLastName;
    private String mEmail;
    private String mUsername;
    private String mPassword;
    private ArrayList<String> mFavoriteTopics = new ArrayList<>();
    //private User mNewUser;

    boolean[] selectedTopics;
    ArrayList<Integer> topicsList = new ArrayList<>();

    //to save user to firebase
    private FirebaseDatabaseHelper mDatabseHelper = new FirebaseDatabaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hide titlebar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_new_user);

        //inflate layout variables
        firstNameTextbox = (EditText) findViewById(R.id.first_name_textbox);
        lastNameTextbox = (EditText) findViewById(R.id.last_name_textbox);
        emailNameTextbox = (EditText) findViewById(R.id.email_textbox);
        userNameTextbox = (EditText) findViewById(R.id.username_textbox);
        passwordTextbox = (EditText) findViewById(R.id.password_textbox);
        selectTopicTextview = (TextView) findViewById(R.id.select_topic_text);
        registerButton = (Button) findViewById(R.id.register_button);
        backToLogin = (ImageView) findViewById(R.id.new_back_to_login);

        //initialize selectedTopic array
        selectedTopics = new boolean[Globals.topics.length];

        registerButton.setOnClickListener((View v)->
        {
            //call register user function
            if(registerUser())
            {
                mDatabseHelper.saveUser(Globals.deviceUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(NewUserActivity.this, "Creation successful", Toast.LENGTH_SHORT).show();
                            Log.i(TAG, "registerButtonOnClick() => new user " + Globals.deviceUser.getUserName() + " created successfully");

                            //INTENT TO MOVE TO NEWSACTIVITY
                            Intent newsActivity = new Intent(NewUserActivity.this, NewsActivity.class);
                            startActivity(newsActivity);
                            finish();
                        }
                    }
                }).addOnFailureListener(error ->
                {

                    Log.i(TAG, "registerButtonOnClick(): " + error.toString());
                });
            }
        });

        selectTopicTextview.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call get topics function
                getTopics();
            }
        }));

        backToLogin.setOnClickListener((View view) -> {
            Intent intent = new Intent(NewUserActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean registerUser()
    {
        mFirstName = firstNameTextbox.getText().toString().trim();
        String[] cannotContain = {".","#","$","[","]"};
        if(mFirstName.isEmpty())
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(NewUserActivity.this);

            alert.setTitle("Empty field!");
            alert.setMessage(("First Name field cannot be empty. \nPlease enter your first name and try again"));
            alert.setCancelable(true);
            alert.show();
            return false;

        }

        mLastName = lastNameTextbox.getText().toString().trim();
        if(mLastName.isEmpty())
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(NewUserActivity.this);

            alert.setTitle("Empty field!");
            alert.setMessage(("Last Name field cannot be empty. \nPlease enter your last name and try again"));
            alert.setCancelable(true);
            alert.show();
            return false;
        }

        mEmail = emailNameTextbox.getText().toString().trim();
        if(mEmail.isEmpty())
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(NewUserActivity.this);

            alert.setTitle("Empty field!");
            alert.setMessage(("Email field cannot be empty. \nPlease enter your email and try again"));
            alert.setCancelable(true);
            alert.show();
            return false;

        }

        mUsername = userNameTextbox.getText().toString().trim();
        if(mUsername.isEmpty())
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(NewUserActivity.this);

            alert.setTitle("Empty field!");
            alert.setMessage(("Username field cannot be empty. \nPlease enter a username and try again"));
            alert.setCancelable(true);
            alert.show();
            return false;

        }

        for(int i = 0; i < cannotContain.length;i++)
        {
            if(mUsername.contains(cannotContain[i]))
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(NewUserActivity.this);

                alert.setTitle("Invalid Character!");
                alert.setMessage(("Username field must no contain '.', '#', '$', '[', or ']'"));
                alert.setCancelable(true);
                alert.show();
                return false;
            }
        }

        //validate password
        mPassword = passwordTextbox.getText().toString().trim();
        if(mPassword.isEmpty())
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(NewUserActivity.this);

            alert.setTitle("Empty field!");
            alert.setMessage(("Password field cannot be empty. \nPlease enter a password and try again"));
            alert.setCancelable(true);
            alert.show();
            return false;
        }
        else if (mPassword.length() < 6)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(NewUserActivity.this);

            alert.setTitle("Password too short!");
            alert.setMessage(("Password needs to be at least 6 characters long. \nPlease enter a password and try again"));
            alert.setCancelable(true);
            alert.show();
            return false;

        }
        else
        {
            //check if password contains number
            boolean foundNumber = false;
            char[] passwordChars = mPassword.toCharArray();
            for(char c : passwordChars)
            {
                if(Character.isDigit(c))
                {
                    foundNumber = true;
                    break;
                }
            }

            if(!foundNumber)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(NewUserActivity.this);

                alert.setTitle("Password too needs a Number!");
                alert.setMessage(("Password needs to have at least 1 number. \nPlease enter a password and try again"));
                alert.setCancelable(true);
                alert.show();
                return false;
            }

        }

        if(mFavoriteTopics.size() < 2)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(NewUserActivity.this);

            alert.setTitle("Choose at least 2 topics!");
            alert.setMessage(("Pick at least 2 topics of interest and try again"));
            alert.setCancelable(true);
            alert.show();
            return false;
        }

        Globals.deviceUser = new User(mFirstName, mLastName, mUsername, mEmail, mPassword, mFavoriteTopics);
        Log.i(TAG, Globals.deviceUser.toString());

        return true;

    }

    private void getTopics()
    {
        AlertDialog.Builder chooseTopicsAlert = new AlertDialog.Builder(NewUserActivity.this);

        chooseTopicsAlert.setTitle("Choose at least 2 topics");
        chooseTopicsAlert.setCancelable(false);

        chooseTopicsAlert.setMultiChoiceItems(Globals.topics, selectedTopics, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean checkboxSelected) {
                // check condition
                if (checkboxSelected) {
                    // when checkbox selected
                    // Add position  in lang list
                    topicsList.add(i);
                    // Sort array list
                    Collections.sort(topicsList);
                } else {
                    // when checkbox unselected
                    // Remove position from langList
                    topicsList.remove(Integer.valueOf(i));
                }
            }
        });

        chooseTopicsAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // Initialize string builder
                StringBuilder stringBuilder = new StringBuilder();
                //clear the favorites arraylist
                mFavoriteTopics.clear();
                // use for loop
                for (int j = 0; j < topicsList.size(); j++) {
                    // concat array value
                    stringBuilder.append(Globals.topics[topicsList.get(j)]);
                    //add topic to favorites
                    mFavoriteTopics.add(Globals.topics[topicsList.get(j)]);
                    // check condition
                    if (j != topicsList.size() - 1) {
                        // When j value  not equal
                        // to lang list size - 1
                        // add comma
                        stringBuilder.append(", ");
                    }
                }
                // set text on textView
                selectTopicTextview.setText(stringBuilder.toString());
            }
        });

        chooseTopicsAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                dialogInterface.dismiss();
            }
        });

        chooseTopicsAlert.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // use for loop
                for (int j = 0; j < selectedTopics.length; j++) {
                    // remove all selection
                    selectedTopics[j] = false;
                    // clear language list
                    topicsList.clear();
                    //clear the favorites array
                    mFavoriteTopics.clear();
                    // clear text view value
                    selectTopicTextview.setText("");
                }
            }
        });

        //show the dialog
        chooseTopicsAlert.show();


    }
}