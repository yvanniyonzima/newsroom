package comp4905.newsroom.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.io.IOException;

import comp4905.newsroom.Classes.NewsAPIClient;
import comp4905.newsroom.R;

public class NewsActivity extends AppCompatActivity {

    NewsAPIClient apiClient = new NewsAPIClient();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        Thread newsThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                try {
                    apiClient.searchNews("elon musk");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        newsThread.start();


    }
}