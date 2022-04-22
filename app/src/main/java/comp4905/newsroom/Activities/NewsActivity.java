package comp4905.newsroom.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;

import comp4905.newsroom.Classes.ArticleCardItem;
import comp4905.newsroom.Classes.FirebaseDatabaseHelper;
import comp4905.newsroom.Classes.Globals;
import comp4905.newsroom.Classes.Group;
import comp4905.newsroom.Classes.NewsAPIClient;
import comp4905.newsroom.Classes.NewsArticle;
import comp4905.newsroom.Classes.RecyclerAdapters.LikedSearchRecyclerAdapter;
import comp4905.newsroom.Classes.RecyclerAdapters.NewsSearchRecyclerAdapter;
import comp4905.newsroom.Classes.ParseJSONResults;
import comp4905.newsroom.R;

public class NewsActivity extends AppCompatActivity {

    private static final String TAG = NewsActivity.class.getName();
    private ImageView mUserMenu;
    private SearchView mNewsSearchBar;
    private Button mSearchNewsButton;
    private RecyclerView mNewsRecyclerView;
    private RecyclerView.LayoutManager mRecyclerViewLayoutManager;
    private NewsSearchRecyclerAdapter mNewsRecyclerAdapter;
    private Button mFilterButton;

    //liked searches recyclerview
    private RecyclerView mLikedNewsRecycleView;
    private RecyclerView.LayoutManager mLikedNewsLayoutManager;
    private LikedSearchRecyclerAdapter mLikedNewsRecyclerAdapter;

    //news feed filter layouts, UI and filter variables
    private View mNewsFeedFilterView;
    private TextView mNewsFeedLanguages;
    private RadioGroup mNewsFeedSort;
    private RadioGroup mNewsFeedSentiment;
    private String mSelectedNewsFeedSorting = "";
    private String mSelectedNewsFeedSentiment = "";

    //world trending filter layout, UI and filer variables
    private View mWorldTrendingFilterView;
    private TextView mWorldTrendingLanguages;

    //topic search filter layout, UI and filter variables
    private View mTopicSearchFilterLayout;
    private TextView mTopicSearchLanguages;
    private RadioGroup mTopicSearchSorting;
    private String mSelectedTopicSearchSorting = "";

    //country news filter layout, UI and filter variable
    private View mCountryNewsFilterLayout;
    private Spinner mCountryNewsSource;
    private ArrayAdapter<String> mCountryNewsSourceSpinnerAdapter;
    private String mFromCountrySelected = "";
    private Spinner mCountryNewsAbout;
    private ArrayAdapter<String> mCountryNewsAboutSpinnerAdapter;
    private String mAboutCountrySelected = "";
    private TextView mCountryNewsLanguages;
    private RadioGroup mCountryNewsInternational;
    private String mCountryNewsInternationalSelected = "";

    private View mSubmitCancelFilterView;

    //view news and liked buttons
    private Button mNews;
    private Button mLikes;

    //filter submit and cancel buttons
    private Button mSubmitFilter;
    private Button mCancelFilter;


    private ArrayList<ArticleCardItem> mArticleCardItems = new ArrayList<>();
    private ArrayList<NewsArticle> mArticles = new ArrayList<>();

    private ArrayList<ArticleCardItem> mLikedArticleCardItems = new ArrayList<>();
    //using globals.userLikedArticles
    //private ArrayList<NewsArticle> mLikedArticles = new ArrayList<>();

    private boolean[] newsFeedSelectedLanguages = new boolean[Globals.languages.length];
    private ArrayList<Integer> newsFeedLanguages = new ArrayList<>();

    private boolean[] worldTrendingSelectedLanguages = new boolean[Globals.languages.length];
    private ArrayList<Integer> worldTrendingLanguages = new ArrayList<>();

    private boolean[] topicSearchSelectedLanguages = new boolean[Globals.languages.length];
    private ArrayList<Integer> topicSearchLanguages = new ArrayList<>();

    private boolean[] countryNewsSelectedLanguages = new boolean[Globals.languages.length];
    private ArrayList<Integer> countryNewsLanguages = new ArrayList<>();

    //database helper
    FirebaseDatabaseHelper mDatabaseHelper = new FirebaseDatabaseHelper();
    NewsAPIClient apiClient = new NewsAPIClient();
    ParseJSONResults newsJSONParser = new ParseJSONResults();

