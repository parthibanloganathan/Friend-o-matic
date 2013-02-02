package com.parthi.friendomatic.Functions;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import com.facebook.*;
import com.facebook.model.*;

public class FacebookFunctions
{
	public void loginFacebook(Activity activity)
	{
	    // start Facebook Login
	    Session.openActiveSession(activity, true, new Session.StatusCallback() {
	
	    // callback when session changes state
	    @Override
	    public void call(Session session, SessionState state, Exception exception)
	    {
	        if(session.isOpened())
	        {
	          // make request to the /me API
	          Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {
	
	          // callback after Graph API response with user object
	          @Override
	          public void onCompleted(GraphUser user, Response response)
	          {
	              if (user != null)
	              {
	              }
	           }
	         });
	       }
	    }
	   });
	}
	
	public void onActivityResult()
	{
		
	}
}