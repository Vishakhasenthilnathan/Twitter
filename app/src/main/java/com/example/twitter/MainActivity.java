package com.example.twitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity {

    TextView LoginOrSignupButton;
    EditText userNameText;
    EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginOrSignupButton = findViewById(R.id.loginOrSignUp);
        if(ParseUser.getCurrentUser()!=null){
            redirectTofollowersList();
        }

        ParseAnalytics.trackAppOpenedInBackground(getIntent());
    }

    public void onClick(View view){
        if(userNamePasswordPresent(view)){
            Log.i("onclick" ,"username present");

            ParseUser.logInInBackground(userNameText.getText().toString(), passwordText.getText().toString(), new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if(user!=null){
                        Log.i("onclick" ,"user present");
                        Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                        redirectTofollowersList();
                    }else {
                        Log.i("onclick" ,"username not present");
                        ParseUser parseUser = new ParseUser();
                        parseUser.setUsername(userNameText.getText().toString());
                        parseUser.setPassword(passwordText.getText().toString());
                        parseUser.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(MainActivity.this, "Signed up successfully", Toast.LENGTH_SHORT).show();
                                    redirectTofollowersList();
                                } else {
                                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
//                        Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    public void redirectTofollowersList(){
        if(ParseUser.getCurrentUser()!=null){
            Intent intent = new Intent(getApplicationContext(),FollowersActivity.class);
            startActivity(intent);
        }
    }


    public Boolean userNamePasswordPresent(View view){
        userNameText = findViewById(R.id.username);
        passwordText = findViewById(R.id.password);
        String username = userNameText.getText().toString();
        String password = passwordText.getText().toString();

        if(username.isEmpty()  || password.isEmpty() ){
            Toast.makeText(this, "Please enter user name and password", Toast.LENGTH_SHORT).show();
            return false;
        }
        else return true;
    }



}