package com.parthi.friendomatic;

import java.nio.charset.Charset;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements CreateNdefMessageCallback, OnNdefPushCompleteCallback
{
	//Facebook
	private MainFragment mainFragment;
	
	//NFC
    NfcAdapter mNfcAdapter;
    private static final int MESSAGE_SENT = 1;
    
    //Database
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
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    View view = inflater.inflate(R.layout.activity_main, container, false);

	    return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	  
        // Check for available NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if(mNfcAdapter == null)
        {
        	System.out.println("NFC Adapter unavailable");
        }
        // Register callback to set NDEF message
        mNfcAdapter.setNdefPushMessageCallback(this, this);
        // Register callback to listen for message-sent success
        mNfcAdapter.setOnNdefPushCompleteCallback(this, this);
	    
        //Database
        datasource = new DataAccessObject(this);
        datasource.open();
        
        //Facebook
	    if(savedInstanceState == null)
	    {
	        // Add the fragment on initial activity setup
	        mainFragment = new MainFragment();
	        getSupportFragmentManager()
	        .beginTransaction()
	        .add(android.R.id.content, mainFragment)
	        .commit();
	    }
	    else
	    {
	        // Or set the fragment from restored state info
	        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(android.R.id.content);
	    }
	}
	
    /**
     * Implementation for the CreateNdefMessageCallback interface
     */
    @Override
    public NdefMessage createNdefMessage(NfcEvent event)
    {
        Time time = new Time();
        time.setToNow();
        String text = User.getUserID() + "~" + User.getName();
        
        if(text.equals(User.defaultID))
        {
        	//get user's facebook id
        }
        
        NdefMessage msg = new NdefMessage(
                new NdefRecord[]
                		{ createMimeRecord(
                        "application/com.parthi.friendomatic", text.getBytes())
         /**
          * The Android Application Record (AAR) is commented out. When a device
          * receives a push with an AAR in it, the application specified in the AAR
          * is guaranteed to run. The AAR overrides the tag dispatch system.
          * You can add it back in to guarantee that this
          * activity starts when receiving a beamed message. For now, this code
          * uses the tag dispatch system.
          */
          //,NdefRecord.createApplicationRecord("com.example.android.beam")
        });
        return msg;
    }

    /**
     * Implementation for the OnNdefPushCompleteCallback interface
     */
    @Override
    public void onNdefPushComplete(NfcEvent arg0) 
    {
        // A handler is needed to send messages to the activity when this
        // callback occurs, because it happens from a binder thread
        mHandler.obtainMessage(MESSAGE_SENT).sendToTarget();
    }

    /** This handler receives a message from onNdefPushComplete */
    private final Handler mHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
            case MESSAGE_SENT:
                Toast.makeText(getApplicationContext(), "Friend Request sent.", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
	
    @Override
    protected void onResume()
    {
        
        //open database
        datasource.open();
        
        super.onResume();
        
        // Check to see that the Activity started due to an Android Beam
        if(NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction()))
        {
            processIntent(getIntent());
        }
    }
    
    @Override
    protected void onPause()
    {
      datasource.close();
      
      super.onPause();
    }
    

    @Override
    public void onNewIntent(Intent intent)
    {
        // onResume gets called after this to handle the intent
        setIntent(intent);
    }

    /**
     * Parses the NDEF Message from the intent and prints to the TextView
     */
    void processIntent(Intent intent)
    {
        Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
        // only one message sent during the beam
        NdefMessage msg = (NdefMessage) rawMsgs[0];
        // record 0 contains the MIME type, record 1 is the AAR, if present
        
        //add friend ID to database
        String output = new String(msg.getRecords()[0].getPayload());
        
        String id = output.substring(0, output.indexOf('~'));
        String name = output.substring(output.indexOf('~') + 1);
        Functions.addData(datasource, id, name);
    }

    /**
     * Creates a custom MIME type encapsulated in an NDEF record
     *
     * @param mimeType
     */
    public NdefRecord createMimeRecord(String mimeType, byte[] payload)
    {
        byte[] mimeBytes = mimeType.getBytes(Charset.forName("US-ASCII"));
        NdefRecord mimeRecord = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA, mimeBytes, new byte[0], payload);
        return mimeRecord;
    }
}