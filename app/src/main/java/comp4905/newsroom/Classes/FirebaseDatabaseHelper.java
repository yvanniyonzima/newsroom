package comp4905.newsroom.Classes;


import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import comp4905.newsroom.Activities.GroupChatsActivity;

public class FirebaseDatabaseHelper {
    //for logging
    private static final String TAG = FirebaseDatabaseHelper.class.getName();

    private FirebaseDatabase mDatabase;
    private DatabaseReference mUserReference;
    private DatabaseReference mGroupReference;




    public FirebaseDatabaseHelper()
    {
        mDatabase = FirebaseDatabase.getInstance();
        //mDatabase.setPersistenceEnabled(true);
        mUserReference = mDatabase.getReference("users");
        mGroupReference = mDatabase.getReference("chat_groups");
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

    //update user ban count


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

    //CREATE AND SAVE NEW MANUAL GROUP
    public Task<Void> saveNewGroup(Group group)
    {
        String groupKey = mGroupReference.push().getKey();
        group.setUniqueKey(groupKey);
        return mGroupReference.child(groupKey).setValue(group);
    }

    //INITIALIZE BANNED AND REQUESTS
    public void bannedAndRequestsInit(String groupKey)
    {
        mGroupReference.child(groupKey).child("bannedMembers").setValue("");
        mGroupReference.child(groupKey).child("joinRequests").setValue("");
        mGroupReference.child(groupKey).child("banNotification").setValue("");
    }

    //RETRIEVE GROUPS NAMES
    public DatabaseReference getGroups()
    {
        return mGroupReference;
    }

    //RETRIEVE SINGLE GROUP
    public DatabaseReference getSingleGroup(String groupKey)
    {
        return mGroupReference.child(groupKey);
    }

    //GET A NEW MESSAGE KEY
    public String getNewMessageKey(String groupKey)
    {
        return mGroupReference.child(groupKey).child("messages").push().getKey();

    }

    //SAVE GROUP MASSAGE
    public Task<Void> saveMessage(Message message, String groupKey)
    {
        return mGroupReference.child(groupKey).child("messages").child(message.getKey()).setValue(message);
    }

    //RETRIEVE MESSAGES FOR GROUPS
    public DatabaseReference getGroupMessages(String groupKey)
    {
        return mGroupReference.child(groupKey).child("messages");
    }

    //ADD USER TO GROUP MEMBERS
    public Task<DataSnapshot> getGroupMembers(String groupKey)
    {
        Log.i(TAG, "getGroupMembers() => retrieving members for group " + groupKey);
        return mGroupReference.child(groupKey).child("members").get();
    }

    public Task<Void> addUserToGroup(String groupKey, ArrayList<String> groupMembers, int numMembers)
    {

        //update the number of members
        mGroupReference.child(groupKey).child("numMembers").setValue(numMembers);

        //set the value for members
        return mGroupReference.child(groupKey).child("members").setValue(groupMembers);

    }

    public void updateGroupMembers(String groupKey, ArrayList<String> groupMembers, int numMembers)
    {
        mGroupReference.child(groupKey).child("members").setValue(groupMembers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                if (task.isComplete())
                {
                    Log.i(TAG, "updateGroupMembers() => Successfully added new member to group");

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Log.e(TAG, "updateGroupMembers() => Failed to add new member to group");

            }
        });

        mGroupReference.child(groupKey).child("numMembers").setValue(numMembers).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //SAVE REQUESTS TO JOIN GROUPS
    private Task<DataSnapshot> getGroupJoinRequests(String groupKey)
    {
        return mGroupReference.child(groupKey).child("joinRequests").get();
    }

    public void saveJoinRequest(String username,String groupKey)
    {
        ArrayList<String> joinRequests = new ArrayList<>();

        getGroupJoinRequests(groupKey).addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isComplete())
                {
                    if(task.getResult().exists())
                    {
                        Iterable<DataSnapshot> iterableRequests = task.getResult().getChildren();

                        for(DataSnapshot request: iterableRequests)
                        {
                            joinRequests.add(String.valueOf(request.getValue()));

                        }

                        joinRequests.add(username);

                        //update the join requests
                        updateRequestList(groupKey,joinRequests);

                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    public Task<Void> updateStatus(String groupKey, String status)
    {
        return mGroupReference.child(groupKey).child("status").setValue(status);
    }

    public Task<DataSnapshot> retrieveUserBanCount(String username)
    {
        return mUserReference.child(username).child("banCount").get();
    }

    public Task<Void> updateRequestList(String groupKey, ArrayList<String> usernames)
    {
        if(usernames.size() > 0)
        {
            return mGroupReference.child(groupKey).child("joinRequests").setValue(usernames);
        }
        else
        {
            return mGroupReference.child(groupKey).child("joinRequests").setValue("");
        }

    }

    public Task<Void> updateMembers(String groupKey, ArrayList<String> members, int membersCount)
    {
        mGroupReference.child(groupKey).child("numMembers").setValue(membersCount);
        return mGroupReference.child(groupKey).child("members").setValue(members);
    }

    public Task<Void> deleteGroupChat(String groupKey)
    {
        return mGroupReference.child(groupKey).removeValue();
    }

    public Task<Void> updateBanedNotifications(String groupKey, ArrayList<String> banNotifications)
    {
        if(banNotifications.size() > 0)
        {
            return mGroupReference.child(groupKey).child("banNotification").setValue(banNotifications);
        }
        else
        {
            return mGroupReference.child(groupKey).child("banNotification").setValue("");
        }

    }

    public Task<Void> updateBanedMembers(String groupKey, ArrayList<String> bannedMembers)
    {
        return mGroupReference.child(groupKey).child("bannedMembers").setValue(bannedMembers);
    }



}
