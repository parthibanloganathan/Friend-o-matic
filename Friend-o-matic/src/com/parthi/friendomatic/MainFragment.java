package com.parthi.friendomatic;

import java.util.Arrays;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.WebDialog;
import com.facebook.widget.WebDialog.OnCompleteListener;

public class MainFragment extends Fragment
{
	private UiLifecycleHelper uiHelper;
	private UserID id;
	private Button sendRequestButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    View view = inflater.inflate(R.layout.activity_main, container, false);

	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));
	    authButton.setFragment(this);
	    
	    sendRequestButton = (Button) view.findViewById(R.id.sendRequestButton);
	    sendRequestButton.setOnClickListener(new View.OnClickListener()
	    {
	        @Override
	        public void onClick(View v)
	        {
	            sendRequestDialog(id.getUserID());        
	        }
	    });
	    
	    return view;
	}
	
	private Session.StatusCallback callback = new Session.StatusCallback()
	{
	    @Override
	    public void call(Session session, SessionState state, Exception exception)
	    {
	        onSessionStateChange(session, state, exception);
	    }
	};
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception)
	{
	    if (session != null && session.isOpened()) {
	        // Get the user's data.
	        userInfoRequest(session);
	    }
	    
	    if(state.isOpened())
	    {
	        System.out.println("Logged in");
	        sendRequestButton.setVisibility(View.VISIBLE);
	    }
	    else if(state.isClosed())
	    {
	    	System.out.println("Logged out");
	    	sendRequestButton.setVisibility(View.INVISIBLE);
	    }
	}
	
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
	    
	    if(session != null && (session.isOpened() || session.isClosed()) )
	    {
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
	
	/**
	 * Request to retrieve user's Facebook ID
	 * @param session
	 */
	private void userInfoRequest(final Session session)
	{
	    // Make an API call to get user data and define a 
	    // new callback to handle the response.
	    Request request = Request.newMeRequest(session, 
	            new Request.GraphUserCallback() {
			        @Override
			        public void onCompleted(GraphUser user, Response response)
			        {
			            // If the response is successful
			            if(session == Session.getActiveSession())
			            {
			                if(user != null)
			                {
			                	//Sets user's Facebook ID
			                    UserID.setUserID(user.getId());
			                    
			                    System.out.println(UserID.getUserID());
			                }
			            }
			            if(response.getError() != null)
			            {
			                // Handle errors, will do so later.
			            }
			        }
	    });
	    request.executeAsync();
	} 
	
	private void sendRequestDialog(String userID)
	{
	    Bundle params = new Bundle();
	    
	    params.putString("id", userID);
	    
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
	                        if(error instanceof FacebookOperationCanceledException)
	                        {
	                            Toast.makeText(getActivity().getApplicationContext(), "Request cancelled", Toast.LENGTH_SHORT).show();
	                        }
	                        else
	                        {
	                        	System.out.println(error);
	                            Toast.makeText(getActivity().getApplicationContext(), "Network Error", Toast.LENGTH_SHORT).show();
	                        }
	                    }
	                    else
	                    {
	                        final String requestId = values.getString("request");
	                        if (requestId != null)
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