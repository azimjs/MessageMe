package com.example.azim.messageme;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class InboxActivity extends AppCompatActivity {

    static final String MESSAGE_KEY = "message_key";
    static final String MESSAGE_TYPE_KEY = "type";
    static final String BEACON_TOP_NAME = "beaconName";
    static final String BEACON_TOP_DURATION = "time";

    private static Map closestBeacon;
    static {
        closestBeacon = new HashMap();
        closestBeacon.put(BEACON_TOP_NAME,"");
        closestBeacon.put(BEACON_TOP_DURATION,0);
    }

    String username, userObjectId;
    ProgressDialog loading;
    ListView messageListView;

    private BeaconManager beaconManager;
    private Region region;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        beaconManager = new BeaconManager(this);
        region = new Region("ESTIMOTE",UUID.fromString("B9407F30-F5F8-466E-AFF9-25556B57FE6D"), null, null);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                //Log.d("bcon",region.toString());
                if (!list.isEmpty()) {
                    Beacon nearestBeacon = list.get(0);
                    String nameOfNearestBeacon = nearestBeaconName(nearestBeacon);

                    if(!closestBeacon.get(BEACON_TOP_NAME).toString().equalsIgnoreCase(nameOfNearestBeacon)){
                        closestBeacon.put(BEACON_TOP_NAME,nameOfNearestBeacon);
                        closestBeacon.put(BEACON_TOP_DURATION,0);
                    }
                    int t= Integer.parseInt(closestBeacon.get(BEACON_TOP_DURATION).toString());
                    t++;
                    closestBeacon.put(BEACON_TOP_DURATION,t);

                    // TODO: update the UI here
                    TextView topBeaconListView = (TextView) findViewById(R.id.topBeacon);

                    topBeaconListView.setText("Nearest Beacon is " + closestBeacon.get(BEACON_TOP_NAME).toString().toUpperCase() + " since <<" + closestBeacon.get(BEACON_TOP_DURATION).toString() + ">> seconds");

                    //Log.d("bcon", "stored top beacon: " + closestBeacon.toString());
                    //Log.d("bcon", "listener top beacon: " + nameOfNearestBeacon + "");
                }
            }
        });

        username = ((ParseUser)ParseUser.getCurrentUser()).getUsername().toString();
        userObjectId = ((ParseUser)ParseUser.getCurrentUser()).getObjectId().toString();

        loading = new ProgressDialog(this);
        loading.setMessage("Loading Messages ...");
        loading.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loading.setCancelable(false);
        loading.show();

        messageListView = (ListView) findViewById(R.id.inboxactivity_listview);

        Log.d("demo", "" + username + "##");
        loadMessages();

    }

    private String nearestBeaconName(Beacon beacon) {
        String beaconKey = String.format("%d:%d", beacon.getMajor(), beacon.getMinor());
        if (ParseApplication.BEACONS_LIST_MAP.containsKey(beaconKey)) {
            return ParseApplication.BEACONS_LIST_MAP.get(beaconKey);
        }
        return "";
    }


    public void  loadMessages(){

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
        Log.d("demo", userObjectId);
        query.whereEqualTo("receiver", userObjectId);

        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> messages, ParseException e) {
                final List<Message> messageList = new ArrayList<>();
                loading.dismiss();
                if (e == null) {
                    Log.d("demo", "Retrieved " + messages.size() + " messages");
//                    Date createdAt = new Date();
//                    Date updatedAt = new Date();
                    for(ParseObject message : messages){
                        try {
                            Date createdAt =  new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH).parse(message.getCreatedAt().toString());
                            Date updatedAt =  new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH).parse(message.getUpdatedAt().toString());
                            Message msg = new Message(message.getObjectId(),message.getString("sender"),message.getString("receiver"),message.getString("senderName"),message.getString("message"), message.getString("beaconId"), message.getBoolean("isRead"), createdAt,updatedAt);
                            Log.d("demo",msg.toString());
                            //Log.d("demo",ParseApplication.BEACONS_LIST.get(0).toString());
                            messageList.add(msg);
                        } catch (java.text.ParseException e1) {
                            e1.printStackTrace();
                        }
                    }

                    MessageAdapter adapter = new MessageAdapter(InboxActivity.this,R.layout.message_list_view,messageList);
                    messageListView.setAdapter(adapter);
                    adapter.setNotifyOnChange(true);

                    messageListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            final Message readMsg = messageList.get(position);
                            String msgBeacon = Beacons.getBeacon(readMsg.getBeaconId()).getLocation();
                            String topBeacon = closestBeacon.get(BEACON_TOP_NAME).toString();
                            int topBeacon_time = Integer.parseInt(closestBeacon.get(BEACON_TOP_DURATION).toString());
                            Log.d("lock", "msg " + msgBeacon);
                            Log.d("lock", "top " + topBeacon);
                            if(readMsg.getIsRead()){
                                startReadMsgActivity(readMsg);
                            }
                            else{
                                if(msgBeacon.trim().equalsIgnoreCase(topBeacon.trim())){
                                    if(topBeacon_time >10) {
                                        Toast.makeText(InboxActivity.this, "Beacon Match found. Message Unlocked.", Toast.LENGTH_LONG).show();
                                        ParseQuery<ParseObject> query = ParseQuery.getQuery("Message");
                                        query.getInBackground(readMsg.getObjectId(), new GetCallback<ParseObject>() {
                                            public void done(ParseObject object, ParseException e) {
                                                if (e == null) {
                                                    object.put("isRead", true);
                                                    object.saveInBackground(new SaveCallback() {
                                                        @Override
                                                        public void done(ParseException e) {

                                                            Map<String, String> params = new HashMap<String, String>();
                                                            params.put("sender", readMsg.getSender());
                                                            params.put("first_name", readMsg.getSenderName());

                                                            ParseCloud.callFunctionInBackground("unlocked", params, new FunctionCallback<Object>() {
                                                                @Override
                                                                public void done(Object o, ParseException e) {

                                                                }
                                                            });
                                                        }
                                                    });



                                                }
                                            }
                                        });
                                        startReadMsgActivity(readMsg);
                                    }
                                    else
                                        Toast.makeText(InboxActivity.this, "Wait for "+ (10-topBeacon_time) + " more seconds near this beacon" , Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(InboxActivity.this, "ACCESS DENIED!!", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }
                    });



                } else {
                    Log.d("demo", "Error+: " + e.getMessage());
                }
            }
        });
    }

    private void startReadMsgActivity(Message readMsg) {
        Intent intent = new Intent(InboxActivity.this, ReadMsgActivity.class);
        intent.putExtra(MESSAGE_KEY, readMsg);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent myintent = new Intent(InboxActivity.this,LoginActivity.class);
        startActivity(myintent);
        ParseUser.getCurrentUser().logOut();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_inbox, menu);
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

        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_new_email:
                Intent myIntent = new Intent(InboxActivity.this, ComposeActivity.class);
                myIntent.putExtra(MESSAGE_TYPE_KEY, "compose");
                startActivity(myIntent);
                finish();
                return true;
            case R.id.action_refresh:
                refreshInbox();
                return true;
            case R.id.show_closest_beacon:
                Log.d("demo",((TextView) findViewById(R.id.topBeacon)).getVisibility() + "");
                if(((TextView) findViewById(R.id.topBeacon)).getVisibility() == 0)
                    ((TextView) findViewById(R.id.topBeacon)).setVisibility(View.INVISIBLE);
                else
                    ((TextView) findViewById(R.id.topBeacon)).setVisibility(View.VISIBLE);

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void refreshInbox() {
        loading.show();
        loadMessages();
    }

    @Override
    protected void onResume() {
        super.onResume();
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(region);
            }
        });
    }

    @Override
    protected void onPause() {
        beaconManager.stopRanging(region);
        super.onPause();
    }


}
