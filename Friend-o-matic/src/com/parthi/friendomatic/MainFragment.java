package com.parthi.friendomatic;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

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
	private TextView userstatus;
	
	//private WebView webview;
	
	//Database
	private DataAccessObject datasource;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
	    View view = inflater.inflate(R.layout.activity_main, container, false);

	    /*
	    webview = (WebView) view.findViewById(R.id.webview);
		webview.getSettings().setJavaScriptEnabled(true);
	    */
	    
	    
	    LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
	    authButton.setFragment(this);
	  
	    userstatus = (TextView) view.findViewById(R.id.loginmessage);
	    
	    Session session = Session.getActiveSession();
	    if(session != null && session.isOpened())
	    {
	        // Get the user's data.
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
	    if(session != null && session.isOpened())
	    {
	        // Get the user's data.
	        userInfoRequest(session);
	        
	        /*webview.loadUrl("http://www.facebook.com/dialog/friends/?"+
	        		  "id=DerekHe&"+
	        		  "app_id=551563974854004&"+
	        		  "redirect_uri=https://www.facebook.com/connect/login_success.html");*/
	    }
	    
	    if(state.isOpened())
	    {
	    	SharedPreferences userInfo = getActivity().getSharedPreferences("FriendomaticUser", Context.MODE_PRIVATE);
	    	String id = userInfo.getString("id", User.defaultID);
	    	String name = userInfo.getString("name", User.defaultName);
	    	
	    	User.setUserID(id);
	    	User.setName(name);
	    }
	    else if(state.isClosed())
	    {
	    	userstatus.setText("Log in using Facebook");
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
	public void userInfoRequest(final Session session)
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
			                	//Sets user's Facebook ID and name
			                    User.setUserID(user.getId());
			                    User.setName(user.getName());
			                    
			        		    //Save to Preferences
			        		    SharedPreferences userInfo = getActivity().getSharedPreferences("FriendomaticUser", Context.MODE_PRIVATE);
			        		    SharedPreferences.Editor editor = userInfo.edit();

			        		    // Edit the saved preferences
			        		    editor.putString("id", User.getUserID());
			        		    editor.putString("name", User.getName());
			        		    
			        		    editor.commit();
			        		    
			        		    userstatus.setText("Logged in as " + User.getName());
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
}