package com.parthi.friendomatic;

public class UserID
{
	static final String defaultID = "Friendomatic_Default_Null_ID_8519347983";
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