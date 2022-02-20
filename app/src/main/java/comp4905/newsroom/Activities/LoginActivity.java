package comp4905.newsroom.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.Classes.User;
import comp4905.newsroom.R;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getName();

    //layout variables
    private EditText userLoginTextBox;
    private EditText userPasswordTextBox;
    private Button loginButton;
    private TextView registerTextview;
    private TextView loginErrorTextview;

    //to get user from firebase
    private FirebaseDatabaseHelper mDatabseHelper = new FirebaseDatabaseHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //inflate layout variables
        userLoginTextBox = (EditText) findViewById(R.id.user_login_textbox);
        userPasswordTextBox = (EditText) findViewById(R.id.user_password_textbox);
        loginButton = (Button) findViewById(R.id.login_button);
        registerTextview = (TextView) findViewById(R.id.register_text);
        loginErrorTextview = (TextView) findViewById(R.id.login_error_texview);

        //onclick events
        registerTextview.setOnClickListener((View V) ->
        {
            //move to create new user activity
            Intent registerIntent = new Intent(LoginActivity.this, NewUserActivity.class);
            startActivity(registerIntent);
        });

        loginButton.setOnClickListener((View v) -> {

            String userEnteredUsername = userLoginTextBox.getText().toString().trim();
            String userEnteredPassword = userPasswordTextBox.getText().toString().trim();
            login(userEnteredUsername, userEnteredPassword);

        });
    }

    private void login(String username, String password)
    {
        mDatabseHelper.getUser(username).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task)
            {
                Log.i(TAG, "login(): task completed");
                if (task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        //username exists
                        Log.i(TAG, "login(): user " + username + " found");

                        //get the data
                        DataSnapshot dataSnapshot = task.getResult();

                        //compare passwords
                        String firebasePass = String.valueOf(dataSnapshot.child("password").getValue());

                        if(firebasePass.equals(password))
                        {
                            Log.i(TAG, "login(): password matches");

                            loginErrorTextview.setText("Successfully logged in!");

                            //get the data
                            String firstName = String.valueOf(dataSnapshot.child("firstname").getValue());
                            String lastName = String.valueOf(dataSnapshot.child("lastname").getValue());
                            String email = String.valueOf(dataSnapshot.child("email").getValue());
                            ArrayList<String> favorites = new ArrayList<>();
                            Iterable<DataSnapshot> favoritesSnapshot =  dataSnapshot.child("favorites").getChildren();

                            for (DataSnapshot topic : favoritesSnapshot)
                            {
                                favorites.add(topic.getValue(String.class));
                            }
                        }
                        else
                        {
                            Log.i(TAG, "login(): password DOES NOT match");
                            loginErrorTextview.setText("Invalid username or password. Please try again!");
                        }
                    }
                    else
                    {
                        //user does not exists
                        Log.i(TAG, "login(): user " + username + " NOT found");
                        loginErrorTextview.setText("Invalid username or password. Please try again!");
                    }
                }
                else
                {
                    Log.i(TAG, "login(): failed to get user " + username);
                    loginErrorTextview.setText("Invalid username or password. Please try again!");
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "login(): failed to get user " + username);
            }
        });
    }
}