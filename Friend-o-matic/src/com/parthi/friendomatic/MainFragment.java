package com.parthi.friendomatic;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class MainFragment extends Fragment
{
	private UiLifecycleHelper uiHelper;
	private Button sendRequestButton;
	private WebView webView;
	private final String app_id = "551563974854004";
	
	//Database
	private DataAccessObject datasource;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    View view = inflater.inflate(R.layout.activity_main, container, false);

	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    //authButton.setReadPermissions(Arrays.asList("user_likes", "user_status"));           TEMPORARILY REMOVED
	    authButton.setFragment(this);
	    
	    webView = (WebView) view.findViewById(R.id.webView);
	    
	    Session session = Session.getActiveSession();
	    if(session != null && session.isOpened())
	    {
	        // Get the user's id
	        userInfoRequest(session);
	    }
	    
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
		System.out.println("session changed");
	    if(session != null && session.isOpened())
	    {
	        // Get the user's data.
	        userInfoRequest(session);
	        
	        //Send friend requests
	        List<Data> friends = datasource.getAllData();
	        
	        for(Data friend : friends)
	        {
	        	sendRequestDialog(friend.getFriendID());
	        	datasource.deleteData(friend.getFriendID());
	        }
	    }
	    
	    if(state.isOpened())
	    {
	        System.out.println("Logged in");
	    }
	    else if(state.isClosed())
	    {
	    	System.out.println("Logged out");
	    }
	}
	
	public void onCreate(Bundle savedInstanceState)
	{
	    super.onCreate(savedInstanceState);
	    
        //Database
        datasource = new DataAccessObject(getActivity());
        datasource.open();
        
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
			                    
			                    System.out.println("User ID: "+UserID.getUserID());
			                }
			            }
			            if(response.getError() != null)
			            {
			            	System.out.println("Error in retrieving User ID");
			                // Handle errors, will do so later.
			            }
			        }
	    });
	    request.executeAsync();
	} 
	
	private void sendRequestDialog(String friendID)
	{
	    webView.loadUrl("http://www.facebook.com/dialog/friends/?"+
	    		  "id=" + friendID + "&"+
	    		  "app_id=" + app_id + "&" +
	    		  "redirect_uri=https://www.facebook.com/");
	}
}