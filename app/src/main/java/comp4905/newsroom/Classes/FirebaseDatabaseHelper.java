package comp4905.newsroom.Classes;


import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseHelper {
    //for logging
    private static final String TAG = FirebaseDatabaseHelper.class.getName();

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserReference;



    public FirebaseDatabaseHelper()
    {
        mDatabase = FirebaseDatabase.getInstance();
        //mDatabase.setPersistenceEnabled(true);
        mUserReference = mDatabase.getReference("users");
    }

    public Task<Void> saveUser(User user)
    {
        if (user == null)
        {
            Log.i(TAG, "saveUser(): user is null");
        }
        else
        {
            Log.i(TAG, "saveUser(): saving user " + user.getUserName() + " to reference " + mUserReference.toString());
        }



        return mUserReference.child(user.getUserName()).setValue(user);
    }

    //GET USER PROFILE

    public Task<DataSnapshot> getUser(String username)
    {
        return mUserReference.child(username).get();
    }

    //READ ALL USER NEWS ARTICLES

    //SAVE LIKED NEWS ARTICLES
    public Task<Void> saveArticle(NewsArticle article, String userName)
    {
        DatabaseReference articleReference = mDatabase.getReference("users/" + userName + "/likedArticles");
        if(article == null)
        {
            Log.i(TAG, "saveArticle(): article is null");

        }
        else
        {
            Log.i(TAG, "saveArticle(): saving article " + article.getTitle() + " to reference " + article.toString());
        }

        return articleReference.push().setValue(article);

    }

    //GET LIKED NEWS ARTICLES


    //DELETE NEWS ARTICLE


}
