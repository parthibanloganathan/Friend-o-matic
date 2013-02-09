package com.parthi.friendomatic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FriendsListActivity extends Activity
{
	private DataAccessObject datasource;
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_friends, menu);
		inflater.inflate(R.menu.menu_home, menu);
		inflater.inflate(R.menu.menu_nfc, menu);
	        
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		    case R.id.friends:
		    {
		    	startActivity(new Intent(this, FriendsListActivity.class));
		        return true;
		    }
		    case R.id.home:
		    {
		    	Intent intent = new Intent(this, MainActivity.class);
		    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	startActivity(intent);
		    	return true;
		    }
		    case android.R.id.home:
		    {
		    	Intent intent = new Intent(this, MainActivity.class);
		    	intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		    	startActivity(intent);
		    	return true;
		    }
            case R.id.nfc:
            {
                Intent intent = new Intent(Settings.ACTION_NFCSHARING_SETTINGS);
                startActivity(intent);
                return true;
		    }
		    default:
		    {
		        return super.onOptionsItemSelected(item);
		    }
		}
	}		
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friendslist);
		
        //Database
        datasource = new DataAccessObject(getApplication());
        datasource.open();
        
        ListView list = (ListView) findViewById(R.id.friendslist);
        //list.setClickable(true);

        final List<Data> friends = datasource.getAllData();
        
        FriendsAdapter adapter = new FriendsAdapter(this, friends);
        
        list.setOnItemClickListener(new OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long index)
            {
                //send friend request
                sendRequestDialog(friends.get(position).getFriendID());
            }
        });

        list.setAdapter(adapter);
    }
    
	public void sendRequestDialog(String friendID)
	{	
		Bundle params = new Bundle();

		params.putString("id", friendID);

		WebDialog friendDialog = (
				new WebDialog.Builder(this, Session.getActiveSession(), "friends", params))
        .setOnCompleteListener(new OnCompleteListener()
        {
            @Override
            public void onComplete(Bundle values, FacebookException error)
            {
                if(error != null)
                {
                    if(error instanceof FacebookOperationCanceledException)
                    {
                        Toast.makeText(getApplication().getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplication().getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    final String requestId = values.getString("request");
                    if (requestId != null)
                    {
                        Toast.makeText(getApplication().getApplicationContext(), "Request sent", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplication().getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
                    }
                }   
            }

        })
        .build();
		friendDialog.show();
	}
}