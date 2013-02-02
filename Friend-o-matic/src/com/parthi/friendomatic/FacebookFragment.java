package com.parthi.friendomatic;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.*;
import com.facebook.widget.WebDialog.*;

/**
 * A fragment representing Facebook section of the app.
 */

public class FacebookFragment extends Fragment
{

	private Session.StatusCallback callback = new Session.StatusCallback() 
	{
	    @Override
	    public void call(Session session, SessionState state, Exception exception)
	    {
	        onSessionStateChange(session, state, exception);
	    }
	};

	private UiLifecycleHelper uiHelper;
	private Button friendButton;
	
	public FacebookFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_facebook, container, false);
		
		friendButton = (Button) view.findViewById(R.id.friend_button);
		
		LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
		authButton.setFragment(this);
		
		//request permissions
		authButton.setReadPermissions(Arrays.asList("read_requests"));
		
		return view;
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception)
	{
	    if(state.isOpened())
	    {
	    	friendButton.setVisibility(Button.VISIBLE);
	    	
	    	friendButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v)
				{
					sendFriendRequestDialog();
				}
			});
	    }
	    else if(state.isClosed())
	    {
	    	friendButton.setVisibility(Button.INVISIBLE);
	    }
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    
	    uiHelper = new UiLifecycleHelper(getActivity(), callback);
	    uiHelper.onCreate(savedInstanceState);
	}
	
	@Override
	public void onResume()
	{
	    super.onResume();
	    
	    // For scenarios where the main activity is launched and user
	    // session is not null, the session state change notification
	    // may not be triggered. Trigger it if it's open/closed.
	    Session session = Session.getActiveSession();
	    if (session != null &&
	           (session.isOpened() || session.isClosed()) ) {
	        onSessionStateChange(session, session.getState(), null);
	    }

	    uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
	    super.onActivityResult(requestCode, resultCode, data);
	    uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause()
	{
	    super.onPause();
	    uiHelper.onPause();
	}

	@Override
	public void onDestroy()
	{
	    super.onDestroy();
	    uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}
	
	private void sendFriendRequestDialog()
	{
		String USER_ID = "vidya.loganathan.14";
		
	    Bundle params = new Bundle();
	   // params.putString("id", USER_ID);

	    params.putString("message", "Learn how to make your Android apps social");
	    
		//facebook.dialog(this, "friends", parameters, this);

	    WebDialog requestsDialog = (
	        new WebDialog.RequestsDialogBuilder(getActivity(),
	            Session.getActiveSession(),
	            params))
	            .setOnCompleteListener(new OnCompleteListener()
	            {
					@Override
					public void onComplete(Bundle values, FacebookException error)
					{
	                    if(error != null)
	                    {
	                    	System.out.println(error.toString());
	                        if(error instanceof FacebookOperationCanceledException)
	                        {
	                            Toast.makeText(getActivity().getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
	                        }
	                        else
	                        {
	                            Toast.makeText(getActivity().getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
	                        }
	                    }
	                    else
	                    {
	                        final String requestId = values.getString("request");
	                        
	                        if(requestId != null)
	                        {
	                            Toast.makeText(getActivity().getApplicationContext(), "Request sent", Toast.LENGTH_SHORT).show();
	                        }
	                        else 
	                        {
	                            Toast.makeText(getActivity().getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
	                        }
	                    }   
					}

	            })
	            .build();
	    requestsDialog.show();
	}
	
}