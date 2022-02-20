package comp4905.newsroom.Classes;

import android.util.Log;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NewsAPIClient
{

    private static  final String TAG = NewsAPIClient.class.getName();
    private static final String KEY_NAME = "x-rapidapi-key";
    private static final String HOST_NAME = "x-rapidapi-host";
    private static final String HOST = "free-news.p.rapidapi.com";
    private static final String API_KEY = "5bfa8ade7dmshd0ca52168f37411p1f1024jsnecb598fbbc8e";
    private static final String BASE_URL = "https://free-news.p.rapidapi.com/v1/search?q=";

    private OkHttpClient okHttpClient = new OkHttpClient();

    private Request request;
    private Response response;

    public NewsAPIClient(){}

    public String searchNews(String query) throws IOException
    {
        //split query into words
        String[] splitQuery = query.split(" ");

        Log.i(TAG,"Query: " + query);

        //make full url
        String fullURL = (new StringBuilder(BASE_URL)).toString();
        for (int i = 0; i < splitQuery.length; i++)
        {
            Log.i(TAG,"Query[i]: " + splitQuery[i]);
            if(i == splitQuery.length - 1)
            {
                fullURL += splitQuery[i];

            }
            else
            {
                fullURL += splitQuery[i] + "%20";
            }

            Log.i(TAG,"full url in loop: " + fullURL);

        }

        //log the url
        Log.i(TAG,"searchNews(): url => " + fullURL);

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

        Log.i(TAG,"searchNews(): " + responseToString);

        //return response
        return responseToString;

    }

}