    //fro group creation
    private String mGroupStatusChoice = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_news);

        //inflate variables and filter views
        inflateVariables();

        //setup main menu
        mUserMenu.setOnClickListener((View view) -> { setUpMenu(); });

        //setup filter menu
        mFilterButton.setOnClickListener((View view) -> { filterButtonClick();});

        //filter items click listeners
        filtersOnClickListeners();

        //build the recycler view
        buildNewsRecyclerView();

        //get latest news with users categories
        Thread newsThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                try {
                    String responseString = apiClient.searchUserPreferredNews();
                    mArticles.addAll(newsJSONParser.parseNewsResults(responseString));

                    //Add articles to recycler adapter
                    for(NewsArticle article: mArticles)
                    {
                        if(!articleIsLiked(article.getLink()))
                        {
                            ArticleCardItem cardItem = new ArticleCardItem(article.getTitle(), article.getTopic(), article.getSummary(),
                                    article.getPublisher(), article.getDatePublished(), article.getLink());

                            mArticleCardItems.add(cardItem);
                        }

                    }

                    //build recyclerview
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            buildNewsRecyclerView();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        newsThread.start();

    }

    @Override
    protected void onStart() {
        super.onStart();

        //get latest news with users categories
        Thread newsThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                try {
                    String responseString = apiClient.searchUserPreferredNews();
                    mArticles.addAll(newsJSONParser.parseNewsResults(responseString));
                    mArticleCardItems.clear();

                    //Add articles to recycler adapter
                    for(NewsArticle article: mArticles)
                    {
                        if(!articleIsLiked(article.getLink()))
                        {
                            ArticleCardItem cardItem = new ArticleCardItem(article.getTitle(), article.getTopic(), article.getSummary(),
                                    article.getPublisher(), article.getDatePublished(), article.getLink());

                            mArticleCardItems.add(cardItem);
                        }

                    }

                    //notify of new data
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNewsRecyclerAdapter.notifyDataSetChanged();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        newsThread.start();
    }

    //function to inflate variable and filter views
    private void inflateVariables()
    {
        mUserMenu = (ImageView) findViewById(R.id.chat_settings_menu);
        mNewsSearchBar = (SearchView) findViewById(R.id.news_search_bar);
        mSearchNewsButton = (Button) findViewById(R.id.news_search_button);
        mFilterButton = (Button) findViewById(R.id.filter_news_button);
        mNews = (Button) findViewById(R.id.view_searches_button);
        mLikes = (Button) findViewById(R.id.liked_searches_button);

        mSearchNewsButton.setOnClickListener((View view) -> {
            handleTopicSearchFiltering();
        });

        mNews.setOnClickListener((View view) -> {
            mLikedNewsRecycleView.setVisibility(View.GONE);
            mNewsRecyclerView.setVisibility(View.VISIBLE);
        });

        mLikes.setOnClickListener((View view) -> {
            mNewsRecyclerView.setVisibility(View.GONE);
            mLikedNewsRecycleView.setVisibility(View.VISIBLE);
        });

        /*INFLATE FILTER VIEWS AND VARIABLES*/
        //news filter view and variables
        mNewsFeedFilterView = (LinearLayout) findViewById(R.id.news_feed_filter_view);
        mNewsFeedLanguages = (TextView) findViewById(R.id.news_feed_languages);
        mNewsFeedSort = (RadioGroup) findViewById(R.id.news_feed_sort_group);
        mNewsFeedSentiment = (RadioGroup) findViewById(R.id.news_feed_sentiment_group);


        //world trending view and variables
        mWorldTrendingFilterView = (LinearLayout) findViewById(R.id.world_trending_filter_view);
        mWorldTrendingLanguages = (TextView) findViewById(R.id.world_trending_languages);

        //topic search view and variables
        mTopicSearchFilterLayout = (LinearLayout) findViewById(R.id.topic_search_filter_view);
        mTopicSearchLanguages = (TextView) findViewById(R.id.topic_search_languages);
        mTopicSearchSorting = (RadioGroup) findViewById(R.id.topic_search_sort_group);

        //country news view and variables
        mCountryNewsFilterLayout = (LinearLayout) findViewById(R.id.country_news_filter_view);
        mCountryNewsSource = (Spinner) findViewById(R.id.country_news_source);
        mCountryNewsAbout = (Spinner) findViewById(R.id.country_news_about);
        mCountryNewsLanguages = (TextView) findViewById(R.id.country_news_languages);
        mCountryNewsInternational = (RadioGroup) findViewById(R.id.country_news_international_group);
        //set the data for the spinner
        mCountryNewsSourceSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Globals.countries);
        mCountryNewsSource.setAdapter(mCountryNewsSourceSpinnerAdapter);

        mCountryNewsAboutSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Globals.countries);
        mCountryNewsAbout.setAdapter(mCountryNewsAboutSpinnerAdapter);


        //cancel and submit filter view and buttons
        mSubmitCancelFilterView = (LinearLayout) findViewById(R.id.filter_submit_cancel_view);
        mSubmitFilter = (Button) findViewById(R.id.submit_news_feed_filter);
        mCancelFilter = (Button) findViewById(R.id.cancel_news_feed_filter);
    }

    //function to setup menu
    private void setUpMenu ()
    {
        //create instance of PopupMenu
        PopupMenu mainMenu = new PopupMenu(NewsActivity.this, mUserMenu);

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
                    Intent userProfileIntent = new Intent(NewsActivity.this, ProfileActivity.class);
                    //TODO: Pass the user information in the intent
                    startActivity(userProfileIntent);
                }
                else if(menuItem.getItemId() == R.id.group_chats_menu_option)
                {
                    Intent groupChatsIntent = new Intent(NewsActivity.this, GroupChatsActivity.class);
                    startActivity(groupChatsIntent);

                }
                else if (menuItem.getItemId() == R.id.new_group_menu_option)
                {
                    //TODO:create new group
                    groupCreation(false, null);
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
    private void groupCreation(boolean fromArticle, NewsArticle article)
    {
        AlertDialog.Builder newGroupBuilder = new AlertDialog.Builder(NewsActivity.this);

        newGroupBuilder.setTitle("Enter group details");

        final View groupCreationView = getLayoutInflater().inflate(R.layout.create_group_layout, null);

        EditText groupNameField = groupCreationView.findViewById(R.id.new_group_name);
        EditText groupDescriptionField = groupCreationView.findViewById(R.id.new_group_description);
        EditText groupTopicField = groupCreationView.findViewById(R.id.new_group_topic);
        EditText groupTopicUrlField = groupCreationView.findViewById(R.id.new_group_topic_url);
        final RadioGroup groupStatusRadio = groupCreationView.findViewById(R.id.new_group_status_choices);
        final String[] checkedStatus = new String[1];

        groupStatusRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup statusGroup, int checkedSort)
            {
                //get the selected radiobutton
                RadioButton selectedSorting = (RadioButton) statusGroup.findViewById(checkedSort);

                //get the text of the selected id
                checkedStatus[0] = selectedSorting.getText().toString();
            }
        });


        //set the view for this Alert
        newGroupBuilder.setView(groupCreationView);

        if(fromArticle)
        {
            groupTopicField.setText(article.getTitle());
            groupTopicUrlField.setText(article.getLink());
            groupDescriptionField.setText("Lets talk about " + article.getTitle());
        }

        newGroupBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String groupName = groupNameField.getText().toString();
                String groupDescription = groupDescriptionField.getText().toString();
                String groupTopic = groupTopicField.getText().toString();
                String groupTopicUrl = groupTopicUrlField.getText().toString();
                String statusChoice = checkedStatus[0];

                if (TextUtils.isEmpty(groupName))
                {
                    Toast.makeText(NewsActivity.this, "Please write group name", Toast.LENGTH_LONG);
                }

                else if(TextUtils.isEmpty(statusChoice))
                {
                    Toast.makeText(NewsActivity.this, "Please choose group status", Toast.LENGTH_LONG);
                }
                else
                {
                    Calendar calForDate = Calendar.getInstance();
                    SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
                    String groupCreationDate = currentDateFormat.format((calForDate.getTime()));

                    Group createGroup = new Group(groupName, Globals.deviceUser.getUserName(), groupDescription, statusChoice, groupTopic, groupTopicUrl, groupCreationDate);
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
                    Toast.makeText(NewsActivity.this, group.getName() + " is created successfully", Toast.LENGTH_SHORT);
                    Log.i(TAG, "createNewGroup() => group name " + group.getName() + " created successfully");

                    //go to groups activity

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(NewsActivity.this, group.getName() + " failed to created", Toast.LENGTH_SHORT);
                Log.e(TAG, "createNewGroup() => group name " + group.getName() + " was not created");
                Log.e(TAG, "createNewGroup() => : " + e);
            }
        });
    }

    //function to build the recycler view
    private void buildNewsRecyclerView()
    {

        //setup news recycler adapter
        mNewsRecyclerView = (RecyclerView) findViewById(R.id.news_search_recyclerView);
        mNewsRecyclerView.setHasFixedSize(true);
        mRecyclerViewLayoutManager = new LinearLayoutManager(NewsActivity.this);
        mNewsRecyclerAdapter = new NewsSearchRecyclerAdapter(mArticleCardItems);
        mNewsRecyclerView.setLayoutManager(mRecyclerViewLayoutManager);
        mNewsRecyclerView.setAdapter(mNewsRecyclerAdapter);

        //implement the interface for the different on clicks
        mNewsRecyclerAdapter.setOnItemClickListener(new NewsSearchRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onSummaryClicked(int position) {
                //TODO: set the text box size to wrap content
            }

            @Override
            public void onChatBubbleClick(int position) {
                groupCreation(true, mArticles.get(position));
            }

            @Override
            public void onExternalLinkClick(int position) {
                String link = mArticles.get(position).getLink();
                Uri uri = Uri.parse(link);
                Log.i(TAG,"buildNewsRecyclerView() => following link: " + link);
                Intent visitArticlePage = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(visitArticlePage);
                }catch (ActivityNotFoundException exception)
                {
                    Toast.makeText(NewsActivity.this, "No browswer found to open webpage", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onLikeClick(int position) {

                likeNewsArticle(position);

            }
        });

        //setup liked news recycler adapter
        mLikedNewsRecycleView = (RecyclerView) findViewById(R.id.liked_search_recyclerView);
        mLikedNewsRecycleView.setHasFixedSize(true);
        mLikedNewsLayoutManager = new LinearLayoutManager(NewsActivity.this);
        mLikedNewsRecyclerAdapter = new LikedSearchRecyclerAdapter(mLikedArticleCardItems);
        mLikedNewsRecycleView.setLayoutManager(mLikedNewsLayoutManager);
        mLikedNewsRecycleView.setAdapter(mLikedNewsRecyclerAdapter);

        //implement the interface for the different on clicks
        mLikedNewsRecyclerAdapter.setOnItemClickListener(new LikedSearchRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onSummaryClicked(int position) {
                //TODO: set the text box size to wrap content
            }

            @Override
            public void onChatBubbleClick(int position) {
                groupCreation(true, Globals.userLikedArticles.get(position));
            }

            @Override
            public void onExternalLinkClick(int position) {
                String link = mArticles.get(position).getLink();
                Uri uri = Uri.parse(link);
                Log.i(TAG,"buildNewsRecyclerView() => following link: " + link);
                Intent visitArticlePage = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(visitArticlePage);
                }catch (ActivityNotFoundException exception)
                {
                    Toast.makeText(NewsActivity.this, "No browswer found to open webpage", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(int position) {

                deleteLikedArticle(position);

            }
        });

        //populate the liked articles recycler with globals.userLikedArticles
        if(Globals.userLikedArticles.size() > 0)
        {
            for(NewsArticle article : Globals.userLikedArticles)
            {
                ArticleCardItem tempArticleItem = new ArticleCardItem(article.getTitle(), article.getTopic(), article.getSummary(),
                        article.getPublisher(), article.getDatePublished(), article.getLink());
                mLikedArticleCardItems.add(tempArticleItem);
            }

            Log.i(TAG, "BuildRecyclerView() => size of liked articles card item: " + mLikedArticleCardItems.size());

            mLikes.setText("Likes: " + Globals.userLikedArticles.size());

            //notify the recycler view
            mLikedNewsRecyclerAdapter.notifyDataSetChanged();
        }

    }

    private void likeNewsArticle(int position)
    {
        Globals.userLikedArticles.add(mArticles.get(position));
        mLikedArticleCardItems.add(mArticleCardItems.get(position));

        //save to firebase
        mDatabaseHelper.saveArticle(mArticles.get(position), Globals.deviceUser.getUserName()).addOnSuccessListener(success ->
        {
            Toast.makeText(NewsActivity.this, "Article Saved Successfully!", Toast.LENGTH_SHORT).show();

            if(success != null)
            {
                Log.i(TAG, "buildNewsRecyclerView() -> onLikeClick(): " + success.toString());
            }


        }).addOnFailureListener(error ->
        {
            Toast.makeText(NewsActivity.this, "Failed to save Article!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "buildNewsRecyclerView() -> onLikeClick(): " + error);
        });

        //remove articles form current search
        mArticles.remove(position);
        mArticleCardItems.remove(position);

        mNewsRecyclerAdapter.notifyDataSetChanged();
        mLikedNewsRecyclerAdapter.notifyDataSetChanged();

        if(Globals.userLikedArticles.size() > 0)
        {
            String likesText = "Likes: " + Globals.userLikedArticles.size();
            mLikes.setText(likesText);
        }
        else
        {
            mLikes.setText("Likes");
        }

    }

    private boolean articleIsLiked(String url)
    {
        for(NewsArticle article: Globals.userLikedArticles)
        {
            //Log.i(TAG, "articleIsLiked() => comparing " + url + " to" + article.getLink());
            if(article.getLink().equals(url))
            {
                return true;
            }
        }

        return false;
    }

    private void deleteLikedArticle(int position)
    {
        AlertDialog.Builder deleteArticleAlert = new AlertDialog.Builder(NewsActivity.this);

        deleteArticleAlert.setTitle("Delete Saved Article");
        deleteArticleAlert.setMessage("Are you sure you want to delete this article?");
        deleteArticleAlert.setIcon(R.drawable.ic_action_warning);
        deleteArticleAlert.setCancelable(false);
        deleteArticleAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String urlDeleted = Globals.userLikedArticles.get(position).getLink();

                mArticles.add(Globals.userLikedArticles.get(position));

                mArticleCardItems.add(mLikedArticleCardItems.get(position));

                Globals.userLikedArticles.remove(position);
                mLikedArticleCardItems.remove(position);

                mLikedNewsRecyclerAdapter.notifyDataSetChanged();
                mNewsRecyclerAdapter.notifyDataSetChanged();

                if(Globals.userLikedArticles.size() > 0)
                {
                    String likesText = "Likes: " + Globals.userLikedArticles.size();
                    mLikes.setText(likesText);
                }
                else
                {
                    mLikes.setText("Likes");
                }

                //delete the article from firebase
                mDatabaseHelper.deleteArticle(Globals.deviceUser.getUserName(), urlDeleted);
            }
        });

        deleteArticleAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        deleteArticleAlert.show();
    }


    //function to show menu of filtering options
    public void filterButtonClick()
    {
        PopupMenu filterMenu = new PopupMenu(NewsActivity.this, mFilterButton);

        //inflate instance of popup menu
        filterMenu.getMenuInflater().inflate(R.menu.filter_menu, filterMenu.getMenu());

        //register menu with OnMenuItemClickListener
        filterMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getItemId() == R.id.news_feed)
                {
                    //set text for the filter button
                    mFilterButton.setText("Filter: News Feed");

                    //make view for news feed filter visible
                    mNewsFeedFilterView.setVisibility(View.VISIBLE);

                    mWorldTrendingFilterView.setVisibility(View.GONE);
                    mTopicSearchFilterLayout.setVisibility(View.GONE);
                    mCountryNewsFilterLayout.setVisibility(View.GONE);
                }
                else if(menuItem.getItemId() == R.id.world_trending)
                {
                    //set the text for the filter button
                    mFilterButton.setText("Filter: World Trending");

                    //make view for world trending feed filter visible
                    mWorldTrendingFilterView.setVisibility(View.VISIBLE);

                    mNewsFeedFilterView.setVisibility(View.GONE);
                    mTopicSearchFilterLayout.setVisibility(View.GONE);
                    mCountryNewsFilterLayout.setVisibility(View.GONE);

                }
                else if (menuItem.getItemId() == R.id.topic_search)
                {
                    //set the text for the filter button
                    mFilterButton.setText("Filter: Topic Search");

                    //make view for the topic search filter visible
                    mTopicSearchFilterLayout.setVisibility(View.VISIBLE);

                    mNewsFeedFilterView.setVisibility(View.GONE);
                    mWorldTrendingFilterView.setVisibility(View.GONE);
                    mCountryNewsFilterLayout.setVisibility(View.GONE);
                }
                else //its country news
                {
                    //set the text for the filter button
                    mFilterButton.setText("Filter: Country News");

                    //make the view for the country news visible
                    mCountryNewsFilterLayout.setVisibility(View.VISIBLE);

                    mNewsFeedFilterView.setVisibility(View.GONE);
                    mWorldTrendingFilterView.setVisibility(View.GONE);
                    mTopicSearchFilterLayout.setVisibility(View.GONE);

                }

                //make submit and cancel buttons appear
                mSubmitCancelFilterView.setVisibility(View.VISIBLE);

                return true;
            }
        });

        filterMenu.show();
    }

    //setup on click listeners for filter elements
    void filtersOnClickListeners()
    {
        //news feed filter listeners
        mNewsFeedLanguages.setOnClickListener((View view) -> {
            filterLanguageClick(newsFeedSelectedLanguages, newsFeedLanguages, mNewsFeedLanguages);});
        mNewsFeedSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup sortGroup, int checkedSort)
            {
                //get the selected radiobutton
                RadioButton selectedSorting = (RadioButton) sortGroup.findViewById(checkedSort);

                //get the text of the selected id
                mSelectedNewsFeedSorting = selectedSorting.getText().toString().toLowerCase();
            }
        });
        mNewsFeedSentiment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup sentimentGroup, int checkedSentiment)
            {
                //get the selected radiobutton
                RadioButton selectedSentiment = (RadioButton) sentimentGroup.findViewById(checkedSentiment);

                //get the text
                mSelectedNewsFeedSentiment = selectedSentiment.getText().toString().toLowerCase();

            }
        });

        //world trending listeners
        mWorldTrendingLanguages.setOnClickListener((View view) -> {
            filterLanguageClick(worldTrendingSelectedLanguages, worldTrendingLanguages, mWorldTrendingLanguages);
        });

        //topic search listeners
        mTopicSearchLanguages.setOnClickListener((View view) -> {
            filterLanguageClick(topicSearchSelectedLanguages, topicSearchLanguages, mTopicSearchLanguages);
        });

        mTopicSearchSorting.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup sortingGroup, int checkedSorting) {

                //get the selected radiobutton
                RadioButton selectedSorting = (RadioButton) sortingGroup.findViewById(checkedSorting);

                //get the text
                mSelectedTopicSearchSorting = selectedSorting.getText().toString().toLowerCase();

            }
        });

        //country news listeners
        mCountryNewsLanguages.setOnClickListener((View view) -> {
            filterLanguageClick(countryNewsSelectedLanguages, countryNewsLanguages, mCountryNewsLanguages);
        });

        mCountryNewsInternational.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup internationalGroup, int checkedChoice) {

                //get the selected group
                RadioButton selectedChoice = (RadioButton) internationalGroup.findViewById(checkedChoice);

                //get the text
                mCountryNewsInternationalSelected = selectedChoice.getText().toString().toLowerCase();

            }
        });

        mCountryNewsSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mFromCountrySelected = adapterView.getItemAtPosition(i).toString();
                //Log.i(TAG, "filtersOnClickListeners() => country news source selected: " + mFromCountrySelected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mCountryNewsAbout.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mAboutCountrySelected = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //filter submit and cancel
        mSubmitFilter.setOnClickListener((View view) -> { handleSubmitFilter(); });
        mCancelFilter.setOnClickListener((View view) -> { hideFilterViews();});
    }

    //functions for language filtering
    private void filterLanguageClick(boolean[] selectedLanguages, ArrayList<Integer> languages, TextView selectedLanguagesView)
    {
        AlertDialog.Builder newsFeedLanguageBuilder = new AlertDialog.Builder(NewsActivity.this);

        newsFeedLanguageBuilder.setTitle("Choose languages");
        newsFeedLanguageBuilder.setCancelable(false);

        newsFeedLanguageBuilder.setMultiChoiceItems(Globals.languages, selectedLanguages, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean checkboxSelected)
            {
                if (checkboxSelected)
                {
                    //when checkbox selected, add position in lang list
                    languages.add(i);
                    //sort arraylist
                    Collections.sort(languages);
                }
                else
                {
                    //when checkbox is unselected, remove position from list
                    languages.remove(Integer.valueOf(i));
                }
            }
        });

        //positive button
        newsFeedLanguageBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                //initialize string builder
                StringBuilder stringBuilder = new StringBuilder();

                for(int j = 0; j < languages.size(); j++)
                {
                    //concatenate choices
                    stringBuilder.append(Globals.languages[languages.get(j)]);

                    //add comma
                    if(j != languages.size() - 1)
                    {
                        stringBuilder.append(", ");
                    }
                }

                //set the textview
                selectedLanguagesView.setText(stringBuilder.toString());

            }
        });

        //negative button
        newsFeedLanguageBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // dismiss dialog
                dialogInterface.dismiss();
            }
        });

        //neutral button
        newsFeedLanguageBuilder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // use for loop
                for (int j = 0; j < selectedLanguages.length; j++) {
                    // remove all selection
                    selectedLanguages[j] = false;
                    // clear language list
                    languages.clear();
                    // clear text view value
                    selectedLanguagesView.setText("");
                }
            }
        });

        //show the dialog
        newsFeedLanguageBuilder.show();

    }

    private void hideFilterViews()
    {
        //set all filter views visibility to gone
        mNewsFeedFilterView.setVisibility(View.GONE);
        mWorldTrendingFilterView.setVisibility(View.GONE);
        mTopicSearchFilterLayout.setVisibility(View.GONE);
        mCountryNewsFilterLayout.setVisibility(View.GONE);
        mSubmitCancelFilterView.setVisibility(View.GONE);
    }

    //function to submit the news feed filter and make search
    private void handleSubmitFilter()
    {
        if(mNewsFeedFilterView.getVisibility() == View.VISIBLE)
        {
            //call news feed filter handler function
            handleNewsFeedFilter();
        }
        else if(mWorldTrendingFilterView.getVisibility() == View.VISIBLE)
        {
            //call world trending filter handler function
            handleWorldTrendingFilter();
        }
        else if (mTopicSearchFilterLayout.getVisibility() == View.VISIBLE)
        {
            //call topic search filter handler function
            handleTopicSearchFiltering();
        }
        else if(mCountryNewsFilterLayout.getVisibility() == View.VISIBLE)
        {
            //call country news filter handler
            Log.i(TAG, "Country news filter layout visible");
            handleCountryNewsFiltering();
        }

        //hide the filter views
        hideFilterViews();

        //show search recycler
        mNewsRecyclerView.setVisibility(View.VISIBLE);

        //hide likes recycler
        mLikedNewsRecycleView.setVisibility(View.GONE);

    }

    //on submit for the news feed filter
    private void handleNewsFeedFilter()
    {
        //make an array of languages
        String languages = mNewsFeedLanguages.getText().toString().trim();
        String[] languagesArray = new String[0];

        if(!languages.isEmpty() || languages != null)
        {
            languagesArray = languages.split(", ");
        }
        else
        {
            Log.i(TAG, "handleNewsFeedFilter() => no language selected");
        }

        //log to make sure of selections
        Log.i(TAG, "submitNewsFeedFilter() => " +
                "{Languages: " + Arrays.toString(languagesArray)+ "}, " +
                "{ Sorting: " + mSelectedNewsFeedSorting + "}, " +
                "{Sentiment: " + mSelectedNewsFeedSentiment + "}");

        //make and start the thread to search to the news feed
        String[] finalLanguagesArray = languagesArray;
        Thread newsFeedSearchThread = new Thread()
        {
            @Override
            public void run() {
                super.run();

                try
                {
                    String newsFeedResults = apiClient.searchNewsFeed(finalLanguagesArray, mSelectedNewsFeedSorting, mSelectedNewsFeedSentiment);
                    ArrayList<NewsArticle> newsFeedArticles = new ArrayList<>();
                    newsFeedArticles.addAll(newsJSONParser.parseNewsResults(newsFeedResults));

                    //clear recycler adapter
                    mArticleCardItems.clear();

                    //Add articles to recycler adapter
                    for(NewsArticle article: newsFeedArticles)
                    {
                        //show the articles that are not liked
                        if(!articleIsLiked(article.getLink()))
                        {
                            ArticleCardItem cardItem = new ArticleCardItem(article.getTitle(), article.getTopic(), article.getSummary(),
                                    article.getPublisher(), article.getDatePublished(), article.getLink());

                            mArticleCardItems.add(cardItem);
                        }


                    }

                    //notify that the data changed
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNewsRecyclerAdapter.notifyDataSetChanged();
                        }
                    });

                }
                catch (IOException exception)
                {
                    Log.e(TAG, "handleNewsFeedFilter => failed to fetch news feed: " + exception);
                }
            }
        };

        //start the thread
        newsFeedSearchThread.start();

    }

    //on submit for the world trending filter
    private void handleWorldTrendingFilter()
    {
        //make an array of languages
        String worldTrendingLanguages = mWorldTrendingLanguages.getText().toString().trim();
        String[] worldTrendingLanguagesArray = new String[0];

        if(!worldTrendingLanguages.isEmpty() || worldTrendingLanguages != null)
        {
            worldTrendingLanguagesArray = worldTrendingLanguages.split(", ");
        }
        else
        {
            Log.i(TAG, "submitNewsFeedFilter() => no language selected");
        }

        //log to make sure of selections
        Log.i(TAG, "handleWorldTrendingFilter() => " +
                "{Languages: " + Arrays.toString(worldTrendingLanguagesArray));

        //make and start a thread to search for world trending news
        String[] finalWorldTrendingLanguagesArray = worldTrendingLanguagesArray;

        Thread getWorldTrendingNews = new Thread()
        {
            @Override
            public void run() {
                super.run();

                try
                {
                    //TODO: call api helper function to get world trending news
                    String worldTrendingResults = apiClient.searchWorldTrending(finalWorldTrendingLanguagesArray);

                    //parse the results
                    ArrayList<NewsArticle> worldTrendingArticles = new ArrayList<>();
                    worldTrendingArticles.addAll(newsJSONParser.parseWorldTrendingResults(worldTrendingResults));

                    //clear recycler adapter
                    mArticleCardItems.clear();

                    //Add articles to recycler adapter: ONLY THOSE THAT ARE NOT LIKED

                    for(NewsArticle worldTrendArticle: worldTrendingArticles)
                    {
                        if(!articleIsLiked(worldTrendArticle.getLink()))
                        {
                            ArticleCardItem worldTrendCardItem = new ArticleCardItem(worldTrendArticle.getTitle(), worldTrendArticle.getTopic(), worldTrendArticle.getSummary(),
                                    worldTrendArticle.getPublisher(), worldTrendArticle.getDatePublished(), worldTrendArticle.getLink());

                            mArticleCardItems.add(worldTrendCardItem);
                        }

                    }

                    //notify that the data changed
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNewsRecyclerAdapter.notifyDataSetChanged();
                        }
                    });
                }
                catch (IOException exception)
                {
                    Log.e(TAG, "handleWorldTrendingFilter => failed to fetch news feed: " + exception);
                }
            }
        };

        //start the thread
        getWorldTrendingNews.start();

    }

    //on submit for topic search
    private void handleTopicSearchFiltering()
    {
        String topic = mNewsSearchBar.getQuery().toString().trim();

        if(!topic.isEmpty() && topic != null)
        {
            String[] topicArray = topic.split(" ");

            //get the languages
            String topicSearchLanguages = mTopicSearchLanguages.getText().toString().trim();
            String[] topicSearchLanguagesArray = new String[0];

            if(!topicSearchLanguages.isEmpty() && topicSearchLanguages != null)
            {
                topicSearchLanguagesArray = topicSearchLanguages.split("' ");
            }
            else
            {
                topicSearchLanguagesArray[0] = "English";
            }

            String[] finalTopicArray = topicArray;
            String[] finalTopicSearchLanguages = topicSearchLanguagesArray;

            Thread topicSearchThread = new Thread()
            {
                @Override
                public void run() {
                    super.run();

                    try {

                        String topicSearchResult = apiClient.searchTopic(finalTopicArray, finalTopicSearchLanguages, mSelectedTopicSearchSorting);

                        //get the articles
                        ArrayList<NewsArticle> topicSearchArticles = new ArrayList<>();
                        topicSearchArticles.addAll(newsJSONParser.parseNewsResults(topicSearchResult));

                        //clear recycler adapter
                        mArticleCardItems.clear();

                        //Add articles to recycler adapter: ONLY THOSE THAT ARE NOT LIKED
                        for(NewsArticle topiSearchArticle: topicSearchArticles)
                        {
                            if(!articleIsLiked(topiSearchArticle.getLink()))
                            {
                                ArticleCardItem topicSearchCardItem = new ArticleCardItem(topiSearchArticle.getTitle(), topiSearchArticle.getTopic(), topiSearchArticle.getSummary(),
                                        topiSearchArticle.getPublisher(), topiSearchArticle.getDatePublished(), topiSearchArticle.getLink());

                                mArticleCardItems.add(topicSearchCardItem);
                            }
                        }

                        //notify that the data changed
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mNewsRecyclerAdapter.notifyDataSetChanged();
                            }
                        });

                    }catch (IOException e)
                    {
                        Log.e(TAG, "handleTopicSearchFiltering() => failed to search topic '" + topic + ",: " + e);
                    }
                }
            };

            topicSearchThread.start();

        }
        else
        {
            //make alert dialog for empty search bar
            AlertDialog.Builder emptySearchbar = new AlertDialog.Builder(NewsActivity.this);

            emptySearchbar.setTitle("Empty Search Field");
            emptySearchbar.setMessage("Please enter a query into the search bar");
            emptySearchbar.setIcon(R.drawable.ic_action_warning);
            emptySearchbar.setCancelable(true);
            emptySearchbar.setPositiveButton("OK", null);
            emptySearchbar.show();
        }

    }

    //handle country news filtering
    private void handleCountryNewsFiltering()
    {
        String countryFilterLanguages = mCountryNewsLanguages.getText().toString();
        String[] countryFilterLanguagesArray = new String[0];

        if(countryFilterLanguages.isEmpty() || countryFilterLanguages == null)
        {
            countryFilterLanguagesArray[0] = "English";
        }
        else
        {
            countryFilterLanguagesArray = countryFilterLanguages.split(", ");
        }

        String[] finalCountryFilterLanguagesArray = countryFilterLanguagesArray;
        String fromCountry = Globals.countryCodes.get(mFromCountrySelected);
        String aboutCountry = Globals.countryCodes.get(mAboutCountrySelected);
        String onlyInternational = "";
        if(mCountryNewsInternationalSelected.equals("yes"))
        {
            onlyInternational = "true";
        }
        else if(mCountryNewsInternationalSelected.equals("no"))
        {
            onlyInternational = "false";
        }

        String finalOnlyInternational = onlyInternational;
        Thread countryNewsThread = new Thread()
        {
            @Override
            public void run() {
                super.run();

                try {

                    String countryNewsResult = apiClient.countryNews(finalCountryFilterLanguagesArray, fromCountry, aboutCountry, finalOnlyInternational);

                    //get the articles
                    ArrayList<NewsArticle> countryNewsArticles = new ArrayList<>();
                    countryNewsArticles.addAll(newsJSONParser.parseNewsResults(countryNewsResult));

                    //clear recycler adapter
                    mArticleCardItems.clear();

                    //Add articles to recycler adapter: ONLY THOSE THAT ARE NOT LIKED
                    for(NewsArticle countryNews: countryNewsArticles)
                    {
                        if(!articleIsLiked(countryNews.getLink()))
                        {
                            ArticleCardItem countryNewsCardItem = new ArticleCardItem(countryNews.getTitle(), countryNews.getTopic(), countryNews.getSummary(),
                                    countryNews.getPublisher(), countryNews.getDatePublished(), countryNews.getLink());

                            mArticleCardItems.add(countryNewsCardItem);
                        }

                    }

                    //notify that the data changed
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mNewsRecyclerAdapter.notifyDataSetChanged();
                        }
                    });

                }catch (IOException e)
                {
                    Log.e(TAG, "handleCountryNewsFiltering() => failed to get country news: " + e);
                }
            }
        };

        countryNewsThread.start();

    }

}