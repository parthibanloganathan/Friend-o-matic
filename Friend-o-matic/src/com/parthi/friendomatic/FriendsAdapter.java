package com.parthi.friendomatic;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class FriendsAdapter extends BaseAdapter implements OnClickListener
{
    private Context context;

    private List<Data> friendsList;

    public FriendsAdapter(Context context, List<Data> friendsList)
    {
        this.context = context;
        this.friendsList = friendsList;
    }

    public int getCount()
    {
        return friendsList.size();
    }

    public Object getItem(int position)
    {
        return friendsList.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup viewGroup)
    {
        Data entry = friendsList.get(position);
        
        //if(convertView == null)
        //{
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_row, null);
        //}
        
        TextView friendText = (TextView) convertView.findViewById(R.id.friend);
        friendText.setText(entry.getFriendName());

        // Set the onClick Listener on this button
        Button deleteButton = (Button) convertView.findViewById(R.id.delete);
        deleteButton.setFocusableInTouchMode(false);
        deleteButton.setFocusable(false);
        deleteButton.setOnClickListener(this);
        // Set the entry, so that you can capture which item was clicked and
        // then remove it
        // As an alternative, you can use the id/position of the item to capture
        // the item
        // that was clicked.
        deleteButton.setTag(entry);

        // btnRemove.setId(position);
       

        return convertView;
    }

    @Override
    public void onClick(View view)
    {
    	Data entry = (Data) view.getTag();
    	friendsList.remove(entry);
    	
        //Database
        DataAccessObject datasource = new DataAccessObject(context);
        datasource.open();
    	
    	Functions.deleteData(datasource, entry.getFriendID());
    	
        notifyDataSetChanged();
    }

}
