package com.parthi.friendomatic;

public class Data
{
	  private String friendID;
	
	  public String getFriendID()
	  {
		return friendID;
	  }
	  
	  public void setFriendID(String friendID)
	  {
			this.friendID = friendID;
	  }

	  // Will be used by the ArrayAdapter in the ListView
	  @Override
	  public String toString() {
	    return friendID;
	  }
} 