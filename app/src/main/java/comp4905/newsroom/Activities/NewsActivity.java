package comp4905.newsroom.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import comp4905.newsroom.Classes.ArticleCardItem;
import comp4905.newsroom.Classes.Globals;
import comp4905.newsroom.Classes.NewsAPIClient;
import comp4905.newsroom.Classes.NewsArticle;
import comp4905.newsroom.Classes.NewsSearchRecycler.NewsSearchRecyclerAdapter;
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

    //news feed filter layouts, UI and filter variables
    private View mNewsFeedView;
    private TextView mNewsFeedLanguages;
    private RadioGroup mNewsFeedSort;
    private RadioGroup mNewsFeedSentiment;
    private String mSelectedSorting = "";
    private String mSelectedSentiment = "";

    //filter submit and cancel buttons
    private Button mSubmitNewsFeedFilter;
    private Button mCancelNewsFeedFilter;


    private ArrayList<ArticleCardItem> mArticleCardItems = new ArrayList<>();
    private ArrayList<NewsArticle> mArticles = new ArrayList<>();

    private boolean[] newsFeedSelectedLanguages = new boolean[Globals.languages.length];
    private ArrayList<Integer> newsFeedLanguages = new ArrayList<>();


    NewsAPIClient apiClient = new NewsAPIClient();
    ParseJSONResults newsJSONParser = new ParseJSONResults();
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

        //get latest news by with users categories
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
                        ArticleCardItem cardItem = new ArticleCardItem(article.getTitle(), article.getTopic(), article.getSummary(),
                                                                    article.getPublisher(), article.getDatePublished(), article.getLink());

                        mArticleCardItems.add(cardItem);

                        //build recyclerview
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                buildNewsRecyclerView();
                            }
                        });

                    }
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
        mUserMenu = (ImageView) findViewById(R.id.user_menu);
        mNewsSearchBar = (SearchView) findViewById(R.id.news_search_bar);
        mSearchNewsButton = (Button) findViewById(R.id.news_search_button);
        mFilterButton = (Button) findViewById(R.id.filter_news_button);

        /*INFLATE FILTER VIEWS AND VARIABLES*/
        //news filter view and variables
        mNewsFeedView = (LinearLayout) findViewById(R.id.news_feed_filter_view);
        mNewsFeedLanguages = (TextView) findViewById(R.id.news_feed_languages);
        mNewsFeedSort = (RadioGroup) findViewById(R.id.news_feed_sort_group);
        mNewsFeedSentiment = (RadioGroup) findViewById(R.id.news_feed_sentiment_group);
        mSubmitNewsFeedFilter = (Button) findViewById(R.id.submit_news_feed_filter);
        mCancelNewsFeedFilter = (Button) findViewById(R.id.cancel_news_feed_filter);
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
                else
                {
                    //TODO:Logout
                }
                return true;
            }
        });

        mainMenu.show();
    }

    //when an item is liked, it is removed from the list
    public void removeItem(int position) {
        mArticleCardItems.remove(position);
        mNewsRecyclerAdapter.notifyItemRemoved(position);
    }

    //function to build the recycler view
    public void buildNewsRecyclerView()
    {
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
                //TODO: create a new chat from the article
            }

            @Override
            public void onExternalLinkClick(int position) {
                String link = mArticles.get(position).getLink();
                Uri uri = Uri.parse(link);
                Log.i(TAG,"buildNewsRecyclerView() => following link: " + link);
                Intent visitArticlePage = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(visitArticlePage);
            }

            @Override
            public void onLikeClick(int position) {
                //TODO: save the article to the users firebase

            }
        });
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
                    mNewsFeedView.setVisibility(View.VISIBLE);

                }
                else if(menuItem.getItemId() == R.id.world_trending)
                {
                    mFilterButton.setText("Filter: World Trending");

                }
                else if (menuItem.getItemId() == R.id.topic_search)
                {
                    mFilterButton.setText("Filter: Topic Search");
                }
                else //its country news
                {
                    mFilterButton.setText("Filter: Country News");

                }

                return true;
            }
        });

        filterMenu.show();
    }

    //setup on click listeners for filter elements
    void filtersOnClickListeners()
    {
        //news feed filter listeners
        mNewsFeedLanguages.setOnClickListener((View view) -> {newsFeedFilterLanguageClick();});
        mNewsFeedSort.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup sortGroup, int checkedSort)
            {
                //get the selected radiobutton
                RadioButton selectedSorting = (RadioButton) sortGroup.findViewById(checkedSort);

                //get the text of the selected id
                mSelectedSorting = selectedSorting.getText().toString().toLowerCase();
            }
        });
        mNewsFeedSentiment.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup sentimentGroup, int checkedSentiment)
            {
                //get the selected radiobutton
                RadioButton selectedSentiment = (RadioButton) sentimentGroup.findViewById(checkedSentiment);

                //get the text
                mSelectedSentiment = selectedSentiment.getText().toString().toLowerCase();

            }
        });
        mSubmitNewsFeedFilter.setOnClickListener((View view) -> {submitNewsFeedFilter();});
        mCancelNewsFeedFilter.setOnClickListener((View view) -> {mNewsFeedView.setVisibility(View.GONE);});
    }

    //functions for news feed filtering
    private void newsFeedFilterLanguageClick()
    {
        AlertDialog.Builder newsFeedLanguageBuilder = new AlertDialog.Builder(NewsActivity.this);

        newsFeedLanguageBuilder.setTitle("Choose languages");
        newsFeedLanguageBuilder.setCancelable(false);

        newsFeedLanguageBuilder.setMultiChoiceItems(Globals.languages, newsFeedSelectedLanguages, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i, boolean checkboxSelected)
            {
                if (checkboxSelected)
                {
                    //when checkbox selected, add position in lang list
                    newsFeedLanguages.add(i);
                    //sort arraylist
                    Collections.sort(newsFeedLanguages);
                }
                else
                {
                    //when checkbox is unselected, remove position from list
                    newsFeedLanguages.remove(i);
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

                for(int j = 0; j < newsFeedLanguages.size(); j++)
                {
                    //concatenate choices
                    stringBuilder.append(Globals.languages[newsFeedLanguages.get(j)]);

                    //add comma
                    if(j != newsFeedLanguages.size() - 1)
                    {
                        stringBuilder.append(", ");
                    }
                }

                //set the textview
                mNewsFeedLanguages.setText(stringBuilder.toString());

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
                for (int j = 0; j < newsFeedSelectedLanguages.length; j++) {
                    // remove all selection
                    newsFeedSelectedLanguages[j] = false;
                    // clear language list
                    newsFeedLanguages.clear();
                    // clear text view value
                    mNewsFeedLanguages.setText("");
                }
            }
        });

        //show the dialog
        newsFeedLanguageBuilder.show();

    }

    //function to submit the news feed filter and make search
    private void submitNewsFeedFilter()
    {
        //hide filter view
        mNewsFeedView.setVisibility(View.GONE);

        //make an array of languages
        String languages = mNewsFeedLanguages.getText().toString().trim();
        String [] languagesArray = new String[0];

        if(!languages.isEmpty() || languages != null)
        {
            languagesArray = languages.split(", ");
        }
        else
        {
            Log.i(TAG, "submitNewsFeedFilter() => no language selected");
        }

        //log to make sure of selections
        Log.i(TAG, "submitNewsFeedFilter() => " +
                "{Languages: " + Arrays.toString(languagesArray)+ "}, " +
                "{ Sorting: " + mSelectedSorting + "}, " +
                "{Sentiment: " + mSelectedSentiment + "}");

        //make and start the thread to search to the news feed
        String[] finalLanguagesArray = languagesArray;
        Thread newsFeedSearchThread = new Thread()
        {
            @Override
            public void run() {
                super.run();

                try
                {
                    String newsFeedResults = apiClient.searchNewsFeed(finalLanguagesArray, mSelectedSorting, mSelectedSentiment);
                    ArrayList<NewsArticle> newsFeedArticles = new ArrayList<>();
                    newsFeedArticles.addAll(newsJSONParser.parseNewsResults(newsFeedResults));

                    //clear recycler adapter
                    mArticleCardItems.clear();

                    //Add articles to recycler adapter
                    for(NewsArticle article: newsFeedArticles)
                    {
                        ArticleCardItem cardItem = new ArticleCardItem(article.getTitle(), article.getTopic(), article.getSummary(),
                                article.getPublisher(), article.getDatePublished(), article.getLink());

                        mArticleCardItems.add(cardItem);



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
                    Log.e(TAG, "submitNewsFeed => failed to fetch news feed: " + exception);
                }
            }
        };

        //start the thread
        newsFeedSearchThread.start();

    }




}