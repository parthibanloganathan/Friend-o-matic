package com.parthi.friendomatic;

public class UserID
{
	private static String userID = "";

	public UserID(String userID)
	{
		this.userID = userID;
	}
	
	public String getUserID()
	{
		return userID;
	}

	public void setUserID(String userID)
	{
		this.userID = userID;
	}
}