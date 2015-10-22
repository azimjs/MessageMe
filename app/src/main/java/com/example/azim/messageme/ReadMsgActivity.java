package com.example.azim.messageme;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

public class ReadMsgActivity extends AppCompatActivity {

    TextView textViewFrom, textViewRegion, messageText;
    Message readMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_msg);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        readMsg = (Message) getIntent().getExtras().getSerializable(InboxActivity.MESSAGE_KEY);

        textViewFrom = (TextView) findViewById(R.id.message_from);
        textViewRegion = (TextView) findViewById(R.id.message_region);
        messageText = (TextView) findViewById(R.id.message_text);

        Beacons beacon = Beacons.getBeacon(readMsg.getBeaconId());

        textViewFrom.setText("From: " + readMsg.getSenderName().toString());
        textViewRegion.setText("Region: " + beacon.getLocation());
        messageText.setText(readMsg.getMessage());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent myintent = new Intent(ReadMsgActivity.this,InboxActivity.class);
        startActivity(myintent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_read_msg, menu);
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

        //handle presses on the action bar item
        switch (item.getItemId()){
            case R.id.action_reply:
                Intent myIntent = new Intent(ReadMsgActivity.this,ComposeActivity.class);
                myIntent.putExtra(InboxActivity.MESSAGE_TYPE_KEY, "reply");
                myIntent.putExtra(InboxActivity.MESSAGE_KEY,readMsg);
                startActivity(myIntent);
                finish();
                return true;
            case R.id.action_delete:
                Log.d("demo", "delete message");
                messageDelete(readMsg.getObjectId().toString());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void messageDelete(final String objectId) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        query.whereEqualTo("objectId", readMsg.getObjectId());
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (object == null) {
                    Log.d("demo", "The getFirst request failed.");
                    Log.d("demo","" + object.toString());
                } else {
                    Log.d("demo", "Retrieved the object.");
                    object.deleteInBackground(new DeleteCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                //myObjectWasDeletedSuccessfully();
                                Toast.makeText(ReadMsgActivity.this,"Message Deleted Successfully",Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(ReadMsgActivity.this,InboxActivity.class);
                                startActivity(myIntent);
                                finish();
                            } else {
                                //myObjectDeleteDidNotSucceed();
                            }
                        }
                    });
                }
            }
        });
    }
}
