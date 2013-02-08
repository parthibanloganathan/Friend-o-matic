package com.parthi.friendomatic;

public class User
{
	static final String defaultID = "Friendomatic_Default_Null_ID_8519347983";
	private static String userID = defaultID;
	
    static final String defaultName = "<Invalid User>";
	private static String name = "";
	
	public static String getUserID()
	{
		return userID;
	}

	public static void setUserID(String id)
	{
		userID = id;
	}

	public static String getName()
	{
		return name;
	}

	public static void setName(String username)
	{
		name = username;
	}
}