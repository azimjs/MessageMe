package com.example.azim.messageme;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComposeActivity extends AppCompatActivity {

    TextView textViewTo, textViewRegion;
    ImageView imageContact, imageLocation;
    EditText messageText;
    Button buttonSend;
    Message msg;
    String MESSAGE_TYPE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        msg = new Message();

        textViewTo = (TextView) findViewById(R.id.message_from);
        textViewRegion = (TextView) findViewById(R.id.message_region);
        messageText = (EditText) findViewById(R.id.message_text);
        buttonSend = (Button) findViewById(R.id.button_send);
        imageContact = (ImageView) findViewById(R.id.imageViewContact);
        imageLocation = (ImageView) findViewById(R.id.imageViewLocation);

        MESSAGE_TYPE = getIntent().getExtras().getString(InboxActivity.MESSAGE_TYPE_KEY);
        if((MESSAGE_TYPE).equalsIgnoreCase("reply")){
            msg = (Message) getIntent().getExtras().getSerializable(InboxActivity.MESSAGE_KEY);

            Beacons replyBeacon = Beacons.getBeacon(msg.getBeaconId());

            textViewTo.setText("To: " + msg.getSenderName().toString());
            textViewRegion.setText("Region: " + replyBeacon.getLocation().toString());

            Log.d("demo", msg.toString());
        }
        else {

            imageContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParseQuery<ParseUser> query = ParseUser.getQuery();
                    query.findInBackground(new FindCallback<ParseUser>() {
                        public void done(final List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                // The query was successful.
                                contactAlertDialog(objects);
                            } else {
                                // Something went wrong.
                            }
                        }
                    });
                }
            });

            imageLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("demo",InboxActivity.BEACONS_LIST.get(0).getLocation() + "<<<<change");
                    regionAlertDialog(ParseApplication.BEACONS_LIST);
                }
            });
        }

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((messageText.getText().toString()).equalsIgnoreCase("") || msg.getReceiver() == null || msg.getBeaconId() == null) {
                    Toast.makeText(ComposeActivity.this, "Error: All the fields are compulsory", Toast.LENGTH_SHORT).show();
                    return;
                } else if (MESSAGE_TYPE.equalsIgnoreCase("reply")) {
                    ParseUser user = ParseUser.getCurrentUser();
                    ParseObject parsedMessage = new ParseObject("Message");
                    parsedMessage.put("sender", user.getObjectId());
                    parsedMessage.put("senderName",user.getString("first_name") + " " + user.getString("last_name"));
                    parsedMessage.put("receiver", msg.getSender());
                    parsedMessage.put("isRead", false);
                    parsedMessage.put("beaconId", msg.getBeaconId());

                    parsedMessage.put("message", messageText.getText().toString());

                    parsedMessage.saveInBackground();
                    messsageSent();
                }
                else{
                msg.setSender(ParseUser.getCurrentUser().getObjectId().toString()); //set sender
                String senderName = ParseUser.getCurrentUser().get("first_name") + " " + ParseUser.getCurrentUser().get("last_name");
                msg.setSenderName(senderName); //set sender name
                msg.setIsRead(false); //set isRead
                msg.setMessage(messageText.getText().toString());  //set Message

                ParseObject parsedMessage = new ParseObject("Message");
                parsedMessage.put("sender", msg.getSender());
                parsedMessage.put("senderName", msg.getSenderName());
                parsedMessage.put("receiver", msg.getReceiver());
                parsedMessage.put("isRead", msg.getIsRead());
                parsedMessage.put("message", msg.getMessage());
                parsedMessage.put("beaconId", msg.getBeaconId());
                parsedMessage.saveInBackground();

                    Map<String, String> params = new HashMap<String, String>();

                    params.put("receiver", msg.getReceiver());
                    params.put("first_name", ParseUser.getCurrentUser().getString("first_name"));

                    ParseCloud.callFunctionInBackground("messageSent", params, new FunctionCallback<Map<String, Object>>() {
                        public void done(Map<String, Object> mapObject, ParseException e) {
                            if (e == null) {

                                //Toast.makeText(SignUpActivity.this, mapObject.get("answer").toString(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });

                messsageSent();
            }
        }
    });

        ParseUser.getCurrentUser();

    }

    private void messsageSent() {
        Toast.makeText(ComposeActivity.this, "Message Successfully Sent", Toast.LENGTH_SHORT).show();
        Intent myintent = new Intent(ComposeActivity.this, InboxActivity.class);
        startActivity(myintent);
        finish();
    }

    private void contactAlertDialog(final List<ParseUser> objects) {
        List<String> temp = new ArrayList();
        for(ParseUser ps : objects){
            temp.add(ps.get("first_name") + " " + ps.get("last_name"));
        }
        final CharSequence[] cs = temp.toArray(new CharSequence[temp.size()]);
        Log.d("demo",cs.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(ComposeActivity.this);
        builder.setTitle("Users");
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("demo", which + "");
                textViewTo.setText("To: " + cs[which]);
                msg.setReceiver(objects.get(which).getObjectId()); // set Receiver
            }
        });
        builder.create().show();
    }

    private void regionAlertDialog(final List<Beacons> beacons) {
        List<String> temp = new ArrayList();
        for(Beacons ps : beacons){
            temp.add(ps.getLocation().toString());
        }
        final CharSequence[] cs = temp.toArray(new CharSequence[temp.size()]);
        Log.d("demo",cs.toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(ComposeActivity.this);
        builder.setTitle("Regions");
        builder.setItems(cs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("demo", which + "");
                textViewRegion.setText("Region: " + cs[which]);
                msg.setBeaconId(beacons.get(which).getObjectId()); // set beaconId
            }
        });
        builder.create().show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent myintent = new Intent(ComposeActivity.this, InboxActivity.class);
        startActivity(myintent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_compose, menu);
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
