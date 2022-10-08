package com.example.twitter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class FollowersActivity extends AppCompatActivity {

    ArrayAdapter arrayAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.tweet_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.tweet) {
            tweetMessage();
        } else if (item.getItemId() == R.id.logout) {
            ParseUser.logOut();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.viewFeed) {
            Intent intent = new Intent(this, ViewTweetsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public void tweetMessage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Tweet");

        final EditText tweetText = new EditText(this);
        builder.setView(tweetText);
        builder.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ParseObject parseObject = new ParseObject("Tweets");
                parseObject.put("tweetMessage",tweetText.getText().toString());
                parseObject.put("username",ParseUser.getCurrentUser().getUsername());

                parseObject.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null){
                            Log.i("Tweet","Tweet success");
                            Toast.makeText(FollowersActivity.this, "Tweet shared", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(FollowersActivity.this, "Tweet failed", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i("Tweet", "Tweet cancelled");
                dialogInterface.cancel();
            }
        });

        builder.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);
        ListView listView = findViewById(R.id.followersListView);
        ArrayList<String> users = new ArrayList<>();

        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked, users);
        listView.setAdapter(arrayAdapter);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CheckedTextView checkedTextView = (CheckedTextView) view;
                if (checkedTextView.isChecked()) {
                    ParseUser.getCurrentUser().add("isFollowing", users.get(i));
                    Log.i("item ", "item clicked + " + users.get(i));
                } else {
                    Log.i("item ", "not clicked");
                    //get the list and remove the user
                    ParseUser.getCurrentUser().getList("isFollowing").remove(users.get(i));
                    List tempUsers = ParseUser.getCurrentUser().getList("isFollowing");
                    ParseUser.getCurrentUser().remove("isFollowing");
                    ParseUser.getCurrentUser().put("isFollowing", tempUsers);
                }
                ParseUser.getCurrentUser().saveInBackground();
            }
        });

        ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
        parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        parseQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e == null && objects.size() > 0) {
                    for (ParseUser user : objects) {
                        users.add(user.getUsername());
                        Log.i("users", user.getUsername());
                    }

                    for (String user : users) {
                        if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getList("isFollowing") != null && ParseUser.getCurrentUser().getList("isFollowing").contains(user)) {
                            listView.setItemChecked(users.indexOf(user), true);
                        }
                    }
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        });

    }
}