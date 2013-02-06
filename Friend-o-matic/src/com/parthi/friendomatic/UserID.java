package com.parthi.friendomatic;

public class UserID
{
	static final String defaultID = "nu11_Fr1end0mat!c_91284143";
	private static String userID = defaultID;
	
	public static String getUserID()
	{
		return userID;
	}

	public static void setUserID(String id)
	{
		userID = id;
	}
}