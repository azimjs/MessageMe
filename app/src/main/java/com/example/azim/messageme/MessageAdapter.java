package com.example.azim.messageme;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Azim on 10/3/2015.
 */
public class MessageAdapter extends ArrayAdapter<Message> {
    List<Message> messageList;
    Context mContext;
    int mResource;

    public MessageAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        this.mContext = context;
        this.mResource = resource;
        this.messageList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(mResource, parent, false);
        }

        Message msg = messageList.get(position);

        ImageView circle = (ImageView) convertView.findViewById(R.id.message_imageview_circle);
        ImageView lock = (ImageView) convertView.findViewById(R.id.message_imageview_lock);
        TextView senderName = (TextView) convertView.findViewById(R.id.message_sendername);
        TextView beaconName = (TextView) convertView.findViewById(R.id.message_beaconname);
        TextView datetime = (TextView) convertView.findViewById(R.id.message_datetime);

        if(!msg.getIsRead()) {
            circle.setImageResource(R.drawable.circle_blue);
            lock.setImageResource(R.drawable.lock);
        }

        senderName.setText(msg.getSenderName());

        for(Beacons b : ParseApplication.BEACONS_LIST){
            //Log.d("demo","beacon location:" + b.getLocation());
            if(b.getObjectId() != null && b.getObjectId().contains(msg.getBeaconId()))
                beaconName.setText(b.getLocation());
        }

        datetime.setText(new SimpleDateFormat("MM/dd/yy, h:mm a").format(msg.getCreatedAt()));

        return convertView;
    }
}
