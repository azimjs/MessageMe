package com.example.azim.messageme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    EditText firstNameEditText, lastNameEditText, userNameEditText, passwordEditText, confirmPassEditText;
    Button signUpButton;
    String firstName, lastName, userName, password, confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        firstNameEditText = (EditText) findViewById(R.id.signupactivity_edittext_firstname);
        lastNameEditText = (EditText) findViewById(R.id.signupactivity_edittext_lastname);
        userNameEditText = (EditText) findViewById(R.id.signupactivity_edittext_username);
        passwordEditText = (EditText) findViewById(R.id.signupactivity_edittext_password);
        confirmPassEditText = (EditText) findViewById(R.id.signupactivity_edittext_confirmpass);

        ((Button) findViewById(R.id.signupactivity_button_signup)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstName = firstNameEditText.getText().toString();
                lastName = lastNameEditText.getText().toString();
                userName = userNameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                confirmPass = confirmPassEditText.getText().toString();

                Log.d("Demo", "onClick signup");

                if(firstName.equals("") || lastName.equals("") || userName.equals("") || password.equals("") || confirmPass.equals("")){
                    Log.d("Demo", "null fields");
                    Toast.makeText(SignUpActivity.this,"Fields cannot be left blank",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!password.equals(confirmPass)){
                    Log.d("Demo", "pass match");
                    Toast.makeText(SignUpActivity.this,"Passwords do not match", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d("Demo", "signup");

                Toast.makeText(SignUpActivity.this, "Success", Toast.LENGTH_SHORT);

                ParseUser user = new ParseUser();
                user.setUsername(userName);
                user.setPassword(password);

                // other fields can be set just like with ParseObject
                user.put("first_name", firstName);
                user.put("last_name", lastName);

                user.signUpInBackground(new SignUpCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            // Hooray! Let them use the app now.
                            Log.d("Demo", "signup done");
/*
                            ParseInstallation parseInstallation= ParseInstallation.getCurrentInstallation();
                            parseInstallation.put("user", ParseUser.getCurrentUser().getObjectId());
                            parseInstallation.saveInBackground();


*/

                            Map<String, String> params = new HashMap<String, String>();
                            params.put("first_name", firstName);
                            ParseCloud.callFunctionInBackground("userSignIn", params, new FunctionCallback<Object>() {
                                @Override
                                public void done(Object o, ParseException e) {

                                }
                            });

                            Toast.makeText(SignUpActivity.this, "User has Successfully logged in!", Toast.LENGTH_SHORT).show();
                            Intent myIntent = new Intent(SignUpActivity.this,InboxActivity.class);
                            startActivity(myIntent);
                            finish();
                        } else {
                            // Sign up didn't succeed. Look at the ParseException
                            // to figure out what went wrong
                            Log.d("Demo", "signup fail");
                            Toast.makeText(SignUpActivity.this, e.getMessage().toUpperCase(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sign_up, menu);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent myintent = new Intent(SignUpActivity.this,LoginActivity.class);
        startActivity(myintent);
        finish();
    }

}
