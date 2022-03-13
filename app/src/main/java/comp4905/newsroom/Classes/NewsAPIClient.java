package comp4905.newsroom.Classes;

import android.util.Log;

import java.io.IOException;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsAPIClient
{

    private static  final String TAG = NewsAPIClient.class.getName();
    private static final String KEY_NAME = "x-rapidapi-key";
    private static final String HOST_NAME = "x-rapidapi-host";

    private static final String HOST = "news67.p.rapidapi.com";
    private static final String API_KEY = "5bfa8ade7dmshd0ca52168f37411p1f1024jsnecb598fbbc8e";
    private static final String BASE_URL = "https://news67.p.rapidapi.com/v2/";

    //ENDPOINTS
    private static final String TOPIC_SEARCH = "topic-search?";

    private OkHttpClient okHttpClient = new OkHttpClient();

    private Request request;
    private Response response;

    public NewsAPIClient(){}

    public String searchNews(String query) throws IOException
    {
        //build the request
        request = new Request.Builder()
                .url("https://news67.p.rapidapi.com/v2/feed?categoryCode=medtop%3A20000392&languages=en")
                .get()
                .addHeader(KEY_NAME,API_KEY)
                .addHeader(HOST_NAME,HOST)
                .build();

        //get the response object
        response = okHttpClient.newCall(request).execute();

        //convert response to json String
        String responseToString = response.body().string();

        Log.i(TAG,"searchNews() => " + responseToString);

        //return response
        return responseToString;

    }

    public String searchUserPreferedNews() throws IOException
    {
        //BUILD THE URL USING PREFFERED LANGUAGE AND TOPICS
        String fullURL = BASE_URL + TOPIC_SEARCH;

        //add the language to the url (currently defaulted to english)
        fullURL += "languages=en";

        //add the topic
        fullURL += "&search=";
        for(int i = 0; i < Globals.deviceUser.getFavorites().size(); i++)
        {
            if (i == (Globals.deviceUser.getFavorites().size() - 1))
            {
                //last topic
                fullURL += Globals.deviceUser.getFavorites().get(i).toLowerCase();
            }
            else
            {
                fullURL += Globals.deviceUser.getFavorites().get(i).toLowerCase() + "%20";
            }

        }

        //add a batch size of 30 for every reload
        fullURL += "&batchSize=30";

        Log.i(TAG,"searchNews() => Request: " + fullURL);

        //build the request
        request = new Request.Builder()
                .url(fullURL)
                .get()
                .addHeader(KEY_NAME,API_KEY)
                .addHeader(HOST_NAME,HOST)
                .build();

        //get the response object
        response = okHttpClient.newCall(request).execute();

        //convert response to json String
        String responseToString = response.body().string();

        Log.i(TAG,"searchNews() => Response:" + responseToString);

        //return response
        return responseToString;

    }

}
