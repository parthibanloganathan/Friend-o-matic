package com.parthi.friendomatic;

import java.util.List;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsAdapter extends BaseAdapter implements OnClickListener
{
    private Context context;

    private List<Data> friendsList;
    
    private Data entryToRemove = new Data();
    
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
        
        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.friend_row, null);
        }
        
        TextView friendText = (TextView) convertView.findViewById(R.id.friend);
        friendText.setText(entry.getFriendName());

        Button addButton = (Button) convertView.findViewById(R.id.accept);
        addButton.setOnClickListener(this);
        addButton.setTag(entry);
        
        Button deleteButton = (Button) convertView.findViewById(R.id.delete);
        deleteButton.setOnClickListener(this);
        deleteButton.setTag(entry);
       
        return convertView;
    }

    @Override
    public void onClick(View view)
    {
		switch(view.getId())
		{
			case R.id.accept:
			{
		    	Data entry = (Data) view.getTag();
				entryToRemove = entry;
				
				sendRequestDialog(entry.getFriendID());
				
				break;
			}
		    case R.id.delete:
		    {
		    	Data entry = (Data) view.getTag();
		    	entryToRemove = entry;
		    	
		    	removeFriend();
		        
		        break;
		    }
		}
    }
    
	private void sendRequestDialog(String friendID)
	{	
		Bundle params = new Bundle();

		params.putString("id", friendID);
		
		WebDialog friendDialog = (
				new WebDialog.Builder(context, Session.getActiveSession(), "friends", params))
        .setOnCompleteListener(new OnCompleteListener()
        {
            @Override
            public void onComplete(Bundle values, FacebookException error)
            {
                if(error != null)
                {
                    if(error instanceof FacebookOperationCanceledException)
                    {
                        Toast.makeText(context, "Friend Request Cancelled", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        //Toast.makeText(getApplication().getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    final String requestId = values.getString("request");
                    if (requestId != null)
                    {
                        Toast.makeText(context, "Friend Request Sent", Toast.LENGTH_SHORT).show();
                        
                        removeFriend();
                    }
                    else
                    {
                        Toast.makeText(context, "Friend Request Cancelled", Toast.LENGTH_SHORT).show();
                    }
                }   
            }

        })
        .build();
		friendDialog.show();
	}
	
	private void removeFriend()
	{
		friendsList.remove(entryToRemove);
		
        DataAccessObject datasource = new DataAccessObject(context);
        datasource.open();
    	
    	Functions.deleteData(datasource, entryToRemove.getFriendID());
    	
        notifyDataSetChanged();
        
        datasource.close();
	}
}
