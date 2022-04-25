package comp4905.newsroom.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.Classes.Globals;
import comp4905.newsroom.Classes.NewsArticle;
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
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
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
            finish();
        });

        loginButton.setOnClickListener((View v) -> {
            hideKeyboard();
            String userEnteredUsername = userLoginTextBox.getText().toString().trim();
            String userEnteredPassword = userPasswordTextBox.getText().toString().trim();
            login(userEnteredUsername, userEnteredPassword);

        });
    }

    private void hideKeyboard()
    {
        View view = this.getCurrentFocus();
        if(view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
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
                            String firstName = String.valueOf(dataSnapshot.child("firstName").getValue());
                            String lastName = String.valueOf(dataSnapshot.child("lastName").getValue());
                            String email = String.valueOf(dataSnapshot.child("email").getValue());
                            ArrayList<String> favorites = new ArrayList<>();
                            Iterable<DataSnapshot> favoritesSnapshot =  dataSnapshot.child("favorites").getChildren();

                            for (DataSnapshot topic : favoritesSnapshot)
                            {
                                favorites.add(topic.getValue(String.class));
                            }

                            //initialize the global user
                            Globals.deviceUser = new User(firstName, lastName, username, email, firebasePass, favorites);

                            //log user to make sure object was created
                            Log.i(TAG, "login() => user: " + Globals.deviceUser);

                            //retrieve user articles
                            retrieveLikedArticles(username);

                            //move to the news activity intent
                            Intent newsActivity = new Intent(LoginActivity.this, NewsActivity.class);
                            startActivity(newsActivity);
                            finish();

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
                        Log.e(TAG, "login(): user " + username + " NOT found");
                        loginErrorTextview.setText("Invalid username or password. Please try again!");
                    }
                }
                else
                {
                    Log.e(TAG, "login(): failed to get user " + username);
                    loginErrorTextview.setText("Invalid username or password. Please try again!");
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "login(): failed to get user " + username);
            }
        });
    }

    private void retrieveLikedArticles(String username)
    {
        mDatabseHelper.getArticles(username).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                Log.i(TAG, "retrieveLikedArticles() => retrieve user articles task complete");
                if(task.isSuccessful())
                {
                    if(task.getResult().exists())
                    {
                        Log.i(TAG, "retrieveLikedArticles() => articles for " + username + " found");

                        DataSnapshot articleSnapshot = task.getResult();

                        Iterable<DataSnapshot> iterableArticles = articleSnapshot.getChildren();

                        for(DataSnapshot article: iterableArticles)
                        {
                            String author = String.valueOf(article.child("author").getValue());
                            String country = String.valueOf(article.child("country").getValue());
                            String published = String.valueOf(article.child("datePublished").getValue());
                            String language = String.valueOf(article.child("language").getValue());
                            String link = String.valueOf(article.child("link").getValue());
                            String mediaLink = String.valueOf(article.child("mediaLink").getValue());
                            String publisher = String.valueOf(article.child("publisher").getValue());
                            String summary = String.valueOf(article.child("summary").getValue());
                            String title = String.valueOf(article.child("title").getValue());
                            String topic = String.valueOf(article.child("topic").getValue());
                            NewsArticle tempArticle = new NewsArticle(title, author, published, link, summary, topic,
                                    mediaLink, publisher, country, language);

                            Log.i(TAG, "retrieveLikedArticles() => " + tempArticle);
                            Globals.userLikedArticles.add(tempArticle);
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "retrieveLikedArticles(): failed to get liked articles for " + username);
            }
        });
    }
}