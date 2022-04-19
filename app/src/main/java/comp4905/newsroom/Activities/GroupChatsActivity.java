package comp4905.newsroom.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.R;

public class GroupChatsActivity extends AppCompatActivity {



    private FirebaseDatabaseHelper databaseHelper = new FirebaseDatabaseHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initializeFields()
    {


    }

    private void retrieveGroups()
    {
        databaseHelper.getGroups().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //hash set to make sure not to display duplicate group names

                Iterator iterator = dataSnapshot.getChildren().iterator();

                while(iterator.hasNext())
                {

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
