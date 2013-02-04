package com.parthi.friendomatic;

import android.app.ActionBar;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentFilter.MalformedMimeTypeException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class MainActivity extends FragmentActivity
{
	private UserID userID;
	private MainFragment mainFragment;
	
	NfcAdapter mNfcAdapter;
    PendingIntent mNfcPendingIntent;
    IntentFilter[] mNdefExchangeFilters;
    
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    View view = inflater.inflate(R.layout.activity_main, container, false);

	    return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);

	    //NFC
	    mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
	    mNfcPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	    // Intent filters for exchanging over p2p.
	    IntentFilter ndefDetected = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
	    try
	    {
	        ndefDetected.addDataType("text/plain");
	    }
	    catch(MalformedMimeTypeException e) {}
	    mNdefExchangeFilters = new IntentFilter[] { ndefDetected };
	    
	    mNfcAdapter.setNdefPushMessage(getUIDAsNdef(userID.getUserID()), this);
	    //
	    
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
	
    @Override
    protected void onResume()
    {
        super.onResume();
        
        enableNdefExchangeMode();
    }
    
    private void enableNdefExchangeMode()
    {
    	mNfcAdapter.setNdefPushMessage(getUIDAsNdef(userID.getUserID()), this);
        mNfcAdapter.enableForegroundDispatch(this, mNfcPendingIntent, mNdefExchangeFilters, null);
    }
    
	public static NdefMessage getUIDAsNdef(String id)
	{
	    byte[] payload = id.getBytes();
	    NdefRecord record = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, "text/plain".getBytes(), new byte[] {}, payload);
		NdefMessage message = new NdefMessage(new NdefRecord[] {record});
		
		return message;
	}
}