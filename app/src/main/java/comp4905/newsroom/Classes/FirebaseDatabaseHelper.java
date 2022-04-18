package comp4905.newsroom.Classes;


import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

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
    public Task<DataSnapshot> getArticles(String username)
    {
        return mUserReference.child(username + "/likedArticles").get();
    }


    //DELETE NEWS ARTICLE
    public Task<DataSnapshot> deleteArticle(String username, String url)
    {

        Task<DataSnapshot> articleTask = mUserReference.child(username + "/likedArticles").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                if(task.isComplete())
                {
                    if(task.getResult().exists())
                    {
                        Iterable<DataSnapshot> articleSnapshots = task.getResult().getChildren();

                        for(DataSnapshot article: articleSnapshots)
                        {
                            String articleURL = String.valueOf(article.child("link").getValue());

                            if(articleURL.equals(url))
                            {
                                Log.w(TAG, "deleteArticle() => deleting article titled '" + String.valueOf(article.child("title").getValue()) + "'");
                                DatabaseReference articleReference = mDatabase.getReference("users/" + username + "/likedArticles/" + article.getKey());

                                articleReference.removeValue();

                                break;
                            }
                        }
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "deleteArticle() => failed to remove article");

            }
        });


        return articleTask;
    }

    //SAVE PASSWORD
    public Task<Void> savePassword(String username, String newPassword)
    {
        DatabaseReference passwordReference = mDatabase.getReference("users/" + username + "/password");

        return passwordReference.setValue(newPassword);
    }

    //UPDATE FAVORITE TOPIC
    public Task<Void> updateFavoriteTopic(String username, ArrayList<String> favorites)
    {
        DatabaseReference favoritesReference = mDatabase.getReference("users/" + username + "/favorites");

        return favoritesReference.setValue(favorites);
    }


}
