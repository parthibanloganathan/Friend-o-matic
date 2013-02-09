/*package com.parthi.friendomatic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FriendsListActivity extends Activity
{

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.home, menu);
		inflater.inflate(R.menu.settingsmenu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch(item.getItemId())
		{
		    case R.id.settings:
		    {
		    	startActivity(new Intent(this, SettingsActivity.class));
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
		    default:
		    {
		        return super.onOptionsItemSelected(item);
		    }
		}
	}		
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friendslist);
		
		ActionBar actionbar = getSupportActionBar();
		actionbar.setDisplayUseLogoEnabled(true);
		actionbar.setDisplayShowTitleEnabled(false);
        
		temperature = (EditText) findViewById(R.id.temperature);

		TextView units1 = (TextView) findViewById(R.id.units1);
		TextView units2 = (TextView) findViewById(R.id.units2);
		
	}
}*/