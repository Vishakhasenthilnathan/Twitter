package com.example.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewTweetsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewtweets);

        ListView tweetList = findViewById(R.id.tweetsListView);
        final List<Map<String,String>> tweetData = new ArrayList<>();

        ParseQuery<ParseObject> tweetQuery = ParseQuery.getQuery("Tweets");
        tweetQuery.whereContainedIn("username",ParseUser.getCurrentUser().getList("isFollowing"));
        tweetQuery.orderByDescending("createdAt");
        tweetQuery.setLimit(20);
        tweetQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                Log.i("tweet","in done");
                if(e==null){
                    for (ParseObject tweets: objects) {
                        Map<String,String> tweetinfo= new HashMap<>();
                        tweetinfo.put("content",tweets.getString("tweetMessage"));
                        tweetinfo.put("username",tweets.getString("username"));
                        tweetData.add(tweetinfo);
                    }

                    SimpleAdapter simpleAdapter = new SimpleAdapter(ViewTweetsActivity.this,tweetData, android.R.layout.simple_list_item_2,new String[]{"content","username"},new int[]{android.R.id.text1,android.R.id.text2});
                    tweetList.setAdapter(simpleAdapter);
                }
            }
        });


    }
}