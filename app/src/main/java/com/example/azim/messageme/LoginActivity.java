package com.example.azim.messageme;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    EditText usernameEditText, passwordEditText;
    Button loginButton, newuserButton;
    String username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
        
        usernameEditText = (EditText) findViewById(R.id.loginactivity_edittext_username);
        passwordEditText = (EditText) findViewById(R.id.loginactivity_edittext_password);

        ParseUser currentUser = ParseUser.getCurrentUser();

        if (currentUser != null) {
            Intent myIntent = new Intent(LoginActivity.this, InboxActivity.class);
            startActivity(myIntent);
            finish();
        }

        ((Button) findViewById(R.id.loginactivity_button_login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();

                if(username.equals("") || password.equals("") ){
                    Log.d("demo","uname:" + username);
                    Toast.makeText(LoginActivity.this, "Username or Password cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    Log.d("demo","going here" + username);
                    ParseUser.logInInBackground(username, password, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                ParseInstallation parseInstallation= ParseInstallation.getCurrentInstallation();
                                parseInstallation.put("user", user.getObjectId());
                                parseInstallation.saveInBackground();

                                Toast.makeText(LoginActivity.this,"LOGGED IN",Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(LoginActivity.this, InboxActivity.class);
                                startActivity(myIntent);
                                finish();

                            } else {
                                Toast.makeText(LoginActivity.this,"Login Failed.",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        ((Button)findViewById(R.id.loginactivity_button_newuser)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myintent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(myintent);
                finish();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
